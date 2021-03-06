/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License 
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

package org.akaza.openclinica.bean.submit;

import org.akaza.openclinica.bean.core.EntityBean;

@SuppressWarnings({"rawtypes", "serial"})
public class ItemGroupMetadataBean extends EntityBean implements Comparable {
	private Integer itemGroupId;
	private String header;
	private String subheader;
	private String layout;
	private Integer repeatNum;
	private Integer repeatMax;
	private String repeatArray;
	private Integer rowStartNumber;
	private Integer crfVersionId;
	private Integer itemId;
	private Integer ordinal;
	private Integer borders;
	private boolean showGroup;
	private boolean isHighlighted;
	private boolean repeatingGroup;

	public ItemGroupMetadataBean() {
		super();
		this.itemId = Integer.valueOf(0);
		this.itemGroupId = Integer.valueOf(0);
		header = "";
		subheader = "";
		layout = "";
		repeatNum = 0;
		repeatArray = "";
		repeatMax = 0;
		rowStartNumber = 0;
		ordinal = 0;
		borders = 1;
		showGroup = true;
		isHighlighted = false;
		repeatingGroup = true;
	}

	/**
	 * @return the crfVersionId
	 */
	public Integer getCrfVersionId() {
		return crfVersionId;
	}

	/**
	 * @param crfVersionId
	 *            the crfVersionId to set
	 */
	public void setCrfVersionId(Integer crfVersionId) {
		this.crfVersionId = crfVersionId;
	}

	/**
	 * @return the header
	 */
	public String getHeader() {
		return header;
	}

	/**
	 * @param header
	 *            the header to set
	 */
	public void setHeader(String header) {
		this.header = header;
	}

	/**
	 * @return the itemGroupId
	 */
	public Integer getItemGroupId() {
		return itemGroupId;
	}

	/**
	 * @param itemGroupId
	 *            the itemGroupId to set
	 */
	public void setItemGroupId(Integer itemGroupId) {
		this.itemGroupId = itemGroupId;
	}

	/**
	 * @return the itemId
	 */
	public Integer getItemId() {
		return itemId;
	}

	/**
	 * @param itemId
	 *            the itemId to set
	 */
	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	/**
	 * @return the layout
	 */
	public String getLayout() {
		return layout;
	}

	/**
	 * @param layout
	 *            the layout to set
	 */
	public void setLayout(String layout) {
		this.layout = layout;
	}

	/**
	 * @return the ordinal
	 */
	public Integer getOrdinal() {
		return ordinal;
	}

	/**
	 * @param ordinal
	 *            the ordinal to set
	 */
	public void setOrdinal(Integer ordinal) {
		this.ordinal = ordinal;
	}

	/**
	 * @return the repeatArray
	 */
	public String getRepeatArray() {
		return repeatArray;
	}

	/**
	 * @param repeatArray
	 *            the repeatArray to set
	 */
	public void setRepeatArray(String repeatArray) {
		this.repeatArray = repeatArray;
	}

	/**
	 * @return the repeatMax
	 */
	public Integer getRepeatMax() {
		return repeatMax;
	}

	/**
	 * @param repeatMax
	 *            the repeatMax to set
	 */
	public void setRepeatMax(Integer repeatMax) {
		this.repeatMax = repeatMax;
	}

	/**
	 * @return the repeatNum
	 */
	public Integer getRepeatNum() {
		return repeatNum;
	}

	/**
	 * @param repeatNum
	 *            the repeatNum to set
	 */
	public void setRepeatNum(Integer repeatNum) {
		this.repeatNum = repeatNum;
	}

	/**
	 * @return the rowStartNumber
	 */
	public Integer getRowStartNumber() {
		return rowStartNumber;
	}

	/**
	 * @param rowStartNumber
	 *            the rowStartNumber to set
	 */
	public void setRowStartNumber(Integer rowStartNumber) {
		this.rowStartNumber = rowStartNumber;
	}

	/**
	 * @return the subheader
	 */
	public String getSubheader() {
		return subheader;
	}

	/**
	 * @param subheader
	 *            the subheader to set
	 */
	public void setSubheader(String subheader) {
		this.subheader = subheader;
	}

	/**
	 * @return the borders
	 */
	public Integer getBorders() {
		return borders;
	}

	/**
	 * @param borders
	 *            the borders to set
	 */
	public void setBorders(Integer borders) {
		this.borders = borders;
	}

	public boolean isShowGroup() {
		return showGroup;
	}

	public void setShowGroup(boolean showGroup) {
		this.showGroup = showGroup;
	}

	public boolean isHighlighted() {
		return isHighlighted;
	}

	public void setHighlighted(boolean isHighlighted) {
		this.isHighlighted = isHighlighted;
	}

	public int compareTo(Object o) {
		if (!(o instanceof ItemGroupMetadataBean))
			return 1;
		return this.getOrdinal().compareTo(((ItemGroupMetadataBean) o).getOrdinal());
	}

	public boolean isRepeatingGroup() {
		return repeatingGroup;
	}

	public void setRepeatingGroup(boolean repeatingGroup) {
		this.repeatingGroup = repeatingGroup;
	}
}
