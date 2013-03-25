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
 * Xalan Transform Job, an XSLT transform job using the Xalan classes
 * 
 * @author thickerson
 * 
 */
public class XalanTransformJob extends QuartzJobBean {

	public static final String DATASET_ID = "dsId";
	public static final String EMAIL = "contactEmail";
	public static final String USER_ID = "user_id";
	public static final String XSL_FILE_PATH = "xslFilePath";
	public static final String XML_FILE_PATH = "xmlFilePath";
	public static final String SQL_FILE_PATH = "sqlFilePath";

	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		// need to generate a Locale so that user beans and other things will
		// generate normally
		// TODO make dynamic?
		Locale locale = new Locale("en-US");
		ResourceBundleProvider.updateLocale(locale);
		JobDataMap dataMap = context.getMergedJobDataMap();
		try {
			TransformerFactory tFactory = TransformerFactory.newInstance();

			// Use the TransformerFactory to instantiate a Transformer that will work with
			// the stylesheet you specify. This method call also processes the stylesheet
			// into a compiled Templates object.
			java.io.InputStream in = new java.io.FileInputStream(dataMap.getString(XSL_FILE_PATH));
			Transformer transformer = tFactory.newTransformer(new StreamSource(in));

			// Use the Transformer to apply the associated Templates object to an XML document
			// (foo.xml) and write the output to a file (foo.out).
			System.out.println("--> job starting: ");
			final long start = System.currentTimeMillis();
			transformer.transform(new StreamSource(dataMap.getString(XML_FILE_PATH)), new StreamResult(
					new FileOutputStream(dataMap.getString(SQL_FILE_PATH))));
			final long done = System.currentTimeMillis() - start;
			System.out.println("--> job completed in " + done + " ms");
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
