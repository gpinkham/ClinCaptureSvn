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

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.control.core.SpringServlet;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import com.clinovo.util.EventDefinitionCRFUtil;
import org.akaza.openclinica.domain.SourceDataVerification;
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
@Component
public class InitUpdateEventDefinitionServlet extends SpringServlet {

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

		checkStudyLocked(Page.LIST_DEFINITION_SERVLET, getResPage().getString("current_study_locked"), request, response);
		if (ub.isSysAdmin()) {
			return;
		}

		int studyId = currentStudy.getId();

		if (ub.hasRoleInStudy(studyId)) {
			Role r = ub.getRoleByStudy(studyId).getRole();
			if (!r.equals(Role.STUDY_DIRECTOR) && !r.equals(Role.STUDY_ADMINISTRATOR)) {
				addPageMessage(getResPage().getString("no_have_permission_to_update_study_event_definition")
						+ getResPage().getString("please_contact_sysadmin_questions"), request);
				throw new InsufficientPermissionException(Page.LIST_DEFINITION_SERVLET,
						getResException().getString("not_study_director"), "1");
			}
		}
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		String idString = request.getParameter("id");
		if (request.getParameter("init") != null || idString != null) {
			UpdateEventDefinitionServlet.clearSession(session);
		}
		StudyBean currentStudy = getCurrentStudy(request);
		setUserNameInsteadEmail(request);
		StudyEventDefinitionDAO sdao = getStudyEventDefinitionDAO();
		logger.info("definition id: " + idString);
		if (StringUtil.isBlank(idString)) {
			addPageMessage(getResPage().getString("please_choose_a_definition_to_edit"), request);
			forwardPage(Page.LIST_DEFINITION_SERVLET, request, response);
		} else {
			// definition id
			int defId = Integer.valueOf(idString.trim());
			StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) sdao.findByPK(defId);

			if (currentStudy.getId() != studyEventDefinitionBean.getStudyId()) {
				addPageMessage(getResPage().getString("no_have_correct_privilege_current_study") + " "
						+ getResPage().getString("change_active_study_or_contact"), request);
				forwardPage(Page.MENU_SERVLET, request, response);
				return;
			}

			Map<Integer, SignStateRestorer> signStateRestorerMap = getEventDefinitionService().prepareSignStateRestorer(studyEventDefinitionBean);
			List<EventDefinitionCRFBean> childEventDefCRFs = getEventDefinitionService().getAllChildrenEventDefinitionCrfs(studyEventDefinitionBean);
			List<EventDefinitionCRFBean> eventDefinitionCRFs = getEventDefinitionService().getAllParentsEventDefinitionCrfs(studyEventDefinitionBean);
			boolean childEDCConfigurationIsSameAsParent = EventDefinitionCRFUtil.compareEDCListConfiguration(childEventDefCRFs, eventDefinitionCRFs);

			boolean isItemLevelSDVAllowed = getCurrentStudy().getStudyParameterConfig().getItemLevelSDV().equals("yes");
			request.getSession().setAttribute(UpdateEventDefinitionServlet.SDV_STATES,
					SourceDataVerification.getAvailableSDVStates(isItemLevelSDVAllowed));
			session.setAttribute("numberOfExistingSitesOnStudy", getStudyDAO().findOlnySiteIdsByStudy(getCurrentStudy()).size());
			session.setAttribute("childEDCConfigurationIsSameAsParent", childEDCConfigurationIsSameAsParent);
			session.setAttribute("definition", studyEventDefinitionBean);
			session.setAttribute("childEventDefCRFs", childEventDefCRFs);
			session.setAttribute("signStateRestorerMap", signStateRestorerMap);
			session.setAttribute("edcSDVMap", EventDefinitionCRFUtil.getEDCSDVMap(eventDefinitionCRFs));
			session.setAttribute("oldEventDefinitionCRFs", EventDefinitionCRFUtil.cloneList(eventDefinitionCRFs));
			session.setAttribute(EventDefinitionCRFUtil.EVENT_DEFINITION_CRFS_LABEL,
					EventDefinitionCRFUtil.mergeEventDefinitions(session, eventDefinitionCRFs));

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
		request.getSession().setAttribute("userNameInsteadEmail", setUserEmail(userBean));
	}

	private String setUserEmail(UserAccountBean userBean) {
		return userBean.getName() != null ? userBean.getName() : getResException().getString("not_found_in_the_db");
	}
}
