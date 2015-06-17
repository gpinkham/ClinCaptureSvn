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
package org.akaza.openclinica.control.login;

import com.clinovo.util.EmailUtil;
import com.clinovo.util.ValidatorHelper;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.core.EmailEngine;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;
import java.util.HashMap;

/**
 * Sends user message to the administrator.
 * 
 * @author jxu
 * 
 */
@SuppressWarnings({ "serial" })
@Component
public class ContactServlet extends Controller {
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		//
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);

		String action = request.getParameter("action");

		if (StringUtil.isBlank(action)) {
			if (ub != null && ub.getId() > 0) {
				request.setAttribute("name", ub.getName());
				request.setAttribute("email", ub.getEmail());
			}
			forwardPage(Page.CONTACT, request, response);
		} else {
			if ("submit".equalsIgnoreCase(action)) {
				Validator v = new Validator(new ValidatorHelper(request, getConfigurationDao()));
				v.addValidation("name", Validator.NO_BLANKS);
				v.addValidation("email", Validator.IS_A_EMAIL);
				v.addValidation("subject", Validator.NO_BLANKS);
				v.addValidation("message", Validator.NO_BLANKS);

				HashMap errors = v.validate();

				FormProcessor fp = new FormProcessor(request);
				if (!errors.isEmpty()) {
					request.setAttribute("name", fp.getString("name"));
					request.setAttribute("email", fp.getString("email"));
					request.setAttribute("subject", fp.getString("subject"));
					request.setAttribute("message", fp.getString("message"));
					request.setAttribute("formMessages", errors);
					forwardPage(Page.CONTACT, request, response);
				} else {
					sendEmail(request, response);
				}
			} else {
				if (ub != null && ub.getId() > 0) {
					request.setAttribute("name", ub.getName());
					request.setAttribute("email", ub.getEmail());
				}
				forwardPage(Page.CONTACT, request, response);
			}

		}

	}

	private void sendEmail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		MessageFormat msg = new MessageFormat("");
		String emailBody;

		UserAccountBean ub = getUserAccountBean(request);

		FormProcessor fp = new FormProcessor(request);
		String name = fp.getString("name");
		String email = fp.getString("email");
		String subject = fp.getString("subject");
		String message = fp.getString("message");
		StudyUserRoleBean role = getCurrentRole(request);
		StudyBean currentStudy = getCurrentStudy(request);
		logger.info("Sending email...");

		msg.applyPattern(restext.getString("support_email_message_html_full"));
		emailBody = EmailUtil.getEmailBodyStart() + msg.format(new Object[] { name, role.getName(),
				currentStudy.getParentStudyId() == 0 ? resword.getString("study") : resword.getString("site"),
				currentStudy.getName(), request.getRequestURL().toString().replaceFirst(request.getServletPath(), ""),
				email, subject, message }) + EmailUtil.getEmailBodyEnd() + EmailUtil.getEmailFooter(getLocale());

		sendEmail(EmailEngine.getAdminEmail(), subject, emailBody, true, request);

		if (ub != null && ub.getId() > 0) {
			forwardPage(Page.MENU_SERVLET, request, response);
		} else {
			forwardPage(Page.LOGIN, request, response);
		}
	}

}
