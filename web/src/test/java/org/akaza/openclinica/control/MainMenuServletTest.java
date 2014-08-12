package org.akaza.openclinica.control;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.web.SQLInitServlet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SQLInitServlet.class })
public class MainMenuServletTest {

	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private MainMenuServlet mainMenuServlet;

	@Before
	public void setUp() throws Exception {
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		mainMenuServlet = Mockito.mock(MainMenuServlet.class);

		StudyBean study = new StudyBean();
		study.setId(1);
		study.setStatus(Status.AVAILABLE);
		request.getSession().setAttribute("study", study);

		StudyUserRoleBean surb = new StudyUserRoleBean();
		surb.setRole(Role.SYSTEM_ADMINISTRATOR);

		ArrayList<StudyUserRoleBean> roles = new ArrayList<StudyUserRoleBean>();
		roles.add(surb);

		UserAccountBean ub = Mockito.mock(UserAccountBean.class);
		ub.setId(1);
		ub.setRoles(roles);
		Mockito.when(ub.getPasswdTimestamp()).thenReturn(null);
		request.getSession().setAttribute("userBean", ub);
		request.getSession().setAttribute("userRole", surb);

		UserAccountDAO udao = Mockito.mock(UserAccountDAO.class);
		Mockito.when(udao.findByPK(1)).thenReturn(ub);

		request.getSession().setAttribute("redirectAfterLogin", "http://www.site.com/clincapture/AccessFile?fid=123");
		Mockito.doCallRealMethod().when(mainMenuServlet).processRequest(request, response);
		Mockito.doCallRealMethod().when(mainMenuServlet).getUserAccountBean(request);
		Mockito.doCallRealMethod().when(mainMenuServlet).getCurrentStudy(request);
		Mockito.doCallRealMethod().when(mainMenuServlet).getCurrentRole(request);
		Mockito.when(mainMenuServlet.getUserAccountDAO()).thenReturn(udao);

		PowerMockito.mockStatic(SQLInitServlet.class);
		PowerMockito.when(SQLInitServlet.getField("pwd.expiration.days")).thenReturn("132");
		PowerMockito.when(SQLInitServlet.getField("pwd.change.required")).thenReturn("0");

	}

	@Test
	public void testThatProcessRequestWritesRedirectAfterLoginAttributeInTheRequest() throws Exception {
		mainMenuServlet.processRequest(request, response);
		assertTrue(request.getAttribute("redirectAfterLogin") != null);
	}
}
