package com.clinovo.context.impl;

import javax.xml.ws.WebServiceException;

import org.apache.commons.httpclient.HttpStatus;

import com.clinovo.model.WebServiceResult;
import com.clinovo.util.XMLUtil;

public class XMLSubmissionContext extends DefaultSubmissionContext {

	public WebServiceResult processResponse(String response, int httpStatus) throws Exception {

		WebServiceResult result = new WebServiceResult();

		if (httpStatus == HttpStatus.SC_OK) {

			result = XMLUtil.createWebServiceResult(response);

		} else if (httpStatus == HttpStatus.SC_SERVICE_UNAVAILABLE) {

			throw new WebServiceException(response);

		} else if (httpStatus == HttpStatus.SC_FORBIDDEN || httpStatus == HttpStatus.SC_UNAUTHORIZED) {

			throw new WebServiceException(response);

		} else {

			log.warn("Web service call failed with message: {} : {}", httpStatus, response);

		}
		return result;
	}
}
