/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2015 Clinovo Inc.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License along with this program.
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

package org.akaza.openclinica.dao.managestudy;

import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

public class ListNotesFilterTest {

	@Test
	public void testThatGetAdditionalFilterReturnsRightSqlFilterStringForPropertyEntityName_1() {

		// SETUP
		ResourceBundleProvider.updateLocale(Locale.ENGLISH);
		ListNotesFilter listNotesFilter = new ListNotesFilter();
		List<ListNotesFilter.Filter> testFiltersList = new ArrayList<ListNotesFilter.Filter>();
		testFiltersList.add(new ListNotesFilter.Filter("entityName", ResourceBundleProvider.getResWord("unique_identifier")));
		Whitebox.setInternalState(listNotesFilter, "filters", testFiltersList);
		String expectedSqlFilter = " where 1=1  and dns.entity_name = 'unique_identifier' ";

		// TEST
		String sqlFilter = listNotesFilter.getAdditionalFilter();

		// VERIFY
		Assert.assertEquals(expectedSqlFilter, sqlFilter);
	}

	@Test
	public void testThatGetAdditionalFilterReturnsRightSqlFilterStringForPropertyEntityName_2() {

		// SETUP
		ResourceBundleProvider.updateLocale(Locale.ENGLISH);
		ListNotesFilter listNotesFilter = new ListNotesFilter();
		List<ListNotesFilter.Filter> testFiltersList = new ArrayList<ListNotesFilter.Filter>();
		testFiltersList.add(new ListNotesFilter.Filter("entityName", ResourceBundleProvider.getResWord("gender")));
		Whitebox.setInternalState(listNotesFilter, "filters", testFiltersList);
		String expectedSqlFilter = " where 1=1  and dns.entity_name = 'gender' ";

		// TEST
		String sqlFilter = listNotesFilter.getAdditionalFilter();

		// VERIFY
		Assert.assertEquals(expectedSqlFilter, sqlFilter);
	}

	@Test
	public void testThatGetAdditionalFilterReturnsRightSqlFilterStringForPropertyEntityName_3() {

		// SETUP
		ResourceBundleProvider.updateLocale(Locale.ENGLISH);
		ListNotesFilter listNotesFilter = new ListNotesFilter();
		List<ListNotesFilter.Filter> testFiltersList = new ArrayList<ListNotesFilter.Filter>();
		testFiltersList.add(new ListNotesFilter.Filter("entityName", ResourceBundleProvider.getResWord("date_of_birth")));
		Whitebox.setInternalState(listNotesFilter, "filters", testFiltersList);
		String expectedSqlFilter = " where 1=1  and dns.entity_name = 'date_of_birth' ";

		// TEST
		String sqlFilter = listNotesFilter.getAdditionalFilter();

		// VERIFY
		Assert.assertEquals(expectedSqlFilter, sqlFilter);
	}

	@Test
	public void testThatGetAdditionalFilterReturnsRightSqlFilterStringForPropertyEntityName_4() {

		// SETUP
		ResourceBundleProvider.updateLocale(Locale.ENGLISH);
		ListNotesFilter listNotesFilter = new ListNotesFilter();
		List<ListNotesFilter.Filter> testFiltersList = new ArrayList<ListNotesFilter.Filter>();
		testFiltersList.add(new ListNotesFilter.Filter("entityName", ResourceBundleProvider.getResWord("enrollment_date")));
		Whitebox.setInternalState(listNotesFilter, "filters", testFiltersList);
		String expectedSqlFilter = " where 1=1  and dns.entity_name = 'enrollment_date' ";

		// TEST
		String sqlFilter = listNotesFilter.getAdditionalFilter();

		// VERIFY
		Assert.assertEquals(expectedSqlFilter, sqlFilter);
	}

	@Test
	public void testThatGetAdditionalFilterReturnsRightSqlFilterStringForPropertyEntityName_5() {

		// SETUP
		ResourceBundleProvider.updateLocale(Locale.ENGLISH);
		ListNotesFilter listNotesFilter = new ListNotesFilter();
		List<ListNotesFilter.Filter> testFiltersList = new ArrayList<ListNotesFilter.Filter>();
		testFiltersList.add(new ListNotesFilter.Filter("entityName", ResourceBundleProvider.getResWord("location")));
		Whitebox.setInternalState(listNotesFilter, "filters", testFiltersList);
		String expectedSqlFilter = " where 1=1  and dns.entity_name = 'location' ";

		// TEST
		String sqlFilter = listNotesFilter.getAdditionalFilter();

		// VERIFY
		Assert.assertEquals(expectedSqlFilter, sqlFilter);
	}

