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

package org.akaza.openclinica.control.submit;

/**
 * Created by IntelliJ IDEA. User: bruceperry Date: Nov 30, 2007 This class is designed to determine whether a user has
 * viewed a certain section, to determine, among other reasons, whether default values should be displayed.
 */
public class SectionVisit {
	private int eventCRFId;
	private boolean visitedOnce;
	private int sectionNumber;

	public SectionVisit() {
		visitedOnce = false;
		sectionNumber = 1;
	}

	public int getEventCRFId() {
		return eventCRFId;
	}

	public void setEventCRFId(int eventCRFId) {
		this.eventCRFId = eventCRFId;
	}

	public boolean isVisitedOnce() {
		return visitedOnce;
	}

	public void setVisitedOnce(boolean visitedOnce) {
		this.visitedOnce = visitedOnce;
	}

	public int getSectionNumber() {
		return sectionNumber;
	}

	public void setSectionNumber(int sectionNumber) {
		this.sectionNumber = sectionNumber;
	}
}
