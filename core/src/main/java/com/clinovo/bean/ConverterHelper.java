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

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO'S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clinovo.util.DateUtil;

/**
 * ConverterHelper.
 */
public class ConverterHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConverterHelper.class);

	public static final int INT_4 = 4;
	public static final String NULL = "null";
	public static final String COUNER = "-couner";

	private List<String> itemOIDList = new ArrayList<String>();
	private Map<String, Object> globalVariables = new HashMap<String, Object>();

	/**
	 * Sets global variable value.
	 *
	 * @param name
	 *            String
	 * @param value
	 *            Object
	 */
	public void set(String name, Object value) {
		globalVariables.put(name, value);
	}

	/**
	 * Returns global variable value.
	 *
	 * @param name
	 *            String
	 * @return Object
	 */
	public Object get(String name) {
		return globalVariables.get(name);
	}

	/**
	 * Increments group counter.
	 *
	 * @param groupName
	 *            String
	 */
	public void incGroupCounter(String groupName) {
		groupName = groupName.concat(COUNER);
		Object value = globalVariables.get(groupName);
		if (value != null && value instanceof Integer) {
			globalVariables.put(groupName, ((Integer) value + 1));
		} else {
			globalVariables.put(groupName, 1);
		}
	}

	/**
	 * Returns true if needs to break group items.
	 *
	 * @param groupName
	 *            String
	 * @return Integer
	 */
	public boolean breakGroupItems(String groupName) {
		Integer counter = (Integer) globalVariables.get(groupName.concat(COUNER));
		return counter != null && counter > 1 && counter % INT_4 == 0;
	}

	/**
	 * Returns trimmed string.
	 *
	 * @param value
	 *            String
	 * @param defaultValue
	 *            String
	 * @return String
	 */
	public String asString(String value, String defaultValue) {
		value = value == null ? "" : value.trim();
		return value.equalsIgnoreCase(NULL) || value.isEmpty() ? defaultValue : value;
	}

	/**
	 * Returns trimmed string.
	 *
	 * @param value
	 *            String
	 * @return String
	 */
	public String asString(String value) {
		return asString(value, "");
	}

	/**
	 * Returns replaced string.
	 *
	 * @param value
	 *            String
	 * @param search
	 *            String
	 * @param replace
	 *            String
	 * @return String
	 */
	public String replace(String value, String search, String replace) {
		return asString(value).replace(search, replace);
	}

	/**
	 * Returns date.
	 *
	 * @param value
	 *            String
	 * @param dateFormat
	 *            String
	 * @param locale
	 *            Locale
	 * @return String
	 */
	public String formatSystemDate(String value, String dateFormat, Locale locale) {
		value = asString(value);
		try {
			Date date = new SimpleDateFormat(DateUtil.DatePattern.ISO_DATE.getPattern()).parse(value);
			value = new SimpleDateFormat(dateFormat, locale).format(date);
		} catch (Exception ex) {
			LOGGER.error("Error has occurred.", ex);
		}
		return value;
	}

	/**
	 * Returns date.
	 *
	 * @param value
	 *            String
	 * @param dateTimeFormat
	 *            String
	 * @param locale
	 *            Locale
	 * @return String
	 */
	public String formatSystemDateTime(String value, String dateTimeFormat, Locale locale) {
		value = asString(value);
		try {
			Date date = new SimpleDateFormat(DateUtil.DatePattern.ISO_TIMESTAMP.getPattern()).parse(value);
			value = new SimpleDateFormat(dateTimeFormat, locale).format(date);
		} catch (Exception ex) {
			LOGGER.error("Error has occurred.", ex);
		}
		return value.replace("T", "");
	}

	/**
	 * Returns year.
	 *
	 * @param date
	 *            String
	 * @return String
	 */
	public String getYear(String date) {
		date = asString(date);
		String search = date.replaceAll("\\d{4}", "");
		return date.replace(search, "");
	}

	/**
	 * Returns html space.
	 *
	 * @return String
	 */
	public String nbsp() {
		return "&nbsp;";
	}

	/**
	 * Checks if option should be checked.
	 *
	 * @param itemDataValue
	 *            String
	 * @param optionValue
	 *            String
	 * @return boolean
	 */
	public boolean shouldBeChecked(String itemDataValue, String optionValue) {
		boolean result = false;
		optionValue = asString(optionValue);
		itemDataValue = asString(itemDataValue);
		for (String value : itemDataValue.trim().split(",")) {
			if (value.trim().equals(optionValue.trim())) {
				result = true;
				break;
			}
		}
		return result;
	}

	/**
	 * Returns file name.
	 *
	 * @param fileName
	 *            String
	 * @return String
	 */
	public String getFileName(String fileName) {
		fileName = asString(fileName);
		String[] fileNameArray = fileName.split("/|\\\\");
		return fileNameArray.length > 0 ? fileNameArray[fileNameArray.length - 1] : "";
	}

	/**
	 * Init itemOIDList.
	 */
	public void initItemOIDList() {
		itemOIDList = new ArrayList<String>();
	}

	/**
	 * Adds itemOID to itemOIDList.
	 *
	 * @param itemOID
	 *            String
	 */
	public void addItemOID(String itemOID) {
		if (itemOIDList != null) {
			itemOIDList.add(itemOID);
		}
	}

	/**
	 * Returns itemOID from itemOIDList.
	 *
	 * @param index
	 *            String
	 * @return String
	 */
	public String getItemOID(String index) {
		String result = "";
		try {
			Integer ind = Integer.parseInt(index.trim());
			result = itemOIDList != null && ind < itemOIDList.size() ? itemOIDList.get(ind) : "";
		} catch (Exception ex) {
			LOGGER.error("Error has occurred.", ex);
		}
		return result;
	}

	/**
	 * Starts repeating group table.
	 *
	 * @param additionalClass
	 *            String
	 * @return String
	 */
	public String startRepeatingGroupTable(String additionalClass) {
		return "<table border=\"1\" class=\"repeating-group-table " + additionalClass
				+ "\" cellspacing=\"0\" cellpadding=\"0\"><tr>";
	}

	/**
	 * Closes repeating group table.
	 *
	 * @return String
	 */
	public String closeRepeatingGroupTable() {
		return "</tr></table>";
	}

	/**
	 * Returns table for empty repeating group table.
	 *
	 * @param groupOID
	 *            String
	 * @return String
	 */
	public String rowForEmptyRepeatingGroupTable(String groupOID) {
		String result = "";
		Integer counter = (Integer) globalVariables.get(groupOID.concat(COUNER));
		if (counter != null) {
			int size = counter % INT_4;
			result = "<table cellpadding=\"0\" cellspacing=\"0\" class=\"repeating-group-table no_border_top \" border=\"1\"><tr class=\"min-height\">";
			for (int i = 0; i < (size == 0 ? INT_4 : size); i++) {
				result += "<td><div class=\"item_data_div no_border_top\"></div></td>";
			}
			result += "</tr></table>";
		}
		return result;
	}
}
