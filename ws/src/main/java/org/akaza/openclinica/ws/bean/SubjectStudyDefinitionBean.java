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

package org.akaza.openclinica.ws.bean;

import org.akaza.openclinica.bean.login.UserAccountBean;

public class SubjectStudyDefinitionBean extends BaseStudyDefinitionBean {

	private String subjectOIDId;
	private String subjectLabel;

	public SubjectStudyDefinitionBean(String studyUniqueId, String siteUniqueId, UserAccountBean user,
			String subjectLabel) {
		super(studyUniqueId, siteUniqueId, user);
		this.setSubjectLabel(subjectLabel);

	}

	public SubjectStudyDefinitionBean(String studyUniqueId, UserAccountBean user, String subjectLabel) {
		super(studyUniqueId, user);
		this.setSubjectLabel(subjectLabel);

	}

	/**
	 * @param subjectLabel
	 *            the subjectLabel to set
	 */
	public void setSubjectLabel(String subjectLabel) {
		this.subjectLabel = subjectLabel;
	}

	/**
	 * @return the subjectLabel
	 */
	public String getSubjectLabel() {
		return subjectLabel;
	}

	/**
	 * @param subjectUniqueId
	 *            the subjectUniqueId to set
	 */
	public void setSubjectOIDId(String subjectUniqueId) {
		this.subjectOIDId = subjectUniqueId;
	}

	/**
	 * @return the subjectUniqueId
	 */
	public String getSubjectOIDId() {
		return subjectOIDId;
	}

}
