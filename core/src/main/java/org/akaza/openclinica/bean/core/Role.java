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

import java.util.*;

@SuppressWarnings({"rawtypes", "unchecked",  "serial"})
public class Role extends Term {
	
	public static final String study_coder = "study_coder";
	
	public static final Role INVALID = new Role(0, "invalid", "invalid", null);
	public static final Role SYSTEM_ADMINISTRATOR = new Role(1, "system_administrator", "System_Administrator", null);
	public static final Role STUDY_ADMINISTRATOR = new Role(2, "study_administrator", "Study_Administrator", null);
	// actually clincapture does not use the STUDY_DIRECTOR role.
	public static final Role STUDY_DIRECTOR = new Role(3, "study_director", "Study_Director", null);
	public static final Role STUDY_MONITOR = new Role(6, "study_monitor", "Study_Monitor", null);
	public static final Role INVESTIGATOR = new Role(4, "investigator", "Investigator", null);
	public static final Role CLINICAL_RESEARCH_COORDINATOR = new Role(5, "clinical_research_coordinator",
			"Clinical_Research_Coordinator", null);
	public static final Role STUDY_CODER = new Role(7, "study_coder", study_coder, null);

	private static final Role[] members = { SYSTEM_ADMINISTRATOR, STUDY_ADMINISTRATOR, STUDY_DIRECTOR, STUDY_MONITOR,
			INVESTIGATOR, CLINICAL_RESEARCH_COORDINATOR, STUDY_CODER };
	public static final List list = Arrays.asList(members);

	public static final Map roleMap = new LinkedHashMap();
	static {
		roleMap.put(1, "System_Administrator");
		roleMap.put(2, "Study_Administrator");
		roleMap.put(3, "Study_Director");
		roleMap.put(6, "Study_Monitor");
		roleMap.put(4, "Investigator");
		roleMap.put(5, "Clinical_Research_Coordinator");
		roleMap.put(7, study_coder);
	}

	public static final Map roleMapWithDescriptions = new LinkedHashMap();
	static {
		roleMapWithDescriptions.put(1, "System_Administrator");
		roleMapWithDescriptions.put(2, "Study_Administrator");
		roleMapWithDescriptions.put(3, "Study_Director");
		roleMapWithDescriptions.put(6, "Study_Monitor");
		roleMapWithDescriptions.put(4, "Investigator");
		roleMapWithDescriptions.put(5, "Clinical_Research_Coordinator");
		roleMapWithDescriptions.put(7, study_coder);
	}

    private List privileges;

	private Role(int id, String name, String description, Privilege[] myPrivs) {
		super(id, name, description);
	}

	private Role() {
	}

	public static void prepareRoleMapWithDescriptions(ResourceBundle resterm) {
		for (Role role : (List<Role>) Role.toArrayList()) {
			Role.roleMapWithDescriptions.put(role.getId(), resterm.getString((String) Role.roleMap.get(role.getId()))
					.trim());
		}
	}

	public static boolean contains(int id) {
		return Term.contains(id, list);
	}

	public static Role get(int id) {
		return (Role) Term.get(id, list);
	}

	public static Role getByName(String name) {
		for (int i = 0; i < list.size(); i++) {
			Role temp = (Role) list.get(i);
			if (temp.getName().equals(name) || temp.name.equals(name)) {
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

	/**
	 * Implicitly orders the Role set in the following way:
	 * <ul>
	 * <li>null is the lowest possible Role
	 * <li>INVALID is the next lowest possible Role
	 * <li>The max of two non-null, non-INVALID roles r1 and r2 is the role with the lowest id.
	 * </ul>
	 * 
	 * @param r1
	 * @param r2
	 * @return The maximum of (r1, r2).
	 */
	public static Role max(Role r1, Role r2) {
		if (r1 == null) {
			return r2;
		}
		if (r2 == null) {
			return r1;
		}
		if (r1 == INVALID) {
			return r2;
		}
		if (r2 == INVALID) {
			return r1;
		}

		if (r1.getId() < r2.getId()) {
			return r1;
		}
		return r2;
	}
}
