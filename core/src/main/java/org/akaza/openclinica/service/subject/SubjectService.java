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

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.managestudy.SubjectTransferBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Date;
import java.util.List;

@SuppressWarnings({ "unchecked" })
public class SubjectService implements SubjectServiceInterface {

	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
	DataSource dataSource;

	public SubjectService(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public SubjectService(SessionManager sessionManager) {
		this.dataSource = sessionManager.getDataSource();
	}

	public List<StudySubjectBean> getStudySubject(StudyBean study) {
		return getStudySubjectDao().findAllByStudy(study);

	}

	public String createSubject(SubjectBean subjectBean, StudyBean studyBean, Date enrollmentDate, String secondaryId) {
		if (subjectBean.getUniqueIdentifier() != null && subjectBean.getUniqueIdentifier().trim().length() > 0
				&& getSubjectDao().findByUniqueIdentifier(subjectBean.getUniqueIdentifier()).getId() != 0) {
			// this condition should always be false - subject should have unique person id or none
			subjectBean = getSubjectDao().findByUniqueIdentifier(subjectBean.getUniqueIdentifier());
		} else {
			subjectBean.setStatus(Status.AVAILABLE);
			subjectBean = getSubjectDao().create(subjectBean);
		}

		StudySubjectBean studySubject = createStudySubject(subjectBean, studyBean, enrollmentDate, secondaryId);
		getStudySubjectDao().createWithoutGroup(studySubject);
		return studySubject.getLabel();
	}

	private StudySubjectBean createStudySubject(SubjectBean subject, StudyBean studyBean, Date enrollmentDate,
			String secondaryId) {
		StudySubjectBean studySubject = new StudySubjectBean();
		studySubject.setSecondaryLabel(secondaryId);
		studySubject.setOwner(getUserAccount());
		studySubject.setEnrollmentDate(enrollmentDate);
		studySubject.setLabel(subject.getLabel());
		subject.setLabel(null);
		studySubject.setSubjectId(subject.getId());
		studySubject.setStudyId(studyBean.getId());
		studySubject.setStatus(Status.AVAILABLE);
		return studySubject;

	}

	public void validateSubjectTransfer(SubjectTransferBean subjectTransferBean) {
		//
	}

	/**
	 * Getting the first user account from the database. This would be replaced by an authenticated user who is doing
	 * the SOAP requests .
	 * 
	 * @return UserAccountBean
	 */
	private UserAccountBean getUserAccount() {

		UserAccountBean user = new UserAccountBean();
		user.setId(1);
		return user;
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

}
