package com.clinovo.coding;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import com.clinovo.coding.model.ClassificationElement;
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
        ClassificationElement classificationElement = new ClassificationElement();

        classificationElement.setCodeName("some-codename");
        classificationElement.setElementName("some-elementname");

		classification.setHttpPath("http://test.ru");
        classification.addClassificationElement(classificationElement);

		
		List<Classification> classifications = new ArrayList<Classification>();
		
		classifications.add(classification);
		
		search.setSearchInterface(searchInterface);
		
		Mockito.when(searchInterface.search(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(classifications);
	}
	
	@Test
	public void testThatGetClassificationsDoesNotReturnNull() throws Exception {
		
		assertNotNull(search.getClassifications("some-term", "some-dictionary", "http://1.1.1.1", "api key"));
	}
	
	@Test
	public void testThatGetClassificationsReturnsValidResultsWithId() throws Exception {
		
		assertEquals(1, search.getClassifications("term", "dictionary", "http://1.1.1.1", "api key").size());
	}

	@Test
	public void testThatGetClassificationsReturnsResultWithHttpPath() throws Exception {

		assertEquals("http://test.ru", search.getClassifications("some-term", "some-dic", "http://1.1.1.1", "api key").get(0).getHttpPath());
	}


	@Test
	public void testThatGetClassificationsReturnsResultWithCodeName() throws Exception {
		
		assertEquals("some-codename", search.getClassifications("some-term", "some-dic", "http://1.1.1.1", "api key").get(0).getClassificationElement().get(0).getCodeName());
	}

	@Test
	public void testThatGetClassificationsReturnsResultWithElementName() throws Exception {
		
		assertEquals("some-elementname", search.getClassifications("some-term", "some-dic", "http://1.1.1.1", "api key").get(0).getClassificationElement().get(0).getElementName());
	}

    @Test
    public void testThatGetClassificationReturnsResultWithEmptyCodeValue() throws Exception {

        assertEquals("", search.getClassifications("some-term", "some-dic", "http://1.1.1.1", "api key").get(0).getClassificationElement().get(0).getCodeValue());
    }
}
