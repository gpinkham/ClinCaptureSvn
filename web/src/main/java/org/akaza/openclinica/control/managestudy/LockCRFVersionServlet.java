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
package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.control.core.SpringServlet;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

/**
 * 
 * Locks CRF version.
 * 
 */
@Component
@SuppressWarnings("rawtypes")
public class LockCRFVersionServlet extends SpringServlet {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		if (userCanLockCRFVersion(request)) {
			return;
		}
		addPageMessage(getResPage().getString("no_have_correct_privilege_current_study")
				+ getResPage().getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, getResException().getString("not_study_director"), "1");

	}

	private boolean userCanLockCRFVersion(HttpServletRequest request) {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);
		return ub.isSysAdmin() || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR);
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		UserAccountBean ub = getUserAccountBean(request);

		FormProcessor fp = new FormProcessor(request);

		int crfVersionId = fp.getInt("id", true);
		String action = fp.getString("action");

		if (crfVersionId == 0) {
			addPageMessage(getResPage().getString("choose_valid_crf_version"), request);
			forwardPage(Page.CRF_LIST_SERVLET, request, response);
			return;
		}

		CRFVersionDAO cvdao = getCRFVersionDAO();
		CRFDAO cdao = getCRFDAO();

		CRFVersionBean version = (CRFVersionBean) cvdao.findByPK(crfVersionId);
		CRFBean crf = (CRFBean) cdao.findByPK(version.getCrfId());

		if (!userCanLockCRFVersion(request)) {
			addPageMessage(getResPage().getString("no_have_correct_privilege_current_study") + " "
					+ getResPage().getString("change_active_study_or_contact"), request);
			forwardPage(Page.MENU_SERVLET, request, response);
			return;
		}

		EventCRFDAO ecdao = getEventCRFDAO();
		ArrayList eventCRFs = ecdao.findAllStudySubjectByCRFVersion(crfVersionId);

		if (StringUtil.isBlank(action)) {
			request.setAttribute("crfVersionToLock", version);
			request.setAttribute("crf", crf);
			request.setAttribute("eventSubjectsUsingVersion", eventCRFs);
			forwardPage(Page.CONFIRM_LOCKING_CRF_VERSION, request, response);
		} else if ("confirm".equalsIgnoreCase(action)) {

			getCrfVersionService().lockCrfVersion(version, ub);

			addPageMessage(getResPage().getString("crf_version_locked_successfully"), request);
			forwardPage(Page.CRF_LIST_SERVLET, request, response);
		}
	}

}
