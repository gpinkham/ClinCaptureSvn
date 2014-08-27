package org.akaza.openclinica.control.admin;

import org.akaza.openclinica.control.form.FormProcessor;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConfigurePasswordRequirementsServletTest {

	@Test
	public void testThatMaxPasswdLengthNotLessThanNoOfMustChars() throws Exception {
		ConfigurePasswordRequirementsServlet servlet = new ConfigurePasswordRequirementsServlet();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("pwd.chars.min", "1");
		request.addParameter("pwd.chars.max", "3");
		request.addParameter("pwd.chars.case.lower", "true");
		request.addParameter("pwd.chars.case.upper", "true");
		request.addParameter("pwd.chars.digits", "true");
		request.addParameter("pwd.chars.specials", "true");
		FormProcessor fp = new FormProcessor(request);

		assertTrue(servlet.passwordMustsGreaterThanMaxLength(fp));
	}

	@Test
	public void testThatPasswordMustsGreaterThanMaxLengthReturnsFalse() throws Exception {
		ConfigurePasswordRequirementsServlet servlet = new ConfigurePasswordRequirementsServlet();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("pwd.chars.min", "1");
		request.addParameter("pwd.chars.max", "16");
		request.addParameter("pwd.chars.case.lower", "true");
		request.addParameter("pwd.chars.case.upper", "true");
		request.addParameter("pwd.chars.digits", "true");
		request.addParameter("pwd.chars.specials", "true");
		FormProcessor fp = new FormProcessor(request);

		assertFalse(servlet.passwordMustsGreaterThanMaxLength(fp));
	}

	@Test
	public void testThatMinPasswdLengthNotLessThanNoOfMustChars() throws Exception {
		ConfigurePasswordRequirementsServlet servlet = new ConfigurePasswordRequirementsServlet();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("pwd.chars.min", "3");
		request.addParameter("pwd.chars.max", "0");
		request.addParameter("pwd.chars.case.lower", "true");
		request.addParameter("pwd.chars.case.upper", "true");
		request.addParameter("pwd.chars.digits", "true");
		request.addParameter("pwd.chars.specials", "true");
		FormProcessor fp = new FormProcessor(request);

		assertTrue(servlet.passwordMustsGreaterThanMinLength(fp));
	}

	@Test
	public void testThatPasswordMustsGreaterThanMinLengthReturnsFalse() throws Exception {
		ConfigurePasswordRequirementsServlet servlet = new ConfigurePasswordRequirementsServlet();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("pwd.chars.min", "6");
		request.addParameter("pwd.chars.max", "0");
		request.addParameter("pwd.chars.case.lower", "true");
		request.addParameter("pwd.chars.case.upper", "true");
		request.addParameter("pwd.chars.digits", "true");
		request.addParameter("pwd.chars.specials", "true");
		FormProcessor fp = new FormProcessor(request);

		assertFalse(servlet.passwordMustsGreaterThanMinLength(fp));
	}
}
