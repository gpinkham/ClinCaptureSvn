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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListDiscNotesSubjectSort implements CriteriaCommand {
	List<Sort> sorts = new ArrayList<Sort>();
	HashMap<String, String> columnMapping = new HashMap<String, String>();

	public ListDiscNotesSubjectSort() {
		columnMapping.put("studySubject.label", "ss.label");
		columnMapping.put("studySubject.status", "ss.status_id");
		columnMapping.put("studySubject.oid", "ss.oc_oid");
		columnMapping.put("studySubject.secondaryLabel", "ss.secondary_label");
		columnMapping.put("subject.charGender", "s.gender");
		columnMapping.put("enrolledAt", "ST.unique_identifier");

	}

	public void addSort(String property, String order) {
		sorts.add(new Sort(property, order));
	}

	public List<Sort> getSorts() {
		return sorts;
	}

	public String execute(String criteria) {
		String theCriteria = "";
		for (Sort sort : sorts) {
			if (theCriteria.length() == 0) {
				theCriteria += buildCriteriaInitial(criteria, sort.getProperty(), sort.getOrder());
			} else {
				theCriteria += buildCriteria(criteria, sort.getProperty(), sort.getOrder());
			}

		}

		return theCriteria;
	}

	private String buildCriteriaInitial(String criteria, String property, String order) {
		if (order.equals(Sort.ASC)) {
			criteria = criteria + " order by " + columnMapping.get(property) + " asc ";
		} else if (order.equals(Sort.DESC)) {
			criteria = criteria + " order by " + columnMapping.get(property) + " desc ";
		}
		return criteria;
	}

	private String buildCriteria(String criteria, String property, String order) {
		if (order.equals(Sort.ASC)) {
			criteria = criteria + " , " + columnMapping.get(property) + " asc ";
		} else if (order.equals(Sort.DESC)) {
			criteria = criteria + " , " + columnMapping.get(property) + " desc ";
		}
		return criteria;
	}

	private static class Sort {
		public final static String ASC = "asc";
		public final static String DESC = "desc";

		private final String property;
		private final String order;

		public Sort(String property, String order) {
			this.property = property;
			this.order = order;
		}

		public String getProperty() {
			return property;
		}

		public String getOrder() {
			return order;
		}
	}
}
