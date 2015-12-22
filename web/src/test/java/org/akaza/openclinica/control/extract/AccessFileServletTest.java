package org.akaza.openclinica.control.extract;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.clinovo.i18n.LocaleResolver;

public class AccessFileServletTest {

	public static final String URL = "http://www.site.com/clincapture/AccessFile?fid=123";

	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private AccessFileServlet accessFileServlet;
	private UserAccountBean currentUser;
	private StudyUserRoleBean currentRole;

	@Before
	public void setUp() throws Exception {
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		accessFileServlet = Mockito.mock(AccessFileServlet.class);
		currentUser = new UserAccountBean();
		currentRole = new StudyUserRoleBean();
		request.getSession().setAttribute("redirectAfterLogin", URL);
		Mockito.doCallRealMethod().when(accessFileServlet).processRequest(request, response);
		Mockito.doCallRealMethod().when(accessFileServlet).mayProceed(request, response);
		Mockito.when(accessFileServlet.getCurrentRole(request)).thenReturn(currentRole);
		Mockito.when(accessFileServlet.getUserAccountBean(request)).thenReturn(currentUser);

		Locale locale = new Locale("en");
		LocaleResolver.updateLocale(request, locale);
		ResourceBundleProvider.updateLocale(locale);
	}

	@Test
	public void testThatProcessRequestDoesRedirectToMainMenuIfAttributeRedirectAfterLoginIsPresent() throws Exception {
		accessFileServlet.processRequest(request, response);
		assertTrue(response.getHeader("Location").equals("/MainMenu"));
	}

	@Test
	public void testThatSysAdminCanProceed() throws InsufficientPermissionException {
		currentUser.addUserType(UserType.SYSADMIN);
		accessFileServlet.mayProceed(request, response);
		assertNull(request.getAttribute("pageMessages"));
	}

	@Test
	public void testThatAdminUserCanProceed() throws InsufficientPermissionException {
		currentUser.addUserType(UserType.SYSADMIN);
		currentRole.setRole(Role.CLINICAL_RESEARCH_COORDINATOR);
		accessFileServlet.mayProceed(request, response);
		assertNull(request.getAttribute("pageMessages"));
	}

	@Test
	public void testThatStudyAdminUserCanProceed() throws InsufficientPermissionException {
		currentUser.addUserType(UserType.USER);
		currentRole.setRole(Role.STUDY_ADMINISTRATOR);
		accessFileServlet.mayProceed(request, response);
		assertNull(request.getAttribute("pageMessages"));
	}

	@Test
	public void testThatInvestigatorCanProceed() throws InsufficientPermissionException {
		currentUser.addUserType(UserType.USER);
		currentRole.setRole(Role.INVESTIGATOR);
		accessFileServlet.mayProceed(request, response);
		assertNull(request.getAttribute("pageMessages"));
	}

	@Test
	public void testThatStudyMonitorCanProceed() throws InsufficientPermissionException {
		currentUser.addUserType(UserType.USER);
		currentRole.setRole(Role.STUDY_MONITOR);
		accessFileServlet.mayProceed(request, response);
		assertNull(request.getAttribute("pageMessages"));
	}

	@Test
	public void testThatSiteMonitorCanProceed() throws InsufficientPermissionException {
		currentUser.addUserType(UserType.USER);
		currentRole.setRole(Role.SITE_MONITOR);
		accessFileServlet.mayProceed(request, response);
		assertNull(request.getAttribute("pageMessages"));
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testThatUnAuthorizedNonAdminUserCannotProceed() throws InsufficientPermissionException {
		currentUser.addUserType(UserType.USER);
		currentRole.setRole(Role.CLINICAL_RESEARCH_COORDINATOR);
		accessFileServlet.mayProceed(request, response);
	}
}
