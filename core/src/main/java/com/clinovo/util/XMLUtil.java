package com.clinovo.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.clinovo.model.WebServiceResult;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class XMLUtil {

	public static WebServiceResult createWebServiceResult(String response) throws Exception {

		WebServiceResult result = new WebServiceResult();

		Document document = createDocument(response);

		Node groupNode = extractNodeFromDocument("group", document);
		Node messageNode = extractNodeFromDocument("message", document);
		Node treatmentNode = extractNodeFromDocument("treatment", document);
		Node displayTreamentNode = extractNodeFromDocument("displayTreatment", document);

		String group = groupNode != null ? groupNode.getTextContent() : "";
		String message = messageNode != null ? messageNode.getTextContent() : "";
		String treatment = treatmentNode != null ? treatmentNode.getTextContent() : "";
		String displayTreatment = displayTreamentNode != null ? displayTreamentNode.getTextContent() : "false";
		
		result.setGroup(group);
		result.setMessage(message);
		result.setTreatment(treatment);
		result.setDisplayTreatment(Boolean.valueOf(displayTreatment));

		return result;
	}

	private static DocumentBuilder createDocumentBuilder() throws Exception {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		factory.setNamespaceAware(true);
		return factory.newDocumentBuilder();
	}

	public static Document createDocument(String source) throws Exception {

		DocumentBuilder builder = createDocumentBuilder();

		return builder.parse(new ByteArrayInputStream(source.getBytes()));

	}

	public static Document createDocument(InputStream source) throws Exception {

		DocumentBuilder builder = createDocumentBuilder();

		return builder.parse(source);
	}

	public static Node extractNodeFromDocument(String tagname, Document document) {

		NodeList nodes = document.getElementsByTagName(tagname);
		return nodes.item(0);
	}

	public static String docToString(Document document) throws Exception {

		OutputFormat format = new OutputFormat(document);

		StringWriter stringOut = new StringWriter();
		XMLSerializer serial = new XMLSerializer(stringOut, format);
		serial.serialize(document);

		return stringOut.toString();
	}

}