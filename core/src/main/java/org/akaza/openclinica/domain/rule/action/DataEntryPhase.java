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

package org.akaza.openclinica.domain.rule.action;

import org.akaza.openclinica.domain.Status;
import org.akaza.openclinica.domain.enumsupport.CodedEnum;

import java.util.HashMap;

public enum DataEntryPhase implements CodedEnum {

	ADMIN_EDITING(1, "Administrative Editing"), INITIAL_DATA_ENTRY(2, "Initial Data Entry"), DOUBLE_DATA_ENTRY(3,
			"Double Data Entry"), IMPORT(4, "Import");

	private int code;
	private String description;

	DataEntryPhase(int code) {
		this(code, null);
	}

	DataEntryPhase(int code, String description) {
		this.code = code;
		this.description = description;
	}

	public static Status getByName(String name) {
		return Status.valueOf(Status.class, name);
	}

	public static DataEntryPhase getByCode(Integer code) {
		HashMap<Integer, DataEntryPhase> enumObjects = new HashMap<Integer, DataEntryPhase>();
		for (DataEntryPhase theEnum : DataEntryPhase.values()) {
			enumObjects.put(theEnum.getCode(), theEnum);
		}
		return enumObjects.get(Integer.valueOf(code));
	}

	public Integer getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

}
