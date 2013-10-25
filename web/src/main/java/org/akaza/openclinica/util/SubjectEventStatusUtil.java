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
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
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

// be careful when you change the logic of this class (talk with Marc or Sergey) !
@SuppressWarnings({ "unchecked" })
public final class SubjectEventStatusUtil {

	final static HashMap<Integer, String> imageIconPaths = new HashMap<Integer, String>();

	static {
		imageIconPaths.put(1, "images/icon_Scheduled.gif");
		imageIconPaths.put(2, "images/icon_NotStarted.gif");
		imageIconPaths.put(3, "images/icon_InitialDE.gif");
		imageIconPaths.put(4, "images/icon_DEcomplete.gif");
		imageIconPaths.put(5, "images/icon_Stopped.gif");
		imageIconPaths.put(6, "images/icon_Skipped.gif");
		imageIconPaths.put(7, "images/icon_Locked.gif");
		imageIconPaths.put(8, "images/icon_Signed.gif");
		imageIconPaths.put(9, "images/icon_DoubleCheck.gif");
		imageIconPaths.put(10, "images/icon_Invalid.gif");
	}

	private SubjectEventStatusUtil() {
	}

	public static String getSESIconUrl(SubjectEventStatus status) {
		String iconUrl = imageIconPaths.get(status.getId());
		return iconUrl == null ? "" : iconUrl;
	}

	public static void determineSubjectEventIconOnTheSubjectMatrix(StringBuilder url,
			HashMap<Integer, String> imageIconPaths, StudyBean currentStudy, StudySubjectBean studySubjectBean,
			List<StudyEventBean> studyEvents, SubjectEventStatus subjectEventStatus, ResourceBundle resword, boolean permission_for_dynamic) {
		if (studyEvents.size() <= 1) {
			if (studySubjectBean.getStatus().isLocked() && subjectEventStatus == SubjectEventStatus.NOT_SCHEDULED) {
				String txt = resword.getString("locked");
				url.append("<img src='"
						+ imageIconPaths.get(SubjectEventStatus.LOCKED.getId())
						+ "' title='"
						+ txt
						+ "' alt='"
						+ txt
						+ "' onmouseout='clearInterval(popupInterval);' onmouseover='if (!subjectMatrixPopupStick) { clearInterval(popupInterval); popupInterval = setInterval(function() { clearInterval(popupInterval); hideAllTooltips(); }, 500); }' border='0' style='position: relative;'>");
			} else if (studySubjectBean.getStatus().isDeleted()
					&& subjectEventStatus == SubjectEventStatus.NOT_SCHEDULED) {
				String txt = resword.getString("removed");
				url.append("<img src='"
						+ imageIconPaths.get(SubjectEventStatus.REMOVED.getId())
						+ "' title='"
						+ txt
						+ "' alt='"
						+ txt
						+ "' onmouseout='clearInterval(popupInterval);' onmouseover='if (!subjectMatrixPopupStick) { clearInterval(popupInterval); popupInterval = setInterval(function() { clearInterval(popupInterval); hideAllTooltips(); }, 500); }' border='0' style='position: relative;'>");
			} else {                
				if (!subjectEventStatus.isNotScheduled() || permission_for_dynamic) {
					url.append("<img src='"
							+ imageIconPaths.get(subjectEventStatus.getId())
							+ "' border='0' style='position: relative;' title=\""
							+ (currentStudy.getStatus() != Status.AVAILABLE && subjectEventStatus.isNotScheduled() ? resword
									.getString("message_for_not_scheduled_event_if_study_is_not_available") : "")
							+ "\" alt=\""
							+ (currentStudy.getStatus() != Status.AVAILABLE && subjectEventStatus.isNotScheduled() ? resword
									.getString("message_for_not_scheduled_event_if_study_is_not_available") : "")
							+ "\">");
				} else {
					url.append("<img src='' border='0' style='position: relative; display: none;'>");
				}	
			}
		} else {
			boolean des = false;
			int countOfSDVd = 0;
			int countOfLocked = 0;
			int countOfRemoved = 0;
			SubjectEventStatus minNotDESStatus = null;
			SubjectEventStatus status = subjectEventStatus;
			for (StudyEventBean studyEventBean : studyEvents) {
				if (studyEventBean.getSubjectEventStatus().getId() > SubjectEventStatus.DATA_ENTRY_STARTED.getId()
						&& (minNotDESStatus == null || studyEventBean.getSubjectEventStatus().getId() < minNotDESStatus
								.getId())) {
					minNotDESStatus = studyEventBean.getSubjectEventStatus();
				}

				if (studyEventBean.getSubjectEventStatus() == SubjectEventStatus.DATA_ENTRY_STARTED) {
					des = true;
				} else if (studyEventBean.getSubjectEventStatus() == SubjectEventStatus.LOCKED) {
					countOfLocked++;
				} else if (studyEventBean.getSubjectEventStatus() == SubjectEventStatus.REMOVED) {
					countOfRemoved++;
				} else if (studyEventBean.getSubjectEventStatus() == SubjectEventStatus.SOURCE_DATA_VERIFIED) {
					countOfSDVd++;
				}
			}
			if (des) {
				status = SubjectEventStatus.DATA_ENTRY_STARTED;
			} else if (countOfLocked == studyEvents.size()) {
				status = SubjectEventStatus.LOCKED;
			} else if (countOfRemoved == studyEvents.size()) {
				status = SubjectEventStatus.REMOVED;
			} else if (countOfSDVd == studyEvents.size()) {
				status = SubjectEventStatus.SOURCE_DATA_VERIFIED;
			} else if (!des && minNotDESStatus != null) {
				status = minNotDESStatus;
			}
			url.append("<img src='" + imageIconPaths.get(status.getId())
					+ "' border='0' style='position: relative;'>");
		}
	}

