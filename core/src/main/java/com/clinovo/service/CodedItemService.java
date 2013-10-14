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

import java.util.List;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;

import com.clinovo.model.CodedItem;
import com.clinovo.model.Status.CodeStatus;

public interface CodedItemService {
	
	/**
	 * Retrieves all the coded items from storage
	 * 
	 * @return List of all coded items
	 */
	List<CodedItem> findAll();

	/**
	 * Returns all the coded items that belong to the specified study.
	 * 
	 * @param studyId The study to which the coded items were created
	 * 
	 * @return List of coded items that belong to the specified study
	 */
	List<CodedItem> findByStudy(int studyId);
	
	/**
	 * Returns all the coded items that belong to the specified scope.
	 * 
	 * @param studyId The study to which the coded items were created
	 * @param siteId The site in the study (above) to which the coded items were created
	 * 
	 * @return List of coded items that belong to the specified scope
	 */
	List<CodedItem> findByStudyAndSite(int studyId, int siteId);
	
	/**
	 * Retrieves all the coded items that belong to the specified item. 
	 * <p>
	 * The coded item must have been created using the specified item to qualify
	 * 
	 * @param itemId The itemId on which to search items
	 * 
	 * @return List of coded items that belong to the specified item
	 */
	List<CodedItem> findByItem(int itemId);
	
	/**
	 * Retrieves all coded items that belong to the specified event-crf
	 * 
	 * @param eventCRFId The eventCRF to filter on
	 * 
	 * @return List of coded items belonging to the specified event-crf
	 */
	List<CodedItem> findByEventCRF(int eventCRFId);

	/**
	 * Retrieves all coded items that belong to the specified crf-version
	 * 
	 * @param crfVersion The crf-version to filter on
	 * 
	 * @return List of coded items belonging to the specified crf-version
	 */
	List<CodedItem> findByCRFVersion(int crfVersion);
	
	/**
	 * Retrieves all coded items that belong to the specified subject. 
	 * <p>
	 * The coded items should have been created for the specified subject to qualify.
	 * 
	 * @param crfVersion The crf-version to filter on
	 * 
	 * @return List of coded items belonging to the specified crf-version
	 */
	List<CodedItem> findBySubject(int subject);

	/**
	 * Retrieves all the coded items that have a verbatim term matching the specified verbatim term
	 * 
	 * @param verbatimTerm The verbatim term to filter on.
	 * 
	 * @return List of coded items that have a matching verbatim term to the specified verbatim term.
	 */
	List<CodedItem> findCodedItemsByVerbatimTerm(String verbatimTerm);

	/**
	 * Retrieves all the coded items that have a verbatim term matching the specified coded term
	 * 
	 * @param codedTerm The verbatim term to filter on.
	 * 
	 * @return List of coded items that have a matching coded term to the specified coded term.
	 */
	List<CodedItem> findCodedItemsByCodedTerm(String codedTerm);

	/**
	 * Retrieves all the coded items that were coded using the same dictionary.
	 * 
	 * <p>
	 * The coded items should have been coded with the specified dictionary to qualify.
	 * 
	 * @param dictionary The dictionary to filter on.
	 * 
	 * @return List of coded items that were coded with a dictionary matching the specified dictionary.
	 */
	List<CodedItem> findCodedItemsByDictionary(String dictionary);

	/**
	 * Retrieves all the coded items that have the specified status
	 * 
	 * @param status The status to filter on.
	 * 
	 * @return List of coded items that have the specified status.
	 */
	List<CodedItem> findCodedItemsByStatus(CodeStatus status);

	/**
	 * Creates and prepares a coded item for saving -
	 * 
	 * @param eventCRF The event-crf the coded item belongs to
	 * @param item The reference CRF-item for the coded item
	 * @param itemData The data that contains the verbatim term
	 * @param currentStudy The study to which the coded item should be scoped
	 * 
	 * @return The coded item
	 * 
	 * @throws Exception For any errors during persistence.
 	 */
	CodedItem createCodedItem(EventCRFBean eventCRF, ItemBean item, ItemDataBean itemData, StudyBean currentStudy) throws Exception;
	
	/**
	 * Save the given coded item to storage.
	 * <p>
	 * Note that this method also updates the referenced CRF item with the coded item value.
	 * 
	 * @param codedItem The coded item to save
	 * 
	 * @return The saved coded item
	 * 
	 * @throws Exception If the item data for the coded item does not exist, or any other exception during persistence routines.
	 */
	CodedItem saveCodedItem(CodedItem codedItem) throws Exception;
	
	/**
	 * Retrieves a coded item given a unique id
	 * 
	 * @param codedItemId The id of the coded item to retrieve
	 * 
	 * @return The coded item, if it exists, otherwise null.
	 */
	CodedItem findCodedItem(int codedItemId);

	/**
	 * Retrieves a unique coded item given the item data that was used to create it.
	 * 
	 * @param itemDataId The item data to filter on.
	 * 
	 * @return The coded item.
	 */
	CodedItem findByItemData(int itemDataId);
	
	/**
	 * Deletes the given coded item from storage
	 * 
	 * @param codedItem The coded item to delete.
	 */
	void deleteCodedItem(CodedItem codedItem);
	
	/**
	 * Sets the status of the coded items belong to the specified CRF-Version to deleted! 
	 * <p>
	 * This means they will not show up when querying by scope
	 * 
	 * @param versionId The crf version for which to remove coded items
	 */
	void removeByCRFVersion(int versionId);

	/**
	 * Restores the deleted coded items that belong to the specified crf-version 
	 * 
	 * @param versionId The crf version for which to restore coded items
	 */
	void restoreByCRFVersion(int versionId);

	/**
	 * Purges from the database all coded items belonging to the specified crf-version 
	 * <p>
	 * This means they will not show up when querying by scope
	 * 
	 * @param versionId The crf version for which to purge coded items
	 */
	void deleteByCRFVersion(int versionId);

}
