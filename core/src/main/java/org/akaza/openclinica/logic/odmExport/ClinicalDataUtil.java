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
 * OpenClinica is distributed under the GNU Lesser General Public License (GNU
 * LGPL).
 *
 * For details see: http://www.openclinica.org/license copyright 2003-2011 Akaza
 * Research
 *
 */

package org.akaza.openclinica.logic.odmExport;

import java.util.HashMap;

public class ClinicalDataUtil {
	/**
	 * Return true if itValue contains at least one nullValue.
	 * 
	 * @param itValue
	 * @param key
	 * @param nullValueCVs
	 * @return
	 */
	public static Boolean isNull(String itValue, String key, HashMap<String, String> nullValueCVs) {
		if (nullValueCVs.containsKey(key)) {
			String[] nullvalues = nullValueCVs.get(key).split(",");
			String[] values = itValue.split(",");
			for (String v : values) {
				v = v.trim();
				for (String n : nullvalues) {
					if (v.equals(n.trim())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * <p>
	 * Precondition: itValue has null value(s) in it. nullValueStr has no space. nullValueStr starts and ends with ","
	 * </p>
	 * 
	 * @param itValue
	 * @param nullValueStr
	 * @return
	 */
	public static boolean isValueWithNull(String itValue, String nullValueStr) {
		String[] values = itValue.split(",");
		for (String v : values) {
			if (!nullValueStr.contains("," + v.trim() + ",")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * <p>
	 * Precondition: itValue has null value(s) in it. nullValueStr has no space. nullValueStr starts and ends with ","
	 * </p>
	 * 
	 * @param itValue
	 * @param nulls
	 * @return
	 */
	public static String getNullsInValue(String itValue, String nullValueStr) {
		String vnull = ",";
		String[] values = itValue.split(",");
		for (String v : values) {
			v = v.trim();
			if (nullValueStr.contains("," + v + ",") && !vnull.contains("," + v + ",")) {
				vnull += v + ",";
			}
		}
		return vnull.substring(1, vnull.length() - 1);
	}

	/**
	 * <p>
	 * Return nullValueStr with no space, and with "," at the beginning and the end.
	 * </p>
	 * 
	 * @param nullValueStr
	 * @return
	 */
	public static String presetNullValueStr(String nullValueStr) {
		String nullvalues = nullValueStr;
		if (nullvalues.contains(" ")) {
			nullvalues = nullvalues.replace(" ", "");
		}
		nullvalues = nullvalues.endsWith(",") ? "," + nullvalues : "," + nullvalues + ",";
		return nullvalues;
	}
}
