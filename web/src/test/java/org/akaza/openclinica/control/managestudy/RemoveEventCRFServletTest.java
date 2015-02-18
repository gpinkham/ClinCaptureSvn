package org.akaza.openclinica.control.managestudy;

import java.util.Locale;
import java.util.ResourceBundle;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.view.Page;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockRequestDispatcher;
import org.springframework.mock.web.MockServletContext;

import com.clinovo.util.SessionUtil;

@RunWith(PowerMockRunner.class)
public class RemoveEventCRFServletTest {

	private RemoveEventCRFServlet removeEventCRFServlet;
	@Mock
	private MockHttpServletResponse response;
	@Mock
	private MockHttpServletRequest request;
	private StudyBean currentStudy;
	private UserAccountBean userAccountBean;
	private MockServletContext servletContext;
	private MockRequestDispatcher requestDispatcher;

	@Before
	public void setUp() throws Exception {
		request = new MockHttpServletRequest();
		removeEventCRFServlet = PowerMockito.spy(new RemoveEventCRFServlet());
		servletContext = Mockito.mock(MockServletContext.class);
		requestDispatcher = Mockito.mock(MockRequestDispatcher.class);

		currentStudy = new StudyBean();
		currentStudy.setId(1);
		currentStudy.setStatus(Status.AVAILABLE);

		userAccountBean = new UserAccountBean();
		userAccountBean.setId(1);

		Locale locale = new Locale("en");
		SessionUtil.updateLocale(request, locale);
		ResourceBundleProvider.updateLocale(locale);
		ResourceBundle respage = ResourceBundleProvider.getPageMessagesBundle(locale);
		ResourceBundle resexception = ResourceBundleProvider.getExceptionsBundle(locale);
		ResourceBundle resformat = ResourceBundleProvider.getFormatBundle(locale);
		Whitebox.setInternalState(removeEventCRFServlet, "respage", respage);
		Whitebox.setInternalState(removeEventCRFServlet, "resexception", resexception);
		Whitebox.setInternalState(removeEventCRFServlet, "resformat", resformat);

		Mockito.doReturn(currentStudy).when(removeEventCRFServlet).getCurrentStudy(request);
		Mockito.doReturn(userAccountBean).when(removeEventCRFServlet).getUserAccountBean(request);
		Mockito.doReturn(Mockito.mock(StudySubjectDAO.class)).when(removeEventCRFServlet).getStudySubjectDAO();
		Mockito.doReturn(Mockito.mock(StudyEventDAO.class)).when(removeEventCRFServlet).getStudyEventDAO();
		Mockito.doReturn(Mockito.mock(EventCRFDAO.class)).when(removeEventCRFServlet).getEventCRFDAO();
		Mockito.doReturn(Mockito.mock(StudyDAO.class)).when(removeEventCRFServlet).getStudyDAO();

		Mockito.when(servletContext.getRequestDispatcher(Mockito.any(String.class))).thenReturn(requestDispatcher);
		Mockito.doReturn(servletContext).when(removeEventCRFServlet).getServletContext();
	}

	@Test
	public void testThatRemoveEventCRFServletWasForwardedToViewStudySubjectServlet() throws Exception {
		removeEventCRFServlet.processRequest(request, response);
		Mockito.verify(servletContext).getRequestDispatcher(Page.VIEW_STUDY_SUBJECT_SERVLET.getFileName());
	}
}
