/** ===================================================================================================================================================================================================================================================================================================================================================================================================================================================================
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 * 
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer. 
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 * 
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use. 
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO‚ÄôS ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 * =================================================================================================================================================================================================================================================================================================================================================================================================================================================================== */
package com.clinovo.clincapture.web.crfdata;

import java.util.List;

import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.crfdata.ImportItemDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ImportItemGroupDataBean;

/**
 * @author Frank
 *
 */
public class ImportDataRefiner {

	/***
	 * Refines ImportItemGroupData objects to make sure items defined in xml during data import
	 * are consistent across all groups. This method implicitly adds items that belong to the
	 * crf version but are not defined in the xml for each group
	 * @param itemGroupDataBeans
	 * @param crfVersionItems 
	 */
	public void refineImportItemGroupData(List<ImportItemGroupDataBean> itemGroupDataBeans, List<ItemBean> crfVersionItems) {
		//Iterate though groups
		for (ImportItemGroupDataBean group : itemGroupDataBeans) {
			//Iterate through crfVersionItems
			for (ItemBean item : crfVersionItems) {
				//If item doesn't exist in group item list from xml, add it to list
				if (!crfVersionItemExistsInGroupItems(item, group.getItemData())) {
					addItemsToGroup(item, group.getItemData());
				}
			}
		}	
	}

	private void addItemsToGroup(ItemBean item, List<ImportItemDataBean> groupItemData) {		 
			ImportItemDataBean itemToAdd = new ImportItemDataBean();
			itemToAdd.setItemOID(item.getOid());
			itemToAdd.setValue("");
			itemToAdd.setAutoAdded(true);
			groupItemData.add(itemToAdd);
	}

	boolean crfVersionItemExistsInGroupItems(ItemBean item, List<ImportItemDataBean> itemData) {
		for (ImportItemDataBean itm : itemData) {
			if (item.getOid().equalsIgnoreCase(itm.getItemOID())) {
				return true;
			}
		}
		return false;
	}

}
