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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.domain.SourceDataVerification;
import org.akaza.openclinica.util.EventDefinitionInfo;
import org.akaza.openclinica.util.SignedData;

/**
 * SubjectEventStatusUtil class.
 */
@SuppressWarnings({"unchecked", "unused"})
public final class SubjectEventStatusUtil {

	private static HashMap<Integer, String> imageIconPaths = new HashMap<Integer, String>();

	static {
		int index = 1;
		imageIconPaths.put(index++, "images/icon_Scheduled.gif");
		imageIconPaths.put(index++, "images/icon_NotStarted.gif");
		imageIconPaths.put(index++, "images/icon_InitialDE.gif");
		imageIconPaths.put(index++, "images/icon_DEcomplete.gif");
		imageIconPaths.put(index++, "images/icon_Stopped.gif");
		imageIconPaths.put(index++, "images/icon_Skipped.gif");
		imageIconPaths.put(index++, "images/icon_Locked.gif");
		imageIconPaths.put(index++, "images/icon_Signed.gif");
		imageIconPaths.put(index++, "images/icon_DoubleCheck.gif");
		imageIconPaths.put(index, "images/icon_Invalid.gif");
	}

	private SubjectEventStatusUtil() {
	}

	public static HashMap<Integer, String> getImageIconPaths() {
		return imageIconPaths;
	}

	/**
	 * Method returns an icon for SubjectEventStatus.
	 * 
	 * @param status
	 *            SubjectEventStatus
	 * @return String
	 */
	public static String getSESIconUrl(SubjectEventStatus status) {
		String iconUrl = imageIconPaths.get(status.getId());
		return iconUrl == null ? "" : iconUrl;
	}

	/**
	 * Method determines an icon for study event on the subject matrix.
	 * 
	 * @param url
	 *            StringBuilder
	 * @param currentStudy
	 *            StudyBean
	 * @param studySubjectBean
	 *            StudySubjectBean
	 * @param studyEvents
	 *            List<StudyEventBean>
	 * @param subjectEventStatus
	 *            SubjectEventStatus
	 * @param resword
	 *            ResourceBundle
	 * @param permissionForDynamic
	 *            boolean
	 */
	public static void determineSubjectEventIconOnTheSubjectMatrix(StringBuilder url, StudyBean currentStudy,
			StudySubjectBean studySubjectBean, List<StudyEventBean> studyEvents, SubjectEventStatus subjectEventStatus,
			ResourceBundle resword, boolean permissionForDynamic) {
		if (studyEvents.size() <= 1) {
			if (studySubjectBean.getStatus().isLocked() && subjectEventStatus == SubjectEventStatus.NOT_SCHEDULED) {
				String txt = resword.getString("locked");
				url.append("<img src='").append(imageIconPaths.get(SubjectEventStatus.LOCKED.getId()))
						.append("' title='").append(txt).append("' alt='").append(txt)
						.append("' onmouseout='clearInterval(popupInterval);' onmouseover='if (!subjectMatrixPopupStick) { clearInterval(popupInterval); popupInterval = setInterval(function() { clearInterval(popupInterval); hideAllTooltips(); }, 500); }' border='0' style='position: relative;'>");
			} else if (studySubjectBean.getStatus().isDeleted()
					&& subjectEventStatus == SubjectEventStatus.NOT_SCHEDULED) {
				String txt = resword.getString("removed");
				url.append("<img src='").append(imageIconPaths.get(SubjectEventStatus.REMOVED.getId()))
						.append("' title='").append(txt).append("' alt='").append(txt)
						.append("' onmouseout='clearInterval(popupInterval);' onmouseover='if (!subjectMatrixPopupStick) { clearInterval(popupInterval); popupInterval = setInterval(function() { clearInterval(popupInterval); hideAllTooltips(); }, 500); }' border='0' style='position: relative;'>");
			} else {
				if (!subjectEventStatus.isNotScheduled() || permissionForDynamic) {
					url.append("<img src='").append(imageIconPaths.get(subjectEventStatus.getId()))
							.append("' border='0' style='position: relative;' title=\"")
							.append(currentStudy.getStatus() != Status.AVAILABLE && subjectEventStatus.isNotScheduled()
									? resword.getString("message_for_not_scheduled_event_if_study_is_not_available")
									: "")
							.append("\" alt=\"")
							.append(currentStudy.getStatus() != Status.AVAILABLE && subjectEventStatus.isNotScheduled()
									? resword.getString("message_for_not_scheduled_event_if_study_is_not_available")
									: "")
							.append("\">");
				} else {
					url.delete(0, url.length());
					url.append("<img src='' border='0' style='position: relative; display: none;'>");
				}
			}
		} else {
			int index = 0;
			SubjectEventStatus status = null;
			Map<SubjectEventStatus, Integer> scoreMap = new HashMap<SubjectEventStatus, Integer>();
			scoreMap.put(SubjectEventStatus.INVALID, index++);
			scoreMap.put(SubjectEventStatus.SCHEDULED, index++);
			scoreMap.put(SubjectEventStatus.DATA_ENTRY_STARTED, index++);
			scoreMap.put(SubjectEventStatus.COMPLETED, index++);
			scoreMap.put(SubjectEventStatus.SOURCE_DATA_VERIFIED, index++);
			scoreMap.put(SubjectEventStatus.SIGNED, index++);
			scoreMap.put(SubjectEventStatus.REMOVED, index++);
			scoreMap.put(SubjectEventStatus.STOPPED, index++);
			scoreMap.put(SubjectEventStatus.SKIPPED, index++);
			scoreMap.put(SubjectEventStatus.LOCKED, index);
			for (StudyEventBean studyEventBean : studyEvents) {
				Integer statusScore = scoreMap.get(status);
				Integer studyEventBeanScore = scoreMap.get(studyEventBean.getSubjectEventStatus());
				if (status == null
						|| (studyEventBeanScore != null && statusScore != null && studyEventBeanScore < statusScore)) {
					status = studyEventBean.getSubjectEventStatus();
				}
			}
			url.append("<img src='")
					.append(imageIconPaths.get((status == null ? SubjectEventStatus.SCHEDULED : status).getId()))
					.append("' border='0' style='position: relative;'>");
		}
	}

