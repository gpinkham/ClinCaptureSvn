package com.clinovo.rule;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.akaza.openclinica.domain.rule.action.ActionType;
import org.akaza.openclinica.domain.rule.action.RuleActionBean;
import org.akaza.openclinica.domain.rule.action.RuleActionRunBean;

@Entity
@DiscriminatorValue("2")
public class WebServiceAction extends RuleActionBean {

	private String username;
	private String rolename;
	private String password;
	private String studyOID;
	private String studySubjectOID;
	private String randomizationUrl;
	private String authenticationUrl;

	// Run flags
	private boolean runOnBatchMode = true;
	private boolean runOnInitiaDataEntry = true;
	private boolean runOnDoubleDataEntry = true;
	private boolean runOnImportDataEntry = false;
	private boolean runOnAdministrativeDataEntry = true;

	public WebServiceAction() {

		// Set type
		setActionType(ActionType.WEB_SERVICE);

		// Set run actions
		setRuleActionRun(new RuleActionRunBean(runOnAdministrativeDataEntry, runOnInitiaDataEntry,
				runOnDoubleDataEntry, runOnImportDataEntry, runOnBatchMode));
	}

	public String getAuthenticationUrl() {
		return authenticationUrl;
	}

	public void setAuthenticationUrl(String url) {
		this.authenticationUrl = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRolename() {
		return rolename;
	}

	public void setRolename(String rolename) {
		this.rolename = rolename;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getStudyOID() {
		return studyOID;
	}

	public void setStudyOID(String studyOID) {
		this.studyOID = studyOID;
	}

	public String getStudySubjectOID() {
		return studySubjectOID;
	}

	public void setStudySubjectOID(String studySubjectOID) {
		this.studySubjectOID = studySubjectOID;
	}

	public String getRandomizationUrl() {
		return randomizationUrl;
	}

	public void setRandomizationUrl(String randomizationUrl) {
		this.randomizationUrl = randomizationUrl;
	}

}
