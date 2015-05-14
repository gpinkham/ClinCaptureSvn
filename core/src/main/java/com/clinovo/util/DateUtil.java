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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.akaza.openclinica.bean.core.ApplicationConstants;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains Date utility operations.
 * 
 * @author Frank
 * 
 */
public final class DateUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(DateUtil.class);

	private static ResourceBundle resformat;

	private static Map<String, String> timeZoneIDsSortedMap;

	private DateUtil() {

	}

	/**
	 * Checks if passed date string is in valid format.
	 * 
	 * @param dateString
	 *            String
	 * @return true if yes, false otherwise
	 */
	public static boolean isValidDate(String dateString) {
		return dateStringIsInValidFormat(dateString, getOcDateFormat())
				|| dateStringIsInValidFormat(dateString, getOcDateTimeFormat())
				|| dateStringIsInValidFormat(dateString, getDateFormat())
				|| dateStringIsInValidFormat(dateString, getDateTimeFormat());
	}

	private static boolean dateStringIsInValidFormat(String dateString, String format) {
		DateFormat dateFormat = new SimpleDateFormat(format, getLocale());
		dateFormat.setLenient(false);
		try {
			dateFormat.parse(dateString);
		} catch (ParseException e) {
			return false;
		}
		return true;
	}

	/**
	 * Converts String in date format to Date object.
	 *
	 * @param dateString
	 *            String
	 * @return Date
	 */
	public static Date convertStringToDate(String dateString) {
		DateFormat dateFormat = new SimpleDateFormat(getFormatOfDateString(dateString), getLocale());
		try {
			dateFormat.setLenient(false);
			return dateFormat.parse(dateString);
		} catch (ParseException e) {
			LOGGER.warn(dateString + " was in invalid date format. " + e.getMessage());
		}
		return null;
	}

	private static String getFormatOfDateString(String dateString) {
		if (dateStringIsInValidFormat(dateString, getOcDateTimeFormat())) {
			return getOcDateTimeFormat();
		}
		if (dateStringIsInValidFormat(dateString, getDateTimeFormat())) {
			return getDateTimeFormat();
		}
		if (dateStringIsInValidFormat(dateString, getDateFormat())) {
			return getDateFormat();
		}
		return getOcDateFormat();
	}

	/**
	 * Converts Date object to String.
	 * 
	 * @param date
	 *            Date
	 * @return String
	 */
	public static String convertDateToString(Date date) {
		return convertDateToStringInSpecifiedFormat(date, getDateFormat());
	}

	/**
	 * Converts Date with time part to String.
	 * 
	 * @param dateTime
	 *            Date
	 * @return String
	 */
	public static String convertDateTimeToString(Date dateTime) {
		return convertDateToStringInSpecifiedFormat(dateTime, getDateTimeFormat());
	}

	private static String convertDateToStringInSpecifiedFormat(Date date, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format, getLocale());
		return dateFormat.format(date);
	}

	private static String getDateFormat() {
		return getFormatBundle().getString("date_format_string");
	}

	private static String getDateTimeFormat() {
		return getFormatBundle().getString("date_time_format_string");
	}

	private static String getOcDateFormat() {
		return ApplicationConstants.getDateFormatInItemData();
	}

	private static String getOcDateTimeFormat() {
		return ApplicationConstants.getDateFormatInStudyEvent();
	}

	private static ResourceBundle getFormatBundle() {
		if (resformat == null) {
			ResourceBundleProvider.updateLocale(getLocale());
			resformat = ResourceBundleProvider.getFormatBundle();
		}
		return resformat;
	}

	private static Locale getLocale() {
		return CoreResources.getSystemLocale();
	}

	/**
	 * Verifies if string passed represents a valid time zone name,
	 * based off on the set of standard time zones, provided by <code>joda-time</code> library.
	 *
	 * @param timeZoneId String time zone name.
	 * @return boolean <code>true</code> if string passed represents a valid time zone name,
	 * <code>false</code> otherwise.
	 */
	public static boolean isValidTimeZoneId(String timeZoneId) {
		return (timeZoneId != null) && DateTimeZone.getAvailableIDs().contains(timeZoneId);
	}

	/**
	 * Returns an immutable linked map, which contains a list of pairs
	 * {key: "time zone ID", value: "(time zone offset) time zone ID"},
	 * sorted by time zone offset in ascending order.
	 * Based off on the set of standard time zones, provided by <code>joda-time</code> library.
	 *
	 * @return Map
	 */
	public static Map<String, String> getAvailableTimeZoneIDsSorted() {

		if (timeZoneIDsSortedMap == null) {
			long instant = System.currentTimeMillis();
			List<DateTimeZone> timeZonesSorted = new ArrayList<DateTimeZone>();
			for (String zoneID : DateTimeZone.getAvailableIDs()) {
				timeZonesSorted.add(DateTimeZone.forID(zoneID));
			}
			Collections.sort(timeZonesSorted, new TimeZoneComparator(instant));

			Map<String, String> timeZoneIDsSorted = new LinkedHashMap<String, String>();
			DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("ZZ");
			for (DateTimeZone timeZone : timeZonesSorted) {
				timeZoneIDsSorted.put(timeZone.getID(),
						"(" + dateTimeFormatter.withZone(timeZone).print(instant) + ") " + timeZone.getID());
			}
			timeZoneIDsSortedMap = Collections.unmodifiableMap(timeZoneIDsSorted);
		}
		return timeZoneIDsSortedMap;
	}

	/**
	 * Returns string representation of a given date, translated into specified time zone
	 * and formatted according to specified date pattern.
	 *
	 * @param dateToPrint Date date to print
	 * @param timeZoneId  String time zone to translate to
	 * @param datePattern DatePattern specifies output format of date
	 * @return String string representation of a given date
	 */
	public static String printDate(Date dateToPrint, String timeZoneId, DatePattern datePattern) {

		DateTimeFormatter dateFormatter = DateTimeFormat.forPattern(datePattern.getPattern())
				.withZone(DateTimeZone.forID(timeZoneId)).withLocale(getLocale());
		return dateFormatter.print(dateToPrint.getTime());
	}

	/**
	 * Comparator class. Compares time zones based on offset.
	 */
	private static final class TimeZoneComparator implements Comparator<DateTimeZone> {

		private final long instant;

		TimeZoneComparator(long instant) {
			this.instant = instant;
		}

		public long getInstant() {
			return instant;
		}

		public int compare(DateTimeZone timeZone1, DateTimeZone timeZone2) {

			int offsetCriteria = ((Integer) timeZone1.getOffset(getInstant()))
					.compareTo(timeZone2.getOffset(getInstant()));
			if (offsetCriteria == 0) {
				return timeZone1.getID().compareTo(timeZone2.getID());
			} else {
				return offsetCriteria;
			}
		}

		public boolean equals(Object obj) {
			return super.equals(obj);
		}
	}

	/**
	 * Enumeration <code>DateUtil.DatePattern</code> represents available date format patterns.
	 * DATE is for pattern <code>yyyy-MM-dd</code>
	 * TIMESTAMP is for pattern <code>dd-MMM-yyyy HH:MM</code>
	 * TIMESTAMP_WITH_SECONDS is for pattern <code>dd-MMM-yyyy HH:mm:ss</code>
	 */
	public enum DatePattern {
		DATE(ResourceBundleProvider.getResFormat("date_format_string")),
		TIMESTAMP(ResourceBundleProvider.getResFormat("date_time_format_short")),
		TIMESTAMP_WITH_SECONDS(ResourceBundleProvider.getResFormat("date_time_format_string"));

		private String pattern;

		private DatePattern(String pattern) {
			this.pattern = pattern;
		}

		public String getPattern() {
			return pattern;
		}
	}
}
