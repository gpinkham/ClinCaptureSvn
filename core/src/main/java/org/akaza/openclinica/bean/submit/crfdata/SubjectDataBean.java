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

package org.akaza.openclinica.bean.submit.crfdata;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.akaza.openclinica.bean.odmbeans.AuditLogsBean;
import org.akaza.openclinica.bean.odmbeans.DiscrepancyNotesBean;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(namespace = "http://www.cdisc.org/ns/odm/v1.3")
public class SubjectDataBean {

	private ArrayList<StudyEventDataBean> studyEventData;
	private String subjectOID;
	private String studySubjectId;
	private AuditLogsBean auditLogs;
	private DiscrepancyNotesBean discrepancyNotes;

	public SubjectDataBean() {
		studyEventData = new ArrayList<StudyEventDataBean>();
		auditLogs = new AuditLogsBean();
		discrepancyNotes = new DiscrepancyNotesBean();
	}

	@XmlAttribute(name = "SubjectKey")
	public String getSubjectOID() {
		return subjectOID;
	}

	public void setSubjectOID(String subjectOID) {
		this.subjectOID = subjectOID;
	}

	@XmlAttribute(name = "StudySubjectID", namespace = "http://www.openclinica.org/ns/odm_ext_v130/v3.1")
	public String getStudySubjectId() {
		return studySubjectId;
	}

	public void setStudySubjectId(String studySubjectId) {
		this.studySubjectId = studySubjectId;
	}

	@XmlElement(name = "StudyEventData", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	public ArrayList<StudyEventDataBean> getStudyEventData() {
		return studyEventData;
	}

	public void setStudyEventData(ArrayList<StudyEventDataBean> studyEventData) {
		this.studyEventData = studyEventData;
	}

	public AuditLogsBean getAuditLogs() {
		return auditLogs;
	}

	public void setAuditLogs(AuditLogsBean auditLogs) {
		this.auditLogs = auditLogs;
	}

	public DiscrepancyNotesBean getDiscrepancyNotes() {
		return discrepancyNotes;
	}

	public void setDiscrepancyNotes(DiscrepancyNotesBean discrepancyNotes) {
		this.discrepancyNotes = discrepancyNotes;
	}
}
