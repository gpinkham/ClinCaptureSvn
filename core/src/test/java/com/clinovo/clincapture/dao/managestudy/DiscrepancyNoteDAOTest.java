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
import java.util.Map;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteStatisticBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.managestudy.ListNotesFilter;
import org.akaza.openclinica.dao.managestudy.ListNotesSort;
import org.junit.Before;
import org.junit.Test;

public class DiscrepancyNoteDAOTest extends DefaultAppContextTest {

	StudyBean study;
	ListNotesFilter notesFilter;
	ListNotesSort notesSort;

	Map<Integer, DiscrepancyNoteBean> noteBeanMap;

	@Before
	public void setUp() throws Exception {

		study = new StudyBean();
		study.setId(1);
		study.setParentStudyId(0);

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
		assertEquals(Integer.valueOf(3), discrepancyNoteDAO.countViewNotesWithFilter(study, notesFilter));

		List<DiscrepancyNoteBean> noteBeans = discrepancyNoteDAO.getViewNotesWithFilterAndSortLimits(study,
				notesFilter, notesSort, 0, 100);
		assertDNBeansInList(noteBeans, Arrays.asList(2, 4, 5));
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
	public void testStatistics() {
		List<DiscrepancyNoteStatisticBean> statisticBeans = discrepancyNoteDAO.countNotesStatistic(study);

		assertTrue(statisticBeans.contains(new DiscrepancyNoteStatisticBean(1, 1, 1)));
		assertTrue(statisticBeans.contains(new DiscrepancyNoteStatisticBean(1, 1, 4)));
		assertTrue(statisticBeans.contains(new DiscrepancyNoteStatisticBean(1, 2, 1)));
		assertTrue(statisticBeans.contains(new DiscrepancyNoteStatisticBean(1, 3, 1)));
		assertTrue(statisticBeans.contains(new DiscrepancyNoteStatisticBean(1, 3, 2)));
	}

	private void assertDNBeansInList(List<DiscrepancyNoteBean> dns, List<Integer> ids) {
		for (Integer id : ids) {
			DiscrepancyNoteBean noteBean = noteBeanMap.get(id);
			assertNotNull(noteBean);
			assertTrue(dns.contains(noteBean));
		}
	}

}
