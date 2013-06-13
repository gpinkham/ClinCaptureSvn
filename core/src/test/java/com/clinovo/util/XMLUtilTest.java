package com.clinovo.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.hamcrest.core.IsNot;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.clinovo.BaseTest;
import com.clinovo.model.RandomizationResult;

public class XMLUtilTest extends BaseTest {

	@Test
	public void testThatXMLToStringDoesNotReturnNullOnValidInput() throws Exception {

		assertNotNull("Should never return null", XMLUtil.docToString(xmlRandomiationResult));
	}

	@Test
	public void testThatXMLToStringReturnsValidXMLString() throws Exception {

		Document doc = parseDocument();

		assertNotNull("The object should be converted to a valid XML document", doc);
	}

	@Test
	public void testThatThatXMLReturnsDocumentWithCorrectRootElement() throws Exception {

		Document doc = parseDocument();

		assertEquals("The root element should be results", "Result", doc.getFirstChild().getNodeName());
	}

	@Test
	public void testThatCreateDocumentDoesNotReturnNull() throws Exception {

		InputStream source = createInputStream();

		assertNotNull("Should never return null", XMLUtil.createDocument(source));
	}

	@Test
	public void testThatCreateDocumentReturnsDocumentWithCorrectRoot() throws Exception {

		Document document = XMLUtil.createDocument(createInputStream());

		assertEquals("Should have expected root", "Result", document.getFirstChild().getNodeName());
	}

	@Test
	public void testThatExtractNodeFromDocumentDoesNotReturnDoesNotReturnNull() throws Exception {

		Document document = XMLUtil.createDocument(createInputStream());

		assertNotNull("Should never return null", XMLUtil.extractNodeFromDocument("patient", document));
	}

	@Test
	public void testThatExtractNodeFormDocumentReturnsCorrectNode() throws Exception {

		Document document = XMLUtil.createDocument(createInputStream());

		Node node = XMLUtil.extractNodeFromDocument("treatment", document);
		assertEquals("Should extract the correct Node", "treatment", node.getNodeName());
	}

	@Test
	public void testThatExtractNodeReturnsNodeWithValue() throws Exception {

		Document document = XMLUtil.createDocument(createInputStream());
		Node node = XMLUtil.extractNodeFromDocument("treatment", document);

		assertEquals("Should extract the correct Node content", "2", node.getTextContent());
	}

	@Test
	public void testThatCreateWebServiceResultDoesNotReturn() throws Exception {

		RandomizationResult result = XMLUtil.createWebServiceResult(XMLUtil.docToString(xmlRandomiationResult));

		assertNotNull("Should never return null", result);
	}

	@Test
	public void testThatCreateWebServiceResultReturnsValidResultWithValidTreatment() throws Exception {

		RandomizationResult result = XMLUtil.createWebServiceResult(XMLUtil.docToString(xmlRandomiationResult));

		assertThat("Treatment should not be null", result.getTreatment(), IsNot.not(""));

	}

	@Test
	public void testThatCreateWebServiceResultReturnsValidResultWithCorrectTreatment() throws Exception {

		RandomizationResult result = XMLUtil.createWebServiceResult(XMLUtil.docToString(xmlRandomiationResult));

		assertThat("Treatment should set correctly", result.getTreatment(), is("2"));
	}
	
	@Test
	public void testThatCreateWebServiceResultReturnsValidResultWithValidRandomizationResult() throws Exception {

		RandomizationResult result = XMLUtil.createWebServiceResult(XMLUtil.docToString(xmlRandomiationResult));

		assertThat("Randomization result should not be null", result.getRandomizationResult(), IsNot.not(""));

	}

	@Test
	public void testThatCreateWebServiceResultReturnsValidResultWithCorrectRandomResult() throws Exception {

		RandomizationResult result = XMLUtil.createWebServiceResult(XMLUtil.docToString(xmlRandomiationResult));

		assertThat("Randomization result should set correctly", result.getRandomizationResult(), is("radiotherapy"));
	}
	
	@Test
	public void testThatCreateWebServiceResultReturnsValidResultWithValidPatientId() throws Exception {

		RandomizationResult result = XMLUtil.createWebServiceResult(XMLUtil.docToString(xmlRandomiationResult));

		assertThat("Patient Id should be set correctly", result.getPatientId(), IsNot.not(""));

	}
	
	@Test
	public void testThatCreateWebServiceResultReturnsValidResultWithCorrectPatientId() throws Exception {

		RandomizationResult result = XMLUtil.createWebServiceResult(XMLUtil.docToString(xmlRandomiationResult));

		assertThat("Patient Id should not be null", result.getPatientId(), is("subject2"));
	}
	
	private Document parseDocument() throws Exception {

		String xmlToString = XMLUtil.docToString(xmlRandomiationResult);

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		Document doc = dBuilder.parse(new InputSource(new ByteArrayInputStream(xmlToString.getBytes("utf-8"))));
		return doc;
	}

	private InputStream createInputStream() throws Exception {

		String xmlToString = XMLUtil.docToString(xmlRandomiationResult);
		InputSource source = new InputSource(new ByteArrayInputStream(xmlToString.getBytes("utf-8")));
		return source.getByteStream();
	}

}
