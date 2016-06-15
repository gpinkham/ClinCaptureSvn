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
package com.clinovo.rest.security;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.rest.exception.RestException;
import com.clinovo.rest.model.UserDetails;
import com.clinovo.rest.util.RestValidator;

/**
 * PermissionChecker class.
 */
@SuppressWarnings("unused")
public class PermissionChecker extends HandlerInterceptorAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(PermissionChecker.class);

	public static final String TOKEN = "token";
	public static final String CLIENT_VERSION = "version";
	public static final String AUTHENTICATE = "authenticate";
	public static final String CLIENT_VERSION_PATTERN = "\\d{1,}.*\\d{1,}";
	public static final String API_AUTHENTICATED_USER_DETAILS = "API_AUTHENTICATED_USER_DETAILS";

	@Autowired
	private DataSource dataSource;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ApplicationContext applicationContext;

	/**
	 * Note that we may be ensure that the connection is SSL. Because we don't allow anything else.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param handler
	 *            Object
	 * @return boolean
	 * @throws Exception
	 *             an Exception
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		boolean proceed = false;
		LocaleResolver.resolveRestApiLocale();
		if (handler instanceof HandlerMethod) {
			proceed = RestValidator.validate(request, dataSource, messageSource, (HandlerMethod) handler);
			if (!proceed) {
				authenticate(request);
				proceed = true;
			}
		}
		return proceed;
	}

	private void authenticate(HttpServletRequest request) throws Exception {
		String token = request.getParameter(TOKEN);
		UserAccountDAO userAccountDao = new UserAccountDAO(dataSource);
		StudyUserRoleBean studyUserRoleBean = userAccountDao.findRoleByToken(token);

		RestValidator.validateStudyUserRole(studyUserRoleBean, messageSource, "rest.userMustBeAuthenticated");

		StudyBean studyBean = (StudyBean) new StudyDAO(dataSource).findByPK(studyUserRoleBean.getStudyId());
		UserAccountBean userAccountBean = (UserAccountBean) userAccountDao
				.findByUserName(studyUserRoleBean.getUserName());
		if (userAccountBean == null || studyBean == null || userAccountBean.getId() == 0 || studyBean.getId() == 0
				|| !userAccountBean.hasUserType(UserType.SYSADMIN) || studyBean.getParentStudyId() > 0) {
			throw new RestException(messageSource, "rest.authenticationservice.wrongUserRoleIsPresent",
					HttpServletResponse.SC_UNAUTHORIZED);
		}

		int tokenExpirationDate = CoreResources.getTokenExpirationDate();
		if (tokenExpirationDate > 0) {
			if (studyUserRoleBean.getTokenExpirationDate() == null
					|| new Date().compareTo(studyUserRoleBean.getTokenExpirationDate()) > 0) {
				throw new RestException(messageSource, "rest.authenticationservice.tokenExpired",
						HttpServletResponse.SC_UNAUTHORIZED);
			}
		}

		UserDetails userDetails = new UserDetails();
		userDetails.setToken(token);
		userDetails.setStudyOid(studyBean.getOid());
		userDetails.setStudyName(studyBean.getName());
		userDetails.setUserId(userAccountBean.getId());
		userDetails.setUserName(userAccountBean.getName());
		userDetails.setUserTypeCode(UserType.SYSADMIN.getCode());
		userDetails.setStudyStatus(studyBean.getStatus().getCode());
		userDetails.setRoleCode(studyUserRoleBean.getRole().getCode());
		userDetails.setUserStatus(userAccountBean.getStatus().getCode());

		request.setAttribute(API_AUTHENTICATED_USER_DETAILS, userDetails);
	}
}
