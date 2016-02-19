/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2015 Clinovo Inc.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package com.clinovo.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.BaseController;
import org.akaza.openclinica.control.core.SpringController;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.model.CRFEvaluationTableFactory;

/**
 * The controller for managing crf evaluation page.
 * 
 */
@Controller
public class CRFEvaluationController extends SpringController {

	public static final Logger LOGGER = LoggerFactory.getLogger(CRFEvaluationController.class);

	public static final String STUDY = "study";
	public static final String SHOW_MORE_LINK = "showMoreLink";
	public static final String MAIN_MENU_REDIRECT = "redirect:/MainMenu";
	public static final String CRF_EVALUATION_TABLE = "crfEvaluationTable";
	public static final String PAGE_CRF_EVALUATION = "/pages/crfEvaluation";
	public static final String EVALUATE_WITH_CONTEXT = "evaluateWithContext";
	public static final String CRF_EVALUATION_STORED_URL = "crfEvaluationStoredUrl";
	public static final String EVALUATION_CRF_EVALUATION = "evaluation/crfEvaluation";
	public static final String NO_PERMISSION_TO_EVALUATE = "no_permission_to_evaluate";

	@Autowired
	private DataSource dataSource;

	@Autowired
	private MessageSource messageSource;

	/**
	 * Handle requests from the crf evaluation page.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @return String
	 * @throws Exception
	 *             an exception
	 */
	@RequestMapping("/crfEvaluation")
	public String crfEvaluation(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String httpPath = (String) request.getSession().getAttribute(CRF_EVALUATION_STORED_URL);
		String queryString = request.getQueryString();
		if (queryString == null && httpPath != null) {
			response.sendRedirect("crfEvaluation?" + httpPath);
		} else {
			request.getSession().setAttribute(CRF_EVALUATION_STORED_URL, queryString);
		}

		String page = EVALUATION_CRF_EVALUATION;
		StudyBean currentStudy = (StudyBean) request.getSession().getAttribute(STUDY);
		StudyUserRoleBean userRole = (StudyUserRoleBean) request.getSession().getAttribute(BaseController.USER_ROLE);
		UserAccountBean userAccountBean = (UserAccountBean) request.getSession().getAttribute(
				BaseController.USER_BEAN_NAME);
		BaseController.removeLockedCRF(userAccountBean.getId());
		if (userRole.getRole() == Role.SYSTEM_ADMINISTRATOR || userRole.getRole() == Role.STUDY_ADMINISTRATOR
				|| userRole.getRole() == Role.STUDY_EVALUATOR) {
			CRFEvaluationTableFactory factory = new CRFEvaluationTableFactory(dataSource, messageSource,
					new StudyParameterValueDAO(dataSource).findByHandleAndStudy(currentStudy.getId(),
							EVALUATE_WITH_CONTEXT), request.getParameter(SHOW_MORE_LINK));
			factory.setUserAccountDAO(new UserAccountDAO(dataSource));
			request.setAttribute(CRF_EVALUATION_TABLE, factory.createTable(request, response).render());
		} else {
			org.akaza.openclinica.control.core.Controller.addPageMessage(
					messageSource.getMessage(NO_PERMISSION_TO_EVALUATE, null, LocaleResolver.getLocale(request)),
					request, LOGGER);
			org.akaza.openclinica.control.core.Controller.storePageMessages(request);
			page = MAIN_MENU_REDIRECT;
		}
		return page;
	}
}
