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

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO'S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/

package com.clinovo.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clinovo.dao.DiscrepancyDescriptionDAO;
import com.clinovo.model.DiscrepancyDescription;
import com.clinovo.model.DiscrepancyDescriptionType;
import com.clinovo.service.DiscrepancyDescriptionService;

@Transactional
@Service("discrepancyDescriptionService")
public class DiscrepancyDescriptionServiceImpl implements DiscrepancyDescriptionService {

	@Autowired DiscrepancyDescriptionDAO discrepancyDescriptionDAO;
	
	public DiscrepancyDescription findById(int id) {
		return discrepancyDescriptionDAO.findById(id);
	}
	
	public List<DiscrepancyDescription> findAll() {
		return discrepancyDescriptionDAO.findAll();
	}

	public List<DiscrepancyDescription> findAllByStudyIdAndTypeId(int studyId, int typeId) {
		return discrepancyDescriptionDAO.findAllByStudyIdAndTypeId(studyId, typeId);
	}

	public DiscrepancyDescription saveDiscrepancyDescription(
			DiscrepancyDescription discrepancyDescription) {
		return discrepancyDescriptionDAO.saveOrUpdate(discrepancyDescription);
	}

	public void deleteDiscrepancyDescription(DiscrepancyDescription discrepancyDescription) {
		discrepancyDescriptionDAO.deleteDiscrepancyDescription(discrepancyDescription);
	}
	
	public Map<String, List<DiscrepancyDescription>> findAllSortedDescriptionsFromStudy(int studyId) {
		Map<String, List<DiscrepancyDescription>> dDescriptionsMap = new HashMap<String, List<DiscrepancyDescription>>();
		
		dDescriptionsMap.put("dnUpdateDescriptions", findAllByStudyIdAndTypeId(studyId, DiscrepancyDescriptionType.DescriptionType.UPDATE_DESCRIPTION.getId()));
		dDescriptionsMap.put("dnCloseDescriptions", findAllByStudyIdAndTypeId(studyId, DiscrepancyDescriptionType.DescriptionType.CLOSE_DESCRIPTION.getId()));
		dDescriptionsMap.put("dnRFCDescriptions", findAllByStudyIdAndTypeId(studyId, DiscrepancyDescriptionType.DescriptionType.RFC_DESCRIPTION.getId()));
		
		return dDescriptionsMap;
	}
	
	public Map<String, List<DiscrepancyDescription>> getAssignedToStudySortedDescriptions(StudyBean study) {
		Map<String, List<DiscrepancyDescription>> dDescriptionsMap = new HashMap<String, List<DiscrepancyDescription>>();
		
		dDescriptionsMap.put("dnUpdateDescriptions", getUpdateDescriptions(study));
		dDescriptionsMap.put("dnCloseDescriptions", getCloseDescriptions(study));
		dDescriptionsMap.put("dnRFCDescriptions", getRFCDescriptions(study));
		
		return dDescriptionsMap;
	}
	
	private ArrayList<DiscrepancyDescription> getUpdateDescriptions(StudyBean study) {
		return findSpecifiedDescriptions(study, DiscrepancyDescriptionType.DescriptionType.UPDATE_DESCRIPTION.getId());
	}
	
	private ArrayList<DiscrepancyDescription> getCloseDescriptions(StudyBean study) {
		return findSpecifiedDescriptions(study, DiscrepancyDescriptionType.DescriptionType.CLOSE_DESCRIPTION.getId());
	}
	
	private ArrayList<DiscrepancyDescription> getRFCDescriptions(StudyBean study) {
		return findSpecifiedDescriptions(study, DiscrepancyDescriptionType.DescriptionType.RFC_DESCRIPTION.getId());
	}
	
	private ArrayList<DiscrepancyDescription> findSpecifiedDescriptions(StudyBean study, int typeId) {
		ArrayList<DiscrepancyDescription> result = new ArrayList<DiscrepancyDescription>();
		ArrayList<DiscrepancyDescription> siteVisibleDescs = new ArrayList<DiscrepancyDescription>();
		ArrayList<DiscrepancyDescription> studyVisibleDescs = new ArrayList<DiscrepancyDescription>();
		int parentStudyId = study.getParentStudyId() > 0 ? study.getParentStudyId() : study.getId();
		ArrayList<DiscrepancyDescription> rfcDescriptions = (ArrayList<DiscrepancyDescription>) discrepancyDescriptionDAO.findAllByStudyIdAndTypeId(parentStudyId, typeId);
		for (DiscrepancyDescription rfcTerm : rfcDescriptions) {
			if ("Site".equals(rfcTerm.getVisibilityLevel())) {
				siteVisibleDescs.add(rfcTerm);
			} else if ("Study".equals(rfcTerm.getVisibilityLevel())) {
				studyVisibleDescs.add(rfcTerm);
			} else if ("Study and Site".equals(rfcTerm.getVisibilityLevel())) {
				studyVisibleDescs.add(rfcTerm);
				siteVisibleDescs.add(rfcTerm);
			}
		}
		
		if (study.getParentStudyId() > 0) {
			result = siteVisibleDescs;
		} else {
			result = studyVisibleDescs;
		}
		return result;
	}
}