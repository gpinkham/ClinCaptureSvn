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
 * Status.
 */
@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
public final class Status extends Term implements Comparable {

	public static final Status INVALID = new Status(0, "invalid");
	public static final Status AVAILABLE = new Status(1, "available");
	public static final Status PENDING = new Status(4, "pending");
	public static final Status PRIVATE = new Status(3, "private");
	public static final Status UNAVAILABLE = new Status(2, "unavailable");
	public static final Status DELETED = new Status(5, "removed");
	public static final Status LOCKED = new Status(6, "locked");
	public static final Status AUTO_DELETED = new Status(7, "auto-removed");
	public static final Status SIGNED = new Status(8, "signed");
	public static final Status FROZEN = new Status(9, "frozen");
	public static final Status SOURCE_DATA_VERIFIED = new Status(10, "source_data_verified");
	public static final Status NOT_STARTED = new Status(11, "not_started");
	public static final Status DATA_ENTRY_STARTED = new Status(12, "data_entry_started");
	public static final Status INITIAL_DATA_ENTRY_COMPLETED = new Status(13, "initial_data_entry_completed");
	public static final Status DOUBLE_DATA_ENTRY = new Status(14, "double_data_entry");
	public static final Status COMPLETED = new Status(15, "completed");
	public static final Status INITIAL = new Status(16, "initial");
	public static final Status PARTIAL_DATA_ENTRY = new Status(17, "partial_data_entry");
	public static final Status PARTIAL_DOUBLE_DATA_ENTRY = new Status(18, "partial_double_data_entry");

	private static final Status[] MEMBERS = { INVALID, AVAILABLE, PENDING, PRIVATE, UNAVAILABLE, LOCKED, DELETED,
			AUTO_DELETED, SIGNED, FROZEN, SOURCE_DATA_VERIFIED, NOT_STARTED, DATA_ENTRY_STARTED, PARTIAL_DATA_ENTRY,
			PARTIAL_DOUBLE_DATA_ENTRY, INITIAL_DATA_ENTRY_COMPLETED, DOUBLE_DATA_ENTRY, COMPLETED, INITIAL };
	private static List list = Arrays.asList(MEMBERS);

	private static final Status[] ACTIVE_MEMBERS = { AVAILABLE, SIGNED, DELETED, AUTO_DELETED };
	private static List activeList = Arrays.asList(ACTIVE_MEMBERS);

	private static final Status[] STUDY_SUBJECT_DROP_DOWN_MEMBERS = { AVAILABLE, SIGNED, DELETED, LOCKED };
	private static List studySubjectDropDownList = Arrays.asList(STUDY_SUBJECT_DROP_DOWN_MEMBERS);

	private static final Status[] SUBJECT_DROP_DOWN_MEMBERS = { AVAILABLE, DELETED };
	private static List subjectDropDownList = Arrays.asList(SUBJECT_DROP_DOWN_MEMBERS);

	private static final Status[] STUDY_UPDATE_MEMBERS = { PENDING, AVAILABLE, FROZEN, LOCKED };
	private static List studyUpdateMembersList = Arrays.asList(STUDY_UPDATE_MEMBERS);

	private static List crfStatusList = Arrays.asList(NOT_STARTED, DATA_ENTRY_STARTED, PARTIAL_DATA_ENTRY, PARTIAL_DOUBLE_DATA_ENTRY,
			INITIAL_DATA_ENTRY_COMPLETED, DOUBLE_DATA_ENTRY, COMPLETED, SOURCE_DATA_VERIFIED, SIGNED, LOCKED, DELETED);

	private Status(int id, String name) {
		super(id, name);
	}

	private Status() {
	}

	/**
	 *
	 * @param id int
	 * @return boolean
	 */
	public static boolean contains(int id) {
		return Term.contains(id, list);
	}

	/**
	 *
	 * @param id int
	 * @return Status
	 */
	public static Status get(int id) {
		return (Status) Term.get(id, list);
	}

	/**
	 *
	 * @param name String
	 * @return Status
	 */
	public static Status getByName(String name) {
		for (Object aList : list) {
			Status temp = (Status) aList;
			if (temp.getName().equals(name)) {
				return temp;
			}
		}
		return INVALID;
	}

	/**
	 *
	 * @return ArrayList
	 */
	public static ArrayList toArrayList() {
		return new ArrayList(list);
	}

	/**
	 *
	 * @return ArrayList
	 */
	public static ArrayList toActiveArrayList() {
		return new ArrayList(activeList);
	}

	/**
	 *
	 * @return ArrayList
	 */
	public static ArrayList toDropDownArrayList() {
		return new ArrayList(studySubjectDropDownList);
	}

	/**
	 *
	 * @return ArrayList
	 */
	public static ArrayList toStudyUpdateMembersList() {
		return new ArrayList(studyUpdateMembersList);
	}

	/**
	 *
	 * @return ArrayList
	 */
	public static ArrayList toSubjectDropDownArrayList() {
		return new ArrayList(subjectDropDownList);
	}

	/**
	 *
	 * @return ArrayList
	 */
	public static ArrayList<Status> toCRFStatusDropDownList() {
		return new ArrayList<Status>(crfStatusList);
	}

	/**
	 *
	 * @param o Object
	 * @return int
	 */
	public int compareTo(Object o) {
		if (!this.getClass().equals(o.getClass())) {
			return 0;
		}

		Status arg = (Status) o;

		return name.compareTo(arg.getName());

	}

	public boolean isInvalid() {
		return this == Status.INVALID;
	}

	public boolean isAvailable() {
		return this == Status.AVAILABLE;
	}

	public boolean isPending() {
		return this == Status.PENDING;
	}

	public boolean isPrivate() {
		return this == Status.PRIVATE;
	}

	public boolean isUnavailable() {
		return this == Status.UNAVAILABLE;
	}

	public boolean isDeleted() {
		return this == Status.DELETED || this == Status.AUTO_DELETED;
	}

	public boolean isAutoDeleted() {
		return this == Status.AUTO_DELETED;
	}

	public boolean isLocked() {
		return this == Status.LOCKED;
	}

	public boolean isCompleted() {
		return this == Status.COMPLETED;
	}

	public boolean isSDVed() {
		return this == Status.SOURCE_DATA_VERIFIED;
	}

	public boolean isSigned() {
		return this == Status.SIGNED;
	}

	public boolean isFrozen() {
		return this == Status.FROZEN;
	}

	public boolean isNotStarted() {
		return this == Status.NOT_STARTED;
	}

	public boolean isPartialDataEntry() {
		return this == Status.PARTIAL_DATA_ENTRY;
	}

	public boolean isPartialDoubleDataEntry() {
		return this == Status.PARTIAL_DOUBLE_DATA_ENTRY;
	}
	
	public boolean isDataEntryStarted() {
		return this == Status.DATA_ENTRY_STARTED;
	}

	public boolean isInitialDataEntryCompleted() {
		return this == Status.INITIAL_DATA_ENTRY_COMPLETED;
	}

	public boolean isDoubleDataEntry() {
		return this == Status.DOUBLE_DATA_ENTRY;
	}
}
