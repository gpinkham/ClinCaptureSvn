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

package org.akaza.openclinica.controller.helper;

import java.util.Date;

/**
 * The Spring BindingResult bean for implementing the SDV search form.
 */
public class SdvFilterDataBean {

	private String study_subject_id;
	private int studyEventDefinition;
	private int studyEventStatus;
	private int eventCRFStatus;
	private int sdvRequirement;
	private String eventcrfSDVStatus;
	private Date startUpdatedDate = null;
	private Date endDate = null;
	private String eventCRFName;

	public SdvFilterDataBean() {
		this.eventCRFName = "";
		this.study_subject_id = "";
		this.eventcrfSDVStatus = "None";
	}

	public String getEventCRFName() {
		return eventCRFName;
	}

	public void setEventCRFName(String eventCRFName) {
		this.eventCRFName = eventCRFName;
	}

	public String getStudy_subject_id() {
		return study_subject_id;
	}

	public void setStudy_subject_id(String study_subject_id) {
		this.study_subject_id = study_subject_id;
	}

	public int getStudyEventDefinition() {
		return studyEventDefinition;
	}

	public void setStudyEventDefinition(int studyEventDefinition) {
		this.studyEventDefinition = studyEventDefinition;
	}

	public int getStudyEventStatus() {
		return studyEventStatus;
	}

	public void setStudyEventStatus(int studyEventStatus) {
		this.studyEventStatus = studyEventStatus;
	}

	public int getEventCRFStatus() {
		return eventCRFStatus;
	}

	public void setEventCRFStatus(int eventCRFStatus) {
		this.eventCRFStatus = eventCRFStatus;
	}

	public int getSdvRequirement() {
		return sdvRequirement;
	}

	public void setSdvRequirement(int sdvRequirement) {
		this.sdvRequirement = sdvRequirement;
	}

	public String getEventcrfSDVStatus() {
		return eventcrfSDVStatus;
	}

	public void setEventcrfSDVStatus(String eventcrfSDVStatus) {
		this.eventcrfSDVStatus = eventcrfSDVStatus;
	}

	public Date getStartUpdatedDate() {
		return startUpdatedDate;
	}

	public void setStartUpdatedDate(Date startUpdatedDate) {
		this.startUpdatedDate = startUpdatedDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

}
