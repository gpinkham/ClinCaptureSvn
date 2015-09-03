package com.clinovo.lib.crf.builder.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.clinovo.lib.crf.service.ImportCrfService;

@RunWith(MockitoJUnitRunner.class)
public class ExcelCrfBuilderTest {

	@Mock
	private Row row;
	@Mock
	private Cell cell;
	@Mock
	private Sheet sheet;
	@Mock
	private Workbook workbook;
	@Mock
	private StudyBean studyBean;
	@Mock
	private DataSource dataSource;
	@Mock
	private UserAccountBean owner;

	private ExcelCrfBuilder excelCrfBuilder;

	@Mock
	private ImportCrfService importCrfService;
	@Mock
	private ResourceBundle pageMessagesResourceBundle;

	@Before
	public void before() {
		ResourceBundleProvider.updateLocale(Locale.ENGLISH);
		Mockito.when(workbook.getSheetAt(Mockito.anyInt())).thenReturn(sheet);
		Mockito.when(row.getCell(Mockito.anyInt())).thenReturn(cell);
		Mockito.when(sheet.getRow(Mockito.anyInt())).thenReturn(row);
		excelCrfBuilder = new ExcelCrfBuilder(workbook, owner, studyBean, dataSource, Locale.ENGLISH,
				ResourceBundleProvider.getPageMessagesBundle(), importCrfService);
	}

	@Test
	public void testDefaultValues() {
		assertEquals(excelCrfBuilder.getRowNumber(), -1);
		assertEquals(excelCrfBuilder.getNumRows(), 0);
		assertEquals(excelCrfBuilder.getIndex(), -1);
		assertNull(excelCrfBuilder.getRow());
		assertNotNull(excelCrfBuilder.getErrorMessageProducer());
		assertTrue(excelCrfBuilder.getHtmlBuffer().toString().isEmpty());
		assertNotNull(excelCrfBuilder.getWorkbook());
		assertNull(excelCrfBuilder.getCurrentSheet());
		assertEquals(excelCrfBuilder.getCurrentSheetNumber(), 0);
		assertNull(excelCrfBuilder.getCurrentSheetName());
	}

}
