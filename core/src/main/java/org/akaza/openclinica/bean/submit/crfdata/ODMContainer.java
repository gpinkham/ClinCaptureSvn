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

package org.akaza.openclinica.bean.submit.crfdata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * ODM Container, the surrounding tag for Clinical Data together with meta data
 * 
 * @author thickerson, 04/2008
 * 
 */

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement (name = "ODM", namespace="http://www.cdisc.org/ns/odm/v1.3")
public class ODMContainer {
	
	
	private CRFDataPostImportContainer crfDataPostImportContainer;
	private String subjectUniqueIdentifier;
	private String studyUniqueIdentifier;
	
	@XmlElement(name = "ClinicalData", namespace="http://www.cdisc.org/ns/odm/v1.3", type=CRFDataPostImportContainer.class)
	public CRFDataPostImportContainer getCrfDataPostImportContainer() {
		return crfDataPostImportContainer;
	}

	public void setCrfDataPostImportContainer(CRFDataPostImportContainer crfDataPostImportContainer) {
		this.crfDataPostImportContainer = crfDataPostImportContainer;
	}

	public String getSubjectUniqueIdentifier() {
		return subjectUniqueIdentifier;
	}

	public void setSubjectUniqueIdentifier(String subjectUniqueIdentifier) {
		this.subjectUniqueIdentifier = subjectUniqueIdentifier;
	}

	public String getStudyUniqueIdentifier() {
		return studyUniqueIdentifier;
	}

	public void setStudyUniqueIdentifier(String studyUniqueIdentifier) {
		this.studyUniqueIdentifier = studyUniqueIdentifier;
	}

}
