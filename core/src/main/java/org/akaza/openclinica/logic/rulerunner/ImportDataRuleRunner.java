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

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.domain.rule.RuleBean;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.akaza.openclinica.domain.rule.action.ActionProcessor;
import org.akaza.openclinica.domain.rule.action.ActionProcessorFacade;
import org.akaza.openclinica.domain.rule.action.EmailActionBean;
import org.akaza.openclinica.domain.rule.action.RuleActionBean;
import org.akaza.openclinica.domain.rule.action.RuleActionRunBean.Phase;
import org.akaza.openclinica.domain.rule.action.RuleActionRunLogBean;
import org.akaza.openclinica.domain.rule.action.ShowActionBean;
import org.akaza.openclinica.domain.rule.expression.ExpressionBean;
import org.akaza.openclinica.domain.rule.expression.ExpressionObjectWrapper;
import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.logic.expressionTree.OpenClinicaExpressionParser;
import org.akaza.openclinica.logic.rulerunner.MessageContainer.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.annotation.Transactional;

/**
 * ImportDataRuleRunner.
 */
public class ImportDataRuleRunner extends RuleRunner {

	private final Logger logger = LoggerFactory.getLogger(ImportDataRuleRunner.class);

	private ResourceBundle resexception;

	/**
	 * ImportDataRuleRunner constructor.
	 * 
	 * @param ds
	 *            DataSource
	 * @param requestURLMinusServletPath
	 *            String
	 * @param contextPath
	 *            String
	 * @param mailSender
	 *            JavaMailSenderImpl
	 */
	public ImportDataRuleRunner(DataSource ds, String requestURLMinusServletPath, String contextPath,
			JavaMailSenderImpl mailSender) {
		super(ds, requestURLMinusServletPath, contextPath, mailSender);
		resexception = ResourceBundleProvider.getExceptionsBundle(ResourceBundleProvider.getLocale());
	}

	/**
	 * @param optimiseRuleValidator
	 *            Boolean
	 * @param connection
	 *            Connection
	 * @param containers
	 *            List<ImportDataRuleRunnerContainer>
	 * @param skippedItemIds
	 *            Set<Integer>
	 * @param study
	 *            StudyBean
	 * @param ub
	 *            UserAccountBean
	 * @param executionMode
	 *            ExecutionMode
	 * @return Returned RuleActionBean summary with key as groupOrdinalPLusItemOid.
	 */
	@Transactional
	public HashMap<String, ArrayList<String>> runRules(Boolean optimiseRuleValidator, Connection connection,
			List<ImportDataRuleRunnerContainer> containers, Set<Integer> skippedItemIds, StudyBean study,
			UserAccountBean ub, ExecutionMode executionMode) {
		HashMap<String, ArrayList<String>> messageMap = new HashMap<String, ArrayList<String>>();

		if (executionMode == ExecutionMode.DRY_RUN) {
			for (ImportDataRuleRunnerContainer container : containers) {
				if (container.getShouldRunRules()) {
					container.setRuleActionContainerMap(this.populateToBeExpected(optimiseRuleValidator, container,
							study));
				}
			}
		} else if (executionMode == ExecutionMode.SAVE) {
			for (ImportDataRuleRunnerContainer container : containers) {
				MessageContainer messageContainer = this.runRules(connection, study, ub, skippedItemIds,
						container.getRuleActionContainerMap());
				messageMap.putAll(messageContainer.getByMessageType(MessageType.ERROR));
			}
		}
		return messageMap;
	}

