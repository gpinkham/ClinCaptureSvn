package com.clinovo.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
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
import com.clinovo.util.XMLUtil;

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

		assertNotNull("Should never return null", XMLUtil.extractNodeFromDocument("message", document));
	}

	@Test
	public void testThatExtractNodeFormDocumentReturnsCorrectNode() throws Exception {

		Document document = XMLUtil.createDocument(createInputStream());

		Node node = XMLUtil.extractNodeFromDocument("message", document);
		assertEquals("Should extract the correct Node", "message", node.getNodeName());
	}

	@Test
	public void testThatExtractNodeReturnsNodeWithValue() throws Exception {

		Document document = XMLUtil.createDocument(createInputStream());
		Node node = XMLUtil.extractNodeFromDocument("message", document);

		assertEquals("Should extract the correct Node", "Owe me like you owe your tax", node.getTextContent());
	}

	@Test
	public void testThatCreateWebServiceResultDoesNotReturn() throws Exception {

		WebServiceResult result = XMLUtil.createWebServiceResult(XMLUtil.docToString(webServiceReturnValue));

		assertNotNull("Should never return null", result);
	}

	@Test
	public void testThatCreateWebServiceResultReturnsValidResultWithValidTreatment() throws Exception {

		WebServiceResult result = XMLUtil.createWebServiceResult(XMLUtil.docToString(webServiceReturnValue));

		assertThat("Treatment should not be null", result.getTreatment(), IsNot.not(""));

	}

	@Test
	public void testThatCreateWebServiceResultReturnsValidResultWithCorrectTreatment() throws Exception {

		WebServiceResult result = XMLUtil.createWebServiceResult(XMLUtil.docToString(webServiceReturnValue));

		assertThat("Treatment should set correctly", result.getTreatment(), is("Treatment-001"));
	}
	
	@Test
	public void testThatCreateWebServiceResultReturnsValidResultWithValidGroup() throws Exception {

		WebServiceResult result = XMLUtil.createWebServiceResult(XMLUtil.docToString(webServiceReturnValue));

		assertThat("Group should be set correctly", result.getGroup(), IsNot.not(""));

	}
	
	@Test
	public void testThatCreateWebServiceResultReturnsValidResultWithCorrectGroup() throws Exception {

		WebServiceResult result = XMLUtil.createWebServiceResult(XMLUtil.docToString(webServiceReturnValue));

		assertThat("Group should not be null", result.getGroup(), is("Test-001"));
	}

	@Test
	public void testThatCreateWebServiceResultReturnsValidResultWithCorrectMessage() throws Exception {

		WebServiceResult result = XMLUtil.createWebServiceResult(XMLUtil.docToString(webServiceReturnValue));

		assertThat("Message should not be null", result.getMessage(), is("Owe me like you owe your tax"));
	}
	
	@Test
	public void testThatCreateWebServiceResultReturnsValidResultWithValidMessage() throws Exception {

		WebServiceResult result = XMLUtil.createWebServiceResult(XMLUtil.docToString(webServiceReturnValue));

		assertThat("Message should be set correctly", result.getMessage(), IsNot.not(""));

	}
	
	@Test
	public void testThatCreateWebServiceResultReturnsValidResultWithValidTreatmentDisplayFlag() throws Exception {

		WebServiceResult result = XMLUtil.createWebServiceResult(XMLUtil.docToString(webServiceReturnValue));

		assertThat("Display treatment flag should be set correctly", result.isDisplayTreatment(), equalTo(Boolean.TRUE));
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
