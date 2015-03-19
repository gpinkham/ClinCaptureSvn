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

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.akaza.openclinica.bean.managestudy.InterventionBean;
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
import org.akaza.openclinica.web.SQLInitServlet;
import org.springframework.stereotype.Component;

import com.clinovo.model.DiscrepancyDescription;
import com.clinovo.service.DiscrepancyDescriptionService;
import com.clinovo.util.ValidatorHelper;

/**
 * Processes request to create a new study.
 */
@SuppressWarnings({"unchecked", "rawtypes", "serial"})
@Component
public class CreateStudyServlet extends Controller {

	public static final String INPUT_START_DATE = "startDate";

	public static final String INPUT_END_DATE = "endDate";

	public static final String INPUT_VER_DATE = "protocolDateVerification";

	public static final String FAC_NAME = "FacName";

	public static final String FAC_CITY = "FacCity";

	public static final String FAC_STATE = "FacState";

	public static final String FAC_ZIP = "FacZIP";

	public static final String FAC_COUNTRY = "FacCountry";

	public static final String FAC_CONTACT_NAME = "FacContactName";

	public static final String FAC_CONTACT_DEGREE = "FacContactDegree";

	public static final String FAC_CONTACT_PHONE = "FacContactPhone";

	public static final String FAC_CONTACT_EMAIL = "FacContactEmail";

