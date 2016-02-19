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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.SpringServlet;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

/**
 * @author jxu
 *
 *         Removes a site from a study
 */
@Component
public class RemoveSiteServlet extends SpringServlet {

	/**
	 *
	 */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {

		checkStudyLocked(Page.SITE_LIST_SERVLET, getResPage().getString("current_study_locked"), request, response);
		if (getUserAccountBean(request).isSysAdmin()
				|| getCurrentRole(request).getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}

		addPageMessage(getResPage().getString("no_have_correct_privilege_current_study")
				+ getResPage().getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.SITE_LIST_SERVLET, getResException().getString("not_study_director"),
				"1");
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		StudyDAO sdao = getStudyDAO();
		String idString = request.getParameter("id");
		logger.info("site id:" + idString);

		StudyBean currentStudy = getCurrentStudy(request);
		UserAccountBean currentUser = getUserAccountBean(request);

		int siteId = Integer.valueOf(idString.trim());
		StudyBean site = (StudyBean) sdao.findByPK(siteId);
		if (currentStudy.getId() != site.getParentStudyId()) {
			addPageMessage(getResPage().getString("no_have_correct_privilege_current_study") + " "
					+ getResPage().getString("change_active_study_or_contact"), request);
			forwardPage(Page.MENU_SERVLET, request, response);
			return;
		}

		String action = request.getParameter("action");
		if (StringUtil.isBlank(idString)) {
			addPageMessage(getResPage().getString("please_choose_a_site_to_remove"), request);
			forwardPage(Page.SITE_LIST_SERVLET, request, response);
		} else {
			if ("confirm".equalsIgnoreCase(action)) {
				request.setAttribute("siteToRemove", site);

				request.setAttribute("userRolesToRemove", getUserAccountDAO().findAllByStudyId(siteId));

				request.setAttribute("subjectsToRemove", getStudySubjectDAO().findAllByStudy(site));

				forwardPage(Page.REMOVE_SITE, request, response);
			} else {
				logger.info("submit to remove the site");

				getStudyService().removeSite(site, currentUser);

				addPageMessage(getResPage().getString("this_site_has_been_removed_succesfully"), request);

				String fromListSite = (String) request.getSession().getAttribute("fromListSite");
				if (fromListSite != null && fromListSite.equals("yes")) {
					request.getSession().removeAttribute("fromListSite");
					forwardPage(Page.SITE_LIST_SERVLET, request, response);
				} else {
					request.getSession().removeAttribute("fromListSite");
					forwardPage(Page.STUDY_LIST_SERVLET, request, response);
				}

			}
		}

	}

}
