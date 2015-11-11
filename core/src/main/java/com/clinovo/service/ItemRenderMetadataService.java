package com.clinovo.service;

import com.clinovo.model.ItemRenderMetadata;
import org.akaza.openclinica.bean.submit.DisplaySectionBean;

/**
 * Service for the ItemRenderMetadata DAO.
 */
public interface ItemRenderMetadataService {

	/**
	 * Find metadata by CRF version and Item ID.
	 * @param crfVersionId CRFVersionBean ID.
	 * @param itemId ItemBean ID.
	 * @return ItemRenderMetadata
	 */
	ItemRenderMetadata findByCrfVersionAndItemID(int crfVersionId, int itemId);

	/**
	 * Save ItemRenderMetadata.
	 * @param itemRenderMetadata ItemRenderMetadata
	 */
	void save(ItemRenderMetadata itemRenderMetadata);

	/**
	 * Get all ItemBeans from DisplayItemWithGroupBean, and set ItemRenderMetadata to them.
	 * @param sectionBean DisplaySectionBean
	 */
	void populateDisplayItemsWithRenderMetadata(DisplaySectionBean sectionBean);
}
