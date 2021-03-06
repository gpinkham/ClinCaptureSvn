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

import javax.sql.DataSource;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.rulerunner.DataEntryRuleRunnerParameter;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.domain.rule.RuleBean;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.akaza.openclinica.domain.rule.action.ActionProcessor;
import org.akaza.openclinica.domain.rule.action.ActionProcessorFacade;
import org.akaza.openclinica.domain.rule.action.ActionType;
import org.akaza.openclinica.domain.rule.action.DiscrepancyNoteActionBean;
import org.akaza.openclinica.domain.rule.action.EmailActionBean;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Handles rule execution during data entry.
 */
@SuppressWarnings({"unchecked", "unused"})
public class DataEntryRuleRunner extends RuleRunner {

	private final Logger logger = LoggerFactory.getLogger(DataEntryRuleRunner.class);
	
	private Phase phase;
	private EventCRFBean ecb;
	private StudyBean currentStudy;
	private ResourceBundle respagemsgs;
	private ResourceBundle resexception;
	private ExecutionMode executionMode;
	private List<RuleActionContainer> allActionContainerListBasedOnRuleExecutionResult;

	/**
	 * Constructs the DataEntryRuleRunner object.
	 * 
	 * @param ds
	 *            DataSource to be used.
	 * @param requestURLMinusServletPath
	 *            Request url to be used.
	 * @param contextPath
	 *            Context path to be used
	 * @param mailSender
	 *            JavaMailSenderImpl to be used.
	 * @param ecb
	 *            EventCRFBean to be used.
	 */
	public DataEntryRuleRunner(DataSource ds, String requestURLMinusServletPath, String contextPath,
			JavaMailSenderImpl mailSender, EventCRFBean ecb) {
		super(ds, requestURLMinusServletPath, contextPath, mailSender);
		this.ecb = ecb;
		respagemsgs = ResourceBundleProvider.getPageMessagesBundle(ResourceBundleProvider.getLocale());
		resexception = ResourceBundleProvider.getExceptionsBundle(ResourceBundleProvider.getLocale());
	}

