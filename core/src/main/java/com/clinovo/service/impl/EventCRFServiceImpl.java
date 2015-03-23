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

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVOâ€™S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/

package com.clinovo.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.DisplayEventCRFBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clinovo.service.EventCRFService;
import com.clinovo.service.ItemDataService;

/**
 * EventCRFServiceImpl class provides service implementation for EventCRFService interface.
 */
@Service("eventCRFService")
@SuppressWarnings("unchecked")
public class EventCRFServiceImpl implements EventCRFService {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private ItemDataService itemDataService;

	public EventCRFDAO getEventCRFDAO() {
		return new EventCRFDAO(dataSource);
	}

	private StudyEventDAO getStudyEventDAO() {
		return new StudyEventDAO(dataSource);
	}

	private StudyEventDefinitionDAO getStudyEventDefinitionDAO() {
		return new StudyEventDefinitionDAO(dataSource);
	}

	private EventDefinitionCRFDAO getEventDefinitionCRFDAO() {
		return new EventDefinitionCRFDAO(dataSource);
	}

	private void remove(EventCRFBean eventCRF, Status crfStatusToSet, UserAccountBean updater) throws Exception {

		if (!eventCRF.getStatus().isDeleted()) {

			if (!eventCRF.getStatus().isLocked()) {
				eventCRF.setOldStatus(eventCRF.getStatus());
			}
			eventCRF.setStatus(crfStatusToSet);
			eventCRF.setUpdater(updater);
			eventCRF.setUpdatedDate(new Date());
			getEventCRFDAO().update(eventCRF);

			itemDataService.removeItemDataByEventCRF(eventCRF, updater);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeEventCRF(EventCRFBean eventCRF, UserAccountBean updater) throws Exception {

		remove(eventCRF, Status.DELETED, updater);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeEventCRFsByStudyEvent(StudyEventBean studyEvent, UserAccountBean updater) throws Exception {

		List<EventCRFBean> eventCRFs = (ArrayList<EventCRFBean>) getEventCRFDAO().findAllByStudyEvent(studyEvent);
		setEventCRFsToAutoRemovedState(eventCRFs, updater);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setEventCRFsToAutoRemovedState(List<EventCRFBean> eventCRFs, UserAccountBean updater) throws Exception {

		for (EventCRFBean eventCRF : eventCRFs) {
			remove(eventCRF, Status.AUTO_DELETED, updater);
		}
	}

	public void removeEventCRFsByEventDefinitionCRF(String studyEventDefinitionOID, String crfOID,
			UserAccountBean updater) throws Exception {

		List<StudyEventBean> studyEventsToUpdate = getStudyEventDAO().findAllByStudyEventDefinitionAndCrfOids(
				studyEventDefinitionOID, crfOID);

		for (StudyEventBean studyEvent : studyEventsToUpdate) {
			List<EventCRFBean> eventCRFsToRemove = getEventCRFDAO().findAllByStudyEventAndCrfOrCrfVersionOid(
					studyEvent, crfOID);
			setEventCRFsToAutoRemovedState(eventCRFsToRemove, updater);
		}
	}

	private void restore(EventCRFBean eventCRF, UserAccountBean updater) throws Exception {

		eventCRF.setStatus(eventCRF.getOldStatus());
		eventCRF.setUpdater(updater);
		eventCRF.setUpdatedDate(new Date());
		eventCRF.setSdvStatus(false);
		eventCRF.setElectronicSignatureStatus(false);
		getEventCRFDAO().update(eventCRF);

		itemDataService.restoreItemDataByEventCRF(eventCRF, updater);
	}

	/**
	 * {@inheritDoc}
	 */
	public void restoreEventCRF(EventCRFBean eventCRF, UserAccountBean updater) throws Exception {

		if (eventCRF.getStatus().isDeleted()) {
			restore(eventCRF, updater);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void restoreEventCRFsByStudyEvent(StudyEventBean studyEvent, UserAccountBean updater) throws Exception {

		List<EventCRFBean> eventCRFs = (ArrayList<EventCRFBean>) getEventCRFDAO().findAllByStudyEvent(studyEvent);
		restoreEventCRFsFromAutoRemovedState(eventCRFs, updater);
	}

	/**
	 * {@inheritDoc}
	 */
	public void restoreEventCRFsFromAutoRemovedState(List<EventCRFBean> eventCRFs, UserAccountBean updater)
			throws Exception {

		for (EventCRFBean eventCRF : eventCRFs) {
			if (eventCRF.getStatus().equals(Status.AUTO_DELETED)) {
				restore(eventCRF, updater);
			}
		}
	}

	public void restoreEventCRFsByEventDefinitionCRF(String studyEventDefinitionOID, String crfOID,
			UserAccountBean updater) throws Exception {

		List<StudyEventBean> studyEventsToUpdate = getStudyEventDAO().findAllByStudyEventDefinitionAndCrfOids(
				studyEventDefinitionOID, crfOID);

		for (StudyEventBean studyEvent : studyEventsToUpdate) {
			List<EventCRFBean> eventCRFsToRestore = getEventCRFDAO().findAllByStudyEventAndCrfOrCrfVersionOid(
					studyEvent, crfOID);
			restoreEventCRFsFromAutoRemovedState(eventCRFsToRestore, updater);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public EventCRFBean getNextEventCRFForDataEntry(StudyEventBean currentStudyEventBean,
			EventDefinitionCRFBean currentEventDefCRF, UserAccountBean currentUser, StudyUserRoleBean currentUserRole,
			StudyBean currentStudy) {
		StudyEventDefinitionBean currentStudyEventDefinition = (StudyEventDefinitionBean) getStudyEventDefinitionDAO()
				.findByEventDefinitionCRFId(currentEventDefCRF.getId());
		Collection<EventDefinitionCRFBean> eventDefinitionCRFs = getEventDefinitionCRFDAO()
				.findAllActiveByEventDefinitionId(currentStudy, currentStudyEventDefinition.getId());
		EventCRFBean eventCRF = getNextCrfAvailableForDataEntry(currentStudy, currentStudyEventBean,
				currentEventDefCRF, currentUser, currentUserRole, eventDefinitionCRFs);
		if (eventCRF != null) {
			eventCRF.setStudyEventBean(currentStudyEventBean);
			return eventCRF;
		}
		List<StudyEventBean> studyEvents = getStudyEventDAO().findAllByDefinitionAndSubjectOrderByOrdinal(
				currentStudyEventDefinition.getId(), currentStudyEventBean.getStudySubjectId());
		if (studyEvents != null && studyEvents.size() > 1) {
			eventCRF = getNextEventOccurenceCrfAvailableForDataEntry(currentUser, currentUserRole, currentStudy,
					studyEvents, currentStudyEventBean, currentStudyEventDefinition);
			if (eventCRF != null) {
				return eventCRF;
			}
		}
		return getNextAvailableCrfFromNextStudyEventDefinitions(currentStudyEventBean, currentUser, currentUserRole,
				currentStudy, currentStudyEventDefinition, studyEvents);
	}

	private EventCRFBean getNextAvailableCrfFromNextStudyEventDefinitions(StudyEventBean currentStudyEventBean,
			UserAccountBean currentUser, StudyUserRoleBean currentUserRole, StudyBean currentStudy,
			StudyEventDefinitionBean currentStudyEventDefinition, List<StudyEventBean> studyEvents) {
		EventCRFBean eventCRF;
		int parentStudyId = currentStudy.isSite() ? currentStudy.getParentStudyId() : currentStudy.getId();
		List<StudyEventDefinitionBean> studyEventDefinitions = getStudyEventDefinitionDAO()
				.findAllActiveByParentStudyId(parentStudyId);
		for (StudyEventDefinitionBean studyEventDefinition : studyEventDefinitions) {
			if (studyEventDefinition.getOrdinal() <= currentStudyEventDefinition.getOrdinal()) {
				continue;
			}
			studyEvents = getStudyEventDAO().findAllByDefinitionAndSubjectOrderByOrdinal(studyEventDefinition.getId(),
					currentStudyEventBean.getStudySubjectId());
			if (studyEvents != null && studyEvents.size() > 0) {
				eventCRF = getNextEventOccurenceCrfAvailableForDataEntry(currentUser, currentUserRole, currentStudy,
						studyEvents, null, studyEventDefinition);
				if (eventCRF != null) {
					return eventCRF;
				}
			}
		}
		return null;
	}

	private EventCRFBean getNextEventOccurenceCrfAvailableForDataEntry(UserAccountBean currentUser,
			StudyUserRoleBean currentUserRole, StudyBean currentStudy, List<StudyEventBean> studyEvents,
			StudyEventBean currentStudyEvent, StudyEventDefinitionBean studyEventDefinition) {
		Collection<EventDefinitionCRFBean> eventDefinitionCRFs;
		EventCRFBean eventCRF;
		eventDefinitionCRFs = getEventDefinitionCRFDAO().findAllActiveByEventDefinitionId(currentStudy,
				studyEventDefinition.getId());
		for (StudyEventBean studyEvent : studyEvents) {
			if (currentStudyEvent != null && studyEvent.getSampleOrdinal() <= currentStudyEvent.getSampleOrdinal()) {
				continue;
			}
			eventCRF = getNextCrfAvailableForDataEntry(currentStudy, studyEvent, null, currentUser, currentUserRole,
					eventDefinitionCRFs);
			if (eventCRF != null) {
				eventCRF.setStudyEventBean(studyEvent);
				return eventCRF;
			}
		}
		return null;
	}

	private EventCRFBean getNextCrfAvailableForDataEntry(StudyBean currentStudy, StudyEventBean currentStudyEvent,
			EventDefinitionCRFBean currentEventDefinitionCRF, UserAccountBean currentUser,
			StudyUserRoleBean currentUserRole, Collection<EventDefinitionCRFBean> eventDefinitionCRFs) {
		for (EventDefinitionCRFBean edcBean : eventDefinitionCRFs) {
			if (currentEventDefinitionCRF != null && edcBean.getOrdinal() <= currentEventDefinitionCRF.getOrdinal()) {
				continue;
			}
			if (currentStudy.isSite() && edcBean.isHideCrf()) {
				continue;
			}
			EventCRFBean eventCrf = getExistingOrNewEventCrfForEventDefinition(edcBean, currentStudyEvent, currentUser,
					currentUserRole);
			if (eventCrf != null) {
				return eventCrf;
			}
		}
		return null;
	}

	private EventCRFBean getExistingOrNewEventCrfForEventDefinition(EventDefinitionCRFBean edcBean,
			StudyEventBean currentStudyEvent, UserAccountBean currentUser, StudyUserRoleBean currentUserRole) {
		List<EventCRFBean> eventCRFs = getEventCRFDAO().findAllActiveByStudyEventIdAndCrfId(currentStudyEvent.getId(),
				edcBean.getCrfId());
		if (eventCRFs != null && eventCRFs.size() > 0) {
			EventCRFBean eventCRF = getEventCrfAvailableToUserForDataEntry(currentUser, currentUserRole, edcBean,
					eventCRFs);
			if (eventCRF != null) {
				return eventCRF;
			}
		} else {
			EventCRFBean newEventCRF = new EventCRFBean();
			newEventCRF.setId(0);
			newEventCRF.setEventDefinitionCrf(edcBean);
			return newEventCRF;
		}
		return null;
	}

	private EventCRFBean getEventCrfAvailableToUserForDataEntry(UserAccountBean currentUser,
			StudyUserRoleBean currentUserRole, EventDefinitionCRFBean edcBean, List<EventCRFBean> eventCRFs) {
		DisplayEventCRFBean displayEventCrfBean;
		for (EventCRFBean eventCRF : eventCRFs) {
			eventCRF.setEventDefinitionCrf(edcBean);
			displayEventCrfBean = new DisplayEventCRFBean();
			displayEventCrfBean.setFlags(eventCRF, currentUser, currentUserRole, edcBean);
			if (displayEventCrfBean.isStartInitialDataEntryPermitted()
					|| displayEventCrfBean.isContinueInitialDataEntryPermitted()
					|| displayEventCrfBean.isStartDoubleDataEntryPermitted()
					|| displayEventCrfBean.isContinueDoubleDataEntryPermitted()) {
				return eventCRF;
			}
		}
		return null;
	}
}
