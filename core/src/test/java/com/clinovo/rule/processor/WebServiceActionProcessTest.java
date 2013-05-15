package com.clinovo.rule.processor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.xml.ws.WebServiceException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.auth.AuthState;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.clinovo.BaseTest;
import com.clinovo.context.SubmissionContext;
import com.clinovo.context.impl.XMLSubmissionContext;
import com.clinovo.model.WebServiceResult;
import com.clinovo.rule.WebServiceAction;
import com.clinovo.rule.ext.HttpTransportProtocol;
import com.clinovo.util.XMLUtil;

public class WebServiceActionProcessTest extends BaseTest {

	private WebServiceActionProcessor processor;
	SubmissionContext context = new XMLSubmissionContext();

	@Before
	public void setUp() throws Exception {

		WebServiceAction action = createWebServiceAction();

		PostMethod method = Mockito.mock(PostMethod.class);

		// Expectations
		Mockito.when(method.getStatusCode()).thenReturn(HttpStatus.SC_OK);
		Mockito.when(method.getParams()).thenReturn(new HttpMethodParams());
		Mockito.when(method.getHostAuthState()).thenReturn(new AuthState());
		Mockito.when(method.getProxyAuthState()).thenReturn(new AuthState());
		Mockito.when(method.getURI()).thenReturn(new URI("http://coachella.com", false));
		Mockito.when(method.getRequestHeaders(Mockito.anyString())).thenReturn(new Header[0]);
		Mockito.when(method.getResponseBodyAsString()).thenReturn(XMLUtil.docToString(webServiceReturnValue));

		HttpTransportProtocol protocol = new HttpTransportProtocol();

		protocol.setHttpMethod(method);
		protocol.setSubmissionContext(context);

		protocol.setHttpMethod(method);

		context.setAction(action);

		protocol.setSubmissionContext(context);

		processor = new WebServiceActionProcessor();
		processor.setTransportProtocol(protocol);

		processor = new WebServiceActionProcessor();
		processor.setTransportProtocol(protocol);
	}

	@Test(expected = WebServiceException.class)
	public void testThatExecuteThrowsExceptionOnInvalidInput() throws Exception {

		processor.execute(null);
	}

	@Test
	public void testThatExecuteDoesNotReturnNull() throws Exception {

		assertNotNull("Should not return null", processor.execute(context));
	}

	@Test
	public void testThatExecuteReturnsValidResultWithTreatment() throws Exception {

		WebServiceResult result = (WebServiceResult) processor.execute(context);
		assertNotNull("The processor should return a valid result with treatment", result.getTreatment());

	}

	@Test
	public void testThatExecuteReturnsValidResultWithCorrectTreatment() throws Exception {

		WebServiceResult result = (WebServiceResult) processor.execute(context);
		assertEquals("The processor should return a valid result with the correct treatment", "2",
				result.getTreatment());

	}

	@Test
	public void testThatExecuteReturnsValidResultWithPatientId() throws Exception {

		WebServiceResult result = (WebServiceResult) processor.execute(context);
		assertNotNull("The processor should return a valid result with patientId", result.getPatientId());

	}

	@Test
	public void testThatExecuteReturnsValidResultWithCorrectPatientId() throws Exception {

		WebServiceResult result = (WebServiceResult) processor.execute(context);
		assertEquals("The processor should return a valid result with the correct treatment", "subject2",
				result.getPatientId());

	}

	@Test
	public void testThatExecuteReturnsValidResultWithRandomizationResult() throws Exception {

		WebServiceResult result = (WebServiceResult) processor.execute(context);
		assertNotNull("The processor should return a valid result with Randomization result", result.getRandomizationResult());

	}

	@Test
	public void testThatExecuteReturnsValidResultWithCorrectRandomizationResult() throws Exception {

		WebServiceResult result = (WebServiceResult) processor.execute(context);
		assertEquals("The processor should return a valid result with the correct Randomization result", "radiotherapy",
				result.getRandomizationResult());

	}

}