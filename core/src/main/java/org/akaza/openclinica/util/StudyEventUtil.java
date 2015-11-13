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

package org.akaza.openclinica.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;

/**
 * StudyEventUtil.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public final class StudyEventUtil {

	private StudyEventUtil() {
	}

	/**
	 * Selects study events sorted like on subject matrix.
	 * 
	 * @param parentStudyId
	 *            int
	 * @param seddao
	 *            StudyEventDefinitionDAO
	 * @param sgcdao
	 *            StudyGroupClassDAO
	 * @return ArrayList of StudyEventDefinitionBean
	 */
	public static ArrayList<StudyEventDefinitionBean> selectStudyEventsSortedLikeOnSubjectMatrix(int parentStudyId,
			StudyEventDefinitionDAO seddao, StudyGroupClassDAO sgcdao) {
		/*
		 * available eventDefs ordered like on Subject Matrix
		 */

		ArrayList<StudyEventDefinitionBean> result = new ArrayList<StudyEventDefinitionBean>();

		List<StudyGroupClassBean> allActiveDynGroupClasses = sgcdao.findAllActiveDynamicGroupsByStudyId(parentStudyId);
		Collections.sort(allActiveDynGroupClasses, StudyGroupClassBean.comparatorForDynGroupClasses);

		// ordered eventDefs from dynGroups
		for (StudyGroupClassBean dynGroup : allActiveDynGroupClasses) {
			result.addAll(seddao.findAllAvailableAndOrderedByStudyGroupClassId(dynGroup.getId()));
		}

		ArrayList eventDefinitionsNotFromDynGroup = seddao.findAllActiveNotClassGroupedByStudyId(parentStudyId);
		// sort by study event definition ordinal
		Collections.sort(eventDefinitionsNotFromDynGroup);

		result.addAll(eventDefinitionsNotFromDynGroup);

		return result;
	}
}
