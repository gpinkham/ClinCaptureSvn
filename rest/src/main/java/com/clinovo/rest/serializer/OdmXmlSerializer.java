/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 *
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clincapture.com/contact for pricing information.
 *
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use.
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO'S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.rest.serializer;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.clinovo.enums.RestServerVersion;
import com.clinovo.rest.model.RestData;
import com.clinovo.rest.model.Server;
import com.clinovo.rest.model.wadl.Application;
import com.clinovo.rest.odm.RestOdmContainer;

/**
 * OdmXmlSerializer.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class OdmXmlSerializer extends Jaxb2RootElementHttpMessageConverter {

	private static final Logger LOGGER = LoggerFactory.getLogger(OdmXmlSerializer.class);

	public static final String ACCEPT = "accept";
	public static final String CONTENT_TYPE = "Content-Type";

	@Override
	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		String accept = "";
		boolean proceed = false;
		try {
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
					.getRequest();
			accept = request.getHeader(ACCEPT);
			String requestURI = request.getRequestURI();
			requestURI = requestURI.concat(requestURI.endsWith("/") ? "" : "/");
			proceed = requestURI.equals(request.getContextPath().concat(request.getServletPath()).concat("/odm/"))
					|| requestURI.equals(request.getContextPath().concat(request.getServletPath()).concat("/wadl/"));
			if (proceed && !accept.contains(MediaType.APPLICATION_XML_VALUE)) {
				setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON));
			}
		} catch (Exception ex) {
			LOGGER.error("Error has occurred.", ex);
		}
		return (accept != null && accept.contains(MediaType.APPLICATION_XML_VALUE)) || proceed;
	}

	@Override
	protected void writeToResult(Object o, HttpHeaders headers, Result result) throws IOException {
		StringBuilder xmlOutput = new StringBuilder();
		if (o instanceof Application) {
			super.writeToResult(o, headers, result);
		} else {
			if (o instanceof String) {
				LinkedList mediaTypes = new LinkedList();
				mediaTypes.add(MediaType.APPLICATION_XML_VALUE);
				headers.put(CONTENT_TYPE, mediaTypes);
				xmlOutput.append((String) o);
			} else {
				try {
					RestOdmContainer restOdmContainer = new RestOdmContainer();
					restOdmContainer.setServer(new Server());
					restOdmContainer.getServer().setVersion(RestServerVersion.VERSION_1_0.getValue());
					restOdmContainer.setRestData(new RestData());
					Method method = getSetterMethod(RestData.class.getMethods(), o);
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
					xmlOutput.append(writer.toString());
				} catch (Exception ex) {
					LOGGER.error("Error has occurred.", ex);
				}
			}
			((StreamResult) result).setWriter(new StringWriter());
			((StreamResult) result).getOutputStream().write(xmlOutput.toString().getBytes("UTF-8"));
		}
	}

	/**
	 * Returns setter method in RestData.
	 * 
	 * @param methods
	 *            Method[]
	 * @param o
	 *            Object
	 * @return Method
	 */
	public static Method getSetterMethod(Method[] methods, Object o) {
		String simpleName = o.getClass().getSimpleName().toLowerCase();
		if (o instanceof List && ((List) o).size() > 0) {
			simpleName = ((List) o).get(0).getClass().getSimpleName().toLowerCase().replace("bean", "list");
		}
		for (Method method : methods) {
			if (method.getName().equalsIgnoreCase("set".concat(simpleName))) {
				return method;
			}
		}
		return null;
	}
}
