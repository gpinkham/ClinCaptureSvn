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

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.core;

import java.util.Arrays;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.dao.login.UserAccountDAO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <P>
 * Auditable Entity Bean, soon to take the place of Entity Bean, by Tom Hickerson.
 * 
 * <P>
 * We plan to make the following division:
 * <P>
 * Entity Bean--holds a name and id, the base class for a controlled vocab;
 * <P>
 * Auditable Entity Bean, holding more information such as date updated, date created, who updated, and who created the
 * object in the database.
 * 
 * @author thickerson
 * 
 * 
 */
@SuppressWarnings({"serial"})
@XmlAccessorType(XmlAccessType.FIELD)
public class AuditableEntityBean extends EntityBean {

	public static final int DEFAULT_STATES_LENGTH = 2;

	@JsonIgnore
	@XmlTransient
	protected Date createdDate;

	@JsonIgnore
	@XmlTransient
	protected Date updatedDate;

	@JsonIgnore
	@XmlTransient
	protected int ownerId;

	@JsonIgnore
	@XmlTransient
	protected UserAccountBean owner;

	@JsonIgnore
	@XmlTransient
	protected int updaterId;

	@JsonIgnore
	@XmlTransient
	protected UserAccountBean updater;

	@JsonProperty("status")
	@XmlElement(name = "Status", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	protected String statusCode = "";

	@JsonIgnore
	@XmlTransient
	protected Status status;

	@JsonIgnore
	@XmlTransient
	protected Status oldStatus;

	@JsonIgnore
	@XmlTransient
	protected String states;

	// used to retrieve the owner and updater when needed
	@JsonIgnore
	@XmlTransient
	protected UserAccountDAO udao;

	/**
	 * AuditableEntityBean constructor.
	 */
	public AuditableEntityBean() {
		createdDate = new Date(0);
		updatedDate = new Date(0);
		ownerId = 0;
		owner = null;
		updaterId = 0;
		updater = null;
		status = null;
		udao = null;
		initStates();
	}

	/**
	 * @return Returns the owner.
	 */
	public UserAccountBean getOwner() {
		if (owner != null) {
			return owner;
		}

		try {
			if (udao == null) {
				// SessionManager sm = new SessionManager();
				udao = new UserAccountDAO(SessionManager.getStaticDataSource());
			}
			if (owner == null || owner.getId() != ownerId) {
				owner = (UserAccountBean) udao.findByPK(ownerId);
			}
		} catch (Exception e) {
			owner = null;
		}

		return owner;
	}

	/**
	 * @param owner
	 *            The owner to set.
	 */
	public void setOwner(UserAccountBean owner) {
		this.owner = owner;
		ownerId = owner.getId();
	}

	/**
	 * @return Returns the ownerId.
	 */
	public int getOwnerId() {
		if (owner == null) {
			return ownerId;
		}
		return owner.getId();
	}

	/**
	 * @deprecated
	 * @param ownerId
	 *            The ownerId to set.
	 */
	@Deprecated
	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}

	/**
	 * @return Returns the updater.
	 */
	public UserAccountBean getUpdater() {
		if (updater != null) {
			return updater;
		}

		try {
			if (udao == null) {
				udao = new UserAccountDAO(SessionManager.getStaticDataSource());
			}
			if (updater == null || updater.getId() != updaterId) {
				updater = (UserAccountBean) udao.findByPK(updaterId);
			}

		} catch (Exception e) {
			updater = null;
		}

		return updater;
	}

	/**
	 * @param updater
	 *            The updater to set.
	 */
	public void setUpdater(UserAccountBean updater) {
		this.updater = updater;
		updaterId = updater.getId();
	}

	/**
	 * @return Returns the updaterId.
	 */
	public int getUpdaterId() {
		if (updater == null) {
			return updaterId;
		}
		return updater.getId();
	}

