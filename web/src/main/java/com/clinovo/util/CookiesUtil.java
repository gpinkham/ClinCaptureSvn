package com.clinovo.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This util was created in order to simplify  work with cookies.
 */
public final class CookiesUtil {

	private CookiesUtil() {
	}

	/**
	 * Get cookie from request.
	 * @param request HttpServletRequest
	 * @param name String
	 * @return String
	 */
	public static String getCookie(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (name.equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	/**
	 * Add new cookie to response.
	 * @param response HttpServletResponse
	 * @param name String
	 * @param value String
	 */
	public static void addCookie(HttpServletResponse response, String name, String value) {
		Cookie cookie = new Cookie(name, value);
		response.addCookie(cookie);
	}
}
