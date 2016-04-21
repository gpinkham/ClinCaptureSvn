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

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVOâ€™S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/

package com.clinovo.service;

import java.util.List;

import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;

/**
 * Defines event CRF bean service interface.
 * 
 */
public interface EventCRFService {

	/**
	 * Delete the EventCRFBean.
	 *
	 * @param eventCRF
	 *            EventCRFBean to be deleted.
	 * @param updater
	 *            user, that initiated action.
	 * @throws Exception
	 *             exception
	 */
	void deleteEventCRF(EventCRFBean eventCRF, UserAccountBean updater) throws Exception;

	/**
	 * Removes status of the given EventCRFBean to REMOVED.
	 * 
	 * @param eventCRF
	 *            EventCRFBean to be removed.
	 * @param updater
	 *            user, that initiated action.
	 * @throws Exception
	 *             exception
	 */
	void removeEventCRF(EventCRFBean eventCRF, UserAccountBean updater) throws Exception;

	/**
	 * Restores given EventCRFBean with previous status.
	 *
	 * @param eventCRF
	 *            EventCRFBean to be restored.
	 * @param updater
	 *            user, that initiated action.
	 * @throws Exception
	 *             exception
	 */
	void restoreEventCRF(EventCRFBean eventCRF, UserAccountBean updater) throws Exception;

	/**
	 * Removes status of the EventCRFBeans, which are contained inside given StudyEventBean, to AUTO_REMOVED.
	 * 
	 * @param studyEvent
	 *            StudyEventBean, whose EventCRFBeans are intended to be removed.
	 * @param updater
	 *            user, that initiated action.
	 * @throws Exception
	 *             exception
	 */
	void removeEventCRFs(StudyEventBean studyEvent, UserAccountBean updater) throws Exception;

	/**
	 * Restores EventCRFBeans, which are contained inside given StudyEventBean, with previous statuses.
	 *
	 * @param studyEvent
	 *            StudyEventBean, whose EventCRFBeans are intended to be restored.
	 * @param updater
	 *            user, that initiated action.
	 * @throws Exception
	 *             exception
	 */
	void restoreEventCRFs(StudyEventBean studyEvent, UserAccountBean updater) throws Exception;

	/**
	 * Locks status of the EventCRFBeans, which are contained inside given StudyEventBean, to AUTO_REMOVED.
	 *
	 * @param studyEvent
	 *            StudyEventBean, whose EventCRFBeans are intended to be removed.
	 * @param updater
	 *            user, that initiated action.
	 * @throws Exception
	 *             exception
	 */
	void lockEventCRFs(StudyEventBean studyEvent, UserAccountBean updater) throws Exception;

	/**
	 * Unlocks EventCRFBeans, which are contained inside given StudyEventBean, with previous statuses.
	 *
	 * @param studyEvent
	 *            StudyEventBean, whose EventCRFBeans are intended to be restored.
	 * @param updater
	 *            user, that initiated action.
	 * @throws Exception
	 *             exception
	 */
	void unlockEventCRFs(StudyEventBean studyEvent, UserAccountBean updater) throws Exception;

	/**
	 * Removes event CRF beans by event definition CRF bean. Target event definition CRF bean is determined by its study
	 * event definition OID and by CRF bean OID, it owns. Event CRF beans status will be set to AUTO_REMOVED.
	 *
	 * @param studyEventDefinitionOID
	 *            OID of the study event definition, which owns the target event definition CRF bean.
	 * @param crfOID
	 *            OID of the CRF bean, which is owned by the target event definition CRF bean.
	 * @param updater
	 *            user, that initiated action.
	 * @throws Exception
	 *             exception
	 */
	void removeEventCRFs(String studyEventDefinitionOID, String crfOID, UserAccountBean updater) throws Exception;

	/**
	 * Restores event CRF beans by event definition CRF bean. Target event definition CRF bean is determined by its
	 * study event definition OID and by CRF bean OID, it owns. Restores EventCRFBeans with current status AUTO_REMOVED
	 * only.
	 *
	 * @param studyEventDefinitionOID
	 *            OID of the study event definition, which owns the target event definition CRF bean.
	 * @param crfOID
	 *            OID of the CRF bean, which is owned by the target event definition CRF bean.
	 * @param updater
	 *            user, that initiated action.
	 * @throws Exception
	 *             exception
	 */
	void restoreEventCRFs(String studyEventDefinitionOID, String crfOID, UserAccountBean updater) throws Exception;

