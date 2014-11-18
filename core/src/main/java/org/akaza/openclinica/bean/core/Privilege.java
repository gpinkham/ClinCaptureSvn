/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2014 Clinovo Inc.
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
 * Users privileges container.
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
public final class Privilege extends Term {

	public static final Privilege SYSTEM_ADMINISTRATOR = new Privilege(1, "system_administrator");
	public static final Privilege STUDY_DIRECTOR = new Privilege(2, "study_director");
	public static final Privilege INVESTIGATOR = new Privilege(3, "investigator");
	public static final Privilege CLINICAL_RESEARCH_COORDINATOR = new Privilege(4, "clinical_research_coordinator");
	public static final Privilege STUDY_MONITOR = new Privilege(5, "study_monitor");
	public static final Privilege STUDY_CODER = new Privilege(6, "study_coder");
	public static final Privilege STUDY_EVALUATOR = new Privilege(7, "study_evaluator");
	public static final Privilege SITE_MONITOR = new Privilege(8, "site_monitor");

	private static final Privilege[] MEMBERS = { SYSTEM_ADMINISTRATOR, STUDY_DIRECTOR, INVESTIGATOR,
			CLINICAL_RESEARCH_COORDINATOR, STUDY_MONITOR, STUDY_CODER, STUDY_EVALUATOR, SITE_MONITOR };
	public static final List MEMBERS_LIST = Arrays.asList(MEMBERS);

	private Privilege(int id, String name) {
		super(id, name);
	}

	/**
	 * Returns true if privilege list contains privilege id.
	 * 
	 * @param id
	 *            the privilege id.
	 * @return true if privilege in the list, otherwise false.
	 */
	public static boolean contains(int id) {
		return Term.contains(id, MEMBERS_LIST);
	}

	/**
	 * Returns <code>Privilege</code> bean if list contains privilege id.
	 * 
	 * @param id
	 *            the privilege id.
	 * @return true if privilege id in the list, null otherwise.
	 */
	public static Privilege get(int id) {
		Term term = Term.get(id, MEMBERS_LIST);
		return (term instanceof Privilege) ? (Privilege) term : null;
	}

	/**
	 * Returns list with all privileges.
	 * 
	 * @return the the list with privileges.
	 */
	public static ArrayList toArrayList() {
		return new ArrayList(MEMBERS_LIST);
	}
}
