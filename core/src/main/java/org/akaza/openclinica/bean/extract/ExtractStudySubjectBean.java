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
 *
 * Created on Feb 23, 2005
 */

package org.akaza.openclinica.bean.extract;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;

import java.util.Date;
import java.util.HashMap;

/**
 * A study subject object for extracting data module
 * 
 * @author ssachs
 */
@SuppressWarnings({"rawtypes","unchecked", "serial"})
public class ExtractStudySubjectBean extends EntityBean {
	private String studyProtocolId;

	private String siteUniqueIdentifier;

	private Date dateOfBirth;

	private String gender;

	private String yearOfBirth;

	private HashMap studyEvents = new HashMap();

	/**
	 * Key is String comprised of studyEventDefinitionId + "-" + sampleNum + "-" + crfVersionId + "-" + itemId.
	 * (sampleNum goes from 1 to numSamples; it is drawn from the values of
	 * numSamplesByStudyEventDefinitionAndDBOrdinal) Value is String with corresponding item's value.
	 */
	private HashMap itemValues = new HashMap();

	/**
	 * Key is String comprised of studyEventDefinitionId + "-" + dbOrdinal (Note: dbOrdinal is a value drawn from the
	 * db; this indicates the number of events the subject has, regardless of the study event def.) Value is Integer
	 * whose intValue represents the sampleNum which corresponds to the StudyEventDefinition/dbOrdinal combination.
	 */
	private HashMap sampleNumByStudyEventDefinitionAndDBOrdinal = new HashMap();

	/**
	 * Key is an Integer whose intValue() is a studyEventDefinitionId Value is Integer whose intValue() represents the
	 * number of events the studySubject has for the given event def.
	 */
	private HashMap numSamplesByStudyEventDefinition = new HashMap();

	/**
	 * For SPSS coding; we generate a var-label listing for name and study label that will be populated by a numeric id
	 * when an SPSS set of files is generated by the system.
	 * 
	 */
	private int intNameResultSet = 0;
	private int intLabelResultSet = 0;

	public ExtractStudySubjectBean() {
		itemValues = new HashMap();
		studyProtocolId = "";
		siteUniqueIdentifier = "";
	}

	/**
	 * Generates the study label as it should be formatted.
	 * 
	 * @return <code>studyProtocolId</code> if <code>siteUniqueIdentifier.equals("")</code>,
	 *         <code>studyProtocolId + "-" + siteUniqueIdentifier</code> otherwise.
	 */
	public String getStudyLabel() {
		if (this.siteUniqueIdentifier.equals("")) {
			return this.studyProtocolId;
		} else {
			return this.studyProtocolId + "-" + this.siteUniqueIdentifier;
		}
	}

	public StudyEventBean getStudyEvent(int studyEventDefinitionId, int sampleOrdinal) {
		String key = getStudyEventsKey(studyEventDefinitionId, sampleOrdinal);
		if (studyEvents.containsKey(key)) {
			return (StudyEventBean) studyEvents.get(key);
		} else {
			return new StudyEventBean();
		}
	}

	private String getItemValuesKey(int studyEventDefinitionId, int sampleNum, int crfVersionId, int itemId) {
		return studyEventDefinitionId + "-" + sampleNum + "-" + crfVersionId + "-" + itemId;
	}

	private int getSampleNum(int studyEventDefinitionId, int dbOrdinal) {
		String key = studyEventDefinitionId + "-" + dbOrdinal;
		Integer sampleNum;

		if (!sampleNumByStudyEventDefinitionAndDBOrdinal.containsKey(key)) {
			int numSamples = getNumSamples(studyEventDefinitionId);
			sampleNum = Integer.valueOf(numSamples + 1);
			sampleNumByStudyEventDefinitionAndDBOrdinal.put(key, sampleNum);

			Integer numSamplesKey = Integer.valueOf(studyEventDefinitionId);
			numSamplesByStudyEventDefinition.put(numSamplesKey, sampleNum);
		} else {
			sampleNum = (Integer) sampleNumByStudyEventDefinitionAndDBOrdinal.get(key);
		}

		return sampleNum == null ? 0 : sampleNum.intValue();
	}

