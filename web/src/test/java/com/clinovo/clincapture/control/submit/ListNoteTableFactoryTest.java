/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License 
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.clinovo.clincapture.control.submit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.akaza.openclinica.bean.core.DiscrepancyNoteType;
import org.akaza.openclinica.bean.core.ResolutionStatus;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteStatisticBean;
import org.akaza.openclinica.control.submit.ListNotesTableFactory;
import org.akaza.openclinica.dao.managestudy.ListNotesFilter;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.apache.commons.collections.ListUtils;
import org.jmesa.limit.FilterSet;
import org.jmesa.limit.FilterSetImpl;
import org.jmesa.limit.Limit;
import org.jmesa.limit.LimitImpl;
import org.jmesa.limit.SortSet;
import org.jmesa.limit.SortSetImpl;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

/**
 * User: Pavel Date: 16.10.12
 */
public class ListNoteTableFactoryTest {

	private Mockery context = new Mockery();

	private List<DiscrepancyNoteStatisticBean> statisticBeans;
	private Map<String, Map<String, String>> notesStatistic;
	private Map<String, String> notesStatusStatistics;

	private static final String TOTAL = "Total";

	private ListNotesTableFactory factory;
	Limit limit;
	Limit statusLimit;
	Limit typeLimit;

	String limitId = "listNotes";
	FilterSet filterSet;
	SortSet sortSet;

	private static final String FILTER_KEY_1 = "filter1";
	private static final String FILTER_KEY_2 = "filter2";
	private static final String FILTER_KEY_3 = "filter3";

	private static final String FILTER_VAL_1 = "value1";
	private static final String FILTER_VAL_2 = "value2";
	private static final String FILTER_VAL_3 = "value3";

	private ResourceBundle resterm;

	@Before
	public void setUp() throws Exception {

		ResourceBundleProvider.updateLocale(Locale.getDefault());
		resterm = ResourceBundleProvider.getTermsBundle();

		context.mock(HttpServletRequest.class);
		context.mock(HttpServletResponse.class);
		context.mock(HttpSession.class);

		factory = new ListNotesTableFactory(false);
		limit = new LimitImpl(limitId);
		statusLimit = new LimitImpl(limitId);
		typeLimit = new LimitImpl(limitId);

		filterSet = new FilterSetImpl();
		filterSet.addFilter(FILTER_KEY_1, FILTER_VAL_1);
		filterSet.addFilter(FILTER_KEY_2, FILTER_VAL_2);
		filterSet.addFilter(FILTER_KEY_3, FILTER_VAL_3);

		sortSet = new SortSetImpl();

		limit.setFilterSet(filterSet);
		limit.setSortSet(sortSet);

		ResourceBundleProvider.updateLocale(Locale.getDefault());

		statisticBeans = new ArrayList<DiscrepancyNoteStatisticBean>();
		statisticBeans.add(new DiscrepancyNoteStatisticBean(10, 1, 1));
		statisticBeans.add(new DiscrepancyNoteStatisticBean(10, 2, 2));
		statisticBeans.add(new DiscrepancyNoteStatisticBean(15, 3, 3));
		statisticBeans.add(new DiscrepancyNoteStatisticBean(13, 4, 4));
		statisticBeans.add(new DiscrepancyNoteStatisticBean(10, 1, 5));
		statisticBeans.add(new DiscrepancyNoteStatisticBean(10, 2, 1));
		statisticBeans.add(new DiscrepancyNoteStatisticBean(20, 3, 2));
		statisticBeans.add(new DiscrepancyNoteStatisticBean(10, 4, 3));
		statisticBeans.add(new DiscrepancyNoteStatisticBean(12, 1, 4));
		statisticBeans.add(new DiscrepancyNoteStatisticBean(10, 2, 5));

		notesStatistic = ListNotesTableFactory.getNotesStatistics(statisticBeans);
		notesStatusStatistics = ListNotesTableFactory.getNotesTypesStatistics(statisticBeans);

		new HashMap<Object, Object>();
	}

