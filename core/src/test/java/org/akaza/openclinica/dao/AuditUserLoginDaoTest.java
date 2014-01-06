/*******************************************************************************
 * Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.akaza.openclinica.dao;

import java.util.Date;
import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.domain.technicaladmin.AuditUserLoginBean;
import org.akaza.openclinica.domain.technicaladmin.LoginStatus;
import org.junit.Test;

public class AuditUserLoginDaoTest extends DefaultAppContextTest {

	@Test
	public void testSaveOrUpdate() {
		AuditUserLoginBean auditUserLoginBean = new AuditUserLoginBean();
		auditUserLoginBean.setUserName("testUser");
		auditUserLoginBean.setLoginAttemptDate(new Date());
		auditUserLoginBean.setLoginStatus(LoginStatus.SUCCESSFUL_LOGIN);

		auditUserLoginBean = auditUserLoginDao.saveOrUpdate(auditUserLoginBean);

		assertNotNull("Persistant id is null", auditUserLoginBean.getId());
	}

	@Test
	public void testfindById() {

		AuditUserLoginBean auditUserLoginBean = auditUserLoginDao.findById(1);

		assertEquals("UserName should be root", "root", auditUserLoginBean.getUserName());
	}
}
