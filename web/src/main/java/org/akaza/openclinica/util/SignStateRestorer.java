package org.akaza.openclinica.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;

public class SignStateRestorer {

	private Map<Integer, EventDefinitionInfo> eventDefinitionInfoMap = new HashMap<Integer, EventDefinitionInfo>();

	public Map<Integer, EventDefinitionInfo> getEventDefinitionInfoMap() {
		return eventDefinitionInfoMap;
	}

	public static Map<Integer, SignedData> initPreSignedData(SubjectEventStatus savedCurrentSubjectEventStatus,
			SignStateRestorer signStateRestorer) {
		Map<Integer, SignedData> signedData = new HashMap<Integer, SignedData>();
		if (signStateRestorer != null && savedCurrentSubjectEventStatus == SubjectEventStatus.SIGNED) {
			for (EventDefinitionInfo edi : signStateRestorer.getEventDefinitionInfoMap().values()) {
				SignedData sd = new SignedData();
				sd.eventDefinitionInfo = edi;
				signedData.put(edi.id, sd);
			}
		}
		return signedData;
	}

	public static Map<Integer, SignedData> initPostSignedData(List<EventDefinitionCRFBean> eventDefCrfs) {
		Map<Integer, SignedData> signedData = new HashMap<Integer, SignedData>();
		for (EventDefinitionCRFBean eventDefinitionCrf : eventDefCrfs) {
			if (eventDefinitionCrf.getStatus() != Status.AVAILABLE || !eventDefinitionCrf.isActive())
				continue;
			SignedData sd = new SignedData();
			EventDefinitionInfo edi = new EventDefinitionInfo();
			edi.id = eventDefinitionCrf.getId();
			edi.required = eventDefinitionCrf.isRequiredCRF();
			edi.defaultVersionId = eventDefinitionCrf.getDefaultVersionId();
			sd.eventDefinitionInfo = edi;
			signedData.put(edi.id, sd);
		}
		return signedData;
	}

	public static void prepareSignedData(EventCRFBean eventCRFBean, EventDefinitionCRFBean eventDefinitionCrf, Map<Integer, SignedData> signedData) {
		SignedData sd = signedData.get(eventDefinitionCrf.getId());
		if (sd != null) {
			EventCrfInfo eci = new EventCrfInfo();
			eci.id = eventCRFBean.getId();
			eci.sdv = eventCRFBean.isSdvStatus();
			eci.statusId = eventCRFBean.getStatus().getId();
			sd.eventCrfInfo = eci;
		}
	}

	public static void restoreSignState(StudyEventBean studyEventBean,
			SubjectEventStatus savedCurrentSubjectEventStatus, SubjectEventStatus savedPrevSubjectEventStatus,
			Map<Integer, SignedData> preSignedData, Map<Integer, SignedData> postSignedData,
			SignStateRestorer signStateRestorer) {
		if (signStateRestorer != null) {
			if (savedCurrentSubjectEventStatus.equals(SubjectEventStatus.SIGNED)) {
				studyEventBean.setSignedData(preSignedData);
			} else {
				boolean restoreSignState = false;
				if ((studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.SCHEDULED)
						|| studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.COMPLETED) || studyEventBean
						.getSubjectEventStatus().equals(SubjectEventStatus.SOURCE_DATA_VERIFIED))
						&& savedPrevSubjectEventStatus.equals(SubjectEventStatus.SIGNED)
						&& studyEventBean.getSignedData() != null && studyEventBean.getSignedData().size() > 0) {
                    restoreSignState = SignStateRestorer.equals(studyEventBean.getSignedData(), postSignedData);
				}
				if (restoreSignState) {
					studyEventBean.setSubjectEventStatus(SubjectEventStatus.SIGNED);
				}
				studyEventBean.getSignedData().clear();
			}
		}
	}

	public static boolean equals(Map<Integer, SignedData> savedSignedData, Map<Integer, SignedData> postSignedData) {
		boolean equals = savedSignedData.size() == postSignedData.size()
				&& savedSignedData.keySet().containsAll(postSignedData.keySet())
				&& postSignedData.keySet().containsAll(savedSignedData.keySet());
		if (equals) {
			for (Integer id : savedSignedData.keySet()) {
				if (!equals(savedSignedData.get(id), postSignedData.get(id))) {
                    equals = false;
					break;
				}
			}
		}
		return equals;
	}

	private static boolean equals(SignedData savedSignedData, SignedData postSignedData) {
		return savedSignedData.eventDefinitionInfo.id == postSignedData.eventDefinitionInfo.id
				&& savedSignedData.eventDefinitionInfo.defaultVersionId == postSignedData.eventDefinitionInfo.defaultVersionId
				&& savedSignedData.eventDefinitionInfo.required == postSignedData.eventDefinitionInfo.required
				&& ((savedSignedData.eventCrfInfo == null && postSignedData.eventCrfInfo == null) || (savedSignedData.eventCrfInfo != null
						&& postSignedData.eventCrfInfo != null
						&& savedSignedData.eventCrfInfo.id == postSignedData.eventCrfInfo.id
						&& savedSignedData.eventCrfInfo.sdv == postSignedData.eventCrfInfo.sdv && savedSignedData.eventCrfInfo.statusId == postSignedData.eventCrfInfo.statusId));
	}
}
