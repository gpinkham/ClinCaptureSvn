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

package org.akaza.openclinica.service.subject;

import java.util.Date;
import java.util.List;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.SubjectBean;

/**
 * SubjectServiceInterface.
 */
public interface SubjectServiceInterface {

	/**
	 * Removes subject.
	 *
	 * @param subjectBean
	 *            SubjectBean
	 * @param updater
	 *            UserAccountBean
	 * @throws Exception
	 *             an Exception
	 */
	void removeSubject(SubjectBean subjectBean, UserAccountBean updater) throws Exception;

	/**
	 * Restores subject.
	 *
	 * @param subjectBean
	 *            SubjectBean
	 * @param updater
	 *            UserAccountBean
	 * @throws Exception
	 *             an Exception
	 */
	void restoreSubject(SubjectBean subjectBean, UserAccountBean updater) throws Exception;

	/**
	 * Create subject.
	 * 
	 * @param subjectBean
	 *            SubjectBean
	 * @param studyBean
	 *            StudyBean
	 * @param enrollmentDate
	 *            Date
	 * @param secondaryId
	 *            String
	 * @return String
	 */
	String createSubject(SubjectBean subjectBean, StudyBean studyBean, Date enrollmentDate, String secondaryId);

	/**
	 * Returns Study subject.
	 * 
	 * @param study
	 *            StudyBean
	 * @return List of StudySubjectBean
	 */
	List<StudySubjectBean> getStudySubject(StudyBean study);

	/**
	 * Returns studySubjectOID.
	 *
	 * @param subjectIdentifier
	 *            String
	 * @param studyOID
	 *            String
	 * @return String
	 */
	String getStudySubjectOID(String subjectIdentifier, String studyOID);
}
