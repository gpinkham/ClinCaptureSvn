/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 *
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clincapture.com/contact for pricing information.
 *
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use.
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO'S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.rest.model;

import javax.sql.DataSource;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.clinovo.enums.study.StudyOrigin;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;

import com.clinovo.rest.security.PermissionChecker;
import com.clinovo.util.RequestUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * UserDetails.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "UserDetails", namespace = "http://www.cdisc.org/ns/odm/v1.3")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, creatorVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class UserDetails {

	private int userId;

	@JsonProperty("userName")
	@XmlElement(name = "UserName", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private String userName;

	@JsonProperty("userStatus")
	@XmlElement(name = "UserStatus", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private String userStatus;

	@JsonProperty("studyName")
	@XmlElement(name = "StudyName", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private String studyName;

	@JsonProperty("studyStatus")
	@XmlElement(name = "StudyStatus", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private String studyStatus;

	private String studyOid;

	@JsonProperty("role")
	@XmlElement(name = "Role", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private String roleCode;

	@JsonProperty("userType")
	@XmlElement(name = "UserType", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private String userTypeCode;

	@JsonProperty("token")
	@XmlElement(name = "Token", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private String token;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

	public String getStudyName() {
		return studyName;
	}

	public void setStudyName(String studyName) {
		this.studyName = studyName;
	}

	public String getStudyStatus() {
		return studyStatus;
	}

	public void setStudyStatus(String studyStatus) {
		this.studyStatus = studyStatus;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getUserTypeCode() {
		return userTypeCode;
	}

	public void setUserTypeCode(String userTypeCode) {
		this.userTypeCode = userTypeCode;
	}

	public String getStudyOid() {
		return studyOid;
	}

	public void setStudyOid(String studyOid) {
		this.studyOid = studyOid;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	/**
	 * Method that returns current user details.
	 *
	 * @return UserDetails
	 */
	public static UserDetails getCurrentUserDetails() {
		return (UserDetails) RequestUtil.getRequest().getAttribute(PermissionChecker.API_AUTHENTICATED_USER_DETAILS);
	}

	/**
	 * Method that returns true if current user is system administrator / root.
	 *
	 * @return boolean
	 */
	public static boolean isSystemAdministrator() {
		return getCurrentUserDetails().getRoleCode().equals(Role.SYSTEM_ADMINISTRATOR.getCode());
	}

	/**
	 * Method that returns current StudyBean.
	 *
	 * @param dataSource
	 *            DataSource
	 * @return StudyBean
	 */
	public StudyBean getCurrentStudy(DataSource dataSource) {
		StudyBean studyBean = new StudyDAO(dataSource).findByOid(studyOid);
		// NOTE that REST API must consider any study as studio's study
		studyBean.setOrigin(StudyOrigin.STUDIO.getName());
		return studyBean;
	}

	/**
	 * Method that returns current UserAccountBean.
	 *
	 * @param dataSource
	 *            DataSource
	 * @return UserAccountBean
	 */
	public UserAccountBean getCurrentUser(DataSource dataSource) {
		return (UserAccountBean) new UserAccountDAO(dataSource).findByPK(userId);
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
