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
 * For details see: http://www.openclinica.org/license copyright 2003-2008 Akaza
 * Research
 *
 */

package org.akaza.openclinica.bean.odmbeans;

import java.util.ArrayList;

/**
 * 
 * This is root bean for ODM XML document
 * 
 * @author ywang (May, 2008)
 * 
 */

public class ODMBean {
	private String description;
	private String fileType;
	private String fileOID;
	private String creationDateTime;
	private String ODMVersion;
	private ArrayList<String> xmlnsList;
	private String xsi;
	private String schemaLocation;

	// JN: to get only the clinical data
	private String odmType;

	public String getOdmType() {
		return odmType;
	}

	public void setOdmType(String odmType) {
		this.odmType = odmType;
	}

	/**
	 * In default constructor, xmlns = "http://www.cdisc.org/ns/odm/v1.2",
	 * xsi="http://www.w3.org/2001/XMLSchema-instance", schemaLocation="http://www.cdisc.org/ns/odm/v1.2 ODM1-2-1.xsd",
	 * fileType="Snapshot", ODMVersion="1.2"
	 */
	public ODMBean() {
		xmlnsList = new ArrayList<String>();
		xmlnsList.add("xmlns=\"http://www.cdisc.org/ns/odm/v1.2\"");
		xsi = "http://www.w3.org/2001/XMLSchema-instance";
		schemaLocation = "http://www.cdisc.org/ns/odm/v1.2 ODM1-2-1.xsd";
		fileType = "Snapshot";
		ODMVersion = "1.2";
		odmType = "Full";
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}

	public void setFileType(String filetype) {
		this.fileType = filetype;
	}

	public String getFileType() {
		return this.fileType;
	}

	public void setFileOID(String oid) {
		this.fileOID = oid;
	}

	public String getFileOID() {
		return this.fileOID;
	}

	public void setCreationDateTime(String odmDateTimeString) {
		this.creationDateTime = odmDateTimeString;
	}

	public String getCreationDateTime() {
		return this.creationDateTime;
	}

	public void setODMVersion(String version) {
		this.ODMVersion = version;
	}

	public String getODMVersion() {
		return this.ODMVersion;
	}

	public void setXmlnsList(ArrayList<String> xmlnsList) {
		this.xmlnsList = xmlnsList;
	}

	public ArrayList<String> getXmlnsList() {
		return this.xmlnsList;
	}

	public void setXsi(String xsi) {
		this.xsi = xsi;
	}

	public String getXsi() {
		return this.xsi;
	}

	public void setSchemaLocation(String schemalocation) {
		this.schemaLocation = schemalocation;
	}

	public String getSchemaLocation() {
		return this.schemaLocation;
	}
}
