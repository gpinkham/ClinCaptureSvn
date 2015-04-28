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

package com.clinovo.rest.security;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.UserRole;
import org.akaza.openclinica.bean.core.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.rest.annotation.RestAccess;
import com.clinovo.rest.exception.RestException;
import com.clinovo.rest.model.UserDetails;
import com.clinovo.rest.service.AuthenticationService;
import com.clinovo.rest.util.RequestParametersValidator;

/**
 * PermissionChecker class.
 */
@SuppressWarnings("unused")
public class PermissionChecker extends HandlerInterceptorAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(PermissionChecker.class);

	public static final String API_AUTHENTICATED_USER_DETAILS = "API_AUTHENTICATED_USER_DETAILS";

	@Autowired
	private DataSource dataSource;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ApplicationContext applicationContext;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		LocaleResolver.resolveRestApiLocale();
		if (!request.isSecure()) {
			throw new RestException(messageSource, "rest.authentication.onlyTheHttpsIsSupported",
					HttpServletResponse.SC_FORBIDDEN);
		} else {
			boolean proceed = false;
			if (handler instanceof HandlerMethod) {
				RequestParametersValidator.validate(request, messageSource, (HandlerMethod) handler);
				proceed = ((HandlerMethod) handler).getBean() instanceof AuthenticationService;
				if (!proceed) {
					UserDetails userDetails = (UserDetails) request.getSession().getAttribute(
							API_AUTHENTICATED_USER_DETAILS);
					if (userDetails != null) {
						if (userDetails.getRoleCode().equals(Role.SYSTEM_ADMINISTRATOR.getCode())) {
							proceed = true;
						} else {
							RestAccess accessAnnotation = ((HandlerMethod) handler).getMethod().getAnnotation(
									RestAccess.class);
							if (accessAnnotation != null && accessAnnotation.value() != null) {
								List<UserRole> methodUserRoles = Arrays.asList(accessAnnotation.value());
								if (methodUserRoles.contains(UserRole.ANY_USER)) {
									proceed = true;
								} else if (methodUserRoles.contains(UserRole.ANY_ADMIN)) {
									proceed = userDetails.getUserTypeCode().equals(UserType.SYSADMIN.getCode());
								}
								if (!proceed) {
									for (UserRole methodUserRole : methodUserRoles) {
										if (methodUserRole != UserRole.ANY_USER
												&& methodUserRole != UserRole.ANY_ADMIN
												&& methodUserRole.getRoleCode().equals(userDetails.getRoleCode())
												&& methodUserRole.getUserTypeCode().equals(
														userDetails.getUserTypeCode())) {
											proceed = true;
											break;
										}
									}
								}
							}
						}
					} else {
						throw new RestException(messageSource, "rest.authentication.userShouldBeAuthenticated",
								HttpServletResponse.SC_UNAUTHORIZED);
					}
				}
				if (!proceed) {
					throw new RestException(messageSource, "rest.authentication.authorisedUserDoesNotHaveAccess",
							HttpServletResponse.SC_FORBIDDEN);
				}
			}
			return proceed;
		}
	}
}
