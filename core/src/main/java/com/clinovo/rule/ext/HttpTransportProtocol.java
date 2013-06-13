package com.clinovo.rule.ext;

import java.io.ByteArrayOutputStream;
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

	private HttpClient client = null;
	private SubmissionContext context;
	private PostMethod method = new PostMethod();

	private final Logger log = LoggerFactory.getLogger(getClass().getName());

	public WebServiceResult call() throws Exception {

		log.info("Initiating call to web service");

		if (context == null)
			throw new WebServiceException("The web service action cannot be null or empty");

		if (client == null)
			client = new HttpClient();

		WebServiceResult result = new WebServiceResult();

		try {

			for (Header header : context.getHttpHeaders()) {

				method.addRequestHeader(header);
			}

			ProtocolSocketFactory sslFactory = new SSLProtocolSocketFactory();
			Protocol.registerProtocol("https", new Protocol("https", sslFactory, 443));

			HttpsURL url = new HttpsURL(context.getAction().getRandomizationUrl());
			method.setURI(url);
			method.setRequestEntity(context.getRequestEntity());

			int status = client.executeMethod(method);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			byte[] byteArray = new byte[1024];
			int count = 0;
			while ((count = method.getResponseBodyAsStream().read(byteArray, 0, byteArray.length)) > 0) {
				outputStream.write(byteArray, 0, count);
			}
			result = context.processResponse(new String(outputStream.toByteArray(), "UTF-8"), status);

		} catch (Exception ex) {

			log.error(ex.getMessage());
			throw new WebServiceException(ex);

		} finally {
			method.releaseConnection();
		}

		return result;
	}

	public void setHttpClient(HttpClient client) {
		this.client = client;
	}

	public void setSubmissionContext(SubmissionContext context) {
		this.context = context;
	}

}
