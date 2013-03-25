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
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.core.form;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * 
 * Help class for string usage
 * 
 * @author jxu
 */
public class StringUtil {
	/*
	 * A utility method for escaping apostrophes in Strings. "My'String" becomes "My\'String". This could be used for
	 * example to escape a String for insertion into a postgresql varchar field.
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

	public static void main(String[] args) {
		System.out.println(StringUtil.escapeSingleQuote("my ' header'"));
	}

	/**
	 * Checks whether a string is blank
	 * 
	 * @param s
	 * @return true if blank, false otherwise
	 */
	public static boolean isBlank(String s) {
		return s == null ? true : s.trim().equals("") ? true : false;

	}

	public static boolean isNumber(String s) {
		// To Do: whether we consider a blank string is still a number?
		return Pattern.matches("[0-9]*", s) ? true : false;

	}

	public static boolean isValidDate(String s) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
		sdf.setLenient(false);
		try {
			java.util.Date date = sdf.parse(s);
			if (date.after(new java.util.Date())) {
				return false; // not a date in the past,for date of birth
			}
		} catch (ParseException fe) {
			return false; // format is wrong
		}

		return true;

	}

	public static boolean isEmail(String s) {
		return Pattern.matches("[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*@[A-Za-z]+(\\.[A-Za-z]+)*", s) ? true : false;
	}

	public static String join(String glue, ArrayList a) {
		String answer = "";
		String join = "";

		for (int i = 0; i < a.size(); i++) {
			String s = (String) a.get(i);
			answer += join + s;
			join = glue;
		}

		return answer;
	}

	/**
	 * @param s
	 * @param dateFormat
	 * @return
	 * 
	 * @author ywang (Nov., 2008)
	 */
	public static boolean isFormatDate(String s, String dateFormat) {
		return isFormatDate(s, dateFormat, Locale.getDefault());
	}

	/**
	 * @param s
	 * @param dateFormat
	 * @param locale
	 * @return
	 */
	public static boolean isFormatDate(String s, String dateFormat, Locale locale) {
		String dateformat = parseDateFormat(dateFormat);
		return isSameDate(dateformat, dateformat, s, locale);
	}

	/**
	 * Allow only 4 digits, no more, no less
	 * 
	 * @param s
	 * @param yearFormat
	 * @return
	 * 
	 * @author ywang (Nov., 2008)
	 * @author clinovo (dec 2012) - updated from OpenClinica 3.1.3
	 */
	public static boolean isPartialYear(String s, String yearFormat) {
		return partialYear(s, yearFormat, null);
	}

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
		if (dn != 4) {
			return false;
		}
		String yearformat = parseDateFormat(yearFormat) + "-MM-dd";
		SimpleDateFormat sdf_y;
		if (locale == null) {
			sdf_y = new SimpleDateFormat(yearformat);
		} else {
			sdf_y = new SimpleDateFormat(yearformat, locale);
		}
		sdf_y.setLenient(false);
		String sy = s + "-01-18";
		try {
			sdf_y.parse(sy);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * The year can only between 1000 and 9999.
	 * 
	 * @param s
	 * @param yearMonthFormat
	 * @return
	 * 
	 * @author ywang (Nov., 2008)
	 * @author clinovo Dec 2012
	 */
	public static boolean isPartialYearMonth(String s, String yearMonthFormat) {
		String yearmonthformat = parseDateFormat(yearMonthFormat) + "-dd";
		String sym = s + "-18";
		return isSameDate(yearmonthformat, yearmonthformat, sym);
	}

	public static boolean isPartialYearMonth(String s, String yearMonthFormat, Locale locale) {
		String yearmonthformat = parseDateFormat(yearMonthFormat) + "-dd";
		String sym = s + "-18";
		return isSameDate(yearmonthformat, yearmonthformat, sym, locale);
	}

	/**
	 * return dateFormat with lowercase "y" and "d"
	 * 
	 * @param dateFormat
	 * @return
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
	 * @param dateFormat1
	 * @param dateFormat2
	 * @param dateStr
	 * @param locale
	 * @return
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
					if (year > 9999 || year < 1000) {
						return false;
					}
					return true;
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
	 * @param dateFormat1
	 * @param dateFormat2
	 * @param dateStr
	 * @return
	 */
	public static boolean isSameDate(String dateFormat1, String dateFormat2, String dateStr) {
		return isSameDate(dateFormat1, dateFormat2, dateStr, Locale.getDefault());
	}

	// /**
	// * @param s A string of words, which are substrings separated
	// * by non-word characters (reg ex "\W").
	// * @param numWords The number of words to cut-off at.
	// * @return A string composed of the first <code>numWords</code> words of
	// <code>s</code>.
	// */
	// public static String firstWords(String s, int numWords) {
	// Pattern p = Pattern.compile("\\W");
	// String[] pieces = p.split(s, numWords);
	// ArrayList a = new ArrayList(Arrays.asList(pieces));
	// return join(" ", a);
	// }
}
