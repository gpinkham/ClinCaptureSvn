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
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.submit;

import org.akaza.openclinica.bean.core.EntityBean;

import java.util.ArrayList;

/**
 * @author jxu
 * 
 *         TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style -
 *         Code Templates
 */
public class DisplaySubjectBean extends EntityBean {
	private SubjectBean subject;
	private ArrayList studySubjects;
	private String studySubjectIds;

	/**
	 * @return Returns the studySubjectIds.
	 */
	public String getStudySubjectIds() {
		return studySubjectIds;
	}

	/**
	 * @param studySubjectIds
	 *            The studySubjectIds to set.
	 */
	public void setStudySubjectIds(String studySubjectIds) {
		this.studySubjectIds = studySubjectIds;
	}

	/**
	 * @return Returns the studySubjects.
	 */
	public ArrayList getStudySubjects() {
		return studySubjects;
	}

	/**
	 * @param studySubjects
	 *            The studySubjects to set.
	 */
	public void setStudySubjects(ArrayList studySubjects) {
		this.studySubjects = studySubjects;
	}

	/**
	 * @return Returns the subject.
	 */
	public SubjectBean getSubject() {
		return subject;
	}

	/**
	 * @param subject
	 *            The subject to set.
	 */
	public void setSubject(SubjectBean subject) {
		this.subject = subject;
	}
}
