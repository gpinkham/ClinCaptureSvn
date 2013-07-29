/*
 * ******************************************************************************
 *  * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
 *  *
 *  * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License
 *  * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 *  * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the Lesser GNU General Public License along with this program.
 *  \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 *  *****************************************************************************
 *
 *
 *  * OpenClinica is distributed under the
 *  * GNU Lesser General Public License (GNU LGPL).
 *
 *  * For details see: http://www.openclinica.org/license
 *  * copyright 2003-2005 Akaza Research
 *
 */

package org.akaza.openclinica.service.calendar;

import org.akaza.openclinica.bean.core.AuditableEntityBean;

import java.util.Date;

public class CalendarFuncBean extends AuditableEntityBean {
	
	private Date dateMax = new Date();
	private Date dateMin = new Date();
	private Date dateEmail = new Date();
	private Date dateSchedule  = new Date();
	private String eventName = "";
	private String referenceVisit = "No";
	private String eventsReferenseVisit = "";
	
	public CalendarFuncBean() {
        super();
	}

	public void setEventsReferenceVisit(String refEventName) {
		this.eventsReferenseVisit = refEventName;
	}
	
	public String getEventsReferenceVisit() {
		return eventsReferenseVisit;
	}
	
	public void setDateMax(Date dateMax) {
		this.dateMax = dateMax;
	}
	
	public Date getDateMax() {
		return dateMax;
	}
	
	public void setDateMin(Date dateMin) {
		this.dateMin = dateMin;
	}
	
	public Date getDateMin() {
		return dateMin;
	}
	
	public void setDateEmail(Date dateEmail) {
		this.dateEmail = dateEmail;
	}
	
	public Date getDateEmail() {
		return dateEmail;
	}
	
	public void setDateSchedule(Date dateSchedule) {
		this.dateSchedule = dateSchedule;
	}
	
	public Date getDateSchedule() {
		return dateSchedule;
	}
	
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	
	public String getEventName() {
		return eventName;
	}
	
	public void setReferenceVisit (String isReference){
		this.referenceVisit = isReference;
	}
	
	public String getReferenceVisit() {
		return referenceVisit;
	}

}
