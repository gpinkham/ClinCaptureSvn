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

import org.akaza.openclinica.control.core.CoreSecureController;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

/**
 * Performs Log out action
 * 
 * @author jxu
 * 
 */
@SuppressWarnings({ "serial" })
public class LogoutServlet extends SecureController {

	@Override
	public void mayProceed() throws InsufficientPermissionException {

	}

	@Override
	public void processRequest() throws Exception {
		sm = null;// set sm to null after user logs out
		logger.info("User  : {} , email address : {} Logged Out ", ub.getName(), ub.getEmail());
		removeLockedCRF(ub.getId());
		CoreSecureController.removeLockedCRF(ub.getId());
		session.removeAttribute("userBean");
		session.removeAttribute("study");
		session.removeAttribute("userRole");
		session.removeAttribute("passwordExpired");
		session.invalidate();
		forwardPage(Page.LOGOUT, false);
	}

}
