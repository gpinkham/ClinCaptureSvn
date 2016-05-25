package org.akaza.openclinica.bean.submit;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Display Section Bean tests.
 */
public class DisplaySectionBeanTest {

	private DisplaySectionBean displaySectionBean;

	public static final String ITEM_OID_1 = "ITEM_OID_1";
	public static final String ITEM_OID_2 = "ITEM_OID_2";
	public static final String ITEM_OID_3 = "ITEM_OID_3";

	public static final int ITEM_ID_1 = 1;
	public static final int ITEM_ID_2 = 2;
	public static final int ITEM_ID_3 = 3;

	public static final int ITEM_COLUMN_1 = 1;
	public static final int ITEM_COLUMN_2 = 1;
	public static final int ITEM_COLUMN_3 = 2;

	@Before
	public void prepare() {
		displaySectionBean = new DisplaySectionBean();
		List<DisplayItemWithGroupBean> displaysWithGroupsList = new ArrayList<DisplayItemWithGroupBean>();
		displaysWithGroupsList.add(createDisplayWithSingleItem(ITEM_OID_1, ITEM_ID_1, ITEM_COLUMN_1));
		displaysWithGroupsList.add(createDisplayWithSingleItem(ITEM_OID_2, ITEM_ID_2, ITEM_COLUMN_2));
		displaysWithGroupsList.add(createDisplayWithSingleItem(ITEM_OID_3, ITEM_ID_3, ITEM_COLUMN_3));
		displaySectionBean.setDisplayItemGroups(displaysWithGroupsList);
	}

	@Test
	public void testThatGetSingleDisplayItemByItemBeanIdReturnsCorrectItem() {
		TestCase.assertEquals(ITEM_OID_2, displaySectionBean
				.getSingleDisplayItemByItemBeanId(ITEM_ID_2).getSingleItem().getItem().getOid());
	}

	@Test
	public void testThatGetFirstDisplayItemBeanInTheRowReturnsCorrectItem() {
		DisplayItemWithGroupBean testGroup = displaySectionBean.getSingleDisplayItemByItemBeanId(ITEM_ID_3);
		TestCase.assertEquals(ITEM_OID_2, displaySectionBean
				.getFirstDisplayItemBeanInTheRow(testGroup).getItem().getOid());
	}

	private DisplayItemWithGroupBean createDisplayWithSingleItem(String itemOid, int itemId, int columnNumber) {
		ItemBean itemBean = new ItemBean();
		itemBean.setOid(itemOid);
		itemBean.setId(itemId);
		ItemFormMetadataBean itemFormMetadata = new ItemFormMetadataBean();
		itemFormMetadata.setColumnNumber(columnNumber);
		DisplayItemBean displayItem = new DisplayItemBean();
		displayItem.setItem(itemBean);
		displayItem.setMetadata(itemFormMetadata);
		DisplayItemWithGroupBean displayWithGroup = new DisplayItemWithGroupBean();
		displayWithGroup.setSingleItem(displayItem);

		return displayWithGroup;
	}
}
