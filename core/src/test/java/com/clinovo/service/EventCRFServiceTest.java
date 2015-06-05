package com.clinovo.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import com.clinovo.service.impl.EventCRFServiceImpl;
import com.clinovo.service.impl.ItemDataServiceImpl;

public class EventCRFServiceTest extends DefaultAppContextTest {

	private EventCRFServiceImpl mockEventCRFService;

	private ItemDataService mockItemDataService;

	private EventCRFDAO mockEventCRFDAO;

	private UserAccountBean updater;

	List<EventCRFBean> eventCRFList;

	@Before
	public void setUp() throws Exception {

		mockEventCRFService = mock(EventCRFServiceImpl.class);
		mockItemDataService = mock(ItemDataServiceImpl.class);
		mockEventCRFDAO = mock(EventCRFDAO.class);
		setInternalState(mockEventCRFService, "itemDataService", mockItemDataService);
		Mockito.doReturn(mockEventCRFDAO).when(mockEventCRFService).getEventCRFDAO();

		updater = new UserAccountBean();
		updater.setId(144);

		eventCRFList = new ArrayList<EventCRFBean>();
		addEventToList(1, Status.UNAVAILABLE, null);
		addEventToList(2, Status.DELETED, Status.AVAILABLE);
		addEventToList(3, Status.AUTO_DELETED, Status.UNAVAILABLE);
		addEventToList(4, Status.LOCKED, Status.PENDING);
		addEventToList(5, Status.PENDING, null);
		addEventToList(6, Status.AVAILABLE, null);
		Mockito.doCallRealMethod().when(mockEventCRFService).setEventCRFsToAutoRemovedState(eventCRFList, updater);
		Mockito.doCallRealMethod().when(mockEventCRFService)
				.restoreEventCRFsFromAutoRemovedState(eventCRFList, updater);
	}

	private void addEventToList(int id, Status status, Status oldStatus) throws Exception {
		EventCRFBean eventCRFInstance = new EventCRFBean();
		eventCRFInstance.setId(id);
		eventCRFInstance.setStatus(status);
		if (oldStatus != null) {
			eventCRFInstance.setOldStatus(oldStatus);
		}
		eventCRFList.add(eventCRFInstance);
		Mockito.doCallRealMethod().when(mockEventCRFService).removeEventCRF(eventCRFInstance, updater);
		Mockito.doCallRealMethod().when(mockEventCRFService).restoreEventCRF(eventCRFInstance, updater);
	}

	@PrepareForTest(EventCRFServiceImpl.class)
	@Test
	public void testThatRemoveEventCRFDoesSetCRFIntoRemovedState1() throws Exception {

		EventCRFBean testEventCRF = eventCRFList.get(5); // CRF in state DES
		Status statusBeforeRemoved = testEventCRF.getStatus();
		mockEventCRFService.removeEventCRF(testEventCRF, updater);

		assertTrue(testEventCRF.getStatus().equals(Status.DELETED));
		assertTrue(testEventCRF.getOldStatus().equals(statusBeforeRemoved));
		assertTrue(testEventCRF.getUpdater().equals(updater));
		assertTrue(testEventCRF.getUpdatedDate().after(new Date(0)));

		verify(mockEventCRFDAO).update(testEventCRF);
		verify(mockItemDataService).removeItemDataByEventCRF(testEventCRF, updater);
	}

	@Test
	public void testThatRemoveEventCRFDoesSetCRFIntoRemovedState2() throws Exception {

		EventCRFBean testEventCRF = eventCRFList.get(4); // CRF in state DDE
		Status statusBeforeRemoved = testEventCRF.getStatus();
		mockEventCRFService.removeEventCRF(testEventCRF, updater);

		assertTrue(testEventCRF.getStatus().equals(Status.DELETED));
		assertTrue(testEventCRF.getOldStatus().equals(statusBeforeRemoved));
		assertTrue(testEventCRF.getUpdater().equals(updater));
		assertTrue(testEventCRF.getUpdatedDate().after(new Date(0)));

		verify(mockEventCRFDAO).update(testEventCRF);
		verify(mockItemDataService).removeItemDataByEventCRF(testEventCRF, updater);
	}

