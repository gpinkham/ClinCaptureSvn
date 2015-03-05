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
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.admin.StudyEventAuditBean;
import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DisplayEventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.DisplayStudyEventBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.DisplayEventCRFBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.submit.CreateNewStudyEventServlet;
import org.akaza.openclinica.core.SecurityManager;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.AuditEventDAO;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
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
@SuppressWarnings({"rawtypes", "unchecked", "serial"})
@Component
public class SignStudySubjectServlet extends Controller {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		checkStudyLocked(Page.LIST_STUDY_SUBJECTS, respage.getString("current_study_locked"), request, response);
		mayAccess(request);

		if (ub.isSysAdmin()) {
			return;
		}

		if (currentRole.getRole().equals(Role.STUDY_DIRECTOR) || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)
				|| currentRole.getRole().equals(Role.INVESTIGATOR)) {
			return;
		}

		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study") + " "
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_study_director"), "1");
	}

	/**
	 * Gets display study events for study subject.
	 * 
	 * @param study
	 *            study to use
	 * @param studySub
	 *            study subject to use
	 * @param ds
	 *            DataSource to use
	 * @param ub
	 *            current user
	 * @param currentRole
	 *            current user's role in study
	 * @return list of display study events
	 */
	public static ArrayList getDisplayStudyEventsForStudySubject(StudyBean study, StudySubjectBean studySub,
			DataSource ds, UserAccountBean ub, StudyUserRoleBean currentRole) {
		StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(ds);
		StudyEventDAO sedao = new StudyEventDAO(ds);
		EventCRFDAO ecdao = new EventCRFDAO(ds);
		EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(ds);
		StudySubjectDAO ssdao = new StudySubjectDAO(ds);

		ArrayList<StudyEventBean> events = sedao.findAllByStudySubject(studySub);

		ArrayList displayEvents = new ArrayList();
		for (StudyEventBean event : events) {
			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao
					.findByPK(event.getStudyEventDefinitionId());
			event.setStudyEventDefinition(sed);

			// find all active crfs in the definition
			ArrayList eventDefinitionCRFs = (ArrayList) edcdao.findAllActiveByEventDefinitionId(study, sed.getId());

			ArrayList eventCRFs = ecdao.findAllByStudyEvent(event);

			// construct info needed on view study event page
			DisplayStudyEventBean de = new DisplayStudyEventBean();
			de.setStudyEvent(event);
			de.setDisplayEventCRFs(getDisplayEventCRFs(study, ds, eventCRFs, ub, currentRole,
					event.getSubjectEventStatus()));
			ArrayList al = getUncompletedCRFs(ds, eventDefinitionCRFs, eventCRFs, event.getSubjectEventStatus());
			populateUncompletedCRFsWithCRFAndVersions(ds, al);
			de.setUncompletedCRFs(al);

			StudySubjectBean studySubject = (StudySubjectBean) ssdao.findByPK(event.getStudySubjectId());
			de.setMaximumSampleOrdinal(sedao.getMaxSampleOrdinal(sed, studySubject));

			displayEvents.add(de);

		}

		return displayEvents;
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
		StudyDAO studyDao = new StudyDAO(getDataSource());
		StudySubjectDAO subdao = new StudySubjectDAO(getDataSource());
		StudyEventDAO sedao = new StudyEventDAO(getDataSource());
		EventCRFDAO ecdao = new EventCRFDAO(getDataSource());
		EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(getDataSource());
		DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(getDataSource());
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
			addPageMessage(respage.getString("please_choose_a_subject_to_view"), request);
			forwardPage(Page.LIST_STUDY_SUBJECTS, request, response);
			return;
		}
		StudySubjectBean studySub = (StudySubjectBean) subdao.findByPK(studySubId);
		if (!SignUtil.permitSign(studySub, new DAOWrapper(studyDao, getCRFVersionDAO(), sedao, subdao, ecdao, edcdao,
				dndao))) {
			addPageMessage(respage.getString("subject_event_cannot_signed"), request);
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
		ArrayList<DisplayStudyEventBean> displayEvents = getDisplayStudyEventsForStudySubject(study, studySub,
				getDataSource(), ub, currentRole);
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
		EntityBeanTable table = fp.getEntityBeanTable();
		table.setSortingIfNotExplicitlySet(1, false); // sort by start date,
		// desc
		ArrayList allEventRows = DisplayStudyEventRow.generateRowsFromBeans(displayEvents);
		String[] columns = {resword.getString("event") + " (" + resword.getString("occurrence_number") + ")",
				resword.getString("start_date1"), resword.getString("location"), resword.getString("status"),
				resword.getString("actions"), resword.getString("CRFs_atrib")};
		table.setColumns(new ArrayList(Arrays.asList(columns)));
		table.hideColumnLink(four);
		table.hideColumnLink(five);
		if (!"removed".equalsIgnoreCase(studySub.getStatus().getName())
				&& !"auto-removed".equalsIgnoreCase(studySub.getStatus().getName())) {
			table.addLink(resword.getString("add_new_event"), "CreateNewStudyEvent?"
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
				addPageMessage(respage.getString("subject_event_signed"), request);
				return Page.LIST_STUDY_SUBJECTS_SERVLET;
			} else {
				addPageMessage(respage.getString("errors_in_submission_see_below"), request);
				return Page.LIST_STUDY_SUBJECTS;
			}
		} else {
			request.setAttribute("id", Integer.toString(studySubId));
			addPageMessage(restext.getString("password_match"), request);
			return null;
		}
	}

	/**
	 * Each of the event CRFs with its corresponding CRFBean. Then generates a list of DisplayEventCRFBeans, one for
	 * each event CRF.
	 * 
	 * @param study
	 *            current study
	 * @param ds
	 *            DataSource to be used.
	 * @param eventCRFs
	 *            the list of event CRFs for this study event.
	 * @param ub
	 *            current user
	 * @param currentRole
	 *            user's role in current study
	 * @param status
	 *            the subject event status
	 * @return The list of DisplayEventCRFBeans for this study event.
	 */
	public static ArrayList getDisplayEventCRFs(StudyBean study, DataSource ds, ArrayList<EventCRFBean> eventCRFs,
			UserAccountBean ub, StudyUserRoleBean currentRole, SubjectEventStatus status) {
		ArrayList answer = new ArrayList();

		StudyEventDAO sedao = new StudyEventDAO(ds);
		CRFDAO cdao = new CRFDAO(ds);
		CRFVersionDAO cvdao = new CRFVersionDAO(ds);
		ItemDataDAO iddao = new ItemDataDAO(ds);
		EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(ds);

		for (EventCRFBean ecb : eventCRFs) {
			// populate the event CRF with its crf bean
			int crfVersionId = ecb.getCRFVersionId();
			CRFBean cb = cdao.findByVersionId(crfVersionId);
			ecb.setCrf(cb);

			CRFVersionBean cvb = (CRFVersionBean) cvdao.findByPK(crfVersionId);
			ecb.setCrfVersion(cvb);

			// then get the definition so we can call
			// DisplayEventCRFBean.setFlags
			int studyEventId = ecb.getStudyEventId();
			int studyEventDefinitionId = sedao.getDefinitionIdFromStudyEventId(studyEventId);

			EventDefinitionCRFBean edc = edcdao.findByStudyEventDefinitionIdAndCRFId(study, studyEventDefinitionId,
					cb.getId());
			if (status.equals(SubjectEventStatus.LOCKED) || status.equals(SubjectEventStatus.SKIPPED)
					|| status.equals(SubjectEventStatus.STOPPED)) {
				ecb.setStage(DataEntryStage.LOCKED);

				// we need to set a SED-wide flag here, because other edcs
				// in this event can be filled in and change the status, tbh
			} else if (status.equals(SubjectEventStatus.INVALID)) {
				ecb.setStage(DataEntryStage.LOCKED);
			} else if (!cb.getStatus().equals(Status.AVAILABLE)) {
				ecb.setStage(DataEntryStage.LOCKED);
			} else if (!cvb.getStatus().equals(Status.AVAILABLE)) {
				ecb.setStage(DataEntryStage.LOCKED);
			}
			// TODO need to refactor since this is similar to other code, tbh
			if (edc != null) {
				DisplayEventCRFBean dec = new DisplayEventCRFBean();
				dec.setFlags(ecb, ub, currentRole, edc);
				ArrayList idata = iddao.findAllByEventCRFId(ecb.getId());
				if (!idata.isEmpty()) {
					answer.add(dec);
				}
			}
		}

		return answer;
	}

	/**
	 * Finds all the event definitions for which no event CRF exists - which is the list of event definitions with
	 * uncompleted event CRFs.
	 * 
	 * @param ds
	 *            DataSource to be used.
	 * @param eventDefinitionCRFs
	 *            All of the event definition CRFs for this study event.
	 * @param eventCRFs
	 *            All of the event CRFs for this study event.
	 * @param status
	 *            subject event status
	 * @return The list of event definitions for which no event CRF exists.
	 */
	public static ArrayList getUncompletedCRFs(DataSource ds, ArrayList eventDefinitionCRFs, ArrayList eventCRFs,
			SubjectEventStatus status) {
		int i;
		HashMap completed = new HashMap();
		HashMap startedButIncompleted = new HashMap();
		ArrayList answer = new ArrayList();

		/**
		 * A somewhat non-standard algorithm is used here: let answer = empty; foreach event definition ED, set
		 * isCompleted(ED) = false foreach event crf EC, set isCompleted(EC.getEventDefinition()) = true foreach event
		 * definition ED, if (!isCompleted(ED)) { answer += ED; } return answer; This algorithm is guaranteed to find
		 * all the event definitions for which no event CRF exists.
		 * 
		 * The motivation for using this algorithm is reducing the number of database hits.
		 * 
		 * -jun-we have to add more CRFs here: the event CRF which dones't have item data yet
		 */

		for (i = 0; i < eventDefinitionCRFs.size(); i++) {
			EventDefinitionCRFBean edcrf = (EventDefinitionCRFBean) eventDefinitionCRFs.get(i);
			completed.put(edcrf.getCrfId(), Boolean.FALSE);
			startedButIncompleted.put(edcrf.getCrfId(), new EventCRFBean());
		}

		CRFVersionDAO cvdao = new CRFVersionDAO(ds);
		ItemDataDAO iddao = new ItemDataDAO(ds);
		for (i = 0; i < eventCRFs.size(); i++) {
			EventCRFBean ecrf = (EventCRFBean) eventCRFs.get(i);
			int crfId = cvdao.getCRFIdFromCRFVersionId(ecrf.getCRFVersionId());
			ArrayList idata = iddao.findAllByEventCRFId(ecrf.getId());
			if (!idata.isEmpty()) { // this crf has data already
				completed.put(crfId, Boolean.TRUE);
			} else { // event crf got created, but no data entered
				startedButIncompleted.put(crfId, ecrf);
			}
		}

		// TODO possible relation to 1689 here, tbh
		for (i = 0; i < eventDefinitionCRFs.size(); i++) {
			DisplayEventDefinitionCRFBean dedc = new DisplayEventDefinitionCRFBean();
			EventDefinitionCRFBean edcrf = (EventDefinitionCRFBean) eventDefinitionCRFs.get(i);

			dedc.setEdc(edcrf);
			if (status.equals(SubjectEventStatus.LOCKED)) {
				dedc.setStatus(Status.LOCKED);
			}
			Boolean b = (Boolean) completed.get(new Integer(edcrf.getCrfId()));
			EventCRFBean ev = (EventCRFBean) startedButIncompleted.get(new Integer(edcrf.getCrfId()));
			if (b == null || !b) {

				dedc.setEventCRF(ev);
				answer.add(dedc);

			}
		}

		return answer;
	}

	/**
	 * Populates uncompleted crfs with crf and versions.
	 * 
	 * @param ds
	 *            DataSource to be used.
	 * @param uncompletedEventDefinitionCRFs
	 *            list of uncompleted CRFs
	 */
	public static void populateUncompletedCRFsWithCRFAndVersions(DataSource ds, ArrayList uncompletedEventDefinitionCRFs) {
		CRFDAO cdao = new CRFDAO(ds);
		CRFVersionDAO cvdao = new CRFVersionDAO(ds);

		int size = uncompletedEventDefinitionCRFs.size();
		for (int i = 0; i < size; i++) {
			DisplayEventDefinitionCRFBean dedcrf = (DisplayEventDefinitionCRFBean) uncompletedEventDefinitionCRFs
					.get(i);
			CRFBean cb = (CRFBean) cdao.findByPK(dedcrf.getEdc().getCrfId());
			dedcrf.getEdc().setCrf(cb);

			ArrayList versions = (ArrayList) cvdao.findAllActiveByCRF(dedcrf.getEdc().getCrfId());
			dedcrf.getEdc().setVersions(versions);
			uncompletedEventDefinitionCRFs.set(i, dedcrf);
		}
	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		UserAccountBean ub = getUserAccountBean(request);
		if (ub.isSysAdmin()) {
			return Controller.ADMIN_SERVLET_CODE;
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
				addPageMessage(respage.getString("required_study_subject_not_belong"), request);
				throw new InsufficientPermissionException(Page.MENU,
						resexception.getString("entity_not_belong_studies"), "1");
			}
		}
	}

}
