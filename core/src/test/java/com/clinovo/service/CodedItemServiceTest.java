package com.clinovo.service;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
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
	public void testThatFindCodedItemReturnsCodedItemWithVerbatimTerm() {
		assertEquals("some-verbatim-term-3", codedItemService.findCodedItem(3).getVerbatimTerm());
	}

	@Test
	public void testThatFindCodedItemsByVerbatimTermDoesNotReturnNull() {
		assertNotNull(codedItemService.findCodedItemsByVerbatimTerm("some-verbatim-term"));
	}

	@Test
	public void testThatFindCodedItemsByVerbatimTermReturnsCorrectNumberOfItems() {
		assertEquals(1, codedItemService.findCodedItemsByVerbatimTerm("some-verbatim-term").size());
	}

	@Test
	public void testThatFindCodedItemsByCodedTermDoesNotReturnNull() {
		assertNotNull(codedItemService.findCodedItemsByCodedTerm("some-coded-term-2"));
	}

	@Test
	public void testThatFindCodedItemsByCodedTermReturnAllMappedItems() {
		assertEquals(1, codedItemService.findCodedItemsByCodedTerm("some-coded-term").size());
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
	public void testThatFindCodedItensByStatusReturnsAllStatusMappedItem() {
		assertEquals(2, codedItemService.findCodedItemsByStatus(CodeStatus.NOT_CODED).size());
	}

	@Test
	public void testThatFindByItemIdDoesNotReturnNull() {
		assertNotNull(codedItemService.findByItem(1));
	}

	@Test
	public void testThatFindByItemIdReturnsCodedItemWithDictionary() {
		assertEquals(codedItemService.findByItem(2).get(0).getDictionary(), "some-dictionary-2");
	}

	@Test
	public void testThatFindByItemIdReturnsCodedItemWithVerbatimTerm() {
		assertEquals(codedItemService.findByItem(3).get(0).getVerbatimTerm(), "some-verbatim-term-3");
	}

	@Test
	public void testThatFindByItemIdReturnsCodedItemWithEventCRFId() {
		assertNotNull(codedItemService.findByItem(1).get(0).getEventCrfId());
	}
	
	@Test
	public void testThatFindByItemIdReturnsCodedItemWithValidEventCRFId() {
		assertEquals(1, codedItemService.findByItem(2).get(0).getEventCrfId());
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
	public void testThatFindByItemDataDoesNotReturnNull() {
		assertNotNull(codedItemService.findByItemData(1));
	}
	
	@Test
	public void testThatFindByItemDataReturnsCodedItemWithCorrectDictionary() {
		assertEquals("some-dictionary-2", codedItemService.findByItemData(2).getDictionary());
	}
	
	@Test
	public void testThatFindByItemDataReturnsCodedItemWithCorrectVerbatimTerm() {
		assertEquals("some-verbatim-term-3", codedItemService.findByItemData(3).getVerbatimTerm());
	}
	
	@Test
	public void testThatFindByItemDataReturnsCodedItemWithCorrectCodedTerm() {
		assertEquals("some-coded-term-3", codedItemService.findByItemData(3).getCodedTerm());	
	}
	
	@Test
	public void testThatFindByItemDataReturnsCodedItemWithCorrectItemId() {
		assertEquals(4, codedItemService.findByItemData(4).getItemId());
	}
	
	@Test
	public void testThatSaveCodedItemDoesNotReturnNull() throws Exception {
		
		CodedItem codedItem = new CodedItem();
		codedItem.setItemId(31);
		codedItem.setEventCrfId(2);
		codedItem.setCodedTerm("modified-coded-term");
		
		assertNotNull(codedItemService.saveCodedItem(codedItem));
	}

	@Test
	public void testThatSaveCodedItemReturnsValidCodedItem() throws Exception {

		// Valid due to Id
		CodedItem codedItem = new CodedItem();
		codedItem.setItemId(32);
		codedItem.setEventCrfId(2);
		codedItem.setCodedTerm("modified-coded-term");

		assertNotNull(codedItemService.saveCodedItem(codedItem).getId());
	}

	@Test
	public void testThatSaveCodedItemUpdatesItemDataValue() throws Exception {
		
		// Valid due to Id
		CodedItem codedItem = new CodedItem();
		codedItem.setItemId(32);
		codedItem.setEventCrfId(2);
		codedItem.setCodedTerm("modified-coded-term");
		
		// Simulate save
		codedItemService.saveCodedItem(codedItem);
		
		ItemDataDAO itemDataDAO = new ItemDataDAO(dataSource);
		
		assertEquals("modified-coded-term", itemDataDAO.findByItemIdAndEventCRFId(32, 2).getValue());
	}
	
	@Test
	public void testThatSaveCodedItemPersistsNewCodedItemToDB() throws Exception {

		CodedItem codedItem = new CodedItem();
		codedItem.setItemId(33);
		codedItem.setEventCrfId(2);
		codedItem.setCodedTerm("modified-coded-term");

		codedItemService.saveCodedItem(codedItem);
		assertEquals(5, codedItemService.findAll().size());
	}

	@Test
	public void testThatDeleteRemovesCodedItemFromDB() throws Exception {

		codedItemService.deleteCodedItem(codedItemService.findCodedItem(1));
		assertEquals(3, codedItemService.findAll().size());
	}
}
