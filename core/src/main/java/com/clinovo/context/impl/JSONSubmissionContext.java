package com.clinovo.context.impl;

import javax.xml.ws.WebServiceException;

import org.apache.commons.httpclient.HttpStatus;
import org.json.JSONObject;

import com.clinovo.model.WebServiceResult;

public class JSONSubmissionContext extends DefaultSubmissionContext {

	public WebServiceResult processResponse(String response, int httpStatus) throws Exception {

		WebServiceResult result = new WebServiceResult();

		if (httpStatus == HttpStatus.SC_OK) {

			JSONObject returnData = new JSONObject(response);

			// Site Id
			String siteId = returnData.getString("SiteID").replaceAll("\"]|\\[\"", "");

			// Trial Id
			String trialId = returnData.getString("TrialID").replaceAll("\"]|\\[\"", "");

			// Patient Id
			String patientId = returnData.getString("PatientID").replaceAll("\"]|\\[\"", "");

			// Initials
			String initials = returnData.getString("Initials").replaceAll("\"]|\\[\"", "");

			result.setSiteId(siteId);
			result.setTrialId(trialId);
			result.setInitials(initials);
			result.setPatientId(patientId);

		} else if (httpStatus == HttpStatus.SC_SERVICE_UNAVAILABLE) {

			throw new WebServiceException(response);

		} else if (httpStatus == HttpStatus.SC_FORBIDDEN || httpStatus == HttpStatus.SC_UNAUTHORIZED) {

			throw new WebServiceException(response);
		}

		return result;
	}

}
