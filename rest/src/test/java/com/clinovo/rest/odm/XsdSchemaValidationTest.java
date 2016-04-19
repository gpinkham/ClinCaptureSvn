package com.clinovo.rest.odm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.StringWriter;
import java.lang.reflect.Method;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.cdisc.ns.odm.v130.ODM;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;

import com.clinovo.enums.study.StudyAllocation;
import com.clinovo.enums.study.StudyAssignment;
import com.clinovo.enums.study.StudyControl;
import com.clinovo.enums.study.StudyDuration;
import com.clinovo.enums.study.StudyEndPoint;
import com.clinovo.enums.study.StudyMasking;
import com.clinovo.enums.study.StudyOrigin;
import com.clinovo.enums.study.StudyPhase;
import com.clinovo.enums.study.StudyProtocolType;
import com.clinovo.enums.study.StudyPurpose;
import com.clinovo.enums.study.StudySelection;
import com.clinovo.enums.study.StudyTiming;
import com.clinovo.rest.model.RestData;
import com.clinovo.rest.serializer.OdmXmlSerializer;

public class XsdSchemaValidationTest {

	private Schema schema;

	@Before
	public void before() throws Exception {
		schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new FileSystemResourceLoader()
				.getResource("classpath:properties/ClinCapture_Rest_ODM1-3-0.xsd").getURL());
	}

	private RestOdmContainer unmarshal(Resource resource) throws Exception {
		JAXBContext jaxbContext = JAXBContext.newInstance(ODM.class, RestOdmContainer.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		jaxbUnmarshaller.setSchema(schema);
		return (RestOdmContainer) jaxbUnmarshaller.unmarshal(resource.getFile());
	}

	private String marshal(Object o) throws Exception {
		RestOdmContainer restOdmContainer = new RestOdmContainer();
		restOdmContainer.setRestData(new RestData());
		Method method = OdmXmlSerializer.getSetterMethod(RestData.class.getMethods(), o);
		method.invoke(restOdmContainer.getRestData(), o);
		restOdmContainer.collectOdmRoot();
		Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
				.newSchema(new FileSystemResourceLoader()
						.getResource("classpath:properties/ClinCapture_Rest_ODM1-3-0.xsd").getURL());
		StringWriter writer = new StringWriter();
		JAXBContext context = JAXBContext.newInstance(RestOdmContainer.class);
		javax.xml.bind.Marshaller jaxbMarshaller = context.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
				"http://www.cdisc.org/ns/odm/v1.3 ClinCapture_Rest_ODM1-3-0.xsd");
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		jaxbMarshaller.setSchema(schema);
		jaxbMarshaller.marshal(restOdmContainer, writer);
		return writer.toString();
	}

	@Test
	public void testThatSchemaPassesValidationDuringUnmarshallingTheEventDefinitionCrfBean() throws Exception {
		RestOdmContainer restOdmContainer = unmarshal(
				new FileSystemResourceLoader().getResource("classpath:xml/eventdefinitioncrf.xml"));
		assertNotNull(restOdmContainer.getRestData().getEventDefinitionCRFBean());
		assertEquals(restOdmContainer.getRestData().getEventDefinitionCRFBean().getEventName(), "TEST EDC");
		marshal(restOdmContainer.getRestData().getEventDefinitionCRFBean());
	}

	@Test
	public void testThatSchemaPassesValidationDuringUnmarshallingTheStudyEventDefinitionBean() throws Exception {
		RestOdmContainer restOdmContainer = unmarshal(
				new FileSystemResourceLoader().getResource("classpath:xml/studyeventdefinition1.xml"));
		assertNotNull(restOdmContainer.getRestData().getStudyEventDefinitionBean());
		assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getName(), "test visit");
		marshal(restOdmContainer.getRestData().getStudyEventDefinitionBean());
	}

	@Test
	public void testThatSchemaPassesValidationDuringUnmarshallingTheStudyEventDefinitionBeans() throws Exception {
		RestOdmContainer restOdmContainer = unmarshal(
				new FileSystemResourceLoader().getResource("classpath:xml/studyeventdefinition3.xml"));
		assertNull(restOdmContainer.getRestData().getStudyEventDefinitionBean());
		assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionList().size(), 2);
		marshal(restOdmContainer.getRestData().getStudyEventDefinitionList());
	}

	@Test
	public void testThatSchemaPassesValidationDuringUnmarshallingTheStudyEventDefinitionBeanWithEventDefinitionCrfBeans()
			throws Exception {
		RestOdmContainer restOdmContainer = unmarshal(
				new FileSystemResourceLoader().getResource("classpath:xml/studyeventdefinition2.xml"));
		assertNotNull(restOdmContainer.getRestData().getStudyEventDefinitionBean());
		assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getName(), "test visit");
		assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getEventDefinitionCrfs().size(), 2);
		marshal(restOdmContainer.getRestData().getStudyEventDefinitionBean());
	}

	@Test
	public void testThatSchemaPassesValidationDuringUnmarshallingTheUserAccountBean() throws Exception {
		RestOdmContainer restOdmContainer = unmarshal(
				new FileSystemResourceLoader().getResource("classpath:xml/user.xml"));
		assertNotNull(restOdmContainer.getRestData().getUserAccountBean());
		assertEquals(restOdmContainer.getRestData().getUserAccountBean().getName(), "user1");
		marshal(restOdmContainer.getRestData().getUserAccountBean());
	}

	@Test
	public void testThatSchemaPassesValidationDuringUnmarshallingTheUserDetails() throws Exception {
		RestOdmContainer restOdmContainer = unmarshal(
				new FileSystemResourceLoader().getResource("classpath:xml/userdetails.xml"));
		assertNotNull(restOdmContainer.getRestData().getUserDetails());
		assertEquals(restOdmContainer.getRestData().getUserDetails().getUserName(), "root");
		marshal(restOdmContainer.getRestData().getUserDetails());
	}

	@Test
	public void testThatSchemaPassesValidationDuringUnmarshallingTheError() throws Exception {
		RestOdmContainer restOdmContainer = unmarshal(
				new FileSystemResourceLoader().getResource("classpath:xml/error.xml"));
		assertNotNull(restOdmContainer.getRestData().getError());
		assertEquals(restOdmContainer.getRestData().getError().getStatus(), "401");
		marshal(restOdmContainer.getRestData().getError());
	}

	@Test
	public void testThatSchemaPassesValidationDuringUnmarshallingTheResponse() throws Exception {
		RestOdmContainer restOdmContainer = unmarshal(
				new FileSystemResourceLoader().getResource("classpath:xml/response.xml"));
		assertNotNull(restOdmContainer.getRestData().getResponse());
		assertEquals(restOdmContainer.getRestData().getResponse().getStatus(), "401");
		marshal(restOdmContainer.getRestData().getResponse());
	}

	@Test
	public void testThatSchemaPassesValidationDuringUnmarshallingTheCrfVersionBean() throws Exception {
		RestOdmContainer restOdmContainer = unmarshal(
				new FileSystemResourceLoader().getResource("classpath:xml/crfversion.xml"));
		assertNotNull(restOdmContainer.getRestData().getCrfVersionBean());
		assertEquals(restOdmContainer.getRestData().getCrfVersionBean().getCrfName(), "Test CRF");
		marshal(restOdmContainer.getRestData().getCrfVersionBean());
	}

	@Test
	public void testThatSchemaPassesValidationDuringUnmarshallingTheStudyBean() throws Exception {
		RestOdmContainer restOdmContainer = unmarshal(
				new FileSystemResourceLoader().getResource("classpath:xml/study.xml"));
		assertNotNull(restOdmContainer.getRestData().getStudyBean());
		assertEquals(restOdmContainer.getRestData().getStudyBean().getId(), 1);
		assertEquals(restOdmContainer.getRestData().getStudyBean().getName(), "TEST1");
		assertEquals(restOdmContainer.getRestData().getStudyBean().getBriefTitle(), "Test Brief Title");
		assertEquals(restOdmContainer.getRestData().getStudyBean().getOrigin(), StudyOrigin.GUI.getName());
		assertEquals(restOdmContainer.getRestData().getStudyBean().getOid(), "OC_TEST1");
		assertEquals(restOdmContainer.getRestData().getStudyBean().getIdentifier(), "UN_TEST1");
		assertEquals(restOdmContainer.getRestData().getStudyBean().getProtocolTypeKey(),
				StudyProtocolType.INTERVENTIONAL.getValue());
		assertEquals(restOdmContainer.getRestData().getStudyBean().getOfficialTitle(), "");
		assertEquals(restOdmContainer.getRestData().getStudyBean().getSecondaryIdentifier(), "");
		assertEquals(restOdmContainer.getRestData().getStudyBean().getSummary(), "test!");
		assertEquals(restOdmContainer.getRestData().getStudyBean().getProtocolDescription(), "");
		assertEquals(restOdmContainer.getRestData().getStudyBean().getPrincipalInvestigator(), "test");
		assertEquals(restOdmContainer.getRestData().getStudyBean().getSponsor(), "");
		assertEquals(restOdmContainer.getRestData().getStudyBean().getCollaborators(), "");
		assertEquals(restOdmContainer.getRestData().getStudyBean().getStatusCode(), "avaliable");
		assertNotNull(restOdmContainer.getRestData().getStudyBean().getStudyFeatureConfig());
		assertEquals(restOdmContainer.getRestData().getStudyBean().getStudyFeatureConfig().getCrfAnnotation(), "yes");
		assertEquals(restOdmContainer.getRestData().getStudyBean().getPhaseKey(), StudyPhase.PHASE2_3.getValue());
		assertEquals(restOdmContainer.getRestData().getStudyBean().getExpectedTotalEnrollment(), 11);
		assertNotNull(restOdmContainer.getRestData().getStudyBean().getDatePlannedStart());
		assertNotNull(restOdmContainer.getRestData().getStudyBean().getDatePlannedEnd());
		assertNotNull(restOdmContainer.getRestData().getStudyBean().getProtocolDateVerification());
		assertEquals(restOdmContainer.getRestData().getStudyBean().getPurposeKey(), StudyPurpose.TREATMENT.getValue());
		assertEquals(restOdmContainer.getRestData().getStudyBean().getAllocationKey(),
				StudyAllocation.NON_RANDOMIZED.getValue());
		assertEquals(restOdmContainer.getRestData().getStudyBean().getMaskingKey(),
				StudyMasking.DOUBLE_BLIND.getValue());
		assertEquals(restOdmContainer.getRestData().getStudyBean().getControlKey(),
				StudyControl.DOSE_COMPARISON.getValue());
		assertEquals(restOdmContainer.getRestData().getStudyBean().getAssignmentKey(),
				StudyAssignment.EXPANDED_ACCESS.getValue());
		assertEquals(restOdmContainer.getRestData().getStudyBean().getEndpointKey(),
				StudyEndPoint.PHARMACODYNAMICS.getValue());
		assertEquals(restOdmContainer.getRestData().getStudyBean().getDurationKey(),
				StudyDuration.CROSS_SECTIONAL.getValue());
		assertEquals(restOdmContainer.getRestData().getStudyBean().getSelectionKey(),
				StudySelection.DEFINED_POPULATION.getValue());
		assertEquals(restOdmContainer.getRestData().getStudyBean().getTimingKey(),
				StudyTiming.RETROSPECTIVE.getValue());
		assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityName(), "NPMedic");
		assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityCity(), "Austin");
		assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityState(), "TX");
		assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityZip(), "54567");
		assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityCountry(), "USA");
		assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityContactName(), "Dr. Tony Kane");
		assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityContactDegree(), "MD");
		assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityContactPhone(), "(843) 678-2390");
		assertEquals(restOdmContainer.getRestData().getStudyBean().getFacilityContactEmail(), "tony.kane@npmedic.com");
		marshal(restOdmContainer.getRestData().getStudyBean());
		assertNotNull(restOdmContainer.getRestData().getStudyBean().getStudyParameterConfig());
		assertEquals(
				restOdmContainer.getRestData().getStudyBean().getStudyParameterConfig().getAdminForcedReasonForChange(),
				"true");
	}
}
