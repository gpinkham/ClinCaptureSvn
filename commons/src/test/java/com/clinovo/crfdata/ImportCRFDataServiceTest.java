package com.clinovo.crfdata;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXSource;

import junit.framework.TestCase;
import org.akaza.openclinica.AbstractContextSentiveTest;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.DisplayItemBeanWrapper;
import org.akaza.openclinica.bean.submit.crfdata.ODMContainer;
import org.akaza.openclinica.dao.hibernate.ConfigurationDao;
import org.akaza.openclinica.service.rule.RuleSetService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.xml.sax.InputSource;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.service.ItemSDVService;
import com.clinovo.service.StudySubjectIdService;
import com.clinovo.util.ValidatorHelper;

public class ImportCRFDataServiceTest extends AbstractContextSentiveTest {

	protected class ObjectsHolder {
		protected ImportCRFDataService service;
		protected ODMContainer container;
		protected InputStream stream;
		protected StudySubjectIdService studySubjectIdService;
		protected MockHttpServletRequest request = new MockHttpServletRequest();
		protected UserAccountBean ub;
		protected ArrayList<Integer> permittedEventCRFIds = new ArrayList<Integer>();
		protected ValidatorHelper validatorHelper;
		protected ConfigurationDao configurationDao;
		{
			studySubjectIdService = Mockito.mock(StudySubjectIdService.class);
			configurationDao = Mockito.mock(ConfigurationDao.class);

			ub = new UserAccountBean();
			ub.setId(1);

			permittedEventCRFIds.add(1);
			permittedEventCRFIds.add(2);
			permittedEventCRFIds.add(3);
			permittedEventCRFIds.add(11);
			permittedEventCRFIds.add(12);
			permittedEventCRFIds.add(13);

			Locale testLocale = new Locale(AbstractContextSentiveTest.locale);
			LocaleResolver.updateLocale(request, testLocale);
			validatorHelper = new ValidatorHelper(request, configurationDao);
		}
	}

	protected ObjectsHolder holder = new ObjectsHolder();
	protected ObjectsHolder holder1 = new ObjectsHolder();
	protected ObjectsHolder holder2 = new ObjectsHolder();
	protected ObjectsHolder holder3 = new ObjectsHolder();

	@Before
	public void setUp() throws Exception {
		super.setUp();
		parseFile(holder, "DataImportServletXmlTest.xml");
		parseFile(holder1, "import1.xml");
		parseFile(holder2, "import2.xml");
		parseFile(holder3, "import3.xml");
	}

