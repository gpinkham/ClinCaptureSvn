package com.clinovo.util;

import org.akaza.openclinica.dao.core.CoreResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;

@SuppressWarnings("unused")
public final class SessionUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(SessionUtil.class);

	private SessionUtil() {
	}

	public static void updateSession(CoreResources coreResources, HttpSession session) {
		for (Object key : coreResources.getDataInfo().keySet()) {
			if (key instanceof String) {
				Object value = session.getAttribute((String) key);
				if (value != null) {
					session.setAttribute((String) key, CoreResources.getField((String) key));
				}
			}
		}
		session.setAttribute("newThemeColor", CoreResources.getField("themeColor"));
	}
}
