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

package com.clinovo.validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.dao.hibernate.ConfigurationDao;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.springframework.context.MessageSource;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.model.EDCItemMetadata;
import com.clinovo.service.EDCItemMetadataService;
import com.clinovo.util.EmailUtil;
import com.clinovo.util.RequestUtil;
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

	private static ArrayList getErrorMessages(String code) {
		ResourceBundle resexception = ResourceBundleProvider.getExceptionsBundle();
		ArrayList errorMessages = new ArrayList();
		errorMessages.add(resexception.getString(code));
		return errorMessages;
	}

	/**
	 * Method perform validation.
	 *
	 * @param configurationDao
	 *            ConfigurationDao
	 * @param userAccountDao
	 *            UserAccountDAO
	 * @param currentStudy
	 *            StudyBean
	 * @return HashMap
	 */
	public static HashMap validate(ConfigurationDao configurationDao, UserAccountDAO userAccountDao,
			StudyBean currentStudy) {
		HttpServletRequest request = RequestUtil.getRequest();
		FormProcessor fp = new FormProcessor(request);
		ResourceBundle resexception = ResourceBundleProvider.getExceptionsBundle();

		ArrayList<StudyUserRoleBean> studyUserRoleBeanList = userAccountDao.findAllByStudyId(currentStudy.getId());

		Validator validator = new Validator(new ValidatorHelper(request, configurationDao));

		validator.addValidation("name", Validator.NO_BLANKS);
		validator.addValidation("type", Validator.NO_BLANKS);
		validator.addValidation("name", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, INT_2000);
		validator.addValidation("description", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, INT_2000);
		validator.addValidation("category", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, INT_2000);

		String isReference = fp.getString("isReference");
		String repeating = fp.getString("repeating");
		String type = fp.getString("type");

		if (CALENDARED_VISIT.equals(type)) {
			validator.addValidation("maxDay", Validator.IS_REQUIRED);
			validator.addValidation("maxDay", Validator.IS_A_FLOAT, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO,
					INT_3);
			validator.addValidation("minDay", Validator.IS_REQUIRED);
			validator.addValidation("minDay", Validator.IS_A_FLOAT, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO,
					INT_3);
			validator.addValidation("schDay", Validator.IS_REQUIRED);
			validator.addValidation("schDay", Validator.IS_A_FLOAT, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO,
					INT_3);
			validator.addValidation("emailDay", Validator.IS_REQUIRED);
			validator.addValidation("emailDay", Validator.IS_A_FLOAT, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO,
					INT_3);

			if (!isReference.equalsIgnoreCase("true")) {
				validator.addValidation("emailUser", Validator.NO_BLANKS);
			}

			request.getSession().setAttribute("changedReference", "true".equalsIgnoreCase(isReference));
			request.getSession().setAttribute("showCalendaredVisitBox", true);
			request.getSession().setAttribute("maxDay", fp.getString("maxDay"));
			request.getSession().setAttribute("minDay", fp.getString("minDay"));
			request.getSession().setAttribute("schDay", fp.getString("schDay"));
			request.getSession().setAttribute("emailUser", fp.getString("emailUser"));
			request.getSession().setAttribute("emailDay", fp.getString("emailDay"));
			request.getSession().setAttribute("isReference", isReference);
		}

		HashMap errors = validator.validate();

		if (errors.isEmpty() && !Arrays.asList(SCHEDULED, UNSCHEDULED, COMMON, CALENDARED_VISIT).contains(type)) {
			errors.put("type", getErrorMessages("eventDefinitionValidator.wrongType"));
			return errors;
		} else if (errors.isEmpty() && repeating.equals("true") && type.equals(CALENDARED_VISIT)) {
			errors.put("repeating", getErrorMessages("eventDefinitionValidator.calendaredVisitCanNotBeRepeating"));
			return errors;
		} else if (errors.isEmpty() && !type.equals(CALENDARED_VISIT) && isReference.equalsIgnoreCase("true")) {
			errors.put("isReference", getErrorMessages("eventDefinitionValidator.onlyCalendaredEventsCanBeReferenced"));
			return errors;
		}

		int maxDay = fp.getInt("maxDay");
		int minDay = fp.getInt("minDay");
		int schDay = fp.getInt("schDay");
		int emailDay = fp.getInt("emailDay");
		String emailUser = fp.getString("emailUser");

		if (errors.isEmpty() && ((type.equals(CALENDARED_VISIT) && isReference.equalsIgnoreCase("true"))
				|| !type.equals(CALENDARED_VISIT))) {
			if (maxDay != 0) {
				errors.put("maxDay",
						getErrorMessages(type.equals(CALENDARED_VISIT)
								? "eventDefinitionValidator.dayMaxIsNotUsedForReferenceEvent"
								: "eventDefinitionValidator.dayMaxIsUsedForCalendaredEventsOnly"));
				return errors;
			} else if (minDay != 0) {
				errors.put("minDay",
						getErrorMessages(type.equals(CALENDARED_VISIT)
								? "eventDefinitionValidator.dayMinIsNotUsedForReferenceEvent"
								: "eventDefinitionValidator.dayMinIsUsedForCalendaredEventsOnly"));
				return errors;
			} else if (schDay != 0) {
				errors.put("schDay",
						getErrorMessages(type.equals(CALENDARED_VISIT)
								? "eventDefinitionValidator.dayScheduleIsNotUsedForReferenceEvent"
								: "eventDefinitionValidator.dayScheduleIsUsedForCalendaredEventsOnly"));
				return errors;
			} else if (emailDay != 0) {
				errors.put("emailDay",
						getErrorMessages(type.equals(CALENDARED_VISIT)
								? "eventDefinitionValidator.dayEmailIsNotUsedForReferenceEvent"
								: "eventDefinitionValidator.dayEmailIsUsedForCalendaredEventsOnly"));
				return errors;
			} else if (!emailUser.isEmpty()) {
				errors.put("emailUser",
						getErrorMessages(type.equals(CALENDARED_VISIT)
								? "eventDefinitionValidator.userNameIsNotUsedForReferenceEvent"
								: "eventDefinitionValidator.userNameIsUsedForCalendaredEventsOnly"));
				return errors;
			}
		}

		if (!(maxDay >= schDay)) {
			Validator.addError(errors, "maxDay", resexception.getString("daymax_greate_or_equal_dayschedule"));
		}
		if (!(minDay <= schDay)) {
			Validator.addError(errors, "minDay", resexception.getString("daymin_less_or_equal_dayschedule"));
		}
		if (!(minDay <= maxDay)) {
			Validator.addError(errors, "minDay", resexception.getString("daymin_less_or_equal_daymax"));
		}
		if (!(emailDay <= schDay)) {
			Validator.addError(errors, "emailDay", resexception.getString("dayemail_less_or_equal_dayschedule"));
		}
		if (!emailUser.equals("root") && !checkUserName(studyUserRoleBeanList, emailUser)
				&& CALENDARED_VISIT.equals(type) && !"true".equalsIgnoreCase(isReference)) {
			Validator.addError(errors, "emailUser", resexception.getString("this_user_name_does_not_exist"));
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

	private static HashMap validateEDC(MessageSource messageSource, int eventId, String versionName, String crfName,
			int sdvCode, String emailWhen, String email, boolean hasSDVRequiredItems, CRFVersionBean crfVersionBean,
			StudyEventDefinitionBean studyEventDefinitionBean, StudyBean currentStudy) {
		HashMap errors = new HashMap();
		Locale locale = LocaleResolver.getLocale();

		if (studyEventDefinitionBean.getId() == 0) {
			ArrayList errorMessages = new ArrayList();
			errorMessages.add(
					messageSource.getMessage("eventDefinitionValidator.isNotFound", new Object[]{eventId}, locale));
			errors.put("eventid", errorMessages);
		} else if (crfVersionBean.getId() == 0) {
			ArrayList errorMessages = new ArrayList();
			errorMessages.add(messageSource.getMessage("eventDefinitionValidator.crfVersionIsNotFound",
					new Object[]{crfName, versionName}, locale));
			errors.put("defaultversion", errorMessages);
		} else if (!crfVersionBean.getStatus().equals(Status.AVAILABLE)) {
			ArrayList errorMessages = new ArrayList();
			errorMessages.add(messageSource.getMessage("eventDefinitionValidator.crfVersionIsNotAvailable",
					new Object[]{crfName, versionName}, locale));
			errors.put("defaultversion", errorMessages);
		} else if (studyEventDefinitionBean.getStudyId() != currentStudy.getId()) {
			ArrayList errorMessages = new ArrayList();
			errorMessages.add(messageSource.getMessage("eventDefinitionValidator.doesNotBelongToCurrentStudy",
					new Object[]{eventId, currentStudy.getName()}, locale));
			errors.put("eventid", errorMessages);
		} else if (!hasSDVRequiredItems && sdvCode == 2) {
			ArrayList errorMessages = new ArrayList();
			errorMessages.add(
					messageSource.getMessage("eventDefinitionValidator.crfDoesNotHaveSDVRequiredItemsButSDVCodeIs2",
							new Object[]{crfVersionBean.getCrfId()}, locale));
			errors.put("sourcedataverification", errorMessages);
		} else if (hasSDVRequiredItems && sdvCode != 2) {
			ArrayList errorMessages = new ArrayList();
			errorMessages
					.add(messageSource.getMessage("eventDefinitionValidator.crfHasSDVRequiredItemsButSDVCodeIsNot2",
							new Object[]{crfVersionBean.getCrfId()}, locale));
			errors.put("sourcedataverification", errorMessages);
		} else if ((emailWhen.equals("sign") || emailWhen.equals("complete")) && email.trim().isEmpty()) {
			ArrayList errorMessages = new ArrayList();
			errorMessages.add(messageSource.getMessage("eventDefinitionValidator.provideEmailAddress", null, locale));
			errors.put("email", errorMessages);
		} else if ((emailWhen.equals("sign") || emailWhen.equals("complete")) && !EmailUtil.isValid(email)) {
			ArrayList errorMessages = new ArrayList();
			errorMessages
					.add(messageSource.getMessage("eventDefinitionValidator.emailAddressIsNotValid", null, locale));
			errors.put("email", errorMessages);
		} else if (!(emailWhen.equals("sign") || emailWhen.equals("complete")) && !email.isEmpty()) {
			ArrayList errorMessages = new ArrayList();
			errorMessages
					.add(messageSource.getMessage("eventDefinitionValidator.emailCanBeSpecifiedOnlyIf", null, locale));
			errors.put("email", errorMessages);
		}

		return errors;
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
	 * @param studyEventDefinitionBean
	 *            StudyEventDefinitionBean
	 * @param currentStudy
	 *            StudyBean
	 * @return HashMap
	 */
	public static HashMap validateNewEDC(MessageSource messageSource, DataSource dataSource, int eventId,
			String versionName, String crfName, int sdvCode, String emailWhen, String email,
			StudyEventDefinitionBean studyEventDefinitionBean, StudyBean currentStudy) {
		Locale locale = LocaleResolver.getLocale();

		CRFVersionBean crfVersionBean = (CRFVersionBean) new CRFVersionDAO(dataSource).findByFullName(versionName,
				crfName);

		HashMap errors = validateEDC(messageSource, eventId, versionName, crfName, sdvCode, emailWhen, email, false,
				crfVersionBean, studyEventDefinitionBean, currentStudy);

		EventDefinitionCRFBean existingEventDefinitionCRFBean = new EventDefinitionCRFDAO(dataSource)
				.findByStudyEventDefinitionIdAndCRFIdAndStudyId(studyEventDefinitionBean.getId(),
						crfVersionBean.getCrfId(), currentStudy.getId());

		if (errors.isEmpty() && existingEventDefinitionCRFBean.getId() > 0) {
			ArrayList errorMessages = new ArrayList();
			errorMessages.add(messageSource.getMessage("eventDefinitionValidator.alreadyExists",
					new Object[]{crfName, studyEventDefinitionBean.getName()}, locale));
			errors.put("eventid", errorMessages);
		}

		return errors;
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
	 * @param studyEventDefinitionBean
	 *            StudyEventDefinitionBean
	 * @param eventDefinitionCRFBean
	 *            EventDefinitionCRFBean
	 * @param currentStudy
	 *            StudyBean
	 * @param edcItemMetadataService
	 *            EDCItemMetadataService
	 * @return HashMap
	 */
	public static HashMap validateEDC(MessageSource messageSource, DataSource dataSource, int eventId,
			StudyEventDefinitionBean studyEventDefinitionBean, EventDefinitionCRFBean eventDefinitionCRFBean,
			StudyBean currentStudy, EDCItemMetadataService edcItemMetadataService) {
		Locale locale = LocaleResolver.getLocale();

		CRFVersionBean crfVersionBean = (CRFVersionBean) new CRFVersionDAO(dataSource)
				.findByFullName(eventDefinitionCRFBean.getDefaultVersionName(), eventDefinitionCRFBean.getCrfName());

		boolean hasSDVRequiredItems = false;
		List<EDCItemMetadata> edcItemMetadataList = edcItemMetadataService
				.findAllByEventDefinitionCRFAndVersion(eventDefinitionCRFBean, crfVersionBean.getId());
		for (EDCItemMetadata edcItemMetadata : edcItemMetadataList) {
			if (edcItemMetadata.sdvRequired()) {
				hasSDVRequiredItems = true;
				break;
			}
		}

		HashMap errors = validateEDC(messageSource, eventId, eventDefinitionCRFBean.getDefaultVersionName(),
				eventDefinitionCRFBean.getCrfName(), eventDefinitionCRFBean.getSourceDataVerification().getCode(),
				eventDefinitionCRFBean.getEmailStep(), eventDefinitionCRFBean.getEmailTo(), hasSDVRequiredItems,
				crfVersionBean, studyEventDefinitionBean, currentStudy);

		if (errors.isEmpty()) {
			if (!eventDefinitionCRFBean.getStatus().isAvailable()) {
				ArrayList errorMessages = new ArrayList();
				errorMessages.add(messageSource.getMessage("eventDefinitionValidator.isNotAvailable",
						new Object[]{eventDefinitionCRFBean.getCrfName(), studyEventDefinitionBean.getName()}, locale));
				errors.put("eventid", errorMessages);
			} else
				if (eventDefinitionCRFBean.isEvaluatedCRF()
						&& currentStudy.getStudyParameterConfig().getStudyEvaluator().equalsIgnoreCase("no")) {
				ArrayList errorMessages = new ArrayList();
				errorMessages.add(
						messageSource.getMessage("eventDefinitionValidator.crfEvaluationIsNotAvailable", null, locale));
				errors.put("dataentryquality", errorMessages);
			}
		}

		return errors;
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
	 * @param studyEventDefinitionBean
	 *            StudyEventDefinitionBean
	 * @param eventDefinitionCRFBean
	 *            EventDefinitionCRFBean
	 * @param currentStudy
	 *            StudyBean
	 * @param edcItemMetadataService
	 *            EDCItemMetadataService
	 * @return HashMap
	 */
	public static HashMap validateSiteEDC(MessageSource messageSource, DataSource dataSource, int eventId,
			StudyEventDefinitionBean studyEventDefinitionBean, EventDefinitionCRFBean eventDefinitionCRFBean,
			StudyBean currentStudy, EDCItemMetadataService edcItemMetadataService) {
		Locale locale = LocaleResolver.getLocale();

		HashMap errors = validateEDC(messageSource, dataSource, eventId, studyEventDefinitionBean,
				eventDefinitionCRFBean, currentStudy, edcItemMetadataService);

		if (errors.isEmpty()) {
			if (eventDefinitionCRFBean.getSelectedVersionNames().trim().isEmpty()) {
				ArrayList errorMessages = new ArrayList();
				errorMessages.add(
						messageSource.getMessage("eventDefinitionValidator.availableVersionsIsEmpty", null, locale));
				errors.put("availableversions", errorMessages);
			} else {
				boolean defaultVersionIsPresent = false;
				CRFVersionDAO crfVersionDao = new CRFVersionDAO(dataSource);
				for (String stringVersionId : eventDefinitionCRFBean.getSelectedVersionIds().split(",")) {
					Integer versionId = Integer.parseInt(stringVersionId.trim());
					defaultVersionIsPresent = !defaultVersionIsPresent
							&& versionId == eventDefinitionCRFBean.getDefaultVersionId() || defaultVersionIsPresent;
					CRFVersionBean crfVersionBean = (CRFVersionBean) crfVersionDao.findByPK(versionId);
					crfVersionBean = (CRFVersionBean) crfVersionDao.findByFullName(crfVersionBean.getName(),
							eventDefinitionCRFBean.getCrfName());
					if (crfVersionBean.getId() == 0) {
						ArrayList errorMessages = new ArrayList();
						errorMessages.add(messageSource.getMessage("eventDefinitionValidator.crfDoesNotHaveVersion",
								new Object[]{eventDefinitionCRFBean.getCrfName(), crfVersionBean.getName()}, locale));
						errors.put("availableversions", errorMessages);
						return errors;
					}
				}
				if (!defaultVersionIsPresent) {
					ArrayList errorMessages = new ArrayList();
					errorMessages.add(messageSource.getMessage(
							"eventDefinitionValidator.defaultVersionShouldBePresentInTheAvailableVersions", null,
							locale));
					errors.put("availableversions", errorMessages);
				}
			}
		}
		return errors;
	}
}
