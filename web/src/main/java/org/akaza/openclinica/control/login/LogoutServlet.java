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
package org.akaza.openclinica.control.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

/**
 * Performs Log out action
 * 
 * @author jxu
 * 
 */
@SuppressWarnings({ "serial" })
@Component
public class LogoutServlet extends Controller {

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response) throws InsufficientPermissionException {
        //
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserAccountBean ub = getUserAccountBean(request);
        request.setAttribute(SESSION_MANAGER, null);
		logger.info("User  : {} , email address : {} Logged Out ", ub.getName(), ub.getEmail());
		removeLockedCRF(ub.getId());
        request.getSession().removeAttribute("userBean");
        request.getSession().removeAttribute("study");
        request.getSession().removeAttribute("userRole");
        request.getSession().removeAttribute("passwordExpired");
        request.getSession().invalidate();
		forwardPage(Page.LOGOUT, false, request, response);
	}

}
