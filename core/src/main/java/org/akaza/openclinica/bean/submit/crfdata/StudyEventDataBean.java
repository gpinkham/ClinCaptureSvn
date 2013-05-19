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

import org.akaza.openclinica.bean.odmbeans.AuditLogsBean;
import org.akaza.openclinica.bean.odmbeans.DiscrepancyNotesBean;

public class StudyEventDataBean {
	private ArrayList<FormDataBean> formData;
	private String studyEventOID;
	private String studyEventRepeatKey;
	private AuditLogsBean auditLogs;
	private DiscrepancyNotesBean discrepancyNotes;

	public StudyEventDataBean() {
		formData = new ArrayList<FormDataBean>();
		auditLogs = new AuditLogsBean();
		discrepancyNotes = new DiscrepancyNotesBean();
	}

	public String getStudyEventRepeatKey() {
		return studyEventRepeatKey;
	}

	public void setStudyEventRepeatKey(String studyEventRepeatKey) {
		this.studyEventRepeatKey = studyEventRepeatKey;
	}

	public String getStudyEventOID() {
		return studyEventOID;
	}

	public void setStudyEventOID(String studyEventOID) {
		this.studyEventOID = studyEventOID;
	}

	public ArrayList<FormDataBean> getFormData() {
		return formData;
	}

	public void setFormData(ArrayList<FormDataBean> formData) {
		this.formData = formData;
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
