package com.clinovo.service;

import java.util.Date;
import java.util.HashMap;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("rawtypes")
public class StudyServiceTest extends DefaultAppContextTest {

	private StudyBean site;
	private StudyBean study;
	private StudyBean currentStudyBean;
	private UserAccountBean userAccountBean;
	private StudyUserRoleBean currentUserRole;

	private void createStudy(UserAccountBean owner, Status status) throws Exception {
		study = new StudyBean();
		study.setOwner(owner);
		study.setStatus(status);
		study.setCreatedDate(new Date());
		study.setName("study_".concat(Long.toString(new Date().getTime())));
		studyDAO.create(study);
	}

	private void createSite(int studyId, UserAccountBean owner, Status status) throws Exception {
		site = new StudyBean();
		site.setOwner(owner);
		site.setStatus(status);
		site.setParentStudyId(studyId);
		site.setCreatedDate(new Date());
		site.setName("site_".concat(Long.toString(new Date().getTime())));
		studyDAO.create(site);
	}

	@Before
	public void before() {
		userAccountBean = (UserAccountBean) userAccountDAO.findByPK(1);
		currentUserRole = userAccountBean.getRoleByStudy(1);
		currentStudyBean = (StudyBean) studyDAO.findByPK(1);
	}

	@After
	public void after() {
		if (site != null) {
			studyDAO.execute("delete from study where study_id = ".concat(Integer.toString(site.getId())),
					new HashMap());
		}
		if (study != null) {
			studyDAO.execute("delete from study where study_id = ".concat(Integer.toString(study.getId())),
					new HashMap());
		}
	}

	@Test
	public void testThatRestoreStudyMethodWorksFine() throws Exception {
		createStudy(userAccountBean, Status.DELETED);

		StudyBean studyBean = (StudyBean) studyDAO.findByPK(study.getId());
		assertTrue(studyBean.getStatus().equals(Status.DELETED));

		studyService.restoreStudy(studyBean, currentStudyBean, currentUserRole, userAccountBean);
		studyBean = (StudyBean) studyDAO.findByPK(study.getId());
		assertTrue(studyBean.getStatus().equals(Status.AVAILABLE));
	}

	@Test
	public void testThatRemoveStudyMethodWorksFine() throws Exception {
		createStudy(userAccountBean, Status.AVAILABLE);

		StudyBean studyBean = (StudyBean) studyDAO.findByPK(study.getId());
		assertTrue(studyBean.getStatus().equals(Status.AVAILABLE));

		studyService.removeStudy(studyBean, currentStudyBean, currentUserRole, userAccountBean);
		studyBean = (StudyBean) studyDAO.findByPK(study.getId());
		assertTrue(studyBean.getStatus().equals(Status.DELETED));
	}

	@Test
	public void testThatRestoreSiteMethodWorksFine() throws Exception {
		createStudy(userAccountBean, Status.DELETED);
		createSite(study.getId(), userAccountBean, Status.DELETED);

		StudyBean siteBean = (StudyBean) studyDAO.findByPK(study.getId());
		assertTrue(siteBean.getStatus().equals(Status.DELETED));

		studyService.restoreStudy(siteBean, currentStudyBean, currentUserRole, userAccountBean);
		siteBean = (StudyBean) studyDAO.findByPK(study.getId());
		assertTrue(siteBean.getStatus().equals(Status.AVAILABLE));
	}

	@Test
	public void testThatRemoveSiteMethodWorksFine() throws Exception {
		createStudy(userAccountBean, Status.AVAILABLE);
		createSite(study.getId(), userAccountBean, Status.AVAILABLE);

		StudyBean siteBean = (StudyBean) studyDAO.findByPK(study.getId());
		assertTrue(siteBean.getStatus().equals(Status.AVAILABLE));

		studyService.removeStudy(siteBean, currentStudyBean, currentUserRole, userAccountBean);
		siteBean = (StudyBean) studyDAO.findByPK(study.getId());
		assertTrue(siteBean.getStatus().equals(Status.DELETED));
	}
}
