package com.clinovo.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Locale;
import org.akaza.openclinica.dao.hibernate.ConfigurationDao;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;

public class ValidatorHelperTest {

	private Locale locale;
	private MockHttpServletRequest request;
	private ValidatorHelper validatorHelper1;
	private ValidatorHelper validatorHelper2;
	private ConfigurationDao configurationDao;

	@Before
	public void setUp() throws Exception {
		locale = new Locale("en");

		request = new MockHttpServletRequest();
		request.setPreferredLocales(Arrays.asList(new Locale[] { locale }));
		ResourceBundleProvider.updateLocale(request.getLocale());

		configurationDao = Mockito.mock(ConfigurationDao.class);

		validatorHelper1 = new ValidatorHelper(configurationDao, locale);
		validatorHelper1.setAttribute("attr1", true);

		validatorHelper2 = new ValidatorHelper(request, configurationDao);
		request.setAttribute("attr2", true);
		request.setParameter("param2", "test value");
	}

	@Test
	public void testLocaleForValidatorHelper1() {
		assertEquals(validatorHelper1.getLocale(), locale);
	}

	@Test
	public void testConfigurationDaoForValidatorHelper1() {
		assertEquals(validatorHelper1.getConfigurationDao(), configurationDao);
	}

	@Test
	public void testGetAttributeForValidatorHelper1() {
		assertTrue((Boolean) validatorHelper1.getAttribute("attr1"));
	}

	@Test
	public void testGetParameterForValidatorHelper1() {
		assertNull(validatorHelper1.getParameter("attr1"));
	}

	@Test
	public void testGetParameterValuesForValidatorHelper1() {
        assertEquals(validatorHelper1.getParameterValues("attr1").length, 1);
	}

	@Test
	public void testLocaleForValidatorHelper2() {
		assertEquals(validatorHelper2.getLocale(), request.getLocale());
	}

	@Test
	public void testConfigurationDaoForValidatorHelper2() {
		assertEquals(validatorHelper2.getConfigurationDao(), configurationDao);
	}

	@Test
	public void testGetAttributeForValidatorHelper2() {
		assertTrue((Boolean) validatorHelper2.getAttribute("attr2"));
	}

	@Test
	public void testGetParameterForValidatorHelper2() {
		assertEquals(validatorHelper2.getParameter("param2"), "test value");
	}

	@Test
	public void testGetParameterValuesForValidatorHelper2() {
		assertEquals(validatorHelper2.getParameterValues("param2").length, 1);
	}
}
