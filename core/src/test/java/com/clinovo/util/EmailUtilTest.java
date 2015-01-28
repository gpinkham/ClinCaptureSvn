package com.clinovo.util;

import org.akaza.openclinica.bean.odmbeans.SymbolBean;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class EmailUtilTest {

	private static ResourceBundle resword;
	private static ResourceBundle resnotes;
	private static EmailUtil emailUtil;

	@Before
	public void prepare() {
		emailUtil = Mockito.mock(EmailUtil.class);
		ResourceBundleProvider.updateLocale(Locale.ENGLISH);
		resword = ResourceBundleProvider.getWordsBundle();
		resnotes = ResourceBundleProvider.getPageMessagesBundle();

		Whitebox.setInternalState(emailUtil, "resword", resword);
		Whitebox.setInternalState(emailUtil, "resnotes", resnotes);
	}

	@Test
	public void testThatGetEmailFooterReturnsFooter() {
		assertTrue(EmailUtil.getEmailFooter(Locale.ENGLISH).contains("https://clincapture.clinovo.com"));
	}

	@Test
	public void testThatGetEmailBodyStartReturnsCorrectText() {
		assertTrue(EmailUtil.getEmailBodyStart().contains("style='font-family:helvetica,trebuchet ms,arial,times new roman;"));
	}

	@Test
	public void testThatGetEmailBodyEndReturnsCorrectText() {
		assertTrue(EmailUtil.getEmailBodyEnd().equals("</td></tr></table>"));
	}
}
