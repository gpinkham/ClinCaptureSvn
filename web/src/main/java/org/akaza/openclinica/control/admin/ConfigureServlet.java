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
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.domain.technicaladmin.ConfigurationBean;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

@SuppressWarnings({ "rawtypes" })
@Component
public class ConfigureServlet extends Controller {

	private static final long serialVersionUID = 2729725318725545575L;

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);

		if (!ub.isSysAdmin()) {
			addPageMessage(
					respage.getString("no_have_correct_privilege_current_study")
							+ respage.getString("change_study_contact_sysadmin"), request);
			throw new InsufficientPermissionException(Page.MENU_SERVLET,
					resexception.getString("you_may_not_perform_administrative_functions"), "1");
		}
	}

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		FormProcessor fp = new FormProcessor(request);

		if (!fp.isSubmitted()) {
			loadPresetValuesFromBean(fp);
			setPresetValues(fp.getPresetValues(), request);
			forwardPage(Page.CONFIGURATION, request, response);
		} else {
			Validator v = new Validator(new ValidatorHelper(request, getConfigurationDao()));
			v.addValidation("lockcount", Validator.IS_AN_INTEGER);
			v.addValidation("lockcount", Validator.NO_BLANKS);
			v.addValidation("lockcount", Validator.IS_IN_RANGE, 1, 25);

			HashMap errors = v.validate();

			if (!errors.isEmpty()) {
				loadPresetValuesFromForm(fp);

				setPresetValues(fp.getPresetValues(), request);
				setInputMessages(errors, request);
				forwardPage(Page.CONFIGURATION, request, response);

			} else {

				ConfigurationBean userLockSwitch = getConfigurationDao().findByKey("user.lock.switch");
				ConfigurationBean userLockAllowedFailedConsecutiveLoginAttempts = getConfigurationDao().findByKey(
						"user.lock.allowedFailedConsecutiveLoginAttempts");

				userLockSwitch.setValue(fp.getString("lockswitch"));
				userLockAllowedFailedConsecutiveLoginAttempts.setValue(fp.getString("lockcount"));
				getConfigurationDao().saveOrUpdate(userLockSwitch);
				getConfigurationDao().saveOrUpdate(userLockAllowedFailedConsecutiveLoginAttempts);
				addPageMessage(respage.getString("lockout_changes_have_been_saved"), request);
				forwardPage(Page.LIST_USER_ACCOUNTS_SERVLET, request, response);
			}
		}

	}

	private void loadPresetValuesFromBean(FormProcessor fp) {
		ConfigurationBean userLockSwitch = getConfigurationDao().findByKey("user.lock.switch");
		ConfigurationBean userLockAllowedFailedConsecutiveLoginAttempts = getConfigurationDao().findByKey(
				"user.lock.allowedFailedConsecutiveLoginAttempts");

		fp.addPresetValue("lockswitch", userLockSwitch.getValue());
		fp.addPresetValue("lockcount", userLockAllowedFailedConsecutiveLoginAttempts.getValue());
	}

	private void loadPresetValuesFromForm(FormProcessor fp) {
		fp.clearPresetValues();

		String textFields[] = { "lockswitch", "lockcount" };
		fp.setCurrentStringValuesAsPreset(textFields);
	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		return Controller.ADMIN_SERVLET_CODE;
	}
}
