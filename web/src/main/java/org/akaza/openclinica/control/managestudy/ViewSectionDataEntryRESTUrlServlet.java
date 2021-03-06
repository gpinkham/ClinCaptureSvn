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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.util.DataEntryUtil;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.core.Utils;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.DisplayItemGroupBean;
import org.akaza.openclinica.bean.submit.DisplayItemWithGroupBean;
import org.akaza.openclinica.bean.submit.DisplaySectionBean;
import org.akaza.openclinica.bean.submit.DisplayTableOfContentsBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemGroupBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.form.FormDiscrepancyNotes;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.submit.AddNewSubjectServlet;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.dao.submit.ItemGroupDAO;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.service.DiscrepancyNoteThread;
import org.akaza.openclinica.service.DiscrepancyNoteUtil;
import org.akaza.openclinica.service.managestudy.DiscrepancyNoteService;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * View a CRF version section data entry.
 * 
 * @author jxu
 * 
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Component
public class ViewSectionDataEntryRESTUrlServlet extends ViewSectionDataEntryServlet {

	private static final Logger LOGGER = LoggerFactory.getLogger(ViewSectionDataEntryServlet.class);

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		FormProcessor fp = new FormProcessor(request);
		StudyBean currentStudy = (StudyBean) request.getSession().getAttribute("study");

		SectionBean sb;
		EventCRFBean ecb;
		EventDefinitionCRFBean edcb;

		StudyEventDAO studyEventDao = getStudyEventDAO();
		StudySubjectDAO studySubjectDao = getStudySubjectDAO();
		DiscrepancyNoteDAO discrepancyNoteDao = new DiscrepancyNoteDAO(getDataSource());

		if (!fp.getString("exitTo", true).equals("")) {
			request.setAttribute("exitTo", request.getContextPath() + "/" + fp.getString("exitTo", true));
		}
		int crfVersionId = fp.getInt("crfVersionId", true);

		Integer sectionId = (Integer) request.getAttribute("sectionId");
		if (sectionId == null || sectionId == 0) {
			sectionId = 1;
		}
		request.setAttribute("sectionId", "" + sectionId);
		int eventCRFId = fp.getInt(EVENT_CRF_ID, true);
		int studySubjectId = fp.getInt("studySubjectId", true);
		String action = fp.getString("action");
		HttpSession session = request.getSession();
		String fromResolvingNotes = fp.getString("fromResolvingNotes", true);
		if (StringUtil.isBlank(fromResolvingNotes)) {
			session.removeAttribute(ViewNotesServlet.WIN_LOCATION);
			session.removeAttribute(ViewNotesServlet.NOTES_TABLE);
		}

		request.setAttribute("studySubjectId", studySubjectId + "");

		request.setAttribute("crfListPage", fp.getString("crfListPage"));

		request.setAttribute("eventId", fp.getString("eventId"));
		int sedId = fp.getInt("sedId");
		request.setAttribute("sedId", sedId + "");
		int crfId = fp.getInt("crfId");
		if (crfId == 0 && crfVersionId > 0) {
			CRFVersionDAO crfVDao = getCRFVersionDAO();
			CRFVersionBean crvVBean = (CRFVersionBean) crfVDao.findByPK(crfVersionId);
			if (crvVBean != null) {
				crfId = crvVBean.getCrfId();
			}
		}

		Integer eventDefinitionCRFId = (Integer) (request.getAttribute("eventDefinitionCRFId"));

		EventDefinitionCRFDAO eventCrfDao = getEventDefinitionCRFDAO();
		edcb = (EventDefinitionCRFBean) eventCrfDao.findByPK(eventDefinitionCRFId);
		if (eventCRFId == 0 && edcb.getStudyId() != currentStudy.getParentStudyId()
				&& edcb.getStudyId() != currentStudy.getId()) {
			addPageMessage(
					getResPage().getString("no_have_correct_privilege_current_study") + " "
							+ getResPage().getString("change_study_contact_sysadmin"), request);
			throw new InsufficientPermissionException(Page.MENU_SERVLET, getResException().getString("not_director"), "1");
		}

		if (crfId == 0 && eventDefinitionCRFId > 0) {
			if (edcb != null) {
				crfId = edcb.getCrfId();
			}
		}
		request.setAttribute("crfId", crfId + "");
		request.setAttribute("eventDefinitionCRFId", eventDefinitionCRFId + "");
		String printVersion = fp.getString("print");
		// This has to be removed for CRFs that do not display an interviewdate
		// for a particular event
		session.removeAttribute("presetValues");

