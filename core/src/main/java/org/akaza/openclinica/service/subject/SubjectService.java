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

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clinovo.service.StudySubjectService;

/**
 * SubjectService.
 */
@Service
@SuppressWarnings({"unchecked"})
public class SubjectService implements SubjectServiceInterface {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private StudySubjectService studySubjectService;

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
	 * {@inheritDoc}
	 */
	public String getStudySubjectOID(String subjectIdentifier, String studyOID) {
		StudySubjectDAO studySubjectDAO = new StudySubjectDAO(dataSource);
		StudySubjectBean studySubject = studySubjectDAO.findByOid(subjectIdentifier);
		if (subjectIdentifier.equals("*") || (studySubject != null && studySubject.getOid() != null)) {
			return subjectIdentifier;
		} else {
			StudyDAO studyDAO = new StudyDAO(dataSource);
			StudyBean study = studyDAO.findByOid(studyOID);
			studySubject = studySubjectDAO.findByLabelAndStudy(subjectIdentifier, study);
			if (studySubject != null && studySubject.getOid() != null) {
				return studySubject.getOid();
			} else {
				return subjectIdentifier;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
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

	private void disableSubject(SubjectBean subjectBean, UserAccountBean updater) throws Exception {
		subjectBean.setStatus(Status.DELETED);
		subjectBean.setUpdater(updater);
		subjectBean.setUpdatedDate(new Date());
		getSubjectDao().update(subjectBean);
		studySubjectService.removeStudySubjects(subjectBean, updater);
	}

	private void enableSubject(SubjectBean subjectBean, UserAccountBean updater) throws Exception {
		subjectBean.setStatus(Status.AVAILABLE);
		subjectBean.setUpdater(updater);
		subjectBean.setUpdatedDate(new Date());
		getSubjectDao().update(subjectBean);
		studySubjectService.restoreStudySubjects(subjectBean, updater);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeSubject(SubjectBean subjectBean, UserAccountBean updater) throws Exception {
		disableSubject(subjectBean, updater);
	}

	/**
	 * {@inheritDoc}
	 */
	public void restoreSubject(SubjectBean subjectBean, UserAccountBean updater) throws Exception {
		enableSubject(subjectBean, updater);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<StudySubjectBean> getStudySubject(StudyBean study) {
		return getStudySubjectDao().findAllByStudy(study);
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
