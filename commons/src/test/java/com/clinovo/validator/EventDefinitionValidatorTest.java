package com.clinovo.validator;

import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.hibernate.ConfigurationDao;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.clinovo.i18n.LocaleResolver;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RequestContextHolder.class)
public class EventDefinitionValidatorTest {

	private MockHttpSession session;

	private MockHttpServletRequest request;

	private StudyBean studyBean;

	@Mock
	private ResourceBundle resourceBundle;

	@Mock
	private UserAccountDAO userAccountDao;

	@Mock
	private ConfigurationDao configurationDao;

	@Mock
	private ServletRequestAttributes servletRequestAttributes;

	private boolean lowerCaseParameterNames;

	private String getSymbols(int size) {
		String result = "";
		for (int i = 1; i <= size; i++) {
			result = result.concat("a");
		}
		return result;
	}

	@Before
	public void setUp() throws Exception {
		lowerCaseParameterNames = false;

		request = new MockHttpServletRequest();
		session = new MockHttpSession();
		request.setSession(session);

		studyBean = new StudyBean();
		studyBean.setId(1);

		Locale locale = new Locale("en");
		LocaleResolver.updateLocale(request, locale);
		ResourceBundleProvider.updateLocale(LocaleResolver.getLocale(request));

		PowerMockito.mockStatic(RequestContextHolder.class);
		Whitebox.setInternalState(servletRequestAttributes, "request", request);
		PowerMockito.when(RequestContextHolder.currentRequestAttributes()).thenReturn(servletRequestAttributes);

		request.setParameter("type", "scheduled");
		request.setParameter("name", "test name");
		request.setParameter("description", "");
		request.setParameter("category", "");
		request.setParameter("schDay", "0");
		request.setParameter("maxDay", "0");
		request.setParameter("minDay", "0");
		request.setParameter("emailDay", "0");
		request.setParameter("emailUser", "root");
		request.setParameter("isReference", "false");
	}

	@Test
	public void testThatWrongTypeProducesError() {
		request.setParameter("type", "scheduledX");
		assertTrue(EventDefinitionValidator
				.validate(configurationDao, userAccountDao, studyBean, lowerCaseParameterNames).size() == 1);
	}

	@Test
	public void testThatNameShouldNotBeBlank() {
		request.setParameter("name", "");
		assertTrue(EventDefinitionValidator
				.validate(configurationDao, userAccountDao, studyBean, lowerCaseParameterNames).size() == 1);
	}

	@Test
	public void testThatDescriptionMayHave2000Symbols() {
		request.setParameter("emailUser", "");
		request.setParameter("description", getSymbols(2000));
		assertTrue(EventDefinitionValidator
				.validate(configurationDao, userAccountDao, studyBean, lowerCaseParameterNames).size() == 0);
	}

	@Test
	public void testThatDescriptionCannotHaveMoreThan2000Symbols() {
		request.setParameter("description", getSymbols(2001));
		assertTrue(EventDefinitionValidator
				.validate(configurationDao, userAccountDao, studyBean, lowerCaseParameterNames).size() == 1);
	}

	@Test
	public void testThatCategoryMayHave2000Symbols() {
		request.setParameter("emailUser", "");
		request.setParameter("category", getSymbols(2000));
		assertTrue(EventDefinitionValidator
				.validate(configurationDao, userAccountDao, studyBean, lowerCaseParameterNames).size() == 0);
	}

	@Test
	public void testThatCategoryCannotHaveMoreThan2000Symbols() {
		request.setParameter("category", getSymbols(2001));
		assertTrue(EventDefinitionValidator
				.validate(configurationDao, userAccountDao, studyBean, lowerCaseParameterNames).size() == 1);
	}

	@Test
	public void testThatMinDayIsRequiredForCalendaredEvent() {
		request.setParameter("type", "calendared_visit");
		request.removeParameter("minDay");
		assertTrue(EventDefinitionValidator
				.validate(configurationDao, userAccountDao, studyBean, lowerCaseParameterNames).size() == 1);
	}

