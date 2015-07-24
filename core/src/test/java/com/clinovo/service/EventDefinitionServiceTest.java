package com.clinovo.service;

import java.util.Date;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.domain.SourceDataVerification;
import org.junit.Before;
import org.junit.Test;

/**
 * EventDefinitionServiceTest.
 */
public class EventDefinitionServiceTest extends DefaultAppContextTest {

	private StudyBean studyBean;
	private UserAccountBean userBean;
	private StudyEventDefinitionBean studyEventDefinitionBean;

	@Before
	public void before() {
		studyBean = (StudyBean) studyDAO.findByPK(1);
		userBean = (UserAccountBean) userAccountDAO.findByPK(1);
		studyEventDefinitionBean = (StudyEventDefinitionBean) studyEventDefinitionDAO.findByPK(1);
	}

	@Test
	public void testThatCreateStudyEventDefinitionMethodCreatesStudyEventDefinitionBeanCorrectly() {
		studyEventDefinitionBean = new StudyEventDefinitionBean();
		studyEventDefinitionBean.setName("test name");
		studyEventDefinitionBean.setRepeating(false);
		studyEventDefinitionBean.setCategory("");
		studyEventDefinitionBean.setDescription("");
		studyEventDefinitionBean.setType("scheduled");
		studyEventDefinitionBean.setOwner(userBean);
		studyEventDefinitionBean.setStudyId(studyBean.getId());
		eventDefinitionService.createStudyEventDefinition(studyBean, "root", studyEventDefinitionBean);
		assertTrue(studyEventDefinitionBean.getId() > 0);
	}

	@Test
	public void testThatUpdateOnlyTheStudyEventDefinitionMethodUpdatesStudyEventDefinitionBeanCorrectly() {
		String name = "test_name_".concat(Long.toString(new Date().getTime()));
		studyEventDefinitionBean.setName(name);
		studyEventDefinitionBean.setUpdater(userBean);
		eventDefinitionService.updateOnlyTheStudyEventDefinition(studyEventDefinitionBean);
		assertTrue(studyEventDefinitionDAO.findByPK(1).getName().equals(name));
	}

	@Test
	public void testThatFillEventDefinitionCrfsMethodFillsEventDefinitionCrfsCorrectly() {
		eventDefinitionService.fillEventDefinitionCrfs(studyBean, studyEventDefinitionBean);
		assertEquals(studyEventDefinitionBean.getEventDefinitionCrfs().size(), 3);
	}

	@Test
	public void testThatAddEventDefinitionCrfMethodAddsEventDefinitionCrfCorrectly() {
		CRFVersionBean crfVersionBean = (CRFVersionBean) crfVersionDao.findByPK(8);
		CRFBean crfBean = (CRFBean) crfdao.findByPK(crfVersionBean.getCrfId());
		EventDefinitionCRFBean eventDefinitionCrfBean = new EventDefinitionCRFBean();
		eventDefinitionCrfBean.setEventName(studyEventDefinitionBean.getName());
		eventDefinitionCrfBean.setStudyEventDefinitionId(studyEventDefinitionBean.getId());
		eventDefinitionCrfBean.setRequiredCRF(true);
		eventDefinitionCrfBean.setDefaultVersionId(crfVersionBean.getId());
		eventDefinitionCrfBean.setDefaultVersionName(crfVersionBean.getName());
		eventDefinitionCrfBean.setElectronicSignature(false);
		eventDefinitionCrfBean.setAcceptNewCrfVersions(false);
		eventDefinitionCrfBean.setHideCrf(false);
		eventDefinitionCrfBean.setDoubleEntry(false);
		eventDefinitionCrfBean.setEvaluatedCRF(false);
		eventDefinitionCrfBean.setSourceDataVerification(SourceDataVerification.NOTREQUIRED);
		eventDefinitionCrfBean.setCrfName(crfBean.getName());
		eventDefinitionCrfBean.setCrfId(crfVersionBean.getCrfId());
		eventDefinitionCrfBean.setStudyId(studyEventDefinitionBean.getStudyId());
		eventDefinitionCrfBean.setEmailStep("");
		eventDefinitionCrfBean.setEmailTo("");
		eventDefinitionCrfBean.setTabbingMode("leftToRight");
		eventDefinitionCrfBean.setOwner(userBean);
		eventDefinitionService.addEventDefinitionCrf(eventDefinitionCrfBean);
		eventDefinitionService.fillEventDefinitionCrfs(studyBean, studyEventDefinitionBean);
		assertEquals(studyEventDefinitionBean.getEventDefinitionCrfs().size(), 4);
	}

