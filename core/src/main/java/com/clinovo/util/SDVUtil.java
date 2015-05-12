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
package com.clinovo.util;

import java.util.ArrayList;
import java.util.Map;

import com.clinovo.service.CRFMaskingService;
import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.domain.SourceDataVerification;

/**
 * SDVUtil.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public final class SDVUtil {

	private SDVUtil() {
	}

	/**
	 * Permits SDV.
	 * 
	 * @param studySubjectBean
	 *            StudySubjectBean
	 * @param daoWrapper
	 *            DAOWrapper
	 * @return boolean
	 */
	public static boolean permitSDV(StudySubjectBean studySubjectBean, DAOWrapper daoWrapper) {
		boolean sdv = !studySubjectBean.getStatus().isSigned() && !studySubjectBean.getStatus().isDeleted();
		if (sdv) {
			StudyBean studyBean = (StudyBean) daoWrapper.getSdao().findByPK(studySubjectBean.getStudyId());
			sdv = daoWrapper.getSsdao().isStudySubjectReadyToBeSDVed(studyBean, studySubjectBean);
		}
		return sdv;
	}

	/**
	 * Permits SDV.
	 * 
	 * @param studyEventBean
	 *            StudyEventBean
	 * @param studyId
	 *            int
	 * @param daoWrapper
	 *            DAOWrapper
	 * @param allowSdvWithOpenQueries
	 *            boolean
	 * @param notedMap
	 *            Map<Integer, String>
	 * @param userId int
	 * @param maskingService CRFMaskingService
	 * @return boolean
	 */
	public static boolean permitSDV(StudyEventBean studyEventBean, int studyId, DAOWrapper daoWrapper,
									boolean allowSdvWithOpenQueries, Map<Integer, String> notedMap, int userId, CRFMaskingService maskingService) {
		boolean hasReadyForSDV = false;
		StudyBean studyBean = (StudyBean) daoWrapper.getSdao().findByPK(studyId);
		ArrayList eventCrfs = daoWrapper.getEcdao().findAllStartedByStudyEvent(studyEventBean);
		for (EventCRFBean eventCRFBean : (ArrayList<EventCRFBean>) eventCrfs) {
			if (eventCRFBean.isNotStarted()) {
				continue;
			}
			EventDefinitionCRFBean eventDefinitionCrf = daoWrapper.getEdcdao().findByStudyEventIdAndCRFVersionId(
					studyBean, studyEventBean.getId(), eventCRFBean.getCRFVersionId());
			if ((eventDefinitionCrf.getSourceDataVerification() == SourceDataVerification.AllREQUIRED || eventDefinitionCrf
					.getSourceDataVerification() == SourceDataVerification.PARTIALREQUIRED)
					&& !eventCRFBean.isSdvStatus()
					&& eventCRFBean.getStatus() == Status.UNAVAILABLE
					&& eventCRFBean.getStage() == DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE
					&& (allowSdvWithOpenQueries || !notedMap.containsKey(eventDefinitionCrf.getCrfId()))
					&& !maskingService.isEventDefinitionCRFMasked(eventDefinitionCrf.getId(), userId, studyId)) {
				hasReadyForSDV = true;
			}
		}
		return hasReadyForSDV;
	}
}
