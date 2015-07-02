/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
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
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * OpenClinica is distributed under the
 * Copyright 2003-2008 Akaza Research
 */
package org.akaza.openclinica.service.rule;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.hibernate.DynamicsItemFormMetadataDao;
import org.akaza.openclinica.dao.hibernate.DynamicsItemGroupMetadataDao;
import org.akaza.openclinica.dao.hibernate.RuleActionRunLogDao;
import org.akaza.openclinica.dao.hibernate.RuleDao;
import org.akaza.openclinica.dao.hibernate.RuleSetAuditDao;
import org.akaza.openclinica.dao.hibernate.RuleSetDao;
import org.akaza.openclinica.dao.hibernate.RuleSetRuleDao;
import org.akaza.openclinica.dao.hibernate.ViewRuleAssignmentFilter;
import org.akaza.openclinica.dao.hibernate.ViewRuleAssignmentSort;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.domain.Status;
import org.akaza.openclinica.domain.crfdata.DynamicsItemFormMetadataBean;
import org.akaza.openclinica.domain.rule.AuditableBeanWrapper;
import org.akaza.openclinica.domain.rule.RuleBean;
import org.akaza.openclinica.domain.rule.RuleBulkExecuteContainer;
import org.akaza.openclinica.domain.rule.RuleBulkExecuteContainerTwo;
import org.akaza.openclinica.domain.rule.RuleSetAuditBean;
import org.akaza.openclinica.domain.rule.RuleSetBasedViewContainer;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean.RuleSetRuleBeanImportStatus;
import org.akaza.openclinica.domain.rule.RulesPostImportContainer;
import org.akaza.openclinica.domain.rule.action.RuleActionBean;
import org.akaza.openclinica.domain.rule.action.RuleActionRunBean.Phase;
import org.akaza.openclinica.domain.rule.expression.ExpressionBean;
import org.akaza.openclinica.logic.rulerunner.CrfBulkRuleRunner;
import org.akaza.openclinica.logic.rulerunner.DataEntryRuleRunner;
import org.akaza.openclinica.logic.rulerunner.ExecutionMode;
import org.akaza.openclinica.logic.rulerunner.ImportDataRuleRunner;
import org.akaza.openclinica.logic.rulerunner.ImportDataRuleRunnerContainer;
import org.akaza.openclinica.logic.rulerunner.MessageContainer;
import org.akaza.openclinica.logic.rulerunner.RuleSetBulkRuleRunner;
import org.akaza.openclinica.service.crfdata.DynamicsMetadataService;
import org.akaza.openclinica.service.rule.expression.ExpressionService;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Contains RuleSet services.
 */
public class RuleSetService implements RuleSetServiceInterface {

	private final Logger logger = LoggerFactory.getLogger(getClass().getName());
	private DataSource dataSource;
	private RuleSetDao ruleSetDao;
	private RuleSetAuditDao ruleSetAuditDao;
	private RuleDao ruleDao;
	private RuleSetRuleDao ruleSetRuleDao;
	private JavaMailSenderImpl mailSender;

	private DynamicsItemFormMetadataDao dynamicsItemFormMetadataDao;
	private ExpressionService expressionService;
	private String requestURLMinusServletPath;
	private String contextPath;
	private DynamicsMetadataService dynamicsMetadataService;
	private RuleActionRunLogDao ruleActionRunLogDao;

	/**
	 * RuleSetService constructor.
	 * 
	 * @param dataSource
	 *            DataSource
	 * @param dynamicsItemFormMetadataDao
	 *            DynamicsItemFormMetadataDao
	 * @param dynamicsItemGroupMetadataDao
	 *            DynamicsItemGroupMetadataDao
	 * @param mailSender
	 *            JavaMailSenderImpl
	 * @param ruleDao
	 *            RuleDao
	 * @param ruleSetDao
	 *            RuleSetDao
	 * @param ruleSetRuleDao
	 *            RuleSetRuleDao
	 * @param ruleSetAuditDao
	 *            RuleSetAuditDao
	 * @param ruleActionRunLogDao
	 *            RuleActionRunLogDao
	 */
	public RuleSetService(DataSource dataSource, DynamicsItemFormMetadataDao dynamicsItemFormMetadataDao,
			DynamicsItemGroupMetadataDao dynamicsItemGroupMetadataDao, JavaMailSenderImpl mailSender, RuleDao ruleDao,
			RuleSetDao ruleSetDao, RuleSetRuleDao ruleSetRuleDao, RuleSetAuditDao ruleSetAuditDao,
			RuleActionRunLogDao ruleActionRunLogDao) {
		dynamicsMetadataService = new DynamicsMetadataService(dynamicsItemFormMetadataDao,
				dynamicsItemGroupMetadataDao, dataSource);
		this.expressionService = dynamicsMetadataService.getExpressionService();
		this.ruleDao = ruleDao;
		this.ruleSetDao = ruleSetDao;
		this.dataSource = dataSource;
		this.mailSender = mailSender;
		this.ruleSetRuleDao = ruleSetRuleDao;
		this.ruleSetAuditDao = ruleSetAuditDao;
		this.ruleActionRunLogDao = ruleActionRunLogDao;
		this.dynamicsItemFormMetadataDao = dynamicsItemFormMetadataDao;
	}

	/**
	 * {@inheritDoc}
	 */
	public RuleSetBean saveRuleSet(RuleSetBean ruleSetBean) {
		return getRuleSetDao().saveOrUpdate(ruleSetBean);
	}

