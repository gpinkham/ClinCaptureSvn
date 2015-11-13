/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License 
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.util.EventDefinitionCRFUtil;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import com.clinovo.util.SignStateRestorer;

/**
 * Prepares to update study event definition.
 * 
 * @author jxu
 * 
 */
@SuppressWarnings({"rawtypes", "serial"})
@Component
public class InitUpdateEventDefinitionServlet extends Controller {

	/**
	 * Checks whether the user has the correct privilege.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);

		checkStudyLocked(Page.LIST_DEFINITION_SERVLET, respage.getString("current_study_locked"), request, response);
		if (ub.isSysAdmin()) {
			return;
		}

		StudyEventDAO sdao = getStudyEventDAO();
		// get current studyid
		int studyId = currentStudy.getId();

		if (ub.hasRoleInStudy(studyId)) {
			Role r = ub.getRoleByStudy(studyId).getRole();
			if (r.equals(Role.STUDY_DIRECTOR) || r.equals(Role.STUDY_ADMINISTRATOR)) {
				return;
			} else {
				addPageMessage(respage.getString("no_have_permission_to_update_study_event_definition")
						+ respage.getString("please_contact_sysadmin_questions"), request);
				throw new InsufficientPermissionException(Page.LIST_DEFINITION_SERVLET,
						resexception.getString("not_study_director"), "1");

			}
		}

		// To Do: the following code doesn't apply to admin for now
		String idString = request.getParameter("id");
		int defId = Integer.valueOf(idString.trim());
		logger.info("defId" + defId);
		ArrayList events = (ArrayList) sdao.findAllByDefinition(defId);
		if (events != null && events.size() > 0) {
			logger.info("has events");
			for (Object event : events) {
				StudyEventBean sb = (StudyEventBean) event;
				if (!sb.getStatus().equals(Status.DELETED) && !sb.getStatus().equals(Status.AUTO_DELETED)) {
					logger.info("found one event");
					addPageMessage(respage.getString("sorry_but_at_this_time_may_not_modufy_SED"), request);
					throw new InsufficientPermissionException(Page.LIST_DEFINITION_SERVLET,
							resexception.getString("not_unpopulated"), "1");
				}
			}
		}

	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String idString = request.getParameter("id");
		if (request.getParameter("init") != null || idString != null) {
			UpdateEventDefinitionServlet.clearSession(request.getSession());
		}
		StudyBean currentStudy = getCurrentStudy(request);
		setUserNameInsteadEmail(request);
		StudyEventDefinitionDAO sdao = getStudyEventDefinitionDAO();
		logger.info("definition id: " + idString);
		if (StringUtil.isBlank(idString)) {
			addPageMessage(respage.getString("please_choose_a_definition_to_edit"), request);
			forwardPage(Page.LIST_DEFINITION_SERVLET, request, response);
		} else {
			// definition id
			int defId = Integer.valueOf(idString.trim());
			StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) sdao.findByPK(defId);

			if (currentStudy.getId() != studyEventDefinitionBean.getStudyId()) {
				addPageMessage(respage.getString("no_have_correct_privilege_current_study") + " "
						+ respage.getString("change_active_study_or_contact"), request);
				forwardPage(Page.MENU_SERVLET, request, response);
				return;
			}

			List<EventDefinitionCRFBean> eventDefinitionCRFs = getEventDefinitionService()
					.getAllParentsEventDefinitionCrfs(studyEventDefinitionBean);

			// Get list of child EventDefinitionCRFs for cascading actions
			List<EventDefinitionCRFBean> childEventDefCRFs = getEventDefinitionService()
					.getAllChildrenEventDefinitionCrfs(studyEventDefinitionBean);

			Map<Integer, SignStateRestorer> signStateRestorerMap = getEventDefinitionService()
					.prepareSignStateRestorer(studyEventDefinitionBean);

			request.getSession().setAttribute("definition", studyEventDefinitionBean);
			request.getSession().setAttribute("eventDefinitionCRFs", eventDefinitionCRFs);
			request.getSession().setAttribute("oldEventDefinitionCRFs",
					EventDefinitionCRFUtil.cloneList(eventDefinitionCRFs));
			// store child list to session
			request.getSession().setAttribute("childEventDefCRFs", childEventDefCRFs);
			// changed above to new list because static, in-place updating is updating all EDCs

			request.getSession().setAttribute("signStateRestorerMap", signStateRestorerMap);

			forwardPage(Page.UPDATE_EVENT_DEFINITION1, request, response);
		}

	}

	private void setUserNameInsteadEmail(HttpServletRequest request) {
		String sedId = request.getParameter("id");
		int eventId = Integer.valueOf(sedId);
		StudyEventDefinitionDAO seddao = getStudyEventDefinitionDAO();
		StudyEventDefinitionBean sedBean = (StudyEventDefinitionBean) seddao.findByPK(eventId);
		int userId = sedBean.getUserEmailId();
		UserAccountDAO uadao = getUserAccountDAO();
		UserAccountBean userBean = (UserAccountBean) uadao.findByPK(userId);
		if (userBean.getName() != null) {
			request.getSession().setAttribute("userNameInsteadEmail", userBean.getName());
		} else {
			request.getSession().setAttribute("userNameInsteadEmail", resexception.getString("not_found_in_the_db"));
		}
	}
}
