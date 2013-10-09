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

import com.clinovo.util.ValidatorHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.dao.hibernate.ConfigurationDao;
import org.akaza.openclinica.dao.hibernate.PasswordRequirementsDao;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.apache.commons.lang.StringUtils;
import org.quartz.JobDataMap;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdScheduler;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Component;

/**
 * @author jxu
 * @version CVS: $Id: UpdateProfileServlet.java,v 1.9 2005/02/23 18:58:11 jxu Exp $
 * 
 *          Servlet for processing 'update profile' request from user
 */
@Component
public class UpdateProfileServlet extends Controller {

	private static final long serialVersionUID = -2519124535258437372L;
	public static final String EMAIL = "contactEmail";
	public static final String USER_ID = "user_id";

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response) throws InsufficientPermissionException {
        //
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserAccountBean ub = getUserAccountBean(request);

		String action = request.getParameter("action");// action sent by user
		StudyDAO sdao = new StudyDAO(getDataSource());
		UserAccountDAO udao = new UserAccountDAO(getDataSource());
		UserAccountBean userBean1 = (UserAccountBean) udao.findByUserName(ub.getName());

		ArrayList studies = (ArrayList) sdao.findAllByUser(ub.getName());

		if (StringUtils.isBlank(action)) {
			request.setAttribute("studies", studies);
			request.getSession().setAttribute("userBean1", userBean1);
			forwardPage(Page.UPDATE_PROFILE, request, response);
		} else {
			if ("back".equalsIgnoreCase(action)) {
				request.setAttribute("studies", studies);
				forwardPage(Page.UPDATE_PROFILE, request, response);
			}
			if ("confirm".equalsIgnoreCase(action)) {
				logger.info("confirm");
				request.setAttribute("studies", studies);
				confirmProfile(request, response, userBean1, udao);

			} else if ("submit".equalsIgnoreCase(action)) {
				logger.info("submit");
				submitProfile(udao, request);

				addPageMessage(respage.getString("profile_updated_succesfully"), request);
				ub.incNumVisitsToMainMenu();
				forwardPage(Page.MENU_SERVLET, request, response);
			}
		}

	}

	@SuppressWarnings("rawtypes")
	private void confirmProfile(HttpServletRequest request, HttpServletResponse response, UserAccountBean userBean1, UserAccountDAO udao) throws Exception {
        UserAccountBean ub = getUserAccountBean(request);

		Validator v = new Validator(new ValidatorHelper(request, getConfigurationDao()));
		FormProcessor fp = new FormProcessor(request);

		v.addValidation("firstName", Validator.NO_BLANKS);
		v.addValidation("lastName", Validator.NO_BLANKS);
		v.addValidation("email", Validator.IS_A_EMAIL);
		v.addValidation("passwdChallengeQuestion", Validator.NO_BLANKS);
		v.addValidation("passwdChallengeAnswer", Validator.NO_BLANKS);
		v.addValidation("oldPasswd", Validator.NO_BLANKS);// old password
		String password = fp.getString("passwd").trim();

		ConfigurationDao configurationDao = getConfigurationDao();

		org.akaza.openclinica.core.SecurityManager sm = getSecurityManager();

		String newDigestPass = sm.encrytPassword(password, getUserDetails());
		List<String> pwdErrors = new ArrayList<String>();

		if (!StringUtils.isBlank(password)) {
			v.addValidation("passwd", Validator.IS_A_PASSWORD);// new password
			v.addValidation("passwd1", Validator.CHECK_SAME, "passwd");// confirm
			// password

			PasswordRequirementsDao passwordRequirementsDao = new PasswordRequirementsDao(configurationDao);
			Locale locale = request.getLocale();
			ResourceBundle resexception = ResourceBundleProvider.getExceptionsBundle(locale);

			pwdErrors = PasswordValidator.validatePassword(passwordRequirementsDao, udao, userBean1.getId(), password,
					newDigestPass, resexception);
		}
		v.addValidation("phone", Validator.NO_BLANKS);
		HashMap errors = v.validate();
		for (String err : pwdErrors) {
			Validator.addError(errors, "passwd", err);
		}

		userBean1.setFirstName(fp.getString("firstName"));
		userBean1.setLastName(fp.getString("lastName"));
		userBean1.setEmail(fp.getString("email"));
		userBean1.setInstitutionalAffiliation(fp.getString("institutionalAffiliation"));
		userBean1.setPasswdChallengeQuestion(fp.getString("passwdChallengeQuestion"));
		userBean1.setPasswdChallengeAnswer(fp.getString("passwdChallengeAnswer"));
		userBean1.setPhone(fp.getString("phone"));
		userBean1.setActiveStudyId(fp.getInt("activeStudyId"));
		StudyDAO sdao = getStudyDAO();

		StudyBean newActiveStudy = (StudyBean) sdao.findByPK(userBean1.getActiveStudyId());
		request.setAttribute("newActiveStudy", newActiveStudy);

		if (errors.isEmpty()) {
			logger.info("no errors");

			request.getSession().setAttribute("userBean1", userBean1);
			String oldPass = fp.getString("oldPasswd").trim();

			if (!sm.isPasswordValid(ub.getPasswd(), oldPass, getUserDetails())) {
				Validator.addError(errors, "oldPasswd", resexception.getString("wrong_old_password"));
				request.setAttribute("formMessages", errors);
				forwardPage(Page.UPDATE_PROFILE, request, response);
			} else {
				if (!StringUtils.isBlank(fp.getString("passwd"))) {
					userBean1.setPasswd(newDigestPass);
					userBean1.setPasswdTimestamp(new Date());
				}
                request.getSession().setAttribute("userBean1", userBean1);
				forwardPage(Page.UPDATE_PROFILE_CONFIRM, request, response);
			}

		} else {
			logger.info("has validation errors");
            request.getSession().setAttribute("userBean1", userBean1);
			request.setAttribute("formMessages", errors);
			forwardPage(Page.UPDATE_PROFILE, request, response);
		}

	}

	/**
	 * Updates user new profile
	 * 
	 */
	private void submitProfile(UserAccountDAO udao, HttpServletRequest request) {
        UserAccountBean ub = getUserAccountBean(request);
		logger.info("user bean to be updated:" + ub.getId() + ub.getFirstName());

		UserAccountBean userBean1 = (UserAccountBean) request.getSession().getAttribute("userBean1");
		if (userBean1 != null) {
			userBean1.setLastVisitDate(new Date());
			userBean1.setUpdater(ub);
			updateCalendarEmailJob(userBean1);
			udao.update(userBean1);

			request.getSession().setAttribute("userBean", userBean1);
			ub = userBean1;
            request.getSession().removeAttribute("userBean1");
		}
	}
	
	@SuppressWarnings("null")
	private void updateCalendarEmailJob (UserAccountBean uaBean) {
		String triggerGroup = "CALENDAR";
        StdScheduler scheduler = getStdScheduler();
		try {
			Set<TriggerKey> legacyTriggers = scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(triggerGroup));
			if (legacyTriggers == null && legacyTriggers.size() == 0) {
				return;
			}
			for (TriggerKey triggerKey : legacyTriggers) {
				Trigger trigger = scheduler.getTrigger(triggerKey);
				JobDataMap dataMap = trigger.getJobDataMap();
				String contactEmail = dataMap.getString(EMAIL).toString();
				int userId = (Integer) dataMap.getInt(USER_ID);
				logger.info("contact email from calendared " +contactEmail + " for user userId " +userId);
				logger.info("Old email " +dataMap.getString(EMAIL).toString());
				if(uaBean.getId() == userId) {
					dataMap.put(EMAIL, uaBean.getEmail());
					JobDetailImpl jobDetailBean = new JobDetailImpl();
					jobDetailBean.setKey(trigger.getJobKey());
					jobDetailBean.setDescription(trigger.getDescription());
					jobDetailBean.setGroup(triggerGroup);
					jobDetailBean.setName(triggerKey.getName());
					jobDetailBean.setJobClass(org.akaza.openclinica.service.calendar.EmailStatefulJob.class);
					jobDetailBean.setJobDataMap(dataMap);
					logger.info("New email " +dataMap.getString(EMAIL).toString());
					jobDetailBean.setDurability(true);
					scheduler.addJob(jobDetailBean, true);
				}
			}
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
