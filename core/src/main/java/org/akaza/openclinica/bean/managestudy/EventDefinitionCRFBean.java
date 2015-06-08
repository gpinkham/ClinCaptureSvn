/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2014 Clinovo Inc.
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
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.managestudy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.domain.SourceDataVerification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * The bean for event definition crf parameters.
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "EventDefinitionCrf", namespace = "http://www.cdisc.org/ns/odm/v1.3")
@JsonPropertyOrder({"id", "eventname", "crfname", "status", "defaultversion", "hidecrf", "required",
		"passwordrequired", "acceptnewcrfversions", "evaluatedcrf", "doubledataentry", "sourcedataverification",
		"tabbingmode", "ordinal", "studyid", "emailwhen", "email"})
@SuppressWarnings({"serial", "rawtypes", "unchecked"})
public class EventDefinitionCRFBean extends AuditableEntityBean implements Comparable {

	@JsonProperty("eventname")
	@XmlElement(name = "EventName", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private String eventName;

	@JsonProperty("crfname")
	@XmlElement(name = "CrfName", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private String crfName = ""; // not in DB

	@JsonProperty("defaultversion")
	@XmlElement(name = "DefaultVersion", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private String defaultVersionName = ""; // not in DB

	@JsonProperty("hidecrf")
	@XmlElement(name = "HideCrf", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private boolean hideCrf = false;

	@JsonIgnore
	@XmlTransient
	private boolean hidden = false;

	@JsonProperty("required")
	@XmlElement(name = "Required", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private boolean requiredCRF = true;

	@JsonProperty("passwordrequired")
	@XmlElement(name = "PasswordRequired", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private boolean electronicSignature = false;

	@JsonProperty("acceptnewcrfversions")
	@XmlElement(name = "AcceptNewCrfVersions", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private boolean acceptNewCrfVersions;

	@JsonProperty("evaluatedcrf")
	@XmlElement(name = "EvaluatedCrf", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private boolean evaluatedCRF = false;

	@JsonProperty("doubledataentry")
	@XmlElement(name = "DoubleDataEntry", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private boolean doubleEntry = false;

	@JsonProperty("sourcedataverification")
	@XmlElement(name = "SourceDataVerification", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private String sdvCode = "";

	@JsonIgnore
	@XmlTransient
	private SourceDataVerification sourceDataVerification = null;

	@JsonProperty("tabbingmode")
	@XmlElement(name = "TabbingMode", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private String tabbingMode;

	@JsonProperty("ordinal")
	@XmlElement(name = "Ordinal", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private int ordinal = 1;

	@JsonProperty("studyid")
	@XmlElement(name = "StudyId", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private int studyId = 0;

	@JsonIgnore
	@XmlTransient
	private boolean requireAllTextFilled = false;

	@JsonIgnore
	@XmlTransient
	private boolean decisionCondition = true;

	@JsonIgnore
	@XmlTransient
	private int studyEventDefinitionId = 0;

	@JsonProperty("emailwhen")
	@XmlElement(name = "EmailWhen", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private String emailStep = "";

	@JsonProperty("email")
	@XmlElement(name = "Email", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private String emailTo = "";

	@JsonIgnore
	@XmlTransient
	private int crfId = 0;

	@JsonIgnore
	@XmlTransient
	private int parentId = 0;

	@JsonIgnore
	@XmlTransient
	private int defaultVersionId = 0;

	@JsonIgnore
	@XmlTransient
	private String selectedVersionIds = "";

	@JsonIgnore
	@XmlTransient
	private String selectedVersionNames = ""; // not in DB

	@JsonIgnore
	@XmlTransient
	private String nullValues = "";

	@JsonIgnore
	@XmlTransient
	private CRFBean crf = new CRFBean(); // not in DB

	@JsonIgnore
	@XmlTransient
	private ArrayList nullValuesList = new ArrayList();

	@JsonIgnore
	@XmlTransient
	private ArrayList versions = new ArrayList(); // not in DB

	@JsonIgnore
	@XmlTransient
	private ArrayList<Integer> selectedVersionIdList = new ArrayList<Integer>(); // not in DB

	@JsonIgnore
	@XmlTransient
	private HashMap nullFlags = new LinkedHashMap(); // not in DB

	@JsonIgnore
	@XmlTransient
	private ArrayList<SourceDataVerification> sdvOptions = new ArrayList<SourceDataVerification>();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((crf == null) ? 0 : crf.hashCode());
		result = prime * result + crfId;
		result = prime * result + ((crfName == null) ? 0 : crfName.hashCode());
		result = prime * result + (decisionCondition ? INT_1231 : INT_1237);
		result = prime * result + defaultVersionId;
		result = prime * result + ((defaultVersionName == null) ? 0 : defaultVersionName.hashCode());
		result = prime * result + (doubleEntry ? INT_1231 : INT_1237);
		result = prime * result + (electronicSignature ? INT_1231 : INT_1237);
		result = prime * result + ((eventName == null) ? 0 : eventName.hashCode());
		result = prime * result + (hidden ? INT_1231 : INT_1237);
		result = prime * result + (hideCrf ? INT_1231 : INT_1237);
		result = prime * result + ((nullFlags == null) ? 0 : nullFlags.hashCode());
		result = prime * result + ((nullValues == null) ? 0 : nullValues.hashCode());
		result = prime * result + ((nullValuesList == null) ? 0 : nullValuesList.hashCode());
		result = prime * result + ordinal;
		result = prime * result + parentId;
		result = prime * result + (requireAllTextFilled ? INT_1231 : INT_1237);
		result = prime * result + (requiredCRF ? INT_1231 : INT_1237);
		result = prime * result + ((selectedVersionIdList == null) ? 0 : selectedVersionIdList.hashCode());
		result = prime * result + ((selectedVersionIds == null) ? 0 : selectedVersionIds.hashCode());
		result = prime * result + ((selectedVersionNames == null) ? 0 : selectedVersionNames.hashCode());
		result = prime * result + ((sourceDataVerification == null) ? 0 : sourceDataVerification.hashCode());
		result = prime * result + studyEventDefinitionId;
		result = prime * result + studyId;
		result = prime * result + ((versions == null) ? 0 : versions.hashCode());
		result = prime * result + ((emailTo == null) ? 0 : emailTo.hashCode());
		result = prime * result + ((emailStep == null) ? 0 : emailStep.hashCode());
		result = prime * result + (evaluatedCRF ? INT_1231 : INT_1237);
		result = prime * result + ((tabbingMode == null) ? 0 : tabbingMode.hashCode());
		result = prime * result + (acceptNewCrfVersions ? INT_1231 : INT_1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		EventDefinitionCRFBean other = (EventDefinitionCRFBean) obj;
		if (crf == null) {
			if (other.crf != null) {
				return false;
			}
		} else if (!crf.equals(other.crf)) {
			return false;
		}
		if (crfId != other.crfId) {
			return false;
		}
		if (crfName == null) {
			if (other.crfName != null) {
				return false;
			}
		} else if (!crfName.equals(other.crfName)) {
			return false;
		}
		if (decisionCondition != other.decisionCondition) {
			return false;
		}
		if (defaultVersionId != other.defaultVersionId) {
			return false;
		}
		if (defaultVersionName == null) {
			if (other.defaultVersionName != null) {
				return false;
			}
		} else if (!defaultVersionName.equals(other.defaultVersionName)) {
			return false;
		}
		if (doubleEntry != other.doubleEntry) {
			return false;
		}
		if (electronicSignature != other.electronicSignature) {
			return false;
		}
		if (eventName == null) {
			if (other.eventName != null) {
				return false;
			}
		} else if (!eventName.equals(other.eventName)) {
			return false;
		}
		if (hidden != other.hidden) {
			return false;
		}
		if (hideCrf != other.hideCrf) {
			return false;
		}
		if (nullFlags == null) {
			if (other.nullFlags != null) {
				return false;
			}
		} else if (!nullFlags.equals(other.nullFlags)) {
			return false;
		}
		if (nullValues == null) {
			if (other.nullValues != null) {
				return false;
			}
		} else if (!nullValues.equals(other.nullValues)) {
			return false;
		}
		if (nullValuesList == null) {
			if (other.nullValuesList != null) {
				return false;
			}
		} else if (!nullValuesList.equals(other.nullValuesList)) {
			return false;
		}
		if (ordinal != other.ordinal) {
			return false;
		}
		if (parentId != other.parentId) {
			return false;
		}
		if (requireAllTextFilled != other.requireAllTextFilled) {
			return false;
		}
		if (requiredCRF != other.requiredCRF) {
			return false;
		}
		if (selectedVersionIdList == null) {
			if (other.selectedVersionIdList != null) {
				return false;
			}
		} else if (!selectedVersionIdList.equals(other.selectedVersionIdList)) {
			return false;
		}
		if (selectedVersionIds == null) {
			if (other.selectedVersionIds != null) {
				return false;
			}
		} else if (!selectedVersionIds.equals(other.selectedVersionIds)) {
			return false;
		}
		if (selectedVersionNames == null) {
			if (other.selectedVersionNames != null) {
				return false;
			}
		} else if (!selectedVersionNames.equals(other.selectedVersionNames)) {
			return false;
		}
		if (sourceDataVerification != other.sourceDataVerification) {
			return false;
		}
		if (studyEventDefinitionId != other.studyEventDefinitionId) {
			return false;
		}
		if (studyId != other.studyId) {
			return false;
		}
		if (evaluatedCRF != other.evaluatedCRF) {
			return false;
		}
		if (emailTo == null) {
			if (other.emailTo != null) {
				return false;
			}
		} else if (!emailTo.equals(other.emailTo)) {
			return false;
		}
		if (emailStep == null) {
			if (other.emailStep != null) {
				return false;
			}
		} else if (!emailStep.equals(other.emailStep)) {
			return false;
		}
		if (versions == null) {
			if (other.versions != null) {
				return false;
			}
		} else if (!versions.equals(other.versions)) {
			return false;
		}
		if (tabbingMode == null) {
			if (other.tabbingMode != null) {
				return false;
			}
		} else if (!tabbingMode.equals(other.tabbingMode)) {
			return false;
		} else if (acceptNewCrfVersions != other.acceptNewCrfVersions) {
			return false;
		}
		return true;
	}

	public String getSelectedVersionIds() {
		return selectedVersionIds;
	}

	public void setSelectedVersionIds(String selectedVersionIds) {
		this.selectedVersionIds = selectedVersionIds;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public boolean isHideCrf() {
		return hideCrf;
	}

	public void setHideCrf(boolean hideCrf) {
		this.hideCrf = hideCrf;
	}

	public int getOrdinal() {
		return ordinal;
	}

	public void setOrdinal(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getCrfId() {
		return crfId;
	}

	public void setCrfId(int crfId) {
		this.crfId = crfId;
	}

	public String getCrfName() {
		return crfName;
	}

	public void setCrfName(String crfName) {
		this.crfName = crfName;
	}

	public boolean isDecisionCondition() {
		return decisionCondition;
	}

	public void setDecisionCondition(boolean decisionCondition) {
		this.decisionCondition = decisionCondition;
	}

	public int getDefaultVersionId() {
		return defaultVersionId;
	}

	public void setDefaultVersionId(int defaultVersionId) {
		this.defaultVersionId = defaultVersionId;
	}

	public boolean isDoubleEntry() {
		return doubleEntry;
	}

	public void setDoubleEntry(boolean doubleEntry) {
		this.doubleEntry = doubleEntry;
	}

	public boolean isElectronicSignature() {
		return electronicSignature;
	}

	public void setElectronicSignature(boolean setElectronicSignature) {
		this.electronicSignature = setElectronicSignature;
	}

	public boolean isRequireAllTextFilled() {
		return requireAllTextFilled;
	}

	public void setRequireAllTextFilled(boolean requireAllTextFilled) {
		this.requireAllTextFilled = requireAllTextFilled;
	}

	public boolean isRequiredCRF() {
		return requiredCRF;
	}

	public void setRequiredCRF(boolean requiredCRF) {
		this.requiredCRF = requiredCRF;
	}

	public int getStudyEventDefinitionId() {
		return studyEventDefinitionId;
	}

	public void setStudyEventDefinitionId(int studyEventDefinitionId) {
		this.studyEventDefinitionId = studyEventDefinitionId;
	}

	public int getStudyId() {
		return studyId;
	}

	public void setStudyId(int studyId) {
		this.studyId = studyId;
	}

	@Deprecated
	public String getNullValues() {
		return nullValues;
	}

	/**
	 * Method sets null values.
	 * 
	 * @param nullValues
	 *            String
	 */
	public void setNullValues(String nullValues) {
		this.nullValues = nullValues;
		String[] nullValuesSeparated = nullValues.split(",");

		nullValuesList = new ArrayList();
		for (String val : nullValuesSeparated) {
			org.akaza.openclinica.bean.core.NullValue nv = org.akaza.openclinica.bean.core.NullValue.getByName(val);
			if (nv.isActive()) {
				nullValuesList.add(nv);
			}
		}
	}

	public ArrayList getVersions() {
		return versions;
	}

	public void setVersions(ArrayList versions) {
		this.versions = versions;
	}

	public CRFBean getCrf() {
		return crf;
	}

	public void setCrf(CRFBean crf) {
		this.crf = crf;
	}

	/**
	 * Method returns null flag map.
	 * 
	 * @return HashMap
	 */
	public HashMap getNullFlags() {
		if (nullFlags.size() == 0) {
			nullFlags.put("NI", "0");
			nullFlags.put("NA", "0");
			nullFlags.put("UNK", "0");
			nullFlags.put("NASK", "0");
			nullFlags.put("NAV", "0");
			nullFlags.put("ASKU", "0");
			nullFlags.put("NAV", "0");
			nullFlags.put("OTH", "0");
			nullFlags.put("PINF", "0");
			nullFlags.put("NINF", "0");
			nullFlags.put("MSK", "0");
			nullFlags.put("NPE", "0");

		}
		return nullFlags;
	}

	public void setNullFlags(HashMap nullFlags) {
		this.nullFlags = nullFlags;
	}

	public ArrayList getNullValuesList() {
		return nullValuesList;
	}

	public void setNullValuesList(ArrayList nullValuesList) {
		this.nullValuesList = nullValuesList;
	}

	public String getDefaultVersionName() {
		return defaultVersionName;
	}

	public void setDefaultVersionName(String defaultVersionName) {
		this.defaultVersionName = defaultVersionName;
	}

	/**
	 * Method compares objects.
	 * 
	 * @param o
	 *            Object
	 * @return int
	 */
	public int compareTo(Object o) {
		if (o == null || !o.getClass().equals(this.getClass())) {
			return 0;
		}

		EventDefinitionCRFBean edcb = (EventDefinitionCRFBean) o;
		return this.ordinal - edcb.ordinal;
	}

	public SourceDataVerification getSourceDataVerification() {
		return sourceDataVerification;
	}

	public void setSourceDataVerification(SourceDataVerification sourceDataVerification) {
		this.sourceDataVerification = sourceDataVerification;
		sdvCode = sourceDataVerification != null ? sourceDataVerification.getDescription() : "";
	}

	public String getSelectedVersionNames() {
		return selectedVersionNames;
	}

	public void setSelectedVersionNames(String selectedVersionNames) {
		this.selectedVersionNames = selectedVersionNames;
	}

	public ArrayList<Integer> getSelectedVersionIdList() {
		return selectedVersionIdList;
	}

	public void setSelectedVersionIdList(ArrayList<Integer> selectedVersionIdList) {
		this.selectedVersionIdList = selectedVersionIdList;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getEmailTo() {
		return emailTo;
	}

	public void setEmailTo(String emailTo) {
		this.emailTo = emailTo;
	}

	public String getEmailStep() {
		return emailStep;
	}

	public void setEmailStep(String emailStep) {
		this.emailStep = emailStep;
	}

	public boolean isEvaluatedCRF() {
		return evaluatedCRF;
	}

	public void setEvaluatedCRF(boolean evaluatedCRF) {
		this.evaluatedCRF = evaluatedCRF;
	}

	public ArrayList<SourceDataVerification> getSdvOptions() {
		return sdvOptions;
	}

	public void setSdvOptions(ArrayList<SourceDataVerification> sdvOptions) {
		this.sdvOptions = sdvOptions;
	}

	public String getTabbingMode() {
		return tabbingMode;
	}

	public void setTabbingMode(String tabbingMode) {
		this.tabbingMode = tabbingMode;
	}

	public boolean isAcceptNewCrfVersions() {
		return acceptNewCrfVersions;
	}

	public void setAcceptNewCrfVersions(boolean acceptNewCrfVersions) {
		this.acceptNewCrfVersions = acceptNewCrfVersions;
	}

	public String getSdvCode() {
		return sdvCode;
	}
}
