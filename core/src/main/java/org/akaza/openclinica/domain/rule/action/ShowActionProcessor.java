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

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.logic.rulerunner.ExecutionMode;
import org.akaza.openclinica.logic.rulerunner.RuleRunner.RuleRunnerMode;
import org.akaza.openclinica.service.crfdata.DynamicsMetadataService;

import com.clinovo.context.SubmissionContext;

import javax.sql.DataSource;
import java.sql.Connection;

public class ShowActionProcessor implements ActionProcessor {

	DataSource ds;
	DynamicsMetadataService itemMetadataService;
	RuleSetBean ruleSet;
	Connection con;

	public ShowActionProcessor(DataSource ds, DynamicsMetadataService itemMetadataService, RuleSetBean ruleSet) {
		this.itemMetadataService = itemMetadataService;
		this.ruleSet = ruleSet;
		this.ds = ds;
	}
	
	public ShowActionProcessor(DataSource ds, DynamicsMetadataService itemMetadataService, RuleSetBean ruleSet, Connection con) {
		this.itemMetadataService = itemMetadataService;
		this.ruleSet = ruleSet;
		this.ds = ds;
		this.con = con;
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
				return saveAndReturnMessage(ruleAction, itemDataBean, itemData, currentStudy, ub, con);
			} else {
				return save(ruleAction, itemDataBean, itemData, currentStudy, ub, con);
			}
		}
		default:
			return null;
		}
	}

	private RuleActionBean save(RuleActionBean ruleAction, ItemDataBean itemDataBean, String itemData,
			StudyBean currentStudy, UserAccountBean ub, Connection con) {
		getItemMetadataService().showNew(itemDataBean.getId(), ((ShowActionBean) ruleAction).getProperties(), ub,
				ruleSet, con);
		return ruleAction;
	}

	private RuleActionBean saveAndReturnMessage(RuleActionBean ruleAction, ItemDataBean itemDataBean, String itemData,
			StudyBean currentStudy, UserAccountBean ub, Connection con) {
		getItemMetadataService().showNew(itemDataBean.getId(), ((ShowActionBean) ruleAction).getProperties(), ub,
				ruleSet, con);
		return ruleAction;
	}

	private RuleActionBean dryRun(RuleActionBean ruleAction, ItemDataBean itemDataBean, String itemData,
			StudyBean currentStudy, UserAccountBean ub) {
		return ruleAction;
	}

	private DynamicsMetadataService getItemMetadataService() {
		return itemMetadataService;
	}
	
	public Object execute(SubmissionContext context) {
		
		// Do nothing
		return null;
	}

}
