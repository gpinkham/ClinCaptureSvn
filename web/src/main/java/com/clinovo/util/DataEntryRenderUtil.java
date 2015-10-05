package com.clinovo.util;

import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.DisplayItemRowBean;
import org.akaza.openclinica.bean.submit.DisplayItemWithGroupBean;
import org.akaza.openclinica.bean.submit.DisplaySectionBean;

import java.util.List;

/**
 * Created in order to support FormStudio CRFs render.
 * Is used to convert old structure of SingleItems to new one wil DisplayItemRow.
 */
public final class DataEntryRenderUtil {

	private DataEntryRenderUtil() {
	}

	/**
	 * Convert DisplayItemWithGroup for not grouped items. Will move all Single items into DisplayItemRows.
	 *
	 * @param displaySection DisplaySectionBean.
	 * @return DisplaySectionBean
	 */
	public static DisplaySectionBean convertSingleItemsToDisplayItemRowsDependingOnSource(DisplaySectionBean displaySection) {
		if (!displaySection.getCrf().getSource().equalsIgnoreCase("formstudio")) {
			return displaySection;
		}
		DisplaySectionBean newSection = displaySection.cloneWithoutDisplayItemGroups();
		List<DisplayItemWithGroupBean> itemContainers = displaySection.getDisplayItemGroups();
		DisplayItemRowBean newRow = new DisplayItemRowBean();
		DisplayItemWithGroupBean previousContainer = null;
		int counter = 1;

		for (DisplayItemWithGroupBean itemContainer : itemContainers) {
			if (itemContainer.isInGroup()) {
				if (previousContainer != null && newRow != new DisplayItemRowBean()) {
					previousContainer.setItemsRow(newRow);
					newSection.getDisplayItemGroups().add(previousContainer);
				}
				newSection.getDisplayItemGroups().add(itemContainer);
				continue;
			}
			DisplayItemBean itemBean = itemContainer.getSingleItem();

			if (newRow.shouldItemBeAddedToThisRow(itemBean)) {
				newRow.addNewItem(itemBean);
			} else {
				if (previousContainer != null) {
					previousContainer.setItemsRow(newRow);
					newSection.getDisplayItemGroups().add(previousContainer);
				} else {
					itemContainer.setItemsRow(newRow);
					newSection.getDisplayItemGroups().add(itemContainer);
				}
				newRow = new DisplayItemRowBean();
				newRow.addNewItem(itemBean);
			}

			if (counter++ == itemContainers.size()) {
				itemContainer.setItemsRow(newRow);
				newSection.getDisplayItemGroups().add(itemContainer);
			}
			previousContainer = itemContainer;
		}
		return newSection;
	}
}
