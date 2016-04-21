/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 * 
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer. 
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clincapture.com/contact for pricing information.
 * 
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use. 
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.service;

import java.util.List;
import java.util.Map;

import org.akaza.openclinica.bean.managestudy.StudyBean;

import com.clinovo.model.DiscrepancyDescription;

/**
 * Service contract specification for the DiscrepancyDescription service to the DAO.
 *
 */
public interface DiscrepancyDescriptionService {
	
	/**
	 * Retrieves all DiscrepancyDescriptions.
	 * 
	 * @return List of DiscrepancyDescriptions
	 */
	public List<DiscrepancyDescription> findAll();
	
	/**
	 * Retrieves DiscrepancyDescription with specified id.
	 * 
	 * @param id The id of DiscrepancyDescription.
	 * 
	 * @return DiscrepancyDescription.
	 */
	public DiscrepancyDescription findById(int id);
	
	/**
	 * Retrieves the DiscrepancyDescriptions with specified type and created for the specified study.
	 * 
	 * @param studyId The study for which to extract the DiscrepancyDescriptions for.
	 * 
	 * @param typeId The type of DiscrepancyDescriptions.
	 * 
	 * @return The list of DiscrepancyDescriptions, if it has been configured.
	 */
	public List<DiscrepancyDescription> findAllByStudyIdAndTypeId(int studyId, int typeId);
	
	/**
	 * Persists a valid DiscrepancyDescription to storage.
	 * 
	 * @param discrepancyDescription DiscrepancyDescription to persist.
	 * 
	 * @return Persisted DiscrepancyDescription, null in case it was invalid
	 */
	public DiscrepancyDescription saveDiscrepancyDescription(DiscrepancyDescription discrepancyDescription);

	/**
	 * Deletes a specified DiscrepancyDescription from storage.
	 * 
	 * @param discrepancyDescription DiscrepancyDescription to delete.
	 */
	public void deleteDiscrepancyDescription(DiscrepancyDescription discrepancyDescription);
	
	/**
	 * Find descriptions from study in DB and split them in groups.
	 * 
	 * @param studyId int
	 * 
	 * @return Map<String, List<DiscrepancyDescription>>, new Map<String, List<DiscrepancyDescription>> in case it was invalid
	 */
	public Map<String, List<DiscrepancyDescription>> findAllSortedDescriptionsFromStudy(int studyId);

	/**
	 * Return descriptions, that assigned to study/site, from DB and split them in groups.
	 * 
	 * @param StudyBean study
	 * 
	 * @return Map<String, List<DiscrepancyDescription>>, new Map<String, List<DiscrepancyDescription>> in case it was invalid
	 */
	public Map<String, List<DiscrepancyDescription>> getAssignedToStudySortedDescriptions(StudyBean study);
}

