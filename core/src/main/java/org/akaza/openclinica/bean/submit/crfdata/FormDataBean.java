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
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.akaza.openclinica.bean.odmbeans.AuditLogsBean;
import org.akaza.openclinica.bean.odmbeans.DiscrepancyNotesBean;

/**
 * FormDataBean.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(namespace = "http://www.cdisc.org/ns/odm/v1.3")
public class FormDataBean {

	private ArrayList<ImportItemGroupDataBean> itemGroupData;
	private AuditLogsBean auditLogs;
	private DiscrepancyNotesBean discrepancyNotes;
	private String formOID;
	private String partialSections;
	private Set<Integer> partialSectionIds = new HashSet<Integer>();

	/**
	 * Constructor.
	 */
	public FormDataBean() {
		itemGroupData = new ArrayList<ImportItemGroupDataBean>();
		auditLogs = new AuditLogsBean();
		discrepancyNotes = new DiscrepancyNotesBean();
	}

	@XmlAttribute(name = "PartialSections")
	public String getPartialSections() {
		return partialSections;
	}

	/**
	 * Sets partialSections and adds them to partialSectionIds.
	 * 
	 * @param partialSections
	 *            String
	 */
	public void setPartialSections(String partialSections) {
		this.partialSections = partialSections;
		if (partialSections != null) {
			for (String partialSection : partialSections.split(",")) {
				try {
					partialSectionIds.add(Integer.parseInt(partialSection.trim()));
				} catch (Exception ex) {
					//
				}
			}
		}
	}

	@XmlAttribute(name = "FormOID")
	public String getFormOID() {
		return formOID;
	}

	public void setFormOID(String formOID) {
		this.formOID = formOID;
	}

	@XmlElement(name = "ItemGroupData", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	public ArrayList<ImportItemGroupDataBean> getItemGroupData() {
		return itemGroupData;
	}

	public void setItemGroupData(ArrayList<ImportItemGroupDataBean> itemGroupData) {
		this.itemGroupData = itemGroupData;
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

	public Set<Integer> getPartialSectionIds() {
		return partialSectionIds;
	}
}
