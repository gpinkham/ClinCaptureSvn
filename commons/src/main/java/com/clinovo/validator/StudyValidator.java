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

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.validator;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.hibernate.ConfigurationDao;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;

import com.clinovo.enums.study.StudyConfigurationParameter;
import com.clinovo.enums.study.StudyFacility;
import com.clinovo.enums.study.StudyOrigin;
import com.clinovo.enums.study.StudyParameter;
import com.clinovo.enums.study.StudyProtocolType;
import com.clinovo.i18n.LocaleResolver;
import com.clinovo.model.DiscrepancyDescription;
import com.clinovo.model.DiscrepancyDescriptionType;
import com.clinovo.util.DateUtil;
import com.clinovo.util.RequestUtil;
import com.clinovo.util.ValidatorHelper;

/**
 * StudyBean validator.
 */
@SuppressWarnings({"rawtypes", "unchecked", "unused"})
public class StudyValidator {

	public static final int VALIDATION_NUM_20 = 20;
	public static final int VALIDATION_NUM_30 = 30;
	public static final int VALIDATION_NUM_64 = 64;
	public static final int VALIDATION_NUM_100 = 100;
	public static final int VALIDATION_NUM_255 = 255;
	public static final int VALIDATION_NUM_500 = 500;
	public static final int VALIDATION_NUM_1000 = 1000;
	public static final int VALIDATION_NUM_2000 = 2000;

	/**
	 * Checks that study fields are unique.
	 * 
	 * @param fp
	 *            FormProcessor
	 * @param errors
	 *            HashMap
	 * @param studyDAO
	 *            StudyDAO
	 * @param respage
	 *            ResourceBundle
	 * @param resexception
	 *            ResourceBundle
	 * @param studyBean
	 *            ResourceBundle
	 * @return boolean
	 */
	public static boolean checkIfStudyFieldsAreUnique(FormProcessor fp, HashMap errors, StudyDAO studyDAO,
			ResourceBundle respage, ResourceBundle resexception, StudyBean studyBean) {
		ArrayList<StudyBean> allStudies = (ArrayList<StudyBean>) studyDAO.findAll();
		boolean result = true;
		for (StudyBean thisBean : allStudies) {
			if (fp.getString(StudyParameter.STUDY_NAME.getName()).trim().equals(thisBean.getName())
					&& isNotTheSameStudy(studyBean, thisBean)) {
				result = false;
				MessageFormat mf = new MessageFormat("");
				mf.applyPattern(respage.getString("study_name_exists"));
				Object[] arguments = {fp.getString(StudyParameter.STUDY_NAME.getName()).trim()};
				Validator.addError(errors, StudyParameter.STUDY_NAME.getName(), mf.format(arguments));
			}
			if (fp.getString(StudyParameter.PROTOCOL_ID.getName()).trim().equals(thisBean.getIdentifier())
					&& isNotTheSameStudy(studyBean, thisBean)) {
				result = false;
				Validator.addError(errors, StudyParameter.PROTOCOL_ID.getName(),
						resexception.getString("unique_protocol_id_existed"));
			}
		}
		return result;
	}

	/**
	 * 
	 * @param studyDao
	 *            StudyDAO
	 * @param configurationDao
	 *            ConfigurationDao
	 * @param studyBean
	 *            StudyBean
	 * @param dDescriptionsMap
	 *            Map
	 * @param datePattern
	 *            DateUtil.DatePattern
	 * @param validateFacilities
	 *            boolean
	 * @return HashMap
	 */
	public static HashMap validate(StudyDAO studyDao, ConfigurationDao configurationDao, StudyBean studyBean,
			Map<String, List<DiscrepancyDescription>> dDescriptionsMap, DateUtil.DatePattern datePattern,
			boolean validateFacilities) {
		return validate(new Validator(new ValidatorHelper(RequestUtil.getRequest(), configurationDao)), studyDao,
				studyBean, dDescriptionsMap, datePattern, validateFacilities);
	}

