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

import javax.servlet.http.HttpServletRequest;

/**
 * A Validator for 'calculation' and 'group-calculation' type Items whose fieldNames are always from request attribute.
 * 
 * @author ywang (Feb. 2008)
 * 
 */
public class ScoreItemValidator extends DiscrepancyValidator {
	public ScoreItemValidator(HttpServletRequest request, FormDiscrepancyNotes notes) {
		// super(request);
		super(request, notes);
	}

	@Override
	protected String getFieldValue(String fieldName) {
		return (String) request.getAttribute(fieldName);
	}

}
