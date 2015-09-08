package com.clinovo.lib.crf.validator;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.exception.CRFReadingException;
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
import org.springframework.context.MessageSource;

import com.clinovo.lib.crf.builder.impl.ExcelCrfBuilder;
import com.clinovo.lib.crf.service.ImportCrfService;

@RunWith(MockitoJUnitRunner.class)
public class WorksheetValidatorTest {

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
	private MessageSource messageSource;

	@Before
	public void before() {
		ResourceBundleProvider.updateLocale(Locale.ENGLISH);
		Mockito.when(workbook.getSheetAt(Mockito.anyInt())).thenReturn(sheet);
		Mockito.when(row.getCell(Mockito.anyInt())).thenReturn(cell);
		Mockito.when(sheet.getRow(Mockito.anyInt())).thenReturn(row);
		Mockito.when(sheet.getRow(Mockito.anyInt())).thenReturn(row);
		excelCrfBuilder = new ExcelCrfBuilder(workbook, owner, studyBean, dataSource, Locale.ENGLISH, messageSource,
				importCrfService);
	}

	@Test(expected = CRFReadingException.class)
	public void testThatWorksheetValidatorThrowsTheCRFReadingExceptionIfFirstSheetIsNull() throws Exception {
		Mockito.when(workbook.getSheetAt(0)).thenReturn(null);
		WorksheetValidator.validate(excelCrfBuilder);
	}

	@Test(expected = CRFReadingException.class)
	public void testThatWorksheetValidatorThrowsTheCRFReadingExceptionIfFirstRowOnFirstSheetIsNull() throws Exception {
		Mockito.when(sheet.getRow(Mockito.anyInt())).thenReturn(null);
		WorksheetValidator.validate(excelCrfBuilder);
	}

	@Test(expected = CRFReadingException.class)
	public void testThatWorksheetValidatorThrowsTheCRFReadingExceptionIfFirstCellInFirstRowOnFirstSheetIsNull()
			throws Exception {
		Mockito.when(sheet.getRow(Mockito.anyInt())).thenReturn(null);
		WorksheetValidator.validate(excelCrfBuilder);
	}

	@Test
	public void testThatWorksheetValidatorGeneratesErrorIfWorkbookDoesNotHaveRequiredSheets() throws Exception {
		WorksheetValidator.validate(excelCrfBuilder);
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatWorksheetValidatorGeneratesErrorIfCrfSheetIsMissing() throws Exception {
		workbook.createSheet(WorksheetValidator.SECTIONS);
		workbook.createSheet(WorksheetValidator.GROUPS);
		workbook.createSheet(WorksheetValidator.ITEMS);
		WorksheetValidator.validate(excelCrfBuilder);
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatWorksheetValidatorGeneratesErrorIfSectionsSheetIsMissing() throws Exception {
		workbook.createSheet(WorksheetValidator.CRF);
		workbook.createSheet(WorksheetValidator.GROUPS);
		workbook.createSheet(WorksheetValidator.ITEMS);
		WorksheetValidator.validate(excelCrfBuilder);
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatWorksheetValidatorGeneratesErrorIfGroupsSheetIsMissing() throws Exception {
		workbook.createSheet(WorksheetValidator.CRF);
		workbook.createSheet(WorksheetValidator.SECTIONS);
		workbook.createSheet(WorksheetValidator.ITEMS);
		WorksheetValidator.validate(excelCrfBuilder);
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatWorksheetValidatorGeneratesErrorIfItemsSheetIsMissing() throws Exception {
		workbook.createSheet(WorksheetValidator.CRF);
		workbook.createSheet(WorksheetValidator.SECTIONS);
		workbook.createSheet(WorksheetValidator.GROUPS);
		WorksheetValidator.validate(excelCrfBuilder);
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatWorksheetValidatorDoesNotGenerateErrorIfWorkbookHasRequiredSheets() throws Exception {
		workbook.createSheet(WorksheetValidator.CRF);
		workbook.createSheet(WorksheetValidator.SECTIONS);
		workbook.createSheet(WorksheetValidator.GROUPS);
		workbook.createSheet(WorksheetValidator.ITEMS);
		WorksheetValidator.validate(excelCrfBuilder);
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
	}
}
