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