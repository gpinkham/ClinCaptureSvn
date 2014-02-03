package com.clinovo.interceptor;

import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.web.SQLInitServlet;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SetUpSessionInterceptor extends HandlerInterceptorAdapter {

	public static final String INCLUDE_REPORTING = "includeReporting";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		StudyUserRoleBean userRole = (StudyUserRoleBean) request.getSession().getAttribute("userRole");
		UserAccountBean userBean = (UserAccountBean) request.getSession().getAttribute("userBean");
		StudyBean currentStudy = (StudyBean) request.getSession().getAttribute("study");
		boolean ok = userBean != null && userRole != null && currentStudy != null;
		if (!ok) {
			response.sendRedirect(request.getContextPath() + "/MainMenu");
		}
		request.setAttribute(INCLUDE_REPORTING, !SQLInitServlet.getField("pentaho.url").trim().equals(""));
		return ok;
	}
}
