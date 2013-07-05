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

/* 
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.control.submit;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.domain.Status;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.akaza.openclinica.service.rule.RuleSetServiceInterface;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({ "serial" })
public class ViewRuleSetServlet extends SecureController {

	private static String RULESET_ID = "ruleSetId";
	private static String RULESET = "ruleSet";
	private RuleSetServiceInterface ruleSetService;

	/**
     * 
     */
	@Override
	public void mayProceed() throws InsufficientPermissionException {
		if (ub.isSysAdmin()) {
			return;
		}
		if (currentRole.getRole().equals(Role.STUDY_DIRECTOR) || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_study_director"), "1");

	}

	@Override
	public void processRequest() throws Exception {

		String ruleSetId = request.getParameter(RULESET_ID);
		if (ruleSetId == null) {
			addPageMessage(respage.getString("please_choose_a_CRF_to_view"));
			forwardPage(Page.CRF_LIST);
		} else {
			RuleSetBean ruleSetBean = getRuleSetService().getRuleSetById(currentStudy, ruleSetId);
			Boolean firstTime = true;
			String validRuleSetRuleIds = "";
			for (int j = 0; j < ruleSetBean.getRuleSetRules().size(); j++) {
				RuleSetRuleBean rsr = ruleSetBean.getRuleSetRules().get(j);
				if (rsr.getStatus() == Status.AVAILABLE) {
					if (firstTime) {
						validRuleSetRuleIds += rsr.getId();
						firstTime = false;
					} else {
						validRuleSetRuleIds += "," + rsr.getId();
					}
				}

			}
			request.setAttribute("validRuleSetRuleIds", validRuleSetRuleIds);
			request.setAttribute("ruleSetRuleBeans", orderRuleSetRulesByStatus(ruleSetBean));
			request.setAttribute(RULESET, ruleSetBean);
			forwardPage(Page.VIEW_RULES);
		}
	}

	List<RuleSetRuleBean> orderRuleSetRulesByStatus(RuleSetBean ruleSet) {
		ArrayList<RuleSetRuleBean> availableRuleSetRules = new ArrayList<RuleSetRuleBean>();
		ArrayList<RuleSetRuleBean> nonAvailableRuleSetRules = new ArrayList<RuleSetRuleBean>();
		for (RuleSetRuleBean ruleSetRuleBean : ruleSet.getRuleSetRules()) {
			if (ruleSetRuleBean.getStatus() == Status.AVAILABLE) {
				availableRuleSetRules.add(ruleSetRuleBean);
			} else {
				nonAvailableRuleSetRules.add(ruleSetRuleBean);
			}
		}

		availableRuleSetRules.addAll(nonAvailableRuleSetRules);
		return availableRuleSetRules;

	}

	@Override
	protected String getAdminServlet() {
		if (ub.isSysAdmin()) {
			return SecureController.ADMIN_SERVLET_CODE;
		} else {
			return "";
		}
	}

	private RuleSetServiceInterface getRuleSetService() {
		ruleSetService = this.ruleSetService != null ? ruleSetService : (RuleSetServiceInterface) SpringServletAccess
				.getApplicationContext(context).getBean("ruleSetService");
		// TODO: Add getRequestURLMinusServletPath(),getContextPath()
		return ruleSetService;
	}

}
