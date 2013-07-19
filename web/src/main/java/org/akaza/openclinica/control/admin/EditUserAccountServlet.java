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

import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.core.SecurityManager;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Servlet for creating a user account.
 * 
 * @author ssachs
 * 
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class EditUserAccountServlet extends SecureController {
	public static final String INPUT_FIRST_NAME = "firstName";

	public static final String INPUT_LAST_NAME = "lastName";

	public static final String INPUT_EMAIL = "email";

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

	// possible values of INPUT_CONFIRM_BUTTON
	public static final String BUTTON_CONFIRM_VALUE = "Submit";

	public static final String BUTTON_BACK_VALUE = "Back";

	public static final String USER_ACCOUNT_NOTIFICATION = "notifyPassword";
	
	public static final String EMAIL = "contactEmail";
	public static final String USER_ID = "user_id";
	private StdScheduler scheduler;

	private ArrayList getAllStudies() {
		StudyDAO sdao = new StudyDAO(sm.getDataSource());
		return (ArrayList) sdao.findAll();
	}

	public static String getLink(int userId) {
		return PATH + '?' + ARG_USERID + '=' + userId;
	}

	@Override
	protected void mayProceed() throws InsufficientPermissionException {
		if (!ub.isSysAdmin()) {
			addPageMessage(respage.getString("no_have_correct_privilege_current_study")
					+ respage.getString("change_study_contact_sysadmin"));
			throw new InsufficientPermissionException(Page.MENU_SERVLET,
					resexception.getString("you_may_not_perform_administrative_functions"), "1");
		}

		return;
	}

	@Override
	protected void processRequest() throws Exception {
		FormProcessor fp = new FormProcessor(request);

		// because we need to use this in the confirmation and error parts too
		ArrayList studies = getAllStudies();
		request.setAttribute("studies", studies);

		int userId = fp.getInt(ARG_USERID);
		UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
		UserAccountBean user = (UserAccountBean) udao.findByPK(userId);
        request.setAttribute("editedUser", user);
		int stepNum = fp.getInt(ARG_STEPNUM);

		if (!fp.isSubmitted()) {
			addEntityList("userTypes", getUserTypes(),
					respage.getString("the_user_could_not_be_edited_because_no_user_types"), Page.ADMIN_SYSTEM);
			loadPresetValuesFromBean(fp, user);
			fp.addPresetValue(ARG_STEPNUM, EDIT_STEP);
			setPresetValues(fp.getPresetValues());

			// addEntityList("userTypes", getUserTypes(),
			// "The user could not be edited because there are no user types
			// available.",
			// Page.ADMIN_SYSTEM);
			request.setAttribute("userName", user.getName());
			forwardPage(Page.EDIT_ACCOUNT);
		} else if (stepNum == EDIT_STEP) {
			Validator v = new Validator(request);

			v.addValidation(INPUT_FIRST_NAME, Validator.NO_BLANKS);
			v.addValidation(INPUT_LAST_NAME, Validator.NO_BLANKS);

			v.addValidation(INPUT_FIRST_NAME, Validator.LENGTH_NUMERIC_COMPARISON,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 50);
			v.addValidation(INPUT_LAST_NAME, Validator.LENGTH_NUMERIC_COMPARISON,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 50);

			v.addValidation(INPUT_EMAIL, Validator.NO_BLANKS);
			v.addValidation(INPUT_EMAIL, Validator.LENGTH_NUMERIC_COMPARISON,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 120);
			v.addValidation(INPUT_EMAIL, Validator.IS_A_EMAIL);

			v.addValidation(INPUT_INSTITUTION, Validator.NO_BLANKS);
			v.addValidation(INPUT_INSTITUTION, Validator.LENGTH_NUMERIC_COMPARISON,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);

			HashMap errors = v.validate();

			if (errors.isEmpty()) {
				loadPresetValuesFromForm(fp);
				fp.addPresetValue(ARG_STEPNUM, CONFIRM_STEP);

				setPresetValues(fp.getPresetValues());
				request.setAttribute("userName", user.getName());
				forwardPage(Page.EDIT_ACCOUNT_CONFIRM);

			} else {
				loadPresetValuesFromForm(fp);
				fp.addPresetValue(ARG_STEPNUM, EDIT_STEP);
				setInputMessages(errors);

				setPresetValues(fp.getPresetValues());
				addEntityList("userTypes", getUserTypes(),
						respage.getString("the_user_could_not_be_edited_because_no_user_types"), Page.ADMIN_SYSTEM);

				addPageMessage(respage.getString("there_were_some_errors_submission")
						+ respage.getString("see_below_for_details"));
				forwardPage(Page.EDIT_ACCOUNT);
			}
		} else if (stepNum == CONFIRM_STEP) {
			String button = fp.getString(INPUT_CONFIRM_BUTTON);

			if (button.equals(resword.getString("back"))) {
				loadPresetValuesFromForm(fp);
				fp.addPresetValue(ARG_STEPNUM, EDIT_STEP);

				addEntityList("userTypes", getUserTypes(),
						respage.getString("the_user_could_not_be_edited_because_no_user_types"), Page.ADMIN_SYSTEM);
				setPresetValues(fp.getPresetValues());
				request.setAttribute("userName", user.getName());
				forwardPage(Page.EDIT_ACCOUNT);
			} else if (button.equals(resword.getString("submit"))) {
				user.setFirstName(fp.getString(INPUT_FIRST_NAME));
				user.setLastName(fp.getString(INPUT_LAST_NAME));
				user.setEmail(fp.getString(INPUT_EMAIL));
				user.setInstitutionalAffiliation(fp.getString(INPUT_INSTITUTION));
				user.setUpdater(ub);
				user.setRunWebservices(fp.getBoolean(INPUT_RUN_WEBSERVICES));

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
					SecurityManager sm = ((SecurityManager) SpringServletAccess.getApplicationContext(context).getBean(
							"securityManager"));
					String password = sm.genPassword();
					String passwordHash = sm.encrytPassword(password, getUserDetails());

					user.setPasswd(passwordHash);
					user.setPasswdTimestamp(null);

					udao.update(user);
                    udao.update(user);
                    if (ub.getId() == user.getId()) {
                        session.setAttribute("reloadUserBean", true);
                    }
					if ("no".equalsIgnoreCase(fp.getString(INPUT_DISPLAY_PWD))) {
						logger.info("displayPwd is no");
						try {
							sendResetPasswordEmail(user, password);
						} catch (Exception e) {
							addPageMessage(respage.getString("there_was_an_error_sending_reset_email_try_reset"));
						}
					} else {
						addPageMessage(respage.getString("new_user_password") + ":<br/> " + password + "<br/>"
								+ respage.getString("please_write_down_the_password_and_provide"));
					}
				} else {
					udao.update(user);
                    if (ub.getId() == user.getId()) {
                        session.setAttribute("reloadUserBean", true);
                    }
				}
				updateCalendarEmailJob(user);
				addPageMessage(respage.getString("the_user_account") + " \"" + user.getName() + "\" "
						+ respage.getString("was_updated_succesfully"));
				forwardPage(Page.LIST_USER_ACCOUNTS_SERVLET);
			} else {
				throw new InconsistentStateException(Page.ADMIN_SYSTEM,
						resexception.getString("an_invalid_submit_button_was_clicked"));
			}
		} else {
			throw new InconsistentStateException(Page.ADMIN_SYSTEM,
					resexception.getString("an_invalid_step_was_specified"));
		}
	}

	// public void processRequest(HttpServletRequest request,
	// HttpServletResponse
	// response)
	// throws OpenClinicaException {
	// session = request.getSession();
	// session.setMaxInactiveInterval(60 * 60 * 3);
	// logger.setLevel(Level.ALL);
	// UserAccountBean ub = (UserAccountBean) session.getAttribute("userBean");
	// try {
	// String userName = request.getRemoteUser();
	//
	// sm = new SessionManager(ub, userName);
	// ub = sm.getUserBean();
	// if (logger.isLoggable(Level.INFO)) {
	// logger.info("user bean from DB" + ub.getName());
	// }
	//
	// SQLFactory factory = SQLFactory.getInstance();
	// UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
	//
	// HashMap presetValues;
	// } catch (Exception e) {
	// e.printStackTrace();
	// logger.warn("OpenClinicaException::
	// OpenClinica.control.editUserAccount:
	// " + e.getMessage());
	//
	// forwardPage(Page.ERROR, request, response);
	// }
	// }

	private void loadPresetValuesFromBean(FormProcessor fp, UserAccountBean user) {
		fp.addPresetValue(INPUT_FIRST_NAME, user.getFirstName());
		fp.addPresetValue(INPUT_LAST_NAME, user.getLastName());
		fp.addPresetValue(INPUT_EMAIL, user.getEmail());
		fp.addPresetValue(INPUT_INSTITUTION, user.getInstitutionalAffiliation());
		int userTypeId = UserType.USER.getId();
		if (user.isTechAdmin()) {
			userTypeId = UserType.TECHADMIN.getId();
		} else if (user.isSysAdmin()) {
			userTypeId = UserType.SYSADMIN.getId();
		}
		// int userTypeId = user.isSysAdmin() ? UserType.SYSADMIN.getId() :
		// UserType.USER.getId();
		fp.addPresetValue(INPUT_USER_TYPE, userTypeId);
		fp.addPresetValue(ARG_USERID, user.getId());
		fp.addPresetValue(INPUT_RUN_WEBSERVICES, user.getRunWebservices() == true ? 1 : 0);

		String sendPwd = SQLInitServlet.getField("user_account_notification");
		fp.addPresetValue(USER_ACCOUNT_NOTIFICATION, sendPwd);
	}

	private void loadPresetValuesFromForm(FormProcessor fp) {
		fp.clearPresetValues();

		String textFields[] = { ARG_USERID, INPUT_FIRST_NAME, INPUT_LAST_NAME, INPUT_EMAIL, INPUT_INSTITUTION,
				INPUT_DISPLAY_PWD };
		fp.setCurrentStringValuesAsPreset(textFields);

		String ddlbFields[] = { INPUT_USER_TYPE, INPUT_RESET_PASSWORD, INPUT_RUN_WEBSERVICES };
		fp.setCurrentIntValuesAsPreset(ddlbFields);

		// String chkFields[] = { };
		// fp.setCurrentBoolValuesAsPreset(chkFields);
	}

	private ArrayList getUserTypes() {

		ArrayList types = UserType.toArrayList();
		types.remove(UserType.INVALID);
		types.remove(UserType.TECHADMIN);
		// Ticket #229
		// if (!ub.isTechAdmin()) {
		// types.remove(UserType.TECHADMIN);
		// }
		return types;
	}

	private void sendResetPasswordEmail(UserAccountBean user, String password) throws Exception {
		logger.info("Sending password reset notification to " + user.getName());

		String body = resword.getString("dear") + " " + user.getFirstName() + " " + user.getLastName() + ",<br/><br/>\n\n";
		body += restext.getString("your_password_has_been_reset_on_openclinica") + ":<br/><br/>\n\n";
		body += resword.getString("user_name") + ": " + user.getName() + "<br/>\n";
		body += resword.getString("password") + ": " + password + "<br/><br/>\n\n";
		body += restext.getString("please_test_your_login_information_and_let") + "<br/>\n";
		body += "<a href='" + SQLInitServlet.getSystemURL() + "'>" + SQLInitServlet.getField("sysURL") + "</a><br/><br/>\n\n";
		StudyDAO sdao = new StudyDAO(sm.getDataSource());
		StudyBean emailParentStudy = new StudyBean();
		if (currentStudy.getParentStudyId() > 0) {
			emailParentStudy = (StudyBean) sdao.findByPK(currentStudy.getParentStudyId());
		} else {
			emailParentStudy = currentStudy;
		}
		body += respage.getString("best_system_administrator").replace("{0}", emailParentStudy.getName());
		sendEmail(user.getEmail().trim(), restext.getString("your_openclinica_account_password_reset"), body, false);
	}

	@Override
	protected String getAdminServlet() {
		return SecureController.ADMIN_SERVLET_CODE;
	}
	
	@SuppressWarnings("null")
	private void updateCalendarEmailJob (UserAccountBean uaBean) {
		String triggerGroup = "CALENDAR";
		scheduler = getScheduler();
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
	
	private StdScheduler getScheduler() {
		scheduler = this.scheduler != null ? scheduler : (StdScheduler) SpringServletAccess.getApplicationContext(
				context).getBean("schedulerFactoryBean");
		return scheduler;
	}
}
