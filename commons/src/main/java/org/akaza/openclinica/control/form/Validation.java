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
 * For details see: http://www.openclinica.org/license copyright 2003-2005 Akaza
 * Research
 */
package org.akaza.openclinica.control.form;

import java.util.ArrayList;

/**
 * Validation.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class Validation {

	private int type;

	private String errorMessage;

	private boolean errorMessageSet;

	private ArrayList arguments;

	private boolean alwaysExecuted;

	private boolean convertPDate;

	private boolean convertDate;

	/**
	 * Constructor.
	 * 
	 * @param type
	 *            int
	 */
	public Validation(int type) {
		this.type = type;
		arguments = new ArrayList();
		errorMessage = "";
		alwaysExecuted = false;
	}

	/**
	 * Get arguments method.
	 * 
	 * @return ArrayList
	 */
	public ArrayList getArguments() {
		return arguments;
	}

	/**
	 * Set arguments method.
	 * 
	 * @param arguments
	 *            ArrayList
	 */
	public void setArguments(ArrayList arguments) {
		this.arguments = arguments;
	}

	/**
	 * Get type method.
	 * 
	 * @return int
	 */
	public int getType() {
		return type;
	}

	/**
	 * Set type method.
	 * 
	 * @param type
	 *            int
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * Adds object argument.
	 * 
	 * @param arg
	 *            Object
	 */
	public void addArgument(Object arg) {
		arguments.add(arg);
	}

	/**
	 * Adds int argument.
	 * 
	 * @param arg
	 *            int
	 */
	public void addArgument(int arg) {
		arguments.add(arg);
	}

	/**
	 * Adds boolean argument.
	 * 
	 * @param arg
	 *            boolean
	 */
	public void addArgument(boolean arg) {
		arguments.add(arg);
	}

	/**
	 * Adds float argument.
	 * 
	 * @param arg
	 *            float
	 */
	public void addArgument(float arg) {
		arguments.add(arg);
	}

	/**
	 * Gets the boolean value of the argument with the specified index.
	 * 
	 * @param index
	 *            int
	 * @return boolean
	 */
	public boolean getBoolean(int index) {
		if (index >= arguments.size()) {
			return false;
		}
		return (Boolean) arguments.get(index);
	}

	/**
	 * Gets the integer value of the argument with the specified index.
	 * 
	 * @param index
	 *            int
	 * @return int
	 */
	public int getInt(int index) {
		if (index >= arguments.size()) {
			return 0;
		}
		return (Integer) arguments.get(index);
	}

	/**
	 * Gets the float value of the argument with the specified index.
	 * 
	 * @param index
	 *            int
	 * @return float
	 */
	public float getFloat(int index) {
		if (index >= arguments.size()) {
			return 0;
		}

		try {
			return (Float) arguments.get(index);
		} catch (ClassCastException ce) {
			return (Integer) arguments.get(index);
		}
	}

	/**
	 * Gets the string value of the argument with the specified index.
	 * 
	 * @param index
	 *            int
	 * @return String
	 */
	public String getString(int index) {
		if (index >= arguments.size()) {
			return "";
		}
		return (String) arguments.get(index);
	}

	/**
	 * Gets the object value of the argument with the specified index.
	 * 
	 * @param index
	 *            int
	 * @return Object
	 */
	public Object getArg(int index) {
		if (index >= arguments.size()) {
			return null;
		}

		return arguments.get(index);
	}

	/**
	 * @return Returns the errorMessage.
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Sets the errorMessage.
	 * 
	 * @param errorMessage
	 *            String
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
		errorMessageSet = true;
	}

	public boolean isErrorMessageSet() {
		return errorMessageSet;
	}

	public boolean isAlwaysExecuted() {
		return alwaysExecuted;
	}

	public void setAlwaysExecuted(boolean alwaysExecuted) {
		this.alwaysExecuted = alwaysExecuted;
	}

	public boolean isConvertPDate() {
		return convertPDate;
	}

	public void setConvertPDate(boolean convertPDate) {
		this.convertPDate = convertPDate;
	}

	public boolean isConvertDate() {
		return convertDate;
	}

	public void setConvertDate(boolean convertDate) {
		this.convertDate = convertDate;
	}
}