	/**
	 * Adds a study event for the subject.
	 * 
	 * @param location
	 * @param start
	 * @param end
	 * @return
	 */
	public StudyEventBean addStudyEvent(ExtractStudyEventDefinitionBean sedb, Integer idObj, String location,
			Date start, Date end, Integer dbOrdinalObj) {
		StudyEventBean seb = new StudyEventBean();

		if (idObj == null || location == null || start == null || end == null || dbOrdinalObj == null) {
			return seb;
		}

		int id = idObj.intValue();
		int dbOrdinal = dbOrdinalObj.intValue();

		seb.setLocation(location);
		seb.setDateStarted(start);
		seb.setDateStarted(end);
		seb.setId(id);
		seb.setSampleOrdinal(dbOrdinal);

		int sampleNum = getSampleNum(sedb.getId(), dbOrdinal);

		String key = getStudyEventsKey(sedb.getId(), sampleNum);
		studyEvents.put(key, seb);

		return seb;
	}

	public String getStudyEventsKey(int studyEventId, int sampleNum) {
		return studyEventId + "-" + sampleNum;
	}

	/**
	 * Adds an item value for the subject.
	 * 
	 * @param sedb
	 * @param dbOrdinalObj
	 * @param crfVersionIdObj
	 * @param itemIdObj
	 * @param itemValue
	 */
	public void addValue(ExtractStudyEventDefinitionBean sedb, Integer dbOrdinalObj, Integer crfVersionIdObj,
			Integer itemIdObj, String itemValue) {
		if (sedb == null || dbOrdinalObj == null || crfVersionIdObj == null || itemIdObj == null) {
			return;
		}

		int dbOrdinal = dbOrdinalObj.intValue();
		int crfVersionId = crfVersionIdObj.intValue();
		int itemId = itemIdObj.intValue();

		int sampleNum = getSampleNum(sedb.getId(), dbOrdinal);

		String key = getItemValuesKey(sedb.getId(), crfVersionId, sampleNum, itemId);
		itemValues.put(key, itemValue);
	}

	/**
	 * @param studyEventDefinitionId
	 * @param sampleOrdinal
	 * @param crfVersionId
	 * @param itemId
	 * @return The item value, if the subject has it, and "", otherwise.
	 */
	public String getValue(int studyEventDefinitionId, int sampleOrdinal, int crfVersionId, int itemId) {
		String key = getItemValuesKey(studyEventDefinitionId, crfVersionId, sampleOrdinal, itemId);
		if (itemValues.containsKey(key)) {
			return (String) itemValues.get(key);
		} else {
			return "";
		}
	}

	public int getNumSamples(int studyEventDefinitionId) {
		Integer key = Integer.valueOf(studyEventDefinitionId);

		if (numSamplesByStudyEventDefinition.containsKey(key)) {
			Integer numSamples = (Integer) numSamplesByStudyEventDefinition.get(key);
			if (numSamples != null) {
				return numSamples.intValue();
			}
		}

		return 0;
	}

	/**
	 * @return Returns the siteUniqueIdentifier.
	 */
	public String getSiteUniqueIdentifier() {
		return siteUniqueIdentifier;
	}

	/**
	 * @param siteUniqueIdentifier
	 *            The siteUniqueIdentifier to set.
	 */
	public void setSiteUniqueIdentifier(String siteUniqueIdentifier) {
		this.siteUniqueIdentifier = siteUniqueIdentifier;
	}

	/**
	 * @return Returns the studyProtocolId.
	 */
	public String getStudyProtocolId() {
		return studyProtocolId;
	}

	/**
	 * @param studyProtocolId
	 *            The studyProtocolId to set.
	 */
	public void setStudyProtocolId(String studyProtocolId) {
		this.studyProtocolId = studyProtocolId;
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
	 * @return Returns the gender.
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * @param gender
	 *            The gender to set.
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * @return Returns the yearOfBirth.
	 */
	public String getYearOfBirth() {
		return yearOfBirth;
	}

	/**
	 * @param yearOfBirth
	 *            The yearOfBirth to set.
	 */
	public void setYearOfBirth(String yearOfBirth) {
		this.yearOfBirth = yearOfBirth;
	}

	/**
	 * @return Returns the intLabelResultSet.
	 */
	public int getIntLabelResultSet() {
		return intLabelResultSet;
	}

	/**
	 * @param intLabelResultSet
	 *            The intLabelResultSet to set.
	 */
	public void setIntLabelResultSet(int intLabelResultSet) {
		this.intLabelResultSet = intLabelResultSet;
	}

	/**
	 * @return Returns the intNameResultSet.
	 */
	public int getIntNameResultSet() {
		return intNameResultSet;
	}

	/**
	 * @param intNameResultSet
	 *            The intNameResultSet to set.
	 */
	public void setIntNameResultSet(int intNameResultSet) {
		this.intNameResultSet = intNameResultSet;
	}
}
