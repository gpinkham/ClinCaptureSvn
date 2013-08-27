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

package org.akaza.openclinica.logic.rulerunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.domain.rule.RuleBean;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.akaza.openclinica.domain.rule.action.ActionProcessor;
import org.akaza.openclinica.domain.rule.action.ActionProcessorFacade;
import org.akaza.openclinica.domain.rule.action.ActionType;
import org.akaza.openclinica.domain.rule.action.DiscrepancyNoteActionBean;
import org.akaza.openclinica.domain.rule.action.RuleActionBean;
import org.akaza.openclinica.domain.rule.action.RuleActionRunBean;
import org.akaza.openclinica.domain.rule.action.RuleActionRunBean.Phase;
import org.akaza.openclinica.domain.rule.action.RuleActionRunLogBean;
import org.akaza.openclinica.domain.rule.action.ShowActionBean;
import org.akaza.openclinica.domain.rule.expression.ExpressionBean;
import org.akaza.openclinica.domain.rule.expression.ExpressionObjectWrapper;
import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.logic.expressionTree.OpenClinicaExpressionParser;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@SuppressWarnings({ "unchecked" })
public class DataEntryRuleRunner extends RuleRunner {

	private Phase phase;
	private EventCRFBean ecb;
	private StudyBean currentStudy;
	private ResourceBundle respagemsgs;
	private HttpServletRequest request;
	private ExecutionMode executionMode;
	private List<RuleActionContainer> allActionContainerListBasedOnRuleExecutionResult;

	public DataEntryRuleRunner(DataSource ds,
			String requestURLMinusServletPath, String contextPath,
			JavaMailSenderImpl mailSender, EventCRFBean ecb) {
		super(ds, requestURLMinusServletPath, contextPath, mailSender);
		this.ecb = ecb;
		respagemsgs = ResourceBundleProvider
				.getPageMessagesBundle(ResourceBundleProvider.getLocale());
	}

