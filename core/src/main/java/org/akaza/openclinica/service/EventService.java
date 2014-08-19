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

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.exception.OpenClinicaSystemException;

/**
 * 
 * Provides Study Event services.
 * 
 */
public class EventService implements EventServiceInterface {

	private DataSource dataSource;

	/**
	 * 
	 * @param dataSource
	 *            DataSource to be used
	 */
	public EventService(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * 
	 * @param sessionManager
	 *            SessionManager to be used
	 */
	public EventService(SessionManager sessionManager) {
		this.dataSource = sessionManager.getDataSource();
	}

	/**
	 * {@inheritDoc}
	 */
	public HashMap<String, String> scheduleEvent(UserAccountBean user, Date startDateTime, Date endDateTime,
			String location, String studyUniqueId, String siteUniqueId, String eventDefinitionOID, String studySubjectId)
			throws OpenClinicaSystemException {

		// Business Validation
		StudyBean study = getStudyDao().findByUniqueIdentifier(studyUniqueId);
		int parentStudyId = study.getId();
		if (siteUniqueId != null) {
			study = getStudyDao().findSiteByUniqueIdentifier(studyUniqueId, siteUniqueId);
		}
		StudyEventDefinitionBean studyEventDefinition = getStudyEventDefinitionDao().findByOidAndStudy(
				eventDefinitionOID, study.getId(), parentStudyId);
		StudySubjectBean studySubject = getStudySubjectDao().findByLabelAndStudy(studySubjectId, study);

		Integer studyEventOrdinal;
		if (canSubjectScheduleAnEvent(studyEventDefinition, studySubject)) {

			StudyEventBean studyEvent = new StudyEventBean();
			studyEvent.setStudyEventDefinitionId(studyEventDefinition.getId());
			studyEvent.setStudySubjectId(studySubject.getId());
			studyEvent.setLocation(location);
			studyEvent.setDateStarted(startDateTime);
			studyEvent.setDateEnded(endDateTime);
			studyEvent.setOwner(user);
			studyEvent.setStatus(Status.AVAILABLE);
			studyEvent.setSubjectEventStatus(SubjectEventStatus.SCHEDULED);
			studyEvent.setSampleOrdinal(getStudyEventDao().getMaxSampleOrdinal(studyEventDefinition, studySubject) + 1);
			studyEvent = (StudyEventBean) getStudyEventDao().create(studyEvent);
			studyEventOrdinal = studyEvent.getSampleOrdinal();

		} else {
			throw new OpenClinicaSystemException("Cannot schedule an event for this Subject");
		}

		HashMap<String, String> h = new HashMap<String, String>();
		h.put("eventDefinitionOID", eventDefinitionOID);
		h.put("studyEventOrdinal", studyEventOrdinal.toString());
		h.put("studySubjectOID", studySubject.getOid());
		return h;

	}

	/**
	 * Determines if StudyEventDefinition can be scheduled for study subject.
	 * 
	 * @param studyEventDefinition
	 *            StudyEventDefinition to check
	 * @param studySubject
	 *            StudySubject to check
	 * @return True if yes, false otherwise
	 */
	public boolean canSubjectScheduleAnEvent(StudyEventDefinitionBean studyEventDefinition,
			StudySubjectBean studySubject) {
		return studyEventDefinition.isRepeating()
				|| getStudyEventDao().findAllByDefinitionAndSubject(studyEventDefinition, studySubject).size() <= 0;
	}

	/**
	 * @return the subjectDao
	 */
	public SubjectDAO getSubjectDao() {
		return new SubjectDAO(dataSource);
	}

	/**
	 * @return the subjectDao
	 */
	public StudyDAO getStudyDao() {
		return new StudyDAO(dataSource);
	}

	/**
	 * @return the subjectDao
	 */
	public StudySubjectDAO getStudySubjectDao() {
		return new StudySubjectDAO(dataSource);
	}

	/**
	 * @return the UserAccountDao
	 */
	public UserAccountDAO getUserAccountDao() {
		return new UserAccountDAO(dataSource);
	}

	/**
	 * @return the StudyEventDefinitionDao
	 */
	public StudyEventDefinitionDAO getStudyEventDefinitionDao() {
		return new StudyEventDefinitionDAO(dataSource);
	}

	/**
	 * @return the StudyEventDao
	 */
	public StudyEventDAO getStudyEventDao() {
		return new StudyEventDAO(dataSource);
	}

	/**
	 * @return the datasource
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * @param dataSource
	 *            the datasource to set
	 */
	public void setDatasource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * {@inheritDoc}
	 */
	public void regenerateStudyEventOrdinals(List<StudyEventBean> studyEvents) {
		int ordinal = 1;
		for (StudyEventBean event : studyEvents) {
			event.setSampleOrdinal(ordinal);
			ordinal++;
		}
	}

}
