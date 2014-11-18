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

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO'S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/

package com.clinovo.coding.source;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.clinovo.coding.model.ClassificationElement;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.clinovo.BaseTest;
import com.clinovo.coding.model.Classification;
import com.clinovo.coding.source.impl.BioPortalSearchInterface;

import java.util.List;

public class BioPortalSearchInterfaceTest extends BaseTest {

	BioPortalSearchInterface searchInterface = Mockito.mock(BioPortalSearchInterface.class);

	@Before
	public void setUp() throws Exception {

		Mockito.doReturn(searchResult).when(searchInterface).termListRequest(Mockito.eq("Leukaemia plasmacytic (in remission)"), Mockito.anyString(), Mockito.anyString());
		Mockito.doReturn(whodSearchResult).when(searchInterface).termListRequest(Mockito.eq("Benzalkonium"), Mockito.anyString(), Mockito.anyString());
		Mockito.doReturn(ctcaeSearchResult).when(searchInterface).termListRequest(Mockito.eq("Leg Pain"), Mockito.anyString(), Mockito.anyString());
		Mockito.doReturn(treeResult).when(searchInterface).getPageDataRequest(Mockito.anyString(), Mockito.anyString());
		Mockito.doReturn(termCodeResult).when(searchInterface).getTermCodeRequest(Mockito.any(ClassificationElement.class), Mockito.eq("MEDDRA"), Mockito.anyString());
		Mockito.doReturn(ctcaeTermCodeResult).when(searchInterface).getTermCodeRequest(Mockito.any(ClassificationElement.class), Mockito.eq("CTCAE"), Mockito.anyString());

		Mockito.doCallRealMethod().when(searchInterface).search(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		Mockito.doCallRealMethod().when(searchInterface).getClassificationTerms(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		Mockito.doCallRealMethod().when(searchInterface).getClassificationCodes(Mockito.any(Classification.class), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

	}

	@Test
	public void testThatSearchDoesNotReturnNull() throws Exception {

		assertNotNull(searchInterface.search("Leukaemia plasmacytic (in remission)", "MEDDRA", "http://1.1.1.1", "api key"));
	}

	@Test
	public void testThatSearchReturnsExpectedNumberOfTerms() throws Exception {

		assertEquals(3, searchInterface.search("Leukaemia plasmacytic (in remission)", "MEDDRA", "http://1.1.1.1", "api key").size());
	}

	@Test
	public void testThatSearchReturnsClassificationEachHavingHttpPath() throws Exception {

		Classification classification = searchInterface.search("Leukaemia plasmacytic (in remission)", "MEDDRA", "http://1.1.1.1", "api key").get(0);
		assertEquals("http://purl.bioontology.org/ontology/MDR/10024319", classification.getHttpPath());
	}

	@Test
	public void testThatSearchReturnsExpectedNumberOfClassificationElements() throws Exception {

		assertEquals(1, searchInterface.search("Leukaemia plasmacytic (in remission)", "MEDDRA", "http://1.1.1.1", "api key").get(0).getClassificationElement().size());
	}

	@Test
	public void testThatSearchReturnsClassificationElementsHavingCodeNames() throws Exception {

		List<Classification> classifications = searchInterface.search("Leukaemia plasmacytic (in remission)", "MEDDRA", "http://1.1.1.1", "api key");

		assertEquals("Leukaemia plasmacytic (in remission)", classifications.get(0).getClassificationElement().get(0).getCodeName());
		assertEquals("Leukaemia", classifications.get(1).getClassificationElement().get(0).getCodeName());
		assertEquals("Plasmacytic", classifications.get(2).getClassificationElement().get(0).getCodeName());
	}

	@Test
	public void testThatSearchReturnsClassificationEachHavingElementNames() throws Exception {

		List<Classification> classifications = searchInterface.search("Leukaemia plasmacytic (in remission)", "MEDDRA", "http://1.1.1.1", "api key");

		assertEquals("LLT", classifications.get(0).getClassificationElement().get(0).getElementName());
		assertEquals("LLT", classifications.get(1).getClassificationElement().get(0).getElementName());
	}

	@Test
	public void testThatGetClassificationTermsDoesNotReturnNull() throws Exception {

		assertNotNull(searchInterface.getClassificationTerms("http://purl.bioontology.org/ontology/MDR/10024319", "http://1.1.1.1", "api key"));
	}

	@Test
	public void testThatGetClassificationTermsReturnsExpectedNumberOfElements() throws Exception {

		assertEquals(3, searchInterface.getClassificationTerms("http://purl.bioontology.org/ontology/MDR/10024319", "http://1.1.1.1", "api key").getClassificationElement().size());
	}

	@Test
	public void testThatGetClassificationTermsElementsHavingElementNames() throws Exception {

		Classification classification = searchInterface.getClassificationTerms("http://purl.bioontology.org/ontology/MDR/10024319", "http://1.1.1.1", "api key");

		assertEquals("SOC", classification.getClassificationElement().get(0).getElementName());
		assertEquals("HLGT", classification.getClassificationElement().get(1).getElementName());
		assertEquals("HLT", classification.getClassificationElement().get(2).getElementName());
	}

	@Test
	public void testThatGetClassificationTermsElementsHavingCodeNames() throws Exception {

		Classification classification = searchInterface.getClassificationTerms("http://purl.bioontology.org/ontology/MDR/10024319", "http://1.1.1.1", "api key");

		assertEquals("Neoplasms benign", classification.getClassificationElement().get(0).getCodeName());
		assertEquals("Plasma cell neoplasms", classification.getClassificationElement().get(1).getCodeName());
		assertEquals("Multiple myelomas", classification.getClassificationElement().get(2).getCodeName());
	}

	@Test
	public void testThatGetCodeReturnsClassificationWithCodes() throws Exception {

		Classification classification = new Classification();

		ClassificationElement classificationElement = new ClassificationElement();
		ClassificationElement classificationElement1 = new ClassificationElement();
		classificationElement.setElementName("PAIN");
		classificationElement1.setElementName("PAIN");

		classification.addClassificationElement(classificationElement);
		classification.addClassificationElement(classificationElement1);

		searchInterface.getClassificationCodes(classification, "MEDDRA", "MEDDRA", "api key");

		assertEquals("10033371", classification.getClassificationElement().get(0).getCodeValue());
		assertEquals("10033371", classification.getClassificationElement().get(1).getCodeValue());
	}

	@Test
	public void testThatWHODSearchDoesNotReturnNull() throws Exception {

		assertNotNull(searchInterface.search("Benzalkonium", "WHOD", "http://1.1.1.1", "api key"));
	}

	@Test
	public void testThatWHODSearchReturnsExpectedNumberOfTerms() throws Exception {

		assertEquals(2, searchInterface.search("Benzalkonium", "WHOD", "http://1.1.1.1", "api key").size());
	}

	@Test
	public void testThatWhodSearchReturnsCorrectTermNames() throws Exception {

		List<Classification> classifications = searchInterface.search("Benzalkonium", "WHOD", "http://1.1.1.1", "api key");
		assertEquals("Benzalkonium", classifications.get(0).getClassificationElement().get(0).getCodeName());
		assertEquals("Benzalkonium chloride", classifications.get(1).getClassificationElement().get(0).getCodeName());
	}

	@Test
	public void testThatWhodSearchReturnsClassificationEachHavingElementNames() throws Exception {

		List<Classification> classifications = searchInterface.search("Benzalkonium", "WHOD", "http://1.1.1.1", "api key");

		assertEquals("MPN", classifications.get(0).getClassificationElement().get(0).getElementName());
		assertEquals("MPN", classifications.get(1).getClassificationElement().get(0).getElementName());
	}

	@Test
	public void testThatCTCAESearchReturnsExpectedNumberOfTerms() throws Exception {

		assertEquals(1, searchInterface.search("Leg Pain", "CTCAE", "http://1.1.1.1", "api key").size());
	}

	@Test
	public void testThatCTCAESearchReturnsCorrectTermNames() throws Exception {
		List<Classification> classifications = searchInterface.search("Leg Pain", "CTCAE", "http://1.1.1.1", "api key");
		assertEquals("Leg pain Grade 2", classifications.get(0).getClassificationElement().get(0).getCodeName());
	}

	@Test
	public void testThatGetCodeReturnsClassificationWithCTCAECodes() throws Exception {

		Classification classification = new Classification();
		ClassificationElement classificationElement = new ClassificationElement();
		classificationElement.setElementName("Leg pain");
		classification.addClassificationElement(classificationElement);
		searchInterface.getClassificationCodes(classification, "CTCAE", "CTCAE", "api key");

		assertEquals("E10904", classification.getClassificationElement().get(0).getCodeValue());
	}

}
