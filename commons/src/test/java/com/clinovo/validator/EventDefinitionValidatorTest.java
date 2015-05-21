package com.clinovo.validator;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.dao.hibernate.ConfigurationDao;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import com.clinovo.i18n.LocaleResolver;

@SuppressWarnings("static-access")
public class EventDefinitionValidatorTest {

	private MockHttpSession session;

	private MockHttpServletRequest request;

	private EventDefinitionValidator eventDefinitionValidator;

	@Mock
	private ResourceBundle resourceBundle;

	@Mock
	private ConfigurationDao configurationDao;

	private ArrayList<StudyUserRoleBean> studyUserRoleBeanList;

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
		studyUserRoleBeanList = new ArrayList<StudyUserRoleBean>();
		eventDefinitionValidator = new EventDefinitionValidator();
		request = new MockHttpServletRequest();
		session = new MockHttpSession();
		request.setSession(session);

		Locale locale = new Locale("en");
		LocaleResolver.updateLocale(request, locale);
		ResourceBundleProvider.updateLocale(LocaleResolver.getLocale(request));

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
		assertTrue(eventDefinitionValidator.validate(request, configurationDao, studyUserRoleBeanList,
				lowerCaseParameterNames).size() == 1);
	}

	@Test
	public void testThatNameShouldNotBeBlank() {
		request.setParameter("name", "");
		assertTrue(eventDefinitionValidator.validate(request, configurationDao, studyUserRoleBeanList,
				lowerCaseParameterNames).size() == 1);
	}

	@Test
	public void testThatDescriptionMayHave2000Symbols() {
		request.setParameter("description", getSymbols(2000));
		assertTrue(eventDefinitionValidator.validate(request, configurationDao, studyUserRoleBeanList,
				lowerCaseParameterNames).size() == 0);
	}

	@Test
	public void testThatDescriptionCannotHaveMoreThan2000Symbols() {
		request.setParameter("description", getSymbols(2001));
		assertTrue(eventDefinitionValidator.validate(request, configurationDao, studyUserRoleBeanList,
				lowerCaseParameterNames).size() == 1);
	}

	@Test
	public void testThatCategoryMayHave2000Symbols() {
		request.setParameter("category", getSymbols(2000));
		assertTrue(eventDefinitionValidator.validate(request, configurationDao, studyUserRoleBeanList,
				lowerCaseParameterNames).size() == 0);
	}

	@Test
	public void testThatCategoryCannotHaveMoreThan2000Symbols() {
		request.setParameter("category", getSymbols(2001));
		assertTrue(eventDefinitionValidator.validate(request, configurationDao, studyUserRoleBeanList,
				lowerCaseParameterNames).size() == 1);
	}

	@Test
	public void testThatMinDayIsRequiredForCalendaredEvent() {
		request.setParameter("type", "calendared_visit");
		request.removeParameter("minDay");
		assertTrue(eventDefinitionValidator.validate(request, configurationDao, studyUserRoleBeanList,
				lowerCaseParameterNames).size() == 1);
	}

	@Test
	public void testThatMinDayShouldBeANumberForCalendaredEvent() {
		request.setParameter("type", "calendared_visit");
		request.setParameter("minDay", "aaa");
		assertTrue(eventDefinitionValidator.validate(request, configurationDao, studyUserRoleBeanList,
				lowerCaseParameterNames).size() == 1);
	}

	@Test
	public void testThatMaxDayIsRequiredForCalendaredEvent() {
		request.setParameter("type", "calendared_visit");
		request.removeParameter("maxDay");
		assertTrue(eventDefinitionValidator.validate(request, configurationDao, studyUserRoleBeanList,
				lowerCaseParameterNames).size() == 1);
	}

	@Test
	public void testThatMaxDayShouldBeANumberForCalendaredEvent() {
		request.setParameter("type", "calendared_visit");
		request.setParameter("maxDay", "aaa");
		assertTrue(eventDefinitionValidator.validate(request, configurationDao, studyUserRoleBeanList,
				lowerCaseParameterNames).size() == 1);
	}

	@Test
	public void testThatSchDayIsRequiredForCalendaredEvent() {
		request.setParameter("type", "calendared_visit");
		request.removeParameter("schDay");
		assertTrue(eventDefinitionValidator.validate(request, configurationDao, studyUserRoleBeanList,
				lowerCaseParameterNames).size() == 1);
	}

	@Test
	public void testThatSchDayShouldBeANumberForCalendaredEvent() {
		request.setParameter("type", "calendared_visit");
		request.setParameter("schDay", "aaa");
		assertTrue(eventDefinitionValidator.validate(request, configurationDao, studyUserRoleBeanList,
				lowerCaseParameterNames).size() == 1);
	}

	@Test
	public void testThatMaxDayShouldBeGreaterThanOrEqualToSchDay() {
		request.setParameter("type", "calendared_visit");
		request.setParameter("schDay", "5");
		request.setParameter("maxDay", "4");
		assertTrue(eventDefinitionValidator.validate(request, configurationDao, studyUserRoleBeanList,
				lowerCaseParameterNames).size() == 1);
	}

	@Test
	public void testThatMinShouldBeLessThanOrEqualToSchDay() {
		request.setParameter("type", "calendared_visit");
		request.setParameter("minDay", "4");
		assertTrue(eventDefinitionValidator.validate(request, configurationDao, studyUserRoleBeanList,
				lowerCaseParameterNames).size() == 1);
	}

	@Test
	public void testThatMinDayShouldBeLessThanOrEqualToMaxDay() {
		request.setParameter("type", "calendared_visit");
		request.setParameter("minDay", "7");
		assertTrue(eventDefinitionValidator.validate(request, configurationDao, studyUserRoleBeanList,
				lowerCaseParameterNames).size() == 1);
	}

	@Test
	public void testThatEmailDayShouldBeLessThanOrEqualToSchDay() {
		request.setParameter("type", "calendared_visit");
		request.setParameter("emailDay", "7");
		assertTrue(eventDefinitionValidator.validate(request, configurationDao, studyUserRoleBeanList,
				lowerCaseParameterNames).size() == 1);
	}

	@Test
	public void testThatErrorIsProducedForCalendaredEventThatIsReferenceEvenIfUserDoesNotExsist() {
		request.setParameter("type", "calendared_visit");
		request.setParameter("emailUser", "test_user".concat(Long.toString(new Date().getTime())));
		assertTrue(eventDefinitionValidator.validate(request, configurationDao, studyUserRoleBeanList,
				lowerCaseParameterNames).size() == 1);
	}
}
