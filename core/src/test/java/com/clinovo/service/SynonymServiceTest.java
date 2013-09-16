package com.clinovo.service;

import org.akaza.openclinica.DefaultAppContextTest;
import org.junit.Test;

import com.clinovo.model.Synonym;

public class SynonymServiceTest extends DefaultAppContextTest {

	@Test
	public void testThatFindSynonymDoesNotReturnNull() {

		assertNotNull(synonymService.findSynonym(1));
	}

	@Test
	public void testThatFindSynonymReturnsSynonymWithName() {
		assertEquals("some synonym 2", synonymService.findSynonym(2).getName());
	}

	@Test
	public void testThatFindSynonymReturnsSynonymWithDictionary() {
		assertNotNull(synonymService.findSynonym(3).getDictionary());
	}

	@Test
	public void testThatFindSynonymReturnsSynonymWithDictionaryWithValidId() {
		assertEquals(new Integer(1), synonymService.findSynonym(1).getDictionary().getId());
	}

	@Test
	public void testThatFindSynonymByNameDoesNotReturnNull() {
		assertNotNull(synonymService.findSynonym("some synonym"));
	}

	@Test
	public void testThatFindSynonymByNameReturnsSynonymWithCode() {
		assertEquals("SOME-CODE-2", synonymService.findSynonym("some synonym 2").getCode());
	}

	@Test
	public void testThatFindSynonymByNameReturnsSynonymWithDictionary() {
		assertEquals("Test Dictionary 2", synonymService.findSynonym("some synonym 3").getDictionary().getName());
	}

	@Test
	public void testThatFindAllDoesNotReturnNull() {
		
		assertNotNull(synonymService.findAll());
	}
	
	@Test
	public void testThatFindAllReturnsAllSynonymsInTheDictionaries() {
		
		assertEquals(3, synonymService.findAll().size());
	}
	
	@Test
	public void testThatFindSynonymByDictionaryDoesNotReturnNull() {
		assertNotNull(synonymService.findSynonym(dictionaryService.findDictionary("Test Dictionary")));
	}
	
	@Test
	public void testThatFindSynonymByDictionaryReturnsCorrectNumberOfSynonymMappedToADictionary() {
		
		assertEquals(2, synonymService.findSynonym(dictionaryService.findDictionary("Test Dictionary 2")).size());
	}
	
	@Test
	public void testThatSaveSynonymDoesNotReturnNull() {
		
		Synonym synon = new Synonym();
		synon.setDictionary(dictionaryService.findDictionary(1));
		
		assertNotNull(synonymService.saveSynonym(synon));
	}
	
	@Test
	public void testThatSaveSynonymReturnsSaveSynonymWithDictionary() {
	
		Synonym synon = new Synonym();
		synon.setDictionary(dictionaryService.findDictionary(2));
		
		assertEquals(new Integer(2), synonymService.saveSynonym(synon).getDictionary().getId());
	}
	
	@Test
	public void testThatSaveSynonymAddsSynonymToDB() {
		
		Synonym synon = new Synonym();
		synon.setDictionary(dictionaryService.findDictionary(2));
		
		synonymService.saveSynonym(new Synonym());
		
		assertEquals(4, synonymService.findAll().size());
		
	}
	
	@Test
	public void testThatDeleteSynonymRemovesSynonymFromDB() {
		
		synonymService.deleteSynonym(synonymService.findSynonym(2));
		
		assertEquals(2, synonymService.findAll().size());
	}
}
