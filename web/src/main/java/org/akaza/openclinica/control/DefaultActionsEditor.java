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
import org.jmesa.view.editor.AbstractFilterEditor;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.toolbar.AbstractItem;
import org.jmesa.view.html.toolbar.ClearItemRenderer;
import org.jmesa.view.html.toolbar.FilterItemRenderer;
import org.jmesa.view.html.toolbar.ToolbarItem;
import org.jmesa.view.html.toolbar.ToolbarItemRenderer;
import org.jmesa.view.html.toolbar.ToolbarItemType;

import java.util.Locale;
import java.util.ResourceBundle;

public class DefaultActionsEditor extends AbstractFilterEditor {

	private ResourceBundle resword;

	public DefaultActionsEditor() {
		// TODO Auto-generated constructor stub
	}

	public DefaultActionsEditor(Locale locale) {
		resword = ResourceBundleProvider.getWordsBundle(locale);
	}

	public Object getValue() {
		HtmlBuilder html = new HtmlBuilder();

		html.append(createFilterItem().getToolbarItemRenderer().render());
		html.append(" ");
		html.append(createResetFilterItem().getToolbarItemRenderer().render());

		return html.toString();
	}

	public ToolbarItem createFilterItem() {

		FilterItem item = new FilterItem();
		item.setCode(ToolbarItemType.FILTER_ITEM.toCode());
		ToolbarItemRenderer renderer = new FilterItemRenderer(item, getCoreContext());
		renderer.setOnInvokeAction("onInvokeAction");
		item.setToolbarItemRenderer(renderer);

		return item;
	}

	public ToolbarItem createResetFilterItem() {

		ResetFilterItem item = new ResetFilterItem();
		item.setCode(ToolbarItemType.CLEAR_ITEM.toCode());
		ToolbarItemRenderer renderer = new ClearItemRenderer(item, getCoreContext());
		renderer.setOnInvokeAction("onInvokeAction");
		item.setToolbarItemRenderer(renderer);

		return item;
	}

	private class FilterItem extends AbstractItem {

		@Override
		public String disabled() {
			return null;
		}

		@Override
		public String enabled() {
			HtmlBuilder html = new HtmlBuilder();
			html.a().href();
			html.quote();
			html.append(getAction());
			html.quote().close();
			html.nbsp().append(resword.getString("table_apply_filter")).nbsp().aEnd();

			return html.toString();

		}

	}

	private class ResetFilterItem extends AbstractItem {

		@Override
		public String disabled() {
			return null;
		}

		@Override
		public String enabled() {
			HtmlBuilder html = new HtmlBuilder();
			html.a().href();
			html.quote();
			html.append(getAction());
			html.quote().close();
			html.nbsp().append(resword.getString("table_clear_filter")).nbsp().aEnd();

			return html.toString();
		}
	}

}
