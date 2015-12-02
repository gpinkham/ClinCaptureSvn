package com.clinovo.dao;

import com.clinovo.model.EDCItemMetadata;
import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Event Definition CRF Item Metadata test.
 */
public class EDCItemMetadataDAOTest  extends DefaultAppContextTest {

	@Autowired
	private  EDCItemMetadataDAO edcItemMetadataDAO;

	@Test
	public void testThatFindAllByEventDefinitionCRFAndVersionReturnsCorrectResult() {
		EventDefinitionCRFBean eventDefinitionCRFBean = new EventDefinitionCRFBean();
		eventDefinitionCRFBean.setId(1);
		assertEquals(4, edcItemMetadataDAO.findAllByEventDefinitionCRFAndVersion(eventDefinitionCRFBean, 1).size());
	}

	@Test
	public void testThatFindByCRFVersionIDEventDefinitionCRFIdAndItemIdReturnsCorrectResult() {
		EDCItemMetadata edcItemMetadata = edcItemMetadataDAO.findByCRFVersionIDEventDefinitionCRFIdAndItemId(1, 1, 1);
		assertTrue(edcItemMetadata.sdvRequired());
		EDCItemMetadata edcItemMetadata12 = edcItemMetadataDAO.findByCRFVersionIDEventDefinitionCRFIdAndItemId(1, 1, 3);
		assertFalse(edcItemMetadata12.sdvRequired());
	}

	@Test
	public void testThatFindAllByEventDefinitionCRFIdReturnsCorrectResult() {
		assertEquals(4, edcItemMetadataDAO.findAllByEventDefinitionCRFId(1).size());
		assertEquals(1, edcItemMetadataDAO.findAllByEventDefinitionCRFId(2).size());
	}
}
