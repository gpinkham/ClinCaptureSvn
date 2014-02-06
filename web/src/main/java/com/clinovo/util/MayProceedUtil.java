package com.clinovo.util;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.control.core.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

@SuppressWarnings("unused")
public final class MayProceedUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(MayProceedUtil.class);

	private MayProceedUtil() {
	}

	public static boolean mayProceed(HttpServletRequest request, Role... roles) {
		boolean mayProceed = false;
		StudyUserRoleBean userRole = (StudyUserRoleBean) request.getSession().getAttribute(BaseController.USER_ROLE);
		for (Role role : roles) {
			if (role.equals(userRole.getRole())) {
				mayProceed = true;
				break;
			}
		}

		return mayProceed;
	}
}
