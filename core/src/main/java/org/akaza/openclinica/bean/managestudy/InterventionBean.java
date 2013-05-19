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

/**
 * @author jxu Intervention object
 */
public class InterventionBean {
	private int id;
	private String name;
	private String type;
	private static int count = 0;

	public InterventionBean(String type, String name) {
		setName(name);
		setType(type);
		setId();
	}

	/**
	 * @return Returns the name.
	 */
	public int getId() {
		return id;
	}

	public void setId() {
		this.id = count++;

	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            The type to set.
	 */
	public void setType(String type) {
		this.type = type;
	}

	public boolean equals(InterventionBean ib) {
		return type.equalsIgnoreCase(ib.getType()) && name.equalsIgnoreCase(ib.getName());
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public String toString() {
		return getType() + "/" + getName();

	}
}
