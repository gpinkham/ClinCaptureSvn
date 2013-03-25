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

/* 
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.bean.rule.expression;

import java.util.HashMap;

/*
 * @Author Krikor Krumlian
 */
public enum Context {

	OC_RULES_V1(1, "OpenClinica rules v1.0");

	private int code;

	/*
	 * Default Constructor
	 */
	Context() {

	}

	Context(int code) {
		this(code, null);
	}

	Context(int code, String longName) {
		this.code = code;
	}

	public Context getByContextName(String name) {
		HashMap<String, Context> operators = new HashMap<String, Context>();
		for (Context operator : Context.values()) {
			operators.put(operator.name(), operator);
		}
		return operators.get(name);
	}

	public static Context getByName(String name) {
		HashMap<String, Context> operators = new HashMap<String, Context>();
		for (Context operator : Context.values()) {
			operators.put(operator.name(), operator);
		}
		return operators.get(name);
	}

	public static Context getByCode(int code) {
		HashMap<Integer, Context> operators = new HashMap<Integer, Context>();
		for (Context operator : Context.values()) {
			operators.put(operator.getCode(), operator);
		}
		return operators.get(Integer.valueOf(code));
	}

	public Integer getCode() {
		return code;
	}

}
