/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License 
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.login;

import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author thickerson
 */
public class UserAccountBean extends AuditableEntityBean {

	private static final long serialVersionUID = -7373737639499260727L;

	public static final String ROOT = "root";

	private String passwd;
	private String firstName;
	private String lastName;
	private String email;
	private String institutionalAffiliation;
	private Date lastVisitDate;
	private Date passwdTimestamp;
	private String passwdChallengeQuestion;
	private String passwdChallengeAnswer;
	private String phone;
	private Boolean enabled;
	private Boolean accountNonLocked;
	private Integer lockCounter;
	private Boolean runWebservices;
	public String pentahoUserSession;
	public Date pentahoTokenDate;

	/**
	 * Counts the number of times the user visited Main Menu servlet.
	 */
	private int numVisitsToMainMenu;

	private int activeStudyId;
	// private Study activeStudy;

	//
	// the following invariant is maintained at all times:
	// there is at most one object in userTypes
	// all elements of userTypes are UserType objects
	// userTypes has a UserType.SYS_ADMIN object IFF sysAdmin == true
	//
	// we store the userType as an ArrayList for forward compatibility
	// ie it may be possible for a user to have multiple usertypes in the future
	// we maintain the sysAdmin flag to speed up isSysAdmin queries
	//
	private boolean sysAdmin; // this is true if the user is the business
	// dmin, false otherwise
	private boolean techAdmin;
	private final ArrayList<UserType> userTypes;

	//
	// the following invariant is maintained at all times:
	// all elements of roles are StudyUserRoleBean objects
	// if there is a StudyUserRoleBean object s which is at index i of roles,
	// then rolesByStudy has a key which is an Integer whose intValue is
	// s.getStudyId
	// and the value of that key is an Integer whose intValue is i
	// in other words, rolesByStudy is a hashmap whose keys are studyIds and
	// whose values
	// are indexes of the corresponding StudyUserRoleBean in roles
	//
	// we maintain rolesByStudy to speed up getRoleByStudy queries
	//

	// elements are StudyUserRoleBeans
	private ArrayList<StudyUserRoleBean> roles = new ArrayList<StudyUserRoleBean>();

	// key is Integer whose intValue is a studyId, value is StudyUserRoleBean
	// for that study
	private final HashMap<Integer, Integer> rolesByStudy = new HashMap<Integer, Integer>();

	private String notes; // not in the DB, only for showing some notes for

	// this acocunt on page

	public UserAccountBean() {
		super();

		passwd = "";
		firstName = "";
		lastName = "";
		email = "";
		institutionalAffiliation = "";
		lastVisitDate = new Date(0);
		passwdTimestamp = new Date(0);
		passwdChallengeQuestion = "";
		passwdChallengeAnswer = "";
		phone = "";
		sysAdmin = false;
		techAdmin = false;

		userTypes = new ArrayList<UserType>();
		status = Status.AVAILABLE;
		numVisitsToMainMenu = 0;
		notes = "";
		enabled = true;
		accountNonLocked = true;
		lockCounter = 0;
		runWebservices = false;

		pentahoTokenDate = new Date(0);
	}

	/**
	 * @return Returns the email.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            The email to set.
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return Returns the firstName.
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName
	 *            The firstName to set.
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return Returns the institutionalAffiliation.
	 */
	public String getInstitutionalAffiliation() {
		return institutionalAffiliation;
	}

	/**
	 * @param institutionalAffiliation
	 *            The institutionalAffiliation to set.
	 */
	public void setInstitutionalAffiliation(String institutionalAffiliation) {
		this.institutionalAffiliation = institutionalAffiliation;
	}

	/**
	 * @return Returns the lastName.
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName
	 *            The lastName to set.
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return Returns the lastVisitDate.
	 */
	public Date getLastVisitDate() {
		return lastVisitDate;
	}

	/**
	 * @param lastVisitDate
	 *            The lastVisitDate to set.
	 */
	public void setLastVisitDate(Date lastVisitDate) {
		this.lastVisitDate = lastVisitDate;
	}

	/**
	 * @return Returns the passwd.
	 */
	public String getPasswd() {
		return passwd;
	}

	/**
	 * @param passwd
	 *            The passwd to set.
	 */
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	/**
	 * @return Returns the passwdChallengeAnswer.
	 */
	public String getPasswdChallengeAnswer() {
		return passwdChallengeAnswer;
	}

