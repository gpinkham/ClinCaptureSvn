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

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.view;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.Status;

/**
 * <P>
 * BreadcrumbBean.java, by Tom Hickerson.
 * <P>
 * A bean to be used in a BreadcrumbTrail.
 * <P>
 * TODO make sure that Page does not lead to the JSP directly, but back to the servlet; ie, "EditDataset?datasetId=9"
 * instead of "editDataset.jsp", since the link will break if it's just the JSP.
 * 
 * @author thickerson
 * 
 */
@SuppressWarnings("serial")
public class BreadcrumbBean extends EntityBean {

	private String url;
	private Status status;

	public BreadcrumbBean(String name, String url, int statusId) {
		this.setName(name);
		this.setUrl(url);
		this.setStatus(Status.get(statusId));
	}

	public BreadcrumbBean(String name, String url, Status status) {
		this.setName(name);
		this.setUrl(url);
		this.setStatus(status);
	}

	/**
	 * @return Returns the url.
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            The url to set.
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return Returns the status.
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            The status to set.
	 */
	public void setStatus(Status status) {
		this.status = status;
	}
}
