/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 * 
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer. 
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 * 
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use. 
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVOÃ¢â‚¬â„¢S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/

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
