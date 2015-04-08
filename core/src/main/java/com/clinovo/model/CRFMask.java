package com.clinovo.model;

import org.akaza.openclinica.domain.AbstractMutableDomainObject;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * CRF Mask Bean.
 */
@Entity
@Table(name = "crfs_masking")
@GenericGenerator(name = "id-generator", strategy = "native",
		parameters = { @Parameter(name = "sequence", value = "crfs_masking_id_seq") })
public class CRFMask extends AbstractMutableDomainObject {

	private int studyId;
	private int studyEventDefinitionId;
	private int eventDefinitionCrfId;
	private int userId;
	private int studyUserRoleId;
	private int statusId;

	public int getStudyId() {
		return studyId;
	}

	public void setStudyId(int studyId) {
		this.studyId = studyId;
	}

	public int getStudyEventDefinitionId() {
		return studyEventDefinitionId;
	}

	public void setStudyEventDefinitionId(int studyEventDefinitionId) {
		this.studyEventDefinitionId = studyEventDefinitionId;
	}

	public int getEventDefinitionCrfId() {
		return eventDefinitionCrfId;
	}

	public void setEventDefinitionCrfId(int eventDefinitionCrfId) {
		this.eventDefinitionCrfId = eventDefinitionCrfId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getStudyUserRoleId() {
		return studyUserRoleId;
	}

	public void setStudyUserRoleId(int studyUserRoleId) {
		this.studyUserRoleId = studyUserRoleId;
	}

	public int getStatusId() {
		return statusId;
	}

	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}
}
