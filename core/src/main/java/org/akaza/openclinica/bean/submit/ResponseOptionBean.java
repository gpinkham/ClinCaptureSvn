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
 */
package org.akaza.openclinica.bean.submit;

import java.io.Serializable;

/**
 * @author ssachs
 */
public class ResponseOptionBean implements Serializable {
	/**
	 * This will be displayed to the user.
	 */
	private String text;

	/**
	 * This will be stored in the database.
	 */
	private String value;

	/**
	 * <code>true</code> if the option was selected - either via form submission or storage in the db <code>false</code>
	 * otherwise
	 */
	private boolean selected;

	public ResponseOptionBean() {
		text = "";
		value = "";
		selected = false;
	}

	/**
	 * @return Returns the text.
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text
	 *            The text to set.
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return Returns the value.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            The value to set.
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return Returns the selected.
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected
	 *            The selected to set.
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
