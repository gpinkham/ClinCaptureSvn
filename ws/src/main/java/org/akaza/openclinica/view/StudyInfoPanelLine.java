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
 */
package org.akaza.openclinica.view;

/**
 * @author ssachs
 */
public class StudyInfoPanelLine {
	protected String title;
	protected String info;
	protected boolean colon;
	protected boolean lastCRF;// for submit module only
	protected boolean current; // indicate whether it is the current object,

	// need to be highlighted

	/**
	 * @param title
	 * @param info
	 */
	public StudyInfoPanelLine(String title, String info) {
		this.title = title;
		this.info = info;
	}

	public StudyInfoPanelLine(String title, String info, boolean colon) {
		this.title = title;
		this.info = info;
		this.colon = colon;
	}

	public StudyInfoPanelLine(String title, String info, boolean colon, boolean lastCRF) {
		this.title = title;
		this.info = info;
		this.colon = colon;
		this.lastCRF = lastCRF;
	}

	public StudyInfoPanelLine(String title, String info, boolean colon, boolean lastCRF, boolean current) {
		this.title = title;
		this.info = info;
		this.colon = colon;
		this.lastCRF = lastCRF;
		this.current = current;
	}

	/**
	 * @return Returns the info.
	 */
	public String getInfo() {
		return info;
	}

	/**
	 * @param info
	 *            The info to set.
	 */
	public void setInfo(String info) {
		this.info = info;
	}

	/**
	 * @return Returns the title.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            The title to set.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return Returns the colon.
	 */
	public boolean isColon() {
		return colon;
	}

	/**
	 * @param colon
	 *            The colon to set.
	 */
	public void setColon(boolean colon) {
		this.colon = colon;
	}

	/**
	 * @return Returns the lastCRF.
	 */
	public boolean isLastCRF() {
		return lastCRF;
	}

	/**
	 * @param lastCRF
	 *            The lastCRF to set.
	 */
	public void setLastCRF(boolean lastCRF) {
		this.lastCRF = lastCRF;
	}

	/**
	 * @return Returns the current.
	 */
	public boolean isCurrent() {
		return current;
	}

	/**
	 * @param current
	 *            The current to set.
	 */
	public void setCurrent(boolean current) {
		this.current = current;
	}
}
