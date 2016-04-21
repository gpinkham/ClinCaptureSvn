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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.hibernate.ConfigurationDao;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import com.clinovo.rest.exception.RestException;
import com.clinovo.rest.model.UserDetails;
import com.clinovo.rest.util.ValidatorUtil;
import com.clinovo.service.UserAccountService;
import com.clinovo.validator.UserValidator;

/**
 * BaseUserService.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class BaseUserService extends BaseService {

	public static final String ROOT = "root";

	@Autowired
	private UserAccountService userAccountService;

	@Autowired
	private ConfigurationDao configurationDao;

	@Autowired
	private MessageSource messageSource;

	protected UserAccountBean getUserAccountBean(String userName, boolean performAnOperation) {
		UserAccountDAO userAccountDAO = getUserAccountDAO();
		UserAccountBean userAccountBean = (UserAccountBean) userAccountDAO.findByUserName(userName);
		if (userAccountBean.getId() == 0) {
			throw new RestException(messageSource, "rest.userservice.userDoesNotExist",
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} else if (!UserDetails.isSystemAdministrator()) {
			boolean allowToProceed = false;
			List<StudyUserRoleBean> studyUserRoleBeanList = (List<StudyUserRoleBean>) userAccountDAO
					.findAllRolesByUserName(getUserDetails().getUserName());
			for (StudyUserRoleBean studyUserRoleBean : studyUserRoleBeanList) {
				if (userAccountDAO.isUserPresentInStudy(userName, studyUserRoleBean.getStudyId())) {
					allowToProceed = true;
					break;
				}
			}
			if (!allowToProceed) {
				throw new RestException(messageSource,
						"rest.userservice.itIsForbiddenToPerformThisOperationOnUserThatDoesNotBelongToCurrentUserScope",
						new Object[]{userName}, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}
		if (performAnOperation) {
			if (userName.equals(ROOT)) {
				throw new RestException(messageSource, "rest.userservice.itIsForbiddenToPerformThisOperationOnRootUser",
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			} else if (userAccountBean.getId() == getUserDetails().getUserId()) {
				throw new RestException(messageSource, "rest.userservice.itIsForbiddenToPerformThisOperationOnYourself",
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}
		userAccountBean.setUserTypeCode(
				userAccountBean.hasUserType(UserType.SYSADMIN) ? UserType.SYSADMIN.getCode() : UserType.USER.getCode());
		userAccountBean.setPasswd("");
		return userAccountBean;
	}

	protected UserAccountBean createUser(String siteName, String userName, String firstName, String lastName,
			String email, String phone, String company, int userType, boolean allowSoap, boolean displayPassword,
			int role, String timeZone) throws Exception {
		if (Arrays.asList(Role.INVESTIGATOR.getId(), Role.CLINICAL_RESEARCH_COORDINATOR.getId(),
				Role.SITE_MONITOR.getId()).contains(role) && (siteName == null || siteName.isEmpty())) {
			throw new RestException(messageSource, "rest.userservice.createuser.pleaseSpecifyASiteName",
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

		StudyBean studyBean = siteName != null ? getSite(siteName) : getCurrentStudy();
		UserAccountDAO userAccountDao = getUserAccountDAO();

		HashMap errors = UserValidator.validateUserCreate(configurationDao, userAccountDao, null, studyBean);
		ValidatorUtil.checkForErrors(errors);

		Role userAccountRole = Role.get(role);
		UserType userAccountType = UserType.get(userType);

		if (!studyBean.isSite()) {
			if (userAccountRole.equals(Role.STUDY_CODER)
					&& !studyBean.getStudyParameterConfig().getMedicalCoding().equals(YES)) {
				throw new RestException(messageSource, "rest.userservice.createuser.codingIsNotAvailable",
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			} else
				if (userAccountRole.equals(Role.STUDY_EVALUATOR)
						&& !studyBean.getStudyParameterConfig().getStudyEvaluator().equals(YES)) {
				throw new RestException(messageSource, "rest.userservice.createuser.evaluationIsNotAvailable",
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}

		UserAccountBean userAccountBean = new UserAccountBean();
		userAccountBean.setName(userName);
		userAccountBean.setFirstName(firstName);
		userAccountBean.setLastName(lastName);
		userAccountBean.setEmail(email);
		userAccountBean.setPhone(phone);
		userAccountBean.setActiveStudyId(studyBean.getId());
		userAccountBean.setRunWebservices(allowSoap);
		userAccountBean.addUserType(userAccountType);
		userAccountBean.setInstitutionalAffiliation(company);
		userAccountBean.setRoleCode(userAccountRole.getCode());
		userAccountBean.setUserTypeCode(userAccountType.getCode());
		userAccountBean.setUserTimeZoneId(timeZone);

		userAccountService.createUser(getCurrentUser(), userAccountBean, userAccountRole, !displayPassword);

		if (userAccountBean.getId() == 0) {
			throw new RestException(messageSource, "rest.userservice.createuser.operationFailed",
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

		userAccountBean.setPasswd(displayPassword ? userAccountBean.getRealPassword() : "");
		return userAccountBean;
	}
}
