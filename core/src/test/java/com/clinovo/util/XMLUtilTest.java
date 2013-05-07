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
import com.clinovo.model.WebServiceResult;

public class XMLUtilTest extends BaseTest {

	@Test
	public void testThatXMLToStringDoesNotReturnNullOnValidInput() throws Exception {

		assertNotNull("Should never return null", XMLUtil.docToString(webServiceReturnValue));
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

		assertNotNull("Should never return null", XMLUtil.extractNodeFromDocument("initials", document));
	}

	@Test
	public void testThatExtractNodeFormDocumentReturnsCorrectNode() throws Exception {

		Document document = XMLUtil.createDocument(createInputStream());

		Node node = XMLUtil.extractNodeFromDocument("initials", document);
		assertEquals("Should extract the correct Node", "initials", node.getNodeName());
	}

	@Test
	public void testThatExtractNodeReturnsNodeWithValue() throws Exception {

		Document document = XMLUtil.createDocument(createInputStream());
		Node node = XMLUtil.extractNodeFromDocument("initials", document);

		assertEquals("Should extract the correct Node content", "NAS", node.getTextContent());
	}

	@Test
	public void testThatCreateWebServiceResultDoesNotReturn() throws Exception {

		WebServiceResult result = XMLUtil.createWebServiceResult(XMLUtil.docToString(webServiceReturnValue));

		assertNotNull("Should never return null", result);
	}

	@Test
	public void testThatCreateWebServiceResultReturnsValidResultWithValidTrialId() throws Exception {

		WebServiceResult result = XMLUtil.createWebServiceResult(XMLUtil.docToString(webServiceReturnValue));

		assertThat("Trial Id should not be null", result.getTrialId(), IsNot.not(""));

	}

	@Test
	public void testThatCreateWebServiceResultReturnsValidResultWithCorrectTrialId() throws Exception {

		WebServiceResult result = XMLUtil.createWebServiceResult(XMLUtil.docToString(webServiceReturnValue));

		assertThat("Trial Id should set correctly", result.getTrialId(), is("some-trial-id"));
	}
	
	@Test
	public void testThatCreateWebServiceResultReturnsValidResultWithValidPatientId() throws Exception {

		WebServiceResult result = XMLUtil.createWebServiceResult(XMLUtil.docToString(webServiceReturnValue));

		assertThat("Patient Id should be set correctly", result.getPatientId(), IsNot.not(""));

	}
	
	@Test
	public void testThatCreateWebServiceResultReturnsValidResultWithCorrectPatientId() throws Exception {

		WebServiceResult result = XMLUtil.createWebServiceResult(XMLUtil.docToString(webServiceReturnValue));

		assertThat("Patient Id should not be null", result.getPatientId(), is("some-patient-id"));
	}

	@Test
	public void testThatCreateWebServiceResultReturnsValidResultWithCorrectInitials() throws Exception {

		WebServiceResult result = XMLUtil.createWebServiceResult(XMLUtil.docToString(webServiceReturnValue));

		assertThat("Intials should not be null or empty", result.getInitials(), is("NAS"));
	}
	
	@Test
	public void testThatCreateWebServiceResultReturnsValidResultWithValidInitials() throws Exception {

		WebServiceResult result = XMLUtil.createWebServiceResult(XMLUtil.docToString(webServiceReturnValue));

		assertThat("Initials should be set correctly", result.getInitials(), IsNot.not(""));

	}
	
	private Document parseDocument() throws Exception {

		String xmlToString = XMLUtil.docToString(webServiceReturnValue);

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		Document doc = dBuilder.parse(new InputSource(new ByteArrayInputStream(xmlToString.getBytes("utf-8"))));
		return doc;
	}

	private InputStream createInputStream() throws Exception {

		String xmlToString = XMLUtil.docToString(webServiceReturnValue);
		InputSource source = new InputSource(new ByteArrayInputStream(xmlToString.getBytes("utf-8")));
		return source.getByteStream();
	}

}
