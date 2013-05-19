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
package org.akaza.openclinica.bean.service;

public class StudyParamsConfig {
	private StudyParameterValueBean value;
	private StudyParameter parameter;

	public StudyParamsConfig() {
		value = new StudyParameterValueBean();
		parameter = new StudyParameter();
	}

	/**
	 * @return Returns the parameter.
	 */
	public StudyParameter getParameter() {
		return parameter;
	}

	/**
	 * @param parameter
	 *            The parameter to set.
	 */
	public void setParameter(StudyParameter parameter) {
		this.parameter = parameter;
	}

	/**
	 * @return Returns the value.
	 */
	public StudyParameterValueBean getValue() {
		return value;
	}

	/**
	 * @param value
	 *            The value to set.
	 */
	public void setValue(StudyParameterValueBean value) {
		this.value = value;
	}

}
