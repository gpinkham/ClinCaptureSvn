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

package org.akaza.openclinica.dao.managestudy;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.apache.commons.lang.StringEscapeUtils;

public class FindSubjectsFilter implements CriteriaCommand {

	List<Filter> filters = new ArrayList<Filter>();
	HashMap<String, String> columnMapping = new HashMap<String, String>();

	public FindSubjectsFilter() {
		columnMapping.put("studySubject.label", "ss.label");
		columnMapping.put("studySubject.createdDate", "ss.date_created");
		columnMapping.put("studySubject.status", "ss.status_id");
		columnMapping.put("studySubject.oid", "ss.oc_oid");
		columnMapping.put("enrolledAt", "ST.unique_identifier");
		columnMapping.put("studySubject.secondaryLabel", "ss.secondary_label");
		columnMapping.put("subject.charGender", "s.gender");

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

	@SuppressWarnings("deprecation")
	private boolean dateIsOk(String dateStr) {
		boolean result = false;
		try {
			new Date(dateStr);
			result = true;
		} catch (Exception e) {
		}
		return result;
	}

	private String buildCriteria(String criteria, String property, Object value) {
		value = StringEscapeUtils.escapeSql(value.toString());
		if (value != null) {
			if (property.equals("studySubject.createdDate")) {
				if (dateIsOk(value.toString())) {
					criteria = criteria + " and ";
					criteria = criteria + " " + columnMapping.get(property) + " = '" + value.toString() + "' ";
				}
			} else if (property.equals("studySubject.status")) {
				criteria = criteria + " and ";
				criteria = criteria + " " + columnMapping.get(property) + " = " + value.toString() + " ";
			} else if (property.startsWith("sed_")) {
				value = SubjectEventStatus.getSubjectEventStatusIdByName(value.toString()) + "";
				if (!value.equals("2")) {
					criteria += " and ";
					criteria += " ( se.study_event_definition_id = " + property.substring(4);
					criteria += " and se.subject_event_status_id = " + value + " )";
				} else {
					criteria += " AND (se.study_subject_id is null or (se.study_event_definition_id != "
							+ property.substring(4);
					criteria += " AND (select count(*) from  study_subject ss1 LEFT JOIN study_event ON ss1.study_subject_id = study_event.study_subject_id";
					criteria += " where  study_event.study_event_definition_id =" + property.substring(4)
							+ " and ss.study_subject_id = ss1.study_subject_id) =0))";

				}
			} else if (property.startsWith("sgc_")) {
				int study_group_class_id = Integer.parseInt(property.substring(4));

				int group_id = Integer.parseInt(value.toString());
				criteria += "AND " + group_id + " = (" + " select distinct sgm.study_group_id"
						+ " FROM SUBJECT_GROUP_MAP sgm, STUDY_GROUP sg, STUDY_GROUP_CLASS sgc, STUDY s" + " WHERE "
						+ " sgm.study_group_class_id = " + study_group_class_id
						+ " AND sgm.study_subject_id = SS.study_subject_id"
						+ " AND sgm.study_group_id = sg.study_group_id"
						+ " AND (s.parent_study_id = sgc.study_id OR SS.study_id = sgc.study_id)"
						+ " AND sgm.study_group_class_id = sgc.study_group_class_id" + " ) ";

			}

			else {
				criteria = criteria + " and ";
				criteria = criteria + " UPPER(" + columnMapping.get(property) + ") like UPPER('%" + value.toString()
						+ "%')" + " ";
			}
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
