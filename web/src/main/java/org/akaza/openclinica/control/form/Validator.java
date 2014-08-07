/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2014 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License 
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.form;

import com.clinovo.util.ValidatorHelper;
import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.bean.core.EntityAction;
import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.TermType;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.core.Utils;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ResponseOptionBean;
import org.akaza.openclinica.bean.submit.ResponseSetBean;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.EntityDAO;
import org.akaza.openclinica.dao.hibernate.PasswordRequirementsDao;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * 
 * A class to validate form input.
 * 
 * <p>
 * The general usage of the Validator is as follows:
 * 
 * <code>
 * Validator v = new Validator(new ValidatorHelper(request, getConfigurationDao())); // request is an HttpServletRequest object
 * 
 * // fieldName is the name of your HTML input field.
 * // validationType is one of the Validator static ints
 * // args depends on which type of validation you re doing,
 * // see below for details
 * v.addValidation(fieldName, validationType, args);
 * 
 * // the following step is optional
 * // if you don't like the default error message for the validationType you are using,
 * // you can specify an error message specific to your form here
 * // note that this call must come after the addValidation call for the input
 * // you are validating, and before the next addValidation call
 * v.setErrorMessage(customErrorMessage);
 * 
 * // add more validations as necessary
 * HashMap errors = v.validate();
 * 
 * if (errors.isEmpty()) {
 *      // this means all of your validations were successful;
 *      // you can proceed with writing to the database or whatever
 * }
 * 
 * else {
 *      // this means at least one of your fields did not validate properly.
 * }
 * </code>
 * 
 * <p>
 * To determine whether there were any errors on the form, you use code like this:
 * 
 * <code>
 * ArrayList fieldMessages = errors.get(fieldname);
 * 
 * if (fieldMessages.isEmpty()) {
 *      // there were no errors on the form
 * }
 * else {
 *      // there were errors on the form
 * }
 * </code>
 * 
 * <p>
 * There are 15 types of validations possible; below there are details on the semantics as well as the syntax of the
 * addValidation call:
 * 
 * <ul>
 * <li>NO_BLANKS test that a given input is not blank
 * <ul>
 * <li>addValidation(fieldname, Validator.NO_BLANKS);
 * </ul>
 * <li>IS_A_NUMBER test that a given input is a valid number, e.g. "5", "5.5", "-5", etc.
 * <ul>
 * <li>addValidation(fieldname, Validator.IS_A_NUMBER);
 * </ul>
 * <li>IS_IN_RANGE test that a given input is an integer in a specified range (inclusive), e.g. is between 1 and 5
 * inclusive
 * <ul>
 * <li>addValidation(fieldname, Validator.IS_IN_RANGE, lowerBound, upperBound);
 * <li>e.g. addValidation(fieldname, Validator.IS_IN_RANGE, 1, 5);
 * </ul>
 * <li>IS_A_DATE test that a given input is in "MM/DD/YYYY" format. Note that this format is specified as
 * Validator.DATE.getDescription() (DATE is a static ValidationRegularExpression object)
 * <ul>
 * <li>addValidation(fieldname, Validator.IS_A_DATE)
 * </ul>
 * <li>IS_DATE_TIME test that a given input is in "MM/DD/YYYY HH:MM a" format. Note that this format is specified as
 * Validator.DATE_TIME.getDescription() (DATE is a static ValidationRegularExpression object)
 * <ul>
 * <li>addValidation(prefix, Validator.IS_A_DATE)
 * <li>This validation is a bit different from the others. It assumes that you are attempting to validate a *group* of
 * inputs, all of which have the same prefix, and have the following suffixes:
 * <ul>
 * <li>Date (in MM/dd/yyyy format)
 * <li>Hour (1 - 12)
 * <li>Minute (0 - 59)
 * <li>Half ("am" or "pm")
 * </ul>
 * <li>e.g., if you have a start datetime and end datetime on the same form, you might have startDate, startHour,
 * startMinute, startHalf, and similarly for end.
 * <li>carrying on with this example, you could validate these inputs as follows:
 * <ul>
 * <li>addValidation("start", IS_DATE_TIME);
 * <li>addValidation("end", IS_DATE_TIME);
 * </ul>
 * <li>to complete the example, if errors exist on the form, they will appear in the "start" and "end" key of the error
 * messages
 * </ul>
 * <li>CHECK_SAME test that one field equals the value of another. This is null-safe meaning that if both fields are
 * blank, no errors are reported.
 * <ul>
 * <li>addValidation(fieldname, Validator.CHECK_SAME, fieldname2)
 * <li>e.g. addValidation( password , Validator.CHECK_SAME, confirmPassword );
 * </ul>
 * <li>IS_A_EMAIL test that one field is in proper e-mail format, ie "username@institution.domain". Note that this
 * format is specified as Validator.EMAIL.getDescription() (EMAIL is a static ValidationRegularExpression object)
 * <ul>
 * <li>addValidation(fieldname, Validator.IS_A_EMAIL);
 * </ul>
 * <li>IS_A_PHONE_NUMBER - test that one field is in proper phone number format, ie "123-456-7890". Note that this
 * format is specified as Validator.PHONE_NUMBER.getDescription() (PHONE_NUMBER is a static ValidationRegularExpression
 * object)
 * <ul>
 * <li>addValidation(fieldname, Validator.IS_A_PHONE_NUMER);
 * </ul>
 * <li>LENGTH_NUMERIC_COMPARISON test that the length of a string meets some numeric comparison test, e.g.
 * ">= 8 characters long".
 * <ul>
 * <li>addValidation(fieldname, Validator.LENGTH_NUMERIC_COMPARISON, operator, compareTo);
 * <li>operator is a NumericComparisonOperator object. (NumericComparisonOperator is a typesafe enumeration which
 * implements a controlled vocabulary of = , != , < , <= , > , >= )
 * <li>compareTo is an integer
 * <li>e.g. addValidation( password , Validator.LENGTH_NUMERIC_COMPARISON,
 * NumericComparisonOperator.GREATER_THAN_OR_EQUAL_TO, 8);
 * </ul>
 * <li>ENTITY_EXISTS not yet implemented, but it will test that the value of an input field holds the primary key of
 * some entity in the database; the entity type is implicitly specified by an EntityDAO passed to addValidation
 * <li>USERNAME_UNIQUE - not yet implemented, but it will test that there are no users with the specified username
 * <li>IS_AN_INTEGER test that a string is a valid integer
 * <ul>
 * <li>addValidation(fieldname, Validator.IS_AN_INTEGER)
 * </ul>
 * <li>IS_A_FILE not yet implemented, but it will test that the value of an input is a successfully updated file
 * <li>IS_OF_FILE_TYPE not yet implemented, but it will test that an uploaded file has a specified file type
 * <li>IS_IN_SET test that an input field belongs to some ad-hoc set
 * <ul>
 * <li>addValidation(fieldname, Validator.IS_IN_SET, set);
 * <li>set is an ArrayList of Entities
 * </ul>
 * <li>IS_A_PASSWORD test that an input field is a valid password
 * <ul>
 * <li>addValidation(fieldname, Validator.IS_A_PASSWORD);
 * </ul>
 * <li>IS_A_USERNAME - test that an input field is a valid username
 * <ul>
 * <li>addValidation(fieldname, Validator.IS_A_USERNAME, username);
 * </ul>
 * <li>IS_VALID_TERM test that an input field holds the id of some controlled vocabulary term
 * <ul>
 * <li>addValidation(fieldname, Validator.IS_VALID_TERM, termType);
 * <li>termType is a TermType object
 * <li>TermType is a controlled vocabulary of controlled vocabularies, e.g. it lists all of the controlled vocabularies
 * <li>e.g. addValidation( statusId , Validator.IS_VALID_TERM, TermType.STATUS);
 * </ul>
 * <li>COMPARES_TO_STATIC_VALUE test that an input field is an integer which matches a Boolean comparison to some fixed
 * integer value
 * <ul>
 * <li>addValidation(fieldname, Validator.COMPARES_TO_STATIC_VALUE, operator, compareTo);
 * <li>operator is a NumericComparisonOperator object
 * <li>e.g. addValidation( numSubjects , Validator.COMPARES_TO_STATIC_VALUE,
 * NumericComparisonOperator.GREATER_THAN_OR_EQUAL_TO, 1)
 * </ul>
 * <li>DATE_IS_AFTER_OR_EQUAL - test that one date comes after or is equal to another
 * <ul>
 * <li>addValidation(laterDateFieldName, Validator.DATE_IS_AFTER_OR_EQUAL, earlierDateFieldName);
 * <li>e.g. addValidation("endDate", Validator.DATE_IS_AFTER_OR_EQUAL, "startDate");
 * </ul>
 * <li>ENTITY_EXISTS_IN_STUDY - test that an entity exists in a particular study
 * <ul>
 * <li>addValidation(entityIDFieldName, Validator.ENTITY_EXISTS_IN_STUDY, auditableEntityDAO, studyBean);
 * <li>note that the DAO provided must define findByPKAndStudyName; otherwise this validation always fails.
 * <li>e.g. addValidation("studySubject", Validator.ENTITY_EXISTS_IN_STUDY, subjectStudyDAO, currentStudy);
 * </ul>
 * <li>NO_BLANKS_SET - test that at least one value was entered for a set (e.g. a checkbox or multiple select)
 * <ul>
 * <li>addValidation(fieldName, Validator.NO_BLANKS_SET);
 * <li>note that while the input name on the form should include brackets (e.g. "input123[]"), brackets should not be
 * included in the call to addValidation
 * <li>e.g. addValidation("input123", Validator.NO_BLANKS_SET);
 * </ul>
 * </ul>
 * 
 * <p>
 * How to add a validation type
 * </p>
 * 
 * <p>
 * We will take, as an example, a new type of validation which checks that the input field contains a multiple of some
 * integer (specified by the control.) For example, if you are asking the user to input time in 15-minute increments,
 * you can use this validation to ensure that the user enters a multiple of 15.
 * 
 * <ul>
 * <li>assign the type a public static int, e.g. IS_MULTIPLE
 * <li>if necessary, write an addValidation method to store that type of validation in the validations HashMap. make
 * sure to note that the addValidation method you wrote is for the validation type in the comments. <br/>
 * a new addValidation method is necessary if there are no other addValidation methods which accept the kind of
 * arguments you need. in this case, we need only the multiple argument (15, in the example used above.) In fact, there
 * is no such method (at the time of writing), so we add one: <br/>
 * <br/>
 * <code>
 * public void addValidation(String fieldName, int type, int multiple) {
 *      // for use with IS_MULTIPLE validations
 *      Validation v = new Validation(type);
 *      v.addArgument(multiple);
 *      addValidation(fieldName, v);
 * }
 * </code>
 * 
 * <br/>
 * <br/>
 * Note that in the code above, we added a comment to indicate which validation type the addValidation method is used
 * for. This is a courtesy to other developers who are using the class.
 * 
 * <li>if it's not necessary to write an addValidation method, determine which addValidation will be used to store the
 * type of validation you are writing, and note this fact in the comments for the appropriate addValidation method.
 * 
 * <li>if necessary, write a method to execute the validation, similar to isBlank, isNumber, etc. note that the utility
 * functions intComparesToStaticValue and matchesRegex can handle a wide variety of validations. don't reinvent the
 * wheel! <br/>
 * a new method is necessary, so we will write one: <br/>
 * <br/>
 * <code>
 * public boolean isMultiple(String fieldName, int multiple) {
 *      String fieldValue = request.getParameter(fieldName);
 * 
 *      if (fieldValue == null) {
 *          return false;
 *      }
 * 
 *      try {
 *          int i = Integer.parseInt(fieldValue);
 *          if ((i % multiple) != 0) {
 *              return false;
 *          }
 *      } catch (Exception e) {
 *          return false;
 *      }
 *      return true;
 * }
 * </code>
 * 
 * <li>in validate(String fieldName, Validation v, HashMap errors), add a case to respond to the new validation type be
 * sure to get all necessary arguments, determine if the input field passes validation, and call addError(fieldName, v)
 * if the field does not pass validation <br/>
 * For example: <br/>
 * <br/>
 * 
 * <code>
 *  // ... (top of switch statement)
 *  case IS_MULTIPLE:
 *      int multiple = v.getArgument(0);
 *      if (!isMultiple(fieldName, multiple)) {
 *          addError(fieldName, v);
 *      }
 *      break;
 *  // ... (bottom of switch statement)
 * </code>
 * 
 * <li>in addError(String fieldName, Validation v), add a case to set a default error message which will be displayed to
 * the user if an input violates the validation rule. <br/>
 * For example: <br/>
 * <br/>
 * 
 * <code>
 *  // ... (top of switch statement)
 *  case IS_MULTIPLE:
 *      int multiple = v.getArgument(0);
 *      errorMessage = "The input you provided is not a multiple of " + multiple + ".";
 *      break;
 *  // ... (bottom of switch statement)
 * </code>
 * </ul>
 * 
 * @author ssachs
 * 
 */

