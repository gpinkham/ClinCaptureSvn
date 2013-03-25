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
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2009 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.dao.hibernate.ConfigurationDao;
import org.akaza.openclinica.domain.technicaladmin.ConfigurationBean;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

import java.util.HashMap;

@SuppressWarnings({ "rawtypes" })
public class ConfigureServlet extends SecureController {

	private static final long serialVersionUID = 2729725318725545575L;
	private ConfigurationDao configurationDao;

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
		FormProcessor fp = new FormProcessor(request);

		if (!fp.isSubmitted()) {
			loadPresetValuesFromBean(fp);
			setPresetValues(fp.getPresetValues());
			forwardPage(Page.CONFIGURATION);
		} else {
			Validator v = new Validator(request);
			v.addValidation("lockcount", Validator.IS_AN_INTEGER);
			v.addValidation("lockcount", Validator.NO_BLANKS);
			v.addValidation("lockcount", Validator.IS_IN_RANGE, 1, 25);

			HashMap errors = v.validate();

			if (!errors.isEmpty()) {
				loadPresetValuesFromForm(fp);

				setPresetValues(fp.getPresetValues());
				setInputMessages(errors);
				forwardPage(Page.CONFIGURATION);

			} else {

				ConfigurationBean userLockSwitch = getConfigurationDao().findByKey("user.lock.switch");
				ConfigurationBean userLockAllowedFailedConsecutiveLoginAttempts = getConfigurationDao().findByKey(
						"user.lock.allowedFailedConsecutiveLoginAttempts");

				userLockSwitch.setValue(fp.getString("lockswitch"));
				userLockAllowedFailedConsecutiveLoginAttempts.setValue(fp.getString("lockcount"));
				getConfigurationDao().saveOrUpdate(userLockSwitch);
				getConfigurationDao().saveOrUpdate(userLockAllowedFailedConsecutiveLoginAttempts);
				addPageMessage(respage.getString("lockout_changes_have_been_saved"));
				forwardPage(Page.LIST_USER_ACCOUNTS_SERVLET);
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

	public ConfigurationDao getConfigurationDao() {
		configurationDao = this.configurationDao != null ? configurationDao : (ConfigurationDao) SpringServletAccess
				.getApplicationContext(context).getBean("configurationDao");
		return configurationDao;
	}

	@Override
	protected String getAdminServlet() {
		return SecureController.ADMIN_SERVLET_CODE;
	}
}
