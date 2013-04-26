package com.clinovo.rule.ext;

import java.io.IOException;
import java.util.concurrent.Callable;

import javax.xml.ws.WebServiceException;

import org.akaza.openclinica.domain.rule.action.RuleActionBean;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clinovo.model.WebServiceResult;
import com.clinovo.rule.WebServiceAction;
import com.clinovo.util.XMLUtil;

public class HttpTransportProtocol implements Callable<WebServiceResult> {

	private GetMethod method = null;
	private WebServiceAction webServiceAction;

	private final Logger log = LoggerFactory.getLogger(getClass().getName());

	public WebServiceResult call() throws Exception {

		log.info("Initiating call to web service");

		if (webServiceAction == null)
			throw new WebServiceException("The web service action cannot be null or empty");

		WebServiceResult result = new WebServiceResult();

		HttpClient client = new HttpClient();

		// Allow testing
		if (method == null) {
			method = new GetMethod(webServiceAction.getUrl());
		}

		try {

			// set parameters
			HttpMethodParams parameters = new HttpMethodParams();

			parameters.setParameter("username", webServiceAction.getUsername());
			parameters.setParameter("rolename", webServiceAction.getRolename());
			parameters.setParameter("studyOID", webServiceAction.getStudyOID());
			parameters.setParameter("studySubjectOID", webServiceAction.getStudySubjectOID());

			method.setParams(parameters);

			client.executeMethod(method);

			result = processResponse();

		} catch (Exception ex) {

			log.error(ex.getMessage());
			throw new WebServiceException(ex);

		} finally {
			method.releaseConnection();
		}

		return result;
	}

	private WebServiceResult processResponse() throws IOException, Exception {

		WebServiceResult result = new WebServiceResult();

		// Everything is chimmy
		if (method.getStatusCode() == HttpStatus.SC_OK) {

			String response = method.getResponseBodyAsString();

			result = XMLUtil.createWebServiceResult(response);

		// You forgot to pay tax
		} else if (method.getStatusCode() == HttpStatus.SC_SERVICE_UNAVAILABLE) {

			result = XMLUtil.createWebServiceResult(method.getResponseBodyAsString());
			throw new WebServiceException(result.getMessage());

		// Gates are closed 
		} else if (method.getStatusCode() == HttpStatus.SC_FORBIDDEN
				|| method.getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {

			result = XMLUtil.createWebServiceResult(method.getResponseBodyAsString());
			throw new WebServiceException(result.getMessage());
			
		} else {

			result = XMLUtil.createWebServiceResult(method.getResponseBodyAsString());
			log.warn("Web service call failed with message: {} : {}", method.getStatusCode(), result.getMessage());

		}
		return result;
	}

	public void setGetMethod(GetMethod method) {
		this.method = method;
	}

	public void setWebServiceAction(RuleActionBean ruleAction) {
		this.webServiceAction = (WebServiceAction) ruleAction;
	}
}
