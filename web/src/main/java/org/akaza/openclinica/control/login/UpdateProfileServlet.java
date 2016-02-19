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
package org.akaza.openclinica.control.login;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.SpringServlet;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.clinovo.util.DateUtil;
import com.clinovo.validator.UserValidator;

/**
 * UpdateProfileServlet.
 * 
 * @author jxu
 * @version CVS: $Id: UpdateProfileServlet.java,v 1.9 2005/02/23 18:58:11 jxu Exp $
 * 
 *          Servlet for processing 'update profile' request from user
 */
@Component
@SuppressWarnings("unused")
public class UpdateProfileServlet extends SpringServlet {

	private static final long serialVersionUID = -2519124535258437372L;
	public static final String EMAIL = "contactEmail";
	public static final String USER_ID = "user_id";

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		//
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		String action = request.getParameter("action"); // action sent by user
		StudyDAO sdao = new StudyDAO(getDataSource());
		UserAccountDAO udao = new UserAccountDAO(getDataSource());
		UserAccountBean userBean1 = (UserAccountBean) udao.findByUserName(ub.getName());

		ArrayList studies = currentRole.getRole() == Role.SYSTEM_ADMINISTRATOR
				? (ArrayList) sdao.findAllParents()
				: (ArrayList) sdao.findAllByUser(ub.getName());

		if (StringUtils.isBlank(action)) {
			request.setAttribute("studies", studies);
			request.getSession().setAttribute("userBean1", userBean1);
			request.setAttribute(TIME_ZONE_IDS_SORTED_REQUEST_ATR, DateUtil.getAvailableTimeZoneIDsSorted());
			forwardPage(Page.UPDATE_PROFILE, request, response);
		} else {
			if ("back".equalsIgnoreCase(action)) {
				request.setAttribute("studies", studies);
				request.setAttribute(TIME_ZONE_IDS_SORTED_REQUEST_ATR, DateUtil.getAvailableTimeZoneIDsSorted());
				forwardPage(Page.UPDATE_PROFILE, request, response);
			}
			if ("confirm".equalsIgnoreCase(action)) {
				logger.info("confirm");
				request.setAttribute("studies", studies);
				confirmProfile(request, response, userBean1);

			} else if ("submit".equalsIgnoreCase(action)) {
				logger.info("submit");
				submitProfile(udao, request);

				addPageMessage(getResPage().getString("profile_updated_succesfully"), request);
				ub.incNumVisitsToMainMenu();
				forwardPage(Page.MENU_SERVLET, request, response);
			}
		}

	}

	@SuppressWarnings("rawtypes")
	private void confirmProfile(HttpServletRequest request, HttpServletResponse response, UserAccountBean userBean1)
			throws Exception {
		FormProcessor fp = new FormProcessor(request);

		String password = fp.getString("passwd").trim();
		String newDigestPass = getSecurityManager().encryptPassword(password, getUserDetails());

		userBean1.setFirstName(fp.getString("firstName"));
		userBean1.setLastName(fp.getString("lastName"));
		userBean1.setEmail(fp.getString("email"));
		userBean1.setInstitutionalAffiliation(fp.getString("company"));
		userBean1.setPasswdChallengeQuestion(fp.getString("passwdChallengeQuestion"));
		userBean1.setPasswdChallengeAnswer(fp.getString("passwdChallengeAnswer"));
		userBean1.setPhone(fp.getString("phone"));
		userBean1.setActiveStudyId(fp.getInt("activeStudyId"));
		userBean1.setUserTimeZoneId(fp.getString(INPUT_TIME_ZONE));

		StudyDAO studyDao = getStudyDAO();
		UserAccountDAO userAccountDao = getUserAccountDAO();

		StudyBean newActiveStudy = (StudyBean) studyDao.findByPK(userBean1.getActiveStudyId());
		request.setAttribute("newActiveStudy", newActiveStudy);

		HashMap errors = UserValidator.validateUpdateProfile(getConfigurationDao(), userAccountDao, newDigestPass,
				userBean1, getUserDetails(), getSecurityManager());

		if (errors.isEmpty()) {
			logger.info("no errors");
			request.getSession().setAttribute("userBean1", userBean1);
			if (!StringUtils.isBlank(fp.getString("passwd"))) {
				userBean1.setPasswd(newDigestPass);
				userBean1.setPasswdTimestamp(new Date());
			}
			request.getSession().setAttribute("userBean1", userBean1);
			forwardPage(Page.UPDATE_PROFILE_CONFIRM, request, response);

		} else {
			logger.info("has validation errors");
			request.getSession().setAttribute("userBean1", userBean1);
			request.setAttribute("formMessages", errors);
			request.setAttribute(TIME_ZONE_IDS_SORTED_REQUEST_ATR, DateUtil.getAvailableTimeZoneIDsSorted());
			forwardPage(Page.UPDATE_PROFILE, request, response);
		}

	}

	private void submitProfile(UserAccountDAO udao, HttpServletRequest request) {
		UserAccountBean ub = getUserAccountBean(request);
		logger.info("user bean to be updated:" + ub.getId() + ub.getFirstName());

		UserAccountBean userBean1 = (UserAccountBean) request.getSession().getAttribute("userBean1");
		if (userBean1 != null) {
			userBean1.setLastVisitDate(new Date());
			userBean1.setUpdater(ub);
			updateCalendarEmailJob(userBean1, logger);
			udao.update(userBean1);

			request.getSession().setAttribute("reloadUserBean", true);
			request.getSession().setAttribute("userBean", userBean1);
			request.getSession().removeAttribute("userBean1");
		}
	}
}
