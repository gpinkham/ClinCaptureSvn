package com.clinovo.service;

import org.akaza.openclinica.DefaultAppContextTest;
import org.junit.Test;

import com.clinovo.exception.CodeException;
import com.clinovo.model.Term;

public class TermServiceTest extends DefaultAppContextTest {

	@Test
	public void testThatFindTermDoesNotReturnNull() {

		assertNotNull(termService.findTerm(1));
	}

	@Test
	public void testThatFindTermReturnsTermWithName() {
		assertEquals("some preferred name 2", termService.findTerm(2).getPreferredName());
	}

	@Test
	public void testThatFindTermReturnsTermWithDictionary() {
		assertNotNull(termService.findTerm(3).getDictionary());
	}

	@Test
	public void testThatFindTermReturnsTermWithDictionaryHavingValidId() {
		assertEquals(new Integer(2), termService.findTerm(3).getDictionary().getId());
	}
	
	@Test
	public void testThatFindTermByNameDoesNotReturnNull() {
		assertNotNull(termService.findTerm("some preferred name"));
	}
	
	@Test
	public void testThatFindTermByNameReturnsTermWithCode() {
		assertEquals("SOME-CODE-2", termService.findTerm("some preferred name 2").getCode());
	}
	
	@Test
	public void testThatFindTermByNameReturnsTermWithDictionary() {
		assertEquals("Test Dictionary 2", termService.findTerm("some preferred name 3").getDictionary().getName());
	}
	
	@Test
	public void testThatFindTermByCodeDoesNotReturnNull() {
		assertNotNull(termService.findTermByCode("SOME-CODE"));
	}
	
	@Test
	public void testThatFindTermByCodeReturnsTermWithDictionary() {
		assertNotNull(termService.findTermByCode("SOME-CODE-2").getDictionary());
	}
	
	@Test
	public void testThatFindTermByCodeReturnsTermWithDictionaryHavingValidId() {
		assertEquals(new Integer(2), termService.findTermByCode("SOME-CODE-3").getDictionary().getId());
	}
	
	@Test
	public void testThatFindTermByCodeReturnsCorrectTerm() {
		assertEquals("some preferred name", termService.findTermByCode("SOME-CODE").getPreferredName());
	}
	
	@Test
	public void testThatFindAllDoesNotReturnNull() {
		assertNotNull(termService.findAll());
	}
	
	@Test
	public void testThatFindAllReturnsAllTerms() {
		assertEquals(3, termService.findAll().size());
	}
	
	@Test
	public void testThatFindByDictionaryDoesNotReturnNull() {
		assertNotNull(termService.findTerm(dictionaryService.findDictionary(2)));
	}
	
	@Test
	public void testThatFindByDictionaryReturnsTheCorrectNumberOfTermsBoundToADictionary() {
		assertEquals(2, termService.findTerm(dictionaryService.findDictionary(1)).size());		
	}
	
	@Test
	public void testThatSaveTermDoesNotReturnNull() throws CodeException {
		
		Term term = new Term();
		term.setDictionary(dictionaryService.findDictionary(2));
		
		assertNotNull(termService.saveTerm(term));
	}
	
	@Test
	public void testThatSaveTermAddsTermToDB() throws CodeException {
		
		Term term = new Term();
		term.setDictionary(dictionaryService.findDictionary(2));
		
		termService.saveTerm(term);
		
		assertEquals(4, termService.findAll().size());
	}
	
	@Test(expected=CodeException.class)
	public void testThatSaveTermThrowsExceptionIfTermExists() throws CodeException {
		
		Term term = termService.findTerm(1);
		termService.saveTerm(term);
	}
	
	@Test
	public void testThatDeleteTermRemovesTermFromDB() {
		
		termService.deleteTerm(termService.findTerm(3));
		assertEquals(2, termService.findAll().size());
	}
}
