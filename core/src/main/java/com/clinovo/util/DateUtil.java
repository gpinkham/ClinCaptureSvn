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
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains Date utility operations.
 */
public final class DateUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(DateUtil.class);

	public static final String ISO_DATE = "yyyy-MM-dd";

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
	 * Returns date formatter fot specified date pattern and system language.
	 * The formatter returned by method will not perform any translations in time zones.
	 *
	 * @param datePattern date pattern string
	 * @param locale      locale
	 * @return returns date formatter fot specified date pattern
	 */
	public static DateTimeFormatter getDateTimeFormatter(DatePattern datePattern, Locale locale) {
		return getDateTimeFormatter(datePattern, locale, null);
	}

	/**
	 * Returns date formatter fot specified date pattern, time zone and system language.
	 *
	 * @param datePattern date pattern string
	 * @param locale      locale
	 * @param timeZone    time zone to translate to
	 * @return returns date formatter fot specified date pattern and time zone
	 */
	public static DateTimeFormatter getDateTimeFormatter(DatePattern datePattern, Locale locale,
			DateTimeZone timeZone) {

		DateTimeFormatter dateFormatter = DateTimeFormat.forPattern(datePattern.getPattern()).withLocale(locale);
		if (timeZone != null) {
			dateFormatter.withZone(timeZone);
		}
		return dateFormatter;
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
	 * Returns string representation of a given date, formatted according to specified date pattern.
	 *
	 * @param dateToPrint Date date to print
	 * @param datePattern DatePattern specifies output format of date
	 * @param locale      locale
	 * @return String string representation of a given date
	 */
	public static String printDate(Date dateToPrint, DatePattern datePattern, Locale locale) {
		return printDate(dateToPrint, null, datePattern, locale);
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
		return printDate(dateToPrint, timeZoneId, datePattern, Locale.getDefault());
	}

	/**
	 * Returns string representation of a given date, translated into specified time zone
	 * and formatted according to specified date pattern.
	 *
	 * @param dateToPrint Date date to print
	 * @param timeZoneId  String time zone to translate to
	 * @param datePattern DatePattern specifies output format of date
	 * @param locale      locale
	 * @return String string representation of a given date
	 */
	public static String printDate(Date dateToPrint, String timeZoneId, DatePattern datePattern, Locale locale) {

		DateTimeFormatter dateFormatter;
		if (timeZoneId != null) {
			String validTargetTimeZoneId = isValidTimeZoneId(timeZoneId) ? timeZoneId : DateTimeZone.getDefault().getID();
			dateFormatter = DateTimeFormat.forPattern(datePattern.getPattern())
					.withZone(DateTimeZone.forID(validTargetTimeZoneId)).withLocale(locale);
		} else {
			dateFormatter = DateTimeFormat.forPattern(datePattern.getPattern()).withLocale(locale);
		}
		return dateFormatter.print(dateToPrint.getTime());
	}

	/**
	 * Parses date from the input date string according to specified date pattern format.
	 *
	 * @param dateTimeString     input date string
	 * @param datePattern        specifies expected format of input date string
	 * @param locale             locale
	 * @return date object, created from the input date string.
	 */
	public static Date parseDateString(String dateTimeString, DatePattern datePattern, Locale locale) {

		DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(datePattern.getPattern()).withLocale(locale);
		return dateTimeFormatter.parseDateTime(dateTimeString.toLowerCase()).toDate();
	}

	/**
	 * Parses date from the input date-time string according to specified date pattern format,
	 * and translates it from the input date time zone into the server time zone.
	 *
	 * @param dateTimeString     input date-time string
	 * @param originalTimeZoneId time zone of input date-time
	 * @param datePattern        specifies expected format of input date-time string
	 * @param locale             locale
	 * @return date object, created from the input date-time string and translated into the server time zone.
	 */
	public static Date parseDateStringToServerDateTime(String dateTimeString, String originalTimeZoneId,
			DatePattern datePattern, Locale locale) {
		return parseDateStringToServerDateTime(dateTimeString, originalTimeZoneId, datePattern, locale, false);
	}

	/**
	 * Parses date from the input date-time string according to specified date pattern format,
	 * and translates it from the input date time zone into the server time zone.
	 * If flag <code>applyCurrentServerTime</code> is set to <code>true</code>,
	 * then the time of the day of input date-time should be replaced with current server time of the day.
	 * If flag <code>applyCurrentServerTime</code> is set to <code>false</code>,
	 * then the time of the day of input date-time will be preserved.
	 *
	 * @param dateTimeString         input date-time string
	 * @param originalTimeZoneId     time zone of input date-time
	 * @param datePattern            specifies expected format of input date-time string
	 * @param locale                 locale
	 * @param applyCurrentServerTime specifies, if the time of the day of input date-time
	 *                               should be replaced with current server time of the day
	 * @return date object, created from the input date-time string and translated into the server time zone.
	 */
	public static Date parseDateStringToServerDateTime(String dateTimeString, String originalTimeZoneId,
			DatePattern datePattern, Locale locale, boolean applyCurrentServerTime) {

		if (dateTimeString == null || dateTimeString.isEmpty()) {
			return new Date();
		}
		String validOriginalTimeZoneId = isValidTimeZoneId(originalTimeZoneId)
				? originalTimeZoneId : DateTimeZone.getDefault().getID();
		DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(datePattern.getPattern())
				.withLocale(locale).withZone(DateTimeZone.forID(validOriginalTimeZoneId));

		DateTime originalDateTime = dateTimeFormatter.parseDateTime(dateTimeString.toLowerCase());
		if (applyCurrentServerTime) {
			originalDateTime = originalDateTime.withTime(new LocalTime(DateTimeZone.forID(validOriginalTimeZoneId)));
		}
		return originalDateTime.withZone(DateTimeZone.getDefault()).toDate();
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
	 * DATE is for pattern <code>dd-MMM-yyyy</code>
	 * TIMESTAMP is for pattern <code>dd-MMM-yyyy HH:mm</code>
	 * TIMESTAMP_WITH_SECONDS is for pattern <code>dd-MMM-yyyy HH:mm:ss</code>
	 */
	public enum DatePattern {
		DATE(ResourceBundleProvider.getResFormat("date_format_string")),
		TIMESTAMP(ResourceBundleProvider.getResFormat("date_time_format_short")),
		TIMESTAMP_WITH_SECONDS(ResourceBundleProvider.getResFormat("date_time_format_string")),
		DATE_AND_HOUR("dd-MMM-yyyy HH"),
		YEAR_AND_MONTH("MMM-yyyy"),
		YEAR("yyyy"),
		ISO_DATE(DateUtil.ISO_DATE),
		ISO_TIMESTAMP("yyyy-MM-dd'T'HH:mm:ss");

		private String pattern;

		private DatePattern(String pattern) {
			this.pattern = pattern;
		}

		public String getPattern() {
			return pattern;
		}
	}
}
