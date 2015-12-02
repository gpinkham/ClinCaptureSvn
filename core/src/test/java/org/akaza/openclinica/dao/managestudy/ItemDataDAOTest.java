package org.akaza.openclinica.dao.managestudy;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.dao.core.TypeNames;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.junit.Before;
import org.junit.Test;

public class ItemDataDAOTest extends DefaultAppContextTest {

	private static final int ITEM_ID = 4;
	private static final int DATE_ITEM_ID = 1;
	private static final int EVENT_CRF_ID = 1;
	private static final int NEW_ITEM_ID = 35;
	private static final String ITEM_OID = "I_AGEN_PERIODSTART";
	private UserAccountBean ub;
	private StudyBean sb;
	private EventCRFBean ecBean;
	private ItemBean itemBean;

	@Before
	public void setUp() {

		ub = new UserAccountBean();
		ub.setId(1);

		sb = new StudyBean();
		sb.setId(1);

		ecBean = new EventCRFBean();
		ecBean.setId(1);

		SectionBean secBean = new SectionBean();
		secBean.setId(1);

		itemBean = new ItemBean();
		itemBean.setId(1);
	}

	@Test
	public void testThatAllExpectedTypesSetCorrectly() {

		itemDataDAO.setTypesExpected();
		int index = 1;
		assertEquals((Integer) TypeNames.INT, itemDataDAO.getTypeExpected(index++));
		assertEquals((Integer) TypeNames.INT, itemDataDAO.getTypeExpected(index++));
		assertEquals((Integer) TypeNames.INT, itemDataDAO.getTypeExpected(index++));
		assertEquals((Integer) TypeNames.INT, itemDataDAO.getTypeExpected(index++));
		assertEquals((Integer) TypeNames.STRING, itemDataDAO.getTypeExpected(index++));
		assertEquals((Integer) TypeNames.TIMESTAMP, itemDataDAO.getTypeExpected(index++));
		assertEquals((Integer) TypeNames.TIMESTAMP, itemDataDAO.getTypeExpected(index++));
		assertEquals((Integer) TypeNames.INT, itemDataDAO.getTypeExpected(index++));
		assertEquals((Integer) TypeNames.INT, itemDataDAO.getTypeExpected(index++));
		assertEquals((Integer) TypeNames.INT, itemDataDAO.getTypeExpected(index++));
		assertEquals((Integer) TypeNames.INT, itemDataDAO.getTypeExpected(index++));
		assertEquals((Integer) TypeNames.BOOL, itemDataDAO.getTypeExpected(index++));
		assertEquals((Integer) TypeNames.STRING, itemDataDAO.getTypeExpected(index++));
		assertNull(itemDataDAO.getTypeExpected(index));
	}

	@Test
	public void testThatUpdateMethodReturnsUpdatedItemDataBean() {

		ItemDataBean item = (ItemDataBean) itemDataDAO.findByPK(ITEM_ID);
		item.setValue("Updated value");
		ItemDataBean updatedItem = (ItemDataBean) itemDataDAO.update(item);

		assertNotNull(updatedItem);
		assertEquals("Updated value", updatedItem.getValue());
	}

	@Test
	public void testThatUpdateMethodWithConnectionReturnsUpdatedItemDataBean() {

		ItemDataBean item = (ItemDataBean) itemDataDAO.findByPK(ITEM_ID);
		item.setValue("Updated value");
		ItemDataBean updatedItem = (ItemDataBean) itemDataDAO.update(item, null);

		assertNotNull(updatedItem);
		assertEquals("Updated value", updatedItem.getValue());
	}

	@Test
	public void testThatUpdateValueMethodUpdatesOnlyValue() {

		int updatedOrdinal = 2;
		int oldOrdinal = 1;
		ItemDataBean item = (ItemDataBean) itemDataDAO.findByPK(ITEM_ID);

		assertEquals(oldOrdinal, item.getOrdinal());
		item.setValue("Updated value");
		item.setOrdinal(updatedOrdinal);
		itemDataDAO.updateValue(item);
		ItemDataBean updatedItem = (ItemDataBean) itemDataDAO.findByPK(ITEM_ID);

		assertNotNull(updatedItem);
		assertEquals("Updated value", updatedItem.getValue());
		assertEquals(oldOrdinal, updatedItem.getOrdinal());
	}

