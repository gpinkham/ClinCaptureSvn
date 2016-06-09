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

package org.akaza.openclinica.dao.managestudy;

import java.util.HashMap;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("rawtypes")
public class StudyDAOTest extends DefaultAppContextTest {

	private UserAccountBean user;
	private StudyUserRoleBean sur;

	@Before
	public void prepare() {
		user = new UserAccountBean();
		user.setId(2);
		user.setName("demo_dm");
		UserAccountBean owner = new UserAccountBean();
		owner.setId(1);
		user.setOwner(owner);
		userAccountDAO.create(user);
		sur = new StudyUserRoleBean();
		sur.setUserName(user.getName());
		sur.setStatus(Status.AVAILABLE);
		sur.setOwner(owner);
		sur.setStudyId(1);
		userAccountDAO.createStudyUserRole(user, sur);
	}

	@Test
	public void testFindByPKNotReturnNull() throws OpenClinicaException {
		assertNotNull(studyDAO.findByPK(1));
	}

	@Test
	public void testFindByPKReturnsCorrectValue() throws OpenClinicaException {
		assertEquals("Default Study", studyDAO.findByPK(1).getName());
	}

	@Test
	public void testThatFindAllActiveWhereUserHasActiveRoleReturnsCorrectResult() {
		assertEquals(1, studyDAO.findAllActiveWhereUserHasActiveRole(user.getName()).size());
	}

	@Test
	public void testThatFindAllActiveWhereUserHasActiveRoleReturnsOnlyActive() {
		sur.setStatus(Status.DELETED);
		userAccountDAO.updateStudyUserRole(sur);
		assertEquals(0, studyDAO.findAllActiveWhereUserHasActiveRole(user.getName()).size());
	}

	@Test
	public void testThatFindAllWhereCRFIsUsedReturnsCorrectResult() {
		assertEquals(1, studyDAO.findAllActiveWhereCRFIsUsed(1).size());
	}

	@After
	public void cleanup() {
		userAccountDAO.execute("delete from authorities where username = '".concat(user.getName()).concat("'"),
				new HashMap());
		userAccountDAO.execute("delete from study_user_role where user_name = '".concat(user.getName()).concat("'"),
				new HashMap());
		userAccountDAO.execute("delete from user_account where user_name = '".concat(user.getName()).concat("'"),
				new HashMap());
	}
}
