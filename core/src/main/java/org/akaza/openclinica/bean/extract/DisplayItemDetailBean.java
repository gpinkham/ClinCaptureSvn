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

import org.akaza.openclinica.bean.submit.ItemBean;

import java.util.HashMap;

@SuppressWarnings("rawtypes")
public class DisplayItemDetailBean {
	private ItemBean item;
	private HashMap metaForVersion = new HashMap();

	/**
	 * @return Returns the item.
	 */
	public ItemBean getItem() {
		return item;
	}

	/**
	 * @param item
	 *            The item to set.
	 */
	public void setItem(ItemBean item) {
		this.item = item;
	}

	/**
	 * @return Returns the metaForVersion.
	 */
	public HashMap getMetaForVersion() {
		return metaForVersion;
	}

	/**
	 * @param metaForVersion
	 *            The metaForVersion to set.
	 */
	public void setMetaForVersion(HashMap metaForVersion) {
		this.metaForVersion = metaForVersion;
	}
}
