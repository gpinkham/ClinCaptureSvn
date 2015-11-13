package com.clinovo.service;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.junit.Test;

public class StudySubjectServiceTest extends DefaultAppContextTest {

	@Test
	public void testThatRemoveStudySubjectMethodWorksFine() throws Exception {
		UserAccountBean updater = (UserAccountBean) userAccountDAO.findByPK(1);

		StudySubjectBean studySubjectBean = (StudySubjectBean) studySubjectDAO.findByPK(1);
		studySubjectBean.setUpdater(updater);
		studySubjectBean.setStatus(Status.AVAILABLE);
		studySubjectDAO.update(studySubjectBean);
		assertTrue(studySubjectBean.getStatus().equals(Status.AVAILABLE));

		studySubjectService.removeStudySubject(studySubjectBean, updater);
		studySubjectBean = (StudySubjectBean) studySubjectDAO.findByPK(1);
		assertTrue(studySubjectBean.getStatus().equals(Status.DELETED));
	}

	@Test
	public void testThatRestoreStudySubjectMethodWorksFine() throws Exception {
		UserAccountBean updater = (UserAccountBean) userAccountDAO.findByPK(1);

		StudySubjectBean studySubjectBean = (StudySubjectBean) studySubjectDAO.findByPK(1);
		studySubjectBean.setUpdater(updater);
		studySubjectBean.setStatus(Status.DELETED);
		studySubjectDAO.update(studySubjectBean);
		assertTrue(studySubjectBean.getStatus().equals(Status.DELETED));

		studySubjectService.restoreStudySubject(studySubjectBean, updater);
		studySubjectBean = (StudySubjectBean) studySubjectDAO.findByPK(1);
		assertTrue(studySubjectBean.getStatus().equals(Status.AVAILABLE));
	}
}
