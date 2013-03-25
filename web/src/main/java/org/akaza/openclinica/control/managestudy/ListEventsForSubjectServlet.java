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

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.managestudy.DisplayStudyEventBean;
import org.akaza.openclinica.bean.managestudy.DisplayStudySubjectBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.DisplayEventCRFBean;
import org.akaza.openclinica.bean.submit.SubjectGroupMapBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.submit.SubmitDataServlet;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectGroupMapDAO;
import org.akaza.openclinica.service.crfdata.HideCRFManager;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.bean.DisplayStudySubjectEventsRow;
import org.akaza.openclinica.web.bean.EntityBeanTable;

@SuppressWarnings({"rawtypes", "unchecked","serial"})
public class ListEventsForSubjectServlet extends SecureController {

	Locale locale;

	@Override
	protected void mayProceed() throws InsufficientPermissionException {

		locale = request.getLocale();

		if (ub.isSysAdmin()) {
			return;
		}

		if (SubmitDataServlet.mayViewData(ub, currentRole)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("may_not_submit_data"), "1");
	}

	@Override
	public void processRequest() throws Exception {

		FormProcessor fp = new FormProcessor(request);
		// checks which module the requests are from
		String module = fp.getString(MODULE);
		request.setAttribute(MODULE, module);

		String pageNumber = fp.getString(ListStudySubjectServlet.SUBJECT_PAGE_NUMBER);
		String filterKeyword = fp.getString(ListStudySubjectServlet.FILTER_KEYWORD);
		String tmpSearch = fp.getString(ListStudySubjectServlet.SEARCH_SUBMITTED);
		boolean searchSubmitted = !(tmpSearch == null || "".equalsIgnoreCase(tmpSearch))
				&& !"".equalsIgnoreCase(filterKeyword);

		SubjectMatrixUtil matrixUtil = new SubjectMatrixUtil();
		String query = matrixUtil.createPaginatingQuery(pageNumber);
		StringBuilder extendedQuery = new StringBuilder(query);

		// URL encode the search keyword, since it will be a parameter in the
		// URL
		String filterKeywordURLEncode = java.net.URLEncoder.encode(filterKeyword, "UTF-8");

		if (searchSubmitted) {
			extendedQuery.append("&ebl_sortColumnInd=0&submitted=1&ebl_sortAscending=1&ebl_filtered=1");
			extendedQuery.append("&").append(ListStudySubjectServlet.FILTER_KEYWORD).append("=")
					.append(filterKeywordURLEncode);
		}
		request.setAttribute(ListStudySubjectServlet.PAGINATING_QUERY, extendedQuery.toString());

		int definitionId = fp.getInt("defId");
		int tabId = fp.getInt("tab");
		if (definitionId <= 0) {
			addPageMessage(respage.getString("please_choose_an_ED_ta_to_vies_details"));
			forwardPage(Page.LIST_STUDY_SUBJECTS);
			return;
		}

		StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
		StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao.findByPK(definitionId);

		StudySubjectDAO sdao = new StudySubjectDAO(sm.getDataSource());
		StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());

		SubjectGroupMapDAO sgmdao = new SubjectGroupMapDAO(sm.getDataSource());
		StudyGroupClassDAO sgcdao = new StudyGroupClassDAO(sm.getDataSource());

		EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
		CRFDAO crfdao = new CRFDAO(sm.getDataSource());

		// find all the groups in the current study
		ArrayList studyGroupClasses = sgcdao.findAllActiveByStudy(currentStudy);

		// information for the event tabs
		ArrayList allDefs = seddao.findAllActiveByStudy(currentStudy);
		if (currentStudy.getParentStudyId() > 0) {

			StudyDAO stdao = new StudyDAO(sm.getDataSource());
			StudyBean parent = (StudyBean) stdao.findByPK(currentStudy.getParentStudyId());

			allDefs = seddao.findAllActiveByStudy(parent);

		}

		ArrayList eventDefinitionCRFs = (ArrayList) edcdao.findAllActiveByEventDefinitionId(this.currentStudy,
				definitionId);

		for (int i = 0; i < eventDefinitionCRFs.size(); i++) {
			// FIXME could we reduce this to one call to the DB?
			EventDefinitionCRFBean edc = (EventDefinitionCRFBean) eventDefinitionCRFs.get(i);
			CRFBean crf = (CRFBean) crfdao.findByPK(edc.getCrfId());
			edc.setCrf(crf);

		}
		request.setAttribute("studyGroupClasses", studyGroupClasses);
		request.setAttribute("allDefsArray", allDefs);
		request.setAttribute("allDefsNumber", new Integer(allDefs.size()));
		request.setAttribute("groupSize", new Integer(studyGroupClasses.size()));
		request.setAttribute("eventDefCRFSize", new Integer(eventDefinitionCRFs.size()));
		request.setAttribute("tabId", new Integer(tabId));
		request.setAttribute("studyEventDef", sed);
		request.setAttribute("eventDefCRFs", eventDefinitionCRFs);

		// find all the subjects in current study
		ArrayList subjects = sdao.findAllByStudyId(currentStudy.getId());

