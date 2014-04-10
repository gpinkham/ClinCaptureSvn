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

package org.akaza.openclinica.control.admin;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class SpreadSheetTableTest {

	private String testFileName = "Test.xlsx";
	private FileInputStream inputStream;
	private XSSFWorkbook workbook;
	@Mock
	private UserAccountBean user;

	@Test
	public void testSpreadSheetTableClassicHasXSSFWorkbookWhenTrueIsPassed() {
		try {
			inputStream = new FileInputStream(new File(testFileName));
			SpreadSheetTableClassic sstc = new SpreadSheetTableClassic(inputStream, user, "", null, 1, true);
			if (sstc.getWorkbook() instanceof XSSFWorkbook) {
				assertTrue(true);
			} else {
				assertFalse(true);
			}
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			assertFalse(true);
		}
	}

	@Test
	public void testSpreadSheetTableClassicGetCellValue() {
		Sheet sheet = workbook.getSheet("Test File Data");
		Row row = sheet.getRow(1);
		Cell cell = row.getCell(1);
		try {
			inputStream = new FileInputStream(new File(testFileName));
			SpreadSheetTableClassic sstc = new SpreadSheetTableClassic(inputStream, user, "", null, 1, true);
			assertEquals("Frank", sstc.getValue(cell));
			inputStream.close();

		} catch (Exception e) {
			e.printStackTrace();
			assertFalse(true);
		}
	}

	@Test
	public void testSpreadSheetTableRepeatingHasXSSFWorkbookWhenTrueIsPassed() {
		try {
			inputStream = new FileInputStream(new File(testFileName));
			SpreadSheetTableRepeating sstr = new SpreadSheetTableRepeating(inputStream, user, "", null, 1, true);
			if (sstr.getWorkbook() instanceof XSSFWorkbook) {
				assertTrue(true);
			} else {
				assertFalse(true);
			}
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			assertFalse(true);
		}
	}

	@Test
	public void testSpreadSheetTableRepeatingGetCellValue() {
		Sheet sheet = workbook.getSheet("Test File Data");
		Row row = sheet.getRow(1);
		Cell cell = row.getCell(1);
		try {
			inputStream = new FileInputStream(new File(testFileName));
			SpreadSheetTableRepeating sstr = new SpreadSheetTableRepeating(inputStream, user, "", null, 1, true);
			assertEquals("Frank", sstr.getValue(cell));
			inputStream.close();

		} catch (Exception e) {
			e.printStackTrace();
			assertFalse(true);
		}
	}

	@Before
	public void setUp() throws Exception {
		writeTestFile();
	}

	@After
	public void cleanUp() throws Exception {
		deleteTestFile();
	}

	private void writeTestFile() throws Exception {

		// Create test workbook
		createTestWorkbook();

		// Write workbook in file system
		FileOutputStream out = new FileOutputStream(new File(testFileName));
		workbook.write(out);
		out.close();
	}

	private void createTestWorkbook() {

		workbook = new XSSFWorkbook();
		XSSFSheet worksheet = workbook.createSheet("Test File Data");

		// Test data to be written
		Map<String, Object[]> data = new TreeMap<String, Object[]>();
		data.put("1", new Object[] { "ID", "FIRST NAME", "LAST NAME" });
		data.put("2", new Object[] { "1", "Frank", "Haga" });
		data.put("3", new Object[] { "2", "Morgan", "August" });
		data.put("4", new Object[] { "3", "John", "Terry" });
		data.put("5", new Object[] { "4", "Sandy", "Pati" });

		Set<String> keySet = data.keySet();
		int rowNum = 0;

		for (String key : keySet) {
			Row row = worksheet.createRow(rowNum++);
			Object[] objArr = data.get(key);
			int cellNum = 0;
			for (Object obj : objArr) {
				Cell cell = row.createCell(cellNum++);
				if (obj instanceof String)
					cell.setCellValue((String) obj);
				else if (obj instanceof Integer)
					cell.setCellValue((Integer) obj);
			}
		}
	}

	private void deleteTestFile() throws Exception {

		File file = new File(testFileName);
		if (!file.delete()) {
			throw new Exception("Delete operation failed");
		}
	}
}
