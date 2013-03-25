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
 * For details see: http://www.openclinica.org/license copyright 2003-2008 Akaza
 * Research
 *
 */

package org.akaza.openclinica.bean.submit.crfdata;

import java.util.ArrayList;

/**
 * OpenClinica event attributes have been included in addition to ODM StudyEventData attributes
 * 
 * @author ywang (Nov, 2008)
 */

public class ExportStudyEventDataBean extends StudyEventDataBean {
	private String location;
	private String startDate;
	private String endDate;
	private String status;
	private Integer ageAtEvent;
	private ArrayList<ExportFormDataBean> exportFormData;

	public ExportStudyEventDataBean() {
		super();
		exportFormData = new ArrayList<ExportFormDataBean>();
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLocation() {
		return this.location;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getStartDate() {
		return this.startDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getEndDate() {
		return this.endDate;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return this.status;
	}

	public ArrayList<ExportFormDataBean> getExportFormData() {
		return exportFormData;
	}

	public void setExportFormData(ArrayList<ExportFormDataBean> formData) {
		this.exportFormData = formData;
	}

	public void setAgeAtEvent(Integer ageAtEvent) {
		this.ageAtEvent = ageAtEvent;
	}

	public Integer getAgeAtEvent() {
		return this.ageAtEvent;
	}
}
