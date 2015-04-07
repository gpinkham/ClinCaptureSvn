package com.clinovo.controller;

import com.clinovo.controller.base.BaseController;
import com.clinovo.i18n.LocaleResolver;
import com.clinovo.service.EmailService;
import com.clinovo.util.EmailUtil;
import com.clinovo.util.PageMessagesUtil;
import com.clinovo.util.ValidatorHelper;

import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.web.SQLInitServlet;
import org.quartz.JobDataMap;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdScheduler;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

/**
 * Edit User Account Controller class.
 */
@Controller
@RequestMapping("/EditUserAccount")
@SuppressWarnings("rawtypes")
public class EditUserAccountController extends BaseController {

	@Autowired
	private EmailService mailer;

	private Locale locale;
	private UserAccountDAO userAccountDAO;

	public static final Logger LOGGER = LoggerFactory.getLogger(CRFEvaluationController.class);
	public static final String INPUT_FIRST_NAME = "firstName";
	public static final String INPUT_LAST_NAME = "lastName";
	public static final String INPUT_EMAIL = "email";
	public static final String INPUT_PHONE = "phone";
	public static final String INPUT_INSTITUTION = "institutionalAffiliation";
	public static final String INPUT_RESET_PASSWORD = "resetPassword";
	public static final String INPUT_USER_TYPE = "userType";
	public static final String INPUT_DISPLAY_PWD = "displayPwd";
	public static final String PATH = "EditUserAccount";
	public static final String ARG_USERID = "userId";
	public static final String INPUT_RUN_WEBSERVICES = "runWebServices";
	public static final String USER_ACCOUNT_NOTIFICATION = "notifyPassword";
	public static final String EMAIL = "contactEmail";
	public static final String USER_ID = "user_id";
	public static final int FIFTY = 50;
	public static final int ONE_H_TWENTY = 120;
	public static final int TWO_H_FIFTY_FIVE = 55;

	/**
	 * Get link to the current page.
	 *
	 * @param userId int.
	 * @return String
	 */
	public static String getLink(int userId) {
		return PATH + '?' + ARG_USERID + '=' + userId;
	}

	/**
	 * Main method that is launched on page initialization.
	 *
	 * @param request HttpServletRequest
	 * @param model   Model
	 * @return String page
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String mainGet(HttpServletRequest request, Model model) {
		String page = "";
		if (!isAdmin(request)) {
			page = "redirect:/MainMenu?message=system_no_permission";
		} else {
			locale = LocaleResolver.getLocale(request);
			FormProcessor fp = new FormProcessor(request);
			// because we need to use this in the confirmation and error parts too
			ArrayList studies = getAllStudies();
			model.addAttribute("studies", studies);

			int userId = fp.getInt(ARG_USERID);
			userAccountDAO = new UserAccountDAO(dataSource);
			UserAccountBean user = (UserAccountBean) userAccountDAO.findByPK(userId);
			model.addAttribute("editedUser", user);

			if (!fp.isSubmitted()) {
				model.addAttribute("userTypes", getUserTypes());
				loadPresetValuesFromBean(fp, user);
				model.addAttribute("presetValues", fp.getPresetValues());
				model.addAttribute("userName", user.getName());
				page = "admin/edituseraccount";
			}
		}
		return page;
	}

	/**
	 * Method that is used to initialize "Summary" page.
	 *
	 * @param request HttpServletRequest
	 * @param model   Model
	 * @return String page
	 */
	@RequestMapping(method = RequestMethod.POST, params = "continue")
	public String editUserAccountContinue(HttpServletRequest request, Model model) {
		String page;
		if (!isAdmin(request)) {
			page = "redirect:/MainMenu?message=system_no_permission";
		} else {
			FormProcessor fp = new FormProcessor(request);
			int userId = fp.getInt(ARG_USERID);
			userAccountDAO = new UserAccountDAO(dataSource);
			UserAccountBean user = (UserAccountBean) userAccountDAO.findByPK(userId);

			Validator v = new Validator(new ValidatorHelper(request, getConfigurationDao()));
			HashMap errors = validateEditPage(v);

			if (errors.isEmpty()) {
				loadPresetValuesFromForm(fp);
				model.addAttribute("presetValues", fp.getPresetValues());
				model.addAttribute("userName", user.getName());
				page = "admin/edituseraccountconfirm";
			} else {
				loadPresetValuesFromForm(fp);
				setInputMessages(errors, request);
				model.addAttribute("presetValues", fp.getPresetValues());
				model.addAttribute("userTypes", getUserTypes());
				PageMessagesUtil.addPageMessage(model,
						messageSource.getMessage("there_were_some_errors_submission", null, locale)
								+ messageSource.getMessage("see_below_for_details", null, locale));
				page = "admin/edituseraccount";
			}
		}
		return page;
	}

