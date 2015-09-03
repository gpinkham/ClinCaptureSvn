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
package org.akaza.openclinica.bean.core;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * EntityBean.
 */
@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.FIELD)
public class EntityBean extends ExcelRowAware implements java.io.Serializable {

	public static final int INT_1231 = 1231;

	public static final int INT_1237 = 1237;

	// ss - changed visibility of these fields so Term could see them
	// think we should change all fields to protected here

	@JsonIgnore
	@XmlTransient
	protected String name;

	@JsonProperty("id")
	@XmlElement(name = "Id", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	protected int id;

	/**
	 * The setID method has changed so that if the id is greater than 0, active is set to true. This reflects our notion
	 * that an entity is active if it comes from the database, and otherwise inactive. Note however that if a bean is
	 * retrieved from the database, changed in the application, and then updated in the databse, it should be changed
	 * when it is changed � the notion being that the bean no longer reflects the current state of the database. The
	 * relevant DAO�s update method should set active to true again once the database has been successfully changed.
	 */
	@JsonIgnore
	@XmlTransient
	protected boolean active = false;

	/*
	 * private java.util.Date createdDate; private java.util.Date updatedDate; private Object owner;//to be replaced by
	 * UserBean, when written private Object updater;//to be replaced by UserBean
	 */

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (active ? INT_1231 : INT_1237);
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		EntityBean other = (EntityBean) obj;
		if (active != other.active) {
			return false;
		}
		if (id != other.id) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	/**
	 * EntityBean constructor.
	 */
	public EntityBean() {
		id = 0;
		name = "";
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return this.active;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	/**
	 * Method that sets id.
	 * 
	 * @param id
	 *            int
	 */
	public void setId(int id) {
		this.id = id;

		if (id > 0) {
			active = true;
		}
	}

	public int getId() {
		return this.id;
	}

}
