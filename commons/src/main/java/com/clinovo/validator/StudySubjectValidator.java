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

import com.clinovo.util.ValidatorHelper;

import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.form.DiscrepancyValidator;
import org.akaza.openclinica.control.form.FormDiscrepancyNotes;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.hibernate.ConfigurationDao;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Validator for StudySubjectBean.
 */
@SuppressWarnings("rawtypes")
public class StudySubjectValidator {

	public static final int INT_255 = 255;
	public static final int INT_30 = 30;
	public static final int INT_1900 = 1900;

	public static final String INPUT_UNIQUE_IDENTIFIER = "uniqueIdentifier";
	public static final String INPUT_DOB = "dateOfBirth";
	public static final String INPUT_YOB = "yearOfBirth";
	public static final String INPUT_GENDER = "gender";
	public static final String INPUT_LABEL = "label";
	public static final String INPUT_SECONDARY_LABEL = "secondaryLabel";
	public static final String INPUT_ENROLLMENT_DATE = "enrollmentDate";
	public static final String FORM_DISCREPANCY_NOTES_NAME = "fdnotes";
	public static final String LOCATION = "location";

	/**
	 * Validate StudySubjectBean.
	 *
	 * @param request          HttpServletRequest.
	 * @param configurationDao ConfigurationDao
	 * @param study            StudyBean
	 * @return HashMap of errors.
	 */
	public HashMap validate(HttpServletRequest request, ConfigurationDao configurationDao, StudyBean study
			, boolean executeStudySubjectValidations) {

		FormProcessor fp = new FormProcessor(request);
		FormDiscrepancyNotes discNotes = (FormDiscrepancyNotes) request.getSession().getAttribute(FORM_DISCREPANCY_NOTES_NAME);

		if (discNotes == null) {
			discNotes = new FormDiscrepancyNotes();
		}

		DiscrepancyValidator v = new DiscrepancyValidator(new ValidatorHelper(request, configurationDao),
				discNotes);

		if (executeStudySubjectValidations) {
			v.addValidation(INPUT_LABEL, Validator.NO_BLANKS);
			v.addValidation(INPUT_LABEL, Validator.LENGTH_NUMERIC_COMPARISON,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, INT_30);

			if (!"".equals(fp.getDateTimeInputString("enrollment"))) {
				v.addValidation(INPUT_ENROLLMENT_DATE, Validator.IS_A_DATE);
				v.alwaysExecuteLastValidation(INPUT_ENROLLMENT_DATE);
				v.addValidation(INPUT_ENROLLMENT_DATE, Validator.DATE_IN_PAST);
			}

			if (!StringUtil.isBlank(fp.getString(INPUT_SECONDARY_LABEL))) {
				v.addValidation(INPUT_SECONDARY_LABEL, Validator.LENGTH_NUMERIC_COMPARISON,
						NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, INT_30);
			}

			if (study.getStudyParameterConfig().getDateOfEnrollmentForStudyRequired().equals("yes")) {
				v.addValidation("enrollmentDate", Validator.NO_BLANKS);
			}

			if (study.getStudyParameterConfig().getSecondaryIdRequired().equals("yes")) {
				v.addValidation("secondaryLabel", Validator.NO_BLANKS);
			}
		}

		if (study.getStudyParameterConfig().getSubjectPersonIdRequired().equals("required")) {
			v.addValidation(INPUT_UNIQUE_IDENTIFIER, Validator.NO_BLANKS);
		}
		if (!study.getStudyParameterConfig().getSubjectPersonIdRequired().equals("not_used")) {
			v.addValidation(INPUT_UNIQUE_IDENTIFIER, Validator.LENGTH_NUMERIC_COMPARISON,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, INT_255);
		}

		String dobSetting = study.getStudyParameterConfig().getCollectDob();

		if (dobSetting.equals("1")) {
			// date of birth
			v.addValidation(INPUT_DOB, Validator.IS_A_DATE);
			if (!StringUtil.isBlank(fp.getString(INPUT_DOB))) {
				v.alwaysExecuteLastValidation(INPUT_DOB);
			}
			v.addValidation(INPUT_DOB, Validator.DATE_IN_PAST);
		} else if (dobSetting.equals("2")) {
			// year of birth
			v.addValidation(INPUT_YOB, Validator.IS_AN_INTEGER);
			v.alwaysExecuteLastValidation(INPUT_YOB);
			v.addValidation(INPUT_YOB, Validator.COMPARES_TO_STATIC_VALUE,
					NumericComparisonOperator.GREATER_THAN_OR_EQUAL_TO, INT_1900);

			Date today = new Date();
			Calendar c = Calendar.getInstance();
			c.setTime(today);
			int currentYear = c.get(Calendar.YEAR);
			v.addValidation(INPUT_YOB, Validator.COMPARES_TO_STATIC_VALUE,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, currentYear);
		}

		ArrayList<String> acceptableGenders = new ArrayList<String>();
		acceptableGenders.add("m");
		acceptableGenders.add("f");

		if (!study.getStudyParameterConfig().getGenderRequired().equals("false")) {
			v.addValidation(INPUT_GENDER, Validator.IS_IN_SET, acceptableGenders);
		}

		return v.validate();
	}
}
