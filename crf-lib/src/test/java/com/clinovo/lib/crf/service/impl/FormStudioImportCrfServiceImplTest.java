package com.clinovo.lib.crf.service.impl;

import java.util.Locale;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;

import com.clinovo.lib.crf.bean.ItemBeanExt;
import com.clinovo.lib.crf.builder.CrfBuilder;
import com.clinovo.lib.crf.enums.CRFSource;
import com.clinovo.lib.crf.enums.Dictionary;
import com.clinovo.lib.crf.factory.CrfBuilderFactory;

public class FormStudioImportCrfServiceImplTest extends DefaultAppContextTest {

	private StudyBean studyBean;

	private UserAccountBean owner;

	private CrfBuilder crfBuilder;

	@Autowired
	private CrfBuilderFactory crfBuilderFactory;

	@Override
	protected void restoreDb() throws Exception {
		// do not restore db
	}

	@Before
	public void before() {
		ResourceBundleProvider.updateLocale(Locale.ENGLISH);
		studyBean = (StudyBean) studyDAO.findByPK(1);
		owner = (UserAccountBean) userAccountDAO.findByPK(1);
	}

	@After
	public void after() throws Exception {
		if (crfBuilder != null && crfBuilder.getCrfBean() != null && crfBuilder.getCrfBean().getId() > 0) {
			deleteCrfService.deleteCrf(crfBuilder.getCrfBean(), owner, Locale.ENGLISH, false);
		}
	}

	@Test
	public void testThatCrfBuilderProcessesTheTestCrfWithCorrectQuantityOfSections() throws Exception {
		String jsonData = IOUtils
				.toString(new DefaultResourceLoader().getResource("data/json/testCrf.json").getInputStream(), "UTF-8");
		crfBuilder = crfBuilderFactory.getCrfBuilder(jsonData, studyBean, owner, Locale.ENGLISH, messageSource);
		crfBuilder.build();
		assertEquals(crfBuilder.getSections().size(), 2);
	}

	@Test
	public void testThatCrfBuilderProcessesTheTestCrfWithCorrectSectionSubtitle() throws Exception {
		String jsonData = IOUtils
				.toString(new DefaultResourceLoader().getResource("data/json/testCrf.json").getInputStream(), "UTF-8");
		crfBuilder = crfBuilderFactory.getCrfBuilder(jsonData, studyBean, owner, Locale.ENGLISH, messageSource);
		crfBuilder.build();
		assertEquals(crfBuilder.getSections().get(0).getSubtitle(), "stt");
	}

	@Test
	public void testThatCrfBuilderProcessesTheTestCrfWithCorrectQuantityOfItemGroups() throws Exception {
		String jsonData = IOUtils
				.toString(new DefaultResourceLoader().getResource("data/json/testCrf.json").getInputStream(), "UTF-8");
		crfBuilder = crfBuilderFactory.getCrfBuilder(jsonData, studyBean, owner, Locale.ENGLISH, messageSource);
		crfBuilder.build();
		assertEquals(crfBuilder.getItemGroups().size(), 2);
	}

	@Test
	public void testThatCrfBuilderProcessesTheTestCrfWithCorrectQuantityOfItems() throws Exception {
		String jsonData = IOUtils
				.toString(new DefaultResourceLoader().getResource("data/json/testCrf.json").getInputStream(), "UTF-8");
		crfBuilder = crfBuilderFactory.getCrfBuilder(jsonData, studyBean, owner, Locale.ENGLISH, messageSource);
		crfBuilder.build();
		assertEquals(crfBuilder.getItems().size(), 33);
	}

	@Test
	public void testThatCrfBuilderProcessesTheTestCrfWithCorrectCrfName() throws Exception {
		String jsonData = IOUtils
				.toString(new DefaultResourceLoader().getResource("data/json/testCrf.json").getInputStream(), "UTF-8");
		crfBuilder = crfBuilderFactory.getCrfBuilder(jsonData, studyBean, owner, Locale.ENGLISH, messageSource);
		crfBuilder.build();
		assertEquals(crfBuilder.getCrfBean().getName(), "FS Test CRF");
	}

	@Test
	public void testThatCrfBuilderProcessesTheTestCrfWithCorrectCrfVersion() throws Exception {
		String jsonData = IOUtils
				.toString(new DefaultResourceLoader().getResource("data/json/testCrf.json").getInputStream(), "UTF-8");
		crfBuilder = crfBuilderFactory.getCrfBuilder(jsonData, studyBean, owner, Locale.ENGLISH, messageSource);
		crfBuilder.build();
		assertEquals(crfBuilder.getCrfVersionBean().getName(), "v1.0");
	}

