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

import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.DisplayItemGroupBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.control.form.DiscrepancyValidator;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.RuleValidator;
import org.akaza.openclinica.control.managestudy.ViewNotesServlet;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

/**
 * Performs 'administrative editing' action for study director/study coordinator
 * 
 * @author jxu
 * 
 */
@SuppressWarnings({ "serial" })
@Component
public class AdministrativeEditingServlet extends DataEntryServlet {

	@Override
	protected Page getServletPage(HttpServletRequest request) {
		FormProcessor fp = new FormProcessor(request);
		String tabId = fp.getString("tab", true);
		String sectionId = fp.getString(DataEntryServlet.INPUT_SECTION_ID, true);
		String eventCRFId = fp.getString(INPUT_EVENT_CRF_ID, true);
		if (StringUtil.isBlank(sectionId) || StringUtil.isBlank(tabId)) {
			return Page.ADMIN_EDIT_SERVLET;
		} else {
			Page target = Page.ADMIN_EDIT_SERVLET;
			String s = target.getFileName().trim();
			if (s.contains("?")) {
				String[] t = s.split("\\?");
				String x = "";
				String y = t[0] + "?";
				if (t.length > 1) {
					if (t[1].contains("&")) {
						String[] ts = t[1].split("&");
						for (int i = 0; i < ts.length; ++i) {
							if (ts[i].contains("eventCRFId=")) {
								ts[i] = "eventCRFId=" + eventCRFId;
								x += "e";
							} else if (ts[i].contains("sectionId=")) {
								ts[i] = "sectionId=" + sectionId;
								x += "s";
							} else if (ts[i].contains("tab=")) {
								ts[i] = "tab=" + tabId;
								x += "t";
							}
							y += ts[i] + "&";
						}
					} else {
						if (t[1].contains("eventCRFId=")) {
							t[1] = "eventCRFId=" + eventCRFId;
							x += "e";
						} else if (t[1].contains("sectionId=")) {
							t[1] = "sectionId=" + sectionId;
							x += "s";
						} else if (t[1].contains("tab=")) {
							t[1] = "tab=" + tabId;
							x += "t";
						}
						y += t[1] + "&";
					}
					if (x.length() < 3) {
						if (!x.contains("e")) {
							y += "eventCRFId=" + eventCRFId + "&";
						}
						if (!x.contains("s")) {
							y += "sectionId=" + sectionId + "&";
						}
						if (!x.contains("t")) {
							y += "tab=" + tabId + "&";
						}
					}
					y = y.substring(0, y.length() - 1);
					target.setFileName(y);
				} else {
					logger.info("It's a wrong servlet page:" + s);
				}
			} else {
				target.setFileName(target.getFileName() + "?eventCRFId=" + eventCRFId + "&sectionId=" + sectionId
						+ "&tab=" + tabId);
			}
			return target;
		}

	}

	@Override
	protected Page getJSPPage() {
		return Page.ADMIN_EDIT;
	}

	@Override
	protected boolean validateInputOnFirstRound() {
		return true;
	}

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		mayAccess(request);
		HttpSession session = request.getSession();
		FormProcessor fp = new FormProcessor(request);
		UserAccountBean ub = (UserAccountBean) request.getSession().getAttribute(USER_BEAN_NAME);
		StudyUserRoleBean currentRole = (StudyUserRoleBean) request.getSession().getAttribute("userRole");

