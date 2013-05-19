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
 * For details see: http://www.openclinica.org/license copyright 2003-2005 Akaza
 * Research
 *
 */

package org.akaza.openclinica.bean.odmbeans;

/**
 * 
 * @author ywang (May, 2008)
 * 
 */
public class OdmStudyBean extends ElementOIDBean {
	private GlobalVariablesBean globalVariables;
	private BasicDefinitionsBean basicDefinitions;
	private MetaDataVersionBean metaDataVersion;

	private String parentStudyOID;

	public OdmStudyBean() {
		globalVariables = new GlobalVariablesBean();
		basicDefinitions = new BasicDefinitionsBean();
		metaDataVersion = new MetaDataVersionBean();
	}

	public void setGlobalVariables(GlobalVariablesBean gv) {
		this.globalVariables = gv;
	}

	public GlobalVariablesBean getGlobalVariables() {
		return this.globalVariables;
	}

	public void setMetaDataVersion(MetaDataVersionBean metadataversion) {
		this.metaDataVersion = metadataversion;
	}

	public MetaDataVersionBean getMetaDataVersion() {
		return this.metaDataVersion;
	}

	public BasicDefinitionsBean getBasicDefinitions() {
		return basicDefinitions;
	}

	public void setBasicDefinitions(BasicDefinitionsBean basicDefinitions) {
		this.basicDefinitions = basicDefinitions;
	}

	public String getParentStudyOID() {
		return parentStudyOID;
	}

	public void setParentStudyOID(String parentStudyOID) {
		this.parentStudyOID = parentStudyOID;
	}
}
