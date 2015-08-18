package com.clinovo.spring;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Ajax authentication failure handler.
 */
public class AjaxAuthenticationFailureHandler extends ExceptionMappingAuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		if ("true".equals(request.getHeader("X-Ajax-call"))) {
			String action = "error";
			if (exception instanceof LockedException) {
				action = "locked";
			} else if (exception instanceof BadCredentialsException) {
				action = "bad_credentials";
			}
			response.getWriter().print(action);
			response.getWriter().flush();
		} else {
			super.onAuthenticationFailure(request, response, exception);
		}
	}
}
