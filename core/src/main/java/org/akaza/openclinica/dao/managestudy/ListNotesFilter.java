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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;

public class ListNotesFilter implements CriteriaCommand {

	List<Filter> filters = new ArrayList<Filter>();
	HashMap<String, String> columnMapping = new HashMap<String, String>();

	HashMap<String, String> additionalColumnMapping = new HashMap<String, String>();

	private boolean dateCreatedCorrect = true;
	private boolean dateUpdatedCorrect = true;

	public ListNotesFilter() {
		columnMapping.put("studySubject.label", "ss.label");
		columnMapping.put("siteId", "ss.label");
		columnMapping.put("studySubject.labelExact", "ss.label");
		columnMapping.put("discrepancyNoteBean.createdDate", "dn.date_created");
		columnMapping.put("discrepancyNoteBean.updatedDate", "dn.date_created");
		columnMapping.put("discrepancyNoteBean.description", "dn.description");
		columnMapping.put("discrepancyNoteBean.user", "ua.user_name");
		columnMapping.put("discrepancyNoteBean.disType", "dn.discrepancy_note_type_id");
		columnMapping.put("discrepancyNoteBean.entityType", "dn.entity_type");
		columnMapping.put("discrepancyNoteBean.resolutionStatus", "dn.resolution_status_id");
		columnMapping.put("age", "age");
		columnMapping.put("days", "days");

		additionalColumnMapping.put("crfName", "dns.crf_name");
		additionalColumnMapping.put("eventName", "dns.event_name");
		additionalColumnMapping.put("entityName", "dns.item_name");
		additionalColumnMapping.put("entityValue", "dns.item_value");
	}

	public void addFilter(String property, Object value) {
		filters.add(new Filter(property, value));
	}

	public String execute(String criteria) {
		String theCriteria = "";
		for (Filter filter : filters) {
			if (columnMapping.get(filter.getProperty()) == null) {
				continue;
			}
			theCriteria += buildCriteria(criteria, filter.getProperty(), filter.getValue());
		}
		return theCriteria;
	}

	private String buildCriteria(String criteria, String property, Object value) {

		String pgDateFormat = "yyyy-MM-dd";
		SimpleDateFormat dateFormat = new SimpleDateFormat(pgDateFormat, ResourceBundleProvider.getLocale());

		if (value != null) {
			if (property.equals("studySubject.labelExact")) {
				criteria += " and  UPPER(" + columnMapping.get(property) + ") = UPPER('" + value.toString() + "')"
						+ " ";
			} else if (property.equals("studySubject.label") || property.equals("discrepancyNoteBean.description")
					|| property.equals("discrepancyNoteBean.user")) {
				criteria += " and UPPER(" + columnMapping.get(property) + ") like UPPER('%" + value.toString() + "%')"
						+ " ";
			} else if (property.equals("siteId")) {
				criteria += " and ss.study_id in ( SELECT study_id FROM study WHERE unique_identifier like '%"
						+ value.toString() + "%')";
			} else if (property.equals("age")) {
				if (value.toString().startsWith(">") || value.toString().startsWith("<")
						|| value.toString().startsWith("=")) {
					criteria += " and age " + value.toString();
				} else {
					try {
						Integer.parseInt(value.toString().trim());
						criteria += " and age=" + value.toString();
					} catch (NumberFormatException nFE) {
						nFE.printStackTrace();
					}
				}
			} else if (property.equals("days")) {
				if (value.toString().startsWith(">") || value.toString().startsWith("<")
						|| value.toString().startsWith("=")) {
					criteria += " and days " + value.toString();
				} else {
					try {
						Integer.parseInt(value.toString());
						criteria += " and days=" + value.toString().trim();
					} catch (NumberFormatException nFE) {
						nFE.printStackTrace();
					}
				}
			} else if ("discrepancyNoteBean.disType".equalsIgnoreCase(property)) {
				if ("31".equals(value.toString())) {
					criteria += " and (dn.discrepancy_note_type_id = 1 or dn.discrepancy_note_type_id = 3)";
				} else {
					criteria += " and " + columnMapping.get(property) + " = '" + value.toString() + "' ";
				}
			} else if ("discrepancyNoteBean.resolutionStatus".equalsIgnoreCase(property)) {
				if ("21".equals(value.toString())) {
					criteria += " and (dn.resolution_status_id = 1 or dn.resolution_status_id = 2) ";
				} else if ("321".equals(value.toString())) {
					criteria += " and (dn.resolution_status_id = 1 or dn.resolution_status_id = 2 or dn.resolution_status_id = 3) ";
				} else {
					criteria += " and " + columnMapping.get(property) + " = '" + value.toString() + "' ";
				}
			} else if ("discrepancyNoteBean.createdDate".equalsIgnoreCase(property)) {
				Date date = parseDate((String) value);
				if (date != null) {
					String pgDate = dateFormat.format(date);
					if ("oracle".equalsIgnoreCase(CoreResources.getDBName())) {
						criteria += " and dn.date_created=TO_DATE('" + pgDate + "', '" + pgDateFormat + "') ";
					} else {
						criteria += " and dn.date_created='" + dateFormat.format(date) + "' ";
					}
				} else {
					dateCreatedCorrect = false;
				}
			} else if ("discrepancyNoteBean.updatedDate".equalsIgnoreCase(property)) {
				Date date = parseDate((String) value);
				if (date != null) {
					String pgDate = dateFormat.format(date);
					if ("oracle".equalsIgnoreCase(CoreResources.getDBName())) {
						criteria += " and (" + "select max(dc.date_created) " + "from (( select date_created "
								+ "from discrepancy_note where parent_dn_id = dn.discrepancy_note_id) "
								+ "union (select dn.date_created)) dc)=TO_DATE('" + pgDate + "', '" + pgDateFormat
								+ "') ";
					} else {
						criteria += " and (" + "select max(dc.date_created) " + "from (( select date_created "
								+ "from discrepancy_note where parent_dn_id = dn.discrepancy_note_id) "
								+ "union (select dn.date_created)) dc)='" + pgDate + "' ";
					}
				} else {
					dateUpdatedCorrect = false;
				}

			} else {
				criteria += " and " + columnMapping.get(property) + " = '" + value.toString() + "' ";
			}
		}
		return criteria;
	}

	public static class Filter {
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

	public List<Filter> getFilters() {
		return filters;
	}

	private Date parseDate(String date) {
		try {
			String format = ResourceBundleProvider.getFormatBundle().getString("date_format_string");
			SimpleDateFormat sdf = new SimpleDateFormat(format, ResourceBundleProvider.getLocale());
			return sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getAdditionalFilter() {

		StringBuilder builder = new StringBuilder(" where 1=1 ");
		for (ListNotesFilter.Filter filter : this.getFilters()) {
			String property = filter.getProperty();
			if (additionalColumnMapping.containsKey(property)) {
				builder.append(" and ").append(additionalColumnMapping.get(property)).append(" like '%")
						.append(filter.getValue()).append("%' ");
			}
		}
		return builder.toString();
	}

	public boolean isDateCreatedCorrect() {
		return dateCreatedCorrect;
	}

	public boolean isDateUpdatedCorrect() {
		return dateUpdatedCorrect;
	}
}
