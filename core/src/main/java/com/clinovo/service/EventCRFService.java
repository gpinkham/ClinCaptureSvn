/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 *
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 *
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use.
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVOâ€™S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/

package com.clinovo.service;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;

import java.util.List;

public interface EventCRFService {

	/**
	 * Updates status of the given EventCRFBean to REMOVED.
	 *
	 * @param eventCRF EventCRFBean to be removed.
	 * @param updater  user, that initiated action.
	 */
	public void removeEventCRF(EventCRFBean eventCRF, UserAccountBean updater) throws Exception;

	/**
	 * Updates status of the EventCRFBeans, which are contained inside given StudyEventBean, to AUTO_REMOVED.
	 *
	 * @param studyEvent StudyEventBean, whose EventCRFBeans are intended to be removed.
	 * @param updater    user, that initiated action.
	 */
	public void removeEventCRFsByStudyEvent(StudyEventBean studyEvent, UserAccountBean updater) throws Exception;

	/**
	 * Updates status of the EventCRFBeans from given list to AUTO_REMOVED.
	 *
	 * @param eventCRFs list of EventCRFBeans, which are intended to be removed.
	 * @param updater   user, that initiated action.
	 */
	public void setEventCRFsToAutoRemovedState(List<EventCRFBean> eventCRFs, UserAccountBean updater) throws Exception;

	/**
	 * Restores given EventCRFBean with previous status.
	 *
	 * @param eventCRF EventCRFBean to be restored.
	 * @param updater  user, that initiated action.
	 */
	public void restoreEventCRF(EventCRFBean eventCRF, UserAccountBean updater) throws Exception;

	/**
	 * Restores EventCRFBeans, which are contained inside given StudyEventBean, with previous statuses.
	 *
	 * @param studyEvent StudyEventBean, whose EventCRFBeans are intended to be restored.
	 * @param updater    user, that initiated action.
	 */
	public void restoreEventCRFsByStudyEvent(StudyEventBean studyEvent, UserAccountBean updater) throws Exception;

	/**
	 * Restores EventCRFBeans with current status AUTO_REMOVED to their previous statuses.
	 *
	 * @param eventCRFs list of EventCRFBeans, which are intended to be restored.
	 * @param updater   user, that initiated action.
	 */
	public void restoreEventCRFsFromAutoRemovedState(List<EventCRFBean> eventCRFs, UserAccountBean updater)
			throws Exception;
}
