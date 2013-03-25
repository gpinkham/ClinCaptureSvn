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

package org.akaza.openclinica.bean.rule.action;

import java.util.HashMap;

/*
 * Use this enum as operator holder
 * @author Krikor Krumlian
 *
 */

public enum ActionType {

	FILE_DISCREPANCY_NOTE(1), EMAIL(2);

	private int code;

	ActionType(int code) {
		this(code, null);
	}

	ActionType(int code, String longName) {
		this.code = code;
	}

	public static ActionType getByName(String name) {
		HashMap<String, ActionType> operators = new HashMap<String, ActionType>();
		for (ActionType operator : ActionType.values()) {
			operators.put(operator.name(), operator);
		}
		return operators.get(name);
	}

	public static ActionType getByCode(int code) {
		HashMap<Integer, ActionType> operators = new HashMap<Integer, ActionType>();
		for (ActionType operator : ActionType.values()) {
			operators.put(operator.getCode(), operator);
		}
		return operators.get(Integer.valueOf(code));
	}

	public Integer getCode() {
		return code;
	}

}
