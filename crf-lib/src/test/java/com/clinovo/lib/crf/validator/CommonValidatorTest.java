package com.clinovo.lib.crf.validator;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.exception.CRFReadingException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;

import com.clinovo.lib.crf.builder.impl.ExcelCrfBuilder;
import com.clinovo.lib.crf.builder.impl.JsonCrfBuilder;
import com.clinovo.lib.crf.enums.CellName;
import com.clinovo.lib.crf.enums.SheetName;
import com.clinovo.lib.crf.factory.CrfBuilderFactory;

public class CommonValidatorTest extends DefaultAppContextTest {

	private StudyBean studyBean;

	private UserAccountBean owner;

	private JsonCrfBuilder jsonCrfBuilder;

	private ExcelCrfBuilder excelCrfBuilder;

	@Autowired
	private CrfBuilderFactory crfBuilderFactory;

	@Before
	public void before() {
		studyBean = (StudyBean) studyDAO.findByPK(1);
		owner = (UserAccountBean) userAccountDAO.findByPK(1);
	}

	@Override
	protected void restoreDb() throws Exception {
		// do not restore db
	}

	private String getJsonData(String fileName) throws Exception {
		InputStream inputStream = null;
		try {
			inputStream = new DefaultResourceLoader().getResource("data/json/".concat(fileName)).getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			StringBuilder out = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				out.append(line);
			}
			return out.toString();
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (Exception ex) {
				//
			}
		}
	}

	private Workbook getWorkbook(String fileName) throws Exception {
		InputStream inputStream = null;
		boolean isXlsx = fileName.toLowerCase().endsWith(".xlsx");
		try {
			inputStream = new DefaultResourceLoader().getResource("data/excel/".concat(fileName)).getInputStream();
			return !isXlsx ? new HSSFWorkbook(new POIFSFileSystem(inputStream)) : new XSSFWorkbook(inputStream);
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (Exception ex) {
				//
			}
		}
	}

	private String generateString(int length) {
		String result = "";
		for (int i = 0; i <= length - 1; i++) {
			result += "a";
		}
		return result;
	}

	@Test
	public void testThatJsonCrfBuilderDoesNotGenerateAnyErrorsForCorrectData() throws Exception {
		jsonCrfBuilder = (JsonCrfBuilder) crfBuilderFactory.getCrfBuilder(getJsonData("testCrf.json"), studyBean, owner,
				Locale.ENGLISH, messageSource);
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().isEmpty());
		assertTrue(jsonCrfBuilder.getErrorsMap().isEmpty());
	}

	@Test
	public void testThatExcelCrfBuilderDoesNotGenerateAnyErrorsForCorrectData() throws Exception {
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(getWorkbook("testCrf.xls"), studyBean,
				owner, Locale.ENGLISH, messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().isEmpty());
		assertTrue(excelCrfBuilder.getErrorsMap().isEmpty());
	}

	@Test(expected = CRFReadingException.class)
	public void testThatExcelCrfBuilderGeneratesErrorsIfCrfVersionIsBlank() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.CRF.getSheetNumber()).getRow(1).createCell(CellName.CRF_VERSION.getColumnNumber())
				.setCellValue("");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfCrfVersionLengthIsExceeded() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.CRF.getSheetNumber()).getRow(1).createCell(CellName.CRF_VERSION.getColumnNumber())
				.setCellValue(generateString(256));
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfCrfVersionDescriptionLengthIsExceeded() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.CRF.getSheetNumber()).getRow(1)
				.createCell(CellName.CRF_VERSION_DESCRIPTION.getColumnNumber()).setCellValue(generateString(4001));
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfRevisionNotesIsBlank() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.CRF.getSheetNumber()).getRow(1)
				.createCell(CellName.CRF_REVISION_NOTES.getColumnNumber()).setCellValue("");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfRevisionNotesLengthIsExceeded() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.CRF.getSheetNumber()).getRow(1)
				.createCell(CellName.CRF_REVISION_NOTES.getColumnNumber()).setCellValue(generateString(256));
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
	}

	@Test(expected = CRFReadingException.class)
	public void testThatExcelCrfBuilderGeneratesErrorsIfCrfNameIsBlank() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.CRF.getSheetNumber()).getRow(1).createCell(CellName.CRF_NAME.getColumnNumber())
				.setCellValue("");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfCrfNameLengthIsExceeded() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.CRF.getSheetNumber()).getRow(1).createCell(CellName.CRF_NAME.getColumnNumber())
				.setCellValue(generateString(256));
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfSectionLabelIsBlank() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.SECTIONS.getSheetNumber()).getRow(1)
				.createCell(CellName.SECTION_LABEL.getColumnNumber()).setCellValue("");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfSectionLabelLengthIsExceeded() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.SECTIONS.getSheetNumber()).getRow(1)
				.createCell(CellName.SECTION_LABEL.getColumnNumber()).setCellValue(generateString(2001));
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfSectionLabelIsDuplicated() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.SECTIONS.getSheetNumber()).getRow(1)
				.createCell(CellName.SECTION_LABEL.getColumnNumber()).setCellValue("section1");
		workbook.getSheetAt(SheetName.SECTIONS.getSheetNumber()).createRow(2)
				.createCell(CellName.SECTION_LABEL.getColumnNumber()).setCellValue("section1");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfSectionTitleIsBlank() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.SECTIONS.getSheetNumber()).getRow(1)
				.createCell(CellName.SECTION_TITLE.getColumnNumber()).setCellValue("");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfSectionTitleLengthIsExceeded() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.SECTIONS.getSheetNumber()).getRow(1)
				.createCell(CellName.SECTION_TITLE.getColumnNumber()).setCellValue(generateString(2001));
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfSectionInstructionsLengthIsExceeded() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.SECTIONS.getSheetNumber()).getRow(1)
				.createCell(CellName.SECTION_INSTRUCTIONS.getColumnNumber()).setCellValue(generateString(10001));
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfSectionPageNumberLengthIsExceeded() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.SECTIONS.getSheetNumber()).getRow(1)
				.createCell(CellName.SECTION_PAGE_NUMBER.getColumnNumber()).setCellValue(generateString(6));
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfGroupLabelLengthIsExceeded() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.GROUPS.getSheetNumber()).getRow(1)
				.createCell(CellName.GROUP_LABEL.getColumnNumber()).setCellValue(generateString(256));
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfGroupHeaderLengthIsExceeded() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.GROUPS.getSheetNumber()).getRow(1)
				.createCell(CellName.GROUP_HEADER.getColumnNumber() - 1).setCellValue(generateString(256));
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfGroupLabelIsDuplicated() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.GROUPS.getSheetNumber()).getRow(1)
				.createCell(CellName.GROUP_LABEL.getColumnNumber()).setCellValue("group1");
		workbook.getSheetAt(SheetName.GROUPS.getSheetNumber()).getRow(2)
				.createCell(CellName.GROUP_LABEL.getColumnNumber()).setCellValue("group1");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemNameIsBlank() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1).createCell(CellName.ITEM_NAME.getColumnNumber())
				.setCellValue("");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemNameLengthIsExceeded() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1).createCell(CellName.ITEM_NAME.getColumnNumber())
				.setCellValue(generateString(256));
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemNameIsDuplicated() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1).createCell(CellName.ITEM_NAME.getColumnNumber())
				.setCellValue("item1");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2).createCell(CellName.ITEM_NAME.getColumnNumber())
				.setCellValue("item1");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemDescriptionIsBlank() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_DESCRIPTION_LABEL.getColumnNumber()).setCellValue("");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemDescriptionLengthIsExceeded() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_DESCRIPTION_LABEL.getColumnNumber()).setCellValue(generateString(4001));
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfRepeatNumIsWrong() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.GROUPS.getSheetNumber()).getRow(1)
				.createCell(CellName.GROUP_REPEAT_NUMBER.getColumnNumber() - 1).setCellValue("-1");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfRepeatMaxIsWrong() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.GROUPS.getSheetNumber()).getRow(1)
				.createCell(CellName.GROUP_REPEAT_MAX.getColumnNumber() - 1).setCellValue("-1");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemNameIsWrong() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1).createCell(CellName.ITEM_NAME.getColumnNumber())
				.setCellValue("$$###");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfLeftItemTextLengthIsExceeded() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_LEFT_ITEM_TEXT.getColumnNumber()).setCellValue(generateString(4001));
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfRightItemTextLengthIsExceeded() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_RIGHT_ITEM_TEXT.getColumnNumber()).setCellValue(generateString(2001));
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemHeaderLengthIsExceeded() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_HEADER.getColumnNumber()).setCellValue(generateString(2001));
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemSubHeaderLengthIsExceeded() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_SUBHEADER.getColumnNumber()).setCellValue(generateString(241));
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemGroupLabelIsWrong() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_GROUP_LABEL.getColumnNumber()).setCellValue("WRONG GROUP");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemSectionLabelLengthIsExceeded() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_SECTION_LABEL.getColumnNumber()).setCellValue("WRONG SECTION");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemGroupLabelLengthIsExceeded() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_GROUP_LABEL.getColumnNumber()).setCellValue(generateString(256));
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemSectionLabelIsWrong() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_SECTION_LABEL.getColumnNumber()).setCellValue(generateString(2001));
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemUnitLengthIsExceeded() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_UNITS.getColumnNumber()).setCellValue(generateString(65));
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemHasWrongParentItem() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_PARENT_ITEM.getColumnNumber()).setCellValue("WRONG ITEM");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfResponseTypeIsBlank() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_RESPONSE_TYPE.getColumnNumber()).setCellValue("");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfResponseTypeIsNotValid() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_RESPONSE_TYPE.getColumnNumber()).setCellValue("WRONG TYPE");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfThereAreNestedParentItems() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_PARENT_ITEM.getColumnNumber()).setCellValue("CM001_TXT_INT1");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(3)
				.createCell(CellName.ITEM_PARENT_ITEM.getColumnNumber()).setCellValue("CM001_TXT_INT2");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfThereAreRepeatingGroupsWithParentItems() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_PARENT_ITEM.getColumnNumber()).setCellValue("CM001_TXT_INT1");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_GROUP_LABEL.getColumnNumber()).setCellValue("group2");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfRadioHasItemWithDefaultValue() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_TYPE.getColumnNumber()).setCellValue("radio");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_DEFAULT_VALUE.getColumnNumber()).setCellValue("1");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfResponseLabelIsBlank() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_RESPONSE_LABEL.getColumnNumber()).setCellValue("");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfResponseOptionsTextIsBlank() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_RESPONSE_OPTIONS_TEXT.getColumnNumber()).setCellValue("");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfResponseOptionsValuesIsBlank() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_RESPONSE_VALUES_OR_CALCULATIONS.getColumnNumber()).setCellValue("");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemHasIncompleteOptionValuePair() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_RESPONSE_OPTIONS_TEXT.getColumnNumber()).setCellValue("a,b,c,d,e");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_RESPONSE_VALUES_OR_CALCULATIONS.getColumnNumber())
				.setCellValue("0,1,2,3,4,5");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemHasDifferentNumberOfOptionsValuesForTheSameResponseLabel()
			throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_TYPE.getColumnNumber()).setCellValue("radio");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_LABEL.getColumnNumber()).setCellValue("yyy");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_OPTIONS_TEXT.getColumnNumber()).setCellValue("a,b,c,d,e");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_VALUES_OR_CALCULATIONS.getColumnNumber())
				.setCellValue("0,1,2,3,4,5");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemHasDifferentValuesForOptionsValuesForTheSameResponseLabel()
			throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_TYPE.getColumnNumber()).setCellValue("radio");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_LABEL.getColumnNumber()).setCellValue("yyy");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_OPTIONS_TEXT.getColumnNumber()).setCellValue("a,b,c,d,e");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_VALUES_OR_CALCULATIONS.getColumnNumber()).setCellValue("0,1,7,3,4");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemHasDifferentNumberOfOptionsTextForTheSameResponseLabel()
			throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_TYPE.getColumnNumber()).setCellValue("radio");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_LABEL.getColumnNumber()).setCellValue("yyy");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_OPTIONS_TEXT.getColumnNumber()).setCellValue("a,b,c,d,e,z");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_VALUES_OR_CALCULATIONS.getColumnNumber()).setCellValue("0,1,2,3,4");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfExpressionDoesNotStartWithFunc() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_TYPE.getColumnNumber()).setCellValue("calculation");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_VALUES_OR_CALCULATIONS.getColumnNumber()).setCellValue(": 2+2");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfExpressionIsNotValid() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_TYPE.getColumnNumber()).setCellValue("calculation");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_VALUES_OR_CALCULATIONS.getColumnNumber())
				.setCellValue("func: $#2*/d;2+2");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemMustBeListedBeforeAnotherItem() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_TYPE.getColumnNumber()).setCellValue("calculation");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_VALUES_OR_CALCULATIONS.getColumnNumber())
				.setCellValue("CM001_TXT_INT3 + CM001_TXT_INT2");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemMustHaveTheSameGroup() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_TYPE.getColumnNumber()).setCellValue("calculation");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_VALUES_OR_CALCULATIONS.getColumnNumber())
				.setCellValue("CM001_TXT_INT1 + CM001_TXT_INT2");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_GROUP_LABEL.getColumnNumber()).setCellValue("group2");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemsShouldNotHaveTheSameGroup() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_TYPE.getColumnNumber()).setCellValue("group-calculation");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_VALUES_OR_CALCULATIONS.getColumnNumber())
				.setCellValue("CM001_TXT_INT1 + CM001_TXT_INT2");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_GROUP_LABEL.getColumnNumber()).setCellValue("group2");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_GROUP_LABEL.getColumnNumber()).setCellValue("group2");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemDataTypeIsBlank() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_DATA_TYPE.getColumnNumber()).setCellValue("");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemDataTypeIsNotValid() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_DATA_TYPE.getColumnNumber()).setCellValue("WRONG DATA TYPE");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemDataTypeShouldBeFile() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_RESPONSE_TYPE.getColumnNumber()).setCellValue("file");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_DATA_TYPE.getColumnNumber()).setCellValue("ST");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemDataTypeShouldBeInteger() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_DATA_TYPE.getColumnNumber()).setCellValue("INT");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_RESPONSE_VALUES_OR_CALCULATIONS.getColumnNumber()).setCellValue("a,1,2,3,4");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemDataTypeShouldBeReal() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_DATA_TYPE.getColumnNumber()).setCellValue("REAL");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_RESPONSE_VALUES_OR_CALCULATIONS.getColumnNumber()).setCellValue("a,1,2,3,4");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfWidthDecimalIsNotAvailable() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_WIDTH_DECIMAL.getColumnNumber()).setCellValue("10");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfWidthDecimalIsNotValid() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_WIDTH_DECIMAL.getColumnNumber()).setCellValue("x10");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfValidationColumnIsNotValid() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_VALIDATION.getColumnNumber()).setCellValue(":#$afasdg32x10");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_VALIDATION_ERROR_MESSAGE.getColumnNumber()).setCellValue("BLA");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfValidationMessageColumnIsBlank() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_VALIDATION.getColumnNumber()).setCellValue("regexp: /2.*/");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_VALIDATION_ERROR_MESSAGE.getColumnNumber()).setCellValue("");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfValidationMessageLengthIsExceeded() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_VALIDATION.getColumnNumber()).setCellValue("regexp: /2.*/");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_VALIDATION_ERROR_MESSAGE.getColumnNumber()).setCellValue(generateString(256));
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfRegexpIsNotValid() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_VALIDATION.getColumnNumber()).setCellValue("func: #$afasdg32x10");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_VALIDATION_ERROR_MESSAGE.getColumnNumber()).setCellValue("BLA");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfRegexpIsInvalidRegularExpression() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_VALIDATION.getColumnNumber()).setCellValue("regexp: #$afasdg32x10");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_VALIDATION_ERROR_MESSAGE.getColumnNumber()).setCellValue("BLA");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfValidationColumnHasInvalidRegularExpression() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_VALIDATION.getColumnNumber()).setCellValue("regexp: \\\\#$afasdg32x10");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_VALIDATION_ERROR_MESSAGE.getColumnNumber()).setCellValue("BLA");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfPhiIsNotValid() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1).createCell(CellName.ITEM_PHI.getColumnNumber())
				.setCellValue("WRONG VALUE");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfRequiredIsNotValid() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_REQUIRED.getColumnNumber()).setCellValue("WRONG VALUE");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemDisplayStatusIsNotValid() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_DISPLAY_STATUS.getColumnNumber()).setCellValue("SHOW");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_SIMPLE_CONDITIONAL_DISPLAY.getColumnNumber())
				.setCellValue("CM001_TXT_INT1,1,MESSAGE!");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfControlResponseValueIsNotValid() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_DISPLAY_STATUS.getColumnNumber()).setCellValue("HIDE");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_SIMPLE_CONDITIONAL_DISPLAY.getColumnNumber())
				.setCellValue("CM001_TXT_INT1,345,MESSAGE!");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfControlItemNameIsNotValid() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_DISPLAY_STATUS.getColumnNumber()).setCellValue("HIDE");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_SIMPLE_CONDITIONAL_DISPLAY.getColumnNumber())
				.setCellValue("CM001_TXT_INT345,1,MESSAGE!");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfSCDIsNotValid() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_DISPLAY_STATUS.getColumnNumber()).setCellValue("HIDE");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_SIMPLE_CONDITIONAL_DISPLAY.getColumnNumber())
				.setCellValue("CM001_TXT_INT1,1");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfOntologyNameIsNotValid() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_DATA_TYPE.getColumnNumber()).setCellValue("CODE");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_CODE_REF.getColumnNumber()).setCellValue("WRONG VALUE");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfNeedToUpdateMedicalCodingReferenceItemType() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_DATA_TYPE.getColumnNumber()).setCellValue("INT");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_CODE_REF.getColumnNumber()).setCellValue("MEDDRA");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfResponseLabelHasBeenUsedForAnotherResponseType()
			throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_TYPE.getColumnNumber()).setCellValue("single-select");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_LABEL.getColumnNumber()).setCellValue("yyy");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_OPTIONS_TEXT.getColumnNumber()).setCellValue("a,b,c,d,e");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_VALUES_OR_CALCULATIONS.getColumnNumber()).setCellValue("0,1,2,3,4");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemOfOneGroupBelongsToMoreThanOneSection() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.SECTIONS.getSheetNumber()).createRow(2)
				.createCell(CellName.SECTION_LABEL.getColumnNumber()).setCellValue("section2");
		workbook.getSheetAt(SheetName.SECTIONS.getSheetNumber()).getRow(2)
				.createCell(CellName.SECTION_TITLE.getColumnNumber()).setCellValue("section2");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_SECTION_LABEL.getColumnNumber()).setCellValue("section2");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_GROUP_LABEL.getColumnNumber()).setCellValue("group2");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_GROUP_LABEL.getColumnNumber()).setCellValue("group2");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfThereIsNotUniqueItemPlacementInGroups() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.SECTIONS.getSheetNumber()).createRow(2)
				.createCell(CellName.SECTION_LABEL.getColumnNumber()).setCellValue("section2");
		workbook.getSheetAt(SheetName.SECTIONS.getSheetNumber()).getRow(2)
				.createCell(CellName.SECTION_TITLE.getColumnNumber()).setCellValue("section2");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_SECTION_LABEL.getColumnNumber()).setCellValue("section2");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_GROUP_LABEL.getColumnNumber()).setCellValue("group2");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_GROUP_LABEL.getColumnNumber()).setCellValue("group2");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
	}
}
