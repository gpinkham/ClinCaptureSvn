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

import org.akaza.openclinica.i18n.util.ResourceBundleProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@SuppressWarnings({"rawtypes", "unchecked", "serial"})
public class SubjectEventStatus extends Term implements Comparable {
	// waiting for the db to come in sync with our set of terms...
	public static final SubjectEventStatus INVALID = new SubjectEventStatus(0, "invalid");

	public static final SubjectEventStatus SCHEDULED = new SubjectEventStatus(1, "scheduled");

	public static final SubjectEventStatus NOT_SCHEDULED = new SubjectEventStatus(2, "not_scheduled");

	public static final SubjectEventStatus DATA_ENTRY_STARTED = new SubjectEventStatus(3, "data_entry_started");

	public static final SubjectEventStatus COMPLETED = new SubjectEventStatus(4, "completed");

	public static final SubjectEventStatus STOPPED = new SubjectEventStatus(5, "stopped");

	public static final SubjectEventStatus SKIPPED = new SubjectEventStatus(6, "skipped");

	public static final SubjectEventStatus LOCKED = new SubjectEventStatus(7, "locked");

	public static final SubjectEventStatus SIGNED = new SubjectEventStatus(8, "signed");

	public static final SubjectEventStatus SOURCE_DATA_VERIFIED = new SubjectEventStatus(9, "source_data_verified");

	public static final SubjectEventStatus REMOVED = new SubjectEventStatus(10, "removed");

    public static final SubjectEventStatus UNLOCK = new SubjectEventStatus(11, "unlock");

	private static List list = Arrays.asList(new SubjectEventStatus[]{ SCHEDULED, NOT_SCHEDULED, DATA_ENTRY_STARTED, COMPLETED, STOPPED, SKIPPED, SIGNED, LOCKED, SOURCE_DATA_VERIFIED, REMOVED, UNLOCK});

	// Solve the problem with the get() method...
	private static final Map<Integer, String> membersMap = new HashMap<Integer, String>();
	static {
		membersMap.put(0, "invalid");
		membersMap.put(1, "scheduled");
		membersMap.put(2, "not_scheduled");
		membersMap.put(3, "data_entry_started");
		membersMap.put(4, "completed");
		membersMap.put(5, "stopped");
		membersMap.put(6, "skipped");
		membersMap.put(7, "locked");
		membersMap.put(8, "signed");
		membersMap.put(9, "source_data_verified");
		membersMap.put(10, "removed");
	}

	public boolean isSourceDataVerified() {
		return this == SubjectEventStatus.SOURCE_DATA_VERIFIED;
	}

	public boolean isInvalid() {
		return this == SubjectEventStatus.INVALID;
	}

	public boolean isScheduled() {
		return this == SubjectEventStatus.SCHEDULED;
	}

	public boolean isNotScheduled() {
		return this == SubjectEventStatus.NOT_SCHEDULED;
	}

	public boolean isDE_Started() {
		return this == SubjectEventStatus.DATA_ENTRY_STARTED;
	}

	public boolean isCompleted() {
		return this == SubjectEventStatus.COMPLETED || this == SubjectEventStatus.SOURCE_DATA_VERIFIED;
	}

	public boolean isStopped() {
		return this == SubjectEventStatus.STOPPED;
	}

	public boolean isSkipped() {
		return this == SubjectEventStatus.SKIPPED;
	}

	public boolean isLocked() {
		return this == SubjectEventStatus.LOCKED;
	}

	public boolean isSigned() {
		return this == SubjectEventStatus.SIGNED;
	}

	private SubjectEventStatus(int id, String name) {
		super(id, name);
	}

	private SubjectEventStatus() {
	}

	public static SubjectEventStatus getFromMap(int id) {
		if (id < 0 || id > membersMap.size() - 1) {
			return new SubjectEventStatus(0, "invalid");
		}

		return new SubjectEventStatus(id, membersMap.get(id));
	}

	public static boolean contains(int id) {
		return Term.contains(id, list);
	}

	public static SubjectEventStatus get(int id) {
		List allList = new ArrayList(list);
		allList.add(0, SubjectEventStatus.INVALID);
		return (SubjectEventStatus) Term.get(id, allList);
	}

	public static ArrayList toArrayList() {
        ArrayList newList = new ArrayList(list);
        newList.remove(UNLOCK);
		return newList;
	}

	public int compareTo(Object o) {
		if (!this.getClass().equals(o.getClass())) {
			return 0;
		}

		SubjectEventStatus arg = (SubjectEventStatus) o;

		return name.compareTo(arg.getName());
	}

	public static Collection<String> getSubjectEventStatusValues() {
		return membersMap.values();
	}

	public static String getSubjectEventStatusName(int id) {
		if (id < 0 || id > membersMap.size() - 1) {
			return "invalid";
		}

		return membersMap.get(id);
	}

	/**
	 * Return an id for a SubjectEventStatus when given a name like "complete."
	 * 
	 * @param name
	 *            A String name
	 * @return An int id, like 1 for "scheduled"
	 */
	public static int getSubjectEventStatusIdByName(String name) {
		ResourceBundle resterm = ResourceBundleProvider.getTermsBundle();

		if (name == null || "".equalsIgnoreCase(name)) {
			return 0;
		}

		String selectedStatus = null;

		for (String status : getSubjectEventStatusValues()) {
			if (resterm.getString(status) != null) {
				if (name.equalsIgnoreCase(resterm.getString(status).trim())) {
					selectedStatus = status;
					break;
				}
			}
		}

		if (selectedStatus == null) {
			return 0;
		}

		for (int key : membersMap.keySet()) {
			if (selectedStatus.equalsIgnoreCase(getSubjectEventStatusName(key))) {
				return key;
			}
		}
		return 0;
	}

	public static SubjectEventStatus getByCode(int code) {
		for (int i = 0; i < list.size(); i++) {
			SubjectEventStatus temp = (SubjectEventStatus) list.get(i);
			if (temp.getId() == code) {
				return temp;
			}
		}
		return INVALID;
	}

	public static SubjectEventStatus getByName(String name) {
		for (int i = 0; i < list.size(); i++) {
			SubjectEventStatus temp = (SubjectEventStatus) list.get(i);
			if (temp.getName().equals(name)) {
				return temp;
			}
		}
		return INVALID;
	}

}
