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

package org.akaza.openclinica.bean.extract;

import java.io.OutputStream;
import java.util.List;

import org.akaza.openclinica.bean.core.EntityBean;

/**
 * User: bruceperry Date: May 15, 2008 The interface for a class that creates a file and downloads to an operating
 * system a bean in various formats, such as CSV or PDF. The class is initially defined for downloading
 * DiscrepancyNoteBeans.
 * 
 * @see DownloadDiscrepancyNote
 * @author Bruce W. Perry
 */
public interface DownLoadBean {

	void downLoad(EntityBean bean, String format, OutputStream stream);

	void downLoad(List<EntityBean> listOfBeans, String format, OutputStream stream);

	int getContentLength(EntityBean bean, String format);
}