		ItemFormMetadataDAO ifmdao = new ItemFormMetadataDAO(getDataSource());
		EventCRFDAO ecdao = new EventCRFDAO(getDataSource());
		SectionDAO sdao = new SectionDAO(getDataSource());
		String age = "";
		if (crfVersionId == 0 && eventCRFId == 0) {
			addPageMessage(getResPage().getString("please_choose_a_CRF_to_view"), request);
			forwardPage(Page.LIST_STUDY_SUBJECTS_SERVLET, request, response);
			return;
		}
		if (studySubjectId > 0) {
			StudySubjectBean sub = (StudySubjectBean) studySubjectDao.findByPK(studySubjectId);
			request.setAttribute("studySubject", sub);
			StudyBean subjectStudy = getStudyService().getSubjectStudy(currentStudy, sub);
			request.setAttribute("subjectStudy", subjectStudy);
		}

		ArrayList<DiscrepancyNoteBean> allNotes;
		List<DiscrepancyNoteBean> eventCrfNotes;
		List<DiscrepancyNoteThread> noteThreads = new ArrayList<DiscrepancyNoteThread>();

		if (eventCRFId > 0) {
			// for event crf, the input crfVersionId from url =0
			ecb = (EventCRFBean) ecdao.findByPK(eventCRFId);

			StudyEventBean event = (StudyEventBean) studyEventDao.findByPK(ecb.getStudyEventId());
			if (event.getSubjectEventStatus().equals(SubjectEventStatus.LOCKED)) {
				request.setAttribute("isLocked", "yes");
			} else {
				request.setAttribute("isLocked", "no");
			}

			if (studySubjectId <= 0) {

				studySubjectId = event.getStudySubjectId();
				request.setAttribute("studySubjectId", studySubjectId + "");

			}
			// Get the status/number of item discrepancy notes

			allNotes = discrepancyNoteDao.findAllTopNotesByEventCRF(eventCRFId);
			eventCrfNotes = discrepancyNoteDao.findOnlyParentEventCRFDNotesFromEventCRF(ecb);
			if (!eventCrfNotes.isEmpty()) {
				allNotes.addAll(eventCrfNotes);
				this.setAttributeForInterviewerDNotes(eventCrfNotes, request);
			}
			// Create disc note threads out of the various notes
			DiscrepancyNoteUtil dNoteUtil = new DiscrepancyNoteUtil();
			noteThreads = dNoteUtil.createThreadsOfParents(allNotes, currentStudy, null, -1);

			List<SectionBean> allSections = sdao.findAllByCRFVersionId(ecb.getCRFVersionId());

			getCrfShortcutsAnalyzer(request, getItemSDVService(), true).prepareCrfShortcutLinks(ecb, ifmdao, edcb,
					allSections, noteThreads);

			DisplayTableOfContentsBean displayBean = getDisplayBean(ecb);

			Date tmpDate = displayBean.getEventCRF().getDateInterviewed();
			String formattedInterviewerDate;
			try {
				DateFormat localDf = new SimpleDateFormat(getResFormat().getString("date_format_string"),
						LocaleResolver.getLocale());
				formattedInterviewerDate = localDf.format(tmpDate);
			} catch (Exception e) {
				formattedInterviewerDate = "";
			}
			HashMap presetVals = (HashMap) session.getAttribute("presetValues");
			if (presetVals == null) {
				presetVals = new HashMap();
				session.setAttribute("presetValues", presetVals);
			}
			presetVals.put("interviewDate", formattedInterviewerDate);
			request.setAttribute("toc", displayBean);

			ArrayList sections = displayBean.getSections();

			request.setAttribute("sectionNum", sections.size() + "");
			if (!sections.isEmpty()) {
				SectionBean firstSec = (SectionBean) sections.get(0);
				sectionId = firstSec.getId();
			} else {
				addPageMessage(getResPage().getString("there_are_no_sections_ins_this_CRF"), request);
				forwardPage(Page.LIST_STUDY_SUBJECTS_SERVLET, request, response);
				return;
			}
		} else if (crfVersionId > 0) {
			// for viewing blank CRF
			DisplayTableOfContentsBean displayBean = getDisplayBeanByCrfVersionId(crfVersionId);
			request.setAttribute("toc", displayBean);
			ArrayList sections = displayBean.getSections();

			request.setAttribute("sectionNum", sections.size() + "");
			if (!sections.isEmpty()) {
				SectionBean firstSec = (SectionBean) sections.get(0);
				sectionId = firstSec.getId();
			} else {
				addPageMessage(getResPage().getString("there_are_no_sections_ins_this_CRF_version"), request);
				if (eventCRFId == 0) {
					forwardPage(Page.CRF_LIST_SERVLET, request, response);
				} else {
					forwardPage(Page.LIST_STUDY_SUBJECTS_SERVLET, request, response);
				}
				return;
			}

		}

