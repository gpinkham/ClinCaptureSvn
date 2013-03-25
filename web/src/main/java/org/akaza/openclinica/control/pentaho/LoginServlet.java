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

package org.akaza.openclinica.control.pentaho;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.SQLInitServlet;
import org.pentaho.platform.web.http.security.CryptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

@SuppressWarnings("serial")
public class LoginServlet extends SecureController {

	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());

	public static final String PARTNER = "clincapture";
	public static final String PENTAHO_URL = "pentaho.url";

	@Override
	protected void processRequest() throws Exception {
		UserAccountBean ub = (UserAccountBean) session.getAttribute(USER_BEAN_NAME);
		if (ub != null) {
			UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
			ub.setPentahoUserSession(request.getSession().getId());
			ub.setPentahoTokenDate(new Date());
			udao.updatePentahoAutoLoginParams(ub);
			response.sendRedirect(SQLInitServlet.getField(PENTAHO_URL) + "?partner=" + PARTNER + "&token="
					+ CryptUtil.generateToken(ub.getId(), ub.getPentahoUserSession()) + "&ts=" + new Date().getTime());
		}
	}

	@Override
	protected void mayProceed() throws InsufficientPermissionException {
		//
	}
}
