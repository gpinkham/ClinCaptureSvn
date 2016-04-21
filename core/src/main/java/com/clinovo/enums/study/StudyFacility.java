/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 *
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 *
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use.
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.enums.study;

import com.clinovo.enums.BaseEnum;
import com.clinovo.enums.ParameterType;

import java.util.Arrays;
import java.util.List;

/**
 * StudyFacility.
 */
public enum StudyFacility implements BaseEnum {

	FACILITY_NAME("facilityName", "facility_name"), FACILITY_CITY("facilityCity", "facility_city"), FACILITY_STATE("facilityState", "facility_state_province"),
	FACILITY_ZIP("facilityZip", "facility_ZIP"), FACILITY_COUNTRY("facilityCountry", "facility_country"),
	FACILITY_CONTACT_NAME("facilityContactName", "facility_contact_name"), FACILITY_CONTACT_DEGREE("facilityContactDegree", "facility_contact_degree"),
	FACILITY_CONTACT_PHONE("facilityContactPhone", "facility_contact_phone"), FACILITY_CONTACT_EMAIL("facilityContactEmail", "facility_contact_email");

	private String name;
	private String code;
	private String[] values;
	private boolean required;
	private ParameterType type;
	private String defaultValue;

	StudyFacility(String name, String code) {
		this.name = name;
		this.code = code;
		defaultValue = "";
		type = ParameterType.TEXT;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getCode() {
		return code;
	}

	/**
	 * {@inheritDoc}
	 */
	public String[] getValues() {
		return values;
	}

	/**
	 * {@inheritDoc}
	 */
	public ParameterType getType() {
		return type;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<StudyFacility> asList() {
		return Arrays.asList(StudyFacility.values());
	}

	/**
	 * {@inheritDoc}
	 */
	public Object find(String name) {
		StudyFacility result = null;
		for (StudyFacility studyFacility : asList()) {
			if (studyFacility.getName().equals(name)) {
				result = studyFacility;
				break;
			}
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasTypo(String name) {
		boolean result = false;
		for (StudyFacility studyFacility : asList()) {
			if (studyFacility.getName().equalsIgnoreCase(name) && !studyFacility.getName().equals(name)) {
				result = true;
				break;
			}
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDefaultValue() {
		return defaultValue;
	}
}