	/**
	 * Method that determines the subject event state for study event by incoming parameters.
	 * 
	 * @param studyEventBean
	 *            StudyEventBean
	 * @param eventCRFs
	 *            List<EventCRFBean>
	 * @param daoWrapper
	 *            DAOWrapper
	 */
	public static void determineSubjectEventState(StudyEventBean studyEventBean, List<EventCRFBean> eventCRFs,
			DAOWrapper daoWrapper) {
		analyzeSubjectEventState(studyEventBean, eventCRFs, daoWrapper, null);
		daoWrapper.getSedao().update(studyEventBean);
	}

	/**
	 * Method that determines the subject event state for study event by incoming parameters.
	 * 
	 * @param studyEventBean
	 *            StudyEventBean
	 * @param daoWrapper
	 *            DAOWrapper
	 */
	public static void determineSubjectEventState(StudyEventBean studyEventBean, DAOWrapper daoWrapper) {
		ArrayList<EventCRFBean> eventCRFs = daoWrapper.getEcdao().findAllByStudyEvent(studyEventBean);
		analyzeSubjectEventState(studyEventBean, eventCRFs, daoWrapper, null);
	}

	/**
	 * Method that determines the subject event state for study event by incoming parameters.
	 * 
	 * @param studyEventBean
	 *            StudyEventBean
	 * @param daoWrapper
	 *            DAOWrapper
	 * @param signStateRestorerMap
	 *            Map<Integer, SignStateRestorer>
	 */
	public static void determineSubjectEventState(StudyEventBean studyEventBean, DAOWrapper daoWrapper,
			Map<Integer, SignStateRestorer> signStateRestorerMap) {
		ArrayList<EventCRFBean> eventCRFs = daoWrapper.getEcdao().findAllByStudyEvent(studyEventBean);
		analyzeSubjectEventState(studyEventBean, eventCRFs, daoWrapper, signStateRestorerMap);
	}

	/**
	 * Method that determines the subject event states for study events by incoming parameters.
	 * 
	 * @param sed
	 *            StudyEventDefinitionBean
	 * @param ss
	 *            StudySubjectBean
	 * @param userAccountBean
	 *            UserAccountBean
	 * @param daoWrapper
	 *            DAOWrapper
	 */
	public static void determineSubjectEventStates(StudyEventDefinitionBean sed, StudySubjectBean ss,
			UserAccountBean userAccountBean, DAOWrapper daoWrapper) {
		List<StudyEventBean> studyEvents = daoWrapper.getSedao().findAllByDefinitionAndSubject(sed, ss);
		determineSubjectEventStates(studyEvents, userAccountBean, daoWrapper, null);
	}

