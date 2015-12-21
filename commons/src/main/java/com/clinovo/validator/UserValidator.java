/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 *
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 *
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use.
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO'S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/

package com.clinovo.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.TermType;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.dao.hibernate.ConfigurationDao;
import org.akaza.openclinica.dao.hibernate.PasswordRequirementsDao;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.util.RequestUtil;
import com.clinovo.util.ValidatorHelper;

/**
 * UserValidator.
 */
@SuppressWarnings("rawtypes")
public final class UserValidator {

	public static final int FIFTY = 50;
	public static final int NAMES_LENGTH = 50;
	public static final int EMAIL_LENGTH = 120;
	public static final int USERNAME_LENGTH = 64;
	public static final int INPUT_COMPANY_LENGTH = 255;

	public static final String ROLE = "role";

	public static final String INPUT_ROLE = "role";
	public static final String INPUT_EMAIL = "email";
	public static final String INPUT_PHONE = "phone";
	public static final String INPUT_PASSWD = "passwd";
	public static final String INPUT_COMPANY = "company";
	public static final String INPUT_PASSWD_1 = "passwd1";
	public static final String INPUT_USERNAME = "userName";
	public static final String INPUT_LAST_NAME = "lastName";
	public static final String INPUT_OLD_PASSWD = "oldPasswd";
	public static final String INPUT_FIRST_NAME = "firstName";
	public static final String INPUT_ACTIVE_STUDY = "activeStudy";
	public static final String INPUT_PASSWD_CHALLENGE_ANSWER = "passwdChallengeAnswer";
	public static final String INPUT_PASSWD_CHALLENGE_QUESTION = "passwdChallengeQuestion";

	private UserValidator() {
	}

	private static void checkRoleConsistency(Role role, StudyBean studyBean, HashMap errors) {
		ResourceBundle exceptionsBundle = ResourceBundleProvider.getExceptionsBundle(LocaleResolver.getLocale());
		if (studyBean.getParentStudyId() == 0 && (role.getCode().equals(Role.CLINICAL_RESEARCH_COORDINATOR.getCode())
				|| role.getCode().equals(Role.INVESTIGATOR.getCode())
				|| role.getCode().equals(Role.SITE_MONITOR.getCode()))) {
			Validator.addError(errors, ROLE, exceptionsBundle.getString("itsForbiddenToAssignSiteLevelRoleToStudy"));
		} else
			if (studyBean.getParentStudyId() > 0 && (role.getCode().equals(Role.STUDY_ADMINISTRATOR.getCode())
					|| role.getCode().equals(Role.STUDY_DIRECTOR.getCode())
					|| role.getCode().equals(Role.STUDY_MONITOR.getCode())
					|| role.getCode().equals(Role.STUDY_CODER.getCode())
					|| role.getCode().equals(Role.STUDY_EVALUATOR.getCode()))) {
			Validator.addError(errors, ROLE, exceptionsBundle.getString("itsForbiddenToAssignStudyLevelRoleToSite"));
		}
	}

	/**
	 * Method perform validation for user create (this method skips validation for activeStudy parameter).
	 *
	 * @param configurationDao
	 *            ConfigurationDao
	 * @param userAccountDao
	 *            UserAccountDAO
	 * @param studyBean
	 *            StudyBean
	 * @return HashMap
	 */
	public static HashMap validateUserCreate(ConfigurationDao configurationDao, UserAccountDAO userAccountDao,
			StudyBean studyBean) {
		return validateUserCreate(configurationDao, userAccountDao, null, studyBean);
	}

	/**
	 * Method perform validation for user create.
	 *
	 * @param configurationDao
	 *            ConfigurationDao
	 * @param userAccountDao
	 *            UserAccountDAO
	 * @param studyDao
	 *            StudyDAO
	 * @param studyBean
	 *            StudyBean
	 * @return HashMap
	 */
	public static HashMap validateUserCreate(ConfigurationDao configurationDao, UserAccountDAO userAccountDao,
			StudyDAO studyDao, StudyBean studyBean) {
		HttpServletRequest request = RequestUtil.getRequest();
		FormProcessor fp = new FormProcessor(request);

		Validator validator = new Validator(new ValidatorHelper(request, configurationDao));

		validator.addValidation(INPUT_USERNAME, Validator.NO_BLANKS);
		validator.addValidation(INPUT_USERNAME, Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, USERNAME_LENGTH);
		validator.addValidation(INPUT_USERNAME, Validator.IS_A_USERNAME);
		validator.addValidation(INPUT_USERNAME, Validator.USERNAME_UNIQUE, userAccountDao);
		validator.addValidation(INPUT_FIRST_NAME, Validator.NO_BLANKS);
		validator.addValidation(INPUT_FIRST_NAME, Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, NAMES_LENGTH);
		validator.addValidation(INPUT_LAST_NAME, Validator.NO_BLANKS);
		validator.addValidation(INPUT_LAST_NAME, Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, NAMES_LENGTH);
		validator.addValidation(INPUT_EMAIL, Validator.NO_BLANKS);
		validator.addValidation(INPUT_EMAIL, Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, EMAIL_LENGTH);
		validator.addValidation(INPUT_EMAIL, Validator.IS_A_EMAIL);
		validator.addValidation(INPUT_COMPANY, Validator.NO_BLANKS);
		validator.addValidation(INPUT_COMPANY, Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, INPUT_COMPANY_LENGTH);
		if (studyDao != null) {
			validator.addValidation(INPUT_ACTIVE_STUDY, Validator.ENTITY_EXISTS, studyDao);
		}
		validator.addValidation(INPUT_ROLE, Validator.IS_VALID_TERM, TermType.ROLE);
		String phone = fp.getString(INPUT_PHONE);
		if (!phone.isEmpty()) {
			validator.addValidation(INPUT_PHONE, Validator.IS_A_PHONE_NUMBER);
		}

		HashMap errors = validator.validate();

		if (errors.isEmpty()) {
			checkRoleConsistency(Role.get(fp.getInt(INPUT_ROLE)), studyBean, errors);
		}

		return errors;
	}