		sb = (SectionBean) sdao.findByPK(sectionId);
		if (eventCRFId == 0) {
			ecb = new EventCRFBean();
			ecb.setCRFVersionId(sb.getCRFVersionId());
			if (currentStudy.getParentStudyId() > 0) {
				// this is a site,find parent
				StudyDAO studydao = getStudyDAO();
				StudyBean parentStudy = (StudyBean) studydao.findByPK(currentStudy.getParentStudyId());
				request.setAttribute("studyTitle", parentStudy.getName());
				request.setAttribute("siteTitle", currentStudy.getName());
			} else {
				request.setAttribute("studyTitle", currentStudy.getName());
			}

		} else {
			ecb = (EventCRFBean) ecdao.findByPK(eventCRFId);

			request.setAttribute(INPUT_EVENT_CRF, ecb);
			request.setAttribute("eventCRF", ecb);
			request.setAttribute(SECTION_BEAN, sb);

			// This is the StudySubjectBean
			StudySubjectBean sub = (StudySubjectBean) studySubjectDao.findByPK(ecb.getStudySubjectId());
			// This is the SubjectBean
			SubjectDAO subjectDao = getSubjectDAO();
			int subjectId = sub.getSubjectId();
			int studyId = sub.getStudyId();
			SubjectBean subject = (SubjectBean) subjectDao.findByPK(subjectId);
			// Check for a null currentStudy
			// Let us process the age
			if (currentStudy.getStudyParameterConfig().getCollectDob().equals("1")) {
				StudyEventBean se = (StudyEventBean) studyEventDao.findByPK(ecb.getStudyEventId());
				StudyEventDefinitionDAO seddao = getStudyEventDefinitionDAO();
				StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao.findByPK(se
						.getStudyEventDefinitionId());
				se.setStudyEventDefinition(sed);
				request.setAttribute("studyEvent", se);

				// Enrollment-date is used for computing age
				age = Utils.getInstance().processAge(sub.getEnrollmentDate(), subject.getDateOfBirth());
			}
			// Get the study then the parent study
			StudyDAO studydao = getStudyDAO();
			StudyBean study = (StudyBean) studydao.findByPK(studyId);

			if (study.getParentStudyId() > 0) {
				// this is a site,find parent
				StudyBean parentStudy = (StudyBean) studydao.findByPK(study.getParentStudyId());
				request.setAttribute("studyTitle", parentStudy.getName());
				request.setAttribute("siteTitle", study.getName());
			} else {
				request.setAttribute("studyTitle", study.getName());
			}

			StudyBean subjectStudy = getStudyService().getSubjectStudy(currentStudy, sub);
			request.setAttribute("subjectStudy", subjectStudy);
			request.setAttribute("studySubject", sub);
			request.setAttribute("subject", subject);
			request.setAttribute("age", age);

		}

		request.setAttribute("eventCrfDoesNotHaveOutstandingDNs",
				ecb.getId() <= 0 || discrepancyNoteDao.doesNotHaveOutstandingDNs(ecb));

		boolean hasItemGroup = false;
		// we will look into db to see if any repeating items for this CRF
		// section
		ItemGroupDAO igdao = getItemGroupDAO();
		List<ItemGroupBean> itemGroups = igdao.findLegitGroupBySectionId(sectionId);
		if (!itemGroups.isEmpty()) {
			hasItemGroup = true;

		}

		// if the List of DisplayFormGroups is empty, then the servlet defers to
		// the prior method
		// of generating a DisplaySectionBean for the application

		DisplaySectionBean dsb;
		// want to get displayBean with grouped and ungrouped items
		request.setAttribute(EVENT_DEF_CRF_BEAN, edcb);
		request.setAttribute(INPUT_EVENT_CRF, ecb);
		request.setAttribute(SECTION_BEAN, sb);
		dsb = super.getDisplayBean(hasItemGroup, request, false);

