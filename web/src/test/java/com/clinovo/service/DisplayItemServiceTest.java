package com.clinovo.service;

import com.clinovo.model.EDCItemMetadata;
import com.clinovo.service.impl.DisplayItemServiceImpl;
import com.clinovo.service.impl.EDCItemMetadataServiceImpl;
import junit.framework.TestCase;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * DisplayItemService Tests
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({EDCItemMetadataService.class, DisplayItemService.class})
public class DisplayItemServiceTest {

	public static final int EDC_ID = 1;
	public static final int CRF_VERSION_ID = 1;
	public static final int ITEM_ID = 1;

	EDCItemMetadataService edcItemMetadataService;

	private DisplayItemService displayItemService;

	@Before
	public void prepare() {
		displayItemService = Mockito.spy(new DisplayItemServiceImpl());
		edcItemMetadataService = Mockito.mock(EDCItemMetadataServiceImpl.class);
		Whitebox.setInternalState(displayItemService, "edcItemMetadataService", edcItemMetadataService);
		PowerMockito.when(edcItemMetadataService
				.findByCRFVersionIDEventDefinitionCRFIDAndItemID(CRF_VERSION_ID, EDC_ID, ITEM_ID))
				.thenReturn(new EDCItemMetadata());
	}

	@Test
	public void testThatPopulateGroupItemsWithEDCMetadataAddsEDCMetadata() {
		List<DisplayItemBean> displayItemBeans = new ArrayList<DisplayItemBean>();
		ItemFormMetadataBean itemFormMetadata = new ItemFormMetadataBean();
		itemFormMetadata.setCrfVersionId(CRF_VERSION_ID);
		DisplayItemBean displayItemBean = new DisplayItemBean();
		displayItemBean.setMetadata(itemFormMetadata);
		ItemBean itemBean = new ItemBean();
		itemBean.setId(ITEM_ID);
		displayItemBean.setItem(itemBean);
		displayItemBeans.add(displayItemBean);

		displayItemService.populateGroupItemsWithEDCMetadata(displayItemBeans, EDC_ID);

		TestCase.assertNotNull(displayItemBeans.get(0).getEdcItemMetadata());
	}
}
