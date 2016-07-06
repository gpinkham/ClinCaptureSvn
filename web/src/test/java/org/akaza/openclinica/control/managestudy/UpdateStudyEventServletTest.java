package org.akaza.openclinica.control.managestudy;

import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

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

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.service.CRFMaskingService;
import com.clinovo.util.DAOWrapper;
import com.clinovo.util.SDVUtil;
import com.clinovo.util.SignUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Status.class, UpdateStudyEventServlet.class, FormProcessor.class, StudySubjectDAO.class,
		SignUtil.class, SDVUtil.class, DAOWrapper.class, StudyEventDefinitionDAO.class, EventCRFDAO.class,
		StudyEventDefinitionUtil.class})
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
	private CRFMaskingService maskingService;
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
	private List<Object> fullCrfList = new ArrayList<Object>();
	private Map<Integer, String> notedMap = new HashMap<Integer, String>();
	private ArrayList<EventCRFBean> eventCRFBeans = new ArrayList<EventCRFBean>();
	private ArrayList<EventDefinitionCRFBean> eventDefinitionCRFs = new ArrayList<EventDefinitionCRFBean>();

	@Before
	public void setUp() throws Exception {
		ResourceBundleProvider.updateLocale(Locale.ENGLISH);
		PowerMockito.doReturn(discNotes).when(session).getAttribute("fdnotes");
		PowerMockito.doReturn(1).when(studyBean).getId();
		PowerMockito.doReturn(0).when(studyBean).getParentStudyId();
		PowerMockito.doReturn(1).when(studySubjectBean).getStudyId();
		currentStudyLocked = "current study locked";
		PowerMockito.doReturn(Locale.ENGLISH).when(session).getAttribute(LocaleResolver.CURRENT_SESSION_LOCALE);
		PowerMockito.mockStatic(Status.class);
		PowerMockito.mockStatic(SignUtil.class);
		PowerMockito.mockStatic(SDVUtil.class);
		PowerMockito.mockStatic(StudyEventDefinitionUtil.class);
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
		PowerMockito.doReturn(studyDAO).when(updateStudyEventServlet).getStudyDAO();
		PowerMockito.doReturn(configurationDao).when(updateStudyEventServlet).getConfigurationDao();
		PowerMockito.doReturn(eventDefinitionCRFDAO).when(updateStudyEventServlet).getEventDefinitionCRFDAO();
		PowerMockito.doReturn(maskingService).when(updateStudyEventServlet).getMaskingService();
		PowerMockito.doReturn(session).when(request).getSession();
		Whitebox.setInternalState(updateStudyEventServlet, "logger", PowerMockito.mock(Logger.class));
		PowerMockito.doReturn(currentStudyLocked).when(respage).getString("current_study_locked");
		PowerMockito.whenNew(FormProcessor.class).withAnyArguments().thenReturn(formProcessor);
		PowerMockito.doReturn(studyEventDefinitionDAO).when(updateStudyEventServlet).getStudyEventDefinitionDAO();
		PowerMockito.whenNew(EventCRFDAO.class).withAnyArguments().thenReturn(eventCRFDAO);
		PowerMockito.whenNew(StudyDAO.class).withAnyArguments().thenReturn(studyDAO);
		PowerMockito.whenNew(DAOWrapper.class).withAnyArguments().thenReturn(daoWrapper);
		initStudyEventLists();
		studySubjectToUpdate = new StudySubjectBean();
		PowerMockito.doReturn(studySubjectBean).when(studySubjectDAO).update(studySubjectToUpdate);
		PowerMockito.when(studyEventDAO.findAllByStudySubject(Mockito.any(StudySubjectBean.class)))
				.thenReturn((ArrayList<StudyEventBean>) studyEvents);
		PowerMockito.when(SignUtil.permitSign(studyEventBean, studyBean, daoWrapper)).thenReturn(true);
		PowerMockito.doReturn(fullCrfList).when(updateStudyEventServlet).prepareFullCrfList(studyBean, studySubjectBean,
				studyEventBean, eventCRFBeans, eventDefinitionCRFs);
		PowerMockito.when(updateStudyEventServlet.prepareNodeMapForFullCrfList(fullCrfList, studySubjectBean,
				studyEventDefinitionBean.getName(), studyEventBean.getId())).thenReturn(notedMap);
	}

	private void initStudyEventLists() {
		int index = 1;
		int id = 0;
		studyEventDefinitionIds = new ArrayList<Integer>();
		studyEventDefinitionIds.add(index++);
		studyEventDefinitionIds.add(index++);
		studyEventDefinitionIds.add(index);

		index = 1;
		studyEvents = new ArrayList<StudyEventBean>();
		addStudyEventToList(id++, index++);
		addStudyEventToList(id++, index++);
		addStudyEventToList(id, index);
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
		eventCRFBeans.add(eventCRFBean);
		PowerMockito.doReturn(true).when(userAccountBean).isSysAdmin();
		PowerMockito.doReturn(Status.AVAILABLE).when(studySubjectBean).getStatus();
		PowerMockito.doReturn(1).when(formProcessor).getInt("event_id", true);
		PowerMockito.doReturn(1).when(formProcessor).getInt("ss_id", true);
		PowerMockito.doReturn("submit").when(formProcessor).getString("action");
		PowerMockito.doReturn(5).when(formProcessor).getInt("statusId");
		PowerMockito.doReturn("").when(formProcessor).getDateTimeInputString(Mockito.anyString());
		PowerMockito.doReturn("").when(formProcessor).getString(Mockito.contains("start"));
		PowerMockito.doReturn(studySubjectDAO).when(updateStudyEventServlet).getStudySubjectDAO();
		PowerMockito.doReturn(studySubjectBean).when(studySubjectDAO).findByPK(1);
		PowerMockito.doReturn(studyEventBean).when(studyEventDAO).findByPK(1);
		PowerMockito.doReturn(SubjectEventStatus.DATA_ENTRY_STARTED).when(studyEventBean).getSubjectEventStatus();
		PowerMockito.doReturn(eventCRFBeans).when(eventCRFDAO).findAllByStudyEvent(studyEventBean);
		PowerMockito.doReturn(eventDefinitionCRFs).when(eventDefinitionCRFDAO)
				.findAllActiveByEventDefinitionId(studyBean, 1);
		PowerMockito.doReturn(studyBean).when(studyDAO).findByPK(1);
		PowerMockito.doReturn(studyEventDefinitionBean).when(studyEventDefinitionDAO).findByPK(1);
		PowerMockito.doReturn(1).when(studyEventBean).getStudyEventDefinitionId();
		PowerMockito.doReturn(1).when(studyEventBean).getStudySubjectId();
		PowerMockito.doReturn(1).when(studyEventBean).getStudyEventDefinitionId();
		PowerMockito.doReturn(new StudyParameterConfig()).when(studyBean).getStudyParameterConfig();
		PowerMockito
				.when(eventDefinitionCRFDAO.findAllByDefinition(studyBean, studyEventBean.getStudyEventDefinitionId()))
				.thenReturn(new ArrayList<EventDefinitionCRFBean>());
		PowerMockito.doNothing().when(updateStudyEventServlet).checkStudyLocked(Page.MENU_SERVLET, currentStudyLocked,
				request, response);
		PowerMockito.when(SDVUtil.permitSDV(studyEventBean, 1, daoWrapper, false, notedMap, 0, maskingService))
				.thenReturn(true);
		updateStudyEventServlet.processRequest(request, response);
		assertFalse(eventCRFBean.getStatus().equals(Status.UNAVAILABLE));
	}
}
