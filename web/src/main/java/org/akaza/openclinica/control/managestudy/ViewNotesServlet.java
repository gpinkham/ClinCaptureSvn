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
 *
 * Created on Sep 23, 2005
 */
package org.akaza.openclinica.control.managestudy;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.akaza.openclinica.bean.core.DiscrepancyNoteType;
import org.akaza.openclinica.bean.core.ResolutionStatus;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteStatisticBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.RememberLastPage;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.submit.ListNotesTableFactory;
import org.akaza.openclinica.control.submit.SubmitDataServlet;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.service.DiscrepancyNoteUtil;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.jmesa.facade.TableFacade;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 
 * View a list of all discrepancy notes in current study
 * 
 * @author ssachs
 * @author jxu
 */
@Component
public class ViewNotesServlet extends RememberLastPage {

	private static final long serialVersionUID = 1L;

	public static final String PRINT = "print";
	public static final String RESOLUTION_STATUS = "resolutionStatus";
	public static final String TYPE = "discNoteType";
	public static final String WIN_LOCATION = "window_location";
	public static final String NOTES_TABLE = "notesTable";
	public static final String DISCREPANCY_NOTE_TYPE = "discrepancyNoteType";
	public static final String DISCREPANCY_NOTE_TYPE_PARAM = "listNotes_f_discrepancyNoteBean.disType";
	public static final String DISCREPANCY_NOTE_STATUS_PARAM = "listNotes_f_discrepancyNoteBean.resolutionStatus";
	public static final String DN_LIST_URL = "dnListUrl";
	public static final int ALL = -1;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		FormProcessor fp = new FormProcessor(request);
		String print = fp.getString(PRINT);

		if (!print.equalsIgnoreCase("yes") && shouldRedirect(request, response)) {
			return;
		}

		UserAccountBean ub = getUserAccountBean(request);

		removeLockedCRF(ub.getId());

		StudyBean currentStudy = getCurrentStudy(request);
		String module = request.getParameter("module");
		String moduleStr = "manage";
		if (module != null && module.trim().length() > 0) {
			if ("submit".equals(module)) {
				request.setAttribute("module", "submit");
				moduleStr = "submit";
			} else if ("admin".equals(module)) {
				request.setAttribute("module", "admin");
				moduleStr = "admin";
			} else {
				request.setAttribute("module", "manage");
			}
		}

		boolean showMoreLink = fp.getString("showMoreLink").equals("")
				|| Boolean.parseBoolean(fp.getString("showMoreLink"));

		int oneSubjectId = fp.getInt("id");
		request.getSession().setAttribute("subjectId", oneSubjectId);

		int discNoteTypeId;
		try {
			DiscrepancyNoteType discNoteType = DiscrepancyNoteType.getByName(request
					.getParameter(DISCREPANCY_NOTE_TYPE_PARAM));
			discNoteTypeId = discNoteType.getId();
		} catch (Exception e) {
			e.printStackTrace();
			discNoteTypeId = ALL;
		}
		request.setAttribute(DISCREPANCY_NOTE_TYPE, discNoteTypeId);

		boolean removeSession = fp.getBoolean("removeSession");

		request.getSession().setAttribute("module", module);

		// Do we only want to view the notes for 1 subject?
		String viewForOne = fp.getString("viewForOne");

		DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(getDataSource());
		dndao.setFetchMapping(true);

		int resolutionStatusId;
		try {
			ResolutionStatus resolutionStatus = ResolutionStatus.getByName(request
					.getParameter(DISCREPANCY_NOTE_STATUS_PARAM));
			resolutionStatusId = resolutionStatus.getId();
		} catch (Exception e) {
			e.printStackTrace();
			resolutionStatusId = ALL;
		}

		if (removeSession) {
			request.getSession().removeAttribute(WIN_LOCATION);
			request.getSession().removeAttribute(NOTES_TABLE);
		}

		// after resolving a note, user wants to go back to view notes page, we
		// save the current URL
		// so we can go back later
		request.getSession().setAttribute(
				WIN_LOCATION,
				"ViewNotes?viewForOne=" + viewForOne + "&id=" + oneSubjectId + "&module=" + module
						+ " &removeSession=1");

		boolean hasAResolutionStatus = resolutionStatusId >= 1 && resolutionStatusId <= 5;
		Set<Integer> resolutionStatusIds = (HashSet) request.getSession().getAttribute(RESOLUTION_STATUS);
		// remove the session if there is no resolution status
		if (!hasAResolutionStatus && resolutionStatusIds != null) {
			request.getSession().removeAttribute(RESOLUTION_STATUS);
			resolutionStatusIds = null;
		}
		if (hasAResolutionStatus) {
			if (resolutionStatusIds == null) {
				resolutionStatusIds = new HashSet<Integer>();
			}
			resolutionStatusIds.add(resolutionStatusId);
			request.getSession().setAttribute(RESOLUTION_STATUS, resolutionStatusIds);
		}

