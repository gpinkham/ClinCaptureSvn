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

import org.akaza.openclinica.dao.hibernate.AuthoritiesDao;
import org.akaza.openclinica.dao.hibernate.RuleDao;
import org.akaza.openclinica.dao.hibernate.RuleSetDao;
import org.akaza.openclinica.dao.hibernate.RuleSetRuleDao;
import org.akaza.openclinica.domain.rule.RuleBean;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.akaza.openclinica.templates.HibernateOcDbTestCase;
import org.hibernate.HibernateException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;

public class RuleSetRuleDaoTest extends HibernateOcDbTestCase {
	private static RuleSetRuleDao ruleSetRuleDao;
	private static RuleDao ruleDao;
	private static RuleSetDao ruleSetDao;

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
	 * "classpath*:org/akaza/openclinica/applicationContext-core-service.xml",
	 * " classpath*:org/akaza/openclinica/applicationContext-core-timer.xml",
	 * "classpath*:org/akaza/openclinica/applicationContext-security.xml" }); transactionManager =
	 * (PlatformTransactionManager) context.getBean("transactionManager"); transactionManager.getTransaction(new
	 * DefaultTransactionDefinition());
	 * 
	 * 
	 * }
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		ruleSetRuleDao = (RuleSetRuleDao) getContext().getBean("ruleSetRuleDao");
		ruleSetDao = (RuleSetDao) getContext().getBean("ruleSetDao");
		ruleDao = (RuleDao) getContext().getBean("ruleDao");
	}

	public void testFindById() {
		// RuleSetRuleDao ruleSetRuleDao = (RuleSetRuleDao) getContext().getBean("ruleSetRuleDao");
		RuleSetRuleBean ruleSetRuleBean = null;
		ruleSetRuleBean = ruleSetRuleDao.findById(3);

		// Test RuleSetRule
		assertNotNull("RuleSet is null", ruleSetRuleBean);
		assertEquals("The id of the retrieved RuleSet should be 1", new Integer(3), ruleSetRuleBean.getId());

	}

	public void testFindByIdEmptyResultSet() {
		// RuleSetRuleDao ruleSetRuleDao = (RuleSetRuleDao) getContext().getBean("ruleSetRuleDao");

		RuleSetRuleBean ruleSetRuleBean = null;
		ruleSetRuleBean = ruleSetRuleDao.findById(6);

		// Test Rule
		assertNull("RuleSet is null", ruleSetRuleBean);
	}

	public void testFindByRuleSetBeanAndRuleBean() {
		// RuleDao ruleDao = (RuleDao) getContext().getBean("ruleDao");
		// RuleSetDao ruleSetDao = (RuleSetDao) getContext().getBean("ruleSetDao");
		// RuleSetRuleDao ruleSetRuleDao = (RuleSetRuleDao) getContext().getBean("ruleSetRuleDao");
		RuleBean persistentRuleBean = ruleDao.findById(-1);
		RuleSetBean persistentRuleSetBean = ruleSetDao.findById(-1);
		List<RuleSetRuleBean> ruleSetRules = ruleSetRuleDao.findByRuleSetBeanAndRuleBean(persistentRuleSetBean,
				persistentRuleBean);

		assertNotNull("RuleSetRules is null", ruleSetRules);
		assertEquals("The size of RuleSetRules should be 1", new Integer(1), new Integer(ruleSetRules.size()));
	}

	public void tearDown() {
		try {
			ruleSetDao.getSessionFactory().getCurrentSession().close();
			ruleDao.getSessionFactory().getCurrentSession().close();
			// ruleDao.getSessionFactory().getCurrentSession().flush();
			ruleSetRuleDao.getSessionFactory().getCurrentSession().close();
		} catch (HibernateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.tearDown();
	}
}
