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

import com.clinovo.util.ValidatorHelper;
import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.ResolutionStatus;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.SubjectGroupMapBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.DiscrepancyValidator;
import org.akaza.openclinica.control.form.FormDiscrepancyNotes;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.control.submit.AddNewSubjectServlet;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectGroupMapDAO;
import org.akaza.openclinica.service.managestudy.DiscrepancyNoteService;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Processes request to update a study subject.
 */
@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
@Component
public class UpdateStudySubjectServlet extends Controller {

	public static final String ENROLLMENT_NOTE_STATUS = "enrollmentNoteStatus";
	public static final String HAS_NOTES = "hasNotes";

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {

		UserAccountBean currentUser = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (currentUser.isSysAdmin()) {
			return;
		}

		if (currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR) || currentRole.getRole().equals(Role.INVESTIGATOR)
				|| currentRole.getRole().equals(Role.CLINICAL_RESEARCH_COORDINATOR)) {
			return;
		}

		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_study_director"), "1");
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		StudyBean currentStudy = getCurrentStudy(request);

		FormDiscrepancyNotes discNotes;
		FormProcessor fp = new FormProcessor(request);
		int defaultDynGroupClassId = 0;
		String defaultDynGroupClassName = "";

		SimpleDateFormat localDateFormat = getLocalDf(request);

		String fromResolvingNotes = fp.getString("fromResolvingNotes", true);
		if (StringUtil.isBlank(fromResolvingNotes)) {

			request.getSession().removeAttribute(ViewNotesServlet.WIN_LOCATION);
			request.getSession().removeAttribute(ViewNotesServlet.NOTES_TABLE);
			checkStudyLocked(Page.LIST_STUDY_SUBJECTS_SERVLET, respage.getString("current_study_locked"), request,
					response);
			checkStudyFrozen(Page.LIST_STUDY_SUBJECTS_SERVLET, respage.getString("current_study_frozen"), request,
					response);
		}

		DiscrepancyNoteDAO dndao = getDiscrepancyNoteDAO();
		int studySubId = fp.getInt("id", true);

		StudySubjectBean subjectToUpdate = (StudySubjectBean) getStudySubjectDAO().findByPK(studySubId);

