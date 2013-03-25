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
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.akaza.openclinica.control;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.HtmlSnippets;

public class ListStudyView extends DefaultView {

	private final ResourceBundle resword;
	private boolean showTitle = false;

	public ListStudyView(Locale locale, HttpServletRequest request) {
		resword = ResourceBundleProvider.getWordsBundle(locale);
		if (request.getRequestURI().contains("MainMenu"))
			showTitle = true;

	}

	public Object render() {
		HtmlSnippets snippets = getHtmlSnippets();
		HtmlBuilder html = new HtmlBuilder();

		html.append(snippets.themeStart());
		html.append(snippets.tableStart());

		html.append(snippets.theadStart());

		html.append(customHeader());
		html.append(snippets.toolbar());
		html.append(snippets.header());
		html.append(snippets.filter());
		html.append(snippets.theadEnd());
		html.append(snippets.tbodyStart());
		setCustomCellEditors();
		html.append(snippets.body());
		html.append(snippets.tbodyEnd());
		html.append(snippets.footer());
		html.append(snippets.statusBar());
		html.append(snippets.tableEnd());
		html.append(snippets.themeEnd());
		html.append(snippets.initJavascriptLimit());
		return html.toString();
	}

	/**
	 * Setting the group cell editor.
	 */
	private void setCustomCellEditors() {
		getTable().setCaption("Subject Enrollment");

	}

	private String customHeader() {

		HtmlBuilder html = new HtmlBuilder();

		html.tr(1).styleClass("header").width("100%").close();
		if (showTitle)
			html.td(0).style("border-bottom: 1px solid white;background-color:white;color:black;").align("center")
					.close().append(resword.getString("subject_matrix")).tdEnd();

		html.trEnd(1);

		return html.toString();
	}

}
