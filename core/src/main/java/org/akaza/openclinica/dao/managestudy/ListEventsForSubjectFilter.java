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
import org.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListEventsForSubjectFilter implements CriteriaCommand {

	private List<Filter> filters = new ArrayList<Filter>();
	private HashMap<String, String> columnMapping = new HashMap<String, String>();
	private Integer studyEventDefinitionId;
	private int dynamicGroupClassIdToFilterBy;

	public ListEventsForSubjectFilter(Integer studyEventDefinitionId, int dynamicGroupClassIdToFilterBy) {
		columnMapping.put("studySubject.label", "ss.label");
		columnMapping.put("studySubject.status", "ss.status_id");
		columnMapping.put("studySubject.oid", "ss.oc_oid");
		columnMapping.put("studySubject.secondaryLabel", "ss.secondary_label");
		columnMapping.put("enrolledAt", "ST.unique_identifier");
		columnMapping.put("subject.charGender", "s.gender");
		this.studyEventDefinitionId = studyEventDefinitionId;
		this.dynamicGroupClassIdToFilterBy = dynamicGroupClassIdToFilterBy;
	}

	public void addFilter(String property, Object value) {
		filters.add(new Filter(property, value));
	}

	public String execute(String criteria) {
		String theCriteria = "";
		for (Filter filter : filters) {
			theCriteria += buildCriteria(criteria, filter.getProperty(), filter.getValue());
		}

		if (dynamicGroupClassIdToFilterBy != 0) {
			theCriteria += " AND ss.dynamic_group_class_id = " + dynamicGroupClassIdToFilterBy + " ";
		}

		return theCriteria;
	}

	private String buildCriteria(String criteria, String property, Object value) {
		value = StringEscapeUtils.escapeSql(value.toString());
		if (value != null) {
			if (property.equals("studySubject.status")) {
				criteria = criteria + " and ";
				criteria = criteria + " " + columnMapping.get(property) + " = " + value.toString() + " ";
			} else if (property.equals("event.status")) {
				
				if (value.equals(String.valueOf(SubjectEventStatus.NOT_SCHEDULED.getId()))) {
					criteria += " AND ss.status_id NOT IN (" + Status.DELETED.getId() + ", " + Status.LOCKED.getId() + ")";
					criteria += " AND (se.study_subject_id IS NULL OR (se.study_event_definition_id != " + studyEventDefinitionId;
					criteria += " AND (SELECT count(*) FROM study_subject ss1 LEFT JOIN study_event ON ss1.study_subject_id = study_event.study_subject_id";
					criteria += " WHERE study_event.study_event_definition_id =" + studyEventDefinitionId
							+ " AND ss.study_subject_id = ss1.study_subject_id) =0))";
				} else if (value.equals(String.valueOf(SubjectEventStatus.LOCKED.getId()))) {
					criteria += " AND ( ss.status_id = " + Status.LOCKED.getId();
					criteria += " OR ( se.study_event_definition_id = " + studyEventDefinitionId;
					criteria += " AND se.subject_event_status_id = " + value + " ))";
				} else if (value.equals(String.valueOf(SubjectEventStatus.REMOVED.getId()))) {
					criteria += " AND ( ss.status_id = " + Status.DELETED.getId();
					criteria += " OR ( se.study_event_definition_id = " + studyEventDefinitionId;
					criteria += " AND se.subject_event_status_id = " + value + " ))";
				} else {
					criteria += " AND";
					criteria += " ( se.study_event_definition_id = " + studyEventDefinitionId;
					criteria += " AND se.subject_event_status_id = " + value + " )";
				}
				
			} else if (property.startsWith("sgc_")) {
				int study_group_class_id = Integer.parseInt(property.substring(4));

				int group_id = Integer.parseInt(value.toString());
				criteria += " AND " + group_id + " = (" + " select distinct sgm.study_group_id"
						+ " FROM SUBJECT_GROUP_MAP sgm, STUDY_GROUP sg, STUDY_GROUP_CLASS sgc, STUDY s" + " WHERE "
						+ " sgm.study_group_class_id = " + study_group_class_id
						+ " AND sgm.study_subject_id = SS.study_subject_id"
						+ " AND sgm.study_group_id = sg.study_group_id"
						+ " AND (s.parent_study_id = sgc.study_id OR SS.study_id = sgc.study_id)"
						+ " AND sgm.study_group_class_id = sgc.study_group_class_id" + " ) ";

			} else if (property.startsWith("crf_")) {

				int crfId = Integer.parseInt(property.toString().substring(4));

				if (value.equals(Status.NOT_STARTED.getName())) {

					criteria += " AND ss.status_id NOT IN (" + Status.DELETED.getId() + ", " + Status.LOCKED.getId() + ")"
							+ " AND se.subject_event_status_id <> " + SubjectEventStatus.LOCKED.getId()
							+ " AND (((SELECT COUNT(study_event.study_event_id)"
							+ " FROM study_event"
							+ " WHERE study_event.study_event_definition_id = " + studyEventDefinitionId
							+ " AND ss.study_subject_id = study_event.study_subject_id) = 0)"
							+ " OR (se.study_event_definition_id = " + studyEventDefinitionId
							+ " AND ((SELECT COUNT(ec.event_crf_id) FROM event_crf ec LEFT JOIN crf_version cv ON ec.crf_version_id = cv.crf_version_id"
							+ " WHERE cv.crf_id = " + crfId
							+ " AND ec.study_event_id = se.study_event_id ) = 0"
							+ " OR (SELECT COUNT(ec.event_crf_id) FROM event_crf ec LEFT JOIN crf_version cv ON ec.crf_version_id = cv.crf_version_id"
							+ " WHERE cv.crf_id = " + crfId
							+ " AND ec.study_event_id = se.study_event_id AND ec.not_started = TRUE) = 1)))";

				} else if (value.equals(Status.DATA_ENTRY_STARTED.getName())) {

					criteria += " AND se.study_event_definition_id = " + studyEventDefinitionId;
					criteria += " AND se.study_event_id IN" + " (SELECT study_event_id"
							+ " FROM event_crf ec LEFT JOIN crf_version cv ON ec.crf_version_id = cv.crf_version_id"
							+ " WHERE crf_id = " + crfId + " AND ec.status_id = " + Status.AVAILABLE.getId()
							+ " AND ec.date_completed IS NULL AND ec.not_started = FALSE )";

				} else if (value.equals(Status.INITIAL_DATA_ENTRY_COMPLETED.getName())) {

					criteria += " AND se.study_event_definition_id = " + studyEventDefinitionId;
					criteria += " AND se.study_event_id IN" + " (SELECT study_event_id"
							+ " FROM event_crf ec LEFT JOIN crf_version cv ON ec.crf_version_id = cv.crf_version_id"
							+ " WHERE crf_id = " + crfId + " AND ec.status_id = " + Status.PENDING.getId()
							+ " AND ec.date_completed IS NOT NULL"
							+ " AND ec.validator_id = 0 AND ec.date_validate_completed IS NULL )";

				} else if (value.equals(Status.DOUBLE_DATA_ENTRY.getName())) {

					criteria += " AND se.study_event_definition_id = " + studyEventDefinitionId;
					criteria += " AND se.study_event_id IN" + " (SELECT study_event_id"
							+ " FROM event_crf ec LEFT JOIN crf_version cv ON ec.crf_version_id = cv.crf_version_id"
							+ " WHERE crf_id = " + crfId + " AND ec.status_id = " + Status.PENDING.getId()
							+ " AND ec.date_completed IS NOT NULL"
							+ " AND ec.validator_id > 0 AND ec.date_validate_completed IS NULL )";

				} else if (value.equals(Status.COMPLETED.getName())) {

					criteria += " AND se.study_event_definition_id = " + studyEventDefinitionId;
					criteria += " AND se.study_event_id IN" + " (SELECT study_event_id"
							+ " FROM event_crf ec LEFT JOIN crf_version cv ON ec.crf_version_id = cv.crf_version_id"
							+ " WHERE crf_id = " + crfId + " AND ec.status_id = " + Status.UNAVAILABLE.getId()
							+ " AND ec.date_validate_completed IS NOT NULL" + " AND sdv_status <> TRUE )"
							+ " AND se.subject_event_status_id <> " + SubjectEventStatus.SIGNED.getId();

				} else if (value.equals(Status.SOURCE_DATA_VERIFIED.getName())) {

					criteria += " AND se.study_event_definition_id = " + studyEventDefinitionId;
					criteria += " AND se.study_event_id IN" + " (SELECT study_event_id"
							+ " FROM event_crf ec LEFT JOIN crf_version cv ON ec.crf_version_id = cv.crf_version_id"
							+ " WHERE crf_id = " + crfId + " AND ec.status_id = " + Status.UNAVAILABLE.getId()
							+ " AND sdv_status = TRUE )" + " AND se.subject_event_status_id <> "
							+ SubjectEventStatus.SIGNED.getId();

				} else if (value.equals(Status.SIGNED.getName())) {

					criteria += " AND se.study_event_definition_id = " + studyEventDefinitionId;
					criteria += " AND se.study_event_id IN" + " (SELECT study_event_id"
							+ " FROM event_crf ec LEFT JOIN crf_version cv ON ec.crf_version_id = cv.crf_version_id"
							+ " WHERE crf_id = " + crfId + " AND ec.status_id = " + Status.UNAVAILABLE.getId()
							+ " AND ec.date_validate_completed IS NOT NULL )" + " AND se.subject_event_status_id = "
							+ SubjectEventStatus.SIGNED.getId();

				} else if (value.equals(Status.LOCKED.getName())) {

					criteria += " AND ((se.study_event_definition_id = " + studyEventDefinitionId
							+ " AND ( se.subject_event_status_id = " + SubjectEventStatus.LOCKED.getId()
							+ " OR ((se.subject_event_status_id = " + SubjectEventStatus.SKIPPED.getId()
							+ " OR se.subject_event_status_id = " + SubjectEventStatus.STOPPED.getId() + ")"
							+ " AND se.study_event_id IN" + " (SELECT study_event_id"
							+ " FROM event_crf ec LEFT JOIN crf_version cv ON ec.crf_version_id = cv.crf_version_id"
							+ " WHERE crf_id = " + crfId + " AND ec.not_started = FALSE))))" + " OR ss.status_id = "
							+ Status.LOCKED.getId() + ")";

				} else if (value.equals(Status.DELETED.getName())) {

					criteria += " AND ((se.study_event_definition_id = " + studyEventDefinitionId
							+ " AND se.study_event_id IN" + " (SELECT study_event_id"
							+ " FROM event_crf ec LEFT JOIN crf_version cv ON ec.crf_version_id = cv.crf_version_id"
							+ " WHERE crf_id = " + crfId + " AND ec.not_started = FALSE" + " AND (ec.status_id = "
							+ Status.DELETED.getId() + " OR ec.status_id = " + Status.AUTO_DELETED.getId() + ")))"
							+ " OR ss.status_id = " + Status.DELETED.getId() + ")";

				}

			} else if (property.equals("studySubject.createdDate")) {
				// do nothing
			} else {
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
