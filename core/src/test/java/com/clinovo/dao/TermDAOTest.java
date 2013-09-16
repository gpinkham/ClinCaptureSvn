package com.clinovo.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.akaza.openclinica.DefaultAppContextTest;
import org.junit.Test;

import com.clinovo.model.Dictionary;
import com.clinovo.model.Term;

public class TermDAOTest extends DefaultAppContextTest {
	
	@Test
	public void testThatFindByIdDoesNotReturnNull() {
		
		assertNotNull(termDAO.findById(1));
	}
	
	@Test
	public void testThatFindByIdReturnsTermWithCorrectName() {

		Term term = termDAO.findById(1);
		assertEquals("some preferred name", term.getPreferredName());
	}

	@Test
	public void testThatFindByIdReturnsTermWithCorrectCode() {

		Term term = termDAO.findById(1);
		assertEquals("SOME-CODE", term.getCode());
	}

	@Test
	public void testThatFindByIdReturnsTermWithDictionary() {

		Term term = termDAO.findById(1);
		assertEquals(new Integer(1), term.getDictionary().getId());
	}

	@Test
	public void testThatFindByIdReturnsTermWithDictionaryWithName() {

		Term term = termDAO.findById(3);
		assertEquals("Test Dictionary 2", term.getDictionary().getName());

	}

	@Test
	public void testThatFindByNameDoesNotReturnNull() {

		assertNotNull(termDAO.findByName("some preferred name 2"));
	}

	@Test
	public void testThatFindByNameReturnsTermWithCreationDate() {

		assertNotNull(termDAO.findByName("some preferred name").getDateCreated());
	}

	@Test
	public void testThatFindByNameReturnsTermWithCorrectCreationDate() throws ParseException {

		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Date date = dateFormat.parse("19-08-2013");

		Term term = termDAO.findByName("some preferred name 3");
		assertEquals(dateFormat.format(date), dateFormat.format(term.getDateCreated()));
	}

	@Test
	public void testThatFindByCodeDoesNotReturnNull() {

		assertNotNull(termDAO.findByCode("SOME-CODE-2"));
	}

	@Test
	public void testThatFindByCodeReturnsTermWithDictionary() {

		assertEquals("Test Dictionary", termDAO.findByCode("SOME-CODE-2").getDictionary().getName());
	}

	@Test
	public void testThatFindAllDoesNotReturnNull() {

		assertNotNull(termDAO.findAll());
	}

	@Test
	public void testThatFindAllReturnsAllTerms() {

		assertEquals(3, termDAO.findAll().size());
	}

	@Test
	public void testThatFindByDictionaryDoesNotReturnNull() {
		
		Dictionary dictionary = dictionaryDAO.findById(2);
		assertNotNull(termDAO.findByDictionary(dictionary));
	}

	@Test
	public void testThatFindByDictionaryReturnsAllTerms() {
		
		Dictionary dictionary = dictionaryDAO.findById(1);
		
		assertEquals(2, termDAO.findByDictionary(dictionary).size());
	}
	
	@Test
	public void testThatSaveOrUpdateDoeNotReturnNull() {
		
		Term term = new Term();
		term.setDictionary(dictionaryDAO.findById(1));
		
		assertNotNull(termDAO.saveOrUpdate(term));
	}
	
	@Test
	public void testThatSaveOrUpdateAssignsValidId() {
		
		Term term = new Term();
		term.setDictionary(dictionaryDAO.findById(1));
		
		assertNotNull(termDAO.saveOrUpdate(term).getId());
	}
	
	@Test
	public void testThatSaveOrUpdateAddsNewTermToDB() {
		
		Term term = new Term();
		term.setDictionary(dictionaryDAO.findById(1));
		termDAO.saveOrUpdate(term);
		
		assertEquals(4, termDAO.findAll().size());
	}
	
	@Test
	public void testThatDeleteTermRemovesTermFromDB() {
		
		termDAO.deleteTerm(termDAO.findById(1));
		
		assertEquals(2, termDAO.findAll().size());
	}
	
	@Test
	public void testThatDeleteTermRemovesDeletedTerm() {
		
		termDAO.deleteTerm(termDAO.findById(1));
		
		assertNull(termDAO.findById(1));
	}
}
