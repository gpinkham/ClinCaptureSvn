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

/**
 * @author thickerson
 */

/**
 * @author ssachs
 * 
 *         The superclass id field is the role id. The superclass name field is the role name.
 */

public class StudyUserRoleBean extends AuditableEntityBean {

	private static final long serialVersionUID = -758476275327298596L;

	private Role role;

	private int studyId;

	// not in the database, and not guaranteed to correspond to studyId; studyId
	// is authoritative
	// this is only provided as a convenience
	private String studyName = "";

	// not in the database, and not guaranteed to correspond to studyId; studyId
	// is authoritative
	// this is only provided as a convenience
	private int parentStudyId = 0;

	private String lastName = ""; // not in the DB,not guaranteed to have a
	// value

	private String firstName = "";// not in the DB,not guaranteed to have a
	// value

	private String userName = ""; // name here is role.name, this is different
	// from name,not guaranteed to have a value

	// User role capabilities, use this instead the role name.
	private boolean canSubmitData;
	private boolean canExtractData;
	private boolean canManageStudy;

	private int userAccountId = 0;

	private boolean canMonitor;

	public StudyUserRoleBean() {
		role = Role.INVALID;
		studyId = 0;
		setRole(role);
		status = Status.AVAILABLE;
	}

	/**
	 * @return Returns the role.
	 */
	public Role getRole() {
		return role;
	}

	/**
	 * @param role
	 *            The role to set.
	 */
	public void setRole(Role role) {
		this.role = role;
		super.setId(role.getId());
		super.setName(role.getCode());
		this.canSubmitData = this.role == Role.SYSTEM_ADMINISTRATOR || this.role == Role.STUDY_ADMINISTRATOR
				|| this.role == Role.STUDY_DIRECTOR || this.role == Role.CLINICAL_RESEARCH_COORDINATOR
				|| this.role == Role.INVESTIGATOR;
		this.canExtractData = this.role == Role.SYSTEM_ADMINISTRATOR || this.role == Role.STUDY_ADMINISTRATOR
				|| this.role == Role.STUDY_DIRECTOR || this.role == Role.INVESTIGATOR;
		this.canManageStudy = this.role == Role.SYSTEM_ADMINISTRATOR || this.role == Role.STUDY_ADMINISTRATOR
				|| this.role == Role.STUDY_DIRECTOR;
		this.canMonitor = this.role == Role.SYSTEM_ADMINISTRATOR || this.role == Role.STUDY_MONITOR;
	}

	public int getUserAccountId() {
		return userAccountId;
	}

	public void setUserAccountId(int userAccountId) {
		this.userAccountId = userAccountId;
	}

	/**
	 * @return Returns the roleName.
	 */
	public String getRoleName() {
		return role.getName();
	}

	/**
	 * @param roleName
	 *            The roleName to set.
	 */
	public void setRoleName(String roleName) {
		Role role = Role.getByName(roleName);
		setRole(role);
	}

	/**
	 * @return Returns the studyId.
	 */
	public int getStudyId() {
		return studyId;
	}

	/**
	 * @param studyId
	 *            The studyId to set.
	 */
	public void setStudyId(int studyId) {
		this.studyId = studyId;
	}

	// this is different from the meaning of "name"
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return Returns the studyName.
	 */
	public String getStudyName() {
		return studyName;
	}

	/**
	 * @param studyName
	 *            The studyName to set.
	 */
	public void setStudyName(String studyName) {
		this.studyName = studyName;
	}

	/**
	 * @return Returns the parentStudyId.
	 */
	public int getParentStudyId() {
		return parentStudyId;
	}

	/**
	 * @param parentStudyId
	 *            The parentStudyId to set.
	 */
	public void setParentStudyId(int parentStudyId) {
		this.parentStudyId = parentStudyId;
	}

	@Override
	public String getName() {
		if (role != null) {
			return role.getName();
		}
		return "";
	}

    @Override
	public int getId() {
		if (role != null) {
			return role.getId();
		}
		return 0;
	}

	@Override
	public void setId(int id) {
		setRole(Role.get(id));
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public boolean isInvalid() {
		return this.role == Role.INVALID;
	}

	public boolean isSubmitData() {
		return canSubmitData;
	}

	public boolean isExtractData() {
		return canExtractData;
	}

	public boolean isManageStudy() {
		return canManageStudy;
	}

	public boolean isMonitor() {
		return canMonitor;
	}

	public boolean isInvestigator() {
		return this.role == Role.INVESTIGATOR;
	}

	public boolean isClinicalResearchCoordinator() {
		return this.role == Role.CLINICAL_RESEARCH_COORDINATOR;
	}

	public boolean isStudyAdministrator() {
		return this.role == Role.STUDY_ADMINISTRATOR;
	}

	public boolean isStudyDirector() {
		return this.role == Role.STUDY_DIRECTOR;
	}

    public boolean isSysAdmin() {
        return this.role == Role.SYSTEM_ADMINISTRATOR;
    }
}
