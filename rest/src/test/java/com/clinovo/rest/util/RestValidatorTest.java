package com.clinovo.rest.util;

import java.lang.reflect.Method;
import java.util.Locale;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.core.CoreResources;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;

import com.clinovo.rest.annotation.PossibleValues;
import com.clinovo.rest.annotation.PossibleValuesHolder;
import com.clinovo.rest.exception.RestException;
import com.clinovo.rest.model.UserDetails;
import com.clinovo.rest.security.PermissionChecker;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CoreResources.class, UserDetails.class, RequestContextHolder.class})
public class RestValidatorTest {

	public static final int METHOD_DEEP = 2;

	@Mock
	private HandlerMethod handler;

	@Mock
	private DataSource dataSource;

	private StudyBean currentStudy;

	@Mock
	private UserDetails userDetails;

	private MockHttpSession session;

	@Mock
	private MessageSource messageSource;

	private MockHttpServletRequest request;

	@Mock
	private ServletRequestAttributes servletRequestAttributes;

	@Before
	public void before() throws Exception {
		request = new MockHttpServletRequest();
		session = new MockHttpSession();
		request.setSession(session);
		request.addParameter("version", "2.2");
		request.addParameter("token", "D6489A37-74E7-46F1-AC11-EE36CA474184");

		Locale locale = Locale.ENGLISH;
		PowerMockito.mockStatic(CoreResources.class);
		PowerMockito.when(CoreResources.getSystemLocale()).thenReturn(locale);

		PowerMockito.mockStatic(RequestContextHolder.class);
		Whitebox.setInternalState(servletRequestAttributes, "request", request);
		PowerMockito.when(RequestContextHolder.currentRequestAttributes()).thenReturn(servletRequestAttributes);

		currentStudy = new StudyBean();
		currentStudy.setId(1);
		Mockito.when(userDetails.getStudyName()).thenReturn(currentStudy.getName());
		Mockito.when(userDetails.getCurrentStudy(dataSource)).thenReturn(currentStudy);
		session.setAttribute(PermissionChecker.API_AUTHENTICATED_USER_DETAILS, userDetails);
	}

	private Method getCurrentMethod() {
		Method currentMethod = null;
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		for (Method method : getClass().getDeclaredMethods()) {
			if (method.getName().equals(stackTraceElements[METHOD_DEEP].getMethodName())) {
				currentMethod = method;
				break;
			}
		}
		return currentMethod;
	}

	@Test(expected = RestException.class)
	@PossibleValuesHolder({
			@PossibleValues(name = "dataentryquality", values = "dde,evaluation", valueDescriptions = "dde -> Double Data Entry, evaluation -> CRF data evaluation")})
	public void testThatRequestParametersValidatorWillThrowRestExceptionIfParameterValueDoesNotCorrespondToRestParameterPossibleValuesAnnotation()
			throws Exception {
		request.setParameter("dataentryquality", "2");
		Mockito.when(handler.getMethod()).thenReturn(getCurrentMethod());
		RestValidator.validate(request, dataSource, messageSource, handler);
	}

	@Test
	@PossibleValuesHolder({
			@PossibleValues(name = "dataentryquality", values = "dde,evaluation", valueDescriptions = "dde -> Double Data Entry, evaluation -> CRF data evaluation")})
	public void testThatRequestParametersValidatorDoesNotThrowRestExceptionIfParameterValueCorrespondToRestParameterPossibleValuesAnnotation()
			throws Exception {
		request.setParameter("dataentryquality", "dde");
		Mockito.when(handler.getMethod()).thenReturn(getCurrentMethod());
		RestValidator.validate(request, dataSource, messageSource, handler);
	}

	@Test
	@PossibleValuesHolder({
			@PossibleValues(name = "dataentryquality", canBeNotSpecified = true, values = "dde,evaluation", valueDescriptions = "dde -> Double Data Entry, evaluation -> CRF data evaluation")})
	public void testThatRequestParametersValidatorDoesNotThrowRestExceptionIfParameterValueWesNotSpecifiedAndTheCanBeNotSpecifiedIsTrue()
			throws Exception {
		Mockito.when(handler.getMethod()).thenReturn(getCurrentMethod());
		RestValidator.validate(request, dataSource, messageSource, handler);
	}

	@Test(expected = RestException.class)
	@PossibleValuesHolder({
			@PossibleValues(name = "dataentryquality", values = "dde,evaluation", valueDescriptions = "dde -> Double Data Entry, evaluation -> CRF data evaluation")})
	public void testThatRequestParametersValidatorThrowsRestExceptionIfParameterValueWesNotSpecifiedAndTheCanBeNotSpecifiedIsFalse()
			throws Exception {
		Mockito.when(handler.getMethod()).thenReturn(getCurrentMethod());
		RestValidator.validate(request, dataSource, messageSource, handler);
	}

	@Test(expected = RestException.class)
	public void testThatRequestParametersValidatorThrowsRestExceptionIfVersionParameterIsEmpty() throws Exception {
		request.removeParameter("version");
		request.addParameter("version", "");
		RestValidator.validate(request, dataSource, messageSource, handler);
	}

	@Test(expected = RestException.class)
	public void testThatRequestParametersValidatorThrowsRestExceptionIfVersionParameterIsmissing() throws Exception {
		request.removeParameter("version");
		RestValidator.validate(request, dataSource, messageSource, handler);
	}
}
