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
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.domain.EntityBeanTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Verify the Rule import , show records that have Errors as well as records that will be saved.
 * 
 * @author Krikor krumlian
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@Component
public class ViewRuleAssignmentServlet extends Controller {

	private static final long serialVersionUID = 9116068126651934226L;
	protected final Logger log = LoggerFactory.getLogger(ViewRuleAssignmentServlet.class);

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        StudyBean currentStudy = getCurrentStudy(request);

		FormProcessor fp = new FormProcessor(request);

		List<RuleSetBean> ruleSets = getRuleSetService().getRuleSetsByStudy(currentStudy);
		ruleSets = getRuleSetService().filterByStatusEqualsAvailableOnlyRuleSetRules(ruleSets);

		EntityBeanTable table = fp.getWebEntityBeanTable();
		ArrayList allRows = ViewRuleAssignmentRow.generateRowsFromBeans((ArrayList) ruleSets);

		String[] columns = { resword.getString("rule_study_event_definition"), resword.getString("CRF_name"),
				resword.getString("rule_group_label"), resword.getString("rule_item_name"),
				resword.getString("rule_rules"), resword.getString("rule_ref_oid"),
				resword.getString("rule_action_type"), resword.getString("actions") };

		table.setColumns(new ArrayList(Arrays.asList(columns)));
		table.hideColumnLink(4);
		table.hideColumnLink(5);
		table.hideColumnLink(6);
		table.setQuery("ViewRuleAssignment", new HashMap());
		table.addLink(resword.getString("test_rule_title"), "TestRule");
		table.setRows(allRows);
		table.computeDisplay();

		request.setAttribute("table", table);

		if (request.getParameter("read") != null && request.getParameter("read").equals("true")) {
			request.setAttribute("readOnly", true);
		}

		forwardPage(Page.VIEW_RULE_SETS, request, response);
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

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response) throws InsufficientPermissionException {
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
		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("may_not_submit_data"), "1");
	}
}