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

import com.clinovo.rest.annotation.RestAccess;
import com.clinovo.rest.annotation.RestParameterPossibleValues;
import com.clinovo.rest.annotation.RestParametersPossibleValues;
import com.clinovo.rest.enums.UserRole;
import com.clinovo.rest.exception.RestException;
import com.clinovo.rest.model.UserDetails;
import com.clinovo.service.UserAccountService;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * UserService.
 */
@Controller("restUserService")
@RequestMapping("/user")
@SuppressWarnings("unused")
public class UserService extends BaseService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private DataSource dataSource;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private UserAccountService userAccountService;

	@Autowired
	private org.akaza.openclinica.core.SecurityManager securityManager;

	/**
	 * Method that creates new user.
	 *
	 * @param userName
	 *            String
	 * @param firstName
	 *            String
	 * @param lastName
	 *            String
	 * @param email
	 *            String
	 * @param phone
	 *            String
	 * @param company
	 *            String
	 * @param userType
	 *            int
	 * @param allowSoap
	 *            boolean
	 * @param displayPassword
	 *            boolean
	 * @param role
	 *            int
	 * @return UserAccountBean
	 * @throws Exception
	 *             an Exception
	 */
	@RestAccess(UserRole.ANY_ADMIN)
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@ResponseBody
	@RestParametersPossibleValues({
			@RestParameterPossibleValues(name = "usertype", values = "1,2", valueDescriptions = "1 -> ADMINISTRATOR, 2 -> USER"),
			@RestParameterPossibleValues(name = "role", values = "1,2,6,7,8,4,5,9", valueDescriptions = "1 -> SYSTEM_ADMINISTRATOR, 2 -> STUDY_ADMINISTRATOR, 6 -> STUDY_MONITOR, 7 -> STUDY_CODER, 8 -> STUDY_EVALUATOR, 4 -> INVESTIGATOR, 5 -> CLINICAL_RESEARCH_COORDINATOR, 9 -> SITE_MONITOR")})
	public UserAccountBean createUser(@RequestParam("username") String userName,
			@RequestParam("firstname") String firstName, @RequestParam("lastname") String lastName,
			@RequestParam("email") String email, @RequestParam("phone") String phone,
			@RequestParam("company") String company, @RequestParam(value = "usertype") int userType,
			@RequestParam(value = "allowsoap", defaultValue = "false", required = false) boolean allowSoap,
			@RequestParam(value = "displaypassword", defaultValue = "false", required = false) boolean displayPassword,
			@RequestParam("role") int role) throws Exception {
		StudyBean studyBean = UserDetails.getCurrentUserDetails().getCurrentStudy(dataSource);

		checkUsernameExistence(userName);
		checkRoleScopeConsistency(role, studyBean);

		Role userAccountRole = Role.get(role);
		UserType userAccountType = UserType.get(userType);

		String password = securityManager.genPassword();
		String passwordHash = securityManager.encryptPassword(password, null);

		UserAccountBean userAccountBean = new UserAccountBean();
		userAccountBean.setName(userName);
		userAccountBean.setFirstName(firstName);
		userAccountBean.setLastName(lastName);
		userAccountBean.setEmail(email);
		userAccountBean.setPhone(phone);
		userAccountBean.setActiveStudyId(studyBean.getId());
		userAccountBean.setPasswd(passwordHash);
		userAccountBean.setRunWebservices(allowSoap);
		userAccountBean.addUserType(userAccountType);
		userAccountBean.setInstitutionalAffiliation(company);
		userAccountBean.setRoleCode(userAccountRole.getCode());
		userAccountBean.setUserTypeCode(userAccountType.getCode());

		userAccountService.createUser(UserDetails.getCurrentUserDetails().getCurrentUser(dataSource), userAccountBean,
				userAccountRole, displayPassword, password);

		if (userAccountBean.getId() == 0) {
			throw new RestException(messageSource, "rest.createUser.operationFailed",
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

		return userAccountBean;
	}

}
