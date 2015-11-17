/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 * <p/>
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 * <p/>
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use.
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.
 * <p/>
 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/

package com.clinovo.states;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.ItemDataType;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.domain.SourceDataVerification;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.clinovo.model.CodedItem;
import com.clinovo.model.CodedItemElement;

@SuppressWarnings("rawtypes")
public class StatesTest extends DefaultAppContextTest {

	public static final int STUDY_EVENT_FEFINITION_CRF_ID = 2;
	public static final int EVENT_DEFINITION_CRF_ID1 = 3;
	public static final int EVENT_DEFINITION_CRF_ID2 = 4;
	public static final int DEFAULT_STUDY_ID = 1;
	public static final int STUDY_SUBJECT_ID = 1;
	public static final int STUDY_EVENT_ID = 2;
	public static final int EVENT_CRF_ID = 2;
	public static final int SUBJECT_ID = 1;
	public static final int UPDATER_ID = 1;

	private long timestamp;
	private UserAccountBean updater;

	private StudyBean site;
	private StudyBean study;
	private SubjectBean subjectBean;
	private EventCRFBean eventCRFBean;
	private CRFVersionBean crfVersionBean;
	private StudyEventBean studyEventBean;
	private StudySubjectBean studySubjectBean;
	private EventDefinitionCRFBean studyEventDefinitionCRFBean1;
	private EventDefinitionCRFBean studyEventDefinitionCRFBean2;
	private EventDefinitionCRFBean siteEventDefinitionCRFBean1;
	private EventDefinitionCRFBean siteEventDefinitionCRFBean2;
	private StudyEventDefinitionBean studyEventDefinitionBean;

	Map<Integer, Status> itemDataStatusMap = new HashMap<Integer, Status>();

	@Before
	public void before() throws Exception {
		timestamp = new Date().getTime();
		updater = (UserAccountBean) userAccountDAO.findByPK(UPDATER_ID);

		createStudy();
		createSite();

		fillItemDataStatusMap();

		subjectBean = (SubjectBean) subjectDAO.findByPK(SUBJECT_ID);
		eventCRFBean = (EventCRFBean) eventCRFDAO.findByPK(EVENT_CRF_ID);
		studyEventBean = (StudyEventBean) studyEventDao.findByPK(STUDY_EVENT_ID);
		studySubjectBean = (StudySubjectBean) studySubjectDAO.findByPK(STUDY_SUBJECT_ID);
		crfVersionBean = (CRFVersionBean) crfVersionDao.findByPK(eventCRFBean.getCRFVersionId());
		studyEventDefinitionBean = (StudyEventDefinitionBean) studyEventDefinitionDAO
				.findByPK(STUDY_EVENT_FEFINITION_CRF_ID);
		studyEventDefinitionCRFBean1 = (EventDefinitionCRFBean) eventDefinitionCRFDAO
				.findByPK(EVENT_DEFINITION_CRF_ID1);
		studyEventDefinitionCRFBean2 = (EventDefinitionCRFBean) eventDefinitionCRFDAO
				.findByPK(EVENT_DEFINITION_CRF_ID2);
		studyEventDefinitionCRFBean1.setCrf((CRFBean) crfdao.findByPK(studyEventDefinitionCRFBean1.getCrfId()));
		studyEventDefinitionCRFBean2.setCrf((CRFBean) crfdao.findByPK(studyEventDefinitionCRFBean2.getCrfId()));

		studySubjectBean.setStudyId(site.getId());
		studySubjectBean.setUpdater(updater);
		studySubjectDAO.update(studySubjectBean);

		studyEventDefinitionCRFBean1.setStudyId(study.getId());
		studyEventDefinitionCRFBean1.setUpdater(updater);
		eventDefinitionCRFDAO.update(studyEventDefinitionCRFBean1);

		studyEventDefinitionCRFBean2.setStudyId(study.getId());
		studyEventDefinitionCRFBean2.setUpdater(updater);
		eventDefinitionCRFDAO.update(studyEventDefinitionCRFBean2);

		studyEventDefinitionBean.setStudyId(study.getId());
		studyEventDefinitionBean.setUpdater(updater);
		studyEventDefinitionDAO.update(studyEventDefinitionBean);

		siteEventDefinitionCRFBean1 = (EventDefinitionCRFBean) eventDefinitionCRFDAO.findByPK(EVENT_DEFINITION_CRF_ID1);
		siteEventDefinitionCRFBean1.setId(0);
		siteEventDefinitionCRFBean1.setUpdater(updater);
		siteEventDefinitionCRFBean1.setStudyId(site.getId());
		siteEventDefinitionCRFBean1.setParentId(studyEventDefinitionCRFBean1.getId());
		eventDefinitionCRFDAO.create(siteEventDefinitionCRFBean1);

		siteEventDefinitionCRFBean2 = (EventDefinitionCRFBean) eventDefinitionCRFDAO.findByPK(EVENT_DEFINITION_CRF_ID2);
		siteEventDefinitionCRFBean2.setId(0);
		siteEventDefinitionCRFBean2.setUpdater(updater);
		siteEventDefinitionCRFBean2.setStudyId(site.getId());
		siteEventDefinitionCRFBean2.setParentId(studyEventDefinitionCRFBean2.getId());
		eventDefinitionCRFDAO.create(siteEventDefinitionCRFBean2);
	}

	@After
	public void after() throws Exception {
		studySubjectBean.setUpdater(updater);
		studySubjectBean.setStudyId(DEFAULT_STUDY_ID);
		studySubjectDAO.update(studySubjectBean);

		studyEventDefinitionCRFBean1.setUpdater(updater);
		studyEventDefinitionCRFBean1.setStudyId(DEFAULT_STUDY_ID);
		eventDefinitionCRFDAO.update(studyEventDefinitionCRFBean1);

		studyEventDefinitionCRFBean2.setUpdater(updater);
		studyEventDefinitionCRFBean2.setStudyId(DEFAULT_STUDY_ID);
		eventDefinitionCRFDAO.update(studyEventDefinitionCRFBean2);

		studyEventDefinitionBean.setUpdater(updater);
		studyEventDefinitionBean.setStudyId(DEFAULT_STUDY_ID);
		studyEventDefinitionDAO.update(studyEventDefinitionBean);

		eventDefinitionCRFDAO.execute("delete from event_definition_crf where event_definition_crf_id in ("
				.concat(Integer.toString(siteEventDefinitionCRFBean1.getId())).concat(",")
				.concat(Integer.toString(siteEventDefinitionCRFBean2.getId())).concat(")"), new HashMap());

		studyDAO.execute("delete from study where study_id = ".concat(Integer.toString(site.getId())), new HashMap());
		studyDAO.execute("delete from study where study_id = ".concat(Integer.toString(study.getId())), new HashMap());
	}

	private void fillItemDataStatusMap() throws Exception {
		List<ItemDataBean> itemDataList = itemDataDAO.findAllByEventCRFId(EVENT_CRF_ID);
		for (ItemDataBean itemDataBean : itemDataList) {
			itemDataStatusMap.put(itemDataBean.getId(), itemDataBean.getStatus());
		}
	}

	private void createStudy() throws Exception {
		study = new StudyBean();
		study.setName("study_".concat(Long.toString(timestamp)));
		study.setOwner(updater);
		study.setCreatedDate(new Date());
		study.setStatus(Status.AVAILABLE);
		study = (StudyBean) studyDAO.create(study);
	}

	private void createSite() throws Exception {
		site = new StudyBean();
		site.setName("site_".concat(Long.toString(timestamp)));
		site.setParentStudyId(study.getId());
		site.setOwner(updater);
		site.setCreatedDate(new Date());
		site.setStatus(Status.AVAILABLE);
		site = (StudyBean) studyDAO.create(site);
	}

	private void checkItemDataStatus(ItemDataBean itemDataBean, Status status) {
		itemDataBean = (ItemDataBean) itemDataDAO.findByPK(itemDataBean.getId());
		if (status.isDeleted() || status.isLocked()) {
			assertTrue(itemDataBean.getStatus().equals(status));
		} else {
			assertTrue(itemDataBean.getStatus().equals(itemDataStatusMap.get(itemDataBean.getId())));
		}
	}

	private void checkCRFStatusOnly(Status status) {
		CRFBean crfBean = (CRFBean) crfdao.findByPK(crfVersionBean.getCrfId());
		assertTrue(crfBean.getStatus().equals(status));
	}

	private void checkCRFVersionStatusOnly(Status status) {
		CRFVersionBean crfVersionBean = (CRFVersionBean) crfVersionDao.findByPK(eventCRFBean.getCRFVersionId());
		assertTrue(crfVersionBean.getStatus().equals(status));
	}

	private void checkEventCRFStatus(Status status) {
		EventCRFBean eventCRFBean = (EventCRFBean) eventCRFDAO.findByPK(EVENT_CRF_ID);
		assertTrue(eventCRFBean.getStatus().equals(status));
		List<ItemDataBean> itemDataBeanList = itemDataDAO.findAllByEventCRFId(eventCRFBean.getId());
		for (ItemDataBean itemDataBean : itemDataBeanList) {
			checkItemDataStatus(itemDataBean, status);
		}
	}

	private void checkEventCRFSDVStatusOnly(boolean sdv) {
		EventCRFBean eventCRFBean = (EventCRFBean) eventCRFDAO.findByPK(EVENT_CRF_ID);
		assertTrue(eventCRFBean.isSdvStatus() == sdv);
	}

