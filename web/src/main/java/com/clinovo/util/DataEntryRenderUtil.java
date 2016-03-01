package com.clinovo.util;

import com.clinovo.model.ItemRenderMetadata;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.DisplayItemRowBean;
import org.akaza.openclinica.bean.submit.DisplayItemWithGroupBean;
import org.akaza.openclinica.bean.submit.DisplaySectionBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created in order to support FormStudio CRFs render.
 * Is used to convert old structure of SingleItems to new one wil DisplayItemRow.
 */
public final class DataEntryRenderUtil {

	public static final int DEFAULT_ITEM_WIDTH = 560;
	public static final int DEFAULT_LEFT_ITEM_BLOCK_WIDTH = 220;
	public static final int DEFAULT_RIGHT_BLOCK_WIDTH = 330;
	public static final int DEFAULT_PADDING_WIDTH = 10;
	public static final int DEFAULT_ROW_PADDING = 5;
	public static final float DEFAULT_LEFT_BLOCK_WIDTH_MULTIPLIER = 0.4f;
	public static final float DEFAULT_RIGHT_BLOCK_WIDTH_MULTIPLIER = 0.6f;


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
			postProcessRepeatingGroups(displaySection);
			return displaySection;
		}
		DisplaySectionBean newSection = displaySection.cloneWithoutDisplayItemGroups();
		List<DisplayItemWithGroupBean> itemContainers = displaySection.getDisplayItemGroups();
		DisplayItemRowBean newRow = new DisplayItemRowBean();
		DisplayItemRowBean prevRow = new DisplayItemRowBean();
		DisplayItemWithGroupBean previousContainer = null;
		int counter = 1;

		for (DisplayItemWithGroupBean itemContainer : itemContainers) {
			if (itemContainer.isInGroup()) {
				if (previousContainer != null && newRow != new DisplayItemRowBean()) {
					previousContainer.setItemsRow(newRow);
					newSection.getDisplayItemGroups().add(previousContainer);
				}
				processRepeatingGroup(itemContainer);
				newSection.getDisplayItemGroups().add(itemContainer);
				continue;
			}
			DisplayItemBean itemBean = itemContainer.getSingleItem();

			if (newRow.shouldItemBeAddedToThisRow(itemBean)) {
				newRow.addNewItem(itemBean);
			} else {
				processRowWhenAllItemsAreAdded(newRow, prevRow);
				if (previousContainer != null) {
					previousContainer.setItemsRow(newRow);
					newSection.getDisplayItemGroups().add(previousContainer);
				} else {
					itemContainer.setItemsRow(newRow);
					newSection.getDisplayItemGroups().add(itemContainer);
				}
				prevRow = newRow.clone();
				newRow = new DisplayItemRowBean();
				newRow.addNewItem(itemBean);
			}
			if (counter++ == itemContainers.size()) {
				processRowWhenAllItemsAreAdded(newRow, prevRow);
				itemContainer.setItemsRow(newRow);
				newSection.getDisplayItemGroups().add(itemContainer);
			}
			previousContainer = itemContainer;
		}
		return newSection;
	}

	private static void postProcessRepeatingGroups(DisplaySectionBean section) {
		List<DisplayItemWithGroupBean> displayItemsWithGroup = section.getDisplayItemGroups();
		for (DisplayItemWithGroupBean displayItemWithGroup : displayItemsWithGroup) {
			if (displayItemWithGroup.isInGroup()) {
				processRepeatingGroup(displayItemWithGroup);
			}
		}
	}

	private static void processRowWhenAllItemsAreAdded(DisplayItemRowBean newRow,
													   DisplayItemRowBean prevRow) {
		processSCDLogic(newRow);

		// Set width of items in the row if both rows have same number of columns.
		if (prevRow != null) {
			populateRenderMetadataFromPrevRow(newRow, prevRow);
		}
		// Set correct width of right item text and left item text depending on item width.
		recalculateWidthOfItemElements(newRow);
	}

	private static void recalculateWidthOfItemElements(DisplayItemRowBean newRow) {
		int rowWidth = 0;

		for (DisplayItemBean displayItemBean : newRow.getItems()) {
			ItemRenderMetadata renderMetadata = displayItemBean.getItem().getItemRenderMetadata();
			// Create new Object or detach from hibernate layer.
			renderMetadata = renderMetadata == null ? new ItemRenderMetadata() : new ItemRenderMetadata(renderMetadata);
			// Skip calculation if it was already done for previous row.
			if (renderMetadata.isInheritedFromPreviousRow()) {
				rowWidth += renderMetadata.getWidth();
				continue;
			}

			int itemWidth = renderMetadata.getWidth();
			int leftBlockWidth = renderMetadata.getLeftItemTextWidth();
			// Set width of whole item block.
			if (itemWidth == 0) {
				if (leftBlockWidth != 0) {
					if (leftBlockWidth > DEFAULT_ITEM_WIDTH - DEFAULT_RIGHT_BLOCK_WIDTH - DEFAULT_PADDING_WIDTH) {
						renderMetadata.setRightBlockWidth(DEFAULT_RIGHT_BLOCK_WIDTH);
						renderMetadata.setWidth(leftBlockWidth + DEFAULT_RIGHT_BLOCK_WIDTH + DEFAULT_PADDING_WIDTH);
					} else {
						renderMetadata.setRightBlockWidth(DEFAULT_ITEM_WIDTH - leftBlockWidth - DEFAULT_PADDING_WIDTH);
						renderMetadata.setWidth(DEFAULT_ITEM_WIDTH);
					}
				} else {
					renderMetadata.setRightBlockWidth(DEFAULT_RIGHT_BLOCK_WIDTH);
					renderMetadata.setLeftItemTextWidth(DEFAULT_LEFT_ITEM_BLOCK_WIDTH);
					renderMetadata.setWidth(DEFAULT_ITEM_WIDTH);
				}
			} else {
				if (leftBlockWidth != 0) {
					if (leftBlockWidth + DEFAULT_PADDING_WIDTH > itemWidth) {
						renderMetadata.setWidth(leftBlockWidth + DEFAULT_PADDING_WIDTH);
						renderMetadata.setRightBlockWidth(leftBlockWidth);
					} else {
						if (leftBlockWidth + DEFAULT_PADDING_WIDTH > itemWidth - DEFAULT_RIGHT_BLOCK_WIDTH) {
							renderMetadata.setRightBlockWidth(itemWidth - DEFAULT_PADDING_WIDTH);
						} else {
							renderMetadata.setRightBlockWidth(itemWidth - leftBlockWidth - DEFAULT_PADDING_WIDTH);
						}
					}
				} else {
					float calculatedItemWidth = (float) (itemWidth - DEFAULT_PADDING_WIDTH);
					float calculatedLeftBlockWidth = calculatedItemWidth * DEFAULT_LEFT_BLOCK_WIDTH_MULTIPLIER;
					float calculatedRightBlockWidth = calculatedItemWidth * DEFAULT_RIGHT_BLOCK_WIDTH_MULTIPLIER;
					renderMetadata.setLeftItemTextWidth((int) calculatedLeftBlockWidth);
					renderMetadata.setRightBlockWidth((int) calculatedRightBlockWidth);
				}
			}
			displayItemBean.getItem().setItemRenderMetadata(renderMetadata);
			rowWidth += renderMetadata.getWidth();
		}
		newRow.setRowWidth(rowWidth + DEFAULT_PADDING_WIDTH * newRow.getTotalItemsInRow() + DEFAULT_ROW_PADDING);
	}

	private static void populateRenderMetadataFromPrevRow(DisplayItemRowBean newRow, DisplayItemRowBean previousRow) {
		if (newRow.getTotalItemsInRow() == 1) {
			return;
		}
		if (previousRow.getTotalItemsInRow() == newRow.getTotalItemsInRow()) {
			List<DisplayItemBean> newDisplayItemBeans = newRow.getItems();
			List<DisplayItemBean> prevDisplayItemBeans = previousRow.getItems();
			if (newDisplayItemBeans.size() == prevDisplayItemBeans.size()) {
				int size = newDisplayItemBeans.size();
				for (int i = 0; i < size; i++) {
					DisplayItemBean prevDisplay = prevDisplayItemBeans.get(i);
					ItemRenderMetadata prevRenderMetadata = prevDisplay.getItem().getItemRenderMetadata();
					if (prevRenderMetadata != null) {
						ItemRenderMetadata clonedMetadata = new ItemRenderMetadata(prevRenderMetadata);
						clonedMetadata.setInheritedFromPreviousRow(true);
						DisplayItemBean newItem = newDisplayItemBeans.get(i);
						newItem.getItem().setItemRenderMetadata(clonedMetadata);
					}
				}
			}
		}
	}

	private static void processSCDLogic(DisplayItemRowBean row) {
		boolean allItemsAreHidden = true;
		ArrayList<DisplayItemBean> items = row.getItems();
		for (DisplayItemBean item : items) {
			if (item.getScdData().getScdDisplayInfo().getScdShowStatus() != 2) {
				allItemsAreHidden = false;
			}
		}
		row.setShown(!allItemsAreHidden);
	}

	private static void processRepeatingGroup(DisplayItemWithGroupBean displayItemWithGroup) {
		List<DisplayItemBean> items = displayItemWithGroup.getItemGroup().getItems();
		int columnsShown = 1;
		for (DisplayItemBean item : items) {
			if (item.getMetadata().isShowItem()) {
				if (item.getMetadata().getResponseLayout().equalsIgnoreCase("horizontal")) {
					columnsShown += item.getMetadata().getResponseSet().getOptions().size();
				} else {
					columnsShown++;
				}
			}
		}
		displayItemWithGroup.setColumnsShown(columnsShown);
	}
}
