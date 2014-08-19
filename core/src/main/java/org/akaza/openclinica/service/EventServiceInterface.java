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

package org.akaza.openclinica.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.exception.OpenClinicaSystemException;

/**
 * 
 * Provides Study Event services.
 * 
 */
public interface EventServiceInterface {

	/**
	 * Schedules an event for a subject.
	 * 
	 * @param user
	 *            User sheduling event
	 * @param startDateTime
	 *            Start date and time of event
	 * @param endDateTime
	 *            End date and time of event
	 * @param location
	 *            Location of event
	 * @param studyUniqueId
	 *            Unique ID of Study
	 * @param siteUniqueId
	 *            Unique ID of site
	 * @param eventDefinitionOID
	 *            Event Definition OID
	 * @param studySubjectId
	 *            Id of Study Subject
	 * @return Map with details of scheduled event including eventDefinitionOID, studyEventOrdinal and studySubjectOID
	 * @throws OpenClinicaSystemException
	 *             Thrown when event cannot be scheduled for study subject
	 */
	HashMap<String, String> scheduleEvent(UserAccountBean user, Date startDateTime, Date endDateTime, String location,
			String studyUniqueId, String siteUniqueId, String eventDefinitionOID, String studySubjectId)
			throws OpenClinicaSystemException;

	/**
	 * Regenerates Study Event ordinals. In case a Study Event has been deleted, this method should regenerate the
	 * ordinals and eliminate any gaps in the numbering
	 * 
	 * @param studyEvents
	 *            List of StudyEvents with the same StudyEventDefinitionId and StudySubjectId
	 */
	void regenerateStudyEventOrdinals(List<StudyEventBean> studyEvents);

}
