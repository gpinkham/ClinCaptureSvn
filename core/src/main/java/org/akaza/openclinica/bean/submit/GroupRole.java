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

import org.akaza.openclinica.bean.core.Term;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @deprecated
 */
@SuppressWarnings({"rawtypes", "unchecked", "serial"})
public class GroupRole extends Term {
	public static final GroupRole INVALID = new GroupRole(0, "invalid");
	public static final GroupRole PROBAND = new GroupRole(1, "proband");

	private static final GroupRole[] members = { PROBAND };

	public static final List list = Arrays.asList(members);

	private GroupRole(int id, String name) {
		super(id, name);
	}

	private GroupRole() {
	}

	public static boolean contains(int id) {
		return Term.contains(id, list);
	}

	public static GroupRole get(int id) {
		return (GroupRole) Term.get(id, list);
	}

	public static ArrayList toArrayList() {
		return new ArrayList(list);
	}

	@Override
	public String getName() {
		return name;
	}

}
