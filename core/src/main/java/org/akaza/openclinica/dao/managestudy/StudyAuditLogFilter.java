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

package org.akaza.openclinica.dao.managestudy;

import org.akaza.openclinica.dao.core.CoreResources;
import org.apache.commons.lang.StringEscapeUtils;
import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class StudyAuditLogFilter implements CriteriaCommand {

	List<Filter> filters = new ArrayList<Filter>();
	HashMap<String, String> columnMapping = new HashMap<String, String>();
	Integer studyEventDefinitionId;
	String defaultFormat = "yyyy-MM-dd";
	String oracleDateFormat = "dd-MMM-yyyy";
	DateFormat theDefaultFormat;
	String i18Format;

	public StudyAuditLogFilter(String dateFormat) {

		theDefaultFormat = new SimpleDateFormat(defaultFormat);
		if ("oracle".equalsIgnoreCase(CoreResources.getDBName())) {
			theDefaultFormat = new SimpleDateFormat(oracleDateFormat);
		}
		i18Format = dateFormat;

		columnMapping.put("studySubject.label", "ss.label");
		columnMapping.put("studySubject.status", "ss.status_id");
		columnMapping.put("studySubject.oid", "ss.oc_oid");
		columnMapping.put("studySubject.secondaryLabel", "ss.secondary_label");
		columnMapping.put("subject.dateOfBirth", "s.date_of_birth");
		columnMapping.put("subject.uniqueIdentifier", "s.unique_identifier");
		columnMapping.put("studySubject.owner", "ua.user_name");
		columnMapping.put("studySubject.status", "ss.status_id");
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
			if (property.equals("studySubject.status")) {
				criteria = criteria + " and ";
				criteria = criteria + " " + columnMapping.get(property) + " = " + value.toString() + " ";
			} else if (property.equals("subject.dateOfBirth")) {
				criteria += onlyYearAndMonthAndDay(String.valueOf(value));
				criteria += onlyYear(String.valueOf(value));
			}

			else {
				criteria = criteria + " and ";
				criteria = criteria + " UPPER(" + columnMapping.get(property) + ") like UPPER('%" + value.toString()
						+ "%')" + " ";
			}
		}
		return criteria;
	}

	private String onlyYear(String value) {
		String criteria = "";
		try {
			DateFormat format = new SimpleDateFormat("yyyy");
			Date startDate = format.parse(value);
			DateTime dt = new DateTime(startDate.getTime());
			dt = dt.plusYears(1);
			Date endDate = dt.toDate();
			if (format.format(startDate).equals(value)) {
				criteria = "AND ( s.date_of_birth between '" + theDefaultFormat.format(startDate) + "' and '"
						+ theDefaultFormat.format(endDate) + "')";
			}

		} catch (Exception e) {
			// Do nothing
		}
		return criteria;
	}

	private String onlyYearAndMonthAndDay(String value) {
		String criteria = "";
		try {
			DateFormat format = new SimpleDateFormat(i18Format);
			Date startDate = format.parse(value);
			DateTime dt = new DateTime(startDate.getTime());
			dt = dt.plusDays(1);
			Date endDate = dt.toDate();
			if (format.format(startDate).equals(value)) {
				criteria = "AND ( s.date_of_birth between '" + theDefaultFormat.format(startDate) + "' and '"
						+ theDefaultFormat.format(endDate) + "')";
			}
		} catch (Exception e) {
			// Do nothing
		}
		return criteria;
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
