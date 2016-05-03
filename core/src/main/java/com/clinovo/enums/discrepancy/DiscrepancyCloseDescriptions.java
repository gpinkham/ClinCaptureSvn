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
package com.clinovo.enums.discrepancy;

import java.util.Arrays;
import java.util.List;

import com.clinovo.enums.BaseEnum;
import com.clinovo.enums.ParameterType;

/**
 * DiscrepancyCloseDescriptions.
 */
@SuppressWarnings("unused")
public enum DiscrepancyCloseDescriptions implements BaseEnum {

	DN_CLOSE_DESCRIPTION("dnCloseDescription", "reason_for_update_descriptions"),
	DN_CLOSE_VISIBILITY_LEVEL("dnCloseVisibilityLevel", "visibility", DiscrepancyConstants.VISIBILITY_VALUES, DiscrepancyConstants.VISIBILITY_VALUE_CODES, ParameterType.RADIO);

	private String name;
	private String code;
	private String[] values;
	private boolean required;
	private ParameterType type;
	private String[] valueCodes;
	private String[] defaultValue;
	private boolean localizedDefaultValues;

	DiscrepancyCloseDescriptions(String name, String code) {
		this.name = name;
		this.code = code;
		type = ParameterType.TEXT;
		localizedDefaultValues = true;
		defaultValue = new String[]{"query_response_monitored", "CRF_data_change_monitored",
				"calendared_event_monitored", "failed_edit_check_monitored"};
	}

	DiscrepancyCloseDescriptions(String name, String code, String[] values, String[] valueCodes, ParameterType type) {
		this.type = type;
		this.name = name;
		this.code = code;
		this.values = values;
		this.valueCodes = valueCodes;
		defaultValue = new String[]{DiscrepancyVisibility.BOTH.getCode(), DiscrepancyVisibility.BOTH.getCode(),
				DiscrepancyVisibility.BOTH.getCode(), DiscrepancyVisibility.BOTH.getCode()};
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
	public List<DiscrepancyCloseDescriptions> asList() {
		return Arrays.asList(DiscrepancyCloseDescriptions.values());
	}

	/**
	 * {@inheritDoc}
	 */
	public Object find(String name) {
		DiscrepancyCloseDescriptions result = null;
		for (DiscrepancyCloseDescriptions studyFacility : asList()) {
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
		for (DiscrepancyCloseDescriptions studyFacility : asList()) {
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
	public String[] getDefaultValue() {
		return defaultValue;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isLocalizedDefaultValues() {
		return localizedDefaultValues;
	}
}
