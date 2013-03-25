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

package org.akaza.openclinica.control.form.spreadsheet;

/**
 * Collect what could be validated upon spreadsheet loading.
 * 
 */
public enum SheetValidationType {
	/*
	 * YW: created file at Aug., 2011 with OnChangeSheetValidator cooperating with current spreadsheet loading
	 * validation style.
	 */

	NONE(0, "none"), IS_REQUIRED(1, "is_required"), SHOULD_BE_EMPTY(2, "should_be_empty"), SHOULD_BE_ST(3,
			"data_type_should_be_ST"), ITEM_NAME_SHOULD_PROVIDED(4, "item_name_should_provided");

	int code;
	String description;

	private SheetValidationType(int code, String description) {
		this.code = code;
		this.description = description;
	}

	public Integer getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}
}
