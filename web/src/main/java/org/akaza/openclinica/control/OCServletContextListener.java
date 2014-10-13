/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2014 Clinovo Inc.
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

package org.akaza.openclinica.control;

import ch.qos.logback.classic.LoggerContext;
import net.sf.ehcache.CacheManager;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.hibernate.OpenClinicaVersionDAO;
import org.akaza.openclinica.dao.hibernate.UsageStatsServiceDAO;
import org.akaza.openclinica.domain.OpenClinicaVersionBean;
import org.akaza.openclinica.service.usageStats.LogUsageStatsService;
import org.quartz.impl.StdScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.Map;

/**
 * ServletContextListener used as a controller for throwing an error when reading up the properties
 * 
 * @author jnyayapathi, pgawade
 * 
 */
public class OCServletContextListener implements ServletContextListener {

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	UsageStatsServiceDAO usageStatsServiceDAO;
	OpenClinicaVersionDAO openClinicaVersionDAO;
	public static String ClinCaptureVersion = "OpenClinica_version";
	private static String SCHEDULER = "schedulerFactoryBean";
	private StdScheduler scheduler;

	public void contextDestroyed(ServletContextEvent event) {
		logger.debug("CCServletContextListener -> contextDestroyed");
		// FIXME probably the place where we need to close everything

		ServletContext context = event.getServletContext();

		// Save the OpenClinica stop time into database
		getUsageStatsServiceDAO(context).saveOCStopTimeToDB();

		// do not wait for jobs to finish when shutting down - make this into datainfo.properties?
		getScheduler(context).shutdown(true);

		CacheManager.getInstance().shutdown();

		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			Driver driver = drivers.nextElement();
			try {
				DriverManager.deregisterDriver(driver);
			} catch (Exception ex) {
				logger.error("Error has occurred.", ex);
			}
		}

		if (LoggerFactory.getILoggerFactory() instanceof LoggerContext) {
			LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
			loggerContext.stop();
		}
	}

	public void contextInitialized(ServletContextEvent event) {
		logger.debug("CCServletContextListener -> contextInitialized");
		ServletContext context = event.getServletContext();

		// Save OpenClinica version to database
		getOpenClinicaVersionDAO(context).saveOCVersionToDB(CoreResources.getField(ClinCaptureVersion));

		// Fetch the OpenClinica started event details
		Map<String, String> OCStartEventDetails = getEventDetailsOCStart(context);

		// Log usage statistics event OpenClinca started
		LogUsageStatsService.logEventOCStart(OCStartEventDetails);

		// Save the OpenClinica start time into database
		getUsageStatsServiceDAO(context).saveOCStartTimeToDB();
	}

	private Map<String, String> getEventDetailsOCStart(ServletContext context) {
		Map<String, String> OCStartEventDetails = getUsageStatsServiceDAO(context).getEventDetailsOCStart();

		OpenClinicaVersionBean openClinicaVersionBean = getOpenClinicaVersionDAO(context).findDefault();
		if (null != openClinicaVersionBean) {
			OCStartEventDetails.put(LogUsageStatsService.OC_version, openClinicaVersionBean.getName());
		}
		return OCStartEventDetails;
	}

	private UsageStatsServiceDAO getUsageStatsServiceDAO(ServletContext context) {
		usageStatsServiceDAO = this.usageStatsServiceDAO != null ? usageStatsServiceDAO
				: (UsageStatsServiceDAO) SpringServletAccess.getApplicationContext(context).getBean(
						"usageStatsServiceDAO");
		return usageStatsServiceDAO;
	}

	private OpenClinicaVersionDAO getOpenClinicaVersionDAO(ServletContext context) {
		openClinicaVersionDAO = this.openClinicaVersionDAO != null ? openClinicaVersionDAO
				: (OpenClinicaVersionDAO) SpringServletAccess.getApplicationContext(context).getBean(
						"openClinicaVersionDAO");
		return openClinicaVersionDAO;
	}

	private StdScheduler getScheduler(ServletContext context) {
		scheduler = this.scheduler != null ? scheduler : (StdScheduler) SpringServletAccess.getApplicationContext(
				context).getBean(SCHEDULER);
		return scheduler;
	}

}
