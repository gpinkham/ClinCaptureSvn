/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2015 Clinovo Inc.
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

package com.clinovo.util;

import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Util to manage Event Definition CRFs.
 */
public final class EventDefinitionCRFUtil {

	public static final String ADDED_EVENT_DEFINITION_CRFS_LABEL = "addedEventDefinitionCRFs";
	public static final String EVENT_DEFINITION_CRFS_LABEL = "eventDefinitionCRFs";

	private EventDefinitionCRFUtil() {
	}

	/**
	 * Create list of clones of this entity.
	 *
	 * @param originalList array of originals
	 * @return ArrayList<EventDefinitionCRFBean>
	 */
	public static ArrayList<EventDefinitionCRFBean> cloneList(List<EventDefinitionCRFBean> originalList) {
		ArrayList<EventDefinitionCRFBean> clonedList = new ArrayList<EventDefinitionCRFBean>();

		for (EventDefinitionCRFBean edc : originalList) {
			EventDefinitionCRFBean clone = new EventDefinitionCRFBean(edc);
			clonedList.add(clone);
		}
		return clonedList;
	}

	/**
	 * Get map of SDV codes for EDCs.
	 *
	 * @param eventDefinitionCRFs List<EventDefinitionCRFBean> eventDefinitionCRFs
	 * @return HashMap of type Integer, Integer
	 */
	public static HashMap<Integer, Integer> getEDCSDVMap(List<EventDefinitionCRFBean> eventDefinitionCRFs) {
		HashMap<Integer, Integer> resultMap = new HashMap<Integer, Integer>();
		for (EventDefinitionCRFBean eventDefinitionCRFBean : eventDefinitionCRFs) {
			resultMap.put(eventDefinitionCRFBean.getId(), eventDefinitionCRFBean.getSourceDataVerification().getCode());
		}
		return resultMap;
	}

	/**
	 * Remove list of added event definition CRFs from session.
	 *
	 * @param session HttpSession
	 */
	public static void resetAddedEvents(HttpSession session) {
		session.setAttribute(ADDED_EVENT_DEFINITION_CRFS_LABEL, new ArrayList<EventDefinitionCRFBean>());
	}

	/**
	 * Merge list of added event definition CRFs from session with list of existing ones.
	 * @param session HttpSession
	 * @param crfs List of EventDefinitionCRFBeans
	 * @return List of EventDefinitionCRFBeans
	 */
	public static List<EventDefinitionCRFBean> mergeEventDefinitions(HttpSession session,
																 List<EventDefinitionCRFBean> crfs) {
		if (getAddedEventDefinitionCRFs(session).size() > 0) {
			List<EventDefinitionCRFBean> addedCrfs = getAddedEventDefinitionCRFs(session);
			for (EventDefinitionCRFBean crf : addedCrfs) {
				if (!existsInList(crfs, crf)) {
					crfs.add(crf);
				}
			}
			session.setAttribute(EVENT_DEFINITION_CRFS_LABEL, crfs);
		}
		return crfs;
	}

	/**
	 * Remove Event Definition CRF from list of added ones if it's present there.
	 * @param session HttpSession
	 * @param crf EventDefinitionCRFBean
	 * @return ArrayList ofEventDefinitionCRFBeans
	 */
	public static ArrayList<EventDefinitionCRFBean> removeEventDefinitionCRFFromListOfAdded(HttpSession session,
																		   EventDefinitionCRFBean crf) {
		ArrayList<EventDefinitionCRFBean> crfs = getAddedEventDefinitionCRFs(session);
		if (existsInList(crfs, crf)) {
			crfs.remove(crf);
		}
		return crfs;
	}

	/**
	 * Get list of added Event Definition CRFs from session.
	 * @param session HttpSession
	 * @return ArrayList of EventDefinitionCRFBeans.
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<EventDefinitionCRFBean> getAddedEventDefinitionCRFs(HttpSession session) {
		ArrayList<EventDefinitionCRFBean> eventCRFs = (ArrayList<EventDefinitionCRFBean>) session
				.getAttribute(ADDED_EVENT_DEFINITION_CRFS_LABEL);
		if (eventCRFs == null) {
			eventCRFs = new ArrayList<EventDefinitionCRFBean>();
		}
		return eventCRFs;
	}

	/**
	 * Get list of existing event definition CRFs from session.
	 * @param session HttpSession
	 * @return ArrayList of EventDefinitionCRFBean
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<EventDefinitionCRFBean> getExistingEventDefinitionCRFs(HttpSession session) {
		ArrayList<EventDefinitionCRFBean> eventCRFs = (ArrayList<EventDefinitionCRFBean>) session
				.getAttribute(EVENT_DEFINITION_CRFS_LABEL);
		if (eventCRFs == null) {
			eventCRFs = new ArrayList<EventDefinitionCRFBean>();
		}
		return eventCRFs;
	}

	private static boolean existsInList(List<EventDefinitionCRFBean> crfs, EventDefinitionCRFBean crf) {
		for (EventDefinitionCRFBean existingCRF : crfs) {
			if (existingCRF.getCrfName().equals(crf.getCrfName())) {
				return true;
			}
		}
		return false;
	}
}
