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
import org.akaza.openclinica.dao.hibernate.ConfigurationDao;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;

import com.clinovo.enums.StudyConfigurationParameter;
import com.clinovo.enums.StudyParameter;
import com.clinovo.enums.StudyProtocolType;
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

	public static final int VALIDATION_NUM1 = 20;
	public static final int VALIDATION_NUM2 = 30;
	public static final int VALIDATION_NUM3 = 64;
	public static final int VALIDATION_NUM4 = 100;
	public static final int VALIDATION_NUM5 = 255;
	public static final int VALIDATION_NUM6 = 500;
	public static final int VALIDATION_NUM7 = 1000;
	public static final int VALIDATION_NUM8 = 2000;

	public static boolean checkIfStudyFieldsAreUnique(FormProcessor fp, HashMap errors, StudyDAO studyDAO,
			ResourceBundle respage, ResourceBundle resexception, StudyBean studyBean) {
		ArrayList<StudyBean> allStudies = (ArrayList<StudyBean>) studyDAO.findAll();
		boolean result = true;

		for (StudyBean thisBean : allStudies) {
			if (fp.getString(StudyParameter.STUDY_NAME.getName()).trim().equals(thisBean.getName())
					&& isNotTheSameStudy(studyBean, thisBean)) {
				result = false;
				MessageFormat mf = new MessageFormat("");
				mf.applyPattern(respage.getString("brief_title_existed"));
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
	 * @param datePattern
	 *            DateUtil.DatePattern
	 * @return HashMap
	 */
	public static HashMap validate(StudyDAO studyDao, ConfigurationDao configurationDao,
			DateUtil.DatePattern datePattern) {
		return validate(studyDao, configurationDao, null, null, datePattern);
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
	 * @return HashMap
	 */
	public static HashMap validate(StudyDAO studyDao, ConfigurationDao configurationDao, StudyBean studyBean,
			Map<String, List<DiscrepancyDescription>> dDescriptionsMap, DateUtil.DatePattern datePattern) {
		return validate(new Validator(new ValidatorHelper(RequestUtil.getRequest(), configurationDao)), studyDao,
				studyBean, dDescriptionsMap, datePattern);
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
	 * @return HashMap
	 */
	public static HashMap validate(Validator validator, StudyDAO studyDao, StudyBean studyBean,
			Map<String, List<DiscrepancyDescription>> dDescriptionsMap, DateUtil.DatePattern datePattern) {
		HttpServletRequest request = RequestUtil.getRequest();

		HashMap errors = new HashMap();
		boolean editMode = studyBean != null;
		Locale locale = LocaleResolver.getLocale();
		FormProcessor fp = new FormProcessor(request);
		ResourceBundle exceptionsBundle = ResourceBundleProvider.getExceptionsBundle();
		ResourceBundle pageMessagesBundle = ResourceBundleProvider.getPageMessagesBundle();

		validator.addValidation(StudyParameter.STUDY_NAME.getName(), Validator.NO_BLANKS);
		validator.addValidation(StudyParameter.PROTOCOL_ID.getName(), Validator.NO_BLANKS);
		validator.addValidation(StudyParameter.SUMMARY.getName(), Validator.NO_BLANKS);
		validator.addValidation(StudyParameter.PRINCIPAL_INVESTIGATOR.getName(), Validator.NO_BLANKS);
		validator.addValidation(StudyParameter.SPONSOR.getName(), Validator.NO_BLANKS);

		validator.addValidation(StudyParameter.SECOND_PRO_ID.getName(), Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, VALIDATION_NUM5);
		validator.addValidation(StudyParameter.COLLABORATORS.getName(), Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, VALIDATION_NUM7);
		validator.addValidation(StudyParameter.DESCRIPTION.getName(), Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, VALIDATION_NUM7);

		validateDate(fp, errors, exceptionsBundle, StudyParameter.START_DATE.getName(), datePattern, locale);
		validateDate(fp, errors, exceptionsBundle, StudyParameter.END_DATE.getName(), datePattern, locale);
		validateDate(fp, errors, exceptionsBundle, StudyParameter.APPROVAL_DATE.getName(), datePattern,
				locale);

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

		if (fp.getString(StudyParameter.STUDY_NAME.getName()).trim().length() > VALIDATION_NUM4) {
			Validator.addError(errors, StudyParameter.STUDY_NAME.getName(),
					exceptionsBundle.getString("maximum_lenght_name_100"));
		}
		if (fp.getString(StudyParameter.PROTOCOL_ID.getName()).trim().length() > VALIDATION_NUM2) {
			Validator.addError(errors, StudyParameter.PROTOCOL_ID.getName(),
					exceptionsBundle.getString("maximum_lenght_unique_protocol_30"));
		}
		if (fp.getString(StudyParameter.SUMMARY.getName()).trim().length() > VALIDATION_NUM8) {
			Validator.addError(errors, StudyParameter.SUMMARY.getName(),
					exceptionsBundle.getString("maximum_lenght_brief_summary_2000"));
		}
		if (fp.getString(StudyParameter.PRINCIPAL_INVESTIGATOR.getName()).trim().length() > VALIDATION_NUM5) {
			Validator.addError(errors, StudyParameter.PRINCIPAL_INVESTIGATOR.getName(),
					exceptionsBundle.getString("maximum_lenght_principal_investigator_255"));
		}
		if (fp.getString(StudyParameter.SPONSOR.getName()).trim().length() > VALIDATION_NUM5) {
			Validator.addError(errors, StudyParameter.SPONSOR.getName(),
					exceptionsBundle.getString("maximum_lenght_sponsor_255"));
		}
		if (fp.getString(StudyParameter.OFFICIAL_TITLE.getName()).trim().length() > VALIDATION_NUM5) {
			Validator.addError(errors, StudyParameter.OFFICIAL_TITLE.getName(),
					exceptionsBundle.getString("maximum_lenght_official_title_255"));
		}
		if (fp.getString(StudyConfigurationParameter.STUDY_SUBJECT_ID_LABEL.getName()).trim()
				.length() > VALIDATION_NUM5) {
			Validator.addError(errors, StudyConfigurationParameter.STUDY_SUBJECT_ID_LABEL.getName(),
					exceptionsBundle.getString("maximum_lenght_studySubjectIdLabel_255"));
		}
		if (fp.getString(StudyConfigurationParameter.SECONDARY_ID_LABEL.getName()).trim().length() > VALIDATION_NUM5) {
			Validator.addError(errors, StudyConfigurationParameter.SECONDARY_ID_LABEL.getName(),
					exceptionsBundle.getString("maximum_lenght_secondaryIdLabel_255"));
		}
		if (fp.getString(StudyConfigurationParameter.DATE_OF_ENROLLMENT_FOR_STUDY_LABEL.getName()).trim()
				.length() > VALIDATION_NUM5) {
			Validator.addError(errors, StudyConfigurationParameter.DATE_OF_ENROLLMENT_FOR_STUDY_LABEL.getName(),
					exceptionsBundle.getString("maximum_lenght_dateOfEnrollmentForStudyLabel_255"));
		}
		if (fp.getString(StudyConfigurationParameter.GENDER_LABEL.getName()).trim().length() > VALIDATION_NUM5) {
			Validator.addError(errors, StudyConfigurationParameter.GENDER_LABEL.getName(),
					exceptionsBundle.getString("maximum_lenght_genderLabel_255"));
		}
		if (fp.getString(StudyConfigurationParameter.START_DATE_TIME_LABEL.getName()).trim()
				.length() > VALIDATION_NUM5) {
			Validator.addError(errors, StudyConfigurationParameter.START_DATE_TIME_LABEL.getName(),
					exceptionsBundle.getString("maximum_lenght_startDateTimeLabel_255"));
		}
		if (fp.getString(StudyConfigurationParameter.END_DATE_TIME_LABEL.getName()).trim().length() > VALIDATION_NUM5) {
			Validator.addError(errors, StudyConfigurationParameter.END_DATE_TIME_LABEL.getName(),
					exceptionsBundle.getString("maximum_lenght_endDateTimeLabel_255"));
		}
		if (fp.getInt(StudyParameter.TOTAL_ENROLLMENT.getName()) <= 0) {
			Validator.addError(errors, StudyParameter.TOTAL_ENROLLMENT.getName(),
					pageMessagesBundle.getString("expected_total_enrollment_must_be_a_positive_number"));
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
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, StudyValidator.VALIDATION_NUM5);
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
			String parameterName, DateUtil.DatePattern datePattern, Locale locale) {
		try {
			if (!fp.getString(parameterName).isEmpty()) {
				DateUtil.parseDateString(fp.getString(parameterName), datePattern, locale);
			}
		} catch (Exception ex) {
			Validator.addError(errors, parameterName, exceptionsBundle.getString("input_not_valid_date_time")
					+ datePattern.getPattern() + " " + exceptionsBundle.getString("format2") + ".");
		}
	}

	private static boolean isNotTheSameStudy(StudyBean currentStudy, StudyBean anotherStudy) {
		return currentStudy == null || currentStudy.getId() != anotherStudy.getId();
	}

	private static void addValidatorIfParamPresented(HttpServletRequest request, String paramName, Validator v,
			int validatorType) {
		if (request.getParameter(paramName) != null) {
			v.addValidation(paramName, validatorType);
		}
	}
}
