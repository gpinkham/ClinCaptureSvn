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
 * Created on Sep 28, 2005
 *
 *
 */
package org.akaza.openclinica.control.login;

import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

/**
 * @author thickerson
 * 
 * 
 */
@SuppressWarnings("serial")
public class EnterpriseServlet extends SecureController {

	@Override
	public void mayProceed() throws InsufficientPermissionException {

	}

	@Override
	public void processRequest() throws Exception {
		resetPanel();
		panel.setStudyInfoShown(false);
		panel.setOrderedData(true);
		setToPanel("", "");
		forwardPage(Page.ENTERPRISE);
	}

}
