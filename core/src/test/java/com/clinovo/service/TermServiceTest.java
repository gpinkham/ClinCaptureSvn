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
    public void testThatFindTermDoesNotReturnNullElementList() {

        assertNotNull(termService.findTerm(1).getTermElementList());
    }

    @Test
    public void testThatFindTermReturnsAllElementsList() {

        assertEquals(2, termService.findTerm(1).getTermElementList().size());
    }

    @Test
    public void testThatFindTermReturnsElementListWithName() {

        assertEquals("element_name_5", termService.findTerm(3).getTermElementList().get(0).getElementName());
    }

    @Test
    public void testThatFindTermReturnsTermWithHttpPath() {

        assertEquals("http://path3", termService.findTerm(3).getHttpPath());
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
	public void testThatFindTermByNameReturnsTermWithDictionary() {
		assertEquals("Test Dictionary 2", termService.findTerm("some preferred name 3").getDictionary().getName());
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
	public void testThatFindByTermAndExternalDictionaryDoesNotReturnNull() {
		
		assertNotNull(termService.findByAliasAndExternalDictionary("some pref", "icd10"));
	}
	
	@Test
	public void testThatFindByTermAndExternalDictionaryReturnsTermWithCorrectName() {
		
		assertEquals("some preferred name" ,termService.findByAliasAndExternalDictionary("some pref", "icd10").getPreferredName());
	}
	
	@Test
	public void testThatFindByTermAndExternalDictionaryReturnsTermWithCorrectExtDictionary() {
		
		assertEquals("icd10", termService.findByAliasAndExternalDictionary("some pref", "icd10").getExternalDictionaryName());
	}

	@Test
	public void testThatFindByExternalDictionaryDoesNotReturnNull() {
		assertNotNull(termService.findByExternalDictionary("medDra"));
	}
	
	@Test
	public void testThatFindByNonUniqueTermAndExternalDictionaryDoesNotReturnNullOnValidTerm() {
		assertNotNull(termService.findByNonUniqueTermAndExternalDictionary("some Pref", "icd10"));
	}
	
	@Test
	public void testThatFindByNonUniqueTermAndExternalDictionaryReturnsTermEvenIfCaseDoesnotMatch() {
		assertNotNull(termService.findByNonUniqueTermAndExternalDictionary("SOME pref 2", "medDra"));
	}
	
	@Test
	public void testThatFindByNonUniqueTermAndExternalDictionaryReturnsNullIfDictionaryDoesNotMatch() {
		assertNull(termService.findByNonUniqueTermAndExternalDictionary("SOME preferred Name 2", "1cd9"));
	}
	
	@Test
	public void testThatFindByExternalDictionaryReturnsCorrectNumberOfItems() {
		assertEquals(1, termService.findByExternalDictionary("icd9").size());
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
