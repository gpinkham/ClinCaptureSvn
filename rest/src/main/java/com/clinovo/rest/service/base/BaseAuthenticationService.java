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
package com.clinovo.rest.service.base;

import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.core.OpenClinicaPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import com.clinovo.rest.exception.RestException;
import com.clinovo.rest.model.UserDetails;
import com.clinovo.rest.util.RestValidator;

/**
 * BaseAuthenticationService.
 */
public abstract class BaseAuthenticationService extends BaseService {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private OpenClinicaPasswordEncoder passwordEncoder;

	protected UserDetails authenticateUser(String userName, String password, String studyName) {
		StudyBean studyBean = getStudyBean(studyName);
		UserAccountBean userAccountBean = getUserAccountBean(userName, password);
		StudyUserRoleBean studyUserRoleBean = getStudyUserRoleBean(studyBean, userAccountBean);

		studyUserRoleBean.setToken(
				Integer.toString(studyUserRoleBean.getPrimaryKey()).concat(UUID.randomUUID().toString().toUpperCase()));
		studyUserRoleBean.setTokenGenerationDate(new Date());
		getUserAccountDAO().saveToken(studyUserRoleBean);

		UserDetails userDetails = new UserDetails();
		userDetails.setUserName(userName);
		userDetails.setStudyName(studyName);
		userDetails.setStudyOid(studyBean.getOid());
		userDetails.setUserId(userAccountBean.getId());
		userDetails.setToken(studyUserRoleBean.getToken());
		userDetails.setUserTypeCode(UserType.SYSADMIN.getCode());
		userDetails.setStudyStatus(studyBean.getStatus().getCode());
		userDetails.setRoleCode(studyUserRoleBean.getRole().getCode());
		userDetails.setUserStatus(userAccountBean.getStatus().getCode());
		return userDetails;
	}

	private StudyBean getStudyBean(String studyName) {
		StudyBean studyBean = (StudyBean) getStudyDAO().findStudyByName(studyName);
		if (studyBean.getId() == 0) {
			throw new RestException(messageSource, "rest.authenticationservice.wrongStudyName",
					HttpServletResponse.SC_UNAUTHORIZED);
		}
		return studyBean;
	}

	private UserAccountBean getUserAccountBean(String userName, String password) {
		UserAccountBean userAccountBean = (UserAccountBean) getUserAccountDAO().findByUserName(userName);
		if (userAccountBean != null && userAccountBean.getId() > 0) {
			if (!passwordEncoder.isPasswordValid(userAccountBean.getPasswd(), password, null)) {
				throw new RestException(messageSource,
						"rest.authenticationservice.authenticate.wrongUserNameOrPassword",
						HttpServletResponse.SC_UNAUTHORIZED);
			} else if (!userAccountBean.hasUserType(UserType.SYSADMIN)) {
				throw new RestException(messageSource,
						"rest.authenticationservice.onlyUsersWithTypeAdministratorCanBeAuthenticated",
						HttpServletResponse.SC_UNAUTHORIZED);
			}
		} else {
			throw new RestException(messageSource, "rest.authenticationservice.authenticate.noUserFound",
					HttpServletResponse.SC_UNAUTHORIZED);
		}
		return userAccountBean;
	}

	private StudyUserRoleBean getStudyUserRoleBean(StudyBean studyBean, UserAccountBean userAccountBean) {
		StudyUserRoleBean studyUserRoleBean = getUserAccountDAO()
				.findRoleByUserNameAndStudyId(userAccountBean.getName(), studyBean.getId());
		RestValidator.validateStudyUserRole(studyUserRoleBean, messageSource,
				"rest.authenticationservice.userIsNotAssignedToStudy");
		return studyUserRoleBean;
	}
}
