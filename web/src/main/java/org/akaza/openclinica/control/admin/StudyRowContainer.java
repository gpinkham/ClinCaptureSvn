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

package org.akaza.openclinica.control.admin;

import org.akaza.openclinica.bean.managestudy.StudyBean;

/**
 * A bean that represents a study row in a view table.
 */
public class StudyRowContainer {
	// The study's name
	private String name = "";

	// The study's unique protocol id
	private String uniqueProtocolid;

	// An actions attribute that represents a link to a ViewStudiesDetails page
	private String actions;

	private StudyBean studyBean;

	public final static String VIEW_STUDY_DETAILS_URL = "<a onmouseup=\"javascript:setImage('bt_View1','images/bt_View.gif');\" onmousedown=\"javascript:setImage('bt_View1','images/bt_View_d.gif');\" href=\"ViewStudy?id=";

	public final static String VIEW_STUDY_DETAILS_SUFFIX = "&amp;viewFull=yes\"><img hspace=\"6\" border=\"0\" align=\"left\" title=\"View\" alt=\"View\" src=\"images/bt_View.gif\" name=\"bt_View1\"/></a>";

	public StudyBean getStudyBean() {
		return studyBean;
	}

	public void setStudyBean(StudyBean studyBean) {
		this.studyBean = studyBean;
	}

	public String getActions() {
		return actions;
	}

	public void setActions(String actions) {
		this.actions = actions;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUniqueProtocolid() {
		return uniqueProtocolid;
	}

	public void setUniqueProtocolid(String uniqueProtocolid) {
		this.uniqueProtocolid = uniqueProtocolid;
	}
}
