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
package org.akaza.openclinica.bean.managestudy;

import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;

@SuppressWarnings("serial")
public class DisplayEventDefinitionCRFBean extends AuditableEntityBean {
	private EventDefinitionCRFBean edc;
	private EventCRFBean eventCRF;
	private boolean completedEventCRFs;

	/**
	 * @return Returns the completedEventCRFs.
	 */
	public boolean isCompletedEventCRFs() {
		return completedEventCRFs;
	}

	/**
	 * @param completedEventCRFs
	 *            The completedEventCRFs to set.
	 */
	public void setCompletedEventCRFs(boolean completedEventCRFs) {
		this.completedEventCRFs = completedEventCRFs;
	}

	/**
	 * @return Returns the edc.
	 */
	public EventDefinitionCRFBean getEdc() {
		return edc;
	}

	/**
	 * @param edc
	 *            The edc to set.
	 */
	public void setEdc(EventDefinitionCRFBean edc) {
		this.edc = edc;
	}

	/**
	 * @return Returns the eventCRF.
	 */
	public EventCRFBean getEventCRF() {
		return eventCRF;
	}

	/**
	 * @param eventCRF
	 *            The eventCRF to set.
	 */
	public void setEventCRF(EventCRFBean eventCRF) {
		this.eventCRF = eventCRF;
	}
}