	@Test
	public void testThatFillEventDefinitionCrfsMethodWorksCorrectly() {
		eventDefinitionService.fillEventDefinitionCrfs(studyBean, studyEventDefinitionBean);
		assertTrue(studyEventDefinitionBean.getEventDefinitionCrfs().get(0).getEventName()
				.equals(studyEventDefinitionBean.getName()));
		assertTrue(studyEventDefinitionBean.getEventDefinitionCrfs().get(0).getCrfName().equals(
				crfdao.findByPK(studyEventDefinitionBean.getEventDefinitionCrfs().get(0).getCrfId()).getName()));
		assertTrue(studyEventDefinitionBean.getEventDefinitionCrfs().get(0).getDefaultVersionName().equals(crfVersionDao
				.findByPK(studyEventDefinitionBean.getEventDefinitionCrfs().get(0).getDefaultVersionId()).getName()));
	}

	@Test
	public void testThatRemoveStudyEventDefinitionMethodRemovesStudyEventDefinitionBeanCorrectly() throws Exception {
		assertTrue(studyEventDefinitionBean.getStatus() == Status.AVAILABLE);
		eventDefinitionService.removeStudyEventDefinition(studyEventDefinitionBean, userBean);
		studyEventDefinitionBean = ((StudyEventDefinitionBean) studyEventDefinitionDAO.findByPK(1));
		assertTrue(studyEventDefinitionBean.getStatus() == Status.DELETED);
	}

	@Test
	public void testThatRestoreStudyEventDefinitionMethodRestoresStudyEventDefinitionBeanCorrectly() throws Exception {
		studyEventDefinitionBean.setStatus(Status.DELETED);
		studyEventDefinitionDAO.updateStatus(studyEventDefinitionBean);
		studyEventDefinitionBean = ((StudyEventDefinitionBean) studyEventDefinitionDAO.findByPK(1));
		assertTrue(studyEventDefinitionBean.getStatus() == Status.DELETED);
		eventDefinitionService.restoreStudyEventDefinition(studyEventDefinitionBean, userBean);
		studyEventDefinitionBean = ((StudyEventDefinitionBean) studyEventDefinitionDAO.findByPK(1));
		assertTrue(studyEventDefinitionBean.getStatus() == Status.AVAILABLE);
	}

	@Test
	public void testThatUpdateStudyEventDefinitionMethodWorksFine() {
		studyEventDefinitionBean.setStatus(Status.DELETED);
		studyEventDefinitionBean.setUpdater(userBean);
		studyEventDefinitionDAO.updateStatus(studyEventDefinitionBean);
		studyEventDefinitionBean = (StudyEventDefinitionBean) studyEventDefinitionDAO.findByPK(1);
		assertTrue(studyEventDefinitionBean.getStatus() == Status.DELETED);
		String newName = "new_name".concat(Long.toString(new Date().getTime()));
		studyEventDefinitionBean.setUpdater(userBean);
		studyEventDefinitionBean.setName(newName);
		eventDefinitionService.updateOnlyTheStudyEventDefinition(studyEventDefinitionBean);
		studyEventDefinitionBean = (StudyEventDefinitionBean) studyEventDefinitionDAO.findByPK(1);
		assertTrue(studyEventDefinitionBean.getName().equals(newName));
		assertTrue(studyEventDefinitionBean.getStatus() == Status.AVAILABLE);
		assertTrue(studyEventDefinitionBean.getUpdaterId() == userBean.getId());
	}
}