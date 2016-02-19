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
import org.akaza.openclinica.domain.Status;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleAuditBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Krikor Krumlian
 * 
 */
@Component
@SuppressWarnings("unused")
public class RemoveRuleSetServlet extends Controller {

	private static final long serialVersionUID = 1L;

	private static String RULESET_ID = "ruleSetId";
	private static String RULESET = "ruleSet";
	private static String ACTION = "action";

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin()) {
			return;
		}

		if (currentRole.getRole().equals(Role.STUDY_DIRECTOR) || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}

		addPageMessage(
				getResPage().getString("no_have_correct_privilege_current_study")
						+ getResPage().getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.LIST_DEFINITION_SERVLET,
				getResException().getString("not_study_director"), "1");

	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);

		String ruleSetId = request.getParameter(RULESET_ID);
		String action = request.getParameter(ACTION);
		if (ruleSetId == null) {
			addPageMessage(getResPage().getString("please_choose_a_CRF_to_view"), request);
			forwardPage(Page.CRF_LIST, request, response);
		} else {
			RuleSetBean ruleSetBean = getRuleSetService().getRuleSetById(currentStudy, ruleSetId);
			if (action != null && action.equals("confirm")) {
				request.setAttribute(RULESET, ruleSetBean);
				forwardPage(Page.REMOVE_RULE_SET, request, response);
			} else {
				for (RuleSetRuleBean ruleSetRuleBean : ruleSetBean.getRuleSetRules()) {
					if (ruleSetRuleBean.getStatus() != Status.DELETED) {
						ruleSetRuleBean.setStatus(Status.DELETED);
						ruleSetRuleBean.setUpdater(ub);
						ruleSetRuleBean = getRuleSetRuleDao().saveOrUpdate(ruleSetRuleBean);
						createRuleSetRuleAuditBean(ruleSetRuleBean, ub, Status.DELETED);
					}
				}
				forwardPage(Page.LIST_RULE_SETS_SERVLET, request, response);
			}
		}
	}

	private void createRuleSetRuleAuditBean(RuleSetRuleBean ruleSetRuleBean, UserAccountBean ub, Status status) {
		RuleSetRuleAuditBean ruleSetRuleAuditBean = new RuleSetRuleAuditBean();
		ruleSetRuleAuditBean.setRuleSetRuleBean(ruleSetRuleBean);
		ruleSetRuleAuditBean.setUpdater(ub);
		ruleSetRuleAuditBean.setStatus(status);
		getRuleSetRuleAuditDao().saveOrUpdate(ruleSetRuleAuditBean);
	}
}
