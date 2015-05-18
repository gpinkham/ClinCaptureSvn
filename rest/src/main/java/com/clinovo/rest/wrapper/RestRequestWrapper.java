package com.clinovo.rest.wrapper;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * RestRequestWrapper.
 */
@SuppressWarnings("unchecked")
public class RestRequestWrapper extends HttpServletRequestWrapper {

	private Map<String, String[]> allParameters = null;

	/**
	 * Constructs a request object wrapping the given request.
	 *
	 * @param request
	 *            HttpServletRequest
	 * @throws IllegalArgumentException
	 *             if the request is null
	 */
	public RestRequestWrapper(HttpServletRequest request) {
		super(request);
		allParameters = new TreeMap<String, String[]>();
		allParameters.putAll(super.getParameterMap());
	}

	/**
	 * Adds new parameter.
	 * 
	 * @param name
	 *            String
	 * @param value
	 *            String
	 */
	public void addParameter(String name, String value) {
		Map<String, String[]> newAllParameters = new TreeMap<String, String[]>();
		newAllParameters.putAll(allParameters);
		newAllParameters.put(name, new String[]{value});
		allParameters = new TreeMap<String, String[]>();
		allParameters.putAll(newAllParameters);
	}

	@Override
	public String getParameter(final String name) {
		String[] strings = getParameterMap().get(name);
		if (strings != null) {
			return strings[0];
		}
		return super.getParameter(name);
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return Collections.unmodifiableMap(allParameters);
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return Collections.enumeration(getParameterMap().keySet());
	}

	@Override
	public String[] getParameterValues(final String name) {
		return getParameterMap().get(name);
	}
}