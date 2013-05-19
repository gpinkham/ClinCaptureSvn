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

/**
 * Type safe enumeration of study group types
 * 
 * @author Jun Xu
 */
@SuppressWarnings({"rawtypes", "unchecked", "serial"})
public class GroupClassType extends Term {
	public static final GroupClassType INVALID = new GroupClassType(0, "invalid");
	
	public static final GroupClassType ARM = new GroupClassType(1, "Arm");

	public static final GroupClassType FAMILY = new GroupClassType(2, "Family/Pedigree");

	public static final GroupClassType DEMOGRAPHIC = new GroupClassType(3, "Demographic");
	
	public static final GroupClassType DYNAMIC = new GroupClassType(4, "Dynamic_Group");

	public static final GroupClassType OTHER = new GroupClassType(5, "Other");

	private static final GroupClassType[] members = { ARM, FAMILY, DEMOGRAPHIC, DYNAMIC, OTHER };

	public static final List list = Arrays.asList(members);

	private GroupClassType(int id, String name) {
		super(id, name);
	}

	private GroupClassType() {
	}

	public static boolean contains(int id) {
		return Term.contains(id, list);
	}

	public static GroupClassType get(int id) {
		Term t = Term.get(id, list);

		if (!t.isActive() || !(t instanceof GroupClassType)) {
			return INVALID;
		} else {
			return (GroupClassType) t;
		}
	}

	public static boolean findByName(String name) {
		for (int i = 0; i < list.size(); i++) {
			GroupClassType temp = (GroupClassType) list.get(i);
			if (temp.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public static GroupClassType getByName(String name) {
		for (int i = 0; i < list.size(); i++) {
			GroupClassType temp = (GroupClassType) list.get(i);
			if (temp.getName().equals(name)) {
				return temp;
			}
		}
		return GroupClassType.INVALID;
	}

	public static ArrayList toArrayList() {
		return new ArrayList(list);
	}

}
