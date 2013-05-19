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

/**
 * 
 * @author ywang (Aug, 2010)
 * 
 */

public class StudyParameterConfigurationBean {
	private String collectSubjectDateOfBirth;
	private String allowDiscrepancyManagement;
	private String sexRequired;
	private String personIDRequired;
	private String showPersonIDOnCRFHeader;
	private String howToGenerateStudySubjectID;
	private String interviewerNameRequiredForDataEntry;
	private String interviewerNameDefaultAsBlank;
	private String interviewerNameEditable;
	private String interviewerDateRequired;
	private String interviewerDateDefaultAsBlank;
	private String interviewDateEditable;
	private String secondatryLabelViewable;
	private String forcedReasonForChangeInAdministrativeEditing;

	public String getCollectSubjectDateOfBirth() {
		return collectSubjectDateOfBirth;
	}

	public void setCollectSubjectDateOfBirth(String collectSubjectDateOfBirth) {
		this.collectSubjectDateOfBirth = collectSubjectDateOfBirth;
	}

	public String getAllowDiscrepancyManagement() {
		return allowDiscrepancyManagement;
	}

	public void setAllowDiscrepancyManagement(String allowDiscrepancyManagement) {
		this.allowDiscrepancyManagement = allowDiscrepancyManagement;
	}

	public String getSexRequired() {
		return sexRequired;
	}

	public void setSexRequired(String sexRequired) {
		this.sexRequired = sexRequired;
	}

	public String getPersonIDRequired() {
		return personIDRequired;
	}

	public void setPersonIDRequired(String personIDRequired) {
		this.personIDRequired = personIDRequired;
	}

	public String getShowPersonIDOnCRFHeader() {
		return showPersonIDOnCRFHeader;
	}

	public void setShowPersonIDOnCRFHeader(String showPersonIDOnCRFHeader) {
		this.showPersonIDOnCRFHeader = showPersonIDOnCRFHeader;
	}

	public String getHowToGenerateStudySubjectID() {
		return howToGenerateStudySubjectID;
	}

	public void setHowToGenerateStudySubjectID(String howToGenerateStudySubjectID) {
		this.howToGenerateStudySubjectID = howToGenerateStudySubjectID;
	}

	public String getInterviewerNameRequiredForDataEntry() {
		return interviewerNameRequiredForDataEntry;
	}

	public void setInterviewerNameRequiredForDataEntry(String interviewerNameRequiredForDataEntry) {
		this.interviewerNameRequiredForDataEntry = interviewerNameRequiredForDataEntry;
	}

	public String getInterviewerNameDefaultAsBlank() {
		return interviewerNameDefaultAsBlank;
	}

	public void setInterviewerNameDefaultAsBlank(String interviewerNameDefaultAsBlank) {
		this.interviewerNameDefaultAsBlank = interviewerNameDefaultAsBlank;
	}

	public String getInterviewerNameEditable() {
		return interviewerNameEditable;
	}

	public void setInterviewerNameEditable(String interviewerNameEditable) {
		this.interviewerNameEditable = interviewerNameEditable;
	}

	public String getInterviewerDateRequired() {
		return interviewerDateRequired;
	}

	public void setInterviewerDateRequired(String interviewerDateRequired) {
		this.interviewerDateRequired = interviewerDateRequired;
	}

	public String getInterviewerDateDefaultAsBlank() {
		return interviewerDateDefaultAsBlank;
	}

	public void setInterviewerDateDefaultAsBlank(String interviewerDateDefaultAsBlank) {
		this.interviewerDateDefaultAsBlank = interviewerDateDefaultAsBlank;
	}

	public String getInterviewDateEditable() {
		return interviewDateEditable;
	}

	public void setInterviewDateEditable(String interviewDateEditable) {
		this.interviewDateEditable = interviewDateEditable;
	}

	public String getSecondatryLabelViewable() {
		return secondatryLabelViewable;
	}

	public void setSecondatryLabelViewable(String secondatryLabelViewable) {
		this.secondatryLabelViewable = secondatryLabelViewable;
	}

	public String getForcedReasonForChangeInAdministrativeEditing() {
		return forcedReasonForChangeInAdministrativeEditing;
	}

	public void setForcedReasonForChangeInAdministrativeEditing(String forcedReasonForChangeInAdministrativeEditing) {
		this.forcedReasonForChangeInAdministrativeEditing = forcedReasonForChangeInAdministrativeEditing;
	}
}
