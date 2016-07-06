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

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clinovo.service.EventCRFService;
import com.clinovo.service.StudyEventService;
import com.clinovo.util.DAOWrapper;
import com.clinovo.util.SubjectEventStatusUtil;

/**
 * StudyEventServiceImpl.
 */
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class StudyEventServiceImpl implements StudyEventService {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private EventCRFService eventCRFService;

	private StudyEventDAO getStudyEventDAO() {
		return new StudyEventDAO(dataSource);
	}

	private void disableChildObjects(StudyEventBean studyEventBean, UserAccountBean updater) throws Exception {
		if (studyEventBean.getStatus().isDeleted()) {
			eventCRFService.removeEventCRFs(studyEventBean, updater);
		} else {
			eventCRFService.lockEventCRFs(studyEventBean, updater);
		}
	}

	private void enableChildObjects(StudyEventBean studyEventBean, UserAccountBean updater, boolean restore)
			throws Exception {
		if (restore) {
			eventCRFService.restoreEventCRFs(studyEventBean, updater);
		} else {
			eventCRFService.unlockEventCRFs(studyEventBean, updater);
		}
	}

	private void disableStudyEvent(StudyEventBean studyEventBean, UserAccountBean updater, int position, Status status,
			SubjectEventStatus subjectEventStatus) throws Exception {
		Status currentStatus = studyEventBean.getStatus();
		studyEventBean.applyState(position, status, updater);
		if (!studyEventBean.getSubjectEventStatus().isInvalid() && !studyEventBean.getSubjectEventStatus().isRemoved()
				&& !studyEventBean.getSubjectEventStatus().isLocked()) {
			studyEventBean.setPrevSubjectEventStatus(studyEventBean.getSubjectEventStatus());
		}
		if (!currentStatus.equals(studyEventBean.getStatus())) {
			studyEventBean.setSubjectEventStatus(subjectEventStatus);
		}
		getStudyEventDAO().update(studyEventBean);
		disableChildObjects(studyEventBean, updater);
	}

	private void enableStudyEvent(StudyEventBean studyEventBean, UserAccountBean updater, int position, boolean restore)
			throws Exception {
		boolean available = studyEventBean.revertState(position, updater);
		if (!available) {
			disableChildObjects(studyEventBean, updater);
		} else {
			enableChildObjects(studyEventBean, updater, restore);
			studyEventBean.setSubjectEventStatus(studyEventBean.getPrevSubjectEventStatus());
			if (studyEventBean.getSubjectEventStatus().isSkipped()
					|| studyEventBean.getSubjectEventStatus().isStopped()) {
				studyEventBean.setStatus(Status.UNAVAILABLE);
			}
			// in case of the corrupted SubjectEventStatus
			if (studyEventBean.getSubjectEventStatus().isInvalid() || studyEventBean.getSubjectEventStatus().isRemoved()
					|| studyEventBean.getSubjectEventStatus().isLocked()) {
				SubjectEventStatusUtil.determineSubjectEventState(studyEventBean, new DAOWrapper(dataSource));
			}
		}
		getStudyEventDAO().update(studyEventBean);
	}

	private void disableStudyEvents(StudySubjectBean studySubjectBean, UserAccountBean updater, Status status,
			SubjectEventStatus subjectEventStatus) throws Exception {
		List<StudyEventBean> studyEventBeanList = getStudyEventDAO().findAllByStudySubject(studySubjectBean);
		for (StudyEventBean studyEventBean : studyEventBeanList) {
			disableStudyEvent(studyEventBean, updater, StudyEventBean.BY_STUDY_SUBJECT, status, subjectEventStatus);
		}
	}

	private void enableStudyEvents(StudySubjectBean studySubjectBean, UserAccountBean updater, boolean restore)
			throws Exception {
		List<StudyEventBean> studyEventBeanList = getStudyEventDAO().findAllByStudySubject(studySubjectBean);
		for (StudyEventBean studyEventBean : studyEventBeanList) {
			enableStudyEvent(studyEventBean, updater, StudyEventBean.BY_STUDY_SUBJECT, restore);
		}
	}

	private void disableStudyEvents(StudyEventDefinitionBean studyEventDefinitionBean, UserAccountBean updater,
			Status status, SubjectEventStatus subjectEventStatus) throws Exception {
		List<StudyEventBean> studyEventBeanList = (List<StudyEventBean>) getStudyEventDAO()
				.findAllByDefinition(studyEventDefinitionBean.getId());
		for (StudyEventBean studyEventBean : studyEventBeanList) {
			disableStudyEvent(studyEventBean, updater, StudyEventBean.BY_STUDY_EVENT_DEFINITION, status,
					subjectEventStatus);
		}
	}

	private void enableStudyEvents(StudyEventDefinitionBean studyEventDefinitionBean, UserAccountBean updater,
			boolean restore) throws Exception {
		List<StudyEventBean> studyEventBeanList = (List<StudyEventBean>) getStudyEventDAO()
				.findAllByDefinition(studyEventDefinitionBean.getId());
		for (StudyEventBean studyEventBean : studyEventBeanList) {
			enableStudyEvent(studyEventBean, updater, StudyEventBean.BY_STUDY_EVENT_DEFINITION, restore);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeStudyEvent(StudyEventBean studyEventBean, UserAccountBean updater) throws Exception {
		disableStudyEvent(studyEventBean, updater, StudyEventBean.BY_ITSELF, Status.DELETED,
				SubjectEventStatus.REMOVED);
	}

	/**
	 * {@inheritDoc}
	 */
	public void restoreStudyEvent(StudyEventBean studyEventBean, UserAccountBean updater) throws Exception {
		enableStudyEvent(studyEventBean, updater, StudyEventBean.BY_ITSELF, true);
	}

	/**
	 * {@inheritDoc}
	 */
	public void lockStudyEvent(StudyEventBean studyEventBean, UserAccountBean updater) throws Exception {
		disableStudyEvent(studyEventBean, updater, StudyEventBean.BY_ITSELF, Status.LOCKED, SubjectEventStatus.LOCKED);
	}

	/**
	 * {@inheritDoc}
	 */
	public void unlockStudyEvent(StudyEventBean studyEventBean, UserAccountBean updater) throws Exception {
		enableStudyEvent(studyEventBean, updater, StudyEventBean.BY_ITSELF, true);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeStudyEvents(StudyEventDefinitionBean studyEventDefinitionBean, UserAccountBean updater)
			throws Exception {
		disableStudyEvents(studyEventDefinitionBean, updater, Status.DELETED, SubjectEventStatus.REMOVED);
	}

	/**
	 * {@inheritDoc}
	 */
	public void restoreStudyEvents(StudyEventDefinitionBean studyEventDefinitionBean, UserAccountBean updater)
			throws Exception {
		enableStudyEvents(studyEventDefinitionBean, updater, false);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeStudyEvents(StudySubjectBean studySubjectBean, UserAccountBean updater) throws Exception {
		disableStudyEvents(studySubjectBean, updater, Status.DELETED, SubjectEventStatus.REMOVED);
	}

	/**
	 * {@inheritDoc}
	 */
	public void restoreStudyEvents(StudySubjectBean studySubjectBean, UserAccountBean updater) throws Exception {
		enableStudyEvents(studySubjectBean, updater, true);
	}

	/**
	 * {@inheritDoc}
	 */
	public void lockStudyEvents(StudySubjectBean studySubjectBean, UserAccountBean updater) throws Exception {
		disableStudyEvents(studySubjectBean, updater, Status.LOCKED, SubjectEventStatus.LOCKED);
	}

	/**
	 * {@inheritDoc}
	 */
	public void unlockStudyEvents(StudySubjectBean studySubjectBean, UserAccountBean updater) throws Exception {
		enableStudyEvents(studySubjectBean, updater, false);
	}

	/**
	 * {@inheritDoc}
	 */
	public void deleteStudyEvent(StudyEventDefinitionBean studyEventDefinitionBean, StudySubjectBean studySubjectBean,
			StudyEventBean studyEventBean, UserAccountBean userAccountBean) {
		StudyEventDAO sedao = getStudyEventDAO();
		ItemDataDAO iddao = new ItemDataDAO(dataSource);
		EventCRFDAO ecdao = new EventCRFDAO(dataSource);
		DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(dataSource);

		for (Object eventCRFObject : ecdao.findAllByStudyEvent(studyEventBean)) {
			EventCRFBean eventCRF = (EventCRFBean) eventCRFObject;
			ArrayList itemData = iddao.findAllByEventCRFId(eventCRF.getId());
			for (Object anItemData : itemData) {
				ItemDataBean item = (ItemDataBean) anItemData;
				ArrayList discrepancyList = dndao.findExistingNotesForItemData(item.getId());
				iddao.deleteDnMap(item.getId());
				for (Object aDiscrepancyList : discrepancyList) {
					DiscrepancyNoteBean noteBean = (DiscrepancyNoteBean) aDiscrepancyList;
					dndao.deleteNotes(noteBean.getId());
				}
				item.setUpdater(userAccountBean);
				iddao.updateUser(item);
				iddao.delete(item.getId());
			}
			ecdao.deleteEventCRFDNMap(eventCRF.getId());
			ecdao.delete(eventCRF.getId());
		}

		List<Integer> dnIdList = dndao.findAllDnIdsByStudyEvent(studyEventBean.getId());
		sedao.deleteStudyEventDNMap(studyEventBean.getId());
		for (Integer dnId : dnIdList) {
			dndao.deleteNotes(dnId);
		}

		// update user id before deleting
		studyEventBean.setUpdater(userAccountBean);
		sedao.update(studyEventBean);
		// delete
		sedao.deleteByPK(studyEventBean.getId());

		updateOrdinalsForEventOccurrences(studyEventDefinitionBean, studySubjectBean);
	}

	private void updateOrdinalsForEventOccurrences(StudyEventDefinitionBean studyEventDefinitionBean,
			StudySubjectBean studySubjectBean) {
		int ordinal = 1;
		StudyEventDAO studyEventDao = getStudyEventDAO();
		List<StudyEventBean> studyEvents = (List<StudyEventBean>) studyEventDao
				.findAllByDefinitionAndSubjectOrderByOrdinal(studyEventDefinitionBean, studySubjectBean);
		for (StudyEventBean event : studyEvents) {
			event.setSampleOrdinal(ordinal++);
		}
		for (StudyEventBean event : studyEvents) {
			studyEventDao.update(event);
		}
	}
}
