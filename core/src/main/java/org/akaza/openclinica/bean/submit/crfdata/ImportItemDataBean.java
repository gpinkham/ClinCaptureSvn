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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import javax.xml.bind.annotation.XmlRootElement;

import org.akaza.openclinica.bean.odmbeans.AuditLogsBean;
import org.akaza.openclinica.bean.odmbeans.DiscrepancyNotesBean;
import org.akaza.openclinica.bean.odmbeans.ElementRefBean;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(namespace="http://www.cdisc.org/ns/odm/v1.3")
public class ImportItemDataBean {
	
	private String itemOID;

	private String transactionType;

	private String value;

	private String isNull; // boolean, tbh?
	private ElementRefBean measurementUnitRef = new ElementRefBean();
	private String reasonForNull;
	private AuditLogsBean auditLogs = new AuditLogsBean();
	private DiscrepancyNotesBean discrepancyNotes = new DiscrepancyNotesBean();

    private boolean skip;
    
	private boolean hasValueWithNull; // this is just a flag, it is not an attribute/element
	
	@XmlAttribute(name = "ItemOID")
	public String getItemOID() {
		return itemOID;
	}

	public void setItemOID(String itemOID) {
		this.itemOID = itemOID;
	}
	
	@XmlAttribute(name = "TransactionType")
	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	
	@XmlAttribute(name = "Value")
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@XmlAttribute(name = "IsNull")
	public String getIsNull() {
		return isNull;
	}

	public void setIsNull(String isNull) {
		this.isNull = isNull;
	}

	public ElementRefBean getMeasurementUnitRef() {
		return measurementUnitRef;
	}

	public void setMeasurementUnitRef(ElementRefBean measurementUnitRef) {
		this.measurementUnitRef = measurementUnitRef;
	}

	public String getReasonForNull() {
		return reasonForNull;
	}

	public void setReasonForNull(String reasonForNull) {
		this.reasonForNull = reasonForNull;
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

	public boolean isHasValueWithNull() {
		return hasValueWithNull;
	}

	public void setHasValueWithNull(boolean hasValueWithNull) {
		this.hasValueWithNull = hasValueWithNull;
	}

	public boolean isSkip() {
		return skip;
	}

	public void setSkip(boolean skip) {
		this.skip = skip;
	}
}