	/**
	 * Method that determines the subject event states for study events by incoming parameters.
	 *
	 * @param studyEvents
	 *            List<StudyEventBean>
	 * @param userAccountBean
	 *            UserAccountBean
	 * @param daoWrapper
	 *            DAOWrapper
	 * @param signStateRestorerMap
	 *            Map<Integer, SignStateRestorer>
	 */
	public static void determineSubjectEventStates(List<StudyEventBean> studyEvents, UserAccountBean userAccountBean,
			DAOWrapper daoWrapper, Map<Integer, SignStateRestorer> signStateRestorerMap) {
		for (StudyEventBean studyEventBean : studyEvents) {
			determineSubjectEventState(studyEventBean, daoWrapper, signStateRestorerMap);
			studyEventBean.setUpdater(userAccountBean);
			studyEventBean.setUpdatedDate(new Date());
			daoWrapper.getSedao().update(studyEventBean);
		}
	}

	/**
	 * Method that determines the subject event states for study events by incoming parameters.
	 *
	 * @param studyEventDefinitionBean
	 *            StudyEventDefinitionBean
	 * @param userAccountBean
	 *            UserAccountBean
	 * @param daoWrapper
	 *            DAOWrapper
	 * @param signStateRestorerMap
	 *            Map<Integer, SignStateRestorer>
	 */
	public static void determineSubjectEventStates(StudyEventDefinitionBean studyEventDefinitionBean,
			UserAccountBean userAccountBean, DAOWrapper daoWrapper,
			Map<Integer, SignStateRestorer> signStateRestorerMap) {
		StudyBean study = (StudyBean) daoWrapper.getSdao().findByPK(studyEventDefinitionBean.getStudyId());
		List<StudyEventBean> studyEvents = (ArrayList<StudyEventBean>) daoWrapper.getSedao()
				.findAllByStudyAndEventDefinitionId(study, studyEventDefinitionBean.getId());
		// findAllByStudyAndEventDefinitionIdExceptLockedSkippedStoppedRemoved
		for (StudyEventBean studyEventBean : studyEvents) {
			boolean studyEventIsNotAvailable = false;
			SubjectEventStatus subjectEventStatus = studyEventBean.getSubjectEventStatus();
			SubjectEventStatus prevSubjectEventStatus = studyEventBean.getPrevSubjectEventStatus();
			if (prevSubjectEventStatus.isSkipped() || prevSubjectEventStatus.isStopped()) {
				continue;
			}
			if (subjectEventStatus.isLocked() || subjectEventStatus.isSkipped() || subjectEventStatus.isStopped()
					|| subjectEventStatus.isRemoved()) {
				studyEventIsNotAvailable = true;
			}
			determineSubjectEventState(studyEventBean, daoWrapper, signStateRestorerMap);
			if (studyEventIsNotAvailable) {
				if (!studyEventBean.getSubjectEventStatus().equals(prevSubjectEventStatus.isSigned()
						? SubjectEventStatus.SOURCE_DATA_VERIFIED
						: prevSubjectEventStatus)) {
					studyEventBean.setPrevSubjectEventStatus(studyEventBean.getSubjectEventStatus());
					studyEventBean.setSubjectEventStatus(subjectEventStatus);
				} else {
					continue;
				}
			}
			studyEventBean.setUpdater(userAccountBean);
			studyEventBean.setUpdatedDate(new Date());
			daoWrapper.getSedao().update(studyEventBean);
		}
	}

	private enum State {
		DES(1), DEC(2), SDV(3), DENS(4);

		private int id;

		public int getId() {
			return id;
		}

		State(int id) {
			this.id = id;
		}
	}

	private static State getHighestState(State currentState, State otherState) {
		State result = otherState;
		if (currentState != null && currentState.getId() <= otherState.getId()) {
			result = currentState;
		}
		return result;
	}

