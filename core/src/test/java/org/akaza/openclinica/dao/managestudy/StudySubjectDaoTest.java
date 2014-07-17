package org.akaza.openclinica.dao.managestudy;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * StudySubjectDaoTest class that tests the StudySubjectDao's methods.
 */
public class StudySubjectDaoTest extends DefaultAppContextTest {

	/**
	 * Test that the allowSDVSubject method return correct value.
	 * 
	 * @throws OpenClinicaException
	 *             the custom OpenClinicaException
	 */
	@Test
	public void testAllowSDVSubject() throws OpenClinicaException {
		boolean result = studySubjectDAO.allowSDVSubject(1, 1, 1);
		assertFalse(result);
	}

	/**
	 * Test that findByLabelAndStudy method returns a subject with randomization date.
	 * 
	 * @throws OpenClinicaException
	 *             the custom OpenClinicaException
	 */
	@Test
	public void testThatFindByLabelAndStudyReturnsSubjectWithRandomizationDate() throws OpenClinicaException {
		StudyBean sb = (StudyBean) studyDAO.findByPK(1);
		StudySubjectBean ss = studySubjectDAO.findByLabelAndStudy("ssID1", sb);
		assertNotNull(ss.getRandomizationDate());
	}

	/**
	 * Test that findByPK method returns a subject with randomization result.
	 * 
	 * @throws OpenClinicaException
	 *             the custom OpenClinicaException
	 */
	@Test
	public void testThatFindByPKReturnsSubjectWithRandomizationResult() throws OpenClinicaException {
		StudySubjectBean ss = (StudySubjectBean) studySubjectDAO.findByPK(1);
		assertNotNull(ss.getRandomizationResult());
	}

	/**
	 * Test that findByPK method returns a subject with correct randomization date.
	 * 
	 * @throws OpenClinicaException
	 *             the custom OpenClinicaException
	 * @throws ParseException
	 *             the custom ParseException
	 */
	@Test
	public void testThatFindByPKReturnsSubjectWithCorrectRandomizationDate() throws OpenClinicaException,
			ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy");
		String dateInString = "17-12-2013";
		Date expectedDate = sdf.parse(dateInString);
		StudySubjectBean ss = (StudySubjectBean) studySubjectDAO.findByPK(1);
		assertEquals(expectedDate, ss.getRandomizationDate());
	}

	/**
	 * Test that findByPK method returns a subject with correct randomization result.
	 * 
	 * @throws OpenClinicaException
	 *             the custom OpenClinicaException
	 */
	@Test
	public void testThatFindByPKReturntSubjectWithCorrectRandomizationResult() throws OpenClinicaException {
		StudySubjectBean ss = (StudySubjectBean) studySubjectDAO.findByPK(1);
		assertEquals("Surgery", ss.getRandomizationResult());
	}

	/**
	 * Test that findAllByStudyIdAndLimit method returns correct collection's size.
	 */
	@Test
	public void checkThatFindAllWithAllStatesByStudyIdMethodReturnsTheCorrectCollectionsSize() {
		List<StudySubjectBean> list = studySubjectDAO.findAllWithAllStatesByStudyId(1);
		assertEquals(list.size(), 1);
	}

	/**
	 * Test that findAllByStudyIdAndLimit method returns correct collection's size.
	 */
	@Test
	public void checkThatFindAllByStudyIdAndLimitMethodReturnsTheCorrectCollectionsSize() {
		List<StudySubjectBean> list = studySubjectDAO.findAllByStudyIdAndLimit(1, false);
		assertEquals(list.size(), 1);
		list = studySubjectDAO.findAllByStudyIdAndLimit(1, true);
		assertEquals(list.size(), 1);
	}

	/**
	 * Test that getWithFilterAndSort method returns correct collection's size.
	 */
	@Test
	public void checkThatGetWithFilterAndSortMethodReturnsTheCorrectCollectionsSize() {
		final int startFrom = 0;
		final int pageSize = 15;
		StudyBean currentStudy = new StudyBean();
		currentStudy.setId(1);
		List<StudySubjectBean> list = studySubjectDAO.getWithFilterAndSort(currentStudy, new FindSubjectsFilter(
				studyGroupClassDAO), new FindSubjectsSort(), startFrom, pageSize);
		assertEquals(list.size(), 1);
	}
}
