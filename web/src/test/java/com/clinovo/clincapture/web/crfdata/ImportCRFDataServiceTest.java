package com.clinovo.clincapture.web.crfdata;

import com.clinovo.util.ValidatorHelper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXSource;
import org.akaza.openclinica.AbstractContextSentiveTest;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.submit.DisplayItemBeanWrapper;
import org.akaza.openclinica.bean.submit.crfdata.ODMContainer;
import org.akaza.openclinica.bean.submit.crfdata.SummaryStatsBean;
import org.akaza.openclinica.dao.hibernate.ConfigurationDao;
import org.akaza.openclinica.web.crfdata.ImportCRFDataService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.xml.sax.InputSource;

public class ImportCRFDataServiceTest extends AbstractContextSentiveTest {

	protected class ObjectsHolder {
		protected ImportCRFDataService service;
		protected ODMContainer container;
		protected InputStream stream;
		protected MockHttpServletRequest request = new MockHttpServletRequest();
		protected UserAccountBean ub;
		protected ArrayList<Integer> permittedEventCRFIds = new ArrayList<Integer>();
		protected ValidatorHelper validatorHelper;
		protected ConfigurationDao configurationDao;
		{
			configurationDao = Mockito.mock(ConfigurationDao.class);

			ub = new UserAccountBean();
			ub.setId(1);

			permittedEventCRFIds.add(1);
			permittedEventCRFIds.add(2);
			permittedEventCRFIds.add(3);
			permittedEventCRFIds.add(11);
			permittedEventCRFIds.add(12);
			permittedEventCRFIds.add(13);

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
		objectsHolder.service = new ImportCRFDataService(getDataSource(), locale);
		objectsHolder.container = new ODMContainer();
		objectsHolder.stream = this.getClass().getClassLoader().getResourceAsStream("com/clinovo/" + fileName);
		JAXBContext jaxbContext = JAXBContext.newInstance(ODMContainer.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		if (objectsHolder.stream != null) {
			InputSource inputSource = new InputSource(objectsHolder.stream);
			SAXSource saxSource = new SAXSource(inputSource);
			try {
				objectsHolder.container = (ODMContainer) jaxbUnmarshaller.unmarshal(saxSource);
			} catch (Exception e) {
				fail("Unmarshaller exception: " + e.getMessage());
			}
		} else {
			fail("XML not found!");
		}
	}

	@Test
	public void testValidateStudyMetadata() {
		ArrayList<String> errorMessages = new ArrayList<String>();
		int currentStudyId = 1;
		errorMessages = (ArrayList<String>) holder.service.validateStudyMetadata(holder.container, currentStudyId);
		assertNotNull(errorMessages);
	}

	@Test
	public void testValidateStudyMetadataWithData() {
		ArrayList<String> errorMessages = new ArrayList<String>();
		int currentStudyId = 1;
		errorMessages = (ArrayList<String>) holder.service.validateStudyMetadata(holder.container, currentStudyId);
		assertEquals(errorMessages.size(), 1);
	}

	@Test
	public void testGenerateSummaryStats() {
		List<DisplayItemBeanWrapper> wrappers = new ArrayList<DisplayItemBeanWrapper>();
		SummaryStatsBean stats = holder.service.generateSummaryStatsBean(holder.container, wrappers);
		assertNotNull(stats);
	}

	@Test
	public void testStatsGetEventCrfCount() {
		List<DisplayItemBeanWrapper> wrappers = new ArrayList<DisplayItemBeanWrapper>();
		SummaryStatsBean stats = holder.service.generateSummaryStatsBean(holder.container, wrappers);
		assertEquals(1, stats.getEventCrfCount());
	}

	@Test
	public void testStatsGetDiscNoteCount() {
		List<DisplayItemBeanWrapper> wrappers = new ArrayList<DisplayItemBeanWrapper>();
		SummaryStatsBean stats = holder.service.generateSummaryStatsBean(holder.container, wrappers);
		assertEquals(0, stats.getDiscNoteCount());
	}

	@Test
	public void testStatsGetStudySubjectCount() {
		List<DisplayItemBeanWrapper> wrappers = new ArrayList<DisplayItemBeanWrapper>();
		SummaryStatsBean stats = holder.service.generateSummaryStatsBean(holder.container, wrappers);
		assertEquals(1, stats.getStudySubjectCount());
	}

	@Test
	public void testSubjectDataSizeForFileImport1() throws Exception {
		assertEquals(holder1.container.getCrfDataPostImportContainer().getSubjectData().size(), 1);
	}

	@Test
	public void testStudyEventDataSizeForFileImport1() throws Exception {
		assertEquals(holder1.container.getCrfDataPostImportContainer().getSubjectData().get(0).getStudyEventData()
				.size(), 1);
	}

	@Test
	public void testFormDataSizeForFileImport1() throws Exception {
		assertEquals(
				holder1.container.getCrfDataPostImportContainer().getSubjectData().get(0).getStudyEventData().get(0)
						.getFormData().size(), 2);
	}

	@Test
	public void testItemGroupDataSizeForFileImport11() throws Exception {
		assertEquals(
				holder1.container.getCrfDataPostImportContainer().getSubjectData().get(0).getStudyEventData().get(0)
						.getFormData().get(0).getItemGroupData().size(), 1);
	}

	@Test
	public void testItemGroupDataSizeForFileImport12() throws Exception {
		assertEquals(
				holder1.container.getCrfDataPostImportContainer().getSubjectData().get(0).getStudyEventData().get(0)
						.getFormData().get(1).getItemGroupData().size(), 1);
	}

	@Test
	public void testSubjectDataSizeForFileImport2() throws Exception {
		assertEquals(holder2.container.getCrfDataPostImportContainer().getSubjectData().size(), 1);
	}

	@Test
	public void testStudyEventDataSizeForFileImport2() throws Exception {
		assertEquals(holder2.container.getCrfDataPostImportContainer().getSubjectData().get(0).getStudyEventData()
				.size(), 2);
	}

	@Test
	public void testFormDataSizeForFileImport21() throws Exception {
		assertEquals(
				holder2.container.getCrfDataPostImportContainer().getSubjectData().get(0).getStudyEventData().get(0)
						.getFormData().size(), 1);
	}

	@Test
	public void testFormDataSizeForFileImport22() throws Exception {
		assertEquals(
				holder2.container.getCrfDataPostImportContainer().getSubjectData().get(0).getStudyEventData().get(1)
						.getFormData().size(), 1);
	}

	@Test
	public void testItemGroupDataSizeForFileImport21() throws Exception {
		assertEquals(
				holder2.container.getCrfDataPostImportContainer().getSubjectData().get(0).getStudyEventData().get(0)
						.getFormData().get(0).getItemGroupData().size(), 5);
	}

	@Test
	public void testItemGroupDataSizeForFileImport22() throws Exception {
		assertEquals(
				holder2.container.getCrfDataPostImportContainer().getSubjectData().get(0).getStudyEventData().get(1)
						.getFormData().get(0).getItemGroupData().size(), 5);
	}

	@Test
	public void testSubjectDataSizeForFileImport3() throws Exception {
		assertEquals(holder3.container.getCrfDataPostImportContainer().getSubjectData().size(), 1);
	}

	@Test
	public void testStudyEventDataSizeForFileImport3() throws Exception {
		assertEquals(holder3.container.getCrfDataPostImportContainer().getSubjectData().get(0).getStudyEventData()
				.size(), 1);
	}

	@Test
	public void testFormDataSizeForFileImport3() throws Exception {
		assertEquals(
				holder3.container.getCrfDataPostImportContainer().getSubjectData().get(0).getStudyEventData().get(0)
						.getFormData().size(), 1);
	}

	@Test
	public void testItemGroupDataSizeForFileImport3() throws Exception {
		assertEquals(
				holder3.container.getCrfDataPostImportContainer().getSubjectData().get(0).getStudyEventData().get(0)
						.getFormData().get(0).getItemGroupData().size(), 5);
	}

	@Test
	public void testLookupValidationErrorsForFileImport1() throws Exception {
		List<DisplayItemBeanWrapper> wrappers = holder1.service.lookupValidationErrors(holder1.validatorHelper,
				holder1.container, holder1.ub, new HashMap<String, String>(), new HashMap<String, String>(),
				holder1.permittedEventCRFIds);
		assertEquals(wrappers.get(0).getDisplayItemBeans().size(), 6);
	}

	@Test
	public void testLookupValidationErrorsForFileImport2() throws Exception {
		List<DisplayItemBeanWrapper> wrappers = holder2.service.lookupValidationErrors(holder2.validatorHelper,
				holder2.container, holder2.ub, new HashMap<String, String>(), new HashMap<String, String>(),
				holder2.permittedEventCRFIds);
		assertEquals(wrappers.get(0).getDisplayItemBeans().size() + wrappers.get(1).getDisplayItemBeans().size(), 48);
	}

	@Test
	public void testLookupValidationErrorsForFileImport3() throws Exception {
		List<DisplayItemBeanWrapper> wrappers = holder3.service.lookupValidationErrors(holder3.validatorHelper,
				holder3.container, holder3.ub, new HashMap<String, String>(), new HashMap<String, String>(),
				holder3.permittedEventCRFIds);
		assertEquals(wrappers.get(0).getDisplayItemBeans().size(), 24);
	}
}
