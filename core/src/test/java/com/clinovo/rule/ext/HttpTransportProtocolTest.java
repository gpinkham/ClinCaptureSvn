package com.clinovo.rule.ext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

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
import com.clinovo.util.XMLUtil;

public class HttpTransportProtocolTest extends BaseTest {

	private GetMethod method;
	private HttpTransportProtocol protocol;

	@Before
	public void setUp() throws Exception {

		WebServiceAction action = createWebServiceAction();

		method = Mockito.mock(GetMethod.class);

		// Expectations
		Mockito.when(method.getStatusCode()).thenReturn(HttpStatus.SC_OK);
		Mockito.when(method.getParams()).thenReturn(new HttpMethodParams());
		Mockito.when(method.getHostAuthState()).thenReturn(new AuthState());
		Mockito.when(method.getProxyAuthState()).thenReturn(new AuthState());
		Mockito.when(method.getURI()).thenReturn(new URI("http://coachella.com", false));
		Mockito.when(method.getRequestHeaders(Mockito.anyString())).thenReturn(new Header[0]);
		Mockito.when(method.getResponseBodyAsString()).thenReturn(XMLUtil.docToString(webServiceReturnValue));

		protocol = new HttpTransportProtocol();
		protocol.setGetMethod(method);
		protocol.setWebServiceAction(action);

	}

	@Test(expected = WebServiceException.class)
	public void testThatCallThrowsExceptionWhenCalledWithInvalidInput() throws Exception {

		protocol.setWebServiceAction(null);
		protocol.call();
	}
	
	public void testThatCallReturnsCorrectFalseFlagForDisplayTreatment() throws Exception {

		protocol.setWebServiceAction(null);
		WebServiceResult result = protocol.call();
		
		assertFalse("The display treatment flag should be false if the input is invalid", result.isDisplayTreatment());
	}

	@Test(expected = WebServiceException.class)
	public void testThatFailedHttpCallRaisesWebServiceException() throws Exception {
		
		String failureMessage = "<result><message>Respect other people's security you tard</message></result>";
		
		Mockito.when(method.getStatusCode()).thenReturn(HttpStatus.SC_FORBIDDEN);
		Mockito.when(method.getResponseBodyAsString()).thenReturn(failureMessage);
		
		protocol.call();
	}
	
	@Test(expected = WebServiceException.class)
	public void testThatUnVailableHttpReturnCodeHttpCallRaisesWebServiceException() throws Exception {
		
		String failureMessage = "<result><message>The randomization service is down</message></result>";
		
		Mockito.when(method.getResponseBodyAsString()).thenReturn(failureMessage);
		Mockito.when(method.getStatusCode()).thenReturn(HttpStatus.SC_SERVICE_UNAVAILABLE);
		
		protocol.call();
	}

	@Test
	public void testThatCallDoesNotReturnNull() throws Exception {

		WebServiceResult result = protocol.call();
		assertNotNull("Should never return null", result);
	}
	
	@Test
	public void testThatCallReturnsValidWebServiceResultWithGroup() throws Exception {
		
		WebServiceResult result = protocol.call();
		
		assertNotNull("Should have a valid group specified", result.getGroup());
	}
	
	@Test
	public void testThatCallReturnsValidWebServiceResultWithACorrectGroup() throws Exception {
		
		WebServiceResult result = protocol.call();
		
		assertEquals("Should have a correct group specified", "Test-001", result.getGroup());
	}
	
	@Test
	public void testThatCallReturnsValidWebServiceResultWithMessage() throws Exception {
		
		WebServiceResult result = protocol.call();
		
		assertNotNull("Should have a valid message specified", result.getMessage());
	}
	
	@Test
	public void testThatCallReturnsValidWebServiceResultWithACorrectMessage() throws Exception {
		
		WebServiceResult result = protocol.call();
		
		assertEquals("Should have a correct message specified", "Owe me like you owe your tax", result.getMessage());
	}
	
	@Test
	public void testThatCallReturnsValidWebServiceResultWithTreatment() throws Exception {
		
		WebServiceResult result = protocol.call();
		
		assertNotNull("Should have a valid treatment specified", result.getTreatment());
	}
	
	@Test
	public void testThatCallReturnsValidWebServiceResultWithACorrectTreatment() throws Exception {
		
		WebServiceResult result = protocol.call();
		
		assertEquals("Should have a correct treatment specified", "Treatment-001", result.getTreatment());
	}
	
	@Test
	public void testThatCallReturnsValidWebServiceResultWithDisplayTreatmentFlag() throws Exception {
		
		WebServiceResult result = protocol.call();
		
		assertTrue("Should have a valid display treatment flag specified", result.isDisplayTreatment());
	}
}