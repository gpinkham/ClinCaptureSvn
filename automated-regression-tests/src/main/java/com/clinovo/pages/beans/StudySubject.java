package com.clinovo.pages.beans;

import java.util.Map;

import com.clinovo.pages.AddSubjectPage;
import com.clinovo.pages.AdministerUsersPage;
import com.clinovo.pages.BuildStudyPage;
import com.clinovo.pages.ChangeStudyPage;
import com.clinovo.pages.ConfigureSystemPropertiesPage;
import com.clinovo.pages.UpdateStudyDetailsPage;

@SuppressWarnings("unused")
public class StudySubject {
	
	private String studySubjectID = "";
    
    private String personID = "";
    
    private String secondaryID = "";
	
	private String dateOfEnrollmentForStudy = "";
    
    private String gender = "";
	
	private String dateOfBirth = "";
    
    private String dynamicGroupName = "";

    public static StudySubject fillStudySubjectFromTableRow(
			Map<String, String> row) {
		
    	StudySubject ssubj = new StudySubject();
		
		if (row.get("Study Subject ID") != null) {
			ssubj.setStudySubjectID(row.get("Study Subject ID"));
    	}
		
    	if (row.get("Person ID") != null) {
    		ssubj.setPersonID(row.get("Person ID"));
    	}
    	
    	if (row.get("Secondary ID") != null) {
			ssubj.setSecondaryID(row.get("Secondary ID"));
    	}
		
    	if (row.get("Date of Enrollment for Study") != null) {
			ssubj.setDateOfEnrollmentForStudy(row.get("Date of Enrollment for Study"));
    	}
    	
    	if (row.get("Gender") != null) {
    		ssubj.setGender(row.get("Gender"));
    	}
    	
    	if (row.get("Date of Birth") != null) {
			ssubj.setDateOfBirth(row.get("Date of Birth"));
    	}
		
    	if (row.get("Dynamic Group") != null) {
    		ssubj.setDynamicGroupName(row.get("Dynamic Group"));
    	}
    	
		return ssubj;
	}

	public String getStudySubjectID() {
		return studySubjectID;
	}

	public void setStudySubjectID(String studySubjectID) {
		this.studySubjectID = studySubjectID;
	}

	public String getPersonID() {
		return personID;
	}

	public void setPersonID(String personID) {
		this.personID = personID;
	}

	public String getSecondaryID() {
		return secondaryID;
	}

	public void setSecondaryID(String secondaryID) {
		this.secondaryID = secondaryID;
	}

	public String getDateOfEnrollmentForStudy() {
		return dateOfEnrollmentForStudy;
	}

	public void setDateOfEnrollmentForStudy(String dateOfEnrollmentForStudy) {
		this.dateOfEnrollmentForStudy = dateOfEnrollmentForStudy;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getDynamicGroupName() {
		return dynamicGroupName;
	}

	public void setDynamicGroupName(String dynamicGroupName) {
		this.dynamicGroupName = dynamicGroupName;
	}

	public static String convertGenderNameToValue(String gender) {
		switch (gender) {
		
		case "Male": 
			return "m";
		case "Female": 
			return "f";
		default: 
			return "f";
		}		
	}
}
