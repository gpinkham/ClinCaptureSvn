package org.akaza.openclinica.control.managestudy;

import com.clinovo.util.SessionUtil;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.service.StudyParameterConfig;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.control.form.FormDiscrepancyNotes;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.dao.hibernate.ConfigurationDao;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.util.DAOWrapper;
import org.akaza.openclinica.util.SignUtil;
import org.akaza.openclinica.util.StudyEventDefinitionUtil;
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
import org.slf4j.Logger;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Status.class, ResourceBundleProvider.class, UpdateStudyEventServlet.class, FormProcessor.class,
		StudySubjectDAO.class, SignUtil.class, DAOWrapper.class, StudyEventDefinitionDAO.class, EventCRFDAO.class,
		StudyEventDefinitionUtil.class })
public class UpdateStudyEventServletTest {

	@Mock
	private UpdateStudyEventServlet updateStudyEventServlet;
	@Mock
	private MockHttpSession session;
	@Mock
	private MockHttpServletRequest request;
	@Mock
	private MockHttpServletResponse response;
	@Mock
	private StudyUserRoleBean studyUserRoleBean;
	@Mock
	private UserAccountBean userAccountBean;
	@Mock
	private StudyBean studyBean;
	@Mock
	private SessionManager sessionManager;
	@Mock
	private ResourceBundle respage;
	@Mock
	private FormProcessor formProcessor;
	@Mock
	private StudySubjectDAO studySubjectDAO;
	@Mock
	private StudyEventDAO studyEventDAO;
	@Mock
	private EventCRFDAO eventCRFDAO;
	@Mock
	private StudySubjectBean studySubjectBean;
	@Mock
	private StudyEventBean studyEventBean;
	@Mock
	private ResourceBundle resterm;
	@Mock
	private StudyDAO studyDAO;
	@Mock
	private DAOWrapper daoWrapper;
	@Mock
	private StudyEventDefinitionBean studyEventDefinitionBean;
	@Mock
	private EventDefinitionCRFDAO eventDefinitionCRFDAO;
	@Mock
	private StudyEventDefinitionDAO studyEventDefinitionDAO;
	@Mock
	private ConfigurationDao configurationDao;
	@Mock
	private FormDiscrepancyNotes discNotes;
	@Mock
	private StudyParameterConfig studyParameterConfig;

	private String currentStudyLocked;
	private List<Integer> studyEventDefinitionIds;
	private List<StudyEventBean> studyEvents;
	private StudySubjectBean studySubjectToUpdate;

	@Before
	public void setUp() throws Exception {
		PowerMockito.doReturn(discNotes).when(session).getAttribute("fdnotes");
		PowerMockito.doReturn(1).when(studyBean).getId();
		PowerMockito.doReturn(0).when(studyBean).getParentStudyId();
		PowerMockito.doReturn(1).when(studySubjectBean).getStudyId();
		currentStudyLocked = "current study locked";
		PowerMockito.doReturn(Locale.ENGLISH).when(session).getAttribute(SessionUtil.CURRENT_SESSION_LOCALE);
		PowerMockito.mockStatic(Status.class);
		PowerMockito.mockStatic(SignUtil.class);
		PowerMockito.mockStatic(ResourceBundleProvider.class);
		PowerMockito.mockStatic(StudyEventDefinitionUtil.class);
		PowerMockito.when(ResourceBundleProvider.getTermsBundle()).thenReturn(resterm);
		PowerMockito.doCallRealMethod().when(updateStudyEventServlet).processRequest(request, response);
		PowerMockito.doReturn(userAccountBean).when(updateStudyEventServlet).getUserAccountBean(request);
		PowerMockito.doReturn(studyUserRoleBean).when(updateStudyEventServlet).getCurrentRole(request);
		PowerMockito.doReturn(studyBean).when(updateStudyEventServlet).getCurrentStudy(request);
		PowerMockito.doReturn(studyParameterConfig).when(studyBean).getStudyParameterConfig();
		PowerMockito.doReturn("").when(studyParameterConfig).getStartDateTimeRequired();
		PowerMockito.doReturn("").when(studyParameterConfig).getEndDateTimeRequired();
		PowerMockito.doReturn(sessionManager).when(updateStudyEventServlet).getSessionManager(request);
		PowerMockito.doReturn(studyEventDAO).when(updateStudyEventServlet).getStudyEventDAO();
		PowerMockito.doReturn(eventCRFDAO).when(updateStudyEventServlet).getEventCRFDAO();
		PowerMockito.doReturn(configurationDao).when(updateStudyEventServlet).getConfigurationDao();
		PowerMockito.doReturn(eventDefinitionCRFDAO).when(updateStudyEventServlet).getEventDefinitionCRFDAO();
		PowerMockito.doReturn(session).when(request).getSession();
		Whitebox.setInternalState(updateStudyEventServlet, "respage", respage);
		Whitebox.setInternalState(updateStudyEventServlet, "logger", PowerMockito.mock(Logger.class));
		PowerMockito.doReturn(currentStudyLocked).when(respage).getString("current_study_locked");
		PowerMockito.whenNew(FormProcessor.class).withAnyArguments().thenReturn(formProcessor);
		PowerMockito.whenNew(StudyEventDefinitionDAO.class).withAnyArguments().thenReturn(studyEventDefinitionDAO);
		PowerMockito.whenNew(StudyDAO.class).withAnyArguments().thenReturn(studyDAO);
		PowerMockito.whenNew(EventCRFDAO.class).withAnyArguments().thenReturn(eventCRFDAO);
		PowerMockito.whenNew(DAOWrapper.class).withAnyArguments().thenReturn(daoWrapper);
		PowerMockito.when(SignUtil.permitSign(studyEventBean, studyBean, daoWrapper)).thenReturn(true);
		initStudyEventLists();
		studySubjectToUpdate = new StudySubjectBean();
		PowerMockito
				.doCallRealMethod()
				.when(updateStudyEventServlet)
				.updateStudySubjectStatus(studyEventDefinitionIds, studySubjectDAO, studySubjectToUpdate,
						studyEventDAO, studyEventBean, studyBean, userAccountBean);
		PowerMockito.doReturn(studySubjectBean).when(studySubjectDAO).update(studySubjectToUpdate);
		PowerMockito.when(studyEventDAO.findAllByStudySubject(Mockito.any(StudySubjectBean.class))).thenReturn(
				(ArrayList<StudyEventBean>) studyEvents);
	}

