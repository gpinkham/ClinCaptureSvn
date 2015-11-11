package com.clinovo.service.impl;

import com.clinovo.dao.ItemRenderMetadataDAO;
import com.clinovo.model.ItemRenderMetadata;
import com.clinovo.service.ItemRenderMetadataService;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.DisplayItemWithGroupBean;
import org.akaza.openclinica.bean.submit.DisplaySectionBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of the ItemRenderMetadataService.
 */
@Transactional
@Service("itemRenderMetadataService")
public class ItemRenderMetadataServiceImpl implements ItemRenderMetadataService {

	@Autowired
	private ItemRenderMetadataDAO itemRenderMetadataDAO;

	/**
	 * {@inheritDoc}
	 */
	public ItemRenderMetadata findByCrfVersionAndItemID(int crfVersionId, int itemId) {
		return itemRenderMetadataDAO.findByCrfVersionAndItemID(crfVersionId, itemId);
	}

	/**
	 * {@inheritDoc}
	 */
	public void save(ItemRenderMetadata itemRenderMetadata) {
		itemRenderMetadataDAO.saveOrUpdate(itemRenderMetadata);
	}

	/**
	 * {@inheritDoc}
	 */
	public void populateDisplayItemsWithRenderMetadata(DisplaySectionBean sectionBean) {
		List<DisplayItemWithGroupBean> displayItemWithGroupList = sectionBean.getDisplayItemGroups();
		int crfVersionId = sectionBean.getCrfVersion().getId();

		for (DisplayItemWithGroupBean displayItemWithGroupBean : displayItemWithGroupList) {
			if (!displayItemWithGroupBean.isInGroup()) {
				DisplayItemBean displayItemBean = displayItemWithGroupBean.getSingleItem();
				ItemBean itemBean = displayItemBean.getItem();
				ItemRenderMetadata itemRenderMetadata = findByCrfVersionAndItemID(crfVersionId, itemBean.getId());
				itemBean.setItemRenderMetadata(itemRenderMetadata);
			} else {
				List<DisplayItemBean> displayItemBeansList = displayItemWithGroupBean.getItemGroup().getItems();
				for (DisplayItemBean displayItemBean : displayItemBeansList) {
					ItemBean itemBean = displayItemBean.getItem();
					ItemRenderMetadata itemRenderMetadata = findByCrfVersionAndItemID(crfVersionId, itemBean.getId());
					itemBean.setItemRenderMetadata(itemRenderMetadata);
				}
			}
		}
	}
}
