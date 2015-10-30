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

package org.akaza.openclinica.service.rule;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.dao.hibernate.RuleDao;
import org.akaza.openclinica.dao.hibernate.RuleSetAuditDao;
import org.akaza.openclinica.dao.hibernate.RuleSetDao;
import org.akaza.openclinica.dao.hibernate.RuleSetRuleDao;
import org.akaza.openclinica.dao.hibernate.ViewRuleAssignmentFilter;
import org.akaza.openclinica.dao.hibernate.ViewRuleAssignmentSort;
import org.akaza.openclinica.domain.Status;
import org.akaza.openclinica.domain.rule.RuleBean;
import org.akaza.openclinica.domain.rule.RuleBulkExecuteContainer;
import org.akaza.openclinica.domain.rule.RuleBulkExecuteContainerTwo;
import org.akaza.openclinica.domain.rule.RuleSetBasedViewContainer;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.akaza.openclinica.domain.rule.RulesPostImportContainer;
import org.akaza.openclinica.domain.rule.action.RuleActionRunBean.Phase;
import org.akaza.openclinica.logic.rulerunner.ExecutionMode;
import org.akaza.openclinica.logic.rulerunner.ImportDataRuleRunnerContainer;
import org.akaza.openclinica.logic.rulerunner.MessageContainer;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Contains ruleset service methods.
 */
public interface RuleSetServiceInterface {

	/**
	 * Saves RuleSetBean.
	 * 
	 * @param ruleSetBean
	 *            rule to save
	 * @return RuleSetBean after saving
	 */
	@Transactional
	RuleSetBean saveRuleSet(RuleSetBean ruleSetBean);

	/**
	 * Saves RuleSet in rulesContainer from import.
	 * 
	 * @param rulesContainer
	 *            contains rules to be saved.
	 */
	@Transactional
	void saveImportFromDesigner(RulesPostImportContainer rulesContainer);

	/**
	 * Saves imported RuleSets in rulesContainer.
	 * 
	 * @param rulesContainer
	 *            contains imported rules.
	 */
	@Transactional
	void saveImport(RulesPostImportContainer rulesContainer);

	/**
	 * Saves RuleSetRule.
	 * 
	 * @param ruleSetRule
	 *            to be saved.
	 */
	@Transactional
	void saveImport(RuleSetRuleBean ruleSetRule);

	/**
	 * Updates RuleSet.
	 * 
	 * @param ruleSetBean
	 *            RuleSet to be updated
	 * @param user
	 *            updater
	 * @param status
	 *            status after update
	 * @return returns updated RuleSet
	 */
	@Transactional
	RuleSetBean updateRuleSet(RuleSetBean ruleSetBean, UserAccountBean user, Status status);

	/**
	 * Loads RuleSet with persistent rules.
	 * 
	 * @param ruleSetBean
	 *            RuleSet to be loaded
	 */
	@Transactional
	void loadRuleSetRuleWithPersistentRules(RuleSetBean ruleSetBean);

	/**
	 * Replaces RuleSetBean.
	 * 
	 * @param ruleSetBean
	 *            RuleSetBean to be used.
	 * @return updated RuleSetBean
	 */
	@Transactional
	RuleSetBean replaceRuleSet(RuleSetBean ruleSetBean);

	/**
	 * Runs all defined rules for crf.
	 * 
	 * @param crfId
	 *            crf's id.
	 * @param executionMode
	 *            ExecutionMode: DRY_RUN or SAVE
	 * @param currentStudy
	 *            current study
	 * @param ub
	 *            current user
	 * @return RuleBulkExecuteContainer.
	 */
	HashMap<RuleBulkExecuteContainer, HashMap<RuleBulkExecuteContainerTwo, Set<String>>> runRulesInBulk(String crfId,
			ExecutionMode executionMode, StudyBean currentStudy, UserAccountBean ub);

