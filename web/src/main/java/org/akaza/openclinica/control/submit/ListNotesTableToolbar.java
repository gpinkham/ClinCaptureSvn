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
import org.akaza.openclinica.dao.managestudy.ListNotesFilter;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.jmesa.core.CoreContext;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.toolbar.AbstractItem;
import org.jmesa.view.html.toolbar.AbstractItemRenderer;
import org.jmesa.view.html.toolbar.ClearItemRenderer;
import org.jmesa.view.html.toolbar.ToolbarItem;
import org.jmesa.view.html.toolbar.ToolbarItemRenderer;
import org.jmesa.view.html.toolbar.ToolbarItemType;

import java.util.ResourceBundle;

public class ListNotesTableToolbar extends DefaultToolbar {
	private ResourceBundle reswords = ResourceBundleProvider.getWordsBundle();
	private String module;
	private int resolutionStatus;
	private int discNoteType;
	private boolean studyHasDiscNotes;
	private ResourceBundle resword;

	private ListNotesFilter listNotesFilter;
	
	private final static String INDEXES_OF_COLUMNS_TO_BE_HIDDEN = "0, 5, 6, 10, 12, 15, 17, 18, 20";

	public ListNotesTableToolbar(boolean showMoreLink) {
		super();
		this.showMoreLink = showMoreLink;
	}

	@Override
	protected void addToolbarItems() {
		addToolbarItem(ToolbarItemType.SEPARATOR);
		addToolbarItem(createShowMoreLinkItem(resword, INDEXES_OF_COLUMNS_TO_BE_HIDDEN));
		if (this.studyHasDiscNotes) {
			addToolbarItem(createDownloadLinkItem());
			addToolbarItem(createNotePopupLinkItem());
		}
		addToolbarItem(createCustomItem(new NewHiddenItem()));

	}
	
	public ToolbarItem createDownloadLinkItem() {
		DownloadLinkItem item = new DownloadLinkItem();
		item.setCode(ToolbarItemType.CLEAR_ITEM.toCode());
		ToolbarItemRenderer renderer = new ClearItemRenderer(item, getCoreContext());
		renderer.setOnInvokeAction("onInvokeAction");
		item.setToolbarItemRenderer(renderer);
		return item;
	}

	public ToolbarItem createNotePopupLinkItem() {
		NotePopupLinkItem item = new NotePopupLinkItem();
		item.setCode(ToolbarItemType.CLEAR_ITEM.toCode());
		ToolbarItemRenderer renderer = new ClearItemRenderer(item, getCoreContext());
		renderer.setOnInvokeAction("onInvokeAction");
		item.setToolbarItemRenderer(renderer);
		return item;
	}

	private ToolbarItem createCustomItem(AbstractItem item) {

		ToolbarItemRenderer renderer = new CustomItemRenderer(item, getCoreContext());
		renderer.setOnInvokeAction("onInvokeAction");
		item.setToolbarItemRenderer(renderer);
		return item;
	}

	public ToolbarItem createBackToNotesMatrixListItem() {
		ShowLinkToNotesMatrix item = new ShowLinkToNotesMatrix();
		item.setCode(ToolbarItemType.CLEAR_ITEM.toCode());
		ToolbarItemRenderer renderer = new ClearItemRenderer(item, getCoreContext());
		renderer.setOnInvokeAction("onInvokeAction");
		item.setToolbarItemRenderer(renderer);

		return item;
	}

	private class ShowLinkToNotesMatrix extends AbstractItem {
		@Override
		public String disabled() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String enabled() {
			HtmlBuilder html = new HtmlBuilder();
			html.a().href("ListDiscNotesSubjectServlet?module=submit").id("backToNotesMatrix");
			html.quote();
			html.quote().close();
			html.nbsp().append(reswords.getString("view_as_matrix")).nbsp().aEnd();

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

	private class DownloadLinkItem extends AbstractItem {
		@Override
		public String disabled() {
			return null;
		}

		@Override
		public String enabled() {

			/*
			 * ClinCapture #71 construct filter string
			 */
			StringBuilder filters = new StringBuilder();
			for (ListNotesFilter.Filter filter : listNotesFilter.getFilters()) {
				filters.append("&").append(filter.getProperty()).append("=").append(filter.getValue());
			}
			HtmlBuilder html = new HtmlBuilder();
			String downloadFilter = "module=" + module + filters.toString();

			html.a().href(
					"javascript:changeValue('filters', '" + downloadFilter
							+ "'); changeValue('fmt', 'pdf'); formSubmit('downloadForm');");
			html.quote();
			html.append(getAction());
			html.quote().close();
			html.img().name("bt_View1").src("images/table/pdf.gif").border("0")
					.alt(resword.getString("download_all_discrepancy_notes"))
					.title(resword.getString("download_notes_in_PDF"))
					.append("class=\"downloadAllDNotes\" width=\"24 \" height=\"15\"").end().aEnd();

			html.a().href(
					"javascript:changeValue('filters', '" + downloadFilter
							+ "'); changeValue('fmt', 'csv'); formSubmit('downloadForm');");
			html.quote();
			html.append(getAction());
			html.quote().close();
			html.img().name("bt_View2").src("images/table/excel.gif").border("0")
					.alt(resword.getString("download_all_discrepancy_notes"))
					.title(resword.getString("download_notes_in_CSV"))
					.append("class=\"downloadAllDNotes\" width=\"24 \" height=\"15\"").end().aEnd();

			return html.toString();
		}
	}

	private class NotePopupLinkItem extends AbstractItem {
		@Override
		public String disabled() {
			return null;
		}

		@Override
		public String enabled() {
			HtmlBuilder html = new HtmlBuilder();
			html.a().href("javascript:void(0)");
			html.onclick("javascript:openPopup()");
			html.quote();
			html.append(getAction());
			html.quote().close();
			html.img().name("bt_View1").src("images/bt_Print.gif").border("0").alt(resword.getString("print"))
					.title(resword.getString("print"))
					.append("class=\"downloadAllDNotes\" width=\"24 \" height=\"15\"").end().aEnd();
			return html.toString();
		}
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public int getResolutionStatus() {
		return resolutionStatus;
	}

	public void setResolutionStatus(int resolutionStatus) {
		this.resolutionStatus = resolutionStatus;
	}

	public int getDiscNoteType() {
		return discNoteType;
	}

	public void setDiscNoteType(int discNoteType) {
		this.discNoteType = discNoteType;
	}

	public boolean isStudyHasDiscNotes() {
		return studyHasDiscNotes;
	}

	public void setStudyHasDiscNotes(boolean studyHasDiscNotes) {
		this.studyHasDiscNotes = studyHasDiscNotes;
	}

	public ResourceBundle getResword() {
		return resword;
	}

	public void setResword(ResourceBundle resword) {
		this.resword = resword;
	}

	public ListNotesFilter getListNotesFilter() {
		return listNotesFilter;
	}

	public void setListNotesFilter(ListNotesFilter listNotesFilter) {
		this.listNotesFilter = listNotesFilter;
	}
}
