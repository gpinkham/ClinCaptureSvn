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
import java.util.ResourceBundle;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.admin.Preview;
import org.akaza.openclinica.control.admin.SpreadsheetPreview;
import org.akaza.openclinica.control.admin.SpreadsheetPreviewNw;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.clinovo.lib.crf.enums.CellName;
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
	private int blankRowCount;
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
	 * @param pageMessagesResourceBundle
	 *            ResourceBundle
	 * @param importCrfService
	 *            ImportCrfService
	 */
	public ExcelCrfBuilder(Workbook workbook, UserAccountBean owner, StudyBean studyBean, DataSource dataSource,
			Locale locale, ResourceBundle pageMessagesResourceBundle, ImportCrfService importCrfService) {
		super(owner, studyBean, dataSource, locale, pageMessagesResourceBundle, importCrfService);
		this.workbook = workbook;
		hasWidthDecimalColumn = getValue(
				workbook.getSheetAt(3).getRow(0).getCell(CellName.ITEM_WIDTH_DECIMAL.getColumnNumber()))
						.equalsIgnoreCase(WIDTH_DECIMAL);
		hasGroupLayoutColumn = getValue(
				workbook.getSheetAt(2).getRow(0).getCell(CellName.GROUP_LAYOUT.getColumnNumber()))
						.equalsIgnoreCase(GROUP_LAYOUT);

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
			if (row == null || getValue(row.getCell(0)).isEmpty()) {
				blankRowCount++;
				if (blankRowCount < INT_5) {
					result = hasNextRow();
				}
			} else {
				index++;
				result = true;
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
		int offset = 0;
		if (currentSheetNumber == 2) {
			offset = hasGroupLayoutColumn
					? 0
					: (cellKey.getColumnNumber() >= CellName.GROUP_LAYOUT.getColumnNumber()
							&& cellKey != CellName.GROUP_LAYOUT ? -1 : 0);
		} else if (currentSheetNumber == 3) {
			offset = hasWidthDecimalColumn
					? 0
					: (cellKey.getColumnNumber() >= CellName.ITEM_WIDTH_DECIMAL.getColumnNumber()
							&& cellKey != CellName.ITEM_WIDTH_DECIMAL ? -1 : 0);
		}
		String value = getValue(row.getCell(cellKey.getColumnNumber() + offset));
		return replaceSpecialSymbols ? value.replaceAll("<[^>]*>", "") : value;
	}

	/**
	 * Returns column number.
	 *
	 * @param cellKey
	 *            CellKey
	 * @return String
	 */
	public int getColumnNumber(CellName cellKey) {
		int offset = 0;
		if (currentSheetNumber == 2) {
			offset = hasGroupLayoutColumn
					? 0
					: (cellKey.getColumnNumber() >= CellName.GROUP_LAYOUT.getColumnNumber()
							&& cellKey != CellName.GROUP_LAYOUT ? -1 : 0);
		} else if (currentSheetNumber == 3) {
			offset = hasWidthDecimalColumn
					? 0
					: (cellKey.getColumnNumber() >= CellName.ITEM_WIDTH_DECIMAL.getColumnNumber()
							&& cellKey != CellName.ITEM_WIDTH_DECIMAL ? -1 : 0);
		}
		return cellKey.getColumnNumber() + offset;
	}

	public Row getRow() {
		return row;
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

	public int getIndex() {
		return index - 1;
	}
}
