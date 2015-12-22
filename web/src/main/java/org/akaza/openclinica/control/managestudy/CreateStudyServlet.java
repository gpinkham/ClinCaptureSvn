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

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import com.clinovo.model.DiscrepancyDescription;
import com.clinovo.service.DiscrepancyDescriptionService;
import com.clinovo.util.ValidatorHelper;
import com.clinovo.validator.StudyValidator;

/**
 * Processes request to create a new study.
 */
@SuppressWarnings({"unchecked", "rawtypes", "serial"})
@Component
public class CreateStudyServlet extends Controller {

	public static final String FAC_NAME = "FacName";
	public static final String FAC_CITY = "FacCity";
	public static final String FAC_STATE = "FacState";
	public static final String FAC_ZIP = "FacZIP";
	public static final String FAC_COUNTRY = "FacCountry";
	public static final String FAC_CONTACT_NAME = "FacContactName";
	public static final String FAC_CONTACT_DEGREE = "FacContactDegree";
	public static final String FAC_CONTACT_PHONE = "FacContactPhone";
	public static final String FAC_CONTACT_EMAIL = "FacContactEmail";

	public static final int VALIDATION_NUM2 = 30;
	public static final int VALIDATION_NUM4 = 100;
	public static final int VALIDATION_NUM5 = 255;
	public static final int VALIDATION_NUM7 = 1000;
	public static final int VALIDATION_NUM8 = 2000;

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);

		if (ub.isSysAdmin()) {
			return;
		}

		addPageMessage(
				getResPage().getString("no_have_correct_privilege_current_study")
						+ getResPage().getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.STUDY_LIST_SERVLET, getResException().getString("not_admin"), "1");

	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		HashMap errors = getErrorsHolder(request);

		String action = request.getParameter("action");
		StudyInfoPanel panel = getStudyInfoPanel(request);
		panel.reset();
		panel.setStudyInfoShown(false);
		panel.setOrderedData(true);
		panel.setExtractData(false);
		panel.setSubmitDataModule(false);
		panel.setCreateDataset(false);
		panel.setIconInfoShown(true);
		panel.setManageSubject(false);

		if (StringUtil.isBlank(action)) {
			request.getSession().setAttribute("newStudy", new StudyBean());

			UserAccountDAO udao = new UserAccountDAO(getDataSource());
			Collection users = udao.findAllByRole(Role.STUDY_ADMINISTRATOR.getCode(), Role.STUDY_DIRECTOR.getCode());
			request.setAttribute("users", users);

			forwardPage(Page.CREATE_STUDY1, request, response);
		} else if ("next".equalsIgnoreCase(action)) {
			confirmStudy1(request, response, errors);
		} else {
			forwardPage(Page.STUDY_LIST_SERVLET, request, response);
		}
	}

	/**
	 * Validates the first section of study and save it into study bean.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @throws Exception
	 */
	private void confirmStudy1(HttpServletRequest request, HttpServletResponse response, HashMap errors)
			throws Exception {
		UserAccountBean ub = getUserAccountBean(request);

		Validator v = new Validator(new ValidatorHelper(request, getConfigurationDao()));
		FormProcessor fp = new FormProcessor(request);

		v.addValidation("name", Validator.NO_BLANKS);
		v.addValidation("uniqueProId", Validator.NO_BLANKS);
		v.addValidation("description", Validator.NO_BLANKS);
		v.addValidation("prinInvestigator", Validator.NO_BLANKS);
		v.addValidation("sponsor", Validator.NO_BLANKS);

		v.addValidation("secondProId", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, VALIDATION_NUM5);
		v.addValidation("collaborators", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, VALIDATION_NUM7);
		v.addValidation("protocolDescription", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, VALIDATION_NUM7);

		// check param presents before validation
		addValidatorIfParamPresented(request, "studySubjectIdLabel", v, Validator.NO_BLANKS);
		addValidatorIfParamPresented(request, "secondaryIdLabel", v, Validator.NO_BLANKS);
		addValidatorIfParamPresented(request, "dateOfEnrollmentForStudyLabel", v, Validator.NO_BLANKS);
		addValidatorIfParamPresented(request, "genderLabel", v, Validator.NO_BLANKS);
		addValidatorIfParamPresented(request, "startDateTimeLabel", v, Validator.NO_BLANKS);
		addValidatorIfParamPresented(request, "endDateTimeLabel", v, Validator.NO_BLANKS);
		errors.putAll(v.validate());
		StudyValidator.checkIfStudyFieldsAreUnique(fp,  errors, getStudyDAO(), getResPage(), getResException());
		if (fp.getString("name").trim().length() > VALIDATION_NUM4) {
			Validator.addError(errors, "name", getResException().getString("maximum_lenght_name_100"));
		}
		if (fp.getString("uniqueProId").trim().length() > VALIDATION_NUM2) {
			Validator.addError(errors, "uniqueProId", getResException().getString("maximum_lenght_unique_protocol_30"));
		}
		if (fp.getString("description").trim().length() > VALIDATION_NUM8) {
			Validator.addError(errors, "description", getResException().getString("maximum_lenght_brief_summary_2000"));
		}
		if (fp.getString("prinInvestigator").trim().length() > VALIDATION_NUM5) {
			Validator.addError(errors, "prinInvestigator",
					getResException().getString("maximum_lenght_principal_investigator_255"));
		}
		if (fp.getString("sponsor").trim().length() > VALIDATION_NUM5) {
			Validator.addError(errors, "sponsor", getResException().getString("maximum_lenght_sponsor_255"));
		}
		if (fp.getString("officialTitle").trim().length() > VALIDATION_NUM5) {
			Validator.addError(errors, "officialTitle", getResException().getString("maximum_lenght_official_title_255"));
		}
		if (fp.getString("studySubjectIdLabel").trim().length() > VALIDATION_NUM5) {
			Validator.addError(errors, "studySubjectIdLabel",
					getResException().getString("maximum_lenght_studySubjectIdLabel_255"));
		}
		if (fp.getString("secondaryIdLabel").trim().length() > VALIDATION_NUM5) {
			Validator.addError(errors, "secondaryIdLabel",
					getResException().getString("maximum_lenght_secondaryIdLabel_255"));
		}
		if (fp.getString("dateOfEnrollmentForStudyLabel").trim().length() > VALIDATION_NUM5) {
			Validator.addError(errors, "dateOfEnrollmentForStudyLabel",
					getResException().getString("maximum_lenght_dateOfEnrollmentForStudyLabel_255"));
		}
		if (fp.getString("genderLabel").trim().length() > VALIDATION_NUM5) {
			Validator.addError(errors, "genderLabel", getResException().getString("maximum_lenght_genderLabel_255"));
		}
		if (fp.getString("startDateTimeLabel").trim().length() > VALIDATION_NUM5) {
			Validator.addError(errors, "startDateTimeLabel",
					getResException().getString("maximum_lenght_startDateTimeLabel_255"));
		}
		if (fp.getString("endDateTimeLabel").trim().length() > VALIDATION_NUM5) {
			Validator.addError(errors, "endDateTimeLabel",
					getResException().getString("maximum_lenght_endDateTimeLabel_255"));
		}

		StudyBean studyBean = createStudyBean(request);

		if (errors.isEmpty()) {
			logger.info("no errors in the first section");
			request.setAttribute("studyPhaseMap", getMapsHolder().getStudyPhaseMap());
			request.setAttribute("statuses", Status.toActiveArrayList());
			logger.info("setting arrays to request, size of list: " + Status.toArrayList().size());
			if (request.getParameter("Save") != null && request.getParameter("Save").length() > 0) {
				StudyDAO sdao = new StudyDAO(getDataSource());
				studyBean.setOwner(ub);
				studyBean.setCreatedDate(new Date());
				studyBean.setStatus(Status.PENDING);
				studyBean = (StudyBean) sdao.create(studyBean);
				createDefaultDiscrepancyDescriptions(studyBean.getId());
				submitStudyParameters(studyBean);
				StudyBean newstudyBean = (StudyBean) sdao.findByName(studyBean.getName());

				String selectedUserIdStr = fp.getString("selectedUser");
				int selectedUserId = 0;
				if (selectedUserIdStr != null && selectedUserIdStr.length() > 0) {
					selectedUserId = Integer.parseInt(fp.getString("selectedUser"));
				}
				createStudyUserRoleForStudy(selectedUserId, newstudyBean, ub);

				addPageMessage(getResPage().getString("the_new_study_created_succesfully_current"), request);
				forwardPage(Page.STUDY_LIST_SERVLET, request, response);
			}
		} else {
			request.getSession().setAttribute("newStudy", studyBean);
			logger.info("has validation errors in the first section");
			request.setAttribute("formMessages", errors);
			UserAccountDAO udao = new UserAccountDAO(getDataSource());
			Collection users = udao.findAllByRole(Role.STUDY_ADMINISTRATOR.getName(), Role.STUDY_DIRECTOR.getName());
			request.setAttribute("users", users);

			forwardPage(Page.CREATE_STUDY1, request, response);
		}

	}

	private void createStudyUserRoleForStudy(int selectedUserId, StudyBean study, UserAccountBean ub) {

		UserAccountDAO udao = new UserAccountDAO(getDataSource());
		if (selectedUserId > 0) {
			UserAccountBean user = (UserAccountBean) udao.findByPK(selectedUserId);
			StudyUserRoleBean sub = new StudyUserRoleBean();
			sub.setRole(Role.STUDY_ADMINISTRATOR);
			sub.setStudyId(study.getId());
			sub.setStatus(Status.AVAILABLE);
			sub.setOwner(ub);
			udao.createStudyUserRole(user, sub);
			if (!ub.isSysAdmin() && ub.getId() != selectedUserId) {
				sub = new StudyUserRoleBean();
				sub.setRole(Role.STUDY_ADMINISTRATOR);
				sub.setStudyId(study.getId());
				sub.setStatus(Status.AVAILABLE);
				sub.setOwner(ub);
				udao.createStudyUserRole(ub, sub);
			}
		} else if (!ub.isSysAdmin()) {
			StudyUserRoleBean sub = new StudyUserRoleBean();
			sub.setRole(Role.STUDY_ADMINISTRATOR);
			sub.setStudyId(study.getId());
			sub.setStatus(Status.AVAILABLE);
			sub.setOwner(ub);
			udao.createStudyUserRole(ub, sub);
		}
	}

	private void createDefaultDiscrepancyDescriptions(int studyId) {
		DiscrepancyDescriptionService dDescriptionService = (DiscrepancyDescriptionService) SpringServletAccess
				.getApplicationContext(getServletContext()).getBean("discrepancyDescriptionService");
		final int dnFailedValidationCheckTypeId = 1;
		final int dnAnnotationTypeId = 2;
		final int dnQueryTypeId = 3;

		// create default update discrepancy descriptions
		dDescriptionService.saveDiscrepancyDescription(new DiscrepancyDescription(getResPage()
				.getString("corrected_CRF_data"), "", studyId, "Study and Site", dnFailedValidationCheckTypeId));
		dDescriptionService.saveDiscrepancyDescription(new DiscrepancyDescription(getResPage()
				.getString("CRF_data_was_correctly_entered"), "", studyId, "Study and Site",
				dnFailedValidationCheckTypeId));
		dDescriptionService.saveDiscrepancyDescription(new DiscrepancyDescription(getResPage()
				.getString("need_additional_clarification"), "", studyId, "Study and Site",
				dnFailedValidationCheckTypeId));
		dDescriptionService.saveDiscrepancyDescription(new DiscrepancyDescription(getResPage()
				.getString("requested_information_is_provided"), "", studyId, "Study and Site",
				dnFailedValidationCheckTypeId));

		// create default close discrepancy descriptions
		dDescriptionService.saveDiscrepancyDescription(new DiscrepancyDescription(getResPage()
				.getString("query_response_monitored"), "", studyId, "Study and Site", dnAnnotationTypeId));
		dDescriptionService.saveDiscrepancyDescription(new DiscrepancyDescription(getResPage()
				.getString("CRF_data_change_monitored"), "", studyId, "Study and Site", dnAnnotationTypeId));
		dDescriptionService.saveDiscrepancyDescription(new DiscrepancyDescription(getResPage()
				.getString("calendared_event_monitored"), "", studyId, "Study and Site", dnAnnotationTypeId));
		dDescriptionService.saveDiscrepancyDescription(new DiscrepancyDescription(getResPage()
				.getString("failed_edit_check_monitored"), "", studyId, "Study and Site", dnAnnotationTypeId));

		// create default RFC discrepancy descriptions
		dDescriptionService.saveDiscrepancyDescription(new DiscrepancyDescription(getResPage()
				.getString("corrected_CRF_data_entry_error"), "", studyId, "Study and Site", dnQueryTypeId));
		dDescriptionService.saveDiscrepancyDescription(new DiscrepancyDescription(getResPage()
				.getString("source_data_was_missing"), "", studyId, "Study and Site", dnQueryTypeId));
		dDescriptionService.saveDiscrepancyDescription(new DiscrepancyDescription(getResPage()
				.getString("source_data_was_incorrect"), "", studyId, "Study and Site", dnQueryTypeId));
		dDescriptionService.saveDiscrepancyDescription(new DiscrepancyDescription(getResPage()
				.getString("information_was_not_available"), "", studyId, "Study and Site", dnQueryTypeId));
	}

	private void addValidatorIfParamPresented(HttpServletRequest request, String paramName, Validator v,
			int validatorType) {
		if (request.getParameter(paramName) != null) {
			v.addValidation(paramName, validatorType);
		}
	}

	/**
	 * Submit study Parameters when study is created.
	 * @param newStudy StudyBean
	 */
	private void submitStudyParameters(StudyBean newStudy) {

		StudyParameterValueDAO spvdao = new StudyParameterValueDAO(getDataSource());

		logger.info("study bean to be created:" + newStudy.getName() + newStudy.getProtocolDateVerification());
		newStudy.setCreatedDate(new Date());

		logger.info("new study created");
		StudyParameterValueBean spv = new StudyParameterValueBean();
		spv.setStudyId(newStudy.getId());
		spv.setParameter("collectDob");
		spv.setValue(newStudy.getStudyParameterConfig().getCollectDob());
		spvdao.create(spv);

		spv.setParameter("discrepancyManagement");
		spv.setValue(newStudy.getStudyParameterConfig().getDiscrepancyManagement());
		spvdao.create(spv);

		spv.setParameter("genderRequired");
		spv.setValue(newStudy.getStudyParameterConfig().getGenderRequired());
		spvdao.create(spv);

		spv.setParameter("subjectPersonIdRequired");
		spv.setValue(newStudy.getStudyParameterConfig().getSubjectPersonIdRequired());
		spvdao.create(spv);

		spv.setParameter("interviewerNameRequired");
		spv.setValue(newStudy.getStudyParameterConfig().getInterviewerNameRequired());
		spvdao.create(spv);

		spv.setParameter("interviewerNameDefault");
		spv.setValue(newStudy.getStudyParameterConfig().getInterviewerNameDefault());
		spvdao.create(spv);

		spv.setParameter("interviewerNameEditable");
		spv.setValue(newStudy.getStudyParameterConfig().getInterviewerNameEditable());
		spvdao.create(spv);

		spv.setParameter("interviewDateRequired");
		spv.setValue(newStudy.getStudyParameterConfig().getInterviewDateRequired());
		spvdao.create(spv);

		spv.setParameter("interviewDateDefault");
		spv.setValue(newStudy.getStudyParameterConfig().getInterviewDateDefault());
		spvdao.create(spv);

		spv.setParameter("interviewDateEditable");
		spv.setValue(newStudy.getStudyParameterConfig().getInterviewDateEditable());
		spvdao.create(spv);

		spv.setParameter("subjectIdGeneration");
		spv.setValue(newStudy.getStudyParameterConfig().getSubjectIdGeneration());
		spvdao.create(spv);

		spv.setParameter("autoGeneratedPrefix");
		spv.setValue(newStudy.getStudyParameterConfig().getAutoGeneratedPrefix());
		spvdao.create(spv);

		spv.setParameter("autoGeneratedSeparator");
		spv.setValue(newStudy.getStudyParameterConfig().getAutoGeneratedSeparator());
		spvdao.create(spv);

		spv.setParameter("autoGeneratedSuffix");
		spv.setValue(newStudy.getStudyParameterConfig().getAutoGeneratedSuffix());
		spvdao.create(spv);

		spv.setParameter("subjectIdPrefixSuffix");
		spv.setValue(newStudy.getStudyParameterConfig().getSubjectIdPrefixSuffix());
		spvdao.create(spv);

		spv.setParameter("personIdShownOnCRF");
		spv.setValue(newStudy.getStudyParameterConfig().getPersonIdShownOnCRF());
		spvdao.create(spv);

		spv.setParameter("secondaryLabelViewable");
		spv.setValue(newStudy.getStudyParameterConfig().getSecondaryLabelViewable());
		spvdao.create(spv);

		spv.setParameter("adminForcedReasonForChange");
		spv.setValue(newStudy.getStudyParameterConfig().getAdminForcedReasonForChange());
		spvdao.create(spv);

		spv.setParameter("secondaryIdRequired");
		spv.setValue(newStudy.getStudyParameterConfig().getSecondaryIdRequired());
		spvdao.create(spv);

		spv.setParameter("dateOfEnrollmentForStudyRequired");
		spv.setValue(newStudy.getStudyParameterConfig().getDateOfEnrollmentForStudyRequired());
		spvdao.create(spv);

		spv.setParameter("studySubjectIdLabel");
		spv.setValue(newStudy.getStudyParameterConfig().getStudySubjectIdLabel());
		spvdao.create(spv);

		spv.setParameter("secondaryIdLabel");
		spv.setValue(newStudy.getStudyParameterConfig().getSecondaryIdLabel());
		spvdao.create(spv);

		spv.setParameter("dateOfEnrollmentForStudyLabel");
		spv.setValue(newStudy.getStudyParameterConfig().getDateOfEnrollmentForStudyLabel());
		spvdao.create(spv);

		spv.setParameter("genderLabel");
		spv.setValue(newStudy.getStudyParameterConfig().getGenderLabel());
		spvdao.create(spv);

		spv.setParameter("startDateTimeRequired");
		spv.setValue(newStudy.getStudyParameterConfig().getStartDateTimeRequired());
		spvdao.create(spv);

		spv.setParameter("useStartTime");
		spv.setValue(newStudy.getStudyParameterConfig().getUseStartTime());
		spvdao.create(spv);

		spv.setParameter("endDateTimeRequired");
		spv.setValue(newStudy.getStudyParameterConfig().getEndDateTimeRequired());
		spvdao.create(spv);

		spv.setParameter("useEndTime");
		spv.setValue(newStudy.getStudyParameterConfig().getUseEndTime());
		spvdao.create(spv);

		spv.setParameter("startDateTimeLabel");
		spv.setValue(newStudy.getStudyParameterConfig().getStartDateTimeLabel());
		spvdao.create(spv);

		spv.setParameter("endDateTimeLabel");
		spv.setValue(newStudy.getStudyParameterConfig().getEndDateTimeLabel());
		spvdao.create(spv);

		spv.setParameter("autoScheduleEventDuringImport");
		spv.setValue(newStudy.getStudyParameterConfig().getAutoScheduleEventDuringImport());
		spvdao.create(spv);

		spv.setParameter("autoCreateSubjectDuringImport");
		spv.setValue(newStudy.getStudyParameterConfig().getAutoCreateSubjectDuringImport());
		spvdao.create(spv);

		spv.setParameter("markImportedCRFAsCompleted");
		spv.setValue(newStudy.getStudyParameterConfig().getMarkImportedCRFAsCompleted());
		spvdao.create(spv);

		spv.setParameter("allowSdvWithOpenQueries");
		spv.setValue(newStudy.getStudyParameterConfig().getAllowSdvWithOpenQueries());
		spvdao.create(spv);

		spv.setParameter("replaceExisitingDataDuringImport");
		spv.setValue(newStudy.getStudyParameterConfig().getReplaceExisitingDataDuringImport());
		spvdao.create(spv);

		spv.setParameter("defaultBioontologyURL");
		spv.setValue(newStudy.getStudyParameterConfig().getDefaultBioontologyURL());
		spvdao.create(spv);

		spv.setParameter("medicalCodingApiKey");
		spv.setValue(newStudy.getStudyParameterConfig().getMedicalCodingApiKey());
		spvdao.create(spv);

		spv.setParameter("medicalCodingApiKey");
		spv.setValue(newStudy.getStudyParameterConfig().getMedicalCodingApiKey());
		spvdao.create(spv);

		spv.setParameter("autoCodeDictionaryName");
		spv.setValue(newStudy.getStudyParameterConfig().getAutoCodeDictionaryName());
		spvdao.create(spv);

		spv.setParameter("medicalCodingApprovalNeeded");
		spv.setValue(newStudy.getStudyParameterConfig().getMedicalCodingApprovalNeeded());
		spvdao.create(spv);

		spv.setParameter("medicalCodingContextNeeded");
		spv.setValue(newStudy.getStudyParameterConfig().getMedicalCodingContextNeeded());
		spvdao.create(spv);

		spv.setParameter("evaluateWithContext");
		spv.setValue(newStudy.getStudyParameterConfig().getEvaluateWithContext());
		spvdao.create(spv);

		spv.setParameter("allowRulesAutoScheduling");
		spv.setValue(newStudy.getStudyParameterConfig().getAllowRulesAutoScheduling());
		spvdao.create(spv);

		spv.setParameter("randomizationEnviroment");
		spv.setValue(newStudy.getStudyParameterConfig().getRandomizationEnviroment());
		spvdao.create(spv);

		spv.setParameter("autoTabbing");
		spv.setValue(newStudy.getStudyParameterConfig().getAutoTabbing());
		spvdao.create(spv);

		spv.setParameter("showYearsInCalendar");
		spv.setValue(newStudy.getStudyParameterConfig().getShowYearsInCalendar());
		spvdao.create(spv);

		spv.setParameter("instanceType");
		spv.setValue(newStudy.getStudyParameterConfig().getInstanceType());
		spvdao.create(spv);

		spv.setParameter("eventLocationRequired");
		spv.setValue(newStudy.getStudyParameterConfig().getEventLocationRequired());
		spvdao.create(spv);

		spv.setParameter("assignRandomizationResultTo");
		spv.setValue(newStudy.getStudyParameterConfig().getAssignRandomizationResultTo());
		spvdao.create(spv);

		spv.setParameter("randomizationTrialId");
		spv.setValue(newStudy.getStudyParameterConfig().getRandomizationTrialId());
		spvdao.create(spv);

		spv.setParameter("allowDynamicGroupsManagement");
		spv.setValue(newStudy.getStudyParameterConfig().getAllowDynamicGroupsManagement());
		spvdao.create(spv);

		spv.setParameter("annotatedCrfSasItemNames");
		spv.setValue(newStudy.getStudyParameterConfig().getAnnotatedCrfSasItemNames());
		spvdao.create(spv);

		spv.setParameter("allowDiscrepancyCorrectionForms");
		spv.setValue(newStudy.getStudyParameterConfig().getAllowDiscrepancyCorrectionForms());
		spvdao.create(spv);
		
		spv.setParameter("crfAnnotation");
		spv.setValue(newStudy.getStudyParameterConfig().getCrfAnnotation());
		spvdao.create(spv);
		
		spv.setParameter("dynamicGroup");
		spv.setValue(newStudy.getStudyParameterConfig().getDynamicGroup());
		spvdao.create(spv);
		
		spv.setParameter("calendaredVisits");
		spv.setValue(newStudy.getStudyParameterConfig().getCalendaredVisits());
		spvdao.create(spv);
		
		spv.setParameter("interactiveDashboards");
		spv.setValue(newStudy.getStudyParameterConfig().getInteractiveDashboards());
		spvdao.create(spv);
		
		spv.setParameter("itemLevelSDV");
		spv.setValue(newStudy.getStudyParameterConfig().getItemLevelSDV());
		spvdao.create(spv);
		
		spv.setParameter("subjectCasebookInPDF");
		spv.setValue(newStudy.getStudyParameterConfig().getSubjectCasebookInPDF());
		spvdao.create(spv);
		
		spv.setParameter("crfMasking");
		spv.setValue(newStudy.getStudyParameterConfig().getCrfMasking());
		spvdao.create(spv);
		
		spv.setParameter("sasExtracts");
		spv.setValue(newStudy.getStudyParameterConfig().getSasExtracts());
		spvdao.create(spv);
		
		spv.setParameter("studyEvaluator");
		spv.setValue(newStudy.getStudyParameterConfig().getStudyEvaluator());
		spvdao.create(spv);
		
		spv.setParameter("randomization");
		spv.setValue(newStudy.getStudyParameterConfig().getRandomization());
		spvdao.create(spv);
		
		spv.setParameter("medicalCoding");
		spv.setValue(newStudy.getStudyParameterConfig().getMedicalCoding());
		spvdao.create(spv);

		logger.info("study parameters created done");
	}

	/**
	 * Constructs study bean from the first section.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return StudyBean
	 */
	private StudyBean createStudyBean(HttpServletRequest request) {
		FormProcessor fp = new FormProcessor(request);
		StudyBean newStudy = new StudyBean();
		newStudy.setName(fp.getString("name"));
		newStudy.setOfficialTitle(fp.getString("officialTitle"));
		newStudy.setIdentifier(fp.getString("uniqueProId"));
		newStudy.setSecondaryIdentifier(fp.getString("secondProId"));
		newStudy.setPrincipalInvestigator(fp.getString("prinInvestigator"));
		newStudy.setProtocolType(fp.getString("protocolType"));
		
		newStudy.setSummary(fp.getString("description"));
		newStudy.setProtocolDescription(fp.getString("protocolDescription"));

		newStudy.setSponsor(fp.getString("sponsor"));
		newStudy.setCollaborators(fp.getString("collaborators"));
		
		// Features 
		newStudy.getStudyParameterConfig().setCrfAnnotation(fp.getString("crfAnnotation"));
		newStudy.getStudyParameterConfig().setDynamicGroup(fp.getString("dynamicGroup"));
		newStudy.getStudyParameterConfig().setCalendaredVisits(fp.getString("calendaredVisits"));
		newStudy.getStudyParameterConfig().setInteractiveDashboards(fp.getString("interactiveDashboards"));
		newStudy.getStudyParameterConfig().setItemLevelSDV(fp.getString("itemLevelSDV"));
		newStudy.getStudyParameterConfig().setSubjectCasebookInPDF(fp.getString("subjectCasebookInPDF"));
		newStudy.getStudyParameterConfig().setCrfMasking(fp.getString("crfMasking"));
		newStudy.getStudyParameterConfig().setSasExtracts(fp.getString("sasExtracts"));
		newStudy.getStudyParameterConfig().setStudyEvaluator(fp.getString("studyEvaluator"));
		newStudy.getStudyParameterConfig().setRandomization(fp.getString("randomization"));
		newStudy.getStudyParameterConfig().setMedicalCoding(fp.getString("medicalCoding"));
				
		return newStudy;
	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		return Controller.ADMIN_SERVLET_CODE;
	}

}
