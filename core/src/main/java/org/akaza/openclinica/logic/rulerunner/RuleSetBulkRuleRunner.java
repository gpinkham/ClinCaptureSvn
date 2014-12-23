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

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.domain.rule.RuleBean;
import org.akaza.openclinica.domain.rule.RuleSetBasedViewContainer;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.akaza.openclinica.domain.rule.action.ActionProcessor;
import org.akaza.openclinica.domain.rule.action.ActionProcessorFacade;
import org.akaza.openclinica.domain.rule.action.RuleActionBean;
import org.akaza.openclinica.domain.rule.action.RuleActionRunBean.Phase;
import org.akaza.openclinica.domain.rule.action.RuleActionRunLogBean;
import org.akaza.openclinica.domain.rule.expression.ExpressionBean;
import org.akaza.openclinica.domain.rule.expression.ExpressionObjectWrapper;
import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.akaza.openclinica.logic.expressionTree.OpenClinicaExpressionParser;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RuleSetBulkRuleRunner extends RuleRunner {

	public RuleSetBulkRuleRunner(DataSource ds, String requestURLMinusServletPath, String contextPath,
			JavaMailSenderImpl mailSender) {
		super(ds, requestURLMinusServletPath, contextPath, mailSender);
	}

	private List<RuleSetBasedViewContainer> populateForRuleSetBasedView(List<RuleSetBasedViewContainer> theList,
			RuleSetBean ruleSet, RuleBean rule, String akey, RuleActionBean ruleAction) {

		StudyEventBean studyEvent = (StudyEventBean) getStudyEventDao().findByPK(
				Integer.valueOf(getExpressionService().getStudyEventDefenitionOrdninalCurated(
						ruleSet.getTarget().getValue())));

		// for (String akey : actionsToBeExecuted.keySet()) {
		// for (RuleActionBean ruleAction : actionsToBeExecuted.get(akey)) {
		RuleSetBasedViewContainer container = new RuleSetBasedViewContainer(rule.getName(), rule.getOid(), rule
				.getExpression().getValue(), akey, ruleAction.getActionType().toString(), ruleAction.getSummary());

		if (!theList.contains(container)) {
			theList.add(container);
		}

		StudySubjectBean studySubject = (StudySubjectBean) getStudySubjectDao()
				.findByPK(studyEvent.getStudySubjectId());
		theList.get(theList.indexOf(container)).addSubject(
				studySubject.getLabel() + "," + studySubject.getCreatedDate() + ","
						+ studySubject.getStatus().getName() + "," + studySubject.getStudyName());

		return theList;

	}

	public List<RuleSetBasedViewContainer> runRulesBulkFromRuleSetScreen(List<RuleSetBean> ruleSets,
			ExecutionMode executionMode, StudyBean currentStudy, HashMap<String, String> variableAndValue,
			UserAccountBean ub) {

		if (variableAndValue == null || variableAndValue.isEmpty()) {
			logger.warn("You must be executing Rules in Batch");
			variableAndValue = new HashMap<String, String>();
		}

		List<RuleSetBasedViewContainer> ruleSetBasedView = new ArrayList<RuleSetBasedViewContainer>();
		HashMap<String, ArrayList<RuleActionContainer>> toBeExecuted = new HashMap<String, ArrayList<RuleActionContainer>>();
		for (RuleSetBean ruleSet : ruleSets) {
			String key = getExpressionService().getItemOid(ruleSet.getOriginalTarget().getValue());
			List<RuleActionContainer> allActionContainerListBasedOnRuleExecutionResult;
			if (toBeExecuted.containsKey(key)) {
				allActionContainerListBasedOnRuleExecutionResult = toBeExecuted.get(key);
			} else {
				toBeExecuted.put(key, new ArrayList<RuleActionContainer>());
				allActionContainerListBasedOnRuleExecutionResult = toBeExecuted.get(key);
			}
			ItemDataBean itemData;

			for (ExpressionBean expressionBean : ruleSet.getExpressions()) {
				ruleSet.setTarget(expressionBean);

				for (RuleSetRuleBean ruleSetRule : ruleSet.getRuleSetRules()) {
					String result;
					RuleBean rule = ruleSetRule.getRuleBean();
					dynamicsMetadataService.getExpressionService().setExpressionWrapper(
							new ExpressionObjectWrapper(ds, currentStudy, rule.getExpression(), ruleSet,
									variableAndValue));
					try {
						OpenClinicaExpressionParser oep = new OpenClinicaExpressionParser(
								dynamicsMetadataService.getExpressionService());
						result = oep.parseAndEvaluateExpression(rule.getExpression().getValue());
						itemData = getExpressionService().getItemDataBeanFromDb(ruleSet.getTarget().getValue());

						List<RuleActionBean> actionListBasedOnRuleExecutionResult = ruleSetRule.getActions(result,
								Phase.BATCH);

						if (itemData != null) {
							Iterator<RuleActionBean> itr = actionListBasedOnRuleExecutionResult.iterator();
							while (itr.hasNext()) {
								RuleActionBean ruleActionBean = itr.next();
								RuleActionRunLogBean ruleActionRunLog = new RuleActionRunLogBean(
										ruleActionBean.getActionType(), itemData, itemData.getValue(), ruleSetRule
												.getRuleBean().getOid());
								if (getRuleActionRunLogDao().findCountByRuleActionRunLogBean(ruleActionRunLog) > 0) {
									itr.remove();
								}
							}
						}
						for (RuleActionBean ruleActionBean : actionListBasedOnRuleExecutionResult) {
							RuleActionContainer ruleActionContainer = new RuleActionContainer(ruleActionBean,
									expressionBean, itemData, ruleSet);
							allActionContainerListBasedOnRuleExecutionResult.add(ruleActionContainer);
						}
						logger.info(
								"RuleSet with target  : {} , Ran Rule : {}  The Result was : {} , Based on that {} action will be executed in {} mode. ",
								new Object[] { ruleSet.getTarget().getValue(), rule.getName(), result,
										actionListBasedOnRuleExecutionResult.size(), executionMode.name() });
					} catch (OpenClinicaSystemException osa) {
						// TODO: report something useful
					}
				}
			}
		}

		for (Map.Entry<String, ArrayList<RuleActionContainer>> entry : toBeExecuted.entrySet()) {
			// Sort the list of actions
			Collections.sort(entry.getValue(), new RuleActionContainerComparator());

			for (RuleActionContainer ruleActionContainer : entry.getValue()) {
				ruleActionContainer.getRuleSetBean().setTarget(ruleActionContainer.getExpressionBean());
				ruleActionContainer.getRuleAction().setCuratedMessage(
						curateMessage(ruleActionContainer.getRuleAction(), ruleActionContainer.getRuleAction()
								.getRuleSetRule()));
				ActionProcessor ap = ActionProcessorFacade.getActionProcessor(ruleActionContainer.getRuleAction()
						.getActionType(), ds, getMailSender(), dynamicsMetadataService, ruleActionContainer
						.getRuleSetBean(), getRuleActionRunLogDao(), ruleActionContainer.getRuleAction()
						.getRuleSetRule());
				RuleActionBean rab = ap.execute(
						RuleRunnerMode.RULSET_BULK,
						executionMode,
						ruleActionContainer.getRuleAction(),
						ruleActionContainer.getItemDataBean(),
						DiscrepancyNoteBean.ITEM_DATA,
						currentStudy,
						ub,
						prepareEmailContents(ruleActionContainer.getRuleSetBean(), ruleActionContainer.getRuleAction()
								.getRuleSetRule(), currentStudy, ruleActionContainer.getRuleAction()));
				if (rab != null) {
					ruleSetBasedView = populateForRuleSetBasedView(ruleSetBasedView,
							ruleActionContainer.getRuleSetBean(), ruleActionContainer.getRuleAction().getRuleSetRule()
									.getRuleBean(), ruleActionContainer.getRuleAction().getExpressionEvaluatesTo()
									.toString(), ruleActionContainer.getRuleAction());
				}
			}

		}
		return ruleSetBasedView;
	}

}
