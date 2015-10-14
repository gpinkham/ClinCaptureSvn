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

package com.clinovo.lib.crf.builder.impl;

import java.util.Locale;
import java.util.Map;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.admin.Preview;
import org.akaza.openclinica.control.admin.SpreadsheetPreview;
import org.akaza.openclinica.control.admin.SpreadsheetPreviewNw;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.MessageSource;

import com.clinovo.lib.crf.enums.CRFSource;
import com.clinovo.lib.crf.enums.CellName;
import com.clinovo.lib.crf.enums.SheetName;
import com.clinovo.lib.crf.producer.ErrorMessageProducer;
import com.clinovo.lib.crf.producer.impl.ExcelErrorMessageProducer;
import com.clinovo.lib.crf.service.ImportCrfService;

/**
 * ExcelCrfBuilder.
 */
@SuppressWarnings("rawtypes")
public class ExcelCrfBuilder extends BaseCrfBuilder {

	// excel assistants
	private int i;
	private Row row;
	private int index;
	private int numRows;
	private Workbook workbook;
	private Sheet currentSheet;
	private int currentSheetNumber;
	private String currentSheetName;
	private boolean hasGroupLayoutColumn;
	private boolean hasWidthDecimalColumn;
	private StringBuilder htmlBuffer = new StringBuilder();

	// error message producer
	private ExcelErrorMessageProducer errorMessageProducer = new ExcelErrorMessageProducer(this);

	/**
	 * Constructor.
	 *
	 * @param workbook
	 *            Workbook
	 * @param owner
	 *            UserAccountBean
	 * @param studyBean
	 *            StudyBean
	 * @param dataSource
	 *            DataSource
	 * @param locale
	 *            Locale
	 * @param messageSource
	 *            MessageSource
	 * @param importCrfService
	 *            ImportCrfService
	 */
	public ExcelCrfBuilder(Workbook workbook, UserAccountBean owner, StudyBean studyBean, DataSource dataSource,
			Locale locale, MessageSource messageSource, ImportCrfService importCrfService) {
		super(owner, studyBean, dataSource, locale, messageSource, importCrfService);
		this.workbook = workbook;
		hasWidthDecimalColumn = getValue(workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(0)
				.getCell(CellName.ITEM_WIDTH_DECIMAL.getColumnNumber())).equalsIgnoreCase(WIDTH_DECIMAL);
		hasGroupLayoutColumn = getValue(workbook.getSheetAt(SheetName.GROUPS.getSheetNumber()).getRow(0)
				.getCell(CellName.GROUP_LAYOUT.getColumnNumber())).equalsIgnoreCase(GROUP_LAYOUT);

	}

	/**
	 * Prepares current sheet by number.
	 * 
	 * @param currentSheetNumber
	 *            int
	 */
	public void goToSheet(int currentSheetNumber) {
		currentSheetName = workbook.getSheetName(currentSheetNumber);
		currentSheet = workbook.getSheetAt(currentSheetNumber);
		numRows = currentSheet.getPhysicalNumberOfRows();
		this.currentSheetNumber = currentSheetNumber;
		index = 0;
		i = 1;
	}

	/**
	 * {@inheritDoc}
	 */
	public ErrorMessageProducer getErrorMessageProducer() {
		return errorMessageProducer;
	}

