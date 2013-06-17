package org.akaza.openclinica.service.calendar;

import org.quartz.StatefulJob;

@SuppressWarnings("deprecation")
public class EmailStatefulJob extends EmailJob implements StatefulJob {
	
	public EmailStatefulJob() {
		super();
	}
}