	@Transactional
	private HashMap<String, ArrayList<RuleActionContainer>> populateToBeExpected(Boolean optimiseRuleValidator,
			ImportDataRuleRunnerContainer container, StudyBean study) {
		// copied code for toBeExpected from DataEntryServlet runRules
		HashMap<String, ArrayList<RuleActionContainer>> toBeExecuted = new HashMap<String, ArrayList<RuleActionContainer>>();
		HashMap<String, String> variableAndValue = (HashMap<String, String>) container.getVariableAndValue();

		if (variableAndValue == null || variableAndValue.isEmpty()) {
			logger.warn("No rule target item with value found.");
			return toBeExecuted;
		}

		List<RuleSetBean> ruleSets = container.getImportDataTrueRuleSets();
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
					getDynamicsMetadataService().getExpressionService().setExpressionWrapper(
							new ExpressionObjectWrapper(getDataSource(), study, rule.getExpression(), ruleSet,
									variableAndValue));
					try {
						OpenClinicaExpressionParser oep = new OpenClinicaExpressionParser(getDynamicsMetadataService()
								.getExpressionService());
						List<String> expressions = getExpressionService().prepareRuleExpression(
								rule.getExpression().getValue(), ruleSet);
						for (String expression : expressions) {
							result = oep.parseAndEvaluateExpression(expression, optimiseRuleValidator);
							itemData = getExpressionService().getItemDataBeanFromDb(ruleSet.getTarget().getValue());
							List<RuleActionBean> actionListBasedOnRuleExecutionResult = ruleSetRule.getActions(result,
									Phase.IMPORT);
							if (itemData != null) {
								Iterator<RuleActionBean> itr = actionListBasedOnRuleExecutionResult.iterator();
								while (itr.hasNext()) {
									RuleActionBean ruleActionBean = itr.next();
									String itemDataValueFromImport;
									if (variableAndValue.containsKey(key)) {
										itemDataValueFromImport = variableAndValue.get(key);
									} else {
										logger.info("Cannot find value from variableAndValue for item=" + key + ". "
												+ "Used itemData.getValue()");
										itemDataValueFromImport = itemData.getValue();
									}
									RuleActionRunLogBean ruleActionRunLog = new RuleActionRunLogBean(
											ruleActionBean.getActionType(), itemData, itemDataValueFromImport,
											ruleSetRule.getRuleBean().getOid());
									if (getRuleActionRunLogDao().findCountByRuleActionRunLogBean(ruleActionRunLog) > 0) {
										itr.remove();
									}
								}
							}
							for (RuleActionBean ruleActionBean : actionListBasedOnRuleExecutionResult) {
								RuleActionContainer ruleActionContainer = new RuleActionContainer(ruleActionBean,
										expressionBean, itemData, ruleSet);
								if (!ruleActionContainerAlreadyExistsInList(ruleActionContainer,
										allActionContainerListBasedOnRuleExecutionResult)) {
									allActionContainerListBasedOnRuleExecutionResult.add(ruleActionContainer);
								}
							}
							logger.info(
									"RuleSet with target  : {} , Ran Rule : {}  The Result was : {} , Based on that {} action will be executed. ",
									ruleSet.getTarget().getValue(), rule.getName(), result,
									actionListBasedOnRuleExecutionResult.size());
						}
					} catch (OpenClinicaSystemException osa) {
						logger.error(osa.getMessage());
					}
				}
			}
		}
		return toBeExecuted;
	}

	@Transactional
	private MessageContainer runRules(Connection connection, StudyBean currentStudy, UserAccountBean ub,
			Set<Integer> skippedItemIds, HashMap<String, ArrayList<RuleActionContainer>> toBeExecuted) {
		// Copied from DataEntryRuleRunner runRules
		MessageContainer messageContainer = new MessageContainer();
		for (Map.Entry<String, ArrayList<RuleActionContainer>> entry : toBeExecuted.entrySet()) {
			// Sort the list of actions
			Collections.sort(entry.getValue(), new RuleActionContainerComparator());

			for (RuleActionContainer ruleActionContainer : entry.getValue()) {
				logger.info("START Expression is : {} , RuleAction : {} ", new Object[]{
						ruleActionContainer.getExpressionBean().getValue(),
						ruleActionContainer.getRuleAction().toString()});

				ruleActionContainer.getRuleSetBean().setTarget(ruleActionContainer.getExpressionBean());
				ruleActionContainer.getRuleAction().setCuratedMessage(
						curateMessage(ruleActionContainer.getRuleAction(), ruleActionContainer.getRuleAction()
								.getRuleSetRule()));
				ActionProcessor ap = ActionProcessorFacade.getActionProcessor(ruleActionContainer.getRuleAction()
						.getActionType(), connection, getDataSource(), getMailSender(), getDynamicsMetadataService(),
						ruleActionContainer.getRuleSetBean(), getRuleActionRunLogDao(), ruleActionContainer
								.getRuleAction().getRuleSetRule());

				ItemDataBean itemData = getExpressionService().getItemDataBeanFromDb(
						ruleActionContainer.getRuleSetBean().getTarget().getValue());

				if (itemData != null && skippedItemIds.contains(itemData.getItemId())) {
					continue;
				}

				if (ruleActionContainer.getRuleAction() instanceof EmailActionBean) {
					((EmailActionBean) ruleActionContainer.getRuleAction()).setExceptionMessage(resexception
							.getString("email_action_processor_exception"));
				}

				RuleActionBean rab = ap.execute(
						RuleRunnerMode.IMPORT_DATA,
						ExecutionMode.SAVE,
						ruleActionContainer.getRuleAction(),
						itemData,
						DiscrepancyNoteBean.ITEM_DATA,
						currentStudy,
						ub,
						prepareEmailContents(ruleActionContainer.getRuleSetBean(), currentStudy, ruleActionContainer.getRuleAction()));

				if (rab != null) {
					if (rab instanceof ShowActionBean) {
						messageContainer.add(
								getExpressionService().getGroupOidOrdinal(
										ruleActionContainer.getRuleSetBean().getTarget().getValue()), rab);
					} else {
						messageContainer.add(
								getExpressionService().getGroupOrdninalConcatWithItemOid(
										ruleActionContainer.getRuleSetBean().getTarget().getValue()),
								ruleActionContainer.getRuleAction());
					}
				}
				logger.info("END Expression is : {} , RuleAction : {} ", new Object[]{
						ruleActionContainer.getExpressionBean().getValue(),
						ruleActionContainer.getRuleAction().toString()});
			}
		}
		return messageContainer;
	}

}