	@Test
	public void testThatUpdateValueForRemovedReturnsUpdatedItemDataBean() {

		ItemDataBean item = (ItemDataBean) itemDataDAO.findByPK(ITEM_ID);
		item.setValue("Updated value");
		ItemDataBean updatedItem = (ItemDataBean) itemDataDAO.updateValueForRemoved(item);

		assertNotNull(updatedItem);
		assertEquals("Updated value", updatedItem.getValue());
	}

	@Test
	public void testThatUpdateStatusMethodUpdatesItemDataBeanStatus() {
		ItemDataBean item = (ItemDataBean) itemDataDAO.findByPK(ITEM_ID);
		assertEquals(Status.AVAILABLE, item.getStatus());
		item.setStatus(Status.DELETED);
		item.setUpdater((UserAccountBean) userAccountDAO.findByPK(1));
		itemDataDAO.updateStatus(item);
		item = (ItemDataBean) itemDataDAO.findByPK(ITEM_ID);
		assertEquals(Status.DELETED, item.getStatus());
	}

	@Test
	public void testThatUpdateValueForDiffDateFormatReturnsUpdatedItemDataBean() {

		ItemDataBean item = (ItemDataBean) itemDataDAO.findByPK(DATE_ITEM_ID);
		item.setValue("2014-14-12");
		String dateFromat = "yyyy-MM-dd";

		ItemDataBean updatedItem = (ItemDataBean) itemDataDAO.updateValue(item, dateFromat);

		assertNotNull(updatedItem);
		assertEquals("2014-14-12", updatedItem.getValue());
	}

	@Test
	public void testThatUpdateValueForDiffDateFormatWithConnectionReturnsUpdatedItemDataBean() {

		ItemDataBean item = (ItemDataBean) itemDataDAO.findByPK(DATE_ITEM_ID);
		item.setValue("2014-14-12");
		String dateFromat = "yyyy-MM-dd";

		ItemDataBean updatedItem = (ItemDataBean) itemDataDAO.updateValue(item, dateFromat, null);

		assertNotNull(updatedItem);
		assertEquals("2014-14-12", updatedItem.getValue());
	}

	@Test
	public void testThatUpdateUserReturnsUpdatedItem() {

		ItemDataBean item = (ItemDataBean) itemDataDAO.findByPK(ITEM_ID);
		item.setValue("Updated value");
		item.setUpdater(ub);

		itemDataDAO.updateUser(item);
		ItemDataBean updatedItem = (ItemDataBean) itemDataDAO.findByPK(ITEM_ID);

		assertEquals("", updatedItem.getValue());
		assertEquals(ub.getId(), updatedItem.getUpdaterId());
	}

	@Test
	public void testThatCreateReturnsCreatedItemData() throws OpenClinicaException {

		ItemDataBean item = createItemDataBean();

		ItemDataBean createdItem = (ItemDataBean) itemDataDAO.create(item);
		assertNotNull(createdItem);
		assertEquals("Test value", createdItem.getValue());
	}

	@Test
	public void testThatCreateWithConnectionReturnsCreatedItemData() throws OpenClinicaException {

		ItemDataBean item = createItemDataBean();

		ItemDataBean createdItem = (ItemDataBean) itemDataDAO.create(item, null);
		assertNotNull(createdItem);
		assertEquals("Test value", createdItem.getValue());
	}

	@Test
	public void testThatUpsertReturnsCreatedItemData() throws OpenClinicaException {

		ItemDataBean item = createItemDataBean();

		ItemDataBean createdItem = (ItemDataBean) itemDataDAO.create(item);
		assertNotNull(createdItem);
		assertEquals("Test value", createdItem.getValue());
	}

	@Test
	public void testThatFormatPDateReturnsFormattedValue() {

		String value = "Mar-2014";
		String returnedValue = itemDataDAO.formatPDate(value);

		assertEquals("2014-03", returnedValue);
	}

	@Test
	public void testThatReFormatPDateReturnsFormattedValue() {

		String value = "Mar-2014";
		String returnedValue = itemDataDAO.reFormatPDate(value);

		assertEquals("Mar-2014", returnedValue);
	}

