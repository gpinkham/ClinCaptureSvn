package com.clinovo.rest.odm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.cdisc.ns.odm.v130.ODM;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.FileSystemResourceLoader;

public class XsdSchemaValidationTest {

	private Schema schema;

	@Before
	public void before() throws Exception {
		schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(
				new FileSystemResourceLoader().getResource("classpath:properties/ClinCapture_Rest_ODM1-3-0.xsd")
						.getURL());
	}

	@Test
	public void testThatSchemaPassesValidationDuringUnmarshallingTheStudyEventDefinitionBean() throws Exception {
		JAXBContext jaxbContext = JAXBContext.newInstance(ODM.class, RestOdmContainer.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		jaxbUnmarshaller.setSchema(schema);
		RestOdmContainer restOdmContainer = (RestOdmContainer) jaxbUnmarshaller
				.unmarshal(new FileSystemResourceLoader().getResource("classpath:xml/studyeventdefinition.xml")
						.getFile());
		assertNotNull(restOdmContainer.getRestData().getStudyEventDefinitionBean());
		assertEquals(restOdmContainer.getRestData().getStudyEventDefinitionBean().getName(), "test visit");
	}

	@Test
	public void testThatSchemaPassesValidationDuringUnmarshallingTheUserAccountBean() throws Exception {
		JAXBContext jaxbContext = JAXBContext.newInstance(ODM.class, RestOdmContainer.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		jaxbUnmarshaller.setSchema(schema);
		RestOdmContainer restOdmContainer = (RestOdmContainer) jaxbUnmarshaller
				.unmarshal(new FileSystemResourceLoader().getResource("classpath:xml/user.xml").getFile());
		assertNotNull(restOdmContainer.getRestData().getUserAccountBean());
		assertEquals(restOdmContainer.getRestData().getUserAccountBean().getName(), "user1");
	}

	@Test
	public void testThatSchemaPassesValidationDuringUnmarshallingTheUserDetails() throws Exception {
		JAXBContext jaxbContext = JAXBContext.newInstance(ODM.class, RestOdmContainer.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		jaxbUnmarshaller.setSchema(schema);
		RestOdmContainer restOdmContainer = (RestOdmContainer) jaxbUnmarshaller
				.unmarshal(new FileSystemResourceLoader().getResource("classpath:xml/userdetails.xml").getFile());
		assertNotNull(restOdmContainer.getRestData().getUserDetails());
		assertEquals(restOdmContainer.getRestData().getUserDetails().getUserName(), "root");
	}

	@Test
	public void testThatSchemaPassesValidationDuringUnmarshallingTheError() throws Exception {
		JAXBContext jaxbContext = JAXBContext.newInstance(ODM.class, RestOdmContainer.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		jaxbUnmarshaller.setSchema(schema);
		RestOdmContainer restOdmContainer = (RestOdmContainer) jaxbUnmarshaller
				.unmarshal(new FileSystemResourceLoader().getResource("classpath:xml/error.xml").getFile());
		assertNotNull(restOdmContainer.getRestData().getError());
		assertEquals(restOdmContainer.getRestData().getError().getCode(), 401);
	}
}
