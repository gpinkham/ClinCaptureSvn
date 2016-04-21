/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 *
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clincapture.com/contact for pricing information.
 *
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use.
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.util.EventCrfInfo;
import org.akaza.openclinica.util.EventDefinitionInfo;
import org.akaza.openclinica.util.SignedData;

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

	public static void prepareSignedData(EventCRFBean eventCRFBean, EventDefinitionCRFBean eventDefinitionCrf,
			Map<Integer, SignedData> signedData) {
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
			Map<Integer, SignedData> preSignedData, Map<Integer, SignedData> postSignedData) {
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
