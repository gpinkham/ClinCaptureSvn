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
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

/* 
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * OpenClinica is distributed under the
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.service.rule;

import org.akaza.openclinica.bean.oid.GenericOidGenerator;
import org.akaza.openclinica.bean.oid.OidGenerator;
import org.akaza.openclinica.bean.rule.RuleBean;
import org.akaza.openclinica.bean.rule.RuleSetBean;
import org.akaza.openclinica.dao.rule.RuleDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public class RuleService {

	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
	DataSource ds;
	private RuleDAO ruleDao;
	private OidGenerator oidGenerator;

	public RuleService(DataSource ds) {
		oidGenerator = new GenericOidGenerator();
		this.ds = ds;
	}

	public boolean enableRules(RuleSetBean ruleSet) {
		return true;
	}

	public boolean disableRules() {
		return true;

	}

	public RuleBean saveRule(RuleBean ruleBean) {
		return (RuleBean) getRuleDao().create(ruleBean);
	}

	public RuleBean updateRule(RuleBean ruleBean) {
		return (RuleBean) getRuleDao().update(ruleBean);
	}

	private RuleDAO getRuleDao() {
		ruleDao = this.ruleDao != null ? ruleDao : new RuleDAO(ds);
		return ruleDao;
	}

}
