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

import com.clinovo.util.DateUtil;
import org.akaza.openclinica.domain.technicaladmin.LoginStatus;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AuditUserLoginFilter implements CriteriaCommand {

	private List<Filter> filters = new ArrayList<Filter>();

	private String userTimeZoneId;

	private Locale locale;

	public AuditUserLoginFilter(String userTimeZoneId, Locale locale) {
		this.userTimeZoneId = userTimeZoneId;
		this.locale = locale;
	}

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
				try {
					onlyYearAndMonthAndDayAndHourAndMinute(String.valueOf(value), criteria);
				} catch (IllegalArgumentException ex) {
					try {
						onlyYearAndMonthAndDayAndHour(String.valueOf(value), criteria);
					} catch (IllegalArgumentException ex2) {
						try {
							onlyYearAndMonthAndDay(String.valueOf(value), criteria);
						} catch (IllegalArgumentException ex3) {
							try {
								onlyYearAndMonth(String.valueOf(value), criteria);
							} catch (IllegalArgumentException ex4) {
								onlyYear(String.valueOf(value), criteria);
							}
						}
					}
				}
			} else {
				criteria.add(Restrictions.like(property, "%" + value + "%").ignoreCase());
			}
		}
	}

	private void onlyYear(String value, Criteria criteria) {

		Date startDate = DateUtil.parseDateStringToServerDateTime(value, userTimeZoneId, DateUtil.DatePattern.YEAR,
				locale);
		DateTime dt = new DateTime(startDate.getTime());
		dt = dt.plusYears(1);
		Date endDate = dt.toDate();
		criteria.add(Restrictions.between("loginAttemptDate", startDate, endDate));
	}

	private void onlyYearAndMonth(String value, Criteria criteria) {

		Date startDate = DateUtil.parseDateStringToServerDateTime(value, userTimeZoneId,
				DateUtil.DatePattern.YEAR_AND_MONTH, locale);
		DateTime dt = new DateTime(startDate.getTime());
		dt = dt.plusMonths(1);
		Date endDate = dt.toDate();
		criteria.add(Restrictions.between("loginAttemptDate", startDate, endDate));
	}

	private void onlyYearAndMonthAndDay(String value, Criteria criteria) {

		Date startDate = DateUtil.parseDateStringToServerDateTime(value, userTimeZoneId, DateUtil.DatePattern.DATE,
				locale);
		DateTime dt = new DateTime(startDate.getTime());
		dt = dt.plusDays(1);
		Date endDate = dt.toDate();
		criteria.add(Restrictions.between("loginAttemptDate", startDate, endDate));
	}

	private void onlyYearAndMonthAndDayAndHour(String value, Criteria criteria) {

		Date startDate = DateUtil.parseDateStringToServerDateTime(value, userTimeZoneId,
				DateUtil.DatePattern.DATE_AND_HOUR, locale);
		DateTime dt = new DateTime(startDate.getTime());
		dt = dt.plusHours(1);
		Date endDate = dt.toDate();
		criteria.add(Restrictions.between("loginAttemptDate", startDate, endDate));
	}

	private void onlyYearAndMonthAndDayAndHourAndMinute(String value, Criteria criteria) {

		Date startDate = DateUtil.parseDateStringToServerDateTime(value, userTimeZoneId, DateUtil.DatePattern.TIMESTAMP,
				locale);
		DateTime dt = new DateTime(startDate.getTime());
		dt = dt.plusMinutes(1);
		Date endDate = dt.toDate();
		criteria.add(Restrictions.between("loginAttemptDate", startDate, endDate));
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
