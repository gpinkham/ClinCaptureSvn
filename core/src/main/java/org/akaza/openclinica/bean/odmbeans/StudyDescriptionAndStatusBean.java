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

public class StudyDescriptionAndStatusBean {
	// attributes
	private String officialTitle;
	private String secondaryIds;
	private String dateCreated;
	private String startDate;
	private String studyCompletionDate;
	// elements
	private String studySytemStatus;
	private String principalInvestigator;
	private String detailedDescription;
	private String sponsor;
	private String collaborators;
	private String studyPhase;
	private String protocolType;
	private String protocolVerificationDate;
	private String purpose;
	private String selection;
	private String studyClassification;

	public String getOfficialTitle() {
		return officialTitle;
	}

	public void setOfficialTitle(String officialTitle) {
		this.officialTitle = officialTitle;
	}

	public String getSecondaryIds() {
		return secondaryIds;
	}

	public void setSecondaryIds(String secondaryIds) {
		this.secondaryIds = secondaryIds;
	}

	public String getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getStudyCompletionDate() {
		return studyCompletionDate;
	}

	public void setStudyCompletionDate(String studyCompletionDate) {
		this.studyCompletionDate = studyCompletionDate;
	}

	public String getStudySytemStatus() {
		return studySytemStatus;
	}

	public void setStudySytemStatus(String studySytemStatus) {
		this.studySytemStatus = studySytemStatus;
	}

	public String getPrincipalInvestigator() {
		return principalInvestigator;
	}

	public void setPrincipalInvestigator(String principalInvestigator) {
		this.principalInvestigator = principalInvestigator;
	}

	public String getDetailedDescription() {
		return detailedDescription;
	}

	public void setDetailedDescription(String detailedDescription) {
		this.detailedDescription = detailedDescription;
	}

	public String getSponsor() {
		return sponsor;
	}

	public void setSponsor(String sponsor) {
		this.sponsor = sponsor;
	}

	public String getCollaborators() {
		return collaborators;
	}

	public void setCollaborators(String collaborators) {
		this.collaborators = collaborators;
	}

	public String getStudyPhase() {
		return studyPhase;
	}

	public void setStudyPhase(String studyPhase) {
		this.studyPhase = studyPhase;
	}

	public String getProtocolType() {
		return protocolType;
	}

	public void setProtocolType(String protocolType) {
		this.protocolType = protocolType;
	}

	public String getProtocolVerificationDate() {
		return protocolVerificationDate;
	}

	public void setProtocolVerificationDate(String protocolVerificationDate) {
		this.protocolVerificationDate = protocolVerificationDate;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getSelection() {
		return selection;
	}

	public void setSelection(String selection) {
		this.selection = selection;
	}

	public String getStudyClassification() {
		return studyClassification;
	}

	public void setStudyClassification(String studyClassification) {
		this.studyClassification = studyClassification;
	}
}
