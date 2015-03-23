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
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.DisplayItemGroupBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.control.form.DiscrepancyValidator;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.RuleValidator;
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
	
	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());


	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		mayAccess(request);
		checkStudyLocked(Page.LIST_STUDY_SUBJECTS, respage.getString("current_study_locked"), request, response);
		checkStudyFrozen(Page.LIST_STUDY_SUBJECTS, respage.getString("current_study_frozen"), request, response);
		HttpSession session = request.getSession();

		session.setAttribute("mayProcessUploading", "true");

		getInputBeans(request);

		return;
	}

	@Override
	protected boolean validateInputOnFirstRound() {
		return true;
	}

	@Override
	protected DisplayItemBean validateDisplayItemBean(DiscrepancyValidator v, DisplayItemBean dib, String inputName,
			RuleValidator rv, HashMap<String, ArrayList<String>> groupOrdinalPLusItemOid, Boolean fireRuleValidation,
			ArrayList<String> messages, HttpServletRequest request) {

		org.akaza.openclinica.bean.core.ResponseType rt = dib.getMetadata().getResponseSet().getResponseType();

		// note that this step sets us up both for
		// displaying the data on the form again, in the event of an error
		// and sending the data to the database, in the event of no error
		if (StringUtil.isBlank(inputName)) {// not an item from group, doesn't
			// need to get data from form again
			dib = loadFormValue(dib, request);
		}

		// types TEL and ED are not supported yet
		if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.TEXT)
				|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.TEXTAREA)) {
			// dib = validateDisplayItemBeanText(v, dib, inputName);
		} else if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.RADIO)
				|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECT)) {
			// dib = validateDisplayItemBeanSingleCV(v, dib, inputName);
		}
		if (groupOrdinalPLusItemOid.containsKey(dib.getItem().getOid()) || fireRuleValidation) {
			messages = messages == null ? groupOrdinalPLusItemOid.get(dib.getItem().getOid()) : messages;
			dib = validateDisplayItemBeanSingleCV(rv, dib, inputName, messages);
		}
		// I_AGEN_DOSEDATE64
		return dib;
	}

	@Override
	protected List<DisplayItemGroupBean> validateDisplayItemGroupBean(DiscrepancyValidator v,
			DisplayItemGroupBean digb, List<DisplayItemGroupBean> digbs, List<DisplayItemGroupBean> formGroups,
			RuleValidator rv, HashMap<String, ArrayList<String>> groupOrdinalPLusItemOid, HttpServletRequest request,
			HttpServletResponse response) {

		EventDefinitionCRFBean edcb = (EventDefinitionCRFBean) request.getAttribute(EVENT_DEF_CRF_BEAN);

		formGroups = loadFormValueForItemGroup(digb, digbs, formGroups, edcb.getId(), request);
		String inputName = "";
		for (int i = 0; i < formGroups.size(); i++) {
			DisplayItemGroupBean displayGroup = formGroups.get(i);

			List<DisplayItemBean> items = displayGroup.getItems();
			int order = displayGroup.getOrdinal();

			for (DisplayItemBean displayItem : items) {
				if (displayGroup.isAuto()) {

					inputName = getGroupItemInputName(displayGroup, displayGroup.getFormInputOrdinal(), displayItem);
				} else {

					inputName = getGroupItemManualInputName(displayGroup, displayGroup.getFormInputOrdinal(), displayItem);
				}

				if (groupOrdinalPLusItemOid.containsKey(displayItem.getItem().getOid())
						|| groupOrdinalPLusItemOid.containsKey(String.valueOf(order + 1)
								+ displayItem.getItem().getOid())) {
					logger.debug("IN : " + String.valueOf(order + 1) + displayItem.getItem().getOid());
					validateDisplayItemBean(v, displayItem, inputName, rv, groupOrdinalPLusItemOid, true,
							groupOrdinalPLusItemOid.get(String.valueOf(order + 1) + displayItem.getItem().getOid()),
							request);
				} else {
					validateDisplayItemBean(v, displayItem, inputName, rv, groupOrdinalPLusItemOid, false, null,
							request);
				}
			}
		}
		return formGroups;

	}

	@Override
	protected DisplayItemBean validateDisplayItemBean(DiscrepancyValidator v, DisplayItemBean dib, String inputName,
			HttpServletRequest request) {

		org.akaza.openclinica.bean.core.ResponseType rt = dib.getMetadata().getResponseSet().getResponseType();

		// note that this step sets us up both for
		// displaying the data on the form again, in the event of an error
		// and sending the data to the database, in the event of no error
		if (StringUtil.isBlank(inputName)) {// not an item from group, doesn't
			// need to get data from form again
			dib = loadFormValue(dib, request);
		}

		// types TEL and ED are not supported yet
		if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.TEXT)
				|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.TEXTAREA)
				|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.FILE)) {
			dib = validateDisplayItemBeanText(v, dib, inputName, request);
		} else if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.RADIO)
				|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECT)) {
			dib = validateDisplayItemBeanSingleCV(v, dib, inputName);
		} else if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.CHECKBOX)
				|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECTMULTI)) {
			dib = validateDisplayItemBeanMultipleCV(v, dib, inputName);
		}

		logger.debug("just ran validate display item bean on " + inputName);
		return dib;
	}

	@Override
	protected List<DisplayItemGroupBean> validateDisplayItemGroupBean(DiscrepancyValidator v,
			DisplayItemGroupBean digb, List<DisplayItemGroupBean> digbs, List<DisplayItemGroupBean> formGroups,
			HttpServletRequest request, HttpServletResponse response) {
		EventDefinitionCRFBean edcb = (EventDefinitionCRFBean) request.getAttribute(EVENT_DEF_CRF_BEAN);
		formGroups = loadFormValueForItemGroup(digb, digbs, formGroups, edcb.getId(), request);
		String inputName = "";
		for (int i = 0; i < formGroups.size(); i++) {
			DisplayItemGroupBean displayGroup = formGroups.get(i);

			List<DisplayItemBean> items = displayGroup.getItems();
			for (DisplayItemBean displayItem : items) {
				if (displayGroup.isAuto()) {
					inputName = getGroupItemInputName(displayGroup, displayGroup.getFormInputOrdinal(), displayItem);
				} else {
					inputName = getGroupItemManualInputName(displayGroup, displayGroup.getFormInputOrdinal(), displayItem);
				}
				validateDisplayItemBean(v, displayItem, inputName, request);
			}
		}
		return formGroups;

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
		return Page.INITIAL_DATA_ENTRY_NW;

	}

	@Override
	protected Page getServletPage(HttpServletRequest request) {
		FormProcessor fp = new FormProcessor(request);
		String tabId = fp.getString("tab", true);
		String sectionId = fp.getString(DataEntryServlet.INPUT_SECTION_ID, true);
		String eventCRFId = fp.getString(INPUT_EVENT_CRF_ID, true);
		request.setAttribute("system_lang", CoreResources.getSystemLanguage());
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
}
