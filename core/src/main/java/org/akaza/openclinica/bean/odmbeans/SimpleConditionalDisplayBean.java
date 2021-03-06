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
 * OpenClinica is distributed under the GNU Lesser General Public License (GNU
 * LGPL).
 *
 * For details see: http://www.openclinica.org/license copyright 
 *
 */
package org.akaza.openclinica.bean.odmbeans;

import java.io.Serializable;

/**
 * SimpleConditionalDisplayBean.
 */
@SuppressWarnings("serial")
public class SimpleConditionalDisplayBean implements Serializable {
	private String controlItemName;
	private String optionValue;
	private String message;

	/**
	 * Default constructor.
	 */
	public SimpleConditionalDisplayBean() {
	}

	/**
	 * Copy constructor.
	 * 
	 * @param origin
	 *            bean to copy from.
	 */
	public SimpleConditionalDisplayBean(SimpleConditionalDisplayBean origin) {

		this.controlItemName = origin.getControlItemName();
		this.optionValue = origin.getOptionValue();
		this.message = origin.getMessage();
	}

	public String getControlItemName() {
		return controlItemName;
	}

	public void setControlItemName(String controlItemName) {
		this.controlItemName = controlItemName;
	}

	public String getOptionValue() {
		return optionValue;
	}

	public void setOptionValue(String optionValue) {
		this.optionValue = optionValue;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
