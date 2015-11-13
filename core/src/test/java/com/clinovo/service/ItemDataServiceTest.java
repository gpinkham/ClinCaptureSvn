package com.clinovo.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

import java.util.ArrayList;
import java.util.List;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.clinovo.service.impl.CodedItemServiceImpl;
import com.clinovo.service.impl.ItemDataServiceImpl;

public class ItemDataServiceTest {

	private ItemDataService itemDataService = new ItemDataServiceImpl();

	private CodedItemService mockCodedItemService;

	private ItemDataDAO mockItemDataDAO;

	private UserAccountBean updater;

	private EventCRFBean eventCRFInstance;

	List<ItemDataBean> itemDataList;

	@Before
	public void setUp() {

		mockCodedItemService = mock(CodedItemServiceImpl.class);
		mockItemDataDAO = mock(ItemDataDAO.class);

		itemDataService = Mockito.spy(new ItemDataServiceImpl());

		setInternalState(itemDataService, "codedItemService", mockCodedItemService);
		Mockito.when(((ItemDataServiceImpl) itemDataService).getItemDataDAO()).thenReturn(mockItemDataDAO);

		updater = new UserAccountBean();
		updater.setId(96);

		itemDataList = new ArrayList<ItemDataBean>();

		// 0 test Item
		ItemDataBean itemDataInstance = new ItemDataBean();
		itemDataInstance.setId(1);
		itemDataInstance.setStatus(Status.AVAILABLE);
		itemDataList.add(itemDataInstance);

		// 1 test Item
		itemDataInstance = new ItemDataBean();
		itemDataInstance.setId(2);
		itemDataInstance.setStatus(Status.DELETED);
		itemDataList.add(itemDataInstance);

		// 2 test Item
		itemDataInstance = new ItemDataBean();
		itemDataInstance.setId(3);
		itemDataInstance.setStatus(Status.AUTO_DELETED);
		itemDataList.add(itemDataInstance);

		// 3 test Item
		itemDataInstance = new ItemDataBean();
		itemDataInstance.setId(4);
		itemDataInstance.setStatus(Status.AVAILABLE);
		itemDataList.add(itemDataInstance);

		eventCRFInstance = new EventCRFBean();
		eventCRFInstance.setId(11);

		doReturn(itemDataList).when(mockItemDataDAO).findAllByEventCRFId(eventCRFInstance.getId());
	}

	@Test
	public void testThatUpdateItemDataStatesMethodSetsDeletedStateFromEventCRFBeanCorrectly() throws Exception {

		eventCRFInstance.setStatus(Status.DELETED);
		itemDataService.updateItemDataStates(eventCRFInstance, updater);

		assertTrue(itemDataList.get(0).getStatus().equals(Status.DELETED));
		assertTrue(itemDataList.get(0).getUpdater().equals(updater));
		verify(mockItemDataDAO).update(itemDataList.get(0));
		verify(mockCodedItemService).findCodedItem(itemDataList.get(0).getId());

		assertTrue(itemDataList.get(1).getStatus().equals(Status.DELETED));
		verify(mockItemDataDAO).update(itemDataList.get(1));
		verify(mockCodedItemService).findCodedItem(itemDataList.get(1).getId());

		assertTrue(itemDataList.get(2).getStatus().equals(Status.DELETED));
		verify(mockItemDataDAO).update(itemDataList.get(2));
		verify(mockCodedItemService).findCodedItem(itemDataList.get(2).getId());

		assertTrue(itemDataList.get(3).getStatus().equals(Status.DELETED));
		assertTrue(itemDataList.get(3).getUpdater().equals(updater));
		verify(mockItemDataDAO).update(itemDataList.get(3));
		verify(mockCodedItemService).findCodedItem(itemDataList.get(3).getId());
	}

	@Test
	public void testThatUpdateItemDataStatesMethodSetsLockedStateFromEventCRFBeanCorrectly() throws Exception {

		eventCRFInstance.setStatus(Status.LOCKED);
		itemDataService.updateItemDataStates(eventCRFInstance, updater);

		assertTrue(itemDataList.get(0).getStatus().equals(Status.LOCKED));
		assertTrue(itemDataList.get(0).getUpdater().equals(updater));
		verify(mockItemDataDAO).update(itemDataList.get(0));
		verify(mockCodedItemService).findCodedItem(itemDataList.get(0).getId());

		assertTrue(itemDataList.get(1).getStatus().equals(Status.LOCKED));
		verify(mockItemDataDAO).update(itemDataList.get(1));
		verify(mockCodedItemService).findCodedItem(itemDataList.get(1).getId());

		assertTrue(itemDataList.get(2).getStatus().equals(Status.LOCKED));
		verify(mockItemDataDAO).update(itemDataList.get(2));
		verify(mockCodedItemService).findCodedItem(itemDataList.get(2).getId());

		assertTrue(itemDataList.get(3).getStatus().equals(Status.LOCKED));
		assertTrue(itemDataList.get(3).getUpdater().equals(updater));
		verify(mockItemDataDAO).update(itemDataList.get(3));
		verify(mockCodedItemService).findCodedItem(itemDataList.get(3).getId());
	}
}
