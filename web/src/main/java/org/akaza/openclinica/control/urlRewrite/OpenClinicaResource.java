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

/**
 * 
 */
package org.akaza.openclinica.control.urlRewrite;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pgawade This represents a resource which could be any OpenClinica object say study or study subject, study
 *         event definition etc. The object of this class will hold logical as well as internal database identifiers to
 *         these resources. Logical identifiers can be used to identify it as REST resource and internal database
 *         identifiers can be used to retrieve the object from database Depending on the type of resource, it will have
 *         values for the related attributes and other unrelated attributes will not hold any value
 */
public class OpenClinicaResource {
	private String StudyOID;
	private Integer StudyID;

	private String StudySubjectOID;
	private Integer StudySubjectID;

	private String StudyEventDefOID;
	private Integer StudyEventDefID;
	private Integer StudyEventRepeatKey;

	private String FormOID;
	private Integer FormID;

	private String FormVersionOID;
	private Integer FormVersionID;

	private String ItemGroupOID;
	private Integer ItemGroupID;
	private Integer ItemGroupRepeatKey;

	private String ItemOID;
	private Integer ItemID;

	/*
	 * Internal identifiers required for accessing the specific CRF data for a given subject
	 */
	private Integer studyEventId; /*
								 * represents a particular event scheduled with given event definition for a given
								 * subject
								 */
	private Integer eventDefinitionCrfId; /*
										 * represents the CRF identifier which it got when included in the given event
										 * definition
										 */
	private Integer eventCrfId;/*
								 * represents the identifier for the combination of studyEventId, eventDefinitionCrfId
								 * i.e. a CRF with data for a given subject for a given event
								 */
	private boolean isInValid; /* this is to validate the OID parameters specified to identify the resource are correct */

	private List<String> messages; /* this is to store the validation error messages */

	public OpenClinicaResource() {
		messages = new ArrayList<String>();
	}

	/**
	 * @return the studyOID
	 */
	public String getStudyOID() {
		return StudyOID;
	}

	/**
	 * @return the studyID
	 */
	public Integer getStudyID() {
		return StudyID;
	}

	/**
	 * @return the studySubjectOID
	 */
	public String getStudySubjectOID() {
		return StudySubjectOID;
	}

	/**
	 * @return the studySubjectID
	 */
	public Integer getStudySubjectID() {
		return StudySubjectID;
	}

	/**
	 * @return the studyEventDefOID
	 */
	public String getStudyEventDefOID() {
		return StudyEventDefOID;
	}

	/**
	 * @return the studyEventDefID
	 */
	public Integer getStudyEventDefID() {
		return StudyEventDefID;
	}

	/**
	 * @return the studyEventRepeatKey
	 */
	public Integer getStudyEventRepeatKey() {
		return StudyEventRepeatKey;
	}

	/**
	 * @return the formDefOID
	 */
	public String getFormOID() {
		return FormOID;
	}

	/**
	 * @return the formDefID
	 */
	public Integer getFormID() {
		return FormID;
	}

	/**
	 * @return the itemGroupOID
	 */
	public String getItemGroupOID() {
		return ItemGroupOID;
	}

	/**
	 * @return the itemGroupID
	 */
	public Integer getItemGroupID() {
		return ItemGroupID;
	}

	/**
	 * @return the itemGroupRepeatKey
	 */
	public Integer getItemGroupRepeatKey() {
		return ItemGroupRepeatKey;
	}

	/**
	 * @return the itemOID
	 */
	public String getItemOID() {
		return ItemOID;
	}

	/**
	 * @return the itemID
	 */
	public Integer getItemID() {
		return ItemID;
	}

	/**
	 * @param studyOID
	 *            the studyOID to set
	 */
	public void setStudyOID(String studyOID) {
		StudyOID = studyOID;
	}

	/**
	 * @param studyID
	 *            the studyID to set
	 */
	public void setStudyID(Integer studyID) {
		StudyID = studyID;
	}

	/**
	 * @param studySubjectOID
	 *            the studySubjectOID to set
	 */
	public void setStudySubjectOID(String studySubjectOID) {
		StudySubjectOID = studySubjectOID;
	}

	/**
	 * @param studySubjectID
	 *            the studySubjectID to set
	 */
	public void setStudySubjectID(Integer studySubjectID) {
		StudySubjectID = studySubjectID;
	}

	/**
	 * @param studyEventDefOID
	 *            the studyEventDefOID to set
	 */
	public void setStudyEventDefOID(String studyEventDefOID) {
		StudyEventDefOID = studyEventDefOID;
	}