		getInputBeans(request);
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);
		String fromResolvingNotes = fp.getString("fromResolvingNotes", true);

		if (StringUtil.isBlank(fromResolvingNotes)) {
			session.removeAttribute(ViewNotesServlet.WIN_LOCATION);
			session.removeAttribute(ViewNotesServlet.NOTES_TABLE);
			checkStudyLocked(Page.LIST_STUDY_SUBJECTS, respage.getString("current_study_locked"), request, response);
			//checkStudyFrozen(Page.LIST_STUDY_SUBJECTS, respage.getString("current_study_frozen"), request, response);
		}
		request.setAttribute("fromResolvingNotes", fromResolvingNotes);
		DataEntryStage stage = ecb.getStage();
		Role r = currentRole.getRole();
		session.setAttribute("mayProcessUploading", "true");

		if (!SubmitDataServlet.maySubmitData(ub, currentRole)) {
			session.setAttribute("mayProcessUploading", "false");
			String exceptionName = resexception.getString("no_permission_validation");
			String noAccessMessage = respage.getString("you_may_not_perform_administrative_editing");

			addPageMessage(noAccessMessage, request);
			throw new InsufficientPermissionException(Page.MENU, exceptionName, "1");
		}
		logger.info("stage name:" + stage.getName());
		if (stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE)) {
			if (r.equals(Role.STUDY_MONITOR)) {
				session.setAttribute("mayProcessUploading", "false");
				addPageMessage(
						respage.getString("no_have_correct_privilege_current_study")
								+ respage.getString("change_study_contact_sysadmin"), request);
				throw new InsufficientPermissionException(Page.LIST_STUDY_SUBJECTS_SERVLET,
						resexception.getString("no_permission_administrative_editing"), "1");
			}
		}

		else {
			session.setAttribute("mayProcessUploading", "false");
			addPageMessage(respage.getString("you_may_not_perform_administrative_editing"), request);
			throw new InsufficientPermissionException(Page.LIST_STUDY_SUBJECTS_SERVLET,
					respage.getString("you_may_not_perform_administrative_editing"), "1");
		}
		return;
	}

	@Override
	protected void setEventCRFAnnotations(String annotations, HttpServletRequest request) {
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);

		ecb.setAnnotations(annotations);
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
					inputName = getGroupItemManualInputName(displayGroup, displayGroup.getFormInputOrdinal(),
							displayItem);
				}
				validateDisplayItemBean(v, displayItem, inputName, request);
			}
		}
		return formGroups;

	}

	@Override
	protected DisplayItemBean validateDisplayItemBean(DiscrepancyValidator v, DisplayItemBean dib, String inputName,
			RuleValidator rv, HashMap<String, ArrayList<String>> groupOrdinalPLusItemOid, Boolean fireRuleValidation,
			ArrayList<String> messages, HttpServletRequest request) {
		if (StringUtil.isBlank(inputName)) {// we pass a blank inputName,which
			// means if not an item from group,
			// doesn't
			// need to get data from form again
			dib = loadFormValue(dib, request);
		}
		if (groupOrdinalPLusItemOid.containsKey(dib.getItem().getOid()) || fireRuleValidation) {
			messages = messages == null ? groupOrdinalPLusItemOid.get(dib.getItem().getOid()) : messages;
			dib = validateDisplayItemBeanSingleCV(rv, dib, inputName, messages);
		}
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
			/*
			 * if (displayGroup.isAuto() && displayGroup.getFormInputOrdinal() > 0) { order =
			 * displayGroup.getFormInputOrdinal(); }
			 */
			for (DisplayItemBean displayItem : items) {
				// int manualcount = 0;
				// tbh trying to set this correctly 01/2010
				if (displayGroup.isAuto()) {
					inputName = getGroupItemInputName(displayGroup, displayGroup.getFormInputOrdinal(), displayItem);
				} else {
					inputName = getGroupItemManualInputName(displayGroup, displayGroup.getFormInputOrdinal(),
							displayItem);
					// manualcount++;
				}
				logger.debug("THe oid is " + displayItem.getItem().getOid() + " order : " + order + " inputName : "
						+ inputName);

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.akaza.openclinica.control.submit.DataEntryServlet#getBlankItemStatus ()
	 */
	@Override
	protected Status getBlankItemStatus() {
		return Status.UNAVAILABLE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.akaza.openclinica.control.submit.DataEntryServlet#getNonBlankItemStatus ()
	 */
	@Override
	protected Status getNonBlankItemStatus(HttpServletRequest request) {
		return Status.UNAVAILABLE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.akaza.openclinica.control.submit.DataEntryServlet#getEventCRFAnnotations ()
	 */
	@Override
	protected String getEventCRFAnnotations(HttpServletRequest request) {
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);

		return ecb.getAnnotations();
	}

	@Override
	protected boolean shouldRunRules() {
		return false;
	}

	@Override
	protected boolean isAdministrativeEditing() {
		return true;
	}

	@Override
	protected boolean isAdminForcedReasonForChange(HttpServletRequest request) {
		// StudyParameterValueDAO spvdao = new
		// StudyParameterValueDAO();
		// ArrayList studyParameters =
		// spvdao.findParamConfigByStudy(currentStudy);

		// currentStudy.setStudyParameters(studyParameters);
		// refresh study params here, tbh 06/2009
		StudyBean currentStudy = (StudyBean) request.getSession().getAttribute("study");
		if (currentStudy.getStudyParameterConfig().getAdminForcedReasonForChange().equals("true")) {
			return true;
		} else {
			return false;
		}
	}

}
