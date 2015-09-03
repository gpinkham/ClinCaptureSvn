package com.clinovo.lib.crf.validator;

import java.io.InputStream;
import java.util.Locale;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;

import com.clinovo.lib.crf.builder.impl.ExcelCrfBuilder;
import com.clinovo.lib.crf.enums.CellName;
import com.clinovo.lib.crf.enums.SheetName;
import com.clinovo.lib.crf.factory.CrfBuilderFactory;

// it will be covered with all cases in next patch for #2492
public class CrfValidatorTest extends DefaultAppContextTest {

	private StudyBean studyBean;

	private UserAccountBean owner;

	private ExcelCrfBuilder crfBuilder;

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

	@Test
	public void testThatCrfBuilderGeneratesErrorIfAnItemInTheTestCrfHasIncorrectGroupLabel() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.getCell(CellName.ITEM_GROUP_LABEL.getColumnNumber()).setCellValue("WRONG GROUP");
		crfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				ResourceBundleProvider.getPageMessagesBundle());
		crfBuilder.build();
		assertTrue(crfBuilder.getErrorsList().size() > 0);
		assertTrue(crfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatCrfBuilderGeneratesErrorIfAnItemInTheTestCrfHasIncorrectSectionLabel() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.getCell(CellName.ITEM_SECTION_LABEL.getColumnNumber()).setCellValue("WRONG SECTION");
		crfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				ResourceBundleProvider.getPageMessagesBundle());
		crfBuilder.build();
		assertTrue(crfBuilder.getErrorsList().size() > 0);
		assertTrue(crfBuilder.getErrorsMap().size() > 0);
	}

	@Test
	public void testThatCrfBuilderGeneratesErrorIfAnItemInTheTestCrfHasIncorrectResponseType() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.getCell(CellName.ITEM_RESPONSE_TYPE.getColumnNumber()).setCellValue("WRONG TYPE");
		crfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				ResourceBundleProvider.getPageMessagesBundle());
		crfBuilder.build();
		assertTrue(crfBuilder.getErrorsList().size() > 0);
		assertTrue(crfBuilder.getErrorsMap().size() > 0);
	}
}
