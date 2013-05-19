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
 *
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.logic.expressionTree;

import java.util.HashMap;

public enum Operator {
	EQUAL(1, "eq"), NOT_EQUAL(2, "ne"), OR(3, "or"), AND(4, "and"), GREATER_THAN(5, "gt"), GREATER_THAN_EQUAL(6, "gte"), LESS_THAN(
			7, "lt"), LESS_THAN_EQUAL(8, "lte"), PLUS(9, "+"), MINUS(10, "-"), MULTIPLY(11, "*"), POWER(12, "^"), DIVIDE(
			13, "/"), CONTAINS(14, "ct");

	private String description;

	Operator(int code) {
		this(code, null);
	}

	Operator(int code, String description) {
		this.description = description;
	}

	public static Operator getByDescription(String description) {
		HashMap<String, Operator> operators = new HashMap<String, Operator>();
		for (Operator operator : Operator.values()) {
			operators.put(operator.getDescription(), operator);
		}
		return operators.get(description.trim());
	}

	public String getDescription() {
		return description;
	}
}
