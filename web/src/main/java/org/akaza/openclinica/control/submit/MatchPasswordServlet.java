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

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.SecurityManager;
import org.akaza.openclinica.web.InsufficientPermissionException;

/**
 * Checks user's password with the one int the session
 * 
 * @author shamim
 * 
 */
@SuppressWarnings({ "serial" })
public class MatchPasswordServlet extends SecureController {
	@Override
	protected void processRequest() throws Exception {
		String password = request.getParameter("password");
		logger.info("password [" + password + "]");
		if (password != null && !password.equals("")) {
			SecurityManager securityManager = ((SecurityManager) SpringServletAccess.getApplicationContext(context)
					.getBean("securityManager"));
			UserAccountBean ub = (UserAccountBean) session.getAttribute("userBean");
			if (securityManager.isPasswordValid(ub.getPasswd(), password, getUserDetails())) {
				response.getWriter().print("true");
			} else {
				response.getWriter().print("false");
			}
			return;
		}
	}

	@Override
	protected void mayProceed() throws InsufficientPermissionException {
		return;
	}
}