	public MessageContainer runRules(UserAccountBean ub,
			List<RuleSetBean> ruleSets, HashMap<String, String> variableAndValue) {
		String currentCrfVersionOid = (String) request.getAttribute("dataEntryCurrentCrfVersionOid");
		String currentCrfOid = (String) request.getAttribute("dataEntryCurrentCrfOid");
		allActionContainerListBasedOnRuleExecutionResult = null;
		if (variableAndValue == null || variableAndValue.isEmpty()) {
			logger.warn("You must be executing Rules in Batch");
			variableAndValue = new HashMap<String, String>();
		}

		MessageContainer messageContainer = new MessageContainer();
		HashMap<String, ArrayList<RuleActionContainer>> toBeExecuted = new HashMap<String, ArrayList<RuleActionContainer>>();
		switch (executionMode) {
		case SAVE: {
			toBeExecuted = (HashMap<String, ArrayList<RuleActionContainer>>) request
					.getAttribute("toBeExecuted");

			if (request.getAttribute("insertAction") == null) // Break only if
																// the action is
																// insertAction;
			{
				break;
			} else {
				toBeExecuted = new HashMap<String, ArrayList<RuleActionContainer>>();
			}

		}
		case DRY_RUN: {
			for (RuleSetBean ruleSet : ruleSets) {
				String key = getExpressionService().getItemOid(
						ruleSet.getOriginalTarget().getValue());
				if (toBeExecuted.containsKey(key)) {
					allActionContainerListBasedOnRuleExecutionResult = toBeExecuted
							.get(key);
				} else {
					toBeExecuted.put(key, new ArrayList<RuleActionContainer>());
					allActionContainerListBasedOnRuleExecutionResult = toBeExecuted
							.get(key);
				}
				for (ExpressionBean expressionBean : ruleSet.getExpressions()) {
					ruleSet.setTarget(expressionBean);
					String ruleSetForCrfVersionOid = getExpressionService().getCrfOid(ruleSet.getTarget().getValue());
					if (!ruleSetForCrfVersionOid.equals(currentCrfVersionOid)
							&& !ruleSetForCrfVersionOid.equals(currentCrfOid)) {
						continue;
					}             
                    for (RuleSetRuleBean ruleSetRule : ruleSet
							.getRuleSetRules()) {
						RuleBean rule = ruleSetRule.getRuleBean();
						ExpressionObjectWrapper eow = new ExpressionObjectWrapper(
								ds, currentStudy, rule.getExpression(),
								ruleSet, variableAndValue, ecb);
						try {
							OpenClinicaExpressionParser oep = new OpenClinicaExpressionParser(
									currentStudy, request, eow);
							String result = oep.parseAndEvaluateExpression(rule
									.getExpression().getValue());
							ItemDataBean itemData = getExpressionService()
									.getItemDataBeanFromDb(
											ruleSet.getTarget().getValue());
							List<RuleActionBean> actionListBasedOnRuleExecutionResult = ruleSetRule
									.getActions(result, phase);

							if (itemData != null) {
								Iterator<RuleActionBean> itr = actionListBasedOnRuleExecutionResult
										.iterator();
								while (itr.hasNext()) {
									if (doesTheRuleActionRunLogExist(itemData,
											ruleSetRule, ruleSet, itr.next(),
											variableAndValue)) {
										itr.remove();
									}
								}
							}

							for (RuleActionBean ruleActionBean : actionListBasedOnRuleExecutionResult) {
								RuleActionContainer ruleActionContainer = new RuleActionContainer(
										ruleActionBean, expressionBean,
										itemData, ruleSet);
								allActionContainerListBasedOnRuleExecutionResult
										.add(ruleActionContainer);
							}
							logger.info(
									"RuleSet with target  : {} , Ran Rule : {}  The Result was : {} , Based on that {} action will be executed in {} mode. ",
									new Object[] {
											ruleSet.getTarget().getValue(),
											rule.getName(),
											result,
											actionListBasedOnRuleExecutionResult
													.size(),
											executionMode.name() });
						} catch (OpenClinicaSystemException ex) {
							String code = ex.getErrorCode();
							if (code != null
									&& (code.equalsIgnoreCase("OCRERR_DATE_SHOULD_BE_ENTERED")
											|| code.equalsIgnoreCase("OCRERR_CANT_GET_SUBJEC_DOB") || code
												.equalsIgnoreCase("OCRERR_CANT_GET_SUBJECT_ENROLLMENT"))) {
								showCustomErrors(ruleSetRule, ruleSet,
										variableAndValue,
										respagemsgs.getString(code));
							}
						} catch (NullPointerException npe) {
							logger.info("found NPE while running rules, possible empty execution Mode");
							logger.info("rule set target value " + ruleSet.getTarget().getValue() + " rule name " + rule.getName());
						}
					}
				}
			}
			request.setAttribute("toBeExecuted", toBeExecuted);
			break;
		}

		}
		for (Map.Entry<String, ArrayList<RuleActionContainer>> entry : toBeExecuted
				.entrySet()) {
			// Sort the list of actions
			Collections.sort(entry.getValue(),
					new RuleActionContainerComparator());

			for (RuleActionContainer ruleActionContainer : entry.getValue()) {
				logger.info(
						"START Expression is : {} , RuleAction : {} , ExecutionMode : {} ",
						new Object[] {
								ruleActionContainer.getExpressionBean()
										.getValue(),
								ruleActionContainer.getRuleAction().toString(),
								executionMode });

				ruleActionContainer.getRuleSetBean().setTarget(
						ruleActionContainer.getExpressionBean());
				ruleActionContainer.getRuleAction().setCuratedMessage(
						curateMessage(ruleActionContainer.getRuleAction(),
								ruleActionContainer.getRuleAction()
										.getRuleSetRule()));
				ActionProcessor ap = ActionProcessorFacade.getActionProcessor(
						ruleActionContainer.getRuleAction().getActionType(),
						ds, getMailSender(), dynamicsMetadataService,
						ruleActionContainer.getRuleSetBean(),
						getRuleActionRunLogDao(), ruleActionContainer
								.getRuleAction().getRuleSetRule());

				ItemDataBean itemData = getExpressionService()
						.getItemDataBeanFromDb(
								ruleActionContainer.getRuleSetBean()
										.getTarget().getValue());

				RuleActionBean rab = ap.execute(
						RuleRunnerMode.DATA_ENTRY,
						executionMode,
						ruleActionContainer.getRuleAction(),
						itemData,
						DiscrepancyNoteBean.ITEM_DATA,
						currentStudy,
						ub,
						prepareEmailContents(ruleActionContainer
								.getRuleSetBean(), ruleActionContainer
								.getRuleAction().getRuleSetRule(),
								currentStudy, ruleActionContainer
										.getRuleAction()));
				if (rab != null) {
					if (rab instanceof ShowActionBean) {
						messageContainer.add(
								getExpressionService().getGroupOidOrdinal(
										ruleActionContainer.getRuleSetBean()
												.getTarget().getValue()), rab);
					} else {
						messageContainer
								.add(getExpressionService()
										.getGroupOrdninalConcatWithItemOid(
												ruleActionContainer
														.getRuleSetBean()
														.getTarget().getValue()),
										ruleActionContainer.getRuleAction());
					}
				}
				logger.info(
						"END Expression is : {} , RuleAction : {} , ExecutionMode : {} ",
						new Object[] {
								ruleActionContainer.getExpressionBean()
										.getValue(),
								ruleActionContainer.getRuleAction().toString(),
								executionMode });
			}
		}
		return messageContainer;
	}

