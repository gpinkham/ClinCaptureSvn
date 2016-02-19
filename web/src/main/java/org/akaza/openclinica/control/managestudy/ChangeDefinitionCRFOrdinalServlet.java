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
package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.view.Page;
import org.springframework.stereotype.Component;

/**
 * Processes request to change CRF ordinals in a study event definition.
 */
@SuppressWarnings("unchecked")
@Component
public class ChangeDefinitionCRFOrdinalServlet extends ChangeOrdinalServlet {

	private static final String PARAM_EVENT_DEF_CRF_ID = "eventCRFDefId";
	private static final String PARAM_ACTION = "action";
	private static final String ACTION_MOVE_UP = "moveUp";
	private static final String ACTION_MOVE_DOWN = "moveDown";
	private static final String EVENT_DEF_CRF_BEAN_TO_MOVE = "eventDefCRFToMove";

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		FormProcessor fp = new FormProcessor(request);
		int eventCRFDefId = fp.getInt(PARAM_EVENT_DEF_CRF_ID);
		String action = fp.getString(PARAM_ACTION);

		EventDefinitionCRFBean eventDefCRFToMove = (EventDefinitionCRFBean) getEventDefinitionCRFDAO()
				.findByPK(eventCRFDefId);
		request.getSession().getServletContext().setAttribute(EVENT_DEF_CRF_BEAN_TO_MOVE, eventDefCRFToMove);

		if (!isEventDefCRFValid(request)
				|| (!action.equalsIgnoreCase(ACTION_MOVE_UP) && !action.equalsIgnoreCase(ACTION_MOVE_DOWN))) {

			addPageMessage(getResPage().getString("invalid_http_request_parameters"), request);
			forwardPage(Page.LIST_DEFINITION_SERVLET, request, response);

		} else {

			changeOrdinalInsideStudyEventDefinition(request);

			request.setAttribute("id", String.valueOf(eventDefCRFToMove.getStudyEventDefinitionId()));
			forwardPage(Page.VIEW_EVENT_DEFINITION_SERVLET, request, response);
		}
	}

	private boolean isEventDefCRFValid(HttpServletRequest request) {

		EventDefinitionCRFBean eventDefCRFToMove = (EventDefinitionCRFBean) request.getSession().getServletContext()
				.getAttribute(EVENT_DEF_CRF_BEAN_TO_MOVE);
		StudyBean currentStudy = getCurrentStudy(request);

		return (eventDefCRFToMove.getId() > 0) && (eventDefCRFToMove.getStudyId() == currentStudy.getId());
	}

	private void changeOrdinalInsideStudyEventDefinition(HttpServletRequest request) {

		FormProcessor fp = new FormProcessor(request);
		String action = fp.getString(PARAM_ACTION);
		StudyBean currentStudy = getCurrentStudy(request);
		UserAccountBean currentUser = getUserAccountBean(request);

		EventDefinitionCRFBean eventDefCRFToMove = (EventDefinitionCRFBean) request.getSession().getServletContext()
				.getAttribute(EVENT_DEF_CRF_BEAN_TO_MOVE);

		List<EventDefinitionCRFBean> eventDefCRFBeansList = (List<EventDefinitionCRFBean>) getEventDefinitionCRFDAO()
				.findAllByDefinition(currentStudy, eventDefCRFToMove.getStudyEventDefinitionId());

		List<Integer> edcIds = new ArrayList<Integer>();
		for (EventDefinitionCRFBean edcBean : eventDefCRFBeansList) {
			edcIds.add(edcBean.getId());
		}
		int posFrom = edcIds.indexOf(eventDefCRFToMove.getId());
		int posTo = action.equalsIgnoreCase(ACTION_MOVE_UP) ? posFrom - 1 : posFrom + 1;
		if (posTo >= 0 && posTo <= edcIds.size() - 1) {
			EventDefinitionCRFBean edcBeanFrom = eventDefCRFBeansList.get(posFrom);
			EventDefinitionCRFBean edcBeanTo = eventDefCRFBeansList.get(posTo);
			eventDefCRFBeansList.set(posFrom, edcBeanTo);
			eventDefCRFBeansList.set(posTo, edcBeanFrom);
			edcIds.clear();

			int ordinal = 1;
			for (EventDefinitionCRFBean eventDefCRFBean : eventDefCRFBeansList) {
				edcIds.add(eventDefCRFBean.getId());
				eventDefCRFBean.setOrdinal(ordinal++);
				eventDefCRFBean.setUpdater(currentUser);
				getEventDefinitionCRFDAO().update(eventDefCRFBean);
			}

			List<EventDefinitionCRFBean> siteLevelEventDefinitionCRFsList = getEventDefinitionCRFDAO()
					.findAllChildrenByDefinition(eventDefCRFToMove.getStudyEventDefinitionId());
			for (EventDefinitionCRFBean siteEventDefCRFBean : siteLevelEventDefinitionCRFsList) {
				siteEventDefCRFBean.setOrdinal(edcIds.indexOf(siteEventDefCRFBean.getParentId()) + 1);
				siteEventDefCRFBean.setUpdater(currentUser);
				getEventDefinitionCRFDAO().update(siteEventDefCRFBean);
			}
		}
	}
}