	public StringBuilder getHtmlBuffer() {
		return htmlBuffer;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getHtmlTable() {
		String htmlTable = htmlBuffer.toString();
		for (String errorKey : getErrorsMap().keySet()) {
			htmlTable = htmlTable.replace("<![CDATA[" + errorKey + "]]>",
					"<span class=\"alert\">" + getErrorsMap().get(errorKey) + "</span>");
		}
		return htmlTable;
	}

	/**
	 * Returns cell value.
	 *
	 * @param cell
	 *            Cell
	 * @return String
	 */
	public String getValue(Cell cell) {
		String val;
		int cellType;
		if (cell == null) {
			cellType = Cell.CELL_TYPE_BLANK;
		} else {
			cellType = cell.getCellType();
		}

		switch (cellType) {
			case Cell.CELL_TYPE_BLANK :
				val = "";
				break;
			case Cell.CELL_TYPE_NUMERIC :
				val = cell.getNumericCellValue() + "";
				double dphi = cell.getNumericCellValue();
				if ((dphi - (int) dphi) * INT_1000 == 0) {
					val = (int) dphi + "";
				}
				break;
			case Cell.CELL_TYPE_STRING :
				val = cell.getStringCellValue();
				break;
			case Cell.CELL_TYPE_BOOLEAN :
				boolean val2 = cell.getBooleanCellValue();
				if (val2) {
					val = "true";
				} else {
					val = "false";
				}
				break;
			default :
				val = "";
		}
		return val.trim();
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, Map> createCrfMetaObject() {
		Preview preview;
		if (isRepeating()) {
			preview = new SpreadsheetPreviewNw();
		} else {
			preview = new SpreadsheetPreview();
		}
		return preview.createCrfMetaObject(getWorkbook());
	}

	/**
	 * Returns true if has next row.
	 *
	 * @return boolean
	 */
	public boolean hasNextRow() {
		boolean result = false;
		if (i < numRows) {
			row = currentSheet.getRow(i++);
			if (isEmptyRow(row)) {
				result = false;
			} else {
				index++;
				result = true;
			}
		}
		return result;
	}

	private boolean isEmptyRow(Row row) {
		boolean result = row == null;
		if (!result) {
			result = true;
			if (currentSheetNumber == SheetName.CRF.getSheetNumber()) {
				for (int i = 0; i <= CellName.CRF_REVISION_NOTES.getColumnNumber() - 1; i++) {
					if (!getValue(row.getCell(i)).isEmpty()) {
						result = false;
						break;
					}
				}
			} else if (currentSheetNumber == SheetName.SECTIONS.getSheetNumber()) {
				for (int i = 0; i <= CellName.SECTION_BORDERS.getColumnNumber() - 1; i++) {
					if (!getValue(row.getCell(i)).isEmpty()) {
						result = false;
						break;
					}
				}
			} else if (currentSheetNumber == SheetName.GROUPS.getSheetNumber()) {
				for (int i = 0; i <= CellName.GROUP_DISPLAY_STATUS.getColumnNumber() - 1; i++) {
					if (!getValue(row.getCell(i)).isEmpty()) {
						result = false;
						break;
					}
				}
			} else if (currentSheetNumber == SheetName.ITEMS.getSheetNumber()) {
				for (int i = 0; i <= CellName.ITEM_CODE_REF.getColumnNumber() - 1; i++) {
					if (!getValue(row.getCell(i)).isEmpty()) {
						result = false;
						break;
					}
				}
			}
		}
		return result;
	}

	/**
	 * Returns cell value.
	 *
	 * @param cellKey
	 *            CellKey
	 * @return String
	 */
	public String getCellValue(CellName cellKey) {
		return getCellValue(cellKey, false);
	}

	/**
	 * Returns cell value.
	 * 
	 * @param cellKey
	 *            CellKey
	 * @param replaceSpecialSymbols
	 *            boolean
	 * @return String
	 */
	public String getCellValue(CellName cellKey, boolean replaceSpecialSymbols) {
		String value = getValue(row.getCell(getColumnNumber(cellKey)));
		if (replaceSpecialSymbols) {
			value = value.replaceAll("<[^>]*>", "");
		}
		return value;
	}

	/**
	 * Returns the correct column number of cell.
	 *
	 * @param cellKey
	 *            CellKey
	 * @return String
	 */
	public int getColumnNumber(CellName cellKey) {
		int offset = 0;
		if (CellName.GROUPS_SHEET_CELL_NAMES.contains(cellKey)) {
			offset = hasGroupLayoutColumn
					? 0
					: (cellKey.getColumnNumber() >= CellName.GROUP_LAYOUT.getColumnNumber()
							&& cellKey != CellName.GROUP_LAYOUT ? -1 : 0);
		} else if (CellName.ITEMS_SHEET_CELL_NAMES.contains(cellKey)) {
			offset = hasWidthDecimalColumn
					? 0
					: (cellKey.getColumnNumber() >= CellName.ITEM_WIDTH_DECIMAL.getColumnNumber()
							&& cellKey != CellName.ITEM_WIDTH_DECIMAL ? -1 : 0);
		}
		return cellKey.getColumnNumber() + offset;
	}

	@Override
	protected void setCrfSource() {
		getCrfBean().setSource(CRFSource.SOURCE_FORM_EXCEL.getSourceName());
	}

	public Row getRow() {
		return row;
	}

	public int getIndex() {
		return index - 1;
	}

	public int getNumRows() {
		return numRows;
	}

	public int getRowNumber() {
		return i - 1;
	}

	public Workbook getWorkbook() {
		return workbook;
	}

	public Sheet getCurrentSheet() {
		return currentSheet;
	}

	public int getCurrentSheetNumber() {
		return currentSheetNumber;
	}

	public String getCurrentSheetName() {
		return currentSheetName;
	}
}
