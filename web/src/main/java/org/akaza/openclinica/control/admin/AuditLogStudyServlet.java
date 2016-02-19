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
 * Created on Sep 21, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.akaza.openclinica.control.admin;

import java.util.ArrayList;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.dao.admin.AuditDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Component
public class AuditLogStudyServlet extends Controller {

	public static String getLink() {
		return "AuditLogStudy";
	}

	/**
	 * Redo this servlet to run the audits per study subject for the study; need to add a studyId param and then use the
	 * StudySubjectDAO.findAllByStudyOrderByLabel() method to grab a lot of study subject beans and then return them
	 * much like in ViewStudySubjectAuditLogServet.process()
	 * 
	 * currentStudy instead of studyId?
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 */
	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		StudyBean currentStudy = getCurrentStudy(request);

		StudySubjectDAO subdao = getStudySubjectDAO();
		SubjectDAO sdao = getSubjectDAO();
		AuditDAO adao = getAuditDAO();

		StudyEventDAO sedao = getStudyEventDAO();
		StudyEventDefinitionDAO seddao = getStudyEventDefinitionDAO();
		EventCRFDAO ecdao = getEventCRFDAO();
		StudyDAO studydao = getStudyDAO();
		HashMap eventsHashMap = new HashMap();
		HashMap studySubjectAuditsHashMap = new HashMap();
		HashMap subjectHashMap = new HashMap();

		ArrayList studySubjects = subdao.findAllByStudyOrderByLabel(currentStudy);
		logger.info("found " + studySubjects.size() + " study subjects");
		request.setAttribute("studySubjects", studySubjects);

		for (Object studySubject1 : studySubjects) {
			ArrayList studySubjectAudits = new ArrayList();
			StudySubjectBean studySubject = (StudySubjectBean) studySubject1;
			SubjectBean subject = (SubjectBean) sdao.findByPK(studySubject.getSubjectId());
			subjectHashMap.put(studySubject.getId(), subject);
			StudyBean study = (StudyBean) studydao.findByPK(studySubject.getStudyId());
			request.setAttribute("study", study);
			// hmm, repetitive work?

			// Show both study subject and subject audit events together
			studySubjectAudits.addAll(adao.findStudySubjectAuditEvents(studySubject.getId())); // Study
			// subject
			// value
			// changed
			studySubjectAudits.addAll(adao.findSubjectAuditEvents(subject.getId())); // Global

			studySubjectAuditsHashMap.put(studySubject.getId(), studySubjectAudits);

			// Get the list of events
			ArrayList events = sedao.findAllByStudySubject(studySubject);
			for (Object event : events) {
				// Link study event definitions
				StudyEventBean studyEvent = (StudyEventBean) event;
				studyEvent.setStudyEventDefinition((StudyEventDefinitionBean) seddao.findByPK(studyEvent
						.getStudyEventDefinitionId()));

				// Link event CRFs
				studyEvent.setEventCRFs(ecdao.findAllByStudyEvent(studyEvent));
			}

			eventsHashMap.put(studySubject.getId(), events);
		}

		request.setAttribute("events", eventsHashMap);
		request.setAttribute("studySubjectAudits", studySubjectAuditsHashMap);
		request.setAttribute("study", currentStudy);
		request.setAttribute("subjects", subjectHashMap);

		logger.warn("*** found servlet, sending to page ***");
		String pattn;
		String pattern2;
		pattn = ResourceBundleProvider.getFormatBundle().getString("date_format_string");
		pattern2 = ResourceBundleProvider.getFormatBundle().getString("date_time_format_string");
		request.setAttribute("dateFormatPattern", pattn);
		request.setAttribute("dateTimeFormatPattern", pattern2);
		forwardPage(Page.AUDIT_LOG_STUDY, request, response);

	}

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
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

		addPageMessage(
				getResPage().getString("no_have_correct_privilege_current_study")
						+ getResPage().getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, getResException().getString("not_director"), "1");
	}
}
