package com.clinovo.rule.ext;

import java.util.concurrent.Callable;

import javax.xml.ws.WebServiceException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clinovo.context.SubmissionContext;
import com.clinovo.model.WebServiceResult;

public class HttpTransportProtocol implements Callable<WebServiceResult> {

	private PostMethod method = null;
	private SubmissionContext context;
	private HttpClient client = new HttpClient();
	private Header randomizationHeader = new Header();

	private final Logger log = LoggerFactory.getLogger(getClass().getName());

	public WebServiceResult call() throws Exception {

		log.info("Initiating call to web service");

		if (context == null)
			throw new WebServiceException("The web service action cannot be null or empty");

		WebServiceResult result = new WebServiceResult();

		// Allow testing
		if (method == null) {
			method = new PostMethod(context.getAction().getRandomizationUrl());
		}

		try {

			// set parameters
			JSONObject postData = new JSONObject();

			// Required post data
			postData.append("TrialID", "");
			postData.append("SiteID", "");
			postData.append("PatientID", "");
			postData.append("BirthDate", "");
			postData.append("Initials", "");

			// Required Headers
			Header contentTypeHeader = new Header();
			contentTypeHeader.setName("Content-Type");
			contentTypeHeader.setValue("application/json");

			Header acceptHeader = new Header();
			acceptHeader.setName("Accept");
			acceptHeader.setValue("application/json");

			method.addRequestHeader(acceptHeader);
			method.addRequestHeader(contentTypeHeader);
			method.addRequestHeader(randomizationHeader);

			String body = postData.toString().replaceAll("]|\\[", "");

			method.setRequestEntity(new StringRequestEntity(body, "application/json", "utf-8"));

			client.executeMethod(method);

			result = context.processResponse(method.getResponseBodyAsString(), method.getStatusCode());

		} catch (Exception ex) {

			log.error(ex.getMessage());
			throw new WebServiceException(ex);

		} finally {
			method.releaseConnection();
		}

		return result;
	}

	public void setHttpMethod(PostMethod method) {
		this.method = method;
	}

	public void setSubmissionContext(SubmissionContext context) {
		this.context = context;
	}

	public void setRandomizationHeader(String token) {

		// Randomization Header
		randomizationHeader.setName("X-RANDOMIZE-TOKEN");
		randomizationHeader.setValue(token);
		
	}
}
