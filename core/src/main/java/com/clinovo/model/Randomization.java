package com.clinovo.model;

/**
 * Encapsulates the content and data that will be used to validate a given subject.
 * 
 * <p>
 * The content in here form part of the contex that is sent over to randomize.net
 *
 */
public class Randomization {

	// Post data
	private String siteId;
	private String trialId;
	private String initials;
	private String patientId;
	
	// Auth details
	private String username;
	private String password;
	
	// urls
	private String randomizationUrl;
	private String authenticationUrl;

	private String level;

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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSiteId() {
		return siteId;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getRandomizationUrl() {
		return randomizationUrl;
	}

	public void setRandomizationUrl(String randomizationUrl) {
		this.randomizationUrl = randomizationUrl;
	}

	public String getInitials() {
		return initials;
	}

	public void setInitials(String initials) {
		this.initials = initials;
	}

	public String getTrialId() {
		return trialId;
	}

	public void setTrialId(String trialId) {
		this.trialId = trialId;
	}

	public void setStratificationLevel(String level) {
		this.level = level;
	}

	public String getStratificationLevel() {
		return this.level;
	}

}
