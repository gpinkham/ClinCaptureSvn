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
 */
package org.akaza.openclinica.web;

import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.view.Page;

/**
 * This exception should be used when the user attempts to perform a use case, but he is not authorized to do so. The
 * user should be sent to an error page and an error message should be displayed.
 * 
 * Typically the error page is Page.MAIN_MENU.
 * 
 * @author ssachs
 */
@SuppressWarnings("serial")
public class InsufficientPermissionException extends OpenClinicaException {
	private Page goTo; // this is the page the user should be forwarded to

	public InsufficientPermissionException(Page goTo, String message, String type, String methodName, String className,
			String errorid) {
		super(message, type, methodName, className, errorid);
		this.goTo = goTo;
	}

	public InsufficientPermissionException(Page goTo, String message, String errorid) {
		super(message, errorid);
		this.goTo = goTo;
	}

	/**
	 * @return Returns the goTo.
	 */
	public Page getGoTo() {
		return goTo;
	}

	/**
	 * @param goTo
	 *            The goTo to set.
	 */
	public void setGoTo(Page goTo) {
		this.goTo = goTo;
	}
}