	@Test
	public void testThatGetEntityFromHashMapReturnsItemDataWithCorrectValue() {

		HashMap<String, Object> map = prepearHashMap();
		ItemDataBean createdItem = (ItemDataBean) itemDataDAO.getEntityFromHashMap(map);

		assertEquals("Test value", createdItem.getValue());
	}

	@Test
	public void testThatFindByStudyEventAndOidsFindsCorrectNumberOfItemDatas() {

		int studyEventId = 1;
		String itemOid = "I_AGEN_PERIODSTART";
		String itemGroupOid = "IG_AGEN_UNGROUPED";

		ArrayList<ItemDataBean> items = (ArrayList<ItemDataBean>) itemDataDAO.findByStudyEventAndOids(studyEventId,
				itemOid, itemGroupOid);

		assertEquals(1, items.size());
	}

	@Test
	public void testThatFindAllReturnsCorrectNumberOfItemsFromDB() {

		ArrayList<ItemDataBean> items = (ArrayList<ItemDataBean>) itemDataDAO.findAll();

		assertEquals(true, items.size() > 0);
	}

	@Test
	public void testThatFindByPkReturnsItemDataWithCorrectValue() {

		ItemDataBean item = (ItemDataBean) itemDataDAO.findByPK(DATE_ITEM_ID);

		assertEquals("07/01/2008", item.getValue());
	}