		FormDiscrepancyNotes discNotes = (FormDiscrepancyNotes) session
				.getAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
		if (discNotes == null) {
			discNotes = new FormDiscrepancyNotes();
			session.setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, discNotes);
		}

		List<DisplayItemWithGroupBean> displayItemWithGroups = getDisplayItemService(getServletContext()).createItemWithGroups(dsb, hasItemGroup,
				eventDefinitionCRFId, request);
		dsb.setDisplayItemGroups(displayItemWithGroups);

		super.populateNotesWithDBNoteCounts(discNotes, noteThreads, dsb, request);

		if (fp.getString("fromViewNotes") != null && "1".equals(fp.getString("fromViewNotes"))) {
			request.setAttribute("fromViewNotes", fp.getString("fromViewNotes"));
		} else {
			session.removeAttribute("viewNotesURL");
		}

		if ("saveNotes".equalsIgnoreCase(action)) {
			LOGGER.info("33333how many group rows:" + dsb.getDisplayItemGroups().size());
			DiscrepancyNoteService dnService = new DiscrepancyNoteService(getDataSource());
			// let's save notes for the blank items
			discNotes = (FormDiscrepancyNotes) session.getAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);

			for (int i = 0; i < dsb.getDisplayItemGroups().size(); i++) {
				DisplayItemWithGroupBean diwb = dsb.getDisplayItemGroups().get(i);

				if (diwb.isInGroup()) {
					List<DisplayItemGroupBean> dgbs = diwb.getItemGroups();
					LOGGER.info("dgbs size: " + dgbs.size());
					for (int j = 0; j < dgbs.size(); j++) {
						DisplayItemGroupBean displayGroup = dgbs.get(j);
						List<DisplayItemBean> items = displayGroup.getItems();
						LOGGER.info("item size: " + items.size());
						for (DisplayItemBean displayItem : items) {
							String inputName = DataEntryUtil.getGroupItemInputName(displayGroup, j, displayItem);
							LOGGER.info("inputName:" + inputName);
							LOGGER.info("item data id:" + displayItem.getData().getId());
							dnService.saveFieldNotes(inputName, discNotes, displayItem.getData().getId(), "itemData",
									currentStudy);

						}
					}

				} else {
					DisplayItemBean dib = diwb.getSingleItem();
					// TODO work on this line

					String inputName = DataEntryUtil.getInputName(dib);
					dnService.saveFieldNotes(inputName, discNotes, dib.getData().getId(), DiscrepancyNoteBean.ITEM_DATA,
							currentStudy);

					ArrayList childItems = dib.getChildren();
					for (Object childItem : childItems) {
						DisplayItemBean child = (DisplayItemBean) childItem;
						inputName = DataEntryUtil.getInputName(child);
						dnService.saveFieldNotes(inputName, discNotes, dib.getData().getId(),
								DiscrepancyNoteBean.ITEM_DATA, currentStudy);
					}
				}
			}

			addPageMessage("Discrepancy notes are saved successfully.", request);
			request.setAttribute("id", studySubjectId + "");
			forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET, request, response);
		} else {
			request.setAttribute(BEAN_DISPLAY, dsb);
			request.setAttribute(BEAN_ANNOTATIONS, ecb.getAnnotations());
			request.setAttribute("sec", sb);
			request.setAttribute("EventCRFBean", ecb);

			int tabNum;
			if ("".equalsIgnoreCase(fp.getString("tabId"))) {
				tabNum = 1;
			} else {
				tabNum = fp.getInt("tabId");
			}
			request.setAttribute("tabId", Integer.toString(tabNum));

			// Signal interviewer.jsp that the containing page is
			// viewSectionData,
			// for the purpose of suppressing discrepancy note icons for the
			// interview date and name fields
			request.setAttribute(ENCLOSING_PAGE, "viewSectionData");

			if ("yes".equalsIgnoreCase(printVersion)) {
				forwardPage(Page.VIEW_SECTION_DATA_ENTRY_PRINT, request, response);
			} else {
				forwardPage(Page.VIEW_SECTION_DATA_ENTRY, request, response);
			}
		}
	}

	/**
	 * Current User may access a requested event CRF in the current user's studies.
	 *
	 * @param eventCrfNotes
	 *            List<DiscrepancyNoteBean>
	 * @param request
	 *            HttpServletRequest
	 */

	private void setAttributeForInterviewerDNotes(List<DiscrepancyNoteBean> eventCrfNotes, HttpServletRequest request) {
		for (DiscrepancyNoteBean dnBean : eventCrfNotes) {

			if (INTERVIEWER_NAME.equalsIgnoreCase(dnBean.getColumn())) {
				request.setAttribute("hasNameNote", "yes");
				request.setAttribute(INTERVIEWER_NAME_NOTE, dnBean);

			}
			if (DATE_INTERVIEWED.equalsIgnoreCase(dnBean.getColumn())) {
				request.setAttribute("hasDateNote", "yes");
				request.setAttribute(INTERVIEWER_DATE_NOTE, dnBean);
			}
		}
	}

}
