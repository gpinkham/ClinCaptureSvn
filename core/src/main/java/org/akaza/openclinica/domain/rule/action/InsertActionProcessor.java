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

package org.akaza.openclinica.domain.rule.action;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.dao.hibernate.RuleActionRunLogDao;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.akaza.openclinica.logic.rulerunner.ExecutionMode;
import org.akaza.openclinica.logic.rulerunner.RuleRunner.RuleRunnerMode;
import org.akaza.openclinica.service.crfdata.DynamicsMetadataService;

import javax.sql.DataSource;

public class InsertActionProcessor implements ActionProcessor {

	DataSource ds;
	DynamicsMetadataService itemMetadataService;
	RuleActionRunLogDao ruleActionRunLogDao;
	RuleSetBean ruleSet;
	RuleSetRuleBean ruleSetRule;

	public InsertActionProcessor(DataSource ds, DynamicsMetadataService itemMetadataService,
			RuleActionRunLogDao ruleActionRunLogDao, RuleSetBean ruleSet, RuleSetRuleBean ruleSetRule) {
		this.itemMetadataService = itemMetadataService;
		this.ruleSet = ruleSet;
		this.ruleSetRule = ruleSetRule;
		this.ruleActionRunLogDao = ruleActionRunLogDao;
		this.ds = ds;
	}

	public RuleActionBean execute(RuleRunnerMode ruleRunnerMode, ExecutionMode executionMode,
			RuleActionBean ruleAction, ItemDataBean itemDataBean, String itemData, StudyBean currentStudy,
			UserAccountBean ub, Object... arguments) {

		switch (executionMode) {
		case DRY_RUN: {
			if (ruleRunnerMode == RuleRunnerMode.DATA_ENTRY) {
				return null;
			} else {
				dryRun(ruleAction, itemDataBean, itemData, currentStudy, ub);
			}
		}
		case SAVE: {
			if (ruleRunnerMode == RuleRunnerMode.DATA_ENTRY) {
				save(ruleAction, itemDataBean, itemData, currentStudy, ub);
			} else {
				save(ruleAction, itemDataBean, itemData, currentStudy, ub);
			}
		}
		default:
			return null;
		}
	}

	private RuleActionBean save(RuleActionBean ruleAction, ItemDataBean itemDataBean, String itemData,
			StudyBean currentStudy, UserAccountBean ub) {
		getItemMetadataService().insert(itemDataBean.getId(), ((InsertActionBean) ruleAction).getProperties(), ub,
				ruleSet);
		RuleActionRunLogBean ruleActionRunLog = new RuleActionRunLogBean(ruleAction.getActionType(), itemDataBean,
				itemDataBean.getValue(), ruleSetRule.getRuleBean().getOid());
		if (ruleActionRunLogDao.findCountByRuleActionRunLogBean(ruleActionRunLog) > 0) {
		} else {
			ruleActionRunLogDao.saveOrUpdate(ruleActionRunLog);
		}
		return null;
	}

	private RuleActionBean dryRun(RuleActionBean ruleAction, ItemDataBean itemDataBean, String itemData,
			StudyBean currentStudy, UserAccountBean ub) {
		return ruleAction;
	}

	private DynamicsMetadataService getItemMetadataService() {
		return itemMetadataService;
	}

}
