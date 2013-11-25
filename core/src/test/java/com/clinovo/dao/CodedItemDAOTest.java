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
	public void testThatFindBySubjectDoesNotReturnNull() {
		assertNotNull(codedItemDAO.findBySubject(1));
	}

	@Test
	public void testThatFindBySubjectReturnsCorrectNumberOfItems() {
		assertEquals(2, codedItemDAO.findBySubject(2).size());
	}
	
	@Test
	public void testThatFindByScopeDoesNotReturnNull() {
		assertNotNull(codedItemDAO.findByStudyAndSite(1, 3));
	}

	@Test
	public void testThatFindByScopeDoesReturnsCorrectNumberOfItems() {
		assertEquals(2, codedItemDAO.findByStudyAndSite(1, 3).size());
	}
		
	@Test
	public void testThatSaveOrUpdatePersistsANewCodedItem() {

		CodedItem codedItem = new CodedItem();

        codedItem.setSiteId(3);
		codedItem.setStudyId(1);
		codedItem.setItemId(31);
		codedItem.setEventCrfId(2);
		
		codedItemDAO.saveOrUpdate(codedItem);

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