// subclassing the Validation class
// and making it more beefy (ie adding a checkIfValidated() type method to that
// class,
// so that the work is done there and not in this class)
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
public class Validator {

	public static final int TWENTY_FOUR = 24;
	public static final int NINE_NINE_NINE_NINE = 9999;
	public static final int FOUR_THOUSAND = 4000;
	public static final int TEN = 10;
	public static final int TWENTY_SIX = 26;
	public static final int THIRTY = 30;
	public static final int THREE = 3;
	public static final int SEVEN = 7;
	private final Logger logger = LoggerFactory.getLogger(getClass().getName());
	private Locale locale;
	private ResourceBundle resexception, resword;
	public static final int THOUSAND = 1000;

	/**
	 * getDateRegEx.
	 * 
	 * @return the regular expression object for localized dates
	 */
	public static ValidatorRegularExpression getDateRegEx() {
		ResourceBundle resformat = ResourceBundleProvider.getFormatBundle();
		return new ValidatorRegularExpression(resformat.getString("date_format"), resformat.getString("date_regexp"));
	}

	/**
	 * getDateTimeRegex.
	 * 
	 * @return regular expression with the localized date time regular expression
	 */
	public static ValidatorRegularExpression getDateTimeRegEx() {
		ResourceBundle resformat = ResourceBundleProvider.getFormatBundle();
		return new ValidatorRegularExpression(resformat.getString("date_time_format"),
				resformat.getString("date_time_regexp"));
	}

	public static final ValidatorRegularExpression EMAIL = new ValidatorRegularExpression(
			"username@institution.domain", ".+@.+\\..*");

	/**
	 * getPhoneRegEx.
	 * 
	 * @return the validator regular expression object with a localized phone number.
	 */
	public static ValidatorRegularExpression getPhoneRegEx() {
		ResourceBundle resformat = ResourceBundleProvider.getFormatBundle();
		return new ValidatorRegularExpression(resformat.getString("phone_format"), resformat.getString("phone_regexp"));
	}

	public static final ValidatorRegularExpression USERNAME = new ValidatorRegularExpression(
			"at least 5 alphanumeric or underscore characters", "[A-Za-z0-9_]{5,}");

	public static final int NO_BLANKS = 1;
	public static final int IS_A_FLOAT = 2;
	public static final int IS_IN_RANGE = 3;
	public static final int IS_A_DATE = 4;
	public static final int IS_A_IMPORT_DATE = 44;
	public static final int IS_A_IMPORT_PARTIAL_DATE = 45;
	public static final int IS_DATE_TIME = 21;
	public static final int CHECK_SAME = 5; // this is for matching passwords,
	// e.g.
	public static final int IS_A_EMAIL = 6;
	public static final int LENGTH_NUMERIC_COMPARISON = 7;
	public static final int ENTITY_EXISTS = 8; // for checking if a primary key
	// is valid
	public static final int USERNAME_UNIQUE = 9;
	public static final int IS_AN_INTEGER = 10;
	public static final int IS_A_FILE = 11; // to check for uploads, making sure
	// that there is a file present
	public static final int IS_OF_FILE_TYPE = 12;
	public static final int DATE_IS_AFTER_OR_EQUAL = 13;
	public static final int IS_IN_SET = 14; // for controlled vocabularies that
	// aren't in the db
	public static final int IS_A_PASSWORD = 15;
	public static final int IS_A_USERNAME = 16;
	public static final int IS_VALID_TERM = 17;
	public static final int COMPARES_TO_STATIC_VALUE = 18; // for comparisons
	// like
	// "NumQuestions >=
	// 1"
	public static final int ENTITY_EXISTS_IN_STUDY = 19;
	public static final int IS_A_PHONE_NUMBER = 20;
	public static final int NO_BLANKS_SET = 22;
	public static final int IN_RESPONSE_SET = 23;
	public static final int IN_RESPONSE_SET_COMMA_SEPERATED = 38;
	public static final int IN_RESPONSE_SET_SINGLE_VALUE = 24;
	public static final int MATCHES_INITIAL_DATA_ENTRY_VALUE = 25;
	public static final int IS_REQUIRED = 26;
	public static final int MATCHES_REGULAR_EXPRESSION = 27;
	public static final int DATE_IN_PAST = 28;
	public static final int CHECK_DIFFERENT = 29;
	public static final int DIFFERENT_NUMBER_OF_GROUPS_IN_DDE = 30;

	public static final int IS_A_DATE_WITHOUT_REQUIRED_CHECK = 31;
	public static final int CALCULATION_FAILED = 32;
	public static final int IS_PARTIAL_DATE = 34;
	public static final int IS_AN_RULE = 33;
	public static final int BARCODE_EAN_13 = 36;
	public static final int IS_VALID_WIDTH_DECIMAL = 35;

	public static final int TO_HIDE_CONDITIONAL_DISPLAY = 37;

	public static final int NO_SEMI_COLONS_OR_COLONS = 43;

	public static final int IS_A_POSITIVE_INTEGER = 46;
	/**
	 * The last field for which an addValidation method was invoked. This is used by setErrorMessage(String).
	 */
	protected String lastField;

	protected HashMap validations;

	protected HashMap errors;

	protected ValidatorHelper validatorHelper;

	protected ResourceBundle resformat;

	/**
	 * Validator, the main creation class for the object.
	 * 
	 * @param validatorHelper
	 *            which assists with the validation.
	 */
	public Validator(ValidatorHelper validatorHelper) {
		validations = new HashMap();
		errors = new HashMap();
		this.validatorHelper = validatorHelper;
		locale = validatorHelper.getLocale();
		resformat = ResourceBundleProvider.getFormatBundle(locale);
		resexception = ResourceBundleProvider.getExceptionsBundle(locale);
		resword = ResourceBundleProvider.getWordsBundle(locale);
		lastField = "";
	}

