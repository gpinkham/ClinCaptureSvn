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
package org.akaza.openclinica.control.submit;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ssachs
 */

/**
 * The following terminology is used in this class: Step 1 - the user chooses whether to browse by Subject or by Study
 * Event Definition Step 2 - the user chooses a subject or study event definition whose study events he wants to see
 * Step 3 - the user chooses the study event he wants to add data to
 */
@SuppressWarnings({ "rawtypes", "serial" })
@Component
public class FindStudyEventServlet extends Controller {

	public static final String INPUT_BROWSEBY = "browseBy";
	public static final String INPUT_PAGENUM = "pageNum";
	public static final String INPUT_ID = "id";

	public static final String ARG_BROWSEBY_SUBJECT = "Subject";
	public static final String ARG_BROWSEBY_DEFINITION = "StudyEventDefinition";
	public static final String ARG_DISPLAY_NEXT_PAGE_YES = "yes";
	public static final String ARG_DISPLAY_NEXT_PAGE_NO = "no";

	public static final String BEAN_DISPLAY_ENTITIES = "displayEntities";
	public static final String BEAN_DISPLAY_NEXT_PAGE = "displayNextPage";
	public static final String BEAN_ENTITY_WITH_STUDY_EVENTS = "entityWithStudyEvents";

	public static final int NUM_ENTITIES_PER_PAGE = 10;

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        StudyBean currentStudy = getCurrentStudy(request);

		FormProcessor fp = new FormProcessor(request);

		String browseBy = fp.getString(INPUT_BROWSEBY);
		int id = fp.getInt(INPUT_ID);

		// User is going to Step 1
		if (browseBy.equals("")) {
			forwardPage(Page.FIND_STUDY_EVENTS_STEP1, request, response);
		} else if (invalidBrowseBy(browseBy)) {
			addPageMessage(respage.getString("must_browse_study_events_by_subject_or_event_definition"), request);
			forwardPage(Page.FIND_STUDY_EVENTS_STEP1, request, response);
		}

		// User was at Step 1, is going to Step 2
		else if (id <= 0) {
			int pageNum = fp.getInt(INPUT_PAGENUM);

			ArrayList allDisplayEntities = new ArrayList();

			if (browseBy.equals(ARG_BROWSEBY_SUBJECT)) {
				StudySubjectDAO ssdao = getStudySubjectDAO();
				allDisplayEntities = ssdao.findAllWithStudyEvent(currentStudy);
			} else {
				StudyEventDefinitionDAO seddao = getStudyEventDefinitionDAO();
				allDisplayEntities = seddao.findAllWithStudyEvent(currentStudy);
			}

			if (pageNum < 0) {
				pageNum = 0;
			} else if (pageNum > allDisplayEntities.size() / NUM_ENTITIES_PER_PAGE) {
				pageNum = allDisplayEntities.size() / NUM_ENTITIES_PER_PAGE;
			}

			int firstIndex = NUM_ENTITIES_PER_PAGE * pageNum;
			int lastIndex = NUM_ENTITIES_PER_PAGE * (pageNum + 1);
			if (allDisplayEntities.size() > lastIndex) {
				request.setAttribute(BEAN_DISPLAY_NEXT_PAGE, ARG_DISPLAY_NEXT_PAGE_YES);
			} else {
				request.setAttribute(BEAN_DISPLAY_NEXT_PAGE, ARG_DISPLAY_NEXT_PAGE_NO);
				lastIndex = allDisplayEntities.size();
			}

			List displayEntities = allDisplayEntities.subList(firstIndex, lastIndex);

			request.setAttribute(INPUT_BROWSEBY, browseBy);
			request.setAttribute(BEAN_DISPLAY_ENTITIES, displayEntities);
			request.setAttribute(INPUT_PAGENUM, new Integer(pageNum));
			forwardPage(Page.FIND_STUDY_EVENTS_STEP2, request, response);
		}

		// User is coming from Step 2, is going to Step 3
		else {
			StudyEventDAO sedao = getStudyEventDAO();
			ArrayList events = new ArrayList();

			EntityBean entityWithStudyEvents;
			if (browseBy.equals(ARG_BROWSEBY_SUBJECT)) {
				events = sedao.findAllByStudyAndStudySubjectId(currentStudy, id);

				StudySubjectDAO ssdao = getStudySubjectDAO();
				entityWithStudyEvents = ssdao.findByPK(id);
			} else {
				events = sedao.findAllByStudyAndEventDefinitionId(currentStudy, id);

				StudyEventDefinitionDAO seddao = getStudyEventDefinitionDAO();
				entityWithStudyEvents = seddao.findByPK(id);
			}

			request.setAttribute(INPUT_BROWSEBY, browseBy);
			request.setAttribute(BEAN_DISPLAY_ENTITIES, events);
			request.setAttribute(BEAN_ENTITY_WITH_STUDY_EVENTS, entityWithStudyEvents);
			forwardPage(Page.FIND_STUDY_EVENTS_STEP3, request, response);
		}
	}

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response) throws InsufficientPermissionException {
        UserAccountBean ub = getUserAccountBean(request);
        StudyUserRoleBean currentRole = getCurrentRole(request);

		String exceptionName = resexception.getString("no_permission_to_submit_data");
		String noAccessMessage = respage.getString("you_may_not_submit_data_for_this_study")
				+ respage.getString("change_study_contact_sysadmin");

		if (maySubmitData(ub, currentRole)) {
			return;
		}

		addPageMessage(noAccessMessage, request);
		throw new InsufficientPermissionException(Page.MENU, exceptionName, "1");
	}

	private boolean invalidBrowseBy(String browseBy) {
		if (browseBy.equals("")) {
			return true;
		}
		if (!browseBy.equals(ARG_BROWSEBY_SUBJECT) && !browseBy.equals(ARG_BROWSEBY_DEFINITION)) {
			return true;
		}
		return false;
	}

}
