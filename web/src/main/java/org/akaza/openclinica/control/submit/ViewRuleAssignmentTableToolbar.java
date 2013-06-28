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

package org.akaza.openclinica.control.submit;

import org.akaza.openclinica.control.DefaultToolbar;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.jmesa.core.CoreContext;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.toolbar.AbstractItem;
import org.jmesa.view.html.toolbar.AbstractItemRenderer;
import org.jmesa.view.html.toolbar.ToolbarItem;
import org.jmesa.view.html.toolbar.ToolbarItemRenderer;
import org.jmesa.view.html.toolbar.ToolbarItemType;

import java.util.List;
import java.util.ResourceBundle;

public class ViewRuleAssignmentTableToolbar extends DefaultToolbar {

	List<Integer> ruleSetRuleIds;
	private final ResourceBundle reswords = ResourceBundleProvider.getWordsBundle();
	public ViewRuleAssignmentTableToolbar(List<Integer> ruleSetRuleIds, boolean showMoreLink, boolean isDesignerRequest) {
		super();
		this.ruleSetRuleIds = ruleSetRuleIds;
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
				html.a().id("showMore").href("javascript:hideCols('ruleAssignments',[" + getIndexes() + "],true);")
						.close();
				html.div().close().nbsp().append(reswords.getString("show_more")).nbsp().divEnd().aEnd();
				html.a().id("hide").style("display: none;")
						.href("javascript:hideCols('ruleAssignments',[" + getIndexes() + "],false);").close();
				html.div().close().nbsp().append(reswords.getString("hide")).nbsp().divEnd().aEnd();

				html.script()
						.type("text/javascript")
						.close()
						.append("$(document).ready(function(){ "
								+ "hideCols('ruleAssignments',[" + getIndexes() + "],false);" + " if($.browser.msie){"
								+ " $('tr.header td div:last').each(function(){" + " $(this).click();"
								+ "var ah = $(this).height();" + "$('tr.header td div').each(function(){"
								+ "$(this).css('height',ah);" + "});" + "});" + "}" + "});").scriptEnd();
			} else {
				html.a().id("hide").href("javascript:hideCols('ruleAssignments',[" + getIndexes() + "],false);")
						.close();
				html.div().close().nbsp().append(reswords.getString("hide")).nbsp().divEnd().aEnd();
				html.a().id("showMore").style("display: none;")
						.href("javascript:hideCols('ruleAssignments',[" + getIndexes() + "],true);").close();
				html.div().close().nbsp().append(reswords.getString("show_more")).nbsp().divEnd().aEnd();
			}
			return html.toString();
		}

		/**
		 * @return Dynamically generate the indexes of studyGroupClasses. It starts from 4 because there are 4 columns
		 *         before study group columns that will require to be hidden.
		 * @see ListStudySubjectTableFactory#configureColumns(org.jmesa.facade.TableFacade, java.util.Locale)
		 */
		String getIndexes() {
			String result = "0,1,3,4,8,9,11,13";
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
