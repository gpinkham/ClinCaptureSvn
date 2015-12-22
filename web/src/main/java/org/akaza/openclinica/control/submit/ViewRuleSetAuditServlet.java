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
import org.akaza.openclinica.domain.rule.RuleSetAuditBean;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleAuditBean;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@SuppressWarnings({ "serial" })
@Component
public class ViewRuleSetAuditServlet extends Controller {

	private static String RULESET_ID = "ruleSetId";
	private static String RULESET = "ruleSet";
	private static String RULESETAUDITS = "ruleSetAudits";
	private static String RULESETRULEAUDITS = "ruleSetRuleAudits";

	/**
	 *
	 * @param request
	 * @param response
	 */
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
		throw new InsufficientPermissionException(Page.MENU_SERVLET, getResException().getString("not_study_director"), "1");

	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		StudyBean currentStudy = getCurrentStudy(request);

		String ruleSetId = request.getParameter(RULESET_ID);
		if (ruleSetId == null) {
			addPageMessage(getResPage().getString("please_choose_a_CRF_to_view"), request);
			forwardPage(Page.CRF_LIST, request, response);
		} else {
			RuleSetBean ruleSetBean = getRuleSetService().getRuleSetById(currentStudy, ruleSetId);
			List<RuleSetAuditBean> ruleSetAudits = getRuleSetAuditDao().findAllByRuleSet(ruleSetBean);
			List<RuleSetRuleAuditBean> ruleSetRuleAudits = getRuleSetRuleAuditDao().findAllByRuleSet(ruleSetBean);
			for (RuleSetRuleAuditBean ruleSetRuleAuditBean : ruleSetRuleAudits) {
				ruleSetRuleAuditBean.setUpdater((UserAccountBean) getUserAccountDAO().findByPK(
						ruleSetRuleAuditBean.getUpdaterId()));
			}
			for (RuleSetAuditBean ruleSetAudit : ruleSetAudits) {
				ruleSetAudit.setUpdater((UserAccountBean) getUserAccountDAO().findByPK(ruleSetAudit.getUpdaterId()));
			}
			request.setAttribute(RULESET, ruleSetBean);
			request.setAttribute(RULESETAUDITS, ruleSetAudits);
			request.setAttribute(RULESETRULEAUDITS, ruleSetRuleAudits);
			forwardPage(Page.VIEW_RULESET_AUDITS, request, response);
		}
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
}
