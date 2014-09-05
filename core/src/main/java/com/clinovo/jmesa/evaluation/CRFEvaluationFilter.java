package com.clinovo.jmesa.evaluation;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.managestudy.CriteriaCommand;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CRFEvaluationFilter class.
 */
public class CRFEvaluationFilter implements CriteriaCommand {

	public static final String CRF_NAME = "crfName";
	public static final String EVENT_NAME = "eventName";
	public static final String CRF_STATUS = "crfStatus";
	public static final String STUDY_SUBJECT_ID = "studySubjectId";

	public static final String C_NAME = "c.name";
	public static final String SS_LABEL = "ss.label";
	public static final String SED_NAME = "sed.name";
	public static final String EC_STATUS_ID = "ec.status_id";

	private Map<Object, Status> optionsMap;
	private List<Filter> filters = new ArrayList<Filter>();
	private HashMap<String, String> columnMapping = new HashMap<String, String>();

	/**
	 * CRFEvaluationFilter constructor.
	 * 
	 * @param optionsMap
	 *            Map<String, Status>
	 */
	public CRFEvaluationFilter(Map<Object, Status> optionsMap) {
		this.optionsMap = optionsMap;
		columnMapping.put(CRF_NAME, C_NAME);
		columnMapping.put(EVENT_NAME, SED_NAME);
		columnMapping.put(CRF_STATUS, EC_STATUS_ID);
		columnMapping.put(STUDY_SUBJECT_ID, SS_LABEL);
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
			theCriteria = theCriteria.concat(buildCriteria(criteria, filter.getProperty(), filter.getValue()));
		}
		return theCriteria;
	}

	private String buildCriteria(String criteria, String property, Object value) {
		StringBuilder theCriteria = new StringBuilder("");
		value = StringEscapeUtils.escapeSql(value.toString());
		if (value != null) {
			if (property.equals(CRF_STATUS)) {
				Status status = optionsMap.get(value);
				if (status.equals(Status.AUTO_DELETED) || status.equals(Status.DELETED)) {
					theCriteria.append(" AND ").append(columnMapping.get(property)).append(" in (5,7) ");
				} else if (status.equals(Status.LOCKED)) {
					theCriteria.append(" AND (").append(columnMapping.get(property))
							.append(" in (6) or se.subject_event_status_id in (5,6,7)) ");
				} else if (status.equals(Status.SIGNED)) {
					theCriteria.append(" AND (").append(columnMapping.get(property))
							.append(" = 2 and se.subject_event_status_id = 8) ");
				} else if (status.equals(Status.SOURCE_DATA_VERIFIED)) {
					theCriteria.append(" AND (").append(columnMapping.get(property))
							.append(" = 2 and ec.sdv_status = ")
							.append(CoreResources.getDBType().equalsIgnoreCase("oracle") ? "1" : "true")
							.append(" and se.subject_event_status_id != 8) ");
				} else if (status.equals(Status.COMPLETED)) {
					theCriteria.append(" AND (").append(columnMapping.get(property))
							.append(" = 2 and ec.sdv_status = ")
							.append(CoreResources.getDBType().equalsIgnoreCase("oracle") ? "0" : "false")
							.append(" and se.subject_event_status_id != 8) ");
				} else if (status.equals(Status.DATA_ENTRY_STARTED)) {
					theCriteria.append(" AND (").append(columnMapping.get(property))
							.append(" in (1,4) and se.subject_event_status_id in (1,3,4,8,9)) ");
				}
			} else {
				theCriteria.append(" AND UPPER(").append(columnMapping.get(property)).append(") like UPPER('%")
						.append(value.toString()).append("%')");
			}
		}
		return criteria.concat(theCriteria.toString());
	}

	private class Filter {
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