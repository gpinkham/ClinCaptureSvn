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

package org.akaza.openclinica.dao.rule;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.domain.rule.RuleBean;
import org.akaza.openclinica.domain.rule.expression.Context;
import org.akaza.openclinica.domain.rule.expression.ExpressionBean;
import org.junit.Test;

public class RuleDaoTest extends DefaultAppContextTest {

	@Test
	public void testFindByOidString() {

		String oid = "RULE_1";
		RuleBean persistentRuleBean = ruleDao.findByOid(oid, 1);

		assertNotNull("RuleSet is null", persistentRuleBean);
		assertEquals("The id of the retrieved RuleSet should be 1", new Integer(1), persistentRuleBean.getId());
	}

	@Test
	public void testFindById() {

		RuleBean ruleBean = null;
		ruleBean = ruleDao.findById(1);

		// Test Rule
		assertNotNull("RuleSet is null", ruleBean);
		assertEquals("The id of the retrieved RuleSet should be 1", new Integer(1), ruleBean.getId());

	}

	@Test
	public void testFindByIdEmptyResultSet() {

		RuleBean ruleBean2 = null;
		ruleBean2 = ruleDao.findById(-3);

		// Test Rule
		assertNull("RuleSet is null", ruleBean2);
	}

	@Test
	public void testFindByOid() {

		RuleBean ruleBean = new RuleBean();
		ruleBean.setOid("RULE_1");
		StudyBean studyBean = new StudyBean();
		studyBean.setId(1);
		ruleBean.setStudy(studyBean);
		RuleBean persistentRuleBean = ruleDao.findByOid(ruleBean);

		assertNotNull("RuleSet is null", persistentRuleBean);
		assertEquals("The id of the retrieved RuleSet should be 1", new Integer(1), persistentRuleBean.getId());
	}

	@Test
	public void testSaveOrUpdate() {

		RuleBean ruleBean = createRuleBeanStub();
		RuleBean persistentRuleBean = ruleDao.saveOrUpdate(ruleBean);

		RuleBean ruleBean2 = createRuleBeanStub();
		RuleBean persistentRuleBean2 = ruleDao.saveOrUpdate(ruleBean2);

		assertNotNull("Persistent id is null", persistentRuleBean.getId());
		assertNotNull("Persistent id is null", persistentRuleBean2.getId());
	}

	private RuleBean createRuleBeanStub() {

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
