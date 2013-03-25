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
package org.akaza.openclinica.bean.admin;

import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;

/**
 * @author Jun Xu
 * 
 *         TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style -
 *         Code Templates
 */
public class StudyEventAuditBean {
	private StudyEventDefinitionBean definition;
	private SubjectEventStatus oldSubjectEventStatus;
	private SubjectEventStatus newSubjectEventStatus;
	private AuditEventBean auditEvent;
	private UserAccountBean updater;

	/**
	 * @return Returns the newSubjectEventStatus.
	 */
	public SubjectEventStatus getNewSubjectEventStatus() {
		return newSubjectEventStatus;
	}

	/**
	 * @param newSubjectEventStatus
	 *            The newSubjectEventStatus to set.
	 */
	public void setNewSubjectEventStatus(SubjectEventStatus newSubjectEventStatus) {
		this.newSubjectEventStatus = newSubjectEventStatus;
	}

	/**
	 * @return Returns the oldSubjectEventStatus.
	 */
	public SubjectEventStatus getOldSubjectEventStatus() {
		return oldSubjectEventStatus;
	}

	/**
	 * @param oldSubjectEventStatus
	 *            The oldSubjectEventStatus to set.
	 */
	public void setOldSubjectEventStatus(SubjectEventStatus oldSubjectEventStatus) {
		this.oldSubjectEventStatus = oldSubjectEventStatus;
	}

	/**
	 * @return Returns the definition.
	 */
	public StudyEventDefinitionBean getDefinition() {
		return definition;
	}

	/**
	 * @param definition
	 *            The definition to set.
	 */
	public void setDefinition(StudyEventDefinitionBean definition) {
		this.definition = definition;
	}

	/**
	 * @return Returns the auditEvent.
	 */
	public AuditEventBean getAuditEvent() {
		return auditEvent;
	}

	/**
	 * @param auditEvent
	 *            The auditEvent to set.
	 */
	public void setAuditEvent(AuditEventBean auditEvent) {
		this.auditEvent = auditEvent;
	}

	/**
	 * @return Returns the updater.
	 */
	public UserAccountBean getUpdater() {
		return updater;
	}

	/**
	 * @param updater
	 *            The updater to set.
	 */
	public void setUpdater(UserAccountBean updater) {
		this.updater = updater;
	}
}