	@Test
	public void testThatRemoveEventCRFDoesSetCRFIntoRemovedState3() throws Exception {

		EventCRFBean testEventCRF = eventCRFList.get(0); // CRF in state COMPLETED
		Status statusBeforeRemoved = testEventCRF.getStatus();
		mockEventCRFService.removeEventCRF(testEventCRF, updater);

		assertTrue(testEventCRF.getStatus().equals(Status.DELETED));
		assertTrue(testEventCRF.getOldStatus().equals(statusBeforeRemoved));
		assertTrue(testEventCRF.getUpdater().equals(updater));
		assertTrue(testEventCRF.getUpdatedDate().after(new Date(0)));

		verify(mockEventCRFDAO).update(testEventCRF);
		verify(mockItemDataService).removeItemDataByEventCRF(testEventCRF, updater);
	}

	@Test
	public void testThatRemoveEventCRFDoesNotChangeStateOfCRFWithStatusRemoved() throws Exception {

		EventCRFBean testEventCRF = eventCRFList.get(1);
		mockEventCRFService.removeEventCRF(testEventCRF, updater);

		verify(mockEventCRFDAO, never()).update(testEventCRF);
		verify(mockItemDataService, never()).removeItemDataByEventCRF(testEventCRF, updater);
	}

	@Test
	public void testThatRemoveEventCRFDoesNotChangeStateOfCRFWithStatusAutoRemoved() throws Exception {

		EventCRFBean testEventCRF = eventCRFList.get(2);
		mockEventCRFService.removeEventCRF(testEventCRF, updater);

		verify(mockEventCRFDAO, never()).update(testEventCRF);
		verify(mockItemDataService, never()).removeItemDataByEventCRF(testEventCRF, updater);
	}

	@Test
	public void testThatRemoveEventCRFDoesSetLockedCRFIntoRemovedStateWithoutUpdatingItsOldStatus() throws Exception {

		EventCRFBean testEventCRF = eventCRFList.get(3);
		mockEventCRFService.removeEventCRF(testEventCRF, updater);

		assertTrue(testEventCRF.getStatus().equals(Status.DELETED));
		assertTrue(!testEventCRF.getOldStatus().isLocked());
		assertTrue(testEventCRF.getUpdater().equals(updater));
		assertTrue(testEventCRF.getUpdatedDate().after(new Date(0)));

		verify(mockEventCRFDAO).update(testEventCRF);
		verify(mockItemDataService).removeItemDataByEventCRF(testEventCRF, updater);
	}

	@Test
	public void testThatSetEventCRFsToAutoRemovedStateDoesSetAvailableCRFsIntoAutoRemovedStateOnly() throws Exception {

		mockEventCRFService.setEventCRFsToAutoRemovedState(eventCRFList, updater);

		assertTrue(eventCRFList.get(0).getStatus().equals(Status.AUTO_DELETED));
		verify(mockEventCRFDAO).update(eventCRFList.get(0));
		verify(mockItemDataService).removeItemDataByEventCRF(eventCRFList.get(0), updater);

		assertTrue(eventCRFList.get(1).getStatus().equals(Status.DELETED));
		verify(mockEventCRFDAO, never()).update(eventCRFList.get(1));
		verify(mockItemDataService, never()).removeItemDataByEventCRF(eventCRFList.get(1), updater);

		assertTrue(eventCRFList.get(2).getStatus().equals(Status.AUTO_DELETED));
		verify(mockEventCRFDAO, never()).update(eventCRFList.get(2));
		verify(mockItemDataService, never()).removeItemDataByEventCRF(eventCRFList.get(2), updater);

		assertTrue(eventCRFList.get(3).getStatus().equals(Status.AUTO_DELETED));
		verify(mockEventCRFDAO).update(eventCRFList.get(3));
		verify(mockItemDataService).removeItemDataByEventCRF(eventCRFList.get(3), updater);

		assertTrue(eventCRFList.get(4).getStatus().equals(Status.AUTO_DELETED));
		verify(mockEventCRFDAO).update(eventCRFList.get(4));
		verify(mockItemDataService).removeItemDataByEventCRF(eventCRFList.get(4), updater);

		assertTrue(eventCRFList.get(5).getStatus().equals(Status.AUTO_DELETED));
		verify(mockEventCRFDAO).update(eventCRFList.get(5));
		verify(mockItemDataService).removeItemDataByEventCRF(eventCRFList.get(5), updater);
	}

	@Test
	public void testThatRestoreEventCRFDoesRestoreCRFIntoPreviousState1() throws Exception {

		EventCRFBean testEventCRF = eventCRFList.get(1); // CRF previous state was DES
		mockEventCRFService.restoreEventCRF(testEventCRF, updater);

		assertTrue(testEventCRF.getStatus().equals(testEventCRF.getOldStatus()));
		assertTrue(testEventCRF.getUpdater().equals(updater));
		assertTrue(testEventCRF.getUpdatedDate().after(new Date(0)));
		assertFalse(testEventCRF.isSdvStatus());
		assertFalse(testEventCRF.isElectronicSignatureStatus());

		verify(mockEventCRFDAO).update(testEventCRF);
		verify(mockItemDataService).restoreItemDataByEventCRF(testEventCRF, updater);
	}

