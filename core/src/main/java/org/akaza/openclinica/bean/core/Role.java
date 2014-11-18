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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * User role types container.
 * 
 */

@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
public final class Role extends Term {

	public static final Role INVALID = new Role(0, "invalid", "invalid");
	public static final Role SYSTEM_ADMINISTRATOR = new Role(1, "system_administrator", "System_Administrator");
	public static final Role STUDY_ADMINISTRATOR = new Role(2, "study_administrator", "Study_Administrator");
	public static final Role STUDY_DIRECTOR = new Role(3, "study_director", "Study_Director");
	public static final Role INVESTIGATOR = new Role(4, "investigator", "Investigator");
	public static final Role CLINICAL_RESEARCH_COORDINATOR = new Role(5, "clinical_research_coordinator",
			"Clinical_Research_Coordinator");
	public static final Role STUDY_MONITOR = new Role(6, "study_monitor", "Study_Monitor");
	public static final Role STUDY_CODER = new Role(7, "study_coder", "study_coder");
	public static final Role STUDY_EVALUATOR = new Role(8, "study_evaluator", "study_evaluator");
	public static final Role SITE_MONITOR = new Role(9, "site_monitor", "site_monitor");

	private static final Role[] MEMBERS = { SYSTEM_ADMINISTRATOR, STUDY_ADMINISTRATOR, STUDY_DIRECTOR, STUDY_MONITOR,
			INVESTIGATOR, CLINICAL_RESEARCH_COORDINATOR, STUDY_CODER, STUDY_EVALUATOR, SITE_MONITOR };
	public static final List MEMBERS_LIST = Arrays.asList(MEMBERS);

	public static final Map ROLE_MAP = new LinkedHashMap();
	public static final Map ROLE_MAP_WITH_DESCRIPTION = new LinkedHashMap();

	static {
		int index = 1;
		ROLE_MAP.put(index++, "System_Administrator");
		ROLE_MAP.put(index++, "Study_Administrator");
		ROLE_MAP.put(index++, "Study_Director");
		ROLE_MAP.put(index++, "Investigator");
		ROLE_MAP.put(index++, "Clinical_Research_Coordinator");
		ROLE_MAP.put(index++, "Study_Monitor");
		ROLE_MAP.put(index++, "study_coder");
		ROLE_MAP.put(index++, "study_evaluator");
		ROLE_MAP.put(index, "site_monitor");
	}

	static {
		int index = 1;
		ROLE_MAP_WITH_DESCRIPTION.put(index++, "System_Administrator");
		ROLE_MAP_WITH_DESCRIPTION.put(index++, "Study_Administrator");
		ROLE_MAP_WITH_DESCRIPTION.put(index++, "Study_Director");
		ROLE_MAP_WITH_DESCRIPTION.put(index++, "Investigator");
		ROLE_MAP_WITH_DESCRIPTION.put(index++, "Clinical_Research_Coordinator");
		ROLE_MAP_WITH_DESCRIPTION.put(index++, "Study_Monitor");
		ROLE_MAP_WITH_DESCRIPTION.put(index++, "study_coder");
		ROLE_MAP_WITH_DESCRIPTION.put(index++, "study_evaluator");
		ROLE_MAP_WITH_DESCRIPTION.put(index, "site_monitor");
	}

	private Role(int id, String name, String description) {
		super(id, name, description);
	}

	/**
	 * Prepare role description for user role localisation.
	 * 
	 * @param resterm
	 *            Res term dictionary.
	 */
	public static void prepareRoleMapWithDescriptions(ResourceBundle resterm) {
		for (Role role : (List<Role>) Role.toArrayList()) {
			Role.ROLE_MAP_WITH_DESCRIPTION.put(role.getId(), resterm
					.getString((String) Role.ROLE_MAP.get(role.getId())).trim());
		}
	}

	/**
	 * Check if role id is present in the roles list.
	 * 
	 * @param id
	 *            possible role id.
	 * @return return role bean.
	 */

	public static boolean contains(int id) {
		return Term.contains(id, MEMBERS_LIST);
	}

	/**
	 * Get role by role id.
	 * 
	 * @param id
	 *            role id.
	 * @return Role bean.
	 */
	public static Role get(int id) {
		return (Role) Term.get(id, MEMBERS_LIST);
	}

	/**
	 * Find role by role name.
	 * 
	 * @param name
	 *            role name.
	 * @return Role bean.
	 */
	public static Role getByName(String name) {
		for (Object object : MEMBERS_LIST) {
			Role temp = (Role) object;
			String tempName = name.replace(" ", "_").toLowerCase();
			if (temp.name.equals(name) || temp.name.equals(tempName)) {
				return temp;
			}
		}
		return INVALID;
	}

	/**
	 * Returns list with all role beans.
	 * 
	 * @return list with role beans.
	 */
	public static ArrayList toArrayList() {
		return new ArrayList(MEMBERS_LIST);
	}

	/**
	 * Pick the higher role.
	 * 
	 * @param r1
	 *            the first role for comparing.
	 * @param r2
	 *            the second role for comparing.
	 * @return maximum prioritised role.
	 */
	public static Role max(Role r1, Role r2) {
		if (r1 == null) {
			return r2;
		} else if (r2 == null) {
			return r1;
		} else if (r1 == INVALID) {
			return r2;
		} else if (r2 == INVALID) {
			return r1;
		} else if (r1.getId() < r2.getId()) {
			return r1;
		}
		return r2;
	}

	/**
	 * Checks if role is monitor role.
	 * 
	 * @param role
	 *            Role to check
	 * @return true if yes, false otherwise
	 */
	public static boolean isMonitor(Role role) {
		return role.equals(Role.STUDY_MONITOR) || role.equals(Role.SITE_MONITOR);
	}
}
