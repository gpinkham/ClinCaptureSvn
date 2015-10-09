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

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.SubjectGroupMapBean;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.SubjectGroupMapDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clinovo.service.EventCRFService;
import com.clinovo.service.StudyService;
import com.clinovo.service.UserAccountService;
import com.clinovo.util.DAOWrapper;
import com.clinovo.util.SubjectEventStatusUtil;

/**
 * StudyServiceImpl.
 */
@Service
@SuppressWarnings("unchecked")
public class StudyServiceImpl implements StudyService {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private EventCRFService eventCRFService;

	@Autowired
	private UserAccountService userAccountService;

	private EventCRFDAO getEventCRFDAO() {
		return new EventCRFDAO(dataSource);
	}

	private StudyDAO getStudyDAO() {
		return new StudyDAO(dataSource);
	}

	private StudyEventDAO getStudyEventDAO() {
		return new StudyEventDAO(dataSource);
	}

	private StudySubjectDAO getStudySubjectDAO() {
		return new StudySubjectDAO(dataSource);
	}

	private UserAccountDAO getUserAccountDAO() {
		return new UserAccountDAO(dataSource);
	}

	private StudyGroupDAO getStudyGroupDAO() {
		return new StudyGroupDAO(dataSource);
	}

	private StudyGroupClassDAO getStudyGroupClassDAO() {
		return new StudyGroupClassDAO(dataSource);
	}

	private SubjectGroupMapDAO getSubjectGroupMapDAO() {
		return new SubjectGroupMapDAO(dataSource);
	}

	private DatasetDAO getDatasetDAO() {
		return new DatasetDAO(dataSource);
	}

	private EventDefinitionCRFDAO getEventDefinitionCRFDAO() {
		return new EventDefinitionCRFDAO(dataSource);
	}

