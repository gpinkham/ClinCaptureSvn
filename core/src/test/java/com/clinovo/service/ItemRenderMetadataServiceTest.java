package com.clinovo.service;

import com.clinovo.model.ItemRenderMetadata;
import org.akaza.openclinica.DefaultAppContextTest;
import org.junit.Test;

/**
 * ItemRenderMetadataService Test.
 */
public class ItemRenderMetadataServiceTest extends DefaultAppContextTest {

	public final int EXPECTED_WIDTH = 350;
	public final int EXPECTED_LEFT_ITEM_TEXT_WIDTH = 50;
	public final int DEFAULT_WIDTH = 0;

	@Test
	public void testThatFindByCrfVersionAndItemIDReturnsCorrectWidth() {
		assertEquals(EXPECTED_WIDTH, itemRenderMetadataService.findByCrfVersionAndItemID(1, 3).getWidth());
	}

	@Test
	public void testThatFindByCrfVersionAndItemIDReturnsCorrectLeftItemTextWidth() {
		assertEquals(EXPECTED_LEFT_ITEM_TEXT_WIDTH, itemRenderMetadataService.findByCrfVersionAndItemID(1, 2).getLeftItemTextWidth());
	}

	@Test
	public void testThatSaveOrUpdatePersistsANewWidget() {
		ItemRenderMetadata renderMetadata = new ItemRenderMetadata();
		renderMetadata.setCrfVersionId(1);
		renderMetadata.setItemId(5);
		renderMetadata.setWidth(EXPECTED_WIDTH);
		itemRenderMetadataService.save(renderMetadata);
		assertEquals(DEFAULT_WIDTH, itemRenderMetadataDAO.findByCrfVersionAndItemID(1, 5).getLeftItemTextWidth());
	}
}
