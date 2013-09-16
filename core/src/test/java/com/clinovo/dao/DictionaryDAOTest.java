package com.clinovo.dao;

import org.akaza.openclinica.DefaultAppContextTest;
import org.junit.Test;

import com.clinovo.model.Dictionary;
import com.clinovo.model.Status.DictionaryType;

public class DictionaryDAOTest extends DefaultAppContextTest {

	@Test
	public void testThatFindByIdDoesNotReturnNull() {

		assertNotNull(dictionaryDAO.findById(1));
	}

	@Test
	public void testThatFindByIdReturnsDictionaryWithCorrectName() {

		Dictionary dictionary = dictionaryDAO.findById(1);
		assertEquals("Test Dictionary", dictionary.getName());
	}

	@Test
	public void testThatFindByIdReturnsDictionaryWithCorrectDescription() {

		Dictionary dictionary = dictionaryDAO.findById(1);
		assertEquals("This is a test dictionary", dictionary.getDescription());
	}

	@Test
	public void testThatFindByIdReturnsDictionaryWithCorrectType() {

		Dictionary dictionary = dictionaryDAO.findById(1);
		assertEquals(DictionaryType.EXTERNAL, DictionaryType.getType(dictionary.getType()));
	}

	@Test
	public void testThatFindByNameDoesNotReturnNull() {

		assertNotNull(dictionaryDAO.findByName("Test Dictionary 2"));
	}

	@Test
	public void testThatFindByNameReturnsDictionaryWithCorrectType() {

		Dictionary dictionary = dictionaryDAO.findByName("Test Dictionary 2");
		assertEquals(DictionaryType.CUSTOM, DictionaryType.getType(dictionary.getType()));
	}

	@Test
	public void testThatFindAllDoesNotReturnNull() {

		assertNotNull(dictionaryDAO.findAll());
	}

	@Test
	public void testThatFindAllReturnsAllDictionaries() {
		
		assertEquals(2, dictionaryDAO.findAll().size());
	}
	
	@Test
	public void testThatSaveOrUpdateDoeNotReturnNull() {
		
		assertNotNull(dictionaryDAO.saveOrUpdate(new Dictionary()));
	}
	
	@Test
	public void testThatSaveOrUpdateAssignsValidId() {
		
		assertNotNull(dictionaryDAO.saveOrUpdate(new Dictionary()).getId());
	}
	
	@Test
	public void testThatSaveOrUpdateAddsNewSynonymToDB() {
		
		Dictionary dic = new Dictionary();
		dictionaryDAO.saveOrUpdate(dic);
		
		assertEquals(3, dictionaryDAO.findAll().size());
	}
	
	@Test
	public void testThatDeleteDictionaryRemovesDictionaryFromDB() {
		
		dictionaryDAO.deleteDictionary(dictionaryDAO.findById(1));
		
		assertEquals(1, dictionaryDAO.findAll().size());
	}
	
	@Test
	public void testThatDeleteDictionaryRemovesDeletedDictionary() {
		
		dictionaryDAO.deleteDictionary(dictionaryDAO.findById(1));
		
		assertNull(dictionaryDAO.findById(1));
	}
}
