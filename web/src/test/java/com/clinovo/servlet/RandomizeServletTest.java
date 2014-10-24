package com.clinovo.servlet;

import javax.servlet.http.HttpServletRequest;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.service.StudyParameterConfig;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.mock.web.MockHttpServletRequest;

import com.clinovo.context.impl.JSONSubmissionContext;
import com.clinovo.exception.RandomizationException;
import com.clinovo.rule.ext.HttpTransportProtocol;

import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ RandomizeServlet.class, HttpTransportProtocol.class })
public class RandomizeServletTest {

	private RandomizeServlet randomizeServlet;

	private JSONSubmissionContext jsonSubmissionContext;

	private MockHttpServletRequest request;

	private HttpTransportProtocol httpTransportProtocol;

	private StudyBean study;

	@Before
	public void setUp() throws Exception {
		request = new MockHttpServletRequest();
		randomizeServlet = PowerMockito.mock(RandomizeServlet.class);
		jsonSubmissionContext = PowerMockito.mock(JSONSubmissionContext.class);
		httpTransportProtocol = PowerMockito.mock(HttpTransportProtocol.class);

		study = new StudyBean();
		study.setId(1);
		study.setStudyParameterConfig(new StudyParameterConfig());
		PowerMockito.when(randomizeServlet.getCurrentStudy(request)).thenReturn(study);
		PowerMockito
				.when(randomizeServlet,
						PowerMockito.method(RandomizeServlet.class, "initiateRandomizationCall",
								HttpServletRequest.class)).withArguments(request).thenCallRealMethod();
		PowerMockito.whenNew(JSONSubmissionContext.class).withNoArguments().thenReturn(jsonSubmissionContext);
		PowerMockito.whenNew(HttpTransportProtocol.class).withNoArguments().thenReturn(httpTransportProtocol);

		Locale locale = new Locale("en");
		request.setPreferredLocales(Arrays.asList(locale));
		ResourceBundleProvider.updateLocale(locale);
		ResourceBundle resexception = ResourceBundleProvider.getExceptionsBundle(locale);
		org.mockito.internal.util.reflection.Whitebox.setInternalState(randomizeServlet, "resexception", resexception);
	}

	@Test(expected = RandomizationException.class)
	public void testThatExceptionWillBeThrownIfTrialIdIsConfiguredInCRFAndInStudyParams() throws Exception {
		study.getStudyParameterConfig().setRandomizationTrialId("123");
		request.setParameter("trialId", "123");
		Whitebox.invokeMethod(randomizeServlet, "initiateRandomizationCall", request);
	}

	@Test(expected = RandomizationException.class)
	public void testThatExceptionWillBeThrownIfTrialIdIsNotConfigured() throws Exception {
		study.getStudyParameterConfig().setRandomizationTrialId("");
		request.setParameter("trialId", "");
		Whitebox.invokeMethod(randomizeServlet, "initiateRandomizationCall", request);
	}

	@Test
	public void testThatInitiateRandomizationCallDontThrowAnExceptionIfSubjectIsRandomizedTwice() throws Exception {
		study.getStudyParameterConfig().setRandomizationTrialId("123");
		request.setParameter("trialId", "");
		request.setParameter("strataLevel", "null");
		request.setParameter("subject", "TEST-001");
		request.getSession().setAttribute("randomizationEnviroment", "test");
		Whitebox.invokeMethod(randomizeServlet, "initiateRandomizationCall", request);
		Whitebox.invokeMethod(randomizeServlet, "initiateRandomizationCall", request);
	}
}