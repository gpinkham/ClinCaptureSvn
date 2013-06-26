package com.clinovo.util;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clinovo.exception.RandomizationException;
import com.clinovo.model.RandomizationResult;

@SuppressWarnings("rawtypes")
public class RandomizationUtil {

	private static StudyBean currentStudy;

	private static SessionManager sessionManager;
	private static StudySubjectDAO studySubjectDAO;
	private static StudyGroupClassDAO studyGroupDAO;
	
	private final static Logger log = LoggerFactory.getLogger(RandomizationUtil.class);

	public static boolean isTrialIdDoubleConfigured(String configuredTrialId, String crfSpecifiedId) {

		if (isConfiguredTrialIdValid(configuredTrialId) && isCRFSpecifiedTrialIdValid(crfSpecifiedId)) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isConfiguredTrialIdValid(String trialId) {

		return trialId != null && trialId.length() > 0 && !trialId.equals("0");
	}

	public static boolean isCRFSpecifiedTrialIdValid(String trialId) {

		return !trialId.equals("null") && trialId.length() > 0 && !trialId.equals("undefined");
	}

	public static StudyGroupClassBean assignSubjectToGroup(RandomizationResult randomizationResult)
			throws RandomizationException {

		// Allow for testing
		if (RandomizationUtil.studyGroupDAO == null) {
			RandomizationUtil.studyGroupDAO = new StudyGroupClassDAO(sessionManager.getDataSource());
		}

		// Allow for testing
		if (RandomizationUtil.studySubjectDAO == null) {
			RandomizationUtil.studySubjectDAO = new StudySubjectDAO(sessionManager.getDataSource());
		}

		StudyGroupClassBean studyGroupClass = RandomizationUtil.studyGroupDAO.findByNameAndStudyId(
				randomizationResult.getRandomizationResult(), Integer.valueOf(randomizationResult.getStudyId()));

		if (studyGroupClass.getId() > 0) {

			// We have a group that matches the random group
			StudySubjectBean subject = RandomizationUtil.studySubjectDAO.findByLabelAndStudy(
					randomizationResult.getPatientId(), RandomizationUtil.currentStudy);

			subject.setDynamicGroupClassId(studyGroupClass.getId());
			
			studySubjectDAO.update(subject);
			
			if(studySubjectDAO.isQuerySuccessful()) {
				
				return studyGroupClass;
				
			} else {
				
				log.error(studySubjectDAO.getFailureDetails().getMessage());
				throw new RandomizationException("Exception occurred during randomization");
			}

		} else {

			throw new RandomizationException(
					"Randomization Group not defined. Please contact your system administrator.");
		}
	}

	public static void setStudyGroupDAO(StudyGroupClassDAO studyGroupDAO) {
		RandomizationUtil.studyGroupDAO = studyGroupDAO;

	}

	public static void setStudySubjectDAO(StudySubjectDAO studySubjectDAO) {

		RandomizationUtil.studySubjectDAO = studySubjectDAO;

	}

	public static void setSessionManager(SessionManager sessionManager) {

		RandomizationUtil.sessionManager = sessionManager;
	}

	public static void setCurrentStudy(StudyBean currentStudy) {
		RandomizationUtil.currentStudy = currentStudy;
	}

}
