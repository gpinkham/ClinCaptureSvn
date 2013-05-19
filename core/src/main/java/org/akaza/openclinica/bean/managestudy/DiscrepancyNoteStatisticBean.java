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

package org.akaza.openclinica.bean.managestudy;

/**
 * User: Pavel Date: 15.10.12
 */
public class DiscrepancyNoteStatisticBean {

	private int discrepancyNotesCount;
	private int discrepancyNoteTypeId;
	private int resolutionStatusId;

	public DiscrepancyNoteStatisticBean() {
	}

	public DiscrepancyNoteStatisticBean(int discrepancyNotesCount, int discrepancyNoteTypeId, int resolutionStatusId) {
		this.discrepancyNotesCount = discrepancyNotesCount;
		this.discrepancyNoteTypeId = discrepancyNoteTypeId;
		this.resolutionStatusId = resolutionStatusId;
	}

	public int getDiscrepancyNotesCount() {
		return discrepancyNotesCount;
	}

	public void setDiscrepancyNotesCount(int discrepancyNotesCount) {
		this.discrepancyNotesCount = discrepancyNotesCount;
	}

	public int getDiscrepancyNoteTypeId() {
		return discrepancyNoteTypeId;
	}

	public void setDiscrepancyNoteTypeId(int discrepancyNoteTypeId) {
		this.discrepancyNoteTypeId = discrepancyNoteTypeId;
	}

	public int getResolutionStatusId() {
		return resolutionStatusId;
	}

	public void setResolutionStatusId(int resolutionStatusId) {
		this.resolutionStatusId = resolutionStatusId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		DiscrepancyNoteStatisticBean that = (DiscrepancyNoteStatisticBean) o;

		if (discrepancyNoteTypeId != that.discrepancyNoteTypeId)
			return false;
		if (discrepancyNotesCount != that.discrepancyNotesCount)
			return false;
		if (resolutionStatusId != that.resolutionStatusId)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = discrepancyNotesCount;
		result = 31 * result + discrepancyNoteTypeId;
		result = 31 * result + resolutionStatusId;
		return result;
	}
}
