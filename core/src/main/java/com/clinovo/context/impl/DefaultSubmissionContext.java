package com.clinovo.context.impl;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clinovo.context.SubmissionContext;
import com.clinovo.rule.WebServiceAction;

public abstract class DefaultSubmissionContext implements SubmissionContext {

	protected WebServiceAction action;
	protected PostMethod method = null;
	protected String currentAuthToken = null;
	protected HttpClient client = new HttpClient();
	public static final int DUPLICATION_RANDOMIZATION = 510;

	protected final Logger log = LoggerFactory.getLogger(getClass().getName());

	abstract String getBody() throws Exception;

	public String authenticate() throws Exception {

		// Re-use token if existing
		if (currentAuthToken != null) {
			return currentAuthToken;
		}
		
		method = new PostMethod(action.getAuthenticationUrl());
		
		// Allow for testing
		if (client == null)
			client = new HttpClient();

		Header contentTypeHeader = new Header();
		contentTypeHeader.setName("Content-Type");
		contentTypeHeader.setValue("application/json");

		Header acceptHeader = new Header();
		acceptHeader.setName("Accept");
		acceptHeader.setValue("application/json");

		method.addRequestHeader(acceptHeader);
		method.addRequestHeader(contentTypeHeader);

		method.setRequestEntity(new StringRequestEntity(getBody(), "application/json", "utf-8"));

		log.info("Making randomization post request with: {} ", getBody());
		client.executeMethod(method);

		String response = method.getResponseBodyAsString();

		return response;
	}

	public void setAction(WebServiceAction action) {
		this.action = action;
	}

	public WebServiceAction getAction() {
		return this.action;
	}

	public void setHttpClient(HttpClient client) {

		this.client = client;
	}
}
