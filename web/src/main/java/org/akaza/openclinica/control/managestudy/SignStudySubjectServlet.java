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
package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.akaza.openclinica.bean.admin.AuditEventBean;
import org.akaza.openclinica.bean.admin.StudyEventAuditBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DisplayStudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.core.SpringServlet;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.submit.CreateNewStudyEventServlet;
import org.akaza.openclinica.core.SecurityManager;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.AuditEventDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectGroupMapDAO;
import org.akaza.openclinica.service.DiscrepancyNoteUtil;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.bean.DisplayStudyEventRow;
import org.akaza.openclinica.web.bean.EntityBeanTable;
import org.springframework.stereotype.Component;

import com.clinovo.util.DAOWrapper;
import com.clinovo.util.SignUtil;

/**
 * Handles signing of subject casebook.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Component
public class SignStudySubjectServlet extends SpringServlet {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		checkStudyLocked(Page.LIST_STUDY_SUBJECTS, getResPage().getString("current_study_locked"), request, response);
		mayAccess(request);

		if (ub.isSysAdmin()) {
			return;
		}

		if (currentRole.getRole().equals(Role.STUDY_DIRECTOR) || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)
				|| currentRole.getRole().equals(Role.INVESTIGATOR)) {
			return;
		}

		addPageMessage(
				getResPage().getString("no_have_correct_privilege_current_study") + " "
						+ getResPage().getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, getResException().getString("not_study_director"), "1");
	}

	/**
	 * Signs subject events.
	 * 
	 * @param studySub
	 *            study subject to be used
	 * @param ds
	 *            DataSource to be used
	 * @param ub
	 *            current user
	 * @return true if signing was successful; false otherwise
	 */
	public static boolean signSubjectEvents(StudySubjectBean studySub, DataSource ds, UserAccountBean ub) {
		boolean updated = true;
		StudyEventDAO sedao = new StudyEventDAO(ds);
		ArrayList<StudyEventBean> studyEvents = sedao.findAllByStudySubject(studySub);
		for (StudyEventBean studyEvent : studyEvents) {
			try {
				studyEvent.setUpdater(ub);
				studyEvent.setUpdatedDate(new Date());
				studyEvent.setSubjectEventStatus(SubjectEventStatus.SIGNED);
				sedao.update(studyEvent);
			} catch (Exception ex) {
				updated = false;
			}
		}
		return updated;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);
		StudySubjectDAO subdao = new StudySubjectDAO(getDataSource());
		StudyEventDAO sedao = new StudyEventDAO(getDataSource());
		StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(getDataSource());
		final int four = 4;
		final int five = 5;
		StudyBean currentStudy = getCurrentStudy(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);
		SubjectDAO sdao = new SubjectDAO(getDataSource());
		FormProcessor fp = new FormProcessor(request);
		String action = fp.getString("action");
		int studySubId = fp.getInt("id", true); // studySubjectId
		String module = fp.getString(MODULE);
		request.setAttribute(MODULE, module);
		if (studySubId == 0) {
			addPageMessage(getResPage().getString("please_choose_a_subject_to_view"), request);
			forwardPage(Page.LIST_STUDY_SUBJECTS, request, response);
			return;
		}
		StudySubjectBean studySub = (StudySubjectBean) subdao.findByPK(studySubId);
		if (!SignUtil.permitSign(studySub, new DAOWrapper(getDataSource()))) {
			addPageMessage(getResPage().getString("subject_event_cannot_signed"), request);
			// for navigation purpose (to avoid double url in stack)
			request.getSession().setAttribute("skipURL", "true");
			forwardPage(Page.LIST_STUDY_SUBJECTS_SERVLET, request, response);
			return;
		}
		if (action.equalsIgnoreCase("confirm")) {
			Page forwardPage = authenticateUser(request, response, ub, subdao, studySubId, studySub);
			if (forwardPage != null) {
				forwardPage(forwardPage, request, response);
			}
		}
		request.setAttribute("studySub", studySub);
		int studyId = studySub.getStudyId();
		int subjectId = studySub.getSubjectId();
		SubjectBean subject = (SubjectBean) sdao.findByPK(subjectId);
		if (currentStudy.getStudyParameterConfig().getCollectDob().equals("2")) {
			Date dob = subject.getDateOfBirth();
			if (dob != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dob);
				int year = cal.get(Calendar.YEAR);
				request.setAttribute("yearOfBirth", year);
			} else {
				request.setAttribute("yearOfBirth", "");
			}
		}
		request.setAttribute("subject", subject);
		StudyDAO studydao = new StudyDAO(getDataSource());
		StudyBean study = (StudyBean) studydao.findByPK(studyId);
		StudyParameterValueDAO spvdao = new StudyParameterValueDAO(getDataSource());
		study.getStudyParameterConfig().setCollectDob(spvdao.findByHandleAndStudy(studyId, "collectDob").getValue());
		// request.setAttribute("study", study);
		if (study.getParentStudyId() > 0) { // this is a site,find parent
			StudyBean parentStudy = (StudyBean) studydao.findByPK(study.getParentStudyId());
			request.setAttribute("parentStudy", parentStudy);
		} else {
			request.setAttribute("parentStudy", new StudyBean());
		}
		ArrayList<DisplayStudyEventBean> displayEvents = getDisplayStudyEventsForStudySubject(studySub, ub, currentRole, false);
		DiscrepancyNoteUtil discNoteUtil = new DiscrepancyNoteUtil();
		// Don't filter for now; disc note beans are returned with eventCRFId
		// set
		discNoteUtil.injectParentDiscNotesIntoDisplayStudyEvents(displayEvents, new HashSet(), getDataSource(), 0);
		// All the displaystudyevents for one subject
		request.setAttribute("displayStudyEvents", displayEvents);
		// Set up a Map for the JSP view, mapping the eventCRFId to another Map:
		// the
		// inner Map maps the resolution status name to the number of notes for
		// that
		// eventCRF id, as in New --> 2
		Map discNoteByEventCRFid = discNoteUtil.createDiscNoteMapByEventCRF(displayEvents);
		request.setAttribute("discNoteByEventCRFid", discNoteByEventCRFid);
		EntityBeanTable table = getEntityBeanTable();
		table.setSortingIfNotExplicitlySet(1, false); // sort by start date,
		// desc
		ArrayList allEventRows = DisplayStudyEventRow.generateRowsFromBeans(displayEvents);
		String[] columns = { getResWord().getString("event") + " (" + getResWord().getString("occurrence_number") + ")",
				getResWord().getString("start_date1"), getResWord().getString("location"), getResWord().getString("status"),
				getResWord().getString("actions"), getResWord().getString("CRFs_atrib")};
		table.setColumns(new ArrayList(Arrays.asList(columns)));
		table.hideColumnLink(four);
		table.hideColumnLink(five);
		if (!"removed".equalsIgnoreCase(studySub.getStatus().getName())
				&& !"auto-removed".equalsIgnoreCase(studySub.getStatus().getName())) {
			table.addLink(getResWord().getString("add_new_event"), "CreateNewStudyEvent?"
					+ CreateNewStudyEventServlet.INPUT_STUDY_SUBJECT_ID_FROM_VIEWSUBJECT + "=" + studySub.getId());
		}
		HashMap args = new HashMap();
		args.put("id", Integer.toString(studySubId));
		table.setQuery("ViewStudySubject", args);
		table.setRows(allEventRows);
		table.computeDisplay();
		request.setAttribute("table", table);
		SubjectGroupMapDAO sgmdao = new SubjectGroupMapDAO(getDataSource());
		ArrayList groupMaps = (ArrayList) sgmdao.findAllByStudySubject(studySubId);
		request.setAttribute("groups", groupMaps);
		AuditEventDAO aedao = new AuditEventDAO(getDataSource());
		ArrayList<AuditEventBean> logs = aedao.findEventStatusLogByStudySubject(studySubId);
		UserAccountDAO udao = new UserAccountDAO(getDataSource());
		ArrayList eventLogs = new ArrayList();
		for (AuditEventBean avb : logs) {
			StudyEventAuditBean sea = new StudyEventAuditBean();
			sea.setAuditEvent(avb);
			StudyEventBean se = (StudyEventBean) sedao.findByPK(avb.getEntityId());
			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao.findByPK(se.getStudyEventDefinitionId());
			sea.setDefinition(sed);
			String old = avb.getOldValue().trim();
			try {
				if (!StringUtil.isBlank(old)) {
					SubjectEventStatus oldStatus = SubjectEventStatus.get(Integer.parseInt(old));
					sea.setOldSubjectEventStatus(oldStatus);
				}
				String newValue = avb.getNewValue().trim();
				if (!StringUtil.isBlank(newValue)) {
					SubjectEventStatus newStatus = SubjectEventStatus.get(Integer.parseInt(newValue));
					sea.setNewSubjectEventStatus(newStatus);
				}
			} catch (NumberFormatException e) {
				logger.info(e.getMessage());
			}
			UserAccountBean updater = (UserAccountBean) udao.findByPK(avb.getUserId());
			sea.setUpdater(updater);
			eventLogs.add(sea);
		}
		request.setAttribute("eventLogs", eventLogs);
		forwardPage(Page.SIGN_STUDY_SUBJECT, request, response);
	}

	Page authenticateUser(HttpServletRequest request, HttpServletResponse response, UserAccountBean ub,
			StudySubjectDAO subdao, int studySubId, StudySubjectBean studySub) {
		String username = request.getParameter("j_user");
		String password = request.getParameter("j_pass");
		SecurityManager securityManager = getSecurityManager();
		if (securityManager.isPasswordValid(ub.getPasswd(), password, getUserDetails())
				&& ub.getName().equals(username)) {
			if (signSubjectEvents(studySub, getDataSource(), ub)) {
				// Making the StudySubject signed as all the events have
				// become signed.
				studySub.setStatus(Status.SIGNED);
				studySub.setUpdater(ub);
				subdao.update(studySub);
				addPageMessage(getResPage().getString("subject_event_signed"), request);
				return Page.LIST_STUDY_SUBJECTS_SERVLET;
			} else {
				addPageMessage(getResPage().getString("errors_in_submission_see_below"), request);
				return Page.LIST_STUDY_SUBJECTS;
			}
		} else {
			request.setAttribute("id", Integer.toString(studySubId));
			addPageMessage(getResText().getString("password_match"), request);
			return null;
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

	/**
	 * Deteremines whether current user has access to this page.
	 * 
	 * @param request
	 *            HttpServletRequest to be used.
	 * @throws InsufficientPermissionException
	 *             thrown if users is not permitted to access page.
	 */
	public void mayAccess(HttpServletRequest request) throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		FormProcessor fp = new FormProcessor(request);
		StudySubjectDAO subdao = new StudySubjectDAO(getDataSource());
		int studySubId = fp.getInt("id", true);

		if (studySubId > 0) {
			if (!entityIncluded(studySubId, ub.getName(), subdao)) {
				addPageMessage(getResPage().getString("required_study_subject_not_belong"), request);
				throw new InsufficientPermissionException(Page.MENU,
						getResException().getString("entity_not_belong_studies"), "1");
			}
		}
	}

}
