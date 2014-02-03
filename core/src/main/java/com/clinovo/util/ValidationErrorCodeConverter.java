package com.clinovo.util;

import org.springmodules.validation.bean.converter.ErrorCodeConverter;

/**
 * Error codes converter (user to convert validation error codes).
 */
@SuppressWarnings("rawtypes")
public class ValidationErrorCodeConverter implements ErrorCodeConverter {
	/**
	 * Get error code for validation.
	 * 
	 * @param s
	 *            default error code
	 * @return error code
	 */
	private String getErrorCode(final String s) {
		return s;
	}

	public String convertGlobalErrorCode(final String s, final Class aClass) {
		return getErrorCode(s);
	}

	public String convertPropertyErrorCode(final String s, final Class aClass, final String s1) {
		return getErrorCode(s);
	}
}
