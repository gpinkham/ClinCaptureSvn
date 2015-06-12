package com.clinovo.pages.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StudyEventDefinition {
	
	public static final String NEW_CREATED_EVENT = "new_created_event";

	public static final String EVENT_TO_SCHEDULE = "event_to_schedule";

	public static final String EVENTS_TO_SCHEDULE = "events_to_schedule";

	public static final String EVENTS_TO_CHECK_SIGN_STATUS = "events_to_check_sign_status";

	private String name = "";
    
    private String description = "";
    
    private String category = "";
	
	private String daySchedule = "";
    
    private String dayMax = "";
	
	private String dayMin = "";
    
    private String dayEmail = "";
    
    private String userName = "";
	
	private String referenceEvent = "";
    
    private String type = "";
    
    private String repeating = "";
    
    private List<String> eCRFs = new ArrayList<String>();
    
    private String startDateTime = "";
    
    private String endDateTime = "";
    
    private String studySubjectID = "";
    
    private String eventOID = "";

    public static StudyEventDefinition fillStudyEventDefinitionFromTableRow(
			Map<String, String> row) {
		
    	StudyEventDefinition event = new StudyEventDefinition();
		
		if (row.get("Name") != null) {
			event.setName(row.get("Name"));
    	}
		
    	if (row.get("Description") != null) {
    		event.setDescription(row.get("Description"));
    	}
    	
    	if (row.get("Type") != null) {
			event.setType(row.get("Type"));
    	}
		
    	if (row.get("Category") != null) {
			event.setCategory(row.get("Category"));
    	}
    	
    	if (row.get("Repeating") != null) {
    		event.setRepeating(row.get("Repeating"));
    	}
    	
    	if (row.get("Reference Event") != null) {
			event.setReferenceEvent(row.get("Reference Event"));
    	}
		
    	if (row.get("Day Schedule") != null) {
    		event.setDaySchedule(row.get("Day Schedule"));
    	}
    	
    	if (row.get("Day Max") != null) {
			event.setDayMax(row.get("Day Max"));
    	}
		
    	if (row.get("Day Min") != null) {
			event.setDayMin(row.get("Day Min"));
    	}
    	
    	if (row.get("Day Email") != null) {
    		event.setDayEmail(row.get("Day Email"));
    	}
    	
    	if (row.get("User Name") != null) {
    		event.setUserName(row.get("User Name"));
    	}
    	
    	if (row.get("CRFs") != null) {
    		event.setCRFList(generateCRFList(row.get("CRFs")));
    	}
    	
    	if (row.get("Start Date/Time") != null) {
    		event.setStartDateTime(row.get("Start Date/Time"));
    	}
    	
    	if (row.get("End Date/Time") != null) {
    		event.setEndDateTime(row.get("End Date/Time"));
    	}
    	
    	
    	if (row.get("Event Name") != null) {
			event.setName(row.get("Event Name"));
    	}
    	
    	if (row.get("Study Subject ID") != null) {
			event.setStudySubjectID(row.get("Study Subject ID"));
    	}
    	
    	if (row.get("Event OID") != null) {
			event.setEventOID(row.get("Event OID"));
    	}
    	
		return event;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDaySchedule() {
		return daySchedule;
	}

	public void setDaySchedule(String daySchedule) {
		this.daySchedule = daySchedule;
	}

	public String getDayMax() {
		return dayMax;
	}

	public void setDayMax(String dayMax) {
		this.dayMax = dayMax;
	}

	public String getDayMin() {
		return dayMin;
	}

	public void setDayMin(String dayMin) {
		this.dayMin = dayMin;
	}

	public String getDayEmail() {
		return dayEmail;
	}

	public void setDayEmail(String dayEmail) {
		this.dayEmail = dayEmail;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getReferenceEvent() {
		return referenceEvent;
	}

	public void setReferenceEvent(String referenceEvent) {
		this.referenceEvent = referenceEvent;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRepeating() {
		return repeating;
	}

	public void setRepeating(String repeating) {
		this.repeating = repeating;
	}

	public List<String> getCRFList() {
		return eCRFs;
	}

	public void setCRFList(List<String> eCRFs) {
		this.eCRFs = eCRFs;
	}
	
	public static String convertTypeNameToTypeValue(String typeName) {
		switch (typeName){
		case "Scheduled": 
			return "scheduled";
		case "Unscheduled": 
			return "unscheduled";
		case "Common": 
			return "common";
		case "Calendared": 
			return "calendared_visit";
		
		default: 
			return "";
		}		
	}
	
	public static List<String> generateCRFList(String eCRFs) {
		List<String> result = new ArrayList<String>();
		for (String crf: eCRFs.split(", ")){
			result.add(crf);
		}
		
		return result;
	}

	public String getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(String startDateTime) {
		this.startDateTime = startDateTime;
	}

	public String getEndDateTime() {
		return endDateTime;
	}

	public void setEndDateTime(String endDateTime) {
		this.endDateTime = endDateTime;
	}

	public String getStudySubjectID() {
		return studySubjectID;
	}

	public void setStudySubjectID(String studySubjectID) {
		this.studySubjectID = studySubjectID;
	}

	public String getEventOID() {
		return eventOID;
	}

	public void setEventOID(String eventOID) {
		this.eventOID = eventOID;
	}
}
