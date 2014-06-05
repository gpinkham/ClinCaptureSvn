package org.akaza.openclinica.util;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.dao.dynamicevent.DynamicEventDao;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({ "unchecked", "rawtypes" })
public final class StudyEventDefinitionUtil {

	private StudyEventDefinitionUtil() {
	}

	public static List<StudyEventDefinitionBean> getStudyEventDefinitionsForStudy(StudyBean study,
			DynamicEventDao dedao, StudyEventDefinitionDAO seddao) {
		List<StudyEventDefinitionBean> studyEventDefinitions;
		if (study.getParentStudyId() > 0) {
			studyEventDefinitions = seddao.findAllActiveByParentStudyId(study.getParentStudyId(),
					dedao.findAllDefIdsInActiveDynGroupsByStudyId(study.getParentStudyId()));
		} else {
			studyEventDefinitions = seddao.findAllActiveByParentStudyId(study.getId(),
					dedao.findAllDefIdsInActiveDynGroupsByStudyId(study.getId()));
		}
		return studyEventDefinitions;
	}

	public static List<StudyEventDefinitionBean> getStudyEventDefinitionsForStudySubject(StudySubjectBean studySubject,
			StudyBean study, DynamicEventDao dedao, StudyGroupClassDAO sgcdao, StudyEventDefinitionDAO seddao) {
		StudyGroupClassBean subjDynGroup = new StudyGroupClassBean();
		List<StudyEventDefinitionBean> studyEventDefinitions = getStudyEventDefinitionsForStudy(study, dedao, seddao);
		StudyGroupClassBean defaultGroup = (StudyGroupClassBean) sgcdao
				.findDefaultByStudyId(study.getParentStudyId() > 0 ? study.getParentStudyId() : study.getId());
		if (studySubject.getDynamicGroupClassId() != 0) {
			subjDynGroup = (StudyGroupClassBean) sgcdao.findByPK(studySubject.getDynamicGroupClassId());
			studyEventDefinitions.addAll(seddao.findAllActiveOrderedByStudyGroupClassId(subjDynGroup.getId()));
		}
		if (defaultGroup.getId() > 0 && defaultGroup.getId() != subjDynGroup.getId()) {
			studyEventDefinitions.addAll(seddao.findAllActiveOrderedByStudyGroupClassId(defaultGroup.getId()));
		}

		return studyEventDefinitions;
	}

	public static List<Integer> getStudyEventDefinitionIdsForStudySubject(StudySubjectBean studySubject,
			StudyBean study, DynamicEventDao dedao, StudyGroupClassDAO sgcdao, StudyEventDefinitionDAO seddao) {
		List<Integer> studyEventDefinitionIds = new ArrayList<Integer>();
		List<StudyEventDefinitionBean> studyEventDefinitions = getStudyEventDefinitionsForStudySubject(studySubject,
				study, dedao, sgcdao, seddao);
		for (StudyEventDefinitionBean studyEventDefinition : studyEventDefinitions) {
			studyEventDefinitionIds.add(studyEventDefinition.getId());
		}
		return studyEventDefinitionIds;
	}
}
