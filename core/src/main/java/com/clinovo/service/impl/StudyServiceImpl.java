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
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.service.StudyConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clinovo.service.DatasetService;
import com.clinovo.service.EventDefinitionCrfService;
import com.clinovo.service.EventDefinitionService;
import com.clinovo.service.StudyService;
import com.clinovo.service.StudySubjectService;
import com.clinovo.service.UserAccountService;

/**
 * StudyServiceImpl.
 */
@Service
@SuppressWarnings("unchecked")
public class StudyServiceImpl implements StudyService {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private DatasetService datasetService;

	@Autowired
	private UserAccountService userAccountService;

	@Autowired
	private StudySubjectService studySubjectService;

	@Autowired
	private EventDefinitionService eventDefinitionService;

	@Autowired
	private EventDefinitionCrfService eventDefinitionCrfService;

	@Autowired
	private StudyConfigService studyConfigService;

	private StudyDAO getStudyDAO() {
		return new StudyDAO(dataSource);
	}

	private UserAccountDAO getUserAccountDAO() {
		return new UserAccountDAO(dataSource);
	}

	private void autoRemoveStudyUserRole(StudyBean studyBean, UserAccountBean updater) throws Exception {
		List<StudyUserRoleBean> studyUserRoleBeanList = getUserAccountDAO().findAllByStudyIdOnly(studyBean.getId());
		for (StudyUserRoleBean studyUserRoleBean : studyUserRoleBeanList) {
			userAccountService.autoRemoveStudyUserRole(studyUserRoleBean, updater);
		}
	}

	private void autoRestoreStudyUserRole(StudyBean studyBean, UserAccountBean updater) throws Exception {
		List<StudyUserRoleBean> studyUserRoleBeanList = getUserAccountDAO().findAllByStudyIdOnly(studyBean.getId());
		for (StudyUserRoleBean studyUserRoleBean : studyUserRoleBeanList) {
			userAccountService.autoRestoreStudyUserRole(studyUserRoleBean, updater);
		}
	}

	private void disableStudyObjects(StudyBean studyBean, UserAccountBean updater, Status status) throws Exception {
		if (status.isDeleted()) {
			autoRemoveStudyUserRole(studyBean, updater);
			studySubjectService.removeStudySubjects(studyBean, updater);
			datasetService.removeDatasets(studyBean, updater);
			if (!studyBean.isSite()) {
				eventDefinitionService.removeStudyEventDefinitions(studyBean, updater);
			} else {
				eventDefinitionCrfService.removeChildEventDefinitionCRFs(studyBean, updater);
			}
		} else {
			studySubjectService.lockStudySubjects(studyBean, updater);
			datasetService.lockDatasets(studyBean, updater);
			eventDefinitionCrfService.lockChildEventDefinitionCRFs(studyBean, updater);
		}
	}

	private void enableStudyObjects(StudyBean studyBean, UserAccountBean updater, boolean restore) throws Exception {
		if (restore) {
			autoRestoreStudyUserRole(studyBean, updater);
			studySubjectService.restoreStudySubjects(studyBean, updater);
			datasetService.restoreDatasets(studyBean, updater);
			if (!studyBean.isSite()) {
				eventDefinitionService.restoreStudyEventDefinitions(studyBean, updater);
			} else {
				eventDefinitionCrfService.restoreChildEventDefinitionCRFs(studyBean, updater);
			}
		} else {
			studySubjectService.unlockStudySubjects(studyBean, updater);
			datasetService.unlockDatasets(studyBean, updater);
			eventDefinitionCrfService.unlockChildEventDefinitionCRFs(studyBean, updater);
		}
	}

	private void disableStudy(StudyBean studyBean, UserAccountBean updater, Status status) throws Exception {
		studyBean.setOldStatus(studyBean.getStatus());
		studyBean.setStatus(status);
		studyBean.setUpdater(updater);
		studyBean.setUpdatedDate(new Date());
		getStudyDAO().update(studyBean);
	}

	private void enableStudy(StudyBean studyBean, UserAccountBean updater) throws Exception {
		studyBean.setStatus(studyBean.getOldStatus());
		studyBean.setOldStatus(Status.AVAILABLE);
		studyBean.setUpdater(updater);
		studyBean.setUpdatedDate(new Date());
		getStudyDAO().update(studyBean);
	}

	private void disableStudyAndItsSites(StudyBean studyBean, UserAccountBean updater, Status status) throws Exception {
		disableStudy(studyBean, updater, status);
		disableStudyObjects(studyBean, updater, status);
		List<StudyBean> siteList = (List<StudyBean>) getStudyDAO().findAllByParent(studyBean.getId());
		for (StudyBean site : siteList) {
			disableStudy(site, updater, status);
		}
	}

	private void enableStudyAndItsSites(StudyBean studyBean, UserAccountBean updater, boolean restore)
			throws Exception {
		enableStudy(studyBean, updater);
		List<StudyBean> siteList = (List<StudyBean>) getStudyDAO().findAllByParent(studyBean.getId());
		for (StudyBean site : siteList) {
			enableStudy(site, updater);
		}
		enableStudyObjects(studyBean, updater, restore);
	}

	private void disableSite(StudyBean studyBean, UserAccountBean updater, Status status) throws Exception {
		disableStudy(studyBean, updater, status);
		disableStudyObjects(studyBean, updater, status);
	}

	private void enableSite(StudyBean studyBean, UserAccountBean updater, boolean restore) throws Exception {
		enableStudy(studyBean, updater);
		enableStudyObjects(studyBean, updater, restore);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeStudy(StudyBean studyBean, UserAccountBean updater) throws Exception {
		disableStudyAndItsSites(studyBean, updater, Status.DELETED);
	}

	/**
	 * {@inheritDoc}
	 */
	public void restoreStudy(StudyBean studyBean, UserAccountBean updater) throws Exception {
		enableStudyAndItsSites(studyBean, updater, true);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeSite(StudyBean studyBean, UserAccountBean updater) throws Exception {
		disableSite(studyBean, updater, Status.DELETED);
	}

	/**
	 * {@inheritDoc}
	 */
	public void restoreSite(StudyBean studyBean, UserAccountBean updater) throws Exception {
		enableSite(studyBean, updater, true);
	}

	/**
	 * {@inheritDoc}
	 */
	public void lockSite(StudyBean studyBean, UserAccountBean updater) throws Exception {
		disableSite(studyBean, updater, Status.LOCKED);
	}

	/**
	 * {@inheritDoc}
	 */
	public void unlockSite(StudyBean studyBean, UserAccountBean updater) throws Exception {
		enableSite(studyBean, updater, false);
	}

	/**
	 * {@inheritDoc}
	 */
	public StudyBean getSubjectStudy(StudyBean currentStudy, StudySubjectBean studySubject) {

		StudyBean subjectStudy;
		if (currentStudy.getId() == studySubject.getStudyId()) {
			subjectStudy = currentStudy;
		} else {
			subjectStudy = (StudyBean) getStudyDAO().findByPK(studySubject.getStudyId());
			studyConfigService.setParametersForStudy(subjectStudy);
		}
		return subjectStudy;
	}
}
