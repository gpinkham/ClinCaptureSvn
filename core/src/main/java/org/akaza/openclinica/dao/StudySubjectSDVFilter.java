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

package org.akaza.openclinica.dao;

import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.managestudy.CriteriaCommand;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StudySubjectSDVFilter implements CriteriaCommand {

	List<Filter> filters = new ArrayList<Filter>();
	HashMap<String, String> columnMapping = new HashMap<String, String>();

	/*
	 * We use set of subqueries - it can be slow if there are a lot of data in DB. The join is faster - so, maybe it
	 * will be better to use join operators.
	 */

	static String NON_SDVD_STUDY_SUBJECTS = "AND NOT ((SELECT count(distinct edc.event_definition_crf_id) FROM study_event_definition sed, study s, event_definition_crf edc WHERE sed.study_id = s.study_id and (s.study_id = mst.study_id or s.study_id = mst.parent_study_id) and sed.status_id = 1 and edc.study_id = sed.study_id and edc.study_event_definition_id = sed.study_event_definition_id and edc.status_id = 1 and edc.source_data_verification_code in (1,2)) = (SELECT count(distinct ec.event_crf_id) FROM study_event_definition sed, study s, event_definition_crf edc, study_event se, event_crf ec, crf_version cv WHERE ec.sdv_status = true and ec.status_id not in (0,1) and ec.crf_version_id = cv.crf_version_id and edc.crf_id = cv.crf_id and ec.study_event_id = se.study_event_id and ec.study_subject_id = se.study_subject_id and se.study_subject_id = mss.study_subject_id and se.study_event_definition_id = sed.study_event_definition_id and sed.study_id = s.study_id and (s.study_id = mst.study_id or s.study_id = mst.parent_study_id) and sed.status_id = 1 and edc.study_id = sed.study_id and edc.study_event_definition_id = sed.study_event_definition_id and edc.status_id = 1 and edc.source_data_verification_code in (1,2)))";
	// prev logic
	/*
	 * "AND NOT (0 < (select count(ec.event_crf_id) from event_crf ec, study_event se, study_subject ss,crf_version cv,study s where ec.sdv_status = true AND ec.study_event_id = se.study_event_id AND ss.study_subject_id = se.study_subject_id AND ec.crf_version_id = cv.crf_version_id AND ss.study_id = s.study_id AND se.subject_event_status_id in (3,4,5,6,7,8) AND ss.study_subject_id = mss.study_subject_id AND "
	 * +
	 * "(select edc.source_data_verification_code from event_definition_crf edc where study_id = s.parent_study_id and crf_id = cv.crf_id and study_event_definition_id = se.study_event_definition_id) in (1,2)) AND "
	 * +
	 * "0 = (select count(ec.event_crf_id) from event_crf ec, study_event se, study_subject ss,crf_version cv,study s where ec.sdv_status = false AND ec.not_started = false AND ec.study_event_id = se.study_event_id AND ss.study_subject_id = se.study_subject_id AND ec.crf_version_id = cv.crf_version_id AND ss.study_id = s.study_id AND se.subject_event_status_id in (3,4,5,6,7,8) AND ss.study_subject_id = mss.study_subject_id AND "
	 * +
	 * "(select edc.source_data_verification_code from event_definition_crf edc where study_id = s.parent_study_id and crf_id = cv.crf_id and study_event_definition_id = se.study_event_definition_id) in (1,2)))"
	 * ;
	 */
	// The filter below is wrong (for oracle I tihnk it's also wrong)
	// " AND ( 0 = (select count(ec.event_crf_id) from event_crf ec, study_event se, study_subject ss,crf_version cv,study s where ec.study_event_id = se.study_event_id AND ss.study_subject_id = se.study_subject_id AND ec.crf_version_id = cv.crf_version_id AND ss.study_id = s.study_id AND se.subject_event_status_id = 4 AND ss.study_subject_id = mss.study_subject_id ) OR 0 < (select count(ec.event_crf_id) from event_crf ec, study_event se, study_subject ss,crf_version cv,study s where ec.study_event_id = se.study_event_id AND ss.study_subject_id = se.study_subject_id AND ec.crf_version_id = cv.crf_version_id AND ss.study_id = s.study_id AND ec.sdv_status = false AND se.subject_event_status_id = 4 AND ss.study_subject_id = mss.study_subject_id AND (  ((1 = ( select edc.source_data_verification_code from event_definition_crf edc where study_id = s.parent_study_id and crf_id = cv.crf_id and study_event_definition_id = se.study_event_definition_id ) OR 2 = ( select edc.source_data_verification_code from event_definition_crf edc where study_id = s.parent_study_id and crf_id = cv.crf_id and study_event_definition_id = se.study_event_definition_id )) AND 0 = ( select count(edc.source_data_verification_code) from event_definition_crf edc where study_id = s.study_id and crf_id = cv.crf_id and study_event_definition_id = se.study_event_definition_id )) OR ( 1 = ( select edc.source_data_verification_code from event_definition_crf edc where study_id = s.study_id and crf_id = cv.crf_id and study_event_definition_id = se.study_event_definition_id ) or 2 = ( select edc.source_data_verification_code from event_definition_crf edc where study_id = s.study_id and crf_id = cv.crf_id and study_event_definition_id = se.study_event_definition_id ) )))) ";
	static String SDVD_STUDY_SUBJECTS = "AND ((SELECT count(distinct edc.event_definition_crf_id) FROM study_event_definition sed, study s, event_definition_crf edc WHERE sed.study_id = s.study_id and (s.study_id = mst.study_id or s.study_id = mst.parent_study_id) and sed.status_id = 1 and edc.study_id = sed.study_id and edc.study_event_definition_id = sed.study_event_definition_id and edc.status_id = 1 and edc.source_data_verification_code in (1,2)) = (SELECT count(distinct ec.event_crf_id) FROM study_event_definition sed, study s, event_definition_crf edc, study_event se, event_crf ec, crf_version cv WHERE ec.sdv_status = true and ec.status_id not in (0,1) and ec.crf_version_id = cv.crf_version_id and edc.crf_id = cv.crf_id and ec.study_event_id = se.study_event_id and ec.study_subject_id = se.study_subject_id and se.study_subject_id = mss.study_subject_id and se.study_event_definition_id = sed.study_event_definition_id and sed.study_id = s.study_id and (s.study_id = mst.study_id or s.study_id = mst.parent_study_id) and sed.status_id = 1 and edc.study_id = sed.study_id and edc.study_event_definition_id = sed.study_event_definition_id and edc.status_id = 1 and edc.source_data_verification_code in (1,2)))";
	// prev logic
	/*
	 * "AND 0 < (select count(ec.event_crf_id) from event_crf ec, study_event se, study_subject ss,crf_version cv,study s where ec.sdv_status = true AND ec.study_event_id = se.study_event_id AND ss.study_subject_id = se.study_subject_id AND ec.crf_version_id = cv.crf_version_id AND ss.study_id = s.study_id AND se.subject_event_status_id in (3,4,5,6,7,8) AND ss.study_subject_id = mss.study_subject_id AND "
	 * +
	 * "(select edc.source_data_verification_code from event_definition_crf edc where study_id = s.parent_study_id and crf_id = cv.crf_id and study_event_definition_id = se.study_event_definition_id) in (1,2)) AND "
	 * +
	 * "0 = (select count(ec.event_crf_id) from event_crf ec, study_event se, study_subject ss,crf_version cv,study s where ec.sdv_status = false AND ec.not_started = false AND ec.study_event_id = se.study_event_id AND ss.study_subject_id = se.study_subject_id AND ec.crf_version_id = cv.crf_version_id AND ss.study_id = s.study_id AND se.subject_event_status_id in (3,4,5,6,7,8) AND ss.study_subject_id = mss.study_subject_id AND "
	 * +
	 * "(select edc.source_data_verification_code from event_definition_crf edc where study_id = s.parent_study_id and crf_id = cv.crf_id and study_event_definition_id = se.study_event_definition_id) in (1,2))"
	 * ;
	 */
	// The filter below is wrong (for oracle I tihnk it's also wrong)
	// " AND ( 0 < (select count(ec.event_crf_id) from event_crf ec, study_event se, study_subject ss,crf_version cv,study s where ec.study_event_id = se.study_event_id AND ss.study_subject_id = se.study_subject_id AND ec.crf_version_id = cv.crf_version_id AND ss.study_id = s.study_id AND se.subject_event_status_id = 4 AND ss.study_subject_id = mss.study_subject_id ) AND 0 = (select count(ec.event_crf_id) from event_crf ec, study_event se, study_subject ss,crf_version cv,study s where ec.study_event_id = se.study_event_id AND ss.study_subject_id = se.study_subject_id AND ec.crf_version_id = cv.crf_version_id AND ss.study_id = s.study_id AND ec.sdv_status = false AND se.subject_event_status_id = 4 AND ss.study_subject_id = mss.study_subject_id AND (  ((1 = ( select edc.source_data_verification_code from event_definition_crf edc where study_id = s.parent_study_id and crf_id = cv.crf_id and study_event_definition_id = se.study_event_definition_id ) OR 2 = ( select edc.source_data_verification_code from event_definition_crf edc where study_id = s.parent_study_id and crf_id = cv.crf_id and study_event_definition_id = se.study_event_definition_id )) AND 0 = ( select count(edc.source_data_verification_code) from event_definition_crf edc where study_id = s.study_id and crf_id = cv.crf_id and study_event_definition_id = se.study_event_definition_id )) OR ( 1 = ( select edc.source_data_verification_code from event_definition_crf edc where study_id = s.study_id and crf_id = cv.crf_id and study_event_definition_id = se.study_event_definition_id ) or 2 = ( select edc.source_data_verification_code from event_definition_crf edc where study_id = s.study_id and crf_id = cv.crf_id and study_event_definition_id = se.study_event_definition_id ) )))) ";
	static String NON_SDVD_STUDY_SUBJECTS_oracle = " AND ( 0 = (select count(ec.event_crf_id) from event_crf ec, study_event se, study_subject ss,crf_version cv,study s where ec.study_event_id = se.study_event_id AND ss.study_subject_id = se.study_subject_id AND ec.crf_version_id = cv.crf_version_id AND ss.study_id = s.study_id AND se.subject_event_status_id = 4 AND ss.study_subject_id = mss.study_subject_id ) OR 0 < (select count(ec.event_crf_id) from event_crf ec, study_event se, study_subject ss,crf_version cv,study s where ec.study_event_id = se.study_event_id AND ss.study_subject_id = se.study_subject_id AND ec.crf_version_id = cv.crf_version_id AND se.subject_event_status_id = 4 AND ss.study_id = s.study_id AND ec.sdv_status = 0 AND ss.study_subject_id = mss.study_subject_id AND (  ((1 = ( select edc.source_data_verification_code from event_definition_crf edc where study_id = s.parent_study_id and crf_id = cv.crf_id and study_event_definition_id = se.study_event_definition_id ) OR 2 = ( select edc.source_data_verification_code from event_definition_crf edc where study_id = s.parent_study_id and crf_id = cv.crf_id and study_event_definition_id = se.study_event_definition_id )) AND 0 = ( select count(edc.source_data_verification_code) from event_definition_crf edc where study_id = s.study_id and crf_id = cv.crf_id and study_event_definition_id = se.study_event_definition_id )) OR ( 1 = ( select edc.source_data_verification_code from event_definition_crf edc where study_id = s.study_id and crf_id = cv.crf_id and study_event_definition_id = se.study_event_definition_id ) or 2 = ( select edc.source_data_verification_code from event_definition_crf edc where study_id = s.study_id and crf_id = cv.crf_id and study_event_definition_id = se.study_event_definition_id ) )))) ";
	static String SDVD_STUDY_SUBJECTS_oracle = " AND ( 0 < (select count(ec.event_crf_id) from event_crf ec, study_event se, study_subject ss,crf_version cv,study s where ec.study_event_id = se.study_event_id AND ss.study_subject_id = se.study_subject_id AND ec.crf_version_id = cv.crf_version_id AND ss.study_id = s.study_id AND se.subject_event_status_id = 4 AND ss.study_subject_id = mss.study_subject_id ) AND 0 = (select count(ec.event_crf_id) from event_crf ec, study_event se, study_subject ss,crf_version cv,study s where ec.study_event_id = se.study_event_id AND ss.study_subject_id = se.study_subject_id AND ec.crf_version_id = cv.crf_version_id AND se.subject_event_status_id = 4 AND ss.study_id = s.study_id AND ec.sdv_status = 0 AND ss.study_subject_id = mss.study_subject_id AND (  ((1 = ( select edc.source_data_verification_code from event_definition_crf edc where study_id = s.parent_study_id and crf_id = cv.crf_id and study_event_definition_id = se.study_event_definition_id ) OR 2 = ( select edc.source_data_verification_code from event_definition_crf edc where study_id = s.parent_study_id and crf_id = cv.crf_id and study_event_definition_id = se.study_event_definition_id )) AND 0 = ( select count(edc.source_data_verification_code) from event_definition_crf edc where study_id = s.study_id and crf_id = cv.crf_id and study_event_definition_id = se.study_event_definition_id )) OR ( 1 = ( select edc.source_data_verification_code from event_definition_crf edc where study_id = s.study_id and crf_id = cv.crf_id and study_event_definition_id = se.study_event_definition_id ) or 2 = ( select edc.source_data_verification_code from event_definition_crf edc where study_id = s.study_id and crf_id = cv.crf_id and study_event_definition_id = se.study_event_definition_id ) )))) ";

	public StudySubjectSDVFilter() {
		columnMapping.put("sdvStatus", "");
		columnMapping.put("studySubjectId", "mss.label");
		columnMapping.put("siteId", "mst.unique_identifier");

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
			if (property.equals("sdvStatus")) {
				if ("oracle".equalsIgnoreCase(CoreResources.getDBName())) {
					if (value.equals("complete")) {
						criteria += SDVD_STUDY_SUBJECTS_oracle;
					} else {
						criteria += NON_SDVD_STUDY_SUBJECTS_oracle;
					}
				} else {
					if (value.equals("complete")) {
						criteria += SDVD_STUDY_SUBJECTS;
					} else {
						criteria += NON_SDVD_STUDY_SUBJECTS;
					}
				}
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
