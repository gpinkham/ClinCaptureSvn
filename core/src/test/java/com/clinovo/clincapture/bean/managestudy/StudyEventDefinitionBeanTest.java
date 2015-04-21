package com.clinovo.clincapture.bean.managestudy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;

public class StudyEventDefinitionBeanTest {

	StudyEventDefinitionBean testBean = new StudyEventDefinitionBean();
	
	@Test
	public void testTheEmailDayColumnExists() {
		assertEquals(testBean.getEmailDay(), 0);
	}
	
	@Test
	public void testTheMaxDayColumnExists() {
		assertEquals(testBean.getMaxDay(), 0);
	}
	
	@Test
	public void testTheMinDayColumnExists() {
		assertEquals(testBean.getMinDay(), 0);
	}
	
	@Test 
	public void testTheScheduleDayColumnExists() {
		assertEquals(testBean.getScheduleDay(), 0);
	}
	
	@Test
	public void testTheReferenceVisitExists() {
		assertFalse(testBean.getReferenceVisit());
	}
	
	@Test
	public void tesOidIsNull() {
		assertNull(testBean.getOid());
	}
	
	@Test 
	public void testStudyIdIsNotNull() {
		assertNotNull(testBean.getStudyId());
	}
	
	@Test
	public void testPopulatedIsFalseByDefault() {
		assertFalse(testBean.isPopulated());
	}
	
	@Test
	public void testRepeatingIsFalseByDefault() {
		assertFalse(testBean.isRepeating());
	}
	
	@Test
	public void testNameDefaultValue() {
		assertEquals("", testBean.getStudyName());
	}

	@Test
	public void testSetStudyName() {
		String beanStudyName = "StudyNameOfBean";
		testBean.setStudyName(beanStudyName);
		assertEquals(beanStudyName, testBean.getStudyName());
	}

}
