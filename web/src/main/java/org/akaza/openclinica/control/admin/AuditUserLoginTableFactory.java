/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2014 Clinovo Inc.
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

package org.akaza.openclinica.control.admin;

import com.clinovo.util.DateUtil;
import org.akaza.openclinica.control.AbstractTableFactory;
import org.akaza.openclinica.control.DefaultActionsEditor;
import org.akaza.openclinica.dao.hibernate.AuditUserLoginDao;
import org.akaza.openclinica.dao.hibernate.AuditUserLoginFilter;
import org.akaza.openclinica.dao.hibernate.AuditUserLoginSort;
import org.akaza.openclinica.domain.technicaladmin.AuditUserLoginBean;
import org.akaza.openclinica.domain.technicaladmin.LoginStatus;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.apache.commons.lang.StringUtils;
import org.jmesa.core.filter.DateFilterMatcher;
import org.jmesa.core.filter.FilterMatcher;
import org.jmesa.core.filter.MatcherKey;
import org.jmesa.facade.TableFacade;
import org.jmesa.limit.Filter;
import org.jmesa.limit.FilterSet;
import org.jmesa.limit.Limit;
import org.jmesa.limit.Sort;
import org.jmesa.limit.SortSet;
import org.jmesa.view.component.Row;
import org.jmesa.view.editor.BasicCellEditor;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.editor.DateCellEditor;
import org.jmesa.view.html.editor.DroplistFilterEditor;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Encapsulates all the functionality required to create tables for user login audit.
 */
public class AuditUserLoginTableFactory extends AbstractTableFactory {

	private AuditUserLoginDao auditUserLoginDao;

	@Override
	protected String getTableName() {
		return "userLogins";
	}

	@Override
	protected void configureColumns(TableFacade tableFacade, Locale locale) {
		tableFacade.setColumnProperties("userName", "loginAttemptDate", "loginStatus", "actions");
		Row row = tableFacade.getTable().getRow();
		configureColumn(row.getColumn("userName"), ResourceBundleProvider.getResWord("user_name"), null, null);
		configureColumn(row.getColumn("loginAttemptDate"), ResourceBundleProvider.getResWord("login_attempt_date"),
				new DateEditor(DateUtil.DatePattern.TIMESTAMP_WITH_SECONDS, getCurrentUser().getUserTimeZoneId()), null);
		configureColumn(row.getColumn("loginStatus"), ResourceBundleProvider.getResWord("login_status"), null,
				new AvailableDroplistFilterEditor());
		String actionsHeader = ResourceBundleProvider.getResWord("actions")
				+ "&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;";
		configureColumn(row.getColumn("actions"), actionsHeader, new ActionsCellEditor(), new DefaultActionsEditor(
				locale), true, false);

	}

	@Override
	protected void configureExportColumns(TableFacade tableFacade, Locale locale) {
		tableFacade.setColumnProperties("userName", "loginAttemptDate", "loginStatus");
		Row row = tableFacade.getTable().getRow();
		configureColumn(row.getColumn("userName"), ResourceBundleProvider.getResWord("user_name"), null, null);
		configureColumn(row.getColumn("loginAttemptDate"), ResourceBundleProvider.getResWord("login_attempt_date"), new DateCellEditor(
				"yyyy-MM-dd HH:mm:ss"), null);
		configureColumn(row.getColumn("loginStatus"), ResourceBundleProvider.getResWord("login_status"), null, new AvailableDroplistFilterEditor());
	}

	@Override
	public void configureTableFacade(HttpServletResponse response, TableFacade tableFacade) {
		super.configureTableFacade(response, tableFacade);
		tableFacade.addFilterMatcher(new MatcherKey(String.class, "loginStatus"), new AvailableFilterMatcher());
		tableFacade.addFilterMatcher(new MatcherKey(Date.class, "loginAttemptDate"), new DateFilterMatcher(
				"yyyy-MM-dd HH:mm"));
	}

	@Override
	public int getSize(Limit limit) {
		return getAuditUserLoginDao().getCountWithFilter(new AuditUserLoginFilter());
	}

