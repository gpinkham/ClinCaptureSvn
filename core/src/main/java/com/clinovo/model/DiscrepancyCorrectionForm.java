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
package com.clinovo.model;

import java.util.Date;

/**
 * DCF model class.
 * 
 * @author Frank
 * 
 */
public class DiscrepancyCorrectionForm {

	private String studyName;
	private String studyProtocolID;
	private String siteName;
	private String siteOID;
	private String subjectId;
	private String investigatorName;
	private Integer noteId;
	private String noteType;
	private Date noteDate;
	private String resolutionStatus;
	private String eventName;
	private String crfName;
	private String page;
	private String crfItemName;
	private String crfItemValue;
	private String subjectItemName;
	private String subjectItemValue;
	private String eventItemName;
	private String eventItemValue;
	private String entityType;
	private Integer entityId;
	private String questionToSite;

	public String getStudyName() {
		return studyName;
	}

	public void setStudyName(String studyName) {
		this.studyName = studyName;
	}

	public String getStudyProtocolID() {
		return studyProtocolID;
	}

	public void setStudyProtocolID(String studyProtocolID) {
		this.studyProtocolID = studyProtocolID;
	}

	public String getSiteOID() {
		return siteOID;
	}

	public void setSiteOID(String siteOID) {
		this.siteOID = siteOID;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public String getInvestigatorName() {
		return investigatorName;
	}

	public void setInvestigatorName(String investigatorName) {
		this.investigatorName = investigatorName;
	}

	public Integer getNoteId() {
		return noteId;
	}

	public void setNoteId(Integer noteId) {
		this.noteId = noteId;
	}

	public String getNoteType() {
		return noteType;
	}

	public void setNoteType(String noteType) {
		this.noteType = noteType;
	}

	public Date getNoteDate() {
		return noteDate;
	}

	public void setNoteDate(Date noteDate) {
		this.noteDate = noteDate;
	}

	public String getResolutionStatus() {
		return resolutionStatus;
	}

	public void setResolutionStatus(String resolutionStatus) {
		this.resolutionStatus = resolutionStatus;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getCrfName() {
		return crfName;
	}

	public void setCrfName(String crfName) {
		this.crfName = crfName;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getCrfItemName() {
		return crfItemName;
	}

	public void setCrfItemName(String crfItemName) {
		this.crfItemName = crfItemName;
	}

	public String getCrfItemValue() {
		return crfItemValue;
	}

	public void setCrfItemValue(String crfItemValue) {
		this.crfItemValue = crfItemValue;
	}

	public String getSubjectItemName() {
		return subjectItemName;
	}

	public void setSubjectItemName(String subjectItemName) {
		this.subjectItemName = subjectItemName;
	}

	public String getSubjectItemValue() {
		return subjectItemValue;
	}

	public void setSubjectItemValue(String subjectItemValue) {
		this.subjectItemValue = subjectItemValue;
	}

	public String getEventItemName() {
		return eventItemName;
	}

	public void setEventItemName(String eventItemName) {
		this.eventItemName = eventItemName;
	}

	public String getEventItemValue() {
		return eventItemValue;
	}

	public void setEventItemValue(String eventItemValue) {
		this.eventItemValue = eventItemValue;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public Integer getEntityId() {
		return entityId;
	}

	public void setEntityId(Integer entityId) {
		this.entityId = entityId;
	}

	public String getQuestionToSite() {
		return questionToSite;
	}

	public void setQuestionToSite(String questionToSite) {
		this.questionToSite = questionToSite;
	}

	/**
	 * Generates DCF file name.
	 * 
	 * @return DCF file name
	 */
	public String getDcfFileName() {
		final String underscore = "_";
		StringBuilder fileName = new StringBuilder(getStudyProtocolID()).append(underscore).append(getSiteOID())
				.append(underscore).append(getNoteType()).append(underscore).append(getNoteId());
		return fileName.toString().replace(' ', '_');
	}
}