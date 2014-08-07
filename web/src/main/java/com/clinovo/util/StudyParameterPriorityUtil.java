/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2014 Clinovo Inc.
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

package com.clinovo.util;

import com.clinovo.dao.SystemDAO;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;

/**
 * Util for analysing priority between System and Study parameters.
 * 
 */
public final class StudyParameterPriorityUtil {

	private StudyParameterPriorityUtil() {
	}

	/**
	 * Checks if parameter should be enabled.
	 * 
	 * @param parameterName
	 *            the parameter name for check.
	 * @param parentStudyId
	 *            the study primary key.
	 * @param sysDao
	 *            the <code>SystemDAO</code> for data base access methods.
	 * @param studyParamDAO
	 *            the <code>StudyParameterDAO</code> for data base access methods.
	 * @param studyDAO
	 *            the <code>StudyDAO</code> for data base access methods.
	 * @return Returns true if study override system parameter.
	 */
	public static boolean isParameterEnabled(String parameterName, int parentStudyId, SystemDAO sysDao,
			StudyParameterValueDAO studyParamDAO, StudyDAO studyDAO) {

		StudyBean studyBean = (StudyBean) studyDAO.findByPK(parentStudyId);
		StudyParameterValueBean studyParameter = studyParamDAO.findByHandleAndStudy(parentStudyId, parameterName);
		com.clinovo.model.System systemParam = sysDao.findByName(parameterName);
		boolean parameterEnabled = false;
		if (systemParam != null
				&& ((studyBean.getExpectedTotalEnrollment() == 0 && systemParam.getValue().equals("yes")) || (studyBean
						.getExpectedTotalEnrollment() > 0 && studyParameter.getValue().equals("yes")))) {
			parameterEnabled = true;
		}
		return parameterEnabled;
	}

}
