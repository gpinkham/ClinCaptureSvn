package com.clinovo.clincapture.bean.managestudy;

import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

public class StudySubjectBeanTest {

	private StudySubjectBean ssBean = null;

	@Before
	public void setUp() throws Exception {
		ssBean = new StudySubjectBean();
	}

	@Test
	public void testAllDefaultValues() {

		assertEquals("", ssBean.getLabel());
		assertEquals("", ssBean.getSecondaryLabel());
		assertEquals("", ssBean.getUniqueIdentifier());
		assertEquals("", ssBean.getStudyName());
		assertEquals('m', ssBean.getGender());
		assertEquals(0, ssBean.getSubjectId());
		assertEquals(0, ssBean.getStudyId());

		assertNull(ssBean.getEnrollmentDate());
		assertNull(ssBean.getDateOfBirth());
		assertNull(ssBean.getOid());
		assertNull(ssBean.getRandomizationDate());
		assertNull(ssBean.getRandomizationResult());
	}

	@Test
	public void testInequalityOfClasses() {
		ssBean.setLabel("SS002");
		ssBean.setOid("SS_SS002");

		assertNotSame(ssBean, new StudySubjectBean());
		assertFalse(ssBean.getLabel() == new StudySubjectBean().getLabel());
	}
}
