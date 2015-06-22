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

package org.akaza.openclinica.control;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.jmesa.facade.TableFacade;
import org.jmesa.facade.TableFacadeImpl;
import org.jmesa.limit.ExportType;
import org.jmesa.limit.Limit;
import org.jmesa.limit.LimitImpl;
import org.jmesa.limit.RowSelect;
import org.jmesa.limit.RowSelectImpl;
import org.jmesa.util.ItemUtils;
import org.jmesa.view.component.Column;
import org.jmesa.view.editor.AbstractCellEditor;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.editor.FilterEditor;
import org.jmesa.view.html.component.HtmlColumn;
import org.jmesa.view.html.component.HtmlTable;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.util.DateUtil;

/**
 * AbstractTableFactory.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class AbstractTableFactory {

	protected Locale locale;

	protected Logger logger = LoggerFactory.getLogger(getClass().getName());

	private UserAccountBean currentUser;

	private HttpServletRequest request;

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public UserAccountBean getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(UserAccountBean currentUser) {
		this.currentUser = currentUser;
	}

	protected abstract String getTableName();

	protected String getCaptionName() {
		return "";
	}

	protected abstract void configureColumns(TableFacade tableFacade, Locale locale);

	protected void configureExportColumns(TableFacade tableFacade, Locale locale) {
		configureColumns(tableFacade, locale);
	}

	/**
	 * Returns TableFacade.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @return TableFacade
	 */
	public TableFacade getTableFacadeImpl(HttpServletRequest request, HttpServletResponse response) {
		return new TableFacadeImpl(getTableName(), request);
	}

	/**
	 * Sets DataAndLimitVariables.
	 * 
	 * @param tableFacade
	 *            TableFacade
	 */
	public abstract void setDataAndLimitVariables(TableFacade tableFacade);

	/**
	 * Creates TableFacade.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @return TableFacade
	 */
	public TableFacade createTable(HttpServletRequest request, HttpServletResponse response) {
		locale = LocaleResolver.getLocale(request);
		setRequest(request);
		TableFacade tableFacade = getTableFacadeImpl(request, response);
		setStateAttr(tableFacade);
		setDataAndLimitVariables(tableFacade);
		configureTableFacade(response, tableFacade);
		if (!tableFacade.getLimit().isExported()) {
			configureColumns(tableFacade, locale);
			tableFacade.setMaxRowsIncrements(getMaxRowIncrements());
			configureTableFacadePostColumnConfiguration(tableFacade);
			configureTableFacadeCustomView(tableFacade);
			configureUnexportedTable(tableFacade, locale);
		} else {
			configureExportColumns(tableFacade, locale);
		}
		return tableFacade;
	}

	/**
	 * Use this method to export all data from table. 1. filters/sorts will be ignored 2. Whole table will be exported
	 * page by page 3. Configure getSize(Limit limit)
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param path
	 *            String
	 */
	public void exportCSVTable(HttpServletRequest request, HttpServletResponse response, String path) {
		locale = LocaleResolver.getLocale(request);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String fileName = getTableName() + "_" + sdf.format(new Date());

		for (Limit limit : createLimits()) {
			TableFacade tableFacade = new OCTableFacadeImpl(getTableName(), request, response, path + File.separator
					+ fileName);
			tableFacade.setStateAttr("restore");
			tableFacade.setLimit(limit);
			tableFacade.autoFilterAndSort(false);
			setDataAndLimitVariables(tableFacade);
			configureTableFacade(response, tableFacade);
			configureExportColumns(tableFacade, locale);
			tableFacade.render();
		}
	}

	private ArrayList<Limit> createLimits() {
		Limit limit = new LimitImpl(getTableName());
		ArrayList<Limit> limits = new ArrayList<Limit>();
		int size = getSize(limit);
		for (RowSelect rowSelect : getRowSelects(size)) {
			Limit theLimit = new LimitImpl(getTableName());
			theLimit.setRowSelect(rowSelect);
			theLimit.setExportType(ExportType.CSV);
			limits.add(theLimit);
		}
		return limits;
	}

	private ArrayList<RowSelect> getRowSelects(int size) {
		ArrayList<RowSelect> rowSelects = new ArrayList<RowSelect>();
		int i = 0;
		for (i = 0; i < size / 50; i++) {
			RowSelect rowSelect = new RowSelectImpl(i + 1, 50, size);
			rowSelects.add(rowSelect);
		}
		if (size % 50 > 0) {
			RowSelect rowSelect = new RowSelectImpl(i + 1, size % 50, size);
			rowSelects.add(rowSelect);
		}
		return rowSelects;
	}

	/**
	 * Returns size.
	 * 
	 * @param limit
	 *            Limit
	 * @return int
	 */
	public int getSize(Limit limit) {
		return 0;
	}

	/**
	 * Configures TableFacade.
	 * 
	 * @param response
	 *            HttpServletResponse
	 * @param tableFacade
	 *            TableFacade
	 */
	public void configureTableFacade(HttpServletResponse response, TableFacade tableFacade) {
		tableFacade.setExportTypes(response, getExportTypes());
	}

	public int[] getMaxRowIncrements() {
		return new int[]{15, 25, 50};
	}

	/**
	 * By Default we configure a default toolbar. Overwrite this method if you need to provide a custom toolbar and
	 * configure other options.
	 * 
	 * @param tableFacade
	 *            TableFacade
	 */
	public void configureTableFacadePostColumnConfiguration(TableFacade tableFacade) {
		tableFacade.setToolbar(new DefaultToolbar());
	}

	/**
	 * By Default we configure a default view. Overwrite this method if you need to provide a custom view.
	 * 
	 * @param tableFacade
	 *            TableFacade
	 * @see http://code.google.com/p/jmesa/wiki/CustomViewTotalsTutorial
	 */
	public void configureTableFacadeCustomView(TableFacade tableFacade) {
		tableFacade.setView(new DefaultView(locale));
	}

	protected void configureUnexportedTable(TableFacade tableFacade, Locale locale) {
		HtmlTable table = (HtmlTable) tableFacade.getTable();
		table.setCaption(getCaptionName());
	}

	protected ExportType[] getExportTypes() {
		return null;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	protected void configureColumn(Column column, String title, CellEditor editor, FilterEditor filterEditor) {
		configureColumn(column, title, editor, filterEditor, true, true);
	}

	protected void configureColumn(Column column, String title, CellEditor editor, FilterEditor filterEditor,
			boolean filterable, boolean sortable) {
		column.setTitle(title);
		if (editor != null) {
			column.getCellRenderer().setCellEditor(editor);
		}

		if (column instanceof HtmlColumn) {
			HtmlColumn htmlColumn = (HtmlColumn) column;
			htmlColumn.setFilterable(filterable);
			htmlColumn.setSortable(sortable);
			if (filterEditor != null) {
				htmlColumn.getFilterRenderer().setFilterEditor(filterEditor);
			}
		}
	}

	/**
	 * Returns DN Flag icon name.
	 * 
	 * @param dnResolutionStatusId
	 *            int
	 * @return String
	 */
	public static String getDNFlagIconName(int dnResolutionStatusId) {
		String name = "";
		switch (dnResolutionStatusId) {
			case 0 :
				name = "icon_noNote";
				break;
			case 1 :
				name = "icon_Note";
				break;
			case 2 :
				name = "icon_flagYellow";
				break;
			case 3 :
				name = "icon_flagBlack";
				break;
			case 4 :
				name = "icon_flagGreen";
				break;
			case 5 :
				name = "icon_flagWhite";
				break;
			default :
				name = "icon_noNote";
				break;
		}

		return name;
	}

	/**
	 * Returns paginated data.
	 * 
	 * @param list
	 *            ArrayList
	 * @param rowStart
	 *            int
	 * @param rowEnd
	 *            int
	 * @return ArrayList
	 */
	public ArrayList paginateData(ArrayList list, int rowStart, int rowEnd) {
		ArrayList mainList = new ArrayList();
		if (rowStart > 0) {
			rowStart = rowStart + 1;
		}
		for (int i = rowStart; i <= rowEnd; i++) {
			if (i < list.size()) {
				mainList.add(list.get(i));
			} else {
				break;
			}

		}
		return mainList;
	}

	/**
	 * Sets state attribute.
	 * 
	 * @param tableFacade
	 *            TableFacade
	 */
	public void setStateAttr(TableFacade tableFacade) {
		if (getTableName() != null) {
			tableFacade.setStateAttr(getTableName() + "_restore");
		} else {
			tableFacade.setStateAttr("restore");
			logger.debug("getTableName() returned null, so tableFacade.setStateAttr = restore");
		}
	}

	protected class DateEditor extends AbstractCellEditor {

		private Logger logger = LoggerFactory.getLogger(DateEditor.class);

		private String userTimeZoneId;

		private DateUtil.DatePattern datePattern;

		public DateEditor() {
			this.userTimeZoneId = DateTimeZone.getDefault().getID();
			this.datePattern = DateUtil.DatePattern.DATE;
		}

		public DateEditor(DateUtil.DatePattern datePattern) {
			this.userTimeZoneId = DateTimeZone.getDefault().getID();
			this.datePattern = datePattern;
		}

		public DateEditor(String userTimeZoneId) {
			setUserTimeZoneId(userTimeZoneId);
			this.datePattern = DateUtil.DatePattern.DATE;
		}

		public DateEditor(DateUtil.DatePattern datePattern, String userTimeZoneId) {
			setUserTimeZoneId(userTimeZoneId);
			this.datePattern = datePattern;
		}

		public void setUserTimeZoneId(String userTimeZoneId) {
			this.userTimeZoneId = DateUtil.isValidTimeZoneId(userTimeZoneId) ? userTimeZoneId : DateTimeZone
					.getDefault().getID();
		}

		public Object getValue(Object item, String property, int rowcount) {

			String output = null;
			try {
				Object itemValue = ItemUtils.getItemValue(item, property);
				if (itemValue == null) {
					return null;
				}
				output = DateUtil.printDate((Date) itemValue, userTimeZoneId, datePattern, getLocale());
			} catch (Exception exception) {
				this.logger.error("Could not process date editor with property " + property, exception);
			}
			return output;
		}
	}
}
