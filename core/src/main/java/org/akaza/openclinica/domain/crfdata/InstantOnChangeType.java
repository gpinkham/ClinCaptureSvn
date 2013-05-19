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
 * copyright 2003-2011 Akaza Research
 */
package org.akaza.openclinica.domain.crfdata;

/**
 * For option of instant-calculation, func: onchange(item,option)
 */
// ywang (Aug., 2011)
public enum InstantOnChangeType {
	INVALID(0, "invalid"), CURRENT_DATE_TIME(1, "_CURRENT_DATE_TIME"), CURRENT_DATE(2, "_CURRENT_DATE");

	private int code;
	private String description;

	InstantOnChangeType(int code, String description) {
		this.code = code;
		this.description = description;
	}

	public static InstantOnChangeType getByDescription(String description) {
		for (InstantOnChangeType theEnum : InstantOnChangeType.values()) {
			if (theEnum.getDescription().equalsIgnoreCase(description)) {
				return theEnum;
			}
		}
		return null;
	}

	public static boolean isValidTypeByDescription(String description) {
		InstantOnChangeType instantOptionType = InstantOnChangeType.getByDescription(description);
		return instantOptionType != null && instantOptionType.getCode() > 0;
	}

	public Integer getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}
}
