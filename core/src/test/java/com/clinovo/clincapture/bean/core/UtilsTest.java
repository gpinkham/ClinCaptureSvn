/*******************************************************************************
 * Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.clinovo.clincapture.bean.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import org.akaza.openclinica.bean.core.Utils;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * User: Pavel Date: 14.10.12
 */
public class UtilsTest {

	private static final String NA = "N/A";
	private static final String LESS_THAN_DAY = "Less_than_a_day";

	private static final String firstDateFormat = "dd-MM-yyyy";
	private static final String wrongFirstDateFormat = "dd/MM-yyyy";
	private static final String firstDateString = "20-10-2001";
	private static final String wrongFirstDateString = "20-10/2001";

	private static final String secondDateFormat = "yy/MM/dd";
	private static final String secondDateString = "01/10/20";

	ResourceBundle reswords;

	private Date eventDate;
	private Date subjectDate;
	private Date olderSubjectDate;

	private Utils utils;

	@Before
	public void setUp() throws Exception {
		utils = Utils.getInstance();

		ResourceBundleProvider.updateLocale(Locale.getDefault());
		reswords = ResourceBundleProvider.getWordsBundle();

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 1990);
		calendar.set(Calendar.MONTH, 1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		subjectDate = calendar.getTime();

		calendar.set(Calendar.YEAR, 1990);
		calendar.set(Calendar.MONTH, 2);
		calendar.set(Calendar.DAY_OF_MONTH, 8);
		olderSubjectDate = calendar.getTime();

		calendar.set(Calendar.YEAR, 2000);
		calendar.set(Calendar.MONTH, 2);
		calendar.set(Calendar.DAY_OF_MONTH, 7);
		eventDate = calendar.getTime();

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testProcessNa() {
		assertEquals(NA, utils.processAge(null, null));
		assertEquals(NA, utils.processAge(eventDate, null));
		assertEquals(NA, utils.processAge(null, subjectDate));
	}

	@Test
	public void testProcess() {

		String expected = "10 " + reswords.getString("Years") + " - " + "1" + " " + reswords.getString("Months")
				+ " - " + "4" + " " + reswords.getString("Days");

		assertEquals(expected, utils.processAge(eventDate, subjectDate));
	}

	@Test
	public void testProcessLessThanDay() {
		assertEquals(reswords.getString(LESS_THAN_DAY), utils.processAge(eventDate, eventDate));
	}

	@Test
	public void testConvert() {
		assertEquals(secondDateString, Utils.convertedItemDateValue(firstDateString, firstDateFormat, secondDateFormat));
	}

	@Test
	public void testConvertNull() {
		assertNull(Utils.convertedItemDateValue(null, firstDateFormat, secondDateFormat));
	}

	@Test
	public void testConvertWrongFirstPattern() {
		assertEquals(firstDateString,
				Utils.convertedItemDateValue(firstDateString, wrongFirstDateFormat, secondDateFormat));
	}

	@Test
	public void testConvertWrongDate() {
		assertEquals(wrongFirstDateString,
				Utils.convertedItemDateValue(wrongFirstDateString, firstDateFormat, secondDateFormat));
	}

	@Test
	public void testGetAge() {
		assertEquals(10, Utils.getAge(subjectDate, eventDate));
		assertEquals(10, Utils.getAge(olderSubjectDate, eventDate));
	}

	@Test
	public void testGetPast() {
		assertEquals(-1, Utils.getAge(olderSubjectDate, subjectDate));
	}

	@Test
	public void testIsMatchRegexp() {
		assertTrue(Utils.isMatchingRegexp("123", "[0-9]*"));
	}

	@Test
	public void testIsMatchRegexpFail() {
		assertFalse(Utils.isMatchingRegexp("12f3", "[0-9]*"));
	}

	@Test
	public void testIsWithinRegexp() {
		assertTrue(Utils.isMatchingRegexp("123", "1[1-3]3"));
	}

	@Test
	public void testIsWithinRegexpFail() {
		assertFalse(Utils.isMatchingRegexp("1f3", "1[1-3]3"));
	}

}