	/**
	 * @param passwdChallengeAnswer
	 *            The passwdChallengeAnswer to set.
	 */
	public void setPasswdChallengeAnswer(String passwdChallengeAnswer) {
		this.passwdChallengeAnswer = passwdChallengeAnswer;
	}

	/**
	 * @return Returns the passwdChallengeQuestion.
	 */
	public String getPasswdChallengeQuestion() {
		return passwdChallengeQuestion;
	}

	/**
	 * @param passwdChallengeQuestion
	 *            The passwdChallengeQuestion to set.
	 */
	public void setPasswdChallengeQuestion(String passwdChallengeQuestion) {
		this.passwdChallengeQuestion = passwdChallengeQuestion;
	}

	/**
	 * @return Returns the passwdTimestamp.
	 */
	public Date getPasswdTimestamp() {
		return passwdTimestamp;
	}

	/**
	 * @param passwdTimestamp
	 *            The passwdTimestamp to set.
	 */
	public void setPasswdTimestamp(Date passwdTimestamp) {
		this.passwdTimestamp = passwdTimestamp;
	}

	/**
	 * @return Returns the phone.
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone
	 *            The phone to set.
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Boolean getAccountNonLocked() {
		return accountNonLocked;
	}

	public void setAccountNonLocked(Boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	public Integer getLockCounter() {
		return lockCounter;
	}

	public void setLockCounter(Integer lockCounter) {
		this.lockCounter = lockCounter;
	}

	// ///////////////////////
	// SECURITY MODEL CODE //
	// ///////////////////////

	public void addUserType(UserType u) {
		// in effect userTypes is just a single UserType object
		// we do things this way for forward-compatibility,
		// i.e. it may be possible for users to have multiple UserTypes in the
		// future
		if (userTypes.size() > 0) {
			userTypes.clear();
		}

		userTypes.add(u);

		if (u.equals(UserType.USER)) {
			sysAdmin = false;
			techAdmin = false;
		}

		if (u.equals(UserType.SYSADMIN)) {
			sysAdmin = true;
		}

		/*
		 * currently set tech admin superior to sys admin, i.e anything a sysadmin can do, a tech admin can do too.
		 */
		if (u.equals(UserType.TECHADMIN)) {
			sysAdmin = true;
			// need to remove this to avoid problems creating and updating
			// users, tbh
			techAdmin = true;
		}
	}

	/**
	 * @return Returns the sysAdmin.
	 */
	public boolean isSysAdmin() {
		return sysAdmin;
	}

	/**
	 * @return Returns the techAdmin flag, for technical administrators.
	 */
	public boolean isTechAdmin() {
		return techAdmin;
	}

	public boolean hasUserType(UserType u) {
		Iterator<UserType> userTypesIt = userTypes.iterator();

		while (userTypesIt.hasNext()) {
			UserType myType = (UserType) userTypesIt.next();
			if (myType.equals(u)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @return Returns the activeStudyId.
	 */
	public int getActiveStudyId() {
		return activeStudyId;
	}

	/**
	 * @param activeStudyId
	 *            The activeStudyId to set.
	 */
	public void setActiveStudyId(int activeStudyId) {
		this.activeStudyId = activeStudyId;
	}

	public void addRole(StudyUserRoleBean sur) {
		Integer key = new Integer(sur.getStudyId());
		if (rolesByStudy.containsKey(key)) {
			Integer index = (Integer) rolesByStudy.get(key);
			roles.set(index.intValue(), sur);
		} else {
			roles.add(sur);
			rolesByStudy.put(key, new Integer(roles.size() - 1));
		}
	}

	public StudyUserRoleBean getRoleByStudy(StudyBean study) {
		return getRoleByStudy(study.getId());
	}

	public void updateSysAdminRole(Integer studyId, Integer prevStudyId) {
		if (name.equals(ROOT)) {
			for (StudyUserRoleBean surb : roles) {
				if (surb.getRole() == Role.SYSTEM_ADMINISTRATOR) {
					rolesByStudy.put(studyId, rolesByStudy.remove(prevStudyId));
					break;
				}
			}
		}
	}

	public StudyUserRoleBean getSysAdminRole() {
		StudyUserRoleBean studyUserRoleBean = null;
		for (StudyUserRoleBean surb : roles) {
			if (surb.getRole() == Role.SYSTEM_ADMINISTRATOR) {
				studyUserRoleBean = surb;
				break;
			}
		}
		return studyUserRoleBean;
	}

	public StudyUserRoleBean getRoleByStudy(int studyId) {
		if (name.equals(ROOT)) {
			return getSysAdminRole();
		}

		Integer key = new Integer(studyId);

		if (rolesByStudy.containsKey(key)) {
			Integer index = (Integer) rolesByStudy.get(key);
			StudyUserRoleBean s = (StudyUserRoleBean) roles.get(index.intValue());

			if (s != null && !s.getStatus().equals(Status.DELETED) && !s.getStatus().equals(Status.AUTO_DELETED)) {
				return s;
			}
		}

		return new StudyUserRoleBean();
	}

	public boolean hasRoleInStudy(int studyId) {
		StudyUserRoleBean s = getRoleByStudy(studyId);
		return s.isActive();
	}

	public Role getActiveStudyRole() {
		return getRoleByStudy(activeStudyId).getRole();
	}

	public String getActiveStudyRoleName() {
		return getRoleByStudy(activeStudyId).getRole().getName();
	}

	// public boolean hasPrivilege(Privilege p) {
	// boolean returnMe = false;
	// Iterator it = userPrivileges.iterator();
	// while (it.hasNext()) {
	// Privilege myPriv = (Privilege)it.next();
	// if (myPriv.equals(p)) {
	// returnMe = true;
	// }
	// } // end of iterator
	// return returnMe;
	// }
	//
	// public Privilege getPrivilege(Privilege p) {
	// if (this.hasPrivilege(p)) {
	// return p;
	// }
	// else {
	// return Privilege.get(0);
	// }
	// }

	/**
	 * @return Returns the roles.
	 */
	public ArrayList<StudyUserRoleBean> getRoles() {
		return roles;
	}

	/**
	 * @param roles
	 *            The roles to set.
	 */
	public void setRoles(ArrayList<?> roles) {
		this.roles = new ArrayList<StudyUserRoleBean>();
		rolesByStudy.clear();

		for (int i = 0; i < roles.size(); i++) {
			StudyUserRoleBean sur = (StudyUserRoleBean) roles.get(i);

			this.roles.add(sur);

			Integer key = new Integer(sur.getStudyId());
			Integer value = new Integer(this.roles.size() - 1);
			rolesByStudy.put(key, value);

			if (sur.getRole().equals(Role.SYSTEM_ADMINISTRATOR)) {
				addUserType(UserType.SYSADMIN);
			}
		}
	}

	public boolean equals(UserAccountBean ub) {
		if (ub == null) {
			return false;
		}
		return id == ub.getId();
	}

	/**
	 * @return Returns the numVisitsToMainMenu.
	 */
	public int getNumVisitsToMainMenu() {
		return numVisitsToMainMenu;
	}

	/**
	 * @param numVisitsToMainMenu
	 *            The numVisitsToMainMenu to set.
	 */
	public void setNumVisitsToMainMenu(int numVisitsToMainMenu) {
		this.numVisitsToMainMenu = numVisitsToMainMenu;
	}

	public void incNumVisitsToMainMenu() {
		numVisitsToMainMenu++;
	}

	/**
	 * @return Returns the notes.
	 */
	public String getNotes() {
		return notes;
	}

	/**
	 * @param notes
	 *            The notes to set.
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Boolean getRunWebservices() {
		return runWebservices;
	}

	public void setRunWebservices(Boolean runWebservices) {
		this.runWebservices = runWebservices;
	}

	public String getPentahoUserSession() {
		return pentahoUserSession;
	}

	public void setPentahoUserSession(String pentahoUserSession) {
		this.pentahoUserSession = pentahoUserSession;
	}

	public Date getPentahoTokenDate() {
		return pentahoTokenDate;
	}

	public void setPentahoTokenDate(Date pentahoTokenDate) {
		this.pentahoTokenDate = pentahoTokenDate;
	}

	public String getJsonData(String password, UserType userType, Role role) throws Exception {
		JSONObject jsonData = new JSONObject();
		jsonData.put("id", id);
		jsonData.put("username", name);
		jsonData.put("firstname", firstName);
		jsonData.put("lastname", lastName);
		jsonData.put("email", email);
		jsonData.put("phone", phone);
		jsonData.put("scope", activeStudyId);
		if (!password.isEmpty()) {
			jsonData.put("password", password);
		}
		jsonData.put("company", institutionalAffiliation);
		jsonData.put("allowsoap", runWebservices);
		jsonData.put("role", role.getCode());
		jsonData.put("usertype", userType.getCode());
		return jsonData.toString();
	}
}
