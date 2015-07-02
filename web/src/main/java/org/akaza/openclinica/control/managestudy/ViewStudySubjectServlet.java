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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.admin.AuditEventBean;
import org.akaza.openclinica.bean.admin.StudyEventAuditBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.DisplayStudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.core.RememberLastPage;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.submit.CreateNewStudyEventServlet;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.AuditEventDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectGroupMapDAO;
import org.akaza.openclinica.service.crfdata.HideCRFManager;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.bean.DisplayStudyEventRow;
import org.akaza.openclinica.web.bean.EntityBeanTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author jxu
 * 
 *         Processes 'view subject' request
 */
@SuppressWarnings({"rawtypes", "unchecked", "serial"})
@Component
public class ViewStudySubjectServlet extends RememberLastPage {

	public static final Logger LOGGER = LoggerFactory.getLogger(ViewStudySubjectServlet.class);

	// The study subject has an existing discrepancy note related to their
	// unique identifier; this
	// value will be saved as a request attribute
	public static final String HAS_UNIQUE_ID_NOTE = "hasUniqueIDNote";
	// The study subject has an existing discrepancy note related to their date
	// of birth; this
	// value will be saved as a request attribute
	public static final String HAS_DOB_NOTE = "hasDOBNote";
	// The study subject has an existing discrepancy note related to their
	// Gender; this
	// value will be saved as a request attribute
	public static final String HAS_GENDER_NOTE = "hasGenderNote";
	// The study subject has an existing discrepancy note related to their
	// Enrollment Date; this
	// value will be saved as a request attribute
	public static final String HAS_ENROLLMENT_NOTE = "hasEnrollmentNote";
	// request attribute for a discrepancy note
	public static final String UNIQUE_ID_NOTE = "uniqueIDNote";
	// request attribute for a discrepancy note
	public static final String DOB_NOTE = "dOBNote";
	// request attribute for a discrepancy note
	public static final String GENDER_NOTE = "genderNote";
	// request attribute for a discrepancy note
	public static final String ENROLLMENT_NOTE = "enrollmentNote";
	public static final String SAVED_VIEW_STUDY_SUBJECT_URL = "savedViewStudySubjectUrl";

	/**
	 * Checks whether the user has the right permission to proceed function
	 */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		// If a study subject with passing parameter does not
		// belong to user's studies, it can not be viewed
		// mayAccess();
		removeLockedCRF(ub.getId());
		if (ub.isSysAdmin()) {
			return;
		}

