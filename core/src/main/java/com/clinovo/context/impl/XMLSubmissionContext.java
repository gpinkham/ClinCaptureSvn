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
		postData.append(randomiation.getTrialId());
		postData.append("</TrialID>");

		// Site Id
		postData.append("<SiteID>");
		postData.append(randomiation.getSiteId());
		postData.append("</SiteID>");

		// Initials
		postData.append("<Initials>");
		postData.append(randomiation.getInitials());
		postData.append("</Initials>");

		// Patient Id
		postData.append("<PatientID>");
		postData.append(randomiation.getPatientId());
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
		body.append(randomiation.getUsername());
		body.append("</SiteID>");
		body.append("<Password>");
		body.append(randomiation.getPassword());
		body.append("</Password>");
		body.append("</Authentication>");

		return body.toString();
	}
}
