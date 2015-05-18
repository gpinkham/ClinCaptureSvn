package com.clinovo.rest.util;

import java.util.HashMap;
import java.util.List;

import com.clinovo.rest.exception.RestException;

/**
 * ValidatorUtil.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public final class ValidatorUtil {

	public static final String DELIMITER = " -> ";

	private ValidatorUtil() {
	}

	/**
	 * Method that throws new REST Exception.
	 * 
	 * @param errors
	 *            HashMap
	 * @throws Exception
	 *             an Exception
	 */
	public static void checkForErrors(HashMap errors) throws Exception {
		if (!errors.isEmpty()) {
			String key = (String) errors.keySet().iterator().next();
			throw new RestException(key.concat(DELIMITER).concat(((List<String>) errors.get(key)).get(0)));
		}
	}
}
