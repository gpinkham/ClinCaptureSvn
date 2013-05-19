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
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.managestudy;

import org.akaza.openclinica.bean.core.AuditableEntityBean;

import java.util.ArrayList;

@SuppressWarnings({"rawtypes", "unchecked", "serial"})
public class DisplayStudySubjectBean extends AuditableEntityBean {
	private StudySubjectBean studySubject;
	private ArrayList studyGroups;
	private ArrayList<StudyEventBean> studyEvents;

	private int sedId;

	private boolean isStudySignable = true;

	private String siteName = "";

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public boolean getIsStudySignable() {
		return isStudySignable;
	}

	public void setStudySignable(boolean studySignable) {
		isStudySignable = studySignable;
	}

	/**
	 * 
	 * @return sedId
	 */
	public int getSedId() {
		return sedId;
	}

	/**
	 * 
	 * @param sedId
	 */
	public void setSedId(int sedId) {
		this.sedId = sedId;
	}

	/**
	 * @return Returns the studyEvents.
	 */
	public ArrayList getStudyEvents() {
		return studyEvents;
	}

	/**
	 * @param studyEvents
	 *            The studyEvents to set.
	 */
	public void setStudyEvents(ArrayList studyEvents) {
		this.studyEvents = studyEvents;
	}

	/**
	 * @return Returns the studySubject.
	 */
	public StudySubjectBean getStudySubject() {
		return studySubject;
	}

	/**
	 * @param studySubject
	 *            The studySubject to set.
	 */
	public void setStudySubject(StudySubjectBean studySubject) {
		this.studySubject = studySubject;
	}

	/**
	 * @return Returns the studyGroups.
	 */
	public ArrayList getStudyGroups() {
		return studyGroups;
	}

	/**
	 * @param studyGroups
	 *            The studyGroups to set.
	 */
	public void setStudyGroups(ArrayList studyGroups) {
		this.studyGroups = studyGroups;
	}
}
