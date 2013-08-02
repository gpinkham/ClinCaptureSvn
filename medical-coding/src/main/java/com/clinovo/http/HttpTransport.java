package com.clinovo.http;

import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpTransport {
	
	private HttpMethod method = null;
	private HttpClient client = null;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	public String processRequest() throws Exception {
		
		log.info("Executing HTTP Request");
		
		// Allow for testing
		if(client == null)
			client = new HttpClient();
		
		client.executeMethod(method);
		
		return processResponse(client.executeMethod(method));
	}

	private String processResponse(int statusCode) throws Exception {
		
		if(Pattern.compile("(4|5)\\d{2}").matcher(String.valueOf(statusCode)).matches()) {
			
			throw new HttpTransportException("An error occurred during the http request");
		}
		
		return method.getResponseBodyAsString();
	}
	
	public void setMethod(HttpMethod method) {
		this.method = method;
	}
	
	public void setClient(HttpClient client) {
		this.client = client;
	}
}
