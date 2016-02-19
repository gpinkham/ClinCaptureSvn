package com.clinovo.interceptor;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.Locale;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.SpringController;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.navigation.Navigation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;

import com.clinovo.controller.CRFEvaluationController;
import com.clinovo.i18n.LocaleResolver;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SecurityContextHolder.class, Navigation.class, RequestContextHolder.class})
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
	@Mock
	private ServletRequestAttributes servletRequestAttributes;

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
		PowerMockito.mockStatic(Navigation.class);
		PowerMockito.mockStatic(SecurityContextHolder.class);
		PowerMockito.when(SecurityContextHolder.getContext()).thenReturn(securityContext);
		PowerMockito.when(securityContext.getAuthentication()).thenReturn(authentication);
		request.setContextPath(CLINCAPTURE);
		request.setSession(session);
		Mockito.when(session.getAttribute(SpringController.STUDY)).thenReturn(studyBean);
		Mockito.when(session.getAttribute(SpringController.USER_BEAN_NAME)).thenReturn(userBean);
		Mockito.when(session.getAttribute(SpringController.USER_ROLE)).thenReturn(userRole);
		Mockito.when(session.getAttribute(LocaleResolver.CURRENT_SESSION_LOCALE)).thenReturn(Locale.ENGLISH);
		PowerMockito.mockStatic(RequestContextHolder.class);
		Whitebox.setInternalState(servletRequestAttributes, "request", request);
		PowerMockito.when(RequestContextHolder.currentRequestAttributes()).thenReturn(servletRequestAttributes);
	}

	@Test
	public void testThatInterceptorSetupsDefaultParameters() throws Exception {

		request.setRemoteUser("root");
		HandlerMethod method = PowerMockito.mock(HandlerMethod.class);
		Method m = PowerMockito.mock(Method.class);
		Whitebox.setInternalState(m, "name", "getPrintCRFController");
		StudyUserRoleBean role = Mockito.mock(StudyUserRoleBean.class);
		UserAccountBean userAccountBean = Mockito.mock(UserAccountBean.class);

		DataSource ds = Mockito.mock(DataSource.class);
		StudyDAO studyDAO = Mockito.mock(StudyDAO.class);
		UserAccountDAO userAccountDAO = Mockito.mock(UserAccountDAO.class);

		Mockito.when(studyDAO.findByPK(Mockito.anyInt())).thenReturn(studyBean);
		Mockito.when(userAccountDAO.findByUserName(Mockito.anyString())).thenReturn(userAccountBean);
		Mockito.when(setUpSessionInterceptor.getDataSource()).thenReturn(ds);
		Mockito.when(setUpSessionInterceptor.getUserAccountDAO(ds)).thenReturn(userAccountDAO);
		Mockito.when(setUpSessionInterceptor.getStudyDAO(ds)).thenReturn(studyDAO);
		Mockito.when(userAccountBean.getRoleByStudy(studyBean.getId())).thenReturn(role);
		PowerMockito.when(method.getMethod()).thenReturn(m);
		PowerMockito.doCallRealMethod().when(setUpSessionInterceptor).preHandle(request, response, method);
		setUpSessionInterceptor.preHandle(request, response, method);
		StudyBean study = (StudyBean) request.getSession().getAttribute(SpringController.STUDY);
		assertEquals(1, study.getId());
	}
}
