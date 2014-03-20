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

		Mockito.doReturn(searchResult).when(searchInterface).termListRequest(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn(treeResult).when(searchInterface).getPageDataRequest(Mockito.anyString(), Mockito.anyString());
		Mockito.doReturn(termCodeResult).when(searchInterface).getTermCodeRequest(Mockito.any(ClassificationElement.class), Mockito.anyString(), Mockito.anyString());

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
		classificationElement.setElementName("PAIN");

		classification.addClassificationElement(classificationElement);
		classification.addClassificationElement(classificationElement1);

		searchInterface.getClassificationCodes(classification, "MEDDRA", "http://1.1.1.1", "api key");

		assertEquals("10033371", classification.getClassificationElement().get(0).getCodeValue());
		assertEquals("10033371", classification.getClassificationElement().get(1).getCodeValue());
	}
}
