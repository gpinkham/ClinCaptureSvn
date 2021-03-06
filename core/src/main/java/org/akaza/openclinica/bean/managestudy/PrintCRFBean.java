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

package org.akaza.openclinica.bean.managestudy;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.DisplaySectionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;

import java.util.List;

@SuppressWarnings("rawtypes")
public class PrintCRFBean {
	private StudyEventBean studyEventBean;
	private CRFBean crfBean;
	private CRFVersionBean crfVersionBean;
	private EventCRFBean eventCrfBean;
	private DisplaySectionBean displaySectionBean;
	private List displaySectionBeans;
	private List allSections;
	private boolean grouped;

	public boolean isGrouped() {
		return grouped;
	}

	public void setGrouped(boolean grouped) {
		this.grouped = grouped;
	}

	public StudyEventBean getStudyEventBean() {
		return studyEventBean;
	}

	public void setStudyEventBean(StudyEventBean studyEventBean) {
		this.studyEventBean = studyEventBean;
	}

	public CRFBean getCrfBean() {
		return crfBean;
	}

	public void setCrfBean(CRFBean crfBean) {
		this.crfBean = crfBean;
	}

	public CRFVersionBean getCrfVersionBean() {
		return crfVersionBean;
	}

	public void setCrfVersionBean(CRFVersionBean crfVersionBean) {
		this.crfVersionBean = crfVersionBean;
	}

	public EventCRFBean getEventCrfBean() {
		return eventCrfBean;
	}

	public void setEventCrfBean(EventCRFBean eventCrfBean) {
		this.eventCrfBean = eventCrfBean;
	}

	public DisplaySectionBean getDisplaySectionBean() {
		return displaySectionBean;
	}

	public void setDisplaySectionBean(DisplaySectionBean displaySectionBean) {
		this.displaySectionBean = displaySectionBean;
	}

	public List getDisplaySectionBeans() {
		return displaySectionBeans;
	}

	public void setDisplaySectionBeans(List displaySectionBeans) {
		this.displaySectionBeans = displaySectionBeans;
	}

	public List getAllSections() {
		return allSections;
	}

	public void setAllSections(List allSections) {
		this.allSections = allSections;
	}
}
