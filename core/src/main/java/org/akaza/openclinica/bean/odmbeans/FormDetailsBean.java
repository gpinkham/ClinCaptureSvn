/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2015 Clinovo Inc.
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
 * For details see: http://www.openclinica.org/license copyright
 *
 */

package org.akaza.openclinica.bean.odmbeans;

import java.util.ArrayList;
import java.util.List;


public class FormDetailsBean extends ElementDefBean {

    private String parentFormOid;
    private String versionDescription;
    private String revisionNotes;
    private ArrayList<PresentInEventDefinitionBean> presentInEventDefinitions = new ArrayList<PresentInEventDefinitionBean>();

    private List<SectionDetails> sectionDetails;


    public ArrayList<SectionDetails> getSectionDetails() {
        return (ArrayList<org.akaza.openclinica.bean.odmbeans.SectionDetails>) sectionDetails;
    }

    public void setSectionDetails(List<SectionDetails> sectionDetails) {
        this.sectionDetails = sectionDetails;
    }

    public String getParentFormOid() {
        return parentFormOid;
    }

    public void setParentFormOid(String parentFormOid) {
        this.parentFormOid = parentFormOid;
    }

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