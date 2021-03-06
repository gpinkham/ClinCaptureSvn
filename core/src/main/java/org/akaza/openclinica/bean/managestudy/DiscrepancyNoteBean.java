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

import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.bean.core.DiscrepancyNoteType;
import org.akaza.openclinica.bean.core.ResolutionStatus;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.service.DiscrepancyNoteUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({ "rawtypes", "serial" })
public class DiscrepancyNoteBean extends AuditableEntityBean implements Comparable {
	public static final String ITEM_DATA = "itemData";
	private String description = "";
	private int discrepancyNoteTypeId;
	private int resolutionStatusId;

	private DiscrepancyNoteType disType;
	private ResolutionStatus resStatus;

	private String detailedNotes = "";
	private int parentDnId;
	private String entityType = "";
	private String column = "";// not in DB
	private int entityId;
	private String field = "";
	private String fieldForDN = "";
	private ArrayList children = new ArrayList();// not in DB
	private int studyId;
	private int eventCRFId;
	private int itemDataOrdinal;

	private UserAccountBean lastUpdator;
	private Date lastDateUpdated;
	private String subjectName = "";
	private String eventName = "";
	private Date eventStart;
	private String crfName = "";
	private String crfStatus = "";
	private int numChildren = 0;
	private String entityName = "";
	private String entityValue = "";
	private boolean isSaved = true;

	private StudyEventBean event = new StudyEventBean();
	private StudySubjectBean studySub = new StudySubjectBean();

	private int subjectId = 0;
	private String siteId;
	private String siteContactEmail;

	// indicates whether a note is in the db or
	// not
	// if entity is ItemData, stageId = 5 means the crf of this ItemData
	// has been marked complete
	private int stageId = 0;
	private int itemId = 0;

	// indicated who the assigned user is if it is a query, tbh
	private int assignedUserId = 0;
	private UserAccountBean assignedUser = new UserAccountBean();

	private String createdDateString = "";
	private String updatedDateString = "";

	private int days = 0;
	private int age = 0;

	public static final Map<String, String> columnToFieldMap = new HashMap<String, String>();
	static {
		columnToFieldMap.put("unique_identifier", "uniqueIdentifier");
		columnToFieldMap.put("date_of_birth", "dob");
		columnToFieldMap.put("gender", "gender");
		columnToFieldMap.put("enrollment_date", "enrollmentDate");
		columnToFieldMap.put("location", "location");
		columnToFieldMap.put("date_start", "date_start");
		columnToFieldMap.put("date_end", "date_end");
	}

	public static Map<String, String> getColumnToFieldMap() {
		return columnToFieldMap;
	}

	private StudyBean study = new StudyBean();

	public String getCreatedDateString() {
		return createdDateString;
	}

	public void setCreatedDateString(String createdDateString) {
		this.createdDateString = createdDateString;
	}

	public String getUpdatedDateString() {
		return updatedDateString;
	}

	public void setUpdatedDateString(String updatedDateString) {
		this.updatedDateString = updatedDateString;
	}

	public int getEventCRFId() {
		return eventCRFId;
	}

	public void setEventCRFId(int eventCRFId) {
		this.eventCRFId = eventCRFId;
	}

	public int getStageId() {
		return this.stageId;
	}

	public void setStageId(int stageid) {
		this.stageId = stageid;
	}

	public int getAssignedUserId() {
		return assignedUserId;
	}

	public void setAssignedUserId(int assignedUserId) {
		this.assignedUserId = assignedUserId;
	}

	public int getItemId() {
		return this.itemId;
	}

	public void setItemId(int itemid) {
		this.itemId = itemid;
	}

	public DiscrepancyNoteBean() {
		this.disType = DiscrepancyNoteType.QUERY;
		this.resStatus = ResolutionStatus.OPEN;
		this.children = new ArrayList();
		this.lastUpdator = new UserAccountBean();
		this.updatedDate = null;
	}

	public DiscrepancyNoteBean(DiscrepancyNoteBean dnb) {
		// simple fields are copied
		this.description = dnb.getDescription();
		this.detailedNotes = dnb.getDetailedNotes();
		this.itemId = dnb.getItemId();
		this.resolutionStatusId = dnb.getResolutionStatusId();
		this.column = dnb.getColumn();
		this.assignedUserId = dnb.getAssignedUserId();
		this.field = dnb.getField();
		this.discrepancyNoteTypeId = dnb.getDiscrepancyNoteTypeId();
		this.parentDnId = dnb.parentDnId;
		this.subjectId = dnb.getSubjectId();
		this.studyId = dnb.getStudyId();
		this.isSaved = dnb.isSaved();
		this.active = dnb.isActive();

		// bean-fields are copied
		this.disType = dnb.getDisType();
		this.resStatus = dnb.getResStatus();
	}

