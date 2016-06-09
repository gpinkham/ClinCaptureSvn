/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 *
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clincapture.com/contact for pricing information.
 *
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use.
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO'S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.controller;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import com.clinovo.bean.EmailDetails;
import com.clinovo.enums.EmailAction;
import com.clinovo.service.EmailService;
import com.clinovo.util.RequestUtil;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.SpringController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.web.SQLInitServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.service.UserAccountService;
import com.clinovo.util.DateUtil;
import com.clinovo.util.EmailUtil;
import com.clinovo.util.PageMessagesUtil;
import com.clinovo.validator.UserValidator;

/**
 * Edit User Account Controller class.
 */
@Controller
@EnableAsync
@RequestMapping("/EditUserAccount")
@SuppressWarnings("rawtypes")
public class EditUserAccountController extends SpringController {

	public static final Logger LOGGER = LoggerFactory.getLogger(EditUserAccountController.class);

	@Autowired
	private EmailService mailer;

	@Autowired
	private DataSource dataSource;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private UserAccountService userAccountService;

	public static final String INPUT_FIRST_NAME = "firstName";
	public static final String INPUT_LAST_NAME = "lastName";
	public static final String INPUT_EMAIL = "email";
	public static final String INPUT_PHONE = "phone";
	public static final String INPUT_COMPANY = "company";
	public static final String INPUT_RESET_PASSWORD = "resetPassword";
	public static final String INPUT_USER_TYPE = "userType";
	public static final String INPUT_DISPLAY_PASSWORD = "displayPassword";
	public static final String PATH = "EditUserAccount";
	public static final String ARG_USERID = "userId";
	public static final String INPUT_ALLOW_SOAP = "allowSoap";
	public static final String USER_ACCOUNT_NOTIFICATION = "notifyPassword";
	public static final String EMAIL = "contactEmail";
	public static final String USER_ID = "user_id";

	/**
	 * Get link to the current page.
	 *
	 * @param userId
	 *            int.
	 * @return String
	 */
	public static String getLink(int userId) {
		return PATH + '?' + ARG_USERID + '=' + userId;
	}

	/**
	 * Main method that is launched on page initialization.
	 *
	 * @param request
	 *            HttpServletRequest
	 * @param model
	 *            Model
	 * @return String page
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String mainGet(HttpServletRequest request, Model model) {
		String page = "";
		if (!isAdmin(request)) {
			page = "redirect:/MainMenu?message=system_no_permission";
		} else {
			restorePageMessages(request);
			FormProcessor fp = new FormProcessor(request);
			// because we need to use this in the confirmation and error parts too
			ArrayList studies = getAllStudies();
			model.addAttribute("studies", studies);

			int userId = fp.getInt(ARG_USERID);
			UserAccountDAO userAccountDAO = new UserAccountDAO(dataSource);
			UserAccountBean user = (UserAccountBean) userAccountDAO.findByPK(userId);
			model.addAttribute("editedUser", user);
			model.addAttribute("isSiteLevelUser", userAccountService.isSiteLevelUser(user));
			if (!fp.isSubmitted()) {
				model.addAttribute(TIME_ZONE_IDS_SORTED_REQUEST_ATR, DateUtil.getAvailableTimeZoneIDsSorted());
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
	 * @param request
	 *            HttpServletRequest
	 * @param model
	 *            Model
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
			UserAccountDAO userAccountDao = new UserAccountDAO(dataSource);
			UserAccountBean user = (UserAccountBean) userAccountDao.findByPK(userId);

			HashMap errors = UserValidator.validateUserEdit(getConfigurationDao());

			if (errors.isEmpty()) {
				loadPresetValuesFromForm(fp);
				model.addAttribute("presetValues", fp.getPresetValues());
				model.addAttribute("userName", user.getName());
				page = "admin/edituseraccountconfirm";
			} else {
				Locale locale = LocaleResolver.getLocale(request);
				loadPresetValuesFromForm(fp);
				setInputMessages(errors, request);
				model.addAttribute("presetValues", fp.getPresetValues());
				model.addAttribute("userName", user.getName());
				model.addAttribute("userTypes", getUserTypes());
				model.addAttribute(TIME_ZONE_IDS_SORTED_REQUEST_ATR, DateUtil.getAvailableTimeZoneIDsSorted());
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
	 * @param request
	 *            HttpServletRequest
	 * @return String name of the page
	 * @throws NoSuchAlgorithmException
	 *             in case if algorithm not exists
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
			UserAccountDAO userAccountDao = new UserAccountDAO(dataSource);
			UserAccountBean user = (UserAccountBean) userAccountDao.findByPK(userId);
			boolean wasSysAdmin = user.isSysAdmin();
			updateMainFieldsForEditedUser(user, fp, request);
			updateCalendarEmailJob(user, LOGGER);
			Locale locale = LocaleResolver.getLocale(request);
			PageMessagesUtil.addPageMessage(request,
					messageSource.getMessage("the_user_account", null, locale) + " \"" + user.getName() + "\" "
							+ messageSource.getMessage("was_updated_succesfully", null, locale));
			if (ub.getId() == user.getId()) {
				request.getSession().setAttribute("reloadUserBean", true);
				if (wasSysAdmin && !user.isSysAdmin()) {
					page = "redirect:/MainMenu";
					PageMessagesUtil.addPageMessage(request,
							messageSource.getMessage("you_may_not_perform_administrative_functions", null, locale));
				}
			}
		}
		storePageMessages(request);
		return page;
	}

	/**
	 * Method that is used to implement back button function.
	 *
	 * @param request
	 *            HttpServletRequest
	 * @param model
	 *            Model
	 * @return String name of the page.
	 */
	@RequestMapping(method = RequestMethod.POST, params = "submit_and_restore")
	public String restoreAfterMasking(HttpServletRequest request, Model model) {
		String page = "";
		if (!isAdmin(request)) {
			page = "redirect:/MainMenu?message=system_no_permission";
		} else {
			FormProcessor fp = new FormProcessor(request);
			int userId = fp.getInt(ARG_USERID);
			UserAccountDAO userAccountDao = new UserAccountDAO(dataSource);
			UserAccountBean user = (UserAccountBean) userAccountDao.findByPK(userId);
			loadPresetValuesFromForm(fp);
			model.addAttribute("presetValues", fp.getPresetValues());
			model.addAttribute("userTypes", getUserTypes());
			model.addAttribute("userName", user.getName());
			model.addAttribute(TIME_ZONE_IDS_SORTED_REQUEST_ATR, DateUtil.getAvailableTimeZoneIDsSorted());
		}
		return page;
	}