	protected ArrayList getFieldValidations(String fieldName) {
		ArrayList fieldValidations;

		if (validations.containsKey(fieldName)) {
			fieldValidations = (ArrayList) validations.get(fieldName);
		} else {
			fieldValidations = new ArrayList();
		}
		return fieldValidations;
	}

	/**
	 * addValidation, function used to squirrel away the validations until validate is called.
	 * 
	 * @param fieldName
	 *            String
	 * @param v
	 *            Validation
	 */
	public void addValidation(String fieldName, Validation v) {
		ArrayList fieldValidations = getFieldValidations(fieldName);
		fieldValidations.add(v);
		validations.put(fieldName, fieldValidations);
	}

	/**
	 * use for: NO_BLANKS, IS_A_NUMBER, IS_A_DATE, IS_A_EMAIL, IS_AN_INTEGER, IS_A_PASSWORD, IS_A_USERNAME,
	 * IS_A_PHONE_NUMBER, IS_DATE_TIME, NO_BLANKS_SET DATE_IN_PAST.
	 * 
	 * @param fieldName
	 *            , a string for the field name in the form.
	 * @param type
	 *            , an int for the type.
	 */
	public void addValidation(String fieldName, int type) {
		Validation v = new Validation(type);
		addValidation(fieldName, v);
	}

	/**
	 * use for: IS_IN_RANGE.
	 * 
	 * @param fieldName
	 *            String
	 * @param type
	 *            int
	 * @param start
	 *            int
	 * @param end
	 *            int
	 */
	public void addValidation(String fieldName, int type, int start, int end) {
		// For finding out if a number is in a range

		Validation v = new Validation(type);
		v.addArgument(start);
		v.addArgument(end);

		addValidation(fieldName, v);
	}

	/**
	 * use for: CHECK_SAME, IS_A_FILE.
	 * 
	 * @param fieldName
	 *            String
	 * @param type
	 *            int
	 * @param dir
	 *            String
	 */
	public void addValidation(String fieldName, int type, String dir) {

		// for checking to see if there is a file present in the system or not
		// also for IS_OF_FILE_TYPE - in this case dir is a file type

		Validation v = new Validation(type);
		v.addArgument(dir);

		addValidation(fieldName, v);
	}

	/**
	 * use for: LENGHT_NUMERIC_COMPARISON, COMPARES_TO_STATIC_VALUE.
	 * 
	 * @param fieldName
	 *            String
	 * @param type
	 *            int
	 * @param operator
	 *            NumericComparisonOperator
	 * @param compareTo
	 *            int
	 */
	public void addValidation(String fieldName, int type, NumericComparisonOperator operator, int compareTo) {

		Validation v = new Validation(type);
		v.addArgument(operator);
		v.addArgument(compareTo);

		addValidation(fieldName, v);
	}

	/**
	 * use for: ENTITY_EXISTS_IN_STUDY.
	 * 
	 * @param fieldName
	 *            String
	 * @param type
	 *            int
	 * @param dao
	 *            AuditableEntityDAO
	 * @param study
	 *            StudyBean
	 */
	public void addValidation(String fieldName, int type, AuditableEntityDAO dao, StudyBean study) {
		// for entity exists validation
		Validation v = new Validation(type);
		v.addArgument(dao);
		v.addArgument(study);

		addValidation(fieldName, v);
	}

	/**
	 * use for: ENTITY_EXISTS.
	 * 
	 * @param fieldName
	 *            String
	 * @param type
	 *            int
	 * @param edao
	 *            EntityDAO
	 */
	public void addValidation(String fieldName, int type, EntityDAO edao) {
		// for entity exists validation
		Validation v = new Validation(type);
		v.addArgument(edao);

		addValidation(fieldName, v);
	}

	/**
	 * use for: IS_IN_SET and IS_VALID_WIDTH_DECIMAL.
	 * 
	 * @param fieldName
	 *            String
	 * @param type
	 *            int
	 * @param set
	 *            ArrayList
	 */
	public void addValidation(String fieldName, int type, ArrayList set) {

		Validation v = new Validation(type);
		v.addArgument(set);

		addValidation(fieldName, v);
	}

	/**
	 * use for: IS_VALID_TERM.
	 * 
	 * @param fieldName
	 *            String
	 * @param type
	 *            int
	 * @param termType
	 *            TermType
	 */
	public void addValidation(String fieldName, int type, TermType termType) {
		// assert type == is_valid_term

		Validation v = new Validation(type);
		v.addArgument(termType);

		addValidation(fieldName, v);
	}

	/**
	 * Add a validation to check that every response provided is an element of the specified set.
	 * 
	 * @param fieldName
	 *            The name of the input on the form.
	 * @param type
	 *            The type of validation. Should be IN_RESPONSE_SET or IN_RESPONSE_SET_SINGLE_VALUE.
	 * @param set
	 *            The response set to check for membership.
	 */
	public void addValidation(String fieldName, int type, ResponseSetBean set) {
		Validation v = new Validation(type);
		v.addArgument(set);
		addValidation(fieldName, v);
	}

	/**
	 * Add a validation to check that the specified input matches the value from initial data entry.
	 * 
	 * @param fieldName
	 *            The name of the input.
	 * @param type
	 *            The type of validation. Should be MATCHES_INITIAL_DATA_ENTRY_VALUE.
	 * @param idb
	 *            The bean representing the value from initial data entry.
	 * @param isMultiple
	 *            flag to let us know if multiples will be required or not.
	 */
	public void addValidation(String fieldName, int type, ItemDataBean idb, boolean isMultiple) {
		Validation v = new Validation(type);
		// we have to make this a a new String
		// to ensure that if someone calls idb.setValue()
		// before we get around to validating, we will still use the original
		// value
		v.addArgument(idb.getValue());
		v.addArgument(isMultiple);
		lastField = fieldName;
		// added tbh, 112007
		addValidation(fieldName, v);
	}

	/**
	 * Executes all of the validations which have been requested.
	 * 
	 * @return HashMap
	 */
	public HashMap validate() {
		Set keys = validations.keySet();

		for (Object key : keys) {
			String fieldName = (String) key;

			ArrayList fieldValidations = getFieldValidations(fieldName);
			for (Object fieldValidation : fieldValidations) {
				Validation v = (Validation) fieldValidation;
				logger.debug("fieldName=" + fieldName);
				validate(fieldName, v);
				if (errors.containsKey(fieldName)) {
					logger.debug("found an error for " + fieldName + " v-type: " + v.getType() + " "
							+ v.getErrorMessage() + ": " + getFieldValue(fieldName));
				} else {
					logger.debug("did NOT find an error for " + fieldName + " v-type: " + v.getType() + " "
							+ v.getErrorMessage() + ": " + getFieldValue(fieldName));
				}
			}
		}

		return errors;
	}

	/**
	 * Same as the validate() method, but does not validate against regex. This method is called ONLY FROM
	 * ImportCRFDataService class.
	 * 
	 * @param exceptionTxt
	 *            String
	 * @return HashMap
	 */
	public HashMap validate(String exceptionTxt) {
		Set keys = validations.keySet();

		for (Object key : keys) {
			String fieldName = (String) key;

			ArrayList fieldValidations = getFieldValidations(fieldName);
			for (Object fieldValidation : fieldValidations) {
				Validation v = (Validation) fieldValidation;
				logger.debug("fieldName=" + fieldName);
				if (v.getType() != MATCHES_REGULAR_EXPRESSION) {
					validate(fieldName, v);
				}
				if (errors.containsKey(fieldName)) {
					logger.debug("found an error for " + fieldName + " v-type: " + v.getType() + " "
							+ v.getErrorMessage() + ": " + getFieldValue(fieldName));
				} else {
					logger.debug("did NOT find an error for " + fieldName + " v-type: " + v.getType() + " "
							+ v.getErrorMessage() + ": " + getFieldValue(fieldName));
				}
			}
		}

		return errors;
	}

	/**
	 * quick debug function to look inside the validations set.
	 * 
	 * @return String
	 */
	public String getKeySet() {
		String retMe = "";
		Set keys = validations.keySet();

		for (Object key : keys) {
			String fieldName = (String) key;
			retMe += fieldName;
			ArrayList fieldValidations = getFieldValidations(fieldName);
			retMe += " found " + fieldValidations.size() + " field validations; ";
		}
		return retMe;
	}

