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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"rawtypes", "unchecked", "serial"})
public class UserType extends Term {

	public static final UserType INVALID = new UserType(0, "invalid");
	public static final UserType SYSADMIN = new UserType(1, "administrator");
	public static final UserType USER = new UserType(2, "user");
	public static final UserType TECHADMIN = new UserType(3, "technical_administrator");

	private static final UserType[] members = { INVALID, USER, SYSADMIN, TECHADMIN };
	public static final List list = Arrays.asList(members);

	private UserType(int id, String name) {
		super(id, name);
	}

	private UserType() {
	}

	public static boolean contains(int id) {
		return Term.contains(id, list);
	}

	public static UserType get(int id) {
		return (UserType) Term.get(id, list);
	}

	public static ArrayList toArrayList() {
		return new ArrayList(list);
	}
}
