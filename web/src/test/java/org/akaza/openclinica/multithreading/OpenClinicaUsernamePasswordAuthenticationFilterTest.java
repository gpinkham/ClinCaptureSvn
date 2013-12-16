package org.akaza.openclinica.multithreading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.akaza.openclinica.AbstractContextSentiveTest;
import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.web.filter.OpenClinicaUsernamePasswordAuthenticationFilter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

public class OpenClinicaUsernamePasswordAuthenticationFilterTest extends AbstractContextSentiveTest {

	private static final int TOTAL_THREADS = 10;

	public static final String ROOT = "root";

	private static boolean errors;
	private static Locale locale = new Locale("en");
	private static final List<Thread> threadList = new ArrayList<Thread>();
	private static OpenClinicaUsernamePasswordAuthenticationFilter filter;

	private static HashMap<Thread, Locale> localeMap = new HashMap<Thread, Locale>();
	private static HashMap<Locale, HashMap<String, ResourceBundle>> resBundleSetMap = new HashMap<Locale, HashMap<String, ResourceBundle>>();

	private static class CustomThread extends Thread {

		@Override
		public void run() {
			try {
				for (int i = 0; i < 10; i++) {
					EntityBean eb = filter.getUserAccountDao().findByUserName(ROOT);
					if (eb.getId() == 0) {
						errors = true;
						break;
					}
				}
			} catch (Exception ex) {
				errors = true;
			}
		}

	}

	@Before
	public void setUp() throws Exception {
		filter = new OpenClinicaUsernamePasswordAuthenticationFilter();
		filter.setDataSource(getDataSource());
	}

	private boolean shouldWait() {
		boolean result = false;
		if (!errors) {
			for (Thread thread : threadList) {
				if (thread.isAlive()) {
					result = true;
					break;
				}
			}
		}
		return result;
	}

	private void updateLocale(Thread thread) {
		localeMap.put(thread, locale);
		if (!resBundleSetMap.containsKey(locale)) {
			HashMap<String, ResourceBundle> resBundleSet = new HashMap<String, ResourceBundle>();
			resBundleSet.put("org.akaza.openclinica.i18n.admin",
					ResourceBundle.getBundle("org.akaza.openclinica.i18n.admin", locale));
			resBundleSet.put("org.akaza.openclinica.i18n.audit_events",
					ResourceBundle.getBundle("org.akaza.openclinica.i18n.audit_events", locale));
			resBundleSet.put("org.akaza.openclinica.i18n.exceptions",
					ResourceBundle.getBundle("org.akaza.openclinica.i18n.exceptions", locale));
			resBundleSet.put("org.akaza.openclinica.i18n.format",
					ResourceBundle.getBundle("org.akaza.openclinica.i18n.format", locale));
			resBundleSet.put("org.akaza.openclinica.i18n.page_messages",
					ResourceBundle.getBundle("org.akaza.openclinica.i18n.page_messages", locale));
			resBundleSet.put("org.akaza.openclinica.i18n.notes",
					ResourceBundle.getBundle("org.akaza.openclinica.i18n.notes", locale));
			resBundleSet.put("org.akaza.openclinica.i18n.terms",
					ResourceBundle.getBundle("org.akaza.openclinica.i18n.terms", locale));
			resBundleSet.put("org.akaza.openclinica.i18n.words",
					ResourceBundle.getBundle("org.akaza.openclinica.i18n.words", locale));
			resBundleSet.put("org.akaza.openclinica.i18n.workflow",
					ResourceBundle.getBundle("org.akaza.openclinica.i18n.workflow", locale));

			resBundleSetMap.put(locale, resBundleSet);
		}
	}

	@Test
	public void testThatGetUserAccountDaoDoesNotThrowAnException() throws Exception {
		updateLocale(Thread.currentThread());
		for (int t = 1; t <= TOTAL_THREADS; t++) {
			CustomThread thread = new CustomThread();
			threadList.add(thread);
			updateLocale(thread);

		}
		Whitebox.setInternalState(new ResourceBundleProvider(), "localeMap", localeMap);
		Whitebox.setInternalState(new ResourceBundleProvider(), "resBundleSetMap", resBundleSetMap);
		for (Thread thread : threadList) {
			thread.start();
		}
		while (shouldWait())
			Thread.sleep(1000);
		assertFalse(errors);
	}
}
