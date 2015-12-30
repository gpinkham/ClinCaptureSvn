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
package org.akaza.openclinica.control.form;

import org.akaza.openclinica.bean.core.DiscrepancyNoteType;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * Provides for DiscrepancyNotes in CRF forms.
 * 
 */

public class FormDiscrepancyNotes {
	private Map<String, List<DiscrepancyNoteBean>> fieldNotes;
	private Map<String, Integer> numExistingFieldNotes;
	private Map<Integer, List<String>> idNotes;

	/**
	 * Initializes FormDiscrepancyNotes object.
	 */
	public FormDiscrepancyNotes() {
		fieldNotes = new HashMap<String, List<DiscrepancyNoteBean>>();
		numExistingFieldNotes = new HashMap<String, Integer>();
		idNotes = new HashMap<Integer, List<String>>();
	}

	/**
	 * Add DN to this FormDiscrepancyNotes.
	 * 
	 * @param field
	 *            Field name to map DN
	 * @param note
	 *            DN to add
	 */
	public void addNote(String field, DiscrepancyNoteBean note) {
		List<DiscrepancyNoteBean> notes;
		if (fieldNotes.containsKey(field)) {
			notes = fieldNotes.get(field);
		} else {
			notes = new ArrayList<DiscrepancyNoteBean>();
		}

		notes.add(note);
		fieldNotes.put(field, notes);
	}

	/**
	 * Want to map entity Id with field names So we know if an entity has discrepancy note giving entity id.
	 * 
	 * @param entityId
	 *            EntityId
	 * @param field
	 *            Field name
	 */
	public void addIdNote(int entityId, String field) {
		List<String> notes;
		if (idNotes.containsKey(entityId)) {
			notes = idNotes.get(entityId);
		} else {
			notes = new ArrayList<String>();
		}
		if (notes != null) {
			notes.add(field);
		}
		idNotes.put(new Integer(entityId), notes);
	}

	/**
	 * Check if FormDiscrepancyNotes has DN for specified field.
	 * 
	 * @param field
	 *            Field to check
	 * @return true if yes, false otherwise
	 */
	public boolean hasNote(String field) {
		List<DiscrepancyNoteBean> notes;
		if (fieldNotes.containsKey(field)) {
			notes = fieldNotes.get(field);
			return notes != null && notes.size() > 0;
		}
		return false;
	}

	/**
	 * Get notes associated with field.
	 * 
	 * @param field
	 *            Field to check
	 * @return List of associated notes
	 */
	public List<DiscrepancyNoteBean> getNotes(String field) {
		List<DiscrepancyNoteBean> notes;
		if (fieldNotes.containsKey(field)) {
			notes = fieldNotes.get(field);
		} else {
			notes = new ArrayList<DiscrepancyNoteBean>();
		}
		return notes;
	}

	/**
	 * Sets number of existing field notes.
	 * 
	 * @param field
	 *            Field to set for
	 * @param num
	 *            Number to set
	 */
	public void setNumExistingFieldNotes(String field, int num) {
		numExistingFieldNotes.put(field, new Integer(num));
	}

	/**
	 * Gets number of existing field notes for field.
	 * 
	 * @param field
	 *            Field to check for
	 * @return Number of existing field notes
	 */
	public int getNumExistingFieldNotes(String field) {
		if (numExistingFieldNotes.containsKey(field)) {
			Integer numInt = numExistingFieldNotes.get(field);
			if (numInt != null) {
				return numInt.intValue();
			}
		}
		return 0;
	}

	/**
	 * @return Returns the numExistingFieldNotes.
	 */
	public Map<String, Integer>  getNumExistingFieldNotes() {
		return numExistingFieldNotes;
	}

	/**
	 * @return the fieldNotes
	 */
	public Map<String, List<DiscrepancyNoteBean>> getFieldNotes() {
		return fieldNotes;
	}

	/**
	 * @param fieldNotes
	 *            the fieldNotes to set
	 */
	public void setFieldNotes(Map<String, List<DiscrepancyNoteBean>>  fieldNotes) {
		this.fieldNotes = fieldNotes;
	}

	/**
	 * @param numExistingFieldNotes
	 *            the numExistingFieldNotes to set
	 */
	public void setNumExistingFieldNotes(Map<String, Integer>  numExistingFieldNotes) {
		this.numExistingFieldNotes = numExistingFieldNotes;
	}

	/**
	 * @return the idNotes
	 */
	public Map<Integer, List<String>> getIdNotes() {
		return idNotes;
	}

	/**
	 * @param idNotes
	 *            the idNotes to set
	 */
	public void setIdNotes(Map<Integer, List<String>>  idNotes) {
		this.idNotes = idNotes;
	}

	/**
	 * Adds list of RFC DiscrepancyNotes to FormDiscrepancyNote object.
	 * 
	 * @param notes
	 *            list of RFCs
	 */
	public void addAutoRFCs(List<DiscrepancyNoteBean> notes) {
		for (DiscrepancyNoteBean note : notes) {
			if (this.hasNote(note.getField())) {
				if (!this.fieldHasRFC(note.getField())) {
					this.addNote(note.getField(), note);
				}
			} else {
				this.addNote(note.getField(), note);
			}
		}
	}

	private boolean fieldHasRFC(String field) {
		 List<DiscrepancyNoteBean> exitingNotes = this.getNotes(field);
		for (DiscrepancyNoteBean note : exitingNotes) {
			DiscrepancyNoteBean existingNote = note;
			if (existingNote.getDiscrepancyNoteTypeId() == DiscrepancyNoteType.REASON_FOR_CHANGE.getId()) {
				return true;
			}
		}
		return false;
	}
}