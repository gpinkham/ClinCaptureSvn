package com.clinovo.pages.beans;

import java.util.HashMap;
import java.util.Map;

public class DNote {
    
    public static final Object DNS_TO_CHECK_EXIST = "dns_to_check_exist";

    private String id = "";
    
	private String description = "";
    
    private String detailedNote = "";
	
	private String type = "";
    
    private String assignToUser = "";
	
	private String emailAssignedUser = "";
    
    private String studySubjectID = "";
    
    private String eventName = "";
	
	private String crfName = "";
    
    private String item = "";
    
    private String entityName = "";
    
    //for updating/closing
	
	private String resolutionStatus = "";
	
	private String entityType = "";
	
	private DNote parentDN;

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
			dn.setType(grtFullTypeName(row.get("Type")));
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
    	
    	if (row.get("Entity Name") != null) {
    		dn.setEntityName(row.get("Entity Name"));
    	}
    	
    	//for updating/closing
    	
    	if (row.get("Study Subject ID") != null) {
			dn.getParentDN().setStudySubjectID(row.get("Study Subject ID"));
    	}
    	
    	if (row.get("Event Name") != null) {
    		dn.getParentDN().setEventName(row.get("Event Name"));
    	}
    	
    	if (row.get("CRF Name") != null) {
    		dn.getParentDN().setCRFName(row.get("CRF Name"));
    	}
    	
    	if (row.get("Parent ID") != null) {
			dn.getParentDN().setId(row.get("Parent ID"));
    	}
    	
    	if (row.get("Parent Description") != null) {
    		dn.getParentDN().setDescription(row.get("Parent Description"));
    	}
    	
    	if (row.get("Parent Detailed Note") != null) {
    		dn.getParentDN().setDetailedNote(row.get("Parent Detailed Note"));
    	}
    	
    	if (row.get("Resolution Status") != null) {
    		dn.setResolutionStatus(row.get("Resolution Status"));
    	}
    	
		return dn;
	}
	
	private static String grtFullTypeName(String type) {
		switch (type){
		case "RFC":
			return "Reason for Change";
		case "FVC":
			return "Failed Validation Check";
		case "Annotation":
			return "Annotation";
		case "Query":
			return "Query";
		default: 
			return "Annotation";
		}
	}

	public static String getValueByDNType(String type) {
		switch (type){
		case "Annotation":
			return "2";
		case "Query":
			return "3";
		default: 
			return "2";
		}
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
		
		if (!dn.getParentDN().getDescription().isEmpty()) {
			map.put("Parent Description", dn.getParentDN().getDescription());
		}
    	
		if (!dn.getResolutionStatus().isEmpty()) {
			map.put("Resolution Status", dn.getResolutionStatus());
		}
		
		if (!dn.getEntityName().isEmpty()) {
			map.put("Entity Name", dn.getEntityName());
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

	public String getResolutionStatus() {
		return resolutionStatus;
	}

	public void setResolutionStatus(String resolutionStatus) {
		this.resolutionStatus = resolutionStatus;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public DNote getParentDN() {
		if (this.parentDN == null) {
			 this.parentDN = new DNote();
		} 
		return this.parentDN;
	}

	public void setParentDN(DNote parentDN) {
		this.parentDN = parentDN;
	}
	
	public boolean hasParentDN(DNote parentDN) {
		return getParentDN().getDescription().isEmpty() && getParentDN().getId().isEmpty() && 
				getParentDN().getDetailedNote().isEmpty() && getParentDN().getResolutionStatus().isEmpty();
	}
}

