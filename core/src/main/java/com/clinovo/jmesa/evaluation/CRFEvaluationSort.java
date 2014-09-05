package com.clinovo.jmesa.evaluation;

import org.akaza.openclinica.dao.managestudy.CriteriaCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * CRFEvaluationSort class.
 */
public class CRFEvaluationSort implements CriteriaCommand {

	private List<Sort> sorts = new ArrayList<Sort>();
	private HashMap<String, String> columnMapping = new HashMap<String, String>();

	/**
	 * CRFEvaluationSort constructor.
	 */
	public CRFEvaluationSort() {
		columnMapping.put("crfName", "c.name");
		columnMapping.put("studySubjectId", "ss.label");
	}

	/**
	 * Method that adds a sort object.
	 *
	 * @param property
	 *            String property
	 * @param order
	 *            String order
	 */
	public void addSort(String property, String order) {
		sorts.add(new Sort(property, order));
	}

	/**
	 * Method that returns list of sort objects.
	 * 
	 * @return List<Sort>
	 */
	public List<Sort> getSorts() {
		return sorts;
	}

	/**
	 * Method that executes all sorts.
	 *
	 * @param criteria
	 *            String criteria
	 * @return String sql string
	 */
	public String execute(String criteria) {
		String theCriteria = "";
		for (Sort sort : sorts) {
			if (theCriteria.length() == 0) {
				theCriteria = theCriteria.concat(buildCriteriaInitial(criteria, sort.getProperty(), sort.getOrder()));
			} else {
				theCriteria = theCriteria.concat(buildCriteria(criteria, sort.getProperty(), sort.getOrder()));
			}

		}

		return theCriteria;
	}

	private String buildCriteriaInitial(String criteria, String property, String order) {
		if (order.equals(Sort.ASC)) {
			criteria = criteria.concat(" order by ").concat(columnMapping.get(property)).concat(" asc ");
		} else if (order.equals(Sort.DESC)) {
			criteria = criteria.concat(" order by ").concat(columnMapping.get(property)).concat(" desc ");
		}
		return criteria;
	}

	private String buildCriteria(String criteria, String property, String order) {
		if (order.equals(Sort.ASC)) {
			criteria = criteria.concat(" , ").concat(columnMapping.get(property)).concat(" asc ");
		} else if (order.equals(Sort.DESC)) {
			criteria = criteria.concat(" , ").concat(columnMapping.get(property)).concat(" desc ");
		}
		return criteria;
	}

	private class Sort {
		public static final String ASC = "asc";
		public static final String DESC = "desc";

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
