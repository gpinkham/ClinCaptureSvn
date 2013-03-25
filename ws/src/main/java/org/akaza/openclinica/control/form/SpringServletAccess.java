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

package org.akaza.openclinica.control.form;

import java.io.IOException;

import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;

public class SpringServletAccess {

	public static ApplicationContext getApplicationContext(ServletContext servletContext) {
		return WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
	}

	public static String getPropertiesDir(ServletContext servletContext) {
		String resource = "properties/placeholder.properties";
		ServletContextResource scr = (ServletContextResource) getApplicationContext(servletContext).getResource(
				resource);
		String absolutePath = null;
		try {
			absolutePath = scr.getFile().getAbsolutePath();
		} catch (IOException e) {
			throw new OpenClinicaSystemException(e.getMessage(), e.fillInStackTrace());
		}
		absolutePath = absolutePath.replaceAll("placeholder.properties", "");
		return absolutePath;
	}

}
