package com.clinovo.rule.processor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.xml.ws.WebServiceException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.auth.AuthState;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.clinovo.BaseTest;
import com.clinovo.model.WebServiceResult;
import com.clinovo.rule.WebServiceAction;
import com.clinovo.rule.ext.HttpTransportProtocol;
import com.clinovo.util.XMLUtil;

public class WebServiceActionProcessTest extends BaseTest {

	private WebServiceAction action;
	private WebServiceActionProcessor processor;

	@Before
	public void setUp() throws Exception {

		action = createWebServiceAction();

		GetMethod method = Mockito.mock(GetMethod.class);

		// Expectations
		Mockito.when(method.getStatusCode()).thenReturn(HttpStatus.SC_OK);
		Mockito.when(method.getParams()).thenReturn(new HttpMethodParams());
		Mockito.when(method.getHostAuthState()).thenReturn(new AuthState());
		Mockito.when(method.getProxyAuthState()).thenReturn(new AuthState());
		Mockito.when(method.getURI()).thenReturn(new URI("http://coachella.com", false));
		Mockito.when(method.getRequestHeaders(Mockito.anyString())).thenReturn(new Header[0]);
		Mockito.when(method.getResponseBodyAsString()).thenReturn(XMLUtil.docToString(webServiceReturnValue));

		HttpTransportProtocol protocol = new HttpTransportProtocol();

		protocol.setGetMethod(method);
		protocol.setWebServiceAction(action);

		processor = new WebServiceActionProcessor();
		processor.setTransportProtocol(protocol);
	}

	@Test(expected = WebServiceException.class)
	public void testThatExecuteThrowsExceptionOnInvalidInput() throws Exception {
		
		processor.execute(null);
	}

	@Test
	public void testThatExecuteDoesNotReturnNull() throws Exception {

		assertNotNull("Should not return null", processor.execute(action));
	}

	@Test
	public void testThatExecuteReturnsValidResultWithGroup() throws Exception {

		WebServiceResult result = (WebServiceResult) processor.execute(action);
		assertNotNull("The processor should return a valid result with group", result.getGroup());

	}

	@Test
	public void testThatExecuteReturnsValidResultWithCorrectGroup() throws Exception {

		WebServiceResult result = (WebServiceResult) processor.execute(action);
		assertEquals("The processor should return a valid result with the correct group", "Test-001", result.getGroup());

	}

	@Test
	public void testThatExecuteReturnsValidResultWithTreatment() throws Exception {

		WebServiceResult result = (WebServiceResult) processor.execute(action);
		assertNotNull("The processor should return a valid result with treatment", result.getTreatment());

	}

	@Test
	public void testThatExecuteReturnsValidResultWithCorrectTreatment() throws Exception {

		WebServiceResult result = (WebServiceResult) processor.execute(action);
		assertEquals("The processor should return a valid result with the correct treatment", "Treatment-001",
				result.getTreatment());

	}

	@Test
	public void testThatExecuteReturnsValidResultWithMessage() throws Exception {

		WebServiceResult result = (WebServiceResult) processor.execute(action);
		assertNotNull("The processor should return a valid result with treatment", result.getMessage());

	}

	@Test
	public void testThatExecuteReturnsValidResultWithCorrectMessage() throws Exception {

		WebServiceResult result = (WebServiceResult) processor.execute(action);
		assertEquals("The processor should return a valid result with the correct message",
				"Owe me like you owe your tax", result.getMessage());

	}

	@Test
	public void testThatExecuteReturnsValidResultWithDisplayTreatmentFlag() throws Exception {

		WebServiceResult result = (WebServiceResult) processor.execute(action);
		assertTrue("The processor should return a valid result with the correct message", result.isDisplayTreatment());
	}
}