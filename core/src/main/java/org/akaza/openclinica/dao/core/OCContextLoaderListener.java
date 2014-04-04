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

package org.akaza.openclinica.dao.core;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import liquibase.log.LogFactory;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.ServletContextEvent;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

public class OCContextLoaderListener extends ContextLoaderListener {

	private org.slf4j.Logger logger;

	public OCContextLoaderListener() {
		super();
		initLoggerFactory();
	}

	private void initLoggerFactory() {
		try {
			String catalinaHome = new File(getClass().getClassLoader().getResource(".").getPath()).getParent();
			System.setProperty("catalina.home", catalinaHome);
			DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
			String webAppName = defaultResourceLoader.getResource("../../").getFile().getName();
			Properties props = PropertiesLoaderUtils.loadProperties(defaultResourceLoader.getResource("datainfo.properties"));
			// Log directory path
			StringBuilder logPath = new StringBuilder(catalinaHome);
			logPath.append(File.separator);
			logPath.append("logs");
			logPath.append(File.separator);
			logPath.append(webAppName);
			logPath.append("-logs");
			logPath.append(File.separator);
			logPath.append(webAppName);
			// Set properties
			System.setProperty("log.dir", logPath.toString());
			System.setProperty("logLevel", props.getProperty("logLevel"));
			System.setProperty("logLocation", props.getProperty("logLocation"));
			System.setProperty("syslog.port", props.getProperty("syslog.port"));
			System.setProperty("syslog.host", props.getProperty("syslog.host"));
			System.setProperty("collectStats", props.getProperty("collectStats"));
			System.setProperty("usage.stats.port", props.getProperty("usage.stats.port"));
			System.setProperty("usage.stats.host", props.getProperty("usage.stats.host"));
			ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
			if (loggerFactory instanceof LoggerContext) {
				LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
				loggerContext.reset();
				JoranConfigurator configurator = new JoranConfigurator();
				configurator.setContext(loggerContext);
				configurator.doConfigure(defaultResourceLoader.getResource("logback.xml").getURL());
				logger = LoggerFactory.getLogger(this.getClass().getName());
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		String path = event.getServletContext().getRealPath("/");
		String webAppName = getWebAppName(path);

		// Put the web application name into the logging context. This value is
		// used inside the logback.xml
		MDC.put("WEBAPP", webAppName);
		String hostName = "";
		try {
			hostName = getHostName();
		} catch (UnknownHostException ex) {
			if (logger != null) {
				logger.error("UnknownHostException when fetching the hostname.", ex);
			}
		}
		MDC.put("HOSTNAME", hostName);
		LogFactory.getLogger().addHandler(new SLF4JBridgeHandler());
		super.contextInitialized(event);
	}

	public String getWebAppName(String servletCtxRealPath) {
		String webAppName = null;
		if (null != servletCtxRealPath) {
			String[] tokens = servletCtxRealPath.split("\\\\");
			webAppName = tokens[(tokens.length - 1)].trim();
		}
		return webAppName;
	}

	public String getHostName() throws UnknownHostException {
		InetAddress addr = InetAddress.getLocalHost();
		return addr.getCanonicalHostName();
	}
}
