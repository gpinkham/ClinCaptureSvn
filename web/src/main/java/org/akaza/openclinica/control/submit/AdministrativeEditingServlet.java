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
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.managestudy.ViewNotesServlet;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

/**
 * Performs 'administrative editing' action for study director/study coordinator.
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
		return Page.DATA_ENTRY;
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
			checkStudyLocked(Page.LIST_STUDY_SUBJECTS, getResPage().getString("current_study_locked"), request, response);
		}
		request.setAttribute("fromResolvingNotes", fromResolvingNotes);
		DataEntryStage stage = ecb.getStage();
		Role r = currentRole.getRole();
		session.setAttribute("mayProcessUploading", "true");

		if (!maySubmitData(ub, currentRole)) {
			session.setAttribute("mayProcessUploading", "false");
			String exceptionName = getResException().getString("no_permission_validation");
			String noAccessMessage = getResPage().getString("you_may_not_perform_administrative_editing");

			addPageMessage(noAccessMessage, request);
			throw new InsufficientPermissionException(Page.MENU, exceptionName, "1");
		}
		logger.info("stage name:" + stage.getName());
		if (stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE)) {
			if (Role.isMonitor(r)) {
				session.setAttribute("mayProcessUploading", "false");
				addPageMessage(
						getResPage().getString("no_have_correct_privilege_current_study")
								+ getResPage().getString("change_study_contact_sysadmin"), request);
				throw new InsufficientPermissionException(Page.LIST_STUDY_SUBJECTS_SERVLET,
						getResException().getString("no_permission_administrative_editing"), "1");
			}
		} else {
			session.setAttribute("mayProcessUploading", "false");
			addPageMessage(getResPage().getString("you_may_not_perform_administrative_editing"), request);
			throw new InsufficientPermissionException(Page.LIST_STUDY_SUBJECTS_SERVLET,
					getResPage().getString("you_may_not_perform_administrative_editing"), "1");
		}
	}

	@Override
	protected void putDataEntryStageFlagToRequest(HttpServletRequest request) {
		request.setAttribute(DATA_ENTRY_STAGE, DataEntryStage.ADMINISTRATIVE_EDITING);
	}

	@Override
	protected void setEventCRFAnnotations(String annotations, HttpServletRequest request) {
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);

		ecb.setAnnotations(annotations);
	}

	@Override
	protected Status getBlankItemStatus() {
		return Status.UNAVAILABLE;
	}

	@Override
	protected Status getNonBlankItemStatus(HttpServletRequest request) {
		return Status.UNAVAILABLE;
	}

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
		StudyBean currentStudy = (StudyBean) request.getSession().getAttribute("study");
		return currentStudy.getStudyParameterConfig().getAdminForcedReasonForChange().equals("true");
	}

	@Override
	protected CurrentDataEntryStage getCurrentDataEntryStage() {
		return CurrentDataEntryStage.ADMINISTRATIVE_DATA_ENTRY;
	}
}
