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

package org.akaza.openclinica.bean.submit.crfdata;

import java.util.ArrayList;

public class ImportItemGroupDataBean {
	private ArrayList<ImportItemDataBean> itemData;
	private String itemGroupOID;
	private String itemGroupRepeatKey;

	public ImportItemGroupDataBean() {
		itemData = new ArrayList<ImportItemDataBean>();
	}

	public String getItemGroupRepeatKey() {
		return itemGroupRepeatKey;
	}

	public void setItemGroupRepeatKey(String itemGroupRepeatKey) {
		this.itemGroupRepeatKey = itemGroupRepeatKey;
	}

	public String getItemGroupOID() {
		return itemGroupOID;
	}

	public void setItemGroupOID(String itemGroupOID) {
		this.itemGroupOID = itemGroupOID;
	}

	public ArrayList<ImportItemDataBean> getItemData() {
		return itemData;
	}

	public void setItemData(ArrayList<ImportItemDataBean> itemData) {
		this.itemData = itemData;
	}
}
