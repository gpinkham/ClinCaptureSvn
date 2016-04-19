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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.SpringServlet;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.service.StudyConfigService;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import com.clinovo.bean.StudyMapsHolder;
import com.clinovo.enums.StudyConfigurationParameter;
import com.clinovo.enums.StudyOrigin;
import com.clinovo.enums.StudyParameter;
import com.clinovo.enums.StudyProtocolType;
import com.clinovo.i18n.LocaleResolver;
import com.clinovo.model.DiscrepancyDescription;
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

		if (!currentStudy.getOrigin().equals(StudyOrigin.GUI.getName()) && !getUserAccountBean().isRoot()) {
			response.sendRedirect(request.getContextPath().concat("/pages/studymodule"));
			return;
		}

		HashMap errors = getErrorsHolder(request);
		StudyInfoPanel panel = getStudyInfoPanel(request);
		panel.reset();

		FormProcessor fp = new FormProcessor(request);
		Validator v = new Validator(new ValidatorHelper(request, getConfigurationDao()));
		int studyId = fp.getInt("id");
		studyId = studyId == 0 ? fp.getInt("studyId") : studyId;
		String action = fp.getString("action");
		StudyDAO sdao = new StudyDAO(getDataSource());

		Map<String, List<DiscrepancyDescription>> dDescriptionsMap = getDiscrepancyDescriptionService()
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
		ArrayList statuses = Status.toStudyUpdateMembersList();
		statuses.add(Status.PENDING);
		request.setAttribute("statuses", statuses);

		String protocolType = study.getProtocolTypeKey();

		boolean isInterventional = StudyProtocolType.INTERVENTIONAL.getValue().equals(protocolType);
		request.setAttribute("isInterventional", isInterventional ? "1" : "0");

		if (study.getParentStudyId() > 0) {
			StudyBean parentStudy = (StudyBean) sdao.findByPK(study.getParentStudyId());
			request.setAttribute("parentStudy", parentStudy);
		}

		if (!action.equals("submit")) {

			// First Load First Form
			if (study.getDatePlannedStart() != null) {
				fp.addPresetValue(StudyParameter.START_DATE.getName(), DateUtil.printDate(study.getDatePlannedStart(),
						getUserAccountBean().getUserTimeZoneId(), DateUtil.DatePattern.DATE, getLocale()));
			}
			if (study.getDatePlannedEnd() != null) {
				fp.addPresetValue(StudyParameter.END_DATE.getName(), DateUtil.printDate(study.getDatePlannedEnd(),
						getUserAccountBean().getUserTimeZoneId(), DateUtil.DatePattern.DATE, getLocale()));
			}
			if (study.getProtocolDateVerification() != null) {
				fp.addPresetValue(StudyParameter.APPROVAL_DATE.getName(),
						DateUtil.printDate(study.getProtocolDateVerification(),
								getUserAccountBean().getUserTimeZoneId(), DateUtil.DatePattern.DATE, getLocale()));
			}
			setPresetValues(fp.getPresetValues(), request);
			// first load 2nd form
		}
		if (action.equals("submit")) {

			validateStudy1(fp, study, errors, v, dDescriptionsMap);
			validateStudy2(fp, study, v);
			validateStudy4(fp, study, errors, v);
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

	private void validateStudy1(FormProcessor fp, StudyBean study, HashMap errors, Validator validator,
			Map<String, List<DiscrepancyDescription>> dDescriptionsMap) {
		study.setId(fp.getInt("studyId"));

		validator.addValidation(StudyConfigurationParameter.INSTANCE_TYPE.getName(), Validator.NO_BLANKS);
		validator.addValidation(StudyConfigurationParameter.INSTANCE_TYPE.getName(),
				Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO,
				StudyValidator.VALIDATION_NUM_20);
		validator.addValidation(StudyConfigurationParameter.STUDY_SUBJECT_ID_LABEL.getName(), Validator.NO_BLANKS);
		validator.addValidation(StudyConfigurationParameter.SECONDARY_ID_LABEL.getName(), Validator.NO_BLANKS);
		validator.addValidation(StudyConfigurationParameter.DATE_OF_ENROLLMENT_FOR_STUDY_LABEL.getName(),
				Validator.NO_BLANKS);
		validator.addValidation(StudyConfigurationParameter.GENDER_LABEL.getName(), Validator.NO_BLANKS);
		validator.addValidation(StudyConfigurationParameter.START_DATE_TIME_LABEL.getName(), Validator.NO_BLANKS);
		validator.addValidation(StudyConfigurationParameter.END_DATE_TIME_LABEL.getName(), Validator.NO_BLANKS);

		errors.putAll(StudyValidator.validate(validator, getStudyDAO(), study, dDescriptionsMap,
				DateUtil.DatePattern.DATE, true));

		StudyMapsHolder studyMapsHolder = new StudyMapsHolder(StudyUtil.getStudyFeaturesMap(),
				StudyUtil.getStudyParametersMap(), StudyUtil.getStudyFacilitiesMap());

		getStudyService().prepareStudyBean(study, getUserAccountBean(), studyMapsHolder, DateUtil.DatePattern.DATE,
				LocaleResolver.getLocale());
	}

	private void validateStudy2(FormProcessor fp, StudyBean study, Validator v) {
		fp.addPresetValue(StudyParameter.START_DATE.getName(), fp.getString(StudyParameter.START_DATE.getName()));
		fp.addPresetValue(StudyParameter.END_DATE.getName(), fp.getString(StudyParameter.END_DATE.getName()));
		fp.addPresetValue(StudyParameter.APPROVAL_DATE.getName(), fp.getString(StudyParameter.APPROVAL_DATE.getName()));
		updateStudy2(fp, study);
		setPresetValues(fp.getPresetValues(), fp.getRequest());
	}

	private void validateStudy4(FormProcessor fp, StudyBean study, HashMap errors, Validator v) {

		v.addValidation("conditions", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, StudyValidator.VALIDATION_NUM_500);
		v.addValidation("keywords", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, StudyValidator.VALIDATION_NUM_255);
		v.addValidation("eligibility", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, StudyValidator.VALIDATION_NUM_500);
		errors.putAll(v.validate());

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
		if (fp.getInt("genetic") == 1) {
			study.setGenetic(true);
		} else {
			study.setGenetic(false);
		}
		String interventional = getResAdmin().getString("interventional");
		return interventional.equalsIgnoreCase(study.getProtocolType());
	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		return SpringServlet.ADMIN_SERVLET_CODE;
	}
}
