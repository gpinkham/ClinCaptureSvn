package com.clinovo.rest.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.clinovo.rest.wrapper.RestRequestWrapper;

/**
 * RestFilter.
 */
public class RestFilter implements Filter {

	/**
	 * Filter init method.
	 *
	 * @param filterConfig
	 *            FilterConfig
	 * @throws ServletException
	 *             the ServletException
	 */
	public void init(FilterConfig filterConfig) throws ServletException {
		//
	}

	/**
	 * Filter method implementation.
	 *
	 * @param request
	 *            ServletRequest
	 * @param response
	 *            ServletResponse
	 * @param chain
	 *            FilterChain
	 * @throws IOException
	 *             the IOException
	 * @throws ServletException
	 *             the ServletException
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		chain.doFilter(new RestRequestWrapper((HttpServletRequest) request), response);
	}

	/**
	 * Destroy method.
	 */
	public void destroy() {
		//
	}
}
