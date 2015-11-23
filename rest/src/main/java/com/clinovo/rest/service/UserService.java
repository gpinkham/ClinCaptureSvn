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

import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.hibernate.ConfigurationDao;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.clinovo.rest.annotation.RestAccess;
import com.clinovo.rest.annotation.RestParameterPossibleValues;
import com.clinovo.rest.annotation.RestParameterPossibleValuesHolder;
import com.clinovo.rest.enums.UserRole;
import com.clinovo.rest.exception.RestException;
import com.clinovo.rest.model.UserDetails;
import com.clinovo.rest.util.ValidatorUtil;
import com.clinovo.service.UserAccountService;
import com.clinovo.validator.UserValidator;

/**
 * UserService.
 */
@Controller("restUserService")
@RequestMapping("/user")
@SuppressWarnings({"unused", "rawtypes"})
public class UserService extends BaseService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private DataSource dataSource;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ConfigurationDao configurationDao;

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
	 * @param timeZone
	 *            String
	 * @return UserAccountBean
	 * @throws Exception
	 *             an Exception
	 */
	@ResponseBody
	@RestAccess({UserRole.SYS_ADMIN, UserRole.STUDY_ADMIN_ADMIN, UserRole.STUDY_MONITOR_ADMIN})
	@RestParameterPossibleValuesHolder({
			@RestParameterPossibleValues(name = "usertype", values = "1,2", valueDescriptions = "rest.usertype.valueDescription"),
			@RestParameterPossibleValues(name = "role", values = "1,2,6,7,8,4,5,9", valueDescriptions = "rest.role.valueDescription")})
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public UserAccountBean createUser(@RequestParam("username") String userName,
			@RequestParam("firstname") String firstName, @RequestParam("lastname") String lastName,
			@RequestParam("email") String email, @RequestParam(value = "phone", required = false) String phone,
			@RequestParam("company") String company, @RequestParam(value = "usertype") int userType,
			@RequestParam(value = "allowsoap", defaultValue = "false", required = false) boolean allowSoap,
			@RequestParam(value = "displaypassword", defaultValue = "false", required = false) boolean displayPassword,
			@RequestParam("role") int role, @RequestParam(value = "timezone", required = false) String timeZone)
					throws Exception {
		StudyDAO studyDao = new StudyDAO(dataSource);
		UserAccountDAO userAccountDao = new UserAccountDAO(dataSource);
		StudyBean studyBean = UserDetails.getCurrentUserDetails().getCurrentStudy(dataSource);

		HashMap errors = UserValidator.validateUserCreate(configurationDao, userAccountDao, studyBean, true);
		ValidatorUtil.checkForErrors(errors);

		Role userAccountRole = Role.get(role);
		UserType userAccountType = UserType.get(userType);

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

		userAccountService.createUser(UserDetails.getCurrentUserDetails().getCurrentUser(dataSource), userAccountBean,
				userAccountRole, !displayPassword);

		if (userAccountBean.getId() == 0) {
			throw new RestException(messageSource, "rest.createUser.operationFailed",
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

		userAccountBean.setPasswd(displayPassword ? userAccountBean.getRealPassword() : "");
		return userAccountBean;
	}

	/**
	 * Method that removes user.
	 *
	 * @param userName
	 *            String
	 * @return String
	 * @throws Exception
	 *             an Exception
	 */
	@ResponseBody
	@RestAccess({UserRole.SYS_ADMIN, UserRole.STUDY_ADMIN_ADMIN, UserRole.STUDY_MONITOR_ADMIN})
	@RequestMapping(value = "/remove", method = RequestMethod.POST)
	public UserAccountBean removeUser(@RequestParam("username") String userName) throws Exception {
		UserAccountBean updater = UserDetails.getCurrentUserDetails().getCurrentUser(dataSource);
		UserAccountBean userAccountBean = getUserAccountBean(userName);
		userAccountService.removeUser(userAccountBean, updater);
		return userAccountBean;
	}

	/**
	 * Method that restores user.
	 *
	 * @param userName
	 *            String
	 * @return String
	 * @throws Exception
	 *             an Exception
	 */
	@ResponseBody
	@RestAccess({UserRole.SYS_ADMIN, UserRole.STUDY_ADMIN_ADMIN, UserRole.STUDY_MONITOR_ADMIN})
	@RequestMapping(value = "/restore", method = RequestMethod.POST)
	public UserAccountBean restoreUser(@RequestParam("username") String userName) throws Exception {
		UserAccountBean updater = UserDetails.getCurrentUserDetails().getCurrentUser(dataSource);
		UserAccountBean userAccountBean = getUserAccountBean(userName);
		userAccountService.restoreUser(userAccountBean, updater);
		return userAccountBean;
	}
}
