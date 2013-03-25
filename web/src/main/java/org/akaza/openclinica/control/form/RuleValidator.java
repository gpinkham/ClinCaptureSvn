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
 * If not, see <http://www.gnu.org/licenses/>.
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

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * A class for validating a form which may have discrepancy notes attached
 * 
 * This class executes the rule that if a form field has a discrepancy note attached, it should not be validated. The
 * mechanism is via the <code>validate(String, Validation)</code> method. When this method is executed by this class,
 * the class checks to see if a discrepancy note is available for the field. If so, it takes no action. Otherwise, it
 * executes the validation using the superclass's validate method.
 * 
 * You can use this class exactly as you use the Validator class. Simple declar objects to be DiscrepancyValidator,
 * rather than Validator, objects, and provide the appropriate FormDiscrepancyNotes object in the constructor.
 * 
 * @author ssachs
 */
@SuppressWarnings({ "rawtypes" })
public class RuleValidator extends Validator {
	public RuleValidator(HttpServletRequest request, FormDiscrepancyNotes notes) {
		super(request);
	}

	public RuleValidator(HttpServletRequest request) {
		super(request);
	}

	@Override
	protected HashMap validate(String fieldName, Validation v) {

		return super.validate(fieldName, v);
	}
}
