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

public class PresentInEventDefinitionBean {
	// attributes
	private String studyEventOid;
	private String isDefaultVersion;
	private String nullValues;
	private String passwordRequired;
	private String doubleDataEntry;
	private String hideCrf;
	private String sourceDataVerification;

	public String getStudyEventOid() {
		return studyEventOid;
	}

	public void setStudyEventOid(String studyEventOid) {
		this.studyEventOid = studyEventOid;
	}

	public String getIsDefaultVersion() {
		return isDefaultVersion;
	}

	public void setIsDefaultVersion(String isDefaultVersion) {
		this.isDefaultVersion = isDefaultVersion;
	}

	public String getNullValues() {
		return nullValues;
	}

	public void setNullValues(String nullValues) {
		this.nullValues = nullValues;
	}

	public String getPasswordRequired() {
		return passwordRequired;
	}

	public void setPasswordRequired(String passwordRequired) {
		this.passwordRequired = passwordRequired;
	}

	public String getDoubleDataEntry() {
		return doubleDataEntry;
	}

	public void setDoubleDataEntry(String doubleDataEntry) {
		this.doubleDataEntry = doubleDataEntry;
	}

	public String getHideCrf() {
		return hideCrf;
	}

	public void setHideCrf(String hideCrf) {
		this.hideCrf = hideCrf;
	}

	public String getSourceDataVerification() {
		return sourceDataVerification;
	}

	public void setSourceDataVerification(String sourceDataVerification) {
		this.sourceDataVerification = sourceDataVerification;
	}
}