	@Test
	public void testThatMinDayShouldBeANumberForCalendaredEvent() {
		request.setParameter("type", "calendared_visit");
		request.setParameter("minDay", "aaa");
		assertTrue(EventDefinitionValidator
				.validate(configurationDao, userAccountDao, studyBean, lowerCaseParameterNames).size() == 1);
	}

	@Test
	public void testThatMaxDayIsRequiredForCalendaredEvent() {
		request.setParameter("type", "calendared_visit");
		request.removeParameter("maxDay");
		assertTrue(EventDefinitionValidator
				.validate(configurationDao, userAccountDao, studyBean, lowerCaseParameterNames).size() == 1);
	}

	@Test
	public void testThatMaxDayShouldBeANumberForCalendaredEvent() {
		request.setParameter("type", "calendared_visit");
		request.setParameter("maxDay", "aaa");
		assertTrue(EventDefinitionValidator
				.validate(configurationDao, userAccountDao, studyBean, lowerCaseParameterNames).size() == 1);
	}

	@Test
	public void testThatSchDayIsRequiredForCalendaredEvent() {
		request.setParameter("type", "calendared_visit");
		request.removeParameter("schDay");
		assertTrue(EventDefinitionValidator
				.validate(configurationDao, userAccountDao, studyBean, lowerCaseParameterNames).size() == 1);
	}

	@Test
	public void testThatSchDayShouldBeANumberForCalendaredEvent() {
		request.setParameter("type", "calendared_visit");
		request.setParameter("schDay", "aaa");
		assertTrue(EventDefinitionValidator
				.validate(configurationDao, userAccountDao, studyBean, lowerCaseParameterNames).size() == 1);
	}

	@Test
	public void testThatMaxDayShouldBeGreaterThanOrEqualToSchDay() {
		request.setParameter("type", "calendared_visit");
		request.setParameter("schDay", "5");
		request.setParameter("maxDay", "4");
		assertTrue(EventDefinitionValidator
				.validate(configurationDao, userAccountDao, studyBean, lowerCaseParameterNames).size() == 1);
	}

	@Test
	public void testThatMinShouldBeLessThanOrEqualToSchDay() {
		request.setParameter("type", "calendared_visit");
		request.setParameter("minDay", "4");
		assertTrue(EventDefinitionValidator
				.validate(configurationDao, userAccountDao, studyBean, lowerCaseParameterNames).size() == 1);
	}

	@Test
	public void testThatMinDayShouldBeLessThanOrEqualToMaxDay() {
		request.setParameter("type", "calendared_visit");
		request.setParameter("minDay", "7");
		assertTrue(EventDefinitionValidator
				.validate(configurationDao, userAccountDao, studyBean, lowerCaseParameterNames).size() == 1);
	}

	@Test
	public void testThatEmailDayShouldBeLessThanOrEqualToSchDay() {
		request.setParameter("type", "calendared_visit");
		request.setParameter("emailDay", "7");
		assertTrue(EventDefinitionValidator
				.validate(configurationDao, userAccountDao, studyBean, lowerCaseParameterNames).size() == 1);
	}

	@Test
	public void testThatErrorIsProducedForCalendaredEventThatIsReferenceEvenIfUserDoesNotExsist() {
		request.setParameter("type", "calendared_visit");
		request.setParameter("emailUser", "test_user".concat(Long.toString(new Date().getTime())));
		assertTrue(EventDefinitionValidator
				.validate(configurationDao, userAccountDao, studyBean, lowerCaseParameterNames).size() == 1);
	}

	@Test
	public void testThatMaxDayIsNotUsedForReferencedCalendaredEvents() {
		request.setParameter("type", "calendared_visit");
		request.setParameter("isReference", "true");
		request.setParameter("maxDay", "1");
		assertTrue(EventDefinitionValidator
				.validate(configurationDao, userAccountDao, studyBean, lowerCaseParameterNames).size() == 1);
	}

