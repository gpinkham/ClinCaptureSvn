/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 * 
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer. 
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clincapture.com/contact for pricing information.
 * 
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use. 
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.akaza.openclinica.DefaultAppContextTest;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class DateUtilTest extends DefaultAppContextTest {

	private DateTimeZone jvmTimeZone = DateTimeZone.getDefault();

	@After
	public void restoreDefault() {
		DateTimeZone.setDefault(jvmTimeZone);
	}

	@Test
	public void testThatStringIsInValidOcDateFormat() {
		assertTrue(DateUtil.isValidDate("2015-02-02"));
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
	public void testThatPrintDateMakesCorrectTranslationBetweenTimeZones_1() {

		// SETUP
		DateTimeZone.setDefault(DateTimeZone.forID("America/Chihuahua"));
		Date dateToTranslate = new Date(1427284953000L); // 25th March 2015 12:02:33 GMT

		// TEST
		String output = DateUtil.printDate(dateToTranslate, "Europe/Helsinki", DateUtil.DatePattern.TIMESTAMP_WITH_SECONDS,
				Locale.ENGLISH);

		// VERIFY
		// As of 25th March 2015 12:02:33 GMT
		// time zone "America/Chihuahua" has offset -07:00
		// and time zone "Europe/Helsinki" has offset +02:00.
		// result of translation "America/Chihuahua" -> "Europe/Helsinki" must be equal to 25th March 2015 14:02:33
		Assert.assertEquals("25-Mar-2015 14:02:33", output);
	}

	@Test
	public void testThatDoEndTagMakesCorrectTranslationBetweenTimeZones_2() throws Exception {

		// SETUP
		DateTimeZone.setDefault(DateTimeZone.forID("America/Chihuahua"));
		Date dateToTranslate = new Date(1431348770000L); // 11th May 2015 12:52:50 GMT

		// TEST
		String output = DateUtil.printDate(dateToTranslate, "Europe/Helsinki", DateUtil.DatePattern.TIMESTAMP_WITH_SECONDS,
				Locale.ENGLISH);

		// VERIFY
		// As of 11th May 2015 12:52:50 GMT
		// time zone "America/Chihuahua" has offset -06:00
		// and time zone "Europe/Helsinki" has offset +03:00.
		// result of translation "America/Chihuahua" -> "Europe/Helsinki" must be equal to 11th May 2015 15:52:50
		Assert.assertEquals("11-May-2015 15:52:50", output);
	}

	@Test
	public void testThatDoEndTagMakesCorrectTranslationBetweenTimeZones_3() throws Exception {

		// SETUP
		DateTimeZone.setDefault(DateTimeZone.forID("America/Chihuahua"));
		Date dateToTranslate = new Date(1427284953000L); // 25th March 2015 12:02:33 GMT

		// TEST
		String output = DateUtil.printDate(dateToTranslate, "Asia/Muscat", DateUtil.DatePattern.TIMESTAMP_WITH_SECONDS,
				Locale.ENGLISH);

		// VERIFY
		// As of 25th March 2015 12:02:33 GMT
		// time zone "America/Chihuahua" has offset -07:00.
		// Time zone "Asia/Muscat" has constant offset +04:00.
		// result of translation "America/Chihuahua" -> "Asia/Muscat" must be equal to 25th March 2015 16:02:33
		Assert.assertEquals("25-Mar-2015 16:02:33", output);
	}

	@Test
	public void testThatDoEndTagMakesCorrectTranslationBetweenTimeZones_4() throws Exception {

		// SETUP
		DateTimeZone.setDefault(DateTimeZone.forID("America/Chihuahua"));
		Date dateToTranslate = new Date(1431348770000L); // 11th May 2015 12:52:50 GMT

		// TEST
		String output = DateUtil.printDate(dateToTranslate, "Asia/Muscat", DateUtil.DatePattern.TIMESTAMP_WITH_SECONDS,
				Locale.ENGLISH);

		// VERIFY
		// As of 11th May 2015 12:52:50 GMT
		// time zone "America/Chihuahua" has offset -06:00.
		// Time zone "Asia/Muscat" has constant offset +04:00.
		// result of translation "America/Chihuahua" -> "Asia/Muscat" must be equal to 11th May 2015 16:52:50
		Assert.assertEquals("11-May-2015 16:52:50", output);
	}

	@Test
		public void testThatPrintDateMakesCorrectTranslationBetweenTimeZones_5() {

		// SETUP
		DateTimeZone.setDefault(DateTimeZone.forID("America/Chihuahua"));
		Date dateToTranslate = new Date(1427284953000L); // 25th March 2015 12:02:33 GMT

		// TEST
		String output = DateUtil.printDate(dateToTranslate, "America/Chihuahua", DateUtil.DatePattern.TIMESTAMP_WITH_SECONDS,
				Locale.ENGLISH);

		// VERIFY
		// As of 25th March 2015 12:02:33 GMT
		// time zone "America/Chihuahua" has offset -07:00.
		// Target time zone was not set, thus date must remain in the JVM time zone
		Assert.assertEquals("25-Mar-2015 05:02:33", output);
	}
}