	@Test
	public void testThatGetAdditionalFilterReturnsRightSqlFilterStringForPropertyEntityName_6() {

		// SETUP
		ResourceBundleProvider.updateLocale(Locale.ENGLISH);
		ListNotesFilter listNotesFilter = new ListNotesFilter();
		List<ListNotesFilter.Filter> testFiltersList = new ArrayList<ListNotesFilter.Filter>();
		testFiltersList.add(new ListNotesFilter.Filter("entityName", ResourceBundleProvider.getResWord("start_date")));
		Whitebox.setInternalState(listNotesFilter, "filters", testFiltersList);
		String expectedSqlFilter = " where 1=1  and dns.entity_name = 'date_start' ";

		// TEST
		String sqlFilter = listNotesFilter.getAdditionalFilter();

		// VERIFY
		Assert.assertEquals(expectedSqlFilter, sqlFilter);
	}

	@Test
	public void testThatGetAdditionalFilterReturnsRightSqlFilterStringForPropertyEntityName_7() {

		// SETUP
		ResourceBundleProvider.updateLocale(Locale.ENGLISH);
		ListNotesFilter listNotesFilter = new ListNotesFilter();
		List<ListNotesFilter.Filter> testFiltersList = new ArrayList<ListNotesFilter.Filter>();
		testFiltersList.add(new ListNotesFilter.Filter("entityName", ResourceBundleProvider.getResWord("end_date")));
		Whitebox.setInternalState(listNotesFilter, "filters", testFiltersList);
		String expectedSqlFilter = " where 1=1  and dns.entity_name = 'date_end' ";

		// TEST
		String sqlFilter = listNotesFilter.getAdditionalFilter();

		// VERIFY
		Assert.assertEquals(expectedSqlFilter, sqlFilter);
	}

	@Test
	public void testThatGetAdditionalFilterReturnsRightSqlFilterStringForPropertyEntityName_8() {

		// SETUP
		ResourceBundleProvider.updateLocale(Locale.ENGLISH);
		ListNotesFilter listNotesFilter = new ListNotesFilter();
		List<ListNotesFilter.Filter> testFiltersList = new ArrayList<ListNotesFilter.Filter>();
		testFiltersList.add(new ListNotesFilter.Filter("entityName", ResourceBundleProvider.getResWord("interviewer_name")));
		Whitebox.setInternalState(listNotesFilter, "filters", testFiltersList);
		String expectedSqlFilter = " where 1=1  and dns.entity_name = 'interviewer_name' ";

		// TEST
		String sqlFilter = listNotesFilter.getAdditionalFilter();

		// VERIFY
		Assert.assertEquals(expectedSqlFilter, sqlFilter);
	}

	@Test
	public void testThatGetAdditionalFilterReturnsRightSqlFilterStringForPropertyEntityName_9() {

		// SETUP
		ResourceBundleProvider.updateLocale(Locale.ENGLISH);
		ListNotesFilter listNotesFilter = new ListNotesFilter();
		List<ListNotesFilter.Filter> testFiltersList = new ArrayList<ListNotesFilter.Filter>();
		testFiltersList.add(new ListNotesFilter.Filter("entityName", ResourceBundleProvider.getResWord("date_interviewed")));
		Whitebox.setInternalState(listNotesFilter, "filters", testFiltersList);
		String expectedSqlFilter = " where 1=1  and dns.entity_name = 'date_interviewed' ";

		// TEST
		String sqlFilter = listNotesFilter.getAdditionalFilter();

		// VERIFY
		Assert.assertEquals(expectedSqlFilter, sqlFilter);
	}

	@Test
	public void testThatGetAdditionalFilterReturnsRightSqlFilterStringForPropertyEntityName_10() {

		// SETUP
		ResourceBundleProvider.updateLocale(Locale.ENGLISH);
		ListNotesFilter listNotesFilter = new ListNotesFilter();
		List<ListNotesFilter.Filter> testFiltersList = new ArrayList<ListNotesFilter.Filter>();
		testFiltersList.add(new ListNotesFilter.Filter("entityName", ResourceBundleProvider.getResWord("AEOUT")));
		Whitebox.setInternalState(listNotesFilter, "filters", testFiltersList);
		String expectedSqlFilter = " where 1=1  and dns.entity_name like '%AEOUT%' ";

		// TEST
		String sqlFilter = listNotesFilter.getAdditionalFilter();

		// VERIFY
		Assert.assertEquals(expectedSqlFilter, sqlFilter);
	}

