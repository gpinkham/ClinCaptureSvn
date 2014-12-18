package com.clinovo.rest.service;

import com.clinovo.rest.annotation.RestAccess;
import com.clinovo.rest.exception.RestException;
import com.clinovo.rest.model.UserDetails;
import com.clinovo.service.UserAccountService;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.UserRole;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.UserAccountBean;
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
@Controller
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
	 * The method.
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
	 * @param scope
	 *            int
	 * @param role
	 *            int
	 * @return String
	 * @throws Exception
	 *             an Exception
	 */
	@RestAccess(UserRole.ANY_ADMIN)
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@ResponseBody
	public String createUser(@RequestParam("username") String userName, @RequestParam("firstname") String firstName,
			@RequestParam("lastname") String lastName, @RequestParam("email") String email,
			@RequestParam("phone") String phone, @RequestParam("company") String company,
			@RequestParam("usertype") int userType, @RequestParam("allowsoap") boolean allowSoap,
			@RequestParam("displaypassword") boolean displayPassword, @RequestParam("scope") int scope,
			@RequestParam("role") int role) throws Exception {
		checkStudyAccess(scope);
		checkUsernameExistence(userName);
		checkRoleScopeConsistency(role, scope);

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
		userAccountBean.setActiveStudyId(scope);
		userAccountBean.setPasswd(passwordHash);
		userAccountBean.setRunWebservices(allowSoap);
		userAccountBean.addUserType(userAccountType);
		userAccountBean.setInstitutionalAffiliation(company);

		userAccountBean = userAccountService.createUser(UserDetails.getCurrentUserDetails().getUserName(),
				userAccountBean, userAccountRole, displayPassword, password);

		if (userAccountBean.getId() == 0) {
			throw new RestException(messageSource, "rest.createUser.operationFailed",
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

		return userAccountBean.getJsonData(displayPassword ? password : "", userAccountType, userAccountRole);
	}

}