	/**
	 * Removes status of the EventCRFBeans from given list to AUTO_REMOVED.
	 * 
	 * @param eventCRFs
	 *            list of EventCRFBeans, which are intended to be removed.
	 * @param updater
	 *            user, that initiated action.
	 * @throws Exception
	 *             exception
	 */
	void removeEventCRFs(List<EventCRFBean> eventCRFs, UserAccountBean updater) throws Exception;

	/**
	 * Restores EventCRFBeans with current status AUTO_REMOVED to their previous statuses.
	 * 
	 * @param eventCRFs
	 *            list of EventCRFBeans, which are intended to be restored.
	 * @param updater
	 *            user, that initiated action.
	 * @throws Exception
	 *             exception
	 */
	void restoreEventCRFs(List<EventCRFBean> eventCRFs, UserAccountBean updater) throws Exception;

	/**
	 * Locks status of the EventCRFBeans from given list to AUTO_REMOVED.
	 *
	 * @param eventCRFs
	 *            list of EventCRFBeans, which are intended to be removed.
	 * @param updater
	 *            user, that initiated action.
	 * @throws Exception
	 *             exception
	 */
	void lockEventCRFs(List<EventCRFBean> eventCRFs, UserAccountBean updater) throws Exception;

	/**
	 * Unlocks EventCRFBeans with current status AUTO_REMOVED to their previous statuses.
	 *
	 * @param eventCRFs
	 *            list of EventCRFBeans, which are intended to be restored.
	 * @param updater
	 *            user, that initiated action.
	 * @throws Exception
	 *             exception
	 */
	void unlockEventCRFs(List<EventCRFBean> eventCRFs, UserAccountBean updater) throws Exception;

	/**
	 * Removes status of the EventCRFBeans from given list to AUTO_REMOVED.
	 *
	 * @param crfVersionBean
	 *            CRFVersionBean
	 * @param updater
	 *            user, that initiated action.
	 * @throws Exception
	 *             exception
	 */
	void removeEventCRFs(CRFVersionBean crfVersionBean, UserAccountBean updater) throws Exception;

	/**
	 * Restores EventCRFBeans with current status AUTO_REMOVED to their previous statuses.
	 *
	 * @param crfVersionBean
	 *            CRFVersionBean
	 * @param updater
	 *            user, that initiated action.
	 * @throws Exception
	 *             exception
	 */
	void restoreEventCRFs(CRFVersionBean crfVersionBean, UserAccountBean updater) throws Exception;

	/**
	 * Locks status of the EventCRFBeans from given list to AUTO_REMOVED.
	 *
	 * @param crfVersionBean
	 *            CRFVersionBean
	 * @param updater
	 *            user, that initiated action.
	 * @throws Exception
	 *             exception
	 */
	void lockEventCRFs(CRFVersionBean crfVersionBean, UserAccountBean updater) throws Exception;

	/**
	 * unlocks EventCRFBeans with current status AUTO_REMOVED to their previous statuses.
	 *
	 * @param crfVersionBean
	 *            CRFVersionBean
	 * @param updater
	 *            user, that initiated action.
	 * @throws Exception
	 *             exception
	 */
	void unlockEventCRFs(CRFVersionBean crfVersionBean, UserAccountBean updater) throws Exception;

	/**
	 * Updates study event state by event crf.
	 *
	 * @param eventCRF
	 *            EventCRFBean
	 * @param updater
	 *            UserAccountBean
	 */
	void updateStudyEventStatus(EventCRFBean eventCRF, UserAccountBean updater);

	/**
	 * Gets the next crf that the current users can enter data for. If there is no next crf for data entry, it returns
	 * null.
	 * 
	 * @param currentStudyEventBean
	 *            Current study event
	 * @param currentEventDefCRF
	 *            Current Event Definition CRF
	 * @param currentUser
	 *            Current user
	 * @param currentUserRole
	 *            Current user's role in current study
	 * @param currentStudy
	 *            Current study
	 * @return EventCRFBean for next crf
	 */
	EventCRFBean getNextEventCRFForDataEntry(StudyEventBean currentStudyEventBean,
			EventDefinitionCRFBean currentEventDefCRF, UserAccountBean currentUser, StudyUserRoleBean currentUserRole,
			StudyBean currentStudy);

	/**
	 * Get all started event CRF, and set correct Study name and Event Name for them.
	 * 
	 * @param eventCRFBeans
	 *            original list of event CRFs
	 * @return filtered list.
	 */
	List<EventCRFBean> getAllStartedEventCRFsWithStudyAndEventName(List<EventCRFBean> eventCRFBeans);
}
