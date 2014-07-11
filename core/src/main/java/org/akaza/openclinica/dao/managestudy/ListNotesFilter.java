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

import com.clinovo.util.RegexpUtil;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class ListNotesFilter implements CriteriaCommand {

	List<Filter> filters = new ArrayList<Filter>();
	HashMap<String, String> columnMapping = new HashMap<String, String>();

	HashMap<String, String> additionalColumnMapping = new HashMap<String, String>();

	HashMap<String, String> additionalStudyEventColumnMapping = new HashMap<String, String>();

	private boolean dateCreatedCorrect = true;
	private boolean dateUpdatedCorrect = true;

	public ListNotesFilter() {
		columnMapping.put("discrepancyNoteBean.id", "dn.discrepancy_note_id");
		columnMapping.put("studySubject.label", "ss.label");
		columnMapping.put("studySubject.id", "ss.study_subject_id");
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

		additionalStudyEventColumnMapping.put("eventId", "se.study_event_id");
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
			if (property.equals("discrepancyNoteBean.id")) {
				try {
					Integer.parseInt(value.toString().trim());
					criteria += " and " + columnMapping.get(property) + " = " + value.toString();
				} catch (NumberFormatException nFE) {
					nFE.printStackTrace();
				}
			} else if (property.equals("studySubject.labelExact")) {
				criteria += " and  UPPER(" + columnMapping.get(property) + ") = UPPER('" + value.toString() + "')"
						+ " ";
			} else if (property.equals("studySubject.id")) {
				criteria += " and " + columnMapping.get(property) + " = " + value.toString() + " ";
			} else if (property.equals("studySubject.label") || property.equals("discrepancyNoteBean.description")) {
				criteria += " and UPPER(" + columnMapping.get(property) + ") like UPPER('%" + value.toString() + "%')"
						+ " ";
			} else if (property.equals("discrepancyNoteBean.user")) {
				criteria += parseUserAccountName(columnMapping.get(property), value.toString());
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
				criteria += parseResolutionStatus(value.toString());
			} else if ("discrepancyNoteBean.createdDate".equalsIgnoreCase(property)) {
				Date date = parseDate((String) value);
				if (date != null) {
					String pgDate = dateFormat.format(date);
					if ("oracle".equalsIgnoreCase(CoreResources.getDBType())) {
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
					if ("oracle".equalsIgnoreCase(CoreResources.getDBType())) {
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

	private String parseResolutionStatus(String stringValue) {
		StringBuilder result = new StringBuilder();
		if (stringValue.length() > 0) {
			result.append(" and (");
			for (int i = 0; i < stringValue.length(); i++) {
				result.append(" or " + "dn.resolution_status_id = ").append(stringValue.charAt(i));
			}
			result.append(")");
		}

		return result.toString().replaceFirst(" or ", "");
	}

	private String parseUserAccountName(String property, String stringValue) {
		StringBuilder result = new StringBuilder();

		if (!stringValue.equalsIgnoreCase(ResourceBundleProvider.getResTerm("not_assigned"))) {
			result.append(" and ").append(property).append(" like '").append(stringValue).append("' ");
		} else {
			result.append(" and ").append(property).append(" is NULL ");
		}

		return result.toString();
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

	public String getAdditionalStudyEventFilter() {
		StringBuilder builder = new StringBuilder("");
		for (ListNotesFilter.Filter filter : this.getFilters()) {
			String property = filter.getProperty();
			if (property.equals("eventId")) {
				builder.append(" and ").append(additionalStudyEventColumnMapping.get("eventId")).append(" = ")
						.append(filter.getValue()).append(" ");
			}
		}
		return builder.toString();
	}

	public String getAdditionalFilter() {
		StringBuilder builder = new StringBuilder(" where 1=1 ");
		for (ListNotesFilter.Filter filter : this.getFilters()) {
			String property = filter.getProperty();
			if (additionalColumnMapping.containsKey(property)) {
				String itemDataOrdinal = null;
				String value = (String) filter.getValue();
				if (property.equalsIgnoreCase("entityName") && Pattern.compile("\\(#\\d*\\)").matcher(value).find()) {
					itemDataOrdinal = RegexpUtil.parseGroup(value, "(\\(#\\d*\\))", 1).replaceAll("\\(#|\\)", "");
					value = RegexpUtil.parseGroup(value, "(\\w*)(\\(#\\d*\\))", 1);
				}
				builder.append(" and ").append(additionalColumnMapping.get(property)).append(" like '%").append(value)
						.append("%' ");
				if (itemDataOrdinal != null) {
					builder.append(" and ").append("dns.item_data_ordinal").append(" = ").append(itemDataOrdinal)
							.append(" ");
				}
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