	private boolean doesTheRuleActionRunLogExist(ItemDataBean itemData,
			RuleSetRuleBean ruleSetRule, RuleSetBean ruleSet,
			RuleActionBean ruleActionBean,
			HashMap<String, String> variableAndValue) {
		boolean exists = false;
		String firstDDE = "firstDDEInsert_" + ruleSetRule.getOid() + "_"
				+ itemData.getId();
		if (ruleActionBean.getActionType() == ActionType.INSERT) {
			request.setAttribute("insertAction", true);
			if (phase == RuleActionRunBean.Phase.DOUBLE_DATA_ENTRY
					&& itemData.getStatus().getId() == 4
					&& request.getAttribute(firstDDE) == null) {
				request.setAttribute(firstDDE, true);
			}
		}
		if (request.getAttribute(firstDDE) == Boolean.TRUE) {
		} else {
			String key = getExpressionService().getItemOid(
					ruleSet.getOriginalTarget().getValue());
			String itemDataValueFromForm = "";
			if (variableAndValue.containsKey(key)) {
				itemDataValueFromForm = variableAndValue.get(key);
			} else {
				itemDataValueFromForm = itemData.getValue();
			}
			RuleActionRunLogBean ruleActionRunLog = new RuleActionRunLogBean(
					ruleActionBean.getActionType(), itemData,
					itemDataValueFromForm, ruleSetRule.getRuleBean().getOid());
			exists = getRuleActionRunLogDao().findCountByRuleActionRunLogBean(
					ruleActionRunLog) > 0;
		}
		return exists;
	}

	private void showCustomErrors(RuleSetRuleBean ruleSetRule,
			RuleSetBean ruleSet, HashMap<String, String> variableAndValue,
			String customMessage) {
		for (RuleActionBean ruleActionBean : ruleSetRule.getActions()) {
			if (ruleActionBean.getRuleSetRule() == ruleSetRule
					&& ruleActionBean.getRuleActionRun().canRun(phase)
					&& ruleActionBean instanceof DiscrepancyNoteActionBean) {
				ItemDataBean itemData = getExpressionService()
						.getItemDataBeanFromDb(ruleSet.getTarget().getValue());
				if (itemData == null
						|| !doesTheRuleActionRunLogExist(itemData, ruleSetRule,
								ruleSet, ruleActionBean, variableAndValue)) {
					((DiscrepancyNoteActionBean) ruleActionBean)
							.setCustomMessage(customMessage);
					RuleActionContainer ruleActionContainer = new RuleActionContainer(
							ruleActionBean, ruleSet.getTarget(), itemData,
							ruleSet);
					allActionContainerListBasedOnRuleExecutionResult
							.add(ruleActionContainer);
				}
			}
		}
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public void setPhase(Phase phase) {
		this.phase = phase;
	}

	public void setCurrentStudy(StudyBean currentStudy) {
		this.currentStudy = currentStudy;
	}

	public void setExecutionMode(ExecutionMode executionMode) {
		this.executionMode = executionMode;
	}
}
