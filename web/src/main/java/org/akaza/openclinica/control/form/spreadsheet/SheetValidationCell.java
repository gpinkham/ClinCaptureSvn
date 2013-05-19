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

/**
 * <p>
 * Class contains final SheetValidationType initialized with Constructor.
 * </p>
 * 
 */
// ywang (Aug., 2011)
public class SheetValidationCell {
	private final SheetValidationType type;
	private SheetArgumentCell sheetArgumentCell;

	/**
	 * SheetValidationType has been set as NONE.
	 * 
	 * @param sheetCell
	 */
	public SheetValidationCell(SheetCell sheetCell) {
		this.type = SheetValidationType.NONE;
		this.sheetArgumentCell = new SheetArgumentCell(sheetCell);
	}

	public SheetValidationCell(SheetValidationType sheetValidationType, SheetCell sheetCell) {
		this.type = sheetValidationType;
		this.sheetArgumentCell = new SheetArgumentCell(sheetCell);
	}

	public SheetArgumentCell getSheetArgumentCell() {
		return sheetArgumentCell;
	}

	public void setSheetArgumentCell(SheetArgumentCell sheetArgumentCell) {
		this.sheetArgumentCell = sheetArgumentCell;
	}

	public SheetValidationType getType() {
		return type;
	}
}
