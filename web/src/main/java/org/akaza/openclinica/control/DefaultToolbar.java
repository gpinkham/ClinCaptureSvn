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

import java.util.ResourceBundle;

import org.jmesa.view.ViewUtils;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.toolbar.AbstractItem;
import org.jmesa.view.html.toolbar.AbstractToolbar;
import org.jmesa.view.html.toolbar.FilterItemRenderer;
import org.jmesa.view.html.toolbar.MaxRowsItem;
import org.jmesa.view.html.toolbar.ToolbarItem;
import org.jmesa.view.html.toolbar.ToolbarItemRenderer;
import org.jmesa.view.html.toolbar.ToolbarItemType;

public class DefaultToolbar extends AbstractToolbar {

	public DefaultToolbar() {
		super();

	}

	public boolean showMoreLink;

	@Override
	public String render() {
		addToolbarItem(ToolbarItemType.FIRST_PAGE_ITEM);
		addToolbarItem(ToolbarItemType.PREV_PAGE_ITEM);
		addToolbarItem(ToolbarItemType.NEXT_PAGE_ITEM);
		addToolbarItem(ToolbarItemType.LAST_PAGE_ITEM);

		addToolbarItem(ToolbarItemType.SEPARATOR);

		MaxRowsItem maxRowsItem = (MaxRowsItem) addToolbarItem(ToolbarItemType.MAX_ROWS_ITEM);
		if (getMaxRowsIncrements() != null) {
			maxRowsItem.setIncrements(getMaxRowsIncrements());
		}

		boolean exportable = ViewUtils.isExportable(getExportTypes());

		if (exportable) {
			addToolbarItem(ToolbarItemType.SEPARATOR);
			addExportToolbarItems(getExportTypes());
		}

		boolean editable = ViewUtils.isEditable(getCoreContext().getWorksheet());

		if (editable) {
			addToolbarItem(ToolbarItemType.SEPARATOR);
			addToolbarItem(ToolbarItemType.SAVE_WORKSHEET_ITEM);
			addToolbarItem(ToolbarItemType.FILTER_WORKSHEET_ITEM);
		}

		addToolbarItems();

		return super.render();
	}

	public class NewHiddenItem extends AbstractItem {

		@Override
		public String disabled() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String enabled() {
			HtmlBuilder html = new HtmlBuilder();
			html.input().id("showMoreLink").type("hidden").name("showMoreLink").value(showMoreLink + "").end();
			return html.toString();
		}

	}
	
	public class ShowMoreItem extends AbstractItem {

		private ResourceBundle resword;
		private String columnIndexes;
		
		public ShowMoreItem(ResourceBundle resword, String columnIndexes) {
			this.resword = resword;
			this.columnIndexes = columnIndexes;
		}
		
		@Override
		public String disabled() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String enabled() {
			HtmlBuilder html = new HtmlBuilder();
			if (showMoreLink) {
				html.a().id("showMore").href().quote().append("javascript: document.getElementById('showMoreLink').value='false'; " + getAction()).quote().close();
				html.div().close().nbsp().append(getResword().getString("show_more")).nbsp().divEnd().aEnd();

				html.script()
						.type("text/javascript")
						.close()
						.append("$(document).ready(function(){ " + "hideCols('listNotes',["
								+ getColumnIndexes() + "],false);});").scriptEnd();
			} else {
				html.a().id("hide").href().quote().append("javascript: document.getElementById('showMoreLink').value='true'; " + getAction()).quote().close();
				html.div().close().nbsp().append(getResword().getString("hide")).nbsp().divEnd().aEnd();
			}
			
			return html.toString();
		}

		public String getColumnIndexes(){
			return columnIndexes;
		}

		public ResourceBundle getResword(){
			return resword;
		}
		
	}
	
	public ToolbarItem createShowMoreLinkItem(ResourceBundle resword, String columnIndexes) {
		ShowMoreItem item = new ShowMoreItem(resword, columnIndexes);
		item.setCode(ToolbarItemType.FILTER_ITEM.toCode());
		ToolbarItemRenderer renderer = new FilterItemRenderer(item, getCoreContext());
		renderer.setOnInvokeAction("onInvokeAction");
		item.setToolbarItemRenderer(renderer);
		return item;
	}

	protected void addToolbarItems() {

	}

}
