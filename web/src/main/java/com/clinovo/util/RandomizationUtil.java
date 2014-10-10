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
import java.util.Arrays;
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
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.dao.core.EntityDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clinovo.exception.RandomizationException;
import com.clinovo.model.RandomizationResult;

/**
 * Utilities to help with randomization at the servlet layer.
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
	private static StudyParameterValueDAO studyParameterValueDAO;

	private static final Logger LOG = LoggerFactory.getLogger(RandomizationUtil.class);

	/**
	 * Ascertains whether the trial has been configured in both the crf and the properties.
	 * 
	 * @param configuredTrialId
	 *            property file configured trial Id
	 * @param crfSpecifiedId
	 *            crf configured trial id
	 * 
	 * @return True if trial match, false otherwise
	 */
	public static boolean isTrialIdDoubleConfigured(String configuredTrialId, String crfSpecifiedId) {

		return isConfiguredTrialIdValid(configuredTrialId) && isCRFSpecifiedTrialIdValid(crfSpecifiedId);
	}

	/**
	 * Checks If the property file configured trial Id configured is valid.
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
	 * Checks If the crf configured trial Id configured is valid.
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
	 * Assigns the returned randomization to a study group.
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

				LOG.error(studySubjectDAO.getFailureDetails().getMessage());
				throw new RandomizationException("Exception occurred during randomization");
			}

		} else {

			throw new RandomizationException(
					"Randomization Group not defined. Please contact your system administrator.");
		}
	}

	/**
	 * Add the returned randomization to a Study Subject ID.
	 * 
	 * @param randomizationResult
	 *            The randomization to added to SSID.
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
	 * Save returned randomization to the study_subject table and item_data table.
	 * 
	 * @param randomizationResult
	 *            The randomization result, that will be saved into DB.
	 * 
	 * @param itemsMap
	 *            HashMap with two ItemBean - randomization result item and randomization date item.
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

		StudySubjectBean subject = RandomizationUtil.studySubjectDAO.findByLabelAndStudy(
				randomizationResult.getPatientId(), RandomizationUtil.currentStudy);

		subject.setRandomizationDate(new Date());
		subject.setRandomizationResult(randomizationResult.getRandomizationResult());

		studySubjectDAO.update(subject);

		checQuerySuccessfull(studySubjectDAO);
	}

	/**
	 * Update status of EventCRF and Study event if data was saved successfully.
	 * 
	 * @param itemsMap
	 *            HashMap with two ItemBean - randomization result item and randomization date item. All information for
	 *            updates will be taken from these items.
	 * 
	 * @throws RandomizationException
	 *             If one of statuses was not updated successfully.
	 */
	public static void checkAndUpdateEventCRFAndStudyEventStatuses(HashMap<String, ItemDataBean> itemsMap)
			throws RandomizationException {

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

		StudyEventBean studyEventBean = (StudyEventBean) studyEventDAO.findByPK(eCRFBean.getStudyEventId());

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
	 *            The randomization result from which data about subject will be extracted.
	 * 
	 * @return StudySubjectBean.
	 */
	private static StudySubjectBean getStudySubjectBeanFromRandomizationResult(RandomizationResult randomizationResult) {

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

		return subject;
	}

	/**
	 * Save stratification variables to the item_data table.
	 * 
	 * @param request
	 *            <code>HttpServletRequest</code> from which all required data will be taken.
	 * 
	 * @throws RandomizationException
	 *             If data was not saved successfully.
	 * @throws JSONException
	 *             if data from request is invalid.
	 */
	public static void saveStratificationVariablesToDatabase(HttpServletRequest request) throws JSONException,
			RandomizationException {

		if (RandomizationUtil.itemDataDAO == null) {

			RandomizationUtil.itemDataDAO = new ItemDataDAO(sessionManager.getDataSource());
		}

		int eventCRFId = Integer.parseInt(request.getParameter("eventCrfId"));
		String strataLevel = request.getParameter("strataLevel").equals("null") ? "" : request
				.getParameter("strataLevel");
		String strataItems = request.getParameter("strataItemIds").equals("null") ? "" : request
				.getParameter("strataItemIds");

		JSONArray strataIds = new JSONArray(strataItems);
		JSONArray array = new JSONArray(strataLevel);

		if (array != null) {

			if (array.length() != strataIds.length()) {
				throw new RandomizationException("Error occured during the saving of the stratification variables.");
			} else {
				for (int i = 0; i < array.length(); i++) {

					String strataField = Arrays.asList(array.get(i).toString().split(",")).get(1);
					String strataValue = Arrays.asList(strataField.toString().split(":")).get(1).replace("\"", "")
							.replace("}", "");
					int itemId = Integer.parseInt(strataIds.get(i).toString());

					ItemDataBean item = itemDataDAO.findByItemIdAndEventCRFId(itemId, eventCRFId);

					item.setValue(strataValue);

					if (item.getEventCRFId() == 0) {
						setAllFieldsForNewItem(item, itemId, request);

						itemDataDAO.create(item);
						checQuerySuccessfull(itemDataDAO);
					} else {
						setAllFieldsForUpdatedItem(item, request);

						itemDataDAO.update(item);
						checQuerySuccessfull(itemDataDAO);
					}
				}
			}
		}
	}

	/**
	 * Save value of Rand_TrialIDs item to the database if exists.
	 * 
	 * @param request
	 *            <code>HttpServletRequest</code> from which all required data will be taken.
	 * 
	 * @throws RandomizationException
	 *             If data was not saved successfully.
	 */
	public static void saveTrialIDItemToDatabase(HttpServletRequest request) throws RandomizationException {

		if (RandomizationUtil.itemDataDAO == null) {

			RandomizationUtil.itemDataDAO = new ItemDataDAO(sessionManager.getDataSource());
		}

		int eventCRFId = Integer.parseInt(request.getParameter("eventCrfId"));

		if (trialIdItemExists(request)) {

			try {
				int itemId = 0;
				itemId = Integer.parseInt(request.getParameter("trialIdItemId"));

				String trialIdItemValue = request.getParameter("trialIdItemValue");
				ItemDataBean item = itemDataDAO.findByItemIdAndEventCRFId(itemId, eventCRFId);

				item.setValue(trialIdItemValue);

				if (item.getEventCRFId() == 0) {
					setAllFieldsForNewItem(item, itemId, request);

					itemDataDAO.create(item);
					checQuerySuccessfull(itemDataDAO);
				} else {
					setAllFieldsForUpdatedItem(item, request);

					itemDataDAO.update(item);
					checQuerySuccessfull(itemDataDAO);
				}
			} catch (Exception e) {
				throw new RandomizationException(
						"An error occurred during the saving of the Rand_TrialIDs variable. Please contact your system administrator.");
			}
		}
	}

	/**
	 * Get ItemDataBean for randomization result item and randomization date item.
	 * 
	 * @param request
	 *            The HttpServletRequest from which all information about items will be extracted.
	 * 
	 * @return HashMap<String, ItemDataBean> with information about randomization items.
	 */
	public static HashMap<String, ItemDataBean> getRandomizationItemData(HttpServletRequest request) {

		if (RandomizationUtil.itemDataDAO == null) {

			RandomizationUtil.itemDataDAO = new ItemDataDAO(sessionManager.getDataSource());
		}

		int eventCRFId = Integer.parseInt(request.getParameter("eventCrfId"));
		int dateItemId = Integer.parseInt(request.getParameter("dateInputId"));
		int resultItemId = Integer.parseInt(request.getParameter("resultInputId"));

		ItemDataBean resultItem = itemDataDAO.findByItemIdAndEventCRFId(resultItemId, eventCRFId);
		ItemDataBean dateItem = itemDataDAO.findByItemIdAndEventCRFId(dateItemId, eventCRFId);

		if (dateItem.getEventCRFId() == 0 || resultItem.getEventCRFId() == 0) {

			setAllFieldsForNewItem(dateItem, dateItemId, request);
			setAllFieldsForNewItem(resultItem, resultItemId, request);

			setItemDataExist(false);

		} else {

			setAllFieldsForUpdatedItem(dateItem, request);
			setAllFieldsForUpdatedItem(resultItem, request);

			setItemDataExist(true);
		}

		HashMap<String, ItemDataBean> itemsMap = new HashMap<String, ItemDataBean>();

		itemsMap.put("dateItem", dateItem);
		itemsMap.put("resultItem", resultItem);

		return itemsMap;
	}

	private static void setAllFieldsForUpdatedItem(ItemDataBean item, HttpServletRequest request) {

		UserAccountBean userAccountBean = (UserAccountBean) request.getSession().getAttribute("userBean");

		item.setOldStatus(item.getStatus());
		item.setUpdater(userAccountBean);
		item.setUpdatedDate(new Date());
		item.setStatus(Status.UNAVAILABLE);
	}

	private static void setAllFieldsForNewItem(ItemDataBean item, Integer itemId, HttpServletRequest request) {

		UserAccountBean userAccountBean = (UserAccountBean) request.getSession().getAttribute("userBean");

		int eventCRFId = Integer.parseInt(request.getParameter("eventCrfId"));

		item.setItemId(itemId);
		item.setCreatedDate(new Date());
		item.setStatus(Status.AVAILABLE);
		item.setOwner(userAccountBean);
		item.setEventCRFId(eventCRFId);
		item.setOrdinal(1);
	}

	/**
	 * Check if the query was performed successful.
	 * 
	 * @param eDao
	 *            EntityDAO which should be checked.
	 * 
	 * @throws RandomizationException
	 *             if query was not successful.
	 */
	@SuppressWarnings("rawtypes")
	private static void checQuerySuccessfull(EntityDAO eDao) throws RandomizationException {

		if (eDao.isQuerySuccessful()) {

			return;
		} else {

			LOG.error(eDao.getFailureDetails().getMessage());
			throw new RandomizationException("Exception occurred during randomization");
		}
	}

	/**
	 * Gets randomization trialId configured for the study.
	 * 
	 * @param study
	 *            Study to check
	 * @return study's randomization trialId or empty string if trialId is not set
	 */
	public static String getRandomizationTrialIdByStudy(StudyBean study) {
		if (RandomizationUtil.studyParameterValueDAO == null) {
			RandomizationUtil.studyParameterValueDAO = new StudyParameterValueDAO(sessionManager.getDataSource());
		}
		int studyId = study.getParentStudyId() > 0 ? study.getParentStudyId() : study.getId();
		StudyParameterValueBean studyParameter = RandomizationUtil.studyParameterValueDAO.findByHandleAndStudy(studyId,
				"randomizationTrialId");
		if (studyParameter != null && studyParameter.getValue() != null) {
			return studyParameter.getValue();
		} else {
			return "";
		}
	}

	private static boolean trialIdItemExists(HttpServletRequest request) {

		String trialId = request.getParameter("trialId");
		return isCRFSpecifiedTrialIdValid(trialId);
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

	public static void setStudyParameterValueDAO(StudyParameterValueDAO studyParameterValueDAO) {
		RandomizationUtil.studyParameterValueDAO = studyParameterValueDAO;
	}
}
