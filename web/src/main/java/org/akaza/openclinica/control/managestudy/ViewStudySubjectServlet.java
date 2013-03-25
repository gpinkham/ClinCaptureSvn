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
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
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
import org.akaza.openclinica.control.RememberLastPage;
import org.akaza.openclinica.control.core.CoreSecureController;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.submit.CreateNewStudyEventServlet;
import org.akaza.openclinica.control.submit.SubmitDataServlet;
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
import org.akaza.openclinica.service.crfdata.HideCRFManager;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.bean.DisplayStudyEventRow;
import org.akaza.openclinica.web.bean.EntityBeanTable;

/**
 * @author jxu
 * 
 *         Processes 'view subject' request
 */
@SuppressWarnings({"rawtypes", "unchecked",  "serial"})
public class ViewStudySubjectServlet extends RememberLastPage {

	// The study subject has an existing discrepancy note related to their
	// unique identifier; this
	// value will be saved as a request attribute
	public final static String HAS_UNIQUE_ID_NOTE = "hasUniqueIDNote";
	// The study subject has an existing discrepancy note related to their date
	// of birth; this
	// value will be saved as a request attribute
	public final static String HAS_DOB_NOTE = "hasDOBNote";
	// The study subject has an existing discrepancy note related to their
	// Gender; this
	// value will be saved as a request attribute
	public final static String HAS_GENDER_NOTE = "hasGenderNote";
	// The study subject has an existing discrepancy note related to their
	// Enrollment Date; this
	// value will be saved as a request attribute
	public final static String HAS_ENROLLMENT_NOTE = "hasEnrollmentNote";
	// request attribute for a discrepancy note
	public final static String UNIQUE_ID_NOTE = "uniqueIDNote";
	// request attribute for a discrepancy note
	public final static String DOB_NOTE = "dOBNote";
	// request attribute for a discrepancy note
	public final static String GENDER_NOTE = "genderNote";
	// request attribute for a discrepancy note
	public final static String ENROLLMENT_NOTE = "enrollmentNote";
	public static final String SAVED_VIEW_STUDY_SUBJECT_URL = "savedViewStudySubjectUrl";

	/**
	 * Checks whether the user has the right permission to proceed function
	 */
	@Override
	public void mayProceed() throws InsufficientPermissionException {
		// If a study subject with passing parameter does not
		// belong to user's studies, it can not be viewed
		// mayAccess();
		removeLockedCRF(ub.getId());
		CoreSecureController.removeLockedCRF(ub.getId());
		if (ub.isSysAdmin()) {
			return;
		}

		if (SubmitDataServlet.mayViewData(ub, currentRole)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study") + " "
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.LIST_STUDY_SUBJECTS,
				resexception.getString("not_study_director"), "1");
	}

	public static ArrayList<DisplayStudyEventBean> getDisplayStudyEventsForStudySubject(StudySubjectBean studySub,
			DataSource ds, UserAccountBean ub, StudyUserRoleBean currentRole) {
		StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(ds);
		StudyEventDAO sedao = new StudyEventDAO(ds);
		EventCRFDAO ecdao = new EventCRFDAO(ds);
		EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(ds);
		StudySubjectDAO ssdao = new StudySubjectDAO(ds);
		StudyDAO sdao = new StudyDAO(ds);

		ArrayList events = sedao.findAllByStudySubject(studySub);

		ArrayList displayEvents = new ArrayList();
		for (int i = 0; i < events.size(); i++) {
			StudyEventBean event = (StudyEventBean) events.get(i);
			StudySubjectBean studySubject = (StudySubjectBean) ssdao.findByPK(event.getStudySubjectId());

			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao
					.findByPK(event.getStudyEventDefinitionId());
			event.setStudyEventDefinition(sed);

			// find all active crfs in the definition
			StudyBean study = (StudyBean) sdao.findByPK(studySubject.getStudyId());
			ArrayList eventDefinitionCRFs = (ArrayList) edcdao.findAllActiveByEventDefinitionId(study, sed.getId());
			ArrayList eventCRFs = ecdao.findAllByStudyEvent(event);

			// construct info needed on view study event page
			DisplayStudyEventBean de = new DisplayStudyEventBean();
			de.setStudyEvent(event);
			de.setDisplayEventCRFs(getDisplayEventCRFs(ds, eventCRFs, eventDefinitionCRFs, ub, currentRole,
					event.getSubjectEventStatus(), study));
			ArrayList al = getUncompletedCRFs(ds, eventDefinitionCRFs, eventCRFs, event.getSubjectEventStatus());
			populateUncompletedCRFsWithCRFAndVersions(ds, al);
			de.setUncompletedCRFs(al);

			de.setMaximumSampleOrdinal(sedao.getMaxSampleOrdinal(sed, studySubject));

			displayEvents.add(de);

		}

		return displayEvents;
	}

