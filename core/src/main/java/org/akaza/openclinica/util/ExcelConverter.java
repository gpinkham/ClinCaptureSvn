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

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package org.akaza.openclinica.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Frank
 * 
 */
public class ExcelConverter {

	/***
	 * Parses file in TSV format and converts it to Excel 2007-2013 format
	 * 
	 * @param tsvFileName
	 *            String
	 * @param outputFileName
	 *            String
	 * @param sheetName
	 *            String
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void convertTabDelimitedToExcel(String tsvFileName, String outputFileName, String sheetName)
			throws IOException {

		FileInputStream fis = new FileInputStream(tsvFileName);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		Workbook workbook = new XSSFWorkbook();
		Sheet worksheet = workbook.createSheet(sheetName);
		Row row;
		int rowIndex = 0;

		String lineOfText = br.readLine();
		String[] cellArray;
		while (lineOfText != null) {

			cellArray = lineOfText.split("\t");
			row = worksheet.createRow(rowIndex++);
			createRowCells(row, cellArray);
			lineOfText = br.readLine();
		}
		br.close();

		writeWorkbookToFile(workbook, outputFileName);
	}

	private static void createRowCells(Row row, String[] cellArray) {

		Cell cell;
		int cellIndex = 0;
		for (String cellValue : cellArray) {
			cell = row.createCell(cellIndex++);
			setCellValue(cell, cellValue);
		}
	}

	private static void setCellValue(Cell cell, String value) {

		if (!StringValidator.hasNumber(value)) {
			cell.setCellValue(value);
		} else if (StringValidator.isValidNumber(value)) {
			cell.setCellValue(Double.parseDouble(value));
		} else if (StringValidator.isValidDateYYYYMMDD(value)) {
			setDateCellValue(cell, value);
		} else {
			cell.setCellValue(value);
		}
	}

	private static void setDateCellValue(Cell cell, String value) {

		try {
			SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
			Date date = formater.parse(value);
			cell.setCellValue(date);
			formatDateCell(cell);

		} catch (ParseException e) {
			cell.setCellValue(value);
		}
	}

	private static void formatDateCell(Cell cell) {

		Workbook wb = cell.getRow().getSheet().getWorkbook();
		CreationHelper createHelper = wb.getCreationHelper();
		CellStyle style = wb.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		style.setDataFormat(createHelper.createDataFormat().getFormat("m/d/yy"));
		cell.setCellStyle(style);
	}

	private static void writeWorkbookToFile(Workbook workbook, String outputFileName) throws IOException {

		FileOutputStream out = new FileOutputStream(new File(outputFileName));
		workbook.write(out);
		out.close();
	}
}