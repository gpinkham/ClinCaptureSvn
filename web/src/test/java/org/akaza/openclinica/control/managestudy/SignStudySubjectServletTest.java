package org.akaza.openclinica.control.managestudy;

import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.core.SecurityManager;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.view.Page;
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
import org.springframework.security.core.userdetails.UserDetails;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ResourceBundleProvider.class)
public class SignStudySubjectServletTest {

	public static final int ID = 1;
	@Mock
	private SignStudySubjectServlet signStudySubjectServlet;	
	@Mock
	private MockHttpServletRequest request;
	@Mock
	private MockHttpServletResponse response;
	@Mock
	private DataSource dataSource;	
	@Mock
	private StudySubjectDAO mockedStudySubjectDAO;
	@Mock
	private StudySubjectBean mockedSSBean;
	@Mock
	private UserAccountBean userAccountBean;
	
	@Before
	public void setUp() throws Exception {
		request.setAttribute("id", "1");
		Locale locale = new Locale("en");
		request.setPreferredLocales(Arrays.asList(locale));		
		ResourceBundleProvider.updateLocale(locale);
		ResourceBundle respage = ResourceBundleProvider.getExceptionsBundle(locale);
		ResourceBundle resexception = ResourceBundleProvider.getPageMessagesBundle();
		ResourceBundle restext = ResourceBundleProvider.getTextsBundle();
		PowerMockito.mockStatic(ResourceBundleProvider.class);
		PowerMockito.when(ResourceBundleProvider.getPageMessagesBundle(Mockito.any(Locale.class))).thenReturn(resexception);
		PowerMockito.when(ResourceBundleProvider.getExceptionsBundle(Mockito.any(Locale.class))).thenReturn(resexception);
		PowerMockito.when(ResourceBundleProvider.getTextsBundle(Mockito.any(Locale.class))).thenReturn(restext);
		Whitebox.setInternalState(signStudySubjectServlet, "respage", respage);
		Whitebox.setInternalState(signStudySubjectServlet, "resexception", resexception);
		Whitebox.setInternalState(signStudySubjectServlet, "restext", restext);
	}

	@Test
	public void testThatEnteringWrongCredentialsKeepsUserOnCorrectPage() throws Exception {
		SecurityManager securityManager = PowerMockito.mock(SecurityManager.class);
		UserDetails userDetails = PowerMockito.mock(UserDetails.class);
		request.addParameter("j_user", "test_pi");
		request.addParameter("j_pass", "pass");		
		userAccountBean.setId(ID);	
		userAccountBean.setName("test_pi");
		PowerMockito.doCallRealMethod().when(signStudySubjectServlet).authenticateUser(request, response, userAccountBean, mockedStudySubjectDAO, 1, mockedSSBean);
		PowerMockito.doReturn(userAccountBean).when(signStudySubjectServlet).getUserAccountBean(request);
		PowerMockito.doReturn(dataSource).when(signStudySubjectServlet).getDataSource();
		PowerMockito.doReturn(securityManager).when(signStudySubjectServlet).getSecurityManager();
		PowerMockito.doReturn(false).when(securityManager).isPasswordValid("test_pi", "pass", userDetails);
		Page page = signStudySubjectServlet.authenticateUser(request, response, userAccountBean, mockedStudySubjectDAO, 1, mockedSSBean);
		assertNull(page);
	}
}
