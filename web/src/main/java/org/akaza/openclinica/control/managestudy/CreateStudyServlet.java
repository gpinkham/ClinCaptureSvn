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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.InterventionBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.SQLInitServlet;

/**
 * Processes request to create a new study
 * 
 * @author jxu
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
public class CreateStudyServlet extends SecureController {
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

	static HashMap<String, String> facRecruitStatusMap = new LinkedHashMap<String, String>();

	static HashMap<String, String> studyPhaseMap = new LinkedHashMap<String, String>();

	static HashMap<String, String> interPurposeMap = new LinkedHashMap<String, String>();

	static HashMap<String, String> allocationMap = new LinkedHashMap<String, String>();

	static HashMap<String, String> maskingMap = new LinkedHashMap<String, String>();

	static HashMap<String, String> controlMap = new LinkedHashMap<String, String>();

	static HashMap<String, String> assignmentMap = new LinkedHashMap<String, String>();

	static HashMap<String, String> endpointMap = new LinkedHashMap<String, String>();

	static HashMap<String, String> interTypeMap = new LinkedHashMap<String, String>();

	static HashMap<String, String> obserPurposeMap = new LinkedHashMap<String, String>();

	static HashMap<String, String> selectionMap = new LinkedHashMap<String, String>();

	static HashMap<String, String> timingMap = new LinkedHashMap<String, String>();

	static {
		// problem here -- if you go directly to the servlet, the resource
		// bundles are not initialized
		// and the servlet throws a fatal error.
		// try {
		facRecruitStatusMap.put("not_yet_recruiting", resadmin.getString("not_yet_recruiting"));
		facRecruitStatusMap.put("recruiting", resadmin.getString("recruiting"));
		facRecruitStatusMap.put("no_longer_recruiting", resadmin.getString("no_longer_recruiting"));
		facRecruitStatusMap.put("completed", resadmin.getString("completed"));
		facRecruitStatusMap.put("suspended", resadmin.getString("suspended"));
		facRecruitStatusMap.put("terminated", resadmin.getString("terminated"));

		studyPhaseMap.put("n_a", resadmin.getString("n_a"));
		studyPhaseMap.put("phaseI", resadmin.getString("phaseI"));
		studyPhaseMap.put("phaseI_II", resadmin.getString("phaseI_II"));
		studyPhaseMap.put("phaseII", resadmin.getString("phaseII"));
		studyPhaseMap.put("phaseII_III", resadmin.getString("phaseII_III"));
		studyPhaseMap.put("phaseIII", resadmin.getString("phaseIII"));
		studyPhaseMap.put("phaseIII_IV", resadmin.getString("phaseIII_IV"));
		studyPhaseMap.put("phaseIV", resadmin.getString("phaseIV"));

		interPurposeMap.put("treatment", resadmin.getString("treatment"));
		interPurposeMap.put("prevention", resadmin.getString("prevention"));
		interPurposeMap.put("diagnosis", resadmin.getString("diagnosis"));
		// interPurposeMap.put("educ_couns_train",
		// resadmin.getString("educ_couns_train"));
		interPurposeMap.put("supportive_care", resadmin.getString("supportive_care"));
		interPurposeMap.put("screening", resadmin.getString("screening"));
		interPurposeMap.put("health_services_research", resadmin.getString("health_services_research"));
		interPurposeMap.put("basic_science", resadmin.getString("basic_science"));
		interPurposeMap.put("other", resadmin.getString("other"));

		allocationMap.put("randomized", resadmin.getString("randomized"));
		allocationMap.put("non_randomized", resadmin.getString("non_randomized"));
		allocationMap.put("n_a", resadmin.getString("n_a"));

		maskingMap.put("open", resadmin.getString("open"));
		maskingMap.put("single_blind", resadmin.getString("single_blind"));
		maskingMap.put("double_blind", resadmin.getString("double_blind"));

		controlMap.put("placebo", resadmin.getString("placebo"));
		controlMap.put("active", resadmin.getString("active"));
		controlMap.put("uncontrolled", resadmin.getString("uncontrolled"));
		controlMap.put("historical", resadmin.getString("historical"));
		controlMap.put("dose_comparison", resadmin.getString("dose_comparison"));

		assignmentMap.put("single_group", resadmin.getString("single_group"));
		assignmentMap.put("parallel", resadmin.getString("parallel"));
		assignmentMap.put("cross_over", resadmin.getString("cross_over"));
		assignmentMap.put("factorial", resadmin.getString("factorial"));
		assignmentMap.put("expanded_access", resadmin.getString("expanded_access"));

		endpointMap.put("safety", resadmin.getString("safety"));
		endpointMap.put("efficacy", resadmin.getString("efficacy"));
		endpointMap.put("safety_efficacy", resadmin.getString("safety_efficacy"));
		endpointMap.put("bio_equivalence", resadmin.getString("bio_equivalence"));
		endpointMap.put("bio_availability", resadmin.getString("bio_availability"));
		endpointMap.put("pharmacokinetics", resadmin.getString("pharmacokinetics"));
		endpointMap.put("pharmacodynamics", resadmin.getString("pharmacodynamics"));
		endpointMap.put("pharmacokinetics_pharmacodynamics", resadmin.getString("pharmacokinetics_pharmacodynamics"));

		interTypeMap.put("drug", resadmin.getString("drug"));
		interTypeMap.put("gene_transfer", resadmin.getString("gene_transfer"));
		interTypeMap.put("vaccine", resadmin.getString("vaccine"));
		interTypeMap.put("behavior", resadmin.getString("behavior"));
		interTypeMap.put("device", resadmin.getString("device"));
		interTypeMap.put("procedure", resadmin.getString("procedure"));
		interTypeMap.put("other", resadmin.getString("other"));

		obserPurposeMap.put("natural_history", resadmin.getString("natural_history"));
		obserPurposeMap.put("screening", resadmin.getString("screening"));
		obserPurposeMap.put("psychosocial", resadmin.getString("psychosocial"));

		selectionMap.put("convenience_sample", resadmin.getString("convenience_sample"));
		selectionMap.put("defined_population", resadmin.getString("defined_population"));
		selectionMap.put("random_sample", resadmin.getString("random_sample"));
		selectionMap.put("case_control", resadmin.getString("case_control"));

		timingMap.put("retrospective", resadmin.getString("retrospective"));
		timingMap.put("prospective", resadmin.getString("prospective"));
	}

	/**
     *
     */
	@Override
	public void mayProceed() throws InsufficientPermissionException {
		if (ub.isSysAdmin()) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.STUDY_LIST_SERVLET, resexception.getString("not_admin"), "1");

	}

	/**
	 * Processes user request
	 */
	@Override
	public void processRequest() throws Exception {
		String action = request.getParameter("action");
		resetPanel();
		panel.setStudyInfoShown(false);
		panel.setOrderedData(true);
		panel.setExtractData(false);
		panel.setSubmitDataModule(false);
		panel.setCreateDataset(false);
		panel.setIconInfoShown(true);
		panel.setManageSubject(false);

		if (StringUtil.isBlank(action)) {
			session.setAttribute("newStudy", new StudyBean());

			UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
			Collection users = udao.findAllByRole("coordinator", "director");
			request.setAttribute("users", users);

			forwardPage(Page.CREATE_STUDY1);
		} else {
			if ("confirm".equalsIgnoreCase(action)) {
				confirmWholeStudy();

			} else if ("cancel".equalsIgnoreCase(action)) {
				addPageMessage(respage.getString("study_creation_cancelled"));
				forwardPage(Page.STUDY_LIST_SERVLET);
				return;
			} else if ("submit".equalsIgnoreCase(action)) {

				submitStudy();

				session.removeAttribute("interventions");
				session.removeAttribute("isInterventionalFlag");
				session.removeAttribute("interventionArray");

				// swith user to the newly created study

				session.setAttribute("study", session.getAttribute("newStudy"));
				session.removeAttribute("newStudy");
				currentStudy = (StudyBean) session.getAttribute("study");

				UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());

				StudyUserRoleBean sub = new StudyUserRoleBean();
				sub.setRole(Role.COORDINATOR);
				sub.setStudyId(currentStudy.getId());
				sub.setStatus(Status.AVAILABLE);
				sub.setOwner(ub);
				udao.createStudyUserRole(ub, sub);
				currentRole = sub;
				session.setAttribute("userRole", sub);

				addPageMessage(respage.getString("the_new_study_created_succesfully_current"));

				ArrayList pageMessages = (ArrayList) request.getAttribute(PAGE_MESSAGE);
				session.setAttribute("pageMessages", pageMessages);
				response.sendRedirect(request.getContextPath() + Page.MANAGE_STUDY_MODULE);
			} else if ("next".equalsIgnoreCase(action)) {
				Integer pageNumber = Integer.valueOf(request.getParameter("pageNum"));
				if (pageNumber != null) {
					if (pageNumber.intValue() == 6) {
						confirmStudy6();
					} else if (pageNumber.intValue() == 5) {
						confirmStudy5();
					} else if (pageNumber.intValue() == 4) {
						confirmStudy4();
					} else if (pageNumber.intValue() == 3) {
						confirmStudy3();
					} else if (pageNumber.intValue() == 2) {
						confirmStudy2();
					} else {
						System.out.println("confirm study 1 " + pageNumber.intValue());
						confirmStudy1();
					}
				} else {
					if (session.getAttribute("newStudy") == null) {
						session.setAttribute("newStudy", new StudyBean());
					}

					UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
					Collection users = udao.findAllByRole("coordinator", "director");
					request.setAttribute("users", users);

					forwardPage(Page.CREATE_STUDY1);
				}
			}
		}
	}

	/**
	 * Validates the first section of study and save it into study bean
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	private void confirmStudy1() throws Exception {
		Validator v = new Validator(request);
		FormProcessor fp = new FormProcessor(request);

		v.addValidation("name", Validator.NO_BLANKS);
		v.addValidation("uniqueProId", Validator.NO_BLANKS);
		v.addValidation("description", Validator.NO_BLANKS);
		v.addValidation("prinInvestigator", Validator.NO_BLANKS);
		v.addValidation("sponsor", Validator.NO_BLANKS);

		v.addValidation("secondProId", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
		v.addValidation("collaborators", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 1000);
		v.addValidation("protocolDescription", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 1000);

		// check param presents before validation
		addValidatorIfParamPresented("studySubjectIdLabel", v, Validator.NO_BLANKS);
		addValidatorIfParamPresented("secondaryIdLabel", v, Validator.NO_BLANKS);
		addValidatorIfParamPresented("dateOfEnrollmentForStudyLabel", v, Validator.NO_BLANKS);
		addValidatorIfParamPresented("genderLabel", v, Validator.NO_BLANKS);
		addValidatorIfParamPresented("startDateTimeLabel", v, Validator.NO_BLANKS);
		addValidatorIfParamPresented("endDateTimeLabel", v, Validator.NO_BLANKS);

		errors = v.validate();
		// check to see if name and uniqueProId are unique, tbh
		StudyDAO studyDAO = new StudyDAO(sm.getDataSource());
		ArrayList<StudyBean> allStudies = (ArrayList<StudyBean>) studyDAO.findAll();
		for (StudyBean thisBean : allStudies) {
			if (fp.getString("name").trim().equals(thisBean.getName())) {
				MessageFormat mf = new MessageFormat("");
				mf.applyPattern(respage.getString("brief_title_existed"));
				Object[] arguments = { fp.getString("name").trim() };

				Validator.addError(errors, "name", mf.format(arguments));
			}
			if (fp.getString("uniqueProId").trim().equals(thisBean.getIdentifier())) {
				Validator.addError(errors, "uniqueProId", resexception.getString("unique_protocol_id_existed"));
			}
		}
		if (fp.getString("name").trim().length() > 100) {
			Validator.addError(errors, "name", resexception.getString("maximum_lenght_name_100"));
		}
		if (fp.getString("uniqueProId").trim().length() > 30) {
			Validator.addError(errors, "uniqueProId", resexception.getString("maximum_lenght_unique_protocol_30"));
		}
		if (fp.getString("description").trim().length() > 255) {
			Validator.addError(errors, "description", resexception.getString("maximum_lenght_brief_summary_255"));
		}
		if (fp.getString("prinInvestigator").trim().length() > 255) {
			Validator.addError(errors, "prinInvestigator",
					resexception.getString("maximum_lenght_principal_investigator_255"));
		}
		if (fp.getString("sponsor").trim().length() > 255) {
			Validator.addError(errors, "sponsor", resexception.getString("maximum_lenght_sponsor_255"));
		}
		if (fp.getString("officialTitle").trim().length() > 255) {
			Validator.addError(errors, "officialTitle", resexception.getString("maximum_lenght_official_title_255"));
		}
		if (fp.getString("studySubjectIdLabel").trim().length() > 255) {
			Validator.addError(errors, "studySubjectIdLabel",
					resexception.getString("maximum_lenght_studySubjectIdLabel_255"));
		}
		if (fp.getString("secondaryIdLabel").trim().length() > 255) {
			Validator.addError(errors, "secondaryIdLabel",
					resexception.getString("maximum_lenght_secondaryIdLabel_255"));
		}
		if (fp.getString("dateOfEnrollmentForStudyLabel").trim().length() > 255) {
			Validator.addError(errors, "dateOfEnrollmentForStudyLabel",
					resexception.getString("maximum_lenght_dateOfEnrollmentForStudyLabel_255"));
		}
		if (fp.getString("genderLabel").trim().length() > 255) {
			Validator.addError(errors, "genderLabel", resexception.getString("maximum_lenght_genderLabel_255"));
		}
		if (fp.getString("startDateTimeLabel").trim().length() > 255) {
			Validator.addError(errors, "startDateTimeLabel",
					resexception.getString("maximum_lenght_startDateTimeLabel_255"));
		}
		if (fp.getString("endDateTimeLabel").trim().length() > 255) {
			Validator.addError(errors, "endDateTimeLabel",
					resexception.getString("maximum_lenght_endDateTimeLabel_255"));
		}

		StudyBean studyBean = createStudyBean();

		if (errors.isEmpty()) {
			logger.info("no errors in the first section");
			request.setAttribute("studyPhaseMap", studyPhaseMap);
			request.setAttribute("statuses", Status.toActiveArrayList());
			logger.info("setting arrays to request, size of list: " + Status.toArrayList().size());
			if (request.getParameter("Save") != null && request.getParameter("Save").length() > 0) {
				StudyDAO sdao = new StudyDAO(sm.getDataSource());
				studyBean.setOwner(ub);
				studyBean.setCreatedDate(new Date());
				studyBean.setStatus(Status.PENDING);
				studyBean = (StudyBean) sdao.create(studyBean);
				StudyBean newstudyBean = (StudyBean) sdao.findByName(studyBean.getName());

				UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
				String selectedUserIdStr = fp.getString("selectedUser");
				int selectedUserId = 0;
				if (selectedUserIdStr != null && selectedUserIdStr.length() > 0) {
					selectedUserId = Integer.parseInt(fp.getString("selectedUser"));
				}
				if (selectedUserId > 0) {
					UserAccountBean user = (UserAccountBean) udao.findByPK(selectedUserId);
					StudyUserRoleBean sub = new StudyUserRoleBean();
					sub.setRole(Role.COORDINATOR);
					sub.setStudyId(newstudyBean.getId());
					sub.setStatus(Status.AVAILABLE);
					sub.setOwner(ub);
					udao.createStudyUserRole(user, sub);
					if (ub.getId() != selectedUserId) {
						sub = new StudyUserRoleBean();
						sub.setRole(Role.COORDINATOR);
						sub.setStudyId(newstudyBean.getId());
						sub.setStatus(Status.AVAILABLE);
						sub.setOwner(ub);
						udao.createStudyUserRole(ub, sub);
					}
				} else {
					StudyUserRoleBean sub = new StudyUserRoleBean();
					sub.setRole(Role.COORDINATOR);
					sub.setStudyId(newstudyBean.getId());
					sub.setStatus(Status.AVAILABLE);
					sub.setOwner(ub);
					udao.createStudyUserRole(ub, sub);
				}
				addPageMessage(respage.getString("the_new_study_created_succesfully_current"));
				forwardPage(Page.STUDY_LIST_SERVLET);
			} else {
				session.setAttribute("newStudy", studyBean);
				forwardPage(Page.CREATE_STUDY2);
			}

		} else {
			session.setAttribute("newStudy", studyBean);
			logger.info("has validation errors in the first section");
			request.setAttribute("formMessages", errors);
			UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
			Collection users = udao.findAllByRole("coordinator", "director");
			request.setAttribute("users", users);

			forwardPage(Page.CREATE_STUDY1);
		}

	}

	private void addValidatorIfParamPresented(String paramName, Validator v, int validatorType) {
		if (request.getParameter(paramName) != null) {
			v.addValidation(paramName, validatorType);
		}
	}

	/**
	 * Validates the second section of study info inputs
	 * 
	 * @throws Exception
	 */
	private void confirmStudy2() throws Exception {
		Validator v = new Validator(request);
		FormProcessor fp = new FormProcessor(request);

		v.addValidation(INPUT_START_DATE, Validator.IS_A_DATE);
		if (!StringUtil.isBlank(fp.getString(INPUT_END_DATE))) {
			v.addValidation(INPUT_END_DATE, Validator.IS_A_DATE);
		}

		v.addValidation("protocolType", Validator.NO_BLANKS);
		if (!StringUtil.isBlank(fp.getString(INPUT_VER_DATE))) {
			v.addValidation(INPUT_VER_DATE, Validator.IS_A_DATE_WITHOUT_REQUIRED_CHECK);
		}

		errors = v.validate();
		boolean isInterventional = updateStudy2();
		session.setAttribute("isInterventionalFlag", new Boolean(isInterventional));

		if (errors.isEmpty()) {
			logger.info("no errors");
			setMaps(isInterventional);
			if (isInterventional) {
				forwardPage(Page.CREATE_STUDY3);
			} else {
				forwardPage(Page.CREATE_STUDY4);
			}

		} else {
			logger.info("has validation errors");
			try {
				local_df.parse(fp.getString(INPUT_START_DATE));
				fp.addPresetValue(INPUT_START_DATE, local_df.format(fp.getDate(INPUT_START_DATE)));
			} catch (ParseException pe) {
				fp.addPresetValue(INPUT_START_DATE, fp.getString(INPUT_START_DATE));
			}
			try {
				local_df.parse(fp.getString(INPUT_VER_DATE));
				fp.addPresetValue(INPUT_VER_DATE, local_df.format(fp.getDate(INPUT_VER_DATE)));
			} catch (ParseException pe) {
				fp.addPresetValue(INPUT_VER_DATE, fp.getString(INPUT_VER_DATE));
			}
			try {
				local_df.parse(fp.getString(INPUT_END_DATE));
				fp.addPresetValue(INPUT_END_DATE, local_df.format(fp.getDate(INPUT_END_DATE)));
			} catch (ParseException pe) {
				fp.addPresetValue(INPUT_END_DATE, fp.getString(INPUT_END_DATE));
			}
			setPresetValues(fp.getPresetValues());
			request.setAttribute("formMessages", errors);
			request.setAttribute("studyPhaseMap", studyPhaseMap);
			request.setAttribute("statuses", Status.toActiveArrayList());
			forwardPage(Page.CREATE_STUDY2);
		}

	}

	/**
	 * Confirms the third section of study info inputs
	 * 
	 * @throws Exception
	 */
	private void confirmStudy3() throws Exception {
		Validator v = new Validator(request);
		FormProcessor fp = new FormProcessor(request);

		v.addValidation("purpose", Validator.NO_BLANKS);
		for (int i = 0; i < 10; i++) {
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

		errors = v.validate();

		boolean isInterventional = true;
		if (session.getAttribute("isInterventionalFlag") != null) {
			isInterventional = ((Boolean) session.getAttribute("isInterventionalFlag")).booleanValue();
		}
		updateStudy3(isInterventional);

		if (errors.isEmpty()) {
			logger.info("no errors");
			if (session.getAttribute("interventionArray") == null) {
				request.setAttribute("interventions", new ArrayList());
			} else {
				request.setAttribute("interventions", session.getAttribute("interventionArray"));
			}
			forwardPage(Page.CREATE_STUDY5);

		} else {
			logger.info("has validation errors");

			request.setAttribute("formMessages", errors);
			setMaps(isInterventional);
			if (isInterventional) {
				forwardPage(Page.CREATE_STUDY3);
			} else {
				forwardPage(Page.CREATE_STUDY4);
			}
		}

	}

	/**
	 * Validates the forth section of study and save it into study bean
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	private void confirmStudy4() throws Exception {
		Validator v = new Validator(request);
		FormProcessor fp = new FormProcessor(request);

		v.addValidation("conditions", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 500);
		v.addValidation("keywords", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
		v.addValidation("eligibility", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 500);

		logger.info("expectedTotalEnrollment:" + fp.getInt("expectedTotalEnrollment"));
		errors = v.validate();
		if (fp.getInt("expectedTotalEnrollment") <= 0) {
			Validator.addError(errors, "expectedTotalEnrollment",
					respage.getString("expected_total_enrollment_must_be_a_positive_number"));
		}

		StudyBean newStudy = (StudyBean) session.getAttribute("newStudy");
		newStudy.setConditions(fp.getString("conditions"));
		newStudy.setKeywords(fp.getString("keywords"));
		newStudy.setEligibility(fp.getString("eligibility"));
		newStudy.setGender(fp.getString("gender"));

		newStudy.setAgeMax(fp.getString("ageMax"));
		newStudy.setAgeMin(fp.getString("ageMin"));
		newStudy.setHealthyVolunteerAccepted(fp.getBoolean("healthyVolunteerAccepted"));
		newStudy.setExpectedTotalEnrollment(fp.getInt("expectedTotalEnrollment"));
		session.setAttribute("newStudy", newStudy);
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

			session.setAttribute("newStudy", newStudy);
			forwardPage(Page.CREATE_STUDY6);

		} else {
			request.setAttribute("formMessages", errors);
			forwardPage(Page.CREATE_STUDY5);
		}
	}

	/**
	 * Validates the forth section of study and save it into study bean
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	private void confirmStudy5() throws Exception {
		FormProcessor fp = new FormProcessor(request);
		Validator v = new Validator(request);
		if (!StringUtil.isBlank(fp.getString("facConEmail"))) {
			v.addValidation("facConEmail", Validator.IS_A_EMAIL);
		}
		v.addValidation("facName", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
		v.addValidation("facCity", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
		v.addValidation("facState", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 20);
		v.addValidation("facZip", Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO,
				64);
		v.addValidation("facCountry", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 64);
		v.addValidation("facConName", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
		v.addValidation("facConDegree", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
		v.addValidation("facConPhone", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
		v.addValidation("facConEmail", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
		errors = v.validate();

		StudyBean newStudy = (StudyBean) session.getAttribute("newStudy");

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

		session.setAttribute("newStudy", newStudy);
		if (errors.isEmpty()) {
			forwardPage(Page.CREATE_STUDY7);
		} else {
			request.setAttribute("formMessages", errors);
			request.setAttribute("facRecruitStatusMap", facRecruitStatusMap);
			forwardPage(Page.CREATE_STUDY6);
		}

	}

	/**
	 * Lets user confirm all the study info entries input
	 * 
	 * @throws Exception
	 */
	private void confirmStudy6() throws Exception {

		FormProcessor fp = new FormProcessor(request);
		Validator v = new Validator(request);
		v.addValidation("medlineIdentifier", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
		v.addValidation("url", Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO,
				255);
		v.addValidation("urlDescription", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);

		errors = v.validate();

		StudyBean newStudy = (StudyBean) session.getAttribute("newStudy");
		newStudy.setMedlineIdentifier(fp.getString("medlineIdentifier"));
		newStudy.setResultsReference(fp.getBoolean("resultsReference"));
		newStudy.setUrl(fp.getString("url"));
		newStudy.setUrlDescription(fp.getString("urlDescription"));

		session.setAttribute("newStudy", newStudy);

		if (errors.isEmpty()) {
			if (session.getAttribute("interventionArray") == null) {
				request.setAttribute("interventions", new ArrayList());
			} else {
				request.setAttribute("interventions", session.getAttribute("interventionArray"));
			}

			forwardPage(Page.CREATE_STUDY8);
		} else {
			request.setAttribute("formMessages", errors);
			forwardPage(Page.CREATE_STUDY7);
		}

	}

	/**
	 * Lets user confirm all the study info entries input
	 * 
	 * @throws Exception
	 */
	private void confirmWholeStudy() throws Exception {

		FormProcessor fp = new FormProcessor(request);
		Validator v = new Validator(request);
		errors = v.validate();

		StudyBean newStudy = (StudyBean) session.getAttribute("newStudy");
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

		session.setAttribute("newStudy", newStudy);

		if (errors.isEmpty()) {
			if (session.getAttribute("interventionArray") == null) {
				request.setAttribute("interventions", new ArrayList());
			} else {
				request.setAttribute("interventions", session.getAttribute("interventionArray"));
			}

			forwardPage(Page.STUDY_CREATE_CONFIRM);

		} else {
			request.setAttribute("formMessages", errors);
			forwardPage(Page.CREATE_STUDY8);
		}

	}

	/**
	 * Inserts the new study into database
	 * 
	 */
	private void submitStudy() {
		StudyDAO sdao = new StudyDAO(sm.getDataSource());
		StudyParameterValueDAO spvdao = new StudyParameterValueDAO(sm.getDataSource());
		StudyBean newStudy = (StudyBean) session.getAttribute("newStudy");

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

		spv.setParameter("markImportedCRFAsCompleted");
		spv.setValue(newStudy.getStudyParameterConfig().getMarkImportedCRFAsCompleted());
		spvdao.create(spv);

		logger.info("study parameters created done");

	}

	/**
	 * Constructs study bean from the first section
	 * 
	 * @param request
	 * @return
	 */
	private StudyBean createStudyBean() {
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
	 * Updates the study bean with inputs from the second section
	 * 
	 * @param request
	 * @return true if study type is Interventional, otherwise false
	 */
	private boolean updateStudy2() {
		FormProcessor fp = new FormProcessor(request);
		StudyBean newStudy = (StudyBean) session.getAttribute("newStudy");
		newStudy.setProtocolType(fp.getString("protocolType"));// protocolType

		// this is not fully supported yet, because the system will not handle
		// studies which are pending
		// or private...
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

		session.setAttribute("newStudy", newStudy);

		String interventional = resadmin.getString("interventional");
		return interventional.equalsIgnoreCase(newStudy.getProtocolType());

	}

	/**
	 * Updates the study bean with inputs from the third section
	 * 
	 * @param isInterventional
	 *            if the study type is internventional
	 */
	private void updateStudy3(boolean isInterventional) {
		FormProcessor fp = new FormProcessor(request);
		StudyBean newStudy = (StudyBean) session.getAttribute("newStudy");
		newStudy.setPurpose(fp.getString("purpose"));
		if (isInterventional) {
			newStudy.setAllocation(fp.getString("allocation"));
			newStudy.setMasking(fp.getString("masking"));
			newStudy.setControl(fp.getString("control"));
			newStudy.setAssignment(fp.getString("assignment"));
			newStudy.setEndpoint(fp.getString("endpoint"));

			// Handle Interventions-type and name
			// repeat 10 times for each pair on the web page
			StringBuffer interventions = new StringBuffer();

			ArrayList interventionArray = new ArrayList();

			for (int i = 0; i < 10; i++) {
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
			session.setAttribute("interventionArray", interventionArray);

		} else {// type = observational
			newStudy.setDuration(fp.getString("duration"));
			newStudy.setSelection(fp.getString("selection"));
			newStudy.setTiming(fp.getString("timing"));
		}
		session.setAttribute("newStudy", newStudy);

	}

	/**
	 * Sets map in request for different JSP pages
	 * 
	 * @param request
	 * @param isInterventional
	 */
	private void setMaps(boolean isInterventional) {
		if (isInterventional) {
			request.setAttribute("interPurposeMap", interPurposeMap);
			request.setAttribute("allocationMap", allocationMap);
			request.setAttribute("maskingMap", maskingMap);
			request.setAttribute("controlMap", controlMap);
			request.setAttribute("assignmentMap", assignmentMap);
			request.setAttribute("endpointMap", endpointMap);
			request.setAttribute("interTypeMap", interTypeMap);
			if (session.getAttribute("interventionArray") == null) {
				session.setAttribute("interventions", new ArrayList());
			} else {
				session.setAttribute("interventions", session.getAttribute("interventionArray"));
			}
		} else {
			request.setAttribute("obserPurposeMap", obserPurposeMap);
			request.setAttribute("selectionMap", selectionMap);
			request.setAttribute("timingMap", timingMap);
		}

	}

	@Override
	protected String getAdminServlet() {
		return SecureController.ADMIN_SERVLET_CODE;
	}

}
