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
package org.akaza.openclinica.control.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.core.SecurityManager;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InconsistentStateException;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.SQLInitServlet;
import org.quartz.JobDataMap;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdScheduler;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Component;

import com.clinovo.util.EmailUtil;
import com.clinovo.util.ValidatorHelper;

/**
 * Servlet for creating a user account.
 * 
 * @author ssachs
 * 
 */
@SuppressWarnings({"rawtypes", "serial"})
@Component
public class EditUserAccountServlet extends Controller {
	public static final String INPUT_FIRST_NAME = "firstName";

	public static final String INPUT_LAST_NAME = "lastName";

	public static final String INPUT_EMAIL = "email";

	public static final String INPUT_PHONE = "phone";

	public static final String INPUT_INSTITUTION = "institutionalAffiliation";

	public static final String INPUT_RESET_PASSWORD = "resetPassword";

	public static final String INPUT_USER_TYPE = "userType";

	public static final String INPUT_CONFIRM_BUTTON = "submit";

	public static final String INPUT_DISPLAY_PWD = "displayPwd";

	public static final String PATH = "EditUserAccount";

	public static final String ARG_USERID = "userId";

	public static final String ARG_STEPNUM = "stepNum";

	public static final String INPUT_RUN_WEBSERVICES = "runWebServices";

	// possible values of ARG_STEPNUM
	public static final int EDIT_STEP = 1;

	public static final int CONFIRM_STEP = 2;

	public static final String USER_ACCOUNT_NOTIFICATION = "notifyPassword";

	public static final String EMAIL = "contactEmail";
	public static final String USER_ID = "user_id";

	public static final int FIFTY = 50;
	public static final int ONE_H_TWENTY = 120;
	public static final int TWO_H_FIFTY_FIVE = 55;

	private ArrayList getAllStudies() {
		StudyDAO sdao = getStudyDAO();
		return (ArrayList) sdao.findAll();
	}

