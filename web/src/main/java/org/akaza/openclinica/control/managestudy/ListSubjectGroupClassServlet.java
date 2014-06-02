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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.akaza.openclinica.bean.core.GroupClassType;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.bean.EntityBeanTable;
import org.akaza.openclinica.web.bean.StudyGroupClassRow;
import org.springframework.stereotype.Component;

/**
 * Lists all the subject group classes in a study
 * 
 * @author jxu
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
@Component
public class ListSubjectGroupClassServlet extends Controller {

	/**
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
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin()) {
			return;
		}

		if (currentRole.getRole().equals(Role.STUDY_DIRECTOR) || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}

		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET,
				resexception.getString("not_study_director"), "1");

	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		StudyBean currentStudy = getCurrentStudy(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		FormProcessor fp = new FormProcessor(request);
		String action = request.getParameter("action");
		StudyGroupClassDAO sgcdao = getStudyGroupClassDAO();
		StudyDAO stdao = getStudyDAO();
		int parentStudyId = currentStudy.getParentStudyId();
		ArrayList groups;
		ArrayList<StudyGroupClassBean> availableDynGroups = new ArrayList<StudyGroupClassBean>();
		HashMap<Integer, String> dynGroupClassIdToEventsNames = new HashMap<Integer, String>();
		ArrayList<Integer> listOfGroupClassOrdinalsWithoutDef = new ArrayList<Integer>();
		boolean defGroupExist = false;

		if (parentStudyId > 0) {
			StudyBean parentStudy = (StudyBean) stdao.findByPK(parentStudyId);
			groups = sgcdao.findAllByStudy(parentStudy);
		} else {
			groups = sgcdao.findAllByStudy(currentStudy);
		}
		StudyGroupDAO sgdao = getStudyGroupDAO();
		StudyEventDefinitionDAO seddao = getStudyEventDefinitionDAO();
		for (Object group1 : groups) {
			StudyGroupClassBean group = (StudyGroupClassBean) group1;
			if (group.getGroupClassTypeId() == GroupClassType.DYNAMIC.getId()) {
				ArrayList<StudyEventDefinitionBean> orderedDefinitions = seddao.findAllAvailableAndOrderedByStudyGroupClassId(group.getId());
				group.setEventDefinitions(orderedDefinitions);
				StringBuilder strOfEventsNames = new StringBuilder("");
				if (group.getStatus().isAvailable()) {
					for (StudyEventDefinitionBean def : orderedDefinitions) {
						strOfEventsNames.append(", ").append(def.getName());
					}
					dynGroupClassIdToEventsNames.put(group.getId(), strOfEventsNames.toString().replaceFirst(", ", ""));
					availableDynGroups.add(group);
				}
			} else {
				ArrayList studyGroups = sgdao.findAllByGroupClass(group);
				group.setStudyGroups(studyGroups);
			}
		}
		Collections.sort(availableDynGroups, StudyGroupClassBean.comparatorForDynGroupClasses);

		if ("submit_order".equals(action)) {
			for (StudyGroupClassBean dynamicGroup : availableDynGroups) {
				if (!dynamicGroup.isDefault()) {
					listOfGroupClassOrdinalsWithoutDef.add(dynamicGroup.getDynamicOrdinal());
				} else {
					defGroupExist = true;
				}
			}
			for (StudyGroupClassBean dynamicGroup : availableDynGroups) {
				if (!dynamicGroup.isDefault()) {
					int index = Integer.valueOf(request.getParameter("dynamicGroup" + dynamicGroup.getId()));
					int newDynamicOrdinal = listOfGroupClassOrdinalsWithoutDef.get(defGroupExist ? index - 2
							: index - 1);
					if (newDynamicOrdinal != dynamicGroup.getDynamicOrdinal()) {
						sgcdao.updateDynamicOrdinal(newDynamicOrdinal,
								parentStudyId > 0 ? parentStudyId : currentStudy.getId(), dynamicGroup.getId());
						dynamicGroup.setDynamicOrdinal(newDynamicOrdinal);
					}
				}
			}
			Collections.sort(availableDynGroups, StudyGroupClassBean.comparatorForDynGroupClasses);
		}

		EntityBeanTable table = fp.getEntityBeanTable();
		ArrayList allGroupRows = StudyGroupClassRow.generateRowsFromBeans(groups);
		boolean isParentStudy = currentStudy.getParentStudyId() <= 0;
		request.setAttribute("isParentStudy", isParentStudy);

		String[] columns = { resword.getString("subject_group_class"), resword.getString("type"),
				resword.getString("subject_assignment"), resword.getString("default"), resword.getString("study_name"),
				resword.getString("subject_groups"), resword.getString("study_events"), resword.getString("status"),
				resword.getString("actions") };
		table.setColumns(new ArrayList(Arrays.asList(columns)));
		table.hideColumnLink(3);
		table.hideColumnLink(5);
		table.hideColumnLink(6);
		table.hideColumnLink(8);
		table.setQuery("ListSubjectGroupClass", new HashMap());
		table.setRows(allGroupRows);
		table.computeDisplay();

		request.setAttribute("availableDynGroups", availableDynGroups);
		request.setAttribute("dynGroupClassIdToEventsNames", dynGroupClassIdToEventsNames);

		request.setAttribute("table", table);

		if (request.getParameter("read") != null && request.getParameter("read").equals("true")
				&& currentRole.getRole().equals(Role.STUDY_DIRECTOR)) {
			request.setAttribute("readOnly", true);
		}
		forwardPage(Page.SUBJECT_GROUP_CLASS_LIST, request, response);

	}

}
