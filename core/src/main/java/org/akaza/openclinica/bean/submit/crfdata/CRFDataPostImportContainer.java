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

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * CRFDataPostImportContainer, meant to serve as the 'ClinicalData' tag in CRF Data Import. Will contain the following:
 * -- SubjectData -- StudyEventData -- FormData -- ItemGroupData -- ItemData Note that each list will have 1 to n
 * elements, and each element is contained inside its parent element.
 * 
 * @author thickerson, 04/2008
 * 
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(namespace="http://www.cdisc.org/ns/odm/v1.3")
public class CRFDataPostImportContainer {
	
	private ArrayList<SubjectDataBean> subjectData;
	private String studyOID;
	
	@XmlAttribute(name = "StudyOID")
	public String getStudyOID() {
		return studyOID;
	}

	public void setStudyOID(String studyOID) {
		this.studyOID = studyOID;
	}
	
	@XmlElement(name = "SubjectData", namespace="http://www.cdisc.org/ns/odm/v1.3")
	public ArrayList<SubjectDataBean> getSubjectData() {
		return subjectData;
	}

	public void setSubjectData(ArrayList<SubjectDataBean> subjectData) {
		this.subjectData = subjectData;
	}

}
