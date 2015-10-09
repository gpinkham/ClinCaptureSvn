package com.clinovo.clincapture.dao.managestudy;

import java.util.Date;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.junit.Test;

public class StudyEventDAOTest extends DefaultAppContextTest {

	@Test
	public void testUpdate() throws OpenClinicaException {
		Date date = new Date();
		UserAccountBean updater = new UserAccountBean();
		StudyEventBean seb = (StudyEventBean) studyEventDao.findByPK(3);
		updater.setId(seb.getUpdaterId());
		seb.setUpdater(updater);
		seb.setUpdatedDate(date);
		seb = (StudyEventBean) studyEventDao.update(seb);
		assertEquals(seb.getUpdatedDate(), date);
	}

	@Test
	public void testFindAll() throws OpenClinicaException {
		assertEquals(7, studyEventDao.findAll().size());
	}

	@Test
	public void testFindByPK() throws OpenClinicaException {
		assertEquals("test", ((StudyEventBean) studyEventDao.findByPK(4)).getLocation());
	}

	@Test
	public void testDelete() throws OpenClinicaException {
		studyEventDao.deleteByPK(4);
		assertEquals("", ((StudyEventBean) studyEventDao.findByPK(4)).getLocation());
	}

	@Test
	public void testThatFindStudyEventsByCrfVersionAndSubjectEventStatusReturnsCorrectSize()
			throws OpenClinicaException {
		assertEquals(
				studyEventDao.findStudyEventsByCrfVersionAndSubjectEventStatus(1,
						SubjectEventStatus.SOURCE_DATA_VERIFIED).size(), 0);
	}

	@Test
	public void testThatUpdateStatusMethodWorksFine() throws OpenClinicaException {
		StudyEventBean studyEventBean = (StudyEventBean) studyEventDao.findByPK(4);
		assertEquals(studyEventBean.getStatus(), Status.AVAILABLE);
		studyEventBean.setUpdater((UserAccountBean) userAccountDAO.findByPK(1));
		studyEventBean.setStatus(Status.DELETED);
		studyEventDao.updateStatus(studyEventBean);
		studyEventBean = (StudyEventBean) studyEventDao.findByPK(4);
		assertEquals(studyEventBean.getStatus(), Status.DELETED);
	}

	@Test
	public void testThatFindAllByDefinitionWithNotRemovedStudySubjectMethodWorksFine() {
		assertEquals(studyEventDao.findAllByDefinitionWithNotRemovedStudySubject(1).size(), 3);
	}

	@Test
	public void testThatDeleteStudyEventMethodWorksFine() {
		UserAccountBean userAccountBean = (UserAccountBean) userAccountDAO.findByPK(1);

		StudyEventBean studyEventBean = new StudyEventBean();
		studyEventBean.setSampleOrdinal(1);
		studyEventBean.setStudySubjectId(1);
		studyEventBean.setOwner(userAccountBean);
		studyEventBean.setDateStarted(new Date());
		studyEventBean.setCreatedDate(new Date());
		studyEventBean.setStatus(Status.AVAILABLE);
		studyEventBean.setStudyEventDefinitionId(1);
		studyEventBean.setSubjectEventStatus(SubjectEventStatus.NOT_SCHEDULED);
		int studyEventId = studyEventDao.create(studyEventBean).getId();
		assertTrue(studyEventId > 0);

		EventCRFBean eventCRFBean = new EventCRFBean();
		eventCRFBean.setCRFVersionId(1);
		eventCRFBean.setNotStarted(true);
		eventCRFBean.setStudySubjectId(1);
		eventCRFBean.setCompletionStatusId(1);
		eventCRFBean.setOwner(userAccountBean);
		eventCRFBean.setStatus(Status.AVAILABLE);
		eventCRFBean.setStudyEventId(studyEventId);
		int eventCrfId = eventCRFDAO.create(eventCRFBean).getId();
		assertTrue(eventCrfId > 0);

		studyEventDao.deleteStudyEvent(studyEventBean);

		assertEquals(eventCRFDAO.findByPK(eventCrfId).getId(), 0);
		assertEquals(studyEventDao.findByPK(studyEventId).getId(), 0);
	}
}