	/**
	 * @deprecated
	 * @param updaterId
	 *            The updaterId to set.
	 */
	@Deprecated
	public void setUpdaterId(int updaterId) {
		this.updaterId = updaterId;

	}

	/**
	 * @return Returns the createdDate.
	 */
	public java.util.Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate
	 *            The createdDate to set.
	 */
	public void setCreatedDate(java.util.Date createdDate) {
		this.createdDate = createdDate;
	}

	public java.util.Date getUpdatedDate() {
		return updatedDate;
	}

	/**
	 * @param updatedDate
	 *            The updatedDate to set.
	 */
	public void setUpdatedDate(java.util.Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public Status getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            The status to set.
	 */
	public void setStatus(Status status) {
		statusCode = status != null ? status.getCode() : "";
		this.status = status;
	}

	public Status getOldStatus() {
		return oldStatus;
	}

	public void setOldStatus(Status oldStatus) {
		this.oldStatus = oldStatus;
	}

	public String getStates() {
		return states;
	}

	public void setStates(String states) {
		this.states = states;
	}

	/**
	 * Returns length of states array.
	 *
	 * @return int
	 */
	protected int getStatesLength() {
		return DEFAULT_STATES_LENGTH;
	}

	private void initStates() {
		states = "";
		int statesLength = getStatesLength();
		for (int i = 0; i <= statesLength - 1; i++) {
			states = states.concat(states.isEmpty() ? "" : ",").concat("0");
		}
	}

	private void changeStates(int position, int value) {
		String[] statesArray = states.split(",");
		statesArray[position] = Integer.toString(value);
		states = Arrays.asList(statesArray).toString().replaceAll("\\]|\\[| ", "");
	}

	/**
	 * Applies status.
	 * 
	 * @param position
	 *            int
	 * @param status
	 *            Status
	 * @param updater
	 *            UserAccountBean
	 */
	public void applyState(int position, Status status, UserAccountBean updater) {
		if (statesIsEmpty()) {
			oldStatus = this.status;
		}
		changeStates(position, status.getId());
		String[] statesArray = states.split(",");
		// set latest nonzero state as current state
		for (int i = statesArray.length - 1; i >= 0; i--) {
			int value = Integer.parseInt(statesArray[i]);
			if (value != 0) {
				this.status = Status.get(value);
				break;
			}
		}
		setUpdater(updater);
		setUpdatedDate(new Date());
	}

	/**
	 * Reverts state.
	 * 
	 * @param position
	 *            int
	 * @param updater
	 *            UserAccountBean
	 * @return boolean
	 */
	public boolean revertState(int position, UserAccountBean updater) {
		if (position >= 0) {
			changeStates(position, 0);
		}
		boolean available = true;
		String[] statesArray = states.split(",");
		for (int i = statesArray.length - 1; i >= 0; i--) {
			int value = Integer.parseInt(statesArray[i]);
			if (value != 0) {
				status = Status.get(value);
				available = false;
				break;
			}
		}
		if (available) {
			status = !oldStatus.isInvalid() && !oldStatus.isDeleted() && !oldStatus.isLocked()
					? oldStatus
					: Status.AVAILABLE;
			if (this instanceof EventCRFBean) {
				status = ((EventCRFBean) this).getDateCompleted() != null ? Status.UNAVAILABLE : Status.AVAILABLE;
			}
		} else {
			if (this instanceof StudyEventBean) {
				if (status.isDeleted()) {
					((StudyEventBean) this).setSubjectEventStatus(SubjectEventStatus.REMOVED);
				} else if (status.isLocked()) {
					((StudyEventBean) this).setSubjectEventStatus(SubjectEventStatus.LOCKED);
				}
			}
		}
		setUpdater(updater);
		setUpdatedDate(new Date());
		return available;
	}

	/**
	 * Returns true if states contains only zero values.
	 *
	 * @return boolean
	 */
	public boolean statesIsEmpty() {
		return states.replaceAll(",|0", "").isEmpty();
	}
}
