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
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * Copyright 2003-2008 Akaza Research
 */
package org.akaza.openclinica.service.rule;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.oid.GenericOidGenerator;
import org.akaza.openclinica.bean.oid.OidGenerator;
import org.akaza.openclinica.dao.hibernate.RuleDao;
import org.akaza.openclinica.dao.hibernate.RuleSetDao;
import org.akaza.openclinica.domain.Status;
import org.akaza.openclinica.domain.rule.AuditableBeanWrapper;
import org.akaza.openclinica.domain.rule.RuleBean;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean.RuleSetRuleBeanImportStatus;
import org.akaza.openclinica.domain.rule.RulesPostImportContainer;
import org.akaza.openclinica.domain.rule.action.DiscrepancyNoteActionBean;
import org.akaza.openclinica.domain.rule.action.EmailActionBean;
import org.akaza.openclinica.domain.rule.action.HideActionBean;
import org.akaza.openclinica.domain.rule.action.InsertActionBean;
import org.akaza.openclinica.domain.rule.action.PropertyBean;
import org.akaza.openclinica.domain.rule.action.RuleActionBean;
import org.akaza.openclinica.domain.rule.action.ShowActionBean;
import org.akaza.openclinica.domain.rule.expression.Context;
import org.akaza.openclinica.domain.rule.expression.ExpressionBean;
import org.akaza.openclinica.domain.rule.expression.ExpressionObjectWrapper;
import org.akaza.openclinica.domain.rule.expression.ExpressionProcessor;
import org.akaza.openclinica.domain.rule.expression.ExpressionProcessorFactory;
import org.akaza.openclinica.service.rule.expression.ExpressionService;
import org.akaza.openclinica.validator.rule.action.InsertActionValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Errors;

import javax.sql.DataSource;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Krikor Krumlian
 * 
 */
