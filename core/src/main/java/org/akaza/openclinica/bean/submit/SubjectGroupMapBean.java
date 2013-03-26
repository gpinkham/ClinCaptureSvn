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
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.submit;

import org.akaza.openclinica.bean.core.AuditableEntityBean;

@SuppressWarnings("serial")
public class SubjectGroupMapBean extends AuditableEntityBean {
	private int studyGroupClassId;
	private int studyGroupId;

	private int studySubjectId;
	private String notes = "";

	private String studyGroupName = "";// not in DB
	private String subjectLabel = "";// not in DB
	private String groupClassName = ""; // not in DB

	/**
	 * @return Returns the subjectLabel.
	 */
	public String getSubjectLabel() {
		return subjectLabel;
	}

	/**
	 * @param subjectLabel
	 *            The subjectLabel to set.
	 */
	public void setSubjectLabel(String subjectLabel) {
		this.subjectLabel = subjectLabel;
	}

	/**
	 * @return Returns the notes.
	 */
	public String getNotes() {
		return notes;
	}

	/**
	 * @param notes
	 *            The notes to set.
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * @return Returns the studyGroupClassId.
	 */
	public int getStudyGroupClassId() {
		return studyGroupClassId;
	}

	/**
	 * @param studyGroupClassId
	 *            The studyGroupClassId to set.
	 */
	public void setStudyGroupClassId(int studyGroupClassId) {
		this.studyGroupClassId = studyGroupClassId;
	}

	/**
	 * @return Returns the studyGroupId.
	 */
	public int getStudyGroupId() {
		return studyGroupId;
	}

	/**
	 * @param studyGroupId
	 *            The studyGroupId to set.
	 */
	public void setStudyGroupId(int studyGroupId) {
		this.studyGroupId = studyGroupId;
	}

	/**
	 * @return Returns the studyGroupName.
	 */
	public String getStudyGroupName() {
		return studyGroupName;
	}

	/**
	 * @param studyGroupName
	 *            The studyGroupName to set.
	 */
	public void setStudyGroupName(String studyGroupName) {
		this.studyGroupName = studyGroupName;
	}

	/**
	 * @return Returns the studySubjectId.
	 */
	public int getStudySubjectId() {
		return studySubjectId;
	}

	/**
	 * @param studySubjectId
	 *            The studySubjectId to set.
	 */
	public void setStudySubjectId(int studySubjectId) {
		this.studySubjectId = studySubjectId;
	}

	/**
	 * @return Returns the groupClassName.
	 */
	public String getGroupClassName() {
		return groupClassName;
	}

	/**
	 * @param groupClassName
	 *            The groupClassName to set.
	 */
	public void setGroupClassName(String groupClassName) {
		this.groupClassName = groupClassName;
	}
}