	/**
	 * Method perform validation.
	 *
	 * @param validator
	 *            Validator
	 * @param studyDao
	 *            StudyDAO
	 * @param studyBean
	 *            StudyBean
	 * @param dDescriptionsMap
	 *            Map
	 * @param datePattern
	 *            DateUtil.DatePattern
	 * @param validateFacilities
	 *            boolean
	 * @return HashMap
	 */
	public static HashMap validate(Validator validator, StudyDAO studyDao, StudyBean studyBean,
			Map<String, List<DiscrepancyDescription>> dDescriptionsMap, DateUtil.DatePattern datePattern,
			boolean validateFacilities) {
		HttpServletRequest request = RequestUtil.getRequest();

		HashMap errors = new HashMap();
		boolean editMode = studyBean.getId() > 0;
		Locale locale = LocaleResolver.getLocale();
		FormProcessor fp = new FormProcessor(request);
		ResourceBundle exceptionsBundle = ResourceBundleProvider.getExceptionsBundle();
		ResourceBundle pageMessagesBundle = ResourceBundleProvider.getPageMessagesBundle();

		validator.addValidation(StudyParameter.STUDY_NAME.getName(), Validator.NO_BLANKS);
		validator.addValidation(StudyParameter.PROTOCOL_ID.getName(), Validator.NO_BLANKS);
		validator.addValidation(StudyParameter.SUMMARY.getName(), Validator.NO_BLANKS);
		validator.addValidation(StudyParameter.PRINCIPAL_INVESTIGATOR.getName(), Validator.NO_BLANKS);
		validator.addValidation(StudyParameter.SPONSOR.getName(), Validator.NO_BLANKS);

		validator.addValidation(StudyParameter.STUDY_NAME.getName(), Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO,
				studyBean.getOrigin().equals(StudyOrigin.STUDIO.getName())
						? StudyValidator.VALIDATION_NUM_20
						: VALIDATION_NUM_100);
		validator.addValidation(StudyParameter.SECOND_PRO_ID.getName(), Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, VALIDATION_NUM_255);
		validator.addValidation(StudyParameter.COLLABORATORS.getName(), Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, VALIDATION_NUM_1000);
		validator.addValidation(StudyParameter.DESCRIPTION.getName(), Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, VALIDATION_NUM_1000);

		validateDate(fp, errors, exceptionsBundle, StudyParameter.START_DATE.getName(), datePattern, locale, false);
		validateDate(fp, errors, exceptionsBundle, StudyParameter.END_DATE.getName(), datePattern, locale, true);
		validateDate(fp, errors, exceptionsBundle, StudyParameter.APPROVAL_DATE.getName(), datePattern, locale, true);

		StudyProtocolType protocolType = StudyProtocolType.get(fp.getInt(StudyParameter.PROTOCOL_TYPE.getName()));

		if (editMode) {
			if (dDescriptionsMap != null) {
				validateSpecifiedDescriptions(fp, pageMessagesBundle, errors, validator, studyBean.getId(),
						dDescriptionsMap.get("dnUpdateDescriptions"), "updateName", "updateVisibilityLevel",
						"updateDescriptionId", "updateDescriptionError",
						DiscrepancyDescriptionType.DescriptionType.UPDATE_DESCRIPTION.getId());
				validateSpecifiedDescriptions(fp, pageMessagesBundle, errors, validator, studyBean.getId(),
						dDescriptionsMap.get("dnCloseDescriptions"), "closeName", "closeVisibilityLevel",
						"closeDescriptionId", "closeDescriptionError",
						DiscrepancyDescriptionType.DescriptionType.CLOSE_DESCRIPTION.getId());
				validateSpecifiedDescriptions(fp, pageMessagesBundle, errors, validator, studyBean.getId(),
						dDescriptionsMap.get("dnRFCDescriptions"), "dnRFCName", "dnRFCVisibilityLevel",
						"dnRFCDescriptionId", "dnRFCDescriptionError",
						DiscrepancyDescriptionType.DescriptionType.RFC_DESCRIPTION.getId());
			}
		}

		if (validateFacilities) {
			if (!StringUtil.isBlank(fp.getString(StudyFacility.FACILITY_CONTACT_EMAIL.getName()))) {
				validator.addValidation(StudyFacility.FACILITY_CONTACT_EMAIL.getName(), Validator.IS_A_EMAIL);
			}
			validator.addValidation(StudyFacility.FACILITY_NAME.getName(), Validator.LENGTH_NUMERIC_COMPARISON,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, StudyValidator.VALIDATION_NUM_255);
			validator.addValidation(StudyFacility.FACILITY_CITY.getName(), Validator.LENGTH_NUMERIC_COMPARISON,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, StudyValidator.VALIDATION_NUM_255);
			validator.addValidation(StudyFacility.FACILITY_STATE.getName(), Validator.LENGTH_NUMERIC_COMPARISON,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, StudyValidator.VALIDATION_NUM_20);
			validator.addValidation(StudyFacility.FACILITY_ZIP.getName(), Validator.LENGTH_NUMERIC_COMPARISON,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, StudyValidator.VALIDATION_NUM_64);
			validator.addValidation(StudyFacility.FACILITY_COUNTRY.getName(), Validator.LENGTH_NUMERIC_COMPARISON,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, StudyValidator.VALIDATION_NUM_64);
			validator.addValidation(StudyFacility.FACILITY_CONTACT_NAME.getName(), Validator.LENGTH_NUMERIC_COMPARISON,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, StudyValidator.VALIDATION_NUM_255);
			validator.addValidation(StudyFacility.FACILITY_CONTACT_DEGREE.getName(),
					Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO,
					StudyValidator.VALIDATION_NUM_255);
			validator.addValidation(StudyFacility.FACILITY_CONTACT_PHONE.getName(), Validator.LENGTH_NUMERIC_COMPARISON,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, StudyValidator.VALIDATION_NUM_255);
			validator.addValidation(StudyFacility.FACILITY_CONTACT_EMAIL.getName(), Validator.LENGTH_NUMERIC_COMPARISON,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, StudyValidator.VALIDATION_NUM_255);
		}

		if (protocolType == StudyProtocolType.INTERVENTIONAL) {
			if (request.getParameter(StudyParameter.DURATION.getName()) != null) {
				Validator.addError(errors, StudyParameter.DURATION.getName(),
						exceptionsBundle.getString("parametersAreUsedWithProtocolTypeObservational"));
			} else if (request.getParameter(StudyParameter.SELECTION.getName()) != null) {
				Validator.addError(errors, StudyParameter.SELECTION.getName(),
						exceptionsBundle.getString("parametersAreUsedWithProtocolTypeObservational"));
			} else if (request.getParameter(StudyParameter.TIMING.getName()) != null) {
				Validator.addError(errors, StudyParameter.TIMING.getName(),
						exceptionsBundle.getString("parametersAreUsedWithProtocolTypeObservational"));
			}
		} else if (protocolType == StudyProtocolType.OBSERVATIONAL) {
			if (request.getParameter(StudyParameter.ALLOCATION.getName()) != null) {
				Validator.addError(errors, StudyParameter.ALLOCATION.getName(),
						exceptionsBundle.getString("parametersAreUsedWithProtocolTypeInterventional"));
			} else if (request.getParameter(StudyParameter.MASKING.getName()) != null) {
				Validator.addError(errors, StudyParameter.MASKING.getName(),
						exceptionsBundle.getString("parametersAreUsedWithProtocolTypeInterventional"));
			} else if (request.getParameter(StudyParameter.CONTROL.getName()) != null) {
				Validator.addError(errors, StudyParameter.CONTROL.getName(),
						exceptionsBundle.getString("parametersAreUsedWithProtocolTypeInterventional"));
			} else if (request.getParameter(StudyParameter.ASSIGNMENT.getName()) != null) {
				Validator.addError(errors, StudyParameter.ASSIGNMENT.getName(),
						exceptionsBundle.getString("parametersAreUsedWithProtocolTypeInterventional"));
			} else if (request.getParameter(StudyParameter.END_POINT.getName()) != null) {
				Validator.addError(errors, StudyParameter.END_POINT.getName(),
						exceptionsBundle.getString("parametersAreUsedWithProtocolTypeInterventional"));
			}
		}

		errors.putAll(validator.validate());

		StudyValidator.checkIfStudyFieldsAreUnique(fp, errors, studyDao, pageMessagesBundle, exceptionsBundle,
				studyBean);

		if (fp.getString(StudyParameter.BRIEF_TITLE.getName()).trim().length() > VALIDATION_NUM_100) {
			Validator.addError(errors, StudyParameter.BRIEF_TITLE.getName(),
					exceptionsBundle.getString("maximum_lenght_name_100"));
		}
		if (fp.getString(StudyParameter.PROTOCOL_ID.getName()).trim().length() > VALIDATION_NUM_30) {
			Validator.addError(errors, StudyParameter.PROTOCOL_ID.getName(),
					exceptionsBundle.getString("maximum_lenght_unique_protocol_30"));
		}
		if (fp.getString(StudyParameter.SUMMARY.getName()).trim().length() > VALIDATION_NUM_2000) {
			Validator.addError(errors, StudyParameter.SUMMARY.getName(),
					exceptionsBundle.getString("maximum_lenght_brief_summary_2000"));
		}
		if (fp.getString(StudyParameter.PRINCIPAL_INVESTIGATOR.getName()).trim().length() > VALIDATION_NUM_255) {
			Validator.addError(errors, StudyParameter.PRINCIPAL_INVESTIGATOR.getName(),
					exceptionsBundle.getString("maximum_lenght_principal_investigator_255"));
		}
		if (fp.getString(StudyParameter.SPONSOR.getName()).trim().length() > VALIDATION_NUM_255) {
			Validator.addError(errors, StudyParameter.SPONSOR.getName(),
					exceptionsBundle.getString("maximum_lenght_sponsor_255"));
		}
		if (fp.getString(StudyParameter.OFFICIAL_TITLE.getName()).trim().length() > VALIDATION_NUM_255) {
			Validator.addError(errors, StudyParameter.OFFICIAL_TITLE.getName(),
					exceptionsBundle.getString("maximum_lenght_official_title_255"));
		}
		if (fp.getString(StudyConfigurationParameter.STUDY_SUBJECT_ID_LABEL.getName()).trim()
				.length() > VALIDATION_NUM_255) {
			Validator.addError(errors, StudyConfigurationParameter.STUDY_SUBJECT_ID_LABEL.getName(),
					exceptionsBundle.getString("maximum_lenght_studySubjectIdLabel_255"));
		}
		if (fp.getString(StudyConfigurationParameter.SECONDARY_ID_LABEL.getName()).trim()
				.length() > VALIDATION_NUM_255) {
			Validator.addError(errors, StudyConfigurationParameter.SECONDARY_ID_LABEL.getName(),
					exceptionsBundle.getString("maximum_lenght_secondaryIdLabel_255"));
		}
		if (fp.getString(StudyConfigurationParameter.DATE_OF_ENROLLMENT_FOR_STUDY_LABEL.getName()).trim()
				.length() > VALIDATION_NUM_255) {
			Validator.addError(errors, StudyConfigurationParameter.DATE_OF_ENROLLMENT_FOR_STUDY_LABEL.getName(),
					exceptionsBundle.getString("maximum_lenght_dateOfEnrollmentForStudyLabel_255"));
		}
		if (fp.getString(StudyConfigurationParameter.GENDER_LABEL.getName()).trim().length() > VALIDATION_NUM_255) {
			Validator.addError(errors, StudyConfigurationParameter.GENDER_LABEL.getName(),
					exceptionsBundle.getString("maximum_lenght_genderLabel_255"));
		}
		if (fp.getString(StudyConfigurationParameter.START_DATE_TIME_LABEL.getName()).trim()
				.length() > VALIDATION_NUM_255) {
			Validator.addError(errors, StudyConfigurationParameter.START_DATE_TIME_LABEL.getName(),
					exceptionsBundle.getString("maximum_lenght_startDateTimeLabel_255"));
		}
		if (fp.getString(StudyConfigurationParameter.END_DATE_TIME_LABEL.getName()).trim()
				.length() > VALIDATION_NUM_255) {
			Validator.addError(errors, StudyConfigurationParameter.END_DATE_TIME_LABEL.getName(),
					exceptionsBundle.getString("maximum_lenght_endDateTimeLabel_255"));
		}
		if (fp.getInt(StudyParameter.TOTAL_ENROLLMENT.getName()) <= 0) {
			Validator.addError(errors, StudyParameter.TOTAL_ENROLLMENT.getName(),
					pageMessagesBundle.getString("expected_total_enrollment_must_be_a_positive_number"));
		}

		String autoGeneratedPrefixValue = fp.getString(StudyConfigurationParameter.AUTO_GENERATED_PREFIX.getName());
		if (!autoGeneratedPrefixValue.isEmpty()) {
			if (!autoGeneratedPrefixValue
					.replaceAll(StudyConfigurationParameter.AUTO_GENERATED_PREFIX.getValidationPattern(), "")
					.equals(autoGeneratedPrefixValue)) {
				Validator.addError(errors, StudyConfigurationParameter.AUTO_GENERATED_PREFIX.getName(), exceptionsBundle
						.getString(StudyConfigurationParameter.AUTO_GENERATED_PREFIX.getValidationErrorMessage()));
			}
		}

		String autoGeneratedSeparatorValue = fp
				.getString(StudyConfigurationParameter.AUTO_GENERATED_SEPARATOR.getName());
		if (!autoGeneratedSeparatorValue.isEmpty()) {
			if (!autoGeneratedSeparatorValue
					.replaceAll(StudyConfigurationParameter.AUTO_GENERATED_SEPARATOR.getValidationPattern(), "")
					.equals(autoGeneratedSeparatorValue)) {
				Validator.addError(errors, StudyConfigurationParameter.AUTO_GENERATED_SEPARATOR.getName(),
						exceptionsBundle.getString(
								StudyConfigurationParameter.AUTO_GENERATED_SEPARATOR.getValidationErrorMessage()));
			}
		}

		return errors;
	}

