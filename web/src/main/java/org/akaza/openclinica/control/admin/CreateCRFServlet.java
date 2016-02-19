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
import java.util.Date;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

/**
 * Creates a new CRF
 * 
 * @author jxu
 */
@SuppressWarnings("rawtypes")
@Component
public class CreateCRFServlet extends Controller {

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
				getResPage().getString("you_not_have_permission_create_CRF")
						+ getResPage().getString("change_study_contact_sysadmin"), request);

		throw new InsufficientPermissionException(Page.CRF_LIST_SERVLET, getResException().getString("not_study_director"),
				"1");

	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);

		CRFDAO cdao = getCRFDAO();
		String action = request.getParameter("action");

		FormProcessor fp = new FormProcessor(request);

		// checks which module the requests are from
		String module = fp.getString(MODULE);
		request.setAttribute(MODULE, module);

		// add the list here so that users can tell about crf creation
		// process together with workflow, tbh

		StudyInfoPanel panel = getStudyInfoPanel(request);
		panel.reset();
		panel.setStudyInfoShown(false);
		panel.setOrderedData(true);

		setToPanel(getResWord().getString("create_CRF"), getResPage().getString("br_create_new_CRF_entering"), request);

		setToPanel(getResWord().getString("create_CRF_version"), getResPage().getString("br_create_new_CRF_uploading"), request);
		setToPanel(getResWord().getString("revise_CRF_version"), getResPage().getString("br_if_you_owner_CRF_version"), request);
		setToPanel(getResWord().getString("CRF_spreadsheet_template"),
				getResPage().getString("br_download_blank_CRF_spreadsheet_from"), request);
		setToPanel(getResWord().getString("example_CRF_br_spreadsheets"),
				getResPage().getString("br_download_example_CRF_instructions_from"), request);

		if (StringUtil.isBlank(action)) {
			request.getSession().setAttribute("crf", new CRFBean());
			forwardPage(Page.CREATE_CRF, request, response);
		} else {
			if ("confirm".equalsIgnoreCase(action)) {

				Validator v = new Validator(new ValidatorHelper(request, getConfigurationDao()));

				v.addValidation("name", Validator.NO_BLANKS);
				String name = fp.getString("name");
				String description = fp.getString("description");
				CRFBean crf = new CRFBean();
				crf.setName(name.trim());
				crf.setDescription(description.trim());
				request.getSession().setAttribute("crf", crf);
				HashMap errors = v.validate();
				if (fp.getString("name").trim().length() > 255) {
					Validator.addError(errors, "name", getResException().getString("maximum_length_name_255"));
				}
				if (fp.getString("description").trim().length() > 2048) {
					Validator.addError(errors, "description", getResException().getString("maximum_length_description_255"));
				}
				if (!errors.isEmpty()) {
					logger.info("has validation errors in the first section");
					request.setAttribute("formMessages", errors);
					forwardPage(Page.CREATE_CRF, request, response);

				} else {

					CRFBean crf1 = (CRFBean) cdao.findByName(name.trim());
					if (crf1.getId() > 0) {
						Validator.addError(errors, "name", getResException().getString("CRF_name_used_choose_unique_name"));
						request.setAttribute("formMessages", errors);
						forwardPage(Page.CREATE_CRF, request, response);
					} else {
						crf = (CRFBean) request.getSession().getAttribute("crf");
						logger.info("The crf to be saved:" + crf.getName());
						crf.setOwner(ub);
						crf.setCreatedDate(new Date());
						crf.setStatus(Status.AVAILABLE);
						cdao.create(crf);

						crf = (CRFBean) cdao.findByName(crf.getName());
						CRFVersionBean version = new CRFVersionBean();
						version.setCrfId(crf.getId());
						request.getSession().setAttribute("version", version);
						request.getSession().setAttribute("crfName", crf.getName());
						request.getSession().removeAttribute("crf");
						forwardPage(Page.CREATE_CRF_VERSION, request, response);
					}
				}
			}
		}

	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		UserAccountBean ub = getUserAccountBean(request);
		if (ub.isSysAdmin()) {
			return Controller.ADMIN_SERVLET_CODE;
		} else {
			return "";
		}
	}
}
