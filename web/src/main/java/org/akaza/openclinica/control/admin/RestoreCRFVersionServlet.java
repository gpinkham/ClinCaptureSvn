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
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.control.core.SpringServlet;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

/**
 * 
 * Restores removed CRF Version.
 * 
 */
@Component
@SuppressWarnings("rawtypes")
public class RestoreCRFVersionServlet extends SpringServlet {

	private static final String CRF_VERSION_ID_PARAMETER = "id";

	private static final String ACTION_PARAMETER = "action";

	private static final String CONFIRM_PAGE_PASSED_PARAMETER = "confirmPagePassed";

	private static final String ACTION_CONFIRM = "confirm";

	private static final String ACTION_SUBMIT = "submit";

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		if (userCanRestoreCRFVersion(request)) {
			return;
		}
		addPageMessage(getResPage().getString("no_have_correct_privilege_current_study")
				+ getResPage().getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.CRF_LIST_SERVLET, getResException().getString("not_admin"), "1");
	}

	private boolean userCanRestoreCRFVersion(HttpServletRequest request) {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin() || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		UserAccountBean currentUser = getUserAccountBean(request);

		FormProcessor fp = new FormProcessor(request);
		int versionId = fp.getInt(CRF_VERSION_ID_PARAMETER, true);
		String action = fp.getString(ACTION_PARAMETER);
		String keyValue = (String) request.getSession().getAttribute("savedListCRFsUrl");

		CRFVersionDAO cvdao = getCRFVersionDAO();
		CRFVersionBean version = (CRFVersionBean) cvdao.findByPK(versionId);
		EventCRFDAO evdao;
		List<EventCRFBean> eventCRFs;

		if (version.getId() != 0 && !StringUtil.isBlank(action)) {

			evdao = getEventCRFDAO();
			// find all event crfs by version id
			eventCRFs = evdao.findAllByCRFVersion(versionId);

			if (ACTION_CONFIRM.equalsIgnoreCase(action)) {
				if (!userCanRestoreCRFVersion(request)) {
					addPageMessage(getResPage().getString("no_have_correct_privilege_current_study") + " "
							+ getResPage().getString("change_active_study_or_contact"), request);
					forwardPage(Page.MENU_SERVLET, request, response);
					return;
				}

				request.setAttribute("versionToRestore", version);
				request.setAttribute("eventCRFs", eventCRFs);
				forwardPage(Page.RESTORE_CRF_VERSION, request, response);
				return;
			} else
				if (ACTION_SUBMIT.equalsIgnoreCase(action)
						&& !fp.getString(CONFIRM_PAGE_PASSED_PARAMETER).equals(FormProcessor.DEFAULT_STRING)) {
				logger.info("submit to restore the crf version");

				getCrfVersionService().restoreCrfVersion(version, currentUser);

				addPageMessage(
						new StringBuilder("").append(getResPage().getString("the_CRF_version")).append(version.getName())
								.append(" ").append(getResPage().getString("has_been_restored_succesfully")).toString(),
						request);

			} else {
				addPageMessage(getResPage().getString("invalid_http_request_parameters"), request);
			}
		} else {
			addPageMessage(getResPage().getString("invalid_http_request_parameters"), request);
		}

		if (keyValue != null) {
			Map storedAttributes = new HashMap();
			storedAttributes.put(SpringServlet.PAGE_MESSAGE, request.getAttribute(SpringServlet.PAGE_MESSAGE));
			request.getSession().setAttribute(STORED_ATTRIBUTES, storedAttributes);
			response.sendRedirect(response.encodeRedirectURL(keyValue));
		} else {
			forwardPage(Page.CRF_LIST_SERVLET, request, response);
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
}
