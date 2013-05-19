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

import java.util.HashMap;

import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.dao.hibernate.ConfigurationDao;
import org.akaza.openclinica.dao.hibernate.PasswordRequirementsDao;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

/**
 * @author Leonel Gayard
 * @author Douglas Rodrigues (drodrigues@openclinica.com)
 */
// clinovo - ticket #120
public class ConfigurePasswordRequirementsServlet extends SecureController {
	private static final long serialVersionUID = 2729725318725545575L;

	@Override
	protected void mayProceed() throws InsufficientPermissionException {
		if (!ub.isSysAdmin()) {
			addPageMessage(respage.getString("no_have_correct_privilege_current_study")
					+ respage.getString("change_study_contact_sysadmin"));
			throw new InsufficientPermissionException(Page.MENU_SERVLET,
					resexception.getString("you_may_not_perform_administrative_functions"), "1");
		}
	}

	@Override
	protected void processRequest() throws Exception {
		FormProcessor fp = new FormProcessor(request);

		ConfigurationDao configurationDao = SpringServletAccess.getApplicationContext(context).getBean(
				ConfigurationDao.class);
		PasswordRequirementsDao passwordRequirementsDao = new PasswordRequirementsDao(configurationDao);

		if (!fp.isSubmitted()) {
			setPresetValues(new HashMap<String, Object>(passwordRequirementsDao.configs()));
			forwardPage(Page.CONFIGURATION_PASSWORD_REQUIREMENTS);

		} else {
			Validator v = new Validator(request);
			for (String key : passwordRequirementsDao.intConfigKeys()) {
				v.addValidation(key, Validator.IS_AN_INTEGER);
			}

			HashMap<?, ?> errors = v.validate();

			int minChars = fp.getInt("pwd.chars.min");
			int maxChars = fp.getInt("pwd.chars.max");
			if (minChars > 0 && maxChars > 0 && maxChars < minChars) {
				Validator.addError(errors, "pwd.chars.min", resexception.getString("pwd_min_greater_than_max"));
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

				addPageMessage(respage.getString("password_req_changes_have_been_saved"));
				forwardPage(Page.LIST_USER_ACCOUNTS_SERVLET);
			} else {
				setPresetValues(submittedValues(passwordRequirementsDao, fp));
				setInputMessages(errors);
				forwardPage(Page.CONFIGURATION_PASSWORD_REQUIREMENTS);
			}
		}
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
	protected String getAdminServlet() {
		return SecureController.ADMIN_SERVLET_CODE;
	}
}
