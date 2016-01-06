package com.clinovo.rest.exception;

import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.dao.core.CoreResources;
import org.junit.Test;

public class RestExceptionTest extends DefaultAppContextTest {

	@Test
	public void testThatItIsPossibleToCreateTheRestExceptionWithStringMessage() throws Exception {
		String msg = "test message";
		RestException restException = new RestException(msg);
		assertEquals(restException.getMessage(), msg);
		assertEquals(restException.getCode(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}

	@Test
	public void testThatItIsPossibleToCreateTheRestExceptionWithLocalizedMessage() throws Exception {
		String msg = messageSource.getMessage("rest.atLeastOneNotRequiredParameterShouldBeSpecified", null,
				CoreResources.getSystemLocale());
		RestException restException = new RestException(messageSource,
				"rest.atLeastOneNotRequiredParameterShouldBeSpecified", HttpServletResponse.SC_FORBIDDEN);
		assertEquals(restException.getMessage(), msg);
		assertEquals(restException.getCode(), HttpServletResponse.SC_FORBIDDEN);
	}

	@Test
	public void testThatItIsPossibleToCreateTheRestExceptionWithLocalizedMessageAndCode() throws Exception {
		String msg = messageSource.getMessage("rest.atLeastOneNotRequiredParameterShouldBeSpecified", null,
				CoreResources.getSystemLocale());
		RestException restException = new RestException(messageSource,
				"rest.atLeastOneNotRequiredParameterShouldBeSpecified", HttpServletResponse.SC_BAD_REQUEST);
		assertEquals(restException.getMessage(), msg);
		assertEquals(restException.getCode(), HttpServletResponse.SC_BAD_REQUEST);
	}

	@Test
	public void testThatItIsPossibleToCreateTheRestExceptionWithLocalizedMessageWithArgumentsAndCode()
			throws Exception {
		Object[] arguments = new Object[]{"X_PARAMETER"};
		String msg = messageSource.getMessage("rest.parameterIsNotSupported", arguments,
				CoreResources.getSystemLocale());
		RestException restException = new RestException(messageSource, "rest.parameterIsNotSupported", arguments,
				HttpServletResponse.SC_BAD_REQUEST);
		assertEquals(restException.getMessage(), msg);
		assertEquals(restException.getCode(), HttpServletResponse.SC_BAD_REQUEST);
	}
}
