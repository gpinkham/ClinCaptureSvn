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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Locale;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.domain.rule.RulesPostImportContainer;
import org.akaza.openclinica.service.rule.RuleSetServiceInterface;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

/**
 * View the uploaded data and verify what is going to be saved into the system and what is not.
 * 
 * @author Krikor Krumlian
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class VerifyImportedRuleServlet extends SecureController {

	Locale locale;
	RuleSetServiceInterface ruleSetService;

	@Override
	public void processRequest() throws Exception {

		String action = request.getParameter("action");
		if ("confirm".equalsIgnoreCase(action)) {
			RulesPostImportContainer rulesContainer = (RulesPostImportContainer) session.getAttribute("importedData");
			logger.info("Size of ruleDefs : " + rulesContainer.getRuleDefs().size());
			logger.info("Size of ruleSets : " + rulesContainer.getRuleSets().size());
			forwardPage(Page.VERIFY_RULES_IMPORT);
		}

		if ("save".equalsIgnoreCase(action)) {
			RulesPostImportContainer rulesContainer = (RulesPostImportContainer) session.getAttribute("importedData");
			getRuleSetService().saveImport(rulesContainer);
			MessageFormat mf = new MessageFormat("");
			mf.applyPattern(resword.getString("successful_rule_upload"));

			Object[] arguments = {
					rulesContainer.getValidRuleDefs().size() + rulesContainer.getDuplicateRuleDefs().size(),
					rulesContainer.getValidRuleSetDefs().size() + rulesContainer.getDuplicateRuleSetDefs().size() };
			addPageMessage(mf.format(arguments));
			ArrayList pageMessages = (ArrayList) request.getAttribute(PAGE_MESSAGE);
			session.setAttribute("pageMessages", pageMessages);
			response.sendRedirect(request.getContextPath() + Page.MANAGE_STUDY_MODULE);
		}
	}

	private RuleSetServiceInterface getRuleSetService() {
		ruleSetService = this.ruleSetService != null ? ruleSetService : (RuleSetServiceInterface) SpringServletAccess
				.getApplicationContext(context).getBean("ruleSetService");
		return ruleSetService;
	}

	@Override
	public void mayProceed() throws InsufficientPermissionException {

		locale = request.getLocale();
		if (ub.isSysAdmin()) {
			return;
		}

		Role r = currentRole.getRole();
		if (r.equals(Role.STUDY_DIRECTOR) || r.equals(Role.STUDY_ADMINISTRATOR) || r.equals(Role.INVESTIGATOR)
				|| r.equals(Role.CLINICAL_RESEARCH_COORDINATOR)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("may_not_submit_data"), "1");
	}
}
