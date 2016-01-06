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

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.clinovo.rest.annotation.RestParameterPossibleValues;
import com.clinovo.rest.annotation.RestParameterPossibleValuesHolder;
import com.clinovo.rest.service.base.BaseUserService;
import com.clinovo.service.UserAccountService;

/**
 * UserService.
 */
@Controller("restUserService")
@RequestMapping("/user")
public class UserService extends BaseUserService {

	@Autowired
	private UserAccountService userAccountService;

	/**
	 * Method that creates new study user.
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
	 * @param siteName
	 *            String
	 * @param timeZone
	 *            String
	 * @return UserAccountBean
	 * @throws Exception
	 *             an Exception
	 */
	@ResponseBody
	@RestParameterPossibleValuesHolder({
			@RestParameterPossibleValues(name = "userType", values = "1,2", valueDescriptions = "rest.userType.valueDescription"),
			@RestParameterPossibleValues(name = "role", values = "2,4,5,6,7,8,9,10", valueDescriptions = "rest.roles.valueDescription")})
	@RequestMapping(value = "/createUser", method = RequestMethod.POST)
	public UserAccountBean createUser(@RequestParam("userName") String userName,
			@RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName,
			@RequestParam("email") String email,
			@RequestParam(value = "phone", defaultValue = "", required = false) String phone,
			@RequestParam("company") String company, @RequestParam(value = "userType") int userType,
			@RequestParam(value = "allowSoap", defaultValue = "false", required = false) boolean allowSoap,
			@RequestParam(value = "displayPassword", defaultValue = "false", required = false) boolean displayPassword,
			@RequestParam("role") int role, @RequestParam(value = "siteName", required = false) String siteName,
			@RequestParam(value = "timeZone", required = false) String timeZone) throws Exception {
		return createUser(siteName, userName, firstName, lastName, email, phone, company, userType, allowSoap,
				displayPassword, role, timeZone);
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
	@RequestMapping(value = "/remove", method = RequestMethod.POST)
	public UserAccountBean removeUser(@RequestParam("userName") String userName) throws Exception {
		return userAccountService.removeUser(getUserAccountBean(userName), getCurrentUser());
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
	@RequestMapping(value = "/restore", method = RequestMethod.POST)
	public UserAccountBean restoreUser(@RequestParam("userName") String userName) throws Exception {
		return userAccountService.restoreUser(getUserAccountBean(userName), getCurrentUser());
	}
}