	/**
	 * {@inheritDoc}
	 */
	public void saveImportFromDesigner(RulesPostImportContainer rulesContainer) {
		HashMap<String, RuleBean> ruleBeans = new HashMap<String, RuleBean>();
		for (AuditableBeanWrapper<RuleBean> ruleBeanWrapper : rulesContainer.getValidRuleDefs()) {
			RuleBean r = getRuleDao().saveOrUpdate(ruleBeanWrapper.getAuditableBean());
			ruleBeans.put(r.getOid(), r);
		}
		for (AuditableBeanWrapper<RuleBean> ruleBeanWrapper : rulesContainer.getDuplicateRuleDefs()) {
			RuleBean r = getRuleDao().saveOrUpdate(ruleBeanWrapper.getAuditableBean());
			ruleBeans.put(r.getOid(), r);
		}

		for (AuditableBeanWrapper<RuleSetBean> ruleBeanWrapper : rulesContainer.getValidRuleSetDefs()) {
			loadRuleSetRuleWithPersistentRules(ruleBeanWrapper.getAuditableBean());
			saveRuleSet(ruleBeanWrapper.getAuditableBean());
		}

		for (AuditableBeanWrapper<RuleSetBean> ruleBeanWrapper : rulesContainer.getDuplicateRuleSetDefs()) {
			loadRuleSetRuleWithPersistentRulesWithHashMap(ruleBeanWrapper.getAuditableBean(), ruleBeans);
			replaceRuleSet(ruleBeanWrapper.getAuditableBean());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void saveImport(RulesPostImportContainer rulesContainer) {
		for (AuditableBeanWrapper<RuleBean> ruleBeanWrapper : rulesContainer.getValidRuleDefs()) {
			getRuleDao().saveOrUpdate(ruleBeanWrapper.getAuditableBean());
		}
		for (AuditableBeanWrapper<RuleBean> ruleBeanWrapper : rulesContainer.getDuplicateRuleDefs()) {
			getRuleDao().saveOrUpdate(ruleBeanWrapper.getAuditableBean());
		}

		for (AuditableBeanWrapper<RuleSetBean> ruleBeanWrapper : rulesContainer.getValidRuleSetDefs()) {
			loadRuleSetRuleWithPersistentRules(ruleBeanWrapper.getAuditableBean());
			saveRuleSet(ruleBeanWrapper.getAuditableBean());
		}

		for (AuditableBeanWrapper<RuleSetBean> ruleBeanWrapper : rulesContainer.getDuplicateRuleSetDefs()) {
			loadRuleSetRuleWithPersistentRules(ruleBeanWrapper.getAuditableBean());
			replaceRuleSet(ruleBeanWrapper.getAuditableBean());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void saveImport(RuleSetRuleBean ruleSetRule) {
		getRuleDao().saveOrUpdate(ruleSetRule.getRuleBean());
		getRuleSetDao().saveOrUpdate(ruleSetRule.getRuleSetBean());
	}

	/**
	 * {@inheritDoc}
	 */
	public RuleSetBean updateRuleSet(RuleSetBean ruleSetBean, UserAccountBean user, Status status) {
		ruleSetBean.setStatus(status);
		ruleSetBean.setUpdater(user);
		for (RuleSetRuleBean ruleSetRuleBean : ruleSetBean.getRuleSetRules()) {
			ruleSetRuleBean.setStatus(status);
			ruleSetRuleBean.setUpdater(user);
		}
		ruleSetBean = saveRuleSet(ruleSetBean);
		ruleSetAuditDao.saveOrUpdate(createRuleSetAuditBean(ruleSetBean, user, status));
		return ruleSetBean;

	}

	private RuleSetAuditBean createRuleSetAuditBean(RuleSetBean ruleSetBean, UserAccountBean user, Status status) {
		RuleSetAuditBean ruleSetAuditBean = new RuleSetAuditBean();
		ruleSetAuditBean.setRuleSetBean(ruleSetBean);
		ruleSetAuditBean.setStatus(status);
		ruleSetAuditBean.setUpdater(user);
		return ruleSetAuditBean;
	}

	/**
	 * Loads RuleSet with persistent rules from passed HashMap.
	 * 
	 * @param ruleSetBean
	 *            ruleSetBean to load
	 * @param persistentRules
	 *            persistent rules map
	 */
	public void loadRuleSetRuleWithPersistentRulesWithHashMap(RuleSetBean ruleSetBean,
			HashMap<String, RuleBean> persistentRules) {
		for (RuleSetRuleBean ruleSetRule : ruleSetBean.getRuleSetRules()) {
			if (ruleSetRule.getId() == null) {
				String ruleOid = ruleSetRule.getOid();
				ruleSetRule.setRuleBean(persistentRules.get(ruleOid));
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void loadRuleSetRuleWithPersistentRules(RuleSetBean ruleSetBean) {
		for (RuleSetRuleBean ruleSetRule : ruleSetBean.getRuleSetRules()) {
			if (ruleSetRule.getId() == null) {
				String ruleOid = ruleSetRule.getOid();
				ruleSetRule.setRuleBean(ruleDao.findByOid(ruleOid, ruleSetBean.getStudyId()));
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public RuleSetBean replaceRuleSet(RuleSetBean ruleSetBean) {
		for (RuleSetRuleBean ruleSetRuleBean : ruleSetBean.getRuleSetRules()) {
			if (ruleSetRuleBean.getId() != null
					&& ruleSetRuleBean.getRuleSetRuleBeanImportStatus() == RuleSetRuleBeanImportStatus.TO_BE_REMOVED) {
				ruleSetRuleBean.setStatus(org.akaza.openclinica.domain.Status.DELETED);
			}
		}
		return getRuleSetDao().saveOrUpdate(ruleSetBean);
	}

	/**
	 * {@inheritDoc}
	 */
	public HashMap<RuleBulkExecuteContainer, HashMap<RuleBulkExecuteContainerTwo, Set<String>>> runRulesInBulk(
			String crfId, ExecutionMode executionMode, StudyBean currentStudy, UserAccountBean ub) {
		CRFBean crf = new CRFBean();
		crf.setId(Integer.valueOf(crfId));
		List<RuleSetBean> ruleSets = getRuleSetsByCrfAndStudy(crf, currentStudy);
		ruleSets = filterByStatusEqualsAvailable(ruleSets);
		ruleSets = filterRuleSetsByStudyEventOrdinal(ruleSets, null);
		ruleSets = filterRuleSetsByGroupOrdinal(ruleSets);
		CrfBulkRuleRunner ruleRunner = new CrfBulkRuleRunner(dataSource, requestURLMinusServletPath, contextPath,
				mailSender);
		ruleRunner.setDynamicsMetadataService(dynamicsMetadataService);
		ruleRunner.setRuleActionRunLogDao(ruleActionRunLogDao);
		return ruleRunner.runRulesBulk(ruleSets, executionMode, currentStudy, null, ub);
	}

	/**
	 * {@inheritDoc}
	 */
	public HashMap<RuleBulkExecuteContainer, HashMap<RuleBulkExecuteContainerTwo, Set<String>>> runRulesInBulk(
			String ruleSetRuleId, String crfVersionId, ExecutionMode executionMode, StudyBean currentStudy,
			UserAccountBean ub) {

		List<RuleSetBean> ruleSets = new ArrayList<RuleSetBean>();
		RuleSetBean ruleSet = getRuleSetBeanByRuleSetRuleAndSubstituteCrfVersion(ruleSetRuleId, crfVersionId,
				currentStudy);
		if (ruleSet != null) {
			ruleSets.add(ruleSet);
		}
		ruleSets = filterByStatusEqualsAvailable(ruleSets);
		ruleSets = filterRuleSetsByStudyEventOrdinal(ruleSets, crfVersionId);
		ruleSets = filterRuleSetsByGroupOrdinal(ruleSets);
		CrfBulkRuleRunner ruleRunner = new CrfBulkRuleRunner(dataSource, requestURLMinusServletPath, contextPath,
				mailSender);
		ruleRunner.setDynamicsMetadataService(dynamicsMetadataService);
		ruleRunner.setRuleActionRunLogDao(ruleActionRunLogDao);
		return ruleRunner.runRulesBulk(ruleSets, executionMode, currentStudy, null, ub);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<RuleSetBasedViewContainer> runRulesInBulk(List<RuleSetBean> ruleSets, Boolean dryRun,
			StudyBean currentStudy, UserAccountBean ub) {
		ruleSets = filterByStatusEqualsAvailable(ruleSets);
		ruleSets = filterRuleSetsByStudyEventOrdinal(ruleSets, null);
		ruleSets = filterRuleSetsByGroupOrdinal(ruleSets);
		RuleSetBulkRuleRunner ruleRunner = new RuleSetBulkRuleRunner(dataSource, requestURLMinusServletPath,
				contextPath, mailSender);
		ruleRunner.setDynamicsMetadataService(dynamicsMetadataService);
		ruleRunner.setRuleActionRunLogDao(ruleActionRunLogDao);
		ExecutionMode executionMode = dryRun ? ExecutionMode.DRY_RUN : ExecutionMode.SAVE;
		return ruleRunner.runRulesBulkFromRuleSetScreen(ruleSets, executionMode, currentStudy, null, ub);
	}

	/**
	 * {@inheritDoc}
	 */
	public MessageContainer runRulesInDataEntry(List<RuleSetBean> ruleSets, Boolean dryRun, UserAccountBean ub,
			HashMap<String, String> variableAndValue, Phase phase, EventCRFBean ecb, HttpServletRequest request) {
		StudyBean currentStudy = (StudyBean) request.getSession().getAttribute("study");
		DataEntryRuleRunner ruleRunner = new DataEntryRuleRunner(dataSource, requestURLMinusServletPath, contextPath,
				mailSender, ecb);
		ruleRunner.setPhase(phase);
		ruleRunner.setRequest(request);
		ruleRunner.setCurrentStudy(currentStudy);
		ruleRunner.setRuleActionRunLogDao(ruleActionRunLogDao);
		ruleRunner.setDynamicsMetadataService(dynamicsMetadataService);
		ruleRunner.setTargetTimeZone(DateTimeZone.forID(ub.getUserTimeZoneId()));
		ruleRunner.setExecutionMode(dryRun ? ExecutionMode.DRY_RUN : ExecutionMode.SAVE);
		return ruleRunner.runRules(ub, ruleSets, variableAndValue);
	}

	/**
	 * {@inheritDoc}
	 */
	public HashMap<String, ArrayList<String>> runRulesInImportData(List<ImportDataRuleRunnerContainer> containers,
			Set<Integer> skippedItemIds, StudyBean study, UserAccountBean ub, ExecutionMode executionMode) {
		return runRulesInImportData(null, null, containers, skippedItemIds, study, ub, executionMode);
	}

	/**
	 * {@inheritDoc}
	 */
	public HashMap<String, ArrayList<String>> runRulesInImportData(Boolean optimiseRuleValidator,
			Connection connection, List<ImportDataRuleRunnerContainer> containers, StudyBean study, UserAccountBean ub,
			ExecutionMode executionMode) {
		return runRulesInImportData(optimiseRuleValidator, connection, containers, new HashSet<Integer>(), study, ub,
				executionMode);
	}

	/**
	 * {@inheritDoc}
	 */
	public HashMap<String, ArrayList<String>> runRulesInImportData(Boolean optimiseRuleValidator,
			Connection connection, List<ImportDataRuleRunnerContainer> containers, Set<Integer> skippedItemIds,
			StudyBean study, UserAccountBean ub, ExecutionMode executionMode) {
		ImportDataRuleRunner ruleRunner = new ImportDataRuleRunner(dataSource, requestURLMinusServletPath, contextPath,
				mailSender);
		ruleRunner.setDynamicsMetadataService(dynamicsMetadataService);
		ruleRunner.setRuleActionRunLogDao(ruleActionRunLogDao);

		return ruleRunner.runRules(optimiseRuleValidator, connection, containers, skippedItemIds, study, ub,
				executionMode);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<RuleSetBean> getRuleSetsByCrfStudyAndStudyEventDefinition(StudyBean study,
			StudyEventDefinitionBean sed, CRFVersionBean crfVersion) {
		CRFBean crf = getCrfDao().findByVersionId(crfVersion.getId());
		logger.debug("crfVersionID : " + crfVersion.getId() + " studyId : " + study.getId()
				+ " studyEventDefinition : " + sed.getId());
		List<RuleSetBean> ruleSets = getRuleSetDao().findByCrfVersionOrCrfAndStudyAndStudyEventDefinition(crfVersion,
				crf, study, sed);
		logger.info("getRuleSetsByCrfStudyAndStudyEventDefinition() : ruleSets Size {} : ", ruleSets.size());
		if (ruleSets.size() > 0) {
			for (RuleSetBean ruleSetBean : ruleSets) {
				getObjects(ruleSetBean);
			}
		} else {
			ruleSets = new ArrayList<RuleSetBean>();
		}
		return ruleSets;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getCountWithFilter(ViewRuleAssignmentFilter viewRuleAssignmentFilter) {
		return getRuleSetRuleDao().getCountWithFilter(viewRuleAssignmentFilter);
	}

	/**
	 * {@inheritDoc}
	 */
	public int getCountByStudy(StudyBean study) {
		return getRuleSetRuleDao().getCountByStudy(study);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<RuleSetRuleBean> getWithFilterAndSort(ViewRuleAssignmentFilter viewRuleAssignmentFilter,
			ViewRuleAssignmentSort viewRuleAssignmentSort, int rowStart, int rowEnd) {
		return getRuleSetRuleDao().getWithFilterAndSort(viewRuleAssignmentFilter, viewRuleAssignmentSort, rowStart,
				rowEnd);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<RuleSetBean> getRuleSetsByStudy(StudyBean study) {
		logger.debug(" Study Id {} ", study.getId());
		List<RuleSetBean> ruleSets = getRuleSetDao().findAllByStudy(study);
		for (RuleSetBean ruleSetBean : ruleSets) {
			getObjects(ruleSetBean);
		}
		logger.info("getRuleSetsByStudy() : ruleSets Size : {}", ruleSets.size());
		return ruleSets;
	}

	/**
	 * {@inheritDoc}
	 */
	public RuleSetBean getRuleSetById(StudyBean study, String id) {
		logger.debug(" Study Id {} ", study.getId());
		RuleSetBean ruleSetBean = getRuleSetDao().findById(Integer.valueOf(id));
		if (ruleSetBean != null) {
			getObjects(ruleSetBean);
		}
		return ruleSetBean;

	}

	/**
	 * {@inheritDoc}
	 */
	public List<RuleSetRuleBean> getRuleSetById(StudyBean study, String id, RuleBean ruleBean) {
		logger.debug(" Study Id {} ", study.getId());
		RuleSetBean ruleSetBean = getRuleSetDao().findById(Integer.valueOf(id));
		return getRuleSetRuleDao().findByRuleSetBeanAndRuleBean(ruleSetBean, ruleBean);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<RuleSetBean> getRuleSetsByCrfAndStudy(CRFBean crfBean, StudyBean study) {
		List<RuleSetBean> ruleSets = getRuleSetDao().findByCrf(crfBean, study);
		for (RuleSetBean ruleSetBean : ruleSets) {
			getObjects(ruleSetBean);
		}
		return ruleSets;
	}

	/**
	 * {@inheritDoc}
	 */
	public RuleSetBean getObjects(RuleSetBean ruleSetBean) {
		ruleSetBean.setStudy((StudyBean) getStudyDao().findByPK(ruleSetBean.getStudyId()));
		if (ruleSetBean.getStudyEventDefinitionId() != null && ruleSetBean.getStudyEventDefinitionId() != 0) {
			ruleSetBean.setStudyEventDefinition((StudyEventDefinitionBean) getStudyEventDefinitionDao().findByPK(
					ruleSetBean.getStudyEventDefinitionId()));
		}
		if (ruleSetBean.getCrfId() != null && ruleSetBean.getCrfId() != 0) {
			ruleSetBean.setCrf((CRFBean) getCrfDao().findByPK(ruleSetBean.getCrfId()));
		}

		if (ruleSetBean.getCrfVersionId() != null) {
			ruleSetBean.setCrfVersion((CRFVersionBean) getCrfVersionDao().findByPK(ruleSetBean.getCrfVersionId()));
		}
		ruleSetBean.setItemGroup(getExpressionService().getItemGroupExpression(ruleSetBean.getTarget().getValue()));
		if (ruleSetBean.getItemId() != null && ruleSetBean.getItemId() != 0) {
			ruleSetBean.setItem((ItemBean) getItemDao().findByPK(ruleSetBean.getItemId()));
		}

		return ruleSetBean;

	}

	private RuleSetBean getRuleSetBeanByRuleSetRuleAndSubstituteCrfVersion(String ruleSetRuleId, String crfVersionId,
			StudyBean currentStudy) {
		RuleSetBean ruleSetBean = null;
		if (ruleSetRuleId != null && crfVersionId != null && ruleSetRuleId.length() > 0 && crfVersionId.length() > 0) {
			RuleSetRuleBean ruleSetRule = getRuleSetRuleDao().findById(Integer.valueOf(ruleSetRuleId));
			ruleSetBean = ruleSetRule.getRuleSetBean();
			filterByRules(ruleSetBean, ruleSetRule.getRuleBean().getId());
			CRFVersionBean crfVersion = (CRFVersionBean) getCrfVersionDao().findByPK(Integer.valueOf(crfVersionId));
			ruleSetBean = replaceCrfOidInTargetExpression(ruleSetBean, crfVersion.getOid());
		}
		return ruleSetBean;
	}

	private ExpressionBean replaceSEDOrdinal(ExpressionBean targetExpression, StudyEventBean studyEvent) {
		ExpressionBean expression = new ExpressionBean(targetExpression.getContext(), targetExpression.getValue());
		expression.setValue(getExpressionService().replaceStudyEventDefinitionOIDWith(expression.getValue(),
				String.valueOf(studyEvent.getId())));
		return expression;
	}

	private ExpressionBean replaceSEDOrdinal(ExpressionBean targetExpression, StudyEventBean studyEvent,
			String fullExpressionValue) {
		ExpressionBean expression = new ExpressionBean(targetExpression.getContext(), fullExpressionValue);
		expression.setValue(getExpressionService().replaceStudyEventDefinitionOIDWith(fullExpressionValue,
				String.valueOf(studyEvent.getId())));
		return expression;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<RuleSetBean> filterByStatusEqualsAvailableOnlyRuleSetRules(List<RuleSetBean> ruleSets) {
		for (RuleSetBean ruleSet : ruleSets) {
			for (Iterator<RuleSetRuleBean> i = ruleSet.getRuleSetRules().iterator(); i.hasNext();) {
				if (i.next().getStatus() != org.akaza.openclinica.domain.Status.AVAILABLE) {
					i.remove();
				}
			}
		}
		return ruleSets;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<RuleSetBean> filterByStatusEqualsAvailable(List<RuleSetBean> ruleSets) {
		for (Iterator<RuleSetBean> j = ruleSets.iterator(); j.hasNext();) {
			RuleSetBean ruleSet = j.next();
			if (ruleSet.getStatus() == org.akaza.openclinica.domain.Status.AVAILABLE) {
				for (Iterator<RuleSetRuleBean> i = ruleSet.getRuleSetRules().iterator(); i.hasNext();) {
					if (i.next().getStatus() != org.akaza.openclinica.domain.Status.AVAILABLE) {
						i.remove();
					}
				}
			} else {
				j.remove();
			}
		}
		return ruleSets;
	}

	/**
	 * {@inheritDoc}
	 */
	public RuleSetBean filterByRules(RuleSetBean ruleSet, Integer ruleBeanId) {

		for (Iterator<RuleSetRuleBean> i = ruleSet.getRuleSetRules().iterator(); i.hasNext();) {
			if (!i.next().getRuleBean().getId().equals(ruleBeanId)) {
				i.remove();
			}
		}
		return ruleSet;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<RuleSetBean> filterRuleSetsByStudyEventOrdinal(List<RuleSetBean> ruleSets, StudyEventBean studyEvent,
			CRFVersionBean crfVersion, StudyEventDefinitionBean studyEventDefinition) {
		ArrayList<RuleSetBean> validRuleSets = new ArrayList<RuleSetBean>();
		for (RuleSetBean ruleSetBean : ruleSets) {
			if (!getExpressionService().isExpressionPartial(ruleSetBean.getTarget().getValue())) {
				String studyEventDefinitionOrdinal = getExpressionService().getStudyEventDefinitionOrdninalCurated(
						ruleSetBean.getTarget().getValue());
				if (studyEventDefinitionOrdinal.equals("")) {
					ruleSetBean.addExpression(replaceSEDOrdinal(ruleSetBean.getTarget(), studyEvent));
					validRuleSets.add(ruleSetBean);
				}
				String compareOrdinal = Integer.toString(studyEvent.getSampleOrdinal());
				if (studyEventDefinitionOrdinal.equals(compareOrdinal)) {
					ruleSetBean.addExpression(replaceSEDOrdinal(ruleSetBean.getTarget(), studyEvent));
					validRuleSets.add(ruleSetBean);
				}
			} else {
				String expression = getExpressionService().constructFullExpressionIfPartialProvided(
						ruleSetBean.getTarget().getValue(), crfVersion, studyEventDefinition);
				ruleSetBean.addExpression(replaceSEDOrdinal(ruleSetBean.getTarget(), studyEvent, expression));
				validRuleSets.add(ruleSetBean);

			}
		}
		logger.debug("Size of RuleSets post filterRuleSetsByStudyEventOrdinal() {} ", validRuleSets.size());
		return validRuleSets;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<RuleSetBean> filterRuleSetsByHiddenItems(List<RuleSetBean> ruleSets, EventCRFBean eventCrf,
			CRFVersionBean crfVersion, List<ItemBean> itemBeansWithSCDShown) {
		ArrayList<RuleSetBean> shownRuleSets = new ArrayList<RuleSetBean>();
		for (RuleSetBean ruleSetBean : ruleSets) {
			logMe("Entering the filterRuleSetsBy HiddenItems? Thread::" + Thread.currentThread() + "eventCrf?"
					+ eventCrf + "crfVersion??" + crfVersion + "ruleSets?" + ruleSets);
			ItemBean target = ruleSetBean.getItem();
			ItemFormMetadataBean metadataBean = this.getItemFormMetadataDao().findByItemIdAndCRFVersionId(
					target.getId(), crfVersion.getId());
			ItemDataBean itemData = this.getItemDataDao().findByItemIdAndEventCRFId(target.getId(), eventCrf.getId());
			DynamicsItemFormMetadataBean dynamicsBean = this.getDynamicsItemFormMetadataDao().findByMetadataBean(
					metadataBean, eventCrf, itemData);
			if (itemBeansWithSCDShown == null) {
				itemBeansWithSCDShown = new ArrayList<ItemBean>();
			}
			if (dynamicsBean == null) {
				if (metadataBean.isShowItem() || itemBeansWithSCDShown.contains(target)) {
					logger.debug("just added rule set bean");
					shownRuleSets.add(ruleSetBean);
				}
			} else {
				if (metadataBean.isShowItem() || dynamicsBean.isShowItem()) {
					logger.debug("just added rule set bean 2, with dyn bean");
					shownRuleSets.add(ruleSetBean);
				}
			}
		}
		return shownRuleSets;
	}

	private void logMe(String message) {
		logger.debug(message);

	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public List<RuleSetBean> filterRuleSetsByStudyEventOrdinal(List<RuleSetBean> ruleSets, String crfVersionId) {
		ArrayList<RuleSetBean> validRuleSets = new ArrayList<RuleSetBean>();
		for (RuleSetBean ruleSetBean : ruleSets) {
			String studyEventDefinitionOrdinal = getExpressionService().getStudyEventDefinitionOrdninalCurated(
					ruleSetBean.getTarget().getValue());
			String studyEventDefinitionOid = getExpressionService().getStudyEventDefenitionOid(
					ruleSetBean.getTarget().getValue());
			String crfOrCrfVersionOid = getExpressionService().getCrfOid(ruleSetBean.getTarget().getValue());
			// whole expression is provided in target
			if (studyEventDefinitionOid != null && crfOrCrfVersionOid != null) {

				List<StudyEventBean> studyEvents = getStudyEventDao().findAllByStudyEventDefinitionAndCrfOids(
						studyEventDefinitionOid, crfOrCrfVersionOid);
				logger.debug(
						"studyEventDefinitionOrdinal {} , studyEventDefinitionOid {} , crfOrCrfVersionOid {} , studyEvents {}",
						new Object[] { studyEventDefinitionOrdinal, studyEventDefinitionOid, crfOrCrfVersionOid,
								studyEvents.size() });

				if (studyEventDefinitionOrdinal.equals("") && studyEvents.size() > 0) {
					for (StudyEventBean studyEvent : studyEvents) {
						ruleSetBean.addExpression(replaceSEDOrdinal(ruleSetBean.getTarget(), studyEvent));
					}
					validRuleSets.add(ruleSetBean);
				} else {
					for (StudyEventBean studyEvent : studyEvents) {
						if (studyEventDefinitionOrdinal.equals("" + studyEvent.getSampleOrdinal())) {
							ruleSetBean.addExpression(replaceSEDOrdinal(ruleSetBean.getTarget(), studyEvent));
							validRuleSets.add(ruleSetBean);
						}
					}
				}

			} else {
				// partial expression is provided in target
				CRFBean crf;
				List<CRFVersionBean> crfVersions = new ArrayList<CRFVersionBean>();
				CRFVersionBean crfVersion;
				if (crfOrCrfVersionOid == null) {
					crf = getCrfDao().findByItemOid(
							getExpressionService().getItemOid(ruleSetBean.getTarget().getValue()));
					if (crfVersionId != null) {
						crfVersion = (CRFVersionBean) getCrfVersionDao().findByPK(Integer.valueOf(crfVersionId));
						crfVersions.add(crfVersion);
					} else {
						crfVersions = (List<CRFVersionBean>) getCrfVersionDao().findAllByCRF(crf.getId());
					}
				} else {
					crf = getExpressionService().getCRFFromExpression(ruleSetBean.getTarget().getValue());
					if (crfVersionId != null) {
						crfVersion = (CRFVersionBean) getCrfVersionDao().findByPK(Integer.valueOf(crfVersionId));
					} else {
						crfVersion = getExpressionService().getCRFVersionFromExpression(
								ruleSetBean.getTarget().getValue());
					}
					if (crfVersion != null) {
						crfVersions.add(crfVersion);
					} else {
						crfVersions = (List<CRFVersionBean>) getCrfVersionDao().findAllByCRF(crf.getId());
					}
				}
				List<StudyEventDefinitionBean> studyEventDefinitions = getStudyEventDefinitionDao().findAllByCrf(crf);
				for (StudyEventDefinitionBean studyEventDefinitionBean : studyEventDefinitions) {
					for (CRFVersionBean crfVersionBean : crfVersions) {
						String expression = getExpressionService().constructFullExpressionIfPartialProvided(
								ruleSetBean.getTarget().getValue(), crfVersionBean, studyEventDefinitionBean);
						List<StudyEventBean> studyEvents = getStudyEventDao().findAllByStudyEventDefinitionAndCrfOids(
								studyEventDefinitionBean.getOid(), crfVersionBean.getOid());
						logger.debug(
								"studyEventDefinitionOrdinal {} , studyEventDefinitionOid {} , crfOrCrfVersionOid {} , studyEvents {}",
								new Object[] { studyEventDefinitionOrdinal, studyEventDefinitionBean.getOid(),
										crfVersionBean.getOid(), studyEvents.size() });
						for (StudyEventBean studyEvent : studyEvents) {
							ruleSetBean
									.addExpression(replaceSEDOrdinal(ruleSetBean.getTarget(), studyEvent, expression));
						}
					}
				}
				validRuleSets.add(ruleSetBean);

			}
		}
		logExpressions(validRuleSets);
		logger.debug("Size of RuleSets post filterRuleSetsByStudyEventOrdinal() {} ", validRuleSets.size());
		return validRuleSets;
	}

	private void logExpressions(List<RuleSetBean> validRuleSets) {
		if (logger.isDebugEnabled()) {
			for (RuleSetBean ruleSetBean : validRuleSets) {
				logger.debug("Expression : {} ", ruleSetBean.getTarget().getValue());
				for (ExpressionBean expression : ruleSetBean.getExpressions()) {
					logger.debug("Expression post filtering SEDs : {} ", expression.getValue());
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public List<RuleSetBean> solidifyGroupOrdinalsUsingFormProperties(List<RuleSetBean> ruleSets,
			HashMap<String, Integer> grouped) {
		for (RuleSetBean ruleSet : ruleSets) {
			ArrayList<ExpressionBean> expressionsWithCorrectGroupOrdinal = new ArrayList<ExpressionBean>();
			for (ExpressionBean expression : ruleSet.getExpressions()) {
				logger.debug("solidifyGroupOrdinals: Expression Value : " + expression.getValue());
				String groupOIDConcatItemOID = getExpressionService().getGroupOidConcatWithItemOid(
						expression.getValue());
				String itemOID = getExpressionService().getItemOid(expression.getValue());
				String groupOrdinal = getExpressionService().getGroupOrdninalCurated(expression.getValue());

				if (grouped.containsKey(groupOIDConcatItemOID) && groupOrdinal.equals("")) {
					for (int i = 0; i < grouped.get(groupOIDConcatItemOID); i++) {
						ExpressionBean expBean = new ExpressionBean();
						expBean.setValue(getExpressionService().replaceGroupOidOrdinalInExpression(
								expression.getValue(), i + 1));
						expBean.setContext(expression.getContext());
						expressionsWithCorrectGroupOrdinal.add(expBean);
					}
				} else if (grouped.containsKey(groupOIDConcatItemOID) && !groupOrdinal.equals("")) {
					ExpressionBean expBean = new ExpressionBean();
					expBean.setValue(expression.getValue());
					expBean.setContext(expression.getContext());
					expressionsWithCorrectGroupOrdinal.add(expBean);
				} else if (grouped.containsKey(itemOID)) {
					ExpressionBean expBean = new ExpressionBean();
					expBean.setValue(getExpressionService().replaceGroupOidOrdinalInExpression(expression.getValue(),
							null));
					expBean.setContext(expression.getContext());
					expressionsWithCorrectGroupOrdinal.add(expBean);
				}
			}
			ruleSet.setExpressions(expressionsWithCorrectGroupOrdinal);
			for (ExpressionBean expressionBean : ruleSet.getExpressions()) {
				logger.debug("expressionBean value : {} ", expressionBean.getValue());
			}
		}
		return ruleSets;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<RuleSetBean> filterRuleSetsBySectionAndGroupOrdinal(List<RuleSetBean> ruleSets,
			HashMap<String, Integer> grouped) {
		List<RuleSetBean> ruleSetsInThisSection = new ArrayList<RuleSetBean>();
		for (RuleSetBean ruleSet : ruleSets) {
			String ruleSetTargetValue = getExpressionService().isExpressionPartial(ruleSet.getTarget().getValue()) ? ruleSet
					.getExpressions().get(0).getValue()
					: ruleSet.getTarget().getValue();
			String expWithGroup = getExpressionService().getGroupOidConcatWithItemOid(ruleSetTargetValue);
			String expWithoutGroup = getExpressionService().getItemOid(ruleSetTargetValue);
			if (grouped.containsKey(expWithGroup)) {
				String ordinal = getExpressionService().getGroupOrdninalCurated(ruleSetTargetValue);
				if (ordinal.length() == 0 || grouped.get(expWithGroup) >= Integer.valueOf(ordinal)) {
					ruleSetsInThisSection.add(ruleSet);
				}
			}
			if (grouped.containsKey(expWithoutGroup)) {
				ruleSetsInThisSection.add(ruleSet);
			}
		}
		logger.info(
				"filterRuleSetsBySectionAndGroupOrdinal : ruleSets affecting the Whole Form : {} , ruleSets affecting this Section {} ",
				ruleSets.size(), ruleSetsInThisSection.size());
		return ruleSetsInThisSection;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<RuleSetBean> filterRuleSetsByGroupOrdinal(List<RuleSetBean> ruleSets) {

		for (RuleSetBean ruleSetBean : ruleSets) {
			List<ExpressionBean> expressionsWithCorrectGroupOrdinal = new ArrayList<ExpressionBean>();
			List<ExpressionBean> ruleSetBeanExpressions = ruleSetBean.getExpressions();
			if (ruleSetBeanExpressions != null) {
				for (ExpressionBean expression : ruleSetBean.getExpressions()) {
					String studyEventId = getExpressionService().getStudyEventDefenitionOrdninalCurated(
							expression.getValue());
					String itemOid = getExpressionService().getItemOid(expression.getValue());
					String itemGroupOid = getExpressionService().getItemGroupOid(expression.getValue());
					String groupOrdinal = getExpressionService().getGroupOrdninalCurated(expression.getValue());
					List<ItemDataBean> itemDatas = getItemDataDao().findByStudyEventAndOids(
							Integer.valueOf(studyEventId), itemOid, itemGroupOid);
					logger.debug("studyEventId {} , itemOid {} , itemGroupOid {} , groupOrdinal {} , itemDatas {}",
							new Object[] { studyEventId, itemOid, itemGroupOid, groupOrdinal, itemDatas.size() });

					// case 1 : group ordinal = ""
					if (groupOrdinal.equals("") && itemDatas.size() > 0) {
						for (int k = 0; k < itemDatas.size(); k++) {
							ExpressionBean expBean = new ExpressionBean();
							expBean.setValue(getExpressionService().replaceGroupOidOrdinalInExpression(
									expression.getValue(), k + 1));
							expBean.setContext(expression.getContext());
							expressionsWithCorrectGroupOrdinal.add(expBean);
						}
					}
					// case 2 : group ordinal = x and itemDatas should be size >= x
					if (!groupOrdinal.equals("") && itemDatas.size() >= Integer.valueOf(groupOrdinal)) {
						ExpressionBean expBean = new ExpressionBean();
						expBean.setValue(getExpressionService().replaceGroupOidOrdinalInExpression(
								expression.getValue(), null));
						expBean.setContext(expression.getContext());
						expressionsWithCorrectGroupOrdinal.add(expBean);
					}
				}
			}
			ruleSetBean.setExpressions(expressionsWithCorrectGroupOrdinal);
		}
		logExpressions(ruleSets);
		return ruleSets;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<String> getGroupOrdinalPlusItemOids(List<RuleSetBean> ruleSets) {
		List<String> groupOrdinalPlusItemOid = new ArrayList<String>();
		for (RuleSetBean ruleSetBean : ruleSets) {
			String text = getExpressionService().getGroupOrdninalConcatWithItemOid(ruleSetBean.getTarget().getValue());
			groupOrdinalPlusItemOid.add(text);
			logger.debug("ruleSet id {} groupOrdinalPlusItemOid : {}", ruleSetBean.getId(), text);
		}
		return groupOrdinalPlusItemOid;
	}

	/**
	 * {@inheritDoc}
	 */
	public RuleSetBean replaceCrfOidInTargetExpression(RuleSetBean ruleSetBean, String replacementCrfOid) {
		String expression = getExpressionService().replaceCRFOidInExpression(ruleSetBean.getTarget().getValue(),
				replacementCrfOid);
		ruleSetBean.getTarget().setValue(expression);
		return ruleSetBean;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean shouldRunRulesForRuleSets(List<RuleSetBean> ruleSets, Phase phase) {
		for (RuleSetBean ruleSetBean : ruleSets) {
			List<RuleSetRuleBean> ruleSetRuleBeans = ruleSetBean.getRuleSetRules();
			for (RuleSetRuleBean ruleSetRuleBean : ruleSetRuleBeans) {
				List<RuleActionBean> ruleActionBeans = ruleSetRuleBean.getActions();
				for (RuleActionBean ruleActionBean : ruleActionBeans) {
					if (ruleActionBean.getRuleActionRun().canRun(phase)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * @return the contextPath
	 */
	public String getContextPath() {
		return contextPath;
	}

	/**
	 * @param contextPath
	 *            the contextPath to set
	 */
	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	/**
	 * @param requestURLMinusServletPath
	 *            the requestURLMinusServletPath to set
	 */
	public void setRequestURLMinusServletPath(String requestURLMinusServletPath) {
		this.requestURLMinusServletPath = requestURLMinusServletPath;
	}

	public String getRequestURLMinusServletPath() {
		return requestURLMinusServletPath;
	}

	public RuleSetDao getRuleSetDao() {
		return ruleSetDao;
	}

	public void setRuleSetDao(RuleSetDao ruleSetDao) {
		this.ruleSetDao = ruleSetDao;
	}

	public void setRuleSetRuleDao(RuleSetRuleDao ruleSetRuleDao) {
		this.ruleSetRuleDao = ruleSetRuleDao;
	}

	public RuleSetRuleDao getRuleSetRuleDao() {
		return ruleSetRuleDao;
	}

	public RuleDao getRuleDao() {
		return ruleDao;
	}

	public void setRuleDao(RuleDao ruleDao) {
		this.ruleDao = ruleDao;
	}

	private CRFDAO getCrfDao() {
		return new CRFDAO(dataSource);
	}

	private StudyEventDAO getStudyEventDao() {
		return new StudyEventDAO(dataSource);
	}

	private ItemDAO getItemDao() {
		return new ItemDAO(dataSource);
	}

	private ItemFormMetadataDAO getItemFormMetadataDao() {

		return new ItemFormMetadataDAO(dataSource);
	}

	public StudyEventDefinitionDAO getStudyEventDefinitionDao() {
		return new StudyEventDefinitionDAO(dataSource);
	}

	public StudyDAO getStudyDao() {
		return new StudyDAO(dataSource);
	}

	private ItemDataDAO getItemDataDao() {
		return new ItemDataDAO(dataSource);
	}

	private CRFVersionDAO getCrfVersionDao() {
		return new CRFVersionDAO(dataSource);
	}

	private ExpressionService getExpressionService() {
		return expressionService;
	}

	public void setExpressionService(ExpressionService expressionService) {
		this.expressionService = expressionService;
	}

	public DynamicsItemFormMetadataDao getDynamicsItemFormMetadataDao() {
		return dynamicsItemFormMetadataDao;
	}

	public void setDynamicsItemFormMetadataDao(DynamicsItemFormMetadataDao dynamicsItemFormMetadataDao) {
		this.dynamicsItemFormMetadataDao = dynamicsItemFormMetadataDao;
	}

	public RuleSetAuditDao getRuleSetAuditDao() {
		return ruleSetAuditDao;
	}

	public void setRuleSetAuditDao(RuleSetAuditDao ruleSetAuditDao) {
		this.ruleSetAuditDao = ruleSetAuditDao;
	}

	public JavaMailSenderImpl getMailSender() {
		return mailSender;
	}

	public void setMailSender(JavaMailSenderImpl mailSender) {
		this.mailSender = mailSender;
	}

	public DynamicsMetadataService getDynamicsMetadataService() {
		return dynamicsMetadataService;
	}

	public void setDynamicsMetadataService(DynamicsMetadataService dynamicsMetadataService) {
		this.dynamicsMetadataService = dynamicsMetadataService;
	}

	public RuleActionRunLogDao getRuleActionRunLogDao() {
		return ruleActionRunLogDao;
	}

	public void setRuleActionRunLogDao(RuleActionRunLogDao ruleActionRunLogDao) {
		this.ruleActionRunLogDao = ruleActionRunLogDao;
	}

	/**
	 * @return the dataSource
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
