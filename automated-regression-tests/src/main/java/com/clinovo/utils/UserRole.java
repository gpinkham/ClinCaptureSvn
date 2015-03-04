package com.clinovo.utils;


public enum UserRole {
		
	SYSTEM_ADMIN(1, 1, "Root", "System Aministrator"), STUDY_ADMIN(2, 2, "Study Admin", "Study Administrator"), 
	CRC(3, 5, "CRC", "Clinical Research Coordinator"), STUDY_MONITOR(4, 6, "Study Monitor", "Study Monitor"), 
	STUDY_CODER(5, 7, "Study Coder", "Study Coder"), STUDY_EVALUATOR(6, 8, "Study Evaluator", "Study Evaluator"), 
	SITE_MONITOR(7, 9, "Site Monitor", "Site Monitor"), PI(8, 4, "PI", "Primary Investigator"), 
	STUDY_LEVEL_USER(9, 0, "Study Level User", "Study Level User"), USER(0, 0, "a User", "User");
	private int id;
	private int userRoleCCId;
	private String userRoleName;
	private String userFullRoleName;

	
	UserRole(int id, int userRoleCCId, String userRoleName, String fullUserRoleName){
		this.id = id;
		this.userRoleCCId = userRoleCCId;
		this.setUserRoleName(userRoleName);
		this.setUserFullRoleName(fullUserRoleName);
	}
	
	public static UserRole getRoleById(int type) {
		for (UserRole x : UserRole.values()) {
			if (x.getId() == type)
				return x;
		}
		return USER;
	}
	
	public static UserRole getRoleByCCId(int ccId) {
		for (UserRole x : UserRole.values()) {
			if (x.getUserRoleCCId() == ccId)
				return x;
		}
		return USER;
	}

	public static UserRole getRoleByRoleName(String userRoleName) {
		for (UserRole x : UserRole.values()) {
			if (x.getUserRoleName().equals(userRoleName))
				return x;
		}
		return USER;
	}
	
	public static UserRole getRoleByFullRoleName(String userFullRoleName) {
		for (UserRole x : UserRole.values()) {
			if (x.getUserFullRoleName().equals(userFullRoleName))
				return x;
		}
		return USER;
	}
	
	public int getId() {
			return id;
	}

	public String getUserRoleName() {
		return userRoleName;
	}

	public void setUserRoleName(String userRoleName) {
		this.userRoleName = userRoleName;
	}

	public String getUserFullRoleName() {
		return userFullRoleName;
	}

	public void setUserFullRoleName(String userFullRoleName) {
		this.userFullRoleName = userFullRoleName;
	}

	public int getUserRoleCCId() {
		return userRoleCCId;
	}

	public void setUserRoleCCId(int userRoleCCId) {
		this.userRoleCCId = userRoleCCId;
	}
}
