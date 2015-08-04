package com.clinovo.pages.beans;

import java.util.Map;

/**
 * Created by Igor.
 */
public class User {
	public static final User SYSTEM_ADMIN = new User("root", "root@123", UserRole.SYSTEM_ADMIN);
	public static final User STUDY_ADMIN = new User("demo_admin", "demo_admin@123", UserRole.STUDY_ADMIN);
	public static final User STUDY_MONITOR = new User("demo_mon", "demo_mon@123", UserRole.STUDY_MONITOR);
	public static final User STUDY_CODER = new User("demo_coder", "demo_coder@123", UserRole.STUDY_CODER);
	public static final User STUDY_EVALUATOR = new User("demo_eval", "demo_eval@123", UserRole.STUDY_EVALUATOR);
	public static final User CRC = new User("demo_crc", "demo_crc@123", UserRole.CRC);
	public static final User PI = new User("demo_pi", "demo_pi@123", UserRole.PI);
	public static final User SITE_MONITOR = new User("demo_site_mon", "demo_site_mon@123", UserRole.SITE_MONITOR);
	public static final User STUDY_LEVEL_USER = new User("demo_study_user", "demo_study_user@123",
			UserRole.STUDY_LEVEL_USER);
	public static final User USER = new User("demo_user", "demo_user@123", UserRole.USER);
	public static final String SAVED_USER_NAME = "saved_user_name";
	public static final String SAVED_USER_PASS = "saved_user_pass";
	public static final String CURRENT_USER = "current_user";
	public static final String SAVED_USER_OLD_PASS = "saved_user_old_pass";
	public static final String NEW_CREATED_USER = "new_created_user";

	static {
		SYSTEM_ADMIN.setOldPassword("12345678");
		SYSTEM_ADMIN.setChallengeQuestionIndex(1);
		SYSTEM_ADMIN.setChallengeAnswer("A");

		STUDY_ADMIN.setChallengeQuestionIndex(1);
		STUDY_ADMIN.setChallengeAnswer("A");
		STUDY_ADMIN.setFirstName("Jon");
		STUDY_ADMIN.setLastName("StudyAdmin");
		STUDY_ADMIN.setEmail("study_admin@google.com");
		STUDY_ADMIN.setPhone("+375295002010");
		STUDY_ADMIN.setInstitutionalAffiliation("A");
		STUDY_ADMIN.setActiveStudyIndex(1);
		STUDY_ADMIN.setRoleValue(Integer.toString(UserRole.STUDY_ADMIN.getUserRoleCCId()));
		STUDY_ADMIN.setUserTypeValue("1");
		STUDY_ADMIN.setAuthorizeSOAP("false");
		STUDY_ADMIN.setShowUserPasswordValue("true");

		STUDY_MONITOR.setChallengeQuestionIndex(1);
		STUDY_MONITOR.setChallengeAnswer("B");
		STUDY_MONITOR.setFirstName("Bob");
		STUDY_MONITOR.setLastName("StudyMon");
		STUDY_MONITOR.setEmail("study_monitor@google.com");
		STUDY_MONITOR.setPhone("+375295002020");
		STUDY_MONITOR.setInstitutionalAffiliation("B");
		STUDY_MONITOR.setActiveStudyIndex(1);
		STUDY_MONITOR.setRoleValue(Integer.toString(UserRole.STUDY_MONITOR.getUserRoleCCId()));
		STUDY_MONITOR.setUserTypeValue("1");
		STUDY_MONITOR.setAuthorizeSOAP("false");
		STUDY_MONITOR.setShowUserPasswordValue("true");

		PI.setChallengeQuestionIndex(1);
		PI.setChallengeAnswer("A");
		PI.setFirstName("Jake");
		PI.setLastName("PrimaryInvest");
		PI.setEmail("prim_investigator@google.com");
		PI.setPhone("+375295002030");
		PI.setInstitutionalAffiliation("C");
		PI.setActiveStudyIndex(2);
		PI.setRoleValue(Integer.toString(UserRole.PI.getUserRoleCCId()));
		PI.setUserTypeValue("2");
		PI.setAuthorizeSOAP("false");
		PI.setShowUserPasswordValue("true");

		CRC.setChallengeQuestionIndex(1);
		CRC.setChallengeAnswer("A");
		CRC.setFirstName("Denis");
		CRC.setLastName("ClinicalResearcher");
		CRC.setEmail("clin_research@google.com");
		CRC.setPhone("+375295002040");
		CRC.setInstitutionalAffiliation("D");
		CRC.setActiveStudyIndex(2);
		CRC.setRoleValue(Integer.toString(UserRole.CRC.getUserRoleCCId()));
		CRC.setUserTypeValue("2");
		CRC.setAuthorizeSOAP("false");
		CRC.setShowUserPasswordValue("true");

		STUDY_CODER.setChallengeQuestionIndex(1);
		STUDY_CODER.setChallengeAnswer("C");
		STUDY_CODER.setFirstName("Rob");
		STUDY_CODER.setLastName("StudyCoder");
		STUDY_CODER.setEmail("study_coder@google.com");
		STUDY_CODER.setPhone("+375295002050");
		STUDY_CODER.setInstitutionalAffiliation("B");
		STUDY_CODER.setActiveStudyIndex(1);
		STUDY_CODER.setRoleValue(Integer.toString(UserRole.STUDY_CODER.getUserRoleCCId()));
		STUDY_CODER.setUserTypeValue("1");
		STUDY_CODER.setAuthorizeSOAP("false");
		STUDY_CODER.setShowUserPasswordValue("true");

		STUDY_EVALUATOR.setChallengeQuestionIndex(1);
		STUDY_EVALUATOR.setChallengeAnswer("B");
		STUDY_EVALUATOR.setFirstName("Nob");
		STUDY_EVALUATOR.setLastName("StudyMon");
		STUDY_EVALUATOR.setEmail("study_evaluator@google.com");
		STUDY_EVALUATOR.setPhone("+375295002020");
		STUDY_EVALUATOR.setInstitutionalAffiliation("B");
		STUDY_EVALUATOR.setActiveStudyIndex(1);
		STUDY_EVALUATOR.setRoleValue(Integer.toString(UserRole.STUDY_EVALUATOR.getUserRoleCCId()));
		STUDY_EVALUATOR.setUserTypeValue("1");
		STUDY_EVALUATOR.setAuthorizeSOAP("false");
		STUDY_EVALUATOR.setShowUserPasswordValue("true");

		SITE_MONITOR.setChallengeQuestionIndex(1);
		SITE_MONITOR.setChallengeAnswer("A");
		SITE_MONITOR.setFirstName("Alex");
		SITE_MONITOR.setLastName("SiteMonitor");
		SITE_MONITOR.setEmail("site_monitor@google.com");
		SITE_MONITOR.setPhone("+375295002050");
		SITE_MONITOR.setInstitutionalAffiliation("E");
		SITE_MONITOR.setActiveStudyIndex(2);
		SITE_MONITOR.setRoleValue(Integer.toString(UserRole.SITE_MONITOR.getUserRoleCCId()));
		SITE_MONITOR.setUserTypeValue("2");
		SITE_MONITOR.setAuthorizeSOAP("false");
		SITE_MONITOR.setShowUserPasswordValue("true");
	}

