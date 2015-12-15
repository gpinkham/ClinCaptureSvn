package com.clinovo.servlet;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import com.clinovo.model.RandomizationResult;
import com.clinovo.util.RandomizationUtil;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.service.StudyParameterConfig;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.mock.web.MockHttpServletRequest;

import com.clinovo.context.impl.JSONSubmissionContext;
import com.clinovo.exception.RandomizationException;
import com.clinovo.i18n.LocaleResolver;
import com.clinovo.rule.ext.HttpTransportProtocol;
import org.springframework.mock.web.MockHttpServletResponse;

@RunWith(PowerMockRunner.class)
@PrepareForTest({RandomizeServlet.class, HttpTransportProtocol.class, RandomizationUtil.class})
public class RandomizeServletTest {

	private RandomizeServlet randomizeServlet;
	private RandomizeServlet spy = PowerMockito.spy(new RandomizeServlet());

	private MockHttpServletRequest request;
	private MockHttpServletResponse response;

	private StudyBean study;

	@Before
	public void setUp() throws Exception {
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		RandomizationUtil randomizationUtil = PowerMockito.mock(RandomizationUtil.class);
		PowerMockito.mockStatic(RandomizationUtil.class);
		randomizeServlet = PowerMockito.mock(RandomizeServlet.class);
		JSONSubmissionContext jsonSubmissionContext = PowerMockito.mock(JSONSubmissionContext.class);
		HttpTransportProtocol httpTransportProtocol = PowerMockito.mock(HttpTransportProtocol.class);

		study = new StudyBean();
		study.setId(1);
		study.setStudyParameterConfig(new StudyParameterConfig());
		study.setParentStudyId(2);
		study.setIdentifier("Test Study");
		PowerMockito.when(randomizeServlet.getCurrentStudy(request)).thenReturn(study);
		PowerMockito.when(randomizeServlet.getSite(study)).thenReturn("testId");
		PowerMockito
				.when(randomizeServlet,
						PowerMockito.method(RandomizeServlet.class, "initiateRandomizationCall",
								HttpServletRequest.class)).withArguments(request).thenCallRealMethod();
		PowerMockito
				.when(randomizationUtil,
						PowerMockito.method(RandomizationUtil.class, "getStudySubjectBean",
								HttpServletRequest.class)).withArguments(request).thenReturn(new StudySubjectBean());
		PowerMockito
				.when(randomizationUtil,
						PowerMockito.method(RandomizationUtil.class, "isCRFSpecifiedTrialIdValid",
								String.class)).withArguments(Mockito.anyString()).thenCallRealMethod();
		PowerMockito
				.when(randomizationUtil,
						PowerMockito.method(RandomizationUtil.class, "isConfiguredTrialIdValid",
								String.class)).withArguments(Mockito.anyString()).thenCallRealMethod();
		PowerMockito
				.when(randomizationUtil,
						PowerMockito.method(RandomizationUtil.class, "isTrialIdDoubleConfigured",
								String.class, String.class)).withArguments(Mockito.anyString(), Mockito.anyString()).thenCallRealMethod();
		PowerMockito.whenNew(JSONSubmissionContext.class).withNoArguments().thenReturn(jsonSubmissionContext);
		PowerMockito.whenNew(HttpTransportProtocol.class).withNoArguments().thenReturn(httpTransportProtocol);

		Locale locale = Locale.ENGLISH;
		LocaleResolver.updateLocale(request, locale);
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

	@Test
	public void testThatAuditLogWillBeWrittenIfExceptionWasThrownWhileRandomization() throws Exception{
		request.setParameter("trialId", "");
		try {
			spy.processRequest(request, response);
		} finally {
			Mockito.verify(spy, Mockito.times(1)).saveRandomizationAuditLog(Mockito.any(HttpServletRequest.class), Mockito.any(RandomizationResult.class), Mockito.any(Exception.class));
		}
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
