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
package org.akaza.openclinica.bean.submit;

import org.akaza.openclinica.bean.core.Term;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author jxu, modified by ywang
 * 
 *         TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style -
 *         Code Templates
 */
@SuppressWarnings({"rawtypes", "unchecked", "serial"})
public class GroupType extends Term {
	
	public static final GroupType INVALID = new GroupType(0, "invalid");
	public static final GroupType ARM = new GroupType(1, "Arm");
	
	public static final GroupType FAMILY = new GroupType(2, "Family/Pedigree");
	public static final GroupType DEMOGRAPHIC = new GroupType(3, "Demographic");
	public static final GroupType OTHER = new GroupType(4, "Other");
	private static final GroupType[] members = { ARM, FAMILY, DEMOGRAPHIC, OTHER };

	public static final List list = Arrays.asList(members);

	private GroupType(int id, String name) {
		super(id, name);
	}

	private GroupType() {
	}

	public static boolean contains(int id) {
		return Term.contains(id, list);
	}

	public static GroupType get(int id) {
		return (GroupType) Term.get(id, list);
	}

	public static ArrayList toArrayList() {
		return new ArrayList(list);
	}

}
