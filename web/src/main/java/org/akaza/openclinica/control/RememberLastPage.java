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

package org.akaza.openclinica.control;

import java.util.HashMap;
import java.util.Map;

import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.view.Page;

@SuppressWarnings({"rawtypes", "unchecked",  "serial"})
public abstract class RememberLastPage extends SecureController {

	public static final String STORED_ATTRIBUTES = "RememberLastPage_storedAttributes";

	protected abstract String getUrlKey();

	protected abstract String getDefaultUrl();

	protected abstract boolean userDoesNotUseJmesaTableForNavigation();

	protected void analyzeUrl() {
		String key = getUrlKey();
		String defaultUrl = getDefaultUrl();
		if (request.getMethod().equalsIgnoreCase("POST") || !userDoesNotUseJmesaTableForNavigation()) {
			request.getSession().setAttribute(
					key,
					request.getMethod().equalsIgnoreCase("GET") ? (request.getRequestURL() + "?" + request
							.getQueryString()) : (defaultUrl != null ? (request.getRequestURL() + defaultUrl) : null));
		} else {
			String keyValue = (String) request.getSession().getAttribute(key);
			if (keyValue == null && defaultUrl != null) {
				keyValue = request.getRequestURL() + defaultUrl;
				request.getSession().setAttribute(key, keyValue);
			}
		}
	}

	protected void analyzeForward(Page page) throws Exception {
		String keyValue = (String) request.getSession().getAttribute(getUrlKey());
		if (userDoesNotUseJmesaTableForNavigation() && keyValue != null) {
			storeAttributes();
			//for navigation purpose (to brake double url in stack)
			session.setAttribute("skipURL", "true");
			
			response.sendRedirect(response.encodeRedirectURL(keyValue));
		} else {		
			restoreAttributes();
			forwardPage(page);
		}
	}

	private void storeAttributes() {
		Map storedAttributes = new HashMap();
		storedAttributes.put(SecureController.PAGE_MESSAGE, request.getAttribute(SecureController.PAGE_MESSAGE));
		request.getSession().setAttribute(STORED_ATTRIBUTES, storedAttributes);
	}

	private void restoreAttributes() {
		Map<String, Object> storedAttributes = (Map) request.getSession().getAttribute(STORED_ATTRIBUTES);
		request.getSession().removeAttribute(STORED_ATTRIBUTES);
		if (storedAttributes != null) {
			request.setAttribute(SecureController.PAGE_MESSAGE, storedAttributes.get(SecureController.PAGE_MESSAGE));
		}
	}
}
