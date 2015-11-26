/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
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
 *
 * Created on Sep 21, 2005
 */
package org.akaza.openclinica.control.form;

import java.util.ArrayList;
import java.util.HashMap;

import com.clinovo.util.ValidatorHelper;

/**
 * 
 * A class for validating a form which may have discrepancy notes attached
 * 
 * This class executes the rule that if a form field has a discrepancy note attached, it should not be validated. The
 * mechanism is via the <code>validate(String, Validation)</code> method. When this method is executed by this class,
 * the class checks to see if a discrepancy note is available for the field. If so, it takes no action. Otherwise, it
 * executes the validation using the superclass's validate method.
 * 
 * You can use this class exactly as you use the Validator class. Simple declare objects to be DiscrepancyValidator,
 * rather than Validator, objects, and provide the appropriate FormDiscrepancyNotes object in the constructor.
 * 
 * @author ssachs
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class DiscrepancyValidator extends Validator {

	public static final int MAX_DESCRIPTION_LENGTH = 255;

	private final FormDiscrepancyNotes notes;

	/**
	 * DiscrepancyValidator constructor.
	 * 
	 * @param validatorHelper
	 *            ValidatorHelper
	 * @param notes
	 *            FormDiscrepancyNotes
	 */
	public DiscrepancyValidator(ValidatorHelper validatorHelper, FormDiscrepancyNotes notes) {
		super(validatorHelper);
		this.notes = notes;
	}

	@Override
	protected HashMap validate(String fieldName, Validation v) {
		if (v.getType() == MATCHES_INITIAL_DATA_ENTRY_VALUE) {
			return super.validate(fieldName, v);
		}
		if (!v.isAlwaysExecuted()) {
			if (notes.hasNote(fieldName) || notes.getNumExistingFieldNotes(fieldName) > 0) {
				return errors;
			}
		}

		return super.validate(fieldName, v);
	}

	/**
	 * Method that always executes last validation.
	 * 
	 * @param fieldName
	 *            String
	 */
	public void alwaysExecuteLastValidation(String fieldName) {
		ArrayList fieldValidations = getFieldValidations(fieldName);

		if (validations.size() >= 1) {
			Validation v = (Validation) fieldValidations.get(fieldValidations.size() - 1);
			v.setAlwaysExecuted(true);
			fieldValidations.set(fieldValidations.size() - 1, v);
		}
		validations.put(fieldName, fieldValidations);
	}
}
