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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.control.core.SpringServlet;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

/**
 * Prepares to create a new CRF Version.
 * 
 * @author jxu
 */
@Component
public class InitCreateCRFVersionServlet extends SpringServlet {
	/**
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @throws InsufficientPermissionException the exception.
	 */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin() || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}

		addPageMessage(
				getResPage().getString("no_have_correct_privilege_current_study")
						+ getResPage().getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, getResException().getString("not_study_director"), "1");
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		StudyInfoPanel panel = getStudyInfoPanel(request);
		panel.reset();
		panel.setStudyInfoShown(false);
		panel.setOrderedData(true);

		setToPanel(getResWord().getString("create_CRF"), getResPage().getString("br_create_new_CRF_entering"), request);

		setToPanel(getResWord().getString("create_CRF_version"), getResPage().getString("br_create_new_CRF_uploading"), request);
		setToPanel(getResWord().getString("revise_CRF_version"), getResPage().getString("br_if_you_owner_CRF_version"), request);
		setToPanel(getResWord().getString("CRF_spreadsheet_template"),
				getResPage().getString("br_download_blank_CRF_spreadsheet_from"), request);
		setToPanel(getResWord().getString("example_CRF_br_spreadsheets"),
				getResPage().getString("br_download_example_CRF_instructions_from"), request);

		String idString = request.getParameter("crfId");
		/*
		 * now that we have automated the choice of crf id, we need to get it from someplace else besides the
		 * request...this is throwing off the generation of filenames and other processes downstream, tbh 06/2008
		 */
		String name = request.getParameter("name");
		logger.info("*** ^^^ *** crf id:" + idString);

		if (StringUtil.isBlank(idString) || StringUtil.isBlank(name)) {
			addPageMessage(getResPage().getString("please_choose_a_CRF_to_add_new_version_for"), request);
			forwardPage(Page.CRF_LIST, request, response);
		} else {
			// crf id
			int crfId = Integer.valueOf(idString.trim());
			CRFVersionBean version = new CRFVersionBean();
			version.setCrfId(crfId);
			request.getSession().setAttribute("version", version);
			request.getSession().setAttribute("crfName", name);
			request.setAttribute("CrfId", crfId);
			forwardPage(Page.CREATE_CRF_VERSION, request, response);
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
