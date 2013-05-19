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

import java.util.ArrayList;
import java.util.List;

/**
 * OpenClinica subject attributes have been included in addition to ODM SubjectData attributes
 * 
 * @author ywang (Nov, 2008)
 */

public class ExportSubjectDataBean extends SubjectDataBean {
	private String studySubjectId;
	private String uniqueIdentifier;
	private String status;
	private String secondaryId;
	private Integer yearOfBirth;
	private String dateOfBirth;
	private String subjectGender;

	private List<ExportStudyEventDataBean> exportStudyEventData;
	private List<SubjectGroupDataBean> subjectGroupData;

	public ExportSubjectDataBean() {
		super();
		this.exportStudyEventData = new ArrayList<ExportStudyEventDataBean>();
		this.subjectGroupData = new ArrayList<SubjectGroupDataBean>();
	}

	public void setStudySubjectId(String studySubjectId) {
		this.studySubjectId = studySubjectId;
	}

	public String getStudySubjectId() {
		return this.studySubjectId;
	}

	public void setUniqueIdentifier(String uniqueIdentifier) {
		this.uniqueIdentifier = uniqueIdentifier;
	}

	public String getUniqueIdentifier() {
		return this.uniqueIdentifier;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return this.status;
	}

	public void setSecondaryId(String secondaryId) {
		this.secondaryId = secondaryId;
	}

	public String getSecondaryId() {
		return this.secondaryId;
	}

	public void setYearOfBirth(Integer yearOfBirth) {
		this.yearOfBirth = yearOfBirth;
	}

	public Integer getYearOfBirth() {
		return this.yearOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getDateOfBirth() {
		return this.dateOfBirth;
	}

	public void setSubjectGender(String gender) {
		this.subjectGender = gender;
	}

	public String getSubjectGender() {
		return this.subjectGender;
	}

	public List<ExportStudyEventDataBean> getExportStudyEventData() {
		return exportStudyEventData;
	}

	public void setExportStudyEventData(List<ExportStudyEventDataBean> studyEventData) {
		this.exportStudyEventData = studyEventData;
	}

	public void setSubjectGroupData(List<SubjectGroupDataBean> subjectGroupData) {
		this.subjectGroupData = subjectGroupData;
	}

	public List<SubjectGroupDataBean> getSubjectGroupData() {
		return this.subjectGroupData;
	}
}
