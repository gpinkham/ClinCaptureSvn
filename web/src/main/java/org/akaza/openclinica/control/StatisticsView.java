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

import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.jmesa.view.component.Column;
import org.jmesa.view.editor.CellEditor;
import org.jmesa.view.editor.GroupCellEditor;
import org.jmesa.view.html.AbstractHtmlView;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.HtmlSnippets;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class StatisticsView extends AbstractHtmlView {

	private final ResourceBundle resword;

	public StatisticsView(Locale locale) {
		resword = ResourceBundleProvider.getWordsBundle(locale);
	}

	public Object render() {
		HtmlSnippets snippets = getHtmlSnippets();
		HtmlBuilder html = new HtmlBuilder();
		setCustomCellEditors();
		html.append(snippets.themeStart());

		html.append(snippets.tableStart());

		html.append(snippets.theadStart());
		html.append(customHeader());
		html.append(snippets.filter());
		html.append(snippets.header());
		html.append(snippets.theadEnd());
		html.append(snippets.tbodyStart());
		html.append(snippets.body());
		html.append(snippets.tbodyEnd());
		html.append(snippets.footer());
		html.append(snippets.tableEnd());
		html.append(snippets.themeEnd());
		html.append(snippets.initJavascriptLimit());

		return html.toString();
	}

	/**
	 * Setting the group cell editor.
	 */
	private void setCustomCellEditors() {
		List<Column> columns = getTable().getRow().getColumns();
		for (Column column : columns) {
			CellEditor decoratedCellEditor = column.getCellRenderer().getCellEditor();

			column.getCellRenderer().setCellEditor(new GroupCellEditor(decoratedCellEditor));
		}

	}

	private String customHeader() {
		HtmlBuilder html = new HtmlBuilder();

		html.thead(0).tr(0).styleClass("header").close();
		html.td(0).colspan("4").style("border-bottom: 1px solid white;background-color:white;color:grey;")
				.align("center").close().append(resword.getString("subject_enrollment")).tdEnd();

		html.theadEnd(0);
		return html.toString();
	}

}
