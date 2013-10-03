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

import com.clinovo.model.RandomizationResult;

public class JSONSubmissionContext extends DefaultSubmissionContext {

	public RandomizationResult processResponse(String response, int httpStatus) throws Exception {

		RandomizationResult result = new RandomizationResult();

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
			
		} else if(httpStatus == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
			
			log.info("Internal server error at randomize.net");
			throw new WebServiceException("Randomization error occurred. Please contact your system administrator");
		}

		return result;
	}

	public RequestEntity getRequestEntity() throws Exception {

		JSONObject postData = new JSONObject();

		postData.put("SiteID", randomization.getSiteId());
		postData.put("Initials", randomization.getInitials());
		postData.put("PatientID", randomization.getPatientId());
		postData.put("TrialID", Integer.parseInt(randomization.getTrialId()));
		
		// Strata
		if (randomization.getStratificationLevel() != null 
				&& randomization.getStratificationLevel().length() > 0) {

			JSONArray array = new JSONArray(randomization.getStratificationLevel());

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
		postData.put("SiteID", randomization.getUsername());
		postData.put("Password", randomization.getPassword());

		return postData.toString();
	}
}
