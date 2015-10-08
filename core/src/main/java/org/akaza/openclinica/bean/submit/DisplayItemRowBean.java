package org.akaza.openclinica.bean.submit;

import java.util.ArrayList;

/**
 * Row of the items. This entity was created in order to store
 * metadata for new render.
 */
public class DisplayItemRowBean {

	private ArrayList<DisplayItemBean> items;
	private ArrayList<String> headers;
	private ArrayList<String> subHeaders;
	private int totalItemsInRow;
	private int maxColumn;
	private boolean containsItemsWithCustomWidth;
	private boolean haveSubHeaders;
	private boolean haveHeaders;

	/**
	 * Default constructor.
	 */
	public DisplayItemRowBean() {
		totalItemsInRow = 0;
		maxColumn = 0;
		containsItemsWithCustomWidth = false;
		items = new ArrayList<DisplayItemBean>();
		headers = new ArrayList<String>();
		subHeaders = new ArrayList<String>();
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

		if (item.getMetadata().getHeader() != null && !item.getMetadata().getHeader().isEmpty()) {
			haveHeaders = true;
			headers.add(item.getMetadata().getHeader());
		} else {
			headers.add("");
		}

		if (item.getMetadata().getSubHeader() != null && !item.getMetadata().getSubHeader().isEmpty()) {
			haveSubHeaders = true;
			subHeaders.add(item.getMetadata().getSubHeader());
		} else {
			subHeaders.add("");
		}
	}

	/**
	 * Check if this item should be added to this row.
	 * @param item DisplayItemBean
	 * @return boolean
	 */
	public boolean shouldItemBeAddedToThisRow(DisplayItemBean item) {
		return maxColumn < item.getMetadata().getColumnNumber();
	}

	public ArrayList<String> getHeaders() {
		return headers;
	}

	public void setHeaders(ArrayList<String> headers) {
		this.headers = headers;
	}

	public ArrayList<String> getSubHeaders() {
		return subHeaders;
	}

	public void setSubHeaders(ArrayList<String> subHeaders) {
		this.subHeaders = subHeaders;
	}

	public boolean isHaveHeaders() {
		return haveHeaders;
	}

	public void setHaveHeaders(boolean haveHeaders) {
		this.haveHeaders = haveHeaders;
	}

	public boolean isHaveSubHeaders() {
		return haveSubHeaders;
	}

	public void setHaveSubHeaders(boolean haveSubHeaders) {
		this.haveSubHeaders = haveSubHeaders;
	}
}
