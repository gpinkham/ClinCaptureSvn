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
package org.akaza.openclinica.bean.service;

import org.akaza.openclinica.bean.core.AuditableEntityBean;

@SuppressWarnings("serial")
public class StudyParameter extends AuditableEntityBean {
	/*
	 * study_parameter_id serial NOT NULL, handle varchar(50), name varchar(50), description varchar(255), default_value
	 * varchar(50), inheritable bool DEFAULT true, overridable bool,
	 */
	private String handle;
	private String name;
	private String description;
	private String defaultValue;
	private boolean inheritable;
	private boolean overridable;
	private Integer systemGroupId;
	private String controlType;
	private String controlValues;
	private int controlSize;
	private String crc;
	private String investigator;
	private String monitor;
	private String admin;
	private String root;

	public StudyParameter() {
		handle = "";
		name = "";
		description = "";
		defaultValue = "";
		inheritable = true;
		overridable = false;
	}

	/**
	 * @return Returns the defaultValue.
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @param defaultValue
	 *            The defaultValue to set.
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return Returns the handle.
	 */
	public String getHandle() {
		return handle;
	}

	/**
	 * @param handle
	 *            The handle to set.
	 */
	public void setHandle(String handle) {
		this.handle = handle;
	}

	/**
	 * @return Returns the inheritable.
	 */
	public boolean isInheritable() {
		return inheritable;
	}

	/**
	 * @param inheritable
	 *            The inheritable to set.
	 */
	public void setInheritable(boolean inheritable) {
		this.inheritable = inheritable;
	}

	/**
	 * @return Returns the name.
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            The name to set.
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Returns the overridable.
	 */
	public boolean isOverridable() {
		return overridable;
	}

	/**
	 * @param overridable
	 *            The overridable to set.
	 */
	public void setOverridable(boolean overridable) {
		this.overridable = overridable;
	}

	/**
	 * @return the systemGroupId
	 */
	public Integer getSystemGroupId() {
		return systemGroupId;
	}

	/**
	 * @param systemGroupId
	 *            the systemGroupId to set
	 */
	public void setSystemGroupId(Integer systemGroupId) {
		this.systemGroupId = systemGroupId;
	}

	/**
	 * @return the type
	 */
	public String getControlType() {
		return controlType;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setControlType(String type) {
		this.controlType = type;
	}

	/**
	 * @return the typeValues
	 */
	public String getControlValues() {
		return controlValues;
	}

	/**
	 * @param typeValues
	 *            the typeValues to set
	 */
	public void setControlValues(String typeValues) {
		this.controlValues = typeValues;
	}

	/**
	 * @return the controlSize
	 */
	public int getControlSize() {
		return controlSize;
	}

	/**
	 * @param controlSize
	 *            the controlSize to set
	 */
	public void setControlSize(int controlSize) {
		this.controlSize = controlSize;
	}

	/**
	 * @return the crc
	 */
	public String getCrc() {
		return crc;
	}

	/**
	 * @param crc
	 *            the crc to set
	 */
	public void setCrc(String crc) {
		this.crc = crc;
	}

	/**
	 * @return the investigator
	 */
	public String getInvestigator() {
		return investigator;
	}

	/**
	 * @param investigator
	 *            the investigator to set
	 */
	public void setInvestigator(String investigator) {
		this.investigator = investigator;
	}

	/**
	 * @return the monitor
	 */
	public String getMonitor() {
		return monitor;
	}

	/**
	 * @param monitor
	 *            the monitor to set
	 */
	public void setMonitor(String monitor) {
		this.monitor = monitor;
	}

	/**
	 * @return the admin
	 */
	public String getAdmin() {
		return admin;
	}

	/**
	 * @param admin
	 *            the admin to set
	 */
	public void setAdmin(String admin) {
		this.admin = admin;
	}

	/**
	 * @return the root
	 */
	public String getRoot() {
		return root;
	}

	/**
	 * @param root
	 *            the root to set
	 */
	public void setRoot(String root) {
		this.root = root;
	}

}
