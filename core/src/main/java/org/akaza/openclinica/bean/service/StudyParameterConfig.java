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
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.service;

/**
 * This is a help class for each study, it saves all the parameter configurations
 * 
 * @author jxu
 * 
 */
public class StudyParameterConfig {

	private String collectDob;

	private String discrepancyManagement;

	private String genderRequired;// true or false

	private String subjectPersonIdRequired; // required, optional or not used

	private String interviewerNameRequired;// required, optional

	private String interviewerNameDefault;// blank,pre-populated

	private String interviewerNameEditable;// editable or not

	private String interviewDateRequired;// required, optional

	private String interviewDateDefault; // blank, pre-populated

	private String interviewDateEditable;// editable or not

	private String subjectIdGeneration;// manual, auto non-editable, auto
	// editable,

	private String subjectIdPrefixSuffix;// auto with prefix/suffix, or not

	private String personIdShownOnCRF;// personal Id is shown on CRF header or
	// not

	private String secondaryLabelViewable;// Subject secondary label would be shown on CRF header

	private String adminForcedReasonForChange;// Administrative editing will not allow changes without a DN added
												// 'reason for change'

	private String eventLocationRequired;

	private String secondaryIdRequired; // required, optional, or not used

	private String dateOfEnrollmentForStudyRequired; // required, optional, or not used

	private String studySubjectIdLabel;

	private String secondaryIdLabel;

	private String dateOfEnrollmentForStudyLabel;

	private String genderLabel;

	private String startDateTimeRequired;

	private String useStartTime;

	private String endDateTimeRequired;

	private String useEndTime;

	private String startDateTimeLabel;

	private String endDateTimeLabel;

	private String markImportedCRFAsCompleted;

    private String allowSdvWithOpenQueries;

    private String replaceExisitingDataDuringImport;
    
    private String autoScheduleEventDuringImport;
    
    // Medical coding parameters
    private String allowCodingVerification = "no";
    private String defaultBioontologyURL = "";
    private String autoCodeDictionaryName = "";
    private String medicalCodingApprovalNeeded = "no";
    private String medicalCodingContextNeeded = "no";
    

	public StudyParameterConfig() {
		
		collectDob = "1";
		discrepancyManagement = "true";
		genderRequired = "true";
		subjectPersonIdRequired = "required";
		interviewerNameRequired = "not_used";

		interviewerNameDefault = "blank";
		interviewerNameEditable = "true";
		interviewDateRequired = "not_used";
		interviewDateDefault = "blank"; // blank, pre-populated
		interviewDateEditable = "true";// editable or not
		subjectIdGeneration = "manual";// manual, auto non-editable, auto
		// editable,
		subjectIdPrefixSuffix = "true";
		personIdShownOnCRF = "false";
		secondaryLabelViewable = "false";
		adminForcedReasonForChange = "true";
		eventLocationRequired = "not_used";

		secondaryIdRequired = "no";
		dateOfEnrollmentForStudyRequired = "yes";
		studySubjectIdLabel = "Study Subject ID";
		secondaryIdLabel = "Secondary ID";
		dateOfEnrollmentForStudyLabel = "Date of Enrollment for Study";
		genderLabel = "Sex";

		startDateTimeRequired = "yes";
		useStartTime = "yes";
		endDateTimeRequired = "no";
		useEndTime = "yes";
		startDateTimeLabel = "Start Date/Time";
		endDateTimeLabel = "End Date/Time";

		markImportedCRFAsCompleted = "no";
		autoScheduleEventDuringImport = "no";
		allowSdvWithOpenQueries = "no";
		replaceExisitingDataDuringImport = "no";
	}

	/**
	 * @return Returns the collectDob.
	 */
	public String getCollectDob() {
		return collectDob;
	}

	/**
	 * @param collectDob
	 *            The collectDob to set.
	 */
	public void setCollectDob(String collectDob) {
		this.collectDob = collectDob;
	}

	/**
	 * @return Returns the discrepancyManagement.
	 */
	public String getDiscrepancyManagement() {
		return discrepancyManagement;
	}

	/**
	 * @param discrepancyManagement
	 *            The discrepancyManagement to set.
	 */
	public void setDiscrepancyManagement(String discrepancyManagement) {
		this.discrepancyManagement = discrepancyManagement;
	}

	/**
	 * @return Returns the genderRequired.
	 */
	public String getGenderRequired() {
		return genderRequired;
	}

	/**
	 * @param genderRequired
	 *            The genderRequired to set.
	 */
	public void setGenderRequired(String genderRequired) {
		this.genderRequired = genderRequired;
	}

	/**
	 * @return Returns the interviewDateDefault.
	 */
	public String getInterviewDateDefault() {
		return interviewDateDefault;
	}

