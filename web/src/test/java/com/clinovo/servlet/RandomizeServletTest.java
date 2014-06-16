package com.clinovo.servlet;

import com.clinovo.context.impl.JSONSubmissionContext;
import com.clinovo.exception.RandomizationException;
import com.clinovo.rule.ext.HttpTransportProtocol;

import org.akaza.openclinica.dao.core.CoreResources;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ RandomizeServlet.class, CoreResources.class, HttpTransportProtocol.class })
public class RandomizeServletTest {

	private RandomizeServlet randomizeServlet;

	private JSONSubmissionContext jsonSubmissionContext;

	private MockHttpServletRequest request;

	private HttpTransportProtocol httpTransportProtocol;

	@Before
	public void setUp() throws Exception {

		request = new MockHttpServletRequest();

		randomizeServlet = PowerMockito.mock(RandomizeServlet.class);
		jsonSubmissionContext = PowerMockito.mock(JSONSubmissionContext.class);
		httpTransportProtocol = PowerMockito.mock(HttpTransportProtocol.class);

		PowerMockito.mockStatic(CoreResources.class);

		PowerMockito.when(randomizeServlet, PowerMockito.method(RandomizeServlet.class, "initiateRandomizationCall",
								HttpServletRequest.class)).withArguments(request).thenCallRealMethod();

		PowerMockito.whenNew(JSONSubmissionContext.class).withNoArguments().thenReturn(jsonSubmissionContext);
		PowerMockito.whenNew(HttpTransportProtocol.class).withNoArguments().thenReturn(httpTransportProtocol);
	}

	@Test(expected = RandomizationException.class)
	public void testThatExceptionWillBeThrownIfTrialIdIsConfiguredInCRFAndInDatainfo() throws Exception {
		PowerMockito.when(CoreResources.getField("randomizationTrialId")).thenReturn("132");
		request.setParameter("trialId", "123");
		Whitebox.invokeMethod(randomizeServlet, "initiateRandomizationCall", request);
	}

	@Test(expected = RandomizationException.class)
	public void testThatExceptionWillBeThrownIfTrialIdIsNotConfigured() throws Exception {
		PowerMockito.when(CoreResources.getField("randomizationTrialId")).thenReturn("");
		request.setParameter("trialId", "");
		Whitebox.invokeMethod(randomizeServlet, "initiateRandomizationCall", request);
	}

	@Test
	public void testThatInitiateRandomizationCallDontThrowAnExceptionIfSubjectIsRandomizedTwice() throws Exception {
		PowerMockito.when(CoreResources.getField("randomizationTrialId")).thenReturn("123");
		request.setParameter("trialId", "");
		request.setParameter("strataLevel", "null");
		request.setParameter("subject", "TEST-001");
		request.getSession().setAttribute("randomizationEnviroment", "test");
		Whitebox.invokeMethod(randomizeServlet, "initiateRandomizationCall", request);
		Whitebox.invokeMethod(randomizeServlet, "initiateRandomizationCall", request);
	}
}
