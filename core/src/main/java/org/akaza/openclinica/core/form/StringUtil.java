/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License along with this program.
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.core.form;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Help class for string usage.
 *
 * @author jxu
 */
public final class StringUtil {

	private StringUtil() {
	}

	public static final int MAX_YEAR = 9999;
	public static final int MIN_YEAR = 1000;
	public static final int MAX_YEAR_LENGTH = 4;

	/**
	 * A utility method for escaping apostrophes in Strings. "My'String" becomes "My\'String". This could be used for
	 * example to escape a String for insertion into a postgresql varchar field.
	 * @param escapeSource - String.
	 * @return String.
	 */
	public static String escapeSingleQuote(String escapeSource) {
		if (escapeSource == null || "".equalsIgnoreCase(escapeSource)) {
			return "";
		}
		// We have to use four backslashes in a row here to properly reproduce
		// \' from
		// a single apostrophe
		return escapeSource.replaceAll("'", "\\\\'");
	}

	/**
	 * Check if string is not blank and equals to one of the options.
	 * @param string String
	 * @param options List of options.
	 * @return boolean.
	 */
	public static boolean notBlankAndEquals(String string, String... options) {
		if (isBlank(string)) {
			return false;
		}
		for (String option : options) {
			if (string.trim().equalsIgnoreCase(option)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether a string is blank.
	 *
	 * @param s - String.
	 * @return true if blank, false otherwise
	 */
	public static boolean isBlank(String s) {
		return s == null || (s.trim().equals(""));
	}

	/**
	 * Join array of Strings.
	 * @param glue - string that will be used as joint.
	 * @param list an array of strings.
	 * @return String.
	 */
	public static String join(String glue, ArrayList<String> list) {
		String answer = "";
		String join = "";

		for (String entry : list) {
			answer += join + entry;
			join = glue;
		}
		return answer;
	}

	/**
	 * Check if String is date.
	 * @param s - String
	 * @param dateFormat - String
	 * @return boolean
	 */
	public static boolean isFormatDate(String s, String dateFormat) {
		return isFormatDate(s, dateFormat, Locale.getDefault());
	}

	/**
	 * Check if is format date.
	 * @param s - String.
	 * @param dateFormat - String.
	 * @param locale - Locale.
	 * @return boolean.
	 */
	public static boolean isFormatDate(String s, String dateFormat, Locale locale) {
		String parsedFormat = parseDateFormat(dateFormat);
		return isSameDate(parsedFormat, parsedFormat, s, locale);
	}

	/**
	 * Allow only 4 digits, no more, no less.
	 * @param s - String
	 * @param yearFormat - String
	 * @return boolean.
	 */
	public static boolean isPartialYear(String s, String yearFormat) {
		return partialYear(s, yearFormat, null);
	}

	/**
	 * Check if String is partial date.
	 * @param s String
	 * @param yearFormat String
	 * @param locale Locale
	 * @return boolean - validation result.
	 */
	public static boolean isPartialYear(String s, String yearFormat, Locale locale) {
		return partialYear(s, yearFormat, locale);
	}

	private static boolean partialYear(String s, String yearFormat, Locale locale) {
		int dn = 0;
		char[] cyear = s.toCharArray();
		for (char c : cyear) {
			if (!Character.isDigit(c)) {
				return false;
			}
			++dn;
		}
		if (dn != MAX_YEAR_LENGTH) {
			return false;
		}
		String parsedYearFormat = parseDateFormat(yearFormat) + "-MM-dd";
		SimpleDateFormat dateFormat;
		if (locale == null) {
			dateFormat = new SimpleDateFormat(parsedYearFormat);
		} else {
			dateFormat = new SimpleDateFormat(parsedYearFormat, locale);
		}
		dateFormat.setLenient(false);
		String sy = s + "-01-18";
		try {
			dateFormat.parse(sy);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * The year can only between 1000 and 9999.
	 * @param s - String to check
	 * @param yearMonthFormat - Date Format
	 * @return boolean.
	 */
	public static boolean isPartialYearMonth(String s, String yearMonthFormat) {
		String parsedFormat = parseDateFormat(yearMonthFormat) + "-dd";
		String sym = s + "-18";
		return isSameDate(parsedFormat, parsedFormat, sym);
	}

	/**
	 * Check if String is partial date.
	 * @param s - String
	 * @param yearMonthFormat - String
	 * @param locale - Locale
	 * @return boolean
	 */
	public static boolean isPartialYearMonth(String s, String yearMonthFormat, Locale locale) {
		String parsedFormat = parseDateFormat(yearMonthFormat) + "-dd";
		String sym = s + "-18";
		return isSameDate(parsedFormat, parsedFormat, sym, locale);
	}

	/**
	 * Return dateFormat with lowercase "y" and "d".
	 * @param dateFormat - String
	 *
	 * @return String date format.
	 */
	public static String parseDateFormat(String dateFormat) {
		String s = dateFormat;
		while (s.contains("Y")) {
			s = s.replace("Y", "y");
		}
		while (s.contains("D")) {
			s = s.replace("D", "d");
		}
		return s;
	}

	/**
	 * Return true if a date String is the same day when it is parsed by two different dateFormats, depends on locale.
	 * The year can only between 1000 and 9999.
	 *
	 * @param dateFormat1 - String
	 * @param dateFormat2 - String
	 * @param dateStr - String
	 * @param locale - Locale
	 * @return boolean
	 */
	public static boolean isSameDate(String dateFormat1, String dateFormat2, String dateStr, Locale locale) {
		SimpleDateFormat sdf1 = new SimpleDateFormat(dateFormat1, locale);
		sdf1.setLenient(false);
		SimpleDateFormat sdf2 = new SimpleDateFormat(dateFormat2, locale);
		sdf2.setLenient(false);
		try {
			Date d1 = sdf1.parse(dateStr);
			try {
				String temp = sdf2.format(d1);
				if (temp.equalsIgnoreCase(dateStr)) {
					Calendar c = Calendar.getInstance();
					c.setTime(d1);
					int year = c.get(Calendar.YEAR);
					return !(year > MAX_YEAR || year < MIN_YEAR);
				} else {
					return false;
				}
			} catch (Exception e) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Return true if a date String is the same day when it is parsed by two different dateFormats. The year can only
	 * between 1000 and 9999.
	 *
	 * @param dateFormat1 String
	 * @param dateFormat2 String
	 * @param dateStr String
	 * @return boolean
	 */
	public static boolean isSameDate(String dateFormat1, String dateFormat2, String dateStr) {
		return isSameDate(dateFormat1, dateFormat2, dateStr, Locale.getDefault());
	}
}
