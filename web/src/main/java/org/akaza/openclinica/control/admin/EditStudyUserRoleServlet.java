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

import com.clinovo.util.ValidatorHelper;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.TermType;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.navigation.Navigation;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

import java.util.*;

/**
 * @author ssachs
 * 
 *         Servlet for creating a user account.
 */
@SuppressWarnings({"rawtypes", "serial"})
public class EditStudyUserRoleServlet extends SecureController {
	public static final String INPUT_ROLE = "role";

	public static final String PATH = "EditStudyUserRole";
	public static final String ARG_STUDY_ID = "studyId";
	public static final String ARG_USER_NAME = "userName";

	public static String getLink(StudyUserRoleBean s, UserAccountBean user) {
		int studyId = s.getStudyId();
		return PATH + "?" + ARG_STUDY_ID + "=" + studyId + "&" + ARG_USER_NAME + "=" + user.getName();
	}

	@Override
	protected void mayProceed() throws InsufficientPermissionException {
		if (!ub.isSysAdmin()) {
			addPageMessage(respage.getString("no_have_correct_privilege_current_study")
					+ respage.getString("change_study_contact_sysadmin"));
			throw new InsufficientPermissionException(Page.MENU_SERVLET,
					resexception.getString("you_may_not_perform_administrative_functions"), "1");
		}

		return;
	}

	@Override
	protected void processRequest() throws Exception {
		UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());

		FormProcessor fp = new FormProcessor(request);

		int studyId = fp.getInt(ARG_STUDY_ID);
		String uName = fp.getString(ARG_USER_NAME);
		StudyUserRoleBean studyUserRole = udao.findRoleByUserNameAndStudyId(uName, studyId);

		StudyDAO sdao = new StudyDAO(sm.getDataSource());
		StudyBean sb = (StudyBean) sdao.findByPK(studyUserRole.getStudyId());
		if (sb != null) {
			studyUserRole.setStudyName(sb.getName());
		}

		if (!studyUserRole.isActive()) {
			String message = respage.getString("the_user_has_no_role_in_study");
			addPageMessage(message);
			forwardPage(Page.LIST_USER_ACCOUNTS_SERVLET);
		} else {
			StudyBean study = (StudyBean) sdao.findByPK(studyUserRole.getStudyId());
            request.setAttribute("isThisStudy", !(study.getParentStudyId() > 0));
			// send the user to the right place..
			if (!fp.isSubmitted()) {
				request.setAttribute("userName", uName);
				request.setAttribute("studyUserRole", studyUserRole);
				request.setAttribute("roles", Role.roleMapWithDescriptions);
				request.setAttribute("chosenRoleId", new Integer(studyUserRole.getRole().getId()));
				forwardPage(Page.EDIT_STUDY_USER_ROLE);
			}

			// process the form
			else {
				Validator v = new Validator(new ValidatorHelper(request, getConfigurationDao()));
				v.addValidation(INPUT_ROLE, Validator.IS_VALID_TERM, TermType.ROLE);
				HashMap errors = v.validate();

				if (errors.isEmpty()) {
                    Page forwardTo = Page.LIST_USER_ACCOUNTS_SERVLET;
					int roleId = fp.getInt(INPUT_ROLE);
					Role r = Role.get(roleId);
					studyUserRole.setRoleName(r.getName());
					studyUserRole.setUpdater(ub);
					udao.updateStudyUserRole(studyUserRole, uName);
					addPageMessage(respage.getString("the_user_in_study_has_been_updated"));
					if (ub.getName().equals(studyUserRole.getUserName())) {
						session.setAttribute("reloadUserBean", true);
						if (!ub.isSysAdmin() && !ub.isTechAdmin()
								&& studyUserRole.getRole() != Role.STUDY_ADMINISTRATOR
								&& studyUserRole.getRole() != Role.STUDY_DIRECTOR) {
							forwardTo = Page.MENU_SERVLET;
							Navigation.removeUrl(request, "/ListUserAccounts");
							addPageMessage(respage.getString("you_may_not_perform_administrative_functions"));
						}
					}
					forwardPage(forwardTo);
				} else {
					String message = respage.getString("the_role_choosen_was_invalid_choose_another");
					addPageMessage(message);

					request.setAttribute("userName", uName);
					request.setAttribute("studyUserRole", studyUserRole);
					request.setAttribute("chosenRoleId", new Integer(fp.getInt(INPUT_ROLE)));
					request.setAttribute("roles", Role.roleMapWithDescriptions);
					forwardPage(Page.EDIT_STUDY_USER_ROLE);
				}
			}
		}
	}

	@Override
	protected String getAdminServlet() {
		return SecureController.ADMIN_SERVLET_CODE;
	}
}
