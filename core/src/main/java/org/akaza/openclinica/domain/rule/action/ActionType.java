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
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.akaza.openclinica.domain.rule.action;

import org.akaza.openclinica.domain.Status;
import org.akaza.openclinica.domain.enumsupport.CodedEnum;

import java.util.HashMap;

public enum ActionType implements CodedEnum {

	FILE_DISCREPANCY_NOTE(1, "DiscrepancyNoteAction"), EMAIL(2, "EmailAction"), SHOW(3, "ShowAction"), INSERT(4,
			"InsertAction"), HIDE(5, "HideAction"), WEB_SERVICE(6, "WebServiceAction");

	private int code;
	private String description;

	ActionType(int code) {
		this(code, null);
	}

	ActionType(int code, String description) {
		this.code = code;
		this.description = description;
	}

	public static Status getByName(String name) {
		return Status.valueOf(Status.class, name);
	}

	public static ActionType getByCode(Integer code) {
		HashMap<Integer, ActionType> enumObjects = new HashMap<Integer, ActionType>();
		for (ActionType theEnum : ActionType.values()) {
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

	public static ActionType getByDescription(String description) {
		for (ActionType theEnum : ActionType.values()) {
			if (theEnum.getDescription().equals(description)) {
				return theEnum;
			}
		}
		return null;
	}
}
