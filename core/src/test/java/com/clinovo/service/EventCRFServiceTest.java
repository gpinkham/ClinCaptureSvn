package com.clinovo.service;

import static org.mockito.Mockito.mock;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

import java.util.ArrayList;
import java.util.List;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.clinovo.service.impl.EventCRFServiceImpl;
import com.clinovo.service.impl.ItemDataServiceImpl;

public class EventCRFServiceTest extends DefaultAppContextTest {

	private EventCRFServiceImpl mockEventCRFService;

	private ItemDataService mockItemDataService;

	private EventCRFDAO mockEventCRFDAO;

	private StudySubjectDAO mockStudySubjectDAO;
	private StudyDAO mockStudyDAO;

	private UserAccountBean updater;

	List<EventCRFBean> eventCRFList;

	@Before
	public void setUp() throws Exception {

		mockEventCRFService = mock(EventCRFServiceImpl.class);
		mockItemDataService = mock(ItemDataServiceImpl.class);
		mockEventCRFDAO = mock(EventCRFDAO.class);
		mockStudySubjectDAO = mock(StudySubjectDAO.class);
		mockStudyDAO = mock(StudyDAO.class);
		setInternalState(mockEventCRFService, "itemDataService", mockItemDataService);
		Mockito.doReturn(mockEventCRFDAO).when(mockEventCRFService).getEventCRFDAO();
		Mockito.doReturn(mockStudySubjectDAO).when(mockEventCRFService).getStudySubjectDAO();
		Mockito.doReturn(mockStudyDAO).when(mockEventCRFService).getStudyDAO();
		Mockito.doNothing().when(mockEventCRFService).updateStudyEventStatus(Mockito.any(EventCRFBean.class),
				Mockito.any(UserAccountBean.class));

		StudySubjectBean subject = new StudySubjectBean();
		subject.setStudyId(1);
		subject.setName("TestSubject1");
		Mockito.doReturn(subject).when(mockStudySubjectDAO).findByPK(Mockito.anyInt());
		StudyBean study = new StudyBean();
		study.setName("TestStudy1");
		Mockito.doReturn(study).when(mockStudyDAO).findByPK(Mockito.anyInt());

		updater = new UserAccountBean();
		updater.setId(144);

		eventCRFList = new ArrayList<EventCRFBean>();
		addEventToList(1, Status.UNAVAILABLE, Status.AVAILABLE);
		addEventToList(2, Status.DELETED, Status.AVAILABLE);
		addEventToList(3, Status.AUTO_DELETED, Status.UNAVAILABLE);
		addEventToList(4, Status.LOCKED, Status.PENDING);
		addEventToList(5, Status.PENDING, Status.AVAILABLE);
		addEventToList(6, Status.AVAILABLE, Status.AVAILABLE);
		Mockito.doCallRealMethod().when(mockEventCRFService).removeEventCRFs(eventCRFList, updater);
		Mockito.doCallRealMethod().when(mockEventCRFService).getAllStartedEventCRFsWithStudyAndEventName(eventCRFList);
		Mockito.doCallRealMethod().when(mockEventCRFService).restoreEventCRFs(eventCRFList, updater);
	}

	private void addEventToList(int id, Status status, Status oldStatus) throws Exception {
		EventCRFBean eventCRFInstance = new EventCRFBean();
		eventCRFInstance.setId(id);
		eventCRFInstance.setStatus(status);
		eventCRFInstance.setOldStatus(oldStatus);
		eventCRFList.add(eventCRFInstance);
		Mockito.doCallRealMethod().when(mockEventCRFService).removeEventCRF(eventCRFInstance, updater);
		Mockito.doCallRealMethod().when(mockEventCRFService).restoreEventCRF(eventCRFInstance, updater);
	}

	@Test
	public void testThatGetNextEventCRFForDataEntryReturnsCorrectEventCRFId() {
		StudyEventBean currentStudyEventBean = (StudyEventBean) studyEventDao.findByPK(1);
		EventDefinitionCRFBean currentEventDefCRF = (EventDefinitionCRFBean) eventDefinitionCRFDAO.findByPK(1);
		UserAccountBean currentUser = (UserAccountBean) userAccountDAO.findByPK(1);
		StudyUserRoleBean studyUserRole = currentUser.getRoleByStudy(1);
		StudyBean currentStudy = (StudyBean) studyDAO.findByPK(1);
		EventCRFBean nextEventCRF = eventCRFService.getNextEventCRFForDataEntry(currentStudyEventBean,
				currentEventDefCRF, currentUser, studyUserRole, currentStudy);
		assertEquals(13, nextEventCRF.getId());
	}

