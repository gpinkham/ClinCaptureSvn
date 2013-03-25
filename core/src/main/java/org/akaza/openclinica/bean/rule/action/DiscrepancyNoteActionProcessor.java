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

package org.akaza.openclinica.bean.rule.action;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.service.managestudy.DiscrepancyNoteService;
import org.akaza.openclinica.service.managestudy.IDiscrepancyNoteService;

import javax.sql.DataSource;

public class DiscrepancyNoteActionProcessor implements ActionProcessor {

	private DataSource ds;
	private IDiscrepancyNoteService discrepancyNoteService;

	public DiscrepancyNoteActionProcessor(DataSource ds) {
		this.ds = ds;
	}

	public void execute(RuleActionBean ruleAction, int itemDataBeanId, String itemData, StudyBean currentStudy,
			UserAccountBean ub, Object... arguments) {
		getDiscrepancyNoteService().saveFieldNotes(ruleAction.getCuratedMessage(), itemDataBeanId, itemData,
				currentStudy, ub);
	}

	private IDiscrepancyNoteService getDiscrepancyNoteService() {
		discrepancyNoteService = this.discrepancyNoteService != null ? discrepancyNoteService
				: new DiscrepancyNoteService(ds);
		return discrepancyNoteService;
	}

	public void setDiscrepancyNoteService(IDiscrepancyNoteService discrepancyNoteService) {
		this.discrepancyNoteService = discrepancyNoteService;
	}

}
