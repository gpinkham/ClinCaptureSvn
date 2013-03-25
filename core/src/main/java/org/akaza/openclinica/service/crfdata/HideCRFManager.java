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

package org.akaza.openclinica.service.crfdata;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.managestudy.DisplayEventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.DisplayStudyEventBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.DisplayEventCRFBean;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

/**
 * This is a convenience service class to help implement the requirement that a user logged in at the site level cannot
 * view/use CRFs that are marked as "hidden." User: bruceperry Date: Feb 9, 2009
 */
public class HideCRFManager {
	public static org.akaza.openclinica.service.crfdata.HideCRFManager createHideCRFManager() {
		return new org.akaza.openclinica.service.crfdata.HideCRFManager();
	}

	private HideCRFManager() {
	}

	public void removeHiddenEventCRF(DisplayStudyEventBean displayStudyEventBean) {

		List<DisplayEventCRFBean> eventCRFBeans = displayStudyEventBean.getAllEventCRFs();

		displayStudyEventBean.setAllEventCRFs(suppressHiddenEventCRFBeans(eventCRFBeans));

		eventCRFBeans = displayStudyEventBean.getDisplayEventCRFs();

		displayStudyEventBean.setDisplayEventCRFs(suppressHiddenEventCRFBeans(eventCRFBeans));

		List<DisplayEventDefinitionCRFBean> displayEventDefinitionCRFBeans = displayStudyEventBean.getUncompletedCRFs();

		ArrayList<DisplayEventDefinitionCRFBean> newDisplayEventDefinitionCRFBeans = new ArrayList<DisplayEventDefinitionCRFBean>();

		for (DisplayEventDefinitionCRFBean displayEventDefinitionCRFBean : displayEventDefinitionCRFBeans) {

			if (!displayEventDefinitionCRFBean.getEdc().isHideCrf()) {
				newDisplayEventDefinitionCRFBeans.add(displayEventDefinitionCRFBean);
			}

		}
		displayStudyEventBean.setUncompletedCRFs(newDisplayEventDefinitionCRFBeans);

	}

	private ArrayList<DisplayEventCRFBean> suppressHiddenEventCRFBeans(List<DisplayEventCRFBean> eventCRFBeans) {

		// Must make this an ArrayList because of the
		// displayStudyEventBean.setAllEventCRFs()
		// method definition
		ArrayList<DisplayEventCRFBean> newEventCRFBeans = new ArrayList<DisplayEventCRFBean>();

		EventDefinitionCRFBean definitionCRFBean = new EventDefinitionCRFBean();

		for (DisplayEventCRFBean deCRFBean : eventCRFBeans) {

			definitionCRFBean = deCRFBean.getEventDefinitionCRF();
			if (!definitionCRFBean.isHideCrf()) {
				newEventCRFBeans.add(deCRFBean);
			}
		}
		return newEventCRFBeans;
	}

	public ArrayList<DisplayEventCRFBean> removeHiddenEventCRFBeans(List<DisplayEventCRFBean> displayEventCRFBeans) {

		ArrayList<DisplayEventCRFBean> newDisplayEventCRFBeans = new ArrayList<DisplayEventCRFBean>();

		for (DisplayEventCRFBean displayEventCRFBean : displayEventCRFBeans) {
			if (!displayEventCRFBean.getEventDefinitionCRF().isHideCrf()) {
				newDisplayEventCRFBeans.add(displayEventCRFBean);
			}
		}

		return newDisplayEventCRFBeans;

	}

	public ArrayList<DisplayEventDefinitionCRFBean> removeHiddenEventDefinitionCRFBeans(
			List<DisplayEventDefinitionCRFBean> displayEventDefinitionCRFBeans) {

		ArrayList<DisplayEventDefinitionCRFBean> newDisplayEventDefinitionCRFBeans = new ArrayList<DisplayEventDefinitionCRFBean>();

		EventDefinitionCRFBean eventDefinitionCRFBean = new EventDefinitionCRFBean();

		for (DisplayEventDefinitionCRFBean displayEventDefinitionCRFBean : displayEventDefinitionCRFBeans) {

			eventDefinitionCRFBean = displayEventDefinitionCRFBean.getEdc();
			if (!eventDefinitionCRFBean.isHideCrf()) {
				newDisplayEventDefinitionCRFBeans.add(displayEventDefinitionCRFBean);
			}
		}
		return newDisplayEventDefinitionCRFBeans;
	}

