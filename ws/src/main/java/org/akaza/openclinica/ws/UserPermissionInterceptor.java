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

package org.akaza.openclinica.ws;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapMessage;

import java.util.Locale;

import javax.sql.DataSource;

public class UserPermissionInterceptor implements EndpointInterceptor {

	private final DataSource dataSource;

	public UserPermissionInterceptor(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public boolean handleRequest(MessageContext messageContext, Object endpoint) throws Exception {
		ResourceBundleProvider.updateLocale(new Locale("en_US"));
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = null;
		if (principal instanceof UserDetails) {
			username = ((UserDetails) principal).getUsername();
		} else {
			username = principal.toString();
		}
		UserAccountDAO userAccountDao = new UserAccountDAO(dataSource);
		UserAccountBean userAccountBean = ((UserAccountBean) userAccountDao.findByUserName(username));
		Boolean result = userAccountBean.getRunWebservices();
		if (!result) {
			SoapBody response = ((SoapMessage) messageContext.getResponse()).getSoapBody();
			response.addClientOrSenderFault(
					"Authorization is required to execute SOAP web services with this account.Please contact your administrator.",
					Locale.ENGLISH);
			return false;

		} else {
			return result;
		}
	}

	public boolean handleResponse(MessageContext messageContext, Object endpoint) throws Exception {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean handleFault(MessageContext messageContext, Object endpoint) throws Exception {
		// TODO Auto-generated method stub
		return true;
	}

}
