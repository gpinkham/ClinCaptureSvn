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

package org.akaza.openclinica.controller.helper;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * An "interceptor" class that sets up a UserAccount and stores it in the Session, before another class is initialized
 * and potentially uses that UserAccount.
 */
public class SetUpUserInterceptor extends HandlerInterceptorAdapter {

	public static final String USER_BEAN_NAME = "userBean";

	@Autowired
	@Qualifier("dataSource")
	private DataSource dataSource;

	@Override
	public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o)
			throws Exception {

		Locale locale = ResourceBundleProvider.localeMap.get(Thread.currentThread());
		if (locale == null) {
			ResourceBundleProvider.updateLocale(httpServletRequest.getLocale());
		}

		// Set up the user account bean: check the Session first
		HttpSession currentSession = httpServletRequest.getSession();
		UserAccountBean userBean = (UserAccountBean) currentSession.getAttribute("userBean");
		String userName;
		boolean userBeanIsInvalid;
		UserAccountDAO userAccountDAO = new UserAccountDAO(dataSource);

		if (userBean == null) {

			userName = httpServletRequest.getRemoteUser();
			userBeanIsInvalid = userName == null || "".equalsIgnoreCase(userName);
			if (!userBeanIsInvalid) {
				userBean = (UserAccountBean) userAccountDAO.findByUserName(userName);
				userBeanIsInvalid = (userBean == null);
				if (!userBeanIsInvalid) {
					currentSession.setAttribute(USER_BEAN_NAME, userBean);
				}

			}
		}

		// The user bean could still be null at this point
		if (userBean == null) {
			userBean = new UserAccountBean();
			userBean.setName("unknown");
			currentSession.setAttribute(USER_BEAN_NAME, userBean);
		}

		userBean = userBean.getId() > 0 ? (UserAccountBean) userAccountDAO.findByPK(userBean.getId()) : userBean;

		SetUpStudyRole setupStudy = new SetUpStudyRole(dataSource);
		setupStudy.setUp(currentSession, userBean);

		return true;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}
