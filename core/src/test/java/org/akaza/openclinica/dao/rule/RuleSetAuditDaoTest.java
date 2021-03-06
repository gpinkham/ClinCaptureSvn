/*******************************************************************************
 * Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.akaza.openclinica.dao.rule;

import java.util.List;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.domain.rule.RuleSetAuditBean;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.junit.Test;

public class RuleSetAuditDaoTest extends DefaultAppContextTest {

	@Test
	public void testFindAllByRuleSet() {

		RuleSetBean ruleSet = ruleSetDao.findById(1);
		List<RuleSetAuditBean> ruleSetAudits = ruleSetAuditDao.findAllByRuleSet(ruleSet);

		assertNotNull("ruleSetAudits is null", ruleSetAudits);
		assertEquals("The size of the ruleSetAudits is not 2", new Integer(2), Integer.valueOf(ruleSetAudits.size()));

	}

	@Test
	public void testFindById() {

		RuleSetAuditBean ruleSetAuditBean = ruleSetAuditDao.findById(1);

		assertNotNull("ruleSetRuleAuditBean is null", ruleSetAuditBean);
		assertEquals("The ruleSetRuleAuditBean.getRuleSetRule.getId should be 3", new Integer(1),
				Integer.valueOf(ruleSetAuditBean.getRuleSetBean().getId()));

	}

	@Test
	public void testSaveOrUpdate() {
		
		RuleSetBean ruleSetBean = ruleSetDao.findById(1);

		RuleSetAuditBean ruleSetAuditBean = new RuleSetAuditBean();
		ruleSetAuditBean.setRuleSetBean(ruleSetBean);
		ruleSetAuditBean = ruleSetAuditDao.saveOrUpdate(ruleSetAuditBean);

		assertNotNull("Persistant id is null", ruleSetAuditBean.getId());
	}
}
