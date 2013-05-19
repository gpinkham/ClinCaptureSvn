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
package org.akaza.openclinica.control.managestudy;

import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.web.bean.EntityBeanTable;

import java.util.ArrayList;
import java.util.Date;

@SuppressWarnings({ "rawtypes" })
public class ViewEventDefinitionBean {
	private StudyEventDefinitionBean definition;
	private int subjectScheduled;
	private int subjectCompleted;
	private int subjectDiscontinued;
	private EntityBeanTable studyEventTable;
	private ArrayList studyEvents;
	private Date firstScheduledStartDate;
	private Date lastCompletionDate;

	/**
	 * @return Returns the firstScheduledStartDate.
	 */
	public Date getFirstScheduledStartDate() {
		return firstScheduledStartDate;
	}

	/**
	 * @param firstScheduledStartDate
	 *            The firstScheduledStartDate to set.
	 */
	public void setFirstScheduledStartDate(Date firstScheduledStartDate) {
		this.firstScheduledStartDate = firstScheduledStartDate;
	}

	/**
	 * @return Returns the lastCompletionDate.
	 */
	public Date getLastCompletionDate() {
		return lastCompletionDate;
	}

	/**
	 * @param lastCompletionDate
	 *            The lastCompletionDate to set.
	 */
	public void setLastCompletionDate(Date lastCompletionDate) {
		this.lastCompletionDate = lastCompletionDate;
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
	 * @return Returns the studyEventTable.
	 */
	public EntityBeanTable getStudyEventTable() {
		return studyEventTable;
	}

	/**
	 * @param studyEventTable
	 *            The studyEventTable to set.
	 */
	public void setStudyEventTable(EntityBeanTable studyEventTable) {
		this.studyEventTable = studyEventTable;
	}

	/**
	 * @return Returns the subjectCompleted.
	 */
	public int getSubjectCompleted() {
		return subjectCompleted;
	}

	/**
	 * @param subjectCompleted
	 *            The subjectCompleted to set.
	 */
	public void setSubjectCompleted(int subjectCompleted) {
		this.subjectCompleted = subjectCompleted;
	}

	/**
	 * @return Returns the subjectDiscontinued.
	 */
	public int getSubjectDiscontinued() {
		return subjectDiscontinued;
	}

	/**
	 * @param subjectDiscontinued
	 *            The subjectDiscontinued to set.
	 */
	public void setSubjectDiscontinued(int subjectDiscontinued) {
		this.subjectDiscontinued = subjectDiscontinued;
	}

	/**
	 * @return Returns the subjectScheduled.
	 */
	public int getSubjectScheduled() {
		return subjectScheduled;
	}

	/**
	 * @param subjectScheduled
	 *            The subjectScheduled to set.
	 */
	public void setSubjectScheduled(int subjectScheduled) {
		this.subjectScheduled = subjectScheduled;
	}

	/**
	 * @return Returns the studyEvents.
	 */
	public ArrayList getStudyEvents() {
		return studyEvents;
	}

	/**
	 * @param studyEvents
	 *            The studyEvents to set.
	 */
	public void setStudyEvents(ArrayList studyEvents) {
		this.studyEvents = studyEvents;
	}
}
