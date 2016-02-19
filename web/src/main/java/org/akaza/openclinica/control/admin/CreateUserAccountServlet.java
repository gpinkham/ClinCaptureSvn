/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2014 Clinovo Inc.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.control.core.SpringServlet;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.SQLInitServlet;
import org.springframework.stereotype.Component;

import com.clinovo.util.DateUtil;
import com.clinovo.util.EmailUtil;
import com.clinovo.validator.UserValidator;

/**
 * Servlet for creating a user account.
 * 
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Component
public class CreateUserAccountServlet extends SpringServlet {

	public static final String SHOW_EXIT_INSTEAD_OF_BACK = "showExitInsteadOfBack";
	public static final String INPUT_USERNAME = "userName";
	public static final String INPUT_FIRST_NAME = "firstName";
	public static final String INPUT_LAST_NAME = "lastName";
	public static final String INPUT_EMAIL = "email";
	public static final String INPUT_PHONE = "phone";
	public static final String INPUT_COMPANY = "company";
	public static final String INPUT_STUDY = "activeStudy";
	public static final String INPUT_ROLE = "role";
	public static final String INPUT_TYPE = "type";
	public static final String INPUT_DISPLAY_PASSWORD = "displayPassword";
	public static final String INPUT_ALLOW_SOAP = "allowSoap";
	public static final String USER_ACCOUNT_NOTIFICATION = "notifyPassword";
	public static final String DEFAULT_TIME_ZONE_ID_REQUEST_ATR = "defaultTimeZoneID";

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);

		checkIfStudySponsor(request);
		
		if (!ub.isSysAdmin()) {
			throw new InsufficientPermissionException(Page.MENU,
					getResException().getString("you_may_not_perform_administrative_functions"), "1");
		}
	}

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);
		FormProcessor fp = new FormProcessor(request);

		StudyDAO sdao = getStudyDAO();
		List<StudyBean> all = sdao.findAllActiveStudiesWhereUserHasRole(ub.getName());

		List<StudyBean> finalList = new ArrayList<StudyBean>();
		for (StudyBean sb : all) {
			finalList.add(sb);
			finalList.addAll(sdao.findAllByParentAndActive(sb.getId()));
		}

		addEntityList("studies", finalList, getResPage().getString("a_user_cannot_be_created_no_study_as_active"),
				Page.ADMIN_SYSTEM, request, response);

		String pageIsChanged = request.getParameter("pageIsChanged");
		if (pageIsChanged != null) {
			request.setAttribute("pageIsChanged", pageIsChanged);
		}

		Map roleMap = customiseUserRoleMap(sdao, currentStudy, fp.getInt(INPUT_STUDY));

		ArrayList types = UserType.toArrayList();
		types.remove(UserType.INVALID);
		types.remove(UserType.TECHADMIN);
		addEntityList("types", types, getResPage().getString("a_user_cannot_be_created_no_user_types_for"),
				Page.ADMIN_SYSTEM, request, response);

		Boolean changeRoles = request.getParameter("changeRoles") != null
				&& Boolean.parseBoolean(request.getParameter("changeRoles"));
		int activeStudy = fp.getInt(INPUT_STUDY);
		StudyBean studyBean = (StudyBean) sdao.findByPK(activeStudy);
		request.setAttribute("roles", roleMap);
		request.setAttribute("activeStudy", activeStudy);
		request.setAttribute("isThisStudy", !(studyBean.getParentStudyId() > 0));

		if (!fp.isSubmitted() || changeRoles) {
			String[] textFields = {INPUT_USERNAME, INPUT_FIRST_NAME, INPUT_LAST_NAME, INPUT_PHONE, INPUT_EMAIL,
					INPUT_COMPANY, INPUT_DISPLAY_PASSWORD, INPUT_ALLOW_SOAP, INPUT_TIME_ZONE};
			fp.setCurrentStringValuesAsPreset(textFields);
			String[] ddlbFields = {INPUT_STUDY, INPUT_ROLE, INPUT_TYPE};
			fp.setCurrentIntValuesAsPreset(ddlbFields);
			HashMap presetValues = fp.getPresetValues();
			String sendPwd = SQLInitServlet.getField("user_account_notification");
			fp.addPresetValue(USER_ACCOUNT_NOTIFICATION, sendPwd);
			setPresetValues(presetValues, request);
			request.setAttribute("pageIsChanged", changeRoles);
			request.setAttribute(TIME_ZONE_IDS_SORTED_REQUEST_ATR, DateUtil.getAvailableTimeZoneIDsSorted());
			request.setAttribute(DEFAULT_TIME_ZONE_ID_REQUEST_ATR, ub.getUserTimeZoneId());
			forwardPage(Page.CREATE_ACCOUNT, request, response);
		} else {
			UserType type = UserType.get(fp.getInt("type"));

			UserAccountDAO userAccountDao = getUserAccountDAO();
			HashMap errors = UserValidator.validateUserCreate(getConfigurationDao(), userAccountDao, getStudyDAO(),
					studyBean);

			if (errors.isEmpty()) {
				UserAccountBean createdUserAccountBean = new UserAccountBean();
				createdUserAccountBean.setName(fp.getString(INPUT_USERNAME));
				createdUserAccountBean.setFirstName(fp.getString(INPUT_FIRST_NAME));
				createdUserAccountBean.setLastName(fp.getString(INPUT_LAST_NAME));
				createdUserAccountBean.setEmail(fp.getString(INPUT_EMAIL));
				createdUserAccountBean.setPhone(fp.getString(INPUT_PHONE));
				createdUserAccountBean.setInstitutionalAffiliation(fp.getString(INPUT_COMPANY));
				createdUserAccountBean.setRunWebservices(fp.getString(INPUT_ALLOW_SOAP).equalsIgnoreCase("true"));
				createdUserAccountBean.setUserTimeZoneId(fp.getString(INPUT_TIME_ZONE));

				int studyId = fp.getInt(INPUT_STUDY);
				int roleId = fp.getInt(INPUT_ROLE);
				StudyUserRoleBean studyUserRole = addActiveStudyRole(request, createdUserAccountBean, studyId,
						Role.get(roleId));

				logger.warn("*** found type: " + fp.getInt("type"));
				logger.warn("*** setting type: " + type.getDescription());
				createdUserAccountBean.addUserType(type);

				getUserAccountService().createUser(ub, createdUserAccountBean, studyUserRole.getRole(), false,
						getUserDetails());

				if (createdUserAccountBean.isActive()) {
					addPageMessage(getResPage().getString("the_user_account") + "\"" + createdUserAccountBean.getName()
							+ "\"" + getResPage().getString("was_created_succesfully"), request);
					if (!"true".equalsIgnoreCase(fp.getString(INPUT_DISPLAY_PASSWORD))) {
						try {
							StudyBean emailParentStudy;
							if (currentStudy.getParentStudyId() > 0) {
								emailParentStudy = (StudyBean) sdao.findByPK(currentStudy.getParentStudyId());
							} else {
								emailParentStudy = currentStudy;
							}
							sendNewAccountEmail(request, createdUserAccountBean, emailParentStudy.getName());
						} catch (Exception e) {
							addPageMessage(getResPage().getString("there_was_an_error_sending_account_creating_mail"),
									request);
						}
					} else {
						addPageMessage(
								getResPage().getString("user_password") + "<br/>" + createdUserAccountBean.getRealPassword()
										+ "<br/> " + getResPage().getString("please_write_down_the_password_and_provide"),
								request);
					}
				} else {
					addPageMessage(getResPage().getString("the_user_account") + "\"" + createdUserAccountBean.getName()
							+ "\"" + getResPage().getString("could_not_created_due_database_error"), request);
				}
				if (createdUserAccountBean.isActive()) {
					request.setAttribute(ViewUserAccountServlet.ARG_USER_ID,
							Integer.toString(createdUserAccountBean.getId()));
					request.setAttribute(SHOW_EXIT_INSTEAD_OF_BACK, true);
					forwardPage(Page.VIEW_USER_ACCOUNT_SERVLET, request, response);
				} else {
					forwardPage(Page.LIST_USER_ACCOUNTS_SERVLET, request, response);
				}
			} else {
				String[] textFields = {INPUT_USERNAME, INPUT_FIRST_NAME, INPUT_LAST_NAME, INPUT_PHONE, INPUT_EMAIL,
						INPUT_COMPANY, INPUT_DISPLAY_PASSWORD, INPUT_ALLOW_SOAP, INPUT_TIME_ZONE};
				fp.setCurrentStringValuesAsPreset(textFields);

				String[] ddlbFields = {INPUT_STUDY, INPUT_ROLE, INPUT_TYPE};
				fp.setCurrentIntValuesAsPreset(ddlbFields);

				HashMap presetValues = fp.getPresetValues();
				setPresetValues(presetValues, request);

				setInputMessages(errors, request);
				addPageMessage(getResPage().getString("there_were_some_errors_submission")
						+ getResPage().getString("see_below_for_details"), request);

				request.setAttribute(TIME_ZONE_IDS_SORTED_REQUEST_ATR, DateUtil.getAvailableTimeZoneIDsSorted());
				forwardPage(Page.CREATE_ACCOUNT, request, response);
			}
		}
	}

	private Map customiseUserRoleMap(StudyDAO sdao, StudyBean currentStudy, int selectedStudy) {
		Map<Object, Object> roleMap = new LinkedHashMap<Object, Object>(Role.ROLE_MAP_WITH_DESCRIPTION);
		
		StudyBean selectedStudyBean = (StudyBean) sdao.findByPK(selectedStudy);
		int parentStudyId = selectedStudyBean.getParentStudyId() > 0
				? selectedStudyBean.getParentStudyId()
				: selectedStudyBean.getId();
		
		boolean isEvaluationEnabled = getStudyParameterValueDAO().findByHandleAndStudy(parentStudyId, "studyEvaluator")
				.getValue().equalsIgnoreCase("yes");
		if (!isEvaluationEnabled) {
			roleMap.remove(Role.STUDY_EVALUATOR.getId());
		}
		StudyParameterValueBean allowCodingVerification = getStudyParameterValueDAO().findByHandleAndStudy(parentStudyId,
				"medicalCoding");

		if (!allowCodingVerification.getValue().equalsIgnoreCase("yes")) {
			roleMap.remove(Role.STUDY_CODER.getId());
		}
		return roleMap;
	}

	private StudyUserRoleBean addActiveStudyRole(HttpServletRequest request, UserAccountBean createdUserAccountBean,
			int studyId, Role r) {
		UserAccountBean ub = getUserAccountBean(request);
		createdUserAccountBean.setActiveStudyId(studyId);

		StudyUserRoleBean activeStudyRole = new StudyUserRoleBean();

		activeStudyRole.setStudyId(studyId);
		activeStudyRole.setRoleName(r.getCode());
		activeStudyRole.setStatus(Status.AVAILABLE);
		activeStudyRole.setOwner(ub);

		createdUserAccountBean.addRole(activeStudyRole);

		return activeStudyRole;
	}

	private void sendNewAccountEmail(HttpServletRequest request, UserAccountBean createdUserAccountBean,
			String studyName) throws Exception {
		StringBuilder sb = new StringBuilder("");
		logger.info("Sending account creation notification to " + createdUserAccountBean.getName());
		String body = sb.append(EmailUtil.getEmailBodyStart()).append(getResWord().getString("dear")).append(" ")
				.append(createdUserAccountBean.getFirstName()).append(" ").append(createdUserAccountBean.getLastName())
				.append(",<br><br>").append(getResText().getString("a_new_user_account_has_been_created_for_you"))
				.append("<br><br>").append(getResWord().getString("user_name")).append(": ")
				.append(createdUserAccountBean.getName()).append("<br>").append(getResWord().getString("password"))
				.append(": ").append(createdUserAccountBean.getRealPassword()).append("<br><br>")
				.append(getResText().getString("please_test_your_login_information_and_let")).append("<br>")
				.append(SQLInitServlet.getSystemURL()).append(" . <br><br> ")
				.append(getResPage().getString("best_system_administrator").replace("{0}", studyName))
				.append(EmailUtil.getEmailBodyEnd()).append(EmailUtil.getEmailFooter(CoreResources.getSystemLocale()))
				.toString();
		sendEmail(createdUserAccountBean.getEmail().trim(), getResText().getString("your_new_openclinica_account"), body,
				false, request);
	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		return SpringServlet.ADMIN_SERVLET_CODE;
	}
}
