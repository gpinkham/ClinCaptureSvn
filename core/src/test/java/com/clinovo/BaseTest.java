package com.clinovo;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.BeforeClass;
import org.w3c.dom.Document;

import com.clinovo.rule.WebServiceAction;

public class BaseTest {

	protected static Document webServiceReturnValue;
	protected static Document webServiceActionDefinition;

	@BeforeClass
	public static void initialize() throws Exception {
		
		webServiceActionDefinition = readTestXMLFile("src/test/resources/WSActionRule.xml");
		webServiceReturnValue = readTestXMLFile("src/test/resources/WebServiceReturnValue.xml");
	}

	private static Document readTestXMLFile(String fileName) throws Exception {
		
		File fXmlFile = new File(fileName);
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		
		return dBuilder.parse(fXmlFile);
	}
	
	protected WebServiceAction createWebServiceAction() {
		
		WebServiceAction action = new WebServiceAction();
		action.setUrl(webServiceActionDefinition.getElementsByTagName("url").item(0).getTextContent());
		action.setUsername(webServiceActionDefinition.getElementsByTagName("username").item(0).getTextContent());
		action.setPassword(webServiceActionDefinition.getElementsByTagName("password").item(0).getTextContent());
		action.setRolename(webServiceActionDefinition.getElementsByTagName("rolename").item(0).getTextContent());
		action.setStudyOID(webServiceActionDefinition.getElementsByTagName("studyoid").item(0).getTextContent());
		action.setStudySubjectOID(webServiceActionDefinition.getElementsByTagName("studysubjectoid").item(0).getTextContent());
		
		return action;
	}
}