	/**
	 * addError, adds an error after a validation pass.
	 * 
	 * @param fieldName
	 *            String
	 * @param v
	 *            Validation
	 */
	protected void addError(String fieldName, Validation v) {

		locale = validatorHelper.getLocale();
		resexception = ResourceBundleProvider.getExceptionsBundle(locale);
		resword = ResourceBundleProvider.getWordsBundle(locale);

		String errorMessage = "";

		if (v.isErrorMessageSet()) {
			errorMessage = v.getErrorMessage();
		} else {
			switch (v.getType()) {
			case NO_BLANKS:
				errorMessage = resexception.getString("field_not_blank");
				break;
			case IS_A_FLOAT:
				errorMessage = resexception.getString("field_should_number");
				break;
			case IS_IN_RANGE:
				float lowerBound = v.getFloat(0);
				float upperBound = v.getFloat(1);
				errorMessage = resexception.getString("input_should_be_between") + new Float(lowerBound).intValue()
						+ " " + resword.getString("and") + " " + new Float(upperBound).intValue() + ".";
				break;
			case IS_A_DATE:
				errorMessage = resexception.getString("input_not_valid_date") + getDateRegEx().getDescription() + " "
						+ resexception.getString("format1") + ".";
				break;
			case IS_PARTIAL_DATE:
				errorMessage = resexception.getString("input_not_partial_date") + " ("
						+ resformat.getString("date_format_year") + ", or "
						+ resformat.getString("date_format_year_month") + ", or "
						+ resformat.getString("date_format_string");
				break;
			case IS_A_IMPORT_DATE:
				errorMessage = resexception.getString("input_not_valid_pdate") + "yyyy-MM-dd" + " "
						+ resexception.getString("format1") + ".";
				break;
			case IS_A_IMPORT_PARTIAL_DATE:
				errorMessage = resexception.getString("input_not_valid_pdate") + "yyyy, or yyyy-MM, or yyyy-MM-dd"
						+ " " + resexception.getString("format1") + ".";
				break;
			case IS_DATE_TIME:
				errorMessage = resexception.getString("input_not_valid_date_time")
						+ getDateTimeRegEx().getDescription() + " " + resexception.getString("format2") + ".";
				break;
			case CHECK_SAME:
				errorMessage = resexception.getString("anwer_not_match");
				break;
			case IS_A_EMAIL:
				errorMessage = resexception.getString("input_not_valid_email") + EMAIL.getDescription() + " "
						+ resexception.getString("format3") + ".";
				break;
			case IS_A_PHONE_NUMBER:
				errorMessage = resexception.getString("input_not_valid_phone") + getPhoneRegEx().getDescription() + " "
						+ resexception.getString("format4") + ".";
				break;
			case ENTITY_EXISTS:
				errorMessage = resexception.getString("not_select_valid_entity");
				break;
			case ENTITY_EXISTS_IN_STUDY:
				errorMessage = resexception.getString("not_select_valid_entity_current_study");
				break;
			case USERNAME_UNIQUE:
				errorMessage = resexception.getString("username_already_exists");
				break;
			case IS_AN_INTEGER:
				errorMessage = resexception.getString("input_not_integer");
				break;
			case IS_IN_SET:
				errorMessage = resexception.getString("input_not_acceptable_option");
				break;
			case IS_A_PASSWORD:
				errorMessage = resexception.getString("password_must_be_at_least") + getPwdMinLen(validatorHelper)
						+ " " + resword.getString("characters_long") + ".";
				break;
			case IS_A_USERNAME:
				errorMessage = resexception.getString("input_not_valid_username") + USERNAME.getDescription() + " "
						+ resexception.getString("format5") + ".";
				break;
			case IS_VALID_TERM:
				errorMessage = resexception.getString("input_invalid");
				break;
			case COMPARES_TO_STATIC_VALUE:
				NumericComparisonOperator operator = (NumericComparisonOperator) v.getArg(0);
				float compareTo = v.getFloat(1);
				errorMessage = resexception.getString("input_provided_is_not") + operator.getDescription() + " "
						+ new Float(compareTo).intValue() + ".";
				break;
			case LENGTH_NUMERIC_COMPARISON:
				NumericComparisonOperator operator2 = (NumericComparisonOperator) v.getArg(0);
				int compareTo2 = v.getInt(1);

				errorMessage = resexception.getString("input_provided_is_not") + operator2.getDescription() + " "
						+ compareTo2 + " " + resword.getString("characters_long") + ".";
				break;
			case DATE_IS_AFTER_OR_EQUAL:
				String earlierDateFieldName = v.getString(0);

				String earlierDateValue = getFieldValue(earlierDateFieldName);
				if (earlierDateValue == null || earlierDateValue.equals("")) {
					errorMessage = resexception.getString("input_provided_not_precede_earlier");
				} else {
					errorMessage = resexception.getString("input_provided_not_precede") + earlierDateValue + ".";
				}
				break;
			case NO_BLANKS_SET:
				errorMessage = resexception.getString("must_choose_leat_one_value");
				break;
			case IN_RESPONSE_SET:
				errorMessage = resexception.getString("all_values_must_from_specified");
				break;
			case IN_RESPONSE_SET_COMMA_SEPERATED:
				errorMessage = resexception.getString("all_values_must_from_specified");
				break;
			case IN_RESPONSE_SET_SINGLE_VALUE:
				errorMessage = resexception.getString("values_must_from_valid");
				break;
			case DIFFERENT_NUMBER_OF_GROUPS_IN_DDE:
				errorMessage = resexception.getString("different_number_of_groups");
				break;
			case MATCHES_INITIAL_DATA_ENTRY_VALUE:
				String value = v.getString(0);
				errorMessage = resexception.getString("value_not_match") + " : " + value;
				break;
			case IS_REQUIRED:
				errorMessage = resexception.getString("input_is_required");
				break;
			case DATE_IN_PAST:
				errorMessage = resexception.getString("date_provided_not_past");
				break;
			case MATCHES_REGULAR_EXPRESSION:
				errorMessage = resexception.getString("input_not_match_regular_expression") + v.getString(0) + ".";
				break;
			case IS_A_DATE_WITHOUT_REQUIRED_CHECK:
				errorMessage = resexception.getString("input_not_valid_date") + getDateRegEx().getDescription() + " "
						+ resexception.getString("format1") + ".";
				break;
			case IS_AN_RULE:
				errorMessage = resexception.getString("input_not_integer");
				break;
			case BARCODE_EAN_13:
				errorMessage = resexception.getString("input_not_barcode");
				break;

			case NO_SEMI_COLONS_OR_COLONS:
				errorMessage = resexception.getString("field_not_have_colons_or_semi");
				break;

			case IS_A_POSITIVE_INTEGER:
				errorMessage = resexception.getString("field_not_a_positive_integer");
				break;
			default:
				logger.error("reached default on " + fieldName + ", unknown validation type with id " + v.getType());
			}
		}
		addError(fieldName, errorMessage);
	}

	/**
	 * addError, without the device of the container.
	 * 
	 * @param fieldName
	 *            String
	 * @param errorMessage
	 *            String
	 */
	protected void addError(String fieldName, String errorMessage) {
		Validator.addError(errors, fieldName, errorMessage);
	}

	/**
	 * Adds an error to a <code>HashMap</code> of errors generated by validate. This can be used for "one-off"
	 * validations, e.g.:
	 * 
	 * <code>
	 * errors = v.validate();
	 * 
	 * if (someSpecialConditionIsNotMet()) {
	 *     Validator.addError(errors, fieldName, "The special condition was not met.");
	 * }
	 * </code>
	 * 
	 * @param existingErrors
	 *            The <code>HashMap</code> of errors generated by a call to <code>validate()</code>.
	 * @param fieldName
	 *            The field name to add the error to.
	 * @param errorMessage
	 *            The message to add to the field name.
	 */
	public static void addError(HashMap existingErrors, String fieldName, String errorMessage) {
		ArrayList fieldErrors;

		if (existingErrors.containsKey(fieldName)) {
			fieldErrors = (ArrayList) existingErrors.get(fieldName);
		} else {
			fieldErrors = new ArrayList();
		}

		fieldErrors.add(errorMessage);

		existingErrors.put(fieldName, fieldErrors);
	}

	/**
	 * validate, does what it says on the tin.
	 * 
	 * @param fieldName
	 *            String
	 * @param v
	 *            Validation
	 * @return hashmap of error messages.
	 */
	protected HashMap validate(String fieldName, Validation v) {
		switch (v.getType()) {
		case NO_BLANKS:
			if (isBlank(fieldName)) {
				addError(fieldName, v);
			}
			break;
		case IS_A_FLOAT:
			if (!isFloat(fieldName)) {
				addError(fieldName, v);
			}
			break;
		case IS_IN_RANGE:
			float lowerBound = v.getFloat(0);
			float upperBound = v.getFloat(1);

			if (!isInRange(fieldName, lowerBound, upperBound)) {
				addError(fieldName, v);
			}
			break;
		case IS_A_DATE:
			if (!isDate(fieldName)) {
				addError(fieldName, v);
			}
			break;
		case IS_PARTIAL_DATE:
			boolean isPDate = Boolean.FALSE;
			String fieldValue = getFieldValue(fieldName);
			if (fieldValue != null) {
				if (StringUtil.isFormatDate(fieldValue, resformat.getString("date_format_string"), locale)
						|| StringUtil.isPartialYear(fieldValue, resformat.getString("date_format_year"), locale)
						|| StringUtil.isPartialYearMonth(fieldValue, resformat.getString("date_format_year_month"),
								locale)) {
					isPDate = true;
				}
			}
			if (!isPDate) {
				addError(fieldName, v);
			}
			break;
		case IS_A_IMPORT_DATE:
			if (!isImportDate(fieldName)) {
				addError(fieldName, v);
			}
			break;
		case IS_A_IMPORT_PARTIAL_DATE:
			boolean isImportPDate = Boolean.FALSE;
			String importFieldValue = getFieldValue(fieldName);
			if (importFieldValue != null) {
				if (StringUtil.isFormatDate(importFieldValue, "yyyy-MM-dd", locale)
						|| StringUtil.isPartialYear(importFieldValue, "yyyy", locale)
						|| StringUtil.isPartialYearMonth(importFieldValue, "yyyy-MM", locale)) {
					isImportPDate = true;
				}
			}
			if (!isImportPDate) {
				addError(fieldName, v);
			}
			break;
		case IS_DATE_TIME:
			if (!isDateTime(fieldName)) {
				addError(fieldName, v);
			}
			break;
		case CHECK_SAME:
			String compareField = v.getString(0);

			if (!isSame(fieldName, compareField)) {
				addError(fieldName, v);
			}
			break;
		case IS_A_EMAIL:
			if (!isEmail(fieldName)) {
				addError(fieldName, v);
			}
			break;
		case IS_A_PHONE_NUMBER:
			if (!isPhoneNumber(fieldName)) {
				addError(fieldName, v);
			}
			break;
		case ENTITY_EXISTS:
			EntityDAO edao = (EntityDAO) v.getArg(0);

			if (!entityExists(fieldName, edao)) {
				addError(fieldName, v);
			}
			break;
		case ENTITY_EXISTS_IN_STUDY:
			AuditableEntityDAO dao = (AuditableEntityDAO) v.getArg(0);
			StudyBean study = (StudyBean) v.getArg(1);

			if (!entityExistsInStudy(fieldName, dao, study)) {
				addError(fieldName, v);
			}
			break;
		case USERNAME_UNIQUE:
			UserAccountDAO udao = (UserAccountDAO) v.getArg(0);

			if (!usernameUnique(fieldName, udao)) {
				addError(fieldName, v);
			}
			break;
		case IS_AN_INTEGER:
			if (!isInteger(fieldName)) {
				addError(fieldName, v);
			}
			break;
		case IS_IN_SET:
			ArrayList set = (ArrayList) v.getArg(0);

			if (!isInSet(fieldName, set)) {
				addError(fieldName, v);
			}
			break;
		case IS_A_PASSWORD:
			int pwdMinLen = getPwdMinLen(validatorHelper);
			if (pwdMinLen > 0
					&& !lengthComparesToStaticValue(fieldName, NumericComparisonOperator.GREATER_THAN_OR_EQUAL_TO,
							pwdMinLen)) {
				addError(fieldName, v);
			}
			break;
		case IS_A_USERNAME:
			if (!isUsername(fieldName)) {
				addError(fieldName, v);
			}
			break;
		case IS_VALID_TERM:
			TermType termType = (TermType) v.getArg(0);

			if (!isValidTerm(fieldName, termType)) {
				addError(fieldName, v);
			}
			break;
		case COMPARES_TO_STATIC_VALUE:
			NumericComparisonOperator operator = (NumericComparisonOperator) v.getArg(0);
			float compareTo = v.getFloat(1);

			if (!comparesToStaticValue(fieldName, operator, compareTo)) {
				addError(fieldName, v);
			}
			break;
		case LENGTH_NUMERIC_COMPARISON:
			NumericComparisonOperator operator2 = (NumericComparisonOperator) v.getArg(0);
			int compareTo2 = v.getInt(1);

			if (!lengthComparesToStaticValue(fieldName, operator2, compareTo2)) {
				addError(fieldName, v);
			}
			break;
		case DATE_IS_AFTER_OR_EQUAL:
			String earlierDateFieldName = v.getString(0);

			if (!isDateAfterOrEqual(fieldName, earlierDateFieldName)) {
				addError(fieldName, v);
			}
			break;
		case NO_BLANKS_SET:
			if (isSetBlank(fieldName)) {
				addError(fieldName, v);
			}
			break;
		case IN_RESPONSE_SET:
			ResponseSetBean rsb = (ResponseSetBean) v.getArg(0);

			if (!isInResponseSet(fieldName, rsb, true)) {
				addError(fieldName, v);
			}
			break;
		case IN_RESPONSE_SET_COMMA_SEPERATED:
			ResponseSetBean rsbs = (ResponseSetBean) v.getArg(0);

			if (!isInResponseSetCommaSeperated(fieldName, rsbs, true)) {
				addError(fieldName, v);
			}
			break;
		case IN_RESPONSE_SET_SINGLE_VALUE:
			ResponseSetBean rsbSingle = (ResponseSetBean) v.getArg(0);

			if (!isInResponseSet(fieldName, rsbSingle, false)) {
				addError(fieldName, v);
			}
			break;
		case MATCHES_INITIAL_DATA_ENTRY_VALUE:
			String oldValue = v.getString(0);
			boolean isMultiple = v.getBoolean(1);
			if (!valueMatchesInitialValue(fieldName, oldValue, isMultiple)) {
				addError(fieldName, v);
			}
			break;
		case DIFFERENT_NUMBER_OF_GROUPS_IN_DDE:

			addError(fieldName, v);

			break;
		case IS_REQUIRED:
			if (isBlank(fieldName)) {
				addError(fieldName, v);
			}
			break;
		case DATE_IN_PAST:
			if (!isDateInPast(fieldName)) {
				addError(fieldName, v);
			}
			break;
		case MATCHES_REGULAR_EXPRESSION:
			if (!matchesRegex(fieldName, v)) {
				addError(fieldName, v);
			}
			break;
		case CHECK_DIFFERENT:
			String old = v.getString(0);

			if (isSame(fieldName, old)) {
				addError(fieldName, v);
			}
			break;
		case IS_A_DATE_WITHOUT_REQUIRED_CHECK:
			if (!isDateWithoutRequiredCheck(fieldName)) {
				addError(fieldName, v);
			}
			break;
		case CALCULATION_FAILED:
			addError(fieldName, v);
			break;
		case IS_AN_RULE:
			ArrayList<String> messages = (ArrayList<String>) v.getArg(0);
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < messages.size(); i++) {
				sb.append(messages.get(i));
				if (i != messages.size() - 1) {
					sb.append(" , ");
				}
				logger.debug(messages.get(i));
			}
			v.setErrorMessage(sb.toString());
			addError(fieldName, v);
			break;
		case IS_VALID_WIDTH_DECIMAL:
			ArrayList<String> params = (ArrayList<String>) v.getArg(0);
			String dataType = params.get(0);
			String widthDecimal = params.get(1);
			StringBuffer error = this.validateFieldWidthDecimal(fieldName, dataType, widthDecimal);
			if (error.length() > 0) {
				addError(fieldName, error.toString());
			}
			break;
		case BARCODE_EAN_13:
			EanCheckDigit eanChk = new EanCheckDigit();
			if (!eanChk.isValid(getFieldValue(fieldName))) {
				addError(fieldName, v);
			}
			break;
		case TO_HIDE_CONDITIONAL_DISPLAY:
			addError(fieldName, v);
			break;

		case NO_SEMI_COLONS_OR_COLONS:
			if (isColonSemiColon(fieldName)) {
				addError(fieldName, v);
			}
			break;

		case IS_A_POSITIVE_INTEGER:
			if (!isInteger(fieldName, true)) {
				addError(fieldName, v);
			}

			break;
		default:
			logger.error("Reached default on field name " + fieldName + ", found unknonwn validation type with id "
					+ v.getType());
		}
		return errors;
	}

	/*
	 * Instead of rewriting the whole Validation do this.
	 */
	protected String getFieldValue(String fieldName) {
		return validatorHelper.getParameter(fieldName) == null ? validatorHelper.getAttribute(fieldName) == null ? null
				: validatorHelper.getAttribute(fieldName).toString() : validatorHelper.getParameter(fieldName);
	}

	// validation functions that determine whether a field passes validation
	protected boolean isBlank(String fieldName) {
		String fieldValue = getFieldValue(fieldName);
		return fieldValue == null || fieldValue.trim().equals("");
	}

	protected boolean isColonSemiColon(String fieldName) {
		String fieldValue = getFieldValue(fieldName);
		return fieldValue.contains(";") || fieldValue.contains(":") || fieldValue.contains("*");
	}

	protected boolean isFloat(String fieldName) {
		String fieldValue = getFieldValue(fieldName);

		if (fieldValue == null) {
			return false;
		}

		if (fieldValue.equals("")) {
			return true;
		}

		try {
			float f = Float.parseFloat(fieldValue);
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	protected boolean isInRange(String fieldName, float lowerBound, float upperBound) {
		String fieldValue = getFieldValue(fieldName);

		if (fieldValue == null) {
			return false;
		}

		try {
			float i = Float.parseFloat(fieldValue);

			if (i >= lowerBound && i <= upperBound) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}

		return false;
	}

	/**
	 * @param fieldName
	 *            The name of a field containing some text string.
	 * @return <code>true</code> if the field contains a valid date in "MM/dd/yyyy" format; <code>false</code>
	 *         otherwise.
	 */
	protected boolean isDate(String fieldName) {
		String fieldValue = getFieldValue(fieldName);
		if (StringUtil.isBlank(fieldValue)) {
			return false;
		}
		if (!StringUtil.isFormatDate(fieldValue, resformat.getString("date_format_string"), locale)) {
			return false;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(resformat.getString("date_format_string"), locale);
		sdf.setLenient(false);
		try {
			java.util.Date date = sdf.parse(fieldValue);
			return isYearNotFourDigits(date);
		} catch (ParseException fe) {
			return false;
		}
	}

	protected boolean isImportDate(String fieldName) {
		String fieldValue = getFieldValue(fieldName);
		if (StringUtil.isBlank(fieldValue)) {
			return false;
		}
		if (!StringUtil.isFormatDate(fieldValue, "yyyy-MM-dd")) {
			return false;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setLenient(false);
		try {
			java.util.Date date = sdf.parse(fieldValue);
			return isYearNotFourDigits(date);
		} catch (ParseException fe) {
			return false;
		}
	}

	/**
	 * @param fieldName
	 *            The name of a field containing some text string.
	 * @return <code>true</code> if the field contains a valid date in "MM/dd/yyyy" format; <code>false</code> if the
	 *         given input is not date formatted or null or empty .
	 */
	protected boolean isDateWithoutRequiredCheck(String fieldName) {
		String fieldValue = validatorHelper.getParameter(fieldName);
		if (StringUtil.isBlank(fieldValue)) {
			return true;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(resformat.getString("date_format_string"),
				ResourceBundleProvider.getLocale());
		sdf.setLenient(false);
		try {
			java.util.Date date = sdf.parse(fieldValue);
			return isYearNotFourDigits(date);
		} catch (ParseException fe) {
			return false;
		}
	}

	/**
	 * @param fieldName
	 *            The name of a field containing some text string.
	 * @return <code>true</code> if the field contains a valid date in "MM/dd/yyyy" format and is in the past;
	 *         <code>false</code> otherwise.
	 */
	protected boolean isDateInPast(String fieldName) {
		Date d = null;
		if (fieldName != null) {
			d = FormProcessor.getDateFromString(getFieldValue(fieldName));
		}
		if (d != null) {
			Date today;
			Calendar cal = Calendar.getInstance();
			/*
			 * Adding one day with the current server date to allow validation for date entered form a client in forward
			 * timezone
			 */
			cal.add(Calendar.HOUR, TWENTY_FOUR);
			today = cal.getTime();
			if (today.compareTo(d) >= 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Assumes the date was entered in "MM/dd/yyyy" or "MM/dd/yyyy hh:mm a" format.
	 * 
	 * @param d
	 *            The date whose year we want to query for having four or less digits.
	 * @return <code>true</code> if the date's year has less than four digits; <code>false</code> otherwise.
	 */
	protected boolean isYearNotFourDigits(Date d) {

		Calendar c = Calendar.getInstance();
		c.setTime(d);
		return !(c.get(Calendar.YEAR) < THOUSAND || c.get(Calendar.YEAR) > NINE_NINE_NINE_NINE);
	}

	/**
	 * @param prefix
	 *            The prefix for a set of fields which together are used to input a date/time.
	 * @return <code>true</code> if the fields encode a valid date/time in "date_time_format_string" format.
	 *         <code>false</code> otherwise.
	 */
	protected boolean isDateTime(String prefix) {
		String date = getFieldValue(prefix + "Date");
		String hour = getFieldValue(prefix + "Hour");
		String minute = getFieldValue(prefix + "Minute");
		String half = getFieldValue(prefix + "Half");

		if (date == null || hour == null || minute == null || half == null) {
			logger.info("one of the date time fields is null");
			return false;
		}

		// If no input for any of these 3 fields, it means that
		// time is not specified.
		// In this case, 12:00 am has been set as default according to original
		// setting
		if ("-1".equals(hour) && "-1".equals(minute) && "".equals(half)) {
			hour = "12";
			minute = "00";
			half = "am";
		}
		if (hour.length() == 1) {
			hour = "0" + hour;
		}
		if (minute.length() == 1) {
			minute = "0" + minute;
		}
		// YW >>
		String fieldValue = date + " " + hour + ":" + minute + ":00 " + half;
		SimpleDateFormat sdf = new SimpleDateFormat(resformat.getString("date_time_format_string"), locale);
		sdf.setLenient(false);
		try {
			java.util.Date result = sdf.parse(fieldValue);
			return isYearNotFourDigits(result);
		} catch (Exception fe) {
			return false;
		}
	}

	protected boolean isUsername(String fieldName) {
		return matchesRegex(fieldName, USERNAME);
	}

	protected boolean isSame(String field1, String field2) {
		String value1 = getFieldValue(field1);
		String value2 = getFieldValue(field2);

		return value1 == null && value2 == null || value1 != null && value2 != null && value1.equals(value2);

	}

	protected boolean isEmail(String fieldName) {
		return matchesRegex(fieldName, EMAIL);
	}

	protected boolean isPhoneNumber(String fieldName) {
		return matchesRegex(fieldName, getPhoneRegEx());
	}

	protected boolean lengthComparesToStaticValue(String fieldName, NumericComparisonOperator operator, int compareTo) {
		String fieldValue = getFieldValue(fieldName);

		if (fieldValue == null) {
			return false;
		}

		try {
			int length = fieldValue.length();
			return intComparesToStaticValue(length, operator, compareTo);
		} catch (Exception e) {
			return false;
		}
	}

	protected boolean isInteger(String fieldName) {
		return isInteger(fieldName, false);
	}

	protected boolean isInteger(String fieldName, boolean checkPositive) {
		String fieldValue = getFieldValue(fieldName);

		if (fieldValue == null) {
			return false;
		}

		if (fieldValue.equals("")) {
			return true;
		}

		try {
			int i = Integer.parseInt(fieldValue);
		} catch (Exception e) {
			return false;
		}
		return !(checkPositive && Integer.parseInt(fieldValue) < 0);
	}

	protected boolean isInSet(String fieldName, ArrayList set) {
		String fieldValue = getFieldValue(fieldName);

		return fieldValue != null && set != null && set.contains(fieldValue);

	}

	protected boolean isValidTerm(String fieldName, TermType termType) {
		String fieldValue = getFieldValue(fieldName);

		if (fieldValue == null) {
			return false;
		}

		try {
			int i = Integer.parseInt(fieldValue);

			if (termType.equals(TermType.ENTITY_ACTION)) {
				return EntityAction.contains(i);
			} else if (termType.equals(TermType.ROLE)) {
				return Role.contains(i);
			} else if (termType.equals(TermType.STATUS)) {
				return Status.contains(i);
			} else if (termType.equals(TermType.USER_TYPE)) {
				return UserType.contains(i);
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	protected boolean comparesToStaticValue(String fieldName, NumericComparisonOperator operator, float compareTo) {
		String fieldValue = getFieldValue(fieldName);

		if (fieldValue == null) {
			return false;
		}

		try {
			float i = Float.parseFloat(fieldValue);
			return floatComparesToStaticValue(i, operator, compareTo);
		} catch (Exception e) {
			return false;
		}
	}

	// utility functions used by the validation functions
	protected boolean intComparesToStaticValue(int i, NumericComparisonOperator operator, int compareTo) {
		boolean compares = false;

		if (operator.equals(NumericComparisonOperator.EQUALS)) {
			compares = i == compareTo;
		} else if (operator.equals(NumericComparisonOperator.NOT_EQUALS)) {
			compares = i != compareTo;
		} else if (operator.equals(NumericComparisonOperator.GREATER_THAN)) {
			compares = i > compareTo;
		} else if (operator.equals(NumericComparisonOperator.GREATER_THAN_OR_EQUAL_TO)) {
			compares = i >= compareTo;
		} else if (operator.equals(NumericComparisonOperator.LESS_THAN)) {
			compares = i < compareTo;
		} else if (operator.equals(NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO)) {
			compares = i <= compareTo;
		}

		return compares;
	}

	// utility functions used by the validation functions
	protected boolean floatComparesToStaticValue(float i, NumericComparisonOperator operator, float compareTo) {
		boolean compares = false;

		if (operator.equals(NumericComparisonOperator.EQUALS)) {
			compares = i == compareTo;
		} else if (operator.equals(NumericComparisonOperator.NOT_EQUALS)) {
			compares = i != compareTo;
		} else if (operator.equals(NumericComparisonOperator.GREATER_THAN)) {
			compares = i > compareTo;
		} else if (operator.equals(NumericComparisonOperator.GREATER_THAN_OR_EQUAL_TO)) {
			compares = i >= compareTo;
		} else if (operator.equals(NumericComparisonOperator.LESS_THAN)) {
			compares = i < compareTo;
		} else if (operator.equals(NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO)) {
			compares = i <= compareTo;
		}

		return compares;
	}

	private boolean matchesRegex(String fieldName, Validation v) {
		ValidatorRegularExpression vre = new ValidatorRegularExpression(v.getString(0), v.getString(0));
		return matchesRegex(fieldName, v, vre);
	}

	private boolean matchesRegex(String fieldName, ValidatorRegularExpression re) {
		return matchesRegex(fieldName, null, re);
	}

	private boolean matchesRegex(String fieldName, Validation v, ValidatorRegularExpression re) {
		String fieldValue = prepareFieldValue(getFieldValue(fieldName), v);

		if (fieldValue == null) {
			return false;
		}

		Pattern p;
		try {
			p = Pattern.compile(re.getRegularExpression());
		} catch (PatternSyntaxException pse) {
			return false;
		}
		Matcher m = p.matcher(fieldValue);

		return m.matches();

	}

	private String prepareFieldValue(String fieldValue, Validation v) {
		if (v != null && fieldValue != null && !fieldValue.isEmpty()) {
			if (v.isConvertDate()) {
				fieldValue = Utils.convertedItemDateValue(fieldValue, "yyyy-MM-dd",
						resformat.getString("date_format_string"), locale);
			} else if (v.isConvertPDate()) {
				if (StringUtil.isFormatDate(fieldValue, "yyyy-MM-dd")) {
					fieldValue = Utils.convertedItemDateValue(fieldValue, "yyyy-MM-dd",
							resformat.getString("date_format_string"), locale);
				} else if (StringUtil.isPartialYear(fieldValue, "yyyy")) {
					fieldValue = Utils.convertedItemDateValue(fieldValue, "yyyy",
							resformat.getString("date_format_year"), locale);
				} else if (StringUtil.isPartialYearMonth(fieldValue, "yyyy-MM")) {
					fieldValue = Utils.convertedItemDateValue(fieldValue, "yyyy-MM",
							resformat.getString("date_format_year_month"), locale);
				}
			}
		}
		return fieldValue;
	}

	protected boolean entityExists(String fieldName, EntityDAO edao) {
		String fieldValue = getFieldValue(fieldName);

		if (fieldValue == null) {
			return false;
		}

		try {
			int id = Integer.parseInt(fieldValue);
			EntityBean e = edao.findByPK(id);

			if (!e.isActive()) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	protected boolean entityExistsInStudy(String fieldName, AuditableEntityDAO dao, StudyBean study) {

		String fieldValue = getFieldValue(fieldName);

		if (fieldValue == null) {
			return false;
		}

		try {
			int id = Integer.parseInt(fieldValue);
			AuditableEntityBean e = dao.findByPKAndStudy(id, study);

			if (!e.isActive()) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	protected boolean usernameUnique(String fieldName, UserAccountDAO udao) {
		String fieldValue = getFieldValue(fieldName);

		if (fieldValue == null) {
			return true;
		}

		try {
			UserAccountBean ub = (UserAccountBean) udao.findByUserName(fieldValue);

			if (ub.isActive()) {
				return false;
			}
		} catch (Exception e) {
			return true;
		}

		return true;
	}

	protected boolean isDateAfterOrEqual(String laterDateFieldName, String earlierDateFieldName) {
		String laterDateValue = getFieldValue(laterDateFieldName);
		String earlierDateValue = getFieldValue(earlierDateFieldName);

		if (laterDateValue == null || earlierDateValue == null) {
			return false;
		}

		Date laterDate = FormProcessor.getDateFromString(laterDateValue);
		Date earlierDate = FormProcessor.getDateFromString(earlierDateValue);

		return laterDate.compareTo(earlierDate) >= 0;
	}

	protected boolean isSetBlank(String fieldName) {
		String[] fieldValues = validatorHelper.getParameterValues(fieldName);

		return fieldValues == null || fieldValues.length == 0;

	}

	protected boolean isInResponseSet(String fieldName, ResponseSetBean set, boolean multValues) {
		// prep work - makes checking for a value in the set very fast
		HashMap values = new HashMap();

		ArrayList options = set.getOptions();
		for (Object option : options) {
			ResponseOptionBean rob = (ResponseOptionBean) option;
			values.put(rob.getValue(), Boolean.TRUE);
		}

		String[] fieldValues;
		if (multValues) {
			fieldValues = validatorHelper.getParameterValues(fieldName);
		} else {
			fieldValues = new String[1];
			String fieldValue = getFieldValue(fieldName);
			fieldValues[0] = fieldValue == null ? "" : fieldValue;
			if (fieldValues[0].equals("")) {
				return true;
			}
		}

		// this means the user didn't fill in anything - and nothing is still,
		// trivially, in the response set
		if (fieldValues == null) {
			return true;
		}

		for (String value : fieldValues) {
			// in principle this shouldn't happen, but since the empty valye is
			// trivially a member of the response set, it's okay
			if (value == null) {
				continue;
			}

			if (!values.containsKey(value)) {
				return false;
			}
		}

		return true;
	}

	protected boolean isInResponseSetCommaSeperated(String fieldName, ResponseSetBean set, boolean multValues) {
		// prep work - makes checking for a value in the set very fast
		HashMap values = new HashMap();

		ArrayList options = set.getOptions();
		for (Object option : options) {
			ResponseOptionBean rob = (ResponseOptionBean) option;
			values.put(rob.getValue(), Boolean.TRUE);
		}

		String[] fieldValues;
		if (multValues) {
			// fieldValues = request.getParameterValues(fieldName);
			fieldValues = validatorHelper.getParameter(fieldName).split(",");
		} else {
			fieldValues = new String[1];
			String fieldValue = getFieldValue(fieldName);
			fieldValues[0] = fieldValue == null ? "" : fieldValue;
		}

		for (String value : fieldValues) {
			// in principle this shouldn't happen, but since the empty valye is
			// trivially a member of the response set, it's okay
			if (value == null) {
				continue;
			}

			if (!values.containsKey(value)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Determine if the value for the specified field matches the value in the bean.
	 * 
	 * @param fieldName
	 *            The name of the input.
	 * @param oldValue
	 *            The data to be matched against the input value.
	 * @param isMultiple
	 *            <code>true</code> if the input is a checkbox or multiple select, <code>false</code> otherwise.
	 * @return <code>true</code> if the value of fieldName matches the value in idb, <code>false</code> otherwise.
	 */
	protected boolean valueMatchesInitialValue(String fieldName, String oldValue, boolean isMultiple) {
		String fieldValue = "";
		String glue = "";

		if (isMultiple) {
			String[] fieldValues = validatorHelper.getParameterValues(fieldName);

			if (fieldValues != null) {
				for (String element : fieldValues) {
					fieldValue += glue + element;
					glue = ",";
				}
			}
		} else {
			// added the NPE catch block, tbh, 09012007
			// throws an error with groups, so we will need to
			// follow up on this, tbh
			try {
				fieldValue = getFieldValue(fieldName);
			} catch (NullPointerException npe) {
				logger.info("line 1444: validator: found NPE with " + fieldName);
				return false;
			}
			// had to re-add: tbh 09222007
		}

		if (fieldValue == null) {
			// we have "" as the default value for item data
			// when the value from page is null, we save "" in DB,so should
			// consider they match
			return "".equals(oldValue);
		}
		logger.debug("value matches initial: found " + oldValue + " versus " + fieldValue);
		return fieldValue.equals(oldValue);
	}

	/**
	 * Set the error message that is generated if the last field added to the Validator does not properly validate.
	 * 
	 * @param message
	 *            The error message to display.
	 */
	public void setErrorMessage(String message) {
		// logger.info("got this far...");
		if (lastField == null) {
			return;
		}

		// logger.info("got this far...");
		ArrayList fieldValidations = (ArrayList) validations.get(lastField);
		if (fieldValidations == null) {
			return;
		}

		// logger.info("got this far...");
		int lastInd = fieldValidations.size() - 1;
		Validation v = (Validation) fieldValidations.get(lastInd);
		if (v == null) {
			return;
		}

		v.setErrorMessage(message);
		// logger.info("set error message successfully: "+message);
		fieldValidations.set(lastInd, v);
		validations.put(lastField, fieldValidations);
	}

	/**
	 * process crf validation function.
	 * 
	 * @param inputFunction
	 *            String
	 * @return Validation
	 * @throws Exception
	 *             an Exception
	 */
	public static Validation processCRFValidationFunction(String inputFunction) throws Exception {

		ResourceBundle resexception = ResourceBundleProvider.getExceptionsBundle();
		Validation v;
		if (inputFunction.equals("func: barcode(EAN-13)")) {
			v = new Validation(BARCODE_EAN_13);
			return v;
		}

		String fname;
		ArrayList args;
		int externalValue = THREE;

		HashMap numArgsByFunction = new HashMap();
		numArgsByFunction.put("range", 2);
		numArgsByFunction.put("gt", 1);
		numArgsByFunction.put("lt", 1);
		numArgsByFunction.put("gte", 1);
		numArgsByFunction.put("lte", 1);
		numArgsByFunction.put("ne", 1);
		numArgsByFunction.put("eq", 1);
		numArgsByFunction.put("getExternalValue", externalValue);

		HashMap valTypeByFunction = new HashMap();
		valTypeByFunction.put("range", Validator.IS_IN_RANGE);
		valTypeByFunction.put("gt", Validator.COMPARES_TO_STATIC_VALUE);
		valTypeByFunction.put("lt", Validator.COMPARES_TO_STATIC_VALUE);
		valTypeByFunction.put("gte", Validator.COMPARES_TO_STATIC_VALUE);
		valTypeByFunction.put("lte", Validator.COMPARES_TO_STATIC_VALUE);
		valTypeByFunction.put("ne", Validator.COMPARES_TO_STATIC_VALUE);
		valTypeByFunction.put("eq", Validator.COMPARES_TO_STATIC_VALUE);

		HashMap compareOpByFunction = new HashMap();
		compareOpByFunction.put("gt", NumericComparisonOperator.GREATER_THAN);
		compareOpByFunction.put("lt", NumericComparisonOperator.LESS_THAN);
		compareOpByFunction.put("gte", NumericComparisonOperator.GREATER_THAN_OR_EQUAL_TO);
		compareOpByFunction.put("lte", NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO);
		compareOpByFunction.put("ne", NumericComparisonOperator.NOT_EQUALS);
		compareOpByFunction.put("eq", NumericComparisonOperator.EQUALS);

		Pattern funcPattern = Pattern.compile("func:\\s*([A-Za-z]+)\\(([^,]*)?(,[^,]*)*\\)");
		Matcher funcMatcher = funcPattern.matcher(inputFunction);

		if (!funcMatcher.matches()) {
			throw new Exception(resexception.getString("syntax_incorrect"));
			// error: the syntax is incorrect, should be func:
			// fname(arg1,...,argn)
		}

		// note that numGroups must be > 1
		fname = funcMatcher.group(1);
		args = new ArrayList();
		for (int i = 2; i <= funcMatcher.groupCount(); i++) {
			String arg = funcMatcher.group(i);

			if (StringUtil.isBlank(arg)) {
				continue;
			}

			// if i > = 3, then we are dealing with arg2 or above
			// this means we need to get rid of the preceding ,
			if (i >= externalValue) {
				arg = arg.substring(1);
			}
			arg = arg.trim();
			args.add(arg);
		}

		if (!fname.equalsIgnoreCase("range") && !fname.equalsIgnoreCase("gt") && !fname.equalsIgnoreCase("lt")
				&& !fname.equalsIgnoreCase("gte") && !fname.equalsIgnoreCase("lte") && !fname.equalsIgnoreCase("eq")
				&& !fname.equalsIgnoreCase("ne") && !fname.equalsIgnoreCase("getexternalvalue")) {
			throw new Exception(resexception.getString("validation_column_invalid_function"));
		}
		// test that the right number of arguments have been provided; complain
		// if not
		Integer numProperArgsInFunction = (Integer) numArgsByFunction.get(fname);
		if (args.size() != numProperArgsInFunction) {
			throw new Exception(resexception.getString("validation_column_invalid_function") + ": "
					+ resexception.getString("number_of_arguments_incorrect"));
		}

		for (int i = 0; i < args.size(); i++) {
			int ord = i + 1;
			try {
				float f = Float.parseFloat((String) args.get(i));
			} catch (Exception e) {
				throw new Exception(resexception.getString("validation_column_invalid_function") + ": "
						+ resexception.getString("argument") + ord + " " + resexception.getString("is_not_a_number"));
			}
		}

		// success - all tests have been passed
		// now we compose the validation object created by this function
		Integer valType = (Integer) valTypeByFunction.get(fname);
		v = new Validation(valType);

		if (!fname.equalsIgnoreCase("range")) {
			NumericComparisonOperator operator = (NumericComparisonOperator) compareOpByFunction.get(fname);
			v.addArgument(operator);
		}

		for (Object arg : args) {
			float f = Float.parseFloat((String) arg);
			v.addArgument(f);

		}
		return v;
	}

	/**
	 * process all the validation regexps.
	 * 
	 * @param inputRegex
	 *            String
	 * @return Validation
	 * @throws Exception
	 *             an Exception
	 */
	public static Validation processCRFValidationRegex(String inputRegex) throws Exception {

		ResourceBundle resexception = ResourceBundleProvider.getExceptionsBundle();

		Validation v = new Validation(Validator.MATCHES_REGULAR_EXPRESSION);
		int subString = SEVEN;
		// inputRegex should be looking like "regexp:/expression/" if it goes
		// this far
		String finalRegexp = inputRegex.substring(subString).trim();
		finalRegexp = finalRegexp.substring(1, finalRegexp.length() - 1);
		// YW >>

		if (StringUtil.isBlank(finalRegexp)) {
			throw new Exception(resexception.getString("regular_expression_is_blank"));
		}

		v.addArgument(finalRegexp);
		return v;
	}

	/**
	 * remove field validations.
	 * 
	 * @param fieldName
	 *            String
	 */
	public void removeFieldValidations(String fieldName) {
		if (validations.containsKey(fieldName)) {
			validations.remove(fieldName);
		}
	}

	/**
	 * Return error message of widthDecimal validation. If valid, no message returns.
	 * 
	 * @param widthDecimal
	 *            String
	 * @param dataType
	 *            String
	 * @param isCalculationItem
	 *            boolean
	 * @param locale
	 *            Locale
	 * 
	 * @return a string with the decimal setting.
	 * 
	 */
	public static StringBuffer validateWidthDecimalSetting(String widthDecimal, String dataType,
			boolean isCalculationItem, Locale locale) {
		int widthLimit = FOUR_THOUSAND;
		int intLimit = TEN;
		int realLimit = TWENTY_SIX;
		int otherRealLimit = THIRTY;
		StringBuffer message = new StringBuffer();
		ResourceBundle resException = ResourceBundleProvider.getExceptionsBundle(locale);
		if (Validator.validWidthDecimalPattern(widthDecimal, isCalculationItem)) {
			int width = Validator.parseWidth(widthDecimal);
			int decimal = Validator.parseDecimal(widthDecimal);
			if (width > 0) {
				if ("ST".equalsIgnoreCase(dataType)) {
					if (width > widthLimit) {
						message.append(" ").append(dataType).append(" ")
								.append(resException.getString("datatype_maximum_width_is")).append(" ")
								.append(FOUR_THOUSAND).append(".");
					}
				} else if ("INT".equalsIgnoreCase(dataType)) {
					if (width > intLimit) {
						message.append(" ").append(dataType).append(" ")
								.append(resException.getString("datatype_maximum_width_is")).append(" ").append(TEN)
								.append(".");
					}
				} else if ("REAL".equalsIgnoreCase(dataType)) {
					if (width > realLimit) {
						message.append(" ").append(dataType).append(" ")
								.append(resException.getString("datatype_maximum_width_is")).append(" ")
								.append(TWENTY_SIX).append(".");
					}
				}
			}
			if (decimal > 0) {
				if ("ST".equalsIgnoreCase(dataType)) {
					message.append(" ").append(dataType).append(" ")
							.append(resException.getString("datatype_decimal_cannot_bigger_than_0"));
				} else if ("INT".equalsIgnoreCase(dataType)) {
					message.append(" ").append(dataType).append(" ")
							.append(resException.getString("datatype_decimal_cannot_bigger_than_0"));
				} else if ("REAL".equalsIgnoreCase(dataType)) {
					if (width > 0 && decimal >= width) {
						message.append(" ").append(resException.getString("decimal_cannot_larger_than_width"));
					}
					if (decimal > otherRealLimit) {
						message.append(" ").append(resException.getString("decimal_cannot_larger_than_30"));
					}
				}
			}
		} else {
			String s;
			if (isCalculationItem) {
				s = resException.getString("calculation_correct_width_decimal_pattern");
			} else {
				s = resException.getString("correct_width_decimal_pattern");
			}
			message.append(s);
		}
		return message;
	}

	/**
	 * generate a true or false base on the width of the decimal.
	 * 
	 * @param widthDecimal
	 *            String
	 * @param isCalculationItem
	 *            boolean
	 * @return boolean
	 */
	public static boolean validWidthDecimalPattern(String widthDecimal, boolean isCalculationItem) {
		String pattern;
		if (isCalculationItem) {
			pattern = "((w[(](d|(\\d)+)[)])|([(](d|(\\d)+)[)]))";
		} else {
			pattern = "((w|(\\d)+)[(](d|(\\d)+)[)])|(w|(\\d)+)|([(](d|(\\d)+)[)])";
		}
		widthDecimal = widthDecimal.trim();
		return Pattern.matches(pattern, widthDecimal);
	}

	/**
	 * generate parse width.
	 * 
	 * @param widthDecimal
	 *            String
	 * @return int
	 */
	public static int parseWidth(String widthDecimal) {
		String w;
		widthDecimal = widthDecimal.trim();
		if (widthDecimal.contains("(")) {
			w = widthDecimal.split("\\(")[0];
		} else {
			w = widthDecimal;
		}
		if (w.length() > 0) {
			return "w".equalsIgnoreCase(w) ? 0 : Integer.parseInt(w);
		}
		return 0;
	}

	/**
	 * generate parse decimal.
	 * 
	 * @param widthDecimal
	 *            String
	 * @return int
	 */
	public static int parseDecimal(String widthDecimal) {
		String d = "";
		widthDecimal = widthDecimal.trim();
		if (widthDecimal.startsWith("(")) {
			d = widthDecimal.substring(1, widthDecimal.length() - 1);
		} else if (widthDecimal.contains("(")) {
			d = widthDecimal.split("\\(")[1].trim();
			d = d.substring(0, d.length() - 1);

		}
		if (d.length() > 0) {
			return "d".equalsIgnoreCase(d) ? 0 : Integer.parseInt(d);
		}
		return 0;
	}

	protected StringBuffer validateFieldWidthDecimal(String fieldName, String dataType, String widthDecimal) {
		logger.debug("find locale=" + resexception.getLocale());
		StringBuffer message = new StringBuffer();
		String fieldValue = getFieldValue(fieldName);
		if (StringUtil.isBlank(fieldValue)) {
			return message;
		}
		int width = Validator.parseWidth(widthDecimal);
		int decimal = Validator.parseDecimal(widthDecimal);
		if (width > 0) {
			if ("ST".equalsIgnoreCase(dataType)) {
				if (fieldValue.length() > width) {
					message.append(resexception.getString("exceeds_width")).append("=").append(width).append(".");
				}
			} else if ("INT".equalsIgnoreCase(dataType)) {
				if (fieldValue.length() > width) {
					message.append(resexception.getString("exceeds_width")).append("=").append(width).append(".");
				}
			} else if ("REAL".equalsIgnoreCase(dataType)) {
				if (fieldValue.length() > width) {
					message.append(resexception.getString("exceeds_width")).append("=").append(width).append(".");
				}
			}
		}
		if (decimal > 0) {
			if ("REAL".equalsIgnoreCase(dataType)) {
				try {
					Double d = NumberFormat.getInstance().parse(fieldValue).doubleValue();
					if (BigDecimal.valueOf(d).scale() > decimal) {
						message.append(resexception.getString("exceeds_decimal")).append("=").append(decimal)
								.append(".");
					}
				} catch (ParseException pe) {
					message.append(resexception.getString("should_be_real_number"));
				}
			}
		}
		return message;
	}

	private int getPwdMinLen(ValidatorHelper validatorHelper) {
		return new PasswordRequirementsDao(validatorHelper.getConfigurationDao()).minLength();
	}
}
