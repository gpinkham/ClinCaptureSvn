package org.akaza.openclinica.dao;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StudySubjectDaoTest extends DefaultAppContextTest {

	@Test
	public void testAllowSDVSubject() throws OpenClinicaException {
		boolean result = studySubjectDAO.allowSDVSubject(1, 1, 1);
		assertFalse(result);
	}

	@Test
	public void testThatfindByLabelAndStudyReturntSubjectWithRandomizationDate() throws OpenClinicaException {

		StudyBean sb = (StudyBean) studyDAO.findByPK(1);
		StudySubjectBean ss = (StudySubjectBean) studySubjectDAO.findByLabelAndStudy("ssID1", sb);
		assertNotNull(ss.getRandomizationDate());
	}

	@Test
	public void testThatFindByPKReturntSubjectWithRandomizationResult() throws OpenClinicaException {

		StudySubjectBean ss = (StudySubjectBean) studySubjectDAO.findByPK(1);
		assertNotNull(ss.getRandomizationResult());
	}

	@Test
	public void testThatFindByPKReturntSubjectWithCorrectRandomizationDate() throws OpenClinicaException,
			ParseException {

		SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy");
		String dateInString = "17-12-2013";
		Date expectedDate = sdf.parse(dateInString);
		StudySubjectBean ss = (StudySubjectBean) studySubjectDAO.findByPK(1);
		assertEquals(expectedDate, ss.getRandomizationDate());
	}

	@Test
	public void testThatFindByPKReturntSubjectWithCorrectRandomizationResult() throws OpenClinicaException {

		StudySubjectBean ss = (StudySubjectBean) studySubjectDAO.findByPK(1);
		assertEquals("Surgery", ss.getRandomizationResult());
	}
}
