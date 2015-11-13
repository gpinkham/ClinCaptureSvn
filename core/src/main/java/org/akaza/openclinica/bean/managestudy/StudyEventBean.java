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
package org.akaza.openclinica.bean.managestudy;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.util.SignedData;

/**
 * StudyEventBean class.
 */
@SuppressWarnings({"rawtypes", "serial"})
public class StudyEventBean extends AuditableEntityBean {

	public static final int STUDY_EVENT_STATES_LENGTH = 3;

	public static final int BY_ITSELF = 0;
	public static final int BY_STUDY_SUBJECT = 2;
	public static final int BY_STUDY_EVENT_DEFINITION = 1;

	// STUDY_EVENT_ID STUDY_EVENT_DEFINITION_ID SUBJECT_ID
	// LOCATION SAMPLE_ORDINAL DATE_START DATE_END
	// OWNER_ID STATUS_ID DATE_CREATED DATE_UPDATED
	// UPDATE_ID
	private int studyEventDefinitionId;

	private int studySubjectId;

	private StudySubjectBean studySubject;

	private String location = "";

	private int sampleOrdinal;

	private SubjectEventStatus prevSubjectEventStatus = SubjectEventStatus.INVALID;

	private Date dateStarted;

	private Date dateEnded;

	// not in database
	private StudyEventDefinitionBean studyEventDefinition = new StudyEventDefinitionBean();

	// not in DB
	private ArrayList eventCRFs = new ArrayList();

	private DataEntryStage stage;

	private SubjectEventStatus subjectEventStatus;

	private String studySubjectLabel;

	// not in DB
	private boolean scheduledDatePast = false;

	private int repeatingNum = 1;

	private ArrayList<StudyEventBean> repeatEvents = new ArrayList<StudyEventBean>();

	// will the edit icon be appeared
	private boolean editable = true;

	private boolean startTimeFlag = false;
	private boolean endTimeFlag = false;

	private int studyEventDefinitionOrdinal;

	private Map<Integer, SignedData> signedData = new HashMap<Integer, SignedData>();

	public void setSignedData(Map<Integer, SignedData> signedData) {
		this.signedData = signedData;
	}

	public Map<Integer, SignedData> getSignedData() {
		return signedData;
	}

	private int referenceVisitId;

	public int getReferenceVisitId() {
		return referenceVisitId;
	}

	public void setReferenceVisitId(int referenceVisitId) {
		this.referenceVisitId = referenceVisitId;
	}

	// For display discrepancy notes in a matrix-type study/event grid; 5/2/2008
	private ArrayList<DiscrepancyNoteBean> discBeanList = new ArrayList<DiscrepancyNoteBean>();

	public ArrayList<DiscrepancyNoteBean> getDiscBeanList() {
		return discBeanList;
	}

	public void setDiscBeanList(ArrayList<DiscrepancyNoteBean> discBeanList) {
		this.discBeanList = discBeanList;
	}

	/**
	 * @return startTimeFlag
	 */
	public boolean getStartTimeFlag() {
		return startTimeFlag;
	}

	/**
	 * 
	 * @param startTimeFlag
	 *            boolean
	 */
	public void setStartTimeFlag(boolean startTimeFlag) {
		this.startTimeFlag = startTimeFlag;
	}

	/**
	 * 
	 * @return endTimeFlag
	 */
	public boolean getEndTimeFlag() {
		return endTimeFlag;
	}

	/**
	 * 
	 * @param endTimeFlag
	 *            boolean
	 */
	public void setEndTimeFlag(boolean endTimeFlag) {
		this.endTimeFlag = endTimeFlag;
	}

	/**
	 * @return the repeatEvents
	 */
	public ArrayList<StudyEventBean> getRepeatEvents() {
		return repeatEvents;
	}

	/**
	 * @param repeatEvents
	 *            the repeatEvents to set
	 */
	public void setRepeatEvents(ArrayList<StudyEventBean> repeatEvents) {
		this.repeatEvents = repeatEvents;
	}

	/**
	 * @return Returns the repeatingNum.
	 */
	public int getRepeatingNum() {
		return repeatingNum;
	}

	/**
	 * @param repeatingNum
	 *            The repeatingNum to set.
	 */
	public void setRepeatingNum(int repeatingNum) {
		this.repeatingNum = repeatingNum;
	}

	/**
	 * @return Returns the studySubjectLabel.
	 */
	public String getStudySubjectLabel() {
		return studySubjectLabel;
	}

	/**
	 * @param studySubjectLabel
	 *            The studySubjectLabel to set.
	 */
	public void setStudySubjectLabel(String studySubjectLabel) {
		this.studySubjectLabel = studySubjectLabel;
	}

	/**
	 * @return Returns the subjectEventStatus.
	 */
	public SubjectEventStatus getSubjectEventStatus() {
		return subjectEventStatus;
	}

	/**
	 * @param subjectEventStatus
	 *            The subjectEventStatus to set.
	 */
	public void setSubjectEventStatus(SubjectEventStatus subjectEventStatus) {
		this.subjectEventStatus = subjectEventStatus;
	}

	/**
	 * StudyEventBean constructor.
	 */
	public StudyEventBean() {
		stage = DataEntryStage.UNCOMPLETED;
		subjectEventStatus = SubjectEventStatus.SCHEDULED;
	}

