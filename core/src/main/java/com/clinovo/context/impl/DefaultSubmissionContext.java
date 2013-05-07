package com.clinovo.context.impl;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clinovo.context.SubmissionContext;
import com.clinovo.rule.WebServiceAction;

public abstract class DefaultSubmissionContext implements SubmissionContext {

	protected WebServiceAction action;
	protected PostMethod method = new PostMethod();
	protected HttpClient client = new HttpClient();

	protected final Logger log = LoggerFactory.getLogger(getClass().getName());

	public String authenticate(WebServiceAction action) throws Exception {

		JSONObject postData = new JSONObject();
		postData.append("SiteID", action.getUsername());
		postData.append("Password", action.getPassword());

		// Allow for testing
		if (method == null)
			method = new PostMethod(action.getAuthenticationUrl());

		Header contentTypeHeader = new Header();
		contentTypeHeader.setName("Content-Type");
		contentTypeHeader.setValue("application/json");

		Header acceptHeader = new Header();
		acceptHeader.setName("Accept");
		acceptHeader.setValue("application/json");

		method.addRequestHeader(acceptHeader);
		method.addRequestHeader(contentTypeHeader);

		// Remove brackets
		String body = postData.toString().replaceAll("]|\\[", "");
		method.setRequestEntity(new StringRequestEntity(body, "application/json", "utf-8"));

		log.info("Making randomization post request with: {} ", body);
		client.executeMethod(method);

		String response = method.getResponseBodyAsString().replaceAll("]|\\[", "");

		return response;
	}

	public void setAction(WebServiceAction action) {
		this.action = action;
	}

	public WebServiceAction getAction() {
		return this.action;
	}

	public void setHttpMethod(HttpMethod method) {

		this.method = (PostMethod) method;
	}
}
