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
 * Control displaying of a simple conditional display item at the front end <br/>
 * SHOW_UNCHANGABLE: 0: always display; SHOW_CHANGABLE: 1: display but changable; HIDE_CHANGABLE: 2: no display but
 * changable;
 */
public enum SCDShowStatus {
	SHOW_UNCHANGABLE(0), SHOW_CHANGABLE(1), HIDE_CHANGABLE(2);

	private int code;

	SCDShowStatus() {
		this.code = 0;
	}

	SCDShowStatus(int code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return name().toString();
	}

	public static SCDShowStatus getByCode(Integer code) {
		HashMap<Integer, SCDShowStatus> enumObjects = new HashMap<Integer, SCDShowStatus>();
		for (SCDShowStatus theEnum : SCDShowStatus.values()) {
			enumObjects.put(theEnum.getCode(), theEnum);
		}
		return enumObjects.get(Integer.valueOf(code));
	}

	public int getCode() {
		return code;
	}
}
