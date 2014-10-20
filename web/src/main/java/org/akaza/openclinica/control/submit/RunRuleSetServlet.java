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
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.domain.rule.RuleSetBasedViewContainer;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.service.rule.RuleSetService;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class used to run rules set.
 */
@SuppressWarnings({ "serial" })
@Component
public class RunRuleSetServlet extends Controller {

	private static final String RULESET_ID = "ruleSetId";
	private static final String RULE_ID = "ruleId";
	private static final String RULESET = "ruleSet";
	private static final String RULESET_RESULT = "ruleSetResult";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin()) {
			return;
		}
		if (currentRole.getRole().equals(Role.STUDY_DIRECTOR) || currentRole.getRole()
				.equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_study_director"), "1");

	}

	@SuppressWarnings("unused")
	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);
		StudyBean currentStudy = getCurrentStudy(request);

		String ruleSetId = request.getParameter(RULESET_ID);
		String ruleId = request.getParameter(RULE_ID);
		String dryRun = request.getParameter("dryRun");

		RuleSetBean ruleSetBean = getRuleSetBean(currentStudy, ruleSetId, ruleId);
		if (ruleSetBean != null) {
			List<RuleSetBean> ruleSets = new ArrayList<RuleSetBean>();
			ruleSets.add(ruleSetBean);
			if (dryRun != null && dryRun.equals("no")) {
				List<RuleSetBasedViewContainer> resultOfRunningRules = getRuleSetService()
						.runRulesInBulk(ruleSets, false, currentStudy, ub);
				addPageMessage(respage.getString("actions_successfully_taken"), request);
				forwardPage(Page.LIST_RULE_SETS_SERVLET, request, response);

			} else {
				List<RuleSetBasedViewContainer> resultOfRunningRules = getRuleSetService().runRulesInBulk(ruleSets,
						true, currentStudy, ub);
				request.setAttribute(RULESET, ruleSetBean);
				request.setAttribute(RULESET_RESULT, resultOfRunningRules);
				if (resultOfRunningRules.size() > 0) {
					addPageMessage(resword.getString("view_executed_rules_affected_subjects"), request);
				} else {
					addPageMessage(resword.getString("view_executed_rules_no_affected_subjects"), request);
				}

				forwardPage(Page.VIEW_EXECUTED_RULES, request, response);

			}

		} else {
			addPageMessage("RuleSet not found", request);
			forwardPage(Page.LIST_RULE_SETS_SERVLET, request, response);
		}
	}

	private RuleSetBean getRuleSetBean(StudyBean currentStudy, String ruleSetId, String ruleId) {
		RuleSetBean ruleSetBean = null;
		if (ruleId != null && ruleSetId != null && ruleId.length() > 0 && ruleSetId.length() > 0) {
			ruleSetBean = getRuleSetService().getRuleSetById(currentStudy, ruleSetId);
			ruleSetBean = getRuleSetService().filterByRules(ruleSetBean, Integer.valueOf(ruleId));
		} else if (ruleSetId != null && ruleSetId.length() > 0) {
			ruleSetBean = getRuleSetService().getRuleSetById(currentStudy, ruleSetId);
		}
		return ruleSetBean;
	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		UserAccountBean ub = getUserAccountBean(request);
		if (ub.isSysAdmin()) {
			return Controller.ADMIN_SERVLET_CODE;
		} else {
			return "";
		}
	}

	@SuppressWarnings("unused")
	private RuleSetService getRuleSetService(HttpServletRequest request) {
		String requestUrl =
				request.getScheme() + "://" + request.getSession().getAttribute(DOMAIN_NAME) + request.getRequestURI()
						.replaceAll(request.getServletPath(), "");
		RuleSetService ruleSetService = getRuleSetService();
		ruleSetService.setContextPath(getContextPath(request));
		ruleSetService.setMailSender(getMailSender());
		ruleSetService.setRequestURLMinusServletPath(requestUrl);
		return ruleSetService;
	}
}
