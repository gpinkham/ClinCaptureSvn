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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.InterventionBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.core.SpringServlet;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.service.StudyConfigService;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import com.clinovo.enums.StudyProtocolType;
import com.clinovo.model.DiscrepancyDescription;
import com.clinovo.model.DiscrepancyDescriptionType;
import com.clinovo.service.DiscrepancyDescriptionService;
import com.clinovo.util.DateUtil;
import com.clinovo.util.StudyUtil;
import com.clinovo.util.ValidatorHelper;
import com.clinovo.validator.StudyValidator;

/**
 * Processes request to update study.
 **/
@SuppressWarnings({"rawtypes", "unchecked"})
@Component
public class UpdateStudyServletNew extends SpringServlet {

	public static final String INPUT_END_DATE = "endDate";
	public static final String INPUT_START_DATE = "startDate";
	public static final String INPUT_VER_DATE = "protocolDateVerification";

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
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
		addPageMessage(getResPage().getString("no_have_correct_privilege_current_study")
				+ getResPage().getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, getResException().getString("may_not_submit_data"),
				"1");
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		StudyBean currentStudy = getCurrentStudy(request);

		HashMap errors = getErrorsHolder(request);
		StudyInfoPanel panel = getStudyInfoPanel(request);
		panel.reset();

		FormProcessor fp = new FormProcessor(request);
		Validator v = new Validator(new ValidatorHelper(request, getConfigurationDao()));
		int studyId = fp.getInt("id");
		studyId = studyId == 0 ? fp.getInt("studyId") : studyId;
		String action = fp.getString("action");
		StudyDAO sdao = new StudyDAO(getDataSource());
		DiscrepancyDescriptionService dDescriptionService = (DiscrepancyDescriptionService) SpringServletAccess
				.getApplicationContext(getServletContext()).getBean("discrepancyDescriptionService");

		Map<String, List<DiscrepancyDescription>> dDescriptionsMap = dDescriptionService
				.findAllSortedDescriptionsFromStudy(studyId);

		StudyBean study = (StudyBean) sdao.findByPK(studyId);
		if (study.getId() != currentStudy.getId()) {
			addPageMessage(getResPage().getString("not_current_study")
					+ getResPage().getString("change_study_contact_sysadmin"), request);
			forwardPage(Page.MENU_SERVLET, request, response);
			return;
		}

		study.setId(studyId);
		StudyConfigService scs = new StudyConfigService(getDataSource());
		study = scs.setParametersForStudy(study);

		request.setAttribute("dDescriptionsMap", dDescriptionsMap);

		request.setAttribute("studyToView", study);
		request.setAttribute("studyId", studyId + "");
		request.setAttribute("studyPhaseMap", getMapsHolder().getStudyPhaseMap());
		ArrayList statuses = Status.toStudyUpdateMembersList();
		statuses.add(Status.PENDING);
		request.setAttribute("statuses", statuses);

		String protocolType = study.getProtocolTypeKey();

		boolean isInterventional = StudyProtocolType.INTERVENTIONAL.getName().equals(protocolType);
		request.setAttribute("isInterventional", isInterventional ? "1" : "0");

		if (study.getParentStudyId() > 0) {
			StudyBean parentStudy = (StudyBean) sdao.findByPK(study.getParentStudyId());
			request.setAttribute("parentStudy", parentStudy);
		}

		ArrayList interventionArray = new ArrayList();
		if (isInterventional) {
			interventionArray = parseInterventions((study));
		}
		setMaps(request, isInterventional, interventionArray);

