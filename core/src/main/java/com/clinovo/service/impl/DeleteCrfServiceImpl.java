/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2014 Clinovo Inc.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License 
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License along with this program.  
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */

package com.clinovo.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.hibernate.RuleSetDao;
import org.akaza.openclinica.dao.hibernate.RuleSetRuleDao;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.util.StudyEventDefinitionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.clinovo.service.DeleteCrfService;
import com.clinovo.service.EventDefinitionCrfService;
import com.clinovo.util.DAOWrapper;
import com.clinovo.util.SubjectEventStatusUtil;

/**
 * DeleteCrfServiceImpl.
 */
@Service
@SuppressWarnings("unchecked")
public class DeleteCrfServiceImpl implements DeleteCrfService {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private RuleSetDao ruleSetDao;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private RuleSetRuleDao ruleSetRuleDao;

	@Autowired
	private EventDefinitionCrfService eventDefinitionCrfService;

	/**
	 * {@inheritDoc}
	 */
	public void deleteCrf(CRFBean crfBean, UserAccountBean userAccountBean, Locale locale, boolean force)
			throws Exception {
		if (!force) {
			List<RuleSetBean> ruleSetBeanList = ruleSetDao.findByCrfIdAndCrfOid(crfBean);
			List<EventCRFBean> eventCrfBeanList = getEventCRFDAO().findAllStartedByCrf(crfBean.getId());
			List<StudyEventDefinitionBean> eventDefinitionListAvailable = StudyEventDefinitionUtil
					.studyEventDefinitionListFilter(dataSource,
							getEventDefinitionCRFDAO().findAllByCRF(crfBean.getId()));
			List<DiscrepancyNoteBean> crfDiscrepancyNotes = getDiscrepancyNoteDAO().findAllByCRFId(crfBean.getId());
			if (eventCrfBeanList.size() > 0 || crfDiscrepancyNotes.size() > 0 || eventDefinitionListAvailable.size() > 0
					|| ruleSetBeanList.size() > 0) {
				throw new Exception(messageSource.getMessage("this_crf_has_associated_data", null, locale));
			}
		}
		deleteCrf(crfBean, userAccountBean, force);
	}

	/**
	 * {@inheritDoc}
	 */
	public void deleteCrfVersion(CRFVersionBean crfVersionBean, Locale locale, boolean force) throws Exception {
		CRFDAO crfDao = getCRFDAO();
		CRFBean crfBean = (CRFBean) crfDao.findByPK(crfVersionBean.getCrfId());
		int crfVersionQuantity = getCRFVersionDAO().findAllByCRF(crfBean.getId()).size();
		if (crfVersionQuantity == 1) {
			throw new Exception(messageSource.getMessage("unable_to_delete_latest_crf_version", null, locale));
		}
		List<EventCRFBean> eventCrfBeanList = getEventCRFDAO().findAllStartedByCrfVersion(crfVersionBean.getId());
		if (!force) {
			List<RuleSetBean> ruleSetBeanList = crfVersionQuantity > 1
					? ruleSetDao.findByCrfVersionIdAndCrfVersionOid(crfVersionBean)
					: ruleSetDao.findByCrfIdAndCrfOid(crfBean);
			List<StudyEventDefinitionBean> eventDefinitionListAvailable = crfVersionQuantity > 1
					? new ArrayList<StudyEventDefinitionBean>()
					: StudyEventDefinitionUtil.studyEventDefinitionListFilter(dataSource,
							getEventDefinitionCRFDAO().findAllByCRF(crfBean.getId()));
			List<DiscrepancyNoteBean> crfDiscrepancyNotes = getDiscrepancyNoteDAO()
					.findAllByCrfVersionId(crfVersionBean.getId());
			if (eventCrfBeanList.size() > 0 || crfDiscrepancyNotes.size() > 0 || eventDefinitionListAvailable.size() > 0
					|| ruleSetBeanList.size() > 0) {
				throw new Exception(messageSource.getMessage("this_crf_version_has_associated_data", null, locale));
			}
		}
		deleteCrfVersion(crfVersionBean);
	}

	private void deleteCrfVersion(CRFVersionBean crfVersionBean) {
		ruleSetDao.unbindRulesFromCrfVersion(crfVersionBean);
		ruleSetRuleDao.deleteAllRulesByCrfVersion(crfVersionBean);
		getDiscrepancyNoteDAO().deleteByCrfVersionId(crfVersionBean.getId());
		eventDefinitionCrfService.setDefaultCRFVersionInsteadOfDeleted(crfVersionBean.getId());
		getCRFVersionDAO().deleteCrfVersion(crfVersionBean.getId());
	}

	private void deleteCrf(CRFBean crfBean, UserAccountBean userAccountBean, boolean force) throws Exception {
		List<StudyEventBean> studyEventBeanList = force
				? getStudyEventDAO().findStartedCompletedOrSDVStudyEventsByCrf(crfBean.getId())
				: new ArrayList<StudyEventBean>();
		ruleSetRuleDao.deleteAllRulesByCrf(crfBean);
		getDiscrepancyNoteDAO().deleteByCrfId(crfBean.getId());
		getCRFDAO().deleteCrf(crfBean.getId());
		if (studyEventBeanList.size() > 0) {
			SubjectEventStatusUtil.determineSubjectEventStates(studyEventBeanList, userAccountBean,
					new DAOWrapper(dataSource), null);
		}
	}

	private DiscrepancyNoteDAO getDiscrepancyNoteDAO() {
		return new DiscrepancyNoteDAO(dataSource);
	}

	private EventDefinitionCRFDAO getEventDefinitionCRFDAO() {
		return new EventDefinitionCRFDAO(dataSource);
	}

	private EventCRFDAO getEventCRFDAO() {
		return new EventCRFDAO(dataSource);
	}

	private StudyEventDAO getStudyEventDAO() {
		return new StudyEventDAO(dataSource);
	}

	private CRFVersionDAO getCRFVersionDAO() {
		return new CRFVersionDAO(dataSource);
	}

	private CRFDAO getCRFDAO() {
		return new CRFDAO(dataSource);
	}
}