	public static final int VALIDATION_NUM1 = 20;
	public static final int VALIDATION_NUM2 = 30;
	public static final int VALIDATION_NUM3 = 64;
	public static final int VALIDATION_NUM4 = 100;
	public static final int VALIDATION_NUM5 = 255;
	public static final int VALIDATION_NUM6 = 500;
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
				respage.getString("no_have_correct_privilege_current_study")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.STUDY_LIST_SERVLET, resexception.getString("not_admin"), "1");

	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);

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
			Collection users = udao.findAllByRole(Role.STUDY_ADMINISTRATOR.getName(), Role.STUDY_DIRECTOR.getName());
			request.setAttribute("users", users);

			forwardPage(Page.CREATE_STUDY1, request, response);
		} else {
			if ("confirm".equalsIgnoreCase(action)) {
				confirmWholeStudy(request, response, errors);

			} else if ("cancel".equalsIgnoreCase(action)) {
				addPageMessage(respage.getString("study_creation_cancelled"), request);
				forwardPage(Page.STUDY_LIST_SERVLET, request, response);
			} else if ("submit".equalsIgnoreCase(action)) {

				submitStudy(request);

				request.getSession().removeAttribute("interventions");
				request.getSession().removeAttribute("isInterventionalFlag");
				request.getSession().removeAttribute("interventionArray");

				// swith user to the newly created study

				request.getSession().setAttribute("study", request.getSession().getAttribute("newStudy"));
				request.getSession().removeAttribute("newStudy");

				UserAccountDAO udao = new UserAccountDAO(getDataSource());
				if (!ub.isSysAdmin()) {
					StudyUserRoleBean sub = new StudyUserRoleBean();
					sub.setRole(Role.STUDY_ADMINISTRATOR);
					sub.setStudyId(currentStudy.getId());
					sub.setStatus(Status.AVAILABLE);
					sub.setOwner(ub);
					udao.createStudyUserRole(ub, sub);
					request.getSession().setAttribute("userRole", sub);
				}

				addPageMessage(respage.getString("the_new_study_created_succesfully_current"), request);

				ArrayList pageMessages = (ArrayList) request.getAttribute(PAGE_MESSAGE);
				request.getSession().setAttribute("pageMessages", pageMessages);
				response.sendRedirect(request.getContextPath() + Page.MANAGE_STUDY_MODULE);
			} else if ("next".equalsIgnoreCase(action)) {
				Integer pageNumber = Integer.valueOf(request.getParameter("pageNum"));
				final int page2 = 2;
				final int page3 = 3;
				final int page4 = 4;
				final int page5 = 5;
				final int page6 = 6;
				if (pageNumber != null) {
					if (pageNumber == page6) {
						confirmStudy6(request, response, errors);
					} else if (pageNumber == page5) {
						confirmStudy5(request, response, errors);
					} else if (pageNumber == page4) {
						confirmStudy4(request, response, errors);
					} else if (pageNumber == page3) {
						confirmStudy3(request, response, errors);
					} else if (pageNumber == page2) {
						confirmStudy2(request, response, errors);
					} else {
						System.out.println("confirm study 1 " + pageNumber);
						confirmStudy1(request, response, errors);
					}
				} else {
					if (request.getSession().getAttribute("newStudy") == null) {
						request.getSession().setAttribute("newStudy", new StudyBean());
					}

					UserAccountDAO udao = new UserAccountDAO(getDataSource());
					Collection users = udao.findAllByRole(Role.STUDY_ADMINISTRATOR.getName(),
							Role.STUDY_DIRECTOR.getName());
					request.setAttribute("users", users);

					forwardPage(Page.CREATE_STUDY1, request, response);
				}
			}
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
		// check to see if name and uniqueProId are unique, tbh
		StudyDAO studyDAO = new StudyDAO(getDataSource());
		ArrayList<StudyBean> allStudies = (ArrayList<StudyBean>) studyDAO.findAll();
		for (StudyBean thisBean : allStudies) {
			if (fp.getString("name").trim().equals(thisBean.getName())) {
				MessageFormat mf = new MessageFormat("");
				mf.applyPattern(respage.getString("brief_title_existed"));
				Object[] arguments = {fp.getString("name").trim()};

				Validator.addError(errors, "name", mf.format(arguments));
			}
			if (fp.getString("uniqueProId").trim().equals(thisBean.getIdentifier())) {
				Validator.addError(errors, "uniqueProId", resexception.getString("unique_protocol_id_existed"));
			}
		}
		if (fp.getString("name").trim().length() > VALIDATION_NUM4) {
			Validator.addError(errors, "name", resexception.getString("maximum_lenght_name_100"));
		}
		if (fp.getString("uniqueProId").trim().length() > VALIDATION_NUM2) {
			Validator.addError(errors, "uniqueProId", resexception.getString("maximum_lenght_unique_protocol_30"));
		}
		if (fp.getString("description").trim().length() > VALIDATION_NUM8) {
			Validator.addError(errors, "description", resexception.getString("maximum_lenght_brief_summary_2000"));
		}
		if (fp.getString("prinInvestigator").trim().length() > VALIDATION_NUM5) {
			Validator.addError(errors, "prinInvestigator",
					resexception.getString("maximum_lenght_principal_investigator_255"));
		}
		if (fp.getString("sponsor").trim().length() > VALIDATION_NUM5) {
			Validator.addError(errors, "sponsor", resexception.getString("maximum_lenght_sponsor_255"));
		}
		if (fp.getString("officialTitle").trim().length() > VALIDATION_NUM5) {
			Validator.addError(errors, "officialTitle", resexception.getString("maximum_lenght_official_title_255"));
		}
		if (fp.getString("studySubjectIdLabel").trim().length() > VALIDATION_NUM5) {
			Validator.addError(errors, "studySubjectIdLabel",
					resexception.getString("maximum_lenght_studySubjectIdLabel_255"));
		}
		if (fp.getString("secondaryIdLabel").trim().length() > VALIDATION_NUM5) {
			Validator.addError(errors, "secondaryIdLabel",
					resexception.getString("maximum_lenght_secondaryIdLabel_255"));
		}
		if (fp.getString("dateOfEnrollmentForStudyLabel").trim().length() > VALIDATION_NUM5) {
			Validator.addError(errors, "dateOfEnrollmentForStudyLabel",
					resexception.getString("maximum_lenght_dateOfEnrollmentForStudyLabel_255"));
		}
		if (fp.getString("genderLabel").trim().length() > VALIDATION_NUM5) {
			Validator.addError(errors, "genderLabel", resexception.getString("maximum_lenght_genderLabel_255"));
		}
		if (fp.getString("startDateTimeLabel").trim().length() > VALIDATION_NUM5) {
			Validator.addError(errors, "startDateTimeLabel",
					resexception.getString("maximum_lenght_startDateTimeLabel_255"));
		}
		if (fp.getString("endDateTimeLabel").trim().length() > VALIDATION_NUM5) {
			Validator.addError(errors, "endDateTimeLabel",
					resexception.getString("maximum_lenght_endDateTimeLabel_255"));
		}

		StudyBean studyBean = createStudyBean(request);

		if (errors.isEmpty()) {
			logger.info("no errors in the first section");
			request.setAttribute("studyPhaseMap", studyPhaseMap);
			request.setAttribute("statuses", Status.toActiveArrayList());
			logger.info("setting arrays to request, size of list: " + Status.toArrayList().size());
			if (request.getParameter("Save") != null && request.getParameter("Save").length() > 0) {
				StudyDAO sdao = new StudyDAO(getDataSource());
				studyBean.setOwner(ub);
				studyBean.setCreatedDate(new Date());
				studyBean.setStatus(Status.PENDING);
				studyBean = (StudyBean) sdao.create(studyBean);
				createDefaultDiscrepancyDescriptions(studyBean.getId(), request);
				StudyBean newstudyBean = (StudyBean) sdao.findByName(studyBean.getName());

				UserAccountDAO udao = new UserAccountDAO(getDataSource());
				String selectedUserIdStr = fp.getString("selectedUser");
				int selectedUserId = 0;
				if (selectedUserIdStr != null && selectedUserIdStr.length() > 0) {
					selectedUserId = Integer.parseInt(fp.getString("selectedUser"));
				}
				if (selectedUserId > 0) {
					UserAccountBean user = (UserAccountBean) udao.findByPK(selectedUserId);
					StudyUserRoleBean sub = new StudyUserRoleBean();
					sub.setRole(Role.STUDY_ADMINISTRATOR);
					sub.setStudyId(newstudyBean.getId());
					sub.setStatus(Status.AVAILABLE);
					sub.setOwner(ub);
					udao.createStudyUserRole(user, sub);
					if (!ub.isSysAdmin() && ub.getId() != selectedUserId) {
						sub = new StudyUserRoleBean();
						sub.setRole(Role.STUDY_ADMINISTRATOR);
						sub.setStudyId(newstudyBean.getId());
						sub.setStatus(Status.AVAILABLE);
						sub.setOwner(ub);
						udao.createStudyUserRole(ub, sub);
					}
				} else if (!ub.isSysAdmin()) {
					StudyUserRoleBean sub = new StudyUserRoleBean();
					sub.setRole(Role.STUDY_ADMINISTRATOR);
					sub.setStudyId(newstudyBean.getId());
					sub.setStatus(Status.AVAILABLE);
					sub.setOwner(ub);
					udao.createStudyUserRole(ub, sub);
				}
				addPageMessage(respage.getString("the_new_study_created_succesfully_current"), request);
				forwardPage(Page.STUDY_LIST_SERVLET, request, response);
			} else {
				request.getSession().setAttribute("newStudy", studyBean);
				forwardPage(Page.CREATE_STUDY2, request, response);
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

	private void createDefaultDiscrepancyDescriptions(int studyId, HttpServletRequest request) {
		DiscrepancyDescriptionService dDescriptionService = (DiscrepancyDescriptionService) SpringServletAccess
				.getApplicationContext(getServletContext()).getBean("discrepancyDescriptionService");
		final int dnFailedValidationCheckTypeId = 1;
		final int dnAnnotationTypeId = 2;
		final int dnQueryTypeId = 3;

		// create default update discrepancy descriptions
		dDescriptionService.saveDiscrepancyDescription(new DiscrepancyDescription(respage
				.getString("corrected_CRF_data"), "", studyId, "Study and Site", dnFailedValidationCheckTypeId));
		dDescriptionService.saveDiscrepancyDescription(new DiscrepancyDescription(respage
				.getString("CRF_data_was_correctly_entered"), "", studyId, "Study and Site",
				dnFailedValidationCheckTypeId));
		dDescriptionService.saveDiscrepancyDescription(new DiscrepancyDescription(respage
				.getString("need_additional_clarification"), "", studyId, "Study and Site",
				dnFailedValidationCheckTypeId));
		dDescriptionService.saveDiscrepancyDescription(new DiscrepancyDescription(respage
				.getString("requested_information_is_provided"), "", studyId, "Study and Site",
				dnFailedValidationCheckTypeId));

		// create default close discrepancy descriptions
		dDescriptionService.saveDiscrepancyDescription(new DiscrepancyDescription(respage
				.getString("query_response_monitored"), "", studyId, "Study and Site", dnAnnotationTypeId));
		dDescriptionService.saveDiscrepancyDescription(new DiscrepancyDescription(respage
				.getString("CRF_data_change_monitored"), "", studyId, "Study and Site", dnAnnotationTypeId));
		dDescriptionService.saveDiscrepancyDescription(new DiscrepancyDescription(respage
				.getString("calendared_event_monitored"), "", studyId, "Study and Site", dnAnnotationTypeId));
		dDescriptionService.saveDiscrepancyDescription(new DiscrepancyDescription(respage
				.getString("failed_edit_check_monitored"), "", studyId, "Study and Site", dnAnnotationTypeId));

		// create default RFC discrepancy descriptions
		dDescriptionService.saveDiscrepancyDescription(new DiscrepancyDescription(respage
				.getString("corrected_CRF_data_entry_error"), "", studyId, "Study and Site", dnQueryTypeId));
		dDescriptionService.saveDiscrepancyDescription(new DiscrepancyDescription(respage
				.getString("source_data_was_missing"), "", studyId, "Study and Site", dnQueryTypeId));
		dDescriptionService.saveDiscrepancyDescription(new DiscrepancyDescription(respage
				.getString("source_data_was_incorrect"), "", studyId, "Study and Site", dnQueryTypeId));
		dDescriptionService.saveDiscrepancyDescription(new DiscrepancyDescription(respage
				.getString("information_was_not_available"), "", studyId, "Study and Site", dnQueryTypeId));
	}

	private void addValidatorIfParamPresented(HttpServletRequest request, String paramName, Validator v,
			int validatorType) {
		if (request.getParameter(paramName) != null) {
			v.addValidation(paramName, validatorType);
		}
	}

	/**
	 * Validates the second section of study info inputs.
	 * 
	 * @throws Exception
	 */
	private void confirmStudy2(HttpServletRequest request, HttpServletResponse response, HashMap errors)
			throws Exception {
		Validator v = new Validator(new ValidatorHelper(request, getConfigurationDao()));
		FormProcessor fp = new FormProcessor(request);

		v.addValidation(INPUT_START_DATE, Validator.IS_A_DATE);
		if (!StringUtil.isBlank(fp.getString(INPUT_END_DATE))) {
			v.addValidation(INPUT_END_DATE, Validator.IS_A_DATE);
		}

		v.addValidation("protocolType", Validator.NO_BLANKS);
		if (!StringUtil.isBlank(fp.getString(INPUT_VER_DATE))) {
			v.addValidation(INPUT_VER_DATE, Validator.IS_A_DATE_WITHOUT_REQUIRED_CHECK);
		}
		errors.putAll(v.validate());
		boolean isInterventional = updateStudy2(request);
		request.getSession().setAttribute("isInterventionalFlag", isInterventional);

		SimpleDateFormat localDf = getLocalDf(request);

		if (errors.isEmpty()) {
			logger.info("no errors");
			setMaps(request, isInterventional);
			if (isInterventional) {
				forwardPage(Page.CREATE_STUDY3, request, response);
			} else {
				forwardPage(Page.CREATE_STUDY4, request, response);
			}

		} else {
			logger.info("has validation errors");
			try {
				localDf.parse(fp.getString(INPUT_START_DATE));
				fp.addPresetValue(INPUT_START_DATE, localDf.format(fp.getDate(INPUT_START_DATE)));
			} catch (ParseException pe) {
				fp.addPresetValue(INPUT_START_DATE, fp.getString(INPUT_START_DATE));
			}
			try {
				localDf.parse(fp.getString(INPUT_VER_DATE));
				fp.addPresetValue(INPUT_VER_DATE, localDf.format(fp.getDate(INPUT_VER_DATE)));
			} catch (ParseException pe) {
				fp.addPresetValue(INPUT_VER_DATE, fp.getString(INPUT_VER_DATE));
			}
			try {
				localDf.parse(fp.getString(INPUT_END_DATE));
				fp.addPresetValue(INPUT_END_DATE, localDf.format(fp.getDate(INPUT_END_DATE)));
			} catch (ParseException pe) {
				fp.addPresetValue(INPUT_END_DATE, fp.getString(INPUT_END_DATE));
			}
			setPresetValues(fp.getPresetValues(), request);
			request.setAttribute("formMessages", errors);
			request.setAttribute("studyPhaseMap", studyPhaseMap);
			request.setAttribute("statuses", Status.toActiveArrayList());
			forwardPage(Page.CREATE_STUDY2, request, response);
		}

	}

	/**
	 * Confirms the third section of study info inputs.
	 * 
	 * @throws Exception
	 */
	private void confirmStudy3(HttpServletRequest request, HttpServletResponse response, HashMap errors)
			throws Exception {
		Validator v = new Validator(new ValidatorHelper(request, getConfigurationDao()));
		FormProcessor fp = new FormProcessor(request);

		v.addValidation("purpose", Validator.NO_BLANKS);
		final int counter = 10;
		for (int i = 0; i < counter; i++) {
			String type = fp.getString("interType" + i);
			String name = fp.getString("interName" + i);
			if (!StringUtil.isBlank(type) && StringUtil.isBlank(name)) {
				v.addValidation("interName", Validator.NO_BLANKS);
				request.setAttribute("interventionError", respage.getString("name_cannot_be_blank_if_type"));
				break;
			}
			if (!StringUtil.isBlank(name) && StringUtil.isBlank(type)) {
				v.addValidation("interType", Validator.NO_BLANKS);
				request.setAttribute("interventionError", respage.getString("name_cannot_be_blank_if_name"));
				break;
			}
		}

		errors.putAll(v.validate());

		boolean isInterventional = true;
		if (request.getSession().getAttribute("isInterventionalFlag") != null) {
			isInterventional = (Boolean) request.getSession().getAttribute("isInterventionalFlag");
		}
		updateStudy3(request, isInterventional);

		if (errors.isEmpty()) {
			logger.info("no errors");
			if (request.getSession().getAttribute("interventionArray") == null) {
				request.setAttribute("interventions", new ArrayList());
			} else {
				request.setAttribute("interventions", request.getSession().getAttribute("interventionArray"));
			}
			forwardPage(Page.CREATE_STUDY5, request, response);

		} else {
			logger.info("has validation errors");

			request.setAttribute("formMessages", errors);
			setMaps(request, isInterventional);
			if (isInterventional) {
				forwardPage(Page.CREATE_STUDY3, request, response);
			} else {
				forwardPage(Page.CREATE_STUDY4, request, response);
			}
		}

	}

	/**
	 * Validates the forth section of study and save it into study bean.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @throws Exception
	 */
	private void confirmStudy4(HttpServletRequest request, HttpServletResponse response, HashMap errors)
			throws Exception {
		Validator v = new Validator(new ValidatorHelper(request, getConfigurationDao()));
		FormProcessor fp = new FormProcessor(request);

		v.addValidation("conditions", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, VALIDATION_NUM6);
		v.addValidation("keywords", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, VALIDATION_NUM5);
		v.addValidation("eligibility", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, VALIDATION_NUM6);

		logger.info("expectedTotalEnrollment:" + fp.getInt("expectedTotalEnrollment"));
		errors.putAll(v.validate());
		if (fp.getInt("expectedTotalEnrollment") <= 0) {
			Validator.addError(errors, "expectedTotalEnrollment",
					respage.getString("expected_total_enrollment_must_be_a_positive_number"));
		}

		StudyBean newStudy = (StudyBean) request.getSession().getAttribute("newStudy");
		newStudy.setConditions(fp.getString("conditions"));
		newStudy.setKeywords(fp.getString("keywords"));
		newStudy.setEligibility(fp.getString("eligibility"));
		newStudy.setGender(fp.getString("gender"));

		newStudy.setAgeMax(fp.getString("ageMax"));
		newStudy.setAgeMin(fp.getString("ageMin"));
		newStudy.setHealthyVolunteerAccepted(fp.getBoolean("healthyVolunteerAccepted"));
		newStudy.setExpectedTotalEnrollment(fp.getInt("expectedTotalEnrollment"));
		request.getSession().setAttribute("newStudy", newStudy);
		request.setAttribute("facRecruitStatusMap", facRecruitStatusMap);
		if (errors.isEmpty()) {
			// get default facility info from property file
			newStudy.setFacilityName(SQLInitServlet.getField(FAC_NAME));
			newStudy.setFacilityCity(SQLInitServlet.getField(FAC_CITY));
			newStudy.setFacilityState(SQLInitServlet.getField(FAC_STATE));
			newStudy.setFacilityCountry(SQLInitServlet.getField(FAC_COUNTRY));
			newStudy.setFacilityContactDegree(SQLInitServlet.getField(FAC_CONTACT_DEGREE));
			newStudy.setFacilityContactEmail(SQLInitServlet.getField(FAC_CONTACT_EMAIL));
			newStudy.setFacilityContactName(SQLInitServlet.getField(FAC_CONTACT_NAME));
			newStudy.setFacilityContactPhone(SQLInitServlet.getField(FAC_CONTACT_PHONE));
			newStudy.setFacilityZip(SQLInitServlet.getField(FAC_ZIP));

			request.getSession().setAttribute("newStudy", newStudy);
			forwardPage(Page.CREATE_STUDY6, request, response);

		} else {
			request.setAttribute("formMessages", errors);
			forwardPage(Page.CREATE_STUDY5, request, response);
		}
	}

	/**
	 * Validates the forth section of study and save it into study bean.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @throws Exception
	 */
	private void confirmStudy5(HttpServletRequest request, HttpServletResponse response, HashMap errors)
			throws Exception {
		FormProcessor fp = new FormProcessor(request);
		Validator v = new Validator(new ValidatorHelper(request, getConfigurationDao()));
		if (!StringUtil.isBlank(fp.getString("facConEmail"))) {
			v.addValidation("facConEmail", Validator.IS_A_EMAIL);
		}
		v.addValidation("facName", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, VALIDATION_NUM5);
		v.addValidation("facCity", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, VALIDATION_NUM5);
		v.addValidation("facState", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, VALIDATION_NUM1);
		v.addValidation("facZip", Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO,
				VALIDATION_NUM3);
		v.addValidation("facCountry", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, VALIDATION_NUM3);
		v.addValidation("facConName", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, VALIDATION_NUM5);
		v.addValidation("facConDegree", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, VALIDATION_NUM5);
		v.addValidation("facConPhone", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, VALIDATION_NUM5);
		v.addValidation("facConEmail", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, VALIDATION_NUM5);

		errors.putAll(v.validate());

		StudyBean newStudy = (StudyBean) request.getSession().getAttribute("newStudy");

		newStudy.setFacilityCity(fp.getString("facCity"));
		newStudy.setFacilityContactDegree(fp.getString("facConDrgree"));
		newStudy.setFacilityName(fp.getString("facName"));
		newStudy.setFacilityContactEmail(fp.getString("facConEmail"));
		newStudy.setFacilityContactPhone(fp.getString("facConPhone"));
		newStudy.setFacilityContactName(fp.getString("facConName"));
		newStudy.setFacilityCountry(fp.getString("facCountry"));
		newStudy.setFacilityContactDegree(fp.getString("facConDegree"));
		newStudy.setFacilityState(fp.getString("facState"));
		newStudy.setFacilityZip(fp.getString("facZip"));

		request.getSession().setAttribute("newStudy", newStudy);
		if (errors.isEmpty()) {
			forwardPage(Page.CREATE_STUDY7, request, response);
		} else {
			request.setAttribute("formMessages", errors);
			request.setAttribute("facRecruitStatusMap", facRecruitStatusMap);
			forwardPage(Page.CREATE_STUDY6, request, response);
		}
	}

	/**
	 * Lets user confirm all the study info entries input.
	 * 
	 * @throws Exception
	 */
	private void confirmStudy6(HttpServletRequest request, HttpServletResponse response, HashMap errors)
			throws Exception {

		FormProcessor fp = new FormProcessor(request);
		Validator v = new Validator(new ValidatorHelper(request, getConfigurationDao()));
		v.addValidation("medlineIdentifier", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, VALIDATION_NUM5);
		v.addValidation("url", Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO,
				VALIDATION_NUM5);
		v.addValidation("urlDescription", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, VALIDATION_NUM5);

		errors.putAll(v.validate());

		StudyBean newStudy = (StudyBean) request.getSession().getAttribute("newStudy");
		newStudy.setMedlineIdentifier(fp.getString("medlineIdentifier"));
		newStudy.setResultsReference(fp.getBoolean("resultsReference"));
		newStudy.setUrl(fp.getString("url"));
		newStudy.setUrlDescription(fp.getString("urlDescription"));

		request.getSession().setAttribute("newStudy", newStudy);

		if (errors.isEmpty()) {
			if (request.getSession().getAttribute("interventionArray") == null) {
				request.setAttribute("interventions", new ArrayList());
			} else {
				request.setAttribute("interventions", request.getSession().getAttribute("interventionArray"));
			}

			forwardPage(Page.CREATE_STUDY8, request, response);
		} else {
			request.setAttribute("formMessages", errors);
			forwardPage(Page.CREATE_STUDY7, request, response);
		}

	}

	/**
	 * Lets user confirm all the study info entries input.
	 * 
	 * @throws Exception
	 */
	private void confirmWholeStudy(HttpServletRequest request, HttpServletResponse response, HashMap errors)
			throws Exception {

		FormProcessor fp = new FormProcessor(request);
		Validator v = new Validator(new ValidatorHelper(request, getConfigurationDao()));

		errors.putAll(v.validate());

		StudyBean newStudy = (StudyBean) request.getSession().getAttribute("newStudy");
		newStudy.getStudyParameterConfig().setCollectDob(fp.getString("collectDob"));
		newStudy.getStudyParameterConfig().setDiscrepancyManagement(fp.getString("discrepancyManagement"));
		newStudy.getStudyParameterConfig().setGenderRequired(fp.getString("genderRequired"));

		newStudy.getStudyParameterConfig().setInterviewerNameRequired(fp.getString("interviewerNameRequired"));
		newStudy.getStudyParameterConfig().setInterviewerNameDefault(fp.getString("interviewerNameDefault"));
		newStudy.getStudyParameterConfig().setInterviewDateEditable(fp.getString("interviewDateEditable"));
		newStudy.getStudyParameterConfig().setInterviewDateRequired(fp.getString("interviewDateRequired"));
		newStudy.getStudyParameterConfig().setInterviewerNameEditable(fp.getString("interviewerNameEditable"));
		newStudy.getStudyParameterConfig().setInterviewDateDefault(fp.getString("interviewDateDefault"));

		newStudy.getStudyParameterConfig().setSubjectIdGeneration(fp.getString("subjectIdGeneration"));
		newStudy.getStudyParameterConfig().setAutoGeneratedPrefix(fp.getString("autoGeneratedPrefix"));
		newStudy.getStudyParameterConfig().setAutoGeneratedSeparator(fp.getString("autoGeneratedSeparator"));
		newStudy.getStudyParameterConfig().setAutoGeneratedSuffix(fp.getString("autoGeneratedSuffix"));
		newStudy.getStudyParameterConfig().setSubjectPersonIdRequired(fp.getString("subjectPersonIdRequired"));
		newStudy.getStudyParameterConfig().setSubjectIdPrefixSuffix(fp.getString("subjectIdPrefixSuffix"));
		newStudy.getStudyParameterConfig().setPersonIdShownOnCRF(fp.getString("personIdShownOnCRF"));
		newStudy.getStudyParameterConfig().setSecondaryLabelViewable(fp.getString("secondaryLabelViewable"));
		newStudy.getStudyParameterConfig().setAdminForcedReasonForChange(fp.getString("adminForcedReasonForChange"));

		newStudy.getStudyParameterConfig().setSecondaryIdRequired(fp.getString("secondaryIdRequired"));
		newStudy.getStudyParameterConfig().setDateOfEnrollmentForStudyRequired(
				fp.getString("dateOfEnrollmentForStudyRequired"));
		newStudy.getStudyParameterConfig().setStudySubjectIdLabel(fp.getString("studySubjectIdLabel"));
		newStudy.getStudyParameterConfig().setSecondaryIdLabel(fp.getString("secondaryIdLabel"));
		newStudy.getStudyParameterConfig().setDateOfEnrollmentForStudyLabel(
				fp.getString("dateOfEnrollmentForStudyLabel"));
		newStudy.getStudyParameterConfig().setGenderLabel(fp.getString("genderLabel"));

		newStudy.getStudyParameterConfig().setStartDateTimeRequired(fp.getString("startDateTimeRequired"));
		newStudy.getStudyParameterConfig().setUseStartTime(fp.getString("useStartTime"));
		newStudy.getStudyParameterConfig().setEndDateTimeRequired(fp.getString("endDateTimeRequired"));
		newStudy.getStudyParameterConfig().setUseEndTime(fp.getString("useEndTime"));
		newStudy.getStudyParameterConfig().setStartDateTimeLabel(fp.getString("startDateTimeLabel"));
		newStudy.getStudyParameterConfig().setEndDateTimeLabel(fp.getString("endDateTimeLabel"));

		newStudy.getStudyParameterConfig().setMarkImportedCRFAsCompleted(fp.getString("markImportedCRFAsCompleted"));
		newStudy.getStudyParameterConfig().setAutoScheduleEventDuringImport(
				fp.getString("autoScheduleEventDuringImport"));
		newStudy.getStudyParameterConfig().setAutoCreateSubjectDuringImport(
				fp.getString("autoCreateSubjectDuringImport"));
		newStudy.getStudyParameterConfig().setAllowSdvWithOpenQueries(fp.getString("allowSdvWithOpenQueries"));
		newStudy.getStudyParameterConfig().setReplaceExisitingDataDuringImport(
				fp.getString("replaceExisitingDataDuringImport"));

		// Medical coding
		newStudy.getStudyParameterConfig().setMedicalCodingApiKey(fp.getString("medicalCodingApiKey"));
		newStudy.getStudyParameterConfig().setDefaultBioontologyURL(fp.getString("defaultBioontologyURL"));
		newStudy.getStudyParameterConfig().setAllowCodingVerification(fp.getString("allowCodingVerification"));

		// Probably create custom dictionary from here
		newStudy.getStudyParameterConfig().setAutoCodeDictionaryName(fp.getString("autoCodeDictionaryName"));
		newStudy.getStudyParameterConfig().setMedicalCodingContextNeeded(fp.getString("medicalCodingContextNeeded"));
		newStudy.getStudyParameterConfig().setMedicalCodingApprovalNeeded(fp.getString("medicalCodingApprovalNeeded"));

		newStudy.getStudyParameterConfig().setAllowCrfEvaluation(fp.getString("allowCrfEvaluation"));
		newStudy.getStudyParameterConfig().setEvaluateWithContext(fp.getString("evaluateWithContext"));

		newStudy.getStudyParameterConfig().setAllowRulesAutoScheduling(fp.getString("allowRulesAutoScheduling"));
		newStudy.getStudyParameterConfig().setRandomizationEnviroment(fp.getString("randomizationEnviroment"));

		newStudy.getStudyParameterConfig().setAutoTabbing(fp.getString("autoTabbing"));

		request.getSession().setAttribute("newStudy", newStudy);

		if (errors.isEmpty()) {
			if (request.getSession().getAttribute("interventionArray") == null) {
				request.setAttribute("interventions", new ArrayList());
			} else {
				request.setAttribute("interventions", request.getSession().getAttribute("interventionArray"));
			}

			forwardPage(Page.STUDY_CREATE_CONFIRM, request, response);

		} else {
			request.setAttribute("formMessages", errors);
			forwardPage(Page.CREATE_STUDY8, request, response);
		}
	}

	private void submitStudy(HttpServletRequest request) {
		UserAccountBean ub = getUserAccountBean(request);

		StudyDAO sdao = new StudyDAO(getDataSource());
		StudyParameterValueDAO spvdao = new StudyParameterValueDAO(getDataSource());
		StudyBean newStudy = (StudyBean) request.getSession().getAttribute("newStudy");

		logger.info("study bean to be created:" + newStudy.getName() + newStudy.getProtocolDateVerification());
		newStudy.setOwner(ub);
		newStudy.setCreatedDate(new Date());
		StudyBean finalStudy = (StudyBean) sdao.create(newStudy);

		logger.info("new study created");
		StudyParameterValueBean spv = new StudyParameterValueBean();
		spv.setStudyId(finalStudy.getId());
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

		spv.setParameter("allowCodingVerification");
		spv.setValue(newStudy.getStudyParameterConfig().getAllowCodingVerification());
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

		spv.setParameter("allowCrfEvaluation");
		spv.setValue(newStudy.getStudyParameterConfig().getAllowCrfEvaluation());
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

		return newStudy;

	}

	/**
	 * Updates the study bean with inputs from the second section.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return true if study type is Interventional, otherwise false
	 */
	private boolean updateStudy2(HttpServletRequest request) {
		FormProcessor fp = new FormProcessor(request);
		StudyBean newStudy = (StudyBean) request.getSession().getAttribute("newStudy");
		newStudy.setProtocolType(fp.getString("protocolType"));
		newStudy.setStatus(Status.get(fp.getInt("statusId")));

		if (StringUtil.isBlank(fp.getString(INPUT_VER_DATE))) {
			newStudy.setProtocolDateVerification(null);
		} else {
			newStudy.setProtocolDateVerification(fp.getDate(INPUT_VER_DATE));
		}

		newStudy.setDatePlannedStart(fp.getDate(INPUT_START_DATE));

		if (StringUtil.isBlank(fp.getString(INPUT_END_DATE))) {
			newStudy.setDatePlannedEnd(null);
		} else {
			newStudy.setDatePlannedEnd(fp.getDate(INPUT_END_DATE));
		}

		newStudy.setPhase(fp.getString("phase"));
		if (fp.getInt("genetic") == 1) {
			newStudy.setGenetic(true);
		} else {
			newStudy.setGenetic(false);
		}

		request.getSession().setAttribute("newStudy", newStudy);

		String interventional = resadmin.getString("interventional");
		return interventional.equalsIgnoreCase(newStudy.getProtocolType());

	}

	/**
	 * Updates the study bean with inputs from the third section.
	 * 
	 * @param isInterventional
	 *            if the study type is internventional.
	 */
	private void updateStudy3(HttpServletRequest request, boolean isInterventional) {
		FormProcessor fp = new FormProcessor(request);
		StudyBean newStudy = (StudyBean) request.getSession().getAttribute("newStudy");
		newStudy.setPurpose(fp.getString("purpose"));
		if (isInterventional) {
			newStudy.setAllocation(fp.getString("allocation"));
			newStudy.setMasking(fp.getString("masking"));
			newStudy.setControl(fp.getString("control"));
			newStudy.setAssignment(fp.getString("assignment"));
			newStudy.setEndpoint(fp.getString("endpoint"));

			// Handle Interventions-type and name
			// repeat 10 times for each pair on the web page
			StringBuilder interventions = new StringBuilder();

			ArrayList interventionArray = new ArrayList();
			final int counter = 10;
			for (int i = 0; i < counter; i++) {
				String type = fp.getString("interType" + i);
				String name = fp.getString("interName" + i);
				if (!StringUtil.isBlank(type) && !StringUtil.isBlank(name)) {
					InterventionBean ib = new InterventionBean(fp.getString("interType" + i), fp.getString("interName"
							+ i));
					interventionArray.add(ib);
					interventions.append(ib.toString()).append(",");
				}
			}
			newStudy.setInterventions(interventions.toString());
			request.getSession().setAttribute("interventionArray", interventionArray);

		} else { // type is observational
			newStudy.setDuration(fp.getString("duration"));
			newStudy.setSelection(fp.getString("selection"));
			newStudy.setTiming(fp.getString("timing"));
		}
		request.getSession().setAttribute("newStudy", newStudy);

	}

	/**
	 * Sets map in request for different JSP pages.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param isInterventional
	 *            boolean
	 */
	private void setMaps(HttpServletRequest request, boolean isInterventional) {
		if (isInterventional) {
			request.setAttribute("interPurposeMap", interPurposeMap);
			request.setAttribute("allocationMap", allocationMap);
			request.setAttribute("maskingMap", maskingMap);
			request.setAttribute("controlMap", controlMap);
			request.setAttribute("assignmentMap", assignmentMap);
			request.setAttribute("endpointMap", endpointMap);
			request.setAttribute("interTypeMap", interTypeMap);
			if (request.getSession().getAttribute("interventionArray") == null) {
				request.getSession().setAttribute("interventions", new ArrayList());
			} else {
				request.getSession().setAttribute("interventions",
						request.getSession().getAttribute("interventionArray"));
			}
		} else {
			request.setAttribute("obserPurposeMap", obserPurposeMap);
			request.setAttribute("selectionMap", selectionMap);
			request.setAttribute("timingMap", timingMap);
		}

	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		return Controller.ADMIN_SERVLET_CODE;
	}

}