	@Test
	public void testThatCrfBuilderProcessesTheTestCrfWithCorrectCrfSource() throws Exception {
		String jsonData = IOUtils
				.toString(new DefaultResourceLoader().getResource("data/json/testCrf.json").getInputStream(), "UTF-8");
		crfBuilder = crfBuilderFactory.getCrfBuilder(jsonData, studyBean, owner, Locale.ENGLISH, messageSource);
		crfBuilder.build();
		assertEquals(crfBuilder.getCrfBean().getSource(), CRFSource.SOURCE_FORM_STUDIO.getSourceName());
	}

	@Test
	public void testThatCrfBuilderSavesDataFromTheTestCrfCorrectly() throws Exception {
		String jsonData = IOUtils
				.toString(new DefaultResourceLoader().getResource("data/json/testCrf.json").getInputStream(), "UTF-8");
		crfBuilder = crfBuilderFactory.getCrfBuilder(jsonData, studyBean, owner, Locale.ENGLISH, messageSource);
		crfBuilder.build();
		crfBuilder.save();
		CRFBean crfBean = (CRFBean) crfdao.findByPK(crfBuilder.getCrfBean().getId());
		assertEquals(crfBean.getName(), "FS Test CRF");
		assertTrue(crfBean.getId() > 0);
		CRFVersionBean crfVersionBean = (CRFVersionBean) crfVersionDao.findByPK(crfBuilder.getCrfBean().getId());
		assertEquals(crfVersionBean.getName(), "v1.0");
		assertTrue(crfVersionBean.getId() > 0);
	}

	@Test
	public void testThatCrfBuilderCreatesCorrectQuantityOfCodingItemsForDictionaryCTCAE() throws Exception {
		String jsonData = IOUtils.toString(
				new DefaultResourceLoader().getResource("data/json/testCodingCrf.json").getInputStream(), "UTF-8");
		crfBuilder = crfBuilderFactory.getCrfBuilder(jsonData, studyBean, owner, Locale.ENGLISH, messageSource);
		crfBuilder.build();
		assertEquals(crfBuilder.getItems().size(), 8);
	}

	@Test
	public void testThatCrfBuilderCreatesCorrectQuantityOfCodingItemsForDictionaryICD9CM() throws Exception {
		String jsonData = IOUtils.toString(
				new DefaultResourceLoader().getResource("data/json/testCodingCrf.json").getInputStream(), "UTF-8");
		jsonData = jsonData.replaceAll("\"dictionary\":\"ctcae\"",
				"\"dictionary\":\"".concat(Dictionary.ICD_9CM.getName()).concat("\""));
		crfBuilder = crfBuilderFactory.getCrfBuilder(jsonData, studyBean, owner, Locale.ENGLISH, messageSource);
		crfBuilder.build();
		assertEquals(crfBuilder.getItems().size(), 7);
	}

	@Test
	public void testThatCrfBuilderCreatesCorrectQuantityOfCodingItemsForDictionaryICD10() throws Exception {
		String jsonData = IOUtils.toString(
				new DefaultResourceLoader().getResource("data/json/testCodingCrf.json").getInputStream(), "UTF-8");
		jsonData = jsonData.replaceAll("\"dictionary\":\"ctcae\"",
				"\"dictionary\":\"".concat(Dictionary.ICD_10.getName()).concat("\""));
		crfBuilder = crfBuilderFactory.getCrfBuilder(jsonData, studyBean, owner, Locale.ENGLISH, messageSource);
		crfBuilder.build();
		assertEquals(crfBuilder.getItems().size(), 7);
	}

	@Test
	public void testThatCrfBuilderCreatesCorrectQuantityOfCodingItemsForDictionaryMEDDRA171() throws Exception {
		String jsonData = IOUtils.toString(
				new DefaultResourceLoader().getResource("data/json/testCodingCrf.json").getInputStream(), "UTF-8");
		jsonData = jsonData.replaceAll("\"dictionary\":\"ctcae\"",
				"\"dictionary\":\"".concat(Dictionary.MEDDRA_171.getName()).concat("\""));
		crfBuilder = crfBuilderFactory.getCrfBuilder(jsonData, studyBean, owner, Locale.ENGLISH, messageSource);
		crfBuilder.build();
		assertEquals(crfBuilder.getItems().size(), 11);
	}

	@Test
	public void testThatCrfBuilderCreatesCorrectQuantityOfCodingItemsForDictionaryMEDDRA180() throws Exception {
		String jsonData = IOUtils.toString(
				new DefaultResourceLoader().getResource("data/json/testCodingCrf.json").getInputStream(), "UTF-8");
		jsonData = jsonData.replaceAll("\"dictionary\":\"ctcae\"",
				"\"dictionary\":\"".concat(Dictionary.MEDDRA_180.getName()).concat("\""));
		crfBuilder = crfBuilderFactory.getCrfBuilder(jsonData, studyBean, owner, Locale.ENGLISH, messageSource);
		crfBuilder.build();
		assertEquals(crfBuilder.getItems().size(), 11);
	}

