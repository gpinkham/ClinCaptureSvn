/*******************************************************************************
 * Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.clinovo.clincapture.bean.admin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Locale;

import org.akaza.openclinica.bean.admin.AuditEventBean;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings({"unchecked"})
public class AuditEventBeanTest {

	private AuditEventBean auditEvent;
	private static final String EMPTY = "";
	private static final String QQQ = "???";
	private static final String NULL_STRING = "NULL";
	private static final String AUDIT_MESSAGE_KEY = "__added_a_study_event";
	private static final String AUDIT_MESSAGE_VAL = "Added a Study Event";
	private static final String WRONG_MESSAGE_KEY = "wrong_message_key";

	@Before
	public void setUp() throws Exception {
		ResourceBundleProvider.updateLocale(Locale.ENGLISH);
		auditEvent = new AuditEventBean();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDefault() {
		assertNull(auditEvent.getAuditDate());
		assertEquals(EMPTY, auditEvent.getAuditTable());
		assertEquals(0, auditEvent.getUserId());
		assertEquals(0, auditEvent.getEntityId());
		assertEquals(QQQ.concat(EMPTY).concat(QQQ), auditEvent.getReasonForChange());
		assertEquals(QQQ.concat(EMPTY).concat(QQQ), auditEvent.getActionMessage());
		assertEquals(EMPTY, auditEvent.getColumnName());
		assertEquals(EMPTY, auditEvent.getOldValue());
		assertEquals(EMPTY, auditEvent.getNewValue());
		assertEquals(0, auditEvent.getUpdateCount());
		assertNotNull(auditEvent.getChanges());
		assertTrue(auditEvent.getChanges().isEmpty());
		assertNotNull(auditEvent.getOtherInfo());
		assertTrue(auditEvent.getOtherInfo().isEmpty());
		assertEquals(NULL_STRING, auditEvent.getStudyName());
		assertEquals(NULL_STRING, auditEvent.getSubjectName());
		assertEquals(0, auditEvent.getStudyId());
		assertEquals(0, auditEvent.getSubjectId());
	}

	@Test
	public void testEqualsDefault() {
		assertEquals(new AuditEventBean(), auditEvent);
	}

	// TODO is comparison correct?
	@Test
	public void testEqualsCustom() {
		AuditEventBean customAuditEvent = new AuditEventBean();
		customAuditEvent.setAuditDate(new Date());
		customAuditEvent.setAuditTable("auditTable");
		customAuditEvent.setUserId(1);
		customAuditEvent.setEntityId(1);
		customAuditEvent.setReasonForChange(AUDIT_MESSAGE_KEY);
		customAuditEvent.setActionMessage(AUDIT_MESSAGE_KEY);
		customAuditEvent.setColumnName("column");
		customAuditEvent.setOldValue("old value");
		customAuditEvent.setNewValue("new value");
		customAuditEvent.setUpdateCount(1);
		customAuditEvent.getChanges().put("key", "value");
		customAuditEvent.getOtherInfo().put("key", "value");
		customAuditEvent.setStudyName("study");
		customAuditEvent.setSubjectName("subject");
		customAuditEvent.setStudyId(1);
		customAuditEvent.setSubjectId(1);

		assertEquals(auditEvent, customAuditEvent);
	}

	@Test
	public void testReasonForChanges() {
		AuditEventBean customAuditEvent = new AuditEventBean();
		customAuditEvent.setReasonForChange(AUDIT_MESSAGE_KEY);
		assertEquals(AUDIT_MESSAGE_KEY, customAuditEvent.getReasonForChangeKey());
		assertEquals(AUDIT_MESSAGE_VAL, customAuditEvent.getReasonForChange());
	}

	@Test
	public void testReasonForChangesWrongKey() {
		AuditEventBean customAuditEvent = new AuditEventBean();
		customAuditEvent.setReasonForChange(WRONG_MESSAGE_KEY);
		assertEquals(WRONG_MESSAGE_KEY, customAuditEvent.getReasonForChangeKey());
		assertEquals(QQQ.concat(WRONG_MESSAGE_KEY).concat(QQQ), customAuditEvent.getReasonForChange());
	}

	@Test
	public void testActionMessage() {
		AuditEventBean customAuditEvent = new AuditEventBean();
		customAuditEvent.setActionMessage(AUDIT_MESSAGE_KEY);
		assertEquals(AUDIT_MESSAGE_KEY, customAuditEvent.getActionMessageKey());
		assertEquals(AUDIT_MESSAGE_VAL, customAuditEvent.getActionMessage());
	}

	@Test
	public void testActionMessageWrongKey() {
		AuditEventBean customAuditEvent = new AuditEventBean();
		customAuditEvent.setActionMessage(WRONG_MESSAGE_KEY);
		assertEquals(WRONG_MESSAGE_KEY, customAuditEvent.getActionMessageKey());
		assertEquals(QQQ.concat(WRONG_MESSAGE_KEY).concat(QQQ), customAuditEvent.getActionMessage());
	}
}
