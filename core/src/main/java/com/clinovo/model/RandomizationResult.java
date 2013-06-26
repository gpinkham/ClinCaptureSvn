package com.clinovo.model;

/**
 * Encapsulates the Randomization result returned from randomize.net
 *
 */
public class RandomizationResult {
	
	private String studyId;
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

	public void setStudyId(String studyId) {
		
		this.studyId = studyId;
	}

	public String getStudyId() {
		
		return this.studyId;
	}
}