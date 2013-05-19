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
package org.akaza.openclinica.bean.managestudy;

import org.akaza.openclinica.bean.core.Term;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"rawtypes", "unchecked", "serial"})
public class StudyType extends Term {

	public static final StudyType INVALID = new StudyType(0, "");

	public static final StudyType GENETIC = new StudyType(1, "genetic");

	public static final StudyType NONGENETIC = new StudyType(2, "non_genetic");

	private static final StudyType[] members = { GENETIC, NONGENETIC };

	public static final List list = Arrays.asList(members);

	private StudyType(int id, String name) {
		super(id, name);
	}

	public static boolean contains(int id) {
		return Term.contains(id, list);
	}

	public static StudyType get(int id) {
		Term t = Term.get(id, list);
		if (!t.isActive()) {
			return StudyType.INVALID;
		}
		return (StudyType) t;
	}

	public static ArrayList toArrayList() {
		return new ArrayList(list);
	}

}