	private void initStudyEventLists() {
		int index = 1;
		int id = 0;
		studyEventDefinitionIds = new ArrayList<Integer>();
		studyEventDefinitionIds.add(index++);
		studyEventDefinitionIds.add(index++);
		studyEventDefinitionIds.add(index++);

		index = 1;
		studyEvents = new ArrayList<StudyEventBean>();
		addStudyEventToList(id++, index++);
		addStudyEventToList(id++, index++);
		addStudyEventToList(id++, index++);
	}

	private void addStudyEventToList(int listIndex, int id) {
		studyEvents.add(new StudyEventBean());
		studyEvents.get(listIndex).setId(id);
		studyEvents.get(listIndex).setStudyEventDefinitionId(id);
	}

	@Test
	public void testThatEventCRFBeanStatusDoesNotReset() throws Exception {
		EventCRFBean eventCRFBean = new EventCRFBean();
		eventCRFBean.setStatus(Status.AVAILABLE);
		List<EventCRFBean> eventCRFBeans = new ArrayList<EventCRFBean>();
		eventCRFBeans.add(eventCRFBean);
		PowerMockito.doReturn(true).when(userAccountBean).isSysAdmin();
		PowerMockito.doReturn(Status.AVAILABLE).when(studySubjectBean).getStatus();
		PowerMockito.doReturn(1).when(formProcessor).getInt("event_id", true);
		PowerMockito.doReturn(1).when(formProcessor).getInt("ss_id", true);
		PowerMockito.doReturn("submit").when(formProcessor).getString("action");
		PowerMockito.doReturn(5).when(formProcessor).getInt("statusId");
		PowerMockito.doReturn("").when(formProcessor).getDateTimeInputString(Mockito.anyString());
		PowerMockito.doReturn("").when(formProcessor).getString(Mockito.contains("start"));
		PowerMockito.whenNew(StudySubjectDAO.class).withAnyArguments().thenReturn(studySubjectDAO);
		PowerMockito.doReturn(studySubjectBean).when(studySubjectDAO).findByPK(1);
		PowerMockito.doReturn(studyEventBean).when(studyEventDAO).findByPK(1);
		PowerMockito.doReturn(SubjectEventStatus.DATA_ENTRY_STARTED).when(studyEventBean).getSubjectEventStatus();
		PowerMockito.doReturn(eventCRFBeans).when(eventCRFDAO).findAllByStudyEvent(studyEventBean);
		PowerMockito.doReturn(studyBean).when(studyDAO).findByPK(1);
		PowerMockito.doReturn(studyEventDefinitionBean).when(studyEventDefinitionDAO).findByPK(1);
		PowerMockito.doReturn(1).when(studyEventBean).getStudySubjectId();
		PowerMockito.doReturn(1).when(studyEventBean).getStudyEventDefinitionId();
		PowerMockito.when(
				eventDefinitionCRFDAO.findAllByDefinition(studyBean, studyEventBean.getStudyEventDefinitionId()))
				.thenReturn(new ArrayList<EventDefinitionCRFBean>());
		PowerMockito.doNothing().when(updateStudyEventServlet)
				.checkStudyLocked(Page.MENU_SERVLET, currentStudyLocked, request, response);
		updateStudyEventServlet.processRequest(request, response);
		assertFalse(eventCRFBean.getStatus().equals(Status.UNAVAILABLE));
	}

	@Test
	public void testThatUpdateStudySubjectStatusLocksStudySubject() throws Exception {
		studyEvents.get(0).setSubjectEventStatus(SubjectEventStatus.LOCKED);
		studyEvents.get(1).setSubjectEventStatus(SubjectEventStatus.LOCKED);
		studyEvents.get(2).setSubjectEventStatus(SubjectEventStatus.LOCKED);
		studySubjectToUpdate.setId(1);
		studySubjectToUpdate.setStatus(Status.AVAILABLE);
		updateStudyEventServlet.updateStudySubjectStatus(studyEventDefinitionIds, studySubjectDAO,
				studySubjectToUpdate, studyEventDAO, studyEventBean, studyBean, userAccountBean);
		assertEquals(Status.LOCKED, studySubjectToUpdate.getStatus());
	}

	@Test
	public void testThatUpdateStudySubjectStatusUnlocksStudySubject() throws Exception {
		studyEvents.get(0).setSubjectEventStatus(SubjectEventStatus.DATA_ENTRY_STARTED);
		studyEvents.get(1).setSubjectEventStatus(SubjectEventStatus.LOCKED);
		studyEvents.get(2).setSubjectEventStatus(SubjectEventStatus.LOCKED);
		studySubjectToUpdate.setStatus(Status.LOCKED);
		updateStudyEventServlet.updateStudySubjectStatus(studyEventDefinitionIds, studySubjectDAO,
				studySubjectToUpdate, studyEventDAO, studyEventBean, studyBean, userAccountBean);
		assertEquals(Status.AVAILABLE, studySubjectToUpdate.getStatus());
	}
}
