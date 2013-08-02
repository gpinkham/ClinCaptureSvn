package org.apache.commons.httpclient;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodBase;

public class HttpClientMock extends HttpClient {
	
	private int expectedResponseStatus;
	private String expectedResponseBody;

	public HttpClientMock(int responseStatus, String responseBody) {
		this.expectedResponseStatus = responseStatus;
		this.expectedResponseBody = responseBody;
	}

	@Override
	public int executeMethod(HttpMethod method) throws UnsupportedEncodingException {
		((HttpMethodBase) method).setResponseStream(new ByteArrayInputStream(expectedResponseBody.getBytes("UTF-8")));
		return expectedResponseStatus;
	}
}