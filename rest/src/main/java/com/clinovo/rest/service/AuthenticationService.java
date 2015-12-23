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

import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.core.OpenClinicaPasswordEncoder;
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

import com.clinovo.rest.exception.RestException;
import com.clinovo.rest.model.UserDetails;
import com.clinovo.rest.security.PermissionChecker;
import com.clinovo.rest.service.base.BaseService;
import com.clinovo.util.RequestUtil;

/**
 * AuthenticationService.
 */
@Controller("restAuthenticationService")
@SuppressWarnings("unused")
public class AuthenticationService extends BaseService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);

	@Autowired
	private DataSource dataSource;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private OpenClinicaPasswordEncoder passwordEncoder;

	/**
	 * Method changes current scope.
	 *
	 * @param studyName
	 *            String
	 * @throws RestException
	 *             the RESTException
	 * @return String
	 */
	@ResponseBody
	@RequestMapping(value = "/changeScope", method = RequestMethod.POST)
	public UserDetails changeScope(@RequestParam("studyName") String studyName) throws RestException {
		UserDetails userDetails = getUserDetails();
		StudyBean studyBean = (StudyBean) new StudyDAO(dataSource).findByName(studyName);
		StudyUserRoleBean surBean = getStudyUserRole(studyBean, getCurrentUser(),
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		userDetails.setStudyName(studyName);
		userDetails.setStudyStatus(studyBean.getStatus().getCode());
		userDetails.setStudyOid(studyBean.getOid());
		userDetails.setRoleCode(surBean.getRole().getCode());
		userDetails.setUserTypeCode(UserType.SYSADMIN.getCode());
		return userDetails;
	}

	/**
	 * Method does user authentication.
	 *
	 * @param userName
	 *            String
	 * @param password
	 *            String
	 * @param studyName
	 *            String
	 * @throws RestException
	 *             the RESTException
	 * @return String
	 */
	@ResponseBody
	@RequestMapping(value = "/authentication", method = RequestMethod.POST)
	public UserDetails authenticate(@RequestParam("userName") String userName,
			@RequestParam("password") String password, @RequestParam("studyName") String studyName)
					throws RestException {
		UserAccountBean userAccountBean = authenticateUser(userName, password, passwordEncoder);
		StudyBean studyBean = (StudyBean) new StudyDAO(dataSource).findByName(studyName);
		StudyUserRoleBean surBean = getStudyUserRole(studyBean, userAccountBean, HttpServletResponse.SC_UNAUTHORIZED);
		UserDetails userDetails = new UserDetails();
		userDetails.setUserId(userAccountBean.getId());
		userDetails.setUserName(userName);
		userDetails.setPassword(password);
		userDetails.setUserStatus(userAccountBean.getStatus().getCode());
		userDetails.setStudyName(studyName);
		userDetails.setStudyStatus(studyBean.getStatus().getCode());
		userDetails.setStudyOid(studyBean.getOid());
		userDetails.setRoleCode(surBean.getRole().getCode());
		userDetails.setUserTypeCode(UserType.SYSADMIN.getCode());
		RequestUtil.getRequest().getSession().setAttribute(PermissionChecker.API_AUTHENTICATED_USER_DETAILS,
				userDetails);
		return userDetails;
	}
}
