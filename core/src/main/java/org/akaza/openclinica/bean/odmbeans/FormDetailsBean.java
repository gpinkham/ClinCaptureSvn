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
 * OpenClinica is distributed under the GNU Lesser General Public License (GNU
 * LGPL).
 *
 * For details see: http://www.openclinica.org/license copyright 
 *
 */

package org.akaza.openclinica.bean.odmbeans;

import java.util.ArrayList;

public class FormDetailsBean extends ElementDefBean {
	// attributes
	private String parentFormOid;
	/*
	 * private String isDefaultVersion; private String nullValues; private String passwordRequired; private String
	 * doubleDataEntry; private String hideCrf; private String sourceDataVerification;
	 */
	// elements
	private String versionDescription;
	private String revisionNotes;
	private ArrayList<PresentInEventDefinitionBean> presentInEventDefinitions = new ArrayList<PresentInEventDefinitionBean>();

	public String getParentFormOid() {
		return parentFormOid;
	}

	public void setParentFormOid(String parentFormOid) {
		this.parentFormOid = parentFormOid;
	}

	/*
	 * public String getIsDefaultVersion() { return isDefaultVersion; } public void setIsDefaultVersion(String
	 * isDefaultVersion) { this.isDefaultVersion = isDefaultVersion; } public String getNullValues() { return
	 * nullValues; } public void setNullValues(String nullValues) { this.nullValues = nullValues; } public String
	 * getPasswordRequired() { return passwordRequired; } public void setPasswordRequired(String passwordRequired) {
	 * this.passwordRequired = passwordRequired; } public String getDoubleDataEntry() { return doubleDataEntry; } public
	 * void setDoubleDataEntry(String doubleDataEntry) { this.doubleDataEntry = doubleDataEntry; } public String
	 * getHideCrf() { return hideCrf; } public void setHideCrf(String hideCrf) { this.hideCrf = hideCrf; } public String
	 * getSourceDataVerification() { return sourceDataVerification; } public void setSourceDataVerification(String
	 * sourceDataVerification) { this.sourceDataVerification = sourceDataVerification; }
	 */
	public String getVersionDescription() {
		return versionDescription;
	}

	public void setVersionDescription(String versionDescription) {
		this.versionDescription = versionDescription;
	}

	public String getRevisionNotes() {
		return revisionNotes;
	}

	public void setRevisionNotes(String revisionNotes) {
		this.revisionNotes = revisionNotes;
	}

	public ArrayList<PresentInEventDefinitionBean> getPresentInEventDefinitions() {
		return presentInEventDefinitions;
	}

	public void setPresentInEventDefinitions(ArrayList<PresentInEventDefinitionBean> presentInEventDefinitions) {
		this.presentInEventDefinitions = presentInEventDefinitions;
	}
}