	@Test
	public void testThatGetAdditionalFilterReturnsRightSqlFilterStringForPropertyEntityName_11() {

		// SETUP
		ResourceBundleProvider.updateLocale(Locale.ENGLISH);
		ListNotesFilter listNotesFilter = new ListNotesFilter();
		List<ListNotesFilter.Filter> testFiltersList = new ArrayList<ListNotesFilter.Filter>();
		testFiltersList.add(new ListNotesFilter.Filter("entityName", ResourceBundleProvider.getResWord("AEOUT(#1)")));
		Whitebox.setInternalState(listNotesFilter, "filters", testFiltersList);
		String expectedSqlFilter = " where 1=1  and dns.entity_name like '%AEOUT%'  and dns.item_data_ordinal = 1 ";

		// TEST
		String sqlFilter = listNotesFilter.getAdditionalFilter();

		// VERIFY
		Assert.assertEquals(expectedSqlFilter, sqlFilter);
	}

	@Test
	public void testThatGetAdditionalFilterReturnsRightSqlFilterStringForPropertyEntityName_12() {

		// SETUP
		ResourceBundleProvider.updateLocale(Locale.ENGLISH);
		ListNotesFilter listNotesFilter = new ListNotesFilter();
		List<ListNotesFilter.Filter> testFiltersList = new ArrayList<ListNotesFilter.Filter>();
		testFiltersList.add(new ListNotesFilter.Filter("crfName", ResourceBundleProvider.getResWord("Adverse Event")));
		Whitebox.setInternalState(listNotesFilter, "filters", testFiltersList);
		String expectedSqlFilter = " where 1=1  and dns.crf_name like '%Adverse Event%' ";

		// TEST
		String sqlFilter = listNotesFilter.getAdditionalFilter();

		// VERIFY
		Assert.assertEquals(expectedSqlFilter, sqlFilter);
	}

	@Test
	public void testThatGetAdditionalFilterReturnsRightSqlFilterStringForPropertyEntityName_13() {

		// SETUP
		ResourceBundleProvider.updateLocale(Locale.ENGLISH);
		ListNotesFilter listNotesFilter = new ListNotesFilter();
		List<ListNotesFilter.Filter> testFiltersList = new ArrayList<ListNotesFilter.Filter>();
		testFiltersList.add(new ListNotesFilter.Filter("eventName", ResourceBundleProvider.getResWord("Screening")));
		Whitebox.setInternalState(listNotesFilter, "filters", testFiltersList);
		String expectedSqlFilter = " where 1=1  and dns.event_name like '%Screening%' ";

		// TEST
		String sqlFilter = listNotesFilter.getAdditionalFilter();

		// VERIFY
		Assert.assertEquals(expectedSqlFilter, sqlFilter);
	}

	@Test
	public void testThatGetAdditionalFilterReturnsRightSqlFilterStringForPropertyEntityName_14() {

		// SETUP
		ResourceBundleProvider.updateLocale(Locale.ENGLISH);
		ListNotesFilter listNotesFilter = new ListNotesFilter();
		List<ListNotesFilter.Filter> testFiltersList = new ArrayList<ListNotesFilter.Filter>();
		testFiltersList.add(new ListNotesFilter.Filter("entityValue", ResourceBundleProvider.getResWord("23")));
		Whitebox.setInternalState(listNotesFilter, "filters", testFiltersList);
		String expectedSqlFilter = " where 1=1  and dns.item_value like '%23%' ";

		// TEST
		String sqlFilter = listNotesFilter.getAdditionalFilter();

		// VERIFY
		Assert.assertEquals(expectedSqlFilter, sqlFilter);
	}

	@Test
	public void testThatGetAdditionalFilterReturnsRightSqlFilterStringForPropertyEntityName_15() {

		// SETUP
		ResourceBundleProvider.updateLocale(Locale.ENGLISH);
		ListNotesFilter listNotesFilter = new ListNotesFilter();
		List<ListNotesFilter.Filter> testFiltersList = new ArrayList<ListNotesFilter.Filter>();
		testFiltersList.add(new ListNotesFilter.Filter("eventName", ResourceBundleProvider.getResWord("Screening")));
		testFiltersList.add(new ListNotesFilter.Filter("crfName", ResourceBundleProvider.getResWord("Demographics")));
		testFiltersList.add(new ListNotesFilter.Filter("entityName", ResourceBundleProvider.getResWord("date_interviewed")));
		Whitebox.setInternalState(listNotesFilter, "filters", testFiltersList);
		String expectedSqlFilter =
				" where 1=1  and dns.event_name like '%Screening%'  and dns.crf_name like '%Demographics%'  and dns.entity_name = 'date_interviewed' ";

		// TEST
		String sqlFilter = listNotesFilter.getAdditionalFilter();

		// VERIFY
		Assert.assertEquals(expectedSqlFilter, sqlFilter);
	}
}
