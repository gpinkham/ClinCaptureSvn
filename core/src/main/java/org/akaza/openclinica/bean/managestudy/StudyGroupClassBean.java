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
package org.akaza.openclinica.bean.managestudy;

import org.akaza.openclinica.bean.core.AuditableEntityBean;

import java.util.ArrayList;

/**
 * Object for study group class
 * 
 * @author jxu
 * 
 */
@SuppressWarnings({"rawtypes", "serial"})
public class StudyGroupClassBean extends AuditableEntityBean {
	// STUDY_GROUP_ID NAME STUDY_ID OWNER_ID
	// DATE_CREATED GROUP_CLASS_TYPE_ID STATUS_ID DATE_UPDATED
	// UPDATE_ID subject_assignment
	private int studyId = 0;
	private String studyName = ""; // not in db
	private int groupClassTypeId = 0;
	private String groupClassTypeName = ""; // not in db
	private String subjectAssignment = "";

	private ArrayList studyGroups = new ArrayList();// not in DB
	private int studyGroupId = 0;// not in DB, indicates which group a
	// subject is in
	private String groupNotes = "";// not in DB
	private String studyGroupName = "";// not in DB
	private boolean selected = false; // not in DB, tbh
	private boolean isDefault; //in DB, for Dyn Events

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean aDefault) {
		isDefault = aDefault;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * @return Returns the studyName.
	 */
	public String getStudyName() {
		return studyName;
	}

	/**
	 * @param studyName
	 *            The studyName to set.
	 */
	public void setStudyName(String studyName) {
		this.studyName = studyName;
	}

	/**
	 * @return Returns the groupClassTypeName.
	 */
	public String getGroupClassTypeName() {
		return org.akaza.openclinica.i18n.util.ResourceBundleProvider.getResTerm(groupClassTypeName);
	}

	/**
	 * @param groupClassTypeName
	 *            The groupClassTypeName to set.
	 */
	public void setGroupClassTypeName(String groupClassTypeName) {
		this.groupClassTypeName = groupClassTypeName;
	}

	/**
	 * @return Returns the subjectAssignment.
	 */
	public String getSubjectAssignment() {
		return subjectAssignment;
	}

	/**
	 * @param subjectAssignment
	 *            The subjectAssignment to set.
	 */
	public void setSubjectAssignment(String subjectAssignment) {
		this.subjectAssignment = subjectAssignment;
	}

	/**
	 * @return Returns the groupClassTypeId.
	 */
	public int getGroupClassTypeId() {
		return groupClassTypeId;
	}

	/**
	 * @param groupClassTypeId
	 *            The groupClassTypeId to set.
	 */
	public void setGroupClassTypeId(int groupClassTypeId) {
		this.groupClassTypeId = groupClassTypeId;
	}

	/**
	 * @return Returns the studyId.
	 */
	public int getStudyId() {
		return studyId;
	}

	/**
	 * @param studyId
	 *            The studyId to set.
	 */
	public void setStudyId(int studyId) {
		this.studyId = studyId;
	}

	/**
	 * @return Returns the studyGroups.
	 */
	public ArrayList getStudyGroups() {
		return studyGroups;
	}

	/**
	 * @param studyGroups
	 *            The studyGroups to set.
	 */
	public void setStudyGroups(ArrayList studyGroups) {
		this.studyGroups = studyGroups;
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
	 * @return Returns the groupNotes.
	 */
	public String getGroupNotes() {
		return groupNotes;
	}

	/**
	 * @param groupNotes
	 *            The groupNotes to set.
	 */
	public void setGroupNotes(String groupNotes) {
		this.groupNotes = groupNotes;
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
}
