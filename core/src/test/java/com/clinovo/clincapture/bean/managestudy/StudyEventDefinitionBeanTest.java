package com.clinovo.clincapture.bean.managestudy;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;

public class StudyEventDefinitionBeanTest {

	StudyEventDefinitionBean testBean = new StudyEventDefinitionBean();
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testNewValues() {
		assertEquals(testBean.getEmailDay(), 0);
		assertEquals(testBean.getMaxDay(), 0);
		assertEquals(testBean.getMinDay(), 0);
		assertEquals(testBean.getScheduleDay(), 0);
		assertFalse(testBean.getReferenceVisit());
	}
	
	@Test
	public void testSomeOldValues() {
		assertNull(testBean.getOid());
		assertNotNull(testBean.getStudyId());
		assertFalse(testBean.isPopulated());
		assertFalse(testBean.isRepeating());
	}

}
