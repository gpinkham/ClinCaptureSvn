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

import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

/**
 * Restores a removed site and all its data, including users. roles, study groups, definitions, events and items
 *
 * @author jxu
 *
 */
@SuppressWarnings({"serial"})
@Component
public class RestoreSiteServlet extends Controller {
	/**
	 *
	 */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {

		checkStudyLocked(Page.SITE_LIST_SERVLET, respage.getString("current_study_locked"), request, response);

		if (getUserAccountBean(request).isSysAdmin()
				|| getCurrentRole(request).getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.SITE_LIST_SERVLET, resexception.getString("not_study_director"),
				"1");

	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		StudyDAO sdao = getStudyDAO();
		String idString = request.getParameter("id");
		logger.info("site id:" + idString);

		UserAccountBean currentUser = getUserAccountBean(request);

		int siteId = Integer.valueOf(idString.trim());
		StudyBean site = (StudyBean) sdao.findByPK(siteId);

		String action = request.getParameter("action");
		if (StringUtil.isBlank(idString)) {
			addPageMessage(respage.getString("please_choose_a_site_to_restore"), request);
			forwardPage(Page.SITE_LIST_SERVLET, request, response);
		} else {
			if ("confirm".equalsIgnoreCase(action)) {
				// site can be restored when its parent study is not "removed"
				StudyBean parentstudy = (StudyBean) sdao.findByPK(site.getParentStudyId());
				if (!"removed".equals(parentstudy.getStatus().getName())) {
					request.setAttribute("siteToRestore", site);

					request.setAttribute("userRolesToRestore", getUserAccountDAO().findAllByStudyId(siteId));

					request.setAttribute("subjectsToRestore", getStudySubjectDAO().findAllByStudy(site));

				} else {
					MessageFormat mf = new MessageFormat("");
					mf.applyPattern(respage.getString("choosen_site_cannot_restored"));
					Object[] arguments = {site.getName(), parentstudy.getName()};
					addPageMessage(mf.format(arguments), request);
					forwardPage(Page.STUDY_LIST_SERVLET, request, response);
				}
				forwardPage(Page.RESTORE_SITE, request, response);
			} else {
				logger.info("submit to restore the site");

				getStudyService().restoreSite(site, currentUser);

				addPageMessage(respage.getString("this_site_has_been_restored_succesfully"), request);
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
