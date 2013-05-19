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

import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.managestudy.*;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.domain.SourceDataVerification;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({ "unchecked", "rawtypes" })
public final class SDVUtil {

	private SDVUtil() {
	}

	public static boolean permitSDV(StudySubjectBean studySubjectBean, DAOWrapper daoWrapper) {
		boolean sdv = !studySubjectBean.getStatus().isSigned();
		if (sdv) {
			List<Integer> sedIdList = new ArrayList<Integer>();
			StudyBean studyBean = (StudyBean) daoWrapper.getSdao().findByPK(studySubjectBean.getStudyId());
			List<StudyEventDefinitionBean> sedList = daoWrapper.getSeddao().findAllByStudy(studyBean);
			for (StudyEventDefinitionBean sedBean : sedList) {
				if (sedBean.isActive()) {
					ArrayList eventDefCrfs = daoWrapper.getEdcdao().findAllByEventDefinitionId(sedBean.getId());
					for (EventDefinitionCRFBean eventDefinitionCrf : (ArrayList<EventDefinitionCRFBean>) eventDefCrfs) {
						if (eventDefinitionCrf.getStatus() != Status.AVAILABLE || !eventDefinitionCrf.isActive())
							continue;
						if (eventDefinitionCrf.getSourceDataVerification() == SourceDataVerification.AllREQUIRED
								|| eventDefinitionCrf.getSourceDataVerification() == SourceDataVerification.PARTIALREQUIRED) {
							sedIdList.add(sedBean.getId());
							break;
						}
					}
				}
			}

			boolean canTheSubjectBeSDVed = daoWrapper.getSsdao().canTheSubjectBeSDVed(studySubjectBean.getId(),
					studyBean.getId(), studyBean.getId());

			if (canTheSubjectBeSDVed) {
				ArrayList studyEvents = daoWrapper.getSedao().findAllByStudySubject(studySubjectBean);
				for (StudyEventBean studyEventBean : (ArrayList<StudyEventBean>) studyEvents) {
					if (studyEventBean.getSubjectEventStatus() == SubjectEventStatus.SOURCE_DATA_VERIFIED) {
						sedIdList.remove((Integer) studyEventBean.getStudyEventDefinitionId());
					}
				}
			}
			sdv = canTheSubjectBeSDVed && sedIdList.size() > 0 ? true : false;
		}
		return sdv;
	}

	public static boolean permitSDV(StudyEventBean studyEventBean, int studyId, DAOWrapper daoWrapper) {
		boolean hasReadyForSDV = false;
		StudyBean studyBean = (StudyBean) daoWrapper.getSdao().findByPK(studyId);
		ArrayList eventCrfs = daoWrapper.getEcdao().findAllStartedByStudyEvent(studyEventBean);
		for (EventCRFBean eventCRFBean : (ArrayList<EventCRFBean>) eventCrfs) {
			if (eventCRFBean.isNotStarted())
				continue;
			EventDefinitionCRFBean eventDefinitionCrf = daoWrapper.getEdcdao().findByStudyEventIdAndCRFVersionId(
					studyBean, studyEventBean.getId(), eventCRFBean.getCRFVersionId());
			if ((eventDefinitionCrf.getSourceDataVerification() == SourceDataVerification.AllREQUIRED || eventDefinitionCrf
					.getSourceDataVerification() == SourceDataVerification.PARTIALREQUIRED)
					&& !eventCRFBean.isSdvStatus()
					&& eventCRFBean.getStatus() == Status.UNAVAILABLE
					&& eventCRFBean.getStage() == DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE) {
				hasReadyForSDV = true;
			}
		}
		return hasReadyForSDV;
	}
}