		if (!action.equals("submit")) {

			// First Load First Form
			if (study.getDatePlannedStart() != null) {
				fp.addPresetValue(INPUT_START_DATE, DateUtil.printDate(study.getDatePlannedStart(),
						getUserAccountBean().getUserTimeZoneId(), DateUtil.DatePattern.DATE, getLocale()));
			}
			if (study.getDatePlannedEnd() != null) {
				fp.addPresetValue(INPUT_END_DATE, DateUtil.printDate(study.getDatePlannedEnd(),
						getUserAccountBean().getUserTimeZoneId(), DateUtil.DatePattern.DATE, getLocale()));
			}
			if (study.getProtocolDateVerification() != null) {
				fp.addPresetValue(INPUT_VER_DATE, DateUtil.printDate(study.getProtocolDateVerification(),
						getUserAccountBean().getUserTimeZoneId(), DateUtil.DatePattern.DATE, getLocale()));
			}
			setPresetValues(fp.getPresetValues(), request);
			// first load 2nd form
		}
		if (action.equals("submit")) {

			validateStudy1(fp, study, errors, v);
			validateStudy2(fp, study, v);
			validateStudy3(fp, study, v, isInterventional);
			validateStudy4(fp, study, errors, v);
			validateStudy5(fp, study, errors, v);
			validateStudy6(fp, study, errors, v);
			validateStudy7(fp, errors, v, study.getId(), dDescriptionsMap);
			confirmWholeStudy(fp, study, errors, v);

			request.setAttribute("studyToView", study);
			if (!errors.isEmpty()) {
				logger.debug("found errors : " + errors.toString());
				request.setAttribute("formMessages", errors);
				request.setAttribute("dDescriptions", dDescriptionsMap);
				forwardPage(Page.UPDATE_STUDY_NEW, request, response);
			} else {
				getStudyService().updateStudy(study, dDescriptionsMap, getUserAccountBean());
				if (study.getId() == currentStudy.getId()) {
					request.getSession().setAttribute("study", study);
				}
				study.setStudyParameters(getStudyParameterValueDAO().findParamConfigByStudy(study));
				updateLastAccessedInstanceType(response, study);
				addPageMessage(getResPage().getString("the_study_has_been_updated_succesfully"), request);
				ArrayList pageMessages = (ArrayList) request.getAttribute(PAGE_MESSAGE);
				request.getSession().setAttribute("pageMessages", pageMessages);
				response.sendRedirect(request.getContextPath() + "/pages/studymodule");
			}
		} else {
			forwardPage(Page.UPDATE_STUDY_NEW, request, response);
		}
	}

	private void validateStudy1(FormProcessor fp, StudyBean study, HashMap errors, Validator validator) {
		study.setId(fp.getInt("studyId"));
		errors.putAll(StudyValidator.validate(validator, getStudyDAO(), study));
		getStudyService().prepareStudyBean(study, StudyUtil.getStudyParametersMap(), StudyUtil.getStudyFeaturesMap());
	}

	private void validateStudy2(FormProcessor fp, StudyBean study, Validator v) {

		v.addValidation(INPUT_START_DATE, Validator.IS_A_DATE);
		if (!StringUtil.isBlank(fp.getString(INPUT_END_DATE))) {
			v.addValidation(INPUT_END_DATE, Validator.IS_A_DATE);
		}
		if (!StringUtil.isBlank(fp.getString(INPUT_VER_DATE))) {
			v.addValidation(INPUT_VER_DATE, Validator.IS_A_DATE);
		}
		fp.addPresetValue(INPUT_START_DATE, fp.getString(INPUT_START_DATE));
		fp.addPresetValue(INPUT_VER_DATE, fp.getString(INPUT_VER_DATE));
		fp.addPresetValue(INPUT_END_DATE, fp.getString(INPUT_END_DATE));
		updateStudy2(fp, study);
		setPresetValues(fp.getPresetValues(), fp.getRequest());
	}

	private void validateStudy3(FormProcessor fp, StudyBean study, Validator v, boolean isInterventional) {

		v.addValidation("purpose", Validator.NO_BLANKS);
		final int counter = 10;
		for (int i = 0; i < counter; i++) {
			String type = fp.getString("interType" + i);
			String name = fp.getString("interName" + i);
			if (!StringUtil.isBlank(type) && StringUtil.isBlank(name)) {
				v.addValidation("interName", Validator.NO_BLANKS);
				fp.getRequest().setAttribute("interventionError",
						getResPage().getString("name_cannot_be_blank_if_type"));
				break;
			}
			if (!StringUtil.isBlank(name) && StringUtil.isBlank(type)) {
				v.addValidation("interType", Validator.NO_BLANKS);
				fp.getRequest().setAttribute("interventionError",
						getResPage().getString("name_cannot_be_blank_if_name"));
				break;
			}
		}
		updateStudy3(study, isInterventional, fp);

	}

	private void validateStudy4(FormProcessor fp, StudyBean study, HashMap errors, Validator v) {

		v.addValidation("conditions", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, StudyValidator.VALIDATION_NUM6);
		v.addValidation("keywords", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, StudyValidator.VALIDATION_NUM5);
		v.addValidation("eligibility", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, StudyValidator.VALIDATION_NUM6);
		errors.putAll(v.validate());
		if (fp.getInt("expectedTotalEnrollment") <= 0) {
			Validator.addError(errors, "expectedTotalEnrollment",
					getResPage().getString("expected_total_enrollment_must_be_a_positive_number"));
		}

		study.setConditions(fp.getString("conditions"));
		study.setKeywords(fp.getString("keywords"));
		study.setEligibility(fp.getString("eligibility"));
		study.setGender(fp.getString("gender"));
		final int ageMaxSize = 3;
		if (fp.getString("ageMax").length() > ageMaxSize) {
			Validator.addError(errors, "ageMax", getResPage().getString("condition_eligibility_3"));
		}
		study.setAgeMax(fp.getString("ageMax"));

		study.setAgeMin(fp.getString("ageMin"));
		study.setHealthyVolunteerAccepted(fp.getBoolean("healthyVolunteerAccepted"));
		study.setExpectedTotalEnrollment(fp.getInt("expectedTotalEnrollment"));
		fp.getRequest().setAttribute("facRecruitStatusMap", getMapsHolder().getFacRecruitStatusMap());
	}

	private void validateStudy5(FormProcessor fp, StudyBean study, HashMap errors, Validator v) {

		if (!StringUtil.isBlank(fp.getString("facConEmail"))) {
			v.addValidation("facConEmail", Validator.IS_A_EMAIL);
		}
		v.addValidation("facName", Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO,
				StudyValidator.VALIDATION_NUM5);
		v.addValidation("facCity", Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO,
				StudyValidator.VALIDATION_NUM5);
		v.addValidation("facState", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, StudyValidator.VALIDATION_NUM1);
		v.addValidation("facZip", Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO,
				StudyValidator.VALIDATION_NUM3);
		v.addValidation("facCountry", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, StudyValidator.VALIDATION_NUM3);
		v.addValidation("facConName", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, StudyValidator.VALIDATION_NUM5);
		v.addValidation("facConDegree", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, StudyValidator.VALIDATION_NUM5);
		v.addValidation("facConPhone", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, StudyValidator.VALIDATION_NUM5);
		v.addValidation("facConEmail", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, StudyValidator.VALIDATION_NUM5);

		errors.putAll(v.validate());

		study.setFacilityCity(fp.getString("facCity"));
		study.setFacilityContactDegree(fp.getString("facConDrgree"));
		study.setFacilityName(fp.getString("facName"));
		study.setFacilityContactEmail(fp.getString("facConEmail"));
		study.setFacilityContactPhone(fp.getString("facConPhone"));
		study.setFacilityContactName(fp.getString("facConName"));
		study.setFacilityCountry(fp.getString("facCountry"));
		study.setFacilityContactDegree(fp.getString("facConDegree"));
		study.setFacilityState(fp.getString("facState"));
		study.setFacilityZip(fp.getString("facZip"));

		if (!errors.isEmpty()) {
			fp.getRequest().setAttribute("formMessages", errors);
			fp.getRequest().setAttribute("facRecruitStatusMap", getMapsHolder().getFacRecruitStatusMap());
		}
	}

	private void validateStudy6(FormProcessor fp, StudyBean study, HashMap errors, Validator v) {
		v.addValidation("medlineIdentifier", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, StudyValidator.VALIDATION_NUM5);
		v.addValidation("url", Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO,
				StudyValidator.VALIDATION_NUM5);
		v.addValidation("urlDescription", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, StudyValidator.VALIDATION_NUM5);

		errors.putAll(v.validate());

		study.setMedlineIdentifier(fp.getString("medlineIdentifier"));
		study.setResultsReference(fp.getBoolean("resultsReference"));
		study.setUrl(fp.getString("url"));
		study.setUrlDescription(fp.getString("urlDescription"));
		if (!errors.isEmpty()) {
			fp.getRequest().setAttribute("formMessages", errors);
		}
	}

	private void validateStudy7(FormProcessor fp, HashMap errors, Validator v, int studyId,
			Map<String, List<DiscrepancyDescription>> dDescriptionsMap) {
		validateSpecifiedDescriptions(fp, errors, v, studyId, dDescriptionsMap.get("dnUpdateDescriptions"),
				"updateName", "updateVisibilityLevel", "updateDescriptionId", "updateDescriptionError",
				DiscrepancyDescriptionType.DescriptionType.UPDATE_DESCRIPTION.getId());
		validateSpecifiedDescriptions(fp, errors, v, studyId, dDescriptionsMap.get("dnCloseDescriptions"), "closeName",
				"closeVisibilityLevel", "closeDescriptionId", "closeDescriptionError",
				DiscrepancyDescriptionType.DescriptionType.CLOSE_DESCRIPTION.getId());
		validateSpecifiedDescriptions(fp, errors, v, studyId, dDescriptionsMap.get("dnRFCDescriptions"), "dnRFCName",
				"dnRFCVisibilityLevel", "dnRFCDescriptionId", "dnRFCDescriptionError",
				DiscrepancyDescriptionType.DescriptionType.RFC_DESCRIPTION.getId());
	}

	private void validateSpecifiedDescriptions(FormProcessor fp, HashMap errors, Validator v, int studyId,
			List<DiscrepancyDescription> newDescriptions, String descriptionName, String visibilityLevel,
			String descriptionId, String descriptionError, int typeId) {
		newDescriptions.clear();
		final int counter = 25;
		for (int i = 0; i < counter; i++) {
			v.addValidation(descriptionName + i, Validator.LENGTH_NUMERIC_COMPARISON,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, StudyValidator.VALIDATION_NUM5);
			// set list of dn descriptions for specified type here
			if (!"".equals(fp.getString(descriptionName + i))) {
				DiscrepancyDescription dDescription = new DiscrepancyDescription();
				dDescription.setTypeId(typeId);
				dDescription.setName(fp.getString(descriptionName + i));
				switch (fp.getInt(visibilityLevel + i)) {
					case 1 :
						dDescription.setVisibilityLevel("Study");
						break;
					case 2 :
						dDescription.setVisibilityLevel("Site");
						break;
					default :
						dDescription.setVisibilityLevel("Study and Site");
				}
				if (fp.getInt(descriptionId + i) != 0) {
					dDescription.setId(fp.getInt(descriptionId + i));
				}
				dDescription.setStudyId(studyId);
				newDescriptions.add(dDescription);
			}
		}
		errors.putAll(v.validate());
		for (int i = 0; i < newDescriptions.size(); i++) {
			DiscrepancyDescription rfcTerm1 = newDescriptions.get(i);
			for (int j = 0; j < i; j++) {
				DiscrepancyDescription rfcTerm2 = newDescriptions.get(j);
				if (rfcTerm1.getName().equals(rfcTerm2.getName())) {
					Validator.addError(errors, descriptionError + i,
							getResPage().getString("please_correct_the_duplicate_description_found_in_row") + " "
									+ (j + 1));
				}
			}
		}
	}

	private void confirmWholeStudy(FormProcessor fp, StudyBean study, HashMap errors, Validator v) {
		errors.putAll(v.validate());
		getStudyService().prepareStudyBeanConfiguration(study, StudyUtil.getStudyConfigurationParametersMap());
		if (!errors.isEmpty()) {
			fp.getRequest().setAttribute("formMessages", errors);
		}
	}

	private boolean updateStudy2(FormProcessor fp, StudyBean study) {

		study.setOldStatus(study.getStatus());
		study.setStatus(Status.get(fp.getInt("status")));
		try {
			study.setProtocolDateVerification(
					fp.getUpdatedDateProperty(INPUT_VER_DATE, study.getProtocolDateVerification()));
			study.setDatePlannedStart(fp.getUpdatedDateProperty(INPUT_START_DATE, study.getDatePlannedStart()));
			study.setDatePlannedEnd(fp.getUpdatedDateProperty(INPUT_END_DATE, study.getDatePlannedEnd()));
		} catch (IllegalArgumentException ex) {
			//
		}
		study.setPhase(fp.getString("phase"));
		if (fp.getInt("genetic") == 1) {
			study.setGenetic(true);
		} else {
			study.setGenetic(false);
		}
		String interventional = getResAdmin().getString("interventional");
		return interventional.equalsIgnoreCase(study.getProtocolType());
	}

	private void updateStudy3(StudyBean study, boolean isInterventional, FormProcessor fp) {

		study.setPurpose(fp.getString("purpose"));
		ArrayList interventionArray = new ArrayList();
		if (isInterventional) {
			study.setAllocation(fp.getString("allocation"));
			study.setMasking(fp.getString("masking"));
			study.setControl(fp.getString("control"));
			study.setAssignment(fp.getString("assignment"));
			study.setEndpoint(fp.getString("endpoint"));

			StringBuilder interventions = new StringBuilder();
			final int counter = 10;
			for (int i = 0; i < counter; i++) {
				String type = fp.getString("interType" + i);
				String name = fp.getString("interName" + i);
				if (!StringUtil.isBlank(type) && !StringUtil.isBlank(name)) {
					InterventionBean ib = new InterventionBean(fp.getString("interType" + i),
							fp.getString("interName" + i));
					interventionArray.add(ib);
					interventions.append(ib.toString()).append(",");
				}
			}
			study.setInterventions(interventions.toString());

		} else {
			study.setDuration(fp.getString("duration"));
			study.setSelection(fp.getString("selection"));
			study.setTiming(fp.getString("timing"));
		}
		fp.getRequest().setAttribute("interventions", interventionArray);
	}

	private ArrayList parseInterventions(StudyBean sb) {
		ArrayList inters = new ArrayList();
		String interventions = sb.getInterventions();
		try {
			if (!StringUtil.isBlank(interventions)) {
				StringTokenizer st = new StringTokenizer(interventions, ",");
				while (st.hasMoreTokens()) {
					String s = st.nextToken();
					StringTokenizer st1 = new StringTokenizer(s, "/");
					String type = st1.nextToken();
					String name = st1.nextToken();
					InterventionBean ib = new InterventionBean(type, name);
					inters.add(ib);

				}
			}
		} catch (NoSuchElementException nse) {
			return new ArrayList();
		}
		return inters;

	}

	private void setMaps(HttpServletRequest request, boolean isInterventional, ArrayList interventionArray) {
		if (isInterventional) {
			request.setAttribute("interPurposeMap", getMapsHolder().getInterPurposeMap());
			request.setAttribute("allocationMap", getMapsHolder().getAllocationMap());
			request.setAttribute("maskingMap", getMapsHolder().getMaskingMap());
			request.setAttribute("controlMap", getMapsHolder().getControlMap());
			request.setAttribute("assignmentMap", getMapsHolder().getAssignmentMap());
			request.setAttribute("endpointMap", getMapsHolder().getEndpointMap());
			request.setAttribute("interTypeMap", getMapsHolder().getInterTypeMap());
			request.getSession().setAttribute("interventions", interventionArray);
		} else {
			request.setAttribute("obserPurposeMap", getMapsHolder().getObserPurposeMap());
			request.setAttribute("selectionMap", getMapsHolder().getSelectionMap());
			request.setAttribute("timingMap", getMapsHolder().getTimingMap());
		}
	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		return SpringServlet.ADMIN_SERVLET_CODE;
	}
}
