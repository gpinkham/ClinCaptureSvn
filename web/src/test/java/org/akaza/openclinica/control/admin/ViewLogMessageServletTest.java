package org.akaza.openclinica.control.admin;

import com.clinovo.i18n.LocaleResolver;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.hibernate.jdbc.Expectation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.ResourceBundle;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ResourceBundleProvider.class)
public class ViewLogMessageServletTest {

	@Mock
	private ViewLogMessageServlet viewLogMessageServlet;
	@Mock
	private MockHttpServletRequest request;
	@Mock
	private MockHttpServletResponse response;
	@Mock
	private DataSource dataSource;
	@Mock
	private UserAccountBean userAccountBean;
	@Mock
	private StudyBean studyBean;
	@Mock
	private StudyUserRoleBean studyUserRoleBean;
	@Mock
	private Logger logger;

	@Before
	public void setUp() throws Exception {
		studyBean = new StudyBean();
		MockHttpSession session = new MockHttpSession();
		PowerMockito.when(request.getSession()).thenReturn(session);
		Locale locale = Locale.ENGLISH;
		LocaleResolver.updateLocale(session, locale);
		ResourceBundleProvider.updateLocale(locale);
		Whitebox.setInternalState(viewLogMessageServlet, "respage", ResourceBundleProvider.getPageMessagesBundle());
		Whitebox.setInternalState(viewLogMessageServlet, "resexception", ResourceBundleProvider.getExceptionsBundle());
		PowerMockito.doReturn(userAccountBean).when(viewLogMessageServlet).getUserAccountBean(request);
		PowerMockito.doReturn(studyUserRoleBean).when(viewLogMessageServlet).getCurrentRole(request);
		PowerMockito.doReturn(studyBean).when(viewLogMessageServlet).getCurrentStudy(request);
		PowerMockito.doCallRealMethod().when(viewLogMessageServlet).processRequest(request, response);
		PowerMockito.doCallRealMethod().when(viewLogMessageServlet).mayProceed(request, response);
		Whitebox.setInternalState(viewLogMessageServlet, "logger", logger);
	}

	@Test
	public void testThatFileNotFoundExceptionWillBeHandled() throws Exception {
		ResourceBundle restext = ResourceBundleProvider.getTextsBundle();
		userAccountBean.addUserType(UserType.USER);
		viewLogMessageServlet.processRequest(request, response);
		Mockito.verify(viewLogMessageServlet).addPageMessage(restext.getString("problem_reading_file"), request, logger);
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testThatCoderDoesNotHaveAccessToPage() throws Exception {
		Role role = Role.STUDY_CODER;
		studyUserRoleBean.setRole(role);
		PowerMockito.doReturn(role).when(studyUserRoleBean).getRole();
		userAccountBean.addUserType(UserType.USER);
		viewLogMessageServlet.mayProceed(request, response);
	}

}
