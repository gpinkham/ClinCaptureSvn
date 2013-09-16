package com.clinovo.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.akaza.openclinica.DefaultAppContextTest;
import org.junit.Test;

import com.clinovo.model.Dictionary;
import com.clinovo.model.Synonym;

public class SynonymDAOTest extends DefaultAppContextTest {

	@Test
	public void testThatFindByIdDoesNotReturnNull() {

		assertNotNull(synonymDAO.findById(1));
	}

	@Test
	public void testThatFindByIdReturnsSynonymWithCorrectName() {

		Synonym synonym = synonymDAO.findById(1);
		assertEquals("some synonym", synonym.getName());
	}

	@Test
	public void testThatFindByIdReturnsSynonymWithCorrectCode() {

		Synonym synonym = synonymDAO.findById(1);
		assertEquals("SOME-CODE", synonym.getCode());
	}

	@Test
	public void testThatFindByIdReturnsSynonymWithDictionary() {

		Synonym synonym = synonymDAO.findById(1);
		assertEquals(new Integer(1), synonym.getDictionary().getId());
	}

	@Test
	public void testThatFindByIdReturnsSynonymWithDictionaryWithName() {

		Synonym synonym = synonymDAO.findById(3);
		assertEquals("Test Dictionary 2", synonym.getDictionary().getName());

	}

	@Test
	public void testThatFindByNameDoesNotReturnNull() {

		assertNotNull(synonymDAO.findByName("some synonym"));
	}

	@Test
	public void testThatFindByNameReturnsSynonymWithCreationDate() {

		assertNotNull(synonymDAO.findByName("some synonym").getDateCreated());
	}

	@Test
	public void testThatFindByNameReturnsSynonymWithCorrectCreationDate() throws ParseException {

		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Date date = dateFormat.parse("19-08-2013");

		Synonym synonym = synonymDAO.findByName("some synonym");
		assertEquals(dateFormat.format(date), dateFormat.format(synonym.getDateCreated()));
	}

	@Test
	public void testThatFindByCodeDoesNotReturnNull() {

		assertNotNull(synonymDAO.findByCode("SOME-CODE-2"));
	}

	@Test
	public void testThatFindByCodeReturnsSynonymWithDictionary() {

		assertEquals("Test Dictionary 2", synonymDAO.findByCode("SOME-CODE-2").getDictionary().getName());
	}

	@Test
	public void testThatFindAllDoesNotReturnNull() {

		assertNotNull(synonymDAO.findAll());
	}

	@Test
	public void testThatFindAllReturnsAllSynonyms() {

		assertEquals(3, synonymDAO.findAll().size());
	}

	@Test
	public void testThatFindByDictionaryDoesNotReturnNull() {
		
		Dictionary dictionary = dictionaryDAO.findById(2);
		assertNotNull(synonymDAO.findByDictionary(dictionary));
	}

	@Test
	public void testThatFindByDictionaryReturnsAllSynonyms() {
		
		Dictionary dictionary = dictionaryDAO.findById(2);
		
		assertEquals(2, synonymDAO.findByDictionary(dictionary).size());
	}
	
	@Test
	public void testThatSaveOrUpdateDoeNotReturnNull() {
		
		Synonym synon = new Synonym();
		synon.setDictionary(dictionaryDAO.findById(1));
		
		assertNotNull(synonymDAO.saveOrUpdate(synon));
	}
	
	@Test
	public void testThatSaveOrUpdateAssignsValidId() {
		
		Synonym synon = new Synonym();
		synon.setDictionary(dictionaryDAO.findById(2));
		
		assertNotNull(synonymDAO.saveOrUpdate(synon).getId());
	}
	
	@Test
	public void testThatSaveOrUpdateAddsNewSynonymToDB() {
		
		Synonym synon = new Synonym();
		synon.setDictionary(dictionaryDAO.findById(2));
		synonymDAO.saveOrUpdate(synon);
		
		assertEquals(4, synonymDAO.findAll().size());
	}
	
	@Test
	public void testThatDeleteSynonymRemovesSynonymFromDB() {
		
		synonymDAO.deleteSynonym(synonymDAO.findById(1));
		
		assertEquals(2, synonymDAO.findAll().size());
	}
	
	@Test
	public void testThatDeleteSynonymRemovesDeletedSynonym() {
		
		synonymDAO.deleteSynonym(synonymDAO.findById(1));
		
		assertNull(synonymDAO.findById(1));
	}
}
