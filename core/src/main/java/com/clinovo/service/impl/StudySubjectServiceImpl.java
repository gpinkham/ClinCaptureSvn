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

import java.util.List;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clinovo.service.StudyEventService;
import com.clinovo.service.StudySubjectService;

/**
 * StudySubjectServiceImpl.
 */
@Service
@SuppressWarnings({"unchecked"})
public class StudySubjectServiceImpl implements StudySubjectService {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private StudyEventService studyEventService;

	private StudySubjectDAO getStudySubjectDAO() {
		return new StudySubjectDAO(dataSource);
	}

	private void disableChildObjects(StudySubjectBean studySubjectBean, UserAccountBean updater, Status status)
			throws Exception {
		if (status.isDeleted()) {
			studyEventService.removeStudyEvents(studySubjectBean, updater);
		} else {
			studyEventService.lockStudyEvents(studySubjectBean, updater);
		}
	}

	private void enableChildObjects(StudySubjectBean studySubjectBean, UserAccountBean updater, boolean restore)
			throws Exception {
		if (restore) {
			studyEventService.restoreStudyEvents(studySubjectBean, updater);
		} else {
			studyEventService.unlockStudyEvents(studySubjectBean, updater);
		}
	}

	private void disableStudySubject(StudySubjectBean studySubjectBean, UserAccountBean updater, int position,
			Status status) throws Exception {
		studySubjectBean.applyState(position, status, updater);
		getStudySubjectDAO().update(studySubjectBean);
		disableChildObjects(studySubjectBean, updater, studySubjectBean.getStatus());
	}

	private void enableStudySubject(StudySubjectBean studySubjectBean, UserAccountBean updater, int position,
			boolean restore) throws Exception {
		boolean available = studySubjectBean.revertState(position, updater);
		getStudySubjectDAO().update(studySubjectBean);
		if (!available) {
			disableChildObjects(studySubjectBean, updater, studySubjectBean.getStatus());
		} else {
			enableChildObjects(studySubjectBean, updater, restore);
		}
	}

	private void disableStudySubjects(StudyBean studyBean, UserAccountBean updater, Status status) throws Exception {
		List<StudySubjectBean> studySubjectBeanList = getStudySubjectDAO().findAllByStudy(studyBean);
		for (StudySubjectBean studySubjectBean : studySubjectBeanList) {
			disableStudySubject(studySubjectBean, updater,
					studyBean.isSite() ? StudySubjectBean.BY_SITE : StudySubjectBean.BY_STUDY, status);
		}
	}

	private void enableStudySubjects(StudyBean studyBean, UserAccountBean updater, boolean restore) throws Exception {
		List<StudySubjectBean> studySubjectBeanList = getStudySubjectDAO().findAllByStudy(studyBean);
		for (StudySubjectBean studySubjectBean : studySubjectBeanList) {
			enableStudySubject(studySubjectBean, updater,
					studyBean.isSite() ? StudySubjectBean.BY_SITE : StudySubjectBean.BY_STUDY, restore);
		}
	}

	private void disableStudySubjects(SubjectBean subjectBean, UserAccountBean updater, Status status)
			throws Exception {
		List<StudySubjectBean> studySubjectBeanList = getStudySubjectDAO().findAllBySubjectId(subjectBean.getId());
		for (StudySubjectBean studySubjectBean : studySubjectBeanList) {
			disableStudySubject(studySubjectBean, updater, StudySubjectBean.BY_SUBJECT, status);
		}
	}

	private void enableStudySubjects(SubjectBean subjectBean, UserAccountBean updater, boolean restore)
			throws Exception {
		List<StudySubjectBean> studySubjectBeanList = getStudySubjectDAO().findAllBySubjectId(subjectBean.getId());
		for (StudySubjectBean studySubjectBean : studySubjectBeanList) {
			enableStudySubject(studySubjectBean, updater, StudySubjectBean.BY_SUBJECT, restore);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeStudySubject(StudySubjectBean studySubjectBean, UserAccountBean updater) throws Exception {
		disableStudySubject(studySubjectBean, updater, StudySubjectBean.BY_ITSELF, Status.DELETED);
	}

	/**
	 * {@inheritDoc}
	 */
	public void restoreStudySubject(StudySubjectBean studySubjectBean, UserAccountBean updater) throws Exception {
		enableStudySubject(studySubjectBean, updater, StudySubjectBean.BY_ITSELF, true);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeStudySubjects(SubjectBean subjectBean, UserAccountBean updater) throws Exception {
		disableStudySubjects(subjectBean, updater, Status.DELETED);
	}

	/**
	 * {@inheritDoc}
	 */
	public void restoreStudySubjects(SubjectBean subjectBean, UserAccountBean updater) throws Exception {
		enableStudySubjects(subjectBean, updater, true);
	}

	/**
	 * {@inheritDoc}
	 */
	public void lockStudySubjects(SubjectBean subjectBean, UserAccountBean updater) throws Exception {
		disableStudySubjects(subjectBean, updater, Status.LOCKED);
	}

	/**
	 * {@inheritDoc}
	 */
	public void unlockStudySubjects(SubjectBean subjectBean, UserAccountBean updater) throws Exception {
		enableStudySubjects(subjectBean, updater, false);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeStudySubjects(StudyBean studyBean, UserAccountBean updater) throws Exception {
		disableStudySubjects(studyBean, updater, Status.DELETED);
	}

	/**
	 * {@inheritDoc}
	 */
	public void restoreStudySubjects(StudyBean studyBean, UserAccountBean updater) throws Exception {
		enableStudySubjects(studyBean, updater, true);
	}

	/**
	 * {@inheritDoc}
	 */
	public void lockStudySubject(StudySubjectBean studySubjectBean, UserAccountBean updater) throws Exception {
		disableStudySubject(studySubjectBean, updater, StudySubjectBean.BY_ITSELF, Status.LOCKED);
	}

	/**
	 * {@inheritDoc}
	 */
	public void unlockStudySubject(StudySubjectBean studySubjectBean, UserAccountBean updater) throws Exception {
		enableStudySubject(studySubjectBean, updater, StudySubjectBean.BY_ITSELF, false);
	}

	/**
	 * {@inheritDoc}
	 */
	public void lockStudySubjects(StudyBean studyBean, UserAccountBean updater) throws Exception {
		disableStudySubjects(studyBean, updater, Status.LOCKED);
	}

	/**
	 * {@inheritDoc}
	 */
	public void unlockStudySubjects(StudyBean studyBean, UserAccountBean updater) throws Exception {
		enableStudySubjects(studyBean, updater, false);
	}
}