	@Test
	public void testGetFilterList() {
		ListNotesFilter notesFilter = factory.getListNoteFilter(limit);
		List<ListNotesFilter.Filter> filterList = notesFilter.getFilters();
		for (ListNotesFilter.Filter filter : filterList) {
			if (FILTER_KEY_1.equals(filter.getProperty())) {
				assertTrue(FILTER_VAL_1.equals(filter.getValue()));
			}
			if (FILTER_KEY_2.equals(filter.getProperty())) {
				assertTrue(FILTER_VAL_2.equals(filter.getValue()));
			}
			if (FILTER_KEY_3.equals(filter.getProperty())) {
				assertTrue(FILTER_VAL_3.equals(filter.getValue()));
			}
		}
	}

	@Test
	public void testGetFilterType() {
		FilterSet typeFilterSet = new FilterSetImpl();
		DiscrepancyNoteType noteType = DiscrepancyNoteType.QUERY;

		typeFilterSet.addFilter(ListNotesTableFactory.DISCREPANCY_NOTE_BEAN_DIS_TYPE, noteType.getName());

		typeLimit.setFilterSet(typeFilterSet);
		ListNotesFilter notesFilter = factory.getListNoteFilter(typeLimit);
		List<ListNotesFilter.Filter> filterList = notesFilter.getFilters();
		assertTrue(filterList.size() == 1);
		ListNotesFilter.Filter filter = filterList.get(0);
		assertEquals(filter.getProperty(), ListNotesTableFactory.DISCREPANCY_NOTE_BEAN_DIS_TYPE);
		assertEquals(filter.getValue(), Integer.toString(noteType.getId()));
	}

	@Test
	public void testGetFilterTypeQueryAndFailedValidationCheck() {
		FilterSet statusFilterSet = new FilterSetImpl();

		statusFilterSet.addFilter(ListNotesTableFactory.DISCREPANCY_NOTE_BEAN_DIS_TYPE,
				resterm.getString(ListNotesTableFactory.QUERY_AND_FAILED_VALIDATION_CHECK_KEY));

		typeLimit.setFilterSet(statusFilterSet);
		ListNotesFilter notesFilter = factory.getListNoteFilter(typeLimit);
		List<ListNotesFilter.Filter> filterList = notesFilter.getFilters();
		assertTrue(filterList.size() == 1);
		ListNotesFilter.Filter filter = filterList.get(0);
		assertEquals(filter.getProperty(), ListNotesTableFactory.DISCREPANCY_NOTE_BEAN_DIS_TYPE);
		assertEquals(filter.getValue(), ListNotesTableFactory.QUERY_AND_FAILED_VALIDATION_CHECK_VALUE);
	}

	@Test
	public void testGetFilterStatus() {
		FilterSet statusFilterSet = new FilterSetImpl();
		ResolutionStatus resolutionStatus = ResolutionStatus.UPDATED;

		statusFilterSet.addFilter(ListNotesTableFactory.DISCREPANCY_NOTE_BEAN_RESOLUTION_STATUS,
				resolutionStatus.getName());

		statusLimit.setFilterSet(statusFilterSet);
		ListNotesFilter notesFilter = factory.getListNoteFilter(statusLimit);
		List<ListNotesFilter.Filter> filterList = notesFilter.getFilters();

		assertTrue(filterList.size() == 1);
		ListNotesFilter.Filter filter = filterList.get(0);
		assertEquals(filter.getProperty(), ListNotesTableFactory.DISCREPANCY_NOTE_BEAN_RESOLUTION_STATUS);
		assertEquals(filter.getValue(), Integer.toString(resolutionStatus.getId()));
	}

	@Test
	public void testGetFilterStatusNewAndUpdated() {
		FilterSet statusFilterSet = new FilterSetImpl();

		statusFilterSet.addFilter(ListNotesTableFactory.DISCREPANCY_NOTE_BEAN_RESOLUTION_STATUS,
				resterm.getString(ListNotesTableFactory.NEW_AND_UPDATED_KEY));

		statusLimit.setFilterSet(statusFilterSet);
		ListNotesFilter notesFilter = factory.getListNoteFilter(statusLimit);
		List<ListNotesFilter.Filter> filterList = notesFilter.getFilters();

		assertTrue(filterList.size() == 1);
		ListNotesFilter.Filter filter = filterList.get(0);
		assertEquals(filter.getProperty(), ListNotesTableFactory.DISCREPANCY_NOTE_BEAN_RESOLUTION_STATUS);
		assertEquals(filter.getValue(), ListNotesTableFactory.NEW_AND_UPDATED_VALUE);
	}