	private static State getState(EventCRFBean eventCRFBean) {
		State result = State.DENS;
		if (!eventCRFBean.isNotStarted()) {
			result = State.DES;
			if (eventCRFBean.getStatus() == Status.UNAVAILABLE) {
				result = State.DEC;
				if (eventCRFBean.isSdvStatus()) {
					result = State.SDV;
				}
			}
		}
		return result;
	}

	private static void setSubjectEventState(StudyEventBean studyEventBean, StudyBean studyBean, DAOWrapper daoWrapper,
			State state) {
		switch (state) {
			case DES :
				studyEventBean.setSubjectEventStatus(SubjectEventStatus.DATA_ENTRY_STARTED);
				break;
			case DEC :
				SubjectEventStatus status = SubjectEventStatus.COMPLETED;
				if (studyEventBean.getSubjectEventStatus() == SubjectEventStatus.SIGNED) {
					status = SignUtil.permitSign(studyEventBean, studyBean, daoWrapper)
							? SubjectEventStatus.SIGNED
							: status;
				}
				studyEventBean.setSubjectEventStatus(status);
				break;
			case SDV :
				status = SubjectEventStatus.SOURCE_DATA_VERIFIED;
				if (studyEventBean.getSubjectEventStatus() == SubjectEventStatus.SIGNED) {
					status = SignUtil.permitSign(studyEventBean, studyBean, daoWrapper)
							? SubjectEventStatus.SIGNED
							: status;
				}
				studyEventBean.setSubjectEventStatus(status);
				break;
			case DENS :
				if (!studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.NOT_SCHEDULED)) {
					studyEventBean.setSubjectEventStatus(SubjectEventStatus.SCHEDULED);
				}
				break;
			default :
		}
	}

	private static SignStateRestorer prepareSignStateRestorer(List<EventDefinitionCRFBean> eventDefinitionCRFBeanList) {
		SignStateRestorer signStateRestorer = new SignStateRestorer();
		for (EventDefinitionCRFBean eventDefinitionCRFBean : eventDefinitionCRFBeanList) {
			if (eventDefinitionCRFBean.getStatus() != Status.AVAILABLE || !eventDefinitionCRFBean.isActive()) {
				continue;
			}
			EventDefinitionInfo edi = new EventDefinitionInfo();
			edi.id = eventDefinitionCRFBean.getId();
			edi.required = eventDefinitionCRFBean.isRequiredCRF();
			edi.defaultVersionId = eventDefinitionCRFBean.getDefaultVersionId();
			signStateRestorer.getEventDefinitionInfoMap().put(eventDefinitionCRFBean.getId(), edi);
		}
		return signStateRestorer;
	}

	private static void analyzeSubjectEventState(StudyEventBean studyEventBean, List<EventCRFBean> eventCRFs,
			DAOWrapper daoWrapper, Map<Integer, SignStateRestorer> signStateRestorerMap) {
		State state = State.DENS;
		boolean hasStarted = false;
		boolean justScheduled = true;
		boolean hasSDVRequiredCRFs = false;
		List<Integer> crfsToProcess = new ArrayList<Integer>();
		List<Integer> requiredCrfIds = new ArrayList<Integer>();
		SubjectEventStatus savedPrevSubjectEventStatus = studyEventBean.getPrevSubjectEventStatus();
		SubjectEventStatus savedCurrentSubjectEventStatus = studyEventBean.getSubjectEventStatus();
		StudySubjectBean studySubjectBean = daoWrapper.getSsdao().findByPK(studyEventBean.getStudySubjectId());
		StudyBean studySubjectStudyBean = (StudyBean) daoWrapper.getSdao().findByPK(studySubjectBean.getStudyId());
		List<EventDefinitionCRFBean> eventDefCrfs = (List<EventDefinitionCRFBean>) daoWrapper.getEdcdao()
				.findAllActiveByEventDefinitionId(studySubjectStudyBean, studyEventBean.getStudyEventDefinitionId());
		SignStateRestorer signStateRestorer = signStateRestorerMap != null
				? signStateRestorerMap.get(studySubjectBean.getStudyId())
				: null;
		Map<Integer, SignedData> preSignedData = SignStateRestorer.initPreSignedData(savedCurrentSubjectEventStatus,
				signStateRestorer);
		Map<Integer, SignedData> postSignedData = SignStateRestorer.initPostSignedData(eventDefCrfs);
		for (EventDefinitionCRFBean eventDefinitionCrf : eventDefCrfs) {
			if (eventDefinitionCrf.getStatus() != Status.AVAILABLE || !eventDefinitionCrf.isActive()
					|| eventDefinitionCrf.isHideCrf()) {
				continue;
			}
			crfsToProcess.add(eventDefinitionCrf.getCrfId());
			if (eventDefinitionCrf.isRequiredCRF()) {
				requiredCrfIds.add(eventDefinitionCrf.getId());
			}
			if (eventDefinitionCrf.getSourceDataVerification() == SourceDataVerification.AllREQUIRED
					|| eventDefinitionCrf.getSourceDataVerification() == SourceDataVerification.PARTIALREQUIRED) {
				hasSDVRequiredCRFs = true;
			}
		}
		boolean hasSDVedCRFs = false;
		boolean hasRequiredCRFs = requiredCrfIds.size() > 0;
		boolean notAllStartedSDVRequiredCRFsAreSDVed = false;
		for (EventCRFBean eventCRFBean : eventCRFs) {
			int crfId = daoWrapper.getCvdao().getCRFIdFromCRFVersionId(eventCRFBean.getCRFVersionId());
			if (eventCRFBean.isNotStarted() || !crfsToProcess.contains(crfId)) {
				continue;
			}
			justScheduled = false;
			EventDefinitionCRFBean eventDefinitionCrf = daoWrapper.getEdcdao().findByStudyEventIdAndCRFVersionId(
					studySubjectStudyBean, studyEventBean.getId(), eventCRFBean.getCRFVersionId());
			SignStateRestorer.prepareSignedData(eventCRFBean, eventDefinitionCrf, preSignedData);
			SignStateRestorer.prepareSignedData(eventCRFBean, eventDefinitionCrf, postSignedData);
			if (!eventCRFBean.statesIsEmpty()) {
				Status status = eventCRFBean.getOldStatus();
				if (!status.isInvalid() && !status.isDeleted() && !status.isLocked()) {
					eventCRFBean.setStatus(status);
				} else {
					eventCRFBean
							.setStatus(eventCRFBean.getDateCompleted() != null ? Status.UNAVAILABLE : Status.AVAILABLE);
				}
				eventCRFBean.setStage(null);
			}
			State eventCRFState = getState(eventCRFBean);
			if (eventCRFBean.getStatus() == Status.UNAVAILABLE
					&& eventCRFBean.getStage() == DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE) {
				if (eventDefinitionCrf.isRequiredCRF()) {
					requiredCrfIds.remove((Integer) eventDefinitionCrf.getId());
				}
				if (hasSDVRequiredCRFs && !eventCRFBean.isSdvStatus()
						&& (eventDefinitionCrf.getSourceDataVerification() == SourceDataVerification.AllREQUIRED
								|| eventDefinitionCrf
										.getSourceDataVerification() == SourceDataVerification.PARTIALREQUIRED)) {
					notAllStartedSDVRequiredCRFsAreSDVed = true;
				}
				if (!hasSDVedCRFs && eventCRFBean.isSdvStatus()) {
					hasSDVedCRFs = true;
				}
			} else {
				hasStarted = true;
			}
			state = getHighestState(state, eventCRFState);
		}
		if (!justScheduled && !hasStarted) {
			if (hasRequiredCRFs) {
				state = requiredCrfIds.size() > 0 ? State.DES : state;
			}
			if (hasSDVedCRFs && hasSDVRequiredCRFs && !notAllStartedSDVRequiredCRFsAreSDVed && state == State.DEC) {
				state = State.SDV;
			}
		}
		setSubjectEventState(studyEventBean, studySubjectStudyBean, daoWrapper, state);
		if (!studyEventBean.getSubjectEventStatus().equals(savedCurrentSubjectEventStatus)
				|| (studyEventBean.getPrevSubjectEventStatus().equals(SubjectEventStatus.SIGNED)
						&& studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.SCHEDULED))) {
			studyEventBean.setPrevSubjectEventStatus(savedCurrentSubjectEventStatus);
			if (signStateRestorer != null) {
				SignStateRestorer.restoreSignState(studyEventBean, savedCurrentSubjectEventStatus,
						savedPrevSubjectEventStatus, preSignedData, postSignedData);
			}
		}
		if (!studyEventBean.getSubjectEventStatus().isSigned() && studySubjectBean.getStatus().isSigned()) {
			studySubjectBean.setStatus(Status.AVAILABLE);
			studySubjectBean.setUpdatedDate(new Date());
			daoWrapper.getSsdao().update(studySubjectBean);
		}
	}

	/**
	 * Method fills the double data owner for event crfs.
	 * 
	 * @param eventCRFs
	 *            ArrayList<EventCRFBean>
	 * @param sm
	 *            SessionManager
	 */
	public static void fillDoubleDataOwner(ArrayList<EventCRFBean> eventCRFs, SessionManager sm) {
		// validatorId field was used for another purpose in the past, but now it is used for DoubleDataEntry
		UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
		for (EventCRFBean eventCRF : eventCRFs) {
			if (eventCRF.getValidatorId() > 0) {
				eventCRF.setDoubleDataOwner((UserAccountBean) udao.findByPK(eventCRF.getValidatorId()));
			}
		}
	}

	/**
	 * Determines the list of available statuses for manual transition for a study event entity, based on its current
	 * status and updater's role.
	 *
	 * @param currentStatus
	 *            current status of a study event
	 * @param updaterRole
	 *            updater role
	 * @param permitSign
	 *            boolean
	 * @param permitSDV
	 *            boolean
	 * @return list of available statuses for manual transition, including the current status
	 */
	public static List<SubjectEventStatus> getAvailableStatusesForManualTransition(SubjectEventStatus currentStatus,
			StudyUserRoleBean updaterRole, boolean permitSign, boolean permitSDV) {

		boolean addSDV = false;
		boolean addSign = false;

		permitSign = permitSign && Role.INVESTIGATOR.equals(updaterRole.getRole());
		permitSDV = permitSDV && (Role.STUDY_ADMINISTRATOR.equals(updaterRole.getRole())
				|| Role.SYSTEM_ADMINISTRATOR.equals(updaterRole.getRole())
				|| Role.STUDY_MONITOR.equals(updaterRole.getRole()));

		List<SubjectEventStatus> statusesForTransition = new ArrayList<SubjectEventStatus>();
		statusesForTransition.add(currentStatus);

		if (currentStatus.isNotScheduled()) {
			statusesForTransition.add(SubjectEventStatus.SCHEDULED);
		} else if (currentStatus.isScheduled()) {
			statusesForTransition.add(SubjectEventStatus.NOT_SCHEDULED);
			statusesForTransition.add(SubjectEventStatus.SKIPPED);
		} else if (currentStatus.isDE_Started()) {
			statusesForTransition.add(SubjectEventStatus.STOPPED);
		} else if (currentStatus.isStopped()) {
			addSDV = permitSDV;
			addSign = permitSign;
			statusesForTransition.add(SubjectEventStatus.DATA_ENTRY_STARTED);
		} else if (currentStatus.isSkipped()) {
			statusesForTransition.add(SubjectEventStatus.SCHEDULED);
		} else if (currentStatus.isSigned()) {
			addSDV = permitSDV;
		} else if (currentStatus.isSourceDataVerified()) {
			addSign = permitSign;
			statusesForTransition.add(SubjectEventStatus.COMPLETED);
		} else if (currentStatus.isCompleted()) {
			addSDV = permitSDV;
			addSign = permitSign;
		}

		if (addSDV) {
			statusesForTransition.add(SubjectEventStatus.SOURCE_DATA_VERIFIED);
		}

		if (addSign) {
			statusesForTransition.add(SubjectEventStatus.SIGNED);
		}

		if (Role.STUDY_ADMINISTRATOR.equals(updaterRole.getRole())
				|| Role.SYSTEM_ADMINISTRATOR.equals(updaterRole.getRole())) {
			if (currentStatus.isLocked()) {
				statusesForTransition.add(SubjectEventStatus.UNLOCK);
			} else if (currentStatus.isRemoved()) {
				statusesForTransition.add(SubjectEventStatus.RESTORE);
			} else {
				statusesForTransition.add(SubjectEventStatus.LOCKED);
				statusesForTransition.add(SubjectEventStatus.REMOVED);
			}
		}

		return statusesForTransition;
	}
}
