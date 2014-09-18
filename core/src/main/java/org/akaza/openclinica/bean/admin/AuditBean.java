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

package org.akaza.openclinica.bean.admin;

import org.akaza.openclinica.bean.core.EntityBean;

import java.sql.Timestamp;
import java.util.Date;

/**
 * AuditBean class.
 */
@SuppressWarnings("serial")
public class AuditBean extends EntityBean {

	private Date auditDate;
	private String auditTable;
	private int userId;
	private int entityId;
	private String entityName;
	private String reasonForChange;
	private int auditEventTypeId;
	private String oldValue;
	private String newValue;
	private int eventCRFId;
	private String userName;
	private String auditEventTypeName;
	private int studyEventId;

	private int itemDataTypeId;
	private int ordinal;

	private int itemId;
	private String itemDescription;

	private String crfName;
	private String crfVersion;
	private Timestamp dateStart;
	private String location;
	private Date dateInterviewed;
	private String interviewerName;
	private int studyEventSampleOrdinal;
	private int eventDefinitionCrfId;
	private int studyEventDefinitionId;
	private int studySubjectId;
	private int crfVersionId;

	public int getItemDataTypeId() {
		return this.itemDataTypeId;
	}

	public void setItemDataTypeId(int itemDataTypeId) {
		this.itemDataTypeId = itemDataTypeId;
	}

	public Date getAuditDate() {
		return auditDate;
	}

	public void setAuditDate(Date auditDate) {
		this.auditDate = auditDate;
	}

	public int getAuditEventTypeId() {
		return auditEventTypeId;
	}

	public void setAuditEventTypeId(int auditEventTypeId) {
		this.auditEventTypeId = auditEventTypeId;
	}

	public String getAuditTable() {
		return auditTable;
	}

	public void setAuditTable(String auditTable) {
		this.auditTable = auditTable;
	}

	public int getEntityId() {
		return entityId;
	}

	public void setEntityId(int entityId) {
		this.entityId = entityId;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public int getEventCRFId() {
		return eventCRFId;
	}

	public void setEventCRFId(int eventCRFId) {
		this.eventCRFId = eventCRFId;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public String getReasonForChange() {
		return reasonForChange;
	}

	public void setReasonForChange(String reasonForChange) {
		this.reasonForChange = reasonForChange;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getAuditEventTypeName() {
		return auditEventTypeName;
	}

	public void setAuditEventTypeName(String auditEventTypeName) {
		this.auditEventTypeName = auditEventTypeName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getStudyEventId() {
		return studyEventId;
	}

	public void setStudyEventId(int studyEventId) {
		this.studyEventId = studyEventId;
	}

	public int getOrdinal() {
		return ordinal;
	}

	public void setOrdinal(int ordinal) {
		this.ordinal = ordinal;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (auditTable == null ? 0 : auditTable.hashCode());
		result = prime * result + userId;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final AuditBean other = (AuditBean) obj;
		if (auditTable == null) {
			if (other.auditTable != null) {
				return false;
			}
		} else if (!auditTable.equals(other.auditTable)) {
			return false;
		}
		if (userId != other.userId) {
			return false;
		}
		if (id != other.id) {
			return false;
		}
		return true;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	public int getStudyEventSampleOrdinal() {
		return studyEventSampleOrdinal;
	}

	public void setStudyEventSampleOrdinal(int studyEventSampleOrdinal) {
		this.studyEventSampleOrdinal = studyEventSampleOrdinal;
	}

	public int getEventDefinitionCrfId() {
		return eventDefinitionCrfId;
	}

	public void setEventDefinitionCrfId(int eventDefinitionCrfId) {
		this.eventDefinitionCrfId = eventDefinitionCrfId;
	}

	public int getStudyEventDefinitionId() {
		return studyEventDefinitionId;
	}

	public void setStudyEventDefinitionId(int studyEventDefinitionId) {
		this.studyEventDefinitionId = studyEventDefinitionId;
	}

	public int getStudySubjectId() {
		return studySubjectId;
	}

	public void setStudySubjectId(int studySubjectId) {
		this.studySubjectId = studySubjectId;
	}

	public int getCrfVersionId() {
		return crfVersionId;
	}

	public void setCrfVersionId(int crfVersionId) {
		this.crfVersionId = crfVersionId;
	}

	public String getInterviewerName() {
		return interviewerName;
	}

	public void setInterviewerName(String interviewerName) {
		this.interviewerName = interviewerName;
	}

	public Date getDateInterviewed() {
		return dateInterviewed;
	}

	public void setDateInterviewed(Date dateInterviewed) {
		this.dateInterviewed = dateInterviewed;
	}

	public Timestamp getDateStart() {
		return dateStart;
	}

	public void setDateStart(Timestamp dateStart) {
		this.dateStart = dateStart;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getCrfName() {
		return crfName;
	}

	public void setCrfName(String crfName) {
		this.crfName = crfName;
	}

	public String getCrfVersion() {
		return crfVersion;
	}

	public void setCrfVersion(String crfVersion) {
		this.crfVersion = crfVersion;
	}
}
