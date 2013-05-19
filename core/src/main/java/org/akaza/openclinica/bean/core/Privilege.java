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
 * @author ssachs
 * 
 *         TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style -
 *         Code Templates
 */
@SuppressWarnings({"rawtypes", "unchecked", "serial"})
public class Privilege extends Term {
	public static final Privilege ADMIN = new Privilege(1, "admin");
	public static final Privilege STUDYDIRECTOR = new Privilege(2, "director");
	public static final Privilege INVESTIGATOR = new Privilege(3, "investigator");
	public static final Privilege RESEARCHASSISTANT = new Privilege(4, "ra");
	public static final Privilege MONITOR = new Privilege(5, "monitor");

	private static final Privilege[] members = { ADMIN, STUDYDIRECTOR, INVESTIGATOR, RESEARCHASSISTANT, MONITOR };
	public static final List list = Arrays.asList(members);

	private Privilege(int id, String name) {
		super(id, name);
	}

	private Privilege() {
	}

	public static boolean contains(int id) {
		return Term.contains(id, list);
	}

	public static Privilege get(int id) {
		Term term = Term.get(id, list);
		return (term instanceof Privilege) ? (Privilege) term : null;
	}

	public static ArrayList toArrayList() {
		return new ArrayList(list);
	}
}
