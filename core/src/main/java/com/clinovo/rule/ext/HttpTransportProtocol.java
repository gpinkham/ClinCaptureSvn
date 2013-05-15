package com.clinovo.rule.ext;

import java.util.concurrent.Callable;

import javax.xml.ws.WebServiceException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpsURL;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.SSLProtocolSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clinovo.context.SubmissionContext;
import com.clinovo.model.WebServiceResult;

public class HttpTransportProtocol implements Callable<WebServiceResult> {

	private PostMethod method = null;
	private SubmissionContext context;
	private HttpClient client = new HttpClient();

	private final Logger log = LoggerFactory.getLogger(getClass().getName());

	public WebServiceResult call() throws Exception {

		log.info("Initiating call to web service");

		if (context == null)
			throw new WebServiceException("The web service action cannot be null or empty");

		WebServiceResult result = new WebServiceResult();

		// Allow testing
		if (method == null) {
			method = new PostMethod();
		}

		try {

			for (Header header : context.getHttpHeaders()) {

				method.addRequestHeader(header);
			}

			ProtocolSocketFactory sslFactory = new SSLProtocolSocketFactory();
			Protocol.registerProtocol("https", new Protocol("https", sslFactory, 443));

			HttpsURL url = new HttpsURL(context.getAction().getRandomizationUrl());
			method.setURI(url);
			method.setRequestEntity(context.getRequestEntity());

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

}
