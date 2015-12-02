/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2014 Clinovo Inc.
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

package org.akaza.openclinica.util;

import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Util to manage Event Definition CRFs.
 */
public final class EventDefinitionCRFUtil {
	private EventDefinitionCRFUtil() {
	}

	/**
	 * Method that will update default version of CRF if current is assigned to event.
	 * 
	 * @param ds
	 *            DataSource
	 * @param deletedCRFVersionId
	 *            int
	 */
	@SuppressWarnings("unchecked")
	public static void setDefaultCRFVersionInsteadOfDeleted(DataSource ds, int deletedCRFVersionId) {

		EventDefinitionCRFDAO eventCRFDAO = new EventDefinitionCRFDAO(ds);
		CRFVersionDAO crfVersionDAO = new CRFVersionDAO(ds);
		ArrayList<EventDefinitionCRFBean> crfs = eventCRFDAO.findByDefaultVersion(deletedCRFVersionId);

		for (EventDefinitionCRFBean crf : crfs) {
			CRFVersionBean latestVersion = crfVersionDAO.findLatestAfterDeleted(deletedCRFVersionId);
			if (latestVersion != null) {
				crf.setDefaultVersionId(latestVersion.getId());
				crf.setDefaultVersionName(latestVersion.getName());
				eventCRFDAO.update(crf);
			}
		}
	}

	/**
	 * Create list of clones of this entity.
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
	 * @param eventDefinitionCRFs List<EventDefinitionCRFBean> eventDefinitionCRFs
	 * @return HashMap<Integer, Integer>
	 */
	public static HashMap<Integer, Integer> getEDCSDVMap(List<EventDefinitionCRFBean> eventDefinitionCRFs) {
		HashMap<Integer, Integer> resultMap = new HashMap<Integer, Integer>();
		for (EventDefinitionCRFBean eventDefinitionCRFBean : eventDefinitionCRFs) {
			resultMap.put(eventDefinitionCRFBean.getId(), eventDefinitionCRFBean.getSourceDataVerification().getCode());
		}
		return resultMap;
	}
}
