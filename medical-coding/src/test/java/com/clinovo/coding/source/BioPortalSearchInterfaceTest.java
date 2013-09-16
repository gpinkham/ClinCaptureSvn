package com.clinovo.coding.source;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.clinovo.BaseTest;
import com.clinovo.coding.SearchException;
import com.clinovo.coding.model.Classification;
import com.clinovo.coding.source.impl.BioPortalSearchInterface;
import com.clinovo.http.HttpTransport;

public class BioPortalSearchInterfaceTest extends BaseTest {

	BioPortalSearchInterface searchInterface = new BioPortalSearchInterface();

	@Before
	public void setUp() throws Exception {

		HttpTransport transport = Mockito.mock(HttpTransport.class);
		Mockito.when(transport.processRequest()).thenReturn(searchResult);

		searchInterface.setTransport(transport);

	}

	@Test
	public void testThatSearchDoesNotReturnNull() throws Exception {

		assertNotNull(searchInterface.search("term", "ICD10"));
	}

	@Test
	public void testThatSearchReturnsExpectedNumberOfTerms() throws Exception {

		assertEquals(3, searchInterface.search("term", "meDdRa").size());
	}

	@Test
	public void testThatSearchReturnsClassificationsEachHavingAndId() throws Exception {

		for (Classification classification : searchInterface.search("term", "ICD10")) {

			assertNotNull(classification.getId());
		}
	}

	@Test
	public void testThatSearchReturnsClassificationEachHavingACode() throws Exception {

		for (Classification classification : searchInterface.search("term", "meDdRa")) {

			assertNotNull(classification.getCode());
		}
	}

	@Test
	public void testThatSearchReturnsClassificationsEachHavingAName() throws Exception {

		for (Classification classification : searchInterface.search("term", "ICD10")) {

			assertNotNull(classification.getTerm());
		}
	}

	@Test
	public void testThatSearchReturnsClassificationsEachHavingADictionary() throws Exception {

		for (Classification classification : searchInterface.search("term", "meDdRa")) {

			assertNotNull(classification.getDictionary());
		}
	}
	
	@Test(expected=SearchException.class)
	public void testThatSearchThrowsExceptionWhenAPIKeyIsNotProvided() throws Exception {
		
		HttpTransport transport = Mockito.mock(HttpTransport.class);
		Mockito.when(transport.processRequest()).thenReturn(forbiddenResult);

		searchInterface.setTransport(transport);
		
		searchInterface.search("term", "dictionary");
	}
}
