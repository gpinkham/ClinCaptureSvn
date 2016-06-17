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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.control.core.RememberLastPage;
import org.akaza.openclinica.control.core.SpringServlet;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.service.crfdata.HideCRFManager;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Verify the Rule import , show records that have Errors as well as records that will be saved.
 * 
 * @author Krikor krumlian
 */
@Component
@SuppressWarnings("unused")
public class ViewRuleAssignmentNewServlet extends RememberLastPage {

	private static final long serialVersionUID = 9116068126651934226L;

	private final Logger log = LoggerFactory.getLogger(ViewRuleAssignmentNewServlet.class);

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (shouldRedirect(request, response)) {
			return;
		}
		FormProcessor fp = new FormProcessor(request);
		boolean showMoreLink = fp.getString("showMoreLink").equals("") || Boolean.parseBoolean(fp.getString("showMoreLink"));
		createTable(request, response, showMoreLink, false);

	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private void createStudyEventForInfoPanel(HttpServletRequest request) {
		StudyBean currentStudy = getCurrentStudy(request);

		StudyEventDefinitionDAO seddao = getStudyEventDefinitionDAO();
		ItemDAO itemdao = getItemDAO();
		StudyBean studyWithEventDefinitions = currentStudy;
		if (currentStudy.getParentStudyId() > 0) {
			studyWithEventDefinitions = new StudyBean();
			studyWithEventDefinitions.setId(currentStudy.getParentStudyId());

		}
		CRFDAO crfdao = getCRFDAO();
		ArrayList seds = seddao.findAllActiveByStudy(studyWithEventDefinitions);

		HashMap events = new LinkedHashMap();
		for (int i = 0; i < seds.size(); i++) {
			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seds.get(i);
			ArrayList<CRFBean> crfs = (ArrayList<CRFBean>) crfdao.findAllActiveByDefinition(sed);

			if (currentStudy.getParentStudyId() > 0) {
				// sift through these CRFs and see which ones are hidden
				HideCRFManager hideCRFs = HideCRFManager.createHideCRFManager();
				crfs = hideCRFs.removeHiddenCRFBeans(studyWithEventDefinitions, sed, crfs, getDataSource());
			}

			if (!crfs.isEmpty()) {
				events.put(sed, crfs);
			}
		}
		request.setAttribute("eventlist", events);
		request.setAttribute("itemCount", itemdao.getCountOfActiveItems());
		request.setAttribute("crfCount", crfdao.getCountOfActiveCRFs(currentStudy));
		request.setAttribute("ruleSetCount", getRuleSetService().getRuleSetDao().count(currentStudy));

	}

	private void createTable(HttpServletRequest request, HttpServletResponse response, boolean showMoreLink,
			boolean isDesigner) {
		StudyBean currentStudy = getCurrentStudy(request);

		log.debug("Creating table");

		ViewRuleAssignmentTableFactory factory = new ViewRuleAssignmentTableFactory(showMoreLink, isDesigner);

		factory.setRuleSetService(getRuleSetService());
		factory.setItemFormMetadataDAO(getItemFormMetadataDAO());
		factory.setCurrentStudy(currentStudy);

		// Datasource needed for pulling extra model objects from db
		factory.setDataSource(getDataSource());

		factory.setCurrentUser(((UserAccountBean) request.getSession().getAttribute(USER_BEAN_NAME)));

		String ruleAssignmentsHtml = factory.createTable(request, response).render();
		request.setAttribute("ruleAssignmentsHtml", ruleAssignmentsHtml);
		createStudyEventForInfoPanel(request);
		if (ruleAssignmentsHtml != null) {
			forwardPage(Page.VIEW_RULE_SETS2, request, response);
		}

	}

	/**
	 * Returns context path.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return String
	 */
	public String getContextPath(HttpServletRequest request) {
		String contextPath = request.getContextPath().replaceAll("/", "");
		return contextPath;
	}

	/**
	 * Returns host path.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return String
	 */
	public String getHostPath(HttpServletRequest request) {
		String requestURLMinusServletPath = getRequestURLMinusServletPath(request);
		String hostPath = "";
		if (null != requestURLMinusServletPath) {
			hostPath = requestURLMinusServletPath.substring(0, requestURLMinusServletPath.lastIndexOf("/"));
		}
		return hostPath;
	}

	/**
	 * Returns request url without servlet path in it.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return String
	 */
	public String getRequestURLMinusServletPath(HttpServletRequest request) {
		return request.getRequestURL().toString().replaceAll(request.getServletPath(), "");
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
		addPageMessage(getResPage().getString("no_have_correct_privilege_current_study")
				+ getResPage().getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, getResException().getString("may_not_submit_data"),
				"1");
	}

	@Override
	protected String getDefaultUrl(HttpServletRequest request) {

		FormProcessor fp = new FormProcessor(request);
		return "?module=" + fp.getString("module")
				+ "&maxRows=15&showMoreLink=true&ruleAssignments_tr_=true&ruleAssignments_p_=1&ruleAssignments_mr_=15";
	}

	@Override
	protected boolean userDoesNotUseJmesaTableForNavigation(HttpServletRequest request) {
		return request.getQueryString() == null || !request.getQueryString().contains("ruleAssignments");
	}

	@Override
	protected String getSavedUrl(String key, HttpServletRequest request) {
		String savedUrl = (String) request.getSession().getAttribute(key);
		return savedUrl == null
				? savedUrl
				: savedUrl.replace("&ruleAssignments_e_=pdf", "").replace("&ruleAssignments_e_=jexcel", "");
	}
}
