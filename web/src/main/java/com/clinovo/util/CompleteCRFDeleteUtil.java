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
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.clinovo.util;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.hibernate.RuleSetDao;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;

import java.util.ArrayList;
import java.util.List;

public final class CompleteCRFDeleteUtil {

	private static SessionManager sessionManager;
	private static RuleSetDao ruleSetDao;

	private CompleteCRFDeleteUtil() {
	}

	@SuppressWarnings("unchecked")
	public static void validateCRF(CRFBean crfBean) {

		EventCRFDAO eventCRFDao = new EventCRFDAO(sessionManager.getDataSource());
		EventDefinitionCRFDAO eventDefinitionCrfDao = new EventDefinitionCRFDAO(sessionManager.getDataSource());

		List<DiscrepancyNoteBean> crfDiscrepancyNotes = new ArrayList<DiscrepancyNoteBean>();
		List<RuleSetBean> crfInTargetRuleSetList = new ArrayList<RuleSetBean>();
		List<EventDefinitionCRFBean> eventDefinitionCrfListFiltered = new ArrayList<EventDefinitionCRFBean>();
		boolean crfUsedInRuleExpression = false;

		List<EventCRFBean> eventCrfBeanList = eventCRFDao.findAllByCRF(crfBean.getId());

		if (eventCrfBeanList.size() == 0) {
			eventDefinitionCrfListFiltered = eventDefinitionCrfListFilter((List<EventDefinitionCRFBean>) eventDefinitionCrfDao.findAllByCRF(crfBean.getId()));
			if (eventDefinitionCrfListFiltered.size() == 0) {
				crfDiscrepancyNotes = collectAllDnFromEventCrfList(eventCrfBeanList);
				if (crfDiscrepancyNotes.size() == 0) {
					crfInTargetRuleSetList = ruleSetListFilter(crfBean.getId());
					if (crfInTargetRuleSetList.size() == 0) {
						crfUsedInRuleExpression = checkCrfInRuleExpressions(crfBean.getId());
					}
				}
			}
		}

		if (eventCrfBeanList.size() > 0 || crfDiscrepancyNotes.size() > 0 || eventDefinitionCrfListFiltered.size() > 0 || crfInTargetRuleSetList.size() > 0 || crfUsedInRuleExpression) {
			crfBean.setDeletable(false);
		} else {
			crfBean.setDeletable(true);
		}
	}

	@SuppressWarnings("unchecked")
	private static boolean checkCrfInRuleExpressions(int crfId) {

		CRFVersionDAO crfVersionDao = new CRFVersionDAO(sessionManager.getDataSource());
		ItemDAO itemDao = new ItemDAO(sessionManager.getDataSource());

		StudyDAO studyDao = new StudyDAO(sessionManager.getDataSource());

		List<CRFVersionBean> crfVersionList = (List<CRFVersionBean>) crfVersionDao.findAllByCRF(crfId);
		List<String> itemOidList = new ArrayList<String>();

		for (CRFVersionBean crfVersionBean : crfVersionList) {
			List<ItemBean> crfVersionItemList = itemDao.findAllItemsByVersionId(crfVersionBean.getId());
			if (crfVersionItemList.size() > 0) {
				for (ItemBean item : crfVersionItemList) {
					itemOidList.add(item.getOid());
				}
			}
		}

		List<RuleSetBean> ruleSetBeanListFromStudies = new ArrayList<RuleSetBean>();
		List<StudyBean> studyList = (List<StudyBean>) studyDao.findAll();

		for (StudyBean study : studyList) {
			List<RuleSetBean> studyRuleSetBeanList = ruleSetDao.findAllByStudy(study);
			if (studyRuleSetBeanList.size() > 0) {
				ruleSetBeanListFromStudies.addAll(studyRuleSetBeanList);
			}
		}

		for (RuleSetBean ruleSetBean : ruleSetBeanListFromStudies) {
			for (RuleSetRuleBean ruleSetRuleBean : ruleSetBean.getRuleSetRules()) {
				String expression = ruleSetRuleBean.getRuleBean().getExpression().getValue();
				for (String itemOid : itemOidList) {
					if (expression.indexOf(itemOid) > 0) {
						return true;
					}
				}
			}
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	private static List<RuleSetBean> ruleSetListFilter(int crfId) {

		StudyDAO studyDao = new StudyDAO(sessionManager.getDataSource());
		CRFDAO crfDao = new CRFDAO(sessionManager.getDataSource());

		List<RuleSetBean> ruleSetBeanList = new ArrayList<RuleSetBean>();
		List<StudyBean> studyList = (List<StudyBean>) studyDao.findAll();
		CRFBean crfBean = (CRFBean) crfDao.findByPK(crfId);

		for (StudyBean study : studyList) {
			List<RuleSetBean> studyRuleSetBeanList = ruleSetDao.findByCrf(crfBean, study);
			if (studyRuleSetBeanList.size() > 0) {
				ruleSetBeanList.addAll(studyRuleSetBeanList);
			}
		}
		return ruleSetBeanList;
	}

	private static List<EventDefinitionCRFBean> eventDefinitionCrfListFilter(List<EventDefinitionCRFBean> eventDefinitionCrfList) {

		List<EventDefinitionCRFBean> eventDefinitionCrfListFiltered = new ArrayList<EventDefinitionCRFBean>();

		for (EventDefinitionCRFBean eventDefCrfBean : eventDefinitionCrfList) {
			if (!eventDefCrfBean.getStatus().isDeleted()) {
				eventDefinitionCrfListFiltered.add(eventDefCrfBean);
				return eventDefinitionCrfListFiltered;
			}
		}

		return eventDefinitionCrfListFiltered;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static List<DiscrepancyNoteBean> collectAllDnFromEventCrfList(List<EventCRFBean> eventCrfBeans) {

		DiscrepancyNoteDAO discrepancyNoteDao = new DiscrepancyNoteDAO(sessionManager.getDataSource());
		List<DiscrepancyNoteBean> dnList = new ArrayList<DiscrepancyNoteBean>();

		for (EventCRFBean eventCrfBean : eventCrfBeans) {
			List discrepancyNotes = discrepancyNoteDao.findAllItemNotesByEventCRF(eventCrfBean.getId());
			if (discrepancyNotes.size() > 0) {
				dnList.addAll(discrepancyNotes);
				return dnList;
			}
		}

		return dnList;
	}

	public static void setSessionManager(SessionManager sessionManager) {
		CompleteCRFDeleteUtil.sessionManager = sessionManager;
	}

	public static void setRuleSetDao(RuleSetDao ruleSetDao) {
		CompleteCRFDeleteUtil.ruleSetDao = ruleSetDao;
	}
}
