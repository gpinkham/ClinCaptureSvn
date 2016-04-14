package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.submit.ListStudySubjectsServlet;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.dynamicevent.DynamicEventDao;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.FindSubjectsFilter;
import org.akaza.openclinica.dao.managestudy.FindSubjectsSort;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockRequestDispatcher;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.clinovo.i18n.LocaleResolver;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CoreResources.class})
@SuppressWarnings({"rawtypes", "unchecked"})
public class ListStudySubjectsServletTest {

	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private ListStudySubjectsServlet listStudySubjectsServlet;
	private UserAccountBean currentUser;
	private StudyUserRoleBean currentRole;
	private MockServletContext servletContext;
	private MockRequestDispatcher requestDispatcher;
	private StudyBean currentStudy;
	private StudySubjectDAO studySubjectDAO;
	private ServletRequestAttributes servletRequestAttributes;

	@Before
	public void setUp() throws Exception {
		request = new MockHttpServletRequest();
		request.setSession(new MockHttpSession());

		response = new MockHttpServletResponse();

		servletContext = Mockito.mock(MockServletContext.class);
		requestDispatcher = Mockito.mock(MockRequestDispatcher.class);
		studySubjectDAO = Mockito.mock(StudySubjectDAO.class);
		listStudySubjectsServlet = PowerMockito.spy(new ListStudySubjectsServlet());
		currentUser = new UserAccountBean();
		currentUser.addUserType(UserType.USER);
		currentRole = new StudyUserRoleBean();
		currentRole.setRole(Role.STUDY_ADMINISTRATOR);

		servletRequestAttributes = Mockito.mock(ServletRequestAttributes.class);
		RequestContextHolder.setRequestAttributes(servletRequestAttributes);
		Whitebox.setInternalState(servletRequestAttributes, "request", request);

		String url = "http://localhost:8080/clincapture/";
		PowerMockito.mockStatic(CoreResources.class);
		PowerMockito.when(CoreResources.getSystemURL()).thenReturn(url);

		StudyDAO studyDAO = Mockito.mock(StudyDAO.class);
		SubjectDAO subjectDAO = Mockito.mock(SubjectDAO.class);
		StudyEventDAO studyEventDAO = Mockito.mock(StudyEventDAO.class);
		DiscrepancyNoteDAO discrepancyNoteDAO = Mockito.mock(DiscrepancyNoteDAO.class);
		StudyGroupClassDAO studyGroupClassDAO = Mockito.mock(StudyGroupClassDAO.class);
		StudyEventDefinitionDAO studyEventDefinitionDAO = Mockito.mock(StudyEventDefinitionDAO.class);
		DynamicEventDao dynamicEventDao = Mockito.mock(DynamicEventDao.class);

		Locale locale = new Locale("en");
		LocaleResolver.updateLocale(request, locale);
		ResourceBundleProvider.updateLocale(locale);

		currentStudy = new StudyBean();
		currentStudy.setId(1);
		currentStudy.setIdentifier("ident");
		currentStudy.setStatus(Status.AVAILABLE);

		List<StudySubjectBean> studySubjectBeanList = new ArrayList<StudySubjectBean>();
		StudySubjectBean studySubject = buildStudySubject();
		SubjectBean subjectBean = new SubjectBean();
		subjectBean.setGender("male".charAt(0));
		studySubjectBeanList.add(studySubject);

		List<StudyEventBean> studyEventBeanList = new ArrayList<StudyEventBean>();
		StudyEventBean studyEvent = new StudyEventBean();
		studyEvent.setStudyEventDefinitionId(1);
		studyEvent.setSubjectEventStatus(SubjectEventStatus.COMPLETED);
		studyEventBeanList.add(studyEvent);

		List<StudyEventDefinitionBean> studyEventDefinitionBeanList = new ArrayList<StudyEventDefinitionBean>();
		StudyEventDefinitionBean studyEventDefinitionBean = new StudyEventDefinitionBean();
		studyEventDefinitionBean.setName("Event 1");
		studyEventDefinitionBean.setReferenceVisit(true);
		studyEventDefinitionBean.setId(1);

		request.getSession().setAttribute("study", currentStudy);
		request.getSession().setAttribute("userRole", currentRole);

		Mockito.when(studySubjectDAO.getWithFilterAndSort(Mockito.any(StudyBean.class),
				Mockito.any(FindSubjectsFilter.class), Mockito.any(FindSubjectsSort.class), Mockito.anyInt(),
				Mockito.anyInt())).thenReturn((ArrayList) studySubjectBeanList);
		Mockito.when(
				studySubjectDAO.getCountWithFilter(Mockito.any(FindSubjectsFilter.class), Mockito.any(StudyBean.class)))
				.thenReturn(15);
		Mockito.when(studySubjectDAO.findNextLabel(Mockito.any(StudyBean.class))).thenReturn("study_parameter");

		Mockito.when(studyDAO.findByPK(Mockito.anyInt())).thenReturn(currentStudy);
		Mockito.when(subjectDAO.findByPK(Mockito.anyInt())).thenReturn(subjectBean);
		Mockito.when(studyEventDAO.findAllByStudySubject(Mockito.any(StudySubjectBean.class)))
				.thenReturn((ArrayList) studyEventBeanList);
		Mockito.when(studyEventDAO.findAllByStudySubject(Mockito.any(StudySubjectBean.class)))
				.thenReturn((ArrayList) studyEventBeanList);
		Mockito.when(
				studyEventDefinitionDAO.findAllActiveByParentStudyId(Mockito.anyInt(), Mockito.any(ArrayList.class)))
				.thenReturn((ArrayList) studyEventDefinitionBeanList);
		Mockito.when(studyEventDefinitionDAO.findByPK(Mockito.anyInt())).thenReturn(studyEventDefinitionBean);
		Mockito.when(dynamicEventDao.findAllDefIdsInActiveDynGroupsByStudyId(Mockito.anyInt()))
				.thenReturn(new ArrayList<Integer>());
		Mockito.when(servletContext.getRequestDispatcher(Mockito.any(String.class))).thenReturn(requestDispatcher);

		Mockito.doReturn(servletContext).when(listStudySubjectsServlet).getServletContext();
		Mockito.doReturn(dynamicEventDao).when(listStudySubjectsServlet).getDynamicEventDao();
		Mockito.doReturn(studyEventDefinitionDAO).when(listStudySubjectsServlet).getStudyEventDefinitionDAO();
		Mockito.doReturn(studyGroupClassDAO).when(listStudySubjectsServlet).getStudyGroupClassDAO();
		Mockito.doReturn(discrepancyNoteDAO).when(listStudySubjectsServlet).getDiscrepancyNoteDAO();
		Mockito.doReturn(subjectDAO).when(listStudySubjectsServlet).getSubjectDAO();
		Mockito.doReturn(studySubjectDAO).when(listStudySubjectsServlet).getStudySubjectDAO();
		Mockito.doReturn(studyDAO).when(listStudySubjectsServlet).getStudyDAO();
		Mockito.doReturn(studyEventDAO).when(listStudySubjectsServlet).getStudyEventDAO();
		Mockito.doReturn(currentUser).when(listStudySubjectsServlet).getUserAccountBean(request);
		Mockito.doReturn(currentRole).when(listStudySubjectsServlet).getCurrentRole(request);
		Mockito.doReturn("").when(listStudySubjectsServlet).createTable(request, response, true);
	}

