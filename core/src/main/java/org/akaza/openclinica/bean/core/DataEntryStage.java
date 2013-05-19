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

@SuppressWarnings({"rawtypes", "unchecked", "serial"})
public class DataEntryStage extends Term {
	public static final DataEntryStage INVALID = new DataEntryStage(0, "invalid");
	public static final DataEntryStage UNCOMPLETED = new DataEntryStage(1, "not_started", "not_started");
	public static final DataEntryStage INITIAL_DATA_ENTRY = new DataEntryStage(2, "initial_data_entry",
			"data_being_entered");
	public static final DataEntryStage INITIAL_DATA_ENTRY_COMPLETE = new DataEntryStage(3,
			"initial_data_entry_complete", "initial_data_entry_completed");
	public static final DataEntryStage DOUBLE_DATA_ENTRY = new DataEntryStage(4, "double_data_entry", "being_validated");
	public static final DataEntryStage DOUBLE_DATA_ENTRY_COMPLETE = new DataEntryStage(5, "data_entry_complete",
			"validation_completed");
	public static final DataEntryStage ADMINISTRATIVE_EDITING = new DataEntryStage(6, "administrative_editing",
			"completed");
	public static final DataEntryStage LOCKED = new DataEntryStage(7, "locked", "locked");

	private static final DataEntryStage[] members = { UNCOMPLETED, INITIAL_DATA_ENTRY, INITIAL_DATA_ENTRY_COMPLETE,
			DOUBLE_DATA_ENTRY, DOUBLE_DATA_ENTRY_COMPLETE, ADMINISTRATIVE_EDITING, LOCKED };

	public boolean isInvalid() {
		return this == DataEntryStage.INVALID;
	}

	public boolean isUncompleted() {
		return this == DataEntryStage.UNCOMPLETED;
	}

	public boolean isInitialDE() {
		return this == DataEntryStage.INITIAL_DATA_ENTRY;
	}

	public boolean isInitialDE_Complete() {
		return this == DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE;
	}

	public boolean isDoubleDE() {
		return this == DataEntryStage.DOUBLE_DATA_ENTRY;
	}

	public boolean isDoubleDE_Complete() {
		return this == DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE;
	}

	public boolean isAdmin_Editing() {
		return this == DataEntryStage.ADMINISTRATIVE_EDITING;
	}

	public boolean isLocked() {
		return this == DataEntryStage.LOCKED;
	}

	public static final List list = Arrays.asList(members);

	private DataEntryStage(int id, String name) {
		super(id, name);
	}

	private DataEntryStage(int id, String name, String description) {
		super(id, name, description);
	}

	private DataEntryStage() {
	}

	public static boolean contains(int id) {
		return Term.contains(id, list);
	}

	public static DataEntryStage get(int id) {
		DataEntryStage term = DataEntryStage.get(id, list);
		return term == null ? null : (DataEntryStage) term;
	}

	public static DataEntryStage get(int id, List customList) {
		Term term = Term.get(id, customList);
		return term instanceof DataEntryStage ? (DataEntryStage) term : null;
	}

	public static ArrayList toArrayList() {
		return new ArrayList(list);
	}

	public String getNameRaw() {
		return super.name;
	}

	public static DataEntryStage getByName(String name) {
		for (int i = 0; i < list.size(); i++) {
			DataEntryStage temp = (DataEntryStage) list.get(i);
			if (temp.getName().equals(name)) {
				return temp;
			}
		}
		return INVALID;
	}
}
