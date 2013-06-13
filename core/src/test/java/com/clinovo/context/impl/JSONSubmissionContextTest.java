package com.clinovo.context.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.xml.ws.WebServiceException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.junit.Before;
import org.junit.Test;

import com.clinovo.BaseTest;
import com.clinovo.context.SubmissionContext;
import com.clinovo.model.WebServiceResult;
import com.clinovo.rule.WebServiceAction;
import com.clinovo.rule.ext.HttpTransportProtocol;

public class JSONSubmissionContextTest extends BaseTest {

	private HttpClient client;
	private SubmissionContext context;
	private HttpTransportProtocol protocol = null;

	@Before
	public void setUp() throws Exception {

		context = new JSONSubmissionContext();

		WebServiceAction action = createWebServiceAction();

		context.setAction(action);

		client = createMockHttpClient(jsonReturnedData.toString(), HttpStatus.SC_OK);

		protocol = new HttpTransportProtocol();
		protocol.setHttpClient(client);

		context.setAction(action);
		protocol.setSubmissionContext(context);

	}

	@Test
	public void testThatProcessResponseDoesNotReturnNull() throws Exception {

		assertNotNull("Should not return null", context.processResponse(jsonReturnedData.toString(), HttpStatus.SC_OK));
	}

	@Test(expected = WebServiceException.class)
	public void testThatProcessResponseThrowsExceptionWhenWebServiceEndpointIsUnavailable() throws Exception {

		context.processResponse(null, HttpStatus.SC_SERVICE_UNAVAILABLE);
	}

	@Test(expected = WebServiceException.class)
	public void testThatProcessResponseThrowsExceptionWhenAccessIsDenied() throws Exception {
		context.processResponse(null, HttpStatus.SC_FORBIDDEN);
	}

	@Test(expected = WebServiceException.class)
	public void testThatProcessResponseThrowsExceptionWhenOperationIsUnAuthorized() throws Exception {
		context.processResponse(null, HttpStatus.SC_UNAUTHORIZED);
	}

	@Test
	public void testThatProcessResponseReturnsResultWithTreatment() throws Exception {

		WebServiceResult result = context.processResponse(jsonReturnedData.toString(), HttpStatus.SC_OK);

		assertNotNull("Should return valid Treatment", result.getTreatment());
	}

	@Test
	public void testThatProcessResponseReturnsresultWithCorrectTreatment() throws Exception {

		WebServiceResult result = context.processResponse(jsonReturnedData.toString(), HttpStatus.SC_OK);

		assertEquals("Should return correct Treatment", "3", result.getTreatment());
	}

	@Test
	public void testThatProcessResponseReturnsResultWithPatientId() throws Exception {

		WebServiceResult result = context.processResponse(jsonReturnedData.toString(), HttpStatus.SC_OK);

		assertNotNull("Should return valid Patient Id", result.getPatientId());
	}

	@Test
	public void testThatProcessResponseReturnsresultWithCorrectPatientId() throws Exception {

		WebServiceResult result = context.processResponse(jsonReturnedData.toString(), HttpStatus.SC_OK);

		assertEquals("Should return correct Patient Id", "abc123", result.getPatientId());
	}

	@Test
	public void testThatProcessResponseReturnsResultWithRandomizationResult() throws Exception {

		WebServiceResult result = context.processResponse(jsonReturnedData.toString(), HttpStatus.SC_OK);

		assertNotNull("Should return valid randomization result", result.getRandomizationResult());
	}

	@Test
	public void testThatProcessResponseReturnsresultWithCorrectRandomizationResult() throws Exception {

		WebServiceResult result = context.processResponse(jsonReturnedData.toString(), HttpStatus.SC_OK);

		assertEquals("Should return correct randomization result", "radiotherapy", result.getRandomizationResult());
	}
}