	/**
	 * Method that is used to save all changes.
	 *
	 * @param request HttpServletRequest
	 * @return String name of the page
	 * @throws NoSuchAlgorithmException in case if algorithm not exists
	 */
	@RequestMapping(method = RequestMethod.POST, params = "submit")
	public String editUserAccountSubmit(HttpServletRequest request) throws NoSuchAlgorithmException {
		String page;
		if (!isAdmin(request)) {
			page = "redirect:/MainMenu?message=system_no_permission";
		} else {
			page = "redirect:/ListUserAccounts";

			UserAccountBean ub = getUserAccountBean(request);
			FormProcessor fp = new FormProcessor(request);
			int userId = fp.getInt(ARG_USERID);
			userAccountDAO = new UserAccountDAO(dataSource);
			UserAccountBean user = (UserAccountBean) userAccountDAO.findByPK(userId);
			boolean wasSysAdmin = user.isSysAdmin();
			updateMainFieldsForEditedUser(user, fp, request);
			updateCalendarEmailJob(user);
			PageMessagesUtil.addPageMessage(request,
					messageSource.getMessage("the_user_account", null, locale) + " \"" + user.getName() + "\" "
							+ messageSource.getMessage("was_updated_succesfully", null, locale));
			if (ub.getId() == user.getId()) {
				request.getSession().setAttribute("reloadUserBean", true);
				if (wasSysAdmin && !user.isSysAdmin()) {
					page = "redirect:/MainMenu";
					PageMessagesUtil.addPageMessage(request,
							messageSource
									.getMessage("you_may_not_perform_administrative_functions", null, locale));
				}
			}
		}
		org.akaza.openclinica.control.core.Controller.storePageMessages(request);
		return page;
	}