		if (mayViewData(ub, currentRole)) {
			return;
		}

		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study") + " "
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.LIST_STUDY_SUBJECTS,
				resexception.getString("not_study_director"), "1");
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (shouldRedirect(request, response)) {
			return;
		}

		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		SubjectDAO sdao = getSubjectDAO();
		StudySubjectDAO subdao = getStudySubjectDAO();
		StudyGroupClassDAO sgcdao = getStudyGroupClassDAO();
		FormProcessor fp = new FormProcessor(request);
		int studySubId = fp.getInt("id", true); // studySubjectId
		studySubId = studySubId == 0 ? fp.getInt("ssId") : studySubId;
		String from = fp.getString("from");

		String module = fp.getString(MODULE);
		request.setAttribute(MODULE, module);
		// width for table
		request.setAttribute("tableWidth", "125");
		// end
		if (studySubId == 0) {
			addPageMessage(respage.getString("please_choose_a_subject_to_view"), request);
			forwardPage(Page.LIST_STUDY_SUBJECTS, request, response);
		} else {
			if (!StringUtil.isBlank(from)) {
				request.setAttribute("from", from); // form ListSubject or
			} else {
				request.setAttribute("from", "");
			}

			StudyEventDAO sedao = getStudyEventDAO();
			StudySubjectBean studySub = (StudySubjectBean) subdao.findByPK(studySubId);

			int studyId = studySub.getStudyId();
			StudyDAO studydao = getStudyDAO();
			StudyBean study = (StudyBean) studydao.findByPK(studyId);

			StudyGroupClassBean subjDynGroup = new StudyGroupClassBean();
			String studyEventDefinitionsString = "";
			StudyEventDefinitionDAO seddao = getStudyEventDefinitionDAO();
			if (studySub.getDynamicGroupClassId() == 0) {
				request.setAttribute("subjDynGroupIsDefault", true);
				StudyGroupClassBean defaultGroup = (StudyGroupClassBean) sgcdao.findDefaultByStudyId(study
						.getParentStudyId() > 0 ? study.getParentStudyId() : study.getId());
				if (defaultGroup.getId() > 0) {
					subjDynGroup = defaultGroup;
				} else {
					request.setAttribute("defaultGroupNotExist", true);
				}
			} else {
				subjDynGroup = (StudyGroupClassBean) sgcdao.findByPK(studySub.getDynamicGroupClassId());
			}
			ArrayList<StudyEventDefinitionBean> listSEDBeans = seddao
					.findAllActiveOrderedByStudyGroupClassId(subjDynGroup.getId());
			for (StudyEventDefinitionBean sedBean : listSEDBeans) {
				studyEventDefinitionsString = studyEventDefinitionsString + ", " + sedBean.getName();
			}
			request.setAttribute("subjDynGroup", subjDynGroup);
			request.setAttribute("studyEventDefinitionsString", studyEventDefinitionsString.replaceFirst(", ", ""));

			List<StudyEventBean> studyEventBeanList = sedao.findAllByStudySubject(studySub);
			if (studyEventBeanList.size() > 0) {
				boolean allLocked = true;
				boolean hasLockedBy = false;
				for (StudyEventBean studyEventBean : studyEventBeanList) {
					hasLockedBy = hasLockedBy || studyEventBean.getSubjectEventStatus() == SubjectEventStatus.LOCKED;
					if (studyEventBean.getSubjectEventStatus() != SubjectEventStatus.LOCKED) {
						allLocked = false;
					}
				}
				if (allLocked && hasLockedBy && (currentRole.isStudyAdministrator() || currentRole.isSysAdmin())) {
					request.setAttribute("showUnlockEventsButton", true);
				} else if (!allLocked && (currentRole.isStudyAdministrator() || currentRole.isSysAdmin())) {
					request.setAttribute("showLockEventsButton", true);
				}
			}

			if (currentStudy.getParentStudyId() > 0) {
				StudyBean parentStudyBean = (StudyBean) getStudyDAO().findByPK(currentStudy.getParentStudyId());
				request.setAttribute("parentStudyOid", parentStudyBean.getOid());
			} else {
				request.setAttribute("parentStudyOid", currentStudy.getOid());
			}
			request.setAttribute("studySub", studySub);

			int subjectId = studySub.getSubjectId();

			// Check if this StudySubject would be accessed from the Current Study
			if (studySub.getStudyId() != currentStudy.getId()) {
				if (currentStudy.getParentStudyId() > 0) {
					addPageMessage(
							respage.getString("no_have_correct_privilege_current_study") + " "
									+ respage.getString("change_active_study_or_contact"), request);
					forwardPage(Page.MENU_SERVLET, request, response);
					return;
				} else {
					// The SubjectStudy is not belong to currentstudy and current study is not a site.
					Collection sites = studydao.findOlnySiteIdsByStudy(currentStudy);
					if (!sites.contains(study.getId())) {
						addPageMessage(
								respage.getString("no_have_correct_privilege_current_study") + " "
										+ respage.getString("change_active_study_or_contact"), request);
						forwardPage(Page.MENU_SERVLET, request, response);
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
			DiscrepancyNoteDAO discrepancyNoteDAO = new DiscrepancyNoteDAO(getDataSource());
			List<DiscrepancyNoteBean> allNotesforSubject;

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
				setRequestAttributesForNotes(currentStudy, request, discrepancyNoteDAO, allNotesforSubject);
			}

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

			StudyParameterValueDAO spvdao = getStudyParameterValueDAO();
			study.getStudyParameterConfig()
					.setCollectDob(spvdao.findByHandleAndStudy(studyId, "collectDob").getValue());
			study.getStudyParameterConfig().setSubjectPersonIdRequired(
					spvdao.findByHandleAndStudy(studyId, "subjectPersonIdRequired").getValue());
			request.setAttribute("subjectStudy", study);

			if (study.getParentStudyId() > 0) { // this is a site,find parent
				StudyBean parentStudy2 = (StudyBean) studydao.findByPK(study.getParentStudyId());
				request.setAttribute("parentStudy", parentStudy2);
			} else {
				request.setAttribute("parentStudy", new StudyBean());
			}

			ArrayList children = (ArrayList) sdao.findAllChildrenByPK(subjectId);

			request.setAttribute("children", children);

			// find study events

			ArrayList<DisplayStudyEventBean> displayEvents = getDisplayStudyEventsForStudySubject(studySub,
					getDataSource(), ub, currentRole, true);

			for (DisplayStudyEventBean decb : displayEvents) {
				if (!(currentRole.isSysAdmin() || currentRole.isStudyAdministrator())
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

			EntityBeanTable table = getEntityBeanTable();

			table.setSortingIfNotExplicitlySet(0, true);

			ArrayList allEventRows = DisplayStudyEventRow.generateRowsFromBeans(displayEvents);

			String[] columns = {resword.getString("event") + " (" + resword.getString("occurrence_number") + ")",
					resword.getString("start_date1"), resword.getString("location"), resword.getString("status"),
					resword.getString("actions"), resword.getString("CRFs_atrib")};
			table.setColumns(new ArrayList(Arrays.asList(columns)));
			table.hideColumnLink(4);
			table.hideColumnLink(5);

			if (!"removed".equalsIgnoreCase(studySub.getStatus().getName())
					&& !"auto-removed".equalsIgnoreCase(studySub.getStatus().getName())) {
				if (currentStudy.getStatus().isAvailable() && !Role.isMonitor(currentRole.getRole())) {

					request.setAttribute("link_schedule_new_event",
							"CreateNewStudyEvent?" + CreateNewStudyEventServlet.INPUT_STUDY_SUBJECT_ID_FROM_VIEWSUBJECT
									+ "=" + studySub.getId());
				}
			}

			HashMap args = new HashMap();
			args.put("id", Integer.toString(studySubId));
			table.setQuery("ViewStudySubject", args);
			table.setRows(allEventRows);
			table.computeDisplay();

			request.setAttribute("table", table);

			// find group info
			SubjectGroupMapDAO sgmdao = getSubjectGroupMapDAO();
			ArrayList groupMaps = (ArrayList) sgmdao.findAllByStudySubject(studySubId);
			request.setAttribute("groups", groupMaps);

			// find audit log for events
			AuditEventDAO aedao = getAuditEventDAO();
			ArrayList logs = aedao.findEventStatusLogByStudySubject(studySubId);
			UserAccountDAO udao = getUserAccountDAO();
			ArrayList eventLogs = new ArrayList();
			for (Object log : logs) {
				// FIXME is there a way to fix this loop so that we only have 2-3 hits to the DB?
				AuditEventBean avb = (AuditEventBean) log;
				StudyEventAuditBean sea = new StudyEventAuditBean();
				sea.setAuditEvent(avb);
				StudyEventBean se = (StudyEventBean) sedao.findByPK(avb.getEntityId());
				StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao.findByPK(se
						.getStudyEventDefinitionId());
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
					e.printStackTrace();
					LOGGER.error(e.getMessage());
				}
				UserAccountBean updater = (UserAccountBean) udao.findByPK(avb.getUserId());
				sea.setUpdater(updater);
				eventLogs.add(sea);

			}
			request.setAttribute("eventLogs", eventLogs);

			forwardPage(Page.VIEW_STUDY_SUBJECT, request, response);
		}
	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		UserAccountBean ub = getUserAccountBean(request);
		if (ub.isSysAdmin()) {
			return ADMIN_SERVLET_CODE;
		} else {
			return "";
		}
	}

	/**
	 * Current User may access a requested study subject in the current user's studies.
	 * 
	 */
	@SuppressWarnings("unused")
	public void mayAccess(HttpServletRequest request) throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

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

	private void setRequestAttributesForNotes(StudyBean currentStudy, HttpServletRequest request,
			DiscrepancyNoteDAO discrepancyNoteDAO, List<DiscrepancyNoteBean> discBeans) {
		for (DiscrepancyNoteBean discrepancyNoteBean : discBeans) {
			ArrayList notes = (ArrayList) discrepancyNoteDAO.findAllByEntityAndColumnAndStudy(currentStudy,
					discrepancyNoteBean.getEntityType(), discrepancyNoteBean.getEntityId(),
					discrepancyNoteBean.getColumn());
			discrepancyNoteBean.setResolutionStatusId(getDiscrepancyNoteResolutionStatus(request, discrepancyNoteDAO,
					discrepancyNoteBean.getEntityId(), notes));
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
	protected String getUrlKey(HttpServletRequest request) {
		return SAVED_VIEW_STUDY_SUBJECT_URL;
	}

	@Override
	protected String getDefaultUrl(HttpServletRequest request) {
		return null;
	}

	@Override
	protected boolean userDoesNotUseJmesaTableForNavigation(HttpServletRequest request) {
		boolean result;
		String id = request.getParameter("id");
		if (request.getQueryString() != null && request.getQueryString().equalsIgnoreCase("id=" + id)) {
			String savedUrl = (String) request.getSession().getAttribute(getUrlKey(request));
			result = savedUrl != null && savedUrl.contains("id=" + id)
					&& !savedUrl.equalsIgnoreCase(request.getRequestURL() + "?" + request.getQueryString());
		} else {
			result = request.getQueryString() == null || !request.getQueryString().contains("&ebl_page=");
		}
		return result;
	}
}
