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

package org.akaza.openclinica.service;

import java.util.LinkedList;

import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;

/**
 * This class represents a Discrepancy Note thread or sequence of notes that are conceptually linked together on an
 * Event CRF.
 */
public class DiscrepancyNoteThread {
	private LinkedList<DiscrepancyNoteBean> linkedNoteList;
	private String latestResolutionStatus;
	private int studyId;

	public DiscrepancyNoteThread() {
		linkedNoteList = new LinkedList<DiscrepancyNoteBean>();
		studyId = 0;
		latestResolutionStatus = "";
	}

	public DiscrepancyNoteThread(LinkedList<DiscrepancyNoteBean> linkedNoteList, int studyId) {
		this.linkedNoteList = linkedNoteList;
		this.studyId = studyId;
	}

	public String getLatestResolutionStatus() {
		return latestResolutionStatus;
	}

	public void setLatestResolutionStatus(String latestResolutionStatus) {
		this.latestResolutionStatus = latestResolutionStatus;
	}

	public LinkedList<DiscrepancyNoteBean> getLinkedNoteList() {
		return linkedNoteList;
	}

	public void setLinkedNoteList(LinkedList<DiscrepancyNoteBean> linkedNoteList) {
		this.linkedNoteList = linkedNoteList;
	}

	public int getStudyId() {
		return studyId;
	}

	public void setStudyId(int studyId) {
		this.studyId = studyId;
	}
}
