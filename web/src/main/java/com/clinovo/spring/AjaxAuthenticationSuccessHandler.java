package com.clinovo.spring;

import org.akaza.openclinica.control.core.OCServletFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Ajax authentication handler.
 */
public class AjaxAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		if ("true".equals(request.getHeader("X-Ajax-call"))) {
			OCServletFilter.createSessionLifetimeCookie(request, response, false);
			response.getWriter().print("success");
			response.getWriter().flush();
		} else {
			super.onAuthenticationSuccess(request, response, authentication);
		}
	}
}
