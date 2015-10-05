package org.akaza.openclinica.bean.submit;

import java.util.ArrayList;

/**
 * Row of the items. This entity was created in order to store
 * metadata for new render.
 */
public class DisplayItemRowBean {

	private ArrayList<DisplayItemBean> items;
	private int totalItemsInRow;
	private int maxColumn;
	private boolean containsItemsWithCustomWidth;

	/**
	 * Default constructor.
	 */
	public DisplayItemRowBean() {
		totalItemsInRow = 0;
		maxColumn = 0;
		containsItemsWithCustomWidth = false;
		items = new ArrayList<DisplayItemBean>();
	}

	public boolean isContainsItemsWithCustomWidth() {
		return containsItemsWithCustomWidth;
	}

	public void setContainsItemsWithCustomWidth(boolean containsItemsWithCustomWidth) {
		this.containsItemsWithCustomWidth = containsItemsWithCustomWidth;
	}

	public ArrayList<DisplayItemBean> getItems() {
		return items;
	}

	public void setItems(ArrayList<DisplayItemBean> items) {
		this.items = items;
	}

	public int getMaxColumn() {
		return maxColumn;
	}

	public void setMaxColumn(int maxColumn) {
		this.maxColumn = maxColumn;
	}

	public int getTotalItemsInRow() {
		return totalItemsInRow;
	}

	public void setTotalItemsInRow(int totalItemsInRow) {
		this.totalItemsInRow = totalItemsInRow;
	}

	/**
	 * Add new item to this row.
	 * @param item DisplayItemBean
	 */
	public void addNewItem(DisplayItemBean item) {
		maxColumn = item.getMetadata().getColumnNumber();
		containsItemsWithCustomWidth = false;
		totalItemsInRow++;
		items.add(item);
	}

	/**
	 * Check if this item should be added to this row.
	 * @param item DisplayItemBean
	 * @return boolean
	 */
	public boolean shouldItemBeAddedToThisRow(DisplayItemBean item) {
		return maxColumn < item.getMetadata().getColumnNumber();
	}
}
