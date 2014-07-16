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

import com.clinovo.service.EventCRFService;

import com.clinovo.service.ItemDataService;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("eventCRFService")
@SuppressWarnings("unchecked")
public class EventCRFServiceImpl implements EventCRFService {

	@Autowired private DataSource dataSource;

	@Autowired ItemDataService itemDataService;

	private EventCRFDAO eventCRFDAO;

	private DataSource getDataSource() {
		return dataSource;
	}

	private ItemDataService getItemDataService() {
		return itemDataService;
	}

	private EventCRFDAO getEventCRFDAO() {

		if (eventCRFDAO == null) {
			return eventCRFDAO = new EventCRFDAO(getDataSource());
		} else {
			return eventCRFDAO;
		}
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

			getItemDataService().removeItemDataByEventCRF(eventCRF, updater);
		}
	}

	public void removeEventCRF(EventCRFBean eventCRF, UserAccountBean updater) throws Exception {

		remove(eventCRF, Status.DELETED, updater);
	}

	public void removeEventCRFsByStudyEvent(StudyEventBean studyEvent, UserAccountBean updater) throws Exception {

		List<EventCRFBean> eventCRFs = (ArrayList<EventCRFBean>) getEventCRFDAO().findAllByStudyEvent(studyEvent);
		setEventCRFsToAutoRemovedState(eventCRFs, updater);

	}

	public void setEventCRFsToAutoRemovedState(List<EventCRFBean> eventCRFs, UserAccountBean updater) throws Exception {

		for (EventCRFBean eventCRF : eventCRFs) {
			remove(eventCRF, Status.AUTO_DELETED, updater);
		}
	}

	private void restore(EventCRFBean eventCRF, UserAccountBean updater) throws Exception {

		eventCRF.setStatus(eventCRF.getOldStatus());
		eventCRF.setUpdater(updater);
		eventCRF.setUpdatedDate(new Date());
		eventCRF.setSdvStatus(false);
		eventCRF.setElectronicSignatureStatus(false);
		getEventCRFDAO().update(eventCRF);

		getItemDataService().restoreItemDataByEventCRF(eventCRF, updater);
	}

	public void restoreEventCRF(EventCRFBean eventCRF, UserAccountBean updater) throws Exception {

		if (eventCRF.getStatus().isDeleted()) {
			restore(eventCRF, updater);
		}
	}

	public void restoreEventCRFsByStudyEvent(StudyEventBean studyEvent, UserAccountBean updater) throws Exception {

		List<EventCRFBean> eventCRFs = (ArrayList<EventCRFBean>) getEventCRFDAO().findAllByStudyEvent(studyEvent);
		restoreEventCRFsFromAutoRemovedState(eventCRFs, updater);
	}

	public void restoreEventCRFsFromAutoRemovedState(List<EventCRFBean> eventCRFs, UserAccountBean updater)
			throws Exception {

		for (EventCRFBean eventCRF : eventCRFs) {
			if (eventCRF.getStatus().equals(Status.AUTO_DELETED)) {
				restore(eventCRF, updater);
			}
		}
	}
}