	@Test
	public void testThatMinDayIsNotUsedForReferencedCalendaredEvents() {
		request.setParameter("type", "calendared_visit");
		request.setParameter("isReference", "true");
		request.setParameter("minDay", "1");
		assertTrue(EventDefinitionValidator
				.validate(configurationDao, userAccountDao, studyBean, lowerCaseParameterNames).size() == 1);
	}

	@Test
	public void testThatSchDayIsNotUsedForReferencedCalendaredEvents() {
		request.setParameter("type", "calendared_visit");
		request.setParameter("isReference", "true");
		request.setParameter("schDay", "1");
		assertTrue(EventDefinitionValidator
				.validate(configurationDao, userAccountDao, studyBean, lowerCaseParameterNames).size() == 1);
	}

	@Test
	public void testThatEmailDayIsNotUsedForReferencedCalendaredEvents() {
		request.setParameter("type", "calendared_visit");
		request.setParameter("isReference", "true");
		request.setParameter("emailDay", "1");
		assertTrue(EventDefinitionValidator
				.validate(configurationDao, userAccountDao, studyBean, lowerCaseParameterNames).size() == 1);
	}

	@Test
	public void testThatUserNameIsNotUsedForReferencedCalendaredEvents() {
		request.setParameter("type", "calendared_visit");
		request.setParameter("isReference", "true");
		request.setParameter("emailUser", "root");
		assertTrue(EventDefinitionValidator
				.validate(configurationDao, userAccountDao, studyBean, lowerCaseParameterNames).size() == 1);
	}

	@Test
	public void testThatCalendaredEventCannotBeRepeating() {
		request.setParameter("type", "calendared_visit");
		request.setParameter("repeating", "true");
		assertTrue(EventDefinitionValidator
				.validate(configurationDao, userAccountDao, studyBean, lowerCaseParameterNames).size() == 1);
	}

	@Test
	public void testThatNotCalendaredEventCannotBeReferenced() {
		request.setParameter("type", "scheduled");
		request.setParameter("isReference", "true");
		assertTrue(EventDefinitionValidator
				.validate(configurationDao, userAccountDao, studyBean, lowerCaseParameterNames).size() == 1);
	}

	@Test
	public void testThatMaxDayIsUsedForCalendaredEventsOnly() {
		request.setParameter("type", "scheduled");
		request.setParameter("maxDay", "1");
		assertTrue(EventDefinitionValidator
				.validate(configurationDao, userAccountDao, studyBean, lowerCaseParameterNames).size() == 1);
	}

	@Test
	public void testThatMinDayIsUsedForCalendaredEventsOnly() {
		request.setParameter("type", "scheduled");
		request.setParameter("minDay", "1");
		assertTrue(EventDefinitionValidator
				.validate(configurationDao, userAccountDao, studyBean, lowerCaseParameterNames).size() == 1);
	}

	@Test
	public void testThatSchDayIsUsedForCalendaredEventsOnly() {
		request.setParameter("type", "scheduled");
		request.setParameter("schDay", "1");
		assertTrue(EventDefinitionValidator
				.validate(configurationDao, userAccountDao, studyBean, lowerCaseParameterNames).size() == 1);
	}

	@Test
	public void testThatEmailDayIsUsedForCalendaredEventsOnly() {
		request.setParameter("type", "scheduled");
		request.setParameter("emailDay", "1");
		assertTrue(EventDefinitionValidator
				.validate(configurationDao, userAccountDao, studyBean, lowerCaseParameterNames).size() == 1);
	}

	@Test
	public void testThatUserNameIsUsedForCalendaredEventsOnly() {
		request.setParameter("type", "scheduled");
		request.setParameter("emailUser", "root");
		assertTrue(EventDefinitionValidator
				.validate(configurationDao, userAccountDao, studyBean, lowerCaseParameterNames).size() == 1);
	}
}
