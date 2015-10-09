package com.clinovo.service;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.junit.Test;

public class StudyEventServiceTest extends DefaultAppContextTest {

	@Test
	public void testThatRemoveStudyEventMethodWorksFine() throws Exception {
		UserAccountBean updater = (UserAccountBean) userAccountDAO.findByPK(1);

		StudyEventBean studyEventBean = (StudyEventBean) studyEventDao.findByPK(1);
		studyEventBean.setUpdater(updater);
		studyEventBean.setStatus(Status.AVAILABLE);
		studyEventBean.setSubjectEventStatus(SubjectEventStatus.SCHEDULED);
		studyEventDao.update(studyEventBean);
		assertTrue(studyEventBean.getStatus().equals(Status.AVAILABLE));
		assertTrue(studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.SCHEDULED));

		studyEventService.removeStudyEvent(studyEventBean, updater);
		studyEventBean = (StudyEventBean) studyEventDao.findByPK(1);
		assertTrue(studyEventBean.getStatus().equals(Status.DELETED));
		assertTrue(studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.REMOVED));
	}

	@Test
	public void testThatRestoreStudyEventMethodWorksFine() throws Exception {
		UserAccountBean updater = (UserAccountBean) userAccountDAO.findByPK(1);

		StudyEventBean studyEventBean = (StudyEventBean) studyEventDao.findByPK(1);
		studyEventBean.setUpdater(updater);
		studyEventBean.setStatus(Status.DELETED);
		studyEventBean.setSubjectEventStatus(SubjectEventStatus.REMOVED);
		studyEventDao.update(studyEventBean);
		assertTrue(studyEventBean.getStatus().equals(Status.DELETED));
		assertTrue(studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.REMOVED));

		studyEventService.restoreStudyEvent(studyEventBean, updater);
		studyEventBean = (StudyEventBean) studyEventDao.findByPK(1);
		assertTrue(studyEventBean.getStatus().equals(Status.AVAILABLE));
		assertTrue(studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.DATA_ENTRY_STARTED));
	}
}