	/**
	 * @return Returns the disType.
	 */
	public DiscrepancyNoteType getDisType() {
		return disType;
	}

	/**
	 * @param disType
	 *            The disType to set.
	 */
	public void setDisType(DiscrepancyNoteType disType) {
		this.disType = disType;
	}

	/**
	 * @return Returns the resStatus.
	 */
	public ResolutionStatus getResStatus() {
		return resStatus;
	}

	/**
	 * @param resStatus
	 *            The resStatus to set.
	 */
	public void setResStatus(ResolutionStatus resStatus) {
		this.resStatus = resStatus;
	}

	/**
	 * @return Returns the field.
	 */
	public String getField() {
		return field;
	}

	/**
	 * @param field
	 *            The field to set.
	 */
	public void setField(String field) {
		this.field = field;
	}

	/**
	 * @return Returns the entityId.
	 */
	public int getEntityId() {
		return entityId;
	}

	/**
	 * @param entityId
	 *            The entityId to set.
	 */
	public void setEntityId(int entityId) {
		this.entityId = entityId;
	}

	/**
	 * @return Returns the column.
	 */
	public String getColumn() {
		return column;
	}

	/**
	 * @param column
	 *            The column to set.
	 */
	public void setColumn(String column) {
		this.column = column;
	}

	/**
	 * @return Returns the children.
	 */
	public ArrayList getChildren() {
		return children;
	}

	/**
	 * @param children
	 *            The children to set.
	 */
	public void setChildren(ArrayList children) {
		this.children = children;
	}

	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return Returns the detailedNotes.
	 */
	public String getDetailedNotes() {
		return detailedNotes;
	}

	/**
	 * @param detailedNotes
	 *            The detailedNotes to set.
	 */
	public void setDetailedNotes(String detailedNotes) {
		this.detailedNotes = detailedNotes;
	}

	/**
	 * @return Returns the discrepancyNoteTypeId.
	 */
	public int getDiscrepancyNoteTypeId() {
		return discrepancyNoteTypeId;
	}

	/**
	 * @param discrepancyNoteTypeId
	 *            The discrepancyNoteTypeId to set.
	 */
	public void setDiscrepancyNoteTypeId(int discrepancyNoteTypeId) {
		this.discrepancyNoteTypeId = discrepancyNoteTypeId;
	}

	/**
	 * @return Returns the parentDnId.
	 */
	public int getParentDnId() {
		return parentDnId;
	}

	/**
	 * @param parentDnId
	 *            The parentDnId to set.
	 */
	public void setParentDnId(int parentDnId) {
		this.parentDnId = parentDnId;
	}

	/**
	 * @return Returns the resolutionStatusId.
	 */
	public int getResolutionStatusId() {
		return resolutionStatusId;
	}

	/**
	 * @param resolutionStatusId
	 *            The resolutionStatusId to set.
	 */
	public void setResolutionStatusId(int resolutionStatusId) {
		this.resolutionStatusId = resolutionStatusId;
		if (resolutionStatusId != 0) {
			this.resStatus = ResolutionStatus.get(resolutionStatusId);
		}
	}

	/**
	 * @return Returns the entityType.
	 */
	public String getEntityType() {
		return entityType;
	}

	/**
	 * @param entityType
	 *            The entityType to set.
	 */
	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	/**
	 * @return Returns the studyId.
	 */
	public int getStudyId() {
		return studyId;
	}

	/**
	 * @param studyId
	 *            The studyId to set.
	 */
	public void setStudyId(int studyId) {
		this.studyId = studyId;
	}

	/**
	 * @return Returns the eventName.
	 */
	public String getEventName() {
		return eventName;
	}

	/**
	 * @param eventName
	 *            The eventName to set.
	 */
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	/**
	 * @return Returns the eventStart.
	 */
	public Date getEventStart() {
		return eventStart;
	}

	/**
	 * @param eventStart
	 *            The eventStart to set.
	 */
	public void setEventStart(Date eventStart) {
		this.eventStart = eventStart;
	}

	/**
	 * @return Returns the lastDateUpdated.
	 */
	public Date getLastDateUpdated() {
		return lastDateUpdated;
	}

	/**
	 * @param lastDateUpdated
	 *            The lastDateUpdated to set.
	 */
	public void setLastDateUpdated(Date lastDateUpdated) {
		this.lastDateUpdated = lastDateUpdated;
	}

