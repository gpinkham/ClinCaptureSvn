/*******************************************************************************
 * Copyright (C) 2009-2013 Clinovo Inc.
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

package org.akaza.openclinica.service.rule;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.domain.rule.RuleBean;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.akaza.openclinica.domain.rule.RulesPostImportContainer;
import org.akaza.openclinica.domain.rule.action.DiscrepancyNoteActionBean;
import org.akaza.openclinica.domain.rule.expression.Context;
import org.akaza.openclinica.domain.rule.expression.ExpressionBean;
import org.akaza.openclinica.templates.HibernateOcDbTestCase;

import java.util.ArrayList;

@SuppressWarnings({"rawtypes"})
public class RulesPostImportContainerServiceTest extends HibernateOcDbTestCase {

	public RulesPostImportContainerServiceTest() {
		super();
	}

	public void testDuplicationRuleSetDefs() {
		StudyDAO studyDao = new StudyDAO(getDataSource());
		StudyBean study = (StudyBean) studyDao.findByPK(1);
		RulesPostImportContainerService postImportContainerService = (RulesPostImportContainerService) getContext()
				.getBean("rulesPostImportContainerService");
		postImportContainerService.setCurrentStudy(study);

		RulesPostImportContainer container = prepareContainer();

		container = postImportContainerService.validateRuleDefs(container);

		assertEquals(0, container.getDuplicateRuleDefs().size());
		assertEquals(0, container.getInValidRuleDefs().size());
		assertEquals(1, container.getValidRuleDefs().size());

	}

	private RulesPostImportContainer prepareContainer() {
		RulesPostImportContainer container = new RulesPostImportContainer();
		ArrayList<RuleSetBean> ruleSets = new ArrayList<RuleSetBean>();
		ArrayList<RuleBean> ruleDefs = new ArrayList<RuleBean>();

		RuleBean rule = createRuleBean();
		RuleSetBean ruleSet = getRuleSet(rule.getOid());
		ruleSets.add(ruleSet);
		ruleDefs.add(rule);
		container.setRuleSets(ruleSets);
		container.setRuleDefs(ruleDefs);
		return container;

	}

	private RuleSetBean getRuleSet(String ruleOid) {
		RuleSetBean ruleSet = new RuleSetBean();
		ruleSet.setTarget(createExpression(Context.OC_RULES_V1,
				"SE_ED2REPEA.F_CONC_V20.IG_CONC_CONCOMITANTMEDICATIONS.I_CONC_CON_MED_NAME"));
		RuleSetRuleBean ruleSetRule = createRuleSetRule(ruleSet, ruleOid);
		ruleSet.addRuleSetRule(ruleSetRule);
		return ruleSet;

	}

	private RuleSetRuleBean createRuleSetRule(RuleSetBean ruleSet, String ruleOid) {
		RuleSetRuleBean ruleSetRule = new RuleSetRuleBean();
		DiscrepancyNoteActionBean ruleAction = new DiscrepancyNoteActionBean();
		ruleAction.setMessage("HELLO WORLD");
		ruleAction.setExpressionEvaluatesTo(true);
		ruleSetRule.addAction(ruleAction);
		ruleSetRule.setRuleSetBean(ruleSet);
		ruleSetRule.setOid(ruleOid);

		return ruleSetRule;
	}

	private RuleBean createRuleBean() {
		RuleBean ruleBean = new RuleBean();
		ruleBean.setName("TEST");
		ruleBean.setOid("BOY");
		ruleBean.setDescription("Yellow");
		ruleBean.setExpression(createExpression(Context.OC_RULES_V1,
				"SE_ED1NONRE.F_AGEN.IG_AGEN_UNGROUPED[1].I_AGEN_PERIODSTART eq \"07/01/2008\" and I_CONC_CON_MED_NAME eq \"Tylenol\""));
		return ruleBean;
	}

	private ExpressionBean createExpression(Context context, String value) {
		ExpressionBean expression = new ExpressionBean();
		expression.setContext(context);
		expression.setValue(value);
		return expression;
	}
}