	@Test
	public void testThatGetNextEventCRFForDataEntryReturnsCorrectEventDefinitionCRFId() {
		StudyEventBean currentStudyEventBean = (StudyEventBean) studyEventDao.findByPK(1);
		EventDefinitionCRFBean currentEventDefCRF = (EventDefinitionCRFBean) eventDefinitionCRFDAO.findByPK(1);
		UserAccountBean currentUser = (UserAccountBean) userAccountDAO.findByPK(1);
		StudyUserRoleBean studyUserRole = currentUser.getRoleByStudy(1);
		StudyBean currentStudy = (StudyBean) studyDAO.findByPK(1);
		EventCRFBean nextEventCRF = eventCRFService.getNextEventCRFForDataEntry(currentStudyEventBean,
				currentEventDefCRF, currentUser, studyUserRole, currentStudy);
		assertEquals(2, nextEventCRF.getEventDefinitionCrf().getId());
	}

	@Test
	public void testThatGetNextEventCRFForDataEntryReturnsCorrectStudyEventId() {
		StudyEventBean currentStudyEventBean = (StudyEventBean) studyEventDao.findByPK(1);
		EventDefinitionCRFBean currentEventDefCRF = (EventDefinitionCRFBean) eventDefinitionCRFDAO.findByPK(1);
		UserAccountBean currentUser = (UserAccountBean) userAccountDAO.findByPK(1);
		StudyUserRoleBean studyUserRole = currentUser.getRoleByStudy(1);
		StudyBean currentStudy = (StudyBean) studyDAO.findByPK(1);
		EventCRFBean nextEventCRF = eventCRFService.getNextEventCRFForDataEntry(currentStudyEventBean,
				currentEventDefCRF, currentUser, studyUserRole, currentStudy);
		assertEquals(1, nextEventCRF.getStudyEventBean().getId());
	}

	@Test
	public void testThatGetNextEventCRFForDataEntryReturnsNullOnLastCRF() {
		StudyEventBean currentStudyEventBean = (StudyEventBean) studyEventDao.findByPK(4);
		EventDefinitionCRFBean currentEventDefCRF = (EventDefinitionCRFBean) eventDefinitionCRFDAO.findByPK(4);
		UserAccountBean currentUser = (UserAccountBean) userAccountDAO.findByPK(1);
		StudyUserRoleBean studyUserRole = currentUser.getRoleByStudy(1);
		StudyBean currentStudy = (StudyBean) studyDAO.findByPK(1);
		EventCRFBean nextEventCRF = eventCRFService.getNextEventCRFForDataEntry(currentStudyEventBean,
				currentEventDefCRF, currentUser, studyUserRole, currentStudy);
		assertNull(nextEventCRF);
	}

	@Test
	public void testThatGetAllStartedEventCRFsWithStudyAndEventNameReturnsCorrectResult() {
		EventCRFBean newEventCRF = new EventCRFBean();
		newEventCRF.setNotStarted(false);
		newEventCRF.setStudySubjectId(1);
		eventCRFList.add(newEventCRF);
		List<EventCRFBean> eventCRFBeanList = mockEventCRFService
				.getAllStartedEventCRFsWithStudyAndEventName(eventCRFList);
		assertEquals(1, eventCRFBeanList.size());
	}

	@Test
	public void testThatGetAllStartedEventCRFsWithStudyAndEventNameReturnsCorrectSubjectName() {
		EventCRFBean newEventCRF = new EventCRFBean();
		newEventCRF.setNotStarted(false);
		newEventCRF.setStudySubjectId(1);
		eventCRFList.add(newEventCRF);
		List<EventCRFBean> eventCRFBeanList = mockEventCRFService
				.getAllStartedEventCRFsWithStudyAndEventName(eventCRFList);
		assertEquals("TestSubject1", eventCRFBeanList.get(0).getStudySubjectName());
	}

	@Test
	public void testThatGetAllStartedEventCRFsWithStudyAndEventNameReturnsCorrectStudyName() {
		EventCRFBean newEventCRF = new EventCRFBean();
		newEventCRF.setNotStarted(false);
		newEventCRF.setStudySubjectId(1);
		eventCRFList.add(newEventCRF);
		List<EventCRFBean> eventCRFBeanList = mockEventCRFService
				.getAllStartedEventCRFsWithStudyAndEventName(eventCRFList);
		assertEquals("TestStudy1", eventCRFBeanList.get(0).getStudyName());
	}
}
