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
package org.akaza.openclinica.view;

/**
 * @author jxu
 * 
 */
public class StudyInfoPanelLineSubmit extends StudyInfoPanelLine {
	private int eventId;
	private int eventCRFId;

	public StudyInfoPanelLineSubmit(String title, String info, boolean colon, boolean lastCRF, int eventId,
			int eventCRFId) {
		super(title, info, colon, lastCRF);
		this.eventId = eventId;
		this.eventCRFId = eventCRFId;

	}

	/**
	 * @return Returns the eventCRFId.
	 */
	public int getEventCRFId() {
		return eventCRFId;
	}

	/**
	 * @param eventCRFId
	 *            The eventCRFId to set.
	 */
	public void setEventCRFId(int eventCRFId) {
		this.eventCRFId = eventCRFId;
	}

	/**
	 * @return Returns the eventId.
	 */
	public int getEventId() {
		return eventId;
	}

	/**
	 * @param eventId
	 *            The eventId to set.
	 */
	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

}
