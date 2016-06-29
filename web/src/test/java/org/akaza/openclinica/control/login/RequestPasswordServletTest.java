package org.akaza.openclinica.control.login;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.core.SecurityManager;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.web.filter.OpenClinicaJdbcService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockRequestDispatcher;
import org.springframework.mock.web.MockServletContext;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * RequestPasswordServlet Test.
 */
public class RequestPasswordServletTest {

	private RequestPasswordServlet servlet;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private UserAccountDAO userAccountDAO;

	@Before
	public void setUp() throws Exception {
		servlet = Mockito.spy(new RequestPasswordServlet());
		Mockito.doNothing().when(servlet).sendPassword(Mockito.anyString(), Mockito.any(UserAccountBean.class),
				Mockito.any(HttpServletRequest.class), Mockito.any(HttpServletResponse.class));
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();

		SessionManager sessionManager = Mockito.mock(SessionManager.class);
		Mockito.doReturn(sessionManager).when(servlet).getSessionManager(Mockito.anyString());
		userAccountDAO = Mockito.mock(UserAccountDAO.class);
		Mockito.doReturn(userAccountDAO).when(servlet).getUserAccountDAO();
		UserAccountBean userAccountBean = getTestUserAccountBean();
		Mockito.doReturn(userAccountBean).when(userAccountDAO).findByUserName(Mockito.anyString());
		Mockito.doReturn(new UserAccountBean()).when(userAccountDAO).update(Mockito.any(UserAccountBean.class));
		ServletContext servletContext = Mockito.mock(MockServletContext.class);
		Mockito.doReturn(servletContext).when(servlet).getServletContext();
		RequestDispatcher requestDispatcher = Mockito.mock(MockRequestDispatcher.class);
		Mockito.doReturn(requestDispatcher).when(servletContext).getRequestDispatcher(Mockito.anyString());
		SecurityManager securityManager = Mockito.mock(SecurityManager.class);
		Mockito.doReturn(securityManager).when(servlet).getSecurityManager();
		OpenClinicaJdbcService openClinicaJdbcService = Mockito.mock(OpenClinicaJdbcService.class);
		Mockito.doReturn(openClinicaJdbcService).when(servlet).getOpenClinicaJdbcService();
	}

	@Test
	public void testThatConfirmPasswordWillNotBeCalledIfActionIsNull() throws Exception {
		servlet.processRequest(request, response);
		Mockito.verify(servlet, Mockito.never()).confirmPassword(request, response);
	}

	@Test
	public void testThatConfirmPasswordWillBeCalledIfActionEqualsConfirm() throws Exception {
		request.setParameter("action", "confirm");
		Mockito.doNothing().when(servlet).confirmPassword(request, response);
		servlet.processRequest(request, response);
		Mockito.verify(servlet).confirmPassword(request, response);
	}

	@Test
	public void testThatPasswordWillNotBeSendIfUserNameIsEmpty() throws Exception {
		setUserParametersToRequest(request);
		request.setParameter("name", "");
		servlet.confirmPassword(request, response);
		Mockito.verify(servlet, Mockito.never()).sendPassword(Mockito.anyString(),
				Mockito.any(UserAccountBean.class), Mockito.any(MockHttpServletRequest.class),
				Mockito.any(HttpServletResponse.class));
	}

	@Test
	public void testThatPasswordWillNotBeSendIfEmailInTheDBIsDifferent() throws Exception {
		setUserParametersToRequest(request);
		request.setParameter("email", "test_2@test.com");
		servlet.confirmPassword(request, response);
		Mockito.verify(servlet, Mockito.never()).sendPassword(Mockito.anyString(),
				Mockito.any(UserAccountBean.class), Mockito.any(HttpServletRequest.class),
				Mockito.any(HttpServletResponse.class));
	}

	@Test
	public void testThatPasswordWillNotBeSendIfPasswordChangeQuestionInTheDBIsDifferent() throws Exception {
		setUserParametersToRequest(request);
		request.setParameter(RequestPasswordServlet.PASS_CHANGE_QUESTION, "Test?");
		servlet.confirmPassword(request, response);
		Mockito.verify(servlet, Mockito.never()).sendPassword(Mockito.anyString(),
				Mockito.any(UserAccountBean.class), Mockito.any(MockHttpServletRequest.class),
				Mockito.any(HttpServletResponse.class));
	}

	@Test
	public void testThatUserAccountWillBeUpdatedIfAllRequirementsAreMet() throws Exception {
		setUserParametersToRequest(request);
		servlet.confirmPassword(request, response);
		Mockito.verify(userAccountDAO).update(Mockito.any(UserAccountBean.class));
		Mockito.verify(servlet).sendPassword(Mockito.anyString(),
				Mockito.any(UserAccountBean.class), Mockito.any(MockHttpServletRequest.class),
				Mockito.any(HttpServletResponse.class));
	}

	private void setUserParametersToRequest(MockHttpServletRequest request) {
		request.setParameter("name", "test");
		request.setParameter("email", "test@test.com");
		request.setParameter(RequestPasswordServlet.PASS_CHANGE_QUESTION, "Are you here?");
		request.setParameter(RequestPasswordServlet.PASS_CHANGE_ANSWER, "no");
	}

	private UserAccountBean getTestUserAccountBean() {
		UserAccountBean userAccountBean = new UserAccountBean();
		userAccountBean.setName("test");
		userAccountBean.setEmail("test@test.com");
		userAccountBean.setPasswdChallengeQuestion("Are you here?");
		userAccountBean.setPasswdChallengeAnswer("No");
		return userAccountBean;
	}
}
