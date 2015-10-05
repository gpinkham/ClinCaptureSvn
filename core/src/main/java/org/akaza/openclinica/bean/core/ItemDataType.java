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
package org.akaza.openclinica.bean.core;

import java.util.Arrays;
import java.util.List;

/**
 * BL - Boolean
 * 
 * BN - BooleanNonNull
 * 
 * ED - Encapsulated Data (Files w/ specified MIME type e.g. ED-pdf, ED-jpg defined in a separate ED Types table in the
 * future)
 * 
 * TEL - A telecommunication address (TEL), such as a URL for HTTP or FTP, which will resolve to precisely the same
 * binary data that could as well have been provided as inline data.
 * 
 * ST - Character String
 * 
 * INTEGER - Integer
 * 
 * REAL - Floating
 * 
 * SET - a value that contains other distinct values in no particular order.
 * 
 * DATE - a date type
 * 
 * @author ssachs
 */
@SuppressWarnings({"rawtypes", "serial"})
public final class ItemDataType extends Term {

	public static final String LABEL_SPLITTER = "<![CDATA[LABEL]]>";

	public static final ItemDataType INVALID = new ItemDataType(0, "invalid", "Invalid_Type");
	public static final ItemDataType BL = new ItemDataType(1, "bl", "Boolean");

	public static final ItemDataType BN = new ItemDataType(2, "bln", "Boolean_Non_Null");

	public static final ItemDataType ED = new ItemDataType(3, "ed", "encapsulated_data");

	public static final ItemDataType TEL = new ItemDataType(4, "tel", "URL");

	public static final ItemDataType ST = new ItemDataType(5, "st", "character_string");

	public static final ItemDataType INTEGER = new ItemDataType(6, "int", "integer");

	public static final ItemDataType REAL = new ItemDataType(7, "real", "floating");

	public static final ItemDataType SET = new ItemDataType(8, "set", "set");
	public static final ItemDataType DATE = new ItemDataType(9, "date", "date");
	public static final ItemDataType PDATE = new ItemDataType(10, "pdate", "partial_date");
	public static final ItemDataType FILE = new ItemDataType(11, "file", "file");
	public static final ItemDataType CODE = new ItemDataType(12, "code", "code");

	public static final ItemDataType DIVIDER = new ItemDataType(13, "divider", "divider"); // supported in FS only
	public static final ItemDataType LABEL = new ItemDataType(14, "label", "label"); // supported in FS only

	private static final ItemDataType[] MEMBERS = {BL, BN, ED, TEL, ST, INTEGER, REAL, SET, DATE, PDATE, FILE, CODE,
			DIVIDER, LABEL};

	public static final List LIST = Arrays.asList(MEMBERS);

	private ItemDataType(int id, String name, String description) {
		super(id, name, description);
	}

	private ItemDataType() {
	}

	/**
	 * Checks the presence of ItemDataType by id.
	 *
	 * @param id
	 *            int
	 * @return boolean
	 */
	public static boolean contains(int id) {
		return Term.contains(id, LIST);
	}

	/**
	 * Finds the ItemDataType by id.
	 *
	 * @param id
	 *            int
	 * @return ItemDataType
	 */
	public static ItemDataType get(int id) {
		Term term = Term.get(id, LIST);
		return term instanceof ItemDataType ? (ItemDataType) term : null;
	}

	/**
	 * Finds the ItemDataType by name.
	 *
	 * @param name
	 *            String
	 * @return ItemDataType
	 */
	public static ItemDataType getByName(String name) {
		for (Object aList : LIST) {
			ItemDataType temp = (ItemDataType) aList;
			if (temp.getName().equalsIgnoreCase(name)) {
				return temp;
			}
		}
		return ItemDataType.INVALID;
	}

	/**
	 * Checks the existence of ItemDataType by name.
	 * 
	 * @param name
	 *            String
	 * @return boolean
	 */
	public static boolean findByName(String name) {
		for (Object aList : LIST) {
			ItemDataType temp = (ItemDataType) aList;
			if (temp.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getName() {
		return name;
	}
}
