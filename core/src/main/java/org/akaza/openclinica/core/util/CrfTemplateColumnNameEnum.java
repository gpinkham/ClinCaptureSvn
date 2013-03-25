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
package org.akaza.openclinica.core.util;

/**
 * @author htaycher
 * 
 */
public enum CrfTemplateColumnNameEnum {

	ITEM_NAME(0), DESCRIPTION_LABEL(1), LEFT_ITEM_TEXT(2), UNITS(3), RIGHT_ITEM_TEXT(4), SECTION_LABEL(5), GROUP_LABEL(
			6), HEADER(7), SUBHEADER(8), PARENT_ITEM(9), COLUMN_NUMBER(10), PAGE_NUMBER(11), QUESTION_NUMBER(12), RESPONSE_TYPE(
			13), RESPONSE_LABEL(14), RESPONSE_OPTIONS_TEXT(15), RESPONSE_VALUES_OR_CALCULATIONS(16), RESPONSE_LAYOUT(17), DEFAULT_VALUE(
			18), DATA_TYPE(19), WIDTH_DECIMAL(20), VALIDATION(21), VALIDATION_ERROR_MESSAGE(22), PHI(23), REQUIRED(24), ITEM_DISPLAY_STATUS(
			25), SIMPLE_CONDITIONAL_DISPLAY(26);

	private int cell_number;

	private CrfTemplateColumnNameEnum(int c) {
		cell_number = c;
	}

	public int getCellNumber() {
		return cell_number;
	}

}
