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

package org.akaza.openclinica.web.table.scheduledjobs;

import com.clinovo.util.SessionUtil;
import org.akaza.openclinica.control.AbstractTableFactory;
import org.akaza.openclinica.control.DefaultActionsEditor;
import org.akaza.openclinica.dao.ScheduledJobSort;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.web.table.sdv.SDVUtil;
import org.jmesa.facade.TableFacade;
import org.jmesa.limit.Limit;
import org.jmesa.limit.Sort;
import org.jmesa.limit.SortSet;
import org.jmesa.view.component.Row;
import org.jmesa.view.html.AbstractHtmlView;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.HtmlSnippets;
import org.jmesa.view.html.component.HtmlTable;
import org.jmesa.web.WebContext;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * View builder for the list of scheduled jobs with an ability to cancel the job
 * 
 * @author jnyayapathi
 * 
 */
@Component
@SuppressWarnings({ "unchecked" })
public class ScheduledJobTableFactory extends AbstractTableFactory {
	@Override
	protected String getTableName() {
		return "scheduledJobs";
	}

	@Override
	public void configureTableFacadeCustomView(TableFacade tableFacade) {
		tableFacade.setView(new ScheduledJobView(getLocale()));
	}

	@Override
	protected void configureColumns(TableFacade tableFacade, Locale locale) {
		tableFacade.setColumnProperties("datasetId", "fireTime", "exportFileName", "jobStatus", "action");
		ResourceBundleProvider.getWordsBundle(locale);
		Row row = tableFacade.getTable().getRow();

		String[] allTitles = new String[] { "DataSet Name", "Fire Time", "Export File", "Job Status", "Actions" };
		SDVUtil sdvUtil = new SDVUtil();// TODO check if this is viable
		sdvUtil.setTitles(allTitles, (HtmlTable) tableFacade.getTable());

		sdvUtil.setHtmlCellEditors(tableFacade, new String[] { "action" }, false);

		configureColumn(row.getColumn("action"), "Actions", sdvUtil.getCellEditorNoEscapes(), new DefaultActionsEditor(
				locale), true, false);

	}

	/**
	 * Creating table
	 */
	@Override
	public TableFacade createTable(HttpServletRequest request, HttpServletResponse response) {
		locale = SessionUtil.getLocale(request);
		TableFacade tableFacade = getTableFacadeImpl(request, response);
		tableFacade.setStateAttr("restore");
		int maxJobs = (Integer) request.getAttribute("totalJobs");
		tableFacade.setTotalRows(maxJobs);
		List<ScheduledJobs> jobs = (List<ScheduledJobs>) request.getAttribute("jobs");

		tableFacade.setItems(jobs);
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

	public void setDataAndLimitVariables(TableFacade tableFacade, List<ScheduledJobs> jobs) {
		WebContext context = tableFacade.getWebContext();
		if (context != null) {
			Integer.parseInt(context.getParameter("studyId"));
			context.getContextPath();
		}
	}

	protected ScheduledJobSort getScheduledJobSort(Limit limit) {
		ScheduledJobSort scheduledJobSort = new ScheduledJobSort();
		SortSet sortSet = limit.getSortSet();
		Collection<Sort> sorts = sortSet.getSorts();
		for (Sort sort : sorts) {
			String property = sort.getProperty();
			String order = sort.getOrder().toParam();
			scheduledJobSort.addSort(property, order);
		}

		return scheduledJobSort;
	}

	class ScheduledJobView extends AbstractHtmlView {

		private final ResourceBundle resword;

		public ScheduledJobView(Locale locale) {
			resword = ResourceBundleProvider.getWordsBundle(locale);
		}

		public Object render() {
			HtmlSnippets snippets = getHtmlSnippets();
			HtmlBuilder html = new HtmlBuilder();
			html.append(snippets.themeStart());
			html.append(snippets.tableStart());
			html.append(snippets.theadStart());
			html.append(snippets.toolbar());
			// html.append(selectAll()); Not required, not selecting all the jobs
			html.append(snippets.header());
			html.append(snippets.filter());
			html.append(snippets.theadEnd());
			html.append(snippets.tbodyStart());
			html.append(snippets.body());
			html.append(snippets.tbodyEnd());
			html.append(snippets.footer());
			html.append(snippets.statusBar());
			html.append(snippets.tableEnd());
			html.append(snippets.themeEnd());
			html.append(snippets.initJavascriptLimit());
			return html.toString();
		}

		String selectAll() {
			HtmlBuilder html = new HtmlBuilder();
			html.tr(1).styleClass("logic").close().td(1).colspan("100%").style("font-size: 12px;").close();
			html.append("<b>" + resword.getString("table_sdv_select") + "</b>&#160;&#160;");
			html.append("<a name='checkSDVAll' href='javascript:selectAllChecks(document.scheduledJobsForm,true)'>"
					+ resword.getString("table_sdv_all"));
			html.append(",</a>");
			html.append("&#160;&#160;&#160;");
			html.append("<a name='checkSDVAll' href='javascript:selectAllChecks(document.scheduledJobsForm,false)'>"
					+ resword.getString("table_sdv_none"));
			html.append("</a>");
			html.tdEnd().trEnd(1);
			return html.toString();
		}
	}

	@Override
	public void setDataAndLimitVariables(TableFacade tableFacade) {
		// TODO Auto-generated method stub

	}

}
