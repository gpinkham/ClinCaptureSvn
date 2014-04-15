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
package com.clinovo.util;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clinovo.exception.RandomizationException;
import com.clinovo.model.RandomizationResult;

/**
 * Utilities to help with randomization at the servlet layer -
 * 
 */
public class RandomizationUtil {

	private static StudyBean currentStudy;

	private static SessionManager sessionManager;
	private static StudySubjectDAO studySubjectDAO;
	private static StudyGroupClassDAO studyGroupDAO;
	private static SubjectDAO subjectDAO;

	private final static Logger log = LoggerFactory.getLogger(RandomizationUtil.class);

	/**
	 * Ascertains whether the trial has been configured in both the crf and the properties
	 * 
	 * @param configuredTrialId
	 *            property file configured trial Id
	 * @param crfSpecifiedId
	 *            crf configured trial id
	 * 
	 * @return True if trial match, false otherwise
	 */
	public static boolean isTrialIdDoubleConfigured(String configuredTrialId, String crfSpecifiedId) {

		if (isConfiguredTrialIdValid(configuredTrialId) && isCRFSpecifiedTrialIdValid(crfSpecifiedId)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Checks If the property file configured trial Id configured is valid
	 * 
	 * @param trialId
	 *            The trial id to check
	 * 
	 * @return If it is not null or empty, otherwise false.
	 */
	public static boolean isConfiguredTrialIdValid(String trialId) {

		return trialId != null && trialId.length() > 0 && !trialId.equals("0");
	}

	/**
	 * Checks If the crf configured trial Id configured is valid
	 * 
	 * @param trialId
	 *            The trial id to check
	 * 
	 * @return If it is not null or empty, otherwise false.
	 */
	public static boolean isCRFSpecifiedTrialIdValid(String trialId) {

		return !trialId.equals("null") && trialId.length() > 0 && !trialId.equals("undefined");
	}

	/**
	 * Assigns the returned randomization to a study group -
	 * 
	 * @param randomizationResult
	 *            The randomization to assign to group.
	 * 
	 * @return Group that the randomization was assigned to.
	 * 
	 * @throws RandomizationException
	 *             If the group does not exist.
	 */
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

		if (studyGroupClass != null && studyGroupClass.getId() > 0) {

			// We have a group that matches the random group
			StudySubjectBean subject = RandomizationUtil.studySubjectDAO.findByLabelAndStudy(
					randomizationResult.getPatientId(), RandomizationUtil.currentStudy);

			subject.setDynamicGroupClassId(studyGroupClass.getId());

			studySubjectDAO.update(subject);

			if (studySubjectDAO.isQuerySuccessful()) {

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

	/**
	 * Add the returned randomization to a Study Subject ID
	 * 
	 * @param randomizationResult
	 *            The randomization to added to SSID.
	 * 
	 * @return 4 digits code that will be added to SSID.
	 * 
	 * @throws RandomizationException
	 *             If generated SSID already exists in Study.
	 */
	public static void addRandomizationResultToSSID(RandomizationResult randomizationResult)
			throws RandomizationException {

		if (RandomizationUtil.studySubjectDAO == null) {
			RandomizationUtil.studySubjectDAO = new StudySubjectDAO(sessionManager.getDataSource());
		}

		if (RandomizationUtil.subjectDAO == null) {
			RandomizationUtil.subjectDAO = new SubjectDAO(sessionManager.getDataSource());
		}

		SubjectBean subjectBean = RandomizationUtil.subjectDAO.findByUniqueIdentifierAndStudy(
				randomizationResult.getPatientId(), RandomizationUtil.currentStudy.getId());

		StudySubjectBean subject = RandomizationUtil.studySubjectDAO.findBySubjectIdAndStudy(subjectBean.getId(),
				RandomizationUtil.currentStudy);

		String newSubjectId = randomizationResult.getRandomizationResult();

		subject.setLabel(newSubjectId);
		studySubjectDAO.update(subject);

		if (studySubjectDAO.isQuerySuccessful()) {

			return;
		} else {

			log.error(studySubjectDAO.getFailureDetails().getMessage());
			throw new RandomizationException("Exception occurred during randomization");
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
