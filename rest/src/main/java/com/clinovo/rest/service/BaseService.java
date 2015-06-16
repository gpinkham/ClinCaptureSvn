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

package com.clinovo.rest.service;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import com.clinovo.rest.exception.RestException;

/**
 * BaseService.
 */
public abstract class BaseService {

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseService.class);

	@Autowired
	private DataSource dataSource;

	@Autowired
	private MessageSource messageSource;

	/**
	 * Method that checks role / scope consistency.
	 *
	 * @param roleId
	 *            int
	 * @param studyBean
	 *            StudyBean
	 * @throws RestException
	 *             the RestException
	 */
	public void checkRoleScopeConsistency(int roleId, StudyBean studyBean) throws RestException {
		Role role = null;
		try {
			role = Role.get(roleId);
		} catch (Exception ex) {
			LOGGER.error("Error has occurred.", ex);
		}
		if (role == null) {
			throw new RestException(messageSource, "rest.createUser.roleDoesNotExist", new Object[]{roleId},
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} else if (studyBean.getId() == 0) {
			throw new RestException(messageSource, "rest.createUser.studyDoesNotExist",
					new Object[]{studyBean.getId()}, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} else if (studyBean.getParentStudyId() == 0
				&& (role.getCode().equals(Role.CLINICAL_RESEARCH_COORDINATOR.getCode())
						|| role.getCode().equals(Role.INVESTIGATOR.getCode()) || role.getCode().equals(
						Role.SITE_MONITOR.getCode()))) {
			throw new RestException(messageSource, "rest.createUser.itsForbiddenToAssignSiteLevelRoleToStudy", null,
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} else if (studyBean.getParentStudyId() > 0
				&& (role.getCode().equals(Role.STUDY_ADMINISTRATOR.getCode())
						|| role.getCode().equals(Role.STUDY_DIRECTOR.getCode())
						|| role.getCode().equals(Role.STUDY_MONITOR.getCode())
						|| role.getCode().equals(Role.STUDY_CODER.getCode()) || role.getCode().equals(
						Role.STUDY_EVALUATOR.getCode()))) {
			throw new RestException(messageSource, "rest.createUser.itsForbiddenToAssignStudyLevelRoleToSite", null,
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Method that checks username existence.
	 * 
	 * @param userName
	 *            String
	 * @throws RestException
	 *             the RestException
	 */
	public void checkUsernameExistence(String userName) throws RestException {
		if (new UserAccountDAO(dataSource).findByUserName(userName).getId() > 0) {
			throw new RestException(messageSource, "rest.createUser.usernameHasBeenTaken",
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
}
