/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License 
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

/**
 * 
 */
package org.akaza.openclinica.control.core;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.log.LoggingConstants;
import org.slf4j.MDC;

/**
 * @author pgawade
 * 
 */
public class OCServletFilter implements javax.servlet.Filter {

	public static final String USER_BEAN_NAME = "userBean";

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		UserAccountBean ub = (UserAccountBean) req.getSession().getAttribute(USER_BEAN_NAME);
		boolean successfulRegistration = false;
		String username = "";

		Principal principal = req.getUserPrincipal();

		if ((ub != null) && (null != ub.getName()) && (!ub.getName().equals(""))) {
			username = ub.getName();
			successfulRegistration = registerUsernameWithLogContext(username);
		} else if (principal != null) {
			username = principal.getName();
			successfulRegistration = registerUsernameWithLogContext(username);
		}

		try {
			chain.doFilter(request, response);
		} finally {
			if (successfulRegistration) {
				MDC.remove(LoggingConstants.USERNAME);
			}
		}
	}

	public void init(FilterConfig arg0) throws ServletException {
	}

	public void destroy() {
		// FIXME close logging here?
	}

	/**
	 * Register the user in the MDC under USERNAME.
	 * 
	 * @param username
	 * @return true id the user can be successfully registered
	 */
	private boolean registerUsernameWithLogContext(String username) {
		if (username != null && username.trim().length() > 0) {
			MDC.put(LoggingConstants.USERNAME, username);
			return true;
		}
		return false;
	}

}
