/*******************************************************************************
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

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.exception.OpenClinicaException;

import com.clinovo.util.CrfShortcutsAnalyzer;

/**
 * Item SDV Service.
 *
 * @author Sergey Kirpichenok
 *
 */
public interface ItemSDVService {

	String YES = "yes";
	String CRF = "crf";
	String SDV = "sdv";
	String ITEM = "item";
	String UN_SDV = "unsdv";
	String ITEM_ID = "itemId";
	String ROW_COUNT = "rowCount";
	String COMPLETED = "completed";
	String ITEM_DATA_ID = "itemDataId";
	String ITEM_DATA_ITEMS = "itemDataItems";
	String TOTAL_ITEMS_TO_SDV = "totalItemsToSDV";
	String TOTAL_SECTION_ITEMS_TO_SDV = "totalSectionItemsToSDV";

	/**
	 * Method processes the changed crf version metadata.
	 *
	 * @param currentStudy
	 *            current study
	 * @param userAccountBean
	 *            updater
	 * @param crfVersionId
	 *            crf version id
	 * @param metadata
	 *            Map<ItemFormMetadataBean.id, ItemFormMetadataBean.sdvRequired>
	 * @throws Exception
	 *             an Exception
	 */
	void processChangedCrfVersionMetadata(StudyBean currentStudy, UserAccountBean userAccountBean, int crfVersionId,
			Map<Integer, Boolean> metadata) throws Exception;

	/**
	 * Method that performs item sdv.
	 *
	 * @param itemDataId
	 *            item data id
	 * @param sectionId
	 *            section id
	 * @param eventDefinitionCrfId
	 *            event definition crf id
	 * @param action
	 *            action
	 * @param userAccountBean
	 *            updater
	 * @param crfShortcutsAnalyzer
	 *            discrepancy shortcuts analyzer
	 * @return String JSON object
	 * @throws Exception
	 *             an Exception
	 */
	String sdvItem(int itemDataId, int sectionId, int eventDefinitionCrfId, String action,
			UserAccountBean userAccountBean, CrfShortcutsAnalyzer crfShortcutsAnalyzer) throws Exception;

	/**
	 * Method resets sdv state for items (should be called after an event crf has changed state from sdv to non sdv).
	 * 
	 * @param changedItemsList
	 *            list of the DisplayItemBean
	 * @param userAccountBean
	 *            updater
	 */
	void resetSDVForItems(ArrayList<DisplayItemBean> changedItemsList, UserAccountBean userAccountBean);

	/**
	 * Returns list of items that are required to be SDV.
	 *
	 * @param eventCrfId
	 *            event crf id
	 * @return Map map of the DisplayItemBean
	 */
	List<DisplayItemBean> getListOfItemsToSDV(int eventCrfId);

	/**
	 * SDV crf items for event crf.
	 *
	 * @param eventCrfId
	 *            event crf id
	 * @param userId
	 *            updater
	 * @param sdv
	 *            boolean
	 * @return boolean
	 */
	boolean sdvCrfItems(int eventCrfId, int userId, boolean sdv);

	/**
	 * SDV crf items for event crf (transactional method that is used only for the VerifyImportedCRFDataServlet).
	 *
	 * @param eventCrfId
	 *            event crf id
	 * @param userId
	 *            updater
	 * @param sdv
	 *            boolean
	 * @param con
	 *            Connection
	 * @return boolean
	 */
	boolean sdvCrfItems(int eventCrfId, int userId, boolean sdv, Connection con);

	/**
	 * Method returns quantity of items that need to be SDV.
	 *
	 * @param eventCrfId
	 *            event crf id
	 * @return int count of items to SDV
	 */
	int getCountOfItemsToSDV(int eventCrfId);

	/**
	 * Method checks that crf has items to SDV.
	 *
	 * @param crfId
	 *            crf id
	 * @return boolean flag that equals to true if crf has items to SDV, otherwise it's false
	 */
	boolean hasItemsToSDV(int crfId);

	/**
	 * Method copies settings from previous crf version.
	 * 
	 * @param previousCrfVersionId
	 *            int
	 * @param newCrfVersionId
	 *            int
	 * @throws OpenClinicaException
	 *             the OpenClinicaException
	 */
	void copySettingsFromPreviousVersion(int previousCrfVersionId, int newCrfVersionId) throws OpenClinicaException;
}
