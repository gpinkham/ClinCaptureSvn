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

/**
 * 
 */
package org.akaza.openclinica.service.usageStats;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

import org.akaza.openclinica.dao.hibernate.UsageStatsServiceDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"rawtypes", "unchecked"})
public class LogUsageStatsService {

	protected final static Logger logger = LoggerFactory
			.getLogger("org.akaza.openclinica.service.usageStats.LogUsageStatsService");

	DataSource dataSource;
	UsageStatsServiceDAO usageStatsServiceDAO;
	ServletContext context;

	public LogUsageStatsService(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	// constants used for usage statistics
	public final static String event = "EVENT: ";
	public final static String event_start = "EVENT START: ";// This will be
																// followed by the
																// name of the
																// event
	public final static String event_complete = "EVENT COMPLETE: ";// This will
																	// be
																	// followed
																	// by the
																	// name of
																	// the event
	public final static String event_details = "EVENT DETAILS: ";// This will be
																	// followed by
																	// the event
																	// details
																	// information

	// constants for event messages
	public final static String event_msg_OC_started = "US003 OpenClinica was started";
	// constants for event detail parameters
	public final static String OC_start_time = "oc_start_time";
	// public final static String OC_init_complete_time = "init_complete_time";
	public final static String OC_stop_time = "oc_stop_time";
	public final static String OC_last_system_start = "Last System start time";
	public final static String OC_last_system_stop = "Last System stop time";// temporary
	public final static String OC_last_up_time = "Last System Uptime";
	public final static String OC_version = "OC Version";

	/**
	 * @pgawade Method to log the event - start of OpenClinica initialization
	 */
	public static void logEventOCStart(Map eventDetailsMap) {
		// Format the event details
		String eventDetails = "";
		StringBuffer bufEventDetails = new StringBuffer();
		if (null != eventDetailsMap) {
			Iterator<Map.Entry<String, String>> mapIter = eventDetailsMap.entrySet().iterator();
			Map.Entry<String, String> mapEntry = null;
			while (mapIter.hasNext()) {
				mapEntry = mapIter.next();
				if (null != mapEntry) {
					if ((mapEntry.getKey().equalsIgnoreCase(OC_last_system_start)) && (mapEntry.getValue() == null)) {
						bufEventDetails.append(mapEntry.getKey() + ": "
								+ "No Last System start time available; it could be first start of OpenClinica");
					} else if ((mapEntry.getKey().equalsIgnoreCase(OC_last_up_time)) && (mapEntry.getValue() == null)) {
					} else {
						bufEventDetails.append(mapEntry.getKey() + ": " + mapEntry.getValue() + "\n");
					}
				}
			}
		}
		eventDetails = bufEventDetails.toString();
		// log event
		logger.info(event + event_msg_OC_started);
		// log event details
		logger.info(event_details + eventDetails);

	}

	/**
	 * @return the dataSource
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * @return the context
	 */
	public ServletContext getContext() {
		return context;
	}

	/**
	 * @param context
	 *            the context to set
	 */
	public void setContext(ServletContext context) {
		this.context = context;
	}

}
