/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 *
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clincapture.com/contact for pricing information.
 *
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use.
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO'S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/

package com.clinovo.lib.crf.validator;

import org.akaza.openclinica.exception.CRFReadingException;
import org.apache.poi.ss.usermodel.Sheet;

import com.clinovo.lib.crf.builder.impl.ExcelCrfBuilder;

/**
 * WorksheetValidator.
 */
public final class WorksheetValidator {

	public static final int INT_4 = 4;

	public static final String CRF = "CRF";
	public static final String ITEMS = "Items";
	public static final String GROUPS = "Groups";
	public static final String SECTIONS = "Sections";

	private WorksheetValidator() {
	}

	/**
	 * Validates worksheet.
	 *
	 * @param crfBuilder
	 *            ExcelCrfBuilder
	 * @throws Exception
	 *             an Exception
	 */
	public static void validate(ExcelCrfBuilder crfBuilder) throws Exception {
		int validSheetNum = 0;
		for (int i = 0; i < crfBuilder.getWorkbook().getNumberOfSheets(); i++) {
			String sheetName = crfBuilder.getWorkbook().getSheetName(i);
			if (sheetName.equalsIgnoreCase(CRF) || sheetName.equalsIgnoreCase(SECTIONS)
					|| sheetName.equalsIgnoreCase(GROUPS) || sheetName.equalsIgnoreCase(ITEMS)) {
				validSheetNum++;
				if (sheetName.equalsIgnoreCase(GROUPS)) {
					crfBuilder.setIsRepeating(true);
				}
			}
		}
		if (validSheetNum != INT_4) {
			crfBuilder.getErrorsList().add(crfBuilder.getMessage("workbook.isNotValid"));
		}
		Sheet sheet = crfBuilder.getWorkbook().getSheetAt(0);
		if (sheet == null || sheet.getRow(1) == null || sheet.getRow(1).getCell(0) == null) {
			throw new CRFReadingException(crfBuilder.getMessage("workbook.blankFound"));
		}
	}

}
