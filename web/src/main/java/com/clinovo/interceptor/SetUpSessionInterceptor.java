package com.clinovo.interceptor;

import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.BaseController;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.navigation.Navigation;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.akaza.openclinica.web.SQLInitServlet;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SetUpSessionInterceptor extends HandlerInterceptorAdapter {

	public static final String INCLUDE_REPORTING = "includeReporting";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		boolean ok = true;
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
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
			}
		}
		return ok;
	}
}