	/**
	 * StudyEventBean constructor.
	 * 
	 * @param obj
	 *            StudyEventBean
	 */
	public StudyEventBean(StudyEventBean obj) {
		this.id = obj.getId();
		this.stage = obj.getStage();
		this.subjectEventStatus = obj.getPrevSubjectEventStatus();
		this.dateEnded = obj.getDateEnded();
		this.dateStarted = obj.getDateStarted();
		this.studyEventDefinitionId = obj.getStudyEventDefinitionId();
		this.studySubjectId = obj.getStudySubjectId();
		this.studySubject = obj.getStudySubject();
		this.location = obj.getLocation();
		this.sampleOrdinal = obj.getSampleOrdinal();
		this.studySubjectLabel = obj.getStudySubjectLabel();
		this.updatedDate = obj.getUpdatedDate();
	}

	/**
	 * @return Returns the dateEnded.
	 */
	public Date getDateEnded() {
		return dateEnded;
	}

	/**
	 * @param dateEnded
	 *            The dateEnded to set.
	 */
	public void setDateEnded(Date dateEnded) {
		this.dateEnded = dateEnded;
	}

	/**
	 * @return Returns the dateStarted.
	 */
	public Date getDateStarted() {
		return dateStarted;
	}

	/**
	 * @param dateStarted
	 *            The dateStarted to set.
	 */
	public void setDateStarted(Date dateStarted) {
		this.dateStarted = dateStarted;
	}

	/**
	 * @return Returns the location.
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param location
	 *            The location to set.
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return Returns the sampleOrdinal.
	 */
	public int getSampleOrdinal() {
		return sampleOrdinal;
	}

	/**
	 * @param sampleOrdinal
	 *            The sampleOrdinal to set.
	 */
	public void setSampleOrdinal(int sampleOrdinal) {
		this.sampleOrdinal = sampleOrdinal;
	}

	/**
	 * @return Returns the studyEventDefinitionId.
	 */
	public int getStudyEventDefinitionId() {
		return studyEventDefinitionId;
	}

	/**
	 * @param studyEventDefinitionId
	 *            The studyEventDefinitionId to set.
	 */
	public void setStudyEventDefinitionId(int studyEventDefinitionId) {
		this.studyEventDefinitionId = studyEventDefinitionId;
	}

	/**
	 * @return Returns the studySubjectId.
	 */
	public int getStudySubjectId() {
		return studySubjectId;
	}

	/**
	 * @param studySubjectId
	 *            The studySubjectId to set.
	 */
	public void setStudySubjectId(int studySubjectId) {
		this.studySubjectId = studySubjectId;
	}

	/**
	 * @return Returns the studyEventDefinition.
	 */
	public StudyEventDefinitionBean getStudyEventDefinition() {
		return studyEventDefinition;
	}

	/**
	 * @param studyEventDefinition
	 *            The studyEventDefinition to set.
	 */
	public void setStudyEventDefinition(StudyEventDefinitionBean studyEventDefinition) {
		this.studyEventDefinition = studyEventDefinition;
	}

	/**
	 * @return Returns the eventCRFs.
	 */
	public ArrayList getEventCRFs() {
		return eventCRFs;
	}

	/**
	 * @param eventCRFs
	 *            The eventCRFs to set.
	 */
	public void setEventCRFs(ArrayList eventCRFs) {
		this.eventCRFs = eventCRFs;
	}

	/**
	 * @return Returns the stage.
	 */
	public DataEntryStage getStage() {
		return stage;
	}

	@Override
	public void setStatus(Status s) {
		this.status = s;

		if (s.equals(Status.AVAILABLE)) {
			stage = DataEntryStage.UNCOMPLETED;
		} else if (s.equals(Status.PENDING)) {
			stage = DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE;
		} else if (s.equals(Status.UNAVAILABLE)) {
			stage = DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE;
		}
	}

	/**
	 * @param stage
	 *            The stage to set.
	 */
	public void setStage(DataEntryStage stage) {
		this.stage = stage;
	}

	/**
	 * @return Returns the scheduledDatePast.
	 */
	public boolean isScheduledDatePast() {
		return scheduledDatePast;
	}

	/**
	 * @param scheduledDatePast
	 *            The scheduledDatePast to set.
	 */
	public void setScheduledDatePast(boolean scheduledDatePast) {
		this.scheduledDatePast = scheduledDatePast;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public StudySubjectBean getStudySubject() {
		return studySubject;
	}

	public void setStudySubject(StudySubjectBean studySubject) {
		this.studySubject = studySubject;
	}

	public SubjectEventStatus getPrevSubjectEventStatus() {
		return prevSubjectEventStatus;
	}

	public void setPrevSubjectEventStatus(SubjectEventStatus prevSubjectEventStatus) {
		this.prevSubjectEventStatus = prevSubjectEventStatus;
	}

	public int getStudyEventDefinitionOrdinal() {
		return studyEventDefinitionOrdinal;
	}

	public void setStudyEventDefinitionOrdinal(int studyEventDefinitionOrdinal) {
		this.studyEventDefinitionOrdinal = studyEventDefinitionOrdinal;
	}

	@Override
	protected int getStatesLength() {
		return STUDY_EVENT_STATES_LENGTH;
	}
}
