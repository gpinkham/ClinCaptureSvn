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
package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.view.Page;

/**
 * Processes request to change CRF ordinals in a study event definition
 * 
 * @author jxu
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class ChangeDefinitionCRFOrdinalServlet extends ChangeOrdinalServlet {

	/**
	 * Override processRequest in super class
	 */
	@Override
	public void processRequest() throws Exception {
		FormProcessor fp = new FormProcessor(request);
		int current = fp.getInt("current");
		int previous = fp.getInt("previous");
		int currOrdinal = fp.getInt("currentOrdinal");
		int prevOrdinal = fp.getInt("previousOrdinal");

		int definitionId = fp.getInt("id");
		EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
		increase(current, previous, currOrdinal, prevOrdinal, definitionId, edcdao);
		int siteId = fp.getInt("siteId");
		if (siteId > 0) {
			request.setAttribute("idToSort", new Integer(definitionId).toString());
			request.setAttribute("siteId", siteId);
			forwardPage(Page.VIEW_SITE_SERVLET);
		} else {
			request.setAttribute("id", new Integer(definitionId).toString());
			forwardPage(Page.VIEW_EVENT_DEFINITION_SERVLET);
		}
	}

	/**
	 * Increases the ordinal for current object and decrease the ordinal of the previous one
	 * 
	 * @param idCurrent
	 * @param idPrevious
	 * @param dao
	 */
	private void increase(int idCurrent, int idPrevious, int currOrdinal, int prevOrdinal, int defId,
			EventDefinitionCRFDAO dao) {
		EventDefinitionCRFBean current = (EventDefinitionCRFBean) dao.findByPK(idCurrent);
		EventDefinitionCRFBean previous = (EventDefinitionCRFBean) dao.findByPK(idPrevious);
        ArrayList allEventDefCRFBeans = dao.findAllActiveByEventDefinitionId(current.getStudyEventDefinitionId());

        int maxOrder = 1;
        for(Object tmpBean : allEventDefCRFBeans) {
          EventDefinitionCRFBean edCRFBean = (EventDefinitionCRFBean) tmpBean;
            if(edCRFBean.getOrdinal() > maxOrder) {
                maxOrder = edCRFBean.getOrdinal();
            }
        }

		if (current.getOrdinal() == currOrdinal && previous.getOrdinal() == prevOrdinal) {
            int currentOrdinal = current.getOrdinal();
            if (idCurrent > 0 && currentOrdinal != 1) {
				current.setOrdinal(currentOrdinal - 1);
				current.setUpdater((UserAccountBean) session.getAttribute("userBean"));
				dao.update(current);
			}
            int previousOrdinal = previous.getOrdinal();
			if (idPrevious > 0 && previousOrdinal != maxOrder) {
				previous.setOrdinal(previousOrdinal + 1);
				previous.setUpdater((UserAccountBean) session.getAttribute("userBean"));
				dao.update(previous);
			}

			ArrayList currOrdlist = dao.findAllByEventDefinitionIdAndOrdinal(defId, current.getOrdinal());
			ArrayList prevOrdlist = dao.findAllByEventDefinitionIdAndOrdinal(defId, previous.getOrdinal());
			if (currOrdlist.size() > 1 || prevOrdlist.size() > 1) {
				fixDuplicates(defId, dao);
			}
		}
	}

	/**
	 * Fixes ordinal values if there is any duplicates
	 * 
	 * @param definitionId
	 * @param dao
	 */
	private void fixDuplicates(int definitionId, EventDefinitionCRFDAO dao) {
		ArrayList list = dao.findAllByEventDefinitionId(definitionId);
		boolean incrementNextOrdinal = false;
		for (int i = 0; i < list.size(); i++) {
			EventDefinitionCRFBean edc = (EventDefinitionCRFBean) list.get(i);
			if (i == 0) {
				if (edc.getOrdinal() != 0) {
					edc.setOrdinal(i);
					dao.update(edc);
				}
				continue;
			}
			if (incrementNextOrdinal) {
				edc.setOrdinal(i);
				dao.update(edc);
				continue;
			}
			if (edc.getOrdinal() != i) {
				edc.setOrdinal(i);
				dao.update(edc);
				incrementNextOrdinal = true;
			}
		}
	}

}