	/**
	 * @param interviewDateDefault
	 *            The interviewDateDefault to set.
	 */
	public void setInterviewDateDefault(String interviewDateDefault) {
		this.interviewDateDefault = interviewDateDefault;
	}

	/**
	 * @return Returns the interviewDateEditable.
	 */
	public String getInterviewDateEditable() {
		return interviewDateEditable;
	}

	/**
	 * @param interviewDateEditable
	 *            The interviewDateEditable to set.
	 */
	public void setInterviewDateEditable(String interviewDateEditable) {
		this.interviewDateEditable = interviewDateEditable;
	}

	/**
	 * @return Returns the interviewDateRequired.
	 */
	public String getInterviewDateRequired() {
		return interviewDateRequired;
	}

	/**
	 * @param interviewDateRequired
	 *            The interviewDateRequired to set.
	 */
	public void setInterviewDateRequired(String interviewDateRequired) {
		this.interviewDateRequired = interviewDateRequired;
	}

	/**
	 * @return Returns the interviewerNameDefault.
	 */
	public String getInterviewerNameDefault() {
		return interviewerNameDefault;
	}

	/**
	 * @param interviewerNameDefault
	 *            The interviewerNameDefault to set.
	 */
	public void setInterviewerNameDefault(String interviewerNameDefault) {
		this.interviewerNameDefault = interviewerNameDefault;
	}

	/**
	 * @return Returns the interviewerNameEditable.
	 */
	public String getInterviewerNameEditable() {
		return interviewerNameEditable;
	}

	/**
	 * @param interviewerNameEditable
	 *            The interviewerNameEditable to set.
	 */
	public void setInterviewerNameEditable(String interviewerNameEditable) {
		this.interviewerNameEditable = interviewerNameEditable;
	}

	/**
	 * @return Returns the interviewerNameRequired.
	 */
	public String getInterviewerNameRequired() {
		return interviewerNameRequired;
	}

	/**
	 * @param interviewerNameRequired
	 *            The interviewerNameRequired to set.
	 */
	public void setInterviewerNameRequired(String interviewerNameRequired) {
		this.interviewerNameRequired = interviewerNameRequired;
	}

	/**
	 * @return Returns the subjectIdGeneration.
	 */
	public String getSubjectIdGeneration() {
		return subjectIdGeneration;
	}

	/**
	 * @param subjectIdGeneration
	 *            The subjectIdGeneration to set.
	 */
	public void setSubjectIdGeneration(String subjectIdGeneration) {
		this.subjectIdGeneration = subjectIdGeneration;
	}

	/**
	 * @return Returns the subjectIdPrefixSuffix.
	 */
	public String getSubjectIdPrefixSuffix() {
		return subjectIdPrefixSuffix;
	}

	/**
	 * @param subjectIdPrefixSuffix
	 *            The subjectIdPrefixSuffix to set.
	 */
	public void setSubjectIdPrefixSuffix(String subjectIdPrefixSuffix) {
		this.subjectIdPrefixSuffix = subjectIdPrefixSuffix;
	}

	/**
	 * @return Returns the subjectPersonIdRequired.
	 */
	public String getSubjectPersonIdRequired() {
		return subjectPersonIdRequired;
	}

	/**
	 * @param subjectPersonIdRequired
	 *            The subjectPersonIdRequired to set.
	 */
	public void setSubjectPersonIdRequired(String subjectPersonIdRequired) {
		this.subjectPersonIdRequired = subjectPersonIdRequired;
	}

	/**
	 * @return Returns the personIdShownOnCRF.
	 */
	public String getPersonIdShownOnCRF() {
		return personIdShownOnCRF;
	}

	/**
	 * @param personIdShownOnCRF
	 *            The personIdShownOnCRF to set.
	 */
	public void setPersonIdShownOnCRF(String personIdShownOnCRF) {
		this.personIdShownOnCRF = personIdShownOnCRF;
	}

	public String getSecondaryLabelViewable() {
		return secondaryLabelViewable;
	}

	public void setSecondaryLabelViewable(String secondaryLabelViewable) {
		this.secondaryLabelViewable = secondaryLabelViewable;
	}

	public String getAdminForcedReasonForChange() {
		return adminForcedReasonForChange;
	}

	public void setAdminForcedReasonForChange(String adminForcedReasonForChange) {
		this.adminForcedReasonForChange = adminForcedReasonForChange;
	}

	public String getEventLocationRequired() {
		return eventLocationRequired;
	}

	public void setEventLocationRequired(String eventLocationRequired) {
		this.eventLocationRequired = eventLocationRequired;
	}

	public String getSecondaryIdRequired() {
		return secondaryIdRequired;
	}

	public void setSecondaryIdRequired(String secondaryIdRequired) {
		this.secondaryIdRequired = secondaryIdRequired;
	}

	public String getDateOfEnrollmentForStudyRequired() {
		return dateOfEnrollmentForStudyRequired;
	}

