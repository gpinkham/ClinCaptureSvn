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

/**
 * 
 */
package org.akaza.openclinica.log;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.filter.Filter;

/**
 * @author pgawade
 * 
 */
public abstract class LogFilterBase extends Filter<LoggingEvent> {

	public final static int SYSLOG_FACILITY_DEFAULT = -1;
	public final static int SYSLOG_FACILITY_KERN = 0;
	public final static int SYSLOG_FACILITY_USER = 1;
	public final static int SYSLOG_FACILITY_MAIL = 2;
	public final static int SYSLOG_FACILITY_DAEMON = 3;
	public final static int SYSLOG_FACILITY_AUTH = 4;
	public final static int SYSLOG_FACILITY_LPR = 6;// 5 is skipped just to
													// match with the standard
													// Syslog facilty codes. 5
													// facilty code is used for
													// internal Syslog messages.
	public final static int SYSLOG_FACILITY_NEWS = 7;
	public final static int SYSLOG_FACILITY_UUCP = 8;
	public final static int SYSLOG_FACILITY_CRON = 9;
	public final static int SYSLOG_FACILITY_AUTHPRIV = 10;
	public final static int SYSLOG_FACILITY_FTP = 11;
	public final static int SYSLOG_FACILITY_AUDIT = 12;

	public final static String FACILITY_CODE_KEY = "FACILITY_CODE";

}
