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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jxu
 */
@SuppressWarnings({"rawtypes", "unchecked",  "serial"})
public abstract class ListStudySubjectServlet extends Controller {

	public static String SUBJECT_PAGE_NUMBER = "ebl_page";
	public static String PAGINATING_QUERY = "paginatingQuery";
	public static String FILTER_KEYWORD = "ebl_filterKeyword";
	public static String SEARCH_SUBMITTED = "submitted";

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        StudyBean currentStudy = getCurrentStudy(request);

		FormProcessor fp = new FormProcessor(request);

		request.setAttribute("closeInfoShowIcons", true);

		String pageNumber = fp.getString(SUBJECT_PAGE_NUMBER);
		StringBuilder paginatingQuery = new StringBuilder("");

		String filterKeyword = fp.getString(FILTER_KEYWORD);

		String tmpSearch = fp.getString(SEARCH_SUBMITTED);
		boolean searchSubmitted = !(tmpSearch == null || "".equalsIgnoreCase(tmpSearch))
				&& !"".equalsIgnoreCase(filterKeyword) && !"+".equalsIgnoreCase(filterKeyword);

		if (pageNumber != null && !"".equalsIgnoreCase(pageNumber)) {
			int tempNum = 0;
			try {
				tempNum = Integer.parseInt(pageNumber);
			} catch (NumberFormatException nfe) {
				// tempNum is already initialized to 0
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

		request.setAttribute(PAGINATING_QUERY, paginatingQuery.toString());

		StudyDAO stdao = new StudyDAO(getDataSource());
		StudySubjectDAO sdao = new StudySubjectDAO(getDataSource());
		StudyEventDAO sedao = new StudyEventDAO(getDataSource());
		StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(getDataSource());
		SubjectGroupMapDAO sgmdao = new SubjectGroupMapDAO(getDataSource());
		StudyGroupClassDAO sgcdao = new StudyGroupClassDAO(getDataSource());
		StudyGroupDAO sgdao = new StudyGroupDAO(getDataSource());
		// Update study parameters of current study.
		// "collectDob" and "genderRequired" are set as the same as the parent
		// study
		// tbh, also add the params "subjectPersonIdRequired",
		// "subjectIdGeneration", "subjectIdPrefixSuffix"
		int parentStudyId = currentStudy.getParentStudyId();
		ArrayList studyGroupClasses;
		ArrayList allDefs;
		// allDefs holds the list of study event definitions used in the table,
		
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

        parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "allowSdvWithOpenQueries");
        currentStudy.getStudyParameterConfig().setAllowSdvWithOpenQueries(parentSPV.getValue());

        parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "replaceExisitingDataDuringImport");
        currentStudy.getStudyParameterConfig().setReplaceExisitingDataDuringImport(parentSPV.getValue());
        
        parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "allowCodingVerification");
        currentStudy.getStudyParameterConfig().setAllowCodingVerification(parentSPV.getValue());
        
        parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "defaultBioontologyURL");
        currentStudy.getStudyParameterConfig().setDefaultBioontologyURL(parentSPV.getValue());
        
        parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "autoCodeDictionaryName");
        currentStudy.getStudyParameterConfig().setAutoCodeDictionaryName(parentSPV.getValue());
        
        parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "medicalCodingApprovalNeeded");
        currentStudy.getStudyParameterConfig().setMedicalCodingApprovalNeeded(parentSPV.getValue());

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

		// find all the subjects in current study
		ArrayList subjects = sdao.findAllByStudyId(currentStudy.getId());

		ArrayList<DisplayStudySubjectBean> displayStudySubs = new ArrayList<DisplayStudySubjectBean>();
        for (Object subject1 : subjects) {
            StudySubjectBean studySub = (StudySubjectBean) subject1;

            ArrayList groups = (ArrayList) sgmdao.findAllByStudySubject(studySub.getId());

            ArrayList subGClasses = new ArrayList();
            for (Object studyGroupClass : studyGroupClasses) {
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
            // find all events order by definition ordinal
            ArrayList events = sedao.findAllByStudySubject(studySub);

            for (Object allDef : allDefs) {
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
                    // how can we set the following:

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
                if (currDefId != prevDefId) {// find a new event
                    if (repeatingNum > 1) {
                        event.setRepeatingNum(repeatingNum);
                        repeatingNum = 1;
                    }
                    finalEvents.add(se); // add current event to final
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

		// Set a subject property to determine whether to show a signed-type
		// icon (electronic signature)
		// in the JSP view or not
		// Get all event crfs by studyevent id; then use
		// EventDefinitionCRFDAO.isRequired to
		// determine whether any uncompleted CRFs are required.
		boolean isRequiredUncomplete;
		for (DisplayStudySubjectBean subject : displayStudySubs) {
            for (Object o : subject.getStudyEvents()) {
                StudyEventBean event = (StudyEventBean) o;
                if (event.getSubjectEventStatus() != null && event.getSubjectEventStatus().getId() == 3) {
                    // disallow the subject from signing any studies
                    subject.setStudySignable(false);
                    break;
                } else {
                    // determine whether the subject has any required,
                    // uncompleted event CRFs
                    isRequiredUncomplete = eventHasRequiredUncompleteCRFs(event);
                    if (isRequiredUncomplete) {
                        subject.setStudySignable(false);
                        break;
                    }
                }
            }
		}

		fp = new FormProcessor(request);
		EntityBeanTable table = fp.getEntityBeanTable();
		ArrayList allStudyRows = DisplayStudySubjectRow.generateRowsFromBeans(displayStudySubs);

		ArrayList columnArray = new ArrayList();

		columnArray.add(currentStudy == null ? resword.getString("study_subject_ID") : currentStudy
				.getStudyParameterConfig().getStudySubjectIdLabel());
		columnArray.add(resword.getString("subject_status"));

		columnArray.add(resword.getString("OID"));

		if (currentStudy == null || currentStudy.getStudyParameterConfig().getGenderRequired().equalsIgnoreCase("true")) {
			columnArray.add(currentStudy == null ? resword.getString("gender") : currentStudy.getStudyParameterConfig()
					.getGenderLabel());
		}
		if (currentStudy == null
				|| !currentStudy.getStudyParameterConfig().getSecondaryIdRequired().equalsIgnoreCase("not_used")) {
			columnArray.add(currentStudy == null ? resword.getString("secondary_ID") : currentStudy
					.getStudyParameterConfig().getSecondaryIdLabel());
		}

        for (Object studyGroupClass : studyGroupClasses) {
            StudyGroupClassBean sgc = (StudyGroupClassBean) studyGroupClass;
            columnArray.add(sgc.getName());
        }
        for (Object allDef : allDefs) {
            StudyEventDefinitionBean sed = (StudyEventDefinitionBean) allDef;
            columnArray.add(sed.getName());
        }
		columnArray.add(resword.getString("actions"));
		String columns[] = new String[columnArray.size()];
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

		String idSetting = currentStudy.getStudyParameterConfig().getSubjectIdGeneration();
		// set up auto study subject id
		if (idSetting.equals("auto editable") || idSetting.equals("auto non-editable")) {

			String nextLabel = getStudySubjectDAO().findNextLabel(currentStudy.getIdentifier());
			request.setAttribute("label", nextLabel);
		}

		FormDiscrepancyNotes discNotes = new FormDiscrepancyNotes();
        request.getSession().setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, discNotes);

		forwardPage(getJSP(), request, response);
	}

	protected abstract String getBaseURL();

	protected abstract Page getJSP();

	private boolean eventHasRequiredUncompleteCRFs(StudyEventBean studyEvent) {

		if (studyEvent == null)
			return false;

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
