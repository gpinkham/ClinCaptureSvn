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

package org.akaza.openclinica.ws.validator;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.springframework.validation.Errors;
import org.akaza.openclinica.bean.core.Status;

public interface BaseWSValidatorInterface {
	public abstract boolean verifyRole(UserAccountBean user, int study_id, int site_id, Role excluded_role,
			Errors errors);

	public abstract boolean verifyRole(UserAccountBean user, int study_id, int site_id, Errors errors);

	public abstract StudyBean verifyStudy(StudyDAO dao, String study_id, Status[] included_status, Errors errors);

	public abstract StudyBean verifyStudyByOID(StudyDAO dao, String study_id, Status[] included_status, Errors errors);

	public abstract StudyBean verifySite(StudyDAO dao, String study_id, String site_id, Status[] included_status,
			Errors errors);

	public abstract StudyBean verifyStudySubject(String study_id, String subjectId, int max_length, Errors errors);

}
