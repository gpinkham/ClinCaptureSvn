package com.clinovo.pages.beans;

import java.util.HashMap;
import java.util.Map;

public class DNote {
    
    public static final Object DNS_TO_CHECK_EXIST = "dns_to_check_exist";

	private String description = "";
    
    private String detailedNote = "";
	
	private String type = "";
    
    private String assignToUser = "";
	
	private String emailAssignedUser = "";
    
    private String studySubjectID = "";
    
    private String eventName = "";
	
	private String crfName = "";
    
    private String item = "";

    public static DNote fillDNoteFromTableRow(
			Map<String, String> row) {
		
    	DNote dn = new DNote();
		
		if (row.get("Description") != null) {
			dn.setDescription(row.get("Description"));
    	}
		
    	if (row.get("Detailed Note") != null) {
    		dn.setDetailedNote(row.get("Detailed Note"));
    	}
    	
    	if (row.get("Type") != null) {
			dn.setType(row.get("Type"));
    	}
		
    	if (row.get("Assign to User") != null) {
			dn.setAssignToUser(row.get("Assign to User"));
    	}
    	
    	if (row.get("Email Assigned User") != null) {
    		dn.setEmailAssignedUser(row.get("Email Assigned User"));
    	}
    	
    	if (row.get("Study Subject ID") != null) {
			dn.setStudySubjectID(row.get("Study Subject ID"));
    	}
    	
    	if (row.get("Event Name") != null) {
    		dn.setEventName(row.get("Event Name"));
    	}
    	
    	if (row.get("CRF Name") != null) {
    		dn.setCRFName(row.get("CRF Name"));
    	}
    	
    	if (row.get("Item") != null) {
    		dn.setItem(row.get("Item"));
    	}
    	
		return dn;
	}
	
	public static Map<String, String> getMapWithFields(DNote dn) {
		Map<String, String> map = new HashMap<String, String>();	
		
		if (!dn.getDescription().isEmpty()) {
			map.put("Description", dn.getDescription());
    	}
		
		if (!dn.getDetailedNote().isEmpty()) {
			map.put("Detailed Note", dn.getDetailedNote());
    	}
		
		if (!dn.getType().isEmpty()) {
			map.put("Type", dn.getType());
    	}
		
		if (!dn.getAssignToUser().isEmpty()) {
			map.put("Assign To User", dn.getAssignToUser());
    	}
		
		if (!dn.getStudySubjectID().isEmpty()) {
			map.put("Study Subject ID", dn.getStudySubjectID());
    	}
		
		if (!dn.getEventName().isEmpty()) {
			map.put("Event Name", dn.getEventName());
    	}
		
		if (!dn.getCRFName().isEmpty()) {
			map.put("CRF Name", dn.getCRFName());
    	}
		
		if (!dn.getAssignToUser().isEmpty()) {
			map.put("Assign To User", dn.getAssignToUser());
		}
    	
    	return map;
	}

	public String getStudySubjectID() {
		return studySubjectID;
	}

	public void setStudySubjectID(String studySubjectID) {
		this.studySubjectID = studySubjectID;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDetailedNote() {
		return detailedNote;
	}

	public void setDetailedNote(String detailedNote) {
		this.detailedNote = detailedNote;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAssignToUser() {
		return assignToUser;
	}

	public void setAssignToUser(String assignToUser) {
		this.assignToUser = assignToUser;
	}

	public String getEmailAssignedUser() {
		return emailAssignedUser;
	}

	public void setEmailAssignedUser(String emailAssignedUser) {
		this.emailAssignedUser = emailAssignedUser;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getCRFName() {
		return crfName;
	}

	public void setCRFName(String crfName) {
		this.crfName = crfName;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}
	
	public boolean isQuery() {
		return this.getType().trim().equals("Query");
	}
}

