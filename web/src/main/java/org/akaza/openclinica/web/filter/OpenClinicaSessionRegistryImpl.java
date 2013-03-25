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

package org.akaza.openclinica.web.filter;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.dao.hibernate.AuditUserLoginDao;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.domain.technicaladmin.AuditUserLoginBean;
import org.akaza.openclinica.domain.technicaladmin.LoginStatus;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.User;

import java.util.Date;
import java.util.Locale;

import javax.sql.DataSource;

public class OpenClinicaSessionRegistryImpl extends SessionRegistryImpl {

	AuditUserLoginDao auditUserLoginDao;
	UserAccountDAO userAccountDao;
	DataSource dataSource;

	@Override
	public void removeSessionInformation(String sessionId) {
		SessionInformation info = getSessionInformation(sessionId);

		if (info != null) {
			User u = (User) info.getPrincipal();
			auditLogout(u.getUsername());
		}
		super.removeSessionInformation(sessionId);
	}

	void auditLogout(String username) {
		ResourceBundleProvider.updateLocale(new Locale("en_US"));
		UserAccountBean userAccount = (UserAccountBean) getUserAccountDao().findByUserName(username);
		AuditUserLoginBean auditUserLogin = new AuditUserLoginBean();
		auditUserLogin.setUserName(username);
		auditUserLogin.setLoginStatus(LoginStatus.SUCCESSFUL_LOGOUT);
		auditUserLogin.setLoginAttemptDate(new Date());
		auditUserLogin.setUserAccountId(userAccount != null ? userAccount.getId() : null);
		getAuditUserLoginDao().saveOrUpdate(auditUserLogin);
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public UserAccountDAO getUserAccountDao() {
		return userAccountDao != null ? userAccountDao : new UserAccountDAO(dataSource);
	}

	public AuditUserLoginDao getAuditUserLoginDao() {
		return auditUserLoginDao;
	}

	public void setAuditUserLoginDao(AuditUserLoginDao auditUserLoginDao) {
		this.auditUserLoginDao = auditUserLoginDao;
	}
}
