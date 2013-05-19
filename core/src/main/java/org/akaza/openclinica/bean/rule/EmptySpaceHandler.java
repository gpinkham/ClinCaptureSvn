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

package org.akaza.openclinica.bean.rule;

import org.exolab.castor.mapping.GeneralizedFieldHandler;

/**
 * The FieldHandler for the Date class
 * 
 */
public class EmptySpaceHandler extends GeneralizedFieldHandler {

	/**
	 * Creates a new MyDateHandler instance
	 */
	public EmptySpaceHandler() {
		super();
	}

	/**
	 * This method is used to convert the value when the getValue method is called. The getValue method will obtain the
	 * actual field value from given 'parent' object. This convert method is then invoked with the field's value. The
	 * value returned from this method will be the actual value returned by getValue method.
	 * 
	 * @param value
	 *            the object value to convert after performing a get operation
	 * @return the converted value.
	 */
	@Override
	public Object convertUponGet(Object value) {
		return value;
	}

	/**
	 * This method is used to convert the value when the setValue method is called. The setValue method will call this
	 * method to obtain the converted value. The converted value will then be used as the value to set for the field.
	 * 
	 * @param value
	 *            the object value to convert before performing a set operation
	 * @return the converted value.
	 */
	@Override
	public Object convertUponSet(Object value) {

		return ((String) value).trim();
	}

	/**
	 * Returns the class type for the field that this GeneralizedFieldHandler converts to and from. This should be the
	 * type that is used in the object model.
	 * 
	 * @return the class type of of the field
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public Class getFieldType() {
		return String.class;
	}

	/**
	 * Creates a new instance of the object described by this field.
	 * 
	 * @param parent
	 *            The object for which the field is created
	 * @return A new instance of the field's value
	 * @throws IllegalStateException
	 *             This field is a simple type and cannot be instantiated
	 */
	@Override
	public Object newInstance(Object parent) throws IllegalStateException {
		// -- Since it's marked as a string...just return null,
		// -- it's not needed.
		return null;
	}

}
