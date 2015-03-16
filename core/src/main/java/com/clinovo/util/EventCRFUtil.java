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

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;

/**
 * Event CRF bean's utility class.
 */
public final class EventCRFUtil {

	private EventCRFUtil() {
	}

	/**
	 * Calculates and returns current status of a particular Event CRF bean, to be displayed to a user.
	 * Note that method parameters <code>studyEvent</code> and <code>eventCrf</code> can be both <code>null</code>.
	 * <p/>
	 * If they are both <code>null</code> (Study Event is not scheduled), then status to display will be calculated,
	 * based on Study Subject's status and proper Event Definition CRF record properties.
	 * <p/>
	 * If <code>eventCrf</code> parameter is <code>null</code> only (Study Event is scheduled, but Event CRF is not started),
	 * then status to display will be calculated, based on Study Event bean's status and proper Event Definition CRF
	 * record properties.
	 * <p/>
	 * If They are both provided (Study Event is scheduled and Event CRF record exists in the data base),
	 * then status to display will be calculated, based on Study Event bean's status, Event CRF bean's properties
	 * and proper Event Definition CRF record properties.
	 * <p/>
	 * Note that case, where <code>eventCrf</code> parameter is provided and <code>studyEvent</code> parameter
	 * is <code>null</code>, is senseless (Event CRF cant exist inside of a Study Event, that is not scheduled).
	 *
	 * @param studySubjectBean       StudySubjectBean
	 * @param studyEvent             StudyEventBean
	 * @param eventDefinitionCRFBean EventDefinitionCRFBean
	 * @param eventCrf               EventCRFBean
	 * @param crfVersionDAO          CRFVersionDAO
	 * @param eventDefinitionCRFDAO  EventDefinitionCRFDAO
	 * @return Status current Event CRF bean's status to display to a user
	 */
	public static Status getEventCRFCurrentStatus(StudySubjectBean studySubjectBean, StudyEventBean studyEvent,
			EventDefinitionCRFBean eventDefinitionCRFBean, EventCRFBean eventCrf, CRFVersionDAO crfVersionDAO,
			EventDefinitionCRFDAO eventDefinitionCRFDAO) {

		SubjectEventStatus subjectEventStatus;
		Status eventCRFStatus;
		boolean doesEventDefCRFHaveAvailableCRFVersionsForDataEntry =
				eventDefinitionCRFDAO.doesEventDefinitionCRFHaveAvailableCRFVersionsForDataEntry(eventDefinitionCRFBean);
		if (studyEvent == null) {
			// if study event not scheduled yet
			if (studySubjectBean.getStatus().isDeleted()) {
				eventCRFStatus = Status.DELETED;
			} else if (studySubjectBean.getStatus().isLocked() || !doesEventDefCRFHaveAvailableCRFVersionsForDataEntry) {
				eventCRFStatus = Status.LOCKED;
			} else {
				eventCRFStatus = Status.NOT_STARTED;
			}
		} else if (eventCrf == null) {
			// if study event already scheduled, but event CRF not started yet
			subjectEventStatus = studyEvent.getSubjectEventStatus();
			if (subjectEventStatus.isLocked() || subjectEventStatus.isStopped() || subjectEventStatus.isSkipped()
					|| !doesEventDefCRFHaveAvailableCRFVersionsForDataEntry) {
				eventCRFStatus = Status.LOCKED;
			} else if (subjectEventStatus.isRemoved()) {
				eventCRFStatus = Status.DELETED;
			} else {
				eventCRFStatus = Status.NOT_STARTED;
			}
		} else {
			// if study event already scheduled and event CRF already started
			subjectEventStatus = studyEvent.getSubjectEventStatus();
			if (subjectEventStatus.isLocked() || subjectEventStatus.isStopped() || subjectEventStatus.isSkipped()) {
				eventCRFStatus = Status.LOCKED;
			} else if (subjectEventStatus.isRemoved()) {
				eventCRFStatus = Status.DELETED;
			} else if (eventCrf.isNotStarted()) {
				eventCRFStatus = doesEventDefCRFHaveAvailableCRFVersionsForDataEntry ? Status.NOT_STARTED : Status.LOCKED;
			} else if (!((CRFVersionBean) crfVersionDAO.findByPK(eventCrf.getCRFVersionId())).getStatus().isAvailable()) {
				eventCRFStatus = Status.LOCKED;
			} else if (eventCrf.getStatus().isDeleted()) {
				eventCRFStatus = Status.DELETED;
			} else if (eventCrf.getStage().isInitialDE()) {
				eventCRFStatus = Status.DATA_ENTRY_STARTED;
			} else if (eventCrf.getStage().isInitialDE_Complete()) {
				eventCRFStatus = Status.INITIAL_DATA_ENTRY_COMPLETED;
			} else if (eventCrf.getStage().isDoubleDE()) {
				eventCRFStatus = Status.DOUBLE_DATA_ENTRY;
			} else if (eventCrf.getStage().isDoubleDE_Complete()) {
				eventCRFStatus = subjectEventStatus.isSigned() ? Status.SIGNED
						: eventCrf.isSdvStatus() ? Status.SOURCE_DATA_VERIFIED : Status.COMPLETED;
			} else {
				eventCRFStatus = Status.INVALID;
			}
		}
		return eventCRFStatus;
	}
}