	/**
	 * Executes appropriate rules.
	 * 
	 * @param ub
	 *            UserAccountBean to be used.
	 * @param ruleSets
	 *            List of RuleSetBeans to be used.
	 * @param variableAndValue
	 *            Variable and value map to be used.
	 * @param dataEntryRuleRunnerParameter
	 *            DataEntryRuleRunnerParameter
	 * @return MessageContainer object
	 */
	public MessageContainer runRules(UserAccountBean ub, List<RuleSetBean> ruleSets,
			HashMap<String, String> variableAndValue, DataEntryRuleRunnerParameter dataEntryRuleRunnerParameter) {
		allActionContainerListBasedOnRuleExecutionResult = null;
		if (variableAndValue == null || variableAndValue.isEmpty()) {
			logger.warn("You must be executing Rules in Batch");
			variableAndValue = new HashMap<String, String>();
		}

		MessageContainer messageContainer = new MessageContainer();
		Map<String, ArrayList<RuleActionContainer>> toBeExecuted = new HashMap<String, ArrayList<RuleActionContainer>>();
		switch (executionMode) {
			case SAVE :
				toBeExecuted = (Map<String, ArrayList<RuleActionContainer>>) dataEntryRuleRunnerParameter
						.getAttributes().get("toBeExecuted");
				if (dataEntryRuleRunnerParameter.getAttributes().get("insertAction") == null) {
					break;
				} else {
					toBeExecuted = new HashMap<String, ArrayList<RuleActionContainer>>();
				}
			case DRY_RUN :
				performDryRun(ruleSets, variableAndValue, dataEntryRuleRunnerParameter, toBeExecuted);
				break;
			default :
				break;

		}
		for (Map.Entry<String, ArrayList<RuleActionContainer>> entry : toBeExecuted.entrySet()) {
			// Sort the list of actions
			Collections.sort(entry.getValue(), new RuleActionContainerComparator());

			for (RuleActionContainer ruleActionContainer : entry.getValue()) {
				logger.info("START Expression is : {} , RuleAction : {} , ExecutionMode : {} ", ruleActionContainer
						.getExpressionBean().getValue(), ruleActionContainer.getRuleAction().toString(), executionMode);

				ruleActionContainer.getRuleSetBean().setTarget(ruleActionContainer.getExpressionBean());
				ruleActionContainer.getRuleAction().setCuratedMessage(
						curateMessage(ruleActionContainer.getRuleAction(), ruleActionContainer.getRuleAction()
								.getRuleSetRule()));
				ActionProcessor ap = ActionProcessorFacade.getActionProcessor(ruleActionContainer.getRuleAction()
						.getActionType(), getDataSource(), getMailSender(), getDynamicsMetadataService(),
						ruleActionContainer.getRuleSetBean(), getRuleActionRunLogDao(), ruleActionContainer
								.getRuleAction().getRuleSetRule());

				ItemDataBean itemData = getDynamicsMetadataService().getExpressionService().getItemDataBeanFromDb(
						ruleActionContainer.getRuleSetBean().getTarget().getValue());

				if (ruleActionContainer.getRuleAction() instanceof EmailActionBean) {
					((EmailActionBean) ruleActionContainer.getRuleAction()).setExceptionMessage(resexception
							.getString("email_action_processor_exception"));
				}

				RuleActionBean rab = ap.execute(
						RuleRunnerMode.DATA_ENTRY,
						executionMode,
						ruleActionContainer.getRuleAction(),
						itemData,
						DiscrepancyNoteBean.ITEM_DATA,
						currentStudy,
						ub,
						shouldCreateDNForItem(itemData, dataEntryRuleRunnerParameter.getTransformedDNs()),
						prepareEmailContents(ruleActionContainer.getRuleSetBean(), currentStudy, ruleActionContainer.getRuleAction()));
				if (rab != null) {
					if (rab instanceof ShowActionBean) {
						messageContainer.add(
								getDynamicsMetadataService().getExpressionService().getGroupOidOrdinal(
										ruleActionContainer.getRuleSetBean().getTarget().getValue()), rab);
					} else {
						messageContainer.add(
								getDynamicsMetadataService().getExpressionService().getGroupOrdninalConcatWithItemOid(
										ruleActionContainer.getRuleSetBean().getTarget().getValue()),
								ruleActionContainer.getRuleAction());
					}
				}
				logger.info("END Expression is : {} , RuleAction : {} , ExecutionMode : {} ", ruleActionContainer
						.getExpressionBean().getValue(), ruleActionContainer.getRuleAction().toString(), executionMode);
			}
		}
		return messageContainer;
	}

