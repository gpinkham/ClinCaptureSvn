package com.clinovo.interceptor;

import com.clinovo.controller.CRFEvaluationController;
import com.clinovo.util.SessionUtil;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.BaseController;
import org.akaza.openclinica.navigation.Navigation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Locale;

import static org.junit.Assert.assertTrue;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SecurityContextHolder.class, Navigation.class })
public class SetUpSessionInterceptorTest {

	public static final String CLINCAPTURE = "/clincapture";
	public static final String PREV_QUERY_STRING = "module=&maxRows=15&showMoreLink=true&findSubjects_tr_=true&findSubjects_p_=1&findSubjects_mr_=15&findSubjects_s_0_studySubject.createdDate=desc";

	@Mock
	private SetUpSessionInterceptor setUpSessionInterceptor;
	@Mock
	private CRFEvaluationController crfEvaluationController;
	@Mock
	private SecurityContext securityContext;
	@Mock
	private Authentication authentication;
	@Mock
	private MockHttpSession session;

	private MockHttpServletRequest request;

	private MockHttpServletResponse response;

	private StudyUserRoleBean userRole;

	private StudyBean studyBean;

	private UserAccountBean userBean;

	@Before
	public void setUp() throws Exception {
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		userBean = new UserAccountBean();
		userBean.setId(1);
		studyBean = new StudyBean();
		studyBean.setId(1);
		userRole = new StudyUserRoleBean();
		userRole.setRole(Role.SYSTEM_ADMINISTRATOR);
		Mockito.when(crfEvaluationController.getUrl()).thenReturn(CRFEvaluationController.PAGE_CRF_EVALUATION);
		PowerMockito.mockStatic(Navigation.class);
		PowerMockito.mockStatic(SecurityContextHolder.class);
		PowerMockito.when(SecurityContextHolder.getContext()).thenReturn(securityContext);
		PowerMockito.when(securityContext.getAuthentication()).thenReturn(authentication);
		request.setContextPath(CLINCAPTURE);
		request.setSession(session);
		Mockito.when(session.getAttribute(BaseController.STUDY)).thenReturn(studyBean);
		Mockito.when(session.getAttribute(BaseController.USER_BEAN_NAME)).thenReturn(userBean);
		Mockito.when(session.getAttribute(BaseController.USER_ROLE)).thenReturn(userRole);
		Mockito.when(session.getAttribute(SessionUtil.CURRENT_SESSION_LOCALE)).thenReturn(Locale.ENGLISH);
	}

	@Test
	public void testThatRedirectionWorksFineForCRFEvaluationController() throws Exception {
		PowerMockito.doCallRealMethod().when(setUpSessionInterceptor)
				.preHandle(request, response, crfEvaluationController);
		Mockito.when(session.getAttribute(Mockito.contains("CRFEvaluationController"))).thenReturn(PREV_QUERY_STRING);
		setUpSessionInterceptor.preHandle(request, response, crfEvaluationController);
		assertTrue(response.getHeader("Location").equals(
				CLINCAPTURE.concat(CRFEvaluationController.PAGE_CRF_EVALUATION).concat("?").concat(PREV_QUERY_STRING)));
	}
}
