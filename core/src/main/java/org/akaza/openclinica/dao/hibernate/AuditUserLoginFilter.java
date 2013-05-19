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
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

package org.akaza.openclinica.dao.hibernate;

import org.akaza.openclinica.domain.technicaladmin.LoginStatus;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AuditUserLoginFilter implements CriteriaCommand {

	List<Filter> filters = new ArrayList<Filter>();

	public void addFilter(String property, Object value) {
		filters.add(new Filter(property, value));
	}

	public Criteria execute(Criteria criteria) {
		for (Filter filter : filters) {
			buildCriteria(criteria, filter.getProperty(), filter.getValue());
		}

		return criteria;
	}

	private void buildCriteria(Criteria criteria, String property, Object value) {
		if (value != null) {
			if (property.equals("loginStatus")) {
				criteria.add(Restrictions.eq(property, LoginStatus.getByName((String) value)));
			} else if (property.equals("loginAttemptDate")) {
				onlyYearAndMonthAndDayAndHourAndMinute(String.valueOf(value), criteria);
				onlyYearAndMonthAndDayAndHour(String.valueOf(value), criteria);
				onlyYearAndMonthAndDay(String.valueOf(value), criteria);
				onlyYearAndMonth(String.valueOf(value), criteria);
				onlyYear(String.valueOf(value), criteria);
			} else
				criteria.add(Restrictions.like(property, "%" + value + "%").ignoreCase());
		}
	}

	private void onlyYear(String value, Criteria criteria) {
		try {
			DateFormat format = new SimpleDateFormat("yyyy");
			Date startDate = format.parse(value);
			DateTime dt = new DateTime(startDate.getTime());
			dt = dt.plusYears(1);
			Date endDate = dt.toDate();
			criteria.add(Restrictions.between("loginAttemptDate", startDate, endDate));
		} catch (Exception e) {
			// Do nothing
		}
	}

	private void onlyYearAndMonth(String value, Criteria criteria) {
		try {
			DateFormat format = new SimpleDateFormat("yyyy-MM");
			Date startDate = format.parse(value);
			DateTime dt = new DateTime(startDate.getTime());
			dt = dt.plusMonths(1);
			Date endDate = dt.toDate();
			criteria.add(Restrictions.between("loginAttemptDate", startDate, endDate));
		} catch (Exception e) {
			// Do nothing
		}
	}

	private void onlyYearAndMonthAndDay(String value, Criteria criteria) {
		try {
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date startDate = format.parse(value);
			DateTime dt = new DateTime(startDate.getTime());
			dt = dt.plusDays(1);
			Date endDate = dt.toDate();
			criteria.add(Restrictions.between("loginAttemptDate", startDate, endDate));
		} catch (Exception e) {
			// Do nothing
		}
	}

	private void onlyYearAndMonthAndDayAndHour(String value, Criteria criteria) {
		try {
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH");
			Date startDate = format.parse(value);
			DateTime dt = new DateTime(startDate.getTime());
			dt = dt.plusHours(1);
			Date endDate = dt.toDate();
			criteria.add(Restrictions.between("loginAttemptDate", startDate, endDate));
		} catch (Exception e) {
			// Do nothing
		}
	}

	private void onlyYearAndMonthAndDayAndHourAndMinute(String value, Criteria criteria) {
		try {
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date startDate = format.parse(value);
			DateTime dt = new DateTime(startDate.getTime());
			dt = dt.plusMinutes(1);
			Date endDate = dt.toDate();
			criteria.add(Restrictions.between("loginAttemptDate", startDate, endDate));
		} catch (Exception e) {
			// Do nothing
		}
	}

	private static class Filter {
		private final String property;
		private final Object value;

		public Filter(String property, Object value) {
			this.property = property;
			this.value = value;
		}

		public String getProperty() {
			return property;
		}

		public Object getValue() {
			return value;
		}
	}

}
