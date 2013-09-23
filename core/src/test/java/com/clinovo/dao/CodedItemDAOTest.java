package com.clinovo.dao;

import org.akaza.openclinica.DefaultAppContextTest;
import org.junit.Test;

import com.clinovo.model.CodedItem;
import com.clinovo.model.Status.CodeStatus;

public class CodedItemDAOTest extends DefaultAppContextTest {

	@Test
	public void testThatFindByIdDoesNotReturnNull() {

		assertNotNull(codedItemDAO.findById(1));
	}

	@Test
	public void testThatFindByIdReturnsCodedItemWithDictionary() {
		assertEquals("some-dictionary-2", codedItemDAO.findById(2).getDictionary());
	}

	@Test
	public void testThatFindByIdReturnsCodedItemWithVertbatimTerm() {
		assertEquals("some-verbatim-term-3", codedItemDAO.findById(3).getVerbatimTerm());
	}

	@Test
	public void testThatFindByIdReturnsCodedItemWithCodedTerm() {
		assertEquals("some-coded-term", codedItemDAO.findById(1).getCodedTerm());
	}

	@Test
	public void testThatFindByIdReturnsCodedTermWithStatus() {
		assertNotNull(codedItemDAO.findById(2).getCodeStatus());
	}

	@Test
	public void testThatFindAllDoesNotReturnNull() {
		assertNotNull(codedItemDAO.findAll());
	}

	@Test
	public void testThatFindAllReturnsAllTheItemsFromTheDB() {
		assertEquals(4, codedItemDAO.findAll().size());
	}

	@Test
	public void testThatFindyVerbatimTermDoesNotReturnNull() {
		assertNotNull(codedItemDAO.findByVerbatimTerm("some-verbatim-term"));
	}

	@Test
	public void testThatFindByVerbatimTermReturnsCodedItemWithDictionary() {
		assertEquals("some-dictionary-2", codedItemDAO.findByVerbatimTerm("some-verbatim-term-2").get(0)
				.getDictionary());
	}

	@Test
	public void testThatFindByVerbatimTermReturnsCodedItemWithCodedTerm() {
		assertEquals("some-coded-term-3", codedItemDAO.findByVerbatimTerm("some-verbatim-term-3").get(0).getCodedTerm());
	}

	@Test
	public void testThatFindByCodedTermDoesNotReturnNull() {
		assertNotNull(codedItemDAO.findByCodedTerm("some-coded-term"));
	}

	@Test
	public void testThatFindByCodedTermReturnsCodedItemWithDictionary() {
		assertEquals("some-dictionary-2", codedItemDAO.findByCodedTerm("some-coded-term-2").get(0).getDictionary());
	}

	@Test
	public void testThatFindByCodedTermReturnsCodedTermWithVerbatimTerm() {
		assertEquals("some-verbatim-term-3", codedItemDAO.findByCodedTerm("some-coded-term-3").get(0).getVerbatimTerm());
	}

	@Test
	public void testThatFindByDictionaryDoesNotReturnNull() {
		assertNotNull(codedItemDAO.findByDictionary("some-dictionary-2"));
	}

	@Test
	public void testThatFindByDictionaryReturnsAllMappedItems() {
		assertEquals(2, codedItemDAO.findByDictionary("some-dictionary").size());
	}

	@Test
	public void testThatFindByStatusDoesNotReturnNull() {
		assertNotNull(codedItemDAO.findByStatus(CodeStatus.CODED));
	}

	@Test
	public void testThatFindByStatusReturnsAllMappedItems() {
		assertEquals(2, codedItemDAO.findByStatus(CodeStatus.NOT_CODED).size());
	}
	
	@Test
	public void testThatFindByItemIdDoesNotReturnNull() {
		assertNotNull(codedItemDAO.findByItemId(1));
	}
	
	@Test
	public void testThatFindByItemIdReturnsCodedItemWithDictionary() {
		assertEquals(codedItemDAO.findByItemId(2).getDictionary(), "some-dictionary-2");
	}
	
	@Test
	public void testThatFindByItemIdReturnsCodedItemWithVerbatimTerm() {
		assertEquals(codedItemDAO.findByItemId(3).getVerbatimTerm(), "some-verbatim-term-3");
	}
	
	@Test
	public void testThatFindByItemIdReturnsCodedItemWithEventCRFId() {
		assertNotNull(codedItemDAO.findByItemId(1).getEventCrfId());
	}
	
	@Test
	public void testThatFindByItemIdReturnsCodedItemWithValidEventCRFId() {
		assertEquals(1, codedItemDAO.findByItemId(2).getEventCrfId());
	}
	
	@Test
	public void testThatFindByEventCRFDoesNotReturnNull() {
		assertNotNull(codedItemDAO.findByEventCRF(1));
	}
	
	@Test
	public void testThatFindByEventCRFReturnsCorrectNumberOfMappedItems() {
		assertEquals(2, codedItemDAO.findByEventCRF(2).size());
	}
	
	@Test
	public void testThatFindByIdReturnsCodedItemWithCRFVersionId() {
		assertNotNull(codedItemDAO.findById(3).getCrfVersionId());
	}
	
	@Test
	public void testThatFindByIdReturnsCodedItemWithCorrectCRFVersionId() {
		assertEquals(2, codedItemDAO.findById(1).getCrfVersionId());
	}
	
	@Test
	public void testThatFindByCRFVersionDoesNotReturnNull() {
		assertNotNull(codedItemDAO.findByCRFVersion(1));
	}
	
	@Test
	public void testThatFindByCRFVersionReturnsCorrectNumberOfMappedItems() {
		assertEquals(2, codedItemDAO.findByCRFVersion(2).size());
	}
	
	@Test
	public void testThatSaveOrUpdatePersistsANewCodedItem() {

		CodedItem item = new CodedItem();
		codedItemDAO.saveOrUpdate(item);

		assertEquals(5, codedItemDAO.findAll().size());
	}

	@Test
	public void testThatDeleteRemovesCodedItemFromDB() {

		codedItemDAO.deleteCodedItem(codedItemDAO.findById(2));
		assertEquals(3, codedItemDAO.findAll().size());
	}

	@Test
	public void testThatDeleteCodedItemRemovesDeletedDictionary() {

		codedItemDAO.deleteCodedItem(codedItemDAO.findById(1));

		assertNull(codedItemDAO.findById(1));
	}
}