	private static void validateSpecifiedDescriptions(FormProcessor fp, ResourceBundle pageMessagesBundle,
			HashMap errors, Validator v, int studyId, List<DiscrepancyDescription> newDescriptions,
			String descriptionName, String visibilityLevel, String descriptionId, String descriptionError, int typeId) {
		newDescriptions.clear();
		final int counter = 25;
		for (int i = 0; i < counter; i++) {
			v.addValidation(descriptionName + i, Validator.LENGTH_NUMERIC_COMPARISON,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, StudyValidator.VALIDATION_NUM_255);
			// set list of dn descriptions for specified type here
			if (!"".equals(fp.getString(descriptionName + i))) {
				DiscrepancyDescription dDescription = new DiscrepancyDescription();
				dDescription.setTypeId(typeId);
				dDescription.setName(fp.getString(descriptionName + i));
				switch (fp.getInt(visibilityLevel + i)) {
					case 1 :
						dDescription.setVisibilityLevel("Study");
						break;
					case 2 :
						dDescription.setVisibilityLevel("Site");
						break;
					default :
						dDescription.setVisibilityLevel("Study and Site");
				}
				if (fp.getInt(descriptionId + i) != 0) {
					dDescription.setId(fp.getInt(descriptionId + i));
				}
				dDescription.setStudyId(studyId);
				newDescriptions.add(dDescription);
			}
		}
		errors.putAll(v.validate());
		for (int i = 0; i < newDescriptions.size(); i++) {
			DiscrepancyDescription rfcTerm1 = newDescriptions.get(i);
			for (int j = 0; j < i; j++) {
				DiscrepancyDescription rfcTerm2 = newDescriptions.get(j);
				if (rfcTerm1.getName().equals(rfcTerm2.getName())) {
					Validator.addError(errors, descriptionError + i,
							pageMessagesBundle.getString("please_correct_the_duplicate_description_found_in_row") + " "
									+ (j + 1));
				}
			}
		}
	}

	private static void validateDate(FormProcessor fp, HashMap errors, ResourceBundle exceptionsBundle,
			String parameterName, DateUtil.DatePattern datePattern, Locale locale, boolean doNotCheckIfEmpty) {
		try {
			if (!doNotCheckIfEmpty || !fp.getString(parameterName).isEmpty()) {
				DateUtil.parseDateString(fp.getString(parameterName), datePattern, locale);
			}
		} catch (Exception ex) {
			Validator.addError(errors, parameterName, exceptionsBundle.getString("input_not_valid_date_time")
					+ datePattern.getPattern() + " " + exceptionsBundle.getString("format2") + ".");
		}
	}

	private static boolean isNotTheSameStudy(StudyBean currentStudy, StudyBean anotherStudy) {
		return currentStudy.getId() != anotherStudy.getId();
	}

	private static void addValidatorIfParamPresented(HttpServletRequest request, String paramName, Validator v,
			int validatorType) {
		if (request.getParameter(paramName) != null) {
			v.addValidation(paramName, validatorType);
		}
	}
}
