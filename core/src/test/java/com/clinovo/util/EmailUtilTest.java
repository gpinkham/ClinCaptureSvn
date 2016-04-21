package com.clinovo.util;

import java.util.Locale;

import org.akaza.openclinica.DefaultAppContextTest;
import org.junit.Test;

public class EmailUtilTest extends DefaultAppContextTest {

	@Test
	public void testThatGetEmailFooterReturnsFooter() {
		assertTrue(EmailUtil.getEmailFooter(Locale.ENGLISH).contains("href='http://www.clincapture.com/Privacy-Policy"));
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
