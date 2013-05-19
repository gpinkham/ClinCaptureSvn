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

package org.akaza.openclinica.view.tags;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.DisplaySectionBean;
import org.akaza.openclinica.view.form.HorizontalFormBuilder;

import java.io.IOException;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * Created by IntelliJ IDEA. User: bruceperry Date: May 4, 2007
 */
public class TableTag extends SimpleTagSupport {
	public static String VIEW_DATA_ENTRY = "viewdataentry";
	public static String INITIAL_DATA_ENTRY = "initialdataentry";
	public static String DOUBLE_DATA_ENTRY = "doubledataentry";
	public static String ADMIN_EDIT = "admin_edit";

	private String datacontext;

	@Override
	public void doTag() throws JspException, IOException {

		JspContext context = getJspContext();
		JspWriter tagWriter = context.getOut();
		boolean isViewData = datacontext != null && datacontext.equalsIgnoreCase(VIEW_DATA_ENTRY);
		DisplaySectionBean dBean = (DisplaySectionBean) context.findAttribute("section");
		StudyBean studyBean = (StudyBean) context.findAttribute("study");

		// tabId is used to seed the tabindex attributes of the form's input
		// elements,
		// according to the section's tab number
		int tabId;
		// this is for viewDataEntryServlet
		Object tabObject = context.findAttribute("tabId");
		if (tabObject == null) {
			// this is for DataEntryServlet
			tabObject = context.findAttribute("tab");
		}
		if (tabObject == null) {
			tabObject = new Integer("1");
		}

		tabId = new Integer(tabObject.toString());

		if (dBean != null) {
			HorizontalFormBuilder formBuilder = new HorizontalFormBuilder();
			// FormBuilder sets tabindexSeed to 1 in its constructor
			if (tabId > 1)
				formBuilder.setTabindexSeed(tabId);
			formBuilder.setDataEntry(isViewData);
			formBuilder.setEventCRFbean(dBean.getEventCRF());
			formBuilder.setDisplayItemGroups(dBean.getDisplayFormGroups());
			formBuilder.setSectionBean(dBean.getSection());
			if (studyBean != null) {
				formBuilder.setStudyBean(studyBean);
			}
			tagWriter.println(formBuilder.createMarkup());
		} else {
			tagWriter.println("The section bean was not found<br />");
		}

		/*
		 * FormBuilderTest builder = new FormBuilderTest(); tagWriter.println(builder.createTable());
		 */

	}

	public String getDatacontext() {
		return datacontext;
	}

	public void setDatacontext(String datacontext) {
		this.datacontext = datacontext;
	}
}
