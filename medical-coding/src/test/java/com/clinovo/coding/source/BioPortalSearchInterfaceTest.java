package com.clinovo.coding.source;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.clinovo.BaseTest;
import com.clinovo.coding.model.Classification;
import com.clinovo.coding.source.impl.BioPortalSearchInterface;

public class BioPortalSearchInterfaceTest extends BaseTest {

    BioPortalSearchInterface searchInterface = Mockito.mock(BioPortalSearchInterface.class);

    @Before
    public void setUp() throws Exception {

        Mockito.doReturn(treeResult).when(searchInterface).getTreeResponse("http://data.bioontology.org/ontologies/MEDDRA/classes/http%3A%2F%2Fpurl.bioontology.org%2Fontology%2FMDR%2F10024319/tree", "api key");
        Mockito.doReturn(searchResult).when(searchInterface).getTermsResponse("Leukaemia plasmacytic (in remission)", "http://1.1.1.1", "api key");
        Mockito.doCallRealMethod().when(searchInterface).search("Leukaemia plasmacytic (in remission)", "MEDDRA", "http://1.1.1.1", "api key");

    }

    @Test
    public void testThatSearchDoesNotReturnNull() throws Exception {

        assertNotNull(searchInterface.search("Leukaemia plasmacytic (in remission)", "MEDDRA", "http://1.1.1.1", "api key"));
    }

    @Test
    public void testThatSearchReturnsExpectedNumberOfTerms() throws Exception {

        assertEquals(1, searchInterface.search("Leukaemia plasmacytic (in remission)", "MEDDRA", "http://1.1.1.1", "api key").size());
    }

    @Test
    public void testThatSearchReturnsClassificationEachHavingHttpPath() throws Exception {

        Classification classification = searchInterface.search("Leukaemia plasmacytic (in remission)", "MEDDRA", "http://1.1.1.1", "api key").get(0);
        assertEquals("http://purl.bioontology.org/ontology/MDR/10024319", classification.getHttpPath());

    }

    @Test
    public void testThatSearchReturnsExpectedNumberOfClassificationElements() throws Exception {

        assertEquals(5, searchInterface.search("Leukaemia plasmacytic (in remission)", "MEDDRA", "http://1.1.1.1", "api key").get(0).getClassificationElement().size());
    }

    @Test
    public void testThatSearchReturnsClassificationEachHavingCodeNames() throws Exception {

        Classification classification = searchInterface.search("Leukaemia plasmacytic (in remission)", "MEDDRA", "http://1.1.1.1", "api key").get(0);
        assertEquals("Neoplasms benign, malignant and unspecified (incl cysts and polyps)", classification.getClassificationElement().get(0).getCodeName());
        assertEquals("Plasma cell neoplasms", classification.getClassificationElement().get(1).getCodeName());
        assertEquals("Multiple myelomas", classification.getClassificationElement().get(2).getCodeName());
        assertEquals("Leukaemia plasmacytic (in remission)", classification.getClassificationElement().get(3).getCodeName());

    }

    @Test
    public void testThatSearchReturnsClassificationEachHavingElementNames() throws Exception {

        Classification classification = searchInterface.search("Leukaemia plasmacytic (in remission)", "MEDDRA", "http://1.1.1.1", "api key").get(0);
        assertEquals("SOC", classification.getClassificationElement().get(0).getElementName());
        assertEquals("HLGT", classification.getClassificationElement().get(1).getElementName());
        assertEquals("HLT", classification.getClassificationElement().get(2).getElementName());
        assertEquals("PT", classification.getClassificationElement().get(3).getElementName());
        assertEquals("LTT", classification.getClassificationElement().get(4).getElementName());
    }
}
