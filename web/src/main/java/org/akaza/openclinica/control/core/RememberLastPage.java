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

package org.akaza.openclinica.control.core;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.akaza.openclinica.view.Page;

@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
public abstract class RememberLastPage extends Controller {

	protected abstract String getUrlKey();

	protected abstract String getDefaultUrl(HttpServletRequest request);

	protected abstract boolean userDoesNotUseJmesaTableForNavigation(HttpServletRequest request);

	protected boolean shouldRedirect(HttpServletRequest request, HttpServletResponse response) throws Exception {
		boolean result = false;
		String keyValue = (String) request.getSession().getAttribute(getUrlKey());
		if (userDoesNotUseJmesaTableForNavigation(request) && keyValue != null) {
			result = true;
			storeAttributes(request);
			// for navigation purpose (to brake double url in stack)
			request.getSession().setAttribute("skipURL", "true");
			response.sendRedirect(response.encodeRedirectURL(keyValue));
		} else {
			String key = getUrlKey();
			String defaultUrl = getDefaultUrl(request);
			if (request.getMethod().equalsIgnoreCase("POST") || !userDoesNotUseJmesaTableForNavigation(request)) {
				request.getSession().setAttribute(
						key,
						request.getMethod().equalsIgnoreCase("GET") ? (request.getRequestURL() + "?" + request
								.getQueryString()) : (defaultUrl != null ? (request.getRequestURL() + defaultUrl)
								: null));
			} else {
				keyValue = (String) request.getSession().getAttribute(key);
				if (keyValue == null && defaultUrl != null) {
					keyValue = request.getRequestURL() + defaultUrl;
					request.getSession().setAttribute(key, keyValue);
				}
			}
		}
		return result;
	}

	protected void forward(Page page, HttpServletRequest request, HttpServletResponse response) throws Exception {
		restoreAttributes(request);
		forwardPage(page, request, response);
	}

	private void storeAttributes(HttpServletRequest request) {
		Map storedAttributes = new HashMap();
		storedAttributes.put(PAGE_MESSAGE, request.getAttribute(PAGE_MESSAGE));
		request.getSession().setAttribute(STORED_ATTRIBUTES, storedAttributes);
	}

	private void restoreAttributes(HttpServletRequest request) {
		Map<String, Object> storedAttributes = (Map) request.getSession().getAttribute(STORED_ATTRIBUTES);
		request.getSession().removeAttribute(STORED_ATTRIBUTES);
		if (storedAttributes != null) {
			request.setAttribute(PAGE_MESSAGE, storedAttributes.get(PAGE_MESSAGE));
		}
	}
}