	private StudySubjectBean buildStudySubject() {
		StudySubjectBean studySubject = new StudySubjectBean();
		studySubject.setId(1);
		studySubject.setCreatedDate(new Date());
		studySubject.setLabel("subject-label");
		studySubject.setStatus(Status.AVAILABLE);
		studySubject.setSecondaryLabel("label");
		StudyBean studyBean = new StudyBean();
		studyBean.setIdentifier("ident");
		studyBean.setGender("male");

		return studySubject;
	}

	@Test
	public void testThatListStudySubjectServletGrantAccessToStudyAdministrator()
			throws InsufficientPermissionException {
		listStudySubjectsServlet.mayProceed(request, response);
		Assert.assertNull(request.getAttribute("pageMessages"));
	}

	@Test(expected = InsufficientPermissionException.class)
	public void testThatListStudySubjectServletDoesNotGrantAccessToStudyCoder() throws InsufficientPermissionException {
		currentRole.setRole(Role.STUDY_CODER);
		listStudySubjectsServlet.mayProceed(request, response);
	}

	@Test
	public void testThatListStudySubjectServletGrantAccessToStudyMonitor() throws InsufficientPermissionException {
		currentRole.setRole(Role.STUDY_MONITOR);
		listStudySubjectsServlet.mayProceed(request, response);
		Assert.assertNull(request.getAttribute("pageMessages"));
	}

	@Test
	public void testThatListStudySubjectServletGrantsAccessToSiteMonitor() throws InsufficientPermissionException {
		currentRole.setRole(Role.SITE_MONITOR);
		listStudySubjectsServlet.mayProceed(request, response);
		Assert.assertNull(request.getAttribute("pageMessages"));
	}

	@Test
	public void testThatListStudySubjectServletGrantAccessToCRC() throws InsufficientPermissionException {
		currentRole.setRole(Role.CLINICAL_RESEARCH_COORDINATOR);
		listStudySubjectsServlet.mayProceed(request, response);
		Assert.assertNull(request.getAttribute("pageMessages"));
	}

	@Test
	public void testThatListStudySubjectServletGrantAccessToInvestigator() throws InsufficientPermissionException {
		currentRole.setRole(Role.INVESTIGATOR);
		listStudySubjectsServlet.mayProceed(request, response);
		Assert.assertNull(request.getAttribute("pageMessages"));
	}

	@Test
	public void testThatListStudySubjectServletReturnsCorrectPageUrl() throws Exception {
		listStudySubjectsServlet.processRequest(request, response);
		Mockito.verify(servletContext).getRequestDispatcher(Page.LIST_STUDY_SUBJECTS.getFileName());
		Mockito.verify(requestDispatcher).forward(request, response);
	}

	@Test
	public void testThatListStudySubjectServletReturnsNotNullResponseObjects() throws Exception {
		listStudySubjectsServlet.processRequest(request, response);
		Assert.assertNotNull(request.getAttribute("findSubjectsHtml"));
		Assert.assertNotNull(request.getAttribute("allDefsArray"));
		Assert.assertNotNull(request.getAttribute("studyGroupClasses"));
	}
}
