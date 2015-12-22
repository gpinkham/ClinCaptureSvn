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
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.domain.rule.RulesPostImportContainer;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;
import java.util.ArrayList;

/**
 * View the uploaded data and verify what is going to be saved into the system and what is not.
 * 
 * @author Krikor Krumlian
 */
@SuppressWarnings({ "rawtypes", "serial" })
@Component
public class VerifyImportedRuleServlet extends Controller {

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String action = request.getParameter("action");
		if ("confirm".equalsIgnoreCase(action)) {
			RulesPostImportContainer rulesContainer = (RulesPostImportContainer) request.getSession().getAttribute(
					"importedData");
			logger.info("Size of ruleDefs : " + rulesContainer.getRuleDefs().size());
			logger.info("Size of ruleSets : " + rulesContainer.getRuleSets().size());
			forwardPage(Page.VERIFY_RULES_IMPORT, request, response);
		}

		if ("save".equalsIgnoreCase(action)) {
			RulesPostImportContainer rulesContainer = (RulesPostImportContainer) request.getSession().getAttribute(
					"importedData");
			getRuleSetService().saveImport(rulesContainer);
			MessageFormat mf = new MessageFormat("");
			mf.applyPattern(getResWord().getString("successful_rule_upload"));

			Object[] arguments = {
					rulesContainer.getValidRuleDefs().size() + rulesContainer.getDuplicateRuleDefs().size(),
					rulesContainer.getValidRuleSetDefs().size() + rulesContainer.getDuplicateRuleSetDefs().size() };
			addPageMessage(mf.format(arguments), request);
			ArrayList pageMessages = (ArrayList) request.getAttribute(PAGE_MESSAGE);
			request.getSession().setAttribute("pageMessages", pageMessages);
			response.sendRedirect(request.getContextPath() + Page.MANAGE_STUDY_MODULE);
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
		if (r.equals(Role.STUDY_DIRECTOR) || r.equals(Role.STUDY_ADMINISTRATOR) || r.equals(Role.INVESTIGATOR)
				|| r.equals(Role.CLINICAL_RESEARCH_COORDINATOR)) {
			return;
		}

		addPageMessage(
				getResPage().getString("no_have_correct_privilege_current_study")
						+ getResPage().getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, getResException().getString("may_not_submit_data"), "1");
	}
}
