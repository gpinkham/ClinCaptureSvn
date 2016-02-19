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
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.SpringServlet;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.domain.rule.RuleBulkExecuteContainer;
import org.akaza.openclinica.domain.rule.RuleBulkExecuteContainerTwo;
import org.akaza.openclinica.logic.rulerunner.ExecutionMode;
import org.akaza.openclinica.service.rule.RuleSetService;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Set;

/**
 * Run Rules Using this Servlet.
 * 
 * @author Krikor krumlian
 */
@Component
@SuppressWarnings("unused")
public class RunRuleServlet extends SpringServlet {
	private static final long serialVersionUID = 9116068126651934226L;

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);

		String action = request.getParameter("action");
		String crfId = request.getParameter("crfId");
		String ruleSetRuleId = request.getParameter("ruleSetRuleId");
		String versionId = request.getParameter("versionId");

		if (StringUtil.isBlank(action)) {
			forwardPage(Page.MENU_SERVLET, request, response);
			return;
		}
		ExecutionMode executionMode = action == null || "dryRun".equalsIgnoreCase(action) ? ExecutionMode.DRY_RUN
				: ExecutionMode.SAVE;
		String submitLinkParams = "";
		RuleSetService ruleSetService = getRuleSetService();
		HashMap<RuleBulkExecuteContainer, HashMap<RuleBulkExecuteContainerTwo, Set<String>>> result = null;
		if (ruleSetRuleId != null && versionId != null) {
			submitLinkParams = "ruleSetRuleId=" + ruleSetRuleId + "&versionId=" + versionId + "&action=no";
			result = ruleSetService.runRulesInBulk(ruleSetRuleId, versionId, executionMode, currentStudy, ub);
		} else {
			submitLinkParams = "crfId=" + crfId + "&action=no";
			result = ruleSetService.runRulesInBulk(crfId, executionMode, currentStudy, ub);
		}

		request.setAttribute("result", result);
		request.setAttribute("submitLinkParams", submitLinkParams);
		if (executionMode == ExecutionMode.SAVE) {
			forwardPage(Page.LIST_RULE_SETS_SERVLET, request, response);
		} else {
			forwardPage(Page.VIEW_EXECUTED_RULES_FROM_CRF, request, response);
		}
	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		UserAccountBean ub = getUserAccountBean(request);
		if (ub.isSysAdmin()) {
			return SpringServlet.ADMIN_SERVLET_CODE;
		} else {
			return "";
		}
	}

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin()) {
			return;
		}
		Role r = currentRole.getRole();
		if (r.equals(Role.STUDY_DIRECTOR) || r.equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}
		addPageMessage(
				getResPage().getString("no_have_correct_privilege_current_study")
						+ getResPage().getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, getResException().getString("may_not_submit_data"), "1");
	}
}
