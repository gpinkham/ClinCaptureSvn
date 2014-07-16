package com.clinovo.service;

import com.clinovo.service.impl.EventCRFServiceImpl;
import com.clinovo.service.impl.ItemDataServiceImpl;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;

import static org.mockito.internal.util.reflection.Whitebox.*;

import static org.junit.Assert.*;

public class EventCRFServiceTest {

	private EventCRFService eventCRFService = new EventCRFServiceImpl();

	private ItemDataService mockItemDataService;

	private EventCRFDAO mockEventCRFDAO;

	private UserAccountBean updater;

	List<EventCRFBean> eventCRFList;

	@Before
	public void setUp() {

		mockItemDataService = mock(ItemDataServiceImpl.class);
		mockEventCRFDAO = mock(EventCRFDAO.class);

		setInternalState(eventCRFService, "itemDataService", mockItemDataService);
		setInternalState(eventCRFService, "eventCRFDAO", mockEventCRFDAO);

		updater = new UserAccountBean();
		updater.setId(144);

		eventCRFList = new ArrayList<EventCRFBean>();

		//0 test CRF
		EventCRFBean EventCRFInstance = new EventCRFBean();
		EventCRFInstance.setId(1);
		EventCRFInstance.setStatus(Status.UNAVAILABLE);    // CRF in state COMPLETED
		eventCRFList.add(EventCRFInstance);

		//1 test CRF
		EventCRFInstance = new EventCRFBean();
		EventCRFInstance.setId(2);
		EventCRFInstance.setStatus(Status.DELETED);    // CRF in state REMOVED
		EventCRFInstance.setOldStatus(Status.AVAILABLE);    // CRF previous state was DES
		eventCRFList.add(EventCRFInstance);

		//2 test CRF
		EventCRFInstance = new EventCRFBean();
		EventCRFInstance.setId(3);
		EventCRFInstance.setStatus(Status.AUTO_DELETED);    // CRF in state AUTO_DELETED
		EventCRFInstance.setOldStatus(Status.UNAVAILABLE);    // CRF previous state was COMPLETED
		eventCRFList.add(EventCRFInstance);

		//3 test CRF
		EventCRFInstance = new EventCRFBean();
		EventCRFInstance.setId(4);
		EventCRFInstance.setStatus(Status.LOCKED);    // CRF in state LOCKED
		EventCRFInstance.setOldStatus(Status.PENDING);    // CRF previous state was DDE
		eventCRFList.add(EventCRFInstance);

		//4 test CRF
		EventCRFBean EventCRFInstance4 = new EventCRFBean();
		EventCRFInstance.setId(5);
		EventCRFInstance4.setStatus(Status.PENDING);    // CRF in state DDE
		eventCRFList.add(EventCRFInstance4);

		//5 test CRF
		EventCRFInstance = new EventCRFBean();
		EventCRFInstance.setId(6);
		EventCRFInstance.setStatus(Status.AVAILABLE);    // CRF in state DES
		eventCRFList.add(EventCRFInstance);
	}

	@Test
	public void testThatRemoveEventCRFDoesSetCRFIntoRemovedState1() throws Exception {

		EventCRFBean testEventCRF = eventCRFList.get(5);    // CRF in state DES
		Status statusBeforeRemoved = testEventCRF.getStatus();
		eventCRFService.removeEventCRF(testEventCRF, updater);

		assertTrue(testEventCRF.getStatus().equals(Status.DELETED));
		assertTrue(testEventCRF.getOldStatus().equals(statusBeforeRemoved));
		assertTrue(testEventCRF.getUpdater().equals(updater));
		assertTrue(testEventCRF.getUpdatedDate().after(new Date(0)));

		verify(mockEventCRFDAO).update(testEventCRF);
		verify(mockItemDataService).removeItemDataByEventCRF(testEventCRF, updater);
	}

	@Test
	public void testThatRemoveEventCRFDoesSetCRFIntoRemovedState2() throws Exception {

		EventCRFBean testEventCRF = eventCRFList.get(4);    // CRF in state DDE
		Status statusBeforeRemoved = testEventCRF.getStatus();
		eventCRFService.removeEventCRF(testEventCRF, updater);

		assertTrue(testEventCRF.getStatus().equals(Status.DELETED));
		assertTrue(testEventCRF.getOldStatus().equals(statusBeforeRemoved));
		assertTrue(testEventCRF.getUpdater().equals(updater));
		assertTrue(testEventCRF.getUpdatedDate().after(new Date(0)));

		verify(mockEventCRFDAO).update(testEventCRF);
		verify(mockItemDataService).removeItemDataByEventCRF(testEventCRF, updater);
	}

