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

package org.akaza.openclinica.control.managestudy;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.control.DefaultToolbar;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.jmesa.core.CoreContext;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.toolbar.AbstractItem;
import org.jmesa.view.html.toolbar.AbstractItemRenderer;
import org.jmesa.view.html.toolbar.ClearItemRenderer;
import org.jmesa.view.html.toolbar.ToolbarItem;
import org.jmesa.view.html.toolbar.ToolbarItemRenderer;
import org.jmesa.view.html.toolbar.ToolbarItemType;

import java.util.ArrayList;
import java.util.ResourceBundle;

public class ListEventsForSubjectTableToolbar extends DefaultToolbar {

	private final ArrayList<StudyEventDefinitionBean> studyEventDefinitions;
	private final ArrayList<StudyGroupClassBean> studyGroupClasses;
	private final StudyEventDefinitionBean selectedStudyEventDefinition;
	protected ResourceBundle resword = ResourceBundleProvider.getWordsBundle();
	protected ResourceBundle resnotes = ResourceBundleProvider.getTextsBundle();

	public ListEventsForSubjectTableToolbar(ArrayList<StudyEventDefinitionBean> studyEventDefinitions,
			ArrayList<StudyGroupClassBean> studyGroupClasses, StudyEventDefinitionBean selectedStudyEventDefinition,
			boolean addSubjectLinkShow, boolean showMoreLink) {
		super();
		this.studyEventDefinitions = studyEventDefinitions;
		this.studyGroupClasses = studyGroupClasses;
		this.selectedStudyEventDefinition = selectedStudyEventDefinition;
		this.showMoreLink = showMoreLink;
	}

	@Override
	protected void addToolbarItems() {
		addToolbarItem(ToolbarItemType.SEPARATOR);
		addToolbarItem(createCustomItem(new ShowMoreItem()));

		addToolbarItem(ToolbarItemType.SEPARATOR);
		addToolbarItem(createCustomItem(new StudyEventDefinitionDropDownItem()));
		addToolbarItem(createCustomItem(new NewHiddenItem()));
	}

	private ToolbarItem createCustomItem(AbstractItem item) {

		ToolbarItemRenderer renderer = new CustomItemRenderer(item, getCoreContext());
		renderer.setOnInvokeAction("onInvokeAction");
		item.setToolbarItemRenderer(renderer);

		return item;
	}

	public ToolbarItem createAddSubjectItem() {

		AddNewSubjectItem item = new AddNewSubjectItem();
		item.setCode(ToolbarItemType.CLEAR_ITEM.toCode());
		ToolbarItemRenderer renderer = new ClearItemRenderer(item, getCoreContext());
		renderer.setOnInvokeAction("onInvokeAction");
		item.setToolbarItemRenderer(renderer);

		return item;
	}

	private class ShowMoreItem extends AbstractItem {

		@Override
		public String disabled() {
			return null;
		}

		@Override
		public String enabled() {
			HtmlBuilder html = new HtmlBuilder();

			if (showMoreLink) {
				html.a().id("showMore")
						.href("javascript:hideCols('listEventsForSubject',[" + getIndexes() + "],true);").close();
				html.div().close().nbsp().append(resword.getString("show_more")).nbsp().divEnd().aEnd();
				html.a().id("hide").style("display: none;")
						.href("javascript:hideCols('listEventsForSubject',[" + getIndexes() + "],false);").close();
				html.div().close().nbsp().append(resword.getString("hide")).nbsp().divEnd().aEnd();

				html.script()
						.type("text/javascript")
						.close()
						.append("$j = jQuery.noConflict(); $j(document).ready(function(){ "
								+ "hideCols('listEventsForSubject',[" + getIndexes() + "],false);});").scriptEnd();
			} else {
				html.a().id("showMore").style("display: none;")
						.href("javascript:hideCols('listEventsForSubject',[" + getIndexes() + "],true);").close();
				html.div().close().nbsp().append(resword.getString("show_more")).nbsp().divEnd().aEnd();
				html.a().id("hide").href("javascript:hideCols('listEventsForSubject',[" + getIndexes() + "],false);")
						.close();
				html.div().close().nbsp().append(resword.getString("hide")).nbsp().divEnd().aEnd();
			}

			return html.toString();
		}

		/**
		 * @return Dynamically generate the indexes of studyGroupClasses. It starts from 4 because there are 4 columns
		 *         before study group columns that will require to be hidden.
		 * @see ListEventsForSubjectTableFactory#configureColumns(org.jmesa.facade.TableFacade, java.util.Locale)
		 */
		String getIndexes() {
			int startFrom = 3;
			String result = "1,2,3";

			StudyBean studyBean = (StudyBean) getWebContext().getSessionAttribute("study");
			if (studyBean != null) {
				startFrom = 2;
				result = "1,2";
				if (!studyBean.getStudyParameterConfig().getGenderRequired().equalsIgnoreCase("false")) {
					result += "," + ++startFrom;
				}
			}

			for (int i = 0; i < studyGroupClasses.size(); i++) {
				result += "," + (startFrom + i + 1);
			}
			return result;
		}
	}

	private class StudyEventDefinitionDropDownItem extends AbstractItem {

		@Override
		public String disabled() {
			return null;
		}

		@Override
		public String enabled() {
			String js = "var selectedValue = document.getElementById('sedDropDown').options[document.getElementById('sedDropDown').selectedIndex].value;  "
					+ " if (selectedValue != null && selectedValue != 0 ) { "
					+ "window.location='ListEventsForSubjects?module=submit&defId='+selectedValue;}"
					+ " if (selectedValue != null && selectedValue == 0 ) { "
					+ "window.location='ListStudySubjects' } ";
			HtmlBuilder html = new HtmlBuilder();
			html.append(resword.getString("events") + ": ");
			html.select().id("sedDropDown").onchange(js).close();
			html.option().value("0");
			html.close().append(resnotes.getString("all_events")).optionEnd();
			for (StudyEventDefinitionBean studyEventDefinition : studyEventDefinitions) {
				html.option().value(String.valueOf(studyEventDefinition.getId()));
				if (studyEventDefinition.getId() == selectedStudyEventDefinition.getId()) {
					html.selected();
				}
				html.close().append(studyEventDefinition.getName()).optionEnd();
			}
			html.selectEnd();
			return html.toString();
		}

	}

	private class AddNewSubjectItem extends AbstractItem {

		@Override
		public String disabled() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String enabled() {
			HtmlBuilder html = new HtmlBuilder();
			html.a().href("#").id("addSubject");
			html.quote();
			html.quote().close();
			html.nbsp().append(resword.getString("add_new_subject")).nbsp().aEnd();

			return html.toString();
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
