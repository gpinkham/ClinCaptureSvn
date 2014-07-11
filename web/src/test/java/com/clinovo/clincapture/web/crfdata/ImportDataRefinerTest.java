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

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.crfdata.ImportItemDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ImportItemGroupDataBean;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Frank
 *
 */
public class ImportDataRefinerTest {
	
	private List<ImportItemGroupDataBean> itemGroupDataBeans;
	private List<ItemBean> crfVersionItems;
	private ImportItemGroupDataBean importItemGroupDataBean;
	private ImportDataRefiner refiner;
	
	@Before
	public void setUp() {
		refiner = new ImportDataRefiner();
		initImportItemGroupDataList();
		initCrfVersionItemsList();
	}

	/***
	 * Initializes list of Crf Version Items
	 */
	private void initCrfVersionItemsList() {
		crfVersionItems = new ArrayList<ItemBean>();
		initCrfVersionItems("I_21_IT_NOCONCOMITANTMEDICATIONS");
		initCrfVersionItems("I_21_IT_CONCOMITANTMEDICATIONNUM");
		initCrfVersionItems("I_21_IT_MEDICATIONGENERICNAME_TX");
		initCrfVersionItems("I_21_IT_MEDICATION_START_DATE_DA");
		initCrfVersionItems("I_21_IT_STARTDATEUNKNOWN_CBX");
		initCrfVersionItems("I_21_IT_STOPDATE_DATE");
		initCrfVersionItems("I_21_IT_STOPDATEUNKNOWN_CBX");
		initCrfVersionItems("I_21_IT_ONGOING_CBX");
		initCrfVersionItems("I_CONCO_IT_DOSE_INT");
		initCrfVersionItems("I_CONCO_IT_DOSE_N_A_CBX");
		initCrfVersionItems("I_CONCO_IT_UNIT_TXT");
		initCrfVersionItems("I_CONCO_IT_N_A_CBX");
		initCrfVersionItems("I_21_IT_INDICATION_TXT");
	}

	/***
	 * Initializes list of ImportItemGroupDataBeans
	 */
	private void initImportItemGroupDataList() {
		itemGroupDataBeans = new ArrayList<ImportItemGroupDataBean>();
		//Group 1
		importItemGroupDataBean = new ImportItemGroupDataBean();
		importItemGroupDataBean.setItemData(new ArrayList<ImportItemDataBean>());
		
		initImportItemDataBean("I_21_IT_STOPDATE_DATE", "2012-12-09");
		initImportItemDataBean("I_21_IT_MEDICATIONGENERICNAME_TX", "Omega 3");
		initImportItemDataBean("I_21_IT_CONCOMITANTMEDICATIONNUM", "1");
		initImportItemDataBean("I_21_IT_STARTDATEUNKNOWN_CBX", "1");
		itemGroupDataBeans.add(importItemGroupDataBean);
		
		//Group 2
		importItemGroupDataBean = new ImportItemGroupDataBean();
		importItemGroupDataBean.setItemData(new ArrayList<ImportItemDataBean>());
		
		initImportItemDataBean("I_21_IT_MEDICATIONGENERICNAME_TX", "ANUSOL");
		initImportItemDataBean("I_21_IT_STARTDATEUNKNOWN_CBX", "1");
		initImportItemDataBean("I_21_IT_STOPDATEUNKNOWN_CBX", "1");
		initImportItemDataBean("I_21_IT_CONCOMITANTMEDICATIONNUM", "2");
		initImportItemDataBean("I_21_IT_INDICATION_TXT", "Proctopathy. Note: Patient did not use");
		itemGroupDataBeans.add(importItemGroupDataBean);
	}

	/***
	 * Initializes ImportDataBean and adds it to list
	 * @param itemOid
	 * @param itemValue
	 */
	private void initImportItemDataBean(String itemOid, String itemValue) {
		ImportItemDataBean importItemDataBean = new ImportItemDataBean();
		importItemDataBean.setItemOID(itemOid);
		importItemDataBean.setValue(itemValue);
		importItemGroupDataBean.getItemData().add(importItemDataBean);
	}
	
	/***
	 * Initalizes ItemBean and adds it to list
	 * @param itemOid
	 */
	private void initCrfVersionItems(String itemOid) {
		ItemBean item = new ItemBean();
		item.setOid(itemOid);
		crfVersionItems.add(item);
	}
	
	@Test
	public void testThatCrfVersionItemExistsInGroupItemsIsValid() {
		ItemBean item = new ItemBean();
		item.setOid("I_21_IT_STOPDATE_DATE");
		assertTrue(refiner.crfVersionItemExistsInGroupItems(item, itemGroupDataBeans.get(0).getItemData()));
	}
	
	@Test
	public void testThatAllCrfVersionItemsExistInAllGroupsAfterRefine() {
		refiner.refineImportItemGroupData(itemGroupDataBeans, crfVersionItems);
		boolean itemExists = true;
		for (ImportItemGroupDataBean group : itemGroupDataBeans) {
			for (ItemBean item : crfVersionItems) {
				itemExists = refiner.crfVersionItemExistsInGroupItems(item, group.getItemData());
				if (!itemExists) {
					break;
				}
			}
			if (!itemExists) {
				break;
			}
		}
		assertTrue(itemExists);
	}
	
	@Test
	public void testThatAllAutoAddedItemsHaveEmptyValue() {
		refiner.refineImportItemGroupData(itemGroupDataBeans, crfVersionItems);
		boolean valueIsEmpty = true;
		for (ImportItemGroupDataBean group : itemGroupDataBeans) {
			for (ImportItemDataBean item : group.getItemData()) {
				if (item.getAutoAdded()) {
					valueIsEmpty = "".equals(item.getValue().trim());
					if (!valueIsEmpty) {
						break;
					}
				}				
			}
			if (!valueIsEmpty) {
				break;
			}
		}
		assertTrue(valueIsEmpty);
	}
}
