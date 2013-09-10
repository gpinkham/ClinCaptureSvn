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

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

import java.util.Date;
import java.util.Locale;

/**
 * Creates a new CRF
 * 
 * @author jxu
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class CreateCRFServlet extends SecureController {

	Locale locale;

	@Override
	public void mayProceed() throws InsufficientPermissionException {

		locale = request.getLocale();

		if (ub.isSysAdmin()) {
			return;
		}

		if (currentRole.getRole().equals(Role.STUDY_DIRECTOR) || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}

		addPageMessage(respage.getString("you_not_have_permission_create_CRF")
				+ respage.getString("change_study_contact_sysadmin"));

		throw new InsufficientPermissionException(Page.CRF_LIST_SERVLET, resexception.getString("not_study_director"),
				"1");

	}

	@Override
	public void processRequest() throws Exception {
		CRFDAO cdao = new CRFDAO(sm.getDataSource());
		String action = request.getParameter("action");

		FormProcessor fp = new FormProcessor(request);

		// checks which module the requests are from
		String module = fp.getString(MODULE);
		request.setAttribute(MODULE, module);

		// add the list here so that users can tell about crf creation
		// process together with workflow, tbh

		resetPanel();
		panel.setStudyInfoShown(false);
		panel.setOrderedData(true);

		setToPanel(resword.getString("create_CRF"), respage.getString("br_create_new_CRF_entering"));

		setToPanel(resword.getString("create_CRF_version"), respage.getString("br_create_new_CRF_uploading"));
		setToPanel(resword.getString("revise_CRF_version"), respage.getString("br_if_you_owner_CRF_version"));
		setToPanel(resword.getString("CRF_spreadsheet_template"),
				respage.getString("br_download_blank_CRF_spreadsheet_from"));
		setToPanel(resword.getString("example_CRF_br_spreadsheets"),
				respage.getString("br_download_example_CRF_instructions_from"));

		if (StringUtil.isBlank(action)) {
			session.setAttribute("crf", new CRFBean());
			forwardPage(Page.CREATE_CRF);
		} else {
			if ("confirm".equalsIgnoreCase(action)) {

				Validator v = new Validator(new ValidatorHelper(request, getConfigurationDao()));

				v.addValidation("name", Validator.NO_BLANKS);
				String name = fp.getString("name");
				String description = fp.getString("description");
				CRFBean crf = new CRFBean();
				crf.setName(name.trim());
				crf.setDescription(description.trim());
				session.setAttribute("crf", crf);
				errors = v.validate();
				if (fp.getString("name").trim().length() > 255) {
					Validator.addError(errors, "name", resexception.getString("maximum_length_name_255"));
				}
				if (fp.getString("description").trim().length() > 2048) {
					Validator.addError(errors, "description", resexception.getString("maximum_length_description_255"));
				}
				if (!errors.isEmpty()) {
					logger.info("has validation errors in the first section");
					request.setAttribute("formMessages", errors);
					forwardPage(Page.CREATE_CRF);

				} else {

					CRFBean crf1 = (CRFBean) cdao.findByName(name.trim());
					if (crf1.getId() > 0) {
						Validator.addError(errors, "name", resexception.getString("CRF_name_used_choose_unique_name"));
						request.setAttribute("formMessages", errors);
						forwardPage(Page.CREATE_CRF);
					} else {
						crf = (CRFBean) session.getAttribute("crf");
						logger.info("The crf to be saved:" + crf.getName());
						crf.setOwner(ub);
						crf.setCreatedDate(new Date());
						crf.setStatus(Status.AVAILABLE);
						cdao.create(crf);

						crf = (CRFBean) cdao.findByName(crf.getName());
						CRFVersionBean version = new CRFVersionBean();
						version.setCrfId(crf.getId());
						session.setAttribute("version", version);
						session.setAttribute("crfName", crf.getName());
						session.removeAttribute("crf");
						forwardPage(Page.CREATE_CRF_VERSION);
					}
				}
			}
		}

	}

	@Override
	protected String getAdminServlet() {
		if (ub.isSysAdmin()) {
			return SecureController.ADMIN_SERVLET_CODE;
		} else {
			return "";
		}
	}
}
