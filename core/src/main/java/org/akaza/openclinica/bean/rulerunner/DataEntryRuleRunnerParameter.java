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
package org.akaza.openclinica.bean.rulerunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.SubjectBean;

/**
 * DataEntryRuleRunnerParameter.
 */
public class DataEntryRuleRunnerParameter {

	private StudyBean study;
	private String currentCrfOid;
	private SubjectBean subjectBean;
	private EventCRFBean eventCRFBean;
	private String currentCrfVersionOid;
	private StudySubjectBean studySubjectBean;
	private List<DiscrepancyNoteBean> transformedDNs;
	private Map<String, Object> attributes = new HashMap<String, Object>();

	public String getCurrentCrfOid() {
		return currentCrfOid;
	}

	public void setCurrentCrfOid(String currentCrfOid) {
		this.currentCrfOid = currentCrfOid;
	}

	public SubjectBean getSubjectBean() {
		return subjectBean;
	}

	public void setSubjectBean(SubjectBean subjectBean) {
		this.subjectBean = subjectBean;
	}

	public String getCurrentCrfVersionOid() {
		return currentCrfVersionOid;
	}

	public void setCurrentCrfVersionOid(String currentCrfVersionOid) {
		this.currentCrfVersionOid = currentCrfVersionOid;
	}

	public StudySubjectBean getStudySubjectBean() {
		return studySubjectBean;
	}

	public void setStudySubjectBean(StudySubjectBean studySubjectBean) {
		this.studySubjectBean = studySubjectBean;
	}

	public List<DiscrepancyNoteBean> getTransformedDNs() {
		return transformedDNs;
	}

	public void setTransformedDNs(List<DiscrepancyNoteBean> transformedDNs) {
		this.transformedDNs = transformedDNs;
	}

	public EventCRFBean getEventCRFBean() {
		return eventCRFBean;
	}

	public void setEventCRFBean(EventCRFBean eventCRFBean) {
		this.eventCRFBean = eventCRFBean;
	}

	public StudyBean getStudy() {
		return study;
	}

	public void setStudy(StudyBean study) {
		this.study = study;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}
}