		ArrayList displayStudySubs = new ArrayList();
		for (int i = 0; i < subjects.size(); i++) {
			StudySubjectBean studySub = (StudySubjectBean) subjects.get(i);

			ArrayList groups = (ArrayList) sgmdao.findAllByStudySubject(studySub.getId());

			ArrayList subGClasses = new ArrayList();
			for (int j = 0; j < studyGroupClasses.size(); j++) {
				StudyGroupClassBean sgc = (StudyGroupClassBean) studyGroupClasses.get(j);
				boolean hasClass = false;
				for (int k = 0; k < groups.size(); k++) {
					SubjectGroupMapBean sgmb = (SubjectGroupMapBean) groups.get(k);
					if (sgmb.getGroupClassName().equalsIgnoreCase(sgc.getName())) {
						subGClasses.add(sgmb);
						hasClass = true;
						break;
					}

				}
				if (!hasClass) {
					subGClasses.add(new SubjectGroupMapBean());
				}

			}

			ArrayList<DisplayStudyEventBean> displayEvents = new ArrayList<DisplayStudyEventBean>();
			ArrayList events = sedao.findAllByStudySubjectAndDefinition(studySub, sed);

			for (int k = 0; k < events.size(); k++) {
				StudyEventBean seb = (StudyEventBean) events.get(k);
				DisplayStudyEventBean dseb = ListStudySubjectServlet.getDisplayStudyEventsForStudySubject(studySub,
						seb, sm.getDataSource(), ub, currentRole, this.currentStudy);

				displayEvents.add(dseb);
			}

			for (int k = 0; k < displayEvents.size(); k++) {
				DisplayStudyEventBean dseb = displayEvents.get(k);
				ArrayList eventCRFs = dseb.getDisplayEventCRFs();

				for (int a = 0; a < eventDefinitionCRFs.size(); a++) {
					EventDefinitionCRFBean edc = (EventDefinitionCRFBean) eventDefinitionCRFs.get(a);
					int crfId = edc.getCrfId();
					boolean hasCRF = false;
					for (int b = 0; b < eventCRFs.size(); b++) {
						DisplayEventCRFBean decb = (DisplayEventCRFBean) eventCRFs.get(b);
						if (decb.getEventCRF().getCrf().getId() == crfId) {
							dseb.getAllEventCRFs().add(decb);

							hasCRF = true;
							break;
						}
					}
					if (hasCRF == false) {
						DisplayEventCRFBean db = new DisplayEventCRFBean();
						db.setEventDefinitionCRF(edc);
						db.getEventDefinitionCRF().setCrf(edc.getCrf());
						dseb.getAllEventCRFs().add(db);

					}

				}

				if (currentStudy.getParentStudyId() > 0) {
					// check each eventDefCRFBean and set its isHidden property
					// to true, if its
					// persistent/database-derived hideCrf is true (domain rule:
					// hide the CRF from users logged into a site)
					HideCRFManager hideCRFManager = HideCRFManager.createHideCRFManager();
					hideCRFManager.optionallyCheckHideCRFProperty(dseb);

					// remove a DisplayEventCRFBean from a DisplayStudyEventBean
					// in the list
					// if it contains a hidden CRF in its event definition
					hideCRFManager.removeHiddenEventCRF(dseb);

					// generate a boolean request attribute indicating whether
					// any of the CRFs
					// should be hidden
					boolean hideCRFs = hideCRFManager.studyEventHasAHideCRFProperty(dseb);
					request.setAttribute("hideCRFs", hideCRFs);
				}
			}

			DisplayStudySubjectBean dssb = new DisplayStudySubjectBean();

			dssb.setStudySubject(studySub);
			dssb.setStudyGroups(subGClasses);
			dssb.setStudyEvents(displayEvents);
			if (definitionId > 0) {
				dssb.setSedId(definitionId);
			} else {
				dssb.setSedId(-1);
			}
			displayStudySubs.add(dssb);
		}

		EntityBeanTable table = fp.getEntityBeanTable();
		ArrayList allStudyRows = DisplayStudySubjectEventsRow.generateRowsFromBeans(displayStudySubs);

		ArrayList columnArray = new ArrayList();

		columnArray.add(resword.getString("ID"));
		columnArray.add(resword.getString("subject_status"));
		if (currentStudy == null || currentStudy.getStudyParameterConfig().getGenderRequired().equalsIgnoreCase("true")) {
			columnArray.add(currentStudy == null ? resword.getString("gender") : currentStudy.getStudyParameterConfig()
					.getGenderLabel());
		}
		for (int i = 0; i < studyGroupClasses.size(); i++) {
			StudyGroupClassBean sgc = (StudyGroupClassBean) studyGroupClasses.get(i);
			columnArray.add(sgc.getName());
		}

		columnArray.add(resword.getString("event_status"));
		columnArray.add(resword.getString("event_date"));

		HideCRFManager hideCRFMgr = HideCRFManager.createHideCRFManager();
		hideCRFMgr.hideSpecifiedEventCRFDefBeans(eventDefinitionCRFs);

		for (int i = 0; i < eventDefinitionCRFs.size(); i++) {
			EventDefinitionCRFBean edc = (EventDefinitionCRFBean) eventDefinitionCRFs.get(i);
			if (!(currentStudy.getParentStudyId() > 0)) {
				columnArray.add(edc.getCrf().getName());
			} else {
				if (!edc.isHideCrf()) {
					columnArray.add(edc.getCrf().getName());
				}
			}
		}
		columnArray.add(resword.getString("actions"));
		String columns[] = new String[columnArray.size()];
		columnArray.toArray(columns);

		table.setColumns(new ArrayList(Arrays.asList(columns)));
		table.setQuery("ListEventsForSubject?module=" + module + "&defId=" + definitionId + "&tab=" + tabId,
				new HashMap());
		table.hideColumnLink(columnArray.size() - 1);

		table.setRows(allStudyRows);
		if (filterKeyword != null && !"".equalsIgnoreCase(filterKeyword)) {
			table.setKeywordFilter(filterKeyword);
		}
		table.computeDisplay();

		request.setAttribute("table", table);

		forwardPage(Page.LIST_EVENTS_FOR_SUBJECT);
	}
}
