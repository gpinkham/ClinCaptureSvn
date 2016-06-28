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

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DisplayStudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.control.core.RememberLastPage;
import org.akaza.openclinica.control.core.SpringServlet;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

/**
 * @author jxu
 * 
 *         Removes a study event and all its related event CRFs, items
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@Component
public class RemoveStudyEventServlet extends SpringServlet {

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {

		checkStudyLocked(Page.LIST_STUDY_SUBJECTS, getResPage().getString("current_study_locked"), request, response);
		checkStudyFrozen(Page.LIST_STUDY_SUBJECTS, getResPage().getString("current_study_frozen"), request, response);

		if (getUserAccountBean(request).isSysAdmin() || getCurrentRole(request).isStudyAdministrator()) {
			return;
		}

		addPageMessage(getResPage().getString("no_have_correct_privilege_current_study")
				+ getResPage().getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, getResException().getString("not_study_director"),
				"1");

	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		FormProcessor fp = new FormProcessor(request);
		// studyEventId
		int studyEventId = fp.getInt("id");
		// studySubjectId
		int studySubId = fp.getInt("studySubId");
		UserAccountBean currentUser = getUserAccountBean(request);

		StudyEventDAO sedao = getStudyEventDAO();
		StudySubjectDAO subdao = getStudySubjectDAO();

		if (studyEventId == 0) {
			addPageMessage(getResPage().getString("please_choose_a_SE_to_remove"), request);
			request.setAttribute("id", Integer.toString(studySubId));
			forwardToViewStudySubjectPage(request, response);
		} else {

			StudyEventBean event = (StudyEventBean) sedao.findByPK(studyEventId);

			StudySubjectBean studySub = (StudySubjectBean) subdao.findByPK(studySubId);
			request.setAttribute("studySub", studySub);

			StudyEventDefinitionDAO seddao = getStudyEventDefinitionDAO();
			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao
					.findByPK(event.getStudyEventDefinitionId());
			event.setStudyEventDefinition(sed);

			StudyDAO studydao = getStudyDAO();
			StudyBean study = (StudyBean) studydao.findByPK(studySub.getStudyId());

			String action = request.getParameter("action");
			if ("confirm".equalsIgnoreCase(action)) {
				ArrayList eventCRFs = getEventCRFDAO().findAllByStudyEvent(event);

				// construct info needed on view study event page
				DisplayStudyEventBean de = new DisplayStudyEventBean();
				de.setStudyEvent(event);
				de.setDisplayEventCRFs(getDisplayEventCRFs(eventCRFs, getUserAccountBean(), getCurrentRole(),
						event.getSubjectEventStatus(), study));

				request.setAttribute("displayEvent", de);

				forwardPage(Page.REMOVE_STUDY_EVENT, request, response);
			} else if ("submit".equalsIgnoreCase(action)) {
				logger.info("submit to remove the event from study");

				getStudyEventService().removeStudyEvent(event, currentUser);

				String emailBody = new StringBuilder("").append(getResPage().getString("the_event")).append(" ")
						.append(event.getStudyEventDefinition().getName()).append(" ")
						.append(getResPage().getString("has_been_removed_from_the_subject_record_for")).append(" ")
						.append(studySub.getLabel()).append(" ")
						.append(study.isSite(study.getParentStudyId())
								? getResPage().getString("in_the_site")
								: getResPage().getString("in_the_study"))
						.append(" ").append(study.getName()).append(".").toString();

				addPageMessage(emailBody, request);
				request.setAttribute("id", Integer.toString(studySubId));
				forwardToViewStudySubjectPage(request, response);
			}
		}
	}

	private void forwardToViewStudySubjectPage(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		storePageMessages(request);
		String id = (String) request.getAttribute("id");
		String savedUrl = (String) request.getSession()
				.getAttribute(RememberLastPage.getUrlKey(ViewStudySubjectServlet.class));
		if (savedUrl != null && savedUrl.contains("id=" + id)) {
			response.sendRedirect(savedUrl);
		} else {
			response.sendRedirect(
					request.getContextPath() + Page.VIEW_STUDY_SUBJECT_SERVLET.getFileName() + "?id=" + id);
		}
	}
}
