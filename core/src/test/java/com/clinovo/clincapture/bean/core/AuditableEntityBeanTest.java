/*******************************************************************************
 * Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.clinovo.clincapture.bean.core;

import static org.junit.Assert.*;

import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

/**
 * User: Pavel Date: 12.10.12
 */
public class AuditableEntityBeanTest {

	private AuditableEntityBean auditableEntity;

	private static final Date DATA0 = new Date(0);
	private static final Date DATA_CURRENT = new Date();

	@Before
	public void setUp() throws Exception {
		auditableEntity = new AuditableEntityBean();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDefault() {
		assertEquals(DATA0, auditableEntity.getCreatedDate());
		assertEquals(DATA0, auditableEntity.getUpdatedDate());
		assertEquals(0, auditableEntity.getOwnerId());
		assertNull(auditableEntity.getOwner());
		assertEquals(0, auditableEntity.getUpdaterId());
		assertNull(auditableEntity.getUpdater());
		assertNull(auditableEntity.getStatus());
		assertNull(auditableEntity.getOldStatus());
	}

	@Test
	public void testEqualDefault() {
		AuditableEntityBean defAuditableEntity = new AuditableEntityBean();
		assertEquals(auditableEntity, defAuditableEntity);
	}

	// TODO is comparison correct?
	@Test
	public void testEqualsCreateDate() {
		AuditableEntityBean customAuditableEntity = new AuditableEntityBean();
		customAuditableEntity.setCreatedDate(DATA_CURRENT);
		assertEquals(customAuditableEntity, auditableEntity);
	}

	// TODO is comparison correct?
	@Test
	public void testEqualsUpdateDate() {
		AuditableEntityBean customAuditableEntity = new AuditableEntityBean();
		customAuditableEntity.setUpdatedDate(DATA_CURRENT);
		assertEquals(customAuditableEntity, auditableEntity);
	}

	// TODO is comparison correct?
	@Test
	public void testEqualsOwner() {
		AuditableEntityBean customAuditableEntity = new AuditableEntityBean();
		customAuditableEntity.setOwner(new UserAccountBean());
		assertEquals(customAuditableEntity, auditableEntity);
	}

	// TODO is comparison correct?
	@Test
	public void testEqualsUpdater() {
		AuditableEntityBean customAuditableEntity = new AuditableEntityBean();
		customAuditableEntity.setUpdater(new UserAccountBean());
		assertEquals(customAuditableEntity, auditableEntity);
	}

	// TODO is comparison correct?
	@Test
	public void testEqualsStatus() {
		AuditableEntityBean customAuditableEntity = new AuditableEntityBean();
		customAuditableEntity.setStatus(Status.INVALID);
		assertEquals(customAuditableEntity, auditableEntity);
	}

	// TODO is comparison correct?
	@Test
	public void testEqualsOldStatus() {
		AuditableEntityBean customAuditableEntity = new AuditableEntityBean();
		customAuditableEntity.setOldStatus(Status.INVALID);
		assertEquals(customAuditableEntity, auditableEntity);
	}

	@Test
	public void testNotEqualNull() {
		assertNotSame(auditableEntity, null);
	}
}
