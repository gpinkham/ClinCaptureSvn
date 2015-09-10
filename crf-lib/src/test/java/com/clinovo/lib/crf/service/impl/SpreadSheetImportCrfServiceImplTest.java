package com.clinovo.lib.crf.service.impl;

import java.io.InputStream;
import java.util.Locale;

import com.clinovo.lib.crf.enums.CRFSource;
import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.DefaultResourceLoader;

import com.clinovo.lib.crf.builder.CrfBuilder;
import com.clinovo.lib.crf.factory.CrfBuilderFactory;

public class SpreadSheetImportCrfServiceImplTest extends DefaultAppContextTest {

	private StudyBean studyBean;

	private UserAccountBean owner;

	private CrfBuilder crfBuilder;

	@Autowired
	private CrfBuilderFactory crfBuilderFactory;

	@Mock
	private MessageSource messageSource;

	@Override
	protected void restoreDb() throws Exception {
		// do not restore db
	}

	@Before
	public void before() {
		studyBean = (StudyBean) studyDAO.findByPK(1);
		owner = (UserAccountBean) userAccountDAO.findByPK(1);
	}

	@After
	public void after() {
		if (crfBuilder != null && crfBuilder.getCrfBean() != null && crfBuilder.getCrfBean().getId() > 0) {
			deleteCrfService.deleteCrf(crfBuilder.getCrfBean().getId());
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

	@Test
	public void testThatCrfBuilderProcessesTheTestCrfWithCorrectQuantityOfSections() throws Exception {
		crfBuilder = crfBuilderFactory.getCrfBuilder(getWorkbook("testCrf.xls"), studyBean, owner, Locale.ENGLISH,
				messageSource);
		crfBuilder.build();
		assertEquals(crfBuilder.getSections().size(), 1);
	}

	@Test
	public void testThatCrfBuilderProcessesTheTestCrfWithCorrectQuantityOfItemGroups() throws Exception {
		crfBuilder = crfBuilderFactory.getCrfBuilder(getWorkbook("testCrf.xls"), studyBean, owner, Locale.ENGLISH,
				messageSource);
		crfBuilder.build();
		assertEquals(crfBuilder.getItemGroups().size(), 3);
	}

	@Test
	public void testThatCrfBuilderProcessesTheTestCrfJsonWithCorrectQuantityOfItems() throws Exception {
		crfBuilder = crfBuilderFactory.getCrfBuilder(getWorkbook("testCrf.xls"), studyBean, owner, Locale.ENGLISH,
				messageSource);
		crfBuilder.build();
		assertEquals(crfBuilder.getItems().size(), 9);
	}

	@Test
	public void testThatCrfBuilderProcessesTheTestCrfWithCorrectCrfName() throws Exception {
		crfBuilder = crfBuilderFactory.getCrfBuilder(getWorkbook("testCrf.xls"), studyBean, owner, Locale.ENGLISH,
				messageSource);
		crfBuilder.build();
		assertEquals(crfBuilder.getCrfBean().getName(), "testCRF");
	}

	@Test
	public void testThatCrfBuilderProcessesTheTestCrfWithCorrectCrfVersion() throws Exception {
		crfBuilder = crfBuilderFactory.getCrfBuilder(getWorkbook("testCrf.xls"), studyBean, owner, Locale.ENGLISH,
				messageSource);
		crfBuilder.build();
		assertEquals(crfBuilder.getCrfVersionBean().getName(), "v1.0");
	}

	@Test
	public void testThatCrfBuilderProcessesTheTestCrfWithCorrectCrfSource() throws Exception {
		crfBuilder = crfBuilderFactory.getCrfBuilder(getWorkbook("testCrf.xls"), studyBean, owner, Locale.ENGLISH,
				messageSource);
		crfBuilder.build();
		assertEquals(CRFSource.SOURCE_DEFAULT.getSourceName(), crfBuilder.getCrfBean().getSource());
	}

	@Test
	public void testThatCrfBuilderSavesDataFromTheTestCrfCorrectly() throws Exception {
		crfBuilder = crfBuilderFactory.getCrfBuilder(getWorkbook("testCrf.xls"), studyBean, owner, Locale.ENGLISH,
				messageSource);
		crfBuilder.build();
		crfBuilder.save();
		CRFBean crfBean = (CRFBean) crfdao.findByPK(crfBuilder.getCrfBean().getId());
		assertEquals(crfBean.getName(), "testCRF");
		assertTrue(crfBean.getId() > 0);
		CRFVersionBean crfVersionBean = (CRFVersionBean) crfVersionDao.findByPK(crfBuilder.getCrfBean().getId());
		assertEquals(crfVersionBean.getName(), "v1.0");
		assertTrue(crfVersionBean.getId() > 0);
	}
}
