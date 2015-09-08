/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 *
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 *
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use.
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO'S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/

package com.clinovo.lib.crf.enums;

import java.util.Arrays;
import java.util.List;

/**
 * CellKey.
 */
public enum CellName {

	// crf sheet
	CRF_NAME(0), CRF_VERSION(1), CRF_VERSION_DESCRIPTION(2), CRF_REVISION_NOTES(3),

	// sections sheet
	SECTION_LABEL(0), SECTION_TITLE(1), SECTION_SUBTITLE(2), SECTION_INSTRUCTIONS(3), SECTION_PAGE_NUMBER(4), SECTION_PARENT_SECTION(5), SECTION_BORDERS(6),

	// groups sheet
	GROUP_LABEL(0), GROUP_LAYOUT(1), GROUP_HEADER(2), GROUP_REPEAT_NUMBER(3), GROUP_REPEAT_MAX(4), GROUP_DISPLAY_STATUS(5),

	// items sheet
	ITEM_NAME(0), ITEM_DESCRIPTION_LABEL(1), ITEM_LEFT_ITEM_TEXT(2), ITEM_UNITS(3), ITEM_RIGHT_ITEM_TEXT(4),
	ITEM_SECTION_LABEL(5), ITEM_GROUP_LABEL(6), ITEM_HEADER(7), ITEM_SUBHEADER(8), ITEM_PARENT_ITEM(9),
	ITEM_COLUMN_NUMBER(10), ITEM_PAGE_NUMBER(11), ITEM_QUESTION_NUMBER(12), ITEM_RESPONSE_TYPE(13),
	ITEM_RESPONSE_LABEL(14), ITEM_RESPONSE_OPTIONS_TEXT(15), ITEM_RESPONSE_VALUES_OR_CALCULATIONS(16),
	ITEM_RESPONSE_LAYOUT(17), ITEM_DEFAULT_VALUE(18), ITEM_DATA_TYPE(19), ITEM_WIDTH_DECIMAL(20),
	ITEM_VALIDATION(21), ITEM_VALIDATION_ERROR_MESSAGE(22), ITEM_PHI(23), ITEM_REQUIRED(24),
	ITEM_DISPLAY_STATUS(25), ITEM_SIMPLE_CONDITIONAL_DISPLAY(26), ITEM_CODE_REF(27);


	public static final List<CellName> CRF_SHEET_CELL_NAMES = Arrays.asList(GROUP_LABEL, GROUP_LAYOUT, GROUP_HEADER,
			GROUP_REPEAT_NUMBER, GROUP_REPEAT_MAX, GROUP_DISPLAY_STATUS);

	public static final List<CellName> SECTIONS_SHEET_CELL_NAMES = Arrays.asList(GROUP_LABEL, GROUP_LAYOUT,
			GROUP_HEADER, GROUP_REPEAT_NUMBER, GROUP_REPEAT_MAX, GROUP_DISPLAY_STATUS);

	public static final List<CellName> GROUPS_SHEET_CELL_NAMES = Arrays.asList(GROUP_LABEL, GROUP_LAYOUT, GROUP_HEADER,
			GROUP_REPEAT_NUMBER, GROUP_REPEAT_MAX, GROUP_DISPLAY_STATUS);

	public static final List<CellName> ITEMS_SHEET_CELL_NAMES = Arrays.asList(ITEM_NAME, ITEM_DESCRIPTION_LABEL,
			ITEM_LEFT_ITEM_TEXT, ITEM_UNITS, ITEM_RIGHT_ITEM_TEXT, ITEM_SECTION_LABEL, ITEM_GROUP_LABEL, ITEM_HEADER,
			ITEM_SUBHEADER, ITEM_PARENT_ITEM, ITEM_COLUMN_NUMBER, ITEM_PAGE_NUMBER, ITEM_QUESTION_NUMBER,
			ITEM_RESPONSE_TYPE, ITEM_RESPONSE_LABEL, ITEM_RESPONSE_OPTIONS_TEXT, ITEM_RESPONSE_VALUES_OR_CALCULATIONS,
			ITEM_RESPONSE_LAYOUT, ITEM_DEFAULT_VALUE, ITEM_DATA_TYPE, ITEM_WIDTH_DECIMAL, ITEM_VALIDATION,
			ITEM_VALIDATION_ERROR_MESSAGE, ITEM_PHI, ITEM_REQUIRED, ITEM_DISPLAY_STATUS,
			ITEM_SIMPLE_CONDITIONAL_DISPLAY, ITEM_CODE_REF);

	private int columnNumber;

	CellName(int columnNumber) {
		this.columnNumber = columnNumber;
	}

	/**
	 * To get the correct column number of cell use the ExcelCrfBuilder.getColumnNumber(CellName cellKey) instead of
	 * this method, cuz some cells can be missed in old crf templates.
	 *
	 * @return columnNumber
	 */
	public int getColumnNumber() {
		return columnNumber;
	}
}