	/**
	 * Runs all defined rules for crf version.
	 * 
	 * @param ruleSetRuleId
	 *            id of RuleSet to execute
	 * @param crfVersionId
	 *            crf version id
	 * @param executionMode
	 *            ExecutionMode: DRY_RUN or SAVE
	 * @param currentStudy
	 *            current study
	 * @param ub
	 *            current user
	 * @return RuleBulkExecuteContainer
	 */
	HashMap<RuleBulkExecuteContainer, HashMap<RuleBulkExecuteContainerTwo, Set<String>>> runRulesInBulk(
			String ruleSetRuleId, String crfVersionId, ExecutionMode executionMode, StudyBean currentStudy,
			UserAccountBean ub);

	/**
	 * Runs rules in bulk.
	 * 
	 * @param ruleSets
	 *            RuleSetBeans
	 * @param dryRun
	 *            specifies if ExecutionMode is DRY_RUN
	 * @param currentStudy
	 *            current study.
	 * @param ub
	 *            current user
	 * @return RuleSetBaseViewContainer list
	 */
	List<RuleSetBasedViewContainer> runRulesInBulk(List<RuleSetBean> ruleSets, Boolean dryRun, StudyBean currentStudy,
			UserAccountBean ub);

	/**
	 * Runs rules in data entry.
	 * 
	 * @param ruleSets
	 *            RuleSets containing rules to be run
	 * @param dryRun
	 *            specifies if ExecutionMode is DRY_RUN
	 * @param ub
	 *            current user
	 * @param variableAndValue
	 *            variable and value pairs
	 * @param phase
	 *            data entry phase
	 * @param ecb
	 *            EventCRFBean
	 * @param request
	 *            HttpServletRequest
	 * @return MessageContainer
	 */
	MessageContainer runRulesInDataEntry(List<RuleSetBean> ruleSets, Boolean dryRun, UserAccountBean ub,
			HashMap<String, String> variableAndValue, Phase phase, EventCRFBean ecb, HttpServletRequest request);

	/**
	 * Runs rules in data import.
	 * 
	 * @param containers
	 *            list of ImportDataRuleRunnerContainers
	 * @param skippedItemIds
	 *            ids of items to skip
	 * @param study
	 *            study being imported to
	 * @param ub
	 *            current user
	 * @param executionMode
	 *            ExecutionMode: DRY_RUN or SAVE
	 * @return RuleActionBean summary with key as groupOrdinalPLusItemOid.
	 */
	HashMap<String, ArrayList<String>> runRulesInImportData(List<ImportDataRuleRunnerContainer> containers,
			Set<Integer> skippedItemIds, StudyBean study, UserAccountBean ub, ExecutionMode executionMode);

	/**
	 * Runs rules in data import.
	 * 
	 * @param optimiseRuleValidator
	 *            specifies whether rule validator should be optimized
	 * @param connection
	 *            Sql Connection to be used
	 * @param containers
	 *            list of ImportDataRuleRunnerContainers
	 * @param study
	 *            study to be imported to
	 * @param ub
	 *            current user
	 * @param executionMode
	 *            ExecutionMode: DRY_RUN or SAVE
	 * @return RuleActionBean summary
	 */
	HashMap<String, ArrayList<String>> runRulesInImportData(Boolean optimiseRuleValidator, Connection connection,
			List<ImportDataRuleRunnerContainer> containers, StudyBean study, UserAccountBean ub,
			ExecutionMode executionMode);

	/**
	 * Runs rules in data import.
	 * 
	 * @param optimiseRuleValidator
	 *            specifies whether rule validator should be optimized
	 * @param connection
	 *            Sql Connection to be used
	 * @param containers
	 *            list of ImportDataRuleRunnerContainers
	 * @param skippedItemIds
	 *            ids of skipped items
	 * @param study
	 *            study to be imported to
	 * @param ub
	 *            current user
	 * @param executionMode
	 *            ExecutionMode: DRY_RUN or SAVE
	 * @return RuleActionBean summary
	 */
	HashMap<String, ArrayList<String>> runRulesInImportData(Boolean optimiseRuleValidator, Connection connection,
			List<ImportDataRuleRunnerContainer> containers, Set<Integer> skippedItemIds, StudyBean study,
			UserAccountBean ub, ExecutionMode executionMode);

