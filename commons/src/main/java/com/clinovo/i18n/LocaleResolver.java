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

package com.clinovo.i18n;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.jstl.core.Config;

import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * LocaleResolver.
 */
@SuppressWarnings("unused")
public class LocaleResolver implements org.springframework.web.servlet.LocaleResolver {

	private static final Logger LOGGER = LoggerFactory.getLogger(LocaleResolver.class);

	public static final String REST_API_LOCALE = "rest.api.locale";

	public static final String CURRENT_SESSION_LOCALE = "current.session.locale";

	/**
	 * Method that updates session attributes.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param coreResources
	 *            CoreResources
	 */
	public static void updateSession(HttpServletRequest request, CoreResources coreResources) {
		HttpSession session = request.getSession();
		for (Object key : coreResources.getDataInfo().keySet()) {
			if (key instanceof String) {
				Object value = session.getAttribute((String) key);
				if (value != null) {
					session.setAttribute((String) key, CoreResources.getField((String) key));
				}
			}
		}
		session.setAttribute("newThemeColor", CoreResources.getField("themeColor"));
		updateLocale(request, CoreResources.getSystemLocale());
	}

	/**
	 * Method that updates locale.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param locale
	 *            Locale
	 */
	public static void updateLocale(HttpServletRequest request, Locale locale) {
		updateLocale(request.getSession(), locale);
	}

	/**
	 * Method that updates locale.
	 *
	 * @param session
	 *            HttpSession
	 * @param locale
	 *            Locale
	 */
	public static void updateLocale(HttpSession session, Locale locale) {
		session.setAttribute(CURRENT_SESSION_LOCALE, locale);
		Config.set(session, Config.FMT_LOCALE, locale);
	}

	/**
	 * Method return locale.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return Locale
	 */
	public static Locale getLocale(HttpServletRequest request) {
		Locale locale = (Locale) request.getAttribute(REST_API_LOCALE);
		if (locale == null) {
			locale = (Locale) request.getSession().getAttribute(CURRENT_SESSION_LOCALE);
			if (locale == null) {
				locale = CoreResources.getSystemLocale();
				updateLocale(request, locale);
			}
		}
		ResourceBundleProvider.updateLocale(locale);
		return locale;
	}

	/**
	 * Method resolves rest api locale.
	 *
	 */
	public static void resolveRestApiLocale() {
		((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().setAttribute(
				LocaleResolver.REST_API_LOCALE, CoreResources.getSystemLocale());
	}

	/**
	 * Method return locale.
	 *
	 * @return Locale
	 */
	public static Locale getLocale() {
		return getLocale(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest());
	}

	/**
	 * Method resolves locale.
	 *
	 */
	public static void resolveLocale() {
		getLocale();
	}

	/**
	 * {@inheritDoc}
	 */
	public Locale resolveLocale(HttpServletRequest request) {
		return getLocale();
	}

	/**
	 * {@inheritDoc}
	 */
	public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		updateLocale(request, locale);
	}
}
