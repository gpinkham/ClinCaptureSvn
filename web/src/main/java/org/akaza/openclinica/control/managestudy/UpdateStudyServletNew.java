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
import com.clinovo.enums.study.StudyOrigin;
import com.clinovo.enums.study.StudyParameter;
import com.clinovo.enums.study.StudyProtocolType;
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

		Map<String, List<DiscrepancyDescription>> dnDescriptionsMap = getDiscrepancyDescriptionService()
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

		request.setAttribute("dDescriptionsMap", dnDescriptionsMap);

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

			validate(fp, study, errors, v, dnDescriptionsMap);
			updateStudyStatus(fp, study);
			addPresetValues(fp);
			confirmWholeStudy(fp, study, errors, v);

			request.setAttribute("studyToView", study);
			if (!errors.isEmpty()) {
				logger.debug("found errors : " + errors.toString());
				request.setAttribute("formMessages", errors);
				request.setAttribute("dDescriptions", dnDescriptionsMap);
				forwardPage(Page.UPDATE_STUDY_NEW, request, response);
			} else {
				getStudyService().updateStudy(study, dnDescriptionsMap, getUserAccountBean());
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

	private void validate(FormProcessor fp, StudyBean studyBean, HashMap errors, Validator validator,
			Map<String, List<DiscrepancyDescription>> dnDescriptionsMap) {
		studyBean.setId(fp.getInt("studyId"));

		StudyUtil.prepareDiscrepancyDescriptions(dnDescriptionsMap, studyBean.getId(), true);

		errors.putAll(StudyValidator.validate(validator, getStudyDAO(), studyBean, dnDescriptionsMap,
				DateUtil.DatePattern.DATE, true, true));

		StudyMapsHolder studyMapsHolder = new StudyMapsHolder(StudyUtil.getStudyFeaturesMap(),
				StudyUtil.getStudyParametersMap(), StudyUtil.getStudyFacilitiesMap());

		getStudyService().prepareStudyBean(studyBean, getUserAccountBean(), studyMapsHolder, DateUtil.DatePattern.DATE,
				LocaleResolver.getLocale());
	}

	private void addPresetValues(FormProcessor fp) {
		fp.addPresetValue(StudyParameter.START_DATE.getName(), fp.getString(StudyParameter.START_DATE.getName()));
		fp.addPresetValue(StudyParameter.END_DATE.getName(), fp.getString(StudyParameter.END_DATE.getName()));
		fp.addPresetValue(StudyParameter.APPROVAL_DATE.getName(), fp.getString(StudyParameter.APPROVAL_DATE.getName()));
		setPresetValues(fp.getPresetValues(), fp.getRequest());
	}

	private void confirmWholeStudy(FormProcessor fp, StudyBean study, HashMap errors, Validator v) {
		errors.putAll(v.validate());
		getStudyService().prepareStudyBeanConfiguration(study, StudyUtil.getStudyConfigurationParametersMap());
		if (!errors.isEmpty()) {
			fp.getRequest().setAttribute("formMessages", errors);
		}
	}

	private void updateStudyStatus(FormProcessor fp, StudyBean study) {
		study.setOldStatus(study.getStatus());
		study.setStatus(Status.get(fp.getInt("status")));
	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		return SpringServlet.ADMIN_SERVLET_CODE;
	}
}
