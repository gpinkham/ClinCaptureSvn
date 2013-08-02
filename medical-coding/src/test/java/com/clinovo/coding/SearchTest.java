package com.clinovo.coding;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.clinovo.coding.model.Classification;
import com.clinovo.coding.source.SearchInterface;
import com.clinovo.coding.source.impl.BioPortalSearchInterface;

public class SearchTest {
	
	Search search = new Search();
	
	@Before
	public void setUp() throws Exception {
		
		SearchInterface searchInterface = Mockito.mock(BioPortalSearchInterface.class);
		
		Classification classification = new Classification();
		
		classification.setId("some-id");
		classification.setTerm("some-term");
		classification.setCode("some-code");
		classification.setDictionary("some-dictionary");
		
		List<Classification> classifications = new ArrayList<Classification>();
		
		classifications.add(classification);
		
		search.setSearchInterface(searchInterface);
		
		Mockito.when(searchInterface.search(Mockito.anyString(), Mockito.anyString())).thenReturn(classifications);
	}
	
	@Test
	public void testThatGetClassificationsDoesNotReturnNull() throws Exception {
		
		assertNotNull(search.getClassifications("some-term", "some-dictionary"));
	}
	
	@Test
	public void testThatGetClassificationsReturnsValidResultsWithId() throws Exception {
		
		assertEquals(1, search.getClassifications("term", "dictionary").size());
	}
	
	@Test
	public void testThatGetClassificationsReturnsResultWithId() throws Exception {
		
		assertEquals("some-id", search.getClassifications("some-term", "some-dic").get(0).getId());
	}
	
	@Test
	public void testThatGetClassificationsReturnsResultWithTerm() throws Exception {
		
		assertEquals("some-term", search.getClassifications("some-term", "some-dic").get(0).getTerm());
	}
	
	@Test
	public void testThatGetClassificationsReturnsResultWithCode() throws Exception {
		
		assertEquals("some-code", search.getClassifications("some-term", "some-dic").get(0).getCode());
	}
	
	@Test
	public void testThatGetClassificationsReturnsResultWithDictionary() throws Exception {
		
		assertEquals("some-dictionary", search.getClassifications("some-term", "some-dic").get(0).getDictionary());
	}
}