	private StudyEventDefinitionDAO getStudyEventDefinitionDAO() {
		return new StudyEventDefinitionDAO(dataSource);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeStudy(StudyBean study, StudyBean currentStudy, StudyUserRoleBean currentRole,
			UserAccountBean updater) throws Exception {
		StudyDAO studyDao = getStudyDAO();
		DatasetDAO datasetDao = getDatasetDAO();
		StudyEventDAO studyEventDao = getStudyEventDAO();
		UserAccountDAO userAccountDao = getUserAccountDAO();
		StudySubjectDAO studySubjectDao = getStudySubjectDAO();
		StudyGroupClassDAO studyGroupClassDao = getStudyGroupClassDAO();
		SubjectGroupMapDAO subjectGroupMapDao = getSubjectGroupMapDAO();
		EventDefinitionCRFDAO eventDefinitionCrfDao = getEventDefinitionCRFDAO();
		StudyEventDefinitionDAO studyEventDefinitionDao = getStudyEventDefinitionDAO();

		if (study.getStatus() != Status.DELETED) {
			study.setOldStatus(study.getStatus());
		} else {
			study.setOldStatus(Status.AVAILABLE);
		}
		study.setStatus(Status.DELETED);
		study.setUpdater(updater);
		study.setUpdatedDate(new Date());
		studyDao.update(study);

		// remove all sites
		List<StudyBean> sites = (List<StudyBean>) studyDao.findAllByParent(study.getId());
		for (StudyBean site : sites) {
			if (!site.getStatus().equals(Status.DELETED)) {
				site.setOldStatus(site.getStatus());
				site.setStatus(Status.AUTO_DELETED);
				site.setUpdater(updater);
				site.setUpdatedDate(new Date());
				studyDao.update(site);
			}
		}

		// remove all users and roles
		List<StudyUserRoleBean> userRoles = userAccountDao.findAllByStudyId(study.getId());
		for (StudyUserRoleBean role : userRoles) {
			userAccountService.autoRemoveStudyUserRole(role, updater);
		}

		// YW << bug fix for that current active study has been deleted
		if (study.getId() == currentStudy.getId()) {
			currentStudy.setStatus(Status.DELETED);
			currentRole.setStatus(Status.DELETED);
		} else if (currentStudy.getParentStudyId() == study.getId()) {
			// if current active study is a site and the deleted study is
			// this active site's parent study,
			// then this active site has to be removed as well
			// (auto-removed)

			currentStudy.setStatus(Status.AUTO_DELETED);
			// we may need handle this later?
			currentRole.setStatus(Status.DELETED);
		}

		// remove all subjects
		List<StudySubjectBean> subjects = studySubjectDao.findAllByStudy(study);
		for (StudySubjectBean subject : subjects) {
			if (!subject.getStatus().equals(Status.DELETED)) {
				subject.setStatus(Status.AUTO_DELETED);
				subject.setUpdater(updater);
				subject.setUpdatedDate(new Date());
				studySubjectDao.update(subject);
			}
		}

		// remove all study_group_class
		// changed by jxu on 08-31-06, to fix the problem of no study_id
		// in study_group table
		List<StudyGroupClassBean> groups = studyGroupClassDao.findAllByStudy(study);
		for (StudyGroupClassBean group : groups) {
			if (!group.getStatus().equals(Status.DELETED)) {
				group.setStatus(Status.AUTO_DELETED);
				group.setUpdater(updater);
				group.setUpdatedDate(new Date());
				studyGroupClassDao.update(group);
				// all subject_group_map
				List<SubjectGroupMapBean> subjectGroupMaps = subjectGroupMapDao.findAllByStudyGroupClassId(group
						.getId());
				for (SubjectGroupMapBean sgMap : subjectGroupMaps) {
					if (!sgMap.getStatus().equals(Status.DELETED)) {
						sgMap.setStatus(Status.AUTO_DELETED);
						sgMap.setUpdater(updater);
						sgMap.setUpdatedDate(new Date());
						subjectGroupMapDao.update(sgMap);
					}
				}
			}
		}

		List<StudyGroupClassBean> groupClasses = studyGroupClassDao.findAllActiveByStudy(study);
		for (StudyGroupClassBean gc : groupClasses) {
			if (!gc.getStatus().equals(Status.DELETED)) {
				gc.setStatus(Status.AUTO_DELETED);
				gc.setUpdater(updater);
				gc.setUpdatedDate(new Date());
				studyGroupClassDao.update(gc);
			}
		}

		// remove all event definitions and event
		List<StudyEventDefinitionBean> definitions = studyEventDefinitionDao.findAllByStudy(study);
		for (StudyEventDefinitionBean definition : definitions) {
			if (!definition.getStatus().equals(Status.DELETED)) {
				definition.setStatus(Status.AUTO_DELETED);
				definition.setUpdater(updater);
				definition.setUpdatedDate(new Date());
				studyEventDefinitionDao.update(definition);
				List<EventDefinitionCRFBean> edcs = (List<EventDefinitionCRFBean>) eventDefinitionCrfDao
						.findAllByDefinition(definition.getId());

				for (EventDefinitionCRFBean edc : edcs) {
					if (!edc.getStatus().equals(Status.DELETED)) {
						edc.setStatus(Status.AUTO_DELETED);
						edc.setUpdater(updater);
						edc.setUpdatedDate(new Date());
						eventDefinitionCrfDao.update(edc);
					}
				}

				List<StudyEventBean> events = (List<StudyEventBean>) studyEventDao.findAllByDefinition(definition
						.getId());
				for (StudyEventBean event : events) {
					if (!event.getStatus().equals(Status.DELETED)) {
						event.setStatus(Status.AUTO_DELETED);
						event.setUpdater(updater);
						event.setUpdatedDate(new Date());
						studyEventDao.update(event);

						eventCRFService.removeEventCRFsByStudyEvent(event, updater);
					}
				}
			}
		}

		List<DatasetBean> datasets = datasetDao.findAllByStudyId(study.getId());
		for (DatasetBean data : datasets) {
			if (!data.getStatus().equals(Status.DELETED)) {
				data.setStatus(Status.AUTO_DELETED);
				data.setUpdater(updater);
				data.setUpdatedDate(new Date());
				datasetDao.update(data);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void restoreStudy(StudyBean study, StudyBean currentStudy, StudyUserRoleBean currentRole,
			UserAccountBean updater) throws Exception {
		StudyDAO studyDao = getStudyDAO();
		DatasetDAO datasetDao = getDatasetDAO();
		EventCRFDAO eventCrfDao = getEventCRFDAO();
		StudyEventDAO studyEventDao = getStudyEventDAO();
		UserAccountDAO userAccountDao = getUserAccountDAO();
		StudySubjectDAO studySubjectDao = getStudySubjectDAO();
		StudyGroupClassDAO studyGroupClassDao = getStudyGroupClassDAO();
		SubjectGroupMapDAO subjectGroupMapDao = getSubjectGroupMapDAO();
		EventDefinitionCRFDAO eventDefinitionCrfDao = getEventDefinitionCRFDAO();
		StudyEventDefinitionDAO studyEventDefinitionDao = getStudyEventDefinitionDAO();

		if (study.getOldStatus() != Status.DELETED) {
			study.setStatus(study.getOldStatus());
		} else {
			study.setStatus(Status.AVAILABLE);
		}
		study.setUpdater(updater);
		study.setUpdatedDate(new Date());
		studyDao.update(study);

		// restore auto-removed sites
		List<StudyBean> sites = (List<StudyBean>) studyDao.findAllByParent(study.getId());
		for (StudyBean site : sites) {
			if (site.getStatus() == Status.AUTO_DELETED) {
				site.setStatus(site.getOldStatus());
				site.setUpdater(updater);
				site.setUpdatedDate(new Date());
				studyDao.update(site);
			}
		}
		// restore all users and roles
		List<StudyUserRoleBean> userRoles = userAccountDao.findAllByStudyId(study.getId());
		for (StudyUserRoleBean role : userRoles) {
			userAccountService.autoRestoreStudyUserRole(role, updater);
		}

		// YW << Meanwhile update current active study if restored study
		// is current active study
		if (study.getId() == currentStudy.getId()) {
			currentStudy.setStatus(Status.AVAILABLE);

			StudyUserRoleBean r = userAccountDao.findRoleByUserNameAndStudyId(updater.getName(), currentStudy.getId());
			currentRole.setRole(r.getRole());
		} else if (currentStudy.getParentStudyId() == study.getId() && currentStudy.getStatus() == Status.AUTO_DELETED) {
			// when an active site's parent study has been restored, this
			// active site will be restored as well if it was auto-removed
			currentStudy.setStatus(Status.AVAILABLE);

			StudyUserRoleBean r = userAccountDao.findRoleByUserNameAndStudyId(updater.getName(), currentStudy.getId());
			StudyUserRoleBean rInParent = userAccountDao.findRoleByUserNameAndStudyId(updater.getName(),
					currentStudy.getParentStudyId());
			// according to logic in Controller.java: inherited
			// role from parent study, pick the higher role
			currentRole.setRole(Role.get(Role.max(r.getRole(), rInParent.getRole()).getId()));
		}
		// YW 06-18-2007 >>

		// restore all subjects
		List<StudySubjectBean> subjects = studySubjectDao.findAllByStudy(study);
		for (StudySubjectBean subject : subjects) {
			if (subject.getStatus().equals(Status.AUTO_DELETED)) {
				subject.setStatus(Status.AVAILABLE);
				subject.setUpdater(updater);
				subject.setUpdatedDate(new Date());
				studySubjectDao.update(subject);
			}
		}

		List<StudyGroupClassBean> groups = studyGroupClassDao.findAllByStudy(study);
		for (StudyGroupClassBean group : groups) {
			if (group.getStatus().equals(Status.AUTO_DELETED)) {
				group.setStatus(Status.AVAILABLE);
				group.setUpdater(updater);
				group.setUpdatedDate(new Date());
				studyGroupClassDao.update(group);
				// all subject_group_map
				List<SubjectGroupMapBean> subjectGroupMaps = subjectGroupMapDao.findAllByStudyGroupClassId(group
						.getId());
				for (SubjectGroupMapBean sgMap : subjectGroupMaps) {
					if (sgMap.getStatus().equals(Status.AUTO_DELETED)) {
						sgMap.setStatus(Status.AVAILABLE);
						sgMap.setUpdater(updater);
						sgMap.setUpdatedDate(new Date());
						subjectGroupMapDao.update(sgMap);
					}
				}
			}
		}

		// restore all event definitions and event
		List<StudyEventDefinitionBean> definitions = studyEventDefinitionDao.findAllByStudy(study);
		for (StudyEventDefinitionBean definition : definitions) {
			if (definition.getStatus().equals(Status.AUTO_DELETED)) {
				definition.setStatus(Status.AVAILABLE);
				definition.setUpdater(updater);
				definition.setUpdatedDate(new Date());
				studyEventDefinitionDao.update(definition);

				List<EventDefinitionCRFBean> edcs = (List<EventDefinitionCRFBean>) eventDefinitionCrfDao
						.findAllByDefinition(definition.getId());
				for (EventDefinitionCRFBean edc : edcs) {
					if (edc.getStatus().equals(Status.AUTO_DELETED)) {
						edc.setStatus(Status.AVAILABLE);
						edc.setUpdater(updater);
						edc.setUpdatedDate(new Date());
						eventDefinitionCrfDao.update(edc);
					}
				}

				List<StudyEventBean> events = (List<StudyEventBean>) studyEventDao.findAllByDefinition(definition
						.getId());
				for (StudyEventBean event : events) {
					if (event.getStatus().equals(Status.AUTO_DELETED)) {
						event.setStatus(Status.AVAILABLE);
						event.setUpdater(updater);
						event.setUpdatedDate(new Date());
						studyEventDao.update(event);

						List<EventCRFBean> eventCRFs = eventCrfDao.findAllByStudyEvent(event);

						eventCRFService.restoreEventCRFsFromAutoRemovedState(eventCRFs, updater);
					}
				}
			}
		}

		List<DatasetBean> datasets = datasetDao.findAllByStudyId(study.getId());
		for (DatasetBean data : datasets) {
			if (data.getStatus().equals(Status.AUTO_DELETED)) {
				data.setStatus(Status.AVAILABLE);
				data.setUpdater(updater);
				data.setUpdatedDate(new Date());
				datasetDao.update(data);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeSite(StudyBean site, StudyBean currentStudy, StudyUserRoleBean currentRole,
			UserAccountBean updater) throws Exception {
		StudyDAO studyDao = getStudyDAO();
		DatasetDAO datasetDao = getDatasetDAO();
		StudyEventDAO studyEventDao = getStudyEventDAO();
		StudyGroupDAO studyGroupDao = getStudyGroupDAO();
		UserAccountDAO userAccountDao = getUserAccountDAO();
		StudySubjectDAO studySubjectDao = getStudySubjectDAO();
		SubjectGroupMapDAO subjectGroupMapDao = getSubjectGroupMapDAO();

		// change all statuses to unavailable
		site.setOldStatus(site.getStatus());
		site.setStatus(Status.DELETED);
		site.setUpdater(updater);
		site.setUpdatedDate(new Date());
		studyDao.update(site);

		// remove all users and roles
		List<StudyUserRoleBean> userRoles = userAccountDao.findAllByStudyId(site.getId());
		for (StudyUserRoleBean role : userRoles) {
			userAccountService.autoRemoveStudyUserRole(role, updater);
		}

		if (site.getId() == currentStudy.getId()) {
			currentStudy.setStatus(Status.DELETED);
			currentRole.setStatus(Status.DELETED);
		}

		// remove all study_group
		List<StudyGroupBean> groups = studyGroupDao.findAllByStudy(site);
		for (StudyGroupBean group : groups) {
			if (!group.getStatus().equals(Status.DELETED)) {
				group.setStatus(Status.AUTO_DELETED);
				group.setUpdater(updater);
				group.setUpdatedDate(new Date());
				studyGroupDao.update(group);
				// all subject_group_map
				List<SubjectGroupMapBean> subjectGroupMaps = subjectGroupMapDao.findAllByStudyGroupId(group.getId());
				for (SubjectGroupMapBean sgMap : subjectGroupMaps) {
					if (!sgMap.getStatus().equals(Status.DELETED)) {
						sgMap.setStatus(Status.AUTO_DELETED);
						sgMap.setUpdater(updater);
						sgMap.setUpdatedDate(new Date());
						subjectGroupMapDao.update(sgMap);
					}
				}
			}
		}

		List<StudySubjectBean> subjects = studySubjectDao.findAllByStudy(site);
		for (StudySubjectBean subject : subjects) {
			if (!subject.getStatus().equals(Status.DELETED)) {
				subject.setStatus(Status.AUTO_DELETED);
				subject.setUpdater(updater);
				subject.setUpdatedDate(new Date());
				studySubjectDao.update(subject);

				List<StudyEventBean> events = studyEventDao.findAllByStudySubject(subject);
				for (StudyEventBean event : events) {
					if (!event.getStatus().equals(Status.DELETED)) {
						event.setPrevSubjectEventStatus(event.getSubjectEventStatus());
						event.setSubjectEventStatus(SubjectEventStatus.REMOVED);
						event.setStatus(Status.AUTO_DELETED);
						event.setUpdater(updater);
						event.setUpdatedDate(new Date());
						studyEventDao.update(event);

						eventCRFService.removeEventCRFsByStudyEvent(event, updater);
					}
				}
			}
		}

		List<DatasetBean> dataset = datasetDao.findAllByStudyId(site.getId());
		for (DatasetBean data : dataset) {
			if (!data.getStatus().equals(Status.DELETED)) {
				data.setStatus(Status.AUTO_DELETED);
				data.setUpdater(updater);
				data.setUpdatedDate(new Date());
				datasetDao.update(data);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void restoreSite(StudyBean site, StudyBean currentStudy, StudyUserRoleBean currentRole,
			UserAccountBean updater) throws Exception {
		StudyDAO studyDao = getStudyDAO();
		DatasetDAO datasetDao = getDatasetDAO();
		EventCRFDAO eventCrfDao = getEventCRFDAO();
		StudyEventDAO studyEventDao = getStudyEventDAO();
		StudyGroupDAO studyGroupDao = getStudyGroupDAO();
		UserAccountDAO userAccountDao = getUserAccountDAO();
		StudySubjectDAO studySubjectDao = getStudySubjectDAO();
		SubjectGroupMapDAO subjectGroupMapDao = getSubjectGroupMapDAO();

		Status newStatus = Status.AVAILABLE;
		if (site.getParentStudyId() > 0) {
			StudyBean parentStudy = (StudyBean) studyDao.findByPK(site.getParentStudyId());
			newStatus = parentStudy.getStatus();
		}

		site.setOldStatus(site.getStatus());
		site.setStatus(newStatus);
		site.setUpdater(updater);
		site.setUpdatedDate(new Date());
		studyDao.update(site);

		// restore all users and roles
		List<StudyUserRoleBean> userRoles = userAccountDao.findAllByStudyId(site.getId());
		for (StudyUserRoleBean role : userRoles) {
			userAccountService.autoRestoreStudyUserRole(role, updater);
		}

		// Meanwhile update current active study
		// attribute of session if restored study is current active
		// study
		if (site.getId() == currentStudy.getId()) {
			currentStudy.setStatus(Status.AVAILABLE);

			StudyUserRoleBean r = userAccountDao.findRoleByUserNameAndStudyId(updater.getName(), currentStudy.getId());
			StudyUserRoleBean rInParent = userAccountDao.findRoleByUserNameAndStudyId(updater.getName(),
					currentStudy.getParentStudyId());
			// according to logic in Controller.java: inherited
			// role from parent study, pick the higher role
			currentRole.setRole(Role.max(r.getRole(), rInParent.getRole()));
		}

		// restore all study_group
		List<StudyGroupBean> groups = (List<StudyGroupBean>) studyGroupDao.findAllByStudy(site);
		for (StudyGroupBean group : groups) {
			if (group.getStatus().equals(Status.AUTO_DELETED)) {
				group.setStatus(Status.AVAILABLE);
				group.setUpdater(updater);
				group.setUpdatedDate(new Date());
				studyGroupDao.update(group);
				// all subject_group_map
				List<SubjectGroupMapBean> subjectGroupMaps = (List<SubjectGroupMapBean>) subjectGroupMapDao
						.findAllByStudyGroupId(group.getId());
				for (SubjectGroupMapBean sgMap : subjectGroupMaps) {
					if (sgMap.getStatus().equals(Status.AUTO_DELETED)) {
						sgMap.setStatus(Status.AVAILABLE);
						sgMap.setUpdater(updater);
						sgMap.setUpdatedDate(new Date());
						subjectGroupMapDao.update(sgMap);
					}
				}
			}
		}

		List<StudySubjectBean> subjects = (List<StudySubjectBean>) studySubjectDao.findAllByStudy(site);
		for (StudySubjectBean subject : subjects) {
			if (subject.getStatus().equals(Status.AUTO_DELETED)) {
				subject.setStatus(Status.AVAILABLE);
				subject.setUpdater(updater);
				subject.setUpdatedDate(new Date());
				studySubjectDao.update(subject);

				List<StudyEventBean> events = (List<StudyEventBean>) studyEventDao.findAllByStudySubject(subject);
				for (StudyEventBean event : events) {
					if (event.getStatus().equals(Status.AUTO_DELETED)) {
						SubjectEventStatus subjectEventStatus = event.getSubjectEventStatus();
						event.setSubjectEventStatus(event.getPrevSubjectEventStatus());
						event.setPrevSubjectEventStatus(subjectEventStatus);
						event.setStatus(Status.AVAILABLE);
						event.setUpdater(updater);
						event.setUpdatedDate(new Date());
						studyEventDao.update(event);

						List<EventCRFBean> eventCRFs = (List<EventCRFBean>) eventCrfDao.findAllByStudyEvent(event);

						eventCRFService.restoreEventCRFsFromAutoRemovedState(eventCRFs, updater);

						SubjectEventStatusUtil.determineSubjectEventState(event, eventCRFs, new DAOWrapper(dataSource));
					}
				}
			}
		}

		List<DatasetBean> dataset = (List<DatasetBean>) datasetDao.findAllByStudyId(site.getId());
		for (DatasetBean data : dataset) {
			data.setStatus(Status.AVAILABLE);
			data.setUpdater(updater);
			data.setUpdatedDate(new Date());
			datasetDao.update(data);
		}
	}
}
