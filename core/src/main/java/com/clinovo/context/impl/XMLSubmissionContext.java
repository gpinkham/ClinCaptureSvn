/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 * 
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer. 
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 * 
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use. 
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.context.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.WebServiceException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import com.clinovo.model.RandomizationResult;
import com.clinovo.util.XMLUtil;

public class XMLSubmissionContext extends DefaultSubmissionContext {

	public RandomizationResult processResponse(String response, int httpStatus) throws Exception {

		RandomizationResult result = new RandomizationResult();

		if (httpStatus == HttpStatus.SC_OK) {

			result = XMLUtil.createWebServiceResult(response);

		} else if (httpStatus == HttpStatus.SC_SERVICE_UNAVAILABLE) {

			throw new WebServiceException(response);

		} else if (httpStatus == HttpStatus.SC_FORBIDDEN || httpStatus == HttpStatus.SC_UNAUTHORIZED) {

			throw new WebServiceException(response);

		} else if (httpStatus == HttpStatus.SC_BAD_REQUEST) {
			
			log.error(response);
			throw new WebServiceException(response);
			
		} else {

			log.warn("Web service call failed with message: {} : {}", httpStatus, response);

		}
		return result;
	}

	public List<Header> getHttpHeaders() throws Exception {

		List<Header> headers = new ArrayList<Header>();

		// Required Headers
		Header contentTypeHeader = new Header();
		contentTypeHeader.setName("Content-Type");
		contentTypeHeader.setValue("application/json");

		Header acceptHeader = new Header();
		acceptHeader.setName("Accept");
		acceptHeader.setValue("application/json");

		headers.add(acceptHeader);
		headers.add(contentTypeHeader);

		return headers;
	}

	public RequestEntity getRequestEntity() throws Exception {

		StringBuilder postData = new StringBuilder("<randomize>");

		// Trial Id
		postData.append("<TrialID>");
		postData.append(randomization.getTrialId());
		postData.append("</TrialID>");

		// Site Id
		postData.append("<SiteID>");
		postData.append(randomization.getSiteId());
		postData.append("</SiteID>");

		// Initials
		postData.append("<Initials>");
		postData.append(randomization.getInitials());
		postData.append("</Initials>");

		// Patient Id
		postData.append("<PatientID>");
		postData.append(randomization.getPatientId());
		postData.append("</PatientID>");

		// Strata data
		postData.append("<StrataAnswers>");
		postData.append("<StratificationID>");
		postData.append("</StratificationID>");
		postData.append("<Level>");
		postData.append("</Level>");
		postData.append("</StrataAnswers>");

		StringRequestEntity entity = new StringRequestEntity(postData.toString(), "application/xml", "utf-8");

		return entity;

	}

	@Override
	String getBody() throws Exception {

		StringBuilder body = new StringBuilder("<Authentication>");

		body.append("<SiteID>");
		body.append(randomization.getUsername());
		body.append("</SiteID>");
		body.append("<Password>");
		body.append(randomization.getPassword());
		body.append("</Password>");
		body.append("</Authentication>");

		return body.toString();
	}
}