	@Test
	public void testThatRemoveEventCRFDoesSetCRFIntoRemovedState3() throws Exception {

		EventCRFBean testEventCRF = eventCRFList.get(0);    // CRF in state COMPLETED
		Status statusBeforeRemoved = testEventCRF.getStatus();
		eventCRFService.removeEventCRF(testEventCRF, updater);

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
		eventCRFService.removeEventCRF(testEventCRF, updater);

		verify(mockEventCRFDAO, never()).update(testEventCRF);
		verify(mockItemDataService, never()).removeItemDataByEventCRF(testEventCRF, updater);
	}

	@Test
	public void testThatRemoveEventCRFDoesNotChangeStateOfCRFWithStatusAutoRemoved() throws Exception {

		EventCRFBean testEventCRF = eventCRFList.get(2);
		eventCRFService.removeEventCRF(testEventCRF, updater);

		verify(mockEventCRFDAO, never()).update(testEventCRF);
		verify(mockItemDataService, never()).removeItemDataByEventCRF(testEventCRF, updater);
	}

	@Test
	public void testThatRemoveEventCRFDoesSetLockedCRFIntoRemovedStateWithoutUpdatingItsOldStatus() throws Exception {

		EventCRFBean testEventCRF = eventCRFList.get(3);
		eventCRFService.removeEventCRF(testEventCRF, updater);

		assertTrue(testEventCRF.getStatus().equals(Status.DELETED));
		assertTrue(!testEventCRF.getOldStatus().isLocked());
		assertTrue(testEventCRF.getUpdater().equals(updater));
		assertTrue(testEventCRF.getUpdatedDate().after(new Date(0)));

		verify(mockEventCRFDAO).update(testEventCRF);
		verify(mockItemDataService).removeItemDataByEventCRF(testEventCRF, updater);
	}

	@Test
	public void testThatSetEventCRFsToAutoRemovedStateDoesSetAvailableCRFsIntoAutoRemovedStateOnly() throws Exception {

		eventCRFService.setEventCRFsToAutoRemovedState(eventCRFList, updater);

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

		EventCRFBean testEventCRF = eventCRFList.get(1);    // CRF previous state was DES
		eventCRFService.restoreEventCRF(testEventCRF, updater);

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

		EventCRFBean testEventCRF = eventCRFList.get(2);    // CRF previous state was COMPLETED
		eventCRFService.restoreEventCRF(testEventCRF, updater);

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

		EventCRFBean testEventCRF = eventCRFList.get(5);    // CRF in state DES
		eventCRFService.restoreEventCRF(testEventCRF, updater);

		verify(mockEventCRFDAO, never()).update(testEventCRF);
		verify(mockItemDataService, never()).restoreItemDataByEventCRF(testEventCRF, updater);
	}

	@Test
	public void testThatRestoreEventCRFDoesNotChangeCRFStateIfItsNotRemoved2() throws Exception {

		EventCRFBean testEventCRF = eventCRFList.get(4);    // CRF in state DDE
		eventCRFService.restoreEventCRF(testEventCRF, updater);

		verify(mockEventCRFDAO, never()).update(testEventCRF);
		verify(mockItemDataService, never()).restoreItemDataByEventCRF(testEventCRF, updater);
	}

	@Test
	public void testThatRestoreEventCRFDoesNotChangeCRFStateIfItsNotRemoved3() throws Exception {

		EventCRFBean testEventCRF = eventCRFList.get(0);    // CRF in state COMPLETED
		eventCRFService.restoreEventCRF(testEventCRF, updater);

		verify(mockEventCRFDAO, never()).update(testEventCRF);
		verify(mockItemDataService, never()).restoreItemDataByEventCRF(testEventCRF, updater);
	}

	@Test
	public void testThatRestoreEventCRFsFromAutoRemovedStateDoesRestoreCRFsWithAutoRemovedStateOnly() throws Exception {

		eventCRFService.restoreEventCRFsFromAutoRemovedState(eventCRFList, updater);

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
}
