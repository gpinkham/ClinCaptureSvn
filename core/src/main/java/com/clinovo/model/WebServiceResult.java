package com.clinovo.model;


public class WebServiceResult {


	private String patientId;
	private String treatmentID;
	private String randomizationResult;

	public String getTreatment() {
		return treatmentID;
	}

	public void setTreatment(String treatment) {
		this.treatmentID = treatment;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getRandomizationResult() {
		return randomizationResult;
	}

	public void setRandomizationResult(String result) {
		this.randomizationResult = result;
	}

}