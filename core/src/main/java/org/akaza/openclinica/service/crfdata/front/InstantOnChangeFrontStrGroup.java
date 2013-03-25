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
 * copyright 2003-2011 Akaza Research
 */
package org.akaza.openclinica.service.crfdata.front;

import java.io.Serializable;

/**
 * <p>
 * Group Destinations of an origin into different InstantOnChangeFrontStr for func:onChange().
 * </p>
 */
// ywang (Aug., 2011)
public class InstantOnChangeFrontStrGroup implements Serializable {
	private static final long serialVersionUID = 2200616942101253858L;

	int originItemId = 0;
	/**
	 * Destination belong to the same repeating group.
	 */
	InstantOnChangeFrontStr sameRepGrpFrontStr = new InstantOnChangeFrontStr();
	/**
	 * Destination is a non-repeating group item or an unGrouped item.
	 */
	InstantOnChangeFrontStr nonRepFrontStr = new InstantOnChangeFrontStr();

	public void setOriginItemId(int originItemId) {
		this.originItemId = originItemId;
	}

	public int getOriginItemId() {
		return originItemId;
	}

	public InstantOnChangeFrontStr getSameRepGrpFrontStr() {
		return sameRepGrpFrontStr;
	}

	public void setSameRepGrpFrontStr(InstantOnChangeFrontStr sameRepGrpFrontStr) {
		this.sameRepGrpFrontStr = sameRepGrpFrontStr;
	}

	public InstantOnChangeFrontStr getNonRepFrontStr() {
		return nonRepFrontStr;
	}

	public void setNonRepFrontStr(InstantOnChangeFrontStr nonRepFrontStr) {
		this.nonRepFrontStr = nonRepFrontStr;
	}

}
