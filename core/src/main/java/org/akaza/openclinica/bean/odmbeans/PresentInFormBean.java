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
 * OpenClinica is distributed under the GNU Lesser General Public License (GNU
 * LGPL).
 *
 * For details see: http://www.openclinica.org/license copyright 
 *
 */

package org.akaza.openclinica.bean.odmbeans;

/**
 * 
 * @author ywang (Augest, 2010)
 * 
 */

public class PresentInFormBean {
	// attributes
	private String formOid;
	private String showGroup;
	// elements
	private String itemGroupHeader;
	private ItemGroupRepeatBean itemGroupRepeatBean;

	public PresentInFormBean() {
		itemGroupRepeatBean = new ItemGroupRepeatBean();
	}

	public String getFormOid() {
		return formOid;
	}

	public void setFormOid(String formOid) {
		this.formOid = formOid;
	}

	public String getItemGroupHeader() {
		return itemGroupHeader;
	}

	public void setItemGroupHeader(String itemGroupHeader) {
		this.itemGroupHeader = itemGroupHeader;
	}

	public ItemGroupRepeatBean getItemGroupRepeatBean() {
		return itemGroupRepeatBean;
	}

	public void setItemGroupRepeatBean(ItemGroupRepeatBean itemGroupRepeatBean) {
		this.itemGroupRepeatBean = itemGroupRepeatBean;
	}

	public String getShowGroup() {
		return showGroup;
	}

	public void setShowGroup(String showGroup) {
		this.showGroup = showGroup;
	}
}
