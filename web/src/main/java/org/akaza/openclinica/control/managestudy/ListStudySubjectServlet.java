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
package org.akaza.openclinica.control.managestudy;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.managestudy.DisplayStudySubjectBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.SubjectGroupMapBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormDiscrepancyNotes;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.submit.AddNewSubjectServlet;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.SubjectGroupMapDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.bean.DisplayStudySubjectRow;
import org.akaza.openclinica.web.bean.EntityBeanTable;

/**
 * ListStudySubjectServlet that handles user request of "Subject Matrix".
 */
@SuppressWarnings({"rawtypes", "unchecked", "serial"})
public abstract class ListStudySubjectServlet extends Controller {

	public static final String SUBJECT_PAGE_NUMBER = "ebl_page";
	public static final String PAGINATING_QUERY = "paginatingQuery";
	public static final String FILTER_KEYWORD = "ebl_filterKeyword";
	public static final String SEARCH_SUBMITTED = "submitted";

	/**
	 * Class holder for internal purposes.
	 */
	@SuppressWarnings("unused")
	private class ArrayListsHolder {

		private ArrayList allDefs;
		private ArrayList studyGroupClasses;

		public ArrayList getStudyGroupClasses() {
			return studyGroupClasses;
		}

		public void setStudyGroupClasses(ArrayList studyGroupClasses) {
			this.studyGroupClasses = studyGroupClasses;
		}
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		StudyBean currentStudy = getCurrentStudy(request);

		FormProcessor fp = new FormProcessor(request);

		request.setAttribute("closeInfoShowIcons", true);

		String pageNumber = fp.getString(SUBJECT_PAGE_NUMBER);

		String filterKeyword = fp.getString(FILTER_KEYWORD);

		String tmpSearch = fp.getString(SEARCH_SUBMITTED);
		boolean searchSubmitted = !(tmpSearch == null || "".equalsIgnoreCase(tmpSearch))
				&& !"".equalsIgnoreCase(filterKeyword) && !"+".equalsIgnoreCase(filterKeyword);

		StringBuilder paginatingQuery = getPaginatingQuery(pageNumber, searchSubmitted, filterKeyword);

		request.setAttribute(PAGINATING_QUERY, paginatingQuery.toString());

		generateTable(request, currentStudy, filterKeyword);

		// set up auto study subject id
		if (currentStudy != null) {
			String idSetting = currentStudy.getStudyParameterConfig().getSubjectIdGeneration();
			if (idSetting.equals("auto editable") || idSetting.equals("auto non-editable")) {
				String nextLabel = getStudySubjectDAO().findNextLabel(currentStudy);
				request.setAttribute("label", nextLabel);
			}
		}

		FormDiscrepancyNotes discNotes = new FormDiscrepancyNotes();
		request.getSession().setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, discNotes);