	@Test
	public void testThatRestoreEventCRFDoesRestoreCRFIntoPreviousState2() throws Exception {

		EventCRFBean testEventCRF = eventCRFList.get(2); // CRF previous state was COMPLETED
		mockEventCRFService.restoreEventCRF(testEventCRF, updater);

		assertTrue(testEventCRF.getStatus().equals(testEventCRF.getOldStatus()));
		assertTrue(testEventCRF.getUpdater().equals(updater));
		assertTrue(testEventCRF.getUpdatedDate().after(new Date(0)));
		assertFalse(testEventCRF.isSdvStatus());
		assertFalse(testEventCRF.isElectronicSignatureStatus());

		verify(mockEventCRFDAO).update(testEventCRF);
		verify(mockItemDataService).restoreItemDataByEventCRF(testEventCRF, updater);
	}

	@Test
	public void testThatRestoreEventCRFDoesNotChangeCRFStateIfItsNotRemoved1() throws Exception {

		EventCRFBean testEventCRF = eventCRFList.get(5); // CRF in state DES
		mockEventCRFService.restoreEventCRF(testEventCRF, updater);

		verify(mockEventCRFDAO, never()).update(testEventCRF);
		verify(mockItemDataService, never()).restoreItemDataByEventCRF(testEventCRF, updater);
	}

	@Test
	public void testThatRestoreEventCRFDoesNotChangeCRFStateIfItsNotRemoved2() throws Exception {

		EventCRFBean testEventCRF = eventCRFList.get(4); // CRF in state DDE
		mockEventCRFService.restoreEventCRF(testEventCRF, updater);

		verify(mockEventCRFDAO, never()).update(testEventCRF);
		verify(mockItemDataService, never()).restoreItemDataByEventCRF(testEventCRF, updater);
	}

	@Test
	public void testThatRestoreEventCRFDoesNotChangeCRFStateIfItsNotRemoved3() throws Exception {

		EventCRFBean testEventCRF = eventCRFList.get(0); // CRF in state COMPLETED
		mockEventCRFService.restoreEventCRF(testEventCRF, updater);

		verify(mockEventCRFDAO, never()).update(testEventCRF);
		verify(mockItemDataService, never()).restoreItemDataByEventCRF(testEventCRF, updater);
	}

	@Test
	public void testThatRestoreEventCRFsFromAutoRemovedStateDoesRestoreCRFsWithAutoRemovedStateOnly() throws Exception {

		mockEventCRFService.restoreEventCRFsFromAutoRemovedState(eventCRFList, updater);

		assertTrue(eventCRFList.get(0).getStatus().equals(Status.UNAVAILABLE));
		verify(mockEventCRFDAO, never()).update(eventCRFList.get(0));
		verify(mockItemDataService, never()).restoreItemDataByEventCRF(eventCRFList.get(0), updater);

		assertTrue(eventCRFList.get(1).getStatus().equals(Status.DELETED));
		verify(mockEventCRFDAO, never()).update(eventCRFList.get(1));
		verify(mockItemDataService, never()).restoreItemDataByEventCRF(eventCRFList.get(1), updater);

		assertTrue(eventCRFList.get(2).getStatus().equals(eventCRFList.get(2).getOldStatus()));
		verify(mockEventCRFDAO).update(eventCRFList.get(2));
		verify(mockItemDataService).restoreItemDataByEventCRF(eventCRFList.get(2), updater);

		assertTrue(eventCRFList.get(3).getStatus().equals(Status.LOCKED));
		verify(mockEventCRFDAO, never()).update(eventCRFList.get(3));
		verify(mockItemDataService, never()).restoreItemDataByEventCRF(eventCRFList.get(3), updater);

		assertTrue(eventCRFList.get(4).getStatus().equals(Status.PENDING));
		verify(mockEventCRFDAO, never()).update(eventCRFList.get(4));
		verify(mockItemDataService, never()).restoreItemDataByEventCRF(eventCRFList.get(4), updater);

		assertTrue(eventCRFList.get(5).getStatus().equals(Status.AVAILABLE));
		verify(mockEventCRFDAO, never()).update(eventCRFList.get(5));
		verify(mockItemDataService, never()).restoreItemDataByEventCRF(eventCRFList.get(5), updater);
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
}
