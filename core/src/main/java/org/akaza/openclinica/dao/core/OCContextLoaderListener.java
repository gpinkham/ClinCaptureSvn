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

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.ServletContextEvent;

import liquibase.log.LogFactory;

import org.slf4j.MDC;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.web.context.ContextLoaderListener;

public class OCContextLoaderListener extends ContextLoaderListener {
	private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass().getName());

	@Override
	public void contextInitialized(ServletContextEvent event) {
		String path = event.getServletContext().getRealPath("/");
		String webAppName = getWebAppName(path);

		// Put the web application name into the logging context. This value is
		// used inside the logback.xml
		MDC.put("WEBAPP", webAppName);
		// @pgawade 18-July-2011: Get hostname to send it through usage
		// statistics information
		String hostName = "";
		try {
			hostName = getHostName();
		} catch (UnknownHostException uhe) {
			logger.error("UnknownHostException when fetching the hostname");
		}
		MDC.put("HOSTNAME", hostName);
		// MDC.put("WEBAPP", webAppName + " FROM " + hostName);
		// Get the liquibase logs inside the application log files using
		// SLF4JBridgeHandler
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

	// @pgawade 18-July-2011
	public String getHostName() throws UnknownHostException {
		InetAddress addr = InetAddress.getLocalHost();
		String cHostName = addr.getCanonicalHostName();
		return cHostName;
	}
}