	@Override
	public void processRequest() throws Exception {
		analyzeUrl();
		SubjectDAO sdao = new SubjectDAO(sm.getDataSource());
		StudySubjectDAO subdao = new StudySubjectDAO(sm.getDataSource());
		FormProcessor fp = new FormProcessor(request);
		int studySubId = fp.getInt("id", true);// studySubjectId
		studySubId = studySubId == 0 ? fp.getInt("ssId") : studySubId;
		String from = fp.getString("from");

		String module = fp.getString(MODULE);
		request.setAttribute(MODULE, module);
		// width for table
		request.setAttribute("tableWidth", "125");
		// end
		if (studySubId == 0) {
			addPageMessage(respage.getString("please_choose_a_subject_to_view"));
			forwardPage(Page.LIST_STUDY_SUBJECTS);
		} else {
			if (!StringUtil.isBlank(from)) {
				request.setAttribute("from", from); // form ListSubject or
			} else {
				request.setAttribute("from", "");
			}

			StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
			StudySubjectBean studySub = (StudySubjectBean) subdao.findByPK(studySubId);

			List<StudyEventBean> studyEventBeanList = sedao.findAllByStudySubject(studySub);
			if (studyEventBeanList.size() > 0) {
				boolean allLocked = true;
				boolean hasLockedBy = false;
				for (StudyEventBean studyEventBean : studyEventBeanList) {
					hasLockedBy = !hasLockedBy ? studyEventBean.isWasLockedBy() : hasLockedBy;
					if (studyEventBean.getSubjectEventStatus() != SubjectEventStatus.LOCKED) {
						allLocked = false;
					}
				}
				if (allLocked && hasLockedBy) {
					request.setAttribute("showUnlockEventsButton", true);
				} else if (!allLocked) {
					request.setAttribute("showLockEventsButton", true);
				}
			}

			request.setAttribute("studySub", studySub);

			int studyId = studySub.getStudyId();
			int subjectId = studySub.getSubjectId();

			StudyDAO studydao = new StudyDAO(sm.getDataSource());
			StudyBean study = (StudyBean) studydao.findByPK(studyId);
			// Check if this StudySubject would be accessed from the Current Study
			if (studySub.getStudyId() != currentStudy.getId()) {
				if (currentStudy.getParentStudyId() > 0) {
					addPageMessage(respage.getString("no_have_correct_privilege_current_study") + " "
							+ respage.getString("change_active_study_or_contact"));
					forwardPage(Page.MENU_SERVLET);
					return;
				} else {
					// The SubjectStudy is not belong to currentstudy and current study is not a site.
					Collection sites = studydao.findOlnySiteIdsByStudy(currentStudy);
					if (!sites.contains(study.getId())) {
						addPageMessage(respage.getString("no_have_correct_privilege_current_study") + " "
								+ respage.getString("change_active_study_or_contact"));
						forwardPage(Page.MENU_SERVLET);
						return;
					}
				}
			}

			// If the study subject derives from a site, and is being viewed
			// from a parent study,
			// then the study IDs will be different. However, since each note is
			// saved with the specific
			// study ID, then its study ID may be different than the study
			// subject's ID.
			boolean subjectStudyIsCurrentStudy = studyId == currentStudy.getId();
			boolean isParentStudy = study.getParentStudyId() < 1;

			// Get any disc notes for this subject : studySubId
			DiscrepancyNoteDAO discrepancyNoteDAO = new DiscrepancyNoteDAO(sm.getDataSource());
			List<DiscrepancyNoteBean> allNotesforSubject = new ArrayList<DiscrepancyNoteBean>();

			// These methods return only parent disc notes
			if (subjectStudyIsCurrentStudy && isParentStudy) {
				allNotesforSubject = discrepancyNoteDAO.findAllSubjectByStudyAndId(study, subjectId);
				allNotesforSubject.addAll(discrepancyNoteDAO.findAllStudySubjectByStudyAndId(study, studySubId));
			} else {
				if (!isParentStudy) {
					StudyBean stParent = (StudyBean) studydao.findByPK(study.getParentStudyId());
					allNotesforSubject = discrepancyNoteDAO.findAllSubjectByStudiesAndSubjectId(stParent, study,
							subjectId);

					allNotesforSubject.addAll(discrepancyNoteDAO.findAllStudySubjectByStudiesAndStudySubjectId(
							stParent, study, studySubId));

				} else {
					allNotesforSubject = discrepancyNoteDAO.findAllSubjectByStudiesAndSubjectId(currentStudy, study,
							subjectId);

					allNotesforSubject.addAll(discrepancyNoteDAO.findAllStudySubjectByStudiesAndStudySubjectId(
							currentStudy, study, studySubId));
				}

			}

			if (!allNotesforSubject.isEmpty()) {
				setRequestAttributesForNotes(allNotesforSubject);
			}

			SubjectBean subject = (SubjectBean) sdao.findByPK(subjectId);
			if (currentStudy.getStudyParameterConfig().getCollectDob().equals("2")) {
				Date dob = subject.getDateOfBirth();
				if (dob != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(dob);
					int year = cal.get(Calendar.YEAR);
					request.setAttribute("yearOfBirth", new Integer(year));
				} else {
					request.setAttribute("yearOfBirth", "");
				}
			}

			request.setAttribute("subject", subject);

			if (subject.getFatherId() > 0) {
				SubjectBean father = (SubjectBean) sdao.findByPK(subject.getFatherId());
				request.setAttribute("father", father);
			} else {
				request.setAttribute("father", new SubjectBean());
			}

			if (subject.getMotherId() > 0) {
				SubjectBean mother = (SubjectBean) sdao.findByPK(subject.getMotherId());
				request.setAttribute("mother", mother);
			} else {
				request.setAttribute("mother", new SubjectBean());
			}

			StudyParameterValueDAO spvdao = new StudyParameterValueDAO(sm.getDataSource());
			study.getStudyParameterConfig()
					.setCollectDob(spvdao.findByHandleAndStudy(studyId, "collectDob").getValue());
			request.setAttribute("subjectStudy", study);

			if (study.getParentStudyId() > 0) {// this is a site,find parent
				StudyBean parentStudy2 = (StudyBean) studydao.findByPK(study.getParentStudyId());
				request.setAttribute("parentStudy", parentStudy2);
			} else {
				request.setAttribute("parentStudy", new StudyBean());
			}

			ArrayList children = (ArrayList) sdao.findAllChildrenByPK(subjectId);

			request.setAttribute("children", children);

			// find study events

			StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());

			ArrayList<DisplayStudyEventBean> displayEvents = getDisplayStudyEventsForStudySubject(studySub,
					sm.getDataSource(), ub, currentRole);
			
			for (int i = 0; i < displayEvents.size(); i++) {
				DisplayStudyEventBean decb = displayEvents.get(i);
				if (!(currentRole.isDirector() || currentRole.isCoordinator())
						&& decb.getStudyEvent().getSubjectEventStatus().isLocked()) {
					decb.getStudyEvent().setEditable(false);
				}
			}

			if (currentStudy.getParentStudyId() > 0) {
				HideCRFManager hideCRFManager = HideCRFManager.createHideCRFManager();

				for (DisplayStudyEventBean displayStudyEventBean : displayEvents) {

					hideCRFManager.removeHiddenEventCRF(displayStudyEventBean);

				}

			}
			
			EntityBeanTable table = fp.getEntityBeanTable();
			
			table.setSortingIfNotExplicitlySet(0, true);

			ArrayList allEventRows = DisplayStudyEventRow.generateRowsFromBeans(displayEvents);

			String[] columns = { resword.getString("event") + " (" + resword.getString("occurrence_number") + ")",
					resword.getString("start_date1"), resword.getString("location"), resword.getString("status"),
					resword.getString("actions"), resword.getString("CRFs_atrib") };
			table.setColumns(new ArrayList(Arrays.asList(columns)));
			table.hideColumnLink(4);
			table.hideColumnLink(5);
			
			if (!"removed".equalsIgnoreCase(studySub.getStatus().getName())
					&& !"auto-removed".equalsIgnoreCase(studySub.getStatus().getName())) {
				if (currentStudy.getStatus().isAvailable() && !currentRole.getRole().equals(Role.MONITOR)) {
					
					request.setAttribute("link_schedule_new_event",
							"CreateNewStudyEvent?" + CreateNewStudyEventServlet.INPUT_STUDY_SUBJECT_ID_FROM_VIEWSUBJECT
									+ "=" + studySub.getId());
				}
			}
			
			HashMap args = new HashMap();
			args.put("id", new Integer(studySubId).toString());
			table.setQuery("ViewStudySubject", args);
			table.setRows(allEventRows);
			table.computeDisplay();

			request.setAttribute("table", table);

			// find group info
			SubjectGroupMapDAO sgmdao = new SubjectGroupMapDAO(sm.getDataSource());
			ArrayList groupMaps = (ArrayList) sgmdao.findAllByStudySubject(studySubId);
			request.setAttribute("groups", groupMaps);

			// find audit log for events
			AuditEventDAO aedao = new AuditEventDAO(sm.getDataSource());
			ArrayList logs = aedao.findEventStatusLogByStudySubject(studySubId);
			UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
			ArrayList eventLogs = new ArrayList();
			for (int i = 0; i < logs.size(); i++) {
				// FIXME is there a way to fix this loop so that we only have 2-3 hits to the DB?
				AuditEventBean avb = (AuditEventBean) logs.get(i);
				StudyEventAuditBean sea = new StudyEventAuditBean();
				sea.setAuditEvent(avb);
				StudyEventBean se = (StudyEventBean) sedao.findByPK(avb.getEntityId());
				StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao.findByPK(se
						.getStudyEventDefinitionId());
				sea.setDefinition(sed);
				String old = avb.getOldValue().trim();
				try {
					if (!StringUtil.isBlank(old)) {
						SubjectEventStatus oldStatus = SubjectEventStatus.get(new Integer(old).intValue());
						sea.setOldSubjectEventStatus(oldStatus);
					}
					String newValue = avb.getNewValue().trim();
					if (!StringUtil.isBlank(newValue)) {
						SubjectEventStatus newStatus = SubjectEventStatus.get(new Integer(newValue).intValue());
						sea.setNewSubjectEventStatus(newStatus);
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
				UserAccountBean updater = (UserAccountBean) udao.findByPK(avb.getUserId());
				sea.setUpdater(updater);
				eventLogs.add(sea);

			}
			request.setAttribute("eventLogs", eventLogs);

			analyzeForward(Page.VIEW_STUDY_SUBJECT);
		}
	}

	/**
	 * Each of the event CRFs with its corresponding CRFBean. Then generates a list of DisplayEventCRFBeans, one for
	 * each event CRF.
	 * 
	 * @param eventCRFs
	 *            The list of event CRFs for this study event.
	 * @param eventDefinitionCRFs
	 *            The list of event definition CRFs for this study event.
	 * @return The list of DisplayEventCRFBeans for this study event.
	 */
	public static ArrayList getDisplayEventCRFs(DataSource ds, ArrayList eventCRFs, ArrayList eventDefinitionCRFs,
			UserAccountBean ub, StudyUserRoleBean currentRole, SubjectEventStatus status, StudyBean study) {
		ArrayList answer = new ArrayList();

		int i;

		StudyEventDAO sedao = new StudyEventDAO(ds);
		CRFDAO cdao = new CRFDAO(ds);
		CRFVersionDAO cvdao = new CRFVersionDAO(ds);
		ItemDataDAO iddao = new ItemDataDAO(ds);
		EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(ds);

		for (i = 0; i < eventCRFs.size(); i++) {
			EventCRFBean ecb = (EventCRFBean) eventCRFs.get(i);

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
			
			if (edc != null) {
				DisplayEventCRFBean dec = new DisplayEventCRFBean();
				dec.setEventDefinitionCRF(edc);
				dec.setFlags(ecb, ub, currentRole, edc.isDoubleEntry());

				ArrayList idata = iddao.findAllByEventCRFId(ecb.getId());
				if (!idata.isEmpty()) {
					// consider an event crf started only if item data get
					// created
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
	 * @param eventDefinitionCRFs
	 *            All of the event definition CRFs for this study event.
	 * @param eventCRFs
	 *            All of the event CRFs for this study event.
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
			completed.put(new Integer(edcrf.getCrfId()), Boolean.FALSE);
			startedButIncompleted.put(new Integer(edcrf.getCrfId()), new EventCRFBean());
		}

		CRFVersionDAO cvdao = new CRFVersionDAO(ds);
		ItemDataDAO iddao = new ItemDataDAO(ds);
		for (i = 0; i < eventCRFs.size(); i++) {
			EventCRFBean ecrf = (EventCRFBean) eventCRFs.get(i);
			int crfId = cvdao.getCRFIdFromCRFVersionId(ecrf.getCRFVersionId());
			ArrayList idata = iddao.findAllByEventCRFId(ecrf.getId());
			if (!idata.isEmpty()) {// this crf has data already
				completed.put(new Integer(crfId), Boolean.TRUE);
			} else {
				startedButIncompleted.put(new Integer(crfId), ecrf);
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
			if (b == null || !b.booleanValue()) {

				dedc.setEventCRF(ev);
				answer.add(dedc);

			}
		}
		return answer;
	}

	public static void populateUncompletedCRFsWithCRFAndVersions(DataSource ds, ArrayList uncompletedEventDefinitionCRFs) {
		CRFDAO cdao = new CRFDAO(ds);
		CRFVersionDAO cvdao = new CRFVersionDAO(ds);

		int size = uncompletedEventDefinitionCRFs.size();
		for (int i = 0; i < size; i++) {
			DisplayEventDefinitionCRFBean dedcrf = (DisplayEventDefinitionCRFBean) uncompletedEventDefinitionCRFs
					.get(i);
			CRFBean cb = (CRFBean) cdao.findByPK(dedcrf.getEdc().getCrfId());
			dedcrf.getEdc().setCrf(cb);

			ArrayList theVersions = (ArrayList) cvdao.findAllActiveByCRF(dedcrf.getEdc().getCrfId());
			ArrayList versions = new ArrayList();
			HashMap<String, CRFVersionBean> crfVersionIds = new HashMap<String, CRFVersionBean>();

			for (int j = 0; j < theVersions.size(); j++) {
				CRFVersionBean crfVersion = (CRFVersionBean) theVersions.get(j);
				crfVersionIds.put(String.valueOf(crfVersion.getId()), crfVersion);
			}

			if (!dedcrf.getEdc().getSelectedVersionIds().equals("")) {
				String[] kk = dedcrf.getEdc().getSelectedVersionIds().split(",");
				for (String string : kk) {
					if (crfVersionIds.get(string) != null) {
						versions.add(crfVersionIds.get(string));
					}
				}
			} else {
				versions = theVersions;
			}
			dedcrf.getEdc().setVersions(versions);
			uncompletedEventDefinitionCRFs.set(i, dedcrf);
		}
	}

	@Override
	protected String getAdminServlet() {
		if (ub.isSysAdmin()) {
			return SecureController.ADMIN_SERVLET_CODE;
		} else {
			return "";
		}
	}

	/**
	 * Current User may access a requested study subject in the current user's studies
	 * 
	 * @author ywang 10-18-2007
	 */
	public void mayAccess() throws InsufficientPermissionException {
		FormProcessor fp = new FormProcessor(request);
		StudySubjectDAO subdao = new StudySubjectDAO(sm.getDataSource());
		int studySubId = fp.getInt("id", true);

		if (studySubId > 0) {
			if (!entityIncluded(studySubId, ub.getName(), subdao, sm.getDataSource())) {
				addPageMessage(respage.getString("required_study_subject_not_belong"));
				throw new InsufficientPermissionException(Page.MENU,
						resexception.getString("entity_not_belong_studies"), "1");
			}
		}
	}

	private void setRequestAttributesForNotes(List<DiscrepancyNoteBean> discBeans) {
		for (DiscrepancyNoteBean discrepancyNoteBean : discBeans) {
			if ("unique_identifier".equalsIgnoreCase(discrepancyNoteBean.getColumn())) {
				request.setAttribute(HAS_UNIQUE_ID_NOTE, "yes");
				request.setAttribute(UNIQUE_ID_NOTE, discrepancyNoteBean);
			} else if ("date_of_birth".equalsIgnoreCase(discrepancyNoteBean.getColumn())) {
				request.setAttribute(HAS_DOB_NOTE, "yes");
				request.setAttribute(DOB_NOTE, discrepancyNoteBean);
			} else if ("enrollment_date".equalsIgnoreCase(discrepancyNoteBean.getColumn())) {
				request.setAttribute(HAS_ENROLLMENT_NOTE, "yes");
				request.setAttribute(ENROLLMENT_NOTE, discrepancyNoteBean);
			} else if ("gender".equalsIgnoreCase(discrepancyNoteBean.getColumn())) {
				request.setAttribute(HAS_GENDER_NOTE, "yes");
				request.setAttribute(GENDER_NOTE, discrepancyNoteBean);
			}

		}

	}

	@Override
	protected String getUrlKey() {
		return SAVED_VIEW_STUDY_SUBJECT_URL;
	}

	@Override
	protected String getDefaultUrl() {
		return null;
	}

	@Override
	protected boolean userDoesNotUseJmesaTableForNavigation() {
		boolean result;
		String id = request.getParameter("id");
		if (request.getQueryString() != null && request.getQueryString().equalsIgnoreCase("id=" + id)) {
			String savedUrl = (String) request.getSession().getAttribute(getUrlKey());
			if (savedUrl != null && savedUrl.contains("id=" + id)
					&& !savedUrl.equalsIgnoreCase(request.getRequestURL() + "?" + request.getQueryString())) {
				result = true;
			} else {
				result = false;
			}
		} else {
			result = request.getQueryString() == null || !request.getQueryString().contains("&ebl_page=");
		}
		return result;
	}
}
