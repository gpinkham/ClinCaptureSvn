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

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

/**
 * Processes the request of removing a top level study, all the data assoicated with this study will be removed.
 * 
 * @author jxu
 * 
 */
@SuppressWarnings({"serial"})
@Component
public class RemoveStudyServlet extends Controller {
	/**
	 *
	 */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {

		if (getUserAccountBean(request).isSysAdmin()) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.STUDY_LIST_SERVLET, resexception.getString("not_admin"), "1");
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		StudyDAO studyDao = getStudyDAO();
		FormProcessor fp = new FormProcessor(request);
		int studyId = fp.getInt("id");
		StudyBean currentStudy = getCurrentStudy(request);
		UserAccountBean currentUser = getUserAccountBean(request);

		// it's impossible to remove the current study
		if ((currentStudy.getParentStudyId() > 0 && currentStudy.getParentStudyId() == studyId)
				|| (currentStudy.getId() == studyId)) {
			addPageMessage(resword.getString("you_are_trying_to_remove_the_current_study"), request);
			forwardPage(Page.STUDY_LIST_SERVLET, request, response);
			return;
		}

		StudyBean study = (StudyBean) studyDao.findByPK(studyId);

		String action = request.getParameter("action");
		if (studyId == 0) {
			addPageMessage(respage.getString("please_choose_a_study_to_remove"), request);
			forwardPage(Page.STUDY_LIST_SERVLET, request, response);
		} else {
			if ("confirm".equalsIgnoreCase(action)) {
				request.setAttribute("studyToRemove", study);
				request.setAttribute("sitesToRemove", studyDao.findAllByParent(studyId));
				request.setAttribute("userRolesToRemove", getUserAccountDAO().findAllByStudyId(studyId));
				request.setAttribute("subjectsToRemove", getStudySubjectDAO().findAllByStudy(study));
				request.setAttribute("definitionsToRemove", getStudyEventDefinitionDAO().findAllByStudy(study));
				forwardPage(Page.REMOVE_STUDY, request, response);
			} else {
				logger.info("submit to remove the study");

				getStudyService().removeStudy(study, currentUser);

				addPageMessage(resexception.getString("this_study_has_been_removed_succesfully"), request);
				forwardPage(Page.STUDY_LIST_SERVLET, request, response);

			}
		}

	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		return Controller.ADMIN_SERVLET_CODE;
	}
}
