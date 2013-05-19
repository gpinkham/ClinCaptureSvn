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

package org.akaza.openclinica.domain;

import org.akaza.openclinica.bean.login.UserAccountBean;

import java.util.Date;

public interface AuditableMutableDomainObject extends MutableDomainObject {

	/**
	 * @return the createdDate
	 */
	public abstract Date getCreatedDate();

	/**
	 * @param createdDate
	 *            the createdDate to set
	 */
	public abstract void setCreatedDate(Date createdDate);

	/**
	 * @return the updatedDate
	 */
	public abstract Date getUpdatedDate();

	/**
	 * @param updatedDate
	 *            the updatedDate to set
	 */
	public abstract void setUpdatedDate(Date updatedDate);

	/**
	 * @return the owner
	 */
	public abstract UserAccountBean getOwner();

	/**
	 * @param owner
	 *            the owner to set
	 */
	public abstract void setOwner(UserAccountBean owner);

	/**
	 * @return the updater
	 */
	public abstract UserAccountBean getUpdater();

	/**
	 * @param updater
	 *            the updater to set
	 */
	public abstract void setUpdater(UserAccountBean updater);

	/**
	 * @return the status
	 */
	public abstract Status getStatus();

	/**
	 * @param status
	 *            the status to set
	 */
	public abstract void setStatus(Status status);

}
