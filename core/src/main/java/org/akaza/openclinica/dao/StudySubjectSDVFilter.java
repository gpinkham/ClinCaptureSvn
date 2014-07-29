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

import org.akaza.openclinica.dao.managestudy.CriteriaCommand;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * StudySubjectSDVFilter class.
 */
public class StudySubjectSDVFilter implements CriteriaCommand {

	private StudySubjectDAO studySubjectDAO;
	private List<Filter> filters = new ArrayList<Filter>();
	private HashMap<String, String> columnMapping = new HashMap<String, String>();

	/**
	 * StudySubjectSDVFilter constructor.
	 */
	public StudySubjectSDVFilter() {
		columnMapping.put("sdvStatus", "");
		columnMapping.put("studySubjectId", "mss.label");
		columnMapping.put("siteId", "mst.unique_identifier");
		studySubjectDAO = new StudySubjectDAO(null);
	}

	/**
	 * Method adds a filter.
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
	 * Method executes all filters.
	 *
	 * @param criteria
	 *            String criteria
	 * @return String
	 */
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
				if (value.equals("complete")) {
					criteria += " AND mss.study_subject_id IN (" + studySubjectDAO.getQuery("sdvCompleteFilterForStudySubject") + ") ";
				} else {
					criteria += " AND NOT mss.study_subject_id IN (" + studySubjectDAO.getQuery("sdvCompleteFilterForStudySubject") + ") ";
				}
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
