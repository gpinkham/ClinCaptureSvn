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
package org.akaza.openclinica.control.form.spreadsheet;

import java.util.ResourceBundle;

/**
 * Utility class for crf spreadsheet validation. Created at Aug. 2011, ywang
 */
public class SheetValidatorHelper {
	private SheetValidatorHelper() {
	}

	/**
	 * Current SheetValidationType validated are: IS_REQUIRED, SHOULD_BE_EMPTY, SHOULD_BE_ST
	 * 
	 * @param sheetValidationCell
	 * @param sheetErrors
	 */
	public static void validateSheetValidationCell(SheetValidationCell sheetValidationCell, SheetErrors sheetErrors) {
		boolean printHtmlInvalidErr = false;
		ResourceBundle resPageMsg = sheetErrors.getResPageMsg();
		SheetCell cell = sheetValidationCell.getSheetArgumentCell().getSheetCell();
		switch (sheetValidationCell.getType()) {
		case IS_REQUIRED:
			if (cell.getColValue() == null || cell.getColValue().isEmpty()) {
				StringBuffer message = sheetErrors.errorMessage(resPageMsg.getString(cell.getColTitle()),
						resPageMsg.getString(sheetValidationCell.getType().getDescription()),
						resPageMsg.getString(cell.getForWhich()));
				sheetErrors.addError(cell.getRowNum(), message);
				sheetErrors.putHtmlError(cell.getSheetNum(), cell.getRowNum(), cell.getColNum(),
						resPageMsg.getString("required_field"));
			}
			break;
		case SHOULD_BE_EMPTY:
			if (cell.getColValue() != null && cell.getColValue().length() > 0) {
				StringBuffer message = sheetErrors.errorMessage(resPageMsg.getString(cell.getColTitle()),
						resPageMsg.getString(sheetValidationCell.getType().getDescription()),
						resPageMsg.getString(cell.getForWhich()));
				sheetErrors.addError(cell.getRowNum(), message);
				printHtmlInvalidErr = true;
			}
			break;
		case SHOULD_BE_ST:
			if (cell.getColValue() == null || cell.getColValue().isEmpty()
					|| !"ST".equalsIgnoreCase(cell.getColValue())) {
				StringBuffer message = sheetErrors.errorMessage(resPageMsg.getString(cell.getColTitle()),
						resPageMsg.getString(sheetValidationCell.getType().getDescription()),
						resPageMsg.getString(cell.getForWhich()));
				sheetErrors.addError(cell.getRowNum(), message);
				printHtmlInvalidErr = true;
			}
			break;
		case NONE:
			break;
		default:
			break;
		}
		// print other message first and "INVALID_FIELD" only once.
		if (printHtmlInvalidErr
				&& !sheetErrors.htmlErrors.containsKey(sheetErrors.htmlErrorKey(cell.getSheetNum(), cell.getRowNum(),
						cell.getColNum())))
			sheetErrors.putHtmlError(cell.getSheetNum(), cell.getRowNum(), cell.getColNum(),
					resPageMsg.getString("INVALID_FIELD"));
	}

}
