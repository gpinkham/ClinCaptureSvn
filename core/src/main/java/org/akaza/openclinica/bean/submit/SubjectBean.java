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

import org.akaza.openclinica.bean.core.AuditableEntityBean;

import java.util.Date;

@SuppressWarnings("serial")
public class SubjectBean extends AuditableEntityBean {
	/*
	 * since we extend entity bean, we already have the following: subject_id, date_created, date_updated, update_id
	 */
	private int fatherId;
	private int motherId;
	private Date dateOfBirth;
	private char gender = 'm';
	private String uniqueIdentifier = "";
	/*
	 * tells that whether the dateOfBirth is a real birthday or only the year part is valid
	 */
	private boolean dobCollected;

	private String study_unique_identifier; // Not from subject table, used for display purposes
	private String label; // Not from subject...

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return this.label;
	}

	public void setStudyIdentifier(String studyId) {
		this.study_unique_identifier = studyId;
	}

	public String getStudyIdentifier() {
		return this.study_unique_identifier;
	}

	/**
	 * @return Returns the dateOfBirth.
	 */
	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	/**
	 * @param dateOfBirth
	 *            The dateOfBirth to set.
	 */
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	/**
	 * @return Returns the fatherId.
	 */
	public int getFatherId() {
		return fatherId;
	}

	/**
	 * @param fatherId
	 *            The fatherId to set.
	 */
	public void setFatherId(int fatherId) {
		this.fatherId = fatherId;
	}

	/**
	 * @return Returns the gender.
	 */
	public char getGender() {
		return gender;
	}

	/**
	 * @param gender
	 *            The gender to set.
	 */
	public void setGender(char gender) {
		this.gender = gender;
	}

	/**
	 * @return Returns the motherId.
	 */
	public int getMotherId() {
		return motherId;
	}

	/**
	 * @param motherId
	 *            The motherId to set.
	 */
	public void setMotherId(int motherId) {
		this.motherId = motherId;
	}

	/**
	 * @return Returns the uniqueIdentifier.
	 */
	public String getUniqueIdentifier() {
		return uniqueIdentifier;
	}

	/**
	 * @param uniqueIdentifier
	 *            The uniqueIdentifier to set.
	 */
	public void setUniqueIdentifier(String uniqueIdentifier) {
		this.uniqueIdentifier = uniqueIdentifier;
	}

	// disambiguate the meaning of superclass's "name" member
	@Override
	public String getName() {
		return getUniqueIdentifier();
	}

	@Override
	public void setName(String name) {
		setUniqueIdentifier(name);
	}

	/**
	 * @return Returns the dobCollected.
	 */
	public boolean isDobCollected() {
		return dobCollected;
	}

	/**
	 * @param dobCollected
	 *            The dobCollected to set.
	 */
	public void setDobCollected(boolean dobCollected) {
		this.dobCollected = dobCollected;
	}
}
