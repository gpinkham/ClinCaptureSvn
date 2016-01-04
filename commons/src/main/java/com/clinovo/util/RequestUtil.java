/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 *
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 *
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use.
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO'S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/

package com.clinovo.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * RequestUtil.
 */
public final class RequestUtil {

	public static final String PAGE_MESSAGE = "pageMessages";
	public static final String STORED_ATTRIBUTES = "RememberLastPage_storedAttributes";

	private RequestUtil() {
	}

	/**
	 * Method returns request for current thread.
	 * 
	 * @return HttpServletRequest
	 */
	public static HttpServletRequest getRequest() {
		return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
	}

	/**
	 * Returns relative url with context path.
	 *
	 * @param withContext
	 *            boolean
	 * @return String
	 */
	public static String getRelative(boolean withContext) {
		HttpServletRequest request = RequestUtil.getRequest();
		String queryString = request.getQueryString();
		return (withContext ? request.getContextPath() : "").concat(request.getRequestURI()).concat("?")
				.concat(queryString == null ? "" : queryString);
	}

	/**
	 * Returns relative url without context path.
	 *
	 * @return String
	 */
	public static String getRelative() {
		return getRelative(false);
	}

	/**
	 * Returns relative url with context path and with new parameters.
	 *
	 * @param withContext
	 *            boolean
	 * @param pairs
	 *            String...
	 * @return String
	 */
	public static String getRelativeWithNewParameters(boolean withContext, String... pairs) {
		String url = getRelative(withContext);
		if (pairs != null) {
			for (String pair : pairs) {
				pair = pair.trim();
				if (pair.indexOf("=") >= 1) {
					String[] pairArray = pair.split("=");
					if (url.contains("&".concat(pairArray[0]).concat("="))) {
						url = url.replaceAll("&".concat(pairArray[0]).concat("=[^&.]*"), "&".concat(pair));
					} else if (url.contains("?".concat(pairArray[0]).concat("="))) {
						url = url.replaceAll("\\?".concat(pairArray[0]).concat("=[^&.]*"), "?".concat(pair));
					} else {
						url = url.concat(url.contains("?") ? "&" : "?").concat(pair);
					}
				}
			}
		}
		return url;
	}

	/**
	 * Returns relative url without context path and with new parameters.
	 *
	 * @param pairs
	 *            String...
	 * @return String
	 */
	public static String getRelativeWithNewParameters(String... pairs) {
		return getRelativeWithNewParameters(false, pairs);
	}

	/**
	 * Save page message to the session.
	 * @param request HttpServletRequest
	 * @param message String
	 */
	@SuppressWarnings("unchecked")
	public static void storePageMessage(HttpServletRequest request, String message) {
		Map<String, Object> storedAttributes = new HashMap<String, Object>();
		ArrayList<String> pageMessages = (ArrayList<String>) request.getAttribute(PAGE_MESSAGE);
		pageMessages = pageMessages == null ? new ArrayList<String>() : pageMessages;
		pageMessages.add(message);
		storedAttributes.put(PAGE_MESSAGE, pageMessages);
		request.getSession().setAttribute(STORED_ATTRIBUTES, storedAttributes);
	}

}
