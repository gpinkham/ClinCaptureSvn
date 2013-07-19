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
package org.akaza.openclinica.control.admin;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

import java.util.*;

/**
 * @author jxu
 * 
 *         Modified by ywang, 11-19-2007.
 * 
 *         TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style -
 *         Code Templates
 */
@SuppressWarnings({"rawtypes", "unchecked",  "serial"})
public class SetUserRoleServlet extends SecureController {
	
	@Override
	public void mayProceed() throws InsufficientPermissionException {
		if (ub.isSysAdmin()) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.LIST_USER_ACCOUNTS_SERVLET, resexception.getString("not_admin"),
				"1");

	}

	@Override
	public void processRequest() throws Exception {
		UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
		StudyDAO sdao = new StudyDAO(sm.getDataSource());
		FormProcessor fp = new FormProcessor(request);
		int userId = fp.getInt("userId");
		String pageIsChanged = request.getParameter("pageIsChanged");
		if (pageIsChanged != null) {
			request.setAttribute("pageIsChanged", pageIsChanged);
		}
		if (userId == 0) {
			addPageMessage(respage.getString("please_choose_a_user_to_set_role_for"));
			forwardPage(Page.LIST_USER_ACCOUNTS_SERVLET);
		} else {
			String action = request.getParameter("action");
			UserAccountBean user = (UserAccountBean) udao.findByPK(userId);
			ArrayList studies = (ArrayList) sdao.findAll();
			ArrayList studiesHaveRole = (ArrayList) sdao.findAllByUser(user.getName());
			studies.removeAll(studiesHaveRole);
			HashSet<StudyBean> studiesNotHaveRole = new HashSet<StudyBean>();
			HashSet<StudyBean> sitesNotHaveRole = new HashSet<StudyBean>();
			for (int i = 0; i < studies.size(); i++) {
				StudyBean study1 = (StudyBean) studies.get(i);

				// TODO: implement equal() according to id
				boolean hasStudy = false;
				for (int j = 0; j < studiesHaveRole.size(); j++) {
					StudyBean study2 = (StudyBean) studiesHaveRole.get(j);
					if (study2.getId() == study1.getId()) {
						hasStudy = true;
						break;
					}
				}
				if (!hasStudy) {
					if (study1.getParentStudyId() > 0) {
						sitesNotHaveRole.add(study1);
					} else {
						studiesNotHaveRole.add(study1);
					}
				}
			}

			Boolean changeRoles = request.getParameter("changeRoles") == null ? false : Boolean.parseBoolean(request
					.getParameter("changeRoles"));
			int studyId = fp.getInt("studyId");
			request.setAttribute("roles", Role.roleMapWithDescriptions);
			request.setAttribute("studyId", studyId);
			if ("confirm".equalsIgnoreCase(action) || changeRoles) {
				// Re-order studiesNotHaveRole so that sites
				// under their studies;
				ArrayList finalStudiesNotHaveRole = new ArrayList();
				Iterator iter_study = studiesNotHaveRole.iterator();
				while (iter_study.hasNext()) {
					StudyBean s = (StudyBean) iter_study.next();
					finalStudiesNotHaveRole.add(s);
					Iterator iter_site = sitesNotHaveRole.iterator();
					while (iter_site.hasNext()) {
						StudyBean site = (StudyBean) iter_site.next();
						if (site.getParentStudyId() == s.getId()) {
							finalStudiesNotHaveRole.add(site);
						}
					}
				}

                StudyBean study = (StudyBean)sdao.findByPK(studyId);
                request.setAttribute("isThisStudy", !(study.getParentStudyId() > 0));

				request.setAttribute("user", user);
				request.setAttribute("studies", finalStudiesNotHaveRole);
				StudyUserRoleBean uRole = new StudyUserRoleBean();
				uRole.setFirstName(user.getFirstName());
				uRole.setLastName(user.getLastName());
				uRole.setUserName(user.getName());
				request.setAttribute("uRole", uRole);

				forwardPage(Page.SET_USER_ROLE);
			} else {
				// set role
				String userName = fp.getString("name");
				studyId = fp.getInt("studyId");
				StudyBean userStudy = (StudyBean) sdao.findByPK(studyId);
				int roleId = fp.getInt("roleId");
				// new user role
				StudyUserRoleBean sur = new StudyUserRoleBean();
				sur.setName(userName);
				sur.setRole(Role.get(roleId));
				sur.setStudyId(studyId);
				sur.setStudyName(userStudy.getName());
				sur.setStatus(Status.AVAILABLE);
				sur.setOwner(ub);
				sur.setCreatedDate(new Date());

				if (studyId > 0) {
					udao.createStudyUserRole(user, sur);
                    if (ub.getId() == user.getId()) {
                        session.setAttribute("reloadUserBean", true);
                    }
                    addPageMessage(user.getFirstName() + " " + user.getLastName() + " ("
							+ resword.getString("username") + ": " + user.getName() + ") "
							+ respage.getString("has_been_granted_the_role") + " \"" + sur.getRole().getDescription()
							+ "\" " + respage.getString("in_the_study_site") + " " + userStudy.getName() + ".");
				}

				forwardPage(Page.LIST_USER_ACCOUNTS_SERVLET);

			}

		}
	}

	@Override
	protected String getAdminServlet() {
		return SecureController.ADMIN_SERVLET_CODE;
	}

}
