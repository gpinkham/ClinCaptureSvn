package com.clinovo.model;

import org.akaza.openclinica.domain.AbstractMutableDomainObject;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Entity that was created in order to store randomization audit log.
 */
@Entity
@Table(name = "audit_log_randomization")
@GenericGenerator(name = "id-generator", strategy = "native",
		parameters = { @Parameter(name = "sequence_name", value = "audit_log_randomization_id_seq") })
public class AuditLogRandomization extends AbstractMutableDomainObject {

	private Date auditDate;
	private int studyId;
	private int studySubjectId;
	private int eventCrfId;
	private int userId;
	private String siteName;
	private String authenticationUrl;
	private String randomizationUrl;
	private String trialId;
	private String strataVariables;
	private String response;
	private String userName;
	private int success;

	public String getAuthenticationUrl() {
		return authenticationUrl;
	}

	public void setAuthenticationUrl(String authenticationUrl) {
		this.authenticationUrl = authenticationUrl;
	}

	public int getEventCrfId() {
		return eventCrfId;
	}

	public void setEventCrfId(int eventCrfId) {
		this.eventCrfId = eventCrfId;
	}

	public String getRandomizationUrl() {
		return randomizationUrl;
	}

	public void setRandomizationUrl(String randomizationUrl) {
		this.randomizationUrl = randomizationUrl;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getStrataVariables() {
		return strataVariables;
	}

	public void setStrataVariables(String strataVariables) {
		this.strataVariables = strataVariables;
	}

	public int getStudyId() {
		return studyId;
	}

	public void setStudyId(int studyId) {
		this.studyId = studyId;
	}

	public int getStudySubjectId() {
		return studySubjectId;
	}

	public void setStudySubjectId(int studySubjectId) {
		this.studySubjectId = studySubjectId;
	}

	public String getTrialId() {
		return trialId;
	}

	public void setTrialId(String trialId) {
		this.trialId = trialId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public Date getAuditDate() {
		return auditDate;
	}

	public void setAuditDate(Date auditDate) {
		this.auditDate = auditDate;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getSuccess() {
		return success;
	}

	public void setSuccess(int success) {
		this.success = success;
	}
}
