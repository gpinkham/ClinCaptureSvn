package com.clinovo.util;

import java.util.HashMap;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.hibernate.ConfigurationDao;

public class ValidatorHelper {

	private Locale locale;
    private HttpServletRequest request;
    private FormProcessor formProcessor;
	private ConfigurationDao configurationDao;
	private HashMap<String, Object> attributes;
	private HashMap<String, String> parameters;

	public ValidatorHelper(ConfigurationDao configurationDao, Locale locale) {
		this.locale = locale;
		this.configurationDao = configurationDao;
		attributes = new HashMap<String, Object>();
		parameters = new HashMap<String, String>();
	}

	public ValidatorHelper(HttpServletRequest request, ConfigurationDao configurationDao) {
        this.request = request;
		this.locale = request.getLocale();
		this.configurationDao = configurationDao;
        formProcessor = new FormProcessor(request);
		attributes = new HashMap<String, Object>();
		parameters = new HashMap<String, String>();
	}
    
	public Locale getLocale() {
		return locale;
	}

	public ConfigurationDao getConfigurationDao() {
		return configurationDao;
	}

	public Object getAttribute(String key) {
		return request != null ? request.getAttribute(key) : attributes.get(key);
	}

	public void setAttribute(String key, Object value) {
		if (request != null) {
			request.setAttribute(key, value);
		} else {
			attributes.put(key, value);
		}
	}

	public String getParameter(String key) {
		return request != null ? request.getParameter(key) : parameters.get(key);
	}

    public String[] getParameterValues(String key) {
		return request != null ? request.getParameterValues(key) : new String[] { parameters.get(key) };
    }

    public FormProcessor getFormProcessor() {
        return formProcessor;
    }
}
