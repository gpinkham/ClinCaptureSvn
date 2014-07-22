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

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * FindSubjectsFilter class.
 */
public class FindSubjectsFilter implements CriteriaCommand {

	public static final Logger LOGGER = LoggerFactory.getLogger(FindSubjectsFilter.class);

	public static final int FOUR = 4;

	private List<Filter> filters = new ArrayList<Filter>();
	private HashMap<String, String> columnMapping = new HashMap<String, String>();
	private StudyGroupClassDAO studyGroupClassDAO;

	/**
	 * FindSubjectsFilter constructor.
	 * 
	 * @param studyGroupClassDAO
	 *            StudyGroupClassDAO
	 */
	public FindSubjectsFilter(StudyGroupClassDAO studyGroupClassDAO) {
		columnMapping.put("studySubject.label", "ss.label");
		columnMapping.put("studySubject.createdDate", "ss.date_created");
		columnMapping.put("studySubject.status", "ss.status_id");
		columnMapping.put("enrolledAt", "ST.unique_identifier");
		columnMapping.put("studySubject.secondaryLabel", "ss.secondary_label");
		columnMapping.put("subject.charGender", "s.gender");

		this.studyGroupClassDAO = studyGroupClassDAO;
	}

	public StudyGroupClassDAO getStudyGroupClassDAO() {
		return studyGroupClassDAO;
	}

	/**
	 * Method that adds a filter.
	 * 
	 * @param property
	 *            String property
	 * @param value
	 *            Object value
	 */
	public void addFilter(String property, Object value) {
		filters.add(new Filter(property, value));
	}

	/**
	 * Method that executes all filters.
	 * 
	 * @param criteria
	 *            String criteria
	 * @return String sql string
	 */
	public String execute(String criteria) {
		String theCriteria = "";
		for (Filter filter : filters) {
			theCriteria += buildCriteria(criteria, filter.getProperty(), filter.getValue());
		}
		return theCriteria;
	}

	private boolean dateIsOk(String dateStr) {

		boolean result = false;
		try {
			new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
			result = true;
		} catch (Exception e) {
			LOGGER.error("Error has occurred.", e);
		}
		return result;
	}

	private String buildCriteria(String criteria, String property, Object value) {

		StringBuilder theCriteria = new StringBuilder("");

		value = StringEscapeUtils.escapeSql(value.toString());
		if (value != null) {

			if (property.equals("studySubject.createdDate")) {

				if (dateIsOk(value.toString())) {

					theCriteria.append(" AND to_char(").append(columnMapping.get(property))
							.append(", 'yyyy-MM-dd') = '").append(value.toString()).append("'");
				}

			} else if (property.equals("studySubject.status")) {

				theCriteria.append(" AND ").append(columnMapping.get(property));
				if (value.toString().equals(String.valueOf(Status.DELETED.getId()))) {
					theCriteria.append(" IN (").append(Status.DELETED.getId()).append(",")
							.append(Status.AUTO_DELETED.getId()).append(")");
				} else {
					theCriteria.append(" = ").append(value.toString());
				}

			} else if (property.equals("studySubject.createdYear")) {

				theCriteria.append(" AND ").append("EXTRACT(YEAR FROM ss.date_created)").append(" = ")
						.append(value.toString());

			} else if (property.startsWith("sed_")) {

				int eventDefinitionId = Integer.valueOf(property.split("_")[1]);
				int subjectEventStatusId = SubjectEventStatus.getSubjectEventStatusIdByName(value.toString());

				// in case if the selected event definition is bound to an available dynamic group class (except default
				// one) -
				// need to add proper filter on subjects, to retrieve only those, who were randomized to this dynamic
				// group,
				// or those, who have already existing study events bound to the selected event definition
				StudyGroupClassBean dynamicGroupClassToFilterOn = getStudyGroupClassDAO()
						.findAvailableDynamicGroupByStudyEventDefinitionId(eventDefinitionId);
				if (dynamicGroupClassToFilterOn.isActive() && !dynamicGroupClassToFilterOn.isDefault()) {

					theCriteria.append(" AND (( ss.dynamic_group_class_id = ")
							.append(dynamicGroupClassToFilterOn.getId())
							.append(") OR (se.study_event_id IS NOT NULL AND se.study_event_definition_id = ")
							.append(eventDefinitionId).append("))");
				}

				String notScheduledFilter = new StringBuilder("")
						.append(" AND (se.study_subject_id IS NULL OR (se.study_event_definition_id != ")
						.append(eventDefinitionId)
						.append(" AND ss.study_subject_id NOT IN (SELECT DISTINCT ss1.study_subject_id ")
						.append(" FROM study_subject ss1 LEFT JOIN study_event")
						.append(" ON ss1.study_subject_id = study_event.study_subject_id")
						.append(" WHERE study_event.study_event_definition_id = ").append(eventDefinitionId)
						.append(")))").toString();

				if (subjectEventStatusId == SubjectEventStatus.NOT_SCHEDULED.getId()) {

					theCriteria.append(" AND ss.status_id NOT IN (").append(Status.DELETED.getId()).append(", ")
							.append(Status.AUTO_DELETED.getId()).append(", ").append(Status.LOCKED.getId()).append(")")
							.append(notScheduledFilter);

				} else if (subjectEventStatusId == SubjectEventStatus.REMOVED.getId()) {

					theCriteria.append(" AND ((ss.status_id IN (").append(Status.DELETED.getId()).append(", ")
							.append(Status.AUTO_DELETED.getId()).append(")").append(notScheduledFilter).append(")")
							.append(" OR ( se.study_event_definition_id = ").append(eventDefinitionId)
							.append(" AND se.subject_event_status_id = ").append(subjectEventStatusId).append(" ))");

				} else if (subjectEventStatusId == SubjectEventStatus.LOCKED.getId()) {

					theCriteria.append(" AND ((ss.status_id = ").append(Status.LOCKED.getId())
							.append(notScheduledFilter).append(")").append(" OR ( se.study_event_definition_id = ")
							.append(eventDefinitionId).append(" AND se.subject_event_status_id = ")
							.append(subjectEventStatusId).append(" ))");

				} else {

					theCriteria.append(" AND ( se.study_event_definition_id = ").append(eventDefinitionId)
							.append(" AND se.subject_event_status_id = ").append(subjectEventStatusId).append(" )");
				}

			} else if (property.startsWith("sgc_")) {

				int studyGroupClassId = Integer.parseInt(property.substring(FOUR));
				int groupId = Integer.parseInt(value.toString());

				theCriteria.append(" AND ").append(groupId).append(" = ( SELECT DISTINCT sgm.study_group_id")
						.append(" FROM subject_group_map sgm, study_group sg, study_group_class sgc, study s")
						.append(" WHERE sgm.study_group_class_id = ").append(studyGroupClassId)
						.append(" AND sgm.study_subject_id = ss.study_subject_id")
						.append(" AND sgm.study_group_id = sg.study_group_id")
						.append(" AND (s.parent_study_id = sgc.study_id OR ss.study_id = sgc.study_id)")
						.append(" AND sgm.study_group_class_id = sgc.study_group_class_id)");
			} else {
				theCriteria.append(" AND UPPER(").append(columnMapping.get(property)).append(") like UPPER('%")
						.append(value.toString()).append("%')");
			}
		}
		return (criteria + theCriteria.toString());
	}

	/**
	 * Filter sub class.
	 */
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
