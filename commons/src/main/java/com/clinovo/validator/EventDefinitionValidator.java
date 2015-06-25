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
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.dao.hibernate.ConfigurationDao;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.springframework.context.MessageSource;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.util.EmailUtil;
import com.clinovo.util.ValidatorHelper;

/**
 * EventDefinitionValidator.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public final class EventDefinitionValidator {

	public static final int INT_3 = 3;
	public static final int INT_2000 = 2000;

	public static final String COMMON = "common";
	public static final String SCHEDULED = "scheduled";
	public static final String UNSCHEDULED = "unscheduled";
	public static final String CALENDARED_VISIT = "calendared_visit";

	private EventDefinitionValidator() {
	}

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

	private static ArrayList getErrorMessages(String code) {
		ResourceBundle resexception = ResourceBundleProvider.getExceptionsBundle();
		ArrayList errorMessages = new ArrayList();
		errorMessages.add(resexception.getString(code));
		return errorMessages;
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
		FormProcessor fp = new FormProcessor(request);
		ResourceBundle resexception = ResourceBundleProvider.getExceptionsBundle();
		Validator validator = new Validator(new ValidatorHelper(request, configurationDao));

		validator.addValidation(name("name", lowerCaseParameterNames), Validator.NO_BLANKS);
		validator.addValidation(name("type", lowerCaseParameterNames), Validator.NO_BLANKS);
		validator.addValidation(name("name", lowerCaseParameterNames), Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, INT_2000);
		validator.addValidation(name("description", lowerCaseParameterNames), Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, INT_2000);
		validator.addValidation(name("category", lowerCaseParameterNames), Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, INT_2000);

		String isReference = fp.getString(name("isReference", lowerCaseParameterNames));
		String repeating = fp.getString(name("repeating", lowerCaseParameterNames));
		String type = fp.getString(name("type", lowerCaseParameterNames));

		if (CALENDARED_VISIT.equals(type)) {
			validator.addValidation(name("maxDay", lowerCaseParameterNames), Validator.IS_REQUIRED);
			validator.addValidation(name("maxDay", lowerCaseParameterNames), Validator.IS_A_FLOAT,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, INT_3);
			validator.addValidation(name("minDay", lowerCaseParameterNames), Validator.IS_REQUIRED);
			validator.addValidation(name("minDay", lowerCaseParameterNames), Validator.IS_A_FLOAT,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, INT_3);
			validator.addValidation(name("schDay", lowerCaseParameterNames), Validator.IS_REQUIRED);
			validator.addValidation(name("schDay", lowerCaseParameterNames), Validator.IS_A_FLOAT,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, INT_3);
			validator.addValidation(name("emailDay", lowerCaseParameterNames), Validator.IS_REQUIRED);
			validator.addValidation(name("emailDay", lowerCaseParameterNames), Validator.IS_A_FLOAT,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, INT_3);

			if (!isReference.equalsIgnoreCase("true")) {
				validator.addValidation(name("emailUser", lowerCaseParameterNames), Validator.NO_BLANKS);
			}

			request.getSession().setAttribute("changedReference", "true".equalsIgnoreCase(isReference));
			request.getSession().setAttribute("showCalendaredVisitBox", true);
			request.getSession().setAttribute("maxDay", fp.getString(name("maxDay", lowerCaseParameterNames)));
			request.getSession().setAttribute("minDay", fp.getString(name("minDay", lowerCaseParameterNames)));
			request.getSession().setAttribute("schDay", fp.getString(name("schDay", lowerCaseParameterNames)));
			request.getSession().setAttribute("emailUser", fp.getString(name("emailUser", lowerCaseParameterNames)));
			request.getSession().setAttribute("emailDay", fp.getString(name("emailDay", lowerCaseParameterNames)));
			request.getSession().setAttribute("isReference", isReference);
		}

		HashMap errors = validator.validate();

		if (errors.isEmpty() && !Arrays.asList(SCHEDULED, UNSCHEDULED, COMMON, CALENDARED_VISIT).contains(type)) {
			errors.put(name("type", lowerCaseParameterNames), getErrorMessages("rest.studyEventDefinition.wrongType"));
			return errors;
		} else if (errors.isEmpty() && repeating.equals("true") && type.equals(CALENDARED_VISIT)) {
			errors.put(name("repeating", lowerCaseParameterNames),
					getErrorMessages("rest.studyEventDefinition.calendaredVisitCanNotBeRepeating"));
			return errors;
		} else if (errors.isEmpty() && !type.equals(CALENDARED_VISIT) && isReference.equalsIgnoreCase("true")) {
			errors.put(name("isReference", lowerCaseParameterNames),
					getErrorMessages("rest.studyEventDefinition.onlyCalendaredEventsCanBeReferenced"));
			return errors;
		}

		int maxDay = fp.getInt(name("maxDay", lowerCaseParameterNames));
		int minDay = fp.getInt(name("minDay", lowerCaseParameterNames));
		int schDay = fp.getInt(name("schDay", lowerCaseParameterNames));
		int emailDay = fp.getInt(name("emailDay", lowerCaseParameterNames));
		String emailUser = fp.getString(name("emailUser", lowerCaseParameterNames));

		if (errors.isEmpty()
				&& ((type.equals(CALENDARED_VISIT) && isReference.equalsIgnoreCase("true")) || !type
						.equals(CALENDARED_VISIT))) {
			if (maxDay != 0) {
				errors.put(name("maxDay", lowerCaseParameterNames), getErrorMessages(type.equals(CALENDARED_VISIT)
						? "rest.studyEventDefinition.dayMaxIsNotUsedForReferenceEvent"
						: "rest.studyEventDefinition.dayMaxIsUsedForCalendaredEventsOnly"));
				return errors;
			} else if (minDay != 0) {
				errors.put(name("minDay", lowerCaseParameterNames), getErrorMessages(type.equals(CALENDARED_VISIT)
						? "rest.studyEventDefinition.dayMinIsNotUsedForReferenceEvent"
						: "rest.studyEventDefinition.dayMinIsUsedForCalendaredEventsOnly"));
				return errors;
			} else if (schDay != 0) {
				errors.put(name("schDay", lowerCaseParameterNames), getErrorMessages(type.equals(CALENDARED_VISIT)
						? "rest.studyEventDefinition.dayScheduleIsNotUsedForReferenceEvent"
						: "rest.studyEventDefinition.dayScheduleIsUsedForCalendaredEventsOnly"));
				return errors;
			} else if (emailDay != 0) {
				errors.put(name("emailDay", lowerCaseParameterNames), getErrorMessages(type.equals(CALENDARED_VISIT)
						? "rest.studyEventDefinition.dayEmailIsNotUsedForReferenceEvent"
						: "rest.studyEventDefinition.dayEmailIsUsedForCalendaredEventsOnly"));
				return errors;
			} else if (!emailUser.isEmpty()) {
				errors.put(name("emailUser", lowerCaseParameterNames), getErrorMessages(type.equals(CALENDARED_VISIT)
						? "rest.studyEventDefinition.userNameIsNotUsedForReferenceEvent"
						: "rest.studyEventDefinition.userNameIsUsedForCalendaredEventsOnly"));
				return errors;
			}
		}

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
				&& CALENDARED_VISIT.equals(type) && !"true".equalsIgnoreCase(isReference)) {
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

	/**
	 * Method perform validation.
	 *
	 * @param messageSource
	 *            MessageSource
	 * @param dataSource
	 *            DataSource
	 * @param eventId
	 *            int
	 * @param versionName
	 *            String
	 * @param crfName
	 *            String
	 * @param sdvCode
	 *            int
	 * @param emailWhen
	 *            String
	 * @param email
	 *            String
	 * @param hasSDVRequiredItems
	 *            boolean
	 * @param studyEventDefinitionBean
	 *            StudyEventDefinitionBean
	 * @param crfVersionBean
	 *            CRFVersionBean
	 * @param currentStudy
	 *            StudyBean
	 * @return HashMap
	 */
	public static HashMap validateCrfAdding(MessageSource messageSource, DataSource dataSource, int eventId,
			String versionName, String crfName, int sdvCode, String emailWhen, String email,
			boolean hasSDVRequiredItems, StudyEventDefinitionBean studyEventDefinitionBean,
			CRFVersionBean crfVersionBean, StudyBean currentStudy) {
		HashMap errors = new HashMap();
		Locale locale = LocaleResolver.getLocale();

		EventDefinitionCRFBean eventDefinitionCrfBean = new EventDefinitionCRFDAO(dataSource)
				.findByStudyEventDefinitionIdAndCRFId(studyEventDefinitionBean.getId(), crfVersionBean.getCrfId());

		if (studyEventDefinitionBean.getId() == 0) {
			ArrayList errorMessages = new ArrayList();
			errorMessages.add(messageSource.getMessage("rest.event.isNotFound", new Object[]{eventId}, locale));
			errors.put("eventid", errorMessages);
		} else if (crfVersionBean.getId() == 0) {
			ArrayList errorMessages = new ArrayList();
			errorMessages.add(messageSource.getMessage("rest.event.addCrf.crfIsNotFound", new Object[]{crfName,
					versionName}, locale));
			errors.put("crfname", errorMessages);
		} else if (studyEventDefinitionBean.getStudyId() != currentStudy.getId()) {
			ArrayList errorMessages = new ArrayList();
			errorMessages.add(messageSource.getMessage("rest.event.doesNotBelongToCurrentStudy", new Object[]{eventId,
					currentStudy.getId()}, locale));
			errors.put("eventid", errorMessages);
		} else if (eventDefinitionCrfBean.getId() > 0) {
			ArrayList errorMessages = new ArrayList();
			errorMessages.add(messageSource.getMessage("rest.event.eventDefinitionCrfAlreadyExists", new Object[]{
					crfVersionBean.getCrfId(), eventId}, locale));
			errors.put("eventid", errorMessages);
		} else if (hasSDVRequiredItems && sdvCode != 2) {
			ArrayList errorMessages = new ArrayList();
			errorMessages.add(messageSource.getMessage("rest.event.crfHasSDVRequiredItems",
					new Object[]{crfVersionBean.getCrfId()}, locale));
			errors.put("sourcedataverification", errorMessages);
		} else if ((emailWhen.equals("sign") || emailWhen.equals("complete")) && !EmailUtil.isValid(email)) {
			ArrayList errorMessages = new ArrayList();
			errorMessages.add(messageSource.getMessage("rest.event.emailAddressIsNotValid", null, locale));
			errors.put("email", errorMessages);
		}

		return errors;
	}
}
