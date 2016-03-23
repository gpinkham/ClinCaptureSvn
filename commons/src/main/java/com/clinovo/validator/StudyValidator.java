package com.clinovo.validator;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.dao.hibernate.ConfigurationDao;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;

import com.clinovo.util.RequestUtil;
import com.clinovo.util.ValidatorHelper;

/**
 * StudyBean validator.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
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
			ResourceBundle respage, ResourceBundle resexception, StudyBean currentStudy) {
		ArrayList<StudyBean> allStudies = (ArrayList<StudyBean>) studyDAO.findAll();
		boolean result = true;

		for (StudyBean thisBean : allStudies) {
			if (fp.getString("studyName").trim().equals(thisBean.getName())
					&& isNotTheSameStudy(currentStudy, thisBean)) {
				result = false;
				MessageFormat mf = new MessageFormat("");
				mf.applyPattern(respage.getString("brief_title_existed"));
				Object[] arguments = {fp.getString("studyName").trim()};
				Validator.addError(errors, "studyName", mf.format(arguments));
			}
			if (fp.getString("protocolId").trim().equals(thisBean.getIdentifier())
					&& isNotTheSameStudy(currentStudy, thisBean)) {
				result = false;
				Validator.addError(errors, "protocolId", resexception.getString("unique_protocol_id_existed"));
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
	 * @return HashMap
	 */
	public static HashMap validate(StudyDAO studyDao, ConfigurationDao configurationDao) {
		return validate(studyDao, configurationDao, null);
	}

	/**
	 * 
	 * @param studyDao
	 *            StudyDAO
	 * @param configurationDao
	 *            ConfigurationDao
	 * @param currentStudy
	 *            StudyBean
	 * @return HashMap
	 */
	public static HashMap validate(StudyDAO studyDao, ConfigurationDao configurationDao, StudyBean currentStudy) {
		return validate(new Validator(new ValidatorHelper(RequestUtil.getRequest(), configurationDao)), studyDao,
				currentStudy);
	}

	/**
	 * Method perform validation.
	 *
	 * @param validator
	 *            Validator
	 * @param studyDao
	 *            StudyDAO
	 * @param currentStudy
	 *            StudyBean
	 * @return HashMap
	 */
	public static HashMap validate(Validator validator, StudyDAO studyDao, StudyBean currentStudy) {
		HttpServletRequest request = RequestUtil.getRequest();

		HashMap errors = new HashMap();
		FormProcessor fp = new FormProcessor(request);
		ResourceBundle exceptionsBundle = ResourceBundleProvider.getExceptionsBundle();
		ResourceBundle pageMessagesBundle = ResourceBundleProvider.getPageMessagesBundle();

		boolean editMode = currentStudy != null;

		validator.addValidation("studyName", Validator.NO_BLANKS);
		validator.addValidation("protocolId", Validator.NO_BLANKS);
		validator.addValidation("summary", Validator.NO_BLANKS);
		validator.addValidation("principalInvestigator", Validator.NO_BLANKS);
		validator.addValidation("sponsor", Validator.NO_BLANKS);

		validator.addValidation("secondProId", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, VALIDATION_NUM5);
		validator.addValidation("collaborators", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, VALIDATION_NUM7);
		validator.addValidation("description", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, VALIDATION_NUM7);

		if (editMode) {
			validator.addValidation("instanceType", Validator.NO_BLANKS);
			validator.addValidation("instanceType", Validator.LENGTH_NUMERIC_COMPARISON,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, VALIDATION_NUM1);

			validator.addValidation("studySubjectIdLabel", Validator.NO_BLANKS);
			validator.addValidation("secondaryIdLabel", Validator.NO_BLANKS);
			validator.addValidation("dateOfEnrollmentForStudyLabel", Validator.NO_BLANKS);
			validator.addValidation("genderLabel", Validator.NO_BLANKS);

			validator.addValidation("startDateTimeLabel", Validator.NO_BLANKS);
			validator.addValidation("endDateTimeLabel", Validator.NO_BLANKS);
		} else {
			// check param presents before validation
			addValidatorIfParamPresented(request, "studySubjectIdLabel", validator, Validator.NO_BLANKS);
			addValidatorIfParamPresented(request, "secondaryIdLabel", validator, Validator.NO_BLANKS);
			addValidatorIfParamPresented(request, "dateOfEnrollmentForStudyLabel", validator, Validator.NO_BLANKS);
			addValidatorIfParamPresented(request, "genderLabel", validator, Validator.NO_BLANKS);
			addValidatorIfParamPresented(request, "startDateTimeLabel", validator, Validator.NO_BLANKS);
			addValidatorIfParamPresented(request, "endDateTimeLabel", validator, Validator.NO_BLANKS);
		}

		errors.putAll(validator.validate());

		StudyValidator.checkIfStudyFieldsAreUnique(fp, errors, studyDao, pageMessagesBundle, exceptionsBundle,
				currentStudy);

		if (fp.getString("studyName").trim().length() > VALIDATION_NUM4) {
			Validator.addError(errors, "studyName", exceptionsBundle.getString("maximum_lenght_name_100"));
		}
		if (fp.getString("protocolId").trim().length() > VALIDATION_NUM2) {
			Validator.addError(errors, "protocolId", exceptionsBundle.getString("maximum_lenght_unique_protocol_30"));
		}
		if (fp.getString("summary").trim().length() > VALIDATION_NUM8) {
			Validator.addError(errors, "summary", exceptionsBundle.getString("maximum_lenght_brief_summary_2000"));
		}
		if (fp.getString("principalInvestigator").trim().length() > VALIDATION_NUM5) {
			Validator.addError(errors, "principalInvestigator",
					exceptionsBundle.getString("maximum_lenght_principal_investigator_255"));
		}
		if (fp.getString("sponsor").trim().length() > VALIDATION_NUM5) {
			Validator.addError(errors, "sponsor", exceptionsBundle.getString("maximum_lenght_sponsor_255"));
		}
		if (fp.getString("officialTitle").trim().length() > VALIDATION_NUM5) {
			Validator.addError(errors, "officialTitle",
					exceptionsBundle.getString("maximum_lenght_official_title_255"));
		}
		if (fp.getString("studySubjectIdLabel").trim().length() > VALIDATION_NUM5) {
			Validator.addError(errors, "studySubjectIdLabel",
					exceptionsBundle.getString("maximum_lenght_studySubjectIdLabel_255"));
		}
		if (fp.getString("secondaryIdLabel").trim().length() > VALIDATION_NUM5) {
			Validator.addError(errors, "secondaryIdLabel",
					exceptionsBundle.getString("maximum_lenght_secondaryIdLabel_255"));
		}
		if (fp.getString("dateOfEnrollmentForStudyLabel").trim().length() > VALIDATION_NUM5) {
			Validator.addError(errors, "dateOfEnrollmentForStudyLabel",
					exceptionsBundle.getString("maximum_lenght_dateOfEnrollmentForStudyLabel_255"));
		}
		if (fp.getString("genderLabel").trim().length() > VALIDATION_NUM5) {
			Validator.addError(errors, "genderLabel", exceptionsBundle.getString("maximum_lenght_genderLabel_255"));
		}
		if (fp.getString("startDateTimeLabel").trim().length() > VALIDATION_NUM5) {
			Validator.addError(errors, "startDateTimeLabel",
					exceptionsBundle.getString("maximum_lenght_startDateTimeLabel_255"));
		}
		if (fp.getString("endDateTimeLabel").trim().length() > VALIDATION_NUM5) {
			Validator.addError(errors, "endDateTimeLabel",
					exceptionsBundle.getString("maximum_lenght_endDateTimeLabel_255"));
		}

		return errors;
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
