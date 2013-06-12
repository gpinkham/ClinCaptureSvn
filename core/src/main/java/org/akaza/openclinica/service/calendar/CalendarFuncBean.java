package org.akaza.openclinica.service.calendar;

import java.util.Date;

public class CalendarFuncBean {
	
	private Date dateMax;
	private Date dateMin;
	private Date dateEmail;
	private Date dateSchedule;
	private String eventName;
	private boolean referenceVisit;
	private String eventsReferenseVisit;
	private String flagColor;
	
	public CalendarFuncBean() {
		dateMax = new Date();
		dateMin = new Date();
		dateEmail = new Date();
		dateSchedule = new Date();
		eventName = "";
		referenceVisit = false;
		eventsReferenseVisit = "";
		flagColor = "";
	}

	public void setFlagColor(String flagColor) {
		this.flagColor = flagColor;
	}
	
	public String getFlagColor() {
		return flagColor;
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
	
	public void setReferenceVisit (boolean isReference){
		this.referenceVisit = isReference;
	}
	
	public boolean getReferenceVisit() {
		return referenceVisit;
	}

}