	private void checkStudyEventStatusOnly(Status status) {
		StudyEventBean studyEventBean = (StudyEventBean) studyEventDao.findByPK(STUDY_EVENT_ID);
		assertTrue(studyEventBean.getStatus().equals(status));
		if (status.isDeleted()) {
			assertTrue(studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.REMOVED));
		} else if (status.isLocked()) {
			assertTrue(studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.LOCKED));
		}
	}

	private void checkStudyEventSubjectEventStatusOnly(SubjectEventStatus subjectEventStatus) {
		StudyEventBean studyEventBean = (StudyEventBean) studyEventDao.findByPK(STUDY_EVENT_ID);
		assertTrue(studyEventBean.getSubjectEventStatus().equals(subjectEventStatus));
	}

	private void checkStudyEventStatus(Status status) {
		StudyEventBean studyEventBean = (StudyEventBean) studyEventDao.findByPK(STUDY_EVENT_ID);
		assertTrue(studyEventBean.getStatus().equals(status));
		if (status.isDeleted()) {
			assertTrue(studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.REMOVED));
		} else if (status.isLocked()) {
			assertTrue(studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.LOCKED));
		}
		checkEventCRFStatus(status);
	}

	private void checkStudySubjectStatusOnly(Status status) {
		StudySubjectBean studySubjectBean = (StudySubjectBean) studySubjectDAO.findByPK(STUDY_SUBJECT_ID);
		assertTrue(studySubjectBean.getStatus().equals(status));
	}

	private void checkStudySubjectStatus(Status status) {
		StudySubjectBean studySubjectBean = (StudySubjectBean) studySubjectDAO.findByPK(STUDY_SUBJECT_ID);
		assertTrue(studySubjectBean.getStatus().equals(status));
		checkStudyEventStatus(status);
	}

	private void checkSubjectStatus(Status status) {
		SubjectBean subjectBean = (SubjectBean) subjectDAO.findByPK(SUBJECT_ID);
		assertTrue(subjectBean.getStatus().equals(status));
		checkStudySubjectStatus(status);
	}

	private void checkSiteEDCStatus(Status status) {
		checkSiteFirstEDCStatus(status);
		checkSiteSecondEDCStatus(status);
	}

	private void checkSiteFirstEDCStatus(Status status) {
		siteEventDefinitionCRFBean1 = ((EventDefinitionCRFBean) eventDefinitionCRFDAO
				.findByPK(siteEventDefinitionCRFBean1.getId()));
		siteEventDefinitionCRFBean1.setCrf((CRFBean) crfdao.findByPK(siteEventDefinitionCRFBean1.getCrfId()));
		assertTrue(siteEventDefinitionCRFBean1.getStatus().equals(status));
	}

	private void checkSiteSecondEDCStatus(Status status) {
		siteEventDefinitionCRFBean2 = ((EventDefinitionCRFBean) eventDefinitionCRFDAO
				.findByPK(siteEventDefinitionCRFBean2.getId()));
		siteEventDefinitionCRFBean2.setCrf((CRFBean) crfdao.findByPK(siteEventDefinitionCRFBean2.getCrfId()));
		assertTrue(siteEventDefinitionCRFBean2.getStatus().equals(status));
	}

	private void checkStudyEDCStatus(Status status) {
		checkStudyFirstEDCStatus(status);
		checkStudySecondEDCStatus(status);
	}

	private void checkStudyFirstEDCStatus(Status status) {
		studyEventDefinitionCRFBean1 = ((EventDefinitionCRFBean) eventDefinitionCRFDAO
				.findByPK(studyEventDefinitionCRFBean1.getId()));
		studyEventDefinitionCRFBean1.setCrf((CRFBean) crfdao.findByPK(studyEventDefinitionCRFBean1.getCrfId()));
		assertTrue(studyEventDefinitionCRFBean1.getStatus().equals(status));
	}

	private void checkStudySecondEDCStatus(Status status) {
		studyEventDefinitionCRFBean2 = ((EventDefinitionCRFBean) eventDefinitionCRFDAO
				.findByPK(studyEventDefinitionCRFBean2.getId()));
		studyEventDefinitionCRFBean2.setCrf((CRFBean) crfdao.findByPK(studyEventDefinitionCRFBean2.getCrfId()));
		assertTrue(studyEventDefinitionCRFBean2.getStatus().equals(status));
	}

	private void checkStudySEDStatus(Status status) {
		assertTrue(((StudyEventDefinitionBean) studyEventDefinitionDAO.findByPK(STUDY_EVENT_FEFINITION_CRF_ID))
				.getStatus().equals(status));
	}

	private void checkSiteStatusOnly(Status status) {
		StudyBean studyBean = (StudyBean) studyDAO.findByPK(site.getId());
		assertTrue(studyBean.getStatus().equals(status));
	}

	private void checkSiteStatus(Status status) {
		StudyBean studyBean = (StudyBean) studyDAO.findByPK(site.getId());
		assertTrue(studyBean.getStatus().equals(status));
		checkStudySEDStatus(Status.AVAILABLE);
		checkSiteEDCStatus(status.isDeleted() ? Status.AUTO_DELETED : status);
		checkStudyEDCStatus(Status.AVAILABLE);
		checkStudySubjectStatus(status);
	}

	private void checkStudyStatusOnly(Status status) {
		StudyBean studyBean = (StudyBean) studyDAO.findByPK(study.getId());
		assertTrue(studyBean.getStatus().equals(status));
	}

	private void checkStudyStatus(Status status) {
		StudyBean studyBean = (StudyBean) studyDAO.findByPK(study.getId());
		assertTrue(studyBean.getStatus().equals(status));
		studyBean = (StudyBean) studyDAO.findByPK(site.getId());
		assertTrue(studyBean.getStatus().equals(status));
		checkStudySEDStatus(status.isDeleted() ? Status.AUTO_DELETED : status);
		checkStudyEDCStatus(status.isDeleted() ? Status.AUTO_DELETED : status);
		checkStudySubjectStatus(status);
	}

	@Test
	public void testThatLockingOfSiteAffectsAllChildObjectsCorrectly() throws Exception {
		studyService.lockSite(site, updater);
		checkSiteStatus(Status.LOCKED);
	}

	@Test
	public void testThatLockingOfStudySubjectAffectsAllChildObjectsCorrectly() throws Exception {
		studySubjectService.lockStudySubject(studySubjectBean, updater);
		checkStudySubjectStatus(Status.LOCKED);
	}

	@Test
	public void testThatLockingOfStudyEventAffectsAllChildObjectsCorrectly() throws Exception {
		studyEventService.lockStudyEvent(studyEventBean, updater);
		checkStudyEventStatus(Status.LOCKED);
	}

	@Test
	public void testThatRemovingOfStudyAffectsAllChildObjectsCorrectly() throws Exception {
		studyService.removeStudy(study, updater);
		checkStudyStatus(Status.DELETED);
	}

	@Test
	public void testThatRemovingOfSiteAffectsAllChildObjectsCorrectly() throws Exception {
		studyService.removeSite(site, updater);
		checkSiteStatus(Status.DELETED);
	}

	@Test
	public void testThatRemovingOfSubjectAffectsAllChildObjectsCorrectly() throws Exception {
		subjectService.removeSubject(subjectBean, updater);
		checkSubjectStatus(Status.DELETED);
	}

	@Test
	public void testThatRemovingOfStudySubjectAffectsAllChildObjectsCorrectly() throws Exception {
		studySubjectService.removeStudySubject(studySubjectBean, updater);
		checkStudySubjectStatus(Status.DELETED);
	}

	@Test
	public void testThatRemovingOfStudyEventAffectsAllChildObjectsCorrectly() throws Exception {
		studyEventService.removeStudyEvent(studyEventBean, updater);
		checkStudyEventStatus(Status.DELETED);
	}

	@Test
	public void testThatRemovingOfEventCRFAffectsAllChildObjectsCorrectly() throws Exception {
		eventCRFService.removeEventCRF(eventCRFBean, updater);
		checkEventCRFStatus(Status.DELETED);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase1() throws Exception {
		eventDefinitionCrfService.removeParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);
		studyService.lockSite(site, updater);
		eventDefinitionService.removeStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.LOCKED);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkEventCRFStatus(Status.LOCKED);
		checkStudySubjectStatusOnly(Status.LOCKED);
		checkStudyEventStatusOnly(Status.LOCKED);

		eventDefinitionService.restoreStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.LOCKED);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkEventCRFStatus(Status.LOCKED);
		checkStudySubjectStatusOnly(Status.LOCKED);
		checkStudyEventStatusOnly(Status.LOCKED);

		studyService.unlockSite(site, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AVAILABLE);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.AVAILABLE);

		eventDefinitionCrfService.restoreParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyEDCStatus(Status.AVAILABLE);
		checkSiteEDCStatus(Status.AVAILABLE);
		checkStudySubjectStatus(Status.AVAILABLE);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase2() throws Exception {
		eventDefinitionCrfService.removeParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);
		studyService.lockSite(site, updater);
		eventDefinitionService.removeStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.LOCKED);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkEventCRFStatus(Status.LOCKED);
		checkStudySubjectStatusOnly(Status.LOCKED);
		checkStudyEventStatusOnly(Status.LOCKED);

		eventDefinitionService.restoreStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.LOCKED);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkEventCRFStatus(Status.LOCKED);
		checkStudySubjectStatusOnly(Status.LOCKED);
		checkStudyEventStatusOnly(Status.LOCKED);

		eventDefinitionCrfService.restoreParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.AVAILABLE);
		checkSiteFirstEDCStatus(Status.LOCKED);
		checkSiteSecondEDCStatus(Status.LOCKED);
		checkEventCRFStatus(Status.LOCKED);
		checkStudySubjectStatusOnly(Status.LOCKED);
		checkStudyEventStatusOnly(Status.LOCKED);

		studyService.unlockSite(site, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyEDCStatus(Status.AVAILABLE);
		checkSiteEDCStatus(Status.AVAILABLE);
		checkStudySubjectStatus(Status.AVAILABLE);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase3() throws Exception {
		eventDefinitionCrfService.removeParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);
		studyService.lockSite(site, updater);
		eventDefinitionService.removeStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.LOCKED);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkEventCRFStatus(Status.LOCKED);
		checkStudySubjectStatusOnly(Status.LOCKED);
		checkStudyEventStatusOnly(Status.LOCKED);

		studyService.unlockSite(site, updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.DELETED);

		eventDefinitionService.restoreStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AVAILABLE);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.AVAILABLE);

		eventDefinitionCrfService.restoreParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyEDCStatus(Status.AVAILABLE);
		checkSiteEDCStatus(Status.AVAILABLE);
		checkStudySubjectStatus(Status.AVAILABLE);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase4() throws Exception {
		studyService.lockSite(site, updater);
		eventDefinitionCrfService.removeParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);
		eventDefinitionService.removeStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.LOCKED);
		checkSiteSecondEDCStatus(Status.LOCKED);
		checkEventCRFStatus(Status.LOCKED);
		checkStudySubjectStatusOnly(Status.LOCKED);
		checkStudyEventStatusOnly(Status.LOCKED);

		eventDefinitionService.restoreStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.LOCKED);
		checkSiteSecondEDCStatus(Status.LOCKED);
		checkEventCRFStatus(Status.LOCKED);
		checkStudySubjectStatusOnly(Status.LOCKED);
		checkStudyEventStatusOnly(Status.LOCKED);

		eventDefinitionCrfService.restoreParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.AVAILABLE);
		checkSiteFirstEDCStatus(Status.LOCKED);
		checkSiteSecondEDCStatus(Status.LOCKED);
		checkEventCRFStatus(Status.LOCKED);
		checkStudySubjectStatusOnly(Status.LOCKED);
		checkStudyEventStatusOnly(Status.LOCKED);

		studyService.unlockSite(site, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyEDCStatus(Status.AVAILABLE);
		checkSiteEDCStatus(Status.AVAILABLE);
		checkStudySubjectStatus(Status.AVAILABLE);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase5() throws Exception {
		studyService.lockSite(site, updater);
		eventDefinitionCrfService.removeParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);
		eventDefinitionService.removeStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.LOCKED);
		checkSiteSecondEDCStatus(Status.LOCKED);
		checkEventCRFStatus(Status.LOCKED);
		checkStudySubjectStatusOnly(Status.LOCKED);
		checkStudyEventStatusOnly(Status.LOCKED);

		eventDefinitionService.restoreStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.LOCKED);
		checkSiteSecondEDCStatus(Status.LOCKED);
		checkEventCRFStatus(Status.LOCKED);
		checkStudySubjectStatusOnly(Status.LOCKED);
		checkStudyEventStatusOnly(Status.LOCKED);

		studyService.unlockSite(site, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AVAILABLE);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.AVAILABLE);

		eventDefinitionCrfService.restoreParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyEDCStatus(Status.AVAILABLE);
		checkSiteEDCStatus(Status.AVAILABLE);
		checkStudySubjectStatus(Status.AVAILABLE);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase6() throws Exception {
		studyService.lockSite(site, updater);
		eventDefinitionCrfService.removeParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);
		eventDefinitionService.removeStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.LOCKED);
		checkSiteSecondEDCStatus(Status.LOCKED);
		checkEventCRFStatus(Status.LOCKED);
		checkStudySubjectStatusOnly(Status.LOCKED);
		checkStudyEventStatusOnly(Status.LOCKED);

		studyService.unlockSite(site, updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.DELETED);

		eventDefinitionService.restoreStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AVAILABLE);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.AVAILABLE);

		eventDefinitionCrfService.restoreParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyEDCStatus(Status.AVAILABLE);
		checkSiteEDCStatus(Status.AVAILABLE);
		checkStudySubjectStatus(Status.AVAILABLE);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase7() throws Exception {
		eventDefinitionCrfService.removeParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);
		studyService.removeSite(site, updater);
		eventDefinitionService.removeStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.DELETED);
		checkStudyEventStatusOnly(Status.DELETED);

		eventDefinitionService.restoreStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.DELETED);
		checkStudyEventStatusOnly(Status.DELETED);

		studyService.restoreSite(site, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AVAILABLE);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.AVAILABLE);

		eventDefinitionCrfService.restoreParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyEDCStatus(Status.AVAILABLE);
		checkSiteEDCStatus(Status.AVAILABLE);
		checkStudySubjectStatus(Status.AVAILABLE);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase8() throws Exception {
		eventDefinitionCrfService.removeParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);
		studyService.removeSite(site, updater);
		eventDefinitionService.removeStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.DELETED);
		checkStudyEventStatusOnly(Status.DELETED);

		eventDefinitionService.restoreStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.DELETED);
		checkStudyEventStatusOnly(Status.DELETED);

		eventDefinitionCrfService.restoreParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.AVAILABLE);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.AUTO_DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.DELETED);
		checkStudyEventStatusOnly(Status.DELETED);

		studyService.restoreSite(site, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyEDCStatus(Status.AVAILABLE);
		checkSiteEDCStatus(Status.AVAILABLE);
		checkStudySubjectStatus(Status.AVAILABLE);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase9() throws Exception {
		eventDefinitionCrfService.removeParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);
		studyService.removeSite(site, updater);
		eventDefinitionService.removeStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.DELETED);
		checkStudyEventStatusOnly(Status.DELETED);

		studyService.restoreSite(site, updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.DELETED);

		eventDefinitionService.restoreStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AVAILABLE);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.AVAILABLE);

		eventDefinitionCrfService.restoreParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyEDCStatus(Status.AVAILABLE);
		checkSiteEDCStatus(Status.AVAILABLE);
		checkStudySubjectStatus(Status.AVAILABLE);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase10() throws Exception {
		studyService.removeSite(site, updater);
		eventDefinitionCrfService.removeParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);
		eventDefinitionService.removeStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.AUTO_DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.DELETED);
		checkStudyEventStatusOnly(Status.DELETED);

		eventDefinitionService.restoreStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.AUTO_DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.DELETED);
		checkStudyEventStatusOnly(Status.DELETED);

		eventDefinitionCrfService.restoreParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.AVAILABLE);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.AUTO_DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.DELETED);
		checkStudyEventStatusOnly(Status.DELETED);

		studyService.restoreSite(site, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyEDCStatus(Status.AVAILABLE);
		checkSiteEDCStatus(Status.AVAILABLE);
		checkStudySubjectStatus(Status.AVAILABLE);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase11() throws Exception {
		studyService.removeSite(site, updater);
		eventDefinitionCrfService.removeParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);
		eventDefinitionService.removeStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.AUTO_DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.DELETED);
		checkStudyEventStatusOnly(Status.DELETED);

		eventDefinitionService.restoreStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.AUTO_DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.DELETED);
		checkStudyEventStatusOnly(Status.DELETED);

		studyService.restoreSite(site, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AVAILABLE);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.AVAILABLE);

		eventDefinitionCrfService.restoreParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyEDCStatus(Status.AVAILABLE);
		checkSiteEDCStatus(Status.AVAILABLE);
		checkStudySubjectStatus(Status.AVAILABLE);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase12() throws Exception {
		studyService.removeSite(site, updater);
		eventDefinitionCrfService.removeParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);
		eventDefinitionService.removeStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.AUTO_DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.DELETED);
		checkStudyEventStatusOnly(Status.DELETED);

		studyService.restoreSite(site, updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.DELETED);

		eventDefinitionService.restoreStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AVAILABLE);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.AVAILABLE);

		eventDefinitionCrfService.restoreParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyEDCStatus(Status.AVAILABLE);
		checkSiteEDCStatus(Status.AVAILABLE);
		checkStudySubjectStatus(Status.AVAILABLE);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase13() throws Exception {
		crfVersionService.removeCrf(studyEventDefinitionCRFBean2.getCrf(), updater);
		studyService.lockSite(site, updater);
		eventDefinitionService.removeStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.AUTO_DELETED);
		checkSiteFirstEDCStatus(Status.LOCKED);
		checkSiteSecondEDCStatus(Status.AUTO_DELETED);
		checkCRFStatusOnly(Status.DELETED);
		checkEventCRFStatus(Status.LOCKED);
		checkStudySubjectStatusOnly(Status.LOCKED);
		checkStudyEventStatusOnly(Status.LOCKED);

		studyService.unlockSite(site, updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.AUTO_DELETED);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.AUTO_DELETED);
		checkCRFStatusOnly(Status.DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.DELETED);

		eventDefinitionService.restoreStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.AUTO_DELETED);
		checkSiteFirstEDCStatus(Status.AVAILABLE);
		checkSiteSecondEDCStatus(Status.AUTO_DELETED);
		checkCRFStatusOnly(Status.DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.AVAILABLE);

		crfVersionService.restoreCrf(studyEventDefinitionCRFBean2.getCrf(), updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyEDCStatus(Status.AVAILABLE);
		checkSiteEDCStatus(Status.AVAILABLE);
		checkStudySubjectStatus(Status.AVAILABLE);
		checkCRFStatusOnly(Status.AVAILABLE);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase14() throws Exception {
		crfVersionService.removeCrf(studyEventDefinitionCRFBean2.getCrf(), updater);
		studyService.lockSite(site, updater);
		eventDefinitionService.removeStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.AUTO_DELETED);
		checkSiteFirstEDCStatus(Status.LOCKED);
		checkSiteSecondEDCStatus(Status.AUTO_DELETED);
		checkCRFStatusOnly(Status.DELETED);
		checkEventCRFStatus(Status.LOCKED);
		checkStudySubjectStatusOnly(Status.LOCKED);
		checkStudyEventStatusOnly(Status.LOCKED);

		studyService.unlockSite(site, updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.AUTO_DELETED);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.AUTO_DELETED);
		checkCRFStatusOnly(Status.DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.DELETED);

		crfVersionService.restoreCrf(studyEventDefinitionCRFBean2.getCrf(), updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.AUTO_DELETED);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.AUTO_DELETED);
		checkCRFStatusOnly(Status.AVAILABLE);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.DELETED);

		eventDefinitionService.restoreStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyEDCStatus(Status.AVAILABLE);
		checkSiteEDCStatus(Status.AVAILABLE);
		checkStudySubjectStatus(Status.AVAILABLE);
		checkCRFStatusOnly(Status.AVAILABLE);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase15() throws Exception {
		crfVersionService.removeCrf(studyEventDefinitionCRFBean2.getCrf(), updater);
		studyService.lockSite(site, updater);
		eventDefinitionService.removeStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.AUTO_DELETED);
		checkSiteFirstEDCStatus(Status.LOCKED);
		checkSiteSecondEDCStatus(Status.AUTO_DELETED);
		checkCRFStatusOnly(Status.DELETED);
		checkEventCRFStatus(Status.LOCKED);
		checkStudySubjectStatusOnly(Status.LOCKED);
		checkStudyEventStatusOnly(Status.LOCKED);

		crfVersionService.restoreCrf(studyEventDefinitionCRFBean2.getCrf(), updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.AUTO_DELETED);
		checkSiteFirstEDCStatus(Status.LOCKED);
		checkSiteSecondEDCStatus(Status.AUTO_DELETED);
		checkCRFStatusOnly(Status.AVAILABLE);
		checkEventCRFStatus(Status.LOCKED);
		checkStudySubjectStatusOnly(Status.LOCKED);
		checkStudyEventStatusOnly(Status.LOCKED);

		studyService.unlockSite(site, updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.AUTO_DELETED);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.AUTO_DELETED);
		checkCRFStatusOnly(Status.AVAILABLE);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.DELETED);

		eventDefinitionService.restoreStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyEDCStatus(Status.AVAILABLE);
		checkSiteEDCStatus(Status.AVAILABLE);
		checkStudySubjectStatus(Status.AVAILABLE);
		checkCRFStatusOnly(Status.AVAILABLE);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase16() throws Exception {
		studyService.lockSite(site, updater);
		eventDefinitionService.removeStudyEventDefinition(studyEventDefinitionBean, updater);
		crfVersionService.removeCrf(studyEventDefinitionCRFBean2.getCrf(), updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.AUTO_DELETED);
		checkSiteFirstEDCStatus(Status.LOCKED);
		checkSiteSecondEDCStatus(Status.LOCKED);
		checkCRFStatusOnly(Status.DELETED);
		checkEventCRFStatus(Status.LOCKED);
		checkStudySubjectStatusOnly(Status.LOCKED);
		checkStudyEventStatusOnly(Status.LOCKED);

		eventDefinitionService.restoreStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.AUTO_DELETED);
		checkSiteFirstEDCStatus(Status.LOCKED);
		checkSiteSecondEDCStatus(Status.LOCKED);
		checkCRFStatusOnly(Status.DELETED);
		checkEventCRFStatus(Status.LOCKED);
		checkStudySubjectStatusOnly(Status.LOCKED);
		checkStudyEventStatusOnly(Status.LOCKED);

		studyService.unlockSite(site, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.AUTO_DELETED);
		checkSiteFirstEDCStatus(Status.AVAILABLE);
		checkSiteSecondEDCStatus(Status.AUTO_DELETED);
		checkCRFStatusOnly(Status.DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.AVAILABLE);

		crfVersionService.restoreCrf(studyEventDefinitionCRFBean2.getCrf(), updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyEDCStatus(Status.AVAILABLE);
		checkSiteEDCStatus(Status.AVAILABLE);
		checkStudySubjectStatus(Status.AVAILABLE);
		checkCRFStatusOnly(Status.AVAILABLE);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase17() throws Exception {
		studyService.lockSite(site, updater);
		eventDefinitionService.removeStudyEventDefinition(studyEventDefinitionBean, updater);
		crfVersionService.removeCrf(studyEventDefinitionCRFBean2.getCrf(), updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.AUTO_DELETED);
		checkSiteFirstEDCStatus(Status.LOCKED);
		checkSiteSecondEDCStatus(Status.LOCKED);
		checkCRFStatusOnly(Status.DELETED);
		checkEventCRFStatus(Status.LOCKED);
		checkStudySubjectStatusOnly(Status.LOCKED);
		checkStudyEventStatusOnly(Status.LOCKED);

		eventDefinitionService.restoreStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.AUTO_DELETED);
		checkSiteFirstEDCStatus(Status.LOCKED);
		checkSiteSecondEDCStatus(Status.LOCKED);
		checkCRFStatusOnly(Status.DELETED);
		checkEventCRFStatus(Status.LOCKED);
		checkStudySubjectStatusOnly(Status.LOCKED);
		checkStudyEventStatusOnly(Status.LOCKED);

		crfVersionService.restoreCrf(studyEventDefinitionCRFBean2.getCrf(), updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.AVAILABLE);
		checkSiteFirstEDCStatus(Status.LOCKED);
		checkSiteSecondEDCStatus(Status.LOCKED);
		checkCRFStatusOnly(Status.AVAILABLE);
		checkEventCRFStatus(Status.LOCKED);
		checkStudySubjectStatusOnly(Status.LOCKED);
		checkStudyEventStatusOnly(Status.LOCKED);

		studyService.unlockSite(site, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyEDCStatus(Status.AVAILABLE);
		checkSiteEDCStatus(Status.AVAILABLE);
		checkStudySubjectStatus(Status.AVAILABLE);
		checkCRFStatusOnly(Status.AVAILABLE);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase18() throws Exception {
		studyService.lockSite(site, updater);
		eventDefinitionService.removeStudyEventDefinition(studyEventDefinitionBean, updater);
		crfVersionService.removeCrf(studyEventDefinitionCRFBean2.getCrf(), updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.AUTO_DELETED);
		checkSiteFirstEDCStatus(Status.LOCKED);
		checkSiteSecondEDCStatus(Status.LOCKED);
		checkCRFStatusOnly(Status.DELETED);
		checkEventCRFStatus(Status.LOCKED);
		checkStudySubjectStatusOnly(Status.LOCKED);
		checkStudyEventStatusOnly(Status.LOCKED);

		crfVersionService.restoreCrf(studyEventDefinitionCRFBean2.getCrf(), updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.AUTO_DELETED);
		checkSiteFirstEDCStatus(Status.LOCKED);
		checkSiteSecondEDCStatus(Status.LOCKED);
		checkCRFStatusOnly(Status.AVAILABLE);
		checkEventCRFStatus(Status.LOCKED);
		checkStudySubjectStatusOnly(Status.LOCKED);
		checkStudyEventStatusOnly(Status.LOCKED);

		eventDefinitionService.restoreStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.AVAILABLE);
		checkSiteFirstEDCStatus(Status.LOCKED);
		checkSiteSecondEDCStatus(Status.LOCKED);
		checkCRFStatusOnly(Status.AVAILABLE);
		checkEventCRFStatus(Status.LOCKED);
		checkStudySubjectStatusOnly(Status.LOCKED);
		checkStudyEventStatusOnly(Status.LOCKED);

		studyService.unlockSite(site, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyEDCStatus(Status.AVAILABLE);
		checkSiteEDCStatus(Status.AVAILABLE);
		checkStudySubjectStatus(Status.AVAILABLE);
		checkCRFStatusOnly(Status.AVAILABLE);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase19() throws Exception {
		studyService.removeSite(site, updater);
		eventDefinitionCrfService.removeParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);
		crfVersionService.removeCrf(studyEventDefinitionCRFBean2.getCrf(), updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.AUTO_DELETED);
		checkCRFStatusOnly(Status.DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.DELETED);
		checkStudyEventStatusOnly(Status.DELETED);

		studyService.restoreSite(site, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AVAILABLE);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkCRFStatusOnly(Status.DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.AVAILABLE);

		crfVersionService.restoreCrf(studyEventDefinitionCRFBean2.getCrf(), updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AVAILABLE);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkCRFStatusOnly(Status.AVAILABLE);
		checkEventCRFStatus(Status.AVAILABLE);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.AVAILABLE);

		eventDefinitionCrfService.restoreParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyEDCStatus(Status.AVAILABLE);
		checkSiteEDCStatus(Status.AVAILABLE);
		checkStudySubjectStatus(Status.AVAILABLE);
		checkCRFStatusOnly(Status.AVAILABLE);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase20() throws Exception {
		studyService.removeSite(site, updater);
		eventDefinitionCrfService.removeParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);
		crfVersionService.removeCrf(studyEventDefinitionCRFBean2.getCrf(), updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.AUTO_DELETED);
		checkCRFStatusOnly(Status.DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.DELETED);
		checkStudyEventStatusOnly(Status.DELETED);

		crfVersionService.restoreCrf(studyEventDefinitionCRFBean2.getCrf(), updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.AUTO_DELETED);
		checkCRFStatusOnly(Status.AVAILABLE);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.DELETED);
		checkStudyEventStatusOnly(Status.DELETED);

		eventDefinitionCrfService.restoreParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.AVAILABLE);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.AUTO_DELETED);
		checkCRFStatusOnly(Status.AVAILABLE);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.DELETED);
		checkStudyEventStatusOnly(Status.DELETED);

		studyService.restoreSite(site, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyEDCStatus(Status.AVAILABLE);
		checkSiteEDCStatus(Status.AVAILABLE);
		checkStudySubjectStatus(Status.AVAILABLE);
		checkCRFStatusOnly(Status.AVAILABLE);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase21() throws Exception {
		eventDefinitionCrfService.removeParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);
		eventDefinitionService.removeStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.DELETED);

		eventDefinitionService.restoreStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AVAILABLE);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.AVAILABLE);

		eventDefinitionCrfService.restoreParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyEDCStatus(Status.AVAILABLE);
		checkSiteEDCStatus(Status.AVAILABLE);
		checkStudySubjectStatus(Status.AVAILABLE);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase22() throws Exception {
		eventDefinitionCrfService.removeParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);
		studyService.removeStudy(study, updater);

		checkStudySEDStatus(Status.AUTO_DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.DELETED);
		checkStudyEventStatusOnly(Status.DELETED);

		studyService.restoreStudy(study, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AVAILABLE);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.AVAILABLE);

		eventDefinitionCrfService.restoreParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyEDCStatus(Status.AVAILABLE);
		checkSiteEDCStatus(Status.AVAILABLE);
		checkStudySubjectStatus(Status.AVAILABLE);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase23() throws Exception {
		eventCRFService.removeEventCRF(eventCRFBean, updater);
		eventDefinitionCrfService.removeParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);
		crfVersionService.lockCrfVersion(crfVersionBean, updater);
		eventDefinitionService.removeStudyEventDefinition(studyEventDefinitionBean, updater);
		studySubjectService.removeStudySubject(studySubjectBean, updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkCRFVersionStatusOnly(Status.LOCKED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.DELETED);
		checkStudyEventStatusOnly(Status.DELETED);

		crfVersionService.unlockCrfVersion(crfVersionBean, updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkCRFVersionStatusOnly(Status.AVAILABLE);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.DELETED);
		checkStudyEventStatusOnly(Status.DELETED);

		eventDefinitionService.restoreStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AVAILABLE);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkCRFVersionStatusOnly(Status.AVAILABLE);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.DELETED);
		checkStudyEventStatusOnly(Status.DELETED);

		eventDefinitionCrfService.restoreParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.AVAILABLE);
		checkSiteFirstEDCStatus(Status.AVAILABLE);
		checkSiteSecondEDCStatus(Status.AVAILABLE);
		checkCRFVersionStatusOnly(Status.AVAILABLE);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.DELETED);
		checkStudyEventStatusOnly(Status.DELETED);

		studySubjectService.restoreStudySubject(studySubjectBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.AVAILABLE);
		checkSiteFirstEDCStatus(Status.AVAILABLE);
		checkSiteSecondEDCStatus(Status.AVAILABLE);
		checkCRFVersionStatusOnly(Status.AVAILABLE);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.AVAILABLE);

		eventCRFService.restoreEventCRF(eventCRFBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyEDCStatus(Status.AVAILABLE);
		checkSiteEDCStatus(Status.AVAILABLE);
		checkStudySubjectStatus(Status.AVAILABLE);
		checkCRFVersionStatusOnly(Status.AVAILABLE);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase24() throws Exception {
		eventCRFService.removeEventCRF(eventCRFBean, updater);
		eventDefinitionCrfService.removeParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);
		crfVersionService.removeCrfVersion(crfVersionBean, updater);
		eventDefinitionService.removeStudyEventDefinition(studyEventDefinitionBean, updater);
		studySubjectService.removeStudySubject(studySubjectBean, updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkCRFVersionStatusOnly(Status.DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.DELETED);
		checkStudyEventStatusOnly(Status.DELETED);

		studySubjectService.restoreStudySubject(studySubjectBean, updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkCRFVersionStatusOnly(Status.DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.DELETED);

		eventDefinitionService.restoreStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AVAILABLE);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkCRFVersionStatusOnly(Status.DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.AVAILABLE);

		crfVersionService.restoreCrfVersion(crfVersionBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AVAILABLE);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkCRFVersionStatusOnly(Status.AVAILABLE);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.AVAILABLE);

		eventDefinitionCrfService.restoreParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.AVAILABLE);
		checkSiteFirstEDCStatus(Status.AVAILABLE);
		checkSiteSecondEDCStatus(Status.AVAILABLE);
		checkCRFVersionStatusOnly(Status.AVAILABLE);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.AVAILABLE);

		eventCRFService.restoreEventCRF(eventCRFBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyEDCStatus(Status.AVAILABLE);
		checkSiteEDCStatus(Status.AVAILABLE);
		checkStudySubjectStatus(Status.AVAILABLE);
		checkCRFVersionStatusOnly(Status.AVAILABLE);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase25() throws Exception {
		eventCRFService.removeEventCRF(eventCRFBean, updater);
		eventDefinitionCrfService.removeParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);
		crfVersionService.removeCrf(studyEventDefinitionCRFBean2.getCrf(), updater);
		eventDefinitionService.removeStudyEventDefinition(studyEventDefinitionBean, updater);
		studySubjectService.removeStudySubject(studySubjectBean, updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkCRFStatusOnly(Status.DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.DELETED);
		checkStudyEventStatusOnly(Status.DELETED);

		studySubjectService.restoreStudySubject(studySubjectBean, updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkCRFStatusOnly(Status.DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.DELETED);

		eventDefinitionService.restoreStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AVAILABLE);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkCRFStatusOnly(Status.DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.AVAILABLE);

		crfVersionService.restoreCrf(studyEventDefinitionCRFBean2.getCrf(), updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AVAILABLE);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkCRFStatusOnly(Status.AVAILABLE);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.AVAILABLE);

		eventDefinitionCrfService.restoreParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.AVAILABLE);
		checkSiteFirstEDCStatus(Status.AVAILABLE);
		checkSiteSecondEDCStatus(Status.AVAILABLE);
		checkCRFStatusOnly(Status.AVAILABLE);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.AVAILABLE);

		eventCRFService.restoreEventCRF(eventCRFBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyEDCStatus(Status.AVAILABLE);
		checkSiteEDCStatus(Status.AVAILABLE);
		checkStudySubjectStatus(Status.AVAILABLE);
		checkCRFVersionStatusOnly(Status.AVAILABLE);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase26() throws Exception {
		eventCRFService.removeEventCRF(eventCRFBean, updater);
		crfVersionService.removeCrf(studyEventDefinitionCRFBean2.getCrf(), updater);
		eventDefinitionService.removeStudyEventDefinition(studyEventDefinitionBean, updater);
		studySubjectService.removeStudySubject(studySubjectBean, updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.AUTO_DELETED);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.AUTO_DELETED);
		checkCRFStatusOnly(Status.DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.DELETED);
		checkStudyEventStatusOnly(Status.DELETED);

		studySubjectService.restoreStudySubject(studySubjectBean, updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.AUTO_DELETED);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.AUTO_DELETED);
		checkCRFStatusOnly(Status.DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.DELETED);

		eventDefinitionService.restoreStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.AUTO_DELETED);
		checkSiteFirstEDCStatus(Status.AVAILABLE);
		checkSiteSecondEDCStatus(Status.AUTO_DELETED);
		checkCRFStatusOnly(Status.DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.AVAILABLE);

		crfVersionService.restoreCrf(studyEventDefinitionCRFBean2.getCrf(), updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.AVAILABLE);
		checkSiteFirstEDCStatus(Status.AVAILABLE);
		checkSiteSecondEDCStatus(Status.AVAILABLE);
		checkCRFStatusOnly(Status.AVAILABLE);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.AVAILABLE);

		eventCRFService.restoreEventCRF(eventCRFBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyEDCStatus(Status.AVAILABLE);
		checkSiteEDCStatus(Status.AVAILABLE);
		checkStudySubjectStatus(Status.AVAILABLE);
		checkCRFVersionStatusOnly(Status.AVAILABLE);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase27() throws Exception {
		crfVersionService.removeCrf(studyEventDefinitionCRFBean2.getCrf(), updater);
		eventDefinitionService.removeStudyEventDefinition(studyEventDefinitionBean, updater);
		studySubjectService.removeStudySubject(studySubjectBean, updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.AUTO_DELETED);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.AUTO_DELETED);
		checkCRFStatusOnly(Status.DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.DELETED);
		checkStudyEventStatusOnly(Status.DELETED);

		studySubjectService.restoreStudySubject(studySubjectBean, updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.AUTO_DELETED);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.AUTO_DELETED);
		checkCRFStatusOnly(Status.DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.DELETED);

		eventDefinitionService.restoreStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.AUTO_DELETED);
		checkSiteFirstEDCStatus(Status.AVAILABLE);
		checkSiteSecondEDCStatus(Status.AUTO_DELETED);
		checkCRFStatusOnly(Status.DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.AVAILABLE);

		crfVersionService.restoreCrf(studyEventDefinitionCRFBean2.getCrf(), updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyEDCStatus(Status.AVAILABLE);
		checkSiteEDCStatus(Status.AVAILABLE);
		checkStudySubjectStatus(Status.AVAILABLE);
		checkCRFVersionStatusOnly(Status.AVAILABLE);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase28() throws Exception {
		crfVersionService.removeCrfVersion(crfVersionBean, updater);
		crfVersionService.removeCrf(studyEventDefinitionCRFBean2.getCrf(), updater);

		checkCRFVersionStatusOnly(Status.DELETED);
		checkCRFStatusOnly(Status.DELETED);

		crfVersionService.restoreCrf(studyEventDefinitionCRFBean2.getCrf(), updater);

		checkCRFVersionStatusOnly(Status.DELETED);
		checkCRFStatusOnly(Status.AVAILABLE);

		crfVersionService.restoreCrfVersion(crfVersionBean, updater);

		checkCRFVersionStatusOnly(Status.AVAILABLE);
		checkCRFStatusOnly(Status.AVAILABLE);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase29() throws Exception {
		crfVersionService.lockCrfVersion(crfVersionBean, updater);
		crfVersionService.removeCrf(studyEventDefinitionCRFBean2.getCrf(), updater);

		checkCRFVersionStatusOnly(Status.LOCKED);
		checkCRFStatusOnly(Status.DELETED);

		crfVersionService.restoreCrf(studyEventDefinitionCRFBean2.getCrf(), updater);

		checkCRFVersionStatusOnly(Status.LOCKED);
		checkCRFStatusOnly(Status.AVAILABLE);

		crfVersionService.unlockCrfVersion(crfVersionBean, updater);

		checkCRFVersionStatusOnly(Status.AVAILABLE);
		checkCRFStatusOnly(Status.AVAILABLE);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase30() throws Exception {
		crfVersionService.removeCrf(studyEventDefinitionCRFBean2.getCrf(), updater);

		checkCRFVersionStatusOnly(Status.AUTO_DELETED);
		checkCRFStatusOnly(Status.DELETED);

		crfVersionService.restoreCrf(studyEventDefinitionCRFBean2.getCrf(), updater);

		checkCRFVersionStatusOnly(Status.AVAILABLE);
		checkCRFStatusOnly(Status.AVAILABLE);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase31() throws Exception {
		studyService.removeSite(site, updater);
		studyService.removeStudy(study, updater);

		checkSiteStatusOnly(Status.DELETED);
		checkStudyStatus(Status.DELETED);

		studyService.restoreStudy(study, updater);

		checkSiteStatus(Status.DELETED);
		checkStudyStatusOnly(Status.AVAILABLE);

		studyService.restoreSite(site, updater);

		checkSiteStatus(Status.AVAILABLE);
		checkStudyStatus(Status.AVAILABLE);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase32() throws Exception {
		studyService.lockSite(site, updater);
		studyService.removeStudy(study, updater);

		checkSiteStatusOnly(Status.DELETED);
		checkStudyStatus(Status.DELETED);

		studyService.restoreStudy(study, updater);

		checkSiteStatus(Status.LOCKED);
		checkStudyStatusOnly(Status.AVAILABLE);

		studyService.unlockSite(site, updater);

		checkSiteStatus(Status.AVAILABLE);
		checkStudyStatus(Status.AVAILABLE);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase33() throws Exception {
		studyService.removeStudy(study, updater);

		checkStudyStatus(Status.DELETED);

		studyService.restoreStudy(study, updater);

		checkStudyStatus(Status.AVAILABLE);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase34() throws Exception {
		eventDefinitionCrfService.removeParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);
		crfVersionService.lockCrfVersion(crfVersionBean, updater);
		studySubjectService.lockStudySubject(studySubjectBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AVAILABLE);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkCRFVersionStatusOnly(Status.LOCKED);
		checkEventCRFStatus(Status.LOCKED);
		checkStudySubjectStatusOnly(Status.LOCKED);
		checkStudyEventStatusOnly(Status.LOCKED);

		crfVersionService.unlockCrfVersion(crfVersionBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AVAILABLE);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkCRFVersionStatusOnly(Status.AVAILABLE);
		checkEventCRFStatus(Status.LOCKED);
		checkStudySubjectStatusOnly(Status.LOCKED);
		checkStudyEventStatusOnly(Status.LOCKED);

		studySubjectService.unlockStudySubject(studySubjectBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AVAILABLE);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkCRFVersionStatusOnly(Status.AVAILABLE);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.AVAILABLE);

		eventDefinitionCrfService.restoreParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyEDCStatus(Status.AVAILABLE);
		checkSiteEDCStatus(Status.AVAILABLE);
		checkStudySubjectStatus(Status.AVAILABLE);
		checkCRFVersionStatusOnly(Status.AVAILABLE);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase35() throws Exception {
		eventDefinitionCrfService.removeParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);
		crfVersionService.lockCrfVersion(crfVersionBean, updater);
		studySubjectService.lockStudySubject(studySubjectBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AVAILABLE);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkCRFVersionStatusOnly(Status.LOCKED);
		checkEventCRFStatus(Status.LOCKED);
		checkStudySubjectStatusOnly(Status.LOCKED);
		checkStudyEventStatusOnly(Status.LOCKED);

		studySubjectService.unlockStudySubject(studySubjectBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AVAILABLE);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkCRFVersionStatusOnly(Status.LOCKED);
		checkEventCRFStatus(Status.LOCKED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.AVAILABLE);

		crfVersionService.unlockCrfVersion(crfVersionBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AVAILABLE);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkCRFVersionStatusOnly(Status.AVAILABLE);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.AVAILABLE);

		eventDefinitionCrfService.restoreParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyEDCStatus(Status.AVAILABLE);
		checkSiteEDCStatus(Status.AVAILABLE);
		checkStudySubjectStatus(Status.AVAILABLE);
		checkCRFVersionStatusOnly(Status.AVAILABLE);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase36() throws Exception {
		eventDefinitionCrfService.removeParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);
		crfVersionService.lockCrfVersion(crfVersionBean, updater);
		studySubjectService.lockStudySubject(studySubjectBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AVAILABLE);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkCRFVersionStatusOnly(Status.LOCKED);
		checkEventCRFStatus(Status.LOCKED);
		checkStudySubjectStatusOnly(Status.LOCKED);
		checkStudyEventStatusOnly(Status.LOCKED);

		studySubjectService.unlockStudySubject(studySubjectBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.DELETED);
		checkSiteFirstEDCStatus(Status.AVAILABLE);
		checkSiteSecondEDCStatus(Status.DELETED);
		checkCRFVersionStatusOnly(Status.LOCKED);
		checkEventCRFStatus(Status.LOCKED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.AVAILABLE);

		eventDefinitionCrfService.restoreParentEventDefinitionCrf(studyEventDefinitionCRFBean2, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.AVAILABLE);
		checkSiteFirstEDCStatus(Status.AVAILABLE);
		checkSiteSecondEDCStatus(Status.AVAILABLE);
		checkCRFVersionStatusOnly(Status.LOCKED);
		checkEventCRFStatus(Status.LOCKED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.AVAILABLE);

		crfVersionService.unlockCrfVersion(crfVersionBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyEDCStatus(Status.AVAILABLE);
		checkSiteEDCStatus(Status.AVAILABLE);
		checkStudySubjectStatus(Status.AVAILABLE);
		checkCRFVersionStatusOnly(Status.AVAILABLE);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase37() throws Exception {
		eventDefinitionService.removeStudyEventDefinition(studyEventDefinitionBean, updater);
		crfVersionService.removeCrf(studyEventDefinitionCRFBean2.getCrf(), updater);
		studySubjectService.lockStudySubject(studySubjectBean, updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.AUTO_DELETED);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.AUTO_DELETED);
		checkCRFStatusOnly(Status.DELETED);
		checkEventCRFStatus(Status.LOCKED);
		checkStudySubjectStatusOnly(Status.LOCKED);
		checkStudyEventStatusOnly(Status.LOCKED);

		studySubjectService.unlockStudySubject(studySubjectBean, updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.AUTO_DELETED);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.AUTO_DELETED);
		checkCRFStatusOnly(Status.DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.DELETED);

		eventDefinitionService.restoreStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.AUTO_DELETED);
		checkSiteFirstEDCStatus(Status.AVAILABLE);
		checkSiteSecondEDCStatus(Status.AUTO_DELETED);
		checkCRFStatusOnly(Status.DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.AVAILABLE);

		crfVersionService.restoreCrf(studyEventDefinitionCRFBean2.getCrf(), updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyEDCStatus(Status.AVAILABLE);
		checkSiteEDCStatus(Status.AVAILABLE);
		checkStudySubjectStatus(Status.AVAILABLE);
		checkCRFStatusOnly(Status.AVAILABLE);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase38() throws Exception {
		crfVersionService.removeCrf(studyEventDefinitionCRFBean2.getCrf(), updater);
		eventDefinitionService.removeStudyEventDefinition(studyEventDefinitionBean, updater);
		studySubjectService.lockStudySubject(studySubjectBean, updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.AUTO_DELETED);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.AUTO_DELETED);
		checkCRFStatusOnly(Status.DELETED);
		checkCRFVersionStatusOnly(Status.AUTO_DELETED);
		checkEventCRFStatus(Status.LOCKED);
		checkStudySubjectStatusOnly(Status.LOCKED);
		checkStudyEventStatusOnly(Status.LOCKED);

		studySubjectService.unlockStudySubject(studySubjectBean, updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.AUTO_DELETED);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.AUTO_DELETED);
		checkCRFStatusOnly(Status.DELETED);
		checkCRFVersionStatusOnly(Status.AUTO_DELETED);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.DELETED);

		crfVersionService.restoreCrf(studyEventDefinitionCRFBean2.getCrf(), updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.AUTO_DELETED);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.AUTO_DELETED);
		checkCRFStatusOnly(Status.AVAILABLE);
		checkCRFVersionStatusOnly(Status.AVAILABLE);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.DELETED);

		eventDefinitionService.restoreStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyEDCStatus(Status.AVAILABLE);
		checkSiteEDCStatus(Status.AVAILABLE);
		checkStudySubjectStatus(Status.AVAILABLE);
		checkCRFStatusOnly(Status.AVAILABLE);
		checkCRFVersionStatusOnly(Status.AVAILABLE);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase39() throws Exception {
		crfVersionService.removeCrf(studyEventDefinitionCRFBean2.getCrf(), updater);
		studySubjectService.lockStudySubject(studySubjectBean, updater);
		eventDefinitionService.removeStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.AUTO_DELETED);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.AUTO_DELETED);
		checkCRFStatusOnly(Status.DELETED);
		checkCRFVersionStatusOnly(Status.AUTO_DELETED);
		checkEventCRFStatus(Status.LOCKED);
		checkStudySubjectStatusOnly(Status.LOCKED);
		checkStudyEventStatusOnly(Status.LOCKED);

		crfVersionService.restoreCrf(studyEventDefinitionCRFBean2.getCrf(), updater);

		checkStudySEDStatus(Status.DELETED);
		checkStudyFirstEDCStatus(Status.AUTO_DELETED);
		checkStudySecondEDCStatus(Status.AUTO_DELETED);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.AUTO_DELETED);
		checkCRFStatusOnly(Status.AVAILABLE);
		checkCRFVersionStatusOnly(Status.AVAILABLE);
		checkEventCRFStatus(Status.LOCKED);
		checkStudySubjectStatusOnly(Status.LOCKED);
		checkStudyEventStatusOnly(Status.LOCKED);

		eventDefinitionService.restoreStudyEventDefinition(studyEventDefinitionBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.AVAILABLE);
		checkSiteFirstEDCStatus(Status.AVAILABLE);
		checkSiteSecondEDCStatus(Status.AVAILABLE);
		checkCRFStatusOnly(Status.AVAILABLE);
		checkCRFVersionStatusOnly(Status.AVAILABLE);
		checkEventCRFStatus(Status.LOCKED);
		checkStudySubjectStatusOnly(Status.LOCKED);
		checkStudyEventStatusOnly(Status.LOCKED);

		studySubjectService.unlockStudySubject(studySubjectBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyEDCStatus(Status.AVAILABLE);
		checkSiteEDCStatus(Status.AVAILABLE);
		checkStudySubjectStatus(Status.AVAILABLE);
		checkCRFStatusOnly(Status.AVAILABLE);
		checkCRFVersionStatusOnly(Status.AVAILABLE);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase40() throws Exception {
		eventCRFService.removeEventCRF(eventCRFBean, updater);
		studyEventService.lockStudyEvent(studyEventBean, updater);
		studySubjectService.lockStudySubject(studySubjectBean, updater);
		studyService.removeSite(site, updater);
		subjectService.removeSubject(subjectBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.AVAILABLE);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.AUTO_DELETED);
		checkCRFStatusOnly(Status.AVAILABLE);
		checkCRFVersionStatusOnly(Status.AVAILABLE);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.DELETED);
		checkStudyEventStatusOnly(Status.DELETED);

		studyService.restoreSite(site, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.AVAILABLE);
		checkSiteFirstEDCStatus(Status.AVAILABLE);
		checkSiteSecondEDCStatus(Status.AVAILABLE);
		checkCRFStatusOnly(Status.AVAILABLE);
		checkCRFVersionStatusOnly(Status.AVAILABLE);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.DELETED);
		checkStudyEventStatusOnly(Status.DELETED);

		subjectService.restoreSubject(subjectBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.AVAILABLE);
		checkSiteFirstEDCStatus(Status.AVAILABLE);
		checkSiteSecondEDCStatus(Status.AVAILABLE);
		checkCRFStatusOnly(Status.AVAILABLE);
		checkCRFVersionStatusOnly(Status.AVAILABLE);
		checkEventCRFStatus(Status.LOCKED);
		checkStudySubjectStatusOnly(Status.LOCKED);
		checkStudyEventStatusOnly(Status.LOCKED);

		studySubjectService.unlockStudySubject(studySubjectBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.AVAILABLE);
		checkSiteFirstEDCStatus(Status.AVAILABLE);
		checkSiteSecondEDCStatus(Status.AVAILABLE);
		checkCRFStatusOnly(Status.AVAILABLE);
		checkCRFVersionStatusOnly(Status.AVAILABLE);
		checkEventCRFStatus(Status.LOCKED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.LOCKED);

		studyEventService.unlockStudyEvent(studyEventBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.AVAILABLE);
		checkSiteFirstEDCStatus(Status.AVAILABLE);
		checkSiteSecondEDCStatus(Status.AVAILABLE);
		checkCRFStatusOnly(Status.AVAILABLE);
		checkCRFVersionStatusOnly(Status.AVAILABLE);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.AVAILABLE);
		checkStudyEventStatusOnly(Status.AVAILABLE);

		eventCRFService.restoreEventCRF(eventCRFBean, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyEDCStatus(Status.AVAILABLE);
		checkSiteEDCStatus(Status.AVAILABLE);
		checkStudySubjectStatus(Status.AVAILABLE);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase41() throws Exception {
		studyService.removeSite(site, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyFirstEDCStatus(Status.AVAILABLE);
		checkStudySecondEDCStatus(Status.AVAILABLE);
		checkSiteFirstEDCStatus(Status.AUTO_DELETED);
		checkSiteSecondEDCStatus(Status.AUTO_DELETED);
		checkCRFStatusOnly(Status.AVAILABLE);
		checkCRFVersionStatusOnly(Status.AVAILABLE);
		checkEventCRFStatus(Status.DELETED);
		checkStudySubjectStatusOnly(Status.DELETED);
		checkStudyEventStatusOnly(Status.DELETED);

		studyService.restoreSite(site, updater);

		checkStudySEDStatus(Status.AVAILABLE);
		checkStudyEDCStatus(Status.AVAILABLE);
		checkSiteEDCStatus(Status.AVAILABLE);
		checkStudySubjectStatus(Status.AVAILABLE);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase42() throws Exception {
		eventCRFBean.setUpdatedDate(new Date());
		eventCRFBean.setUpdater(updater);
		eventCRFBean.setStatus(Status.UNAVAILABLE);
		eventCRFBean.setSdvStatus(true);
		eventCRFDAO.update(eventCRFBean);

		EventDefinitionCRFBean eventDefinitionCRFBean = eventDefinitionCRFDAO
				.findForSiteByEventCrfId(eventCRFBean.getId());
		eventDefinitionCRFBean.setUpdatedDate(new Date());
		eventDefinitionCRFBean.setUpdater(updater);
		eventDefinitionCRFBean.setSourceDataVerification(SourceDataVerification.AllREQUIRED);
		eventDefinitionCRFDAO.update(eventDefinitionCRFBean);

		studyEventBean.setSubjectEventStatus(SubjectEventStatus.SOURCE_DATA_VERIFIED);
		studyEventBean.setUpdatedDate(new Date());
		studyEventBean.setUpdater(updater);
		studyEventDao.update(studyEventBean);

		checkEventCRFStatus(Status.UNAVAILABLE);
		checkEventCRFSDVStatusOnly(true);
		checkStudyEventStatusOnly(Status.AVAILABLE);
		checkStudyEventSubjectEventStatusOnly(SubjectEventStatus.SOURCE_DATA_VERIFIED);

		studyEventService.removeStudyEvent(studyEventBean, updater);

		checkEventCRFStatus(Status.DELETED);
		checkEventCRFSDVStatusOnly(true);
		checkStudyEventStatusOnly(Status.DELETED);
		checkStudyEventSubjectEventStatusOnly(SubjectEventStatus.REMOVED);

		studyEventService.restoreStudyEvent(studyEventBean, updater);

		checkEventCRFStatus(Status.UNAVAILABLE);
		checkEventCRFSDVStatusOnly(true);
		checkStudyEventStatusOnly(Status.AVAILABLE);
		checkStudyEventSubjectEventStatusOnly(SubjectEventStatus.SOURCE_DATA_VERIFIED);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase43() throws Exception {
		eventCRFBean.setUpdatedDate(new Date());
		eventCRFBean.setUpdater(updater);
		eventCRFBean.setStatus(Status.UNAVAILABLE);
		eventCRFBean.setSdvStatus(true);
		eventCRFDAO.update(eventCRFBean);

		EventDefinitionCRFBean eventDefinitionCRFBean = eventDefinitionCRFDAO
				.findForSiteByEventCrfId(eventCRFBean.getId());
		eventDefinitionCRFBean.setUpdatedDate(new Date());
		eventDefinitionCRFBean.setUpdater(updater);
		eventDefinitionCRFBean.setSourceDataVerification(SourceDataVerification.AllREQUIRED);
		eventDefinitionCRFDAO.update(eventDefinitionCRFBean);

		siteEventDefinitionCRFBean1.setStatus(Status.DELETED);
		siteEventDefinitionCRFBean1.setUpdatedDate(new Date());
		siteEventDefinitionCRFBean1.setUpdater(updater);
		eventDefinitionCRFDAO.update(siteEventDefinitionCRFBean1);

		studyEventBean.setSubjectEventStatus(SubjectEventStatus.SOURCE_DATA_VERIFIED);
		studyEventBean.setUpdatedDate(new Date());
		studyEventBean.setUpdater(updater);
		studyEventDao.update(studyEventBean);

		checkEventCRFStatus(Status.UNAVAILABLE);
		checkEventCRFSDVStatusOnly(true);
		checkStudyEventStatusOnly(Status.AVAILABLE);
		checkStudyEventSubjectEventStatusOnly(SubjectEventStatus.SOURCE_DATA_VERIFIED);

		List<ItemDataBean> itemDataBeanList = itemDataDAO.findAllByEventCRFId(eventCRFBean.getId());
		ItemDataBean itemDataBean = itemDataBeanList.get(0);
		itemDataBean.setValue("tooth pain");
		itemDataBean.setUpdatedDate(new Date());
		itemDataBean.setUpdater(updater);
		itemDataDAO.update(itemDataBean);

		ItemBean itemBean = (ItemBean) idao.findByPK(itemDataBean.getItemId());
		itemBean.setDataType(ItemDataType.CODE);
		itemBean.setUpdatedDate(new Date());
		itemBean.setUpdater(updater);
		idao.update(itemBean);

		ItemFormMetadataBean itemFormMetadataBean = imfdao.findByItemIdAndCRFVersionId(itemBean.getId(),
				eventCRFBean.getCRFVersionId());
		itemFormMetadataBean.setCodeRef("icd_9cm");
		imfdao.update(itemFormMetadataBean);

		CodedItem codedItem = codedItemService.createCodedItem(eventCRFBean, itemBean, itemDataBean, site);
		assertTrue(codedItem.getId() > 0);

		CodedItemElement codedItemElement = new CodedItemElement(itemDataBean.getId(),
				StringUtils.substringAfterLast(itemBean.getName(), "_"));
		codedItem.addCodedItemElements(codedItemElement);
		codedItemDAO.saveOrUpdate(codedItem);

		studyEventService.removeStudyEvent(studyEventBean, updater);

		checkStudyEventStatus(Status.DELETED);

		studyEventService.restoreStudyEvent(studyEventBean, updater);

		checkEventCRFStatus(Status.UNAVAILABLE);
		checkEventCRFSDVStatusOnly(true);
		checkStudyEventStatusOnly(Status.AVAILABLE);
		checkStudyEventSubjectEventStatusOnly(SubjectEventStatus.SOURCE_DATA_VERIFIED);

		codedItemService.saveCodedItem(codedItem);

		checkEventCRFStatus(Status.UNAVAILABLE);
		checkEventCRFSDVStatusOnly(false);
		checkStudyEventStatusOnly(Status.AVAILABLE);
		checkStudyEventSubjectEventStatusOnly(SubjectEventStatus.COMPLETED);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase44() throws Exception {
		eventCRFBean.setUpdatedDate(new Date());
		eventCRFBean.setUpdater(updater);
		eventCRFBean.setStatus(Status.UNAVAILABLE);
		eventCRFBean.setSdvStatus(true);
		eventCRFDAO.update(eventCRFBean);

		EventDefinitionCRFBean eventDefinitionCRFBean = eventDefinitionCRFDAO
				.findForSiteByEventCrfId(eventCRFBean.getId());
		eventDefinitionCRFBean.setUpdatedDate(new Date());
		eventDefinitionCRFBean.setUpdater(updater);
		eventDefinitionCRFBean.setSourceDataVerification(SourceDataVerification.AllREQUIRED);
		eventDefinitionCRFDAO.update(eventDefinitionCRFBean);

		siteEventDefinitionCRFBean1.setStatus(Status.DELETED);
		siteEventDefinitionCRFBean1.setUpdatedDate(new Date());
		siteEventDefinitionCRFBean1.setUpdater(updater);
		eventDefinitionCRFDAO.update(siteEventDefinitionCRFBean1);

		studyEventBean.setSubjectEventStatus(SubjectEventStatus.SIGNED);
		studyEventBean.setUpdatedDate(new Date());
		studyEventBean.setUpdater(updater);
		studyEventDao.update(studyEventBean);

		checkEventCRFStatus(Status.UNAVAILABLE);
		checkEventCRFSDVStatusOnly(true);
		checkStudyEventStatusOnly(Status.AVAILABLE);
		checkStudyEventSubjectEventStatusOnly(SubjectEventStatus.SIGNED);

		List<ItemDataBean> itemDataBeanList = itemDataDAO.findAllByEventCRFId(eventCRFBean.getId());
		ItemDataBean itemDataBean = itemDataBeanList.get(0);
		itemDataBean.setValue("tooth pain");
		itemDataBean.setUpdatedDate(new Date());
		itemDataBean.setUpdater(updater);
		itemDataDAO.update(itemDataBean);

		ItemBean itemBean = (ItemBean) idao.findByPK(itemDataBean.getItemId());
		itemBean.setDataType(ItemDataType.CODE);
		itemBean.setUpdatedDate(new Date());
		itemBean.setUpdater(updater);
		idao.update(itemBean);

		ItemFormMetadataBean itemFormMetadataBean = imfdao.findByItemIdAndCRFVersionId(itemBean.getId(),
				eventCRFBean.getCRFVersionId());
		itemFormMetadataBean.setCodeRef("icd_9cm");
		imfdao.update(itemFormMetadataBean);

		CodedItem codedItem = codedItemService.createCodedItem(eventCRFBean, itemBean, itemDataBean, site);
		assertTrue(codedItem.getId() > 0);

		CodedItemElement codedItemElement = new CodedItemElement(itemDataBean.getId(),
				StringUtils.substringAfterLast(itemBean.getName(), "_"));
		codedItem.addCodedItemElements(codedItemElement);
		codedItemDAO.saveOrUpdate(codedItem);

		studyEventService.removeStudyEvent(studyEventBean, updater);

		checkStudyEventStatus(Status.DELETED);

		studyEventService.restoreStudyEvent(studyEventBean, updater);

		checkEventCRFStatus(Status.UNAVAILABLE);
		checkEventCRFSDVStatusOnly(true);
		checkStudyEventStatusOnly(Status.AVAILABLE);
		checkStudyEventSubjectEventStatusOnly(SubjectEventStatus.SIGNED);

		codedItemService.saveCodedItem(codedItem);

		checkEventCRFStatus(Status.UNAVAILABLE);
		checkEventCRFSDVStatusOnly(false);
		checkStudyEventStatusOnly(Status.AVAILABLE);
		checkStudyEventSubjectEventStatusOnly(SubjectEventStatus.COMPLETED);
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase45() throws Exception {
		eventCRFBean.setUpdatedDate(new Date());
		eventCRFBean.setUpdater(updater);
		eventCRFBean.setStatus(Status.PENDING);
		eventCRFBean.setSdvStatus(false);
		eventCRFBean.setValidatorId(0);
		eventCRFDAO.update(eventCRFBean);

		checkEventCRFStatus(Status.PENDING);
		assertTrue(eventCRFBean.getStage().equals(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE));

		studySubjectService.removeStudySubject(studySubjectBean, updater);

		checkStudySubjectStatus(Status.DELETED);

		studySubjectService.restoreStudySubject(studySubjectBean, updater);

		checkEventCRFStatus(Status.PENDING);
		assertTrue(eventCRFBean.getStage().equals(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE));
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase46() throws Exception {
		eventCRFBean.setUpdatedDate(new Date());
		eventCRFBean.setUpdater(updater);
		eventCRFBean.setStatus(Status.PENDING);
		eventCRFBean.setSdvStatus(false);
		eventCRFBean.setValidatorId(1);
		eventCRFDAO.update(eventCRFBean);

		List<ItemDataBean> itemDataList = itemDataDAO.findAllByEventCRFId(EVENT_CRF_ID);

		ItemDataBean itemDataBean = itemDataList.get(0);
		itemDataBean.setUpdater(updater);
		itemDataBean.setUpdatedDate(new Date());
		itemDataBean.setStatus(Status.UNAVAILABLE);
		itemDataDAO.update(itemDataBean);

		itemDataBean = itemDataList.get(1);
		itemDataBean.setUpdater(updater);
		itemDataBean.setUpdatedDate(new Date());
		itemDataBean.setStatus(Status.PENDING);
		itemDataDAO.update(itemDataBean);

		fillItemDataStatusMap();

		checkEventCRFStatus(Status.PENDING);
		assertTrue(eventCRFBean.getStage().equals(DataEntryStage.DOUBLE_DATA_ENTRY));

		studySubjectService.removeStudySubject(studySubjectBean, updater);

		checkStudySubjectStatus(Status.DELETED);

		studySubjectService.restoreStudySubject(studySubjectBean, updater);

		checkEventCRFStatus(Status.PENDING);
		assertTrue(eventCRFBean.getStage().equals(DataEntryStage.DOUBLE_DATA_ENTRY));
	}

	@Test
	public void testThatObjectsGetStatusesCorrectlyCase47() throws Exception {
		eventCRFBean.setUpdatedDate(new Date());
		eventCRFBean.setUpdater(updater);
		eventCRFBean.setStatus(Status.PARTIAL_DATA_ENTRY);
		eventCRFBean.setSdvStatus(false);
		eventCRFDAO.update(eventCRFBean);

		checkEventCRFStatus(Status.PARTIAL_DATA_ENTRY);
		assertTrue(eventCRFBean.getStage().equals(DataEntryStage.INITIAL_DATA_ENTRY));

		studySubjectService.removeStudySubject(studySubjectBean, updater);

		checkStudySubjectStatus(Status.DELETED);

		studySubjectService.restoreStudySubject(studySubjectBean, updater);

		checkEventCRFStatus(Status.PARTIAL_DATA_ENTRY);
		assertTrue(eventCRFBean.getStage().equals(DataEntryStage.INITIAL_DATA_ENTRY));
	}
}
