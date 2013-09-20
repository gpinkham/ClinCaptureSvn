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

import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.control.core.RememberLastPage;
import org.akaza.openclinica.control.form.FormDiscrepancyNotes;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.submit.SubmitDataServlet;
import org.akaza.openclinica.control.submit.AddNewSubjectServlet;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings({ "rawtypes" })
@Component
public class ListEventsForSubjectsServlet extends RememberLastPage {

    private static final long serialVersionUID = 1L;
    public static final String SAVED_LIST_EVENTS_FOR_SUBJECTS_URL = "savedListEventsForSubjectsUrl";

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response) throws InsufficientPermissionException {
        UserAccountBean ub = getUserAccountBean(request);
        StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin()) {
			return;
		}

		if (SubmitDataServlet.mayViewData(ub, currentRole)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("may_not_submit_data"), "1");
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (shouldRedirect(request, response)) {
            return;
        }

        UserAccountBean ub = getUserAccountBean(request);
        StudyBean currentStudy = getCurrentStudy(request);
        StudyUserRoleBean currentRole = getCurrentRole(request);

        boolean showMoreLink;
		FormProcessor fp = new FormProcessor(request);
		if (fp.getString("showMoreLink").equals("")) {
			showMoreLink = true;
		} else {
			showMoreLink = Boolean.parseBoolean(fp.getString("showMoreLink"));
		}
		String idSetting = currentStudy.getStudyParameterConfig().getSubjectIdGeneration();
		// set up auto study subject id
		if (idSetting.equals("auto editable") || idSetting.equals("auto non-editable")) {
			String nextLabel = getStudySubjectDAO().findNextLabel(currentStudy.getIdentifier());
			request.setAttribute("label", nextLabel);
		}

		// checks which module the requests are from
		String module = fp.getString(MODULE);
		request.setAttribute(MODULE, module);

		int definitionId = fp.getInt("defId");
		if (definitionId <= 0) {
			addPageMessage(respage.getString("please_choose_an_ED_ta_to_vies_details"), request);
			forwardPage(Page.LIST_STUDY_SUBJECTS_SERVLET, request, response);
			return;
		}

		ListEventsForSubjectTableFactory factory = new ListEventsForSubjectTableFactory(showMoreLink);
		factory.setStudyEventDefinitionDao(getStudyEventDefinitionDAO());
		factory.setSubjectDAO(getSubjectDAO());
		factory.setStudySubjectDAO(getStudySubjectDAO());
		factory.setStudyEventDAO(getStudyEventDAO());
		factory.setStudyBean(currentStudy);
		factory.setStudyGroupClassDAO(getStudyGroupClassDAO());
		factory.setSubjectGroupMapDAO(getSubjectGroupMapDAO());
		factory.setStudyDAO(getStudyDAO());
		factory.setStudyGroupDAO(getStudyGroupDAO());
		factory.setCurrentRole(currentRole);
		factory.setCurrentUser(ub);
		factory.setEventCRFDAO(getEventCRFDAO());
		factory.setEventDefintionCRFDAO(getEventDefinitionCRFDAO());
		factory.setCrfDAO(getCRFDAO());
		factory.setSelectedStudyEventDefinition((StudyEventDefinitionBean) getStudyEventDefinitionDAO().findByPK(
				definitionId));
		String listEventsForSubjectsHtml = factory.createTable(request, response).render();
		request.setAttribute("listEventsForSubjectsHtml", listEventsForSubjectsHtml);
		request.setAttribute("defId", definitionId);
		// For event definitions and group class list in the add subject popup
		request.setAttribute("allDefsArray", getEventDefinitionsByCurrentStudy(request));
		request.setAttribute("studyGroupClasses", getStudyGroupClassesByCurrentStudy(request));
		FormDiscrepancyNotes discNotes = new FormDiscrepancyNotes();
		request.getSession().setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, discNotes);

        forward(Page.LIST_EVENTS_FOR_SUBJECTS, request, response);
	}

	private String parseDefId(String currentDefId, String savedUrl) {
		String pattern = ".*defId=(\\d*).*";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(savedUrl);
		return m.find() ? m.group(m.groupCount()) : currentDefId;
	}

	@Override
	protected String getUrlKey() {
		return SAVED_LIST_EVENTS_FOR_SUBJECTS_URL;
	}

	@Override
	protected String getDefaultUrl(HttpServletRequest request) {
		FormProcessor fp = new FormProcessor(request);
        boolean showMoreLink;
		if (fp.getString("showMoreLink").equals("")) {
			showMoreLink = true;
		} else {
			showMoreLink = Boolean.parseBoolean(fp.getString("showMoreLink"));
		}
		String currentDefId = fp.getString("defId");
		String savedUrl = (String) request.getSession().getAttribute(SAVED_LIST_EVENTS_FOR_SUBJECTS_URL);
		if (savedUrl != null && !currentDefId.equals(parseDefId(currentDefId, savedUrl))) {
			savedUrl = null;
			request.getSession().removeAttribute(SAVED_LIST_EVENTS_FOR_SUBJECTS_URL);
		}
		savedUrl = savedUrl != null ? savedUrl.replaceAll(".*" + request.getContextPath() + "/ListStudySubjects", "")
				: savedUrl;
		return request.getMethod().equalsIgnoreCase("POST") && savedUrl != null ? savedUrl : "?module="
				+ fp.getString("module") + "&defId=" + fp.getString("defId") + "&maxRows=15&showMoreLink="
				+ showMoreLink + "&listEventsForSubject_tr_=true&listEventsForSubject_p_=1&listEventsForSubject_mr_=15";
	}

	@Override
	protected boolean userDoesNotUseJmesaTableForNavigation(HttpServletRequest request) {
		return request.getQueryString() == null || !request.getQueryString().contains("&listEventsForSubject_p_=");
	}
}
