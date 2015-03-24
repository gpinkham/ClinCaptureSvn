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

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.core.CoreResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.jstl.core.Config;
import java.util.Locale;

/**
 * SessionUtil.
 */
@SuppressWarnings("unused")
public final class SessionUtil {

	public static final String STUDY = "study";
	public static final String CURRENT_USER = "userBean";
	private static final Logger LOGGER = LoggerFactory.getLogger(SessionUtil.class);
	public static final String CURRENT_SESSION_LOCALE = "current.session.locale";

	private SessionUtil() {
	}

	/**
	 * Method that updates session attributes.
	 * 
	 * @param coreResources
	 *            CoreResources
	 * @param localeResolver
	 *            SessionLocaleResolver
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 */
	public static void updateSession(CoreResources coreResources, SessionLocaleResolver localeResolver,
			HttpServletRequest request, HttpServletResponse response) {
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
		updateLocale(request, response, localeResolver, new Locale(CoreResources.getSystemLanguage()));
	}

	/**
	 * Method that updates locale.
	 *
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param localeResolver
	 *            SessionLocaleResolver
	 * @param locale
	 *            Locale
	 */
	public static void updateLocale(HttpServletRequest request, HttpServletResponse response,
			SessionLocaleResolver localeResolver, Locale locale) {
		updateLocale(request.getSession(), locale);
		localeResolver.setLocale(request, response, locale);
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
		request.getSession().setAttribute(CURRENT_SESSION_LOCALE, locale);
		Config.set(request.getSession(), Config.FMT_LOCALE, locale);
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
		return (Locale) request.getSession().getAttribute(CURRENT_SESSION_LOCALE);
	}

	/**
	 * Method return locale.
	 * 
	 * @param session
	 *            HttpSession
	 * @return Locale
	 */
	public static Locale getLocale(HttpSession session) {
		return (Locale) session.getAttribute(CURRENT_SESSION_LOCALE);
	}

	public static StudyBean getCurrentStudy(HttpServletRequest request) {
		return (StudyBean) request.getSession().getAttribute(STUDY);
	}

	public static UserAccountBean getCurrentUser(HttpServletRequest request) {
		return (UserAccountBean) request.getSession().getAttribute(CURRENT_USER);
	}
}
