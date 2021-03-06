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
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.managestudy;

import org.akaza.openclinica.bean.core.AuditableEntityBean;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Object for study group class
 * 
 * @author jxu
 * 
 */
@SuppressWarnings({"serial"})
public class StudyGroupClassBean extends AuditableEntityBean {
	// STUDY_GROUP_ID NAME STUDY_ID OWNER_ID
	// DATE_CREATED GROUP_CLASS_TYPE_ID STATUS_ID DATE_UPDATED
	// UPDATE_ID subject_assignment
	private int studyId = 0;
	private String studyName = ""; // not in db
	private int groupClassTypeId = 0;
	private String groupClassTypeName = ""; // not in db
	private String subjectAssignment = "";

	private List<StudyGroupBean> studyGroups = new ArrayList<StudyGroupBean>();// not in DB
	private List<StudyEventDefinitionBean> eventDefinitions = new ArrayList<StudyEventDefinitionBean>();// not in DB, show events which is in Dynamic Group, for Dynamic Group only
	private int studyGroupId = 0;// not in DB, indicates which group a
	// subject is in
	private String groupNotes = "";// not in DB
	private String studyGroupName = "";// not in DB
	private boolean selected = false; // not in DB, tbh
	private boolean isDefault; //in DB, for Dyn Groups
	private int dynamicOrdinal = 0; //in DB, for Dyn Groups
	
	public static ComparatorForDynGroupClasses comparatorForDynGroupClasses = new ComparatorForDynGroupClasses();

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
	 * @return Returns the eventDefinitions for Dynamic Group.
	 */
	public List<StudyEventDefinitionBean> getEventDefinitions() {
		return eventDefinitions;
	}

	/**
	 * @param eventDefinitions 
	 *            The eventDefinitions to set for Dynamic Group.
	 */
	public void setEventDefinitions(List<StudyEventDefinitionBean> eventDefinitions) {
		this.eventDefinitions = eventDefinitions;
	}
	
	/**
	 * @return Returns the studyGroups.
	 */
	public List<StudyGroupBean> getStudyGroups() {
		return studyGroups;
	}

	/**
	 * @param studyGroups
	 *            The studyGroups to set.
	 */
	public void setStudyGroups(List<StudyGroupBean> studyGroups) {
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

	public int getDynamicOrdinal() {
		return dynamicOrdinal;
	}

	public void setDynamicOrdinal(int dynamicOrdinal) {
		this.dynamicOrdinal = dynamicOrdinal;
	}
}

class ComparatorForDynGroupClasses implements Comparator<StudyGroupClassBean> {
	public int compare(StudyGroupClassBean bean1, StudyGroupClassBean bean2) {
		if (bean2.isDefault()){
			return 1;
		}
		if (bean1.isDefault()){
			return -1;
		}
		if (bean2.getDynamicOrdinal() > bean1.getDynamicOrdinal()){
			return -1;
		}
		if (bean2.getDynamicOrdinal() < bean1.getDynamicOrdinal()){
			return 1;
		}	
		return 0;
	}
}

