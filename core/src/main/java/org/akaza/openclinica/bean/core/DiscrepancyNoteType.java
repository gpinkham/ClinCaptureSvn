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
import java.util.Iterator;
import java.util.List;

@SuppressWarnings({"rawtypes", "unchecked", "serial"})
public class DiscrepancyNoteType extends Term {

	public static final DiscrepancyNoteType INVALID = new DiscrepancyNoteType(0, "Invalid", null);

	public static final DiscrepancyNoteType FAILEDVAL = new DiscrepancyNoteType(1, "Failed_Validation_Check", null);

	public static final DiscrepancyNoteType ANNOTATION = new DiscrepancyNoteType(2, "Annotation", null);

	public static final DiscrepancyNoteType QUERY = new DiscrepancyNoteType(3, "query", null);

	public static final DiscrepancyNoteType REASON_FOR_CHANGE = new DiscrepancyNoteType(4, "reason_for_change", null);

	public static final List list = Arrays.asList(FAILEDVAL, ANNOTATION, QUERY, REASON_FOR_CHANGE);

	public static final List<DiscrepancyNoteType> simpleList = Arrays.asList(QUERY, ANNOTATION);

	private List privileges;

	private DiscrepancyNoteType(int id, String name, Privilege[] myPrivs) {
		super(id, name);

	}

	private DiscrepancyNoteType() {
	}

	public static boolean contains(int id) {
		return Term.contains(id, list);
	}

	public static DiscrepancyNoteType get(int id) {
		Term term = Term.get(id, list);
		return term instanceof DiscrepancyNoteType ? (DiscrepancyNoteType) term : null;
	}

	public static DiscrepancyNoteType getByName(String name) {
		for (int i = 0; i < list.size(); i++) {
			DiscrepancyNoteType temp = (DiscrepancyNoteType) list.get(i);
			if (temp.getName().equals(name)) {
				return temp;
			}
		}
		return INVALID;
	}

	public static ArrayList toArrayList() {
		return new ArrayList(list);
	}

	public boolean hasPrivilege(Privilege p) {
		Iterator it = privileges.iterator();

		while (it.hasNext()) {
			Privilege myPriv = (Privilege) it.next();
			if (myPriv.equals(p)) {
				return true;
			}
		}
		return false;
	}

}
