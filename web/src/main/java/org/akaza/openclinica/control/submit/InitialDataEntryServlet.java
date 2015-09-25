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
package org.akaza.openclinica.control.submit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.clinovo.enums.CurrentDataEntryStage;
import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author ssachs
 */
@Component
@RequestMapping(value = "/InitialDataEntry")
@SuppressWarnings({"serial"})
public class InitialDataEntryServlet extends DataEntryServlet {

	private final Logger logger = LoggerFactory.getLogger(getClass().getName());

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		mayAccess(request);
		checkStudyLocked(Page.LIST_STUDY_SUBJECTS, respage.getString("current_study_locked"), request, response);
		checkStudyFrozen(Page.LIST_STUDY_SUBJECTS, respage.getString("current_study_frozen"), request, response);
		HttpSession session = request.getSession();
		session.setAttribute("mayProcessUploading", "true");

		getInputBeans(request);
	}

	@Override
	protected boolean validateInputOnFirstRound() {
		return true;
	}

	@Override
	protected Status getBlankItemStatus() {
		return Status.AVAILABLE;
	}

	@Override
	protected Status getNonBlankItemStatus(HttpServletRequest request) {
		EventDefinitionCRFBean edcb = (EventDefinitionCRFBean) request.getAttribute(EVENT_DEF_CRF_BEAN);
		return edcb.isDoubleEntry() ? Status.PENDING : Status.UNAVAILABLE;
	}

	@Override
	protected String getEventCRFAnnotations(HttpServletRequest request) {
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);
		return ecb.getAnnotations();
	}

	@Override
	protected void setEventCRFAnnotations(String annotations, HttpServletRequest request) {
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);
		ecb.setAnnotations(annotations);
	}

	@Override
	protected Page getJSPPage() {
		return Page.DATA_ENTRY;
	}

	@Override
	protected Page getServletPage(HttpServletRequest request) {
		FormProcessor fp = new FormProcessor(request);
		String tabId = fp.getString("tab", true);
		String sectionId = fp.getString(DataEntryServlet.INPUT_SECTION_ID, true);
		String eventCRFId = fp.getString(INPUT_EVENT_CRF_ID, true);
		request.setAttribute("system_lang", CoreResources.getSystemLocale().toString());
		if (StringUtil.isBlank(sectionId) || StringUtil.isBlank(tabId)) {
			return Page.INITIAL_DATA_ENTRY_SERVLET;
		} else {
			Page target = Page.INITIAL_DATA_ENTRY_SERVLET;
			target.setFileName(target.getFileName() + "?eventCRFId=" + eventCRFId + "&sectionId=" + sectionId + "&tab="
					+ tabId);
			return target;
		}
	}

	@Override
	protected boolean shouldRunRules() {
		return true;
	}

	@Override
	protected boolean isAdministrativeEditing() {
		return false;
	}

	@Override
	protected boolean isAdminForcedReasonForChange(HttpServletRequest request) {
		return false;
	}

	@Override
	protected CurrentDataEntryStage getCurrentDataEntryStage() {
		return CurrentDataEntryStage.INITIAL_DATA_ENTRY;
	}

	@Override
	protected void putDataEntryStageFlagToRequest(HttpServletRequest request) {
		request.setAttribute(DATA_ENTRY_STAGE, DataEntryStage.INITIAL_DATA_ENTRY);
	}
}
