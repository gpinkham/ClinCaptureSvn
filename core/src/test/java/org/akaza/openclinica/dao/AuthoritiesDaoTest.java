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

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.domain.user.AuthoritiesBean;
import org.junit.Test;

public class AuthoritiesDaoTest extends DefaultAppContextTest {
	
	@Test
	public void testSaveOrUpdate() {
		
		AuthoritiesBean authorities = new AuthoritiesBean();
		authorities.setUsername("root");
		authorities.setAuthority("ROLE_USER");
		authorities.setId(-1);
		authorities = authoritiesDao.saveOrUpdate(authorities);

		assertNotNull("Persistant id is null", authorities.getId());
	}

	@Test
	public void testFindById() {

		AuthoritiesBean authorities = null;
		authorities = authoritiesDao.findById(-1);

		// Test Authorities
		assertNotNull("RuleSet is null", authorities);
		assertEquals("The id of the retrieved Domain Object should be -1", new Integer(-1), authorities.getId());
	}

	@Test
	public void testFindByUsername() {

		AuthoritiesBean authorities = null;
		authorities = authoritiesDao.findByUsername("root");

		// Test Authorities
		assertNotNull("RuleSet is null", authorities);
		assertEquals("The id of the retrieved Domain Object should be -1", new Integer(-1), authorities.getId());
	}

}
