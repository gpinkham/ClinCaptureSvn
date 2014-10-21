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

/**
 * 
 * Provides for DiscrepancyNotes in CRF forms.
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class FormDiscrepancyNotes {
	private HashMap fieldNotes;
	private HashMap numExistingFieldNotes;
	private HashMap idNotes;

	/**
	 * Initializes FormDiscrepancyNotes object.
	 */
	public FormDiscrepancyNotes() {
		fieldNotes = new HashMap();
		numExistingFieldNotes = new HashMap();
		idNotes = new HashMap();
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
		ArrayList notes;
		if (fieldNotes.containsKey(field)) {
			notes = (ArrayList) fieldNotes.get(field);
		} else {
			notes = new ArrayList();
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
		ArrayList notes;
		if (idNotes.containsKey(entityId)) {
			notes = (ArrayList) fieldNotes.get(entityId);
		} else {
			notes = new ArrayList();
		}
		System.out.println("field:" + field);
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
		ArrayList notes;
		if (fieldNotes.containsKey(field)) {
			notes = (ArrayList) fieldNotes.get(field);
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
	public ArrayList getNotes(String field) {
		ArrayList notes;
		if (fieldNotes.containsKey(field)) {
			notes = (ArrayList) fieldNotes.get(field);
		} else {
			notes = new ArrayList();
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
			Integer numInt = (Integer) numExistingFieldNotes.get(field);
			if (numInt != null) {
				return numInt.intValue();
			}
		}
		return 0;
	}

	/**
	 * @return Returns the numExistingFieldNotes.
	 */
	public HashMap getNumExistingFieldNotes() {
		return numExistingFieldNotes;
	}

	/**
	 * @return the fieldNotes
	 */
	public HashMap getFieldNotes() {
		return fieldNotes;
	}

	/**
	 * @param fieldNotes
	 *            the fieldNotes to set
	 */
	public void setFieldNotes(HashMap fieldNotes) {
		this.fieldNotes = fieldNotes;
	}

	/**
	 * @param numExistingFieldNotes
	 *            the numExistingFieldNotes to set
	 */
	public void setNumExistingFieldNotes(HashMap numExistingFieldNotes) {
		this.numExistingFieldNotes = numExistingFieldNotes;
	}

	/**
	 * @return the idNotes
	 */
	public HashMap getIdNotes() {
		return idNotes;
	}

	/**
	 * @param idNotes
	 *            the idNotes to set
	 */
	public void setIdNotes(HashMap idNotes) {
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
		ArrayList exitingNotes = this.getNotes(field);
		for (Object note : exitingNotes) {
			DiscrepancyNoteBean existingNote = (DiscrepancyNoteBean) note;
			if (existingNote.getDiscrepancyNoteTypeId() == DiscrepancyNoteType.REASON_FOR_CHANGE.getId()) {
				return true;
			}
		}
		return false;
	}
}