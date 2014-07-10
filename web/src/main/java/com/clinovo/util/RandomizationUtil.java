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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.dao.core.EntityDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
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
	private static boolean itemDataExist;

	private static SessionManager sessionManager;
	private static StudySubjectDAO studySubjectDAO;
	private static StudyGroupClassDAO studyGroupDAO;
	private static SubjectDAO subjectDAO;
	private static ItemDataDAO itemDataDAO;
	private static EventCRFDAO eventCRFDAO;
	private static StudyEventDAO studyEventDAO;

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

		StudySubjectBean subject = getStudySubjectBeanFromRandomizationResult(randomizationResult);

		String newSubjectId = randomizationResult.getRandomizationResult();

		subject.setLabel(newSubjectId);
		studySubjectDAO.update(subject);

		checQuerySuccessfull(studySubjectDAO);
	}

	/**
	 * Save returned randomization to the study_subject table
	 * and item_data table.
	 * 
	 * @param randomizationResult
	 *            The randomization result, that will be saved into DB.
	 * 
	 * @param itemsMap
	 *            HashMap with two ItemBean - randomization result item
	 *            and randomization date item.
	 * 
	 * @throws RandomizationException
	 *             If data was not saved successfully.
	 */
	public static void saveRandomizationResultToDatabase(RandomizationResult randomizationResult,
			HashMap<String, ItemDataBean> itemsMap) throws RandomizationException {

		if (RandomizationUtil.itemDataDAO == null) {

			RandomizationUtil.itemDataDAO = new ItemDataDAO(sessionManager.getDataSource());
		}

		ItemDataBean dateItem = (ItemDataBean) itemsMap.get("dateItem");
		ItemDataBean resultItem = (ItemDataBean) itemsMap.get("resultItem");

		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

		dateItem.setValue(formatter.format(new Date()));
		resultItem.setValue(randomizationResult.getRandomizationResult());

		if (itemDataExist) {

			itemDataDAO.update(resultItem);
			itemDataDAO.update(dateItem);

			checQuerySuccessfull(itemDataDAO);
		} else {

			itemDataDAO.create(resultItem);
			itemDataDAO.create(dateItem);

			checQuerySuccessfull(itemDataDAO);
		}

		if (RandomizationUtil.studySubjectDAO == null) {
			RandomizationUtil.studySubjectDAO = new StudySubjectDAO(sessionManager.getDataSource());
		}

		StudySubjectBean subject = RandomizationUtil.studySubjectDAO
				.findByLabelAndStudy(randomizationResult.getPatientId(),
						RandomizationUtil.currentStudy);

		subject.setRandomizationDate(new Date());
		subject.setRandomizationResult(randomizationResult
				.getRandomizationResult());

		studySubjectDAO.update(subject);

		checQuerySuccessfull(studySubjectDAO);
	}

	/**
	 * Update status of EventCRF and Study event if data was saved successfully.
	 * 
	 * @param itemsMap
	 *            HashMap with two ItemBean - randomization result item
	 *            and randomization date item. All information for updates will
	 *            be taken from these items.
	 * 
	 * @throws RandomizationException
	 *             If one of statuses was not updated successfully.
	 */
	public static void checkAndUpdateEventCRFAndStudyEventStatuses(
			HashMap<String, ItemDataBean> itemsMap) throws RandomizationException {

		if (RandomizationUtil.studyEventDAO == null) {

			RandomizationUtil.studyEventDAO = new StudyEventDAO(sessionManager.getDataSource());
		}

		if (RandomizationUtil.eventCRFDAO == null) {

			RandomizationUtil.eventCRFDAO = new EventCRFDAO(sessionManager.getDataSource());
		}

		ItemDataBean resultItem = itemsMap.get("resultItem");
		UserAccountBean ub = new UserAccountBean();
		int eventCRFId = resultItem.getEventCRFId();

		ub = itemDataExist ? resultItem.getUpdater() : resultItem.getOwner();

		EventCRFBean eCRFBean = (EventCRFBean) eventCRFDAO.findByPK(eventCRFId);

		if (eCRFBean.isNotStarted()) {

			eCRFBean.setNotStarted(false);
			eCRFBean.setOwner(ub);
			eCRFBean.setUpdatedDate(new Date());
			eCRFBean.setUpdater(ub);
			eventCRFDAO.update(eCRFBean);

			checQuerySuccessfull(eventCRFDAO);
		}

		StudyEventBean studyEventBean = (StudyEventBean) studyEventDAO
				.findByPK(eCRFBean.getStudyEventId());

		if (studyEventBean.getSubjectEventStatus().isScheduled()) {
			
			studyEventBean.setSubjectEventStatus(SubjectEventStatus.DATA_ENTRY_STARTED);
			studyEventBean.setPrevSubjectEventStatus(SubjectEventStatus.SCHEDULED);
			studyEventBean.setUpdatedDate(new Date());
			studyEventBean.setUpdater(ub);
			studyEventDAO.update(studyEventBean);

			checQuerySuccessfull(studyEventDAO);
		}
	}

	/**
	 * Get StudySubject bean from randomization result.
	 * 
	 * @param randomizationResult
	 *            The randomization result from which data about subject 
	 *            will be extracted.
	 * 
	 * @return StudySubjectBean.
	 */
	private static StudySubjectBean getStudySubjectBeanFromRandomizationResult(
			RandomizationResult randomizationResult) {

		if (RandomizationUtil.studySubjectDAO == null) {
			RandomizationUtil.studySubjectDAO = new StudySubjectDAO(
					sessionManager.getDataSource());
		}

		if (RandomizationUtil.subjectDAO == null) {
			RandomizationUtil.subjectDAO = new SubjectDAO(
					sessionManager.getDataSource());
		}

		SubjectBean subjectBean = RandomizationUtil.subjectDAO
				.findByUniqueIdentifierAndStudy(
						randomizationResult.getPatientId(),
						RandomizationUtil.currentStudy.getId());

		StudySubjectBean subject = RandomizationUtil.studySubjectDAO
				.findBySubjectIdAndStudy(subjectBean.getId(),
						RandomizationUtil.currentStudy);

		return subject;
	}

	/**
	 * Get ItemDataBean for randomization result item and randomization date item.
	 * 
	 * @param request
	 *            The HttpServletRequest from which all information about items
	 *            will be extracted.
	 * 
	 * @return HashMap<String, ItemDataBean> 
	 *            with information about randomization items.
	 */
	public static HashMap<String, ItemDataBean> getRandomizationItemData(
			HttpServletRequest request) {

		if (RandomizationUtil.itemDataDAO == null) {

			RandomizationUtil.itemDataDAO = new ItemDataDAO(sessionManager.getDataSource());
		}

		UserAccountBean userAccountBean = (UserAccountBean) request.getSession().getAttribute(
				"userBean");

		int eventCRFId = Integer.parseInt(request.getParameter("eventCrfId"));
		int dateItemId = Integer.parseInt(request.getParameter("dateInputId"));
		int resultItemId = Integer.parseInt(request
				.getParameter("resultInputId"));

		ItemDataBean resultItem = itemDataDAO.findByItemIdAndEventCRFId(
				resultItemId, eventCRFId);
		ItemDataBean dateItem = itemDataDAO.findByItemIdAndEventCRFId(
				dateItemId, eventCRFId);

		if (dateItem.getEventCRFId() == 0 || resultItem.getEventCRFId() == 0) {

			dateItem.setCreatedDate(new Date());
			dateItem.setStatus(Status.AVAILABLE);
			dateItem.setOwner(userAccountBean);
			dateItem.setItemId(dateItemId);
			dateItem.setEventCRFId(eventCRFId);
			dateItem.setOrdinal(1);

			resultItem.setCreatedDate(new Date());
			resultItem.setStatus(Status.AVAILABLE);
			resultItem.setOwner(userAccountBean);
			resultItem.setItemId(resultItemId);
			resultItem.setEventCRFId(eventCRFId);
			resultItem.setOrdinal(1);

			setItemDataExist(false);

		} else {

			dateItem.setOldStatus(dateItem.getStatus());
			dateItem.setUpdater(userAccountBean);
			dateItem.setUpdatedDate(new Date());
			dateItem.setStatus(Status.UNAVAILABLE);

			resultItem.setOldStatus(resultItem.getStatus());
			resultItem.setUpdater(userAccountBean);
			resultItem.setUpdatedDate(new Date());
			resultItem.setStatus(Status.UNAVAILABLE);

			setItemDataExist(true);
		}

		HashMap<String, ItemDataBean> itemsMap = new HashMap<String, ItemDataBean>();

		itemsMap.put("dateItem", dateItem);
		itemsMap.put("resultItem", resultItem);

		return itemsMap;
	}

	/**
	 * Check if the query was performed successful.
	 * 
	 * @param eDao
	 *            EntityDAO which should be checked.
	 * 
	 * @throws RandomizationException 
	 *            if query was not successful.
	 */
	@SuppressWarnings("rawtypes")
	private static void checQuerySuccessfull(EntityDAO eDao)
			throws RandomizationException {

		if (eDao.isQuerySuccessful()) {

			return;
		} else {

			log.error(eDao.getFailureDetails().getMessage());
			throw new RandomizationException(
					"Exception occurred during randomization");
		}
	}

	public static void setStudyGroupDAO(StudyGroupClassDAO studyGroupDAO) {

		RandomizationUtil.studyGroupDAO = studyGroupDAO;
	}

	public static void setStudySubjectDAO(StudySubjectDAO studySubjectDAO) {

		RandomizationUtil.studySubjectDAO = studySubjectDAO;
	}

	public static void setSubjectDAO(SubjectDAO subjectDAO) {

		RandomizationUtil.subjectDAO = subjectDAO;
	}

	public static void setSessionManager(SessionManager sessionManager) {

		RandomizationUtil.sessionManager = sessionManager;
	}

	public static void setCurrentStudy(StudyBean currentStudy) {

		RandomizationUtil.currentStudy = currentStudy;
	}

	public static void setItemDataDAO(ItemDataDAO itemDataDAO) {

		RandomizationUtil.itemDataDAO = itemDataDAO;
	}

	public static void setItemDataExist(boolean itemDataExist) {

		RandomizationUtil.itemDataExist = itemDataExist;
	}
}
