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
 *
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.domain.rule.action;

import org.akaza.openclinica.domain.AbstractMutableDomainObject;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author Krikor Krumlian
 */

@Entity
@Table(name = "rule_action_run")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = { @Parameter(name = "sequence", value = "rule_action_run_id_seq") })
public class RuleActionRunBean extends AbstractMutableDomainObject {

	Boolean administrativeDataEntry;
	Boolean initialDataEntry;
	Boolean doubleDataEntry;
	Boolean importDataEntry;
	Boolean batch;

	public RuleActionRunBean() {
		// TODO Auto-generated constructor stub
		this.administrativeDataEntry = true;
		this.initialDataEntry = true;
		this.doubleDataEntry = true;
		this.importDataEntry = false;
		this.batch = true;
	}

	public RuleActionRunBean(Boolean administrativeDataEntry, Boolean initialDataEntry, Boolean doubleDataEntry,
			Boolean importDataEntry, Boolean batch) {
		super();
		this.administrativeDataEntry = administrativeDataEntry;
		this.initialDataEntry = initialDataEntry;
		this.doubleDataEntry = doubleDataEntry;
		this.importDataEntry = importDataEntry;
		this.batch = batch;
	}

	public enum Phase {
		ADMIN_EDITING, INITIAL_DATA_ENTRY, DOUBLE_DATA_ENTRY, IMPORT, BATCH
	}

	public Boolean getAdministrativeDataEntry() {
		return administrativeDataEntry;
	}

	public void setAdministrativeDataEntry(Boolean administrativeDataEntry) {
		this.administrativeDataEntry = administrativeDataEntry;
	}

	public Boolean getInitialDataEntry() {
		return initialDataEntry;
	}

	public void setInitialDataEntry(Boolean initialDataEntry) {
		this.initialDataEntry = initialDataEntry;
	}

	public Boolean getDoubleDataEntry() {
		return doubleDataEntry;
	}

	public void setDoubleDataEntry(Boolean doubleDataEntry) {
		this.doubleDataEntry = doubleDataEntry;
	}

	public Boolean getImportDataEntry() {
		return importDataEntry;
	}

	public void setImportDataEntry(Boolean importDataEntry) {
		this.importDataEntry = importDataEntry;
	}

	public Boolean getBatch() {
		return batch;
	}

	public void setBatch(Boolean batch) {
		this.batch = batch;
	}

	@Transient
	public String getRunActionRunsForDisplay() {

		ArrayList<String> r = new ArrayList<String>();
		if (getAdministrativeDataEntry() == true)
			r.add("Administrative Data Entry");
		if (getInitialDataEntry() == true)
			r.add("Initial Data Entry");
		if (getDoubleDataEntry() == true)
			r.add("Double Data Entry");
		if (getImportDataEntry() == true)
			r.add("Import Data Entry");
		if (getBatch() == true)
			r.add("Batch");
		return seperateStringBasedListBy(r, ", ", "");

	}

	@Transient
	private String seperateStringBasedListBy(ArrayList<String> list, String seperator, String terminator) {
		StringBuffer sb = new StringBuffer();
		if (list.size() == 0)
			return sb.toString();
		if (list.size() == 1) {
			sb.append(list.get(0));
		} else {
			for (int i = 0; i < list.size() - 1; i++) {
				sb.append(list.get(i) + seperator);
			}
			sb.append(list.get(list.size() - 1));
		}
		sb.append(terminator);
		return sb.toString();

	}

	@Transient
	public Boolean canRun(Phase phase) {
		switch (phase) {
		case ADMIN_EDITING: {
			return getAdministrativeDataEntry();
		}
		case INITIAL_DATA_ENTRY: {
			return getInitialDataEntry();
		}
		case DOUBLE_DATA_ENTRY: {
			return getDoubleDataEntry();
		}
		case IMPORT: {
			return getImportDataEntry();
		}
		case BATCH: {
			return getBatch();
		}

		default:
			return false;
		}

	}

}
