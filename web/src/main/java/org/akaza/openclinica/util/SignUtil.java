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

package org.akaza.openclinica.util;

import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.domain.SourceDataVerification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class SignUtil {

	private SignUtil() {
	}

	@SuppressWarnings("unchecked")
	public static boolean permitSign(StudySubjectBean studySubjectBean, DAOWrapper daoWrapper) {
		boolean sign = !studySubjectBean.getStatus().isSigned();
		if (sign) {
			int signedEvents = 0;
			ArrayList<StudyEventBean> studyEvents = daoWrapper.getSedao().findAllByStudySubject(studySubjectBean);
			for (StudyEventBean studyEventBean : (ArrayList<StudyEventBean>) studyEvents) {
				if (studyEventBean.getSubjectEventStatus() != SubjectEventStatus.SIGNED) {
					StudyBean studyBean = (StudyBean) daoWrapper.getSdao().findByPK(studySubjectBean.getStudyId());
					sign = permitSign(studyEventBean, studyBean, daoWrapper);
					if (!sign)
						break;
				} else {
					signedEvents++;
				}
			}
			sign = signedEvents == studyEvents.size() ? false : sign;
		}
		return sign;
	}

	@SuppressWarnings("unchecked")
	public static boolean permitSign(StudyEventBean studyEventBean, StudyBean studyBean, DAOWrapper daoWrapper) {
		boolean sign = daoWrapper.getDiscDao().doesNotHaveOutstandingDNs(studyEventBean);
		if (sign) {
			List<EventCRFBean> eventCrfs = daoWrapper.getEcdao().findAllStartedByStudyEvent(studyEventBean);
			if (studyEventBean.getSubjectEventStatus() == SubjectEventStatus.NOT_SCHEDULED
					|| (studyEventBean.getSubjectEventStatus() == SubjectEventStatus.SCHEDULED && eventCrfs.size() == 0)
					|| studyEventBean.getSubjectEventStatus() == SubjectEventStatus.STOPPED
					|| studyEventBean.getSubjectEventStatus() == SubjectEventStatus.SKIPPED) {
				sign = true;
			} else if (studyEventBean.getSubjectEventStatus() == SubjectEventStatus.COMPLETED
					|| studyEventBean.getSubjectEventStatus() == SubjectEventStatus.SOURCE_DATA_VERIFIED) {
				Collection<EventDefinitionCRFBean> eventDefCrfs = daoWrapper.getEdcdao().findAllByDefinition(studyBean,
						studyEventBean.getStudyEventDefinitionId());
				List<Integer> requiredCrfIds = new ArrayList<Integer>();
				for (EventDefinitionCRFBean eventDefinitionCrf : (ArrayList<EventDefinitionCRFBean>) eventDefCrfs) {
					if (eventDefinitionCrf.getStatus() != Status.AVAILABLE || !eventDefinitionCrf.isActive())
						continue;
					if (eventDefinitionCrf.isActive() && eventDefinitionCrf.isRequiredCRF()) {
						requiredCrfIds.add(eventDefinitionCrf.getId());
					}
				}
				for (EventCRFBean eventCRFBean : eventCrfs) {
					if (eventCRFBean.isNotStarted())
						continue;
					EventDefinitionCRFBean eventDefinitionCrf = daoWrapper.getEdcdao()
							.findByStudyEventIdAndCRFVersionId(studyBean, studyEventBean.getId(),
									eventCRFBean.getCRFVersionId());
					requiredCrfIds.remove((Integer) eventDefinitionCrf.getId());

					if (!(eventCRFBean.getStatus() == Status.UNAVAILABLE && eventCRFBean.getStage().equals(
							DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE))) {
						sign = false;
						break;
					}
					if ((eventDefinitionCrf.getSourceDataVerification() == SourceDataVerification.AllREQUIRED || eventDefinitionCrf
							.getSourceDataVerification() == SourceDataVerification.PARTIALREQUIRED)
							&& !eventCRFBean.isSdvStatus()) {
						sign = false;
						break;
					}
				}
				sign = sign ? requiredCrfIds.size() == 0 : sign;
			} else {
				sign = studyEventBean.getSubjectEventStatus() == SubjectEventStatus.SIGNED;
			}
		}
		return sign;
	}
}
