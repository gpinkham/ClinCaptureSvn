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
 * OpenClinica is distributed under the GNU Lesser General Public License (GNU
 * LGPL).
 *
 * For details see: http://www.openclinica.org/license copyright 2003-2010 Akaza
 * Research
 *
 */

package org.akaza.openclinica.bean.odmbeans;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author ywang (March, 2010)
 * 
 */
public class OdmAdminDataBean extends ElementOIDBean {
	private String studyOID;
	private String metaDataVersionOID;
	private List<UserBean> users = new ArrayList<UserBean>();
	private List<LocationBean> locations = new ArrayList<LocationBean>();

	public List<UserBean> getUsers() {
		return users;
	}

	public void setUsers(ArrayList<UserBean> users) {
		this.users = users;
	}

	public List<LocationBean> getLocations() {
		return locations;
	}

	public void setLocations(ArrayList<LocationBean> locations) {
		this.locations = locations;
	}

	public String getStudyOID() {
		return studyOID;
	}

	public void setStudyOID(String studyOID) {
		this.studyOID = studyOID;
	}

	public String getMetaDataVersionOID() {
		return metaDataVersionOID;
	}

	public void setMetaDataVersionOID(String metaDataVersionOID) {
		this.metaDataVersionOID = metaDataVersionOID;
	}
}
