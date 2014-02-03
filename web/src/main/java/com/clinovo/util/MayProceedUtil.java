package com.clinovo.util;

import javax.servlet.http.HttpServletRequest;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public final class MayProceedUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(MayProceedUtil.class);

	private MayProceedUtil() {
	}

	public static boolean mayProceed(HttpServletRequest request, Role... roles) {
		boolean mayProceed = false;
		StudyInfoPanel panel = new StudyInfoPanel();
		panel.reset();
		request.setAttribute("panel", panel);

		StudyUserRoleBean userRole = (StudyUserRoleBean) request.getSession().getAttribute("userRole");
		UserAccountBean userBean = (UserAccountBean) request.getSession().getAttribute("userBean");

		request.setAttribute("userRole", userRole);
		request.setAttribute("userBean", userBean);

		ResourceBundleProvider.updateLocale(request.getLocale());

		for (Role role : roles) {
			if (role.equals(userRole.getRole())) {
				mayProceed = true;
				break;
			}
		}

		return mayProceed;
	}
}
