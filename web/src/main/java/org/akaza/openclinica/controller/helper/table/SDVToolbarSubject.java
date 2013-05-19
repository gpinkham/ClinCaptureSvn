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

package org.akaza.openclinica.controller.helper.table;

import java.util.ResourceBundle;

import org.akaza.openclinica.control.DefaultToolbar;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.jmesa.core.CoreContext;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.toolbar.AbstractItem;
import org.jmesa.view.html.toolbar.AbstractItemRenderer;
import org.jmesa.view.html.toolbar.ToolbarItem;
import org.jmesa.view.html.toolbar.ToolbarItemRenderer;
import org.jmesa.view.html.toolbar.ToolbarItemType;

public class SDVToolbarSubject extends DefaultToolbar {

	private ResourceBundle reswords = ResourceBundleProvider.getWordsBundle();

	public SDVToolbarSubject(boolean showMoreLink) {
		this.showMoreLink = showMoreLink;
	}

	@Override
	protected void addToolbarItems() {
		addToolbarItem(ToolbarItemType.SEPARATOR);
		addToolbarItem(createCustomItem(new ShowMoreItem()));
		addToolbarItem(createCustomItem(new NewHiddenItem()));
	}

	private ToolbarItem createCustomItem(AbstractItem item) {

		ToolbarItemRenderer renderer = new CustomItemRenderer(item, getCoreContext());
		renderer.setOnInvokeAction("onInvokeAction");
		item.setToolbarItemRenderer(renderer);

		return item;
	}

	private class ShowMoreItem extends AbstractItem {

		@Override
		public String disabled() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String enabled() {
			HtmlBuilder html = new HtmlBuilder();

			if (showMoreLink) {
				html.a().id("showMore").href("javascript:hideCols('s_sdv',[" + getIndexes() + "],true);").close();
				html.div().close().nbsp().append(reswords.getString("show_more")).nbsp().divEnd().aEnd();
				html.a().id("hide").style("display: none;")
						.href("javascript:hideCols('s_sdv',[" + getIndexes() + "],false);").close();
				html.div().close().nbsp().append(reswords.getString("hide")).nbsp().divEnd().aEnd();

				html.script()
						.type("text/javascript")
						.close()
						.append("$j = jQuery.noConflict(); $j(document).ready(function(){ " + "hideCols('s_sdv',["
								+ getIndexes() + "],false);});").scriptEnd();
			} else {
				html.a().id("hide").href("javascript:hideCols('s_sdv',[" + getIndexes() + "],false);").close();
				html.div().close().nbsp().append(reswords.getString("hide")).nbsp().divEnd().aEnd();
				html.a().id("showMore").style("display: none;")
						.href("javascript:hideCols('s_sdv',[" + getIndexes() + "],true);").close();
				html.div().close().nbsp().append(reswords.getString("show_more")).nbsp().divEnd().aEnd();

				html.script()
						.type("text/javascript")
						.close()
						.append("$j = jQuery.noConflict(); $j(document).ready(function(){ " + "hideCols('s_sdv',["
								+ getIndexes() + "],true);});").scriptEnd();

			}

			return html.toString();
		}

		String getIndexes() {
			String result = "2,3,4";
			return result;
		}

	}

	private static class CustomItemRenderer extends AbstractItemRenderer {
		public CustomItemRenderer(ToolbarItem item, CoreContext coreContext) {
			setToolbarItem(item);
			setCoreContext(coreContext);
		}

		public String render() {
			ToolbarItem item = getToolbarItem();
			return item.enabled();
		}
	}
}
