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

package org.akaza.openclinica.dao.submit;

import com.clinovo.util.DateUtil;
import org.akaza.openclinica.dao.managestudy.CriteriaCommand;
import org.apache.commons.lang.StringEscapeUtils;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ListSubjectFilter implements CriteriaCommand {

	private List<Filter> filters = new ArrayList<Filter>();
	private HashMap<String, String> columnMapping = new HashMap<String, String>();
	private Locale locale;
	private String userTimeZoneId;

	public ListSubjectFilter(String userTimeZoneId, Locale locale) {

		this.userTimeZoneId = userTimeZoneId;
		this.locale = locale;
		columnMapping.put("subject.uniqueIdentifier", "s.unique_identifier");
		columnMapping.put("subject.gender", "s.gender");
		columnMapping.put("subject.createdDate", "s.date_created");
		columnMapping.put("subject.owner", "ua.user_name");
		columnMapping.put("subject.updatedDate", "s.date_updated");
		columnMapping.put("subject.updater", "ua.user_name");
		columnMapping.put("subject.status", "s.status_id");
		columnMapping.put("studySubjectIdAndStudy", "");
	}

	public void addFilter(String property, Object value) {
		filters.add(new Filter(property, value));
	}

	public String execute(String criteria) {
		String theCriteria = "";
		for (Filter filter : filters) {
			theCriteria += buildCriteria(criteria, filter.getProperty(), filter.getValue());
		}

		return theCriteria;
	}

	private String buildCriteria(String criteria, String property, Object value) {
		value = StringEscapeUtils.escapeSql(value.toString());
		if (value != null) {
			if (property.equals("subject.status")) {
				criteria = criteria + " and ";
				criteria = criteria + " " + columnMapping.get(property) + " = " + value.toString() + " ";
			} else if (property.equals("subject.createdDate") || property.equals("subject.updatedDate")) {
				try {
					criteria += onlyYearAndMonthAndDay(String.valueOf(value), columnMapping.get(property));
				} catch (IllegalArgumentException ex) {
					criteria += onlyYear(String.valueOf(value), columnMapping.get(property));
				}
			} else if (property.equals("subject.owner")) {
				criteria = criteria + " and s.owner_id = ua.user_id and ";
				criteria = criteria + " UPPER(" + columnMapping.get(property) + ") like UPPER('%" + value.toString()
						+ "%')" + " ";
			} else if (property.equals("subject.updater")) {
				criteria = criteria + " and s.update_id = ua.user_id and ";
				criteria = criteria + " UPPER(" + columnMapping.get(property) + ") like UPPER('%" + value.toString()
						+ "%')" + " ";
			} else if (property.equals("studySubjectIdAndStudy")) {
				criteria = criteria + " and (";
				String val = value.toString();
				for (int i = 1; i < val.split("-", -1).length; i++) {
					StringBuilder strB = new StringBuilder();
					for (int j = 0; j < i; j++) {
						strB.append("-");
						strB.append(val.split("-", -1)[j]);
					}
					String str1 = strB.toString().replaceFirst("-", "");
					String str2 = val.replaceFirst(str1 + "-", "");
					
					criteria = criteria + " ( UPPER(study.unique_identifier) like UPPER('%" + str1 + "')";
					criteria = criteria + " and ";
					criteria = criteria + "  UPPER(ss.label) like UPPER('" + str2 + "%')" + " ) ";
					criteria = criteria + " or ";
				}
				criteria = criteria + " ( UPPER(study.unique_identifier) like UPPER('%" + val + "%')";
				criteria = criteria + " or ";
				criteria = criteria + "  UPPER(ss.label) like UPPER('%" + val + "%')" + " ) ";

				criteria = criteria + " )";
			} else {
				criteria = criteria + " and ";
				criteria = criteria + " UPPER(" + columnMapping.get(property) + ") like UPPER('%" + value.toString()
						+ "%')" + " ";
			}
		}
		return criteria;
	}

	private String onlyYear(String value, String column) {

		Date startDate = DateUtil.parseDateStringToServerDateTime(value, userTimeZoneId, DateUtil.DatePattern.YEAR,
				locale);
		DateTime dt = new DateTime(startDate.getTime());
		dt = dt.plusYears(1);
		Date endDate = dt.toDate();
		return (" AND ( " + column + " between '" + DateUtil.printDate(startDate, DateUtil.DatePattern.ISO_DATE,
				locale) + "' and '" + DateUtil.printDate(endDate, DateUtil.DatePattern.ISO_DATE, locale) + "')");
	}

	private String onlyYearAndMonthAndDay(String value, String column) {

		Date startDate = DateUtil.parseDateStringToServerDateTime(value, userTimeZoneId, DateUtil.DatePattern.DATE,
				locale);
		DateTime dt = new DateTime(startDate.getTime());
		dt = dt.plusDays(1);
		Date endDate = dt.toDate();
		return (" AND (  " + column + " between '" + DateUtil.printDate(startDate, DateUtil.DatePattern.ISO_DATE,
				locale) + "' and '" + DateUtil.printDate(endDate, DateUtil.DatePattern.ISO_DATE, locale) + "')");
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
