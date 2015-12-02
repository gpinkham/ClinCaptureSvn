package com.clinovo.bean;

import com.clinovo.model.EDCItemMetadata;
import org.akaza.openclinica.DefaultAppContextTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Event Definition CRF Item Metadata Test.
 */
public class EDCItemMetadataTest extends DefaultAppContextTest {

	private EDCItemMetadata metadata1;
	private EDCItemMetadata metadata2;

	@Before
	public void prepare() {
		metadata1 = new EDCItemMetadata();
		metadata1.setStudyEventDefinitionId(1);
		metadata1.setEventDefinitionCrfId(1);
		metadata1.setCrfVersionId(1);
		metadata1.setBoolSdvRequired(true);

		metadata2 = new EDCItemMetadata();
		metadata2.setStudyEventDefinitionId(1);
		metadata2.setEventDefinitionCrfId(1);
		metadata2.setCrfVersionId(1);
		metadata2.setBoolSdvRequired(true);
	}

	@Test
	public void testThatEqualsComparesObjectsWithoutVersion() {
		metadata2.setVersion(2);
		assertEquals(metadata1, metadata2);
		metadata2.setBoolSdvRequired(false);
		assertFalse(metadata1.equals(metadata2));
		metadata2.setBoolSdvRequired(true);
		metadata2.setCrfVersionId(2);
		assertFalse(metadata1.equals(metadata2));
		metadata2.setCrfVersionId(1);
	}

	@Test
	public void testThatEqualsComparesObjectsWithoutId() {
		metadata2.setId(2);
		assertEquals(metadata1, metadata2);
		metadata2.setBoolSdvRequired(false);
		assertFalse(metadata1.equals(metadata2));
		metadata2.setBoolSdvRequired(true);
		metadata2.setCrfVersionId(2);
		assertFalse(metadata1.equals(metadata2));
		metadata2.setCrfVersionId(1);
	}

	@Test
	public void testThatCopyConstructorCopiesAllRequiredField() {
		EDCItemMetadata clone = new EDCItemMetadata(metadata1);
		assertEquals(clone.getStudyEventDefinitionId(), metadata1.getStudyEventDefinitionId());
		assertEquals(clone.getEventDefinitionCrfId(), metadata1.getEventDefinitionCrfId());
		assertEquals(clone.getCrfVersionId(), metadata1.getCrfVersionId());
		assertEquals(clone.getItemId(), metadata1.getItemId());
	}
}
