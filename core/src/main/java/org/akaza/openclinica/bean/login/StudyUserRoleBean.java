/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2014 Clinovo Inc.
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
import org.akaza.openclinica.bean.managestudy.StudyBean;

/**
 * Container for study user role.
 * 
 */
public class StudyUserRoleBean extends AuditableEntityBean {

	private static final long serialVersionUID = -758476275327298596L;

	private Role role;
	private int studyId;
	private int userAccountId = 0;
	private String studyName = "";
	private String lastName = "";
	private String firstName = "";
	private String userName = "";
	private int parentStudyId = 0;
	private boolean canMonitor;
	private boolean canSubmitData;
	private boolean canExtractData;
	private boolean canManageStudy;
	private boolean canCode;
	private boolean canEvaluate;

	/**
	 * Default study user role bean constructor.
	 */
	public StudyUserRoleBean() {
		role = Role.INVALID;
		studyId = 0;
		setRole(role);
		status = Status.AVAILABLE;
	}

	public Role getRole() {
		return role;
	}

	/**
	 * Set new role for current study user role.
	 * 
	 * @param role
	 *            the new role for study user role bean.
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

		this.canMonitor = this.role == Role.SYSTEM_ADMINISTRATOR || Role.isMonitor(this.role);

		this.canCode = this.role == Role.STUDY_CODER || this.role == Role.STUDY_ADMINISTRATOR;

		this.canEvaluate = this.role == Role.STUDY_EVALUATOR || this.role == Role.STUDY_ADMINISTRATOR;
	}

	public int getUserAccountId() {
		return userAccountId;
	}

	public void setUserAccountId(int userAccountId) {
		this.userAccountId = userAccountId;
	}

	public String getRoleName() {
		return role.getName();
	}

	public String getRoleCode() {
		return role.getCode();
	}

	/**
	 * Set new role for current user role bean using role name.
	 * 
	 * @param roleName
	 *            the role name.
	 */
	public void setRoleName(String roleName) {
		Role role = Role.getByName(roleName);
		setRole(role);
	}

	public int getStudyId() {
		return studyId;
	}

	public void setStudyId(int studyId) {
		this.studyId = studyId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getStudyName() {
		return studyName;
	}

	public void setStudyName(String studyName) {
		this.studyName = studyName;
	}

	public int getParentStudyId() {
		return parentStudyId;
	}

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

	public boolean isSiteMonitor() {
		return this.role == Role.SITE_MONITOR;
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

	public boolean isStudyMonitor() {
		return this.role.equals(Role.STUDY_MONITOR);
	}

	public boolean isStudyCoder() {
		return this.role.equals(Role.STUDY_CODER);
	}

	public boolean isCanCode() {
		return canCode;
	}

	public void setCanCode(boolean canCode) {
		this.canCode = canCode;
	}

	public boolean isStudyEvaluator() {
		return this.role.equals(Role.STUDY_EVALUATOR);
	}

	public boolean isCanEvaluate() {
		return canEvaluate;
	}

	public void setCanEvaluate(boolean canEvaluate) {
		this.canEvaluate = canEvaluate;
	}

	public boolean isStudyLevelRole() {
		return this.role == Role.SYSTEM_ADMINISTRATOR || this.role == Role.STUDY_ADMINISTRATOR
				|| this.role == Role.STUDY_MONITOR || this.role == Role.STUDY_CODER
				|| this.role == Role.STUDY_EVALUATOR;
	}

	/**
	 * Determines role of user in current study.
	 * 
	 * @param currentUser
	 *            UserAccountBean
	 * @param currentStudy
	 *            StudyBean
	 * @return Role in currentStudy
	 */
	public static Role determineRoleInCurrentStudy(UserAccountBean currentUser, StudyBean currentStudy) {
		Role role = currentUser.getRoleByStudy(currentStudy.getId()).getRole();
		if (!role.equals(Role.INVALID)) {
			return role;
		}
		return currentUser.getRoleByStudy(currentStudy.getParentStudyId()).getRole();
	}
}
