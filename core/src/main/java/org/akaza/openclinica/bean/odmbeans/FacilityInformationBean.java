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
 * OpenClinica is distributed under the GNU Lesser General Public License (GNU
 * LGPL).
 *
 * For details see: http://www.openclinica.org/license copyright 
 *
 */

package org.akaza.openclinica.bean.odmbeans;

/**
 * 
 * @author ywang (Aug, 2010)
 * 
 */

public class FacilityInformationBean {
	private String facilityName;
	private String facilityCity;
	private String facilityState;
	private String postalCode;
	private String facilityCountry;
	private String facilityContactName;
	private String facilityContactDegree;
	private String facilityContactPhone;
	private String facilityContactEmail;

	public String getFacilityName() {
		return facilityName;
	}

	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}

	public String getFacilityCity() {
		return facilityCity;
	}

	public void setFacilityCity(String facilityCity) {
		this.facilityCity = facilityCity;
	}

	public String getFacilityState() {
		return facilityState;
	}

	public void setFacilityState(String facilityState) {
		this.facilityState = facilityState;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getFacilityCountry() {
		return facilityCountry;
	}

	public void setFacilityCountry(String facilityCountry) {
		this.facilityCountry = facilityCountry;
	}

	public String getFacilityContactName() {
		return facilityContactName;
	}

	public void setFacilityContactName(String facilityContactName) {
		this.facilityContactName = facilityContactName;
	}

	public String getFacilityContactDegree() {
		return facilityContactDegree;
	}

	public void setFacilityContactDegree(String facilityContactDegree) {
		this.facilityContactDegree = facilityContactDegree;
	}

	public String getFacilityContactPhone() {
		return facilityContactPhone;
	}

	public void setFacilityContactPhone(String facilityContactPhone) {
		this.facilityContactPhone = facilityContactPhone;
	}

	public String getFacilityContactEmail() {
		return facilityContactEmail;
	}

	public void setFacilityContactEmail(String facilityContactEmail) {
		this.facilityContactEmail = facilityContactEmail;
	}
}
