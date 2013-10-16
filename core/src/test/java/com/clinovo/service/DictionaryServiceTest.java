package com.clinovo.service;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.junit.Test;

import com.clinovo.exception.CodeException;
import com.clinovo.model.Dictionary;

public class DictionaryServiceTest extends DefaultAppContextTest {
	
	@Test
	public void testThatFindDictionaryDoesNotReturnNull() {
		assertNotNull(dictionaryService.findDictionary(1));
	}
	
	@Test
	public void testThatFindDictionaryReturnsDictionaryWithName() {
		assertEquals("Test Dictionary 2", dictionaryService.findDictionary(2).getName());
	}
	
	@Test
	public void testThatFindDictionaryReturnsDictionaryWithDescription() {
		assertEquals("This is a second test dictionary", dictionaryService.findDictionary(2).getDescription());
	}
	
	@Test
	public void testThatFindAllDoesNotReturnNull() {
		assertNotNull(dictionaryService.findAll());
	}
	
	@Test
	public void testThatFindAllReturnsAllAvailableDictionaries() {
		assertEquals(2, dictionaryService.findAll().size());
	}
	
	@Test
	public void testThatFindDictionaryByNameDoesNotReturnNull() {
		assertNotNull(dictionaryService.findDictionary("Test Dictionary"));
	}
	
	@Test
	public void testThatFindDictionaryByNameReturnsValidDictionary() {
		
		assertEquals(new Integer(2), dictionaryService.findDictionary("Test Dictionary 2").getId());
	}
	
	@Test 
	public void testThatSaveDictionaryAddsDictionaryReturnsValidDictionary() {
		
		assertNotNull(dictionaryService.saveDictionary(new Dictionary()));
	}
	
	@Test
	public void testThatCreateDictionaryDoesNotReturnNull() throws CodeException {

		StudyBean study = (StudyBean) studyDAO.findByPK(1);
		Dictionary dictionary = dictionaryService.createDictionary("name", study);
		assertNotNull(dictionary);

	}

	@Test
	public void testThatCreateDictionaryCreatesADictionaryWithCorrectName() throws CodeException {

		StudyBean study = (StudyBean) studyDAO.findByPK(1);
		dictionaryService.createDictionary("name", study);
		
		Dictionary newDictionary = dictionaryService.findDictionary("name");
		assertEquals("name", newDictionary.getName());

	}
	
	@Test
	public void testThatCreateDictionaryCreatesADictionaryWithCorrectStudy() throws CodeException {

		StudyBean study = (StudyBean) studyDAO.findByPK(1);
		dictionaryService.createDictionary("name", study);
		
		Dictionary newDictionary = dictionaryService.findDictionary("name");
		assertEquals(1, newDictionary.getStudy());

	}
	
	@Test(expected = CodeException.class)
	public void testThatCreatingSimilarNamedDictionaryRaisesException() throws CodeException {
		dictionaryService.createDictionary("Test Dictionary", null);
	}
	
	@Test
	public void testThatFindByStudyDoesNotReturnNull() {
		assertNotNull(dictionaryService.findByStudy(2));
	}
	
	@Test
	public void testThatFindByStudyReturnsDictionaryWithCorrectName() {
		
		Dictionary dictionary = dictionaryService.findByStudy(1);
		
		assertEquals("Test Dictionary", dictionary.getName());
	}
	
	@Test
	public void testThatFindByStudyReturnsDictionaryWithCorrectStudy() {
		
		Dictionary dictionary = dictionaryService.findByStudy(1);
		
		assertEquals(1, dictionary.getStudy());
	}
	
	@Test 
	public void testThatSaveDictionaryAddsDictionaryReturnsValidDictionaryWithId() {
		
		assertNotNull(dictionaryService.saveDictionary(new Dictionary()).getId());
	}
	
	@Test 
	public void testThatSaveDictionaryAddsDictionaryToDB() {
		
		dictionaryService.saveDictionary(new Dictionary());
		assertEquals(3, dictionaryService.findAll().size());
	}
	
	@Test
	public void testThatDeleteDictionaryRemovesDictionary() {
		
		dictionaryService.deleteDictionary(dictionaryService.findDictionary(1));
		assertEquals(1, dictionaryService.findAll().size());
	}
}
