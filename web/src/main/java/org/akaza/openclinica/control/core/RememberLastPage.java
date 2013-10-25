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

    protected abstract String getUrlKey(HttpServletRequest request);

	protected abstract String getDefaultUrl(HttpServletRequest request);

	protected abstract boolean userDoesNotUseJmesaTableForNavigation(HttpServletRequest request);

	private boolean redirect(HttpServletRequest request, HttpServletResponse response) throws Exception {
		storeAttributes(request);
		// for navigation purpose (to brake double url in stack)
		request.getSession().setAttribute("skipURL", "true");
		response.sendRedirect(response.encodeRedirectURL((String) request.getSession().getAttribute(getUrlKey(request))));
		return true;
	}

	protected boolean shouldRedirect(HttpServletRequest request, HttpServletResponse response) throws Exception {
		boolean result = false;
		String key = getUrlKey(request);
		String defaultUrl = getDefaultUrl(request);
		String keyValue = (String) request.getSession().getAttribute(key);
		if (keyValue == null && defaultUrl != null) {
			request.getSession().setAttribute(key, request.getRequestURL() + defaultUrl);
		}
		if (request.getMethod().equalsIgnoreCase("POST")) {
			if (request.getHeader(REFERER) != null
					&& !request.getHeader(REFERER).toLowerCase()
							.startsWith(request.getRequestURL().toString().toLowerCase())) {
				result = redirect(request, response);
			} else if (defaultUrl != null) {
				request.getSession().setAttribute(key, request.getRequestURL() + defaultUrl);
			}
		} else if (request.getMethod().equalsIgnoreCase("GET")) {
			if (userDoesNotUseJmesaTableForNavigation(request)) {
				result = redirect(request, response);
			} else {
				if (request.getQueryString() != null) {
					request.getSession().setAttribute(key, request.getRequestURL() + "?" + request.getQueryString());
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
