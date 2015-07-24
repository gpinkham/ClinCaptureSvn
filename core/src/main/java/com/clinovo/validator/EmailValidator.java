package com.clinovo.validator;

import java.util.regex.Pattern;

/**
 * EmailValidator.
 */
public final class EmailValidator {

	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	private EmailValidator() {
	}

	/**
	 * Validate hex with regular expression.
	 *
	 * @param hex
	 *            hex for validation
	 * @return true valid hex, false invalid hex
	 */
	public static boolean validate(final String hex) {
		return Pattern.compile(EMAIL_PATTERN).matcher(hex).matches();
	}
}