	@Test
	public void testThatDeleRemovesItemDataFromDb() {

		int itemID = 64;
		itemDataDAO.delete(itemID);
		ItemDataBean item = (ItemDataBean) itemDataDAO.findByPK(itemID);

		assertNull(item.getOwner());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testThatDeleteDnMapRemovesRowsFromDb() {

		int itemDataId = 1;
		ArrayList<DiscrepancyNoteBean> dns = (ArrayList<DiscrepancyNoteBean>) discrepancyNoteDAO
				.findAllByEntityAndColumnAndStudy(sb, "itemData", itemDataId, "value");
		assertEquals(1, dns.size());

		itemDataDAO.deleteDnMap(itemDataId);
		ArrayList<DiscrepancyNoteBean> dnsUpdated = (ArrayList<DiscrepancyNoteBean>) discrepancyNoteDAO
				.findAllByEntityAndColumnAndStudy(sb, "itemData", itemDataId, "value");
		assertEquals(0, dnsUpdated.size());
	}

	@Test
	public void testThatFindAllBySectionIdAndEventCRFIdReturnsCorrectNumber() {

		ArrayList<ItemDataBean> items = itemDataDAO.findAllBySectionIdAndEventCRFId(1, 1);
		assertEquals(true, items.size() > 0);
	}

	@Test
	public void thatFindAllActiveBySectionIdAndEventCRFIdReturnsCorrectNumber() {

		ArrayList<ItemDataBean> items = itemDataDAO.findAllActiveBySectionIdAndEventCRFId(1, 1);
		assertEquals(true, items.size() > 0);
	}

	@Test
	public void testThatFindAllByEventCRFIdReturnsItemDataFromDB() {

		assertEquals(true, itemDataDAO.findAllByEventCRFId(EVENT_CRF_ID).size() > 0);
	}

	@Test
	public void testThatFindAllByEventCRFIdAndItemIdReturnsItemDataFromDB() {

		assertEquals(true, itemDataDAO.findAllByEventCRFIdAndItemId(EVENT_CRF_ID, ITEM_ID).size() > 0);
	}

	@Test
	public void testThatFindAllByEventCRFIdAndItemIdNoStatusReturnsItemDataFromDB() {

		assertEquals(true, itemDataDAO.findAllByEventCRFIdAndItemIdNoStatus(EVENT_CRF_ID, ITEM_ID).size() > 0);
	}

	@Test
	public void testThatFindAllBlankRequiredByEventCRFIdReturnsItemDataFromDB() {

		assertEquals(false, itemDataDAO.findAllBlankRequiredByEventCRFId(EVENT_CRF_ID, 1).size() > 0);
	}

	@Test
	public void testThatUpdateStatusByEventCRFUpdatesAllItemDataRows() {

		itemDataDAO.updateStatusByEventCRF(ecBean, Status.AUTO_DELETED);
		ArrayList<ItemDataBean> items = itemDataDAO.findAllByEventCRFId(EVENT_CRF_ID);

		assertEquals(Status.AUTO_DELETED, items.get(0).getStatus());

		itemDataDAO.updateStatusByEventCRF(ecBean, Status.AVAILABLE);
	}

	@Test
	public void testThatUpdateStatusByEventCRFWithConnectionUpdatesAllItemDataRows() {

		itemDataDAO.updateStatusByEventCRF(ecBean, Status.AUTO_DELETED, null);
		ArrayList<ItemDataBean> items = itemDataDAO.findAllByEventCRFId(EVENT_CRF_ID);

		assertEquals(Status.AUTO_DELETED, items.get(0).getStatus());

		itemDataDAO.updateStatusByEventCRF(ecBean, Status.AVAILABLE);
	}

	@Test
	public void testThatFindByItemIdAndEventCRFIdReturnsItemDataWithCorrectValue() {

		assertEquals("07/01/2008", itemDataDAO.findByItemIdAndEventCRFId(1, EVENT_CRF_ID).getValue());
	}

	@Test
	public void testThatFindByItemIdAndEventCRFIdAndOrdinalReturnsItemWithCorrectValue() {

		assertEquals("07/01/2008", itemDataDAO.findByItemIdAndEventCRFIdAndOrdinal(1, EVENT_CRF_ID, 1).getValue());
	}

	@Test
	public void testThatFindAllRequiredByEventCRFIdReturnsItemDatasFromDB() {

		assertEquals(true, itemDataDAO.findAllRequiredByEventCRFId(ecBean) > 0);
	}

	@Test
	public void testThatGetMaxOrdinalForGroupByItemAndEventCrfReturnsCorrectNumber() {

		assertEquals(1, itemDataDAO.getMaxOrdinalForGroupByItemAndEventCrf(itemBean, ecBean));
	}

	@Test
	public void testThatFindValuesByItemOIDReturnsCorrectValues() {

		List<String> values = new ArrayList<String>();
		values.add("07/01/2008");
		assertEquals(values, itemDataDAO.findValuesByItemOID(ITEM_OID));
	}

	@Test
	public void testThatFindByPkAndYearReturnsItemDataFromDB() {

		int itemId = 1;
		int year = 2008;
		assertEquals("07/01/2008", itemDataDAO.findByPKAndYear(itemId, year).getValue());
	}

	private ItemDataBean createItemDataBean() {

		ItemDataBean item = new ItemDataBean();
		item.setValue("Test value");
		item.setUpdater(ub);
		item.setEventCRFId(EVENT_CRF_ID);
		item.setItemId(NEW_ITEM_ID);
		item.setOwner(ub);
		item.setOrdinal(1);
		item.setStatus(Status.AVAILABLE);

		return item;
	}

	private HashMap<String, Object> prepearHashMap() {

		int itemDataId = 84;
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("item_data_id", NEW_ITEM_ID);
		map.put("event_crf_id", EVENT_CRF_ID);
		map.put("item_id", itemDataId);
		map.put("value", "Test value");
		map.put("status_id", Status.AVAILABLE.getId());
		map.put("ordinal", 1);
		map.put("owner_id", 1);
		map.put("update_id", 1);

		return map;
	}

	@Test
	public void testThatGetCountOfItemsToSDVReturnsTrue() {
		assertEquals(2, itemDataDAO.getCountOfItemsToSDV(1));
	}

	@Test
	public void testThatTransactionalSDVCrfItemsDoesNotThrowAnException() throws Exception {
		Connection con = getDataSource().getConnection();
		con.setAutoCommit(false);
		itemDataDAO.sdvCrfItems(1, ub.getId(), true, con);
		con.commit();
	}

	@Test
	public void testThatSDVCrfItemsReturnsTrue() {
		assertTrue(itemDataDAO.sdvCrfItems(1, ub.getId(), true));
	}

	@Test
	public void testThatSDVItemsReturnsTrue() {
		assertTrue(itemDataDAO.sdvItems(Arrays.asList(1, 2, 3), ub.getId(), true));
	}
}