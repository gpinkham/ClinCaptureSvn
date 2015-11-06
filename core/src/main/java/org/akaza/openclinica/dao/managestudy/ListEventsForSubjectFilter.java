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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListEventsForSubjectFilter implements CriteriaCommand {

	private List<Filter> filters = new ArrayList<Filter>();
	private HashMap<String, String> columnMapping = new HashMap<String, String>();
	private int studyEventDefinitionId;
	private StudyGroupClassDAO studyGroupClassDAO;

	public ListEventsForSubjectFilter(int studyEventDefinitionId, StudyGroupClassDAO studyGroupClassDAO) {
		columnMapping.put("studySubject.label", "ss.label");
		columnMapping.put("studySubject.status", "ss.status_id");
		columnMapping.put("studySubject.oid", "ss.oc_oid");
		columnMapping.put("studySubject.secondaryLabel", "ss.secondary_label");
		columnMapping.put("enrolledAt", "ST.unique_identifier");
		columnMapping.put("subject.charGender", "s.gender");
		this.studyEventDefinitionId = studyEventDefinitionId;
		this.studyGroupClassDAO = studyGroupClassDAO;
	}

	public StudyGroupClassDAO getStudyGroupClassDAO() {
		return studyGroupClassDAO;
	}

	public int getStudyEventDefinitionId() {
		return studyEventDefinitionId;
	}

	public void addFilter(String property, Object value) {
		filters.add(new Filter(property, value));
	}

	public String execute(String criteria) {

		StringBuilder theCriteria = new StringBuilder("");

		// in case if the selected event definition is bound to an available dynamic group class (except default one) -
		// need to add proper filter on subjects, to retrieve only those, who were randomized to this dynamic group,
		// or those, who have already existing study events bound to the selected event definition
		StudyGroupClassBean dynamicGroupClassToFilterOn = getStudyGroupClassDAO()
				.findAvailableDynamicGroupByStudyEventDefinitionId(getStudyEventDefinitionId());
		if (dynamicGroupClassToFilterOn.isActive() && !dynamicGroupClassToFilterOn.isDefault()) {

			theCriteria.append(" AND (( ss.dynamic_group_class_id = ").append(dynamicGroupClassToFilterOn.getId())
					.append(") OR (se.study_event_id IS NOT NULL AND se.study_event_definition_id = ")
					.append(getStudyEventDefinitionId()).append(")) ");
		}

		for (Filter filter : filters) {
			theCriteria.append(buildCriteria(criteria, filter.getProperty(), filter.getValue()));
		}

		return theCriteria.toString();
	}

	private String buildCriteria(String criteria, String property, Object value) {

		StringBuilder sqlCriteria = new StringBuilder("");

		value = StringEscapeUtils.escapeSql(value.toString());

		if (value != null) {
			if (property.equals("studySubject.status")) {
				sqlCriteria.append(" and ").append(" ").append(columnMapping.get(property)).append(" = ")
						.append(value.toString()).append(" ");
			} else if (property.equals("event.status")) {

				if (value.equals(String.valueOf(SubjectEventStatus.NOT_SCHEDULED.getId()))) {
					sqlCriteria
							.append(" AND ss.status_id NOT IN (")
							.append(Status.DELETED.getId())
							.append(", ")
							.append(Status.LOCKED.getId())
							.append(")")
							.append(" AND (se.study_subject_id IS NULL OR (se.study_event_definition_id != ")
							.append(getStudyEventDefinitionId())
							.append(" AND (SELECT count(*) FROM study_subject ss1 LEFT JOIN study_event ON ss1.study_subject_id = study_event.study_subject_id")
							.append(" WHERE study_event.study_event_definition_id =")
							.append(getStudyEventDefinitionId())
							.append(" AND ss.study_subject_id = ss1.study_subject_id) =0))");
				} else if (value.equals(String.valueOf(SubjectEventStatus.LOCKED.getId()))) {
					sqlCriteria.append(" AND ( ss.status_id = ").append(Status.LOCKED.getId())
							.append(" OR ( se.study_event_definition_id = ").append(getStudyEventDefinitionId())
							.append(" AND se.subject_event_status_id = ").append(value).append(" ))");
				} else if (value.equals(String.valueOf(SubjectEventStatus.REMOVED.getId()))) {
					sqlCriteria.append(" AND ( ss.status_id = ").append(Status.DELETED.getId())
							.append(" OR ( se.study_event_definition_id = ").append(getStudyEventDefinitionId())
							.append(" AND se.subject_event_status_id = ").append(value).append(" ))");
				} else {
					sqlCriteria.append(" AND").append(" ( se.study_event_definition_id = ")
							.append(getStudyEventDefinitionId()).append(" AND se.subject_event_status_id = ")
							.append(value).append(" )");
				}

			} else if (property.startsWith("sgc_")) {
				int study_group_class_id = Integer.parseInt(property.substring(4));

				int group_id = Integer.parseInt(value.toString());

				sqlCriteria.append(" AND ").append(group_id).append(" = ( select distinct sgm.study_group_id")
						.append(" FROM SUBJECT_GROUP_MAP sgm, STUDY_GROUP sg, STUDY_GROUP_CLASS sgc, STUDY s")
						.append(" WHERE sgm.study_group_class_id = ").append(study_group_class_id)
						.append(" AND sgm.study_subject_id = SS.study_subject_id")
						.append(" AND sgm.study_group_id = sg.study_group_id")
						.append(" AND (s.parent_study_id = sgc.study_id OR SS.study_id = sgc.study_id)")
						.append(" AND sgm.study_group_class_id = sgc.study_group_class_id ) ");

			} else if (property.startsWith("crf_")) {

				int crfId = Integer.parseInt(property.substring(4));

				if (value.equals(Status.NOT_STARTED.getName())) {

					sqlCriteria
							.append(" AND ss.status_id NOT IN (").append(Status.AUTO_DELETED.getId()).append(", ")
							.append(Status.DELETED.getId()).append(", ").append(Status.LOCKED.getId()).append(")")
							.append(" AND ( NOT EXISTS (SELECT study_event.study_event_id")
							.append(" FROM study_event")
							.append(" WHERE study_event.study_event_definition_id = ").append(getStudyEventDefinitionId())
							.append(" AND ss.study_subject_id = study_event.study_subject_id)")
							.append(" OR (se.study_event_definition_id = ").append(getStudyEventDefinitionId())
							.append(" AND se.subject_event_status_id != ").append(SubjectEventStatus.LOCKED.getId())
							.append(" AND EXISTS (SELECT * FROM crf_version WHERE crf_id = ").append(crfId)
							.append(" AND status_id = ").append(Status.AVAILABLE.getId())
							.append(" ) AND ( NOT EXISTS (SELECT ec.event_crf_id FROM event_crf ec LEFT JOIN crf_version cv ON ec.crf_version_id = cv.crf_version_id")
							.append(" WHERE cv.crf_id = ").append(crfId)
							.append(" AND ec.study_event_id = se.study_event_id )")
							.append(" OR EXISTS (SELECT ec.event_crf_id FROM event_crf ec LEFT JOIN crf_version cv ON ec.crf_version_id = cv.crf_version_id")
							.append(" WHERE cv.crf_id = ").append(crfId)
							.append(" AND ec.study_event_id = se.study_event_id AND ec.not_started = TRUE))))");

				} else if (value.equals(Status.DATA_ENTRY_STARTED.getName())) {

					sqlCriteria
							.append(" AND se.study_event_definition_id = ")
							.append(getStudyEventDefinitionId())
							.append(" AND se.study_event_id IN")
							.append(" (SELECT study_event_id")
							.append(" FROM event_crf ec LEFT JOIN crf_version cv ON ec.crf_version_id = cv.crf_version_id")
							.append(" WHERE crf_id = ").append(crfId).append(" AND ec.status_id = ")
							.append(Status.AVAILABLE.getId())
							.append(" AND ec.date_completed IS NULL AND ec.not_started = FALSE AND cv.status_id = ")
							.append(Status.AVAILABLE.getId()).append(")");

				} else if (value.equals(Status.PARTIAL_DATA_ENTRY.getName())) {

					sqlCriteria.append(" AND se.study_event_definition_id = ")
							.append(getStudyEventDefinitionId())
							.append(" AND se.study_event_id IN")
							.append(" (SELECT study_event_id")
							.append(" FROM event_crf ec LEFT JOIN crf_version cv ON ec.crf_version_id = cv.crf_version_id")
							.append(" WHERE crf_id = ").append(crfId).append(" AND ec.status_id = ")
							.append(Status.PARTIAL_DATA_ENTRY.getId())
							.append(" AND cv.status_id = ")
							.append(Status.AVAILABLE.getId()).append(")");

				} else if (value.equals(Status.INITIAL_DATA_ENTRY_COMPLETED.getName())) {

					sqlCriteria
							.append(" AND se.study_event_definition_id = ")
							.append(getStudyEventDefinitionId())
							.append(" AND se.study_event_id IN")
							.append(" (SELECT study_event_id")
							.append(" FROM event_crf ec LEFT JOIN crf_version cv ON ec.crf_version_id = cv.crf_version_id")
							.append(" WHERE crf_id = ")
							.append(crfId)
							.append(" AND ec.status_id = ")
							.append(Status.PENDING.getId())
							.append(" AND ec.date_completed IS NOT NULL")
							.append(" AND ec.validator_id = 0 AND ec.date_validate_completed IS NULL AND cv.status_id = ")
							.append(Status.AVAILABLE.getId()).append(")");

				} else if (value.equals(Status.PARTIAL_DOUBLE_DATA_ENTRY.getName())) {

					sqlCriteria
							.append(" AND se.study_event_definition_id = ")
							.append(getStudyEventDefinitionId())
							.append(" AND se.study_event_id IN")
							.append(" (SELECT study_event_id")
							.append(" FROM event_crf ec LEFT JOIN crf_version cv ON ec.crf_version_id = cv.crf_version_id")
							.append(" WHERE crf_id = ")
							.append(crfId)
							.append(" AND ec.status_id = ")
							.append(Status.PARTIAL_DOUBLE_DATA_ENTRY.getId())
							.append(" AND ec.date_completed IS NOT NULL")
							.append(" AND ec.validator_id > 0 AND ec.date_validate_completed IS NULL AND cv.status_id = ")
							.append(Status.AVAILABLE.getId()).append(")");
					
				} else if (value.equals(Status.DOUBLE_DATA_ENTRY.getName())) {

					sqlCriteria
							.append(" AND se.study_event_definition_id = ")
							.append(getStudyEventDefinitionId())
							.append(" AND se.study_event_id IN")
							.append(" (SELECT study_event_id")
							.append(" FROM event_crf ec LEFT JOIN crf_version cv ON ec.crf_version_id = cv.crf_version_id")
							.append(" WHERE crf_id = ")
							.append(crfId)
							.append(" AND ec.status_id = ")
							.append(Status.PENDING.getId())
							.append(" AND ec.date_completed IS NOT NULL")
							.append(" AND ec.validator_id > 0 AND ec.date_validate_completed IS NULL AND cv.status_id = ")
							.append(Status.AVAILABLE.getId()).append(")");

				} else if (value.equals(Status.COMPLETED.getName())) {

					sqlCriteria
							.append(" AND se.study_event_definition_id = ")
							.append(getStudyEventDefinitionId())
							.append(" AND se.study_event_id IN")
							.append(" (SELECT study_event_id")
							.append(" FROM event_crf ec LEFT JOIN crf_version cv ON ec.crf_version_id = cv.crf_version_id")
							.append(" WHERE crf_id = ").append(crfId).append(" AND ec.status_id = ")
							.append(Status.UNAVAILABLE.getId()).append(" AND ec.date_validate_completed IS NOT NULL")
							.append(" AND sdv_status <> TRUE AND cv.status_id = ").append(Status.AVAILABLE.getId())
							.append(")").append(" AND se.subject_event_status_id <> ")
							.append(SubjectEventStatus.SIGNED.getId());

				} else if (value.equals(Status.SOURCE_DATA_VERIFIED.getName())) {

					sqlCriteria
							.append(" AND se.study_event_definition_id = ")
							.append(getStudyEventDefinitionId())
							.append(" AND se.study_event_id IN")
							.append(" (SELECT study_event_id")
							.append(" FROM event_crf ec LEFT JOIN crf_version cv ON ec.crf_version_id = cv.crf_version_id")
							.append(" WHERE crf_id = ").append(crfId).append(" AND ec.status_id = ")
							.append(Status.UNAVAILABLE.getId()).append(" AND sdv_status = TRUE AND cv.status_id = ")
							.append(Status.AVAILABLE.getId()).append(")").append(" AND se.subject_event_status_id <> ")
							.append(SubjectEventStatus.SIGNED.getId());

				} else if (value.equals(Status.SIGNED.getName())) {

					sqlCriteria
							.append(" AND se.study_event_definition_id = ")
							.append(getStudyEventDefinitionId())
							.append(" AND se.study_event_id IN")
							.append(" (SELECT study_event_id")
							.append(" FROM event_crf ec LEFT JOIN crf_version cv ON ec.crf_version_id = cv.crf_version_id")
							.append(" WHERE crf_id = ").append(crfId).append(" AND ec.status_id = ")
							.append(Status.UNAVAILABLE.getId())
							.append(" AND ec.date_validate_completed IS NOT NULL AND cv.status_id = ")
							.append(Status.AVAILABLE.getId()).append(")").append(" AND se.subject_event_status_id = ")
							.append(SubjectEventStatus.SIGNED.getId());

				} else if (value.equals(Status.LOCKED.getName())) {

					sqlCriteria
							.append(" AND ((se.study_event_definition_id = ")
							.append(getStudyEventDefinitionId())
							.append(" AND ( se.subject_event_status_id = ")
							.append(SubjectEventStatus.LOCKED.getId())
							.append(" OR ((se.subject_event_status_id = ")
							.append(SubjectEventStatus.SKIPPED.getId())
							.append(" OR se.subject_event_status_id = ")
							.append(SubjectEventStatus.STOPPED.getId())
							.append(" OR (SELECT cv.status_id FROM event_crf ec LEFT JOIN crf_version cv ON ec.crf_version_id = cv.crf_version_id")
							.append(" WHERE ec.study_event_id = se.study_event_id AND cv.crf_id = ")
							.append(crfId)
							.append(" ) NOT IN (")
							.append(Status.AVAILABLE.getId())
							.append("))")
							.append(" AND se.study_event_id IN")
							.append(" (SELECT study_event_id")
							.append(" FROM event_crf ec LEFT JOIN crf_version cv ON ec.crf_version_id = cv.crf_version_id")
							.append(" WHERE crf_id = ").append(crfId).append(" AND ec.not_started = FALSE))")
							.append(" OR (se.subject_event_status_id NOT IN (")
							.append(SubjectEventStatus.REMOVED.getId()).append(")")
							.append(" AND NOT EXISTS (SELECT * FROM crf_version WHERE crf_id = ").append(crfId)
							.append(" AND status_id = ").append(Status.AVAILABLE.getId()).append("))))")
							.append(" OR ss.status_id = ").append(Status.LOCKED.getId()).append(")");

				} else if (value.equals(Status.DELETED.getName())) {

					sqlCriteria
							.append(" AND (se.study_event_definition_id = ")
							.append(getStudyEventDefinitionId())
							.append(" AND se.study_event_id IN")
							.append(" (SELECT study_event_id")
							.append(" FROM event_crf ec LEFT JOIN crf_version cv ON ec.crf_version_id = cv.crf_version_id")
							.append(" WHERE crf_id = ").append(crfId).append(" AND ec.not_started = FALSE")
							.append(" AND ec.status_id IN (").append(Status.DELETED.getId()).append(", ")
							.append(Status.AUTO_DELETED.getId()).append(")))");

				}

			} else {

				sqlCriteria.append(" AND ").append(" UPPER(").append(columnMapping.get(property))
						.append(") like UPPER('%").append(value.toString()).append("%') ");

			}

		}

		return (criteria + sqlCriteria.toString());
	}

	public boolean isEmpty() {
		return filters.isEmpty();
	}

	public String getFilterValueByProperty(String property) {

		for (Filter filter : filters) {
			if (filter.getProperty().equalsIgnoreCase(property)) {
				return (String) filter.getValue();
			}
		}

		return null;
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
