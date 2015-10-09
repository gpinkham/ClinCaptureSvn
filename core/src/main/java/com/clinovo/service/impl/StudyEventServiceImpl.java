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

import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
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
@SuppressWarnings("unchecked")
public class StudyEventServiceImpl implements StudyEventService {

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

	/**
	 * {@inheritDoc}
	 */
	public void removeStudyEvent(StudyEventBean studyEventBean, UserAccountBean userAccountBean) throws Exception {
		studyEventBean.setPrevSubjectEventStatus(studyEventBean.getSubjectEventStatus());
		studyEventBean.setSubjectEventStatus(SubjectEventStatus.REMOVED);
		studyEventBean.setStatus(Status.DELETED);
		studyEventBean.setUpdater(userAccountBean);
		studyEventBean.setUpdatedDate(new Date());
		getStudyEventDAO().update(studyEventBean);
		eventCRFService.removeEventCRFsByStudyEvent(studyEventBean, userAccountBean);

	}

	/**
	 * {@inheritDoc}
	 */
	public void restoreStudyEvent(StudyEventBean studyEventBean, UserAccountBean userAccountBean) throws Exception {
		List<EventCRFBean> eventCRFs = (List<EventCRFBean>) getEventCRFDAO().findAllByStudyEvent(studyEventBean);
		eventCRFService.restoreEventCRFsFromAutoRemovedState(eventCRFs, userAccountBean);
		SubjectEventStatus subjectEventStatus = studyEventBean.getSubjectEventStatus();
		studyEventBean.setSubjectEventStatus(studyEventBean.getPrevSubjectEventStatus());
		studyEventBean.setPrevSubjectEventStatus(subjectEventStatus);
		SubjectEventStatusUtil.determineSubjectEventState(studyEventBean, new DAOWrapper(dataSource));
		studyEventBean.setStatus(Status.AVAILABLE);
		studyEventBean.setUpdater(userAccountBean);
		studyEventBean.setUpdatedDate(new Date());
		getStudyEventDAO().update(studyEventBean);
	}
}
