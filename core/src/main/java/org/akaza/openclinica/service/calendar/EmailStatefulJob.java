package org.akaza.openclinica.service.calendar;

import org.quartz.StatefulJob;

public class EmailStatefulJob extends EmailJob implements StatefulJob {
	
	public EmailStatefulJob() {
		super();
	}
}