	private String userName;
	private String password;
	private String oldPassword;
	private UserRole userRole;

	private String firstName;
	private String lastName;
	private String email;
	private String phone;
	private String institutionalAffiliation;

	private int activeStudyIndex;
	private String activeStudyName;

	private String roleName;
	private String roleValue;

	private String userTypeValue;
	private String userTypeName;

	private String authorizeSOAP;
	private String showUserPasswordValue;

	private int challengeQuestionIndex;
	private String challengeQuestion;

	private String challengeAnswer;

	public User(String userName, String password, UserRole userRole) {
		this.setUserName(userName);
		this.setPassword(password);
		this.oldPassword = "";
		this.setUserRole(userRole);
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String username) {
		this.userName = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public UserRole getUserRole() {
		return userRole;
	}

	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getInstitutionalAffiliation() {
		return institutionalAffiliation;
	}

	public void setInstitutionalAffiliation(String institutionalAffiliation) {
		this.institutionalAffiliation = institutionalAffiliation;
	}

	public int getActiveStudyIndex() {
		return activeStudyIndex;
	}

	public void setActiveStudyIndex(int activeStudyIndex) {
		this.activeStudyIndex = activeStudyIndex;
	}

	public String getRoleValue() {
		return roleValue;
	}

	public void setRoleValue(String roleValue) {
		this.roleValue = roleValue;
	}

	public String getUserTypeValue() {
		return userTypeValue;
	}

	public void setUserTypeValue(String userTypeValue) {
		this.userTypeValue = userTypeValue;
	}

	public String getAuthorizeSOAP() {
		return authorizeSOAP;
	}

	public void setAuthorizeSOAP(String authorizeSOAP) {
		this.authorizeSOAP = authorizeSOAP;
	}

	public String getShowUserPasswordValue() {
		return showUserPasswordValue;
	}

	public void setShowUserPasswordValue(String showUserPasswordValue) {
		this.showUserPasswordValue = showUserPasswordValue;
	}

	public int getChallengeQuestionIndex() {
		return challengeQuestionIndex;
	}

	public void setChallengeQuestionIndex(int challengeQuestionIndex) {
		this.challengeQuestionIndex = challengeQuestionIndex;
	}

	public String getChallengeAnswer() {
		return challengeAnswer;
	}

	public void setChallengeAnswer(String challengeAnswer) {
		this.challengeAnswer = challengeAnswer;
	}

	public String getActiveStudyName() {
		return activeStudyName;
	}

	public void setActiveStudyName(String activeStudyName) {
		this.activeStudyName = activeStudyName;
	}

	public String getChallengeQuestion() {
		return challengeQuestion;
	}

	public void setChallengeQuestion(String challengeQuestion) {
		this.challengeQuestion = challengeQuestion;
	}

	public static User fillUserFromTableRow(String userRoleName, Map<String, String> row) {
		User user = new User("", "", UserRole.USER);

		if (row.get("User Name") != null) {
			user.setUserName(row.get("User Name"));
		}

		if (row.get("Password") != null) {
			user.setPassword(row.get("Password"));
		}

		if (row.get("Old Password") != null) {
			user.setOldPassword(row.get("Old Password"));
		}

		if (row.get("Challenge Question") != null) {
			user.setChallengeQuestion(row.get("Challenge Question"));
		} else if (row.get("Challenge Question Index") != null) {
			user.setChallengeQuestionIndex(Integer.parseInt(row.get("Challenge Question Index")));
		}

		if (row.get("Challenge Answer") != null) {
			user.setChallengeAnswer(row.get("Challenge Answer"));
		}

		if (row.get("First Name") != null) {
			user.setFirstName(row.get("First Name"));
		}

		if (row.get("Last Name") != null) {
			user.setLastName(row.get("Last Name"));
		}

		if (row.get("Email") != null) {
			user.setEmail(row.get("Email"));
		}

		if (row.get("Phone") != null) {
			user.setPhone(row.get("Phone"));
		}

		if (row.get("Institutional Affiliation") != null) {
			user.setInstitutionalAffiliation(row.get("Institutional Affiliation"));
		}

		if (row.get("Active Study") != null) {
			user.setActiveStudyName(row.get("Active Study"));
		}

		if (row.get("Role") != null) {
			user.setRoleName(row.get("Role"));
			user.setUserRole(UserRole.getRoleByFullRoleName(user.getRoleName()));
			user.setRoleValue(Integer.toString(user.getUserRole().getUserRoleCCId()));
		} else if (row.get("Role Value") != null) {
			user.setRoleValue(row.get("Role Value"));
			user.setUserRole(UserRole.getRoleByCCId(Integer.parseInt(user.getRoleValue())));
		}

		if (row.get("User Type") != null) {
			user.setUserTypeName(row.get("User Type"));
		} else if (row.get("User Type Value") != null) {
			user.setUserTypeValue(row.get("User Type Value"));
		}

		if (row.get("Authorize SOAP") != null) {
			user.setAuthorizeSOAP(row.get("Authorize SOAP"));
		}

		if (row.get("Show User Password") != null) {
			user.setShowUserPasswordValue(row.get("Show User Password"));
		}

		if (user.getUserRole() == UserRole.USER) {
			user.setUserRole(defineDefaultUser(userRoleName).getUserRole());
		}

		return user;
	}

	public String getUserTypeName() {
		return userTypeName;
	}

	public void setUserTypeName(String userTypeName) {
		this.userTypeName = userTypeName;
	}

	public static User defineDefaultUser(String user) {
		User currentUser;

		if (user.startsWith(UserRole.SYSTEM_ADMIN.getUserRoleName())) {
			currentUser = User.SYSTEM_ADMIN;
		} else if (user.startsWith(UserRole.STUDY_ADMIN.getUserRoleName())) {
			currentUser = User.STUDY_ADMIN;
		} else if (user.startsWith(UserRole.STUDY_MONITOR.getUserRoleName())) {
			currentUser = User.STUDY_MONITOR;
		} else if (user.startsWith(UserRole.STUDY_EVALUATOR.getUserRoleName())) {
			currentUser = User.STUDY_EVALUATOR;
		} else if (user.startsWith(UserRole.STUDY_CODER.getUserRoleName())) {
			currentUser = User.STUDY_CODER;
		} else if (user.startsWith(UserRole.PI.getUserRoleName())) {
			currentUser = User.PI;
		} else if (user.startsWith(UserRole.SITE_MONITOR.getUserRoleName())) {
			currentUser = User.SITE_MONITOR;
		} else if (user.startsWith(UserRole.CRC.getUserRoleName())) {
			currentUser = User.CRC;
		} else if (user.startsWith(UserRole.STUDY_LEVEL_USER.getUserRoleName())) {
			currentUser = User.STUDY_LEVEL_USER;
		} else {
			currentUser = User.USER;
		}

		return currentUser;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
}