		if (subjectToUpdate.getId() == 0) {

			addPageMessage(respage.getString("please_choose_study_subject_to_edit"), request);
			forwardPage(Page.LIST_STUDY_SUBJECTS, request, response);
		} else {

			ArrayList<DiscrepancyNoteBean> dbNotes = (ArrayList<DiscrepancyNoteBean>) dndao
					.findAllByEntityAndColumnAndStudy(currentStudy, "studySub", studySubId, "enrollment_date");
			request.setAttribute(HAS_NOTES, dbNotes != null && dbNotes.size() > 0);
			request.setAttribute(ENROLLMENT_NOTE_STATUS,
					ResolutionStatus.get(getDiscrepancyNoteResolutionStatus(request, dndao, studySubId, dbNotes)));

			String action = fp.getString("action", true);
			if (StringUtil.isBlank(action)) {

				addPageMessage(respage.getString("no_action_specified"), request);
				forwardPage(Page.LIST_STUDY_SUBJECTS, request, response);
				return;
			}

			StudyGroupClassDAO sgcdao = getStudyGroupClassDAO();
			StudyGroupDAO sgdao = getStudyGroupDAO();
			SubjectGroupMapDAO sgmdao = getSubjectGroupMapDAO();
			List<SubjectGroupMapBean> groupMapsForStudySubject = (List<SubjectGroupMapBean>) sgmdao
					.findAllByStudySubject(studySubId);

			Map<Integer, SubjectGroupMapBean> groupMapsForStudySubjectByGroupClassId = new HashMap<Integer, SubjectGroupMapBean>();
			for (SubjectGroupMapBean groupMap : groupMapsForStudySubject) {
				groupMapsForStudySubjectByGroupClassId.put(groupMap.getStudyGroupClassId(), groupMap);
			}

			List<StudyGroupClassBean> classes;
			List<StudyGroupClassBean> dynamicClasses;
			if (!"submit".equalsIgnoreCase(action)) {

				int studyIdToSearchOn = currentStudy.isSite() ? currentStudy.getParentStudyId() : currentStudy.getId();
				classes = sgcdao.findAllActiveByStudyId(studyIdToSearchOn, true);
				dynamicClasses = getDynamicGroupClassesByStudyId(studyIdToSearchOn);

				for (StudyGroupClassBean group : classes) {

					ArrayList<StudyGroupBean> studyGroups = (ArrayList<StudyGroupBean>) sgdao
							.findAllByGroupClass(group);
					group.setStudyGroups(studyGroups);
					SubjectGroupMapBean gMap = groupMapsForStudySubjectByGroupClassId.get(group.getId());
					if (gMap != null) {
						group.setStudyGroupId(gMap.getStudyGroupId());
						group.setGroupNotes(gMap.getNotes());
					}
				}
				request.setAttribute("groups", classes);
				request.setAttribute("dynamicGroups", dynamicClasses);

				if (dynamicClasses.size() > 0) {
					if (dynamicClasses.get(0).isDefault()) {

						defaultDynGroupClassId = dynamicClasses.get(0).getId();
						defaultDynGroupClassName = dynamicClasses.get(0).getName();
					}
				}
				request.setAttribute("defaultDynGroupClassId", defaultDynGroupClassId);
				request.setAttribute("defaultDynGroupClassName", defaultDynGroupClassName);
				if (!fp.getString("dynamicGroupClassId").equals("")) {
					request.getSession().setAttribute("selectedDynGroupClassId", fp.getInt("dynamicGroupClassId"));
				}
			}

			if ("back".equalsIgnoreCase(action)) {

				request.setAttribute("groups", request.getSession().getAttribute("groups"));
				forwardPage(Page.UPDATE_STUDY_SUBJECT, request, response);

			} else if ("show".equalsIgnoreCase(action)) {

				clearSession(request);
				request.getSession().setAttribute("selectedDynGroupClassId", subjectToUpdate.getDynamicGroupClassId());
				request.getSession().setAttribute("studySub", subjectToUpdate);
				String enrollDateStr = localDateFormat.format(subjectToUpdate.getEnrollmentDate());
				request.getSession().setAttribute("enrollDateStr", enrollDateStr);
				discNotes = new FormDiscrepancyNotes();
				request.getSession().setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, discNotes);
				forwardPage(Page.UPDATE_STUDY_SUBJECT, request, response);

			} else if ("confirm".equalsIgnoreCase(action)) {

				confirm(request, response);

			} else if ("submit".equalsIgnoreCase(action)) {

				getServletContext().setAttribute("groupMapsForStudySubjectByGroupClassId",
						groupMapsForStudySubjectByGroupClassId);
				submit(request, response);

			} else {

				addPageMessage(respage.getString("no_action_specified"), request);
				forwardPage(Page.LIST_STUDY_SUBJECTS, request, response);

			}

		}
	}

	private void clearSession(HttpServletRequest request) {

		request.getSession().removeAttribute("studySub");
		request.getSession().removeAttribute("groups");
		request.getSession().removeAttribute("enrollDateStr");
		request.getSession().removeAttribute("selectedDynGroupClassId");
		request.getSession().removeAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
	}

	/**
	 * Processes 'confirm' request, validate the study subject object.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @throws Exception
	 */
	private void confirm(HttpServletRequest request, HttpServletResponse response) throws Exception {

		UserAccountBean currentUser = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		StudyGroupDAO sgdao = getStudyGroupDAO();
		FormProcessor fp = new FormProcessor(request);
		List<StudyGroupClassBean> classes = (List<StudyGroupClassBean>) request.getAttribute("groups");

		HashMap errors = getErrorsHolder(request);
		SimpleDateFormat localDateFormat = getLocalDf(request);

		StudySubjectBean subjectToUpdate = (StudySubjectBean) request.getSession().getAttribute("studySub");
		FormDiscrepancyNotes discNotes = (FormDiscrepancyNotes) request.getSession().getAttribute(
				AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
		DiscrepancyValidator v = new DiscrepancyValidator(new ValidatorHelper(request, getConfigurationDao()),
				discNotes);
		Date enrollDate = subjectToUpdate.getEnrollmentDate();

		if (currentUser.isSysAdmin() || currentRole.isManageStudy() || currentRole.isInvestigator()
				|| (currentStudy.getParentStudyId() > 0 && currentRole.isClinicalResearchCoordinator())) {

			v.addValidation("label", Validator.NO_BLANKS);

			String eDateString = fp.getString("enrollmentDate");
			if (!StringUtil.isBlank(eDateString)) {
				v.addValidation("enrollmentDate", Validator.IS_A_DATE);
				v.alwaysExecuteLastValidation("enrollmentDate");
			}

			errors = v.validate();

			if (!StringUtil.isBlank(fp.getString("label"))) {

				StudySubjectDAO ssdao = getStudySubjectDAO();

				StudySubjectBean sub1 = (StudySubjectBean) ssdao.findAnotherBySameLabel(fp.getString("label").trim(),
						currentStudy.getId(), subjectToUpdate.getId());

				// Also look for labels in the child studies
				if (sub1.getId() == 0) {
					sub1 = (StudySubjectBean) ssdao.findAnotherBySameLabelInSites(fp.getString("label").trim(),
							currentStudy.getId(), subjectToUpdate.getId());
				}

				if (sub1.getId() > 0) {
					Validator.addError(errors, "label",
							resexception.getString("subject_ID_used_by_another_choose_unique"));
				}
			}

			subjectToUpdate.setLabel(fp.getString("label"));
			subjectToUpdate.setSecondaryLabel(fp.getString("secondaryLabel"));

			try {

				localDateFormat.setLenient(false);
				if (!StringUtil.isBlank(eDateString)) {
					enrollDate = localDateFormat.parse(eDateString);
				}
			} catch (ParseException fe) {

				logger.error("Date parsing error.", fe);
			}
			subjectToUpdate.setEnrollmentDate(enrollDate);

		}

		String enrollDateStr = localDateFormat.format(enrollDate);

		request.getSession().setAttribute("enrollDateStr", enrollDateStr);
		request.getSession().setAttribute("studySub", subjectToUpdate);

		if (!classes.isEmpty()) {

			for (int i = 0; i < classes.size(); i++) {

				StudyGroupClassBean sgc = classes.get(i);
				int groupId = fp.getInt("studyGroupId" + i);
				String notes = fp.getString("notes" + i);
				v.addValidation("notes" + i, Validator.LENGTH_NUMERIC_COMPARISON,
						NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, DiscrepancyValidator.MAX_DESCRIPTION_LENGTH);
				sgc.setStudyGroupId(groupId);
				sgc.setGroupNotes(notes);
				if (groupId > 0) {

					StudyGroupBean sgb = (StudyGroupBean) sgdao.findByPK(groupId);
					sgc.setStudyGroupName(sgb.getName());
				}
			}
		}

		request.getSession().setAttribute("groups", classes);

		if (!errors.isEmpty()) {

			logger.info("has errors");
			if (StringUtil.isBlank(subjectToUpdate.getLabel())) {

				addPageMessage(
						new StringBuilder("").append(respage.getString("must_enter_subject_ID_for_identifying"))
								.append(respage.getString("this_may_be_external_ID_number"))
								.append(respage.getString("you_may_enter_study_subject_ID_listed"))
								.append(respage.getString("study_subject_ID_should_not_contain_protected_information"))
								.toString(), request);
			} else {

				StudySubjectDAO subdao = getStudySubjectDAO();
				StudySubjectBean sub1 = (StudySubjectBean) subdao.findAnotherBySameLabel(subjectToUpdate.getLabel(),
						subjectToUpdate.getStudyId(), subjectToUpdate.getId());
				if (sub1.getId() > 0) {
					addPageMessage(resexception.getString("subject_ID_used_by_another_choose_unique"), request);
				}
			}

			request.setAttribute("formMessages", errors);
			forwardPage(Page.UPDATE_STUDY_SUBJECT, request, response);

		} else {

			forwardPage(Page.UPDATE_STUDY_SUBJECT_CONFIRM, request, response);
		}

	}

	private void submit(HttpServletRequest request, HttpServletResponse response) throws Exception {

		StudySubjectDAO subdao = getStudySubjectDAO();
		SubjectGroupMapDAO sgmdao = getSubjectGroupMapDAO();
		UserAccountBean currentUser = getUserAccountBean(request);
		StudySubjectBean subjectToUpdate = (StudySubjectBean) request.getSession().getAttribute("studySub");

		subjectToUpdate.setUpdater(currentUser);

		int selectedDynGroupClassId = 0;
		if (!"".equals(request.getSession().getAttribute("selectedDynGroupClassId"))) {
			selectedDynGroupClassId = (Integer) request.getSession().getAttribute("selectedDynGroupClassId");
		}
		subjectToUpdate.setDynamicGroupClassId(selectedDynGroupClassId);

		subdao.update(subjectToUpdate);

		// save discrepancy notes into DB
		DiscrepancyNoteService dnService = new DiscrepancyNoteService(getDataSource());
		FormDiscrepancyNotes fdn = (FormDiscrepancyNotes) request.getSession().getAttribute(
				AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
		dnService.saveFieldNotes("enrollmentDate", fdn, subjectToUpdate.getId(), "studySub", getCurrentStudy(request));

		List<StudyGroupClassBean> groups = (List<StudyGroupClassBean>) request.getSession().getAttribute("groups");
		if (!groups.isEmpty()) {

			Map<Integer, SubjectGroupMapBean> groupMapsForStudySubjectByGroupClassId = (Map<Integer, SubjectGroupMapBean>) getServletContext()
					.getAttribute("groupMapsForStudySubjectByGroupClassId");

			for (StudyGroupClassBean group : groups) {

				if (group.getStudyGroupId() == 0) {

					List<SubjectGroupMapBean> subjectGroups = (List<SubjectGroupMapBean>) sgmdao
							.findAllByStudySubject(subjectToUpdate.getId());
					for (SubjectGroupMapBean subjectGroup : subjectGroups) {

						if (subjectGroup.getGroupClassName().equals(group.getName())) {
							sgmdao.deleteTestGroupMap(subjectGroup.getId());
						}
					}
				} else {

					SubjectGroupMapBean sgm = new SubjectGroupMapBean();
					SubjectGroupMapBean gMap = groupMapsForStudySubjectByGroupClassId.get(group.getId());
					sgm.setStudyGroupId(group.getStudyGroupId());
					sgm.setNotes(group.getGroupNotes());
					sgm.setStudyGroupClassId(group.getId());
					sgm.setStudySubjectId(subjectToUpdate.getId());
					sgm.setStatus(Status.AVAILABLE);
					if (sgm.getStudyGroupId() > 0) {

						if (gMap != null && gMap.getId() > 0) {

							sgm.setUpdater(currentUser);
							sgm.setId(gMap.getId());
							sgmdao.update(sgm);
						} else {

							sgm.setOwner(currentUser);
							sgmdao.create(sgm);
						}
					}
				}
			}
		}

		addPageMessage(respage.getString("study_subject_updated_succesfully"), request);
		clearSession(request);

		String viewStudySubjectRequestURL = new StringBuilder("")
				.append(request.getRequestURL().toString()
						.substring(0, request.getRequestURL().toString().lastIndexOf("/")))
				.append(Page.VIEW_STUDY_SUBJECT_SERVLET.getFileName()).append("?id=").append(subjectToUpdate.getId())
				.toString();

		response.sendRedirect(response.encodeRedirectURL(viewStudySubjectRequestURL));
	}

}
