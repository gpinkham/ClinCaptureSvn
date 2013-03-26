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

package org.akaza.openclinica.bean.submit;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Display Item Bean Wrapper, code to generate a front end view of errors generated during Data Import.
 * 
 * @author kkrumlian, thickerson
 * 
 */
@SuppressWarnings("rawtypes")
public class DisplayItemBeanWrapper {
	boolean isOverwrite = false;
	boolean isSavable = false;
	List<DisplayItemBean> displayItemBeans;
	HashMap validationErrors;
	// Values to display on the jsp
	String studyEventId;
	String crfVersionId;
	//
	String studyEventName;
	String studySubjectName;
	Date dateOfEvent;
	String nameOfEvent;
	String crfName;
	String crfVersionName;
	String studySubjectOid;
	String studyEventRepeatKey;

	// need to add here
	// study_subject_id, date_of_event, name_of_event, crf_name and version

	public DisplayItemBeanWrapper(List<DisplayItemBean> displayItemBeans, boolean isSavable, boolean isOverwrite,
			HashMap validationErrors, String studyEventId, String crfVersionId, String studyEventName,
			String studySubjectName, Date dateOfEvent, String crfName, String crfVersionName, String studySubjectOid,
			String studyEventRepeatKey) {
		this.isSavable = isSavable;
		this.isOverwrite = isOverwrite;
		this.displayItemBeans = displayItemBeans;
		this.validationErrors = validationErrors;
		this.studyEventId = studyEventId;
		this.crfVersionId = crfVersionId;
		this.studyEventName = studyEventName;
		this.studySubjectName = studySubjectName;
		this.dateOfEvent = dateOfEvent;
		this.crfName = crfName;
		this.crfVersionName = crfVersionName;
		this.studySubjectOid = studySubjectOid;
		this.studyEventRepeatKey = studyEventRepeatKey;

	}

	public HashMap getValidationErrors() {
		return validationErrors;
	}

	public void setValidationErrors(HashMap validationErrors) {
		this.validationErrors = validationErrors;
	}

	public List<DisplayItemBean> getDisplayItemBeans() {
		return displayItemBeans;
	}

	public boolean isSavable() {
		return isSavable;
	}

	public void setSavable(boolean isSavable) {
		this.isSavable = isSavable;
	}

	public String getStudySubjectOid() {
		return studySubjectOid;
	}

	public void setStudySubjectOid(String studySubjectOid) {
		this.studySubjectOid = studySubjectOid;
	}

	public String getCrfVersionId() {
		return crfVersionId;
	}

	public void setCrfVersionId(String crfVersionId) {
		this.crfVersionId = crfVersionId;
	}

	public String getStudyEventId() {
		return studyEventId;
	}

	public void setStudyEventId(String studyEventId) {
		this.studyEventId = studyEventId;
	}

	public String getStudyEventName() {
		return studyEventName;
	}

	public void setStudyEventName(String studyEventName) {
		this.studyEventName = studyEventName;
	}

	public String getStudySubjectName() {
		return studySubjectName;
	}

	public void setStudySubjectName(String studySubjectName) {
		this.studySubjectName = studySubjectName;
	}

	public Date getDateOfEvent() {
		return dateOfEvent;
	}

	public void setDateOfEvent(Date dateOfEvent) {
		this.dateOfEvent = dateOfEvent;
	}

	public String getNameOfEvent() {
		return nameOfEvent;
	}

	public void setNameOfEvent(String nameOfEvent) {
		this.nameOfEvent = nameOfEvent;
	}

	public String getCrfName() {
		return crfName;
	}

	public void setCrfName(String crfName) {
		this.crfName = crfName;
	}

	public String getCrfVersionName() {
		return crfVersionName;
	}

	public void setCrfVersionName(String crfVersionName) {
		this.crfVersionName = crfVersionName;
	}

	public boolean isOverwrite() {
		return isOverwrite;
	}

	public void setOverwrite(boolean isOverwrite) {
		this.isOverwrite = isOverwrite;
	}

	public String getStudyEventRepeatKey() {
		return studyEventRepeatKey;
	}

	public void setStudyEventRepeatKey(String studyEventRepeatKey) {
		this.studyEventRepeatKey = studyEventRepeatKey;
	}

}
