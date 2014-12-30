package org.akaza.openclinica.dao.service;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.junit.Test;

public class StudyParameterValueDAOTest extends DefaultAppContextTest {

	@Test
	public void testThatCreateWorksFine() throws OpenClinicaException {
		StudyParameterValueBean studyParameterValueBean = new StudyParameterValueBean();
		studyParameterValueBean.setStudyId(1);
		studyParameterValueBean.setValue("");
		studyParameterValueBean.setParameter("interviewerNameDefault");
		studyParameterValueBean = (StudyParameterValueBean) studyParameterValueDAO.create(studyParameterValueBean);
		assertTrue(studyParameterValueBean.getId() > 0);
	}
}