	private void parseFile(ObjectsHolder objectsHolder, String fileName) throws Exception {
		Locale locale = new Locale("EN");
		objectsHolder.service = new ImportCRFDataService(Mockito.mock(RuleSetService.class),
				Mockito.mock(ItemSDVService.class), objectsHolder.studySubjectIdService, getDataSource(), locale);
		objectsHolder.container = new ODMContainer();
		objectsHolder.stream = this.getClass().getClassLoader().getResourceAsStream("data/" + fileName);
		JAXBContext jaxbContext = JAXBContext.newInstance(ODMContainer.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		if (objectsHolder.stream != null) {
			InputSource inputSource = new InputSource(objectsHolder.stream);
			SAXSource saxSource = new SAXSource(inputSource);
			try {
				objectsHolder.container = (ODMContainer) jaxbUnmarshaller.unmarshal(saxSource);
			} catch (Exception e) {
				TestCase.fail("Unmarshaller exception: " + e.getMessage());
			}
		} else {
			TestCase.fail("XML not found!");
		}
	}

	private int filterAutoAddedCount(List<DisplayItemBeanWrapper> wrappers, int index) {
		int countFilterAutoAdded = 0;
		for (DisplayItemBean item : wrappers.get(index).getDisplayItemBeans()) {
			if (!item.getAutoAdded()) {
				countFilterAutoAdded++;
			}
		}
		return countFilterAutoAdded;
	}

	@Test
	public void testThatErrorMessagesListIsNotNull() {
		int currentStudyId = 1;
		TestCase.assertNotNull(holder.service.validateStudyMetadata(holder.container, currentStudyId, holder.ub));
	}

	@Test
	public void testThatSizeOfTheErrorMessagesListIsCorrect() {
		int currentStudyId = 1;
		TestCase.assertEquals(holder.service.validateStudyMetadata(holder.container, currentStudyId, holder.ub).size(),
				1);
	}

	@Test
	public void testThatGetSubjectDataFromTestFile1ReturnsCorrectSize() throws Exception {
		TestCase.assertEquals(holder1.container.getCrfDataPostImportContainer().getSubjectData().size(), 1);
	}

	@Test
	public void testThatGetStudyEventDataFromTestFile1ReturnsCorrectSize() throws Exception {
		TestCase.assertEquals(
				holder1.container.getCrfDataPostImportContainer().getSubjectData().get(0).getStudyEventData().size(), 1);
	}

	@Test
	public void testThatGetFormDataFromTestFile1ReturnsCorrectSize() throws Exception {
		TestCase.assertEquals(
				holder1.container.getCrfDataPostImportContainer().getSubjectData().get(0).getStudyEventData().get(0)
						.getFormData().size(), 2);
	}

	@Test
	public void testThatGetItemGroupDataFromFirstFormDataFromTestFile1ReturnsCorrectSize() throws Exception {
		TestCase.assertEquals(
				holder1.container.getCrfDataPostImportContainer().getSubjectData().get(0).getStudyEventData().get(0)
						.getFormData().get(0).getItemGroupData().size(), 1);
	}

	@Test
	public void testThatGetItemGroupDataFromSecondFormDataFromTestFile1ReturnsCorrectSize() throws Exception {
		TestCase.assertEquals(
				holder1.container.getCrfDataPostImportContainer().getSubjectData().get(0).getStudyEventData().get(0)
						.getFormData().get(1).getItemGroupData().size(), 1);
	}

	@Test
	public void testThatGetSubjectDataFromTestFile2ReturnsCorrectSize() throws Exception {
		TestCase.assertEquals(holder2.container.getCrfDataPostImportContainer().getSubjectData().size(), 1);
	}

	@Test
	public void testThatGetStudyEventDataFromTestFile2ReturnsCorrectSize() throws Exception {
		TestCase.assertEquals(
				holder2.container.getCrfDataPostImportContainer().getSubjectData().get(0).getStudyEventData().size(), 2);
	}

	@Test
	public void testThatGetFormDataFromFirstStudyEventFromTestFile2ReturnsCorrectSize() throws Exception {
		TestCase.assertEquals(
				holder2.container.getCrfDataPostImportContainer().getSubjectData().get(0).getStudyEventData().get(0)
						.getFormData().size(), 1);
	}

	@Test
	public void testThatGetFormDataFromSecondStudyEventFromTestFile2ReturnsCorrectSize() throws Exception {
		TestCase.assertEquals(
				holder2.container.getCrfDataPostImportContainer().getSubjectData().get(0).getStudyEventData().get(1)
						.getFormData().size(), 1);
	}

	@Test
	public void testThatGetItemGroupDataFromFirstStudyEventFromTestFile2ReturnsCorrectSize() throws Exception {
		TestCase.assertEquals(
				holder2.container.getCrfDataPostImportContainer().getSubjectData().get(0).getStudyEventData().get(0)
						.getFormData().get(0).getItemGroupData().size(), 5);
	}

	@Test
	public void testThatGetItemGroupDataFromSecondStudyEventFromTestFile2ReturnsCorrectSize() throws Exception {
		TestCase.assertEquals(
				holder2.container.getCrfDataPostImportContainer().getSubjectData().get(0).getStudyEventData().get(1)
						.getFormData().get(0).getItemGroupData().size(), 5);
	}

	@Test
	public void testThatGetSubjectDataFromTestFile3ReturnsCorrectSize() throws Exception {
		TestCase.assertEquals(holder3.container.getCrfDataPostImportContainer().getSubjectData().size(), 1);
	}

	@Test
	public void testThatGetStudyEventFromTestFile3ReturnsCorrectSize() throws Exception {
		TestCase.assertEquals(
				holder3.container.getCrfDataPostImportContainer().getSubjectData().get(0).getStudyEventData().size(), 1);
	}

	@Test
	public void testThatGetFormDataFromTestFile3ReturnsCorrectSize() throws Exception {
		TestCase.assertEquals(
				holder3.container.getCrfDataPostImportContainer().getSubjectData().get(0).getStudyEventData().get(0)
						.getFormData().size(), 1);
	}

	@Test
	public void testThatGetItemGroupDataFromTestFile3ReturnsCorrectSize() throws Exception {
		TestCase.assertEquals(
				holder3.container.getCrfDataPostImportContainer().getSubjectData().get(0).getStudyEventData().get(0)
						.getFormData().get(0).getItemGroupData().size(), 6);
	}

	@Test
	public void testThatGetDisplayItemBeansFromTestFile1ReturnsCorrectSize() throws Exception {
		List<DisplayItemBeanWrapper> wrappers = holder1.service.lookupValidationErrors(holder1.validatorHelper,
				holder1.container, holder1.ub, new HashMap<String, String>(), new HashMap<String, String>(),
				holder1.permittedEventCRFIds);
		int countFilterAutoAdd = filterAutoAddedCount(wrappers, 0);
		TestCase.assertEquals(countFilterAutoAdd, 6);
	}

	@Test
	public void testThatGetDisplayItemBeansFromTestFile2ReturnsCorrectSize() throws Exception {
		List<DisplayItemBeanWrapper> wrappers = holder2.service.lookupValidationErrors(holder2.validatorHelper,
				holder2.container, holder2.ub, new HashMap<String, String>(), new HashMap<String, String>(),
				holder2.permittedEventCRFIds);
		int countFilterAutoAdd = filterAutoAddedCount(wrappers, 0);
		countFilterAutoAdd += filterAutoAddedCount(wrappers, 1);
		TestCase.assertEquals(countFilterAutoAdd, 48);
	}

	@Test
	public void testThatGetDisplayItemBeansFromTestFile3ReturnsCorrectSize() throws Exception {
		List<DisplayItemBeanWrapper> wrappers = holder3.service.lookupValidationErrors(holder3.validatorHelper,
				holder3.container, holder3.ub, new HashMap<String, String>(), new HashMap<String, String>(),
				holder3.permittedEventCRFIds);
		int countFilterAutoAdd = filterAutoAddedCount(wrappers, 0);
		TestCase.assertEquals(countFilterAutoAdd, 29);
	}
}