	@Test
	public void testNotesStatisticsOpen() {
		Map<String, String> openNotes = notesStatistic.get(ResolutionStatus.OPEN.getName());
		assertEquals("10", openNotes.get(DiscrepancyNoteType.FAILEDVAL.getName()));
		assertEquals("10", openNotes.get(DiscrepancyNoteType.ANNOTATION.getName()));
		assertNull(openNotes.get(DiscrepancyNoteType.REASON_FOR_CHANGE.getName()));
		assertEquals("20", openNotes.get(TOTAL));
	}

	@Test
	public void testNotesStatisticsUpdated() {
		Map<String, String> updatedNotes = notesStatistic.get(ResolutionStatus.UPDATED.getName());
		assertNull(updatedNotes.get(DiscrepancyNoteType.FAILEDVAL.getName()));
		assertEquals("10", updatedNotes.get(DiscrepancyNoteType.ANNOTATION.getName()));
		assertEquals("20", updatedNotes.get(DiscrepancyNoteType.QUERY.getName()));
		assertEquals("30", updatedNotes.get(TOTAL));
	}

	@Test
	public void testNotesStatisticsResolved() {
		Map<String, String> resolvedNotes = notesStatistic.get(ResolutionStatus.RESOLVED.getName());
		assertEquals("15", resolvedNotes.get(DiscrepancyNoteType.QUERY.getName()));
		assertEquals("10", resolvedNotes.get(DiscrepancyNoteType.REASON_FOR_CHANGE.getName()));
		assertEquals("25", resolvedNotes.get(TOTAL));
	}

	@Test
	public void testNotesStatisticsClosed() {
		Map<String, String> closedNotes = notesStatistic.get(ResolutionStatus.CLOSED.getName());
		assertEquals("13", closedNotes.get(DiscrepancyNoteType.REASON_FOR_CHANGE.getName()));
		assertEquals("12", closedNotes.get(DiscrepancyNoteType.FAILEDVAL.getName()));
		assertEquals("25", closedNotes.get(TOTAL));
	}

	@Test
	public void testNotesStatisticsNotApplicable() {
		Map<String, String> notApplicableNotes = notesStatistic.get(ResolutionStatus.NOT_APPLICABLE.getName());
		assertEquals("10", notApplicableNotes.get(DiscrepancyNoteType.FAILEDVAL.getName()));
		assertEquals("10", notApplicableNotes.get(DiscrepancyNoteType.ANNOTATION.getName()));
		assertEquals("20", notApplicableNotes.get(TOTAL));
	}

	@Test
	public void testNotesTypesStatistics() {
		assertEquals("32", notesStatusStatistics.get(DiscrepancyNoteType.FAILEDVAL.getName()));
		assertEquals("30", notesStatusStatistics.get(DiscrepancyNoteType.ANNOTATION.getName()));
		assertEquals("35", notesStatusStatistics.get(DiscrepancyNoteType.QUERY.getName()));
		assertEquals("23", notesStatusStatistics.get(DiscrepancyNoteType.REASON_FOR_CHANGE.getName()));
		assertEquals("120", notesStatusStatistics.get(TOTAL));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testEmpty() {
		Map<String, Map<String, String>> emptyNotesStatistic = ListNotesTableFactory
				.getNotesStatistics(ListUtils.EMPTY_LIST);
		assertNotNull(emptyNotesStatistic.get(ResolutionStatus.OPEN.getName()));
		assertNotNull(emptyNotesStatistic.get(ResolutionStatus.NOT_APPLICABLE.getName()));
		Map<String, String> emptyOpenNotes = emptyNotesStatistic.get(ResolutionStatus.OPEN.getName());
		assertNull(emptyOpenNotes.get(DiscrepancyNoteType.FAILEDVAL.getName()));
		assertNull(emptyOpenNotes.get(DiscrepancyNoteType.ANNOTATION.getName()));
	}
}
