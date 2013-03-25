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

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.hibernate.RuleDao;
import org.akaza.openclinica.dao.hibernate.RuleSetAuditDao;
import org.akaza.openclinica.domain.rule.RuleBean;
import org.akaza.openclinica.domain.rule.expression.Context;
import org.akaza.openclinica.domain.rule.expression.ExpressionBean;
import org.akaza.openclinica.templates.HibernateOcDbTestCase;
import org.hibernate.HibernateException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class RuleDaoTest extends HibernateOcDbTestCase {
	private static RuleDao ruleDao;

	/*
	 * static {
	 * 
	 * loadProperties(); dbName = properties.getProperty("dbName"); dbUrl = properties.getProperty("url"); dbUserName =
	 * properties.getProperty("username"); dbPassword = properties.getProperty("password"); dbDriverClassName =
	 * properties.getProperty("driver"); locale = properties.getProperty("locale"); initializeLocale();
	 * initializeQueriesInXml();
	 * 
	 * 
	 * 
	 * context = new ClassPathXmlApplicationContext( new String[] { "classpath*:applicationContext-core-s*.xml",
	 * "classpath*:org/akaza/openclinica/applicationContext-core-db.xml",
	 * "classpath*:org/akaza/openclinica/applicationContext-core-email.xml",
	 * "classpath*:org/akaza/openclinica/applicationContext-core-hibernate.xml",
	 * "classpath*:org/akaza/openclinica/applicationContext-core-scheduler.xml",
	 * "classpath*:org/akaza/openclinica/applicationContext-core-service.xml",
	 * " classpath*:org/akaza/openclinica/applicationContext-core-timer.xml",
	 * "classpath*:org/akaza/openclinica/applicationContext-security.xml" }); transactionManager =
	 * (PlatformTransactionManager) context.getBean("transactionManager"); transactionManager.getTransaction(new
	 * DefaultTransactionDefinition());
	 * 
	 * 
	 * }
	 */
	public RuleDaoTest() {
		super();

	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		ruleDao = (RuleDao) getContext().getBean("ruleDao");

	}

	public void testFindByOidString() {
		// RuleDao ruleDao = (RuleDao) getContext().getBean("ruleDao");
		String oid = "RULE_1";
		RuleBean persistentRuleBean = ruleDao.findByOid(oid, 1);

		assertNotNull("RuleSet is null", persistentRuleBean);
		assertEquals("The id of the retrieved RuleSet should be 1", new Integer(-1), persistentRuleBean.getId());
	}

	public void testFindById() {
		// RuleDao ruleDao = (RuleDao) getContext().getBean("ruleDao");
		RuleBean ruleBean = null;
		ruleBean = ruleDao.findById(-1);

		// Test Rule
		assertNotNull("RuleSet is null", ruleBean);
		assertEquals("The id of the retrieved RuleSet should be 1", new Integer(-1), ruleBean.getId());

	}

	public void testFindByIdEmptyResultSet() {
		// RuleDao ruleDao = (RuleDao) getContext().getBean("ruleDao");
		RuleBean ruleBean2 = null;
		ruleBean2 = ruleDao.findById(-3);

		// Test Rule
		assertNull("RuleSet is null", ruleBean2);
	}

	public void testFindByOid() {
		// RuleDao ruleDao = (RuleDao) getContext().getBean("ruleDao");
		RuleBean ruleBean = new RuleBean();
		ruleBean.setOid("RULE_1");
		StudyBean studyBean = new StudyBean();
		studyBean.setId(1);
		ruleBean.setStudy(studyBean);
		RuleBean persistentRuleBean = ruleDao.findByOid(ruleBean);

		assertNotNull("RuleSet is null", persistentRuleBean);
		assertEquals("The id of the retrieved RuleSet should be 1", new Integer(-1), persistentRuleBean.getId());
	}

	public void testSaveOrUpdate() {
		// RuleDao ruleDao = (RuleDao) getContext().getBean("ruleDao");
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

	public void tearDown() {
		try {
			ruleDao.getSessionFactory().getCurrentSession().close();

		} catch (HibernateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.tearDown();
	}
}
