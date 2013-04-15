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

import java.sql.Connection;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.logic.rulerunner.ExecutionMode;
import org.akaza.openclinica.logic.rulerunner.RuleRunner.RuleRunnerMode;
import org.akaza.openclinica.service.crfdata.DynamicsMetadataService;

import javax.sql.DataSource;

public class HideActionProcessor implements ActionProcessor {

	DataSource ds;
	DynamicsMetadataService dynamicsMetadataService;
	RuleSetBean ruleSet;
	Connection con;
	

	public HideActionProcessor(DataSource ds, DynamicsMetadataService dynamicsMetadataService, RuleSetBean ruleSet) {
		this.dynamicsMetadataService = dynamicsMetadataService;
		this.ds = ds;
		this.ruleSet = ruleSet;
	}
	
	public HideActionProcessor(DataSource ds, DynamicsMetadataService dynamicsMetadataService, RuleSetBean ruleSet, Connection con) {
		this.dynamicsMetadataService = dynamicsMetadataService;
		this.ds = ds;
		this.ruleSet = ruleSet;
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
		getDynamicsMetadataService().hideNew(itemDataBean.getId(), ((HideActionBean) ruleAction).getProperties(), ub,
				ruleSet, con);
		return ruleAction;
	}

	private RuleActionBean saveAndReturnMessage(RuleActionBean ruleAction, ItemDataBean itemDataBean, String itemData,
			StudyBean currentStudy, UserAccountBean ub, Connection con) {
		getDynamicsMetadataService().hideNew(itemDataBean.getId(), ((HideActionBean) ruleAction).getProperties(), ub,
				ruleSet, con);
		return ruleAction;
	}

	private RuleActionBean dryRun(RuleActionBean ruleAction, ItemDataBean itemDataBean, String itemData,
			StudyBean currentStudy, UserAccountBean ub) {
		return ruleAction;
	}

	private DynamicsMetadataService getDynamicsMetadataService() {
		return dynamicsMetadataService;
	}

}
