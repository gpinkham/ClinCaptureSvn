package com.clinovo.service;

import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;

/**
 * EventDefinitionService.
 */
public interface EventDefinitionService {

	/**
	 * Method that creates new study event definition.
	 *
	 * @param studyBean
	 *            StudyBean
	 * @param emailUser
	 *            String
	 * @param studyEventDefinitionBean
	 *            StudyEventDefinitionBean
	 */
	void createStudyEventDefinition(StudyBean studyBean, String emailUser,
			StudyEventDefinitionBean studyEventDefinitionBean);

	/**
	 * Method that updates study event definition.
	 *
	 * @param studyBean
	 *            StudyBean
	 * @param emailUser
	 *            String
	 * @param studyEventDefinitionBean
	 *            StudyEventDefinitionBean
	 */
	void updateStudyEventDefinition(StudyBean studyBean, String emailUser,
			StudyEventDefinitionBean studyEventDefinitionBean);

	/**
	 * Add event definition crf.
	 * 
	 * @param eventDefinitionCrfBean
	 *            EventDefinitionCRFBean
	 */
	void addEventDefinitionCrf(EventDefinitionCRFBean eventDefinitionCrfBean);

	/**
	 * Fills info for EventDefinitionCRFBeans.
	 * 
	 * @param currentStudy
	 *            StudyBean
	 * @param studyEventDefinitionBean
	 *            StudyEventDefinitionBean
	 */
	void fillEventDefinitionCrfs(StudyBean currentStudy, StudyEventDefinitionBean studyEventDefinitionBean);
}
