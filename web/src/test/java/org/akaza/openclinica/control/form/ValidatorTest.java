package org.akaza.openclinica.control.form;

import com.clinovo.util.ValidatorHelper;

import java.util.Arrays;
import java.util.Locale;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.akaza.openclinica.dao.hibernate.ConfigurationDao;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;

public class ValidatorTest {

	private Validator validator;

	@Before
	public void setUp() {
		Locale locale = new Locale("en");

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setPreferredLocales(Arrays.asList(locale));
		ResourceBundleProvider.updateLocale(request.getLocale());

		ConfigurationDao configurationDao = Mockito.mock(ConfigurationDao.class);

		ValidatorHelper requestBasedValidatorHelper = new ValidatorHelper(request, configurationDao);
		request.setParameter("field1", "1234");
		request.setParameter("field2", "1234.0");

		validator = new Validator(requestBasedValidatorHelper);
	}

	@Test
	public void testThatMethodIsIntegerParsesIntegerNumbersCorrectly() {
		assertTrue(validator.isInteger("field1"));
	}

	@Test
	public void testThatMethodIsIntegerParsesFloatNumbersCorrectly() {
		assertFalse(validator.isInteger("field2"));
	}
	
	@Test
	public void testThatWholeNumberAreIdentifiedAsValidFloatNumber() {
		assertTrue(validator.isFloat("field1"));
	}

	@Test
	public void testThatFloatingPointNumbersAreParsedAsFloats() {
		assertTrue(validator.isFloat("field2"));
	}
}