	private void loadPresetValuesFromBean(FormProcessor fp, UserAccountBean user) {
		fp.addPresetValue(INPUT_FIRST_NAME, user.getFirstName());
		fp.addPresetValue(INPUT_LAST_NAME, user.getLastName());
		fp.addPresetValue(INPUT_EMAIL, user.getEmail());
		fp.addPresetValue(INPUT_PHONE, user.getPhone());
		fp.addPresetValue(INPUT_COMPANY, user.getInstitutionalAffiliation());
		int userTypeId = UserType.USER.getId();
		if (user.isTechAdmin()) {
			userTypeId = UserType.TECHADMIN.getId();
		} else if (user.isSysAdmin()) {
			userTypeId = UserType.SYSADMIN.getId();
		}
		fp.addPresetValue(INPUT_USER_TYPE, userTypeId);
		fp.addPresetValue(ARG_USERID, user.getId());
		fp.addPresetValue(INPUT_ALLOW_SOAP, user.getRunWebservices());
		fp.addPresetValue(INPUT_TIME_ZONE, user.getUserTimeZoneId());

		String sendPwd = SQLInitServlet.getField("user_account_notification");
		fp.addPresetValue(USER_ACCOUNT_NOTIFICATION, sendPwd);
	}

	private void loadPresetValuesFromForm(FormProcessor fp) {
		fp.clearPresetValues();

		String[] textFields = {ARG_USERID, INPUT_FIRST_NAME, INPUT_LAST_NAME, INPUT_PHONE, INPUT_EMAIL, INPUT_COMPANY,
				INPUT_DISPLAY_PASSWORD, INPUT_ALLOW_SOAP, INPUT_TIME_ZONE};
		fp.setCurrentStringValuesAsPreset(textFields);

		String[] ddlbFields = {INPUT_USER_TYPE, INPUT_RESET_PASSWORD};
		fp.setCurrentIntValuesAsPreset(ddlbFields);
	}

	private ArrayList getUserTypes() {

		ArrayList types = UserType.toArrayList();
		types.remove(UserType.INVALID);
		types.remove(UserType.TECHADMIN);

		return types;
	}

	private void sendResetPasswordEmail(HttpServletRequest request, UserAccountBean user, String password) {

		StudyBean currentStudy = getCurrentStudy(request);
		UserAccountBean currentUser = RequestUtil.getUserAccountBean();
		LOGGER.info("Sending password reset notification to " + user.getName());
		Locale locale = LocaleResolver.getLocale(request);
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

		EmailDetails emailDetails = new EmailDetails();
		emailDetails.setStudyId(emailParentStudy.getId());
		emailDetails.setAction(EmailAction.RESET_PASSWORD);
		emailDetails.setTo(user.getEmail());
		emailDetails.setMessage(body);
		emailDetails.setSubject(messageSource.getMessage("your_openclinica_account_password_reset", null, locale));
		emailDetails.setSentBy(currentUser.getId());

		try {
			mailer.sendEmail(emailDetails);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void updateMainFieldsForEditedUser(UserAccountBean user, FormProcessor fp, HttpServletRequest request) {
		UserAccountDAO userAccountDao = new UserAccountDAO(dataSource);
		UserAccountBean ub = getUserAccountBean(request);
		user.setFirstName(fp.getString(INPUT_FIRST_NAME));
		user.setLastName(fp.getString(INPUT_LAST_NAME));
		user.setEmail(fp.getString(INPUT_EMAIL));
		user.setPhone(fp.getString(INPUT_PHONE));
		user.setInstitutionalAffiliation(fp.getString(INPUT_COMPANY));
		user.setUpdater(ub);
		user.setRunWebservices(fp.getString(INPUT_ALLOW_SOAP).equalsIgnoreCase("true"));
		user.setUserTimeZoneId(fp.getString(INPUT_TIME_ZONE));
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
			userAccountDao.update(user);

			Locale locale = LocaleResolver.getLocale(request);

			if (!"true".equalsIgnoreCase(fp.getString(INPUT_DISPLAY_PASSWORD))) {
				LOGGER.info("displayPwd is no");
				sendResetPasswordEmail(request, user, password);
			} else {
				PageMessagesUtil.addPageMessage(request,
						messageSource.getMessage("new_user_password", null, locale) + ":<br/> " + password + "<br/>"
								+ messageSource.getMessage("please_write_down_the_password_and_provide", null, locale));
			}
		} else {
			userAccountDao.update(user);
		}
	}

	private boolean isAdmin(HttpServletRequest request) {
		UserAccountBean user = (UserAccountBean) request.getSession().getAttribute("userBean");
		return user.isSysAdmin();
	}
}
