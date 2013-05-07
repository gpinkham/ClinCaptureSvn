package com.clinovo.context.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.xml.ws.WebServiceException;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.Before;
import org.junit.Test;

import com.clinovo.BaseTest;
import com.clinovo.context.SubmissionContext;
import com.clinovo.model.WebServiceResult;
import com.clinovo.rule.WebServiceAction;
import com.clinovo.rule.ext.HttpTransportProtocol;

public class JSONSubmissionContextTest extends BaseTest {

	private PostMethod method;
	private SubmissionContext context;

	@Before
	public void setUp() throws Exception {

		context = new JSONSubmissionContext();

		WebServiceAction action = createWebServiceAction();

		context.setAction(action);

		method = createPostMethodMock(action.getRandomizationUrl(), jsonReturnedData.toString());

		HttpTransportProtocol protocol = new HttpTransportProtocol();
		protocol.setHttpMethod(method);

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
	public void testThatProcessResponseReturnsResultWithTrialId() throws Exception {

		WebServiceResult result = context.processResponse(jsonReturnedData.toString(), HttpStatus.SC_OK);

		assertNotNull("Should return valid Trial Id", result.getTrialId());
	}

	@Test
	public void testThatProcessResponseReturnsresultWithCorrectTrialId() throws Exception {

		WebServiceResult result = context.processResponse(jsonReturnedData.toString(), HttpStatus.SC_OK);

		assertEquals("Should return correct Trial Id", "1185", result.getTrialId());
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
	public void testThatProcessResponseReturnsResultWithSiteId() throws Exception {
		
		WebServiceResult result = context.processResponse(jsonReturnedData.toString(), HttpStatus.SC_OK);
		
		assertNotNull("Should return valid site Id", result.getSiteId());
	}
	
	@Test
	public void testThatProcessResponseReturnsresultWithCorrectSiteId() throws Exception {

		WebServiceResult result = context.processResponse(jsonReturnedData.toString(), HttpStatus.SC_OK);

		assertEquals("Should return correct Site Id", "clinovotest", result.getSiteId());
	}
	
	@Test
	public void testThatProcessResponseReturnsResultWithInitials() throws Exception {
		
		WebServiceResult result = context.processResponse(jsonReturnedData.toString(), HttpStatus.SC_OK);
		
		assertNotNull("Should return valid initials", result.getInitials());
	}
	
	@Test
	public void testThatProcessResponseReturnsresultWithCorrectInitials() throws Exception {

		WebServiceResult result = context.processResponse(jsonReturnedData.toString(), HttpStatus.SC_OK);

		assertEquals("Should return correct Initials", "MDG", result.getInitials());
	}
	
}
