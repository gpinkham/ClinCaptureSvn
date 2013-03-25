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
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.extract;

/**
 * <P>
 * FilterObjectBean, an object that carries information about a specific parameter and its value, together with minimal
 * extra information for the filter.
 * 
 * @author thickerson
 * 
 */
public class FilterObjectBean {
	private int itemId;
	private String value;
	private String operand;
	private String logical;
	private String itemName;

	/**
	 * @return Returns the itemId.
	 */
	public int getItemId() {
		return itemId;
	}

	/**
	 * @param itemId
	 *            The itemId to set.
	 */
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	/**
	 * @return Returns the operand.
	 */
	public String getOperand() {
		return operand;
	}

	/**
	 * @param operand
	 *            The operand to set.
	 */
	public void setOperand(String operand) {
		this.operand = operand;
	}

	/**
	 * @return Returns the value.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            The value to set.
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return Returns the logical.
	 */
	public String getLogical() {
		return logical;
	}

	/**
	 * @param logical
	 *            The logical to set.
	 */
	public void setLogical(String logical) {
		this.logical = logical;
	}

	/**
	 * @return Returns the itemName.
	 */
	public String getItemName() {
		return itemName;
	}

	/**
	 * @param itemName
	 *            The itemName to set.
	 */
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
}