	public void setDateOfEnrollmentForStudyRequired(String dateOfEnrollmentForStudyRequired) {
		this.dateOfEnrollmentForStudyRequired = dateOfEnrollmentForStudyRequired;
	}

	public String getStudySubjectIdLabel() {
		return studySubjectIdLabel;
	}

	public void setStudySubjectIdLabel(String studySubjectIdLabel) {
		this.studySubjectIdLabel = studySubjectIdLabel;
	}

	public String getSecondaryIdLabel() {
		return secondaryIdLabel;
	}

	public void setSecondaryIdLabel(String secondaryIdLabel) {
		this.secondaryIdLabel = secondaryIdLabel;
	}

	public String getDateOfEnrollmentForStudyLabel() {
		return dateOfEnrollmentForStudyLabel;
	}

	public void setDateOfEnrollmentForStudyLabel(String dateOfEnrollmentForStudyLabel) {
		this.dateOfEnrollmentForStudyLabel = dateOfEnrollmentForStudyLabel;
	}

	public String getGenderLabel() {
		return genderLabel;
	}

	public void setGenderLabel(String genderLabel) {
		this.genderLabel = genderLabel;
	}

	public String getStartDateTimeRequired() {
		return startDateTimeRequired;
	}

	public void setStartDateTimeRequired(String startDateTimeRequired) {
		this.startDateTimeRequired = startDateTimeRequired;
	}

	public String getUseStartTime() {
		return useStartTime;
	}

	public void setUseStartTime(String useStartTime) {
		this.useStartTime = useStartTime;
	}

	public String getEndDateTimeRequired() {
		return endDateTimeRequired;
	}

	public void setEndDateTimeRequired(String endDateTimeRequired) {
		this.endDateTimeRequired = endDateTimeRequired;
	}

	public String getUseEndTime() {
		return useEndTime;
	}

	public void setUseEndTime(String useEndTime) {
		this.useEndTime = useEndTime;
	}

	public String getStartDateTimeLabel() {
		return startDateTimeLabel;
	}

	public void setStartDateTimeLabel(String startDateTimeLabel) {
		this.startDateTimeLabel = startDateTimeLabel;
	}

	public String getEndDateTimeLabel() {
		return endDateTimeLabel;
	}

	public void setEndDateTimeLabel(String endDateTimeLabel) {
		this.endDateTimeLabel = endDateTimeLabel;
	}

	public String getMarkImportedCRFAsCompleted() {
		return markImportedCRFAsCompleted;
	}

	public void setMarkImportedCRFAsCompleted(String markImportedCRFAsCompleted) {
		this.markImportedCRFAsCompleted = markImportedCRFAsCompleted;
	}

	public String getAllowSdvWithOpenQueries() {
		return allowSdvWithOpenQueries;
	}

	public void setAllowSdvWithOpenQueries(String allowSdvWithOpenQueries) {
		this.allowSdvWithOpenQueries = allowSdvWithOpenQueries;
	}

	public String getReplaceExisitingDataDuringImport() {
		return replaceExisitingDataDuringImport;
	}

	public void setReplaceExisitingDataDuringImport(String replaceExisitingDataDuringImport) {
		this.replaceExisitingDataDuringImport = replaceExisitingDataDuringImport;
	}

	public String getAllowCodingVerification() {
		return allowCodingVerification;
	}

	public void setAllowCodingVerification(String allowCodingVerification) {
		this.allowCodingVerification = allowCodingVerification;
	}

	public String getDefaultBioontologyURL() {
		return defaultBioontologyURL;
	}

	public void setDefaultBioontologyURL(String defaultBioontologyURL) {
		this.defaultBioontologyURL = defaultBioontologyURL;
	}

	public String getAutoCodeDictionaryName() {
		return autoCodeDictionaryName;
	}

	public void setAutoCodeDictionaryName(String autoCodeDictionaryName) {
		this.autoCodeDictionaryName = autoCodeDictionaryName;
	}

	public String getMedicalCodingApprovalNeeded() {
		return medicalCodingApprovalNeeded;
	}

	public void setMedicalCodingApprovalNeeded(String medicalCodingApprovalNeeded) {
		this.medicalCodingApprovalNeeded = medicalCodingApprovalNeeded;
	}

	public String getAutoScheduleEventDuringImport() {
		return autoScheduleEventDuringImport;
	}

	public void setAutoScheduleEventDuringImport(String autoScheduleEventDuringImport) {
		this.autoScheduleEventDuringImport = autoScheduleEventDuringImport;
	}

    public String getMedicalCodingContextNeeded() {
        return medicalCodingContextNeeded;
    }

    public void setMedicalCodingContextNeeded(String medicalCodingContextNeeded) {
        this.medicalCodingContextNeeded = medicalCodingContextNeeded;
    }
}
