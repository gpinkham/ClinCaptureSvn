package com.clinovo.rest.validator;

import java.util.Locale;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.dao.core.CoreResources;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.clinovo.rest.exception.RestException;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CoreResources.class, RequestContextHolder.class})
public class EventServiceValidatorTest {

	private StudyBean currentStudy;

	private MockHttpSession session;

	private MockHttpServletRequest request;

	private StudyEventDefinitionBean studyEventDefinitionBean;

	@Mock
	private MessageSource messageSource;

	@Mock
	private ServletRequestAttributes servletRequestAttributes;

	@Before
	public void before() throws Exception {
		currentStudy = new StudyBean();
		currentStudy.setId(1);

		studyEventDefinitionBean = new StudyEventDefinitionBean();
		studyEventDefinitionBean.setId(1);
		studyEventDefinitionBean.setStudyId(1);

		request = new MockHttpServletRequest();
		session = new MockHttpSession();
		request.setSession(session);

		Locale locale = Locale.ENGLISH;
		PowerMockito.mockStatic(CoreResources.class);
		PowerMockito.when(CoreResources.getSystemLocale()).thenReturn(locale);

		PowerMockito.mockStatic(RequestContextHolder.class);
		Whitebox.setInternalState(servletRequestAttributes, "request", request);
		PowerMockito.when(RequestContextHolder.currentRequestAttributes()).thenReturn(servletRequestAttributes);

	}

	@Test
	public void testThatValidateStudyEventDefinitionDoesNotThrowAnyException() throws Exception {
		EventServiceValidator.validateStudyEventDefinition(messageSource, 1, studyEventDefinitionBean, currentStudy);
	}

	@Test(expected = RestException.class)
	public void testThatValidateStudyEventDefinitionThrowsAnExceptionIfStudyEventDefinitionIdIsZero() throws Exception {
		EventServiceValidator.validateStudyEventDefinition(messageSource, 1, new StudyEventDefinitionBean(),
				currentStudy);
	}

	@Test(expected = RestException.class)
	public void testThatValidateStudyEventDefinitionThrowsAnExceptionIfStudyEventDefinitionStudyIdBelongsToAnotherScope()
			throws Exception {
		studyEventDefinitionBean.setStudyId(88);
		EventServiceValidator.validateStudyEventDefinition(messageSource, 1, studyEventDefinitionBean, currentStudy);
	}

}