	/**
	 * Method that is used to implement back button function.
	 *
	 * @param request HttpServletRequest
	 * @param model   Model
	 * @return String name of the page.
	 */
	@RequestMapping(method = RequestMethod.POST, params = "submit_and_restore")
	public String restoreAfterMasking(HttpServletRequest request, Model model) {
		String page = "";
		if (!isAdmin(request)) {
			page = "redirect:/MainMenu?message=system_no_permission";
		} else {
			locale = LocaleResolver.getLocale(request);
			FormProcessor fp = new FormProcessor(request);
			int userId = fp.getInt(ARG_USERID);
			userAccountDAO = new UserAccountDAO(dataSource);
			UserAccountBean user = (UserAccountBean) userAccountDAO.findByPK(userId);
			loadPresetValuesFromForm(fp);
			model.addAttribute("presetValues", fp.getPresetValues());
			model.addAttribute("userTypes", getUserTypes());
			model.addAttribute("userName", user.getName());
		}
		return page;
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

		String[] textFields = { ARG_USERID, INPUT_FIRST_NAME, INPUT_LAST_NAME, INPUT_PHONE, INPUT_EMAIL,
				INPUT_INSTITUTION, INPUT_DISPLAY_PWD };
		fp.setCurrentStringValuesAsPreset(textFields);

		String[] ddlbFields = { INPUT_USER_TYPE, INPUT_RESET_PASSWORD, INPUT_RUN_WEBSERVICES };
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

		LOGGER.info("Sending password reset notification to " + user.getName());

		String body = EmailUtil.getEmailBodyStart();
		body += messageSource.getMessage("dear", null, locale) + " " + user.getFirstName() + " " + user.getLastName()
				+ ",<br/><br/>\n\n";
		body += messageSource.getMessage("your_password_has_been_reset_on_openclinica", null, locale)
				+ ":<br/><br/>\n\n";
		body += messageSource.getMessage("user_name", null, locale) + ": " + user.getName() + "<br/>\n";
		body += messageSource.getMessage("password", null, locale) + ": " + password + "<br/><br/>\n\n";
		body += messageSource.getMessage("please_test_your_login_information_and_let", null, locale) + "<br/>\n";
		body += "<a href='" + SQLInitServlet.getSystemURL() + "'>" + SQLInitServlet.getField("sysURL")
				+ "</a><br/><br/>\n\n";
		StudyDAO sdao = new StudyDAO(dataSource);
		StudyBean emailParentStudy;
		if (currentStudy.getParentStudyId() > 0) {
			emailParentStudy = (StudyBean) sdao.findByPK(currentStudy.getParentStudyId());
		} else {
			emailParentStudy = currentStudy;
		}
		body += messageSource.getMessage("best_system_administrator", null, locale).replace("{0}",
				emailParentStudy.getName());
		body += EmailUtil.getEmailBodyEnd();
		body += EmailUtil.getEmailFooter(locale);
		mailer.sendEmail(user.getEmail().trim(),
				messageSource.getMessage("your_openclinica_account_password_reset", null, locale), body, false,
				request);
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
					LOGGER.info("contact email from calendared " + contactEmail + " for user userId " + userId);
					LOGGER.info("Old email " + dataMap.getString(EMAIL));
					if (uaBean.getId() == userId) {
						dataMap.put(EMAIL, uaBean.getEmail());
						JobDetailImpl jobDetailBean = new JobDetailImpl();
						jobDetailBean.setKey(trigger.getJobKey());
						jobDetailBean.setDescription(trigger.getDescription());
						jobDetailBean.setGroup(triggerGroup);
						jobDetailBean.setName(triggerKey.getName());
						jobDetailBean.setJobClass(org.akaza.openclinica.service.calendar.EmailStatefulJob.class);
						jobDetailBean.setJobDataMap(dataMap);
						LOGGER.info("New email " + dataMap.getString(EMAIL));
						jobDetailBean.setDurability(true);
						scheduler.addJob(jobDetailBean, true);
					}
				}
			}
		} catch (SchedulerException e) {
			LOGGER.error(e.getMessage());
			e.printStackTrace();
		}
	}

	private UserAccountBean getUserAccountBean(HttpServletRequest request) {
		return (UserAccountBean) request.getSession().getAttribute("userBean");
	}

	/**
	 * This method checks if any field on Edit page contains incorrect data.
	 *
	 * @param v Validator
	 * @return HashMap of errors
	 */
	private HashMap validateEditPage(Validator v) {
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
		return v.validate();
	}

	private void updateMainFieldsForEditedUser(UserAccountBean user, FormProcessor fp, HttpServletRequest request) {
		UserAccountBean ub = getUserAccountBean(request);
		user.setFirstName(fp.getString(INPUT_FIRST_NAME));
		user.setLastName(fp.getString(INPUT_LAST_NAME));
		user.setEmail(fp.getString(INPUT_EMAIL));
		user.setPhone(fp.getString(INPUT_PHONE));
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
			org.akaza.openclinica.core.SecurityManager sm = getSecurityManager();
			String password = sm.genPassword();
			String passwordHash = null;
			try {
				passwordHash = sm.encryptPassword(password, getUserDetails());
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			user.setPasswd(passwordHash);
			user.setPasswdTimestamp(null);
			userAccountDAO.update(user);

			if ("no".equalsIgnoreCase(fp.getString(INPUT_DISPLAY_PWD))) {
				LOGGER.info("displayPwd is no");
				try {
					sendResetPasswordEmail(request, user, password);
				} catch (Exception e) {
					PageMessagesUtil.addPageMessage(request, messageSource.getMessage(
							"there_was_an_error_sending_reset_email_try_reset", null, locale));
				}
			} else {
				PageMessagesUtil.addPageMessage(request,
						messageSource.getMessage("new_user_password", null, locale) + ":<br/> " + password
								+ "<br/>"
								+ messageSource
								.getMessage("please_write_down_the_password_and_provide", null,
										locale));
			}
		} else {
			userAccountDAO.update(user);
		}
	}

	private StudyBean getCurrentStudy(HttpServletRequest request) {
		return (StudyBean) request.getSession().getAttribute("study");
	}

	private boolean isAdmin(HttpServletRequest request) {
		UserAccountBean user = (UserAccountBean) request.getSession().getAttribute("userBean");
		return user.isSysAdmin();
	}
}