	public static void preparePossibleSubjectEventStates(List<EventCRFBean> events, List<SubjectEventStatus> statuses) {
		// TODO + add the checking of rights
		int countOfSDVd = 0;
		int countOfDeleted = 0;
		int countOfStarted = 0;
		for (EventCRFBean eventCRFBean : events) {
			if (!eventCRFBean.isNotStarted()) {
				countOfStarted++;
				if (eventCRFBean.isSdvStatus() && eventCRFBean.getStatus() != Status.AVAILABLE
						&& eventCRFBean.getStatus() != Status.INVALID) {
					countOfSDVd++;
				}
				if (eventCRFBean.getStatus() == Status.DELETED) {
					countOfDeleted++;
				}
			}
		}
		if (countOfStarted == 0 || countOfSDVd < countOfStarted) {
			statuses.remove(SubjectEventStatus.SOURCE_DATA_VERIFIED);
		}
		if (countOfStarted == 0 || countOfDeleted < countOfStarted) {
			statuses.remove(SubjectEventStatus.REMOVED);
		}
	}

	public static void determineSubjectEventState(StudyEventBean studyEventBean, StudyBean studyBean,
			List<EventCRFBean> eventCRFs, DAOWrapper daoWrapper) {
		analyzeSubjectEventState(studyEventBean, studyBean, eventCRFs, daoWrapper);
		daoWrapper.getSedao().update(studyEventBean);
	}

	public static void determineSubjectEventState(StudyEventBean studyEventBean, StudyBean studyBean,
			DAOWrapper daoWrapper) {
		ArrayList<EventCRFBean> eventCRFs = daoWrapper.getEcdao().findAllByStudyEvent(studyEventBean);
		analyzeSubjectEventState(studyEventBean, studyBean, eventCRFs, daoWrapper);
	}

	public static void determineSubjectEventStates(StudyEventDefinitionBean sed, StudySubjectBean ss,
			StudyBean studyBean, DAOWrapper daoWrapper) {
		List<StudyEventBean> studyEvents = daoWrapper.getSedao().findAllByDefinitionAndSubject(sed, ss);
		determineSubjectEventStates(studyEvents, studyBean, daoWrapper);
	}

	public static void determineSubjectEventStates(List<StudyEventBean> studyEvents, StudyBean studyBean,
			DAOWrapper daoWrapper) {
		for (StudyEventBean studyEventBean : studyEvents) {
			determineSubjectEventState(studyEventBean, studyBean, daoWrapper);
			daoWrapper.getSedao().update(studyEventBean);
		}
	}

	private static enum State {
		DES(1), DEC(2), SDV(3), DENS(4);

