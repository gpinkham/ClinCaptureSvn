package com.clinovo.model;

public class WebServiceResult {

	private String group;
	private String message;
	private String treatment;
	private boolean displayTreatment;

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTreatment() {
		return treatment;
	}

	public void setTreatment(String treatment) {
		this.treatment = treatment;
	}

	public boolean isDisplayTreatment() {
		return displayTreatment;
	}

	public void setDisplayTreatment(boolean displayTreatment) {
		this.displayTreatment = displayTreatment;
	}

}