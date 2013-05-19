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

package org.akaza.openclinica.dao.hibernate;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import java.util.ArrayList;
import java.util.List;

public class AuditUserLoginSort implements CriteriaCommand {
	List<Sort> sorts = new ArrayList<Sort>();

	public void addSort(String property, String order) {
		sorts.add(new Sort(property, order));
	}

	public List<Sort> getSorts() {
		return sorts;
	}

	public Criteria execute(Criteria criteria) {
		for (Sort sort : sorts) {
			buildCriteria(criteria, sort.getProperty(), sort.getOrder());
		}

		return criteria;
	}

	private void buildCriteria(Criteria criteria, String property, String order) {
		if (order.equals(Sort.ASC)) {
			criteria.addOrder(Order.asc(property));
		} else if (order.equals(Sort.DESC)) {
			criteria.addOrder(Order.desc(property));
		}
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
