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

@SuppressWarnings({"serial", "rawtypes"})
public class TermType extends Term {
	public static final TermType ENTITY_ACTION = new TermType(1, "entity_action");
	public static final TermType ROLE = new TermType(2, "role");
	public static final TermType STATUS = new TermType(3, "status");
	public static final TermType USER_TYPE = new TermType(4, "user_type");
	public static final TermType NUMERIC_COMPARISON_OPERATOR = new TermType(5, "numeric_comparison_operator");

	private static final TermType[] members = { ENTITY_ACTION, ROLE, STATUS, USER_TYPE, NUMERIC_COMPARISON_OPERATOR };
	private static List list = Arrays.asList(members);

	private TermType(int id, String name) {
		super(id, name);
	}

	private TermType() {
	}

	public static boolean contains(int id) {
		return Term.contains(id, list);
	}

	public static TermType get(int id) {
		return (TermType) Term.get(id, list);
	}

	@Override
	public String getName() {
		return name;
	}
}
