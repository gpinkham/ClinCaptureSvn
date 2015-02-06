/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 * 
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer. 
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 * 
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use. 
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.util;

import java.util.Calendar;
import java.util.Locale;
import java.util.ResourceBundle;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

public class DateUtilTest extends DefaultAppContextTest {

	@Before
	public void setUp() {
		DateUtil dateUtil = Mockito.mock(DateUtil.class);
		ResourceBundleProvider.updateLocale(Locale.ENGLISH);
		ResourceBundle resformat = ResourceBundleProvider.getFormatBundle();
		Whitebox.setInternalState(dateUtil, "resformat", resformat);
	}

	@Test
	public void testThatStringIsInValidOcDateFormat() {
		assertTrue(DateUtil.isValidDate("2015-02-02"));
	}

	@Test
	public void testThatStringIsInValidDateFormat() {
		assertTrue(DateUtil.isValidDate("02-Feb-2015"));
	}

	@Test
	public void testThatStringIsInValidDateTimeFormat() {
		assertTrue(DateUtil.isValidDate("02-Feb-2015 15:45:00"));
	}

	@Test
	public void testThatStringIsNotInValidDateFormat() {
		assertFalse(DateUtil.isValidDate("wrong format"));
	}

	@Test
	public void testThatStringWithInvalidMonthIsNotInValidDateFormat() {
		assertFalse(DateUtil.isValidDate("02-Fev-2015"));
	}

	@Test
	public void testThatStringWithInvalidDayIsNotInValidDateFormat() {
		assertFalse(DateUtil.isValidDate("200-Feb-2015"));
	}

	@Test
	public void testThatConvertStringInOcFormatToDateReturnsCorrectDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(2015, 1, 2, 0, 0, 0);
		assertEquals(calendar.getTime().toString(), DateUtil.convertStringToDate("2015-02-02").toString());
	}

	@Test
	public void testThatConvertStringToDateReturnsCorrectDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(2015, 1, 2, 0, 0, 0);
		assertEquals(calendar.getTime().toString(), DateUtil.convertStringToDate("02-Feb-2015").toString());
	}

	@Test
	public void testThatConvertStringToDateTimeReturnsCorrectDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(2015, 1, 2, 6, 45, 0);
		assertEquals(calendar.getTime().toString(), DateUtil.convertStringToDate("02-Feb-2015 06:45:00").toString());
	}

	@Test
	public void testThatConvertStringInOcFormatToDateTimeReturnsCorrectDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(2015, 1, 1, 6, 45, 0);
		assertEquals(calendar.getTime().toString(), DateUtil.convertStringToDate("2015-02-01 06:45:00").toString());
	}

	@Test
	public void testThatConvertStringToDateReturnsNullForInvalidFormat() {
		assertNull(DateUtil.convertStringToDate("wrong format"));
	}

	@Test
	public void testThatConvertDateToStringReturnsCorrectString() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(2015, 1, 2, 0, 0, 0);
		assertEquals("02-Feb-2015", DateUtil.convertDateToString(calendar.getTime()));
	}

	@Test
	public void testThatConvertDateTimeToStringReturnsCorrectString() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(2015, 1, 2, 6, 45, 0);
		assertEquals("02-Feb-2015 06:45:00", DateUtil.convertDateTimeToString(calendar.getTime()));
	}
}