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

package org.akaza.openclinica.domain.rule.action;

import com.clinovo.context.SubmissionContext;

import java.sql.Connection;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.dao.hibernate.RuleActionRunLogDao;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.akaza.openclinica.logic.rulerunner.ExecutionMode;
import org.akaza.openclinica.logic.rulerunner.RuleRunner.RuleRunnerMode;
import org.akaza.openclinica.service.managestudy.DiscrepancyNoteService;

public class DiscrepancyNoteActionProcessor implements ActionProcessor {

	Connection connection;
	DataSource ds;
	DiscrepancyNoteService discrepancyNoteService;
	RuleActionRunLogDao ruleActionRunLogDao;
	RuleSetRuleBean ruleSetRule;

	public DiscrepancyNoteActionProcessor(Connection connection, DataSource ds,
			RuleActionRunLogDao ruleActionRunLogDao, RuleSetRuleBean ruleSetRule) {
		this.connection = connection;
		this.ds = ds;
		this.ruleActionRunLogDao = ruleActionRunLogDao;
		this.ruleSetRule = ruleSetRule;
	}

	public RuleActionBean execute(RuleRunnerMode ruleRunnerMode, ExecutionMode executionMode,
			RuleActionBean ruleAction, ItemDataBean itemDataBean, String itemData, StudyBean currentStudy,
			UserAccountBean ub, Object... arguments) {
		boolean shouldCreateDNForItem = arguments.length > 1 ? (Boolean) arguments[0] : true;
		switch (executionMode) {
		case DRY_RUN: {
			return dryRun(ruleAction, itemDataBean, itemData, currentStudy, ub);
		}

		case SAVE: {
			return save(shouldCreateDNForItem, ruleAction, itemDataBean, itemData, currentStudy, ub);
		}

		default:
			return null;
		}
	}

	private RuleActionBean save(boolean shouldCreateDNForItem, RuleActionBean ruleAction, ItemDataBean itemDataBean,
			String itemData, StudyBean currentStudy, UserAccountBean ub) {
		if (itemDataBean != null) {
			if (shouldCreateDNForItem) {
				getDiscrepancyNoteService().saveFieldNotes(ruleAction.getCuratedMessage(), itemDataBean.getId(),
						itemData, connection, currentStudy, ub, true);
			}
			RuleActionRunLogBean ruleActionRunLog = new RuleActionRunLogBean(ruleAction.getActionType(), itemDataBean,
					itemDataBean.getValue(), ruleSetRule.getRuleBean().getOid());
			ruleActionRunLogDao.saveOrUpdate(ruleActionRunLog);
		}
		return null;
	}

	private RuleActionBean dryRun(RuleActionBean ruleAction, ItemDataBean itemDataBean, String itemData,
			StudyBean currentStudy, UserAccountBean ub) {
		return ruleAction;
	}

	public void execute(String message, int itemDataBeanId, String itemData, StudyBean currentStudy,
			UserAccountBean ub, Object... arguments) {
		getDiscrepancyNoteService().saveFieldNotes(message, itemDataBeanId, itemData, currentStudy, ub);
	}

	private DiscrepancyNoteService getDiscrepancyNoteService() {
		discrepancyNoteService = this.discrepancyNoteService != null ? discrepancyNoteService
				: new DiscrepancyNoteService(ds);
		return discrepancyNoteService;
	}

	public Object execute(SubmissionContext context) {

		// Do nothing
		return null;
	}

}
