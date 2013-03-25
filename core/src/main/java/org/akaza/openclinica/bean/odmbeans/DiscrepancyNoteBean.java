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
 * For details see: http://www.openclinica.org/license copyright 2003-2010 Akaza
 * Research
 *
 */

package org.akaza.openclinica.bean.odmbeans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author ywang (March, 2010)
 * 
 */
public class DiscrepancyNoteBean extends ElementOIDBean {
	private String status;
	private String noteType;
	private Date dateUpdated;
	private int numberOfChildNotes;
	private List<ChildNoteBean> childNotes = new ArrayList<ChildNoteBean>();

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getNoteType() {
		return noteType;
	}

	public void setNoteType(String noteType) {
		this.noteType = noteType;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public void setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

	public int getNumberOfChildNotes() {
		return numberOfChildNotes;
	}

	public void setNumberOfChildNotes(int numberOfChildNotes) {
		this.numberOfChildNotes = numberOfChildNotes;
	}

	public List<ChildNoteBean> getChildNotes() {
		return childNotes;
	}

	public void setChildNotes(ArrayList<ChildNoteBean> childNotes) {
		this.childNotes = childNotes;
	}
}
