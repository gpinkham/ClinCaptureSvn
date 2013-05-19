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
 * copyright 2003-2011 Akaza Research
 */
package org.akaza.openclinica.service.crfdata.front;

import java.io.Serializable;
import java.util.Map;

/**
 * Collection of InstantOnChangeFrontStrGroup in a crf section.
 */
// ywang (Aug., 2011)
public class InstantOnChangeFrontStrParcel implements Serializable {
	private static final long serialVersionUID = -2240052862489035165L;

	int sectionId;
	int crfVersionId;
	/**
	 * String key = origin item-group-oid; Integer key = origin item_id
	 */
	Map<String, Map<Integer, InstantOnChangeFrontStrGroup>> repOrigins;
	/**
	 * Include both non-repeating group & Ungrouped. key = origin item_id
	 */
	Map<Integer, InstantOnChangeFrontStrGroup> nonRepOrigins;

	public int getSectionId() {
		return sectionId;
	}

	public void setSectionId(int sectionId) {
		this.sectionId = sectionId;
	}

	public int getCrfVersionId() {
		return crfVersionId;
	}

	public void setCrfVersionId(int crfVersionId) {
		this.crfVersionId = crfVersionId;
	}

	public Map<String, Map<Integer, InstantOnChangeFrontStrGroup>> getRepOrigins() {
		return repOrigins;
	}

	public void setRepOrigins(Map<String, Map<Integer, InstantOnChangeFrontStrGroup>> repOrigins) {
		this.repOrigins = repOrigins;
	}

	public Map<Integer, InstantOnChangeFrontStrGroup> getNonRepOrigins() {
		return nonRepOrigins;
	}

	public void setNonRepOrigins(Map<Integer, InstantOnChangeFrontStrGroup> nonRepOrigins) {
		this.nonRepOrigins = nonRepOrigins;
	}

}
