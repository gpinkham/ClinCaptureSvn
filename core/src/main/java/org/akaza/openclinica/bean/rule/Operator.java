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

package org.akaza.openclinica.bean.rule;

import java.util.HashMap;

/*
 * Use this enum as operator holder
 * @author Krikor Krumlian
 *
 */

public enum Operator {

	EQUAL(1), NOTEQUAL(2), GREATER(3), GREATERorEQUAL(4), LESS(5), LESSorEQUAL(6);

	private int code;

	Operator(int code) {
		this(code, null);
	}

	Operator(int code, String longName) {
		this.code = code;
	}

	public static Operator getByName(String name) {
		HashMap<String, Operator> operators = new HashMap<String, Operator>();
		for (Operator operator : Operator.values()) {
			operators.put(operator.name(), operator);
		}
		return operators.get(name);
	}

	public static Operator getByCode(int code) {
		HashMap<Integer, Operator> operators = new HashMap<Integer, Operator>();
		for (Operator operator : Operator.values()) {
			operators.put(operator.getCode(), operator);
		}
		return operators.get(Integer.valueOf(code));
	}

	public Integer getCode() {
		return code;
	}

}
