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

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.clinovo.enums.CurrentDataEntryStage;
import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

/**
 * Double Data Entry Servlet.
 */
@SuppressWarnings({"rawtypes", "serial"})
@Component
public class DoubleDataEntryServlet extends DataEntryServlet {

	public static final String COUNT_VALIDATE = "countValidate";
	public static final String DDE_ENTERED = "ddeEntered";
	public static final String DDE_PROGESS = "doubleDataProgress";

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		checkStudyLocked(Page.LIST_STUDY_SUBJECTS, getResPage().getString("current_study_locked"), request, response);
		checkStudyFrozen(Page.LIST_STUDY_SUBJECTS, getResPage().getString("current_study_frozen"), request, response);
		HttpSession session = request.getSession();

		getInputBeans(request);
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);
		FormProcessor fp = new FormProcessor(request);
		SectionBean sb = (SectionBean) request.getAttribute(SECTION_BEAN);

		// The following COUNT_VALIDATE session attribute is not
		// accessible,
		// for unknown reasons (threading problems?), when
		// double-data entry displays error messages; it's value is always 0; so
		// I have to create my
		// own session variable here to keep track of DDE stages

		// We'll go by the SectionBean's ordinal first
		int tabNumber = 1;
		if (sb != null) {
			tabNumber = sb.getOrdinal();
		}
		// if tabNumber still isn't valid, check the "tab" parameter
		if (tabNumber < 1) {
			String tab = fp.getString("tab");
			if (tab == null || tab.length() < 1) {
				tabNumber = 1;
			} else {
				tabNumber = fp.getInt("tab");
			}
		}
		SectionDAO sectionDao = getSectionDAO();
		int crfVersionId = ecb.getCRFVersionId();
		int eventCRFId = ecb.getId();
		ArrayList sections = sectionDao.findAllByCRFVersionId(crfVersionId);
		int sectionSize = sections.size();

		HttpSession mySession = request.getSession();
		DoubleDataProgress doubleDataProgress = (DoubleDataProgress) mySession.getAttribute(DDE_PROGESS);
		if (doubleDataProgress == null || doubleDataProgress.getEventCRFId() != eventCRFId) {
			doubleDataProgress = new DoubleDataProgress(sectionSize, eventCRFId);
			mySession.setAttribute(DDE_PROGESS, doubleDataProgress);
		}
		boolean hasVisitedSection = doubleDataProgress.getSectionVisited(tabNumber, eventCRFId);

		// setting up one-time validation here
		// admit that it's an odd place to put it, but where else?
		// placing it in dataentryservlet is creating too many counts
		int keyId = ecb.getId();
		Integer count = (Integer) session.getAttribute(COUNT_VALIDATE + keyId);
		if (count != null) {
			count++;
			session.setAttribute(COUNT_VALIDATE + keyId, count);
			logger.info("^^^just set count to session: " + count);
		} else {
			count = 0;
			session.setAttribute(COUNT_VALIDATE + keyId, count);
			logger.info("***count not found, set to session: " + count);
		}

		DataEntryStage stage = ecb.getStage();
		if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE) && !hasVisitedSection) {
			// if the user has not entered this section yet in Double Data
			// Entry, then
			// set a flag that default values should be shown in the form
			request.setAttribute(DDE_ENTERED, true);

		}
		// Now update the session attribute
		doubleDataProgress.setSectionVisited(eventCRFId, tabNumber, true);
		mySession.setAttribute("doubleDataProgress", doubleDataProgress);
		session.setAttribute("mayProcessUploading", "true");
	}

	@Override
	protected void putDataEntryStageFlagToRequest(HttpServletRequest request) {
		request.setAttribute(DATA_ENTRY_STAGE, DataEntryStage.DOUBLE_DATA_ENTRY);
	}

	@Override
	protected boolean validateInputOnFirstRound() {
		return true;
	}

	@Override
	protected Status getBlankItemStatus() {
		return Status.PENDING;
	}

	@Override
	protected Status getNonBlankItemStatus(HttpServletRequest request) {
		return Status.UNAVAILABLE;
	}

	@Override
	protected String getEventCRFAnnotations(HttpServletRequest request) {
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);

		return ecb.getValidatorAnnotations();
	}

	@Override
	protected void setEventCRFAnnotations(String annotations, HttpServletRequest request) {
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);

		ecb.setValidatorAnnotations(annotations);
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
		String hideSaveAndNextButton = fp.getString("hideSaveAndNextButton", true);
		if (hideSaveAndNextButton.equals("true")) {
			request.setAttribute("hideSaveAndNextButton", "true");
		}
		request.setAttribute("system_lang", CoreResources.getSystemLocale().toString());
		if (StringUtil.isBlank(sectionId) || StringUtil.isBlank(tabId)) {
			return Page.DOUBLE_DATA_ENTRY_SERVLET;
		} else {
			Page target = Page.DOUBLE_DATA_ENTRY_SERVLET;
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
		return CurrentDataEntryStage.DOUBLE_DATA_ENTRY;
	}
}
