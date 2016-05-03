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
 * If not, see <http://www.gnu.org/licenses/>.  Updated by Clinovo Inc on 05/19/2013.
 ******************************************************************************/
package com.clinovo.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.akaza.openclinica.domain.AbstractMutableDomainObject;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "DiscrepancyDescription", namespace = "http://www.cdisc.org/ns/odm/v1.3")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, creatorVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
@Entity
@Table(name = "discrepancy_description")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = {
		@Parameter(name = "sequence_name", value = "discrepancy_description_id_seq")})
public class DiscrepancyDescription extends AbstractMutableDomainObject {

	private int typeId = 0;
	private int studyId = 0;
	private String description = "";

	@JsonProperty("value")
	@XmlElement(name = "Value", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private String name = "";

	@JsonProperty("visibilityLevel")
	@XmlElement(name = "VisibilityLevel", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private String visibilityLevel = "";

	// these parameters are needed to store request parameter names
	private String parameterName = "";
	private String parameterErrorName = "";

	public DiscrepancyDescription() {
	}

	public DiscrepancyDescription(String name, String description, int studyId, String visibilityLevel, int typeId) {
		this.name = name;
		this.description = description;
		this.studyId = studyId;
		this.visibilityLevel = visibilityLevel;
		this.typeId = typeId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVisibilityLevel() {
		return visibilityLevel;
	}

	public void setVisibilityLevel(String visibilityLevel) {
		this.visibilityLevel = visibilityLevel;
	}

	public int getStudyId() {
		return studyId;
	}

	public void setStudyId(int studyId) {
		this.studyId = studyId;
	}

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Transient
	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	@Transient
	public String getParameterErrorName() {
		return parameterErrorName;
	}

	public void setParameterErrorName(String parameterErrorName) {
		this.parameterErrorName = parameterErrorName;
	}
}
