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
package org.akaza.openclinica.control.form;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.util.DateUtil;
import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.core.EntityDAO;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;

/**
 * @author ssachs
 *
 *         This class does two things: retrieve input from a form, and prepare to set default values
 *
 *         three dimensions:
 *         <ul>
 *         <li>do we throw an exception when the key isn't present?</li>
 *         <li>do we look in the attributes and parameters, or just the parameters?</li>
 *         <li>do we look in an HttpServletRequest, or a MultipartRequest?</li>
 *         </ul>
 *
 *         TODO handle MultiPartRequests - is this a priority, since we don't have many file uploads?
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class FormProcessor {
	private HttpServletRequest request;
	private HashMap presetValues;

	public static final String DEFAULT_STRING = "";
	public static final int DEFAULT_INT = 0;
	public static final float DEFAULT_FLOAT = (float) 0.0;
	public static final boolean DEFAULT_BOOLEAN = false;
	public static final Date DEFAULT_DATE = new Date();
	public static final String FIELD_SUBMITTED = "submitted";
	public static final String CURRENT_USER_ATTR_NAME = "userBean";

	/**
	 *
	 * @param request
	 *            HttpServletRequest
	 */
	public FormProcessor(HttpServletRequest request) {
		this.request = request;
		this.presetValues = new HashMap();
	}

	/**
	 * @return Returns the request.
	 */
	public HttpServletRequest getRequest() {
		return request;
	}

	/**
	 * @param request
	 *            The request to set.
	 */
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	/**
	 * @return Returns the presetValues.
	 */
	public HashMap getPresetValues() {
		return presetValues;
	}

	/**
	 * @param presetValues
	 *            The presetValues to set.
	 */
	public void setPresetValues(HashMap presetValues) {
		this.presetValues = presetValues;
	}

	/**
	 * Clears preset values.
	 */
	public void clearPresetValues() {
		presetValues = new HashMap();
	}

	/**
	 *
	 * @param fieldName
	 *            String
	 * @param searchAttributes
	 *            boolean
	 * @return String
	 */
	public String getString(String fieldName, boolean searchAttributes) {
		String result = DEFAULT_STRING;

		if (searchAttributes) {
			result = request.getAttribute(fieldName) != null ? request.getAttribute(fieldName).toString() : null;

			if (result == null) {
				result = request.getParameter(fieldName);

				if (result == null) {
					return DEFAULT_STRING;
				}
			}
		} else {
			result = request.getParameter(fieldName);
			if (result == null) {
				return DEFAULT_STRING;
			}
		}
		return result;
	}

	/**
	 *
	 * @param fieldName
	 *            String
	 * @return String
	 */
	public String getString(String fieldName) {
		return getString(fieldName, false);
	}

	/**
	 * For an input which is supposed to return an array of strings, such as a checkbox or multiple-select input,
	 * retrieve all of those strings in an ArrayList.
	 *
	 * Note that the values must be contained in the request parameters, not in the attributes.
	 *
	 * @param fieldName
	 *            The name of the input
	 * @return An array of all the Strings corresponding to that input. Guaranteed to be non-null. All elements
	 *         guaranteed to be non-null.
	 */
	public ArrayList getStringArray(String fieldName) {
		ArrayList answer = new ArrayList();

		String[] values = request.getParameterValues(fieldName);

		if (values != null) {
			for (String element : values) {
				if (element != null) {
					answer.add(element);
				}
			}
		}

		return answer;
	}

	/**
	 *
	 * @param partialFieldName
	 *            String
	 * @return boolean
	 */
	public boolean getStartsWith(String partialFieldName) {
		boolean answer = false;
		CharSequence seq = partialFieldName.subSequence(0, partialFieldName.length());
		java.util.Enumeration<String> names = request.getParameterNames();

		while (names.hasMoreElements()) {

			String name = names.nextElement();
			if (name.contains(seq)) {
				return true;
			}

		}
		return answer;
	}

	/**
	 *
	 * @param value
	 *            String
	 * @return int
	 */
	public static int getIntFromString(String value) {
		if (value == null) {
			return DEFAULT_INT;
		}

		int result;

		try {
			result = Integer.parseInt(value);
		} catch (Exception e) {
			result = DEFAULT_INT;
		}

		return result;
	}

	/**
	 *
	 * @param fieldName
	 *            String
	 * @param searchAttributes
	 *            boolean
	 * @return int
	 */
	public int getInt(String fieldName, boolean searchAttributes) {
		String fieldValue = getString(fieldName, searchAttributes);
		return FormProcessor.getIntFromString(fieldValue);
	}

	/**
	 *
	 * @param fieldName
	 *            String
	 * @return int
	 */
	public int getInt(String fieldName) {
		return getInt(fieldName, false);
	}

	/**
	 *
	 * @param fieldName
	 *            String
	 * @param searchAttributes
	 *            boolean
	 * @return int
	 * @throws Exception
	 *             in case of failure
	 */
	public int getPresentInt(String fieldName, boolean searchAttributes) throws Exception {
		String fieldValue = getString(fieldName, searchAttributes);
		int result;

		try {
			result = Integer.parseInt(fieldValue);
		} catch (Exception e) {
			throw new Exception("The attribute or parameter with name " + fieldName
					+ " is not an integer; the form is corrupt.");
		}

		return result;
	}

	/**
	 *
	 * @param fieldName
	 *            String
	 * @return int
	 * @throws Exception
	 *             in case of failure
	 */
	public int getPresentInt(String fieldName) throws Exception {
		return getPresentInt(fieldName, false);
	}

	/**
	 *
	 * @param fieldName
	 *            String
	 * @param searchAttributes
	 *            boolean
	 * @return boolean
	 */
	public boolean getBoolean(String fieldName, boolean searchAttributes) {
		int fieldValue = getInt(fieldName, searchAttributes);

		if (fieldValue != 0) {
			return true;
		}
		return DEFAULT_BOOLEAN;
	}

	/**
	 *
	 * @param fieldName
	 *            String
	 * @return boolean
	 */
	public boolean getBoolean(String fieldName) {
		return getBoolean(fieldName, false);
	}

	/**
	 *
	 * @param fieldName
	 *            String
	 * @param searchAttributes
	 *            boolean
	 * @return float
	 */
	public float getFloat(String fieldName, boolean searchAttributes) {
		String fieldValue = getString(fieldName, searchAttributes);
		float fltValue;

		try {
			fltValue = Float.parseFloat(fieldValue);
		} catch (Exception e) {
			fltValue = DEFAULT_FLOAT;
		}

		return fltValue;
	}

	/**
	 *
	 * @param fieldName
	 *            String
	 * @return float
	 */
	public float getFloat(String fieldName) {
		return getFloat(fieldName, false);
	}

	/**
	 * @param fieldName
	 *            The name of the HTML form field which holds the Entity's primary key.
	 * @param edao
	 *            The data source for the Entity.
	 * @return The Entity whose primary key is specified by fieldName, and which can be retrieved by edao.
	 * @throws OpenClinicaException
	 *             in case of failure
	 */
	public EntityBean getEntity(String fieldName, EntityDAO edao) throws OpenClinicaException {
		int id = getInt(fieldName);
		EntityBean result = edao.findByPK(id);
		return result;
	}

	/**
	 *
	 * @param date
	 *            String
	 * @return Date
	 * @deprecated use {@link #getDate(String) getDate} instead.
	 */
	@Deprecated
	public static Date getDateFromString(String date) {
		Date answer;
		ResourceBundle resformat = ResourceBundleProvider.getFormatBundle();
		try {
			SimpleDateFormat f = new SimpleDateFormat(resformat.getString("date_format_string"),
					ResourceBundleProvider.getLocale());
			f.setLenient(false);
			answer = f.parse(date);
		} catch (Exception e) {
			answer = DEFAULT_DATE;
		}
		return answer;
	}

	/**
	 * Parses date from the input date string, stored in the http request parameter/attribute.
	 *
	 * @param fieldName        request parameter name, which stores input date string.
	 * @param datePattern      expected date format.
	 * @param searchAttributes if <code>true</code>, then method searches for input date string
	 *                         both in request parameters and attributes;
	 *                         if <code>false</code>, then method searches for input date string
	 *                         in request parameters only.
	 * @return date object, created from the input date string.
	 */
	public Date getDate(String fieldName, DateUtil.DatePattern datePattern, boolean searchAttributes) {
		String fieldValue = getString(fieldName, searchAttributes);
		return DateUtil.parseDateString(fieldValue, datePattern, LocaleResolver.getLocale());
	}

	/**
	 * Parses date from the input date string, stored in the http request parameter.
	 *
	 * @param fieldName   request parameter name, which stores input date string.
	 * @param datePattern expected date format
	 * @return date object, created from the input date string.
	 */
	public Date getDate(String fieldName, DateUtil.DatePattern datePattern) {
		return getDate(fieldName, datePattern, false);
	}

	/**
	 * Parses date from the input date string, stored in the http request parameter.
	 * The date format <code>dd-Mmm-yyyy</code> is expected for input date string.
	 *
	 * @param fieldName request parameter name, which stores input date string.
	 * @return date object, created from the input date string.
	 */
	public Date getDate(String fieldName) {
		return getDate(fieldName, DateUtil.DatePattern.DATE, false);
	}

	/**
	 * Parses date from the input date string, stored in the http request parameter/attribute,
	 * and translates it from the user's time zone into the server time zone.
	 * The date format <code>dd-Mmm-yyyy</code> is expected for input date string.
	 * The time of the day for the result date object will be set to current server time of the day.
	 *
	 * @param searchAttributes if <code>true</code>, then method searches for input date string
	 *                         both in request parameters and attributes;
	 *                         if <code>false</code>, then method searches for input date string
	 *                         in request parameters only.
	 * @param fieldName        request parameter name, which stores input date string.
	 * @return date object, created from the input date string.
	 */
	public Date getDateInputWithServerTimeOfDay(String fieldName, boolean searchAttributes) {
		String fieldValue = getString(fieldName, searchAttributes);
		return DateUtil.parseDateStringToServerDateTime(fieldValue, getCurrentUser().getUserTimeZoneId(),
				DateUtil.DatePattern.DATE, LocaleResolver.getLocale(request), true);
	}

	/**
	 * Parses date from the input date string, stored in the http request parameter,
	 * and translates it from the user's time zone into the server time zone.
	 * The date format <code>dd-Mmm-yyyy</code> is expected for input date string.
	 * The time of the day for the result date object will be set to current server time of the day.
	 *
	 * @param fieldName request parameter name, which stores input date string.
	 * @return date object, created from the input date string.
	 */
	public Date getDateInputWithServerTimeOfDay(String fieldName) {
		return getDateInputWithServerTimeOfDay(fieldName, false);
	}

	/**
	 * Parses date from the input date string, stored in the http request parameter,
	 * and translates it from the user's time zone into the server time zone.
	 * The date format <code>dd-Mmm-yyyy</code> is expected for input date string.
	 * The time of the day for the result date object will be set to current server time of the day.
	 *
	 * @param inputDateParamName         request parameter name, which stores input date string.
	 * @param currentValueOfDateProperty current value of date property stored in the data base.
	 * @return <code>null</code>, if a user leave date input empty;
	 * date object, created from the input date string, if the date input was changed by a user;
	 * value of <code>currentValueOfDateProperty</code> parameter, if the date input was not changed.
	 */
	public Date getUpdatedDateProperty(String inputDateParamName, Date currentValueOfDateProperty) {

		if (!StringUtil.isBlank(getString(inputDateParamName))) {
			if (currentValueOfDateProperty != null) {
				String printedDate = DateUtil.printDate(currentValueOfDateProperty,
						getCurrentUser().getUserTimeZoneId(), DateUtil.DatePattern.DATE,
						LocaleResolver.getLocale(request));
				if (!printedDate.equals(getString(inputDateParamName))) {
					return getDateInputWithServerTimeOfDay(inputDateParamName);
				} else {
					return currentValueOfDateProperty;
				}
			} else {
				return getDateInputWithServerTimeOfDay(inputDateParamName);
			}
		} else {
			return null;
		}
	}

	/**
	 * Parses date from the input date and time strings, stored in the http request parameters,
	 * and translates it from the user's time zone into the server time zone.
	 * If exact time of a day is not provided, then it will default to 12:00.
	 *
	 * @param prefix request parameters name prefix, which store input date, hour and minute values.
	 * @return date object, created from the input date and time strings.
	 */
	public Date getDateTimeInput(String prefix) {

		String date = getString(prefix + "Date");
		String hour = getString(prefix + "Hour");
		String minute = getString(prefix + "Minute");
		if (hour.startsWith("-1")) {
			hour = "12";
		} else if (hour.length() == 1) {
			hour = "0" + hour;
		}
		if (minute.startsWith("-1")) {
			minute = "00";
		} else if (minute.length() == 1) {
			minute = "0" + minute;
		}
		String fieldValue = date + " " + hour + ":" + minute + ":00";
		return DateUtil.parseDateStringToServerDateTime(fieldValue, getCurrentUser().getUserTimeZoneId(),
				DateUtil.DatePattern.TIMESTAMP_WITH_SECONDS, LocaleResolver.getLocale(request));
	}

	/**
	 * @return true if the form was submitted; false otherwise.
	 */
	public boolean isSubmitted() {
		return getBoolean(FIELD_SUBMITTED, true);
	}

	/**
	 *
	 * @param fieldName
	 *            String
	 * @param value
	 *            int
	 */
	public void addPresetValue(String fieldName, int value) {
		Integer fieldValue = new Integer(value);
		presetValues.put(fieldName, fieldValue);
	}

	/**
	 *
	 * @param fieldName
	 *            String
	 * @param value
	 *            float
	 */
	public void addPresetValue(String fieldName, float value) {
		Float fieldValue = new Float(value);
		presetValues.put(fieldName, fieldValue);
	}

	/**
	 *
	 * @param fieldName
	 *            String
	 * @param value
	 *            String
	 */
	public void addPresetValue(String fieldName, boolean value) {
		Boolean fieldValue = new Boolean(value);
		presetValues.put(fieldName, fieldValue);
	}

	/**
	 *
	 * @param fieldName
	 *            String
	 * @param fieldValue
	 *            String
	 */
	public void addPresetValue(String fieldName, String fieldValue) {
		presetValues.put(fieldName, fieldValue);
	}

	/**
	 * @param fieldName
	 *            The name of the HTML form field whose value should be the Entity's primary key.
	 * @param value
	 *            The Entity whose primary key will populate the HTML form field.
	 */
	public void addPresetValue(String fieldName, EntityBean value) {
		presetValues.put(fieldName, value);
	}

	/**
	 *
	 * @param fieldNames
	 *            String[]
	 */
	public void setCurrentStringValuesAsPreset(String[] fieldNames) {
		for (String fieldName : fieldNames) {
			String fieldValue = getString(fieldName);
			addPresetValue(fieldName, fieldValue);
		}
	}

	/**
	 *
	 * @param fieldNames
	 *            String[]
	 */
	public void setCurrentIntValuesAsPreset(String[] fieldNames) {
		for (String fieldName : fieldNames) {
			int fieldValue = getInt(fieldName);
			addPresetValue(fieldName, fieldValue);
		}
	}

	/**
	 *
	 * @param fieldNames
	 *            String[]
	 */
	public void setCurrentBoolValuesAsPreset(String[] fieldNames) {
		for (String fieldName : fieldNames) {
			boolean fieldValue = getBoolean(fieldName);
			addPresetValue(fieldName, fieldValue);
		}
	}

	/**
	 * Propogates values in date/time fields to the preset values, so that they can be used to populate a form.
	 *
	 * In particular, for each prefix in prefixes, the following strings are loaded in from the form, and propagated to
	 * the preset values: prefix + "Date" prefix + "Hour" prefix + "Minute" prefix + "Half"
	 *
	 * @param prefixes
	 *            An array of Strings. Each String is a prefix for a set of date/time fields.
	 */
	public void setCurrentDateTimeValuesAsPreset(String[] prefixes) {
		for (String prefix : prefixes) {
			String fieldName = prefix + "Date";
			String date = getString(fieldName);
			addPresetValue(fieldName, date);

			fieldName = prefix + "Hour";
			int hour = getInt(fieldName);
			addPresetValue(fieldName, hour);

			fieldName = prefix + "Minute";
			int minute = getInt(fieldName);
			addPresetValue(fieldName, minute);

			fieldName = prefix + "Half";
			String half = getString(fieldName);
			addPresetValue(fieldName, half);

		}
	}

	/**
	 * Return a String which concatenates inputed "Date", "Hour", "Minute" and "am/pm" if applicable. Empty string will
	 * be returned if none of them has been entered.
	 *
	 * @param prefix
	 *            String
	 * @return String
	 */
	public String getDateTimeInputString(String prefix) {
		String str = "";
		str = getString(prefix + "Date");
		String temp = getString(prefix + "Hour");
		str += "-1".equals(temp) ? "" : temp;
		temp = getString(prefix + "Minute");
		str += "-1".equals(temp) ? "" : temp;
		temp = getString(prefix + "Half");
		str += temp == null || "-1".equals(temp) ? "" : temp;

		return str;
	}

	/**
	 * Precondition: is a valid datetime.
	 *
	 * @param prefix
	 *            String
	 * @return boolean
	 */
	public boolean timeEntered(String prefix) {
		ResourceBundle resformat = ResourceBundleProvider.getFormatBundle();
		if (!"-1".equals(getString(prefix + "Hour")) && !"-1".equals(getString(prefix + "Minute"))) {
			if (resformat.getString("date_time_format_string").contains("HH")) {
				return true;
			} else {
				if (!"".equals(getString(prefix + "Half"))) {
					return true;
				}
			}
		}
		return false;
	}

	private UserAccountBean getCurrentUser() {
		return (UserAccountBean) getRequest().getSession().getAttribute(CURRENT_USER_ATTR_NAME);
	}
}
