package com.clinovo.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.akaza.openclinica.dao.hibernate.ConfigurationDao;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;

import com.clinovo.i18n.LocaleResolver;

public class ValidatorHelperTest {

	private Locale locale;
	private MockHttpServletRequest request;
	private ValidatorHelper notRequestBasedValidatorHelper;
	private ValidatorHelper requestBasedValidatorHelper;
	private ConfigurationDao configurationDao;

	@Before
	public void setUp() throws Exception {
		locale = Locale.ENGLISH;
		request = new MockHttpServletRequest();
		LocaleResolver.updateLocale(request, locale);
		ResourceBundleProvider.updateLocale(LocaleResolver.getLocale(request));

		configurationDao = Mockito.mock(ConfigurationDao.class);

		notRequestBasedValidatorHelper = new ValidatorHelper(configurationDao, locale);
		notRequestBasedValidatorHelper.setAttribute("attr1", true);

		requestBasedValidatorHelper = new ValidatorHelper(request, configurationDao);
		request.setAttribute("attr2", true);
		request.setParameter("param2", "test value");
	}

	@Test
	public void testThatNotRequestBasedValidatorHelperReturnsCorrectLocale() {
		assertEquals(notRequestBasedValidatorHelper.getLocale(), locale);
	}

	@Test
	public void testThatNotRequestBasedValidatorHelperReturnsCorrectConfigurationDao() {
		assertEquals(notRequestBasedValidatorHelper.getConfigurationDao(), configurationDao);
	}

	@Test
	public void testThatGetAttributeFromNotRequestBasedValidatorHelperReturnsCorrectValue() {
		assertTrue((Boolean) notRequestBasedValidatorHelper.getAttribute("attr1"));
	}

	@Test
	public void testThatGetParameterFromNotRequestBasedValidatorHelperReturnsNull() {
		assertNull(notRequestBasedValidatorHelper.getParameter("attr1"));
	}

	@Test
	public void testThatGetParameterValuesFromNotRequestBasedValidatorHelperReturnsCorrectLength() {
		assertEquals(notRequestBasedValidatorHelper.getParameterValues("attr1").length, 1);
	}

	@Test
	public void testThatRequestBasedValidatorHelperReturnsCorrectLocale() {
		assertEquals(requestBasedValidatorHelper.getLocale(), LocaleResolver.getLocale(request));
	}

	@Test
	public void testThatRequestBasedValidatorHelperReturnsCorrectConfigurationDao() {
		assertEquals(requestBasedValidatorHelper.getConfigurationDao(), configurationDao);
	}

	@Test
	public void testThatGetAttributeFromRequestBasedValidatorHelperReturnsCorrectValue() {
		assertTrue((Boolean) requestBasedValidatorHelper.getAttribute("attr2"));
	}

	@Test
	public void testThatGetParameterFromRequestBasedValidatorHelperReturnsCorrectValue() {
		assertEquals(requestBasedValidatorHelper.getParameter("param2"), "test value");
	}

	@Test
	public void testThatGetParameterValuesFromRequestBasedValidatorHelperReturnsCorrectLength() {
		assertEquals(requestBasedValidatorHelper.getParameterValues("param2").length, 1);
	}
}
