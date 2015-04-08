package org.akaza.openclinica.view.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.service.crfdata.DynamicsMetadataService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * FormBeanUtilTest.
 */
public class FormBeanUtilTest {

	private List<ItemBean> itemBeans;
	private Map<Integer, List<ItemDataBean>> itemDataCache;
	private Map<Integer, ItemFormMetadataBean> itemFormMetadataCache;

	private ItemBean itemBean;
	private EventCRFBean eventCrfBean;
	private EventDefinitionCRFBean edcb;
	private ItemFormMetadataBean itemFormMetadataBean;

	private int sectionId;
	private int ordinal;

	private DynamicsMetadataService dynamicsMetadataService;

	@Before
	public void before() {
		dynamicsMetadataService = Mockito.mock(DynamicsMetadataService.class);

		itemBeans = new ArrayList<ItemBean>();
		itemDataCache = new HashMap<Integer, List<ItemDataBean>>();
		itemFormMetadataCache = new HashMap<Integer, ItemFormMetadataBean>();

		itemBean = new ItemBean();
		itemBean.setId(1);
		itemBeans.add(itemBean);

		itemFormMetadataBean = new ItemFormMetadataBean();
		itemFormMetadataBean.setId(1);

		itemFormMetadataCache.put(itemBean.getId(), itemFormMetadataBean);

		sectionId = 1;
		ordinal = 1;

		edcb = new EventDefinitionCRFBean();
		edcb.setId(1);
		edcb.setCrfId(1);
		edcb.setStudyId(1);

		eventCrfBean = new EventCRFBean();
		eventCrfBean.setEventDefinitionCrf(edcb);
		eventCrfBean.setStudyEventId(1);
		eventCrfBean.setCRFVersionId(1);
	}

	@Test
	public void testThatGetDisplayBeansFromItemsDoesNotThrowNPEIfListInTheItemDataCacheIsEmptyForItemBeanId() {
		itemDataCache.put(itemBean.getId(), new ArrayList<ItemDataBean>());
		FormBeanUtil.getDisplayBeansFromItems(itemBeans, itemDataCache, itemFormMetadataCache, eventCrfBean, sectionId,
				edcb, ordinal, dynamicsMetadataService);
	}

	@Test
	public void testThatGetDisplayBeansFromItemsDoesNotThrowNPEIfListInTheItemDataCacheIsNotPresentForItemBeanId() {
		itemDataCache.remove(itemBean.getId());
		FormBeanUtil.getDisplayBeansFromItems(itemBeans, itemDataCache, itemFormMetadataCache, eventCrfBean, sectionId,
				edcb, ordinal, dynamicsMetadataService);
	}
}
