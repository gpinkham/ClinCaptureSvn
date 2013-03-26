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

package org.akaza.openclinica.dao.hibernate;

import org.akaza.openclinica.domain.managestudy.StudyModuleStatus;

public class StudyModuleStatusDao extends AbstractDomainDao<StudyModuleStatus> {
	@Override
	Class<StudyModuleStatus> domainClass() {
		return StudyModuleStatus.class;
	}

	public StudyModuleStatus findByStudyId(int studyId) {
		String query = "from " + getDomainClassName() + " sms  where sms.studyId = :studyId ";
		org.hibernate.Query q = getCurrentSession().createQuery(query);
		q.setInteger("studyId", studyId);
		return (StudyModuleStatus) q.uniqueResult();
	}

}