	/**
	 * Gets RuleSets by CRF study and StudyEventDefinition.
	 * 
	 * @param study
	 *            study to be used.
	 * @param sed
	 *            StudyEventDefinition to be used.
	 * @param crfVersion
	 *            CRFVersion to be used.
	 * @return list of RuleSetBeans
	 */
	List<RuleSetBean> getRuleSetsByCrfStudyAndStudyEventDefinition(StudyBean study, StudyEventDefinitionBean sed,
			CRFVersionBean crfVersion);

	/**
	 * Gets all RuleSets of a study, both removed and available.
	 * 
	 * @param study
	 *            study to be used.
	 * @return list of RuleSetBeans
	 */
	List<RuleSetBean> getRuleSetsByStudy(StudyBean study);

	/**
	 * Gets count with filter.
	 * 
	 * @param viewRuleAssignmentFilter
	 *            ViewRuleAssignmentFilter to be used.
	 * @return count.
	 */
	@Transactional
	int getCountWithFilter(ViewRuleAssignmentFilter viewRuleAssignmentFilter);

	/**
	 * Gets count by study.
	 * 
	 * @param study
	 *            study to be used.
	 * @return count
	 */
	@Transactional
	int getCountByStudy(StudyBean study);

	/**
	 * Gets RuleSetRules with filter and sort.
	 * 
	 * @param viewRuleAssignmentFilter
	 *            filter to be used.
	 * @param viewRuleAssignmentSort
	 *            sort to be used.
	 * @param rowStart
	 *            first row
	 * @param rowEnd
	 *            last row
	 * @return list of RuleSetRules
	 */
	@Transactional
	List<RuleSetRuleBean> getWithFilterAndSort(ViewRuleAssignmentFilter viewRuleAssignmentFilter,
			ViewRuleAssignmentSort viewRuleAssignmentSort, int rowStart, int rowEnd);

	/**
	 * Gets RuleSet by id.
	 * 
	 * @param study
	 *            study it belongs to.
	 * @param id
	 *            id of RuleSet
	 * @return RuleSet
	 */
	RuleSetBean getRuleSetById(StudyBean study, String id);

	/**
	 * Gets RuleSetRule by id.
	 * 
	 * @param study
	 *            study it belongs to
	 * @param id
	 *            id of RuleSetRule
	 * @param ruleBean
	 *            RuleSet it belongs to
	 * @return RuleSetRule
	 */
	@Transactional
	List<RuleSetRuleBean> getRuleSetById(StudyBean study, String id, RuleBean ruleBean);

	/**
	 * Gets RuleSets by CRF and Study.
	 * 
	 * @param crfBean
	 *            CRF to be used.
	 * @param study
	 *            study to be used.
	 * @return list of RuleSetBeans
	 */
	List<RuleSetBean> getRuleSetsByCrfAndStudy(CRFBean crfBean, StudyBean study);

	/**
	 * Filters by status available only for RuleSetRules.
	 * 
	 * @param ruleSets
	 *            RuleSets to be filtered.
	 * @return filter RuleSets
	 */
	@Transactional
	List<RuleSetBean> filterByStatusEqualsAvailableOnlyRuleSetRules(List<RuleSetBean> ruleSets);

	/**
	 * Filters by status available.
	 * 
	 * @param ruleSets
	 *            RuleSets to filter
	 * @return filtered RuleSets
	 */
	@Transactional
	List<RuleSetBean> filterByStatusEqualsAvailable(List<RuleSetBean> ruleSets);

	/**
	 * Filters by rules.
	 * 
	 * @param ruleSet
	 *            RuleSetContaining RuleSetRules to be filtered
	 * @param ruleBeanId
	 *            RuleSet id to filter
	 * @return RuleSet with filter rules
	 */
	@Transactional
	RuleSetBean filterByRules(RuleSetBean ruleSet, Integer ruleBeanId);

