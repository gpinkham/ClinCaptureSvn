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
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.submit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.core.SecurityManager;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

/**
 * Checks user's password with the one int the session
 * 
 * @author shamim
 * 
 */
@SuppressWarnings({ "serial" })
@Component
public class MatchPasswordServlet extends Controller {
	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String password = request.getParameter("password");
		logger.info("password [" + password + "]");
		if (password != null && !password.equals("")) {
			SecurityManager securityManager = getSecurityManager();
			UserAccountBean ub = (UserAccountBean) request.getSession().getAttribute("userBean");
			if (securityManager.isPasswordValid(ub.getPasswd(), password, getUserDetails())) {
				response.getWriter().print("true");
			} else {
				response.getWriter().print("false");
			}
			return;
		}
	}

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response) throws InsufficientPermissionException {
		return;
	}
}