		StudySubjectDAO subdao = getStudySubjectDAO();
		StudyDAO studyDao = getStudyDAO();

		SubjectDAO sdao = getSubjectDAO();

		UserAccountDAO uadao = getUserAccountDAO();
		CRFVersionDAO crfVersionDao = getCRFVersionDAO();
		CRFDAO crfDao = getCRFDAO();
		StudyEventDAO studyEventDao = getStudyEventDAO();
		StudyEventDefinitionDAO studyEventDefinitionDao = getStudyEventDefinitionDAO();
		EventDefinitionCRFDAO eventDefinitionCRFDao = getEventDefinitionCRFDAO();
		ItemDataDAO itemDataDao = getItemDataDAO();
		ItemDAO itemDao = getItemDAO();
		EventCRFDAO eventCRFDao = getEventCRFDAO();

		ListNotesTableFactory factory = new ListNotesTableFactory(showMoreLink);
		factory.setSubjectDao(sdao);
		factory.setStudySubjectDao(subdao);
		factory.setUserAccountDao(uadao);
		factory.setStudyDao(studyDao);
		factory.setCurrentStudy(currentStudy);
		factory.setDiscrepancyNoteDao(dndao);
		factory.setCrfDao(crfDao);
		factory.setCrfVersionDao(crfVersionDao);
		factory.setStudyEventDao(studyEventDao);
		factory.setStudyEventDefinitionDao(studyEventDefinitionDao);
		factory.setEventDefinitionCRFDao(eventDefinitionCRFDao);
		factory.setItemDao(itemDao);
		factory.setItemDataDao(itemDataDao);
		factory.setEventCRFDao(eventCRFDao);
		factory.setModule(moduleStr);
		factory.setDiscNoteType(discNoteTypeId);
		factory.setResolutionStatus(resolutionStatusId);

		// Set data source
		factory.setDataSource(getDataSource());

		TableFacade tf = factory.createTable(request, response);

		if ("yes".equalsIgnoreCase(print)) {
			request.setAttribute("allNotes", factory.getNotesForPrintPop(tf.getLimit()));
			forwardPage(Page.VIEW_DISCREPANCY_NOTES_IN_STUDY_PRINT, request, response);
			return;
		}

		String viewNotesHtml = tf.render();

		request.setAttribute("viewNotesHtml", viewNotesHtml);
		String viewNotesURL = this.getPageURL(request);
		request.getSession().setAttribute("viewNotesURL", viewNotesURL);
		String viewNotesPageFileName = this.getPageServletFileName(request);
		request.getSession().setAttribute("viewNotesPageFileName", viewNotesPageFileName);

		List<DiscrepancyNoteStatisticBean> statisticBeans = dndao.countNotesStatistic(currentStudy);

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserAccountBean loggedInUser = (UserAccountBean) uadao.findByUserName(authentication.getName());

		if (loggedInUser.getRoleByStudy(currentStudy.getId()).getName().equalsIgnoreCase("study coder")) {

			statisticBeans = factory.getFilteredNotesStatistics();
		}

		Map<String, Map<String, String>> customStat = ListNotesTableFactory.getNotesStatistics(statisticBeans);
		Map<String, String> customTotalMap = ListNotesTableFactory.getNotesTypesStatistics(statisticBeans);

		request.setAttribute("summaryMap", customStat);
		request.setAttribute("mapKeys", ResolutionStatus.getMembers());
		request.setAttribute("typeNames", DiscrepancyNoteUtil.getTypeNames(resterm));
		request.setAttribute("typeKeys", customTotalMap);

		if (loggedInUser.getRoleByStudy(currentStudy.getId()).getName().equalsIgnoreCase("study coder")) {

			request.setAttribute("grandTotal", statisticBeans.size());

		} else {

			request.setAttribute("grandTotal", customTotalMap.get("Total"));
		}

		forwardPage(Page.VIEW_DISCREPANCY_NOTES_IN_STUDY, request, response);
	}

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (SubmitDataServlet.mayViewData(ub, currentRole)) {
			return;
		}

		addPageMessage(
				respage.getString("no_permission_to_view_discrepancies")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET,
				resexception.getString("not_study_director_or_study_cordinator"), "1");
	}

	@Override
	protected String getUrlKey(HttpServletRequest request) {
		return DN_LIST_URL;
	}

	@Override
	protected String getDefaultUrl(HttpServletRequest request) {
		FormProcessor fp = new FormProcessor(request);
		return "?module=" + fp.getString("module")
				+ "&maxRows=15&showMoreLink=true&listNotes_tr_=true&listNotes_p_=1&listNotes_mr_=15";
	}

	@Override
	protected boolean userDoesNotUseJmesaTableForNavigation(HttpServletRequest request) {
		return request.getQueryString() == null || !request.getQueryString().contains("&listNotes_")
				|| request.getQueryString().contains("&print=yes");
	}
}
