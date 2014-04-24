package com.clinovo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegexpUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(RegexpUtil.class);

	private RegexpUtil() {
	}

	public static String parseGroup(String value, String pattern, int group) {
		String result = value;
		try {
			Matcher m = Pattern.compile(pattern).matcher(value);
			result = m.find() ? m.group(group) : value;
		} catch (Exception ex) {
			LOGGER.error("Error has occurred.", ex);
		}
		return result;
	}
}
