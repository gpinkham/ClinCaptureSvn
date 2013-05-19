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

package org.akaza.openclinica.web.job;

import org.akaza.openclinica.bean.rule.XmlSchemaValidationHelper;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * Import Spring Job, a job running asynchronously on the Tomcat server using Spring and Quartz.
 * 
 * @author thickerson, 04/2009
 * 
 */
public class ImportSpringJob extends QuartzJobBean {

	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());

	XmlSchemaValidationHelper schemaValidator = new XmlSchemaValidationHelper();
	public static final String DIR_PATH = "scheduled_data_import";

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

	}
}
