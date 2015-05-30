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
import java.util.Arrays;
import java.util.HashMap;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.dao.hibernate.ConfigurationDao;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;

import com.clinovo.util.ValidatorHelper;

/**
 * EventDefinitionValidator.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class EventDefinitionValidator {

	public static final int INT_3 = 3;
	public static final int INT_2000 = 2000;

	public static final String COMMON = "common";
	public static final String SCHEDULED = "scheduled";
	public static final String UNSCHEDULED = "unscheduled";
	public static final String CALENDARED_VISIT = "calendared_visit";

	private static String name(String name, boolean lowerCaseParameterNames) {
		return lowerCaseParameterNames && name != null ? name.toLowerCase() : name;
	}

	/**
	 * Method perform validation.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param configurationDao
	 *            ConfigurationDao
	 * @param studyUserRoleBeanList
	 *            List of StudyUserRoleBeans
	 * @return HashMap
	 */
	public static HashMap validate(HttpServletRequest request, ConfigurationDao configurationDao,
			ArrayList<StudyUserRoleBean> studyUserRoleBeanList) {
		return validate(request, configurationDao, studyUserRoleBeanList, false);
	}

	/**
	 * Method perform validation.
	 *
	 * @param request
	 *            HttpServletRequest
	 * @param configurationDao
	 *            ConfigurationDao
	 * @param studyUserRoleBeanList
	 *            List of StudyUserRoleBeans
	 * @param lowerCaseParameterNames
	 *            boolean
	 * @return HashMap
	 */
	public static HashMap validate(HttpServletRequest request, ConfigurationDao configurationDao,
			ArrayList<StudyUserRoleBean> studyUserRoleBeanList, boolean lowerCaseParameterNames) {
		HashMap errors = new HashMap();
		FormProcessor fp = new FormProcessor(request);
		ResourceBundle resexception = ResourceBundleProvider.getExceptionsBundle();
		Validator validator = new Validator(new ValidatorHelper(request, configurationDao));

		String type = fp.getString("type");
		if (!Arrays.asList(SCHEDULED, UNSCHEDULED, COMMON, CALENDARED_VISIT).contains(type)) {
			ArrayList errorMessages = new ArrayList();
			errorMessages.add(resexception.getString("rest.studyEventDefinition.wrongType"));
			errors.put("type", errorMessages);
			return errors;
		}

		validator.addValidation(name("name", lowerCaseParameterNames), Validator.NO_BLANKS);
		validator.addValidation(name("type", lowerCaseParameterNames), Validator.NO_BLANKS);
		validator.addValidation(name("name", lowerCaseParameterNames), Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, INT_2000);
		validator.addValidation(name("description", lowerCaseParameterNames), Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, INT_2000);
		validator.addValidation(name("category", lowerCaseParameterNames), Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, INT_2000);

		String calendaredVisitType = fp.getString(name("type", lowerCaseParameterNames));
		if ("calendared_visit".equalsIgnoreCase(calendaredVisitType)) {
			String isReference = fp.getString(name("isReference", lowerCaseParameterNames));
			validator.addValidation(name("maxDay", lowerCaseParameterNames), Validator.IS_REQUIRED);
			validator.addValidation(name("maxDay", lowerCaseParameterNames), Validator.IS_A_FLOAT,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, INT_3);
			validator.addValidation(name("minDay", lowerCaseParameterNames), Validator.IS_REQUIRED);
			validator.addValidation(name("minDay", lowerCaseParameterNames), Validator.IS_A_FLOAT,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, INT_3);
			validator.addValidation(name("schDay", lowerCaseParameterNames), Validator.IS_REQUIRED);
			validator.addValidation(name("schDay", lowerCaseParameterNames), Validator.IS_A_FLOAT,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, INT_3);
			if ("".equalsIgnoreCase(isReference)) {
				validator.addValidation(name("emailUser", lowerCaseParameterNames), Validator.NO_BLANKS);
			}
			validator.addValidation(name("emailDay", lowerCaseParameterNames), Validator.IS_REQUIRED);
			validator.addValidation(name("emailDay", lowerCaseParameterNames), Validator.IS_A_FLOAT,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, INT_3);

			request.getSession().setAttribute("changedReference", "true".equalsIgnoreCase(isReference));
			request.getSession().setAttribute("showCalendaredVisitBox", true);
			request.getSession().setAttribute("maxDay", fp.getString(name("maxDay", lowerCaseParameterNames)));
			request.getSession().setAttribute("minDay", fp.getString(name("minDay", lowerCaseParameterNames)));
			request.getSession().setAttribute("schDay", fp.getString(name("schDay", lowerCaseParameterNames)));
			request.getSession().setAttribute("emailUser", fp.getString(name("emailUser", lowerCaseParameterNames)));
			request.getSession().setAttribute("emailDay", fp.getString(name("emailDay", lowerCaseParameterNames)));
			request.getSession()
					.setAttribute("isReference", fp.getString(name("isReference", lowerCaseParameterNames)));
		}

		errors = validator.validate();

		int minDay = fp.getInt(name("minDay", lowerCaseParameterNames));
		int maxDay = fp.getInt(name("maxDay", lowerCaseParameterNames));
		int schDay = fp.getInt(name("schDay", lowerCaseParameterNames));
		int emailDay = fp.getInt(name("emailDay", lowerCaseParameterNames));
		String emailUser = fp.getString(name("emailUser", lowerCaseParameterNames));

		if (!(maxDay >= schDay)) {
			Validator.addError(errors, name("maxDay", lowerCaseParameterNames),
					resexception.getString("daymax_greate_or_equal_dayschedule"));
		}
		if (!(minDay <= schDay)) {
			Validator.addError(errors, name("minDay", lowerCaseParameterNames),
					resexception.getString("daymin_less_or_equal_dayschedule"));
		}
		if (!(minDay <= maxDay)) {
			Validator.addError(errors, name("minDay", lowerCaseParameterNames),
					resexception.getString("daymin_less_or_equal_daymax"));
		}
		if (!(emailDay <= schDay)) {
			Validator.addError(errors, name("emailDay", lowerCaseParameterNames),
					resexception.getString("dayemail_less_or_equal_dayschedule"));
		}
		if (!emailUser.equals("root") && !checkUserName(studyUserRoleBeanList, emailUser)
				&& "calendared_visit".equalsIgnoreCase(calendaredVisitType)
				&& !"true".equalsIgnoreCase(fp.getString(name("isReference", lowerCaseParameterNames)))) {
			Validator.addError(errors, name("emailUser", lowerCaseParameterNames),
					resexception.getString("this_user_name_does_not_exist"));
		}

		return errors;
	}

	private static boolean checkUserName(ArrayList<StudyUserRoleBean> studyUserRoleBeanList, String emailUser) {
		boolean isValid = false;
		for (StudyUserRoleBean studyUserRoleBean : studyUserRoleBeanList) {
			if (emailUser.equals(studyUserRoleBean.getUserName())) {
				isValid = true;
				break;
			} else {
				isValid = false;
			}
		}
		return isValid;
	}
}
