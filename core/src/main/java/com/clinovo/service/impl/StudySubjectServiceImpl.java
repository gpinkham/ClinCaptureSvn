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

package com.clinovo.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DisplayStudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clinovo.service.EventCRFService;
import com.clinovo.service.StudySubjectService;
import com.clinovo.util.DAOWrapper;
import com.clinovo.util.SubjectEventStatusUtil;

/**
 * StudySubjectServiceImpl.
 */
@Service
@SuppressWarnings({"rawtypes", "unchecked"})
public class StudySubjectServiceImpl implements StudySubjectService {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private EventCRFService eventCRFService;

	private EventCRFDAO getEventCRFDAO() {
		return new EventCRFDAO(dataSource);
	}

	private StudyEventDAO getStudyEventDAO() {
		return new StudyEventDAO(dataSource);
	}

	private StudySubjectDAO getStudySubjectDAO() {
		return new StudySubjectDAO(dataSource);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeStudySubject(StudySubjectBean studySubjectBean, List<DisplayStudyEventBean> displayEvents,
			UserAccountBean userAccountBean) throws Exception {
		StudyEventDAO studyEventDao = getStudyEventDAO();

		studySubjectBean.setStatus(Status.DELETED);
		studySubjectBean.setUpdater(userAccountBean);
		studySubjectBean.setUpdatedDate(new Date());
		getStudySubjectDAO().update(studySubjectBean);

		for (DisplayStudyEventBean dispEvent : displayEvents) {
			StudyEventBean event = dispEvent.getStudyEvent();
			if (!event.getStatus().equals(Status.DELETED)) {
				event.setPrevSubjectEventStatus(event.getSubjectEventStatus());
				event.setSubjectEventStatus(SubjectEventStatus.REMOVED);
				event.setStatus(Status.AUTO_DELETED);
				event.setUpdater(userAccountBean);
				event.setUpdatedDate(new Date());
				studyEventDao.update(event);

				eventCRFService.removeEventCRFsByStudyEvent(event, userAccountBean);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void restoreStudySubject(StudySubjectBean studySubjectBean, List<DisplayStudyEventBean> displayEvents,
			UserAccountBean userAccountBean) throws Exception {
		EventCRFDAO eventCrfDao = getEventCRFDAO();
		StudyEventDAO studyEventDao = getStudyEventDAO();

		studySubjectBean.setStatus(Status.AVAILABLE);
		studySubjectBean.setUpdater(userAccountBean);
		studySubjectBean.setUpdatedDate(new Date());
		getStudySubjectDAO().update(studySubjectBean);

		for (DisplayStudyEventBean dispEvent : displayEvents) {
			StudyEventBean event = dispEvent.getStudyEvent();
			if (event.getStatus().equals(Status.AUTO_DELETED)) {
				SubjectEventStatus subjectEventStatus = event.getSubjectEventStatus();
				event.setSubjectEventStatus(event.getPrevSubjectEventStatus());
				event.setPrevSubjectEventStatus(subjectEventStatus);
				event.setStatus(Status.AVAILABLE);
				event.setUpdater(userAccountBean);
				event.setUpdatedDate(new Date());
				studyEventDao.update(event);

				ArrayList eventCRFs = eventCrfDao.findAllByStudyEvent(event);

				eventCRFService.restoreEventCRFsFromAutoRemovedState(eventCRFs, userAccountBean);

				SubjectEventStatusUtil.determineSubjectEventState(event, eventCRFs, new DAOWrapper(dataSource));
			}
		}
	}
}
