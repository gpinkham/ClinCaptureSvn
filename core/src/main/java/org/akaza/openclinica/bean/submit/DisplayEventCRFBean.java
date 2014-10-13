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

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.submit;

import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;

/**
 * A class which manages all the information needed to view an event CRF or an uncompleted event CRF. Note that
 * uncompleted event CRFs are distinguished from other event CRFs in that uncompleted event CRFs do not exist in the
 * database.
 * 
 * @author ssachs
 */

@SuppressWarnings("rawtypes")
public class DisplayEventCRFBean implements Comparable {

	protected static final Logger LOGGER = LoggerFactory.getLogger(DisplayEventCRFBean.class.getName());

	public static final int TWELVE = 12;

	private DataEntryStage stage;
	private EventCRFBean eventCRF;
	private EventDefinitionCRFBean eventDefinitionCRF;

	private boolean locked = false;
	private boolean startDoubleDataEntryPermitted = false;
	private boolean startInitialDataEntryPermitted = false;
	private boolean continueDoubleDataEntryPermitted = false;
	private boolean continueInitialDataEntryPermitted = false;
	private boolean performAdministrativeEditingPermitted = false;

	/**
	 * Does the user have to wait twelve hours before starting double data entry?
	 */
	private boolean twelveHourWaitRequired = false;

	/**
	 * DisplayEventCRFBean constructor.
	 */
	public DisplayEventCRFBean() {
		eventDefinitionCRF = new EventDefinitionCRFBean();
		eventCRF = new EventCRFBean();
	}

	/**
	 * @return Returns the eventDefinitionCRF.
	 */
	public EventDefinitionCRFBean getEventDefinitionCRF() {
		return eventDefinitionCRF;
	}

	/**
	 * @param eventDefinitionCRF
	 *            The eventDefinitionCRF to set.
	 */
	public void setEventDefinitionCRF(EventDefinitionCRFBean eventDefinitionCRF) {
		this.eventDefinitionCRF = eventDefinitionCRF;
	}

	/**
	 * @return Returns the eventCRF.
	 */
	public EventCRFBean getEventCRF() {
		return eventCRF;
	}

	public void setEventCRF(EventCRFBean eventCRF) {
		this.eventCRF = eventCRF;
	}

	/**
	 * @return Returns the stage.
	 */
	public DataEntryStage getStage() {
		return stage;
	}

	/**
	 * @return Returns the continueDoubleDataEntryPermitted.
	 */
	public boolean isContinueDoubleDataEntryPermitted() {
		return continueDoubleDataEntryPermitted;
	}

	/**
	 * @return Returns the continueInitialDataEntryPermitted.
	 */
	public boolean isContinueInitialDataEntryPermitted() {
		return continueInitialDataEntryPermitted;
	}

	/**
	 * @return Returns the performAdministrativeEditingPermitted.
	 */
	public boolean isPerformAdministrativeEditingPermitted() {
		return performAdministrativeEditingPermitted;
	}

	/**
	 * @return Returns the startDoubleDataEntryPermitted.
	 */
	public boolean isStartDoubleDataEntryPermitted() {
		return startDoubleDataEntryPermitted;
	}

	/**
	 * @return Returns the startInitialDataEntryPermitted.
	 */
	public boolean isStartInitialDataEntryPermitted() {
		return startInitialDataEntryPermitted;
	}

	/**
	 * @return The locked flag.
	 */
	public boolean isLocked() {
		return locked;
	}

	/**
	 * @return Returns the twelveHourWaitRequired.
	 */
	public boolean isTwelveHourWaitRequired() {
		return twelveHourWaitRequired;
	}

