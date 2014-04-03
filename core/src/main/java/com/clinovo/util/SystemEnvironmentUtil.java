package com.clinovo.util;

import java.io.File;

public final class SystemEnvironmentUtil {

	private SystemEnvironmentUtil() {
	}

	public static String getCatalinaHome() {
		String tomcatHome = System.getProperty("CATALINA_HOME");
		if (tomcatHome == null) {
			tomcatHome = System.getProperty("catalina.home");
		}
		if (tomcatHome == null) {
			tomcatHome = System.getenv("CATALINA_HOME");
		}
		if (tomcatHome == null) {
			tomcatHome = System.getenv("catalina.home");
		}
		return tomcatHome + File.separator;
	}
}
