package com.clinovo.util;

import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.Assert.assertTrue;

public class EmailUtilTest {

	@Before
	public void prepare() {
		EmailUtil emailUtil = Mockito.mock(EmailUtil.class);
		ResourceBundleProvider.updateLocale(Locale.ENGLISH);
		ResourceBundle resword = ResourceBundleProvider.getWordsBundle();
		ResourceBundle resnotes = ResourceBundleProvider.getPageMessagesBundle();

		Whitebox.setInternalState(emailUtil, "resword", resword);
		Whitebox.setInternalState(emailUtil, "resnotes", resnotes);
	}

	@Test
	public void testThatGetEmailFooterReturnsFooter() {
		assertTrue(EmailUtil.getEmailFooter(Locale.ENGLISH).contains("href='https://www.clinovo.com/Privacy-Policy"));
	}

	@Test
	public void testThatGetEmailBodyStartReturnsCorrectText() {
		assertTrue(EmailUtil.getEmailBodyStart().contains(
				"font-family:Arial,Helvetica,sans-serif;font-size:12px;font-weight:normal;color:#333;"));
	}

	@Test
	public void testThatGetEmailBodyEndReturnsCorrectText() {
		assertTrue(EmailUtil.getEmailBodyEnd().contains("https://www.facebook.com/Clinovo"));
	}
}
