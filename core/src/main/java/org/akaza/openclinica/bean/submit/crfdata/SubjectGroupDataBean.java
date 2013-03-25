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
 * For details see: http://www.openclinica.org/license copyright 2003-2008 Akaza
 * Research
 *
 */

package org.akaza.openclinica.bean.submit.crfdata;

/**
 * 
 * @author ywang (Nov, 2008)
 * 
 */
public class SubjectGroupDataBean {
	private String studyGroupClassId;
	private String studyGroupClassName;
	private String studyGroupName;

	public void setStudyGroupClassId(String studyGroupClassId) {
		this.studyGroupClassId = studyGroupClassId;
	}

	public String getStudyGroupClassId() {
		return this.studyGroupClassId;
	}

	public void setStudyGroupClassName(String studyGroupClassName) {
		this.studyGroupClassName = studyGroupClassName;
	}

	public String getStudyGroupClassName() {
		return this.studyGroupClassName;
	}

	public void setStudyGroupName(String studyGroupName) {
		this.studyGroupName = studyGroupName;
	}

	public String getStudyGroupName() {
		return this.studyGroupName;
	}
}
