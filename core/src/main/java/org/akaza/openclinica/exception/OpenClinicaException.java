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
package org.akaza.openclinica.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

@SuppressWarnings("serial")
public class OpenClinicaException extends Exception {

	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
	public StringBuffer logInfo = new StringBuffer("----> ");

	public String errorID;
	public String className;
	public String message;

	/** Creates a new instance of OpenClinicaException */
	public OpenClinicaException(String message, String type, String methodName, String className, String errorid) {
		if (message == null)
			message = "< No Message >";
		this.message = message;
		if (type == null)
			type = "< No Type >";
		if (methodName == null)
			methodName = "< No Method Name >";
		if (className == null)
			className = "<No Class Name >";
		if (errorid == null)
			errorid = "<No Error ID >";
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS");
		java.util.Date todayDate = new java.util.Date();
		String pDate = df.format(todayDate);
		logInfo.append("Timestamp: ").append(pDate);
		logInfo.append(" Message Info: ").append(message).append("; Type Info: ").append(type);
		logInfo.append("; Method Info: ").append(methodName).append("; Class Info: ").append(className);
		errorID = errorid;
		logger.info(logInfo.toString());
	}

	public OpenClinicaException(String message, String errorid) {
		if (message == null)
			message = "< No Message >";
		this.message = message;
		errorID = errorid;
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS");
		java.util.Date todayDate = new java.util.Date();
		String pDate = df.format(todayDate);
		logInfo.append("Timestamp: ").append(pDate);
		logInfo.append(" Message Info: ").append(message);
		logger.info(logInfo.toString());
	}

	public void addInfo(String type, String methodName, String className) {
		if (type == null)
			type = "< No Type >";
		if (methodName == null)
			methodName = "< No Method Name >";
		if (className == null)
			className = "<No Class Name >";
		logInfo.append(" --> propagated to: Type ").append(type).append("; Method: ").append(methodName);
		logInfo.append("; Class: ").append(className);
		logger.info(logInfo.toString());
	}

	public String getOpenClinicaMessage() {
		return message;
	}

	public void setOpenCinicaMessage(String m) {
		this.message = m;
	}

}
