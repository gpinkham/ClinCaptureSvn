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

/* 
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * Copyright 2003-2009 Akaza Research 
 */
package org.akaza.openclinica.ws.ccts;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.akaza.openclinica.service.subject.SubjectServiceInterface;
import org.akaza.openclinica.ws.logic.CctsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.XPathParam;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@Endpoint
public class CctsSubjectEndpoint {

	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
	private final String NAMESPACE_URI_V1 = "http://openclinica.org/ws/ccts/subject/v1";
	private final String SUCCESS_MESSAGE = "success";
	private String dateFormat;

	/**
	 * Constructor
	 * 
	 * @param subjectService
	 * @param cctsService
	 */
	public CctsSubjectEndpoint(SubjectServiceInterface subjectService, CctsService cctsService) {
	}

	/**
	 * if NAMESPACE_URI_V1:commitRequest execute this method
	 * 
	 * @param gridId
	 * @param subject
	 * @param studyOid
	 * @return
	 * @throws Exception
	 */
	@PayloadRoot(localPart = "commitRequest", namespace = NAMESPACE_URI_V1)
	public Source createSubject(@XPathParam("//s:gridId") String gridId, @XPathParam("//s:subject") NodeList subject,
			@XPathParam("//s:study/@oid") String studyOid) throws Exception {
		logger.debug("In CreateSubject");
		return new DOMSource(mapConfirmation(SUCCESS_MESSAGE));
	}

	/**
	 * if NAMESPACE_URI_V1:commitRequest execute this method
	 * 
	 * @param gridId
	 * @param subject
	 * @param studyOid
	 * @return
	 * @throws Exception
	 */
	@PayloadRoot(localPart = "rollbackRequest", namespace = NAMESPACE_URI_V1)
	public Source rollBackSubject(@XPathParam("//s:gridId") String gridId, @XPathParam("//s:subject") NodeList subject,
			@XPathParam("//s:study/@oid") String studyOid) throws Exception {
		return new DOMSource(mapConfirmation(SUCCESS_MESSAGE));
	}

	/**
	 * Create Response
	 * 
	 * @param confirmation
	 * @return
	 * @throws Exception
	 */
	private Element mapConfirmation(String confirmation) throws Exception {
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document document = docBuilder.newDocument();

		Element responseElement = document.createElementNS(NAMESPACE_URI_V1, "commitResponse");
		Element resultElement = document.createElementNS(NAMESPACE_URI_V1, "result");
		resultElement.setTextContent(confirmation);
		responseElement.appendChild(resultElement);
		return responseElement;

	}

	/**
	 * @return
	 */
	public String getDateFormat() {
		return dateFormat;
	}

	/**
	 * @param dateFormat
	 */
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

}