	private void performDryRun(List<RuleSetBean> ruleSets, HashMap<String, String> variableAndValue,
			DataEntryRuleRunnerParameter dataEntryRuleRunnerParameter,
			Map<String, ArrayList<RuleActionContainer>> toBeExecuted) {
		for (RuleSetBean ruleSet : ruleSets) {
			String key = getDynamicsMetadataService().getExpressionService().getItemOid(
					ruleSet.getOriginalTarget().getValue());
			if (toBeExecuted.containsKey(key)) {
				allActionContainerListBasedOnRuleExecutionResult = toBeExecuted.get(key);
			} else {
				toBeExecuted.put(key, new ArrayList<RuleActionContainer>());
				allActionContainerListBasedOnRuleExecutionResult = toBeExecuted.get(key);
			}
			for (ExpressionBean expressionBean : ruleSet.getExpressions()) {
				ruleSet.setTarget(expressionBean);
				String ruleSetForCrfVersionOid = getDynamicsMetadataService().getExpressionService().getCrfOid(
						ruleSet.getTarget().getValue());
				if (!ruleSetForCrfVersionOid.equals(dataEntryRuleRunnerParameter.getCurrentCrfVersionOid())
						&& !ruleSetForCrfVersionOid.equals(dataEntryRuleRunnerParameter.getCurrentCrfOid())) {
					continue;
				}
				for (RuleSetRuleBean ruleSetRule : ruleSet.getRuleSetRules()) {
					RuleBean rule = ruleSetRule.getRuleBean();
					getDynamicsMetadataService().getExpressionService().setExpressionWrapper(
							new ExpressionObjectWrapper(getDataSource(), currentStudy, rule.getExpression(), ruleSet,
									variableAndValue, ecb));
					try {
						OpenClinicaExpressionParser oep = new OpenClinicaExpressionParser(currentStudy, dataEntryRuleRunnerParameter.getSubjectBean(),
								dataEntryRuleRunnerParameter.getStudySubjectBean(), getDynamicsMetadataService().getExpressionService(),
								getTargetTimeZone());
						List<String> expressions = getDynamicsMetadataService().getExpressionService()
								.prepareRuleExpression(rule.getExpression().getValue(), ruleSet);
						for (String expression : expressions) {
							String result = oep.parseAndEvaluateExpression(expression);
							ItemDataBean itemData = getDynamicsMetadataService().getExpressionService()
									.getItemDataBeanFromDb(ruleSet.getTarget().getValue());
							List<RuleActionBean> actionListBasedOnRuleExecutionResult = ruleSetRule.getActions(result,
									phase);

							if (itemData != null) {
								Iterator<RuleActionBean> itr = actionListBasedOnRuleExecutionResult.iterator();
								while (itr.hasNext()) {
									if (doesTheRuleActionRunLogExist(itemData, ruleSetRule, ruleSet, itr.next(),
											variableAndValue, dataEntryRuleRunnerParameter)) {
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
									"RuleSet with target  : {}, Ran Rule : {}  The Result was : {}, Based on that {} action will be executed in {} mode.",
									ruleSet.getTarget().getValue(), rule.getName(), result,
									actionListBasedOnRuleExecutionResult.size(), executionMode.name());
						}
					} catch (OpenClinicaSystemException ex) {
						String code = ex.getErrorCode();
						if (code != null
								&& (code.equalsIgnoreCase("OCRERR_DATE_SHOULD_BE_ENTERED")
										|| code.equalsIgnoreCase("OCRERR_CANT_GET_SUBJEC_DOB") || code
											.equalsIgnoreCase("OCRERR_CANT_GET_SUBJECT_ENROLLMENT"))) {
							showCustomErrors(ruleSetRule, ruleSet, variableAndValue, respagemsgs.getString(code),
									dataEntryRuleRunnerParameter);
						}
					} catch (NullPointerException npe) {
						logger.info("found NPE while running rules, possible empty execution Mode");
						logger.info("rule set target value " + ruleSet.getTarget().getValue() + " rule name "
								+ rule.getName());
					}
				}
			}
		}
		dataEntryRuleRunnerParameter.getAttributes().put("toBeExecuted", toBeExecuted);
	}

	/**
	 * Determines whether DN should be created for item.
	 * 
	 * @param itemData
	 *            ItemDataBean to be used.
	 * @param transformedDNs
	 *            List of DiscrepancyNoteBean
	 * @return true if dn should be created, false otherwise.
	 */
	public static boolean shouldCreateDNForItem(ItemDataBean itemData, List<DiscrepancyNoteBean> transformedDNs) {
		// some DNs have been already transformed from Annotations, so we should skip them to prevent duplicating
		if (transformedDNs == null || itemData == null || transformedDNs.isEmpty()) {
			return true;
		}
		for (DiscrepancyNoteBean dn : transformedDNs) {
			if (dn.getEntityId() == itemData.getId()) {
				return false;
			}
		}
		return true;
	}

	private boolean doesTheRuleActionRunLogExist(ItemDataBean itemData, RuleSetRuleBean ruleSetRule,
			RuleSetBean ruleSet, RuleActionBean ruleActionBean, HashMap<String, String> variableAndValue,
			DataEntryRuleRunnerParameter dataEntryRuleRunnerParameter) {
		boolean exists = false;
		String firstDDE = "firstDDEInsert_" + ruleSetRule.getOid() + "_" + itemData.getId();
		if (ruleActionBean.getActionType() == ActionType.INSERT) {
			dataEntryRuleRunnerParameter.getAttributes().put("insertAction", true);
			final int four = 4;
			if (phase == RuleActionRunBean.Phase.DOUBLE_DATA_ENTRY && itemData.getStatus().getId() == four
					&& dataEntryRuleRunnerParameter.getAttributes().get(firstDDE) == null) {
				dataEntryRuleRunnerParameter.getAttributes().put(firstDDE, true);
			}
		}

		if (!(dataEntryRuleRunnerParameter.getAttributes().get(firstDDE) == Boolean.TRUE)) {

			String key = getDynamicsMetadataService().getExpressionService().getItemOid(
					ruleSet.getOriginalTarget().getValue());
			String itemDataValueFromForm;
			if (variableAndValue.containsKey(key)) {
				itemDataValueFromForm = variableAndValue.get(key);
			} else {
				itemDataValueFromForm = itemData.getValue();
			}

			RuleActionRunLogBean ruleActionRunLog = new RuleActionRunLogBean(ruleActionBean.getActionType(), itemData,
					itemDataValueFromForm, ruleSetRule.getRuleBean().getOid());

			exists = getRuleActionRunLogDao().findCountByRuleActionRunLogBean(ruleActionRunLog) > 0;
		}
		return exists;
	}

	private void showCustomErrors(RuleSetRuleBean ruleSetRule, RuleSetBean ruleSet,
			HashMap<String, String> variableAndValue, String customMessage,
			DataEntryRuleRunnerParameter dataEntryRuleRunnerParameter) {
		for (RuleActionBean ruleActionBean : ruleSetRule.getActions()) {
			if (ruleActionBean.getRuleSetRule() == ruleSetRule && ruleActionBean.getRuleActionRun().canRun(phase)
					&& ruleActionBean instanceof DiscrepancyNoteActionBean) {
				ItemDataBean itemData = getDynamicsMetadataService().getExpressionService()
						.getItemDataBeanFromDb(ruleSet.getTarget().getValue());
				if (itemData == null || !doesTheRuleActionRunLogExist(itemData, ruleSetRule, ruleSet, ruleActionBean,
						variableAndValue, dataEntryRuleRunnerParameter)) {
					((DiscrepancyNoteActionBean) ruleActionBean).setCustomMessage(customMessage);
					RuleActionContainer ruleActionContainer = new RuleActionContainer(ruleActionBean,
							ruleSet.getTarget(), itemData, ruleSet);
					allActionContainerListBasedOnRuleExecutionResult.add(ruleActionContainer);
				}
			}
		}
	}

	/**
	 * Sets phase for this object.
	 * 
	 * @param phase
	 *            to be set.
	 */
	public void setPhase(Phase phase) {
		this.phase = phase;
	}

	/**
	 * Sets current study for this object.
	 * 
	 * @param currentStudy
	 *            to be set.
	 */
	public void setCurrentStudy(StudyBean currentStudy) {
		this.currentStudy = currentStudy;
	}

	/**
	 * Sets ExecutionMode for this object.
	 * 
	 * @param executionMode
	 *            to be set.
	 */
	public void setExecutionMode(ExecutionMode executionMode) {
		this.executionMode = executionMode;
	}

	private boolean ruleActionContainerAlreadyExistsInList(RuleActionContainer ruleActionContainer) {
		for (RuleActionContainer rac : allActionContainerListBasedOnRuleExecutionResult) {
			if (rac.getRuleAction().getId().equals(ruleActionContainer.getRuleAction().getId())
					&& rac.getRuleSetBean().getId().equals(ruleActionContainer.getRuleSetBean().getId())
					&& rac.getExpressionBean().getValue().equals(ruleActionContainer.getExpressionBean().getValue())) {
				return true;
			}
		}
		return false;
	}
}
