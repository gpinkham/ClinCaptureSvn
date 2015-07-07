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

/*
 * OpenClinica is distributed under the GNU Lesser General Public License (GNU
 * LGPL).
 *
 * For details see: http://www.openclinica.org/license copyright 2003-2008 Akaza
 * Research
 *
 */

package org.akaza.openclinica.bean.submit.crfdata;

import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.domain.datamap.StudyEventDefinition;

import java.util.ArrayList;
import java.util.Date;

/**
 * ClinCapture event attributes have been included in addition to ODM StudyEventData attributes.
 */
public class ExportStudyEventDataBean extends StudyEventDataBean {
    private String location;
    private Date startDate;
    private Date endDate;
	private Boolean startTimeFlag;
	private Boolean endTimeFlag;
    private String status;
    private Integer ageAtEvent;
    private StudyEventDefinitionBean studyEventDefinitionBean;
    private StudyEventDefinition studyEventDefinition;
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

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return this.endDate;
    }

	public Boolean getStartTimeFlag() {
		return startTimeFlag;
	}

	public void setStartTimeFlag(Boolean startTimeFlag) {
		this.startTimeFlag = startTimeFlag;
	}

	public Boolean getEndTimeFlag() {
		return endTimeFlag;
	}

	public void setEndTimeFlag(Boolean endTimeFlag) {
		this.endTimeFlag = endTimeFlag;
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

    public StudyEventDefinitionBean getStudyEventDefinitionBean() {
        return studyEventDefinitionBean;
    }

    public void setStudyEventDefinitionBean(StudyEventDefinitionBean studyEventDefinitionBean) {
        this.studyEventDefinitionBean = studyEventDefinitionBean;
    }

    public StudyEventDefinition getStudyEventDefinition() {
        return studyEventDefinition;
    }

    public void setStudyEventDefinition(StudyEventDefinition studyEventDefinition) {
        this.studyEventDefinition = studyEventDefinition;
    }
}
