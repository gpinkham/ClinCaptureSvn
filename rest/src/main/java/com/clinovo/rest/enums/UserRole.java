/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 *
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 *
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use.
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO'S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/

package com.clinovo.rest.enums;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.UserType;

/**
 * UserRole enum class.
 */
public enum UserRole {

	ANY_USER(null, null),

	ANY_ADMIN(null, null),

	SYS_ADMIN(Role.SYSTEM_ADMINISTRATOR.getCode(), UserType.SYSADMIN.getCode()),

	STUDY_ADMIN_ADMIN(Role.STUDY_ADMINISTRATOR.getCode(), UserType.SYSADMIN.getCode()), STUDY_ADMIN_USER(
			Role.STUDY_ADMINISTRATOR.getCode(), UserType.USER.getCode()),

	STUDY_MONITOR_ADMIN(Role.STUDY_MONITOR.getCode(), UserType.SYSADMIN.getCode()), STUDY_MONITOR_USER(
			Role.STUDY_MONITOR.getCode(), UserType.USER.getCode()),

	STUDY_CODER_ADMIN(Role.STUDY_CODER.getCode(), UserType.SYSADMIN.getCode()), STUDY_CODER_USER(Role.STUDY_CODER
			.getCode(), UserType.USER.getCode()),

	STUDY_EVALUATOR_ADMIN(Role.STUDY_EVALUATOR.getCode(), UserType.SYSADMIN.getCode()), STUDY_EVALUATOR_USER(
			Role.STUDY_EVALUATOR.getCode(), UserType.USER.getCode()),

	STUDY_DIRECTOR_ADMIN(Role.STUDY_DIRECTOR.getCode(), UserType.SYSADMIN.getCode()), STUDY_DIRECTOR_USER(
			Role.STUDY_DIRECTOR.getCode(), UserType.USER.getCode()),

	CRC_ADMIN(Role.CLINICAL_RESEARCH_COORDINATOR.getCode(), UserType.SYSADMIN.getCode()), CRC_USER(
			Role.CLINICAL_RESEARCH_COORDINATOR.getCode(), UserType.USER.getCode()),

	INVESTIGATOR_ADMIN(Role.INVESTIGATOR.getCode(), UserType.SYSADMIN.getCode()), INVESTIGATOR_USER(Role.INVESTIGATOR
			.getCode(), UserType.USER.getCode());

	private String roleCode;
	private String userTypeCode;

	UserRole(String roleCode, String userTypeCode) {
		this.roleCode = roleCode;
		this.userTypeCode = userTypeCode;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public String getUserTypeCode() {
		return userTypeCode;
	}
}