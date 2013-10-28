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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.List;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.bean.EntityBeanTable;
import org.akaza.openclinica.web.bean.UserAccountRow;

/**
 * Processes request to assign a user to a study
 * 
 * @author jxu
 * 
 */
@SuppressWarnings({"rawtypes", "unchecked",  "serial"})
public class AssignUserToStudyServlet extends SecureController {

	/**
     *
     */
	
	@Override
	public void mayProceed() throws InsufficientPermissionException {
		if (ub.isSysAdmin() || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_study_director"), "1");

	}

	@Override
	public void processRequest() throws Exception {

		String action = request.getParameter("action");
		ArrayList users = findUsers();
		String nextListPage = request.getParameter("next_list_page");
		if (StringUtil.isBlank(action) || (nextListPage != null && nextListPage.equalsIgnoreCase("true"))) {
			FormProcessor fp = new FormProcessor(request);
			EntityBeanTable table = fp.getEntityBeanTable();
			ArrayList allRows = UserAccountRow.generateRowsFromBeans(users);

			if (nextListPage == null) {
				session.removeAttribute("tmpSelectedUsersMap");
			}

			/*
			 * The tmpSelectedUsersMap will hold all the selected users in the session when the user is navigating
			 * through the list. This has been done so that when the user moves to the next page of Users list, the
			 * selection made in the previous page doesn't get lost.
			 */
			Map tmpSelectedUsersMap = (HashMap) session.getAttribute("tmpSelectedUsersMap");
			if (tmpSelectedUsersMap == null) {
				tmpSelectedUsersMap = new HashMap();
			}
			if (nextListPage != null && nextListPage.equalsIgnoreCase("true")) {
				for (int i = 0; i < users.size(); i++) {
					int id = fp.getInt("id" + i);
					int roleId = fp.getInt("activeStudyRoleId" + i);
					String checked = fp.getString("selected" + i);
					// logger.info("selected:" + checked);
					if (!StringUtil.isBlank(checked) && "yes".equalsIgnoreCase(checked.trim())) {
						tmpSelectedUsersMap.put(id, roleId);
					} else {
						// Removing the elements from session which has been
						// deselected.
						if (tmpSelectedUsersMap.containsKey(id)) {
							tmpSelectedUsersMap.remove(id);
						}
					}
				}
				session.setAttribute("tmpSelectedUsersMap", tmpSelectedUsersMap);
			}

			String[] columns = { resword.getString("user_name"), resword.getString("first_name"),
					resword.getString("last_name"), resword.getString("role"), resword.getString("selected"),
					resword.getString("notes") };
			table.setColumns(new ArrayList(Arrays.asList(columns)));
			table.hideColumnLink(3);
			table.hideColumnLink(4);
			table.hideColumnLink(5);
			table.setQuery("AssignUserToStudy", new HashMap());
			table.setRows(allRows);
			table.computeDisplay();

			StudyParameterValueDAO dao = new StudyParameterValueDAO(sm.getDataSource());
			StudyParameterValueBean allowCodingVerification = dao.findByHandleAndStudy(currentStudy.getId(), "allowCodingVerification");
			
			request.setAttribute("table", table);
			ArrayList roles = Role.toArrayList();
			if (currentStudy.getParentStudyId() > 0) {
				roles.remove(Role.STUDY_ADMINISTRATOR);
                roles.remove(Role.STUDY_MONITOR);
                roles.remove(Role.STUDY_CODER);
			} else {
				if(!allowCodingVerification.getValue().equalsIgnoreCase("yes")) {
					roles.remove(Role.STUDY_CODER);
				}
                roles.remove(Role.INVESTIGATOR);
                roles.remove(Role.CLINICAL_RESEARCH_COORDINATOR);
            }

            roles.remove(Role.STUDY_DIRECTOR); // clincapture does not user the STUDY_DIRECTOR role
			roles.remove(Role.SYSTEM_ADMINISTRATOR); // admin is not a user role, only used
			// for
			// tomcat
			request.setAttribute("roles", roles);
			forwardPage(Page.STUDY_USER_LIST);
		} else {
			if ("submit".equalsIgnoreCase(action)) {
				addUser(users);
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void addUser(ArrayList users) throws Exception {
		String pageMass = "";
		UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
		FormProcessor fp = new FormProcessor(request);
		Map tmpSelectedUsersMap = (HashMap) session.getAttribute("tmpSelectedUsersMap");
		Set addedUsers = new HashSet();
		boolean continueLoop = true;
		for (int i = 0; i < users.size() && continueLoop; i++) {
			int id = fp.getInt("id" + i);
			String firstName = fp.getString("firstName" + i);
			String lastName = fp.getString("lastName" + i);
			String name = fp.getString("name" + i);
			String email = fp.getString("email" + i);
			int roleId = fp.getInt("activeStudyRoleId" + i);
			String checked = fp.getString("selected" + i);

			if (!StringUtil.isBlank(checked) && "yes".equalsIgnoreCase(checked.trim())) {
				logger.info("one user selected");
				UserAccountBean u = new UserAccountBean();
				u.setId(id);
				u.setLastName(lastName);
				u.setFirstName(firstName);
				u.setName(name);
				u.setEmail(email);
				u.setActiveStudyId(ub.getActiveStudyId());
				u.setOwnerId(id);
				addedUsers.add(id);

				StudyUserRoleBean sub = new StudyUserRoleBean();
				sub.setRoleName(Role.get(roleId).getName());
				sub.setStudyId(currentStudy.getId());
				sub.setStatus(Status.AVAILABLE);
				sub.setOwner(ub);
				if (udao.findStudyUserRole(u, sub).getName() != null
						&& udao.findStudyUserRole(u, sub).getName().isEmpty())// create only when it doesn't exist in
																				// database
					udao.createStudyUserRole(u, sub);
				else {
					continueLoop = false;
					break;
				}
				logger.info("one user added");
				pageMass = pageMass + sendEmail(u, sub);

			} else {
				if (tmpSelectedUsersMap != null && tmpSelectedUsersMap.containsKey(id)) {
					tmpSelectedUsersMap.remove(id);
				}
			}
		}

		/* Assigning users which might have been selected during list navigation */
		if (tmpSelectedUsersMap != null) {// try to fix the null pointer
			// exception
			for (Iterator iterator = tmpSelectedUsersMap.keySet().iterator(); iterator.hasNext();) {
				int id = (Integer) iterator.next();
				int roleId = (Integer) tmpSelectedUsersMap.get(id);
				boolean alreadyAdded = false;
				for (Iterator it = addedUsers.iterator(); it.hasNext();) {
					if (id == (Integer) it.next()) {
						alreadyAdded = true;
					}
				}
				if (!alreadyAdded) {
					UserAccountBean u = new UserAccountBean();
					u.setId(id);
					u.setName(udao.findByPK(id).getName());
					u.setActiveStudyId(ub.getActiveStudyId());
					u.setOwnerId(id);

					StudyUserRoleBean sub = new StudyUserRoleBean();
					sub.setRoleName(Role.get(roleId).getName());
					sub.setStudyId(currentStudy.getId());
					sub.setStatus(Status.AVAILABLE);
					sub.setOwner(ub);
					udao.createStudyUserRole(u, sub);
					logger.info("one user added");
					pageMass = pageMass + sendEmail(u, sub);
				}
			}
		}
		session.removeAttribute("tmpSelectedUsersMap");

		if ("".equals(pageMass)) {
			addPageMessage(respage.getString("no_new_user_assigned_to_study"));
		} else {
			addPageMessage(pageMass);
		}

		ArrayList pageMessages = (ArrayList) request.getAttribute(PAGE_MESSAGE);
		session.setAttribute("pageMessages", pageMessages);
		forwardPage(Page.LIST_USER_IN_STUDY_SERVLET);

	}

	/**
	 * Find all users in the system
	 * 
	 * @return
	 */
	private ArrayList findUsers() {
		
		UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
		StudyDAO sdao = new StudyDAO(sm.getDataSource());
		StudyBean site;
		StudyBean study;
		List <UserAccountBean> userAvailable = new ArrayList <UserAccountBean>();
		List <UserAccountBean> userListbyRoles;
		String notes;
		boolean hasRoleInCurrentStudy;
		boolean hasRoleWithStatusRemovedInCurrentStudy;
		
	
		if (currentStudy.getParentStudyId() > 0) {
			userListbyRoles = (ArrayList <UserAccountBean>) udao.findAllByRole(Role.CLINICAL_RESEARCH_COORDINATOR.getName(), Role.INVESTIGATOR.getName());
		} else {
			userListbyRoles = (ArrayList <UserAccountBean>) udao.findAllByRole(Role.STUDY_ADMINISTRATOR.getName(), Role.STUDY_MONITOR.getName());
		}
		
		for (UserAccountBean accountBean : userListbyRoles) {
			
			if (accountBean.getEnabled() == null || !accountBean.getEnabled()) {
				continue;
			}
			
			hasRoleInCurrentStudy = false;
			hasRoleWithStatusRemovedInCurrentStudy = false;
			notes = "";
			for (StudyUserRoleBean roleBean : accountBean.getRoles()) {
				
				if (currentStudy.getId() == roleBean.getStudyId() && roleBean.getStatus().equals(Status.DELETED)) {
					hasRoleWithStatusRemovedInCurrentStudy = true;
					break;
				} else if (currentStudy.getId() == roleBean.getStudyId()) {
					hasRoleInCurrentStudy = true;
				} else if (currentStudy.getParentStudyId() > 0 && !roleBean.getStatus().equals(Status.DELETED)) {
					site = (StudyBean) sdao.findByPK(roleBean.getStudyId());
					study = (StudyBean) sdao.findByPK(site.getParentStudyId());
					notes = notes + roleBean.getRole().getDescription() + respage.getString("in_site") + ": " + site.getName() 
							+ "," + respage.getString("in_the_study") + ": " + study.getName() + "; ";
				} else if (currentStudy.getParentStudyId() == 0 && !roleBean.getStatus().equals(Status.DELETED)) {
					study = (StudyBean) sdao.findByPK(roleBean.getStudyId());
					notes = notes + roleBean.getRole().getDescription() + respage.getString("in_the_study") + ": " + study.getName() + "; ";
				}
				
			}
			
			if (!hasRoleWithStatusRemovedInCurrentStudy) {
				
				accountBean.setNotes(notes);
			
				if (hasRoleInCurrentStudy) {
					accountBean.setStatus(Status.UNAVAILABLE);
					accountBean.setActiveStudyId(currentStudy.getId());
				} else {
					accountBean.setStatus(Status.AVAILABLE);
				}
			
				userAvailable.add(accountBean);
			}
			
		}
		
		return (ArrayList <UserAccountBean>) userAvailable;
	}

	private String sendEmail(UserAccountBean u, StudyUserRoleBean sub) throws Exception {
		logger.info("Sending email...");
		String body = u.getFirstName() + " " + u.getLastName() + "(" + resword.getString("username") + ": "
				+ u.getName() + ") " + respage.getString("has_been_assigned_to_the_study") + currentStudy.getName()
				+ " " + resword.getString("as") + " \"" + sub.getRole().getDescription() + "\". ";

		if (currentStudy.getParentStudyId() > 0) {
			body = u.getFirstName() + " " + u.getLastName() + "(" + resword.getString("username") + ": " + u.getName()
					+ ") " + respage.getString("has_been_assigned_to_the_site") + currentStudy.getName()
					+ " under the Study " + currentStudy.getParentStudyName() + " " + resword.getString("as") + " \""
					+ sub.getRole().getDescription() + "\". ";
		}

		return body;

	}
}