	/**
	 * @param studyEventDefID
	 *            the studyEventDefID to set
	 */
	public void setStudyEventDefID(Integer studyEventDefID) {
		StudyEventDefID = studyEventDefID;
	}

	/**
	 * @param studyEventRepeatKey
	 *            the studyEventRepeatKey to set
	 */
	public void setStudyEventRepeatKey(Integer studyEventRepeatKey) {
		StudyEventRepeatKey = studyEventRepeatKey;
	}

	/**
	 * @param formOID
	 *            the formDefOID to set
	 */
	public void setFormOID(String formOID) {
		FormOID = formOID;
	}

	/**
	 * @param formID
	 *            the formDefID to set
	 */
	public void setFormID(Integer formID) {
		FormID = formID;
	}

	/**
	 * @param itemGroupOID
	 *            the itemGroupOID to set
	 */
	public void setItemGroupOID(String itemGroupOID) {
		ItemGroupOID = itemGroupOID;
	}

	/**
	 * @param itemGroupID
	 *            the itemGroupID to set
	 */
	public void setItemGroupID(Integer itemGroupID) {
		ItemGroupID = itemGroupID;
	}

	/**
	 * @param itemGroupRepeatKey
	 *            the itemGroupRepeatKey to set
	 */
	public void setItemGroupRepeatKey(Integer itemGroupRepeatKey) {
		ItemGroupRepeatKey = itemGroupRepeatKey;
	}

	/**
	 * @param itemOID
	 *            the itemOID to set
	 */
	public void setItemOID(String itemOID) {
		ItemOID = itemOID;
	}

	/**
	 * @param itemID
	 *            the itemID to set
	 */
	public void setItemID(Integer itemID) {
		ItemID = itemID;
	}

	/**
	 * @return the studyEventId
	 */
	public Integer getStudyEventId() {
		return studyEventId;
	}

	/**
	 * @return the eventDefinitionCrfId
	 */
	public Integer getEventDefinitionCrfId() {
		return eventDefinitionCrfId;
	}

	/**
	 * @return the eventCrfId
	 */
	public Integer getEventCrfId() {
		return eventCrfId;
	}

	/**
	 * @param studyEventId
	 *            the studyEventId to set
	 */
	public void setStudyEventId(Integer studyEventId) {
		this.studyEventId = studyEventId;
	}

	/**
	 * @param eventDefinitionCrfId
	 *            the eventDefinitionCrfId to set
	 */
	public void setEventDefinitionCrfId(Integer eventDefinitionCrfId) {
		this.eventDefinitionCrfId = eventDefinitionCrfId;
	}

	/**
	 * @param eventCrfId
	 *            the eventCrfId to set
	 */
	public void setEventCrfId(Integer eventCrfId) {
		this.eventCrfId = eventCrfId;
	}

	/**
	 * @return the formVersionOID
	 */
	public String getFormVersionOID() {
		return FormVersionOID;
	}

	/**
	 * @return the formVersionID
	 */
	public Integer getFormVersionID() {
		return FormVersionID;
	}

	/**
	 * @param formVersionOID
	 *            the formVersionOID to set
	 */
	public void setFormVersionOID(String formVersionOID) {
		FormVersionOID = formVersionOID;
	}

	/**
	 * @param formVersionID
	 *            the formVersionID to set
	 */
	public void setFormVersionID(Integer formVersionID) {
		FormVersionID = formVersionID;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("StudyOID: ").append(this.StudyOID).append("\n StudyID: ").append(this.StudyID)

		.append("\n StudySubjectOID: ").append(this.StudySubjectOID).append("\n StudySubjectID: ")
				.append(this.StudySubjectID)

				.append("\n StudyEventDefOID: ").append(this.StudyEventDefOID).append("\n StudyEventDefID: ")
				.append(this.StudyEventDefID).append("\n StudyEventRepeatKey: ").append(this.StudyEventRepeatKey)

				.append("\n FormOID: ").append(this.FormOID).append("\n FormID: ").append(this.FormID)

				.append("\n FormVersionOID: ").append(this.FormVersionOID).append("\n FormVersionID: ")
				.append(this.FormVersionID)

				.append("\n ItemGroupOID: ").append(this.ItemGroupOID).append("\n ItemGroupID: ")
				.append(this.ItemGroupID).append("\n ItemGroupRepeatKey: ").append(this.ItemGroupRepeatKey)

				.append("\n ItemOID: ").append(this.ItemOID).append("\n ItemID: ").append(this.ItemID);

		return sb.toString();
	}

	public boolean isInValid() {
		return isInValid;
	}

	public void setInValid(boolean isInValid) {
		this.isInValid = isInValid;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}

}