	/**
	 * @return Returns the lastUpdator.
	 */
	public UserAccountBean getLastUpdator() {
		return lastUpdator;
	}

	/**
	 * @param lastUpdator
	 *            The lastUpdator to set.
	 */
	public void setLastUpdator(UserAccountBean lastUpdator) {
		this.lastUpdator = lastUpdator;
	}

	/**
	 * @return Returns the subjectName.
	 */
	public String getSubjectName() {
		return subjectName;
	}

	/**
	 * @param subjectName
	 *            The subjectName to set.
	 */
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	/**
	 * @return Returns the crfName.
	 */
	public String getCrfName() {
		return crfName;
	}

	/**
	 * @param crfName
	 *            The crfName to set.
	 */
	public void setCrfName(String crfName) {
		this.crfName = crfName;
	}

	public String getCrfStatus() {
		return crfStatus;
	}

	public void setCrfStatus(String crfStatus) {
		this.crfStatus = crfStatus;
	}

	/**
	 * @return Returns the numChildren.
	 */
	public int getNumChildren() {
		return numChildren;
	}

	/**
	 * @param numChildren
	 *            The numChildren to set.
	 */
	public void setNumChildren(int numChildren) {
		this.numChildren = numChildren;
	}

	/**
	 * @return Returns the entityName.
	 */
	public String getEntityName() {
		return entityName;
	}

	/**
	 * @param entityName
	 *            The entityName to set.
	 */
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	/**
	 * @return Returns the entityValue.
	 */
	public String getEntityValue() {
		return entityValue;
	}

	/**
	 * @param entityValue
	 *            The entityValue to set.
	 */
	public void setEntityValue(String entityValue) {
		this.entityValue = entityValue;
	}

	/**
	 * @return Returns the isSaved.
	 */
	public boolean isSaved() {
		return isSaved;
	}

	/**
	 * @param isSaved
	 *            The isSaved to set.
	 */
	public void setSaved(boolean isSaved) {
		this.isSaved = isSaved;
	}

	/**
	 * @return the subjectId
	 */
	public int getSubjectId() {
		return subjectId;
	}

	/**
	 * @param subjectId
	 *            the subjectId to set
	 */
	public void setSubjectId(int subjectId) {
		this.subjectId = subjectId;
	}

	/**
	 * @return the event
	 */
	public StudyEventBean getEvent() {
		return event;
	}

	/**
	 * @param event
	 *            the event to set
	 */
	public void setEvent(StudyEventBean event) {
		this.event = event;
	}

	/**
	 * @return the studySub
	 */
	public StudySubjectBean getStudySub() {
		return studySub;
	}

	/**
	 * @param studySub
	 *            the studySub to set
	 */
	public void setStudySub(StudySubjectBean studySub) {
		this.studySub = studySub;
	}

	public int compareTo(Object o) {
		if (!o.getClass().equals(this.getClass())) {
			return 0;
		}

		DiscrepancyNoteBean discBean2 = (DiscrepancyNoteBean) o;
		return this.getId() > discBean2.getId() ? 1 : -1;
	}

	public UserAccountBean getAssignedUser() {
		return assignedUser;
	}

	public void setAssignedUser(UserAccountBean assignedUser) {
		this.assignedUser = assignedUser;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public StudyBean getStudy() {
		return study;
	}

	public void setStudy(StudyBean study) {
		this.study = study;
	}

	public String getSiteId() {
		return siteId;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	public String getSiteContactEmail() {
		return siteContactEmail;
	}

	public void setSiteContactEmail(String siteContactEmail) {
		this.siteContactEmail = siteContactEmail != null ? siteContactEmail.trim() : "";
	}

	public String getFieldForDN() {
		return fieldForDN;
	}

	public void setFieldForDN(String fieldForDN) {
		this.fieldForDN = fieldForDN;
	}

	public int getItemDataOrdinal() {
		return itemDataOrdinal;
	}

	public void setItemDataOrdinal(int itemDataOrdinal) {
		this.itemDataOrdinal = itemDataOrdinal;
	}
	
	public boolean isAnnotation() {
		return DiscrepancyNoteUtil.hasType(this, DiscrepancyNoteType.ANNOTATION);
	}
	
	public boolean isQuery() {
		return DiscrepancyNoteUtil.hasType(this, DiscrepancyNoteType.QUERY);
	}

	public boolean isRFC() {
		return DiscrepancyNoteUtil.hasType(this, DiscrepancyNoteType.REASON_FOR_CHANGE);
	}
	
	public boolean isFVC() {
		return DiscrepancyNoteUtil.hasType(this, DiscrepancyNoteType.FAILEDVAL);
	}
}