public final class RulesPostImportContainerService {

	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());

	private ResourceBundle respage;

	private DataSource ds;
	private RuleDao ruleDao;
	private RuleSetDao ruleSetDao;

	private StudyBean currentStudy;
	private UserAccountBean userAccount;

	private final OidGenerator oidGenerator;

	private ExpressionService expressionService;
	private InsertActionValidator insertActionValidator;

	public RulesPostImportContainerService(DataSource ds, StudyBean currentStudy, UserAccountBean userAccount,
			RuleDao ruleDao, RuleSetDao ruleSetDao, ResourceBundle respage) {
		this.ds = ds;
		this.respage = respage;
		this.ruleDao = ruleDao;
		this.ruleSetDao = ruleSetDao;
		this.userAccount = userAccount;
		this.currentStudy = currentStudy;
		this.oidGenerator = new GenericOidGenerator();
		insertActionValidator = new InsertActionValidator(ds);
		expressionService = new ExpressionService(new ExpressionObjectWrapper(ds, currentStudy, null, null));
	}

	public RulesPostImportContainer validateRuleSetDefs(RulesPostImportContainer importContainer) {
		for (RuleSetBean ruleSetBean : importContainer.getRuleSets()) {
			AuditableBeanWrapper<RuleSetBean> ruleSetBeanWrapper = new AuditableBeanWrapper<RuleSetBean>(ruleSetBean);
			ruleSetBeanWrapper.getAuditableBean().setStudy(currentStudy);
			if (isRuleSetExpressionValid(ruleSetBeanWrapper)) {

				RuleSetBean persistentRuleSetBean = getRuleSetDao().findByExpressionAndStudy(ruleSetBean,
						currentStudy.getId());
				if (persistentRuleSetBean != null) {
					List<RuleSetRuleBean> importedRuleSetRules = ruleSetBeanWrapper.getAuditableBean()
							.getRuleSetRules();
					persistentRuleSetBean.setUpdaterAndDate(getUserAccount());
					ruleSetBeanWrapper.setAuditableBean(persistentRuleSetBean);
					Iterator<RuleSetRuleBean> itr = importedRuleSetRules.iterator();
					while (itr.hasNext()) {
						RuleSetRuleBean ruleSetRuleBean = itr.next();
						ruleSetRuleBean.setRuleBean(getRuleDao().findByOid(ruleSetRuleBean.getOid(),
								persistentRuleSetBean.getStudyId()));
						if (ruleSetRuleBean.getRuleBean() == null) {
							AuditableBeanWrapper<RuleBean> ruleBeanWrapper = importContainer.getValidRules().get(
									ruleSetRuleBean.getOid());
							if (ruleBeanWrapper != null) {
								ruleSetRuleBean.setRuleBean(ruleBeanWrapper.getAuditableBean());
							}
						}
						for (RuleSetRuleBean persistentruleSetRuleBean : persistentRuleSetBean.getRuleSetRules()) {
							if (persistentruleSetRuleBean.getStatus() != Status.DELETED
									&& ruleSetRuleBean.equals(persistentruleSetRuleBean)) {
								persistentruleSetRuleBean
										.setRuleSetRuleBeanImportStatus(RuleSetRuleBeanImportStatus.EXACT_DOUBLE);
								itr.remove();
								break;
							} else if (persistentruleSetRuleBean.getStatus() != Status.DELETED
									&& ruleSetRuleBean.getRuleBean() != null
									&& ruleSetRuleBean.getRuleBean().equals(persistentruleSetRuleBean.getRuleBean())) {
								persistentruleSetRuleBean
										.setRuleSetRuleBeanImportStatus(RuleSetRuleBeanImportStatus.TO_BE_REMOVED);
								break;
							}
							ruleSetRuleBean.setRuleSetRuleBeanImportStatus(RuleSetRuleBeanImportStatus.LINE);
						}
					}
					ruleSetBeanWrapper.getAuditableBean().addRuleSetRules(importedRuleSetRules);
				} else {
					if (importContainer.getValidRuleSetExpressionValues().contains(
							ruleSetBeanWrapper.getAuditableBean().getTarget().getValue())) {
						ruleSetBeanWrapper.error(createError("OCRERR_0031"));
					}
					ruleSetBeanWrapper.getAuditableBean().setOwner(getUserAccount());
					ruleSetBeanWrapper.getAuditableBean().setCrf(
							expressionService.getCRFFromExpression(ruleSetBean.getTarget().getValue()));
					ruleSetBeanWrapper.getAuditableBean().setItem(
							expressionService.getItemBeanFromExpression(ruleSetBean.getTarget().getValue()));
					ruleSetBeanWrapper.getAuditableBean().setItemGroup(
							expressionService.getItemGroupExpression(ruleSetBean.getTarget().getValue()));
					ruleSetBeanWrapper.getAuditableBean().setCrfVersion(
							expressionService.getCRFVersionFromExpression(ruleSetBean.getTarget().getValue()));
					ruleSetBeanWrapper.getAuditableBean()
							.setStudyEventDefinition(
									expressionService.getStudyEventDefinitionFromExpression(ruleSetBean.getTarget()
											.getValue()));
				}
				isRuleSetRuleValid(importContainer, ruleSetBeanWrapper);
			}
			putRuleSetInCorrectContainer(ruleSetBeanWrapper, importContainer);
		}
		logger.info("# of Valid RuleSetDefs : " + importContainer.getValidRuleSetDefs().size());
		logger.info("# of InValid RuleSetDefs : " + importContainer.getInValidRuleSetDefs().size());
		logger.info("# of Overwritable RuleSetDefs : " + importContainer.getDuplicateRuleSetDefs().size());
		return importContainer;
	}

	public RulesPostImportContainer validateRuleDefs(RulesPostImportContainer importContainer) {
		for (RuleBean ruleBean : importContainer.getRuleDefs()) {
			AuditableBeanWrapper<RuleBean> ruleBeanWrapper = new AuditableBeanWrapper<RuleBean>(ruleBean);
			ruleBeanWrapper.getAuditableBean().setStudy(currentStudy);
			// Remove illegal characters from expression value
			ruleBeanWrapper
					.getAuditableBean()
					.getExpression()
					.setValue(
							ruleBeanWrapper.getAuditableBean().getExpression().getValue().trim()
									.replaceAll("(\n|\t|\r)", " "));

			if (isRuleOidValid(ruleBeanWrapper) && isRuleExpressionValid(ruleBeanWrapper, null)) {
				RuleBean persistentRuleBean = getRuleDao().findByOid(ruleBeanWrapper.getAuditableBean());
				if (persistentRuleBean != null) {
					String name = ruleBeanWrapper.getAuditableBean().getName();
					String expressionValue = ruleBeanWrapper.getAuditableBean().getExpression().getValue();
					String expressionContextName = ruleBeanWrapper.getAuditableBean().getExpression().getContextName();
					String description = ruleBeanWrapper.getAuditableBean().getDescription();
					Context context = expressionContextName != null ? Context.getByName(expressionContextName)
							: Context.OC_RULES_V1;
					persistentRuleBean.setUpdaterAndDate(getUserAccount());
					ruleBeanWrapper.setAuditableBean(persistentRuleBean);
					ruleBeanWrapper.getAuditableBean().setName(name);
					ruleBeanWrapper.getAuditableBean().setDescription(description);
					ruleBeanWrapper.getAuditableBean().getExpression().setValue(expressionValue);
					ruleBeanWrapper.getAuditableBean().getExpression().setContext(context);
					doesPersistentRuleBeanBelongToCurrentStudy(ruleBeanWrapper);
				} else {
					ruleBeanWrapper.getAuditableBean().setOwner(getUserAccount());
				}
			}
			putRuleInCorrectContainer(ruleBeanWrapper, importContainer);
		}
		logger.info("# of Valid RuleDefs : {} , # of InValid RuleDefs : {} , # of Overwritable RuleDefs : {}",
				new Object[] { importContainer.getValidRuleDefs().size(), importContainer.getInValidRuleDefs().size(),
						importContainer.getDuplicateRuleDefs().size() });
		return importContainer;
	}

	private void putRuleSetInCorrectContainer(AuditableBeanWrapper<RuleSetBean> ruleSetBeanWrapper,
			RulesPostImportContainer importContainer) {
		if (!ruleSetBeanWrapper.isSavable()) {
			importContainer.getInValidRuleSetDefs().add(ruleSetBeanWrapper);
		} else if (expressionService
				.getEventDefinitionCRF(ruleSetBeanWrapper.getAuditableBean().getTarget().getValue()) != null
				&& expressionService
						.getEventDefinitionCRF(ruleSetBeanWrapper.getAuditableBean().getTarget().getValue())
						.getStatus().isDeleted()) {
			importContainer.getInValidRuleSetDefs().add(ruleSetBeanWrapper);
		} else if (ruleSetBeanWrapper.getAuditableBean().getId() == null) {
			importContainer.getValidRuleSetDefs().add(ruleSetBeanWrapper);
			importContainer.getValidRuleSetExpressionValues().add(
					ruleSetBeanWrapper.getAuditableBean().getTarget().getValue());
		} else if (ruleSetBeanWrapper.getAuditableBean().getId() != null) {
			importContainer.getDuplicateRuleSetDefs().add(ruleSetBeanWrapper);
		}
	}

	private void putRuleInCorrectContainer(AuditableBeanWrapper<RuleBean> ruleBeanWrapper,
			RulesPostImportContainer importContainer) {
		if (!ruleBeanWrapper.isSavable()) {
			importContainer.getInValidRuleDefs().add(ruleBeanWrapper);
			importContainer.getInValidRules().put(ruleBeanWrapper.getAuditableBean().getOid(), ruleBeanWrapper);
		} else if (ruleBeanWrapper.getAuditableBean().getId() == null) {
			importContainer.getValidRuleDefs().add(ruleBeanWrapper);
			importContainer.getValidRules().put(ruleBeanWrapper.getAuditableBean().getOid(), ruleBeanWrapper);
		} else if (ruleBeanWrapper.getAuditableBean().getId() != null) {
			importContainer.getDuplicateRuleDefs().add(ruleBeanWrapper);
			importContainer.getValidRules().put(ruleBeanWrapper.getAuditableBean().getOid(), ruleBeanWrapper);
		}
	}

	/**
	 * If the RuleSet contains any RuleSetRule object with an invalid RuleRef OID (OID that is not in DB or in the Valid
	 * Rule Lists) , Then add an error to the ruleSetBeanWrapper, which in terms will make the RuleSet inValid.
	 * 
	 * @param importContainer
	 *            RulesPostImportContainer
	 * @param ruleSetBeanWrapper
	 *            AuditableBeanWrapper<RuleSetBean>
	 */
	private void isRuleSetRuleValid(RulesPostImportContainer importContainer,
			AuditableBeanWrapper<RuleSetBean> ruleSetBeanWrapper) {
		for (RuleSetRuleBean ruleSetRuleBean : ruleSetBeanWrapper.getAuditableBean().getRuleSetRules()) {
			String ruleDefOid = ruleSetRuleBean.getOid();
			if (ruleSetRuleBean.getId() == null
					|| ruleSetRuleBean.getRuleSetRuleBeanImportStatus() == RuleSetRuleBeanImportStatus.EXACT_DOUBLE) {
				EventDefinitionCRFBean eventDefinitionCRFBean = expressionService
						.getEventDefinitionCRF(ruleSetBeanWrapper.getAuditableBean().getTarget().getValue());
				if (eventDefinitionCRFBean != null && eventDefinitionCRFBean.getStatus().isDeleted()) {
					ruleSetBeanWrapper.error(createError("OCRERR_0026"));
				}
				if (importContainer.getInValidRules().get(ruleDefOid) != null
						|| importContainer.getValidRules().get(ruleDefOid) == null
						&& getRuleDao().findByOid(ruleDefOid, ruleSetBeanWrapper.getAuditableBean().getStudyId()) == null) {
					ruleSetBeanWrapper.error(createError("OCRERR_0025"));
				}
				if (importContainer.getValidRules().get(ruleDefOid) != null) {
					AuditableBeanWrapper<RuleBean> r = importContainer.getValidRules().get(ruleDefOid);
					if (!isRuleExpressionValid(r, ruleSetBeanWrapper.getAuditableBean()))
						ruleSetBeanWrapper.error(createError("OCRERR_0027"));
				}
				if (importContainer.getValidRules().get(ruleDefOid) == null) {
					RuleBean rule = getRuleDao().findByOid(ruleDefOid,
							ruleSetBeanWrapper.getAuditableBean().getStudyId());
					AuditableBeanWrapper<RuleBean> r = new AuditableBeanWrapper<RuleBean>(rule);
					if (rule == null || !isRuleExpressionValid(r, ruleSetBeanWrapper.getAuditableBean()))
						ruleSetBeanWrapper.error(createError("OCRERR_0027"));
				}

				if (ruleSetRuleBean.getActions().size() == 0) {
					ruleSetBeanWrapper.error(createError("OCRERR_0027"));
				}

				for (RuleActionBean ruleActionBean : ruleSetRuleBean.getActions()) {
					isRuleActionValid(ruleActionBean, ruleSetBeanWrapper, eventDefinitionCRFBean);
				}

			}
		}
	}

	private void isRuleActionValid(RuleActionBean ruleActionBean, AuditableBeanWrapper<RuleSetBean> ruleSetBeanWrapper,
			EventDefinitionCRFBean eventDefinitionCRFBean) {
		if (ruleActionBean instanceof ShowActionBean) {
			List<PropertyBean> properties = ((ShowActionBean) ruleActionBean).getProperties();
			if (ruleActionBean.getRuleActionRun().getBatch()) {
				ruleSetBeanWrapper.error("ShowAction " + ruleActionBean.toString()
						+ " is not Valid. You cannot have Batch=\"true\". ");
			}
			for (PropertyBean propertyBean : properties) {
				String result = expressionService.checkValidityOfItemOrItemGroupOidInCrf(propertyBean.getOid(),
						ruleSetBeanWrapper.getAuditableBean());
				if (!result.equals("OK")) {
					ruleSetBeanWrapper.error("ShowAction OID " + result + " is not Valid. ");
				}
			}
		}
		if (ruleActionBean instanceof HideActionBean) {
			List<PropertyBean> properties = ((HideActionBean) ruleActionBean).getProperties();
			if (ruleActionBean.getRuleActionRun().getBatch()) {
				ruleSetBeanWrapper.error("HideAction " + ruleActionBean.toString()
						+ " is not Valid. You cannot have Batch=\"true\". ");
			}
			for (PropertyBean propertyBean : properties) {
				String result = expressionService.checkValidityOfItemOrItemGroupOidInCrf(propertyBean.getOid(),
						ruleSetBeanWrapper.getAuditableBean());
				if (!result.equals("OK")) {
					ruleSetBeanWrapper.error("HideAction OID " + result + " is not Valid. ");
				}
			}
		}
		if (ruleActionBean instanceof InsertActionBean) {
			if (ruleActionBean.getRuleActionRun().getBatch()) {
				ruleSetBeanWrapper.error("InsertAction " + ruleActionBean.toString() + " is not Valid. ");
			}
			DataBinder dataBinder = new DataBinder(ruleActionBean);
			Errors errors = dataBinder.getBindingResult();
			insertActionValidator.setExpressionService(expressionService);
			insertActionValidator.validate(
					new InsertActionValidator.InsertActionHolder(ruleSetBeanWrapper.getAuditableBean(),
							eventDefinitionCRFBean, ruleActionBean), errors);
			if (errors.hasErrors()) {
				ruleSetBeanWrapper.error("InsertAction is not valid: "
						+ errors.getAllErrors().get(0).getDefaultMessage());
			}
		}
		if (ruleActionBean instanceof EmailActionBean) {
			EmailActionBean emailActionBean = (EmailActionBean) ruleActionBean;
			if (emailActionBean.getMessage() != null) {
				if (emailActionBean.getMessage().length() > 2000) {
					ruleSetBeanWrapper.error("Your message cannot be more than 2000 characters.");
				}
			}
		}
		if (ruleActionBean instanceof DiscrepancyNoteActionBean) {
			DiscrepancyNoteActionBean discrepancyNoteActionBean = (DiscrepancyNoteActionBean) ruleActionBean;
			if (discrepancyNoteActionBean.getMessage() != null) {
				if (discrepancyNoteActionBean.getMessage().length() > 2000) {
					ruleSetBeanWrapper.error("Your message cannot be more than 2000 characters.");
				}
			}
		}
	}

	private String createError(String key) {
		MessageFormat mf = new MessageFormat("");
		mf.applyPattern(respage.getString(key));
		Object[] arguments = {};
		return key + ": " + mf.format(arguments);
	}

	private boolean isRuleExpressionValid(AuditableBeanWrapper<RuleBean> ruleBeanWrapper, RuleSetBean ruleSet) {
		boolean isValid = true;
		ExpressionBean expressionBean = isExpressionValid(ruleBeanWrapper.getAuditableBean().getExpression(),
				ruleBeanWrapper);
		expressionService.setExpressionWrapper(new ExpressionObjectWrapper(ds, currentStudy, expressionBean, ruleSet));
		ExpressionProcessor ep = ExpressionProcessorFactory.createExpressionProcessor(expressionService);
		ep.setRespage(respage);
		String errorString = ep.isRuleExpressionValid();
		if (errorString != null) {
			ruleBeanWrapper.error(errorString);
			isValid = false;
		}
		return isValid;
	}

	private boolean isRuleSetExpressionValid(AuditableBeanWrapper<RuleSetBean> beanWrapper) {
		boolean isValid = true;
		ExpressionBean expressionBean = isExpressionValid(beanWrapper.getAuditableBean().getTarget(), beanWrapper);
		expressionService.setExpressionWrapper(new ExpressionObjectWrapper(ds, currentStudy, expressionBean));
		ExpressionProcessor ep = ExpressionProcessorFactory.createExpressionProcessor(expressionService);
		ep.setRespage(respage);
		String errorString = ep.isRuleAssignmentExpressionValid();
		if (errorString != null) {
			beanWrapper.error(errorString);
			isValid = false;
		}
		return isValid;
	}

	private ExpressionBean isExpressionValid(ExpressionBean expressionBean, AuditableBeanWrapper<?> beanWrapper) {

		if (expressionBean.getContextName() == null && expressionBean.getContext() == null) {
			expressionBean.setContext(Context.OC_RULES_V1);
		}
		if (expressionBean.getContextName() != null && expressionBean.getContext() == null) {
			beanWrapper.warning(createError("OCRERR_0029"));
			expressionBean.setContext(Context.OC_RULES_V1);
		}
		if (expressionBean.getValue().length() > 4000) {
			beanWrapper.error(createError("OCRERR_0035"));
			expressionBean.setContext(Context.OC_RULES_V1);
		}
		return expressionBean;
	}

	private boolean isRuleOidValid(AuditableBeanWrapper<RuleBean> ruleBeanWrapper) {
		boolean isValid = true;
		try {
			oidGenerator.validate(ruleBeanWrapper.getAuditableBean().getOid());
		} catch (Exception e) {
			ruleBeanWrapper.error(createError("OCRERR_0028"));
			isValid = false;
		}
		return isValid;
	}

	private boolean doesPersistentRuleBeanBelongToCurrentStudy(AuditableBeanWrapper<RuleBean> ruleBeanWrapper) {
		boolean isValid = true;
		if (ruleBeanWrapper.getAuditableBean().getRuleSetRules().size() > 0) {
			int studyId = ruleBeanWrapper.getAuditableBean().getRuleSetRules().get(0).getRuleSetBean().getStudyId();
			if (studyId != currentStudy.getId()) {
				ruleBeanWrapper.error(createError("OCRERR_0030"));
				isValid = false;
			}
		}
		return isValid;
	}

	/**
	 * @return the ruleDao
	 */
	public RuleDao getRuleDao() {
		return ruleDao;
	}

	/**
	 * @param ruleDao
	 *            the ruleDao to set
	 */
	public void setRuleDao(RuleDao ruleDao) {
		this.ruleDao = ruleDao;
	}

	/**
	 * @return the ruleSetDao
	 */
	public RuleSetDao getRuleSetDao() {
		return ruleSetDao;
	}

	/**
	 * @param ruleSetDao
	 *            the ruleSetDao to set
	 */
	public void setRuleSetDao(RuleSetDao ruleSetDao) {
		this.ruleSetDao = ruleSetDao;
	}

	/**
	 * @return the currentStudy
	 */
	public StudyBean getCurrentStudy() {
		return currentStudy;
	}

	/**
	 * @param currentStudy
	 *            the currentStudy to set
	 */
	public void setCurrentStudy(StudyBean currentStudy) {
		this.currentStudy = currentStudy;
	}

	/**
	 * @return the respage
	 */
	public ResourceBundle getRespage() {
		return respage;
	}

	/**
	 * @param respage
	 *            ResourceBundle
	 */
	public void setRespage(ResourceBundle respage) {
		this.respage = respage;
	}

	/**
	 * @return userAccount
	 */
	public UserAccountBean getUserAccount() {
		return userAccount;
	}

	/**
	 * @param userAccount
	 *            UserAccountBean
	 */
	public void setUserAccount(UserAccountBean userAccount) {
		this.userAccount = userAccount;
	}

	public void setExpressionService(ExpressionService expressionService) {
		this.expressionService = expressionService;
	}

}
