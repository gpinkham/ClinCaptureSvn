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
package org.akaza.openclinica.bean.submit;

import org.akaza.openclinica.bean.core.AuditableEntityBean;

/**
 * <P>
 * ItemDataBean.java, the object that represents an actual answer, or a point of data, in the database.
 * 
 * @author thickerson
 * 
 * 
 */
@SuppressWarnings("serial")
public class ItemDataBean extends AuditableEntityBean {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (auditLog ? 1231 : 1237);
		result = prime * result + eventCRFId;
		result = prime * result + itemId;
		result = prime * result + ordinal;
		result = prime * result + (selected ? 1231 : 1237);
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ItemDataBean other = (ItemDataBean) obj;
		if (auditLog != other.auditLog)
			return false;
		if (eventCRFId != other.eventCRFId)
			return false;
		if (itemId != other.itemId)
			return false;
		if (ordinal != other.ordinal)
			return false;
		if (selected != other.selected)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	private int eventCRFId;
	private int itemId;
	private String value;// name will be null

	private int ordinal;// for repeating items

	private boolean selected;// for construct data only

	private boolean auditLog = false;

	public ItemDataBean copy() {
		ItemDataBean idb = new ItemDataBean();
		idb.setAuditLog(isAuditLog());
		idb.setEventCRFId(getEventCRFId());
		idb.setItemId(getItemId());
		idb.setOrdinal(getOrdinal());
		idb.setSelected(isSelected());
		idb.setValue(getValue());
		idb.setActive(isActive());
		idb.setCreatedDate(getCreatedDate());
		idb.setId(getId());
		idb.setName(getName());
		idb.setOldStatus(getOldStatus());
		if (owner != null) {
			idb.setOwner(owner);
		}
		idb.setStatus(getStatus());
		idb.setUpdatedDate(getUpdatedDate());
		if (updater != null) {
			idb.setUpdater(updater);
		}
		return idb;
	}

	public ItemDataBean() {
		eventCRFId = 0;
		itemId = 0;
		value = "";
		ordinal = 1;
		selected = false;
		auditLog = false;
	}

	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected
	 *            the selected to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * @return Returns the itemId.
	 */
	public int getItemId() {
		return itemId;
	}

	/**
	 * @param itemId
	 *            The itemId to set.
	 */
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	/**
	 * @return Returns the eventCRFId.
	 */
	public int getEventCRFId() {
		return eventCRFId;
	}

	/**
	 * @param eventCRFId
	 *            The eventCRFId to set.
	 */
	public void setEventCRFId(int eventCRFId) {
		this.eventCRFId = eventCRFId;
	}

	/**
	 * @return Returns the value.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            The value to set.
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the ordinal
	 */
	public int getOrdinal() {
		return ordinal;
	}

	/**
	 * @param ordinal
	 *            the ordinal to set
	 */
	public void setOrdinal(int ordinal) {
		this.ordinal = ordinal;
	}

	public boolean isAuditLog() {
		return auditLog;
	}

	public void setAuditLog(boolean auditLog) {
		this.auditLog = auditLog;
	}
}
