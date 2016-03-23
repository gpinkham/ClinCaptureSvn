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
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.SpringServlet;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import com.clinovo.util.StudyUtil;
import com.clinovo.validator.StudyValidator;

/**
 * Processes request to create a new study.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@Component
public class CreateStudyServlet extends SpringServlet {

	public static final String FAC_ZIP = "FacZIP";
	public static final String FAC_NAME = "FacName";
	public static final String FAC_CITY = "FacCity";
	public static final String FAC_STATE = "FacState";
	public static final String FAC_COUNTRY = "FacCountry";
	public static final String FAC_CONTACT_NAME = "FacContactName";
	public static final String FAC_CONTACT_PHONE = "FacContactPhone";
	public static final String FAC_CONTACT_EMAIL = "FacContactEmail";
	public static final String FAC_CONTACT_DEGREE = "FacContactDegree";

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);

		if (ub.isSysAdmin()) {
			return;
		}

		addPageMessage(getResPage().getString("no_have_correct_privilege_current_study")
				+ getResPage().getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.STUDY_LIST_SERVLET, getResException().getString("not_admin"),
				"1");

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
		FormProcessor fp = new FormProcessor(request);
		UserAccountBean currentUser = getUserAccountBean();

		errors.putAll(StudyValidator.validate(getStudyDAO(), getConfigurationDao()));

		StudyBean studyBean = getStudyService().prepareStudyBean(new StudyBean(), StudyUtil.getStudyParametersMap(),
				StudyUtil.getStudyFeaturesMap());

		if (errors.isEmpty()) {
			logger.info("no errors in the first section");
			request.setAttribute("studyPhaseMap", getMapsHolder().getStudyPhaseMap());
			request.setAttribute("statuses", Status.toActiveArrayList());
			logger.info("setting arrays to request, size of list: " + Status.toArrayList().size());
			if (request.getParameter("Save") != null && request.getParameter("Save").length() > 0) {
				getStudyService().saveStudyBean(fp.getInt("selectedUser"), studyBean, currentUser, getResPage());
				addPageMessage(getResPage().getString("the_new_study_created_succesfully_current"), request);
				forwardPage(Page.STUDY_LIST_SERVLET, request, response);
			}
		} else {
			request.getSession().setAttribute("newStudy", studyBean);
			logger.info("has validation errors in the first section");
			request.setAttribute("formMessages", errors);
			Collection users = getUserAccountDAO().findAllByRole(Role.STUDY_ADMINISTRATOR.getName(),
					Role.STUDY_DIRECTOR.getName());
			request.setAttribute("users", users);
			forwardPage(Page.CREATE_STUDY1, request, response);
		}
	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		return SpringServlet.ADMIN_SERVLET_CODE;
	}

}
