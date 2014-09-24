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

package com.clinovo.interceptor;

import com.clinovo.controller.Redirection;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.BaseController;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.navigation.Navigation;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.akaza.openclinica.web.SQLInitServlet;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * SetUpSessionInterceptor that able to manage controller requests.
 */
public class SetUpSessionInterceptor extends HandlerInterceptorAdapter {

	public static final String PREV_URL = "_PrevUrl";
	public static final String FIRST_URL_PARAMETER = "?";
	public static final String INCLUDE_REPORTING = "includeReporting";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		boolean ok = true;
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			Controller.restorePageMessages(request);
			Navigation.addToNavigationStack(request);
			StudyUserRoleBean userRole = (StudyUserRoleBean) request.getSession()
					.getAttribute(BaseController.USER_ROLE);
			UserAccountBean userBean = (UserAccountBean) request.getSession().getAttribute(
					BaseController.USER_BEAN_NAME);
			StudyBean currentStudy = (StudyBean) request.getSession().getAttribute(BaseController.STUDY);
			ok = userBean != null && userRole != null && currentStudy != null;
			ResourceBundleProvider.updateLocale(request.getLocale());
			request.setAttribute(BaseController.USER_ROLE, userRole);
			request.setAttribute(BaseController.STUDY, currentStudy);
			request.setAttribute(BaseController.USER_BEAN_NAME, userBean);
			request.getSession().setAttribute(BaseController.STUDY_INFO_PANEL, new StudyInfoPanel());
			request.setAttribute(INCLUDE_REPORTING, !SQLInitServlet.getField("pentaho.url").trim().equals(""));
			if (!ok) {
				response.sendRedirect(request.getContextPath() + "/MainMenu");
			} else {
				ok = checkForRedirection(request, response, handler);
			}
		}
		return ok;
	}

	private boolean checkForRedirection(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		boolean result = true;
		if (handler instanceof Redirection) {
			String queryString = request.getQueryString();
			String key = handler.getClass().getName().concat(PREV_URL);
			String prevQueryString = (String) request.getSession().getAttribute(key);
			if (prevQueryString != null && queryString == null) {
				response.sendRedirect(request.getContextPath().concat(((Redirection) handler).getUrl())
						.concat(FIRST_URL_PARAMETER).concat(prevQueryString));
				result = false;
			} else {
				request.getSession().setAttribute(key, queryString);
			}
		}
		return result;
	}
}
