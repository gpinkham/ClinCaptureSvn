package com.clinovo.service;

import com.clinovo.model.EDCItemMetadata;
import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Event Definition CRF Item Metadata service Test.
 */
public class EDCItemMetadataServiceTest extends DefaultAppContextTest {

	@Autowired
	private EDCItemMetadataService edcItemMetadataService;

	@Test
	public void testThatFindAllByEventDefinitionCRFAndVersionReturnsCorrectResult() {
		EventDefinitionCRFBean eventDefinitionCRFBean = new EventDefinitionCRFBean();
		eventDefinitionCRFBean.setId(1);
		assertEquals(4, edcItemMetadataService.findAllByEventDefinitionCRFAndVersion(eventDefinitionCRFBean, 1).size());
	}

	@Test
	public void testThatFindAllByEventDefinitionCRFReturnsCorrectResult() {
		EventDefinitionCRFBean eventDefinitionCRFBean = new EventDefinitionCRFBean();
		eventDefinitionCRFBean.setId(1);
		assertEquals(4, edcItemMetadataService.findAllByEventDefinitionCRF(eventDefinitionCRFBean).size());
	}

	@Test
	public void testThatFindAllByEventCRFIdReturnsCorrectResult() {
		assertEquals(4, edcItemMetadataService.findAllByEventCRFId(1).size());
		assertEquals(1, edcItemMetadataService.findAllByEventCRFId(13).size());
	}

	@Test
	public void testThatFindByEventCRFAndItemIDReturnsCorrectResult() {
		EDCItemMetadata edcItemMetadata = edcItemMetadataService.findByEventCRFAndItemID(1, 1);
		assertEquals(1, edcItemMetadata.getEventDefinitionCrfId());
		assertEquals(1, edcItemMetadata.getStudyEventDefinitionId());
	}

	@Test
	public void testThatFindByCRFVersionIDEventDefinitionCRFIDAndItemIDReturnsCorrectResult() {
		assertNotNull(edcItemMetadataService.findByCRFVersionIDEventDefinitionCRFIDAndItemID(1, 1, 1));
	}

	@Test
	public void testThatSaveOrUpdateSavesEntityToTheDatabaseReturnsCorrectResult() {
		EDCItemMetadata edcItemMetadata = new EDCItemMetadata();
		edcItemMetadata.setStudyEventDefinitionId(1);
		edcItemMetadata.setCrfVersionId(4);
		edcItemMetadata.setBoolSdvRequired(true);
		edcItemMetadata.setEventDefinitionCrfId(1);
		edcItemMetadata.setItemId(1);
		edcItemMetadataService.saveOrUpdate(edcItemMetadata);
		EDCItemMetadata newEdcItemMetadata = edcItemMetadataService.findByCRFVersionIDEventDefinitionCRFIDAndItemID(4, 1, 1);
		assertNotNull(newEdcItemMetadata);
	}
}
