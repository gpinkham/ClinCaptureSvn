/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License 
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

package org.akaza.openclinica.bean.rule;

import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

public class XmlSchemaValidationHelper {

	public void validateAgainstSchema(File xmlFile, File xsdFile) {
		try {
			// parse an XML document into a DOM tree
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			builderFactory.setNamespaceAware(true);
			DocumentBuilder parser = builderFactory.newDocumentBuilder();
			Document document = parser.parse(xmlFile);

			// create a SchemaFactory capable of understanding WXS schemas
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

			// load a WXS schema, represented by a Schema instance
			Source schemaFile = new StreamSource(xsdFile);
			Schema schema = factory.newSchema(schemaFile);

			// create a Validator instance, which can be used to validate an
			// instance document
			Validator validator = schema.newValidator();

			// validate the DOM tree
			validator.validate(new DOMSource(document));

		} catch (FileNotFoundException ex) {
			throw new OpenClinicaSystemException("File was not found", ex.getCause());
		} catch (IOException ioe) {
			throw new OpenClinicaSystemException("IO Exception", ioe.getCause());
		} catch (SAXParseException spe) {
			// spe.printStackTrace();
			throw new OpenClinicaSystemException("Line : " + spe.getLineNumber() + " - " + spe.getMessage(),
					spe.getCause());
		} catch (SAXException e) {
			throw new OpenClinicaSystemException(e.getMessage(), e.getCause());
		} catch (ParserConfigurationException pce) {
			throw new OpenClinicaSystemException(pce.getMessage(), pce.getCause());
		}
	}

	public void validateAgainstSchema(String xml, File xsdFile) {
		try {
			// parse an XML document into a DOM tree
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			builderFactory.setNamespaceAware(true);
			DocumentBuilder parser = builderFactory.newDocumentBuilder();
			Document document = parser.parse(xml);

			// create a SchemaFactory capable of understanding WXS schemas
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

			// load a WXS schema, represented by a Schema instance
			Source schemaFile = new StreamSource(xsdFile);
			Schema schema = factory.newSchema(schemaFile);

			// create a Validator instance, which can be used to validate an
			// instance document
			Validator validator = schema.newValidator();

			// validate the DOM tree
			validator.validate(new DOMSource(document));

		} catch (FileNotFoundException ex) {
			throw new OpenClinicaSystemException("File was not found", ex.getCause());
		} catch (IOException ioe) {
			throw new OpenClinicaSystemException("IO Exception", ioe.getCause());
		} catch (SAXParseException spe) {
			// spe.printStackTrace();
			throw new OpenClinicaSystemException("Line : " + spe.getLineNumber() + " - " + spe.getMessage(),
					spe.getCause());
		} catch (SAXException e) {
			throw new OpenClinicaSystemException(e.getMessage(), e.getCause());
		} catch (ParserConfigurationException pce) {
			throw new OpenClinicaSystemException(pce.getMessage(), pce.getCause());
		}

	}

	public void validateAgainstSchema(String xml, InputStream xsdFile) {
		try {
			// parse an XML document into a DOM tree
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			builderFactory.setNamespaceAware(true);
			DocumentBuilder parser = builderFactory.newDocumentBuilder();
			Document document = parser.parse(new InputSource(new StringReader(xml)));

			// create a SchemaFactory capable of understanding WXS schemas
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

			// load a WXS schema, represented by a Schema instance
			Source schemaFile = new StreamSource(xsdFile);
			Schema schema = factory.newSchema(schemaFile);

			// create a Validator instance, which can be used to validate an
			// instance document
			Validator validator = schema.newValidator();

			// validate the DOM tree
			validator.validate(new DOMSource(document));

		} catch (FileNotFoundException ex) {
			throw new OpenClinicaSystemException("File was not found", ex.getCause());
		} catch (IOException ioe) {
			// ioe.printStackTrace();
			System.out.println(": " + ioe.getMessage());
			throw new OpenClinicaSystemException("IO Exception", ioe.getCause());
		} catch (SAXParseException spe) {
			// spe.printStackTrace();
			throw new OpenClinicaSystemException("Line : " + spe.getLineNumber() + " - " + spe.getMessage(),
					spe.getCause());
		} catch (SAXException e) {
			throw new OpenClinicaSystemException(e.getMessage(), e.getCause());
		} catch (ParserConfigurationException pce) {
			throw new OpenClinicaSystemException(pce.getMessage(), pce.getCause());
		}
	}

	public void validateAgainstSchema(File xmlFile, InputStream xsdFile) {
		try {
			// parse an XML document into a DOM tree
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			builderFactory.setNamespaceAware(true);
			DocumentBuilder parser = builderFactory.newDocumentBuilder();
			// parser.isNamespaceAware();
			Document document = parser.parse(xmlFile);

			// create a SchemaFactory capable of understanding WXS schemas
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

			// load a WXS schema, represented by a Schema instance
			Source schemaFile = new StreamSource(xsdFile);
			Schema schema = factory.newSchema(schemaFile);

			// create a Validator instance, which can be used to validate an
			// instance document
			Validator validator = schema.newValidator();

			// validate the DOM tree
			validator.validate(new DOMSource(document));

		} catch (FileNotFoundException ex) {
			throw new OpenClinicaSystemException("File was not found", ex.getCause());
		} catch (IOException ioe) {
			throw new OpenClinicaSystemException("IO Exception", ioe.getCause());
		} catch (SAXParseException spe) {
			// spe.printStackTrace();
			throw new OpenClinicaSystemException("Line : " + spe.getLineNumber() + " - " + spe.getMessage(),
					spe.getCause());
		} catch (SAXException e) {
			throw new OpenClinicaSystemException(e.getMessage(), e.getCause());
		} catch (ParserConfigurationException pce) {
			throw new OpenClinicaSystemException(pce.getMessage(), pce.getCause());
		}
	}
}
