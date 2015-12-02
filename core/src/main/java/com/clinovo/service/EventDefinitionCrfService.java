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

import com.clinovo.model.EDCItemMetadata;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EventDefinitionCrfService.
 */
public interface EventDefinitionCrfService {

	/**
	 * Removes event definition crfs.
	 *
	 * @param studyEventDefinitionBean StudyEventDefinitionBean
	 * @param updater                  UserAccountBean
	 * @throws Exception an Exception
	 */
	void removeEventDefinitionCRFs(StudyEventDefinitionBean studyEventDefinitionBean, UserAccountBean updater)
			throws Exception;

	/**
	 * Restores event definition crfs.
	 *
	 * @param studyEventDefinitionBean StudyEventDefinitionBean
	 * @param updater                  UserAccountBean
	 * @throws Exception an Exception
	 */
	void restoreEventDefinitionCRFs(StudyEventDefinitionBean studyEventDefinitionBean, UserAccountBean updater)
			throws Exception;

	/**
	 * Method that removes parent event definition crf.
	 *
	 * @param parentEventDefinitionCRFBean EventDefinitionCRFBean
	 * @param updater                      UserAccountBean
	 * @throws Exception an Exception
	 */
	void removeParentEventDefinitionCrf(EventDefinitionCRFBean parentEventDefinitionCRFBean, UserAccountBean updater)
			throws Exception;

	/**
	 * Method that restores parent event definition crf.
	 *
	 * @param parentEventDefinitionCRFBean EventDefinitionCRFBean
	 * @param updater                      UserAccountBean
	 * @throws Exception an Exception
	 */
	void restoreParentEventDefinitionCrf(EventDefinitionCRFBean parentEventDefinitionCRFBean, UserAccountBean updater)
			throws Exception;

	/**
	 * Method that removes parent event definition crfs.
	 *
	 * @param crfBean CRFBean
	 * @param updater UserAccountBean
	 * @throws Exception an Exception
	 */
	void removeParentEventDefinitionCRFs(CRFBean crfBean, UserAccountBean updater) throws Exception;

	/**
	 * Method that restores parent event definition crfs.
	 *
	 * @param crfBean CRFBean
	 * @param updater UserAccountBean
	 * @throws Exception an Exception
	 */
	void restoreParentEventDefinitionCRFs(CRFBean crfBean, UserAccountBean updater) throws Exception;

	/**
	 * Method that removes child event definition crfs.
	 *
	 * @param studyBean StudyBean
	 * @param updater   UserAccountBean
	 * @throws Exception an Exception
	 */
	void removeChildEventDefinitionCRFs(StudyBean studyBean, UserAccountBean updater) throws Exception;

	/**
	 * Method that restores child event definition crfs.
	 *
	 * @param studyBean StudyBean
	 * @param updater   UserAccountBean
	 * @throws Exception an Exception
	 */
	void restoreChildEventDefinitionCRFs(StudyBean studyBean, UserAccountBean updater) throws Exception;

	/**
	 * Method that locks child event definition crfs.
	 *
	 * @param studyBean StudyBean
	 * @param updater   UserAccountBean
	 * @throws Exception an Exception
	 */
	void lockChildEventDefinitionCRFs(StudyBean studyBean, UserAccountBean updater) throws Exception;

	/**
	 * Method that unlocks child event definition crfs.
	 *
	 * @param studyBean StudyBean
	 * @param updater   UserAccountBean
	 * @throws Exception an Exception
	 */
	void unlockChildEventDefinitionCRFs(StudyBean studyBean, UserAccountBean updater) throws Exception;

	/**
	 * Method updates chiild event definition crfs for new crf version.
	 *
	 * @param crfVersionBean CRFVersionBean
	 * @param updater        UserAccountBean
	 */
	void updateChildEventDefinitionCrfsForNewCrfVersion(CRFVersionBean crfVersionBean, UserAccountBean updater);

	/**
	 * Method updates child event definition crfs.
	 *
	 * @param childEventDefinitionCRFsToUpdate List<EventDefinitionCRFBean>
	 * @param parentsMap                       Map<Integer, EventDefinitionCRFBean>
	 * @param updater                          UserAccountBean
	 * @param parentsBeforeUpdateMap           Map<Integer, EventDefinitionCRFBean>
	 */
	void updateChildEventDefinitionCRFs(List<EventDefinitionCRFBean> childEventDefinitionCRFsToUpdate,
										Map<Integer, EventDefinitionCRFBean> parentsMap,
										Map<Integer, EventDefinitionCRFBean> parentsBeforeUpdateMap, UserAccountBean updater);

	/**
	 * Fills info for EventDefinitionCRFBean.
	 *
	 * @param eventDefinitionCRFBean   EventDefinitionCRFBean
	 * @param studyEventDefinitionBean StudyEventDefinitionBean
	 */
	void fillEventDefinitionCrf(EventDefinitionCRFBean eventDefinitionCRFBean,
								StudyEventDefinitionBean studyEventDefinitionBean);

	/**
	 * This method will check if current default version is available and set another one if not.
	 *
	 * @param crfVersionBean CRFVersionBean
	 * @param updater        UserAccountBean
	 */
	void updateDefaultVersionOfEventDefinitionCRF(CRFVersionBean crfVersionBean, UserAccountBean updater);

	/**
	 * Delete crf from study event definition.
	 *
	 * @param eventDefinitionCRFBean EventDefinitionCRFBean
	 * @throws Exception in case if some item data is present for this Event Definition CRF.
	 */
	void deleteEventDefinitionCrf(EventDefinitionCRFBean eventDefinitionCRFBean) throws Exception;

	/**
	 * Check if SDV status was updated for some EDCs and update item_data and event_crf statuses.
	 *
	 * @param parentsMap         Map<Integer, EventDefinitionCRFBean>
	 * @param oldEDCsMap         Map<Integer, EventDefinitionCRFBean>
	 * @param edcItemMetadataMap HashMap<Integer, ArrayList<EDCItemMetadata>>
	 * @param updater            UserAccountBean
	 */
	void checkIfEventCRFSDVStatusWasUpdated(Map<Integer, EventDefinitionCRFBean> parentsMap,
											Map<Integer, EventDefinitionCRFBean> oldEDCsMap,
											HashMap<Integer, ArrayList<EDCItemMetadata>> edcItemMetadataMap,
											UserAccountBean updater);

	/**
	 * Save EDC Item Metadata for all EDCs with SDV status = specific items.
	 *
	 * @param edcItemMetadataMap HashMap<Integer, ArrayList<EDCItemMetadata>>
	 * @param edcBean            EventDefinitionCRFBean
	 * @return true if new items were marked as SDV required.
	 */
	boolean saveEDCItemMetadataMapToDatabaseAndCheckIfNewItemsWereAdded(HashMap<Integer, ArrayList<EDCItemMetadata>> edcItemMetadataMap,
																		EventDefinitionCRFBean edcBean);
}
