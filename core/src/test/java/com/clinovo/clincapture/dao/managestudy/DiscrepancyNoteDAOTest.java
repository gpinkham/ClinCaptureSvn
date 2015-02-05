/*******************************************************************************
 * Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.clinovo.clincapture.dao.managestudy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteStatisticBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.dao.managestudy.ListNotesFilter;
import org.akaza.openclinica.dao.managestudy.ListNotesSort;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.Before;
import org.junit.Test;

import com.clinovo.model.DiscrepancyCorrectionForm;

public class DiscrepancyNoteDAOTest extends DefaultAppContextTest {

	private StudyBean study;
	private UserAccountBean user;
	private ListNotesFilter notesFilter;
	private ListNotesSort notesSort;
	private ResourceBundle resword;

	Map<Integer, DiscrepancyNoteBean> noteBeanMap;

	@Before
	public void setUp() throws Exception {
		study = (StudyBean) studyDAO.findByPK(1);
		user = (UserAccountBean) userAccountDAO.findByPK(1);
		resword = ResourceBundleProvider.getWordsBundle(Locale.ENGLISH);
		notesFilter = new ListNotesFilter();
		notesSort = new ListNotesSort();
		noteBeanMap = new HashMap<Integer, DiscrepancyNoteBean>();
		for (int i = 0; i < 5; i++) {
			noteBeanMap.put(i + 1, (DiscrepancyNoteBean) discrepancyNoteDAO.findByPK(i + 1));
		}
	}

	@Test
	public void testFilterByStudySubjectId() {
		notesFilter = new ListNotesFilter();
		notesFilter.addFilter("studySubject.label", "ssID1");
		assertEquals(Integer.valueOf(5), discrepancyNoteDAO.countViewNotesWithFilter(study, notesFilter));

		List<DiscrepancyNoteBean> noteBeans = discrepancyNoteDAO.getViewNotesWithFilterAndSortLimits(study,
				notesFilter, notesSort, 0, 100);
		assertDNBeansInList(noteBeans, Arrays.asList(1, 2, 3, 4, 5));
	}

	@Test
	public void testOffset() {
		List<DiscrepancyNoteBean> noteBeans = discrepancyNoteDAO.getViewNotesWithFilterAndSortLimits(study,
				notesFilter, notesSort, 2, 100);
		assertEquals(3, noteBeans.size());
	}

	@Test
	public void testLimit() {
		List<DiscrepancyNoteBean> noteBeans = discrepancyNoteDAO.getViewNotesWithFilterAndSortLimits(study,
				notesFilter, notesSort, 0, 3);
		assertEquals(3, noteBeans.size());
	}

	@Test
	public void testFilterByType() {
		notesFilter = new ListNotesFilter();
		notesFilter.addFilter("discrepancyNoteBean.disType", "1");
		assertEquals(Integer.valueOf(2), discrepancyNoteDAO.countViewNotesWithFilter(study, notesFilter));

		List<DiscrepancyNoteBean> noteBeans = discrepancyNoteDAO.getViewNotesWithFilterAndSortLimits(study,
				notesFilter, notesSort, 0, 100);
		assertDNBeansInList(noteBeans, Arrays.asList(1, 4));
	}

	@Test
	public void testFilterByStatus() {
		notesFilter = new ListNotesFilter();
		notesFilter.addFilter("discrepancyNoteBean.resolutionStatus", "1");
		assertEquals(Integer.valueOf(2), discrepancyNoteDAO.countViewNotesWithFilter(study, notesFilter));

		List<DiscrepancyNoteBean> noteBeans = discrepancyNoteDAO.getViewNotesWithFilterAndSortLimits(study,
				notesFilter, notesSort, 0, 100);
		assertDNBeansInList(noteBeans, Arrays.asList(4, 5));
	}

	@Test
	public void testFilterBySiteId() {
		notesFilter = new ListNotesFilter();
		notesFilter.addFilter("siteId", "default-study");
		assertEquals(Integer.valueOf(5), discrepancyNoteDAO.countViewNotesWithFilter(study, notesFilter));

		List<DiscrepancyNoteBean> noteBeans = discrepancyNoteDAO.getViewNotesWithFilterAndSortLimits(study,
				notesFilter, notesSort, 0, 100);
		assertDNBeansInList(noteBeans, Arrays.asList(1, 2, 3, 4, 5));
	}

	@Test
	public void testFilterByEventName() {
		notesFilter = new ListNotesFilter();
		notesFilter.addFilter("eventName", "ED-1");
		assertEquals(Integer.valueOf(3), discrepancyNoteDAO.countViewNotesWithFilter(study, notesFilter));

		List<DiscrepancyNoteBean> noteBeans = discrepancyNoteDAO.getViewNotesWithFilterAndSortLimits(study,
				notesFilter, notesSort, 0, 100);
		assertDNBeansInList(noteBeans, Arrays.asList(1, 4, 5));
	}

	@Test
	public void testFilterByCrf() {
		notesFilter = new ListNotesFilter();
		notesFilter.addFilter("crfName", "Administration");
		assertEquals(Integer.valueOf(2), discrepancyNoteDAO.countViewNotesWithFilter(study, notesFilter));

		List<DiscrepancyNoteBean> noteBeans = discrepancyNoteDAO.getViewNotesWithFilterAndSortLimits(study,
				notesFilter, notesSort, 0, 100);
		assertDNBeansInList(noteBeans, Arrays.asList(1, 4));
	}

	@Test
	public void testFilterByEntityName() {
		notesFilter = new ListNotesFilter();
		notesFilter.addFilter("entityName", "periodStart");
		assertEquals(Integer.valueOf(1), discrepancyNoteDAO.countViewNotesWithFilter(study, notesFilter));

		List<DiscrepancyNoteBean> noteBeans = discrepancyNoteDAO.getViewNotesWithFilterAndSortLimits(study,
				notesFilter, notesSort, 0, 100);
		assertDNBeansInList(noteBeans, Arrays.asList(1));
	}

	@Test
	public void testFilterByEntityValue() {
		notesFilter = new ListNotesFilter();
		notesFilter.addFilter("entityValue", "2008");
		assertEquals(Integer.valueOf(1), discrepancyNoteDAO.countViewNotesWithFilter(study, notesFilter));

		List<DiscrepancyNoteBean> noteBeans = discrepancyNoteDAO.getViewNotesWithFilterAndSortLimits(study,
				notesFilter, notesSort, 0, 100);
		assertDNBeansInList(noteBeans, Arrays.asList(1));
	}

	@Test
	public void testFilterByEntityTypeItemData() {
		notesFilter = new ListNotesFilter();
		notesFilter.addFilter("discrepancyNoteBean.entityType", "itemData");
		assertEquals(Integer.valueOf(1), discrepancyNoteDAO.countViewNotesWithFilter(study, notesFilter));

		List<DiscrepancyNoteBean> noteBeans = discrepancyNoteDAO.getViewNotesWithFilterAndSortLimits(study,
				notesFilter, notesSort, 0, 100);
		assertDNBeansInList(noteBeans, Arrays.asList(1));
	}

	@Test
	public void testFilterByEntityTypeSubject() {
		notesFilter = new ListNotesFilter();
		notesFilter.addFilter("discrepancyNoteBean.entityType", "subject");
		assertEquals(Integer.valueOf(1), discrepancyNoteDAO.countViewNotesWithFilter(study, notesFilter));

		List<DiscrepancyNoteBean> noteBeans = discrepancyNoteDAO.getViewNotesWithFilterAndSortLimits(study,
				notesFilter, notesSort, 0, 100);
		assertDNBeansInList(noteBeans, Arrays.asList(2));
	}

	@Test
	public void testFilterByEntityTypeStudySubject() {
		notesFilter = new ListNotesFilter();
		notesFilter.addFilter("discrepancyNoteBean.entityType", "studySub");
		assertEquals(Integer.valueOf(1), discrepancyNoteDAO.countViewNotesWithFilter(study, notesFilter));

		List<DiscrepancyNoteBean> noteBeans = discrepancyNoteDAO.getViewNotesWithFilterAndSortLimits(study,
				notesFilter, notesSort, 0, 100);
		assertDNBeansInList(noteBeans, Arrays.asList(3));
	}

	@Test
	public void testFilterByEntityTypeEventCRF() {
		notesFilter = new ListNotesFilter();
		notesFilter.addFilter("discrepancyNoteBean.entityType", "eventCRF");
		assertEquals(Integer.valueOf(1), discrepancyNoteDAO.countViewNotesWithFilter(study, notesFilter));

		List<DiscrepancyNoteBean> noteBeans = discrepancyNoteDAO.getViewNotesWithFilterAndSortLimits(study,
				notesFilter, notesSort, 0, 100);
		assertDNBeansInList(noteBeans, Arrays.asList(4));
	}

	@Test
	public void testFilterByEntityTypeStudyEvent() {
		notesFilter = new ListNotesFilter();
		notesFilter.addFilter("discrepancyNoteBean.entityType", "studyEvent");
		assertEquals(Integer.valueOf(1), discrepancyNoteDAO.countViewNotesWithFilter(study, notesFilter));

		List<DiscrepancyNoteBean> noteBeans = discrepancyNoteDAO.getViewNotesWithFilterAndSortLimits(study,
				notesFilter, notesSort, 0, 100);
		assertDNBeansInList(noteBeans, Arrays.asList(5));
	}

	@Test
	public void testFilterByDescription() {
		notesFilter = new ListNotesFilter();
		notesFilter.addFilter("discrepancyNoteBean.description", "entry error");
		assertEquals(Integer.valueOf(2), discrepancyNoteDAO.countViewNotesWithFilter(study, notesFilter));

		List<DiscrepancyNoteBean> noteBeans = discrepancyNoteDAO.getViewNotesWithFilterAndSortLimits(study,
				notesFilter, notesSort, 0, 100);
		assertDNBeansInList(noteBeans, Arrays.asList(4, 5));
	}

	@Test
	public void testFilterByAssignedUser() {
		notesFilter = new ListNotesFilter();
		notesFilter.addFilter("discrepancyNoteBean.user", "root");
		assertEquals(Integer.valueOf(3), discrepancyNoteDAO.countViewNotesWithFilter(study, notesFilter));

		List<DiscrepancyNoteBean> noteBeans = discrepancyNoteDAO.getViewNotesWithFilterAndSortLimits(study,
				notesFilter, notesSort, 0, 100);
		assertDNBeansInList(noteBeans, Arrays.asList(1, 3, 4));
	}

	@Test
	public void complexFilter() {
		notesFilter = new ListNotesFilter();
		notesFilter.addFilter("discrepancyNoteBean.resolutionStatus", "1");
		notesFilter.addFilter("eventName", "ED-1");
		notesFilter.addFilter("discrepancyNoteBean.description", "entry error");
		assertEquals(Integer.valueOf(2), discrepancyNoteDAO.countViewNotesWithFilter(study, notesFilter));

		List<DiscrepancyNoteBean> noteBeans = discrepancyNoteDAO.getViewNotesWithFilterAndSortLimits(study,
				notesFilter, notesSort, 0, 100);
		assertDNBeansInList(noteBeans, Arrays.asList(4, 5));
	}

	@Test
	public void complexFilter2() {
		notesFilter = new ListNotesFilter();
		notesFilter.addFilter("discrepancyNoteBean.disType", "1");
		notesFilter.addFilter("eventName", "ED-1");
		notesFilter.addFilter("crfName", "Administration");
		notesFilter.addFilter("discrepancyNoteBean.user", "root");
		assertEquals(Integer.valueOf(2), discrepancyNoteDAO.countViewNotesWithFilter(study, notesFilter));

		List<DiscrepancyNoteBean> noteBeans = discrepancyNoteDAO.getViewNotesWithFilterAndSortLimits(study,
				notesFilter, notesSort, 0, 100);
		assertDNBeansInList(noteBeans, Arrays.asList(1, 4));
	}

	@Test
	public void testThatGetUserDNsReturnsCorrectSizedListOfUserDNs() {
		assertEquals(
				5,
				discrepancyNoteDAO.getViewNotesWithFilterAndSort(study, user, new ListNotesFilter(),
						new ListNotesSort()).size());
	}

	@Test
	public void testThatGetUserDNsReturnsEmptyListForUserWithNoDNs() {
		UserAccountBean newUser = new UserAccountBean();
		newUser.setId(111);
		assertEquals(
				0,
				discrepancyNoteDAO.getViewNotesWithFilterAndSort(study, newUser, new ListNotesFilter(),
						new ListNotesSort()).size());
	}

	@Test
	public void testStatistics() {
		List<DiscrepancyNoteStatisticBean> statisticBeans = discrepancyNoteDAO.countNotesStatistic(study);

		assertEquals(5, statisticBeans.size());
		assertTrue(statisticBeans.contains(new DiscrepancyNoteStatisticBean(1, 1, 1)));
		assertTrue(statisticBeans.contains(new DiscrepancyNoteStatisticBean(1, 1, 4)));
		assertTrue(statisticBeans.contains(new DiscrepancyNoteStatisticBean(1, 2, 5)));
		assertTrue(statisticBeans.contains(new DiscrepancyNoteStatisticBean(1, 3, 1)));
		assertTrue(statisticBeans.contains(new DiscrepancyNoteStatisticBean(1, 3, 2)));
	}

	@Test
	public void testThatCountUserDNStatisticsReturnsCorrectSizedListOfStats() {
		assertEquals(5, discrepancyNoteDAO.countUserNotesStatistics(study, user).size());
	}

	@Test
	public void testThatCountUserDNStatisticsReturnsEmptyListForUserWithNoDNs() {
		UserAccountBean newUser = new UserAccountBean();
		newUser.setId(111);
		assertEquals(0, discrepancyNoteDAO.countUserNotesStatistics(study, newUser).size());
	}

	@Test
	public void testStatisticsOnStudySubjectRemoved() {
		// change statuses to Removed and save initial statuses
		UserAccountBean updater = new UserAccountBean();
		updater.setId(1);

		StudySubjectBean ssb = (StudySubjectBean) studySubjectDAO.findByPK(1);
		Status subjectInitStatus = ssb.getStatus();
		ssb.setStatus(Status.DELETED);
		ssb.setUpdater(updater);
		studySubjectDAO.update(ssb);

		StudyEventBean seb = (StudyEventBean) studyEventDao.findByPK(1);
		Status eventInitStatus = seb.getStatus();
		seb.setStatus(Status.AUTO_DELETED);
		seb.setUpdater(updater);
		studyEventDao.update(seb);

		EventCRFBean ecb = (EventCRFBean) eventCRFDAO.findByPK(1);
		Status eventCRFInitStatus = ecb.getStatus();
		ecb.setStatus(Status.AUTO_DELETED);
		eventCRFDAO.update(ecb);
		// ----------------------------------------------------

		List<DiscrepancyNoteStatisticBean> statisticBeans = discrepancyNoteDAO.countNotesStatistic(study);

		assertTrue(statisticBeans.isEmpty());

		// restore initial statuses
		ssb.setStatus(subjectInitStatus);
		studySubjectDAO.update(ssb);

		seb.setStatus(eventInitStatus);
		studyEventDao.update(seb);

		ecb.setStatus(eventCRFInitStatus);
		eventCRFDAO.update(ecb);
	}

	@Test
	public void testStatisticsOnStudyEventRemoved() {
		// change statuses to Removed and save initial statuses
		UserAccountBean updater = new UserAccountBean();
		updater.setId(1);

		StudySubjectBean ssb = (StudySubjectBean) studySubjectDAO.findByPK(1);
		Status subjectInitStatus = ssb.getStatus();
		ssb.setStatus(Status.AVAILABLE);
		ssb.setUpdater(updater);
		studySubjectDAO.update(ssb);

		StudyEventBean seb = (StudyEventBean) studyEventDao.findByPK(1);
		Status eventInitStatus = seb.getStatus();
		seb.setStatus(Status.DELETED);
		seb.setUpdater(updater);
		studyEventDao.update(seb);

		EventCRFBean ecb = (EventCRFBean) eventCRFDAO.findByPK(1);
		Status eventCRFInitStatus = ecb.getStatus();
		ecb.setStatus(Status.AUTO_DELETED);
		eventCRFDAO.update(ecb);
		// ----------------------------------------------------

		List<DiscrepancyNoteStatisticBean> statisticBeans = discrepancyNoteDAO.countNotesStatistic(study);

		assertEquals(2, statisticBeans.size());
		assertTrue(statisticBeans.contains(new DiscrepancyNoteStatisticBean(1, 2, 5)));
		assertTrue(statisticBeans.contains(new DiscrepancyNoteStatisticBean(1, 3, 2)));

		// restore initial statuses
		ssb.setStatus(subjectInitStatus);
		studySubjectDAO.update(ssb);

		seb.setStatus(eventInitStatus);
		studyEventDao.update(seb);

		ecb.setStatus(eventCRFInitStatus);
		eventCRFDAO.update(ecb);
	}

	@Test
	public void testStatisticsOnEventCRFRemoved() {
		// change statuses to Removed and save initial statuses
		UserAccountBean updater = new UserAccountBean();
		updater.setId(1);

		StudySubjectBean ssb = (StudySubjectBean) studySubjectDAO.findByPK(1);
		Status subjectInitStatus = ssb.getStatus();
		ssb.setStatus(Status.AVAILABLE);
		ssb.setUpdater(updater);
		studySubjectDAO.update(ssb);

		StudyEventBean seb = (StudyEventBean) studyEventDao.findByPK(1);
		Status eventInitStatus = seb.getStatus();
		seb.setStatus(Status.AVAILABLE);
		seb.setUpdater(updater);
		studyEventDao.update(seb);

		EventCRFBean ecb = (EventCRFBean) eventCRFDAO.findByPK(1);
		Status eventCRFInitStatus = ecb.getStatus();
		ecb.setStatus(Status.DELETED);
		eventCRFDAO.update(ecb);
		// ----------------------------------------------------

		List<DiscrepancyNoteStatisticBean> statisticBeans = discrepancyNoteDAO.countNotesStatistic(study);

		assertEquals(3, statisticBeans.size());
		assertTrue(statisticBeans.contains(new DiscrepancyNoteStatisticBean(1, 2, 5)));
		assertTrue(statisticBeans.contains(new DiscrepancyNoteStatisticBean(1, 3, 1)));
		assertTrue(statisticBeans.contains(new DiscrepancyNoteStatisticBean(1, 3, 2)));

		// restore initial statuses
		ssb.setStatus(subjectInitStatus);
		studySubjectDAO.update(ssb);

		seb.setStatus(eventInitStatus);
		studyEventDao.update(seb);

		ecb.setStatus(eventCRFInitStatus);
		eventCRFDAO.update(ecb);
	}

	@Test
	public void testThatStudyEventDoesNotHaveDNs() {
		StudyEventBean seb = new StudyEventBean();
		seb.setId(1);
		assertFalse(discrepancyNoteDAO.doesNotHaveOutstandingDNs(seb));
	}

	private void assertDNBeansInList(List<DiscrepancyNoteBean> dns, List<Integer> ids) {
		for (Integer id : ids) {
			DiscrepancyNoteBean noteBean = noteBeanMap.get(id);
			assertNotNull(noteBean);
			assertTrue(dns.contains(noteBean));
		}
	}

	@Test
	public void testCountAllByStudyEventTypeAndStudyEvent() {
		StudyEventBean studyEvent = new StudyEventBean();
		studyEvent.setId(1000);

		assertNotNull(discrepancyNoteDAO.countAllByStudyEventTypeAndStudyEvent(studyEvent));
		assertEquals(new Integer(0), discrepancyNoteDAO.countAllByStudyEventTypeAndStudyEvent(studyEvent));
	}

	@Test
	public void testCountViewNotesByStatusId() {
		int studyId = 1;
		int statusId = 1;

		assertNotNull(discrepancyNoteDAO.countViewNotesByStatusId(studyId, statusId));
		assertEquals(2, discrepancyNoteDAO.countViewNotesByStatusId(studyId, statusId));
	}

	@Test
	public void testFindAllByEntityAndColumnAndStudyCorrectForSubject() {
		int entityId = 1;
		StudyBean currentStudy = new StudyBean();
		currentStudy.setId(1);
		currentStudy.setParentStudyId(0);
		assertNotNull(discrepancyNoteDAO.findAllByEntityAndColumnAndStudy(currentStudy, "subject", entityId, ""));
		assertEquals(1, discrepancyNoteDAO.findAllByEntityAndColumnAndStudy(currentStudy, "subject", entityId, "")
				.size());
	}

	@Test
	public void testFindAllEvCRFIdsWithUnclosedDNsByStSubIdReturnsCorrectValue() {
		int studySubjectId = 1;
		assertNotNull(discrepancyNoteDAO.findAllEvCRFIdsWithUnclosedDNsByStSubId(studySubjectId));
		assertEquals(0, discrepancyNoteDAO.findAllEvCRFIdsWithUnclosedDNsByStSubId(studySubjectId).size());
	}

	@Test
	public void testThatFindAllByCRFIdNotEmpty() {

		assertEquals(1, discrepancyNoteDAO.findAllByCRFId(1).size());
	}

	@Test
	public void testThatFindAllByCrfVersionIdNotEmpty() {

		assertEquals(1, discrepancyNoteDAO.findAllByCrfVersionId(1).size());
	}

	@Test
	public void testThatFindAllByCRFIdContainsSubjectLabel() {

		assertEquals("ssID1", discrepancyNoteDAO.findAllByCRFId(1).get(0).getStudySub().getLabel());
	}

	@Test
	public void testThatGetDCFsByNoteIdsReturnsCorrectNumberOfDCFs() {
		List<DiscrepancyCorrectionForm> dcfs = discrepancyNoteDAO.getDiscrepancyCorrectionFormsByNoteIds(study,
				resword, 1);
		assertEquals(1, dcfs.size());
	}

	@Test
	public void testThatGetDCFsByNoteIdsReturnsDCFWithCorrectStudyName() {
		List<DiscrepancyCorrectionForm> dcfs = discrepancyNoteDAO.getDiscrepancyCorrectionFormsByNoteIds(study,
				resword, 1);
		assertEquals("Default Study", dcfs.get(0).getStudyName());
	}

	@Test
	public void testThatGetDCFsByNoteIdsReturnsDCFWithCorrectSiteName() {
		List<DiscrepancyCorrectionForm> dcfs = discrepancyNoteDAO.getDiscrepancyCorrectionFormsByNoteIds(study,
				resword, 1);
		assertEquals("Default Study", dcfs.get(0).getSiteName());
	}

	@Test
	public void testThatGetDCFsByNoteIdsReturnsDCFWithCorrectCrfItemName() {
		List<DiscrepancyCorrectionForm> dcfs = discrepancyNoteDAO.getDiscrepancyCorrectionFormsByNoteIds(study,
				resword, 1);
		assertEquals("Start of Agent Administration Period", dcfs.get(0).getCrfItemName());
	}

	@Test
	public void testThatGetDCFsByNoteIdsReturnsDCFWithCorrectCrfName() {
		List<DiscrepancyCorrectionForm> dcfs = discrepancyNoteDAO.getDiscrepancyCorrectionFormsByNoteIds(study,
				resword, 1);
		assertEquals("Agent Administration", dcfs.get(0).getCrfName());
	}

	@Test
	public void testThatGetDCFsByNoteIdsReturnsDCFWithCorrectSubjectID() {
		List<DiscrepancyCorrectionForm> dcfs = discrepancyNoteDAO.getDiscrepancyCorrectionFormsByNoteIds(study,
				resword, 1);
		assertEquals("ssID1", dcfs.get(0).getSubjectId());
	}

	@Test
	public void testThatGetDCFsByNoteIdsReturnsDCFWithCorrectEventName() {
		List<DiscrepancyCorrectionForm> dcfs = discrepancyNoteDAO.getDiscrepancyCorrectionFormsByNoteIds(study,
				resword, 1);
		assertEquals("ED-1-NonRepeating", dcfs.get(0).getEventName());
	}

	@Test
	public void testThatGetDCFsByNoteIdsReturnsDCFWithCorrectQuestion() {
		List<DiscrepancyCorrectionForm> dcfs = discrepancyNoteDAO.getDiscrepancyCorrectionFormsByNoteIds(study,
				resword, 1);
		assertEquals("10:00pm bedtime\n[The input you provided is not an integer.]", dcfs.get(0).getQuestionToSite());
	}

	@Test
	public void testThatGetDCFsByNoteIdsReturnsDCFWithCorrectPageNumber() {
		List<DiscrepancyCorrectionForm> dcfs = discrepancyNoteDAO.getDiscrepancyCorrectionFormsByNoteIds(study,
				resword, 1);
		assertEquals("AdministrationandDosage", dcfs.get(0).getPage());
	}

	@Test
	public void testThatGetDCFsByNoteIdsReturnsDCFWithCorrectNoteId() {
		List<DiscrepancyCorrectionForm> dcfs = discrepancyNoteDAO.getDiscrepancyCorrectionFormsByNoteIds(study,
				resword, 1);
		assertEquals(1, dcfs.get(0).getNoteId().intValue());
	}

	@Test
	public void testThatGetDCFsByNoteIdsReturnsDCFWithCorrectNoteType() {
		List<DiscrepancyCorrectionForm> dcfs = discrepancyNoteDAO.getDiscrepancyCorrectionFormsByNoteIds(study,
				resword, 1);
		assertEquals("Failed Validation Check", dcfs.get(0).getNoteType());
	}

	@Test
	public void testThatGetDCFsByNoteIdsReturnsDCFWithCorrectResolutionStatus() {
		List<DiscrepancyCorrectionForm> dcfs = discrepancyNoteDAO.getDiscrepancyCorrectionFormsByNoteIds(study,
				resword, 1);
		assertEquals("Closed", dcfs.get(0).getResolutionStatus());
	}

	@Test
	public void testThatGetDCFsByNoteIdsReturnsDCFWithCorrectStudyProtocol() {
		List<DiscrepancyCorrectionForm> dcfs = discrepancyNoteDAO.getDiscrepancyCorrectionFormsByNoteIds(study,
				resword, 1);
		assertEquals("default-study", dcfs.get(0).getStudyProtocolID());
	}

	@Test
	public void testThatGetDCFsByNoteIdsReturnsDCFWithCorrectSiteOID() {
		List<DiscrepancyCorrectionForm> dcfs = discrepancyNoteDAO.getDiscrepancyCorrectionFormsByNoteIds(study,
				resword, 1);
		assertEquals("S_DEFAULTS1", dcfs.get(0).getSiteOID());
	}

	@Test
	public void testThatEventCrfDoesNotHaveDNs() {
		EventCRFBean ecb = new EventCRFBean();
		ecb.setId(1);
		assertFalse(discrepancyNoteDAO.doesNotHaveOutstandingDNs(ecb));
	}

}