		int id;

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
		case DES: {
			studyEventBean.setSubjectEventStatus(SubjectEventStatus.DATA_ENTRY_STARTED);
			break;
		}
		case DEC: {
			SubjectEventStatus status = SubjectEventStatus.COMPLETED;
			if (studyEventBean.getSubjectEventStatus() == SubjectEventStatus.SIGNED) {
				status = SignUtil.permitSign(studyEventBean, studyBean, daoWrapper) ? SubjectEventStatus.SIGNED
						: status;
			}
			studyEventBean.setSubjectEventStatus(status);
			break;
		}
		case SDV: {
			SubjectEventStatus status = SubjectEventStatus.SOURCE_DATA_VERIFIED;
			if (studyEventBean.getSubjectEventStatus() == SubjectEventStatus.SIGNED) {
				status = SignUtil.permitSign(studyEventBean, studyBean, daoWrapper) ? SubjectEventStatus.SIGNED
						: status;
			}
			studyEventBean.setSubjectEventStatus(status);
			break;
		}
		case DENS: {
			studyEventBean.setSubjectEventStatus(SubjectEventStatus.SCHEDULED);
			break;
		}
		}
	}

	private static void analyzeSubjectEventState(StudyEventBean studyEventBean, StudyBean studyBean,
			List<EventCRFBean> eventCRFs, DAOWrapper daoWrapper) {
		State state = State.DENS;
		boolean hasStarted = false;
		boolean justScheduled = true;
		boolean hasSDVRequiredCRFs = false;
		List<Integer> requiredCrfIds = new ArrayList<Integer>();
		List<EventDefinitionCRFBean> eventDefCrfs = (List<EventDefinitionCRFBean>) daoWrapper.getEdcdao()
				.findAllByDefinition(studyBean, studyEventBean.getStudyEventDefinitionId());
		for (EventDefinitionCRFBean eventDefinitionCrf : eventDefCrfs) {
			if (eventDefinitionCrf.getStatus() != Status.AVAILABLE || !eventDefinitionCrf.isActive())
				continue;
			if (eventDefinitionCrf.isRequiredCRF()) {
				requiredCrfIds.add(eventDefinitionCrf.getId());
			}
			if (eventDefinitionCrf.getSourceDataVerification() == SourceDataVerification.AllREQUIRED
					|| eventDefinitionCrf.getSourceDataVerification() == SourceDataVerification.PARTIALREQUIRED) {
				hasSDVRequiredCRFs = true;
			}
		}
		boolean notAllStartedSDVRequiredCRFsAreSDVed = false;
		boolean hasRequiredCRFs = requiredCrfIds.size() > 0;
        boolean hasSDVedCRFs = false;
		for (EventCRFBean eventCRFBean : eventCRFs) {
			if (eventCRFBean.isNotStarted())
				continue;
			justScheduled = false;
			EventDefinitionCRFBean eventDefinitionCrf = daoWrapper.getEdcdao().findByStudyEventIdAndCRFVersionId(
					studyBean, studyEventBean.getId(), eventCRFBean.getCRFVersionId());
			State eventCRFState = getState(eventCRFBean);
			if (eventCRFBean.getStatus() == Status.UNAVAILABLE
					&& eventCRFBean.getStage() == DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE) {
				if (eventDefinitionCrf.isRequiredCRF()) {
					requiredCrfIds.remove((Integer) eventDefinitionCrf.getId());
				}
				if (hasSDVRequiredCRFs
						&& !eventCRFBean.isSdvStatus()
						&& (eventDefinitionCrf.getSourceDataVerification() == SourceDataVerification.AllREQUIRED || eventDefinitionCrf
								.getSourceDataVerification() == SourceDataVerification.PARTIALREQUIRED)) {
					notAllStartedSDVRequiredCRFsAreSDVed = true;
				}
                if (!hasSDVedCRFs && eventCRFBean.isSdvStatus()) {
                    hasSDVedCRFs  = true;
                }
			} else {
				hasStarted = true;
			}
			state = getHighestState(state, eventCRFState);
		}

		if (!justScheduled && !hasStarted) {
			if (hasRequiredCRFs) {
				state = requiredCrfIds.size() > 0 ? State.DES : State.DEC;
			}
			if (hasSDVedCRFs && hasSDVRequiredCRFs && !notAllStartedSDVRequiredCRFsAreSDVed && state == State.DEC) {
				state = State.SDV;
			}
		}
		setSubjectEventState(studyEventBean, studyBean, daoWrapper, state);
	}
	
	public static void fillDoubleDataOwner(ArrayList<EventCRFBean> eventCRFs, SessionManager sm) {
		// validatorId field was used for another purpose in the past, but now it is used for DoubleDataEntry 
		UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
		for (EventCRFBean eventCRF: eventCRFs){
			if (eventCRF.getValidatorId() > 0) {
				eventCRF.setDoubleDataOwner((UserAccountBean) udao.findByPK(eventCRF.getValidatorId()));
			}
		}
	}
}
