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
 * copyright 2003-2009 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import com.clinovo.util.ValidatorHelper;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.hibernate.ConfigurationDao;
import org.akaza.openclinica.dao.hibernate.PasswordRequirementsDao;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.SQLInitServlet;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * @author Leonel Gayard
 * @author Douglas Rodrigues (drodrigues@openclinica.com)
 */
@Component
public class ConfigurePasswordRequirementsServlet extends Controller {
	private static final long serialVersionUID = 2729725318725545575L;

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);

		if (!ub.isSysAdmin()) {
			addPageMessage(
					getResPage().getString("no_have_correct_privilege_current_study")
							+ getResPage().getString("change_study_contact_sysadmin"), request);
			throw new InsufficientPermissionException(Page.MENU_SERVLET,
					getResException().getString("you_may_not_perform_administrative_functions"), "1");
		}
	}

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		FormProcessor fp = new FormProcessor(request);

		ConfigurationDao configurationDao = getConfigurationDao();
		PasswordRequirementsDao passwordRequirementsDao = new PasswordRequirementsDao(configurationDao);

		if (!fp.isSubmitted()) {
			setPresetValues(new HashMap<String, Object>(passwordRequirementsDao.configs()), request);
			forwardPage(Page.CONFIGURATION_PASSWORD_REQUIREMENTS, request, response);

		} else {
			Validator v = new Validator(new ValidatorHelper(request, getConfigurationDao()));
			for (String key : passwordRequirementsDao.intConfigKeys()) {
				v.addValidation(key, Validator.IS_A_POSITIVE_INTEGER);
			}

			HashMap<?, ?> errors = v.validate();

			int minChars = fp.getInt("pwd.chars.min");
			int maxChars = fp.getInt("pwd.chars.max");
			if (minChars > 0 && maxChars > 0 && maxChars < minChars) {
				Validator.addError(errors, "pwd.chars.min", getResException().getString("pwd_min_greater_than_max"));
			}
			if (minChars > 0 && passwordMustsGreaterThanMinLength(fp)) {
				Validator.addError(errors, "pwd.chars.min", getResException().getString("pwd_min_less_than_must_chars"));
			}
			if (passwordMustsGreaterThanMaxLength(fp)) {
				Validator.addError(errors, "pwd.chars.max", getResException().getString("pwd_max_less_than_must_chars"));
			}
			if (errors.isEmpty()) {
				passwordRequirementsDao.setHasLower(Boolean.valueOf(fp.getString("pwd.chars.case.lower")));
				passwordRequirementsDao.setHasUpper(Boolean.valueOf(fp.getString("pwd.chars.case.upper")));
				passwordRequirementsDao.setHasDigits(Boolean.valueOf(fp.getString("pwd.chars.digits")));
				passwordRequirementsDao.setHasSpecials(Boolean.valueOf(fp.getString("pwd.chars.specials")));

				passwordRequirementsDao.setMinLength(fp.getInt("pwd.chars.min"));
				passwordRequirementsDao.setMaxLength(fp.getInt("pwd.chars.max"));
				passwordRequirementsDao.setExpirationDays(fp.getInt("pwd.expiration.days"));
				passwordRequirementsDao.setChangeRequired(fp.getInt("pwd.change.required"));

				CoreResources.setField("pwd.expiration.days", fp.getString("pwd.expiration.days"));
				CoreResources.setField("pwd.change.required", fp.getString("pwd.change.required"));
				SQLInitServlet.setField("pwd.expiration.days", fp.getString("pwd.expiration.days"));
				SQLInitServlet.setField("pwd.change.required", fp.getString("pwd.change.required"));

				addPageMessage(getResPage().getString("password_req_changes_have_been_saved"), request);
				forwardPage(Page.LIST_USER_ACCOUNTS_SERVLET, request, response);
			} else {
				setPresetValues(submittedValues(passwordRequirementsDao, fp), request);
				setInputMessages(errors, request);
				forwardPage(Page.CONFIGURATION_PASSWORD_REQUIREMENTS, request, response);
			}
		}
	}

	boolean passwordMustsGreaterThanMaxLength(FormProcessor fp) {
		int maxChars = fp.getInt("pwd.chars.max");
		int mustsCount = 0;
		Boolean lowerCase = Boolean.valueOf(fp.getString("pwd.chars.case.lower"));
		Boolean upperCase = Boolean.valueOf(fp.getString("pwd.chars.case.upper"));
		Boolean digits = Boolean.valueOf(fp.getString("pwd.chars.digits"));
		Boolean specials = Boolean.valueOf(fp.getString("pwd.chars.specials"));
		if (lowerCase) {
			mustsCount++;
		}
		if (upperCase) {
			mustsCount++;
		}
		if (digits) {
			mustsCount++;
		}
		if (specials) {
			mustsCount++;
		}
		return maxChars > 0 && maxChars <= mustsCount;
	}

	boolean passwordMustsGreaterThanMinLength(FormProcessor fp) {
		int minChars = fp.getInt("pwd.chars.min");
		int mustsCount = 0;
		Boolean lowerCase = Boolean.valueOf(fp.getString("pwd.chars.case.lower"));
		Boolean upperCase = Boolean.valueOf(fp.getString("pwd.chars.case.upper"));
		Boolean digits = Boolean.valueOf(fp.getString("pwd.chars.digits"));
		Boolean specials = Boolean.valueOf(fp.getString("pwd.chars.specials"));
		if (lowerCase) {
			mustsCount++;
		}
		if (upperCase) {
			mustsCount++;
		}
		if (digits) {
			mustsCount++;
		}
		if (specials) {
			mustsCount++;
		}
		return mustsCount > minChars;
	}

	private HashMap<String, Object> submittedValues(PasswordRequirementsDao passwordRequirementsDao, FormProcessor fp) {
		HashMap<String, Object> values = new HashMap<String, Object>();
		for (String key : passwordRequirementsDao.boolConfigKeys()) {
			String val = fp.getString(key);
			if (val != null) {
				values.put(key, Boolean.valueOf(val));
			}
		}
		for (String key : passwordRequirementsDao.intConfigKeys()) {
			String val = fp.getString(key);
			if (val != null) {
				values.put(key, val);
			}
		}
		return values;
	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		return Controller.ADMIN_SERVLET_CODE;
	}
}