	/**
	 * Get link to the current page.
	 * 
	 * @param userId
	 *            int. \ * @return String
	 */
	public static String getLink(int userId) {
		return PATH + '?' + ARG_USERID + '=' + userId;
	}

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		if (!ub.isSysAdmin()) {
			addPageMessage(
					respage.getString("no_have_correct_privilege_current_study")
							+ respage.getString("change_study_contact_sysadmin"), request);
			throw new InsufficientPermissionException(Page.MENU_SERVLET,
					resexception.getString("you_may_not_perform_administrative_functions"), "1");
		}
	}

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);

		FormProcessor fp = new FormProcessor(request);

		// because we need to use this in the confirmation and error parts too
		ArrayList studies = getAllStudies();
		request.setAttribute("studies", studies);

		int userId = fp.getInt(ARG_USERID);
		UserAccountDAO udao = getUserAccountDAO();
		UserAccountBean user = (UserAccountBean) udao.findByPK(userId);
		request.setAttribute("editedUser", user);
		int stepNum = fp.getInt(ARG_STEPNUM);

		if (!fp.isSubmitted()) {
			addEntityList("userTypes", getUserTypes(),
					respage.getString("the_user_could_not_be_edited_because_no_user_types"), Page.ADMIN_SYSTEM,
					request, response);
			loadPresetValuesFromBean(fp, user);
			fp.addPresetValue(ARG_STEPNUM, EDIT_STEP);
			setPresetValues(fp.getPresetValues(), request);

			// addEntityList("userTypes", getUserTypes(),
			// "The user could not be edited because there are no user types
			// available.",
			// Page.ADMIN_SYSTEM);
			request.setAttribute("userName", user.getName());
			forwardPage(Page.EDIT_ACCOUNT, request, response);
		} else if (stepNum == EDIT_STEP) {
			Validator v = new Validator(new ValidatorHelper(request, getConfigurationDao()));

			v.addValidation(INPUT_FIRST_NAME, Validator.NO_BLANKS);
			v.addValidation(INPUT_LAST_NAME, Validator.NO_BLANKS);

			v.addValidation(INPUT_FIRST_NAME, Validator.LENGTH_NUMERIC_COMPARISON,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, FIFTY);
			v.addValidation(INPUT_LAST_NAME, Validator.LENGTH_NUMERIC_COMPARISON,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, FIFTY);

			v.addValidation(INPUT_EMAIL, Validator.NO_BLANKS);
			v.addValidation(INPUT_EMAIL, Validator.LENGTH_NUMERIC_COMPARISON,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, ONE_H_TWENTY);
			v.addValidation(INPUT_EMAIL, Validator.IS_A_EMAIL);

			v.addValidation(INPUT_INSTITUTION, Validator.NO_BLANKS);
			v.addValidation(INPUT_INSTITUTION, Validator.LENGTH_NUMERIC_COMPARISON,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, TWO_H_FIFTY_FIVE);

			HashMap errors = v.validate();

			if (errors.isEmpty()) {
				loadPresetValuesFromForm(fp);
				fp.addPresetValue(ARG_STEPNUM, CONFIRM_STEP);

				setPresetValues(fp.getPresetValues(), request);
				request.setAttribute("userName", user.getName());
				forwardPage(Page.EDIT_ACCOUNT_CONFIRM, request, response);

			} else {
				loadPresetValuesFromForm(fp);
				fp.addPresetValue(ARG_STEPNUM, EDIT_STEP);
				setInputMessages(errors, request);

				setPresetValues(fp.getPresetValues(), request);
				addEntityList("userTypes", getUserTypes(),
						respage.getString("the_user_could_not_be_edited_because_no_user_types"), Page.ADMIN_SYSTEM,
						request, response);

				addPageMessage(
						respage.getString("there_were_some_errors_submission")
								+ respage.getString("see_below_for_details"), request);
				forwardPage(Page.EDIT_ACCOUNT, request, response);
			}
		} else if (stepNum == CONFIRM_STEP) {
			String button = fp.getString(INPUT_CONFIRM_BUTTON);

			if (button.equals(resword.getString("back"))) {
				loadPresetValuesFromForm(fp);
				fp.addPresetValue(ARG_STEPNUM, EDIT_STEP);

				addEntityList("userTypes", getUserTypes(),
						respage.getString("the_user_could_not_be_edited_because_no_user_types"), Page.ADMIN_SYSTEM,
						request, response);
				setPresetValues(fp.getPresetValues(), request);
				request.setAttribute("userName", user.getName());
				forwardPage(Page.EDIT_ACCOUNT, request, response);
			} else if (button.equals(resword.getString("submit"))) {
				Page forwardTo = Page.LIST_USER_ACCOUNTS_SERVLET;
				user.setFirstName(fp.getString(INPUT_FIRST_NAME));
				user.setLastName(fp.getString(INPUT_LAST_NAME));
				user.setEmail(fp.getString(INPUT_EMAIL));
				user.setPhone(fp.getString(INPUT_PHONE));
				user.setInstitutionalAffiliation(fp.getString(INPUT_INSTITUTION));
				user.setUpdater(ub);
				user.setRunWebservices(fp.getBoolean(INPUT_RUN_WEBSERVICES));
				boolean wasSysAdmin = user.isSysAdmin();
				if (!user.getName().equalsIgnoreCase("root")) {
					UserType ut = UserType.get(fp.getInt(INPUT_USER_TYPE));
					if (ut.equals(UserType.SYSADMIN)) {
						user.addUserType(ut);
					} else if (ut.equals(UserType.TECHADMIN)) {
						user.addUserType(ut);
					} else {
						user.addUserType(UserType.USER);
					}
				} else {
					user.setName("root");
					user.addUserType(UserType.SYSADMIN);
				}
				if (fp.getBoolean(INPUT_RESET_PASSWORD)) {
					SecurityManager sm = getSecurityManager();
					String password = sm.genPassword();
					String passwordHash = sm.encryptPassword(password, getUserDetails());

					user.setPasswd(passwordHash);
					user.setPasswdTimestamp(null);

					udao.update(user);
					udao.update(user);

					if ("no".equalsIgnoreCase(fp.getString(INPUT_DISPLAY_PWD))) {
						logger.info("displayPwd is no");
						try {
							sendResetPasswordEmail(request, user, password);
						} catch (Exception e) {
							addPageMessage(respage.getString("there_was_an_error_sending_reset_email_try_reset"),
									request);
						}
					} else {
						addPageMessage(respage.getString("new_user_password") + ":<br/> " + password + "<br/>"
								+ respage.getString("please_write_down_the_password_and_provide"), request);
					}
				} else {
					udao.update(user);
				}
				updateCalendarEmailJob(user);
				addPageMessage(
						respage.getString("the_user_account") + " \"" + user.getName() + "\" "
								+ respage.getString("was_updated_succesfully"), request);
				if (ub.getId() == user.getId()) {
					request.getSession().setAttribute("reloadUserBean", true);
					if (wasSysAdmin && !user.isSysAdmin()) {
						forwardTo = Page.MENU_SERVLET;
						addPageMessage(respage.getString("you_may_not_perform_administrative_functions"), request);
					}
				}
				forwardPage(forwardTo, request, response);
			} else {
				throw new InconsistentStateException(Page.ADMIN_SYSTEM,
						resexception.getString("an_invalid_submit_button_was_clicked"));
			}
		} else {
			throw new InconsistentStateException(Page.ADMIN_SYSTEM,
					resexception.getString("an_invalid_step_was_specified"));
		}
	}

	private void loadPresetValuesFromBean(FormProcessor fp, UserAccountBean user) {
		fp.addPresetValue(INPUT_FIRST_NAME, user.getFirstName());
		fp.addPresetValue(INPUT_LAST_NAME, user.getLastName());
		fp.addPresetValue(INPUT_EMAIL, user.getEmail());
		fp.addPresetValue(INPUT_PHONE, user.getPhone());
		fp.addPresetValue(INPUT_INSTITUTION, user.getInstitutionalAffiliation());
		int userTypeId = UserType.USER.getId();
		if (user.isTechAdmin()) {
			userTypeId = UserType.TECHADMIN.getId();
		} else if (user.isSysAdmin()) {
			userTypeId = UserType.SYSADMIN.getId();
		}
		fp.addPresetValue(INPUT_USER_TYPE, userTypeId);
		fp.addPresetValue(ARG_USERID, user.getId());
		fp.addPresetValue(INPUT_RUN_WEBSERVICES, user.getRunWebservices() ? 1 : 0);

		String sendPwd = SQLInitServlet.getField("user_account_notification");
		fp.addPresetValue(USER_ACCOUNT_NOTIFICATION, sendPwd);
	}

	private void loadPresetValuesFromForm(FormProcessor fp) {
		fp.clearPresetValues();

		String textFields[] = {ARG_USERID, INPUT_FIRST_NAME, INPUT_LAST_NAME, INPUT_PHONE, INPUT_EMAIL,
				INPUT_INSTITUTION, INPUT_DISPLAY_PWD};
		fp.setCurrentStringValuesAsPreset(textFields);

		String ddlbFields[] = {INPUT_USER_TYPE, INPUT_RESET_PASSWORD, INPUT_RUN_WEBSERVICES};
		fp.setCurrentIntValuesAsPreset(ddlbFields);
	}

	private ArrayList getUserTypes() {

		ArrayList types = UserType.toArrayList();
		types.remove(UserType.INVALID);
		types.remove(UserType.TECHADMIN);

		return types;
	}

	private void sendResetPasswordEmail(HttpServletRequest request, UserAccountBean user, String password)
			throws Exception {
		StudyBean currentStudy = getCurrentStudy(request);

		logger.info("Sending password reset notification to " + user.getName());

		String body = EmailUtil.getEmailBodyStart();
		body += resword.getString("dear") + " " + user.getFirstName() + " " + user.getLastName() + ",<br/><br/>\n\n";
		body += restext.getString("your_password_has_been_reset_on_openclinica") + ":<br/><br/>\n\n";
		body += resword.getString("user_name") + ": " + user.getName() + "<br/>\n";
		body += resword.getString("password") + ": " + password + "<br/><br/>\n\n";
		body += restext.getString("please_test_your_login_information_and_let") + "<br/>\n";
		body += "<a href='" + SQLInitServlet.getSystemURL() + "'>" + SQLInitServlet.getField("sysURL")
				+ "</a><br/><br/>\n\n";
		StudyDAO sdao = getStudyDAO();
		StudyBean emailParentStudy;
		if (currentStudy.getParentStudyId() > 0) {
			emailParentStudy = (StudyBean) sdao.findByPK(currentStudy.getParentStudyId());
		} else {
			emailParentStudy = currentStudy;
		}
		body += respage.getString("best_system_administrator").replace("{0}", emailParentStudy.getName());
		body += EmailUtil.getEmailBodyEnd();
		body += EmailUtil.getEmailFooter(CoreResources.getSystemLocale());
		sendEmail(user.getEmail().trim(), restext.getString("your_openclinica_account_password_reset"), body, false,
				request);
	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		return Controller.ADMIN_SERVLET_CODE;
	}

	private void updateCalendarEmailJob(UserAccountBean uaBean) {
		String triggerGroup = "CALENDAR";
		StdScheduler scheduler = getStdScheduler();
		try {
			Set<TriggerKey> legacyTriggers = scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(triggerGroup));
			if (legacyTriggers != null && legacyTriggers.size() > 0) {
				for (TriggerKey triggerKey : legacyTriggers) {
					Trigger trigger = scheduler.getTrigger(triggerKey);
					JobDataMap dataMap = trigger.getJobDataMap();
					String contactEmail = dataMap.getString(EMAIL);
					int userId = dataMap.getInt(USER_ID);
					logger.info("contact email from calendared " + contactEmail + " for user userId " + userId);
					logger.info("Old email " + dataMap.getString(EMAIL));
					if (uaBean.getId() == userId) {
						dataMap.put(EMAIL, uaBean.getEmail());
						JobDetailImpl jobDetailBean = new JobDetailImpl();
						jobDetailBean.setKey(trigger.getJobKey());
						jobDetailBean.setDescription(trigger.getDescription());
						jobDetailBean.setGroup(triggerGroup);
						jobDetailBean.setName(triggerKey.getName());
						jobDetailBean.setJobClass(org.akaza.openclinica.service.calendar.EmailStatefulJob.class);
						jobDetailBean.setJobDataMap(dataMap);
						logger.info("New email " + dataMap.getString(EMAIL));
						jobDetailBean.setDurability(true);
						scheduler.addJob(jobDetailBean, true);
					}
				}
			}
		} catch (SchedulerException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
}