	@Test
	public void testThatCrfBuilderCreatesCorrectQuantityOfCodingItemsForDictionaryWHODRUGSEP14() throws Exception {
		String jsonData = IOUtils.toString(
				new DefaultResourceLoader().getResource("data/json/testCodingCrf.json").getInputStream(), "UTF-8");
		jsonData = jsonData.replaceAll("\"dictionary\":\"ctcae\"",
				"\"dictionary\":\"".concat(Dictionary.WHODRUG_SEP_14.getName()).concat("\""));
		crfBuilder = crfBuilderFactory.getCrfBuilder(jsonData, studyBean, owner, Locale.ENGLISH, messageSource);
		crfBuilder.build();
		assertEquals(crfBuilder.getItems().size(), 19);
	}

	@Test
	public void testThatCrfBuilderCreatesCorrectQuantityOfCodingItemsForDictionaryWHODRUGMAR15() throws Exception {
		String jsonData = IOUtils.toString(
				new DefaultResourceLoader().getResource("data/json/testCodingCrf.json").getInputStream(), "UTF-8");
		jsonData = jsonData.replaceAll("\"dictionary\":\"ctcae\"",
				"\"dictionary\":\"".concat(Dictionary.WHODRUG_MAR_15.getName()).concat("\""));
		crfBuilder = crfBuilderFactory.getCrfBuilder(jsonData, studyBean, owner, Locale.ENGLISH, messageSource);
		crfBuilder.build();
		assertEquals(crfBuilder.getItems().size(), 19);
	}

	@Test
	public void testThatCrfBuilderProcessesTheTestCrfAndSavesItemRenderMetadata() throws Exception {
		String jsonData = IOUtils
				.toString(new DefaultResourceLoader().getResource("data/json/testCrf.json").getInputStream(), "UTF-8");
		crfBuilder = crfBuilderFactory.getCrfBuilder(jsonData, studyBean, owner, Locale.ENGLISH, messageSource);
		crfBuilder.build();
		assertNotNull(crfBuilder.getItems().get(0).getItemRenderMetadata());
	}

	@Test
	public void testThatCrfBuilderProcessesTheTestCrfAndSavesItemRenderMetadataWithCorrectWidth() throws Exception {
		String jsonData = IOUtils
				.toString(new DefaultResourceLoader().getResource("data/json/testCrf.json").getInputStream(), "UTF-8");
		crfBuilder = crfBuilderFactory.getCrfBuilder(jsonData, studyBean, owner, Locale.ENGLISH, messageSource);
		crfBuilder.build();
		assertEquals(350, crfBuilder.getItems().get(0).getItemRenderMetadata().getWidth());
	}

	@Test
	public void testThatCrfBuilderProcessesTheTestCrfAndSavesItemRenderMetadataWithCorrectLeftTextWidth()
			throws Exception {
		String jsonData = IOUtils
				.toString(new DefaultResourceLoader().getResource("data/json/testCrf.json").getInputStream(), "UTF-8");
		crfBuilder = crfBuilderFactory.getCrfBuilder(jsonData, studyBean, owner, Locale.ENGLISH, messageSource);
		crfBuilder.build();
		assertEquals(50, crfBuilder.getItems().get(0).getItemRenderMetadata().getLeftItemTextWidth());
	}

	@Test
	public void testThatCrfBuilderProcessesTheTestCrfOptionsWithCommasCorrectly() throws Exception {
		String jsonData = IOUtils.toString(
				new DefaultResourceLoader().getResource("data/json/testCrfOptionsWithCommas.json").getInputStream(),
				"UTF-8");
		crfBuilder = crfBuilderFactory.getCrfBuilder(jsonData, studyBean, owner, Locale.ENGLISH, messageSource);
		crfBuilder.build();
		assertEquals(((ItemBeanExt) crfBuilder.getItems().get(0)).getResponseSet().getOptionsText(),
				"option with \\, comma 1,option with \\, comma 2");
		assertEquals(((ItemBeanExt) crfBuilder.getItems().get(0)).getResponseSet().getOptionsValues(), "0,1");
		assertEquals(((ItemBeanExt) crfBuilder.getItems().get(1)).getResponseSet().getOptionsText(),
				"option \\, comma 1,option \\, comma 2,option \\, comma 3");
		assertEquals(((ItemBeanExt) crfBuilder.getItems().get(1)).getResponseSet().getOptionsValues(), "0,1,2");
	}
}
