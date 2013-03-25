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
 * copyright 2003-2011 Akaza Research
 */
package org.akaza.openclinica.bean.submit;

import java.util.HashMap;

/**
 * Control displaying of a row with simple conditional display item at the front end <br/>
 * SHOW_UNCHANGABLE: 0; row always display; SHOW_CHANGABLE 1: current display but changable; HIDE_CHANGABLE 2: current
 * no display but changable; <br/>
 */
public enum SCDRowDisplayStatus {
	SHOW_UNCHANGABLE(0), SHOW_CHANGABLE(1), HIDE_CHANGABLE(2);

	private int code;

	SCDRowDisplayStatus() {
		this.code = 0;
	}

	SCDRowDisplayStatus(int code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return name().toString();
	}

	public static SCDRowDisplayStatus getByCode(Integer code) {
		HashMap<Integer, SCDRowDisplayStatus> enumObjects = new HashMap<Integer, SCDRowDisplayStatus>();
		for (SCDRowDisplayStatus theEnum : SCDRowDisplayStatus.values()) {
			enumObjects.put(theEnum.getCode(), theEnum);
		}
		return enumObjects.get(Integer.valueOf(code));
	}

	public int getCode() {
		return code;
	}
}
