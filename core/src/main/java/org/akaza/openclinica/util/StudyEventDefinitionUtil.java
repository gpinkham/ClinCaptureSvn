package org.akaza.openclinica.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.dao.dynamicevent.DynamicEventDao;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;

/**
 * StudyEventDefinitionUtil class.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public final class StudyEventDefinitionUtil {

	private StudyEventDefinitionUtil() {
	}

	/**
	 * Method that returns list of StudyEventDefinitionBeans using incoming parameters.
	 * 
	 * @param study
	 *            StudyBean
	 * @param dedao
	 *            DynamicEventDao
	 * @param seddao
	 *            StudyEventDefinitionDAO
	 * @return List<StudyEventDefinitionBean>
	 */
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

	/**
	 * Method that returns list of StudyEventDefinitionBeans using incoming parameters.
	 * 
	 * @param studySubject
	 *            StudySubjectBean
	 * @param study
	 *            StudyBean
	 * @param dedao
	 *            DynamicEventDao
	 * @param sgcdao
	 *            StudyGroupClassDAO
	 * @param seddao
	 *            StudyEventDefinitionDAO
	 * @return List<StudyEventDefinitionBean>
	 */
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

	/**
	 * Method that returns list of StudyEventDefinitionBeans using incoming parameters.
	 * 
	 * @param studySubject
	 *            StudySubjectBean
	 * @param study
	 *            StudyBean
	 * @param dedao
	 *            DynamicEventDao
	 * @param sgcdao
	 *            StudyGroupClassDAO
	 * @param seddao
	 *            StudyEventDefinitionDAO
	 * @return List<Integer>
	 */
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

	/**
	 * Method returns list of study event definitions with statuses as in the event definition crfs.
	 * 
	 * @param ds
	 *            DataSource
	 * @param crfId
	 *            crf id
	 * @return List<StudyEventDefinitionBean>
	 */
	public static List<StudyEventDefinitionBean> studyEventDefinitionStatusUpdate(DataSource ds, int crfId) {
		EventDefinitionCRFDAO eventDefinitionCrfDao = new EventDefinitionCRFDAO(ds);
		StudyEventDefinitionDAO studyEventDefinitionDao = new StudyEventDefinitionDAO(ds);
		StudyDAO studyDao = new StudyDAO(ds);

		List<StudyEventDefinitionBean> studyEventDefinitionUpdated = new ArrayList<StudyEventDefinitionBean>();
		List<EventDefinitionCRFBean> eventDefinitionCrfList = (List<EventDefinitionCRFBean>) eventDefinitionCrfDao
				.findAllByCRF(crfId);

		HashMap<Integer, String> studyIdName = new HashMap<Integer, String>();
		ArrayList<Integer> studyIds = studyDao.getStudyIdsByCRF(crfId);

		for (int id : studyIds) {
			studyIdName.put(id, studyDao.findByPK(id).getName());
		}

		for (EventDefinitionCRFBean eventDefinitionCrfBean : eventDefinitionCrfList) {
			StudyEventDefinitionBean studyEventDefinition = (StudyEventDefinitionBean) studyEventDefinitionDao
					.findByPK(eventDefinitionCrfBean.getStudyEventDefinitionId());
			studyEventDefinition.setStudyName(studyIdName.get(eventDefinitionCrfBean.getStudyId()));
			studyEventDefinition.setStatus(eventDefinitionCrfBean.getStatus());
			studyEventDefinitionUpdated.add(studyEventDefinition);
		}

		return studyEventDefinitionUpdated;
	}

	/**
	 * Method returns list of study event definitions with at least one event definition crf with non deleted state.
	 * 
	 * @param ds
	 *            DataSource
	 * @param eventDefinitionCrfList
	 *            Collection<EventDefinitionCRFBean>
	 * @return List<StudyEventDefinitionBean>
	 */
	public static List<StudyEventDefinitionBean> studyEventDefinitionListFilter(DataSource ds,
			Collection<EventDefinitionCRFBean> eventDefinitionCrfList) {
		List<StudyEventDefinitionBean> studyEventDefinitionListFiltered = new ArrayList<StudyEventDefinitionBean>();
		StudyEventDefinitionDAO studyEventDefinitionDao = new StudyEventDefinitionDAO(ds);
		UserAccountDAO userAccountDao = new UserAccountDAO(ds);

		for (EventDefinitionCRFBean eventDefCrfBean : eventDefinitionCrfList) {

			if (!eventDefCrfBean.getStatus().isDeleted()) {
				StudyEventDefinitionBean studyEventDefinition = (StudyEventDefinitionBean) studyEventDefinitionDao
						.findByPK(eventDefCrfBean.getStudyEventDefinitionId());
				UserAccountBean userAccountBean = (UserAccountBean) userAccountDao
						.findByPK(studyEventDefinition.getOwnerId());
				studyEventDefinition.setOwner(userAccountBean);

				studyEventDefinitionListFiltered.add(studyEventDefinition);
			}
		}

		return studyEventDefinitionListFiltered;
	}
}
