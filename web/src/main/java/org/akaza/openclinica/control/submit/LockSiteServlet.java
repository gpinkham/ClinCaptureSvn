package org.akaza.openclinica.control.submit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.BaseController;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

/**
 * LockSiteServlet.
 */
@SuppressWarnings({"rawtypes", "unchecked", "serial"})
@Component
public class LockSiteServlet extends Controller {

	public static final String REFERER_URL = "refererUrl";
	public static final String REFERER = "referer";
	public static final String LOCK_SITE = "LockSite";

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin()) {
			return;
		}

		if (mayViewData(ub, currentRole)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("may_not_submit_data"),
				"1");
	}

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);

		String referer = request.getHeader(REFERER);
		if (referer != null && !referer.contains(LOCK_SITE)) {
			request.getSession().setAttribute(REFERER_URL, referer);
		}

		String action = request.getParameter("action");
		int siteId = Integer.parseInt(request.getParameter("id"));

		StudyDAO studyDao = getStudyDAO();
		StudyBean site = (StudyBean) studyDao.findByPK(siteId);

		checkRoleByUserAndStudy(request, response, ub, site.getParentStudyId(), site.getId());

		StudyParameterValueDAO spvdao = getStudyParameterValueDAO();
		ArrayList configs = spvdao.findParamConfigByStudy(site);
		site.setStudyParameters(configs);

		String parentStudyName = "";
		if (site.getParentStudyId() > 0) {
			StudyBean parent = (StudyBean) studyDao.findByPK(site.getParentStudyId());
			parentStudyName = parent.getName();
		}

		request.setAttribute("parentName", parentStudyName);
		request.setAttribute("siteToView", site);
		request.setAttribute("studyBean", site);
		request.setAttribute("action", action);

		if (request.getParameter("Submit") != null) {
			String message = "";
			if (action.equalsIgnoreCase("lock")) {
				message = resword.getString("lockSiteStudySubjectsResultMsg");
				getStudyService().lockSite(site, ub);
			} else if (action.equalsIgnoreCase("unlock")) {
				message = resword.getString("unlockSiteStudySubjectsResultMsg");
				getStudyService().unlockSite(site, ub);
			}
			showResultMessage(request, site, message);
			response.sendRedirect((String) request.getSession().getAttribute(REFERER_URL));
		} else {
			forwardPage(Page.LOCK_SITE, request, response);
		}
	}

	private void showResultMessage(HttpServletRequest request, StudyBean studyBean, String message) {
		addPageMessage(message.replace("{0}", studyBean.getName()), request);
		Map storedAttributes = new HashMap();
		storedAttributes.put(Controller.PAGE_MESSAGE, request.getAttribute(Controller.PAGE_MESSAGE));
		request.getSession().setAttribute(BaseController.STORED_ATTRIBUTES, storedAttributes);
	}
}
