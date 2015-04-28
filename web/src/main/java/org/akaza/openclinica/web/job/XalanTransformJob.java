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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Locale;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * Xalan Transform Job, an XSLT transform job using the Xalan classes.
 * 
 * @author thickerson
 * 
 */
public class XalanTransformJob extends QuartzJobBean {

	public static final String EMAIL = "contactEmail";
	public static final String XSL_FILE_PATH = "xslFilePath";
	public static final String XML_FILE_PATH = "xmlFilePath";
	public static final String SQL_FILE_PATH = "sqlFilePath";

	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		Locale locale = new Locale("en");
		ResourceBundleProvider.updateLocale(locale);
		JobDataMap dataMap = context.getMergedJobDataMap();
		try {
			TransformerFactory tFactory = TransformerFactory.newInstance();
			java.io.InputStream in = new java.io.FileInputStream(dataMap.getString(XSL_FILE_PATH));
			Transformer transformer = tFactory.newTransformer(new StreamSource(in));
			transformer.transform(new StreamSource(dataMap.getString(XML_FILE_PATH)), new StreamResult(
					new FileOutputStream(dataMap.getString(SQL_FILE_PATH))));

		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

}