		forwardPage(getJSP(), request, response);
	}

	private void generateTable(HttpServletRequest request, StudyBean currentStudy, String filterKeyword) {
		StudySubjectDAO sdao = new StudySubjectDAO(getDataSource());
		StudyEventDAO sedao = new StudyEventDAO(getDataSource());
		SubjectGroupMapDAO sgmdao = new SubjectGroupMapDAO(getDataSource());

		ArrayListsHolder arrayListsHolder = processRequest(request, currentStudy, currentStudy.getParentStudyId());
		List<StudySubjectBean> subjects = sdao.findAllByStudyId(currentStudy.getId());

		ArrayList<DisplayStudySubjectBean> displayStudySubs = new ArrayList<DisplayStudySubjectBean>();
		for (StudySubjectBean studySub : subjects) {
			ArrayList groups = (ArrayList) sgmdao.findAllByStudySubject(studySub.getId());
			ArrayList subGClasses = new ArrayList();
			for (Object studyGroupClass : arrayListsHolder.studyGroupClasses) {
				StudyGroupClassBean sgc = (StudyGroupClassBean) studyGroupClass;
				boolean hasClass = false;
				for (Object group : groups) {
					SubjectGroupMapBean sgmb = (SubjectGroupMapBean) group;
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

			ArrayList subEvents = new ArrayList();

			ArrayList events = sedao.findAllByStudySubject(studySub);

			for (Object allDef : arrayListsHolder.allDefs) {
				StudyEventDefinitionBean sed = (StudyEventDefinitionBean) allDef;
				boolean hasDef = false;
				for (Object event : events) {
					StudyEventBean se = (StudyEventBean) event;
					if (se.getStudyEventDefinitionId() == sed.getId()) {
						se.setStudyEventDefinition(sed);

						subEvents.add(se);
						hasDef = true;
					}
				}
				if (!hasDef) {
					StudyEventBean blank = new StudyEventBean();
					blank.setSubjectEventStatus(SubjectEventStatus.NOT_SCHEDULED);
					blank.setStudyEventDefinitionId(sed.getId());
					blank.setStudyEventDefinition(sed);
					subEvents.add(blank);
				}
			}

			int currDefId;
			int prevDefId = 0;
			ArrayList finalEvents = new ArrayList();
			int repeatingNum = 1;
			StudyEventBean event = new StudyEventBean();

			for (int j = 0; j < subEvents.size(); j++) {
				StudyEventBean se = (StudyEventBean) subEvents.get(j);
				currDefId = se.getStudyEventDefinitionId();
				if (currDefId != prevDefId) {
					if (repeatingNum > 1) {
						event.setRepeatingNum(repeatingNum);
						repeatingNum = 1;
					}
					finalEvents.add(se);
					event = se;
				} else {
					repeatingNum++;
					event.getRepeatEvents().add(se);
					if (j == subEvents.size() - 1) {
						event.setRepeatingNum(repeatingNum);
						repeatingNum = 1;
					}
				}
				prevDefId = currDefId;
			}
			DisplayStudySubjectBean dssb = new DisplayStudySubjectBean();
			dssb.setStudyEvents(finalEvents);
			dssb.setStudySubject(studySub);
			dssb.setStudyGroups(subGClasses);
			displayStudySubs.add(dssb);
		}

		boolean isRequiredUncomplete;
		for (DisplayStudySubjectBean subject : displayStudySubs) {
			for (Object o : subject.getStudyEvents()) {
				StudyEventBean event = (StudyEventBean) o;
				if (event.getSubjectEventStatus() != null
						&& event.getSubjectEventStatus().getId() == SubjectEventStatus.DATA_ENTRY_STARTED.getId()) {
					subject.setStudySignable(false);
					break;
				} else {
					isRequiredUncomplete = eventHasRequiredUncompleteCRFs(event);
					if (isRequiredUncomplete) {
						subject.setStudySignable(false);
						break;
					}
				}
			}
		}

		FormProcessor fp = new FormProcessor(request);
		EntityBeanTable table = fp.getEntityBeanTable();
		ArrayList allStudyRows = DisplayStudySubjectRow.generateRowsFromBeans(displayStudySubs);

		ArrayList columnArray = new ArrayList();

		columnArray.add(currentStudy.getStudyParameterConfig().getStudySubjectIdLabel());
		columnArray.add(resword.getString("subject_status"));

		columnArray.add(resword.getString("OID"));

		if (currentStudy.getStudyParameterConfig().getGenderRequired().equalsIgnoreCase("true")) {
			columnArray.add(currentStudy.getStudyParameterConfig().getGenderLabel());
		}
		if (!currentStudy.getStudyParameterConfig().getSecondaryIdRequired().equalsIgnoreCase("not_used")) {
			columnArray.add(currentStudy.getStudyParameterConfig().getSecondaryIdLabel());
		}

		for (Object studyGroupClass : arrayListsHolder.studyGroupClasses) {
			StudyGroupClassBean sgc = (StudyGroupClassBean) studyGroupClass;
			columnArray.add(sgc.getName());
		}
		for (Object allDef : arrayListsHolder.allDefs) {
			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) allDef;
			columnArray.add(sed.getName());
		}
		columnArray.add(resword.getString("actions"));
		String[] columns = new String[columnArray.size()];
		columnArray.toArray(columns);

		table.setColumns(new ArrayList(Arrays.asList(columns)));
		table.setQuery(getBaseURL(), new HashMap());

		table.hideColumnLink(columnArray.size() - 1);

		table.setRows(allStudyRows);
		if (filterKeyword != null && !"".equalsIgnoreCase(filterKeyword)) {
			table.setKeywordFilter(filterKeyword);
		}
		table.computeDisplay();

		request.setAttribute("table", table);
	}

	private ArrayListsHolder processRequest(HttpServletRequest request, StudyBean currentStudy, int parentStudyId) {
		ArrayList allDefs;
		ArrayList studyGroupClasses;
		// allDefs holds the list of study event definitions used in the table,
		StudyDAO stdao = getStudyDAO();
		StudyGroupDAO sgdao = getStudyGroupDAO();
		StudyGroupClassDAO sgcdao = getStudyGroupClassDAO();
		StudyEventDefinitionDAO seddao = getStudyEventDefinitionDAO();
		if (parentStudyId > 0) {
			StudyBean parentStudy = (StudyBean) stdao.findByPK(parentStudyId);
			studyGroupClasses = sgcdao.findAllActiveByStudy(parentStudy);
			allDefs = seddao.findAllActiveByStudy(parentStudy);
		} else {
			parentStudyId = currentStudy.getId();
			studyGroupClasses = sgcdao.findAllActiveByStudy(currentStudy);
			allDefs = seddao.findAllActiveByStudy(currentStudy);
		}

		StudyParameterValueDAO spvdao = new StudyParameterValueDAO(getDataSource());
		StudyParameterValueBean parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "collectDob");
		currentStudy.getStudyParameterConfig().setCollectDob(parentSPV.getValue());
		parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "genderRequired");
		currentStudy.getStudyParameterConfig().setGenderRequired(parentSPV.getValue());
		parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "subjectPersonIdRequired");
		currentStudy.getStudyParameterConfig().setSubjectPersonIdRequired(parentSPV.getValue());
		parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "subjectIdGeneration");
		currentStudy.getStudyParameterConfig().setSubjectIdGeneration(parentSPV.getValue());
		parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "subjectIdPrefixSuffix");
		currentStudy.getStudyParameterConfig().setSubjectIdPrefixSuffix(parentSPV.getValue());

		parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "secondaryIdRequired");
		currentStudy.getStudyParameterConfig().setSecondaryIdRequired(parentSPV.getValue());
		parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "dateOfEnrollmentForStudyRequired");
		currentStudy.getStudyParameterConfig().setDateOfEnrollmentForStudyRequired(parentSPV.getValue());
		parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "studySubjectIdLabel");
		currentStudy.getStudyParameterConfig().setStudySubjectIdLabel(parentSPV.getValue());
		parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "secondaryIdLabel");
		currentStudy.getStudyParameterConfig().setSecondaryIdLabel(parentSPV.getValue());
		parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "dateOfEnrollmentForStudyLabel");
		currentStudy.getStudyParameterConfig().setDateOfEnrollmentForStudyLabel(parentSPV.getValue());
		parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "genderLabel");
		currentStudy.getStudyParameterConfig().setGenderLabel(parentSPV.getValue());

		parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "startDateTimeRequired");
		currentStudy.getStudyParameterConfig().setStartDateTimeRequired(parentSPV.getValue());
		parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "useStartTime");
		currentStudy.getStudyParameterConfig().setUseStartTime(parentSPV.getValue());
		parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "endDateTimeRequired");
		currentStudy.getStudyParameterConfig().setEndDateTimeRequired(parentSPV.getValue());
		parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "useEndTime");
		currentStudy.getStudyParameterConfig().setUseEndTime(parentSPV.getValue());
		parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "startDateTimeLabel");
		currentStudy.getStudyParameterConfig().setStartDateTimeLabel(parentSPV.getValue());
		parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "endDateTimeLabel");
		currentStudy.getStudyParameterConfig().setEndDateTimeLabel(parentSPV.getValue());

		parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "markImportedCRFAsCompleted");
		currentStudy.getStudyParameterConfig().setMarkImportedCRFAsCompleted(parentSPV.getValue());

		parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "autoScheduleEventDuringImport");
		currentStudy.getStudyParameterConfig().setAutoScheduleEventDuringImport(parentSPV.getValue());

		parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "autoCreateSubjectDuringImport");
		currentStudy.getStudyParameterConfig().setAutoCreateSubjectDuringImport(parentSPV.getValue());

		parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "allowSdvWithOpenQueries");
		currentStudy.getStudyParameterConfig().setAllowSdvWithOpenQueries(parentSPV.getValue());

		parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "replaceExisitingDataDuringImport");
		currentStudy.getStudyParameterConfig().setReplaceExisitingDataDuringImport(parentSPV.getValue());

		parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "allowCodingVerification");
		currentStudy.getStudyParameterConfig().setAllowCodingVerification(parentSPV.getValue());

		com.clinovo.model.System systemParam = getSystemDAO().findByName("defaultBioontologyURL");
		currentStudy.getStudyParameterConfig().setDefaultBioontologyURL(systemParam.getValue());

		systemParam = getSystemDAO().findByName("medicalCodingApiKey");
		currentStudy.getStudyParameterConfig().setMedicalCodingApiKey(systemParam.getValue());

		parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "autoCodeDictionaryName");
		currentStudy.getStudyParameterConfig().setAutoCodeDictionaryName(parentSPV.getValue());

		parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "medicalCodingApprovalNeeded");
		currentStudy.getStudyParameterConfig().setMedicalCodingApprovalNeeded(parentSPV.getValue());

		parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "medicalCodingContextNeeded");
		currentStudy.getStudyParameterConfig().setMedicalCodingContextNeeded(parentSPV.getValue());

		parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "allowCrfEvaluation");
		currentStudy.getStudyParameterConfig().setAllowCrfEvaluation(parentSPV.getValue());

		parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "evaluateWithContext");
		currentStudy.getStudyParameterConfig().setEvaluateWithContext(parentSPV.getValue());

		parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "allowRulesAutoScheduling");
		currentStudy.getStudyParameterConfig().setAllowRulesAutoScheduling(parentSPV.getValue());

		parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "autoTabbing");
		currentStudy.getStudyParameterConfig().setAutoTabbing(parentSPV.getValue());

		// for all the study groups for each group class
		for (Object studyGroupClass1 : studyGroupClasses) {
			StudyGroupClassBean sgc = (StudyGroupClassBean) studyGroupClass1;
			ArrayList groups = sgdao.findAllByGroupClass(sgc);
			sgc.setStudyGroups(groups);
		}
		request.setAttribute("studyGroupClasses", studyGroupClasses);

		// information for the event tabs
		request.getSession().setAttribute("allDefsArray", allDefs);
		request.getSession().setAttribute("allDefsNumber", allDefs.size());
		request.getSession().setAttribute("groupSize", studyGroupClasses.size());

		ArrayListsHolder arrayListsHolder = new ArrayListsHolder();
		arrayListsHolder.allDefs = allDefs;
		arrayListsHolder.studyGroupClasses = studyGroupClasses;

		return arrayListsHolder;
	}

	private StringBuilder getPaginatingQuery(String pageNumber, boolean searchSubmitted, String filterKeyword)
			throws UnsupportedEncodingException {
		StringBuilder paginatingQuery = new StringBuilder();
		if (pageNumber != null && !"".equalsIgnoreCase(pageNumber)) {
			int tempNum = 0;
			try {
				tempNum = Integer.parseInt(pageNumber);
			} catch (NumberFormatException nfe) {
				logger.error("Error has occurred.", nfe);
			}
			if (tempNum > 0) {
				paginatingQuery = new StringBuilder(SUBJECT_PAGE_NUMBER).append("=").append(pageNumber);
				paginatingQuery.append("&ebl_paginated=1");

			}
		}

		// URL encode the search keyword, since it will be a parameter in the
		// URL
		String filterKeywordURLEncode = java.net.URLEncoder.encode(filterKeyword, "UTF-8");
		if (searchSubmitted) {
			paginatingQuery.append("&ebl_sortColumnInd=0&submitted=1&ebl_sortAscending=1&ebl_filtered=1");
			paginatingQuery.append("&").append(FILTER_KEYWORD).append("=").append(filterKeywordURLEncode);
		}

		return paginatingQuery;
	}

	protected abstract String getBaseURL();

	protected abstract Page getJSP();

	private boolean eventHasRequiredUncompleteCRFs(StudyEventBean studyEvent) {

		if (studyEvent == null) {
			return false;
		}

		EventCRFDAO eventCRFDAO = new EventCRFDAO(getDataSource());
		EventDefinitionCRFDAO eventDefinitionDAO = new EventDefinitionCRFDAO(getDataSource());
		List<EventCRFBean> crfBeans = new ArrayList<EventCRFBean>();

		crfBeans.addAll(eventCRFDAO.findAllByStudyEvent(studyEvent));
		// If the EventCRFBean has a completionStatusId of 0
		// (indicating that it is not complete),
		// then find
		// out whether it's required. If so, then return from the method false.
		for (EventCRFBean crfBean : crfBeans) {
			if (crfBean != null && crfBean.getCompletionStatusId() == 0) {
				if (eventDefinitionDAO.isRequiredInDefinition(crfBean.getCRFVersionId(), studyEvent)) {
					return true;
				}
			}
		}

		return false;

	}
}
