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
 *
 * Copyright 2003-2009 Akaza Research 
 */
package org.akaza.openclinica.bean.managestudy;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.login.UserAccountBean;

import java.util.Date;

/**
 * @author Krikor Krumlian
 */
public class SubjectTransferBean extends EntityBean {

	private static final long serialVersionUID = 2270466335721404526L;
	private StudyBean study;
	private String personId;
	private String studySubjectId;
	private Date dateOfBirth;
	private String yearOfBirth;
	private char gender;
	private String studyOid;
	private String studyUniqueIdentifier;
	private Date dateReceived;
	private Date enrollmentDate;
	private String secondaryId;
	private String siteIdentifier;

	UserAccountBean owner;

	public SubjectTransferBean() {
		// TODO Auto-generated constructor stub
	}

	public SubjectTransferBean(String personId, String studySubjectId, Date dateOfBirth, char gender, String studyOid) {
		super();
		this.personId = personId;
		this.studySubjectId = studySubjectId;
		this.dateOfBirth = dateOfBirth;
		this.gender = gender;
		this.studyOid = studyOid;
		this.dateReceived = new Date();
	}

	public String getPersonId() {
		return personId;
	}

	public void setPersonId(String personId) {
		this.personId = personId;
	}

	public String getStudySubjectId() {
		return studySubjectId;
	}

	public void setStudySubjectId(String studySubjectId) {
		this.studySubjectId = studySubjectId;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public char getGender() {
		return gender;
	}

	public void setGender(char gender) {
		this.gender = gender;
	}

	public String getStudyOid() {
		return studyOid;
	}

	public void setStudyOid(String studyOid) {
		this.studyOid = studyOid;
	}

	public Date getDateReceived() {
		return dateReceived;
	}

	public void setDateReceived(Date dateReceived) {
		this.dateReceived = dateReceived;
	}

	public UserAccountBean getOwner() {
		return owner;
	}

	public void setOwner(UserAccountBean owner) {
		this.owner = owner;
	}

	public Date getEnrollmentDate() {
		return enrollmentDate;
	}

	public void setEnrollmentDate(Date enrollmentDate) {
		this.enrollmentDate = enrollmentDate;
	}

	public String getYearOfBirth() {
		return yearOfBirth;
	}

	public void setYearOfBirth(String yearOfBirth) {
		this.yearOfBirth = yearOfBirth;
	}

	public String getStudyUniqueIdentifier() {
		return studyUniqueIdentifier;
	}

	public void setStudyUniqueIdentifier(String studyUniqueIdentifier) {
		this.studyUniqueIdentifier = studyUniqueIdentifier;
	}

	public String getSecondaryId() {
		return secondaryId;
	}

	public void setSecondaryId(String secondaryId) {
		this.secondaryId = secondaryId;
	}

	public String getSiteIdentifier() {
		return siteIdentifier;
	}

	public void setSiteIdentifier(String siteIdentifier) {
		this.siteIdentifier = siteIdentifier;
	}

	public StudyBean getStudy() {
		return study;
	}

	public void setStudy(StudyBean study) {
		this.study = study;
	}

}
