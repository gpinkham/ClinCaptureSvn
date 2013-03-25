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
package org.akaza.openclinica.bean.submit;

import org.akaza.openclinica.bean.core.Privilege;
import org.akaza.openclinica.bean.core.Term;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * BL - Boolean
 * 
 * BN � BooleanNonNull
 * 
 * ED � Encapsulated Data (Files w/ specified MIME type e.g. ED-pdf, ED-jpg defined in a separate ED Types table in
 * the future)
 * 
 * TEL - A telecommunication address (TEL), such as a URL for HTTP or FTP, which will resolve to precisely the same
 * binary data that could as well have been provided as inline data.
 * 
 * ST � Character String
 * 
 * INT � Integer
 * 
 * REAL � Floating
 * 
 * SET - a value that contains other distinct values in no particular order.
 * 
 * @author Jun Xu
 * @deprecated
 */
@Deprecated
public class DataType extends Term {
	public static final DataType INVALID = new DataType(0, "INVALID", null);
	public static final DataType BN = new DataType(1, "BN", null);
	public static final DataType ED = new DataType(2, "ED", null);
	public static final DataType TEL = new DataType(3, "TEL", null);
	public static final DataType ST = new DataType(4, "ST", null);
	public static final DataType INT = new DataType(5, "INT", null);
	public static final DataType REAL = new DataType(6, "REAL", null);
	public static final DataType SET = new DataType(7, "SET", null);

	private static final DataType[] members = { BN, ED, TEL, ST, INT, REAL, SET };

	public static final List list = Arrays.asList(members);

	private List privileges;

	private DataType(int id, String name, Privilege[] myPrivs) {
		super(id, name);
	}

	private DataType() {
	}

	public static boolean contains(int id) {
		return Term.contains(id, list);
	}

	public static DataType get(int id) {
		return (DataType) Term.get(id, list);
	}

	public static DataType getByName(String name) {
		for (int i = 0; i < list.size(); i++) {
			DataType temp = (DataType) list.get(i);
			if (temp.getName().equals(name)) {
				return temp;
			}
		}
		return new DataType();
	}

	public static boolean findByName(String name) {
		for (int i = 0; i < list.size(); i++) {
			DataType temp = (DataType) list.get(i);
			if (temp.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public static ArrayList toArrayList() {
		return new ArrayList(list);
	}

	@Override
	public String getName() {
		return name;
	}
}
