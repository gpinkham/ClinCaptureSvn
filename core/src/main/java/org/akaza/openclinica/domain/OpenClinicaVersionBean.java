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

/* 
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.domain;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * <p>
 * OpenClinica Version
 * </p>
 * 
 * @author Pradnya Gawade
 */
@Entity
@Table(name = "openclinica_version")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = { @Parameter(name = "sequence", value = "openclinica_version_id_seq") })
public class OpenClinicaVersionBean extends AbstractMutableDomainObject {

	private String name;
	private String build_number;
	private Timestamp update_timestamp;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the test_path
	 */
	public String getBuild_number() {
		return build_number;
	}

	/**
	 * @param test_path
	 *            the test_path to set
	 */
	public void setBuild_number(String build_number) {
		this.build_number = build_number;
	}

	/**
	 * @return the update_timestamp
	 */
	public Timestamp getUpdate_timestamp() {
		return update_timestamp;
	}

	/**
	 * @param update_timestamp
	 *            the update_timestamp to set
	 */
	public void setUpdate_timestamp(Timestamp update_timestamp) {
		this.update_timestamp = update_timestamp;
	}

}
