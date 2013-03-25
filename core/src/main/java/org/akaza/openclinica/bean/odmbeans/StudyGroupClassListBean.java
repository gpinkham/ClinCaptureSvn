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

/*
 * OpenClinica is distributed under the GNU Lesser General Public License (GNU
 * LGPL).
 *
 * For details see: http://www.openclinica.org/license copyright 2003-2005 Akaza
 * Research
 *
 */
package org.akaza.openclinica.bean.odmbeans;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author ywang (Nov, 2008)
 * 
 */

public class StudyGroupClassListBean {
	private String ID;
	private String name;
	private String type;
	private String status;
	private String subjectAssignment;
	private List<org.akaza.openclinica.bean.odmbeans.StudyGroupItemBean> studyGroupItems;

	public StudyGroupClassListBean() {
		studyGroupItems = new ArrayList<org.akaza.openclinica.bean.odmbeans.StudyGroupItemBean>();
	}

	public void setID(String id) {
		this.ID = id;
	}

	public String getId() {
		return this.ID;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return this.type;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return this.status;
	}

	public void setSubjectAssignment(String subjectAssignment) {
		this.subjectAssignment = subjectAssignment;
	}

	public String getSubjectAssignment() {
		return this.subjectAssignment;
	}

	public void setStudyGroupItems(List<org.akaza.openclinica.bean.odmbeans.StudyGroupItemBean> studyGroupItems) {
		this.studyGroupItems = studyGroupItems;
	}

	public List<org.akaza.openclinica.bean.odmbeans.StudyGroupItemBean> getStudyGroupItems() {
		return this.studyGroupItems;
	}

}
