package com.clinovo.service;

import java.util.ArrayList;
import java.util.List;

import com.clinovo.model.CodedItemElement;
import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.clinovo.model.CodedItem;
import com.clinovo.model.Status.CodeStatus;

public class CodedItemServiceTest extends DefaultAppContextTest {

	@Before
	public void setUp() {
		
		// I know, but is there a better way?
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken("BB", "is dreaming about halle berry"));

	}

	@Test
	public void testThatFindAllDoesNotReturnNull() throws Exception {
		assertNotNull(codedItemService.findAll());
	}

	@Test
	public void testThatFindAllReturnsAllDBItems() throws Exception {
		assertEquals(4, codedItemService.findAll().size());
	}

	@Test
	public void testThatFindCodedItemDoesNotReturnNull() {
		assertNotNull(codedItemService.findCodedItem(1));
	}

	@Test
	public void testThatFindCodedItemReturnsCodedItemWithDictionary() {
		assertEquals("some-dictionary-2", codedItemService.findCodedItem(2).getDictionary());
	}

	@Test
	public void testThatFindCodedItemsByDictionaryDoesNotReturnNull() {
		assertNotNull(codedItemService.findCodedItemsByDictionary("some-dictionary-2"));
	}

	@Test
	public void testThatFindCodedItemsByDictionaryReturnsAllMappedItems() {
		assertEquals(2, codedItemService.findCodedItemsByDictionary("some-dictionary").size());
	}

	@Test
	public void testThatFindCodedItemsByStatusDoesNotReturnNull() {
		assertNotNull(codedItemService.findCodedItemsByStatus(CodeStatus.CODED));
	}

	@Test
	public void testThatFindCodedItemsByStatusReturnsAllStatusMappedItem() {
		assertEquals(2, codedItemService.findCodedItemsByStatus(CodeStatus.NOT_CODED).size());
	}

	@Test
	public void testThatFindByEventCRFDoesNotReturnNull() {
		assertNotNull(codedItemService.findByEventCRF(1));
	}
	
	@Test
	public void testThatFindByEventCRFReturnsCorrectNumberOfMappedItems() {
		assertEquals(2, codedItemService.findByEventCRF(2).size());
	}
	
	@Test
	public void testThatFindByIdReturnsCodedItemWithCRFVersionId() {
		assertNotNull(codedItemService.findCodedItem(3).getCrfVersionId());
	}
	
	@Test
	public void testThatFindByIdReturnsCodedItemWithCorrectCRFVersionId() {
		assertEquals(1, codedItemService.findCodedItem(4).getCrfVersionId());
	}
	
	@Test
	public void testThatFindByCRFVersionDoesNotReturnNull() {
		assertNotNull(codedItemService.findByCRFVersion(1));
	}
	
	@Test
	public void testThatFindByCRFVersionReturnsCorrectNumberOfMappedItems() {
		assertEquals(2, codedItemService.findByCRFVersion(2).size());
	}

	@Test
	public void testThatFindByScopeDoesNotReturnNull() {
		assertNotNull(codedItemService.findByStudyAndSite(2, 4));
	}

	@Test
	public void testThatFindByScopeDoesReturnsCorrectNumberOfItems() {
		assertEquals(2, codedItemService.findByStudyAndSite(2, 4).size());
	}
	
  	@Test
 	public void testThatDeleteByVersionRemovesTheCodedItemsFromTheDB() {
 		
 		codedItemService.deleteByCRFVersion(2);
 		assertEquals(0, codedItemService.findByStudyAndSite(1, 3).size());
 	}
 	
 	@Test
 	public void testThatRemoveByVersionRemovesTheCodedItemsFromReturnedList() {
 		
 		codedItemService.removeByCRFVersion(1);
 		assertEquals(0, codedItemService.findByStudyAndSite(2, 4).size());
 	}
 	
 	@Test
 	public void testThatRemoveByVersionUpdatedCodedItemStatusToDeleted() {
 		
 		codedItemService.removeByCRFVersion(2);
 		
 		List<CodedItem> removeCodedItems = codedItemService.findCodedItemsByStatus(CodeStatus.REMOVED);
 		assertEquals(2, removeCodedItems.size());
 	}
 	
	@Test
	public void testThatSaveCodedItemDoesNotReturnNull() throws Exception {
		
		ItemDataBean itemData = (ItemDataBean) itemDataDAO.findByPK(1);
		
		CodedItem codedItem = new CodedItem();
        List<CodedItemElement> codedItemElementList = new ArrayList<CodedItemElement>();

        codedItemElementList.add(new CodedItemElement(1, "itemName", "itemCode"));
        codedItemElementList.add(new CodedItemElement(1, "itemName1", "itemCode1"));
        codedItem.setCodedItemElements(codedItemElementList);
		codedItem.setItemId(itemData.getItemId());
		codedItem.setEventCrfId(itemData.getEventCRFId());
		
		assertNotNull(codedItemService.saveCodedItem(codedItem));
	}

	@Test
	public void testThatSaveCodedItemReturnsValidCodedItem() throws Exception {
		
		ItemDataBean itemData = (ItemDataBean) itemDataDAO.findByPK(1);
		
		CodedItem codedItem = new CodedItem();

        List<CodedItemElement> codedItemElementList = new ArrayList<CodedItemElement>();

        codedItemElementList.add(new CodedItemElement(1, "itemName2", "itemCode2"));
        codedItemElementList.add(new CodedItemElement(1, "itemName3", "itemCode3"));
        codedItem.setCodedItemElements(codedItemElementList);
		codedItem.setItemId(itemData.getItemId());
		codedItem.setEventCrfId(itemData.getEventCRFId());

		assertNotNull(codedItemService.saveCodedItem(codedItem).getId());
	}

	@Test
	public void testThatSaveCodedItemUpdatesItemDataValue() throws Exception {
		
		ItemDataBean itemData = (ItemDataBean) itemDataDAO.findByPK(1);
		
		CodedItem codedItem = new CodedItem();
        List<CodedItemElement> codedItemElementList = new ArrayList<CodedItemElement>();

        codedItemElementList.add(new CodedItemElement(1, "itemName4", "itemCode4"));
        codedItemElementList.add(new CodedItemElement(1, "itemName5", "itemCode5"));
        codedItem.setCodedItemElements(codedItemElementList);

		codedItem.setItemId(itemData.getItemId());
		codedItem.setEventCrfId(itemData.getEventCRFId());
		
		// Simulate save
		codedItemService.saveCodedItem(codedItem);
		
		// Item data value should match coded item codedTerm
		assertEquals("itemCode5", ((ItemDataBean)itemDataDAO.findByPK(1)).getValue());
	}
	
	@Test
	public void testThatSaveCodedItemPersistsNewCodedItemToDB() throws Exception {

		ItemDataBean itemData = (ItemDataBean) itemDataDAO.findByPK(1);
		
		CodedItem codedItem = new CodedItem();
        List<CodedItemElement> codedItemElementList = new ArrayList<CodedItemElement>();

        codedItemElementList.add(new CodedItemElement(1, "itemName6", "itemCode6"));
        codedItemElementList.add(new CodedItemElement(1, "itemName7", "itemCode7"));

        codedItem.setCodedItemElements(codedItemElementList);
        codedItem.setItemId(itemData.getItemId());
		codedItem.setEventCrfId(itemData.getEventCRFId());

		codedItemService.saveCodedItem(codedItem);
		assertEquals(5, codedItemService.findAll().size());
	}

	@Test
	public void testThatDeleteRemovesCodedItemFromDB() throws Exception {

		codedItemService.deleteCodedItem(codedItemService.findCodedItem(1));
		assertEquals(3, codedItemService.findAll().size());
	}

    @Test
    public void testThatSaveCodedItemReturnsValidCodedItemElementsList() throws Exception {

        ItemDataBean itemData = (ItemDataBean) itemDataDAO.findByPK(8);

        CodedItem codedItem = new CodedItem();

        List<CodedItemElement> codedItemElementList = new ArrayList<CodedItemElement>();

        codedItemElementList.add(new CodedItemElement(8, "itemName2", "itemCode2"));
        codedItemElementList.add(new CodedItemElement(8, "itemName3", "itemCode3"));
        codedItem.setCodedItemElements(codedItemElementList);
        codedItem.setItemId(itemData.getId());
        codedItem.setEventCrfId(itemData.getEventCRFId());

        assertEquals(2,codedItemService.saveCodedItem(codedItem).getCodedItemElements().size());
    }


}
