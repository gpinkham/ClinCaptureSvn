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

@SuppressWarnings({"rawtypes", "unchecked"})
public class Validation {
	private int type;

	private String errorMessage;

	private boolean errorMessageSet;

	private ArrayList arguments;

	private boolean alwaysExecuted;

	private boolean convertPDate;

	private boolean convertDate;

	public Validation(int type) {
		this.type = type;
		arguments = new ArrayList();
		errorMessage = "";
		alwaysExecuted = false;
	}

	/**
	 * @return Returns the arguments.
	 */
	public ArrayList getArguments() {
		return arguments;
	}

	/**
	 * @param arguments
	 *            The arguments to set.
	 */
	public void setArguments(ArrayList arguments) {
		this.arguments = arguments;
	}

	/**
	 * @return Returns the type.
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type
	 *            The type to set.
	 */
	public void setType(int type) {
		this.type = type;
	}

	public void addArgument(Object arg) {
		arguments.add(arg);
	}

	public void addArgument(int arg) {
		arguments.add(new Integer(arg));
	}

	public void addArgument(boolean b) {
		arguments.add(new Boolean(b));
	}

	public void addArgument(float arg) {
		arguments.add(new Float(arg));
	}

	/*
	 * Gets the boolean value of the argument with the specified index.
	 */
	public boolean getBoolean(int index) {
		if (index >= arguments.size()) {
			return false;
		}

		Boolean b = (Boolean) arguments.get(index);
		return b.booleanValue();
	}

	/*
	 * Gets the integer value of the argument with the specified index.
	 */
	public int getInt(int index) {
		if (index >= arguments.size()) {
			return 0;
		}

		Integer i = (Integer) arguments.get(index);
		return i.intValue();
	}

	/*
	 * Gets the float value of the argument with the specified index.
	 */
	public float getFloat(int index) {
		if (index >= arguments.size()) {
			return 0;
		}

		try {
			Float i = (Float) arguments.get(index);
			return i.floatValue();

		} catch (ClassCastException ce) {
			Integer i = (Integer) arguments.get(index);
			return i.intValue();
		}

	}

	public String getString(int index) {
		if (index >= arguments.size()) {
			return "";
		}

		String s = (String) arguments.get(index);
		return s;
	}

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
	 * @param errorMessage
	 *            The errorMessage to set.
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
		errorMessageSet = true;
	}

	/**
	 * @return Returns the errorMessageSet.
	 */
	public boolean isErrorMessageSet() {
		return errorMessageSet;
	}

	/**
	 * @return Returns the alwaysExecuted.
	 */
	public boolean isAlwaysExecuted() {
		return alwaysExecuted;
	}

	/**
	 * @param alwaysExecuted
	 *            The alwaysExecuted to set.
	 */
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