	/**
	 * Initializes RuleSet properties.
	 * 
	 * @param ruleSetBean
	 *            RuleSet to be initialized.
	 * @return initialized RuleSet
	 */
	RuleSetBean getObjects(RuleSetBean ruleSetBean);

	/**
	 * Use in DataEntry Rule Execution Scenarios
	 * 
	 * A RuleSet has a Target with Value which is provided by rule Creator. value might be :
	 * SE_TESTINGF[ALL].F_AGEN_8_V204.IG_AGEN_DOSETABLE6[ALL].I_AGEN_DOSEDATE64 OR
	 * SE_TESTINGF[1].F_AGEN_8_V204.IG_AGEN_DOSETABLE6[ALL].I_AGEN_DOSEDATE64 OR
	 * SE_TESTINGF.F_AGEN_8_V204.IG_AGEN_DOSETABLE6[ALL].I_AGEN_DOSEDATE64 in which case it would need to be transformed
	 * to SE_TESTINGF[x].F_AGEN_8_V204.IG_AGEN_DOSETABLE6[ALL].I_AGEN_DOSEDATE64 where x is the studyEventId.
	 * 
	 * @param ruleSets
	 *            RuleSets to be filtered
	 * @param studyEvent
	 *            StudyEvent to be used.
	 * @param crfVersion
	 *            CRFVersion to be used. TODO
	 * @param studyEventDefinition
	 *            StudyEventDefinition to be used. TODO
	 * @return filtered RuleSetBeans
	 */
	@Transactional
	List<RuleSetBean> filterRuleSetsByStudyEventOrdinal(List<RuleSetBean> ruleSets, StudyEventBean studyEvent,
			CRFVersionBean crfVersion, StudyEventDefinitionBean studyEventDefinition);

	/**
	 * Filters RuleSets by StudyEventOrdinal.
	 * 
	 * @param ruleSets
	 *            RuleSets to be filtered.
	 * @param crfVersionId
	 *            Crf Version Id to be used
	 * @return filtered RuleSetBeans
	 */
	List<RuleSetBean> filterRuleSetsByStudyEventOrdinal(List<RuleSetBean> ruleSets, String crfVersionId);

	/**
	 * Iterate over ruleSet.getExpressions(). Given the following expression
	 * SE_TESTINGF[studyEventId].F_AGEN_8_V204.IG_AGEN_DOSETABLE6[X].I_AGEN_DOSEDATE64 X could be : ALL , "" , Number if
	 * ALL or "" then iterate over all group ordinals if they exist and add. if Number just add the number.
	 * 
	 * @param ruleSets
	 *            RuleSets to be used.
	 * @param grouped
	 *            form properties to use.
	 * @return list of solidified RuleSets
	 */
	@Transactional
	List<RuleSetBean> solidifyGroupOrdinalsUsingFormProperties(List<RuleSetBean> ruleSets,
			HashMap<String, Integer> grouped);

	/**
	 * Filters RuleSets by section and group ordinal.
	 * 
	 * @param ruleSets
	 *            RuleSets to filter
	 * @param grouped
	 *            properties to use
	 * @return filtered RuleSets
	 */
	@Transactional
	List<RuleSetBean> filterRuleSetsBySectionAndGroupOrdinal(List<RuleSetBean> ruleSets,
			HashMap<String, Integer> grouped);

	/**
	 * Iterate over rulesets and remove those which are currently hidden.
	 * 
	 * @param ruleSets
	 *            RuleSets to be used.
	 * @param eventCrf
	 *            EventCRF to be used.
	 * @param crfVersion
	 *            CRFVersion to be used.
	 * @param itemBeansWithSCDShown
	 *            Items with SCD shown
	 * @return filtered RuleSets
	 */
	List<RuleSetBean> filterRuleSetsByHiddenItems(List<RuleSetBean> ruleSets, EventCRFBean eventCrf,
			CRFVersionBean crfVersion, List<ItemBean> itemBeansWithSCDShown);

