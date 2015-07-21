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

package org.akaza.openclinica.domain;

import org.akaza.openclinica.domain.enumsupport.CodedEnum;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * An enum that represents an Event definition's requirement for SourceDataVerification.
 */
public enum SourceDataVerification implements CodedEnum {

	AllREQUIRED(1, "entireCRF"), PARTIALREQUIRED(2, "specificItems"), NOTREQUIRED(3, "not_required"), NOTAPPLICABLE(4,
			"not_applicable");

	private int code;
	private String description;

	SourceDataVerification(int code, String description) {
		this.code = code;
		this.description = description;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		ResourceBundle resterm = ResourceBundleProvider.getTermsBundle();
		return resterm.getString(getDescription());
	}

	/**
	 * Method returns SourceDataVerification by name.
	 * 
	 * @param name
	 *            String
	 * @return SourceDataVerification
	 */
	public static SourceDataVerification getByName(String name) {
		return SourceDataVerification.valueOf(SourceDataVerification.class, name);
	}

	/**
	 * Method returns SourceDataVerification by code.
	 * 
	 * @param code
	 *            Integer
	 * @return SourceDataVerification
	 */
	public static SourceDataVerification getByCode(Integer code) {
		HashMap<Integer, SourceDataVerification> enumObjects = new HashMap<Integer, SourceDataVerification>();
		for (SourceDataVerification theEnum : SourceDataVerification.values()) {
			enumObjects.put(theEnum.getCode(), theEnum);
		}
		return enumObjects.get(code);
	}

	/**
	 * Method returns SourceDataVerification by description.
	 * 
	 * @param description
	 *            String
	 * @return SourceDataVerification
	 */
	public static SourceDataVerification getByDescription(String description) {
		HashMap<String, SourceDataVerification> sdvObjects = new HashMap<String, SourceDataVerification>();
		for (SourceDataVerification theEnum : SourceDataVerification.values()) {
			sdvObjects.put(theEnum.getDescription(), theEnum);
		}
		return sdvObjects.get(description);
	}

	/**
	 * Method returns SourceDataVerification by i18n description.
	 * 
	 * @param description
	 *            String
	 * @return SourceDataVerification
	 */
	public static SourceDataVerification getByI18nDescription(String description) {
		HashMap<String, SourceDataVerification> sdvObjects = new HashMap<String, SourceDataVerification>();
		for (SourceDataVerification theEnum : SourceDataVerification.values()) {
			sdvObjects.put(theEnum.toString(), theEnum);
		}
		return sdvObjects.get(description);
	}

	/**
	 * A wrapper for name() method to be used in JSPs.
	 * 
	 * @return A String, the name of the requirement.
	 */
	public String getName() {
		return this.name();
	}

	/**
	 * Method that fills sdv statuses.
	 * 
	 * @param sdvOptions
	 *            ArrayList<SourceDataVerification>
	 * @param hasItemsToSDV
	 *            boolean
	 */
	public static void fillSDVStatuses(ArrayList<SourceDataVerification> sdvOptions, boolean hasItemsToSDV) {
		sdvOptions.clear();
		if (!hasItemsToSDV) {
			sdvOptions.add(SourceDataVerification.AllREQUIRED);
			sdvOptions.add(SourceDataVerification.NOTREQUIRED);
		} else {
			sdvOptions.add(SourceDataVerification.PARTIALREQUIRED);
		}
	}
}
