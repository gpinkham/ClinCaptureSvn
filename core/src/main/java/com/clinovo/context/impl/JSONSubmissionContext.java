package com.clinovo.context.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.ws.WebServiceException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.clinovo.model.WebServiceResult;

public class JSONSubmissionContext extends DefaultSubmissionContext {

	public WebServiceResult processResponse(String response, int httpStatus) throws Exception {

		WebServiceResult result = new WebServiceResult();

		if (httpStatus == HttpStatus.SC_OK) {

			log.info("Successfully Randomized Request.");

			JSONObject returnData = new JSONObject(response);

			// Treatment Id
			String treament = returnData.getString("TreatmentID");

			// Patient Id
			String patientId = returnData.getString("PatientID");

			// Randomization Result
			String randomizationResult = returnData.getString("RandomizationResult");

			result.setTreatment(treament);
			result.setPatientId(patientId);
			result.setRandomizationResult(randomizationResult);

		} else if (httpStatus == HttpStatus.SC_SERVICE_UNAVAILABLE) {

			log.warn(response);
			throw new WebServiceException(response);

		} else if (httpStatus == HttpStatus.SC_FORBIDDEN || httpStatus == HttpStatus.SC_UNAUTHORIZED) {

			log.warn(response);
			throw new WebServiceException(response);

		} else if (httpStatus == HttpStatus.SC_BAD_REQUEST) {

			log.error(response);
			throw new WebServiceException(response);

		} else if (httpStatus == DefaultSubmissionContext.DUPLICATION_RANDOMIZATION) {

			log.info("Duplicate randomization attempt");
			throw new WebServiceException("This subject has already been randomized");
		}

		return result;
	}

	public RequestEntity getRequestEntity() throws Exception {

		JSONObject postData = new JSONObject();

		postData.put("SiteID", action.getSiteId());
		postData.put("Initials", action.getInitials());
		postData.put("PatientID", action.getPatientId());
		postData.put("TrialID", Integer.parseInt(action.getTrialId()));

		// Strata
		if (action.getStratificationId() != null || action.getStratificationId() != "") {

			JSONArray array = new JSONArray();
			JSONObject strataObject = new JSONObject();
			strataObject.put("StratificationID", "1");
			strataObject.put("Level", action.getStratificationId());

			array.put(strataObject);

			postData.put("StrataAnswers", array);
		}

		log.info("Randomizing with: {}", postData.toString());

		StringRequestEntity entity = new StringRequestEntity(postData.toString(), "application/json", "utf-8");

		return entity;
	}

	public List<Header> getHttpHeaders() throws Exception {

		Pattern incorrectCredentialsPattern = Pattern.compile("\\{\"Code\":400,\"Error\":\"Credentials not valid.\"");

		String authToken = authenticate();

		Matcher matcher = incorrectCredentialsPattern.matcher(authToken);

		// Incorrect creds
		if (matcher.find()) {

			throw new WebServiceException("Authentication error, please contact your Study Administrator");
		}

		try {

			JSONObject token = new JSONObject(authToken);
			currentAuthToken = token.getString("Token");

		} catch (JSONException ex) {
			log.error("An error occurred during the authentication with the randomization service", ex.getMessage());
			throw new WebServiceException(ex);
		}

		List<Header> headers = new ArrayList<Header>();

		// Required Headers
		Header contentTypeHeader = new Header();
		contentTypeHeader.setName("Content-Type");
		contentTypeHeader.setValue("application/json");

		Header acceptHeader = new Header();
		acceptHeader.setName("Accept");
		acceptHeader.setValue("application/json");

		// Randomization Header
		Header randomizationHeader = new Header();
		randomizationHeader.setName("X-RANDOMIZE-TOKEN");

		randomizationHeader.setValue(currentAuthToken);

		headers.add(acceptHeader);
		headers.add(contentTypeHeader);
		headers.add(randomizationHeader);

		return headers;
	}

	@Override
	String getBody() throws Exception {

		JSONObject postData = new JSONObject();
		postData.put("SiteID", action.getUsername());
		postData.put("Password", action.getPassword());

		return postData.toString();
	}
}