	/**
	 * Method that sets flags.
	 * 
	 * @param eventCRF
	 *            EventCRFBean
	 * @param user
	 *            UserAccountBean
	 * @param surb
	 *            StudyUserRoleBean
	 * @param eventDefinitionCRF
	 *            EventDefinitionCRFBean
	 */
	public void setFlags(EventCRFBean eventCRF, UserAccountBean user, StudyUserRoleBean surb,
			EventDefinitionCRFBean eventDefinitionCRF) {
		this.eventCRF = eventCRF;
		this.eventDefinitionCRF = eventDefinitionCRF;
		boolean crfEvaluationOrDDEPermitted = eventDefinitionCRF.isDoubleEntry() || eventDefinitionCRF.isEvaluatedCRF();
		stage = eventCRF.getStage();
		Role r = surb.getRole();
		boolean isEditor = isEditor(user, r);

		if (stage.equals(DataEntryStage.LOCKED)) {
			locked = true;
			return;
		}

		if (stage.equals(DataEntryStage.UNCOMPLETED) || eventCRF.isNotStarted()) {
			startInitialDataEntryPermitted = true;
		} else if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY)) {
			continueInitialDataEntryPermitted = true;
		} else if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE)) {
			if (crfEvaluationOrDDEPermitted) {
				if (eventCRF.getOwner().equals(user)) {
					if (initialDataEntryCompletedMoreThanTwelveHoursAgo(eventCRF)) {
						startDoubleDataEntryPermitted = true;
					} else {
						startDoubleDataEntryPermitted = false;
						twelveHourWaitRequired = true;
					}
				} else {
					startDoubleDataEntryPermitted = true;
				}
			} else {
				if (isEditor) {
					performAdministrativeEditingPermitted = true;
				}
			}
		} else if (stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY) && crfEvaluationOrDDEPermitted) {
			if (eventCRF.getValidatorId() == user.getId()) {
				continueDoubleDataEntryPermitted = true;
			}
		} else if (stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE)) {
			if (isEditor) {
				performAdministrativeEditingPermitted = true;
			}
		}
		if (eventDefinitionCRF.isEvaluatedCRF()
				&& !eventDefinitionCRF.isDoubleEntry()
				&& (startDoubleDataEntryPermitted || continueDoubleDataEntryPermitted)
				&& (surb.getRole().equals(Role.CLINICAL_RESEARCH_COORDINATOR) || surb.getRole().equals(
						Role.INVESTIGATOR))) {
			startDoubleDataEntryPermitted = false;
			continueDoubleDataEntryPermitted = false;
		}
	}

	/**
	 * @param user
	 *            The current user.
	 * @param studyRole
	 *            The current user's role in the current study.
	 * @return <code>true</code> if the user is a System Administrator or Study Director for the current study;
	 *         <code>false</code> otherwise.
	 */
	public static boolean isSuper(UserAccountBean user, Role studyRole) {
		return studyRole.equals(Role.SYSTEM_ADMINISTRATOR) || studyRole.equals(Role.STUDY_DIRECTOR)
				|| studyRole.equals(Role.STUDY_ADMINISTRATOR);
	}

	/**
	 * Method checks that user with role is able to do data entry.
	 * 
	 * @param user
	 *            UserAccountBean
	 * @param studyRole
	 *            Role
	 * @return boolean
	 */
	public static boolean isEditor(UserAccountBean user, Role studyRole) {
		return isSuper(user, studyRole) || studyRole.equals(Role.INVESTIGATOR)
				|| studyRole.equals(Role.CLINICAL_RESEARCH_COORDINATOR);
	}

	/**
	 * Method checks that data entry was completed more than 12 hours ago.
	 *
	 * @param eventCRF
	 *            EventCRFBean
	 * @return boolean
	 */
	public static boolean initialDataEntryCompletedMoreThanTwelveHoursAgo(EventCRFBean eventCRF) {
		Date ideCompleted = eventCRF.getDateCompleted();
		LOGGER.info("id: " + eventCRF.getId() + "; idec: " + ideCompleted);
		Date now = new Date();
		LOGGER.info("now: " + now);
		Calendar c = Calendar.getInstance();
		c.setTime(ideCompleted);
		c.add(Calendar.HOUR, TWELVE);

		Date twelveHoursAfterIDECompleted = c.getTime();
		LOGGER.info("aft12: " + twelveHoursAfterIDECompleted);
		LOGGER.info("returning: " + (now.getTime() > twelveHoursAfterIDECompleted.getTime()));
		return now.getTime() > twelveHoursAfterIDECompleted.getTime();

		// long twelveHoursInMilliSeconds = 12 * 60 * 60 * 1000;
		// Date nowMinusTwelve =
		// new Date(System.currentTimeMillis() - twelveHoursInMilliSeconds);
		//
		// Date created = eventCRF.getCreatedDate();
		//
		// /*
		// * if the current time is 8 pm, and created is:
		// * - 7 am: nowMinusTwelve is 8 am, and created is not after
		// nowMinusTwelve, so we return false
		// * (which is correct since initial data entry was completed at 7 am,
		// *more* than 12 hours ago)
		// * - 9 am: nowMinusTwelve is 8 am, and created is after
		// nowMinusTwelve, so we return true
		// * (which is correct since initial data entry was completed at 9 am,
		// *less* than 12 hours ago)
		// */
		//
		// return created.after(nowMinusTwelve);
	}

	/**
	 * Method to compare DisplayEventCRFBean objects.
	 * 
	 * @param o
	 *            Object
	 * @return int
	 */
	public int compareTo(Object o) {
		if (o == null || !o.getClass().equals(this.getClass())) {
			return 0;
		}

		DisplayEventCRFBean decb = (DisplayEventCRFBean) o;
		return this.eventDefinitionCRF.getOrdinal() - decb.getEventDefinitionCRF().getOrdinal();
	}
}
