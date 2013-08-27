package com.clinovo.clincapture.web.crfdata;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXSource;

import org.akaza.openclinica.bean.submit.DisplayItemBeanWrapper;
import org.akaza.openclinica.bean.submit.crfdata.ODMContainer;
import org.akaza.openclinica.bean.submit.crfdata.SummaryStatsBean;
import org.akaza.openclinica.web.crfdata.ImportCRFDataService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;

public class ImportCRFDataServiceTest {
	
	protected ImportCRFDataService service;
	protected ODMContainer container;
	protected InputStream stream;

	@Before
	public void setUp() throws Exception {
		Locale locale = new Locale("EN");
		service = new ImportCRFDataService(null, locale);
		container = new ODMContainer();
		ClassLoader loader = Thread.currentThread().getContextClassLoader(); 
		stream = loader.getResourceAsStream("DataImportServletXmlTest.xml");
		JAXBContext jaxbContext = JAXBContext.newInstance(ODMContainer.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		if (stream != null) {
			InputSource inputSource = new InputSource(stream);
			SAXSource saxSource = new SAXSource(inputSource);
			try {
				container = (ODMContainer) jaxbUnmarshaller.unmarshal(saxSource);
			} catch (Exception e) {
				fail("Unmarshaller exception: " +e.getMessage());
			}
		} else {
			fail("XML not found!");
		}
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testValidateStudyMetadata() {
		ArrayList<String> errorMessages = new ArrayList<String>();
		int currentStudyId = 1;
		errorMessages = (ArrayList<String>) 
				service.validateStudyMetadata(container, currentStudyId);
		assertNotNull(errorMessages);
	}
	
	@Test
	public void testValidateStudyMetadataWithData() {
		ArrayList<String> errorMessages = new ArrayList<String>();
		int currentStudyId = 1;
		errorMessages = (ArrayList<String>) 
				service.validateStudyMetadata(container, currentStudyId);
		assertEquals(errorMessages.size(), 0);
	}
	
	@Test
	public void testGenerateSummaryStats() {
		List<DisplayItemBeanWrapper> wrappers = new ArrayList<DisplayItemBeanWrapper>();
		SummaryStatsBean stats = service.generateSummaryStatsBean(container, wrappers);
		assertNotNull(stats);	
	}
	
	@Test
	public void testStatsGetEventCrfCount() {
		List<DisplayItemBeanWrapper> wrappers = new ArrayList<DisplayItemBeanWrapper>();
		SummaryStatsBean stats = service.generateSummaryStatsBean(container, wrappers);
		assertEquals(1, stats.getEventCrfCount());
	}
	
	@Test
	public void testStatsGetDiscNoteCount() {
		List<DisplayItemBeanWrapper> wrappers = new ArrayList<DisplayItemBeanWrapper>();
		SummaryStatsBean stats = service.generateSummaryStatsBean(container, wrappers);
		assertEquals(0, stats.getDiscNoteCount());
	}
	
	@Test
	public void testStatsGetStudySubjectCount() {
		List<DisplayItemBeanWrapper> wrappers = new ArrayList<DisplayItemBeanWrapper>();
		SummaryStatsBean stats = service.generateSummaryStatsBean(container, wrappers);
		assertEquals(1, stats.getStudySubjectCount());
	}

}
