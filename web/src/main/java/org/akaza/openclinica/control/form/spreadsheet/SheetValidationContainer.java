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

package org.akaza.openclinica.control.form.spreadsheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * For validation of SpreadSheet Loading.
 * 
 */
public class SheetValidationContainer {
	/*
	 * YW: created file at Aug., 2011 with OnChangeSheetValidator cooperating with current spreadsheet loading
	 * validation style.
	 */

	/**
	 * key: itemName; value: groupLabel
	 */
	Map<String, String> allItems = new HashMap<String, String>();
	/**
	 * list of all repeating group labels of a spreadsheet
	 */
	List<String> repeatingGroupLabels = new ArrayList<String>();
	/**
	 * key: itemName; value: sectionName
	 */
	Map<String, String> itemSectionNameMap = new HashMap<String, String>();
	/**
	 * key: itemName; value: repeating group labels
	 */
	Map<String, String> itemRepGrpNameMap = new HashMap<String, String>();

	public void collectRepGrpItemNameMap(String itemName, String groupLabel) {
		if (this.repeatingGroupLabels.contains(groupLabel))
			this.itemRepGrpNameMap.put(itemName, groupLabel);
	}

	/**
	 * Precondition: itemSectionNameMap collecting has passed collecting itemNameA, itemNameB. <br/>
	 * Return false if itemSectionNames is null or empty.
	 * 
	 * @param itemNameA
	 * @param itemNameB
	 * @param items
	 * @return
	 */
	public boolean inSameSection(String itemNameA, String itemNameB) {
		if (itemSectionNameMap == null) {
			return false;
		} else {
			String a = "", b = "";
			if (itemSectionNameMap.containsKey(itemNameA)) {
				a = itemSectionNameMap.get(itemNameA);
			}
			if (itemSectionNameMap.containsKey(itemNameB)) {
				b = itemSectionNameMap.get(itemNameB);
			}
			return !a.isEmpty() && !b.isEmpty() && a.equals(b);
		}
	}

	/**
	 * Precondition: itemRepGrpNameMap collecting has passed collecting itemName. Return false if itemRepGrpNameMap is
	 * null or empty.
	 * 
	 * @param itemName
	 * @return
	 */
	public boolean inRepeatingGroup(String itemName) {
		return itemRepGrpNameMap != null && itemRepGrpNameMap.containsKey(itemName);
	}

	/**
	 * Precondition: itemRepGrpNameMap collecting has passed collecting itemNameA, itemNameB.<br/>
	 * Return false if itemRepGrpNameMap is null or empty.
	 * 
	 * @param itemNameA
	 * @param itemNameB
	 * @return
	 */
	public boolean inSameRepeatingGroup(String itemNameA, String itemNameB) {
		if (itemRepGrpNameMap == null) {
			return false;
		} else {
			String o = "", d = "";
			if (itemRepGrpNameMap.containsKey(itemNameA)) {
				o = itemRepGrpNameMap.get(itemNameA);
			}
			if (itemRepGrpNameMap.containsKey(itemNameB)) {
				d = itemRepGrpNameMap.get(itemNameB);
			}
			return !o.isEmpty() && !d.isEmpty() && o.equals(d);
		}
	}

	public boolean itemNameAvailable(String itemName) {
		return allItems.containsKey(itemName);
	}

	public Map<String, String> getAllItems() {
		return allItems;
	}

	public void setAllItems(Map<String, String> allItems) {
		this.allItems = allItems;
	}

	public List<String> getRepeatingGroupLabels() {
		return repeatingGroupLabels;
	}

	public void setRepeatingGroupLabels(List<String> repeatingGroupLabels) {
		this.repeatingGroupLabels = repeatingGroupLabels;
	}

	public Map<String, String> getItemSectionNameMap() {
		return itemSectionNameMap;
	}

	public void setItemSectionNameMap(Map<String, String> itemSectionNameMap) {
		this.itemSectionNameMap = itemSectionNameMap;
	}

	public Map<String, String> getItemRepGrpNameMap() {
		return itemRepGrpNameMap;
	}

	public void setItemRepGrpNameMap(Map<String, String> itemRepGrpNameMap) {
		this.itemRepGrpNameMap = itemRepGrpNameMap;
	}
}
