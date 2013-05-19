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
 *
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.exception;

/**
 * @author Krikor Krumlian
 */
@SuppressWarnings("serial")
public class OpenClinicaSystemException extends RuntimeException {
	private String errorCode;
	private Object[] errorParams;

	public OpenClinicaSystemException(String code, String message) {
		this(message);
		this.errorCode = code;
	}

	public OpenClinicaSystemException(String code, String message, Throwable cause) {
		this(message, cause);
		this.errorCode = code;
	}

	public OpenClinicaSystemException(String message, Throwable cause) {
		super(message, cause);
	}

	public OpenClinicaSystemException(Throwable cause) {
		super(cause);
	}

	public OpenClinicaSystemException(String message) {
		super(message);
		this.errorCode = message;
	}

	public OpenClinicaSystemException(String code, Object[] errorParams) {
		this.errorCode = code;
		this.errorParams = errorParams;
	}

	public OpenClinicaSystemException(String code, Object[] errorParams, String message) {
		this(message);
		this.errorCode = code;
		this.errorParams = errorParams;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public Object[] getErrorParams() {
		return errorParams;
	}

	public void setErrorParams(Object[] errorParams) {
		this.errorParams = errorParams;
	}
}