	@Override
	public void setDataAndLimitVariables(TableFacade tableFacade) {

		Limit limit = tableFacade.getLimit();
		AuditUserLoginFilter auditUserLoginFilter = getAuditUserLoginFilter(limit);

		/*
		 * Because we are using the State feature (via stateAttr) we can do a check to see if we have a complete limit
		 * already. See the State feature for more details Very important to set the totalRow before trying to get the
		 * row start and row end variables. Very important to set the totalRow before trying to get the row start and
		 * row end variables.
		 */
		if (!limit.isComplete()) {
			int totalRows = getAuditUserLoginDao().getCountWithFilter(auditUserLoginFilter);
			tableFacade.setTotalRows(totalRows);
		}

		AuditUserLoginSort auditUserLoginSort = getAuditUserLoginSort(limit);
		if (auditUserLoginSort.getSorts().size() == 0) {
			auditUserLoginSort.addSort("loginAttemptDate", "desc");
		}
		int rowStart = limit.getRowSelect().getRowStart();
		int rowEnd = limit.getRowSelect().getRowEnd();
		Collection<AuditUserLoginBean> items = getAuditUserLoginDao().getWithFilterAndSort(auditUserLoginFilter,
				auditUserLoginSort, rowStart, rowEnd);
		tableFacade.setItems(items);
	}

	/**
	 * A very custom way to filter the items. The AuditUserLoginFilter acts as a command for the Hibernate criteria
	 * object. Take the Limit information and filter the rows.
	 * 
	 * @param limit
	 *            The Limit to use.
	 */
	protected AuditUserLoginFilter getAuditUserLoginFilter(Limit limit) {
		AuditUserLoginFilter auditUserLoginFilter = new AuditUserLoginFilter();
		FilterSet filterSet = limit.getFilterSet();
		if (filterSet != null) {
			Collection<Filter> filters = filterSet.getFilters();
			for (Filter filter : filters) {
				String property = filter.getProperty();
				String value = filter.getValue();
				auditUserLoginFilter.addFilter(property, value);
			}
		}

		return auditUserLoginFilter;
	}

	/**
	 * A very custom way to sort the items. The AuditUserLoginSort acts as a command for the Hibernate criteria object.
	 * Take the Limit information and sort the rows.
	 * 
	 * @param limit
	 *            The Limit to use.
	 */
	protected AuditUserLoginSort getAuditUserLoginSort(Limit limit) {
		AuditUserLoginSort auditUserLoginSort = new AuditUserLoginSort();
		SortSet sortSet = limit.getSortSet();
		if (sortSet != null) {
			Collection<Sort> sorts = sortSet.getSorts();
			for (Sort sort : sorts) {
				String property = sort.getProperty();
				String order = sort.getOrder().toParam();
				auditUserLoginSort.addSort(property, order);
			}
		}

		return auditUserLoginSort;
	}

	public AuditUserLoginDao getAuditUserLoginDao() {
		return auditUserLoginDao;
	}

	public void setAuditUserLoginDao(AuditUserLoginDao auditUserLoginDao) {
		this.auditUserLoginDao = auditUserLoginDao;
	}

	private class AvailableDroplistFilterEditor extends DroplistFilterEditor {
		@Override
		protected List<Option> getOptions() {
			List<Option> options = new ArrayList<Option>();
			for (LoginStatus loginStatus : LoginStatus.values()) {
				options.add(new Option(loginStatus.toString(), loginStatus.toString()));
			}
			return options;
		}
	}

	private class AvailableFilterMatcher implements FilterMatcher {
		public boolean evaluate(Object itemValue, String filterValue) {
			String item = StringUtils.lowerCase(String.valueOf(itemValue));
			String filter = StringUtils.lowerCase(String.valueOf(filterValue)).replaceAll("\\+", " ");
			if (filter.equals(item)) {
				return true;
			}
			return false;
		}
	}

	private class ActionsCellEditor implements CellEditor {
		public Object getValue(Object item, String property, int rowcount) {
			BasicCellEditor bce = new BasicCellEditor();
			String value = "";
			Integer userAccountId = (Integer) bce.getValue(item, "userAccountId", rowcount);
			String loginAttemptDate = bce.getValue(item, "loginAttemptDate", rowcount).toString();
			if (userAccountId != null) {
				StringBuilder url = new StringBuilder();
				url.append("<a onmouseup=\"javascript:setImage('bt_View1','images/bt_View.gif');\" onmousedown=\"javascript:setImage('bt_View1','images/bt_View_d.gif');\" href=\"ViewUserAccount?userId=");
				url.append(userAccountId.toString());
				url.append("&amp;viewFull=yes\" onclick=\"setAccessedObjected(this)\" data-cc-auditUserId=\"");
				url.append(loginAttemptDate);
				url.append("\"><img hspace=\"6\" border=\"0\" align=\"left\" title=\"View\" alt=\"View\" src=\"images/bt_View.gif\" name=\"bt_View1\"/></a>");
				value = url.toString();
			}
			return value;
		}

	}

}
