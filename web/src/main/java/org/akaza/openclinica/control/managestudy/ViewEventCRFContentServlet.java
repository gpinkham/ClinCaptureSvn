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

import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.DisplayTableOfContentsBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

/**
 * Views the content of an event CRF
 * 
 * @author jxu
 * 
 */
@SuppressWarnings({ "serial" })
@Component
public class ViewEventCRFContentServlet extends Controller {

	public static final String BEAN_STUDY_EVENT = "studyEvent";

	/**
	 * Checks whether the user has the correct privilege
     * @param request
     * @param response
     */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response) throws InsufficientPermissionException {
        UserAccountBean ub = getUserAccountBean(request);
        StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin()) {
			return;
		}
		if (currentRole.getRole().equals(Role.STUDY_DIRECTOR) || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)
				|| currentRole.getRole().equals(Role.INVESTIGATOR)
				|| currentRole.getRole().equals(Role.CLINICAL_RESEARCH_COORDINATOR)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study") + " "
				+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("no_permission"), "1");

	}

	/*
	 * Get the Study Event to display on screen as well as print some of its information. Krikor 10/19/2006
	 */
	private StudyEventBean getStudyEvent(HttpServletRequest request, int eventId) throws Exception {
        StudyBean currentStudy = getCurrentStudy(request);

		StudyEventDAO sedao = getStudyEventDAO();
		StudyBean studyWithSED = currentStudy;
		if (currentStudy.getParentStudyId() > 0) {
			studyWithSED = new StudyBean();
			studyWithSED.setId(currentStudy.getParentStudyId());
		}

		AuditableEntityBean aeb = sedao.findByPKAndStudy(eventId, studyWithSED);

		if (!aeb.isActive()) {
			addPageMessage(respage.getString("the_SE_you_attempting_enter_data_not_belong"), request);
			throw new InsufficientPermissionException(Page.LIST_STUDY_SUBJECTS_SERVLET,
					resexception.getString("SE_does_not_belong_current_study"), "1");

			// >> changed tbh, 06/2009
		}

		StudyEventBean seb = (StudyEventBean) aeb;

		StudyEventDefinitionDAO seddao = getStudyEventDefinitionDAO();
		StudyEventDefinitionBean sedb = (StudyEventDefinitionBean) seddao.findByPK(seb.getStudyEventDefinitionId());
		seb.setStudyEventDefinition(sedb);
		return seb;
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		FormProcessor fp = new FormProcessor(request);
		int eventCRFId = fp.getInt("ecId", true);
		int studySubId = fp.getInt("id", true);
		int eventId = fp.getInt("eventId", true);
		if (eventCRFId == 0) {
			addPageMessage(respage.getString("please_choose_an_event_CRF_to_view"), request);
			forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET, request, response);
			return;
		}

		StudyEventBean seb = getStudyEvent(request, eventId);

		StudySubjectDAO subdao = getStudySubjectDAO();
		StudySubjectBean studySub = (StudySubjectBean) subdao.findByPK(studySubId);
		request.setAttribute("studySub", studySub);

		EventCRFDAO ecdao = getEventCRFDAO();
		EventCRFBean eventCRF = (EventCRFBean) ecdao.findByPK(eventCRFId);
		DisplayTableOfContentsBean displayBean = getDisplayBean(eventCRF);
		request.setAttribute("toc", displayBean);
		request.getSession().setAttribute(BEAN_STUDY_EVENT, seb);
		forwardPage(Page.VIEW_EVENT_CRF_CONTENT, request, response);

	}

}
