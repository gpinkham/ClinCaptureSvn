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

import com.clinovo.validator.StudySubjectValidator;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.core.SpringServlet;
import org.akaza.openclinica.control.form.FormDiscrepancyNotes;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.control.managestudy.ViewNotesServlet;
import org.akaza.openclinica.control.submit.AddNewSubjectServlet;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.service.StudyConfigService;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.service.managestudy.DiscrepancyNoteService;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
@Component
public class UpdateSubjectServlet extends SpringServlet {

	public final static String HAS_UNIQUE_ID_NOTE = "hasUniqueIDNote";
	public final static String HAS_DOB_NOTE = "hasDOBNote";
	public final static String HAS_GENDER_NOTE = "hasGenderNote";
	public final static String UNIQUE_ID_NOTE = "uniqueIDNote";
	public final static String DOB_NOTE = "dOBNote";
	public final static String GENDER_NOTE = "genderNote";

	public final static String SUBJECTS_STUDY = "subjectsStudy";

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);

		if (ub.isSysAdmin() && !getCurrentRole(request).isStudySponsor()) {
			return;
		}

		addPageMessage(
				getResPage().getString("no_have_correct_privilege_current_study")
						+ getResPage().getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.SUBJECT_LIST_SERVLET, getResException().getString("not_admin"), "1");
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);
		SimpleDateFormat localDf = getLocalDf(request);

		SubjectDAO subjdao = new SubjectDAO(getDataSource());
		DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(getDataSource());
		FormProcessor fp = new FormProcessor(request);
		FormDiscrepancyNotes discNotes;
		Map<String, String> fields = new HashMap<String, String>();
		SubjectBean subject;

		String fromResolvingNotes = fp.getString("fromResolvingNotes", true);
		if (StringUtil.isBlank(fromResolvingNotes)) {
			request.getSession().removeAttribute(ViewNotesServlet.WIN_LOCATION);
			request.getSession().removeAttribute(ViewNotesServlet.NOTES_TABLE);
			checkStudyLocked(Page.LIST_SUBJECT_SERVLET, getResPage().getString("current_study_locked"), request, response);
			checkStudyFrozen(Page.LIST_SUBJECT_SERVLET, getResPage().getString("current_study_frozen"), request, response);
		}
		int subjectId = fp.getInt("id", true);

		if (subjectId == 0) {
			addPageMessage(getResPage().getString("please_choose_subject_to_edit"), request);
			forwardPage(Page.LIST_SUBJECT_SERVLET, request, response);
		} else {
			String action = fp.getString("action", true);

			DiscrepancyNoteBean discrepancyNoteBean = new DiscrepancyNoteBean();
			ArrayList notes = (ArrayList) dndao.findAllByEntityAndColumnAndStudy(currentStudy, "subject", subjectId,
					"unique_identifier");
			discrepancyNoteBean.setResolutionStatusId(getDiscrepancyNoteResolutionStatus(request, dndao, subjectId,
					notes));
			request.setAttribute(HAS_UNIQUE_ID_NOTE, notes.size() > 0 ? "yes" : "");
			request.setAttribute(UNIQUE_ID_NOTE, discrepancyNoteBean);

			discrepancyNoteBean = new DiscrepancyNoteBean();
			notes = (ArrayList) dndao.findAllByEntityAndColumnAndStudy(currentStudy, "subject", subjectId, "gender");
			discrepancyNoteBean.setResolutionStatusId(getDiscrepancyNoteResolutionStatus(request, dndao, subjectId,
					notes));
			request.setAttribute(HAS_GENDER_NOTE, notes.size() > 0 ? "yes" : "");
			request.setAttribute(GENDER_NOTE, discrepancyNoteBean);

			discrepancyNoteBean = new DiscrepancyNoteBean();
			notes = (ArrayList) dndao.findAllByEntityAndColumnAndStudy(currentStudy, "subject", subjectId,
					"date_of_birth");
			discrepancyNoteBean.setResolutionStatusId(getDiscrepancyNoteResolutionStatus(request, dndao, subjectId,
					notes));
			request.setAttribute(HAS_DOB_NOTE, notes.size() > 0 ? "yes" : "");
			request.setAttribute(DOB_NOTE, discrepancyNoteBean);

			if ("show".equalsIgnoreCase(action)) {
				clearSession(request);

				StudyDAO sdao = new StudyDAO(getDataSource());
				subject = (SubjectBean) subjdao.findByPK(subjectId);
				request.getSession().setAttribute("subjectToUpdate", subject);

				StudySubjectBean studySubjectBean = (StudySubjectBean) getStudySubjectDAO()
						.findAllBySubjectId(subjectId).get(0);
				StudyBean subjectStudy = (StudyBean) sdao.findByPK(studySubjectBean.getStudyId());
				StudyConfigService studyConfigService = getStudyConfigService();
				StudyParameterValueDAO studyParameterValueDAO = getStudyParameterValueDAO();
				ArrayList studyParameters = studyParameterValueDAO.findParamConfigByStudy(currentStudy);
				subjectStudy.setStudyParameters(studyParameters);
				if (subjectStudy.isSite()) {
					studyConfigService.setParametersForSite(subjectStudy);
				} else {
					studyConfigService.setParametersForStudy(subjectStudy);
				}
				request.getSession().setAttribute(SUBJECTS_STUDY, subjectStudy);
				request.getSession().setAttribute("parameters", subjectStudy.getStudyParameterConfig());

				String personId = subject.getUniqueIdentifier() == null ? "" : subject.getUniqueIdentifier();
				String gender = (Object) subject.getGender() == null ? "" : String.valueOf(subject.getGender());
				Date birthDate = subject.getDateOfBirth();

				String personIdToDisplay = "";
				String genderToDisplay = "";
				String dateToDisplay = "";

				if (!"".equals(personId)) {
					if (!"not_used".equals(subjectStudy.getStudyParameterConfig().getSubjectPersonIdRequired())) {
						personIdToDisplay = personId;
					}
				}

				if (!"".equals(gender)) {
					if ("true".equals(subjectStudy.getStudyParameterConfig().getGenderRequired())) {
						genderToDisplay = gender;
					}
				}

				if (birthDate != null) {
					if ("1".equals(subjectStudy.getStudyParameterConfig().getCollectDob())) {
						dateToDisplay = localDf.format(birthDate);
					} else if ("2".equals(subjectStudy.getStudyParameterConfig().getCollectDob())) {
						GregorianCalendar cal = new GregorianCalendar();
						cal.setTime(birthDate);
						dateToDisplay = String.valueOf(cal.get(Calendar.YEAR));
					}
				}

				fields.put("personId", personIdToDisplay);
				fields.put("gender", genderToDisplay);
				fields.put("dateOfBirth", dateToDisplay);

				request.getSession().setAttribute("fields", fields);
				request.getSession().setAttribute("oldValues", new HashMap<String, String>(fields));

				discNotes = new FormDiscrepancyNotes();
				request.getSession().setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, discNotes);

				forwardPage(Page.UPDATE_SUBJECT, request, response);
			} else if ("confirm".equalsIgnoreCase(action)) {
				confirm(request, response);
			} else if ("back".equalsIgnoreCase(action)) {
				fields = (HashMap<String, String>) request.getSession().getAttribute("fields");
				HashMap<String, String> oldValues = (HashMap<String, String>) request.getSession().getAttribute(
						"oldValues");
				boolean isDataChanged = false;

				if (!fields.get("personId").equals(oldValues.get("personId"))
						|| !fields.get("gender").equals(oldValues.get("gender"))
						|| !fields.get("dateOfBirth").equals(oldValues.get("dateOfBirth"))) {
					isDataChanged = true;
				}
				request.setAttribute("isDataChanged", isDataChanged);

				forwardPage(Page.UPDATE_SUBJECT, request, response);
			} else if ("submit".equalsIgnoreCase(action)) {
				subject = (SubjectBean) request.getSession().getAttribute("subjectToUpdate");
				fields = (HashMap<String, String>) request.getSession().getAttribute("fields");
				StudyBean subjectsStudy = (StudyBean) request.getSession().getAttribute(SUBJECTS_STUDY);

				subject.setUpdater(ub);
				if (!"not_used".equals(subjectsStudy.getStudyParameterConfig().getSubjectPersonIdRequired())) {
					subject.setUniqueIdentifier(fields.get("personId"));
				}

				if ("true".equals(subjectsStudy.getStudyParameterConfig().getGenderRequired())) {
					if ("m".equals(fields.get("gender"))) {
						subject.setGender('m');
					} else if ("f".equals(fields.get("gender"))) {
						subject.setGender('f');
					} else {
						subject.setGender(' ');
					}
				}

				Date dateOfBirth;
				if ("1".equals(subjectsStudy.getStudyParameterConfig().getCollectDob())) {
					try {
						dateOfBirth = localDf.parse(fields.get("dateOfBirth"));
						subject.setDateOfBirth(dateOfBirth);
						subject.setDobCollected(true);
					} catch (ParseException pe) {
						logger.info("Parse exception happened.");
					}
				} else if ("2".equals(subjectsStudy.getStudyParameterConfig().getCollectDob())) {
					try {
						Calendar cal = Calendar.getInstance();
						cal.set(Integer.valueOf(fields.get("dateOfBirth")), Calendar.JANUARY, 1);
						subject.setDateOfBirth(cal.getTime());
						subject.setDobCollected(false);
					} catch (NumberFormatException pe) {
						logger.info("Parse exception happened.");
					}
				}
				subjdao.update(subject);

				// save discrepancy notes into DB
				DiscrepancyNoteService dnService = new DiscrepancyNoteService(getDataSource());
				FormDiscrepancyNotes fdn = (FormDiscrepancyNotes) request.getSession().getAttribute(
						AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
				dnService.saveFieldNotes("uniqueIdentifier", fdn, subject.getId(), "subject", currentStudy);
				dnService.saveFieldNotes("gender", fdn, subject.getId(), "subject", currentStudy);
				dnService.saveFieldNotes(StudySubjectValidator.INPUT_YOB, fdn, subject.getId(), "subject", currentStudy);
				dnService.saveFieldNotes(StudySubjectValidator.INPUT_DOB, fdn, subject.getId(), "subject", currentStudy);

				addPageMessage(getResPage().getString("subject_updated_succcesfully"), request);
				clearSession(request);

				forwardPage(Page.LIST_SUBJECT_SERVLET, request, response);
			} else {
				addPageMessage(getResPage().getString("no_action_specified"), request);
				forwardPage(Page.LIST_SUBJECT_SERVLET, request, response);
			}
		}
	}

	private void confirm(HttpServletRequest request, HttpServletResponse response) throws Exception {
		SubjectBean subject = (SubjectBean) request.getSession().getAttribute("subjectToUpdate");
		FormProcessor fp = new FormProcessor(request);
		StudyBean subjectsStudy = (StudyBean) request.getSession().getAttribute(SUBJECTS_STUDY);


		StudySubjectValidator studySubjectValidator = new StudySubjectValidator();
		HashMap errors = studySubjectValidator.validate(request, getConfigurationDao(), subjectsStudy, false);

		if (!"not_used".equals(subjectsStudy.getStudyParameterConfig().getSubjectPersonIdRequired())) {
			if (!fp.getString("uniqueIdentifier").isEmpty()) {
				String uid = fp.getString("uniqueIdentifier").trim();
				SubjectDAO sdao = new SubjectDAO(getDataSource());
				SubjectBean subjectWithSameUID = (SubjectBean) sdao.findAnotherByIdentifier(uid, subject.getId());
				if (subjectWithSameUID.getId() > 0) {
					Validator.addError(errors, "uniqueIdentifier",
							getResException().getString("person_ID_used_by_another_choose_unique"));
				}
			}
		}

		saveFieldsToSession(subjectsStudy, request);

		if (errors.isEmpty()) {
			logger.info("no errors");
			forwardPage(Page.UPDATE_SUBJECT_CONFIRM, request, response);
		} else {
			logger.info("validation errors");
			setInputMessages(errors, request);
			forwardPage(Page.UPDATE_SUBJECT, request, response);
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

	private void saveFieldsToSession(StudyBean study, HttpServletRequest request) {
		Map<String, String> fields = (HashMap<String, String>) request.getSession().getAttribute("fields");
		FormProcessor fp = new FormProcessor(request);

		if (!"not_used".equals(study.getStudyParameterConfig().getSubjectPersonIdRequired())) {
			fields.put("personId", fp.getString("uniqueIdentifier"));
		}
		if ("1".equals(study.getStudyParameterConfig().getCollectDob())) {
			fields.put(StudySubjectValidator.INPUT_DOB, fp.getString(StudySubjectValidator.INPUT_DOB));
		} else if ("2".equals(study.getStudyParameterConfig().getCollectDob())) {
			fields.put(StudySubjectValidator.INPUT_DOB, fp.getString(StudySubjectValidator.INPUT_YOB));
		}
		if ("true".equals(study.getStudyParameterConfig().getGenderRequired())) {
			String gender = fp.getString("gender");
			fields.put("gender", gender.isEmpty() ? "" : String.valueOf(gender.charAt(0)));
		}
	}

	private void clearSession(HttpServletRequest request) {
		request.getSession().removeAttribute("parameters");
		request.getSession().removeAttribute("fields");
		request.getSession().removeAttribute("oldValues");
		request.getSession().removeAttribute("subjectToUpdate");
		request.getSession().removeAttribute("id");
		request.getSession().removeAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
		request.getSession().removeAttribute(SUBJECTS_STUDY);
	}
}
