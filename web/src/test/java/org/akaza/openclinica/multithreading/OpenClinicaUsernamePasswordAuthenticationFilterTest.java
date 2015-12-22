package org.akaza.openclinica.multithreading;

import java.util.ArrayList;
import java.util.List;

import org.akaza.openclinica.AbstractContextSentiveTest;
import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.web.filter.OpenClinicaUsernamePasswordAuthenticationFilter;
import org.junit.Before;
import org.junit.Test;

public class OpenClinicaUsernamePasswordAuthenticationFilterTest extends AbstractContextSentiveTest {

	private static final int TOTAL_THREADS = 10;

	public static final String ROOT = "root";

	private static boolean errors;
	private static OpenClinicaUsernamePasswordAuthenticationFilter filter;
	private static final List<Thread> threadList = new ArrayList<Thread>();

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

	@Test
	public void testThatGetUserAccountDaoDoesNotThrowAnException() throws Exception {
		for (int t = 1; t <= TOTAL_THREADS; t++) {
			CustomThread thread = new CustomThread();
			threadList.add(thread);

		}
		for (Thread thread : threadList) {
			thread.start();
		}
		while (shouldWait())
			Thread.sleep(1000);
		assertFalse(errors);
	}
}
