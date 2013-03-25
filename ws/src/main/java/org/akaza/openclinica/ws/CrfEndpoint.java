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
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

/*
 - * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * Copyright 20032009 Akaza Research
 */
package org.akaza.openclinica.ws;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.service.subject.SubjectServiceInterface;
import org.openclinica.ws.crf.v1.CreateCrfResponse;
import org.openclinica.ws.crf.v1.CrfType;
import org.openclinica.ws.crf.v1.ObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.sql.DataSource;
import javax.xml.bind.JAXBElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * @author Krikor Krumlian
 * 
 */
@Endpoint
public class CrfEndpoint {

	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
	private final String NAMESPACE_URI_V1 = "http://openclinica.org/ws/crf/v1";
	private final String SUCCESS_MESSAGE = "success";
	private final String FAIL_MESSAGE = "fail";
	private String dateFormat;
	private Properties dataInfo;

	private final SubjectServiceInterface subjectService;
	private final DataSource dataSource;
	private final ObjectFactory objectFactory;

	/**
	 * Constructor
	 * 
	 * @param subjectService
	 * @param cctsService
	 */
	public CrfEndpoint(SubjectServiceInterface subjectService, DataSource dataSource) {
		this.subjectService = subjectService;
		this.dataSource = dataSource;
		this.objectFactory = new ObjectFactory();
	}

	@PayloadRoot(localPart = "createCrfRequest", namespace = NAMESPACE_URI_V1)
	public CreateCrfResponse store(JAXBElement<CrfType> requestElement) throws Exception {

		CrfType crf = requestElement.getValue();
		String filePathWithName = getDataInfo().getProperty("filePath") + "crf/original/" + crf.getFileName();

		try {
			FileOutputStream fstream = new FileOutputStream(filePathWithName);
			crf.getFile().writeTo(fstream);
			fstream.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		CreateCrfResponse crfResponse = new CreateCrfResponse();
		crfResponse.setResult(SUCCESS_MESSAGE);
		crfResponse.setKey("test");
		return crfResponse;
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
	 * Helper Method to resolve dates
	 * 
	 * @param dateAsString
	 * @return
	 * @throws ParseException
	 */
	private Date getDate(String dateAsString) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(getDateFormat());
		return sdf.parse(dateAsString);
	}

	/**
	 * Helper Method to get the user account
	 * 
	 * @return UserAccountBean
	 */
	private UserAccountBean getUserAccount() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = null;
		if (principal instanceof UserDetails) {
			username = ((UserDetails) principal).getUsername();
		} else {
			username = principal.toString();
		}
		UserAccountDAO userAccountDao = new UserAccountDAO(dataSource);
		return (UserAccountBean) userAccountDao.findByUserName(username);
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

	public Properties getDataInfo() {
		return dataInfo;
	}

	public void setDataInfo(Properties dataInfo) {
		this.dataInfo = dataInfo;
	}
}