	/**
	 * Method perform validation for user edit.
	 *
	 * @param configurationDao
	 *            ConfigurationDao
	 * @return HashMap
	 */
	public static HashMap validateUserEdit(ConfigurationDao configurationDao) {
		HttpServletRequest request = RequestUtil.getRequest();
		FormProcessor fp = new FormProcessor(request);

		Validator validator = new Validator(new ValidatorHelper(request, configurationDao));

		validator.addValidation(INPUT_FIRST_NAME, Validator.NO_BLANKS);
		validator.addValidation(INPUT_LAST_NAME, Validator.NO_BLANKS);
		validator.addValidation(INPUT_FIRST_NAME, Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, FIFTY);
		validator.addValidation(INPUT_LAST_NAME, Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, FIFTY);
		validator.addValidation(INPUT_EMAIL, Validator.NO_BLANKS);
		validator.addValidation(INPUT_EMAIL, Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, EMAIL_LENGTH);
		validator.addValidation(INPUT_EMAIL, Validator.IS_A_EMAIL);
		validator.addValidation(INPUT_COMPANY, Validator.NO_BLANKS);
		validator.addValidation(INPUT_COMPANY, Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, INPUT_COMPANY_LENGTH);
		String phone = fp.getString(INPUT_PHONE);
		if (!phone.isEmpty()) {
			validator.addValidation(INPUT_PHONE, Validator.IS_A_PHONE_NUMBER);
		}

		return validator.validate();
	}

	/**
	 * Method perform validation for update profile.
	 *
	 * @param configurationDao
	 *            ConfigurationDao
	 * @param userAccountDao
	 *            UserAccountDAO
	 * @param newDigestPass
	 *            String
	 * @param userAccountBean
	 *            UserAccountBean
	 * @param userDetails
	 *            UserDetails
	 * @param securityManager
	 *            org.akaza.openclinica.core.SecurityManager
	 * @throws Exception
	 *             an Exception
	 * @return HashMap
	 */
	public static HashMap validateUpdateProfile(ConfigurationDao configurationDao, UserAccountDAO userAccountDao,
			String newDigestPass, UserAccountBean userAccountBean, UserDetails userDetails,
			org.akaza.openclinica.core.SecurityManager securityManager) throws Exception {
		HttpServletRequest request = RequestUtil.getRequest();
		FormProcessor fp = new FormProcessor(request);

		Validator validator = new Validator(new ValidatorHelper(request, configurationDao));

		validator.addValidation(INPUT_FIRST_NAME, Validator.NO_BLANKS);
		validator.addValidation(INPUT_LAST_NAME, Validator.NO_BLANKS);
		validator.addValidation(INPUT_EMAIL, Validator.IS_A_EMAIL);
		validator.addValidation(INPUT_PASSWD_CHALLENGE_QUESTION, Validator.NO_BLANKS);
		validator.addValidation(INPUT_PASSWD_CHALLENGE_ANSWER, Validator.NO_BLANKS);
		validator.addValidation(INPUT_OLD_PASSWD, Validator.NO_BLANKS); // old password
		String phone = fp.getString(INPUT_PHONE);
		if (!phone.isEmpty()) {
			validator.addValidation(INPUT_PHONE, Validator.IS_A_PHONE_NUMBER);
		}

		String password = fp.getString(INPUT_PASSWD).trim();
		String oldPass = fp.getString(INPUT_OLD_PASSWD).trim();

		List<String> pwdErrors = new ArrayList<String>();

		if (!StringUtils.isBlank(password)) {
			validator.addValidation(INPUT_PASSWD, Validator.IS_A_PASSWORD); // new
																			// password
			validator.addValidation(INPUT_PASSWD_1, Validator.CHECK_SAME, INPUT_PASSWD); // confirm password

			Locale locale = LocaleResolver.getLocale(request);
			ResourceBundle resexception = ResourceBundleProvider.getExceptionsBundle(locale);
			PasswordRequirementsDao passwordRequirementsDao = new PasswordRequirementsDao(configurationDao);

			pwdErrors = PasswordValidator.validatePassword(passwordRequirementsDao, userAccountDao,
					userAccountBean.getId(), password, newDigestPass, resexception);
		}

		HashMap errors = validator.validate();
		for (String err : pwdErrors) {
			Validator.addError(errors, INPUT_PASSWD, err);
		}

		if (errors.isEmpty() && !securityManager.isPasswordValid(userAccountBean.getPasswd(), oldPass, userDetails)) {
			Validator.addError(errors, INPUT_OLD_PASSWD,
					ResourceBundleProvider.getExceptionsBundle().getString("wrong_old_password"));
		}

		return errors;
	}
}