	public void optionallyCheckHideCRFProperty(DisplayStudyEventBean displayStudyEventBean) {

		EventDefinitionCRFBean tempEventCRFBean = new EventDefinitionCRFBean();

		for (DisplayEventCRFBean deCRFBean : displayStudyEventBean.getAllEventCRFs()) {
			tempEventCRFBean = deCRFBean.getEventDefinitionCRF();
			if (tempEventCRFBean.isHideCrf()) {
				tempEventCRFBean.setHidden(true);
			}
		}

		for (DisplayEventCRFBean deCRFBean : displayStudyEventBean.getDisplayEventCRFs()) {
			tempEventCRFBean = deCRFBean.getEventDefinitionCRF();
			if (tempEventCRFBean.isHideCrf()) {
				tempEventCRFBean.setHidden(true);
			}
		}

	}

	public boolean studyEventHasAHideCRFProperty(DisplayStudyEventBean displayStudyEventBean) {

		EventDefinitionCRFBean tempEventCRFBean = new EventDefinitionCRFBean();
		boolean hasAHideCRFProperty = false;

		for (DisplayEventCRFBean deCRFBean : displayStudyEventBean.getAllEventCRFs()) {
			tempEventCRFBean = deCRFBean.getEventDefinitionCRF();
			if (tempEventCRFBean.isHideCrf()) {
				hasAHideCRFProperty = true;
			}
		}

		for (DisplayEventCRFBean deCRFBean : displayStudyEventBean.getDisplayEventCRFs()) {
			tempEventCRFBean = deCRFBean.getEventDefinitionCRF();
			if (tempEventCRFBean.isHideCrf()) {
				hasAHideCRFProperty = true;
			}
		}
		return hasAHideCRFProperty;
	}

	public void hideSpecifiedEventCRFDefBeans(List<EventDefinitionCRFBean> eventDefinitionCRFBeans) {

		for (EventDefinitionCRFBean eventDefinitionCRFBean : eventDefinitionCRFBeans) {
			eventDefinitionCRFBean.setHidden(true);
		}
	}

	/**
	 * Given a StudyEventDefinitionBean and a list of CRFBeans as parameters, this method returns of new list of
	 * CRFBeans containing only unhidden CRFs (their isHideCRF property is false).
	 * 
	 * @param studyEventBean
	 *            The StudyEventDefinitionBean
	 * @param crfBeans
	 *            An ArrayList of CRFBeans
	 * @param dataSource
	 *            A javax.sql.DataSource for the use of the EventDefinitionCRFDAO.
	 * @return An ArrayList of CRFBeans containing only "unhidden" CRFs.
	 */
	public ArrayList<CRFBean> removeHiddenCRFBeans(StudyBean study, StudyEventDefinitionBean studyEventBean,
			ArrayList<CRFBean> crfBeans, DataSource dataSource) {

		ArrayList<CRFBean> newBeans = new ArrayList<CRFBean>();
		if (crfBeans == null || crfBeans.isEmpty()) {
			return newBeans;
		}
		EventDefinitionCRFDAO eventDefinitionCRFDAO = new EventDefinitionCRFDAO(dataSource);
		EventDefinitionCRFBean tempBean = new EventDefinitionCRFBean();
		for (CRFBean crfBean : crfBeans) {
			tempBean = eventDefinitionCRFDAO.findByStudyEventDefinitionIdAndCRFId(study, studyEventBean.getId(),
					crfBean.getId());
			if (tempBean != null && !tempBean.isHideCrf()) {
				newBeans.add(crfBean);
			}
		}

		return newBeans;
	}
}
