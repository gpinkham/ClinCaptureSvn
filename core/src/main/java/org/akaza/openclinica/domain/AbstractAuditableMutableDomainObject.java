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

package org.akaza.openclinica.domain;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.hibernate.annotations.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
public abstract class AbstractAuditableMutableDomainObject extends AbstractMutableDomainObject implements
		MutableDomainObject, AuditableMutableDomainObject {

	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
	protected Date createdDate;
	protected Date updatedDate;
	protected UserAccountBean owner;
	protected UserAccountBean updater;
	protected Status status;

	// TODO: phase out the use of these Once the above beans become Hibernated
	protected Integer ownerId;
	protected Integer updateId;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.akaza.openclinica.domain.AuditableMutableDomainObject#getCreatedDate()
	 */
	@Column(name = "date_created", updatable = false)
	public Date getCreatedDate() {
		if (createdDate != null) {
			return createdDate;
		} else
			return new Date();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.akaza.openclinica.domain.AuditableMutableDomainObject#setCreatedDate(java.util.Date)
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.akaza.openclinica.domain.AuditableMutableDomainObject#getUpdatedDate()
	 */
	@Column(name = "date_updated", insertable = false)
	public Date getUpdatedDate() {
		return updatedDate;
	}

	@Transient
	public Date getCurrentUpdatedDate() {
		return this.updatedDate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.akaza.openclinica.domain.AuditableMutableDomainObject#setUpdatedDate(java.util.Date)
	 */
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.akaza.openclinica.domain.AuditableMutableDomainObject#getOwner()
	 */
	@Transient
	public UserAccountBean getOwner() {
		return owner;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.akaza.openclinica.domain.AuditableMutableDomainObject#setOwner(org.akaza.openclinica.bean.login.UserAccountBean
	 * )
	 */
	public void setOwner(UserAccountBean owner) {
		if (this.owner != null) {
			this.ownerId = owner.getId();
		}
		this.owner = owner;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.akaza.openclinica.domain.AuditableMutableDomainObject#getUpdater()
	 */
	@Transient
	public UserAccountBean getUpdater() {
		return updater;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.akaza.openclinica.domain.AuditableMutableDomainObject#setUpdater(org.akaza.openclinica.bean.login.UserAccountBean
	 * )
	 */
	public void setUpdater(UserAccountBean updater) {
		if (this.updater != null) {
			this.updateId = updater.getId();
		}
		this.updater = updater;

	}

	@Transient
	public void setUpdaterAndDate(UserAccountBean updater) {
		setUpdater(updater);
		setUpdatedDate(new Date());
	}

	/**
	 * @return the status
	 */
	@Type(type = "status")
	@Column(name = "status_id")
	public Status getStatus() {
		if (status != null) {
			return status;
		} else
			return Status.AVAILABLE;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * @return the ownerId
	 */
	@Column(name = "owner_id")
	public Integer getOwnerId() {
		return ownerId;
	}

	/**
	 * @param ownerId
	 *            the ownerId to set
	 */
	public void setOwnerId(Integer ownerId) {
		this.ownerId = ownerId;
	}

	/**
	 * @return the updaterId
	 */
	@Column(name = "update_id")
	public Integer getUpdateId() {
		return updateId;
	}

	/**
	 * @param updaterId
	 *            the updaterId to set
	 */
	public void setUpdateId(Integer updateId) {
		this.updateId = updateId;
	}

}
