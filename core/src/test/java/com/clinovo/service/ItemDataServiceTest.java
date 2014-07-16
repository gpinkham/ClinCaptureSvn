package com.clinovo.service;

import com.clinovo.service.impl.CodedItemServiceImpl;
import com.clinovo.service.impl.ItemDataServiceImpl;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;

import static org.mockito.internal.util.reflection.Whitebox.*;

import static org.junit.Assert.*;

public class ItemDataServiceTest {

	private ItemDataService itemDataService = new ItemDataServiceImpl();

	private CodedItemService mockCodedItemService;

	private ItemDataDAO mockItemDataDAO;

	private UserAccountBean updater;

	private EventCRFBean EventCRFInstance;

	List<ItemDataBean> ItemDataList;

	@Before
	public void setUp() {

		mockCodedItemService = mock(CodedItemServiceImpl.class);
		mockItemDataDAO = mock(ItemDataDAO.class);

		setInternalState(itemDataService, "codedItemService", mockCodedItemService);
		setInternalState(itemDataService, "itemDataDAO", mockItemDataDAO);

		updater = new UserAccountBean();
		updater.setId(96);

		ItemDataList = new ArrayList<ItemDataBean>();

		//0 test Item
		ItemDataBean ItemDataInstance = new ItemDataBean();
		ItemDataInstance.setId(1);
		ItemDataInstance.setStatus(Status.AVAILABLE);
		ItemDataList.add(ItemDataInstance);

		//1 test Item
		ItemDataInstance = new ItemDataBean();
		ItemDataInstance.setId(2);
		ItemDataInstance.setStatus(Status.DELETED);
		ItemDataList.add(ItemDataInstance);

		//2 test Item
		ItemDataInstance = new ItemDataBean();
		ItemDataInstance.setId(3);
		ItemDataInstance.setStatus(Status.AUTO_DELETED);
		ItemDataList.add(ItemDataInstance);

		//3 test Item
		ItemDataInstance = new ItemDataBean();
		ItemDataInstance.setId(4);
		ItemDataInstance.setStatus(Status.AVAILABLE);
		ItemDataList.add(ItemDataInstance);

		EventCRFInstance = new EventCRFBean();
		EventCRFInstance.setId(11);

		doReturn(ItemDataList).when(mockItemDataDAO).findAllByEventCRFId(EventCRFInstance.getId());
	}

	@Test
	public void testThatRemoveItemDataByEventCRFDoesRemoveAvailableItemsOnly() throws Exception {

		itemDataService.removeItemDataByEventCRF(EventCRFInstance, updater);

		assertTrue(ItemDataList.get(0).getStatus().equals(Status.AUTO_DELETED));
		assertTrue(ItemDataList.get(0).getUpdater().equals(updater));
		assertTrue(ItemDataList.get(0).getUpdatedDate().after(new Date(0)));
		verify(mockItemDataDAO).update(ItemDataList.get(0));
		verify(mockCodedItemService).findCodedItem(ItemDataList.get(0).getId());

		assertTrue(ItemDataList.get(1).getStatus().equals(Status.DELETED));
		verify(mockItemDataDAO, never()).update(ItemDataList.get(1));
		verify(mockCodedItemService).findCodedItem(ItemDataList.get(1).getId());

		assertTrue(ItemDataList.get(2).getStatus().equals(Status.AUTO_DELETED));
		verify(mockItemDataDAO, never()).update(ItemDataList.get(2));
		verify(mockCodedItemService).findCodedItem(ItemDataList.get(2).getId());

		assertTrue(ItemDataList.get(3).getStatus().equals(Status.AUTO_DELETED));
		assertTrue(ItemDataList.get(3).getUpdater().equals(updater));
		assertTrue(ItemDataList.get(3).getUpdatedDate().after(new Date(0)));
		verify(mockItemDataDAO).update(ItemDataList.get(3));
		verify(mockCodedItemService).findCodedItem(ItemDataList.get(3).getId());
	}

	@Test
	public void testThatRestoreItemDataByEventCRFDoesRestoreItemsWithStateAutoRemovedOnly() throws Exception {

		itemDataService.restoreItemDataByEventCRF(EventCRFInstance, updater);

		assertTrue(ItemDataList.get(0).getStatus().equals(Status.AVAILABLE));
		verify(mockItemDataDAO, never()).update(ItemDataList.get(0));
		verify(mockCodedItemService).findCodedItem(ItemDataList.get(0).getId());

		assertTrue(ItemDataList.get(1).getStatus().equals(Status.DELETED));
		verify(mockItemDataDAO, never()).update(ItemDataList.get(1));
		verify(mockCodedItemService).findCodedItem(ItemDataList.get(1).getId());

		assertTrue(ItemDataList.get(2).getStatus().equals(Status.AVAILABLE));
		assertTrue(ItemDataList.get(2).getUpdater().equals(updater));
		assertTrue(ItemDataList.get(2).getUpdatedDate().after(new Date(0)));
		verify(mockItemDataDAO).update(ItemDataList.get(2));
		verify(mockCodedItemService).findCodedItem(ItemDataList.get(2).getId());

		assertTrue(ItemDataList.get(3).getStatus().equals(Status.AVAILABLE));
		verify(mockItemDataDAO, never()).update(ItemDataList.get(3));
		verify(mockCodedItemService).findCodedItem(ItemDataList.get(3).getId());
	}
}