	/**
	 * Iterate over ruleSet.getExpressions(). Given the following expression
	 * SE_TESTINGF[studyEventId].F_AGEN_8_V204.IG_AGEN_DOSETABLE6[X].I_AGEN_DOSEDATE64 X could be : ALL , "" , Number
	 * case 1 : if "" then iterate over itemDatas if they exist add. case 2 : if Number just add the number.
	 * 
	 * @param ruleSets
	 *            RuleSets to be used.
	 * @return filtered RuleSets
	 */
	List<RuleSetBean> filterRuleSetsByGroupOrdinal(List<RuleSetBean> ruleSets);

	/**
	 * Gets group ordinal and item oids in a String list.
	 * 
	 * @param ruleSets
	 *            RuleSets to use.
	 * @return list of group ordinals plus item oids
	 */
	@Transactional
	List<String> getGroupOrdinalPlusItemOids(List<RuleSetBean> ruleSets);

	/**
	 * Replaces crf oid in target expression.
	 * 
	 * @param ruleSetBean
	 *            RuleSet to use
	 * @param replacementCrfOid
	 *            new crf oid
	 * @return RuleSet with replaced crf oid
	 */
	@Transactional
	RuleSetBean replaceCrfOidInTargetExpression(RuleSetBean ruleSetBean, String replacementCrfOid);

	/**
	 * Gets context path.
	 * 
	 * @return context path
	 */
	String getContextPath();

	/**
	 * Sets context path.
	 * 
	 * @param contextPath
	 *            context path to set.
	 */
	void setContextPath(String contextPath);

	/**
	 * Sets request url excluding servlet path.
	 * 
	 * @param requestURLMinusServletPath
	 *            path to set.
	 */
	void setRequestURLMinusServletPath(String requestURLMinusServletPath);

	/**
	 * Gets request path excluding servlet path.
	 * 
	 * @return request path
	 */
	String getRequestURLMinusServletPath();

	/**
	 * Gets RuleSetDao.
	 * 
	 * @return the ruleSetDao.
	 */
	RuleSetDao getRuleSetDao();

	/**
	 * Sets RuleSetDao.
	 * 
	 * @param ruleSetDao
	 *            the ruleSetDao to set
	 */
	void setRuleSetDao(RuleSetDao ruleSetDao);

	/**
	 * Sets RuleSetRuleDao.
	 * 
	 * @param ruleSetRuleDao
	 *            the ruleSetRuleDao to set
	 */
	void setRuleSetRuleDao(RuleSetRuleDao ruleSetRuleDao);

	/**
	 * @return the ruleSetRuleDao
	 */
	RuleSetRuleDao getRuleSetRuleDao();

	/**
	 * @return the ruleDao
	 */
	RuleDao getRuleDao();

	/**
	 * @param ruleDao
	 *            the ruleDao to set
	 */
	void setRuleDao(RuleDao ruleDao);

	/**
	 * @return the RuleSetAuditDao
	 */
	RuleSetAuditDao getRuleSetAuditDao();

	/**
	 * @param ruleSetAuditDao
	 *            to set
	 */
	void setRuleSetAuditDao(RuleSetAuditDao ruleSetAuditDao);

	/**
	 * @return JavaMailSender
	 */
	JavaMailSenderImpl getMailSender();

	/**
	 * @param mailSender
	 *            to set
	 */
	void setMailSender(JavaMailSenderImpl mailSender);

	/**
	 * Return true if there is at least one rule should be run for a phase.
	 * 
	 * @param ruleSets
	 *            to be used
	 * @param phase
	 *            phase to check
	 * @return true if yes, false if no
	 */
	@Transactional
	boolean shouldRunRulesForRuleSets(List<RuleSetBean> ruleSets, Phase phase);

	/**
	 * Find all ruleSetRules for some EventDefinitionCRF by event name and CRF Id.
	 * @param eventName String
	 * @param crfId int
	 * @return List of RuleSetRuleBeans
	 */
	@Transactional
	List<RuleSetRuleBean> findAllRulesForEventDefinitionCRF(String eventName, int crfId);
}
