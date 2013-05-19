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
 * copyright 2003-2010 Akaza Research
 */
package org.akaza.openclinica.log;

import org.slf4j.MDC;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.spi.FilterReply;

/**
 * @author pgawade
 * @version 1.0 (22/Nov/2010) Logback log filter to get logs for facility CRON
 * 
 */
public class LogFilterFacilityCRON extends LogFilterBase {

	@Override
	public FilterReply decide(LoggingEvent event) {
		if ((MDC.get(FACILITY_CODE_KEY) != null)
				&& (Integer.parseInt(MDC.get(FACILITY_CODE_KEY)) == (SYSLOG_FACILITY_CRON))) {
			return FilterReply.ACCEPT;
		} else {
			return FilterReply.DENY;
		}
	}// decide

}// class LogFilterFacilityCRON

