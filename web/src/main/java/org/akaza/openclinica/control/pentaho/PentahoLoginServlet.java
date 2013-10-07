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

package org.akaza.openclinica.control.pentaho;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.SQLInitServlet;
import org.pentaho.platform.web.http.security.CryptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@Component
public class PentahoLoginServlet extends Controller {

	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());

	public static final String PARTNER = "clincapture";
	public static final String PENTAHO_URL = "pentaho.url";

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = (UserAccountBean) request.getSession().getAttribute(USER_BEAN_NAME);
		if (ub != null) {
			UserAccountDAO udao = new UserAccountDAO(getDataSource());
			ub.setPentahoUserSession(request.getSession().getId());
			ub.setPentahoTokenDate(new Date());
			udao.updatePentahoAutoLoginParams(ub);
			response.sendRedirect(SQLInitServlet.getField(PENTAHO_URL) + "?partner=" + PARTNER + "&token="
					+ CryptUtil.generateToken(ub.getId(), ub.getPentahoUserSession()) + "&ts=" + new Date().getTime());
		}
	}

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response) throws InsufficientPermissionException {
		//
	}
}
