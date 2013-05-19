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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.akaza.openclinica.bean.core.ResolutionStatus;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.core.Utils;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
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
import org.akaza.openclinica.control.form.DiscrepancyValidator;
import org.akaza.openclinica.control.form.FormDiscrepancyNotes;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.submit.AddNewSubjectServlet;
import org.akaza.openclinica.control.submit.DataEntryServlet;
import org.akaza.openclinica.control.submit.SubmitDataServlet;
import org.akaza.openclinica.control.submit.TableOfContentsServlet;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemGroupDAO;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.service.DiscrepancyNoteThread;
import org.akaza.openclinica.service.DiscrepancyNoteUtil;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

/**
 *  View a CRF version section data entry
 *  
 * @author jxu
 */
@SuppressWarnings({"unchecked", "rawtypes", "serial"})
public class ViewSectionDataEntryServlet extends DataEntryServlet {

	public static final String VIEW_SECTION_DATA_ENTRY_REFERER = "viewSectionDataEntryReferer";
	public static final String REFERER = "referer";
	public static final String VIEW_SECTION_DATA_ENTRY = "ViewSectionDataEntry";
	public static final String RESOLVE_DISCREPANCY = "ResolveDiscrepancy";
	public static final String JUST_CLOSE_WINDOW = "justCloseWindow";
	Locale locale;
	public static String EVENT_CRF_ID = "ecId";
	public static String ENCLOSING_PAGE = "enclosingPage";

	/**
	 * Checks whether the user has the correct privilege
	 */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		mayAccess(request);
		UserAccountBean ub = (UserAccountBean) request.getSession().getAttribute(USER_BEAN_NAME);
		StudyUserRoleBean currentRole = (StudyUserRoleBean) request.getSession().getAttribute("userRole");
		locale = request.getLocale();

		if (ub.isSysAdmin()) {
			return;
		}
		if (SubmitDataServlet.mayViewData(ub, currentRole)) {
			return;
		}

		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study") + " "
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_director"), "1");

	}

	public Map getErrors() {
		return errors;
	}

	public void initializeMembers(Locale locale) {
		resadmin = ResourceBundleProvider.getAdminBundle(locale);
		resaudit = ResourceBundleProvider.getAuditEventsBundle(locale);
		resexception = ResourceBundleProvider.getExceptionsBundle(locale);
		resformat = ResourceBundleProvider.getFormatBundle(locale);
		restext = ResourceBundleProvider.getTextsBundle(locale);
		resterm = ResourceBundleProvider.getTermsBundle(locale);
		resword = ResourceBundleProvider.getWordsBundle(locale);
		respage = ResourceBundleProvider.getPageMessagesBundle(locale);
		resworkflow = ResourceBundleProvider.getWorkflowBundle(locale);

		local_df = new SimpleDateFormat(resformat.getString("date_format_string"), ResourceBundleProvider.getLocale());
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		FormProcessor fp = new FormProcessor(request);

		String cw = request.getParameter("cw");
		if (cw != null) {
			request.setAttribute(JUST_CLOSE_WINDOW, true);
		} else {
			String referer = request.getHeader(REFERER);
			if (referer != null && !referer.contains(VIEW_SECTION_DATA_ENTRY) && !referer.contains(RESOLVE_DISCREPANCY)) {
				request.getSession().setAttribute(VIEW_SECTION_DATA_ENTRY_REFERER, referer);
			}
		}

		StudyBean currentStudy = (StudyBean) request.getSession().getAttribute("study");
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);

		SectionBean sb = (SectionBean) request.getAttribute(SECTION_BEAN);
		boolean isSubmitted = false;
		EventDefinitionCRFBean edcb = (EventDefinitionCRFBean) request.getAttribute(EVENT_DEF_CRF_BEAN);
		if (!fp.getString("exitTo").equals("")) {
			request.setAttribute("exitTo", fp.getString("exitTo"));
		}
		int crfVersionId = fp.getInt("crfVersionId", true);
		int sectionId = fp.getInt("sectionId");
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
			CRFVersionDAO crfVDao = new CRFVersionDAO(getDataSource());
			CRFVersionBean crvVBean = (CRFVersionBean) crfVDao.findByPK(crfVersionId);
			if (crvVBean != null) {
				crfId = crvVBean.getCrfId();
			}
		}

		int eventDefinitionCRFId = fp.getInt("eventDefinitionCRFId");
		EventDefinitionCRFDAO eventCrfDao = new EventDefinitionCRFDAO(getDataSource());
		edcb = (EventDefinitionCRFBean) eventCrfDao.findByPK(eventDefinitionCRFId);
		if (eventCRFId == 0
				&& (edcb.getStudyId() != currentStudy.getParentStudyId() && edcb.getStudyId() != currentStudy.getId())) {
			addPageMessage(
					respage.getString("no_have_correct_privilege_current_study") + " "
							+ respage.getString("change_study_contact_sysadmin"), request);
			throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_director"), "1");
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

		EventCRFDAO ecdao = new EventCRFDAO(getDataSource());
		SectionDAO sdao = new SectionDAO(getDataSource());
		String age = "";
		if (sectionId == 0 && crfVersionId == 0 && eventCRFId == 0) {
			addPageMessage(respage.getString("please_choose_a_CRF_to_view"), request);
			forwardPage(Page.LIST_STUDY_SUBJECTS_SERVLET, request, response);
			return;
		}
		if (studySubjectId > 0) {
			StudySubjectDAO ssdao = new StudySubjectDAO(getDataSource());
			StudySubjectBean sub = (StudySubjectBean) ssdao.findByPK(studySubjectId);
			request.setAttribute("studySubject", sub);
		}

		if (eventCRFId > 0) {
			// for event crf, the input crfVersionId from url =0
			ecb = (EventCRFBean) ecdao.findByPK(eventCRFId);

			StudyEventDAO sedao = new StudyEventDAO(getDataSource());
			StudyEventBean event = (StudyEventBean) sedao.findByPK(ecb.getStudyEventId());
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
			DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(getDataSource());
			ArrayList<DiscrepancyNoteBean> allNotes = new ArrayList<DiscrepancyNoteBean>();
			List<DiscrepancyNoteBean> eventCrfNotes = new ArrayList<DiscrepancyNoteBean>();
			List<DiscrepancyNoteThread> noteThreads = new ArrayList<DiscrepancyNoteThread>();

			allNotes = dndao.findAllTopNotesByEventCRF(eventCRFId);
			// add interviewer.jsp related notes to this Collection
			eventCrfNotes = dndao.findOnlyParentEventCRFDNotesFromEventCRF(ecb);
			if (!eventCrfNotes.isEmpty()) {
				allNotes.addAll(eventCrfNotes);
				// make sure a necessary request attribute "hasNameNote" is set
				// properly
				this.setAttributeForInterviewerDNotes(eventCrfNotes, request);
			}
			// Create disc note threads out of the various notes
			DiscrepancyNoteUtil dNoteUtil = new DiscrepancyNoteUtil();
			noteThreads = dNoteUtil.createThreadsOfParents(allNotes, getDataSource(), currentStudy, null, -1, true);
			// variables that provide values for the CRF discrepancy note header
			int updatedNum = 0;
			int openNum = 0;
			int closedNum = 0;
			int resolvedNum = 0;
			int notAppNum = 0;
			DiscrepancyNoteBean tempBean;
			for (DiscrepancyNoteThread dnThread : noteThreads) {
				//Do not count parent beans, only the last child disc note of the thread.
				tempBean = dnThread.getLinkedNoteList().getLast();
				if (tempBean != null) {
					if (ResolutionStatus.UPDATED.equals(tempBean.getResStatus())) {
						updatedNum++;
					} else if (ResolutionStatus.OPEN.equals(tempBean.getResStatus())) {
						openNum++;
					} else if (ResolutionStatus.CLOSED.equals(tempBean.getResStatus())) {
						closedNum++;
					} else if (ResolutionStatus.RESOLVED.equals(tempBean.getResStatus())) {
						resolvedNum++;
					} else if (ResolutionStatus.NOT_APPLICABLE.equals(tempBean.getResStatus())) {
						notAppNum++;
					}
				}

			}
			request.setAttribute("updatedNum", updatedNum + "");
			request.setAttribute("openNum", openNum + "");
			request.setAttribute("closedNum", closedNum + "");
			request.setAttribute("resolvedNum", resolvedNum + "");
			request.setAttribute("notAppNum", notAppNum + "");

			DisplayTableOfContentsBean displayBean = TableOfContentsServlet.getDisplayBean(ecb, getDataSource(),
					currentStudy);
			
			Date tmpDate = displayBean.getEventCRF().getDateInterviewed();
			String formattedInterviewerDate;
			try {
				formattedInterviewerDate = local_df.format(tmpDate);
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
				if (sectionId == 0) {
					SectionBean firstSec = (SectionBean) sections.get(0);
					sectionId = firstSec.getId();
				}
			} else {
				addPageMessage(respage.getString("there_are_no_sections_ins_this_CRF"), request);
				forwardPage(Page.LIST_STUDY_SUBJECTS_SERVLET, request, response);
				return;
			}
		} else if (crfVersionId > 0) {// for viewing blank CRF
			DisplayTableOfContentsBean displayBean = ViewTableOfContentServlet.getDisplayBean(getDataSource(),
					crfVersionId);
			request.setAttribute("toc", displayBean);
			ArrayList sections = displayBean.getSections();

			request.setAttribute("sectionNum", sections.size() + "");
			if (!sections.isEmpty()) {
				if (sectionId == 0) {
					SectionBean firstSec = (SectionBean) sections.get(0);
					sectionId = firstSec.getId();
				}
			} else {
				addPageMessage(respage.getString("there_are_no_sections_ins_this_CRF_version"), request);
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
				StudyDAO studydao = new StudyDAO(getDataSource());
				StudyBean parentStudy = (StudyBean) studydao.findByPK(currentStudy.getParentStudyId());
				request.setAttribute("studyTitle", parentStudy.getName());
				request.setAttribute("siteTitle", currentStudy.getName());
			} else {
				request.setAttribute("studyTitle", currentStudy.getName());
			}

		} else {
			ecb = (EventCRFBean) ecdao.findByPK(eventCRFId);

			request.setAttribute(INPUT_EVENT_CRF, ecb);
			request.setAttribute(SECTION_BEAN, sb);

			// This is the StudySubjectBean
			StudySubjectDAO ssdao = new StudySubjectDAO(getDataSource());
			StudySubjectBean sub = (StudySubjectBean) ssdao.findByPK(ecb.getStudySubjectId());
			// This is the SubjectBean
			SubjectDAO subjectDao = new SubjectDAO(getDataSource());
			int subjectId = sub.getSubjectId();
			int studyId = sub.getStudyId();
			SubjectBean subject = (SubjectBean) subjectDao.findByPK(subjectId);
			// Check for a null currentStudy
			// Let us process the age
			if (currentStudy.getStudyParameterConfig().getCollectDob().equals("1")) {
				StudyEventDAO sedao = new StudyEventDAO(getDataSource());
				StudyEventBean se = (StudyEventBean) sedao.findByPK(ecb.getStudyEventId());
				StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(getDataSource());
				StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao.findByPK(se
						.getStudyEventDefinitionId());
				se.setStudyEventDefinition(sed);
				request.setAttribute("studyEvent", se);

				// Enrollment-date is used for computing age
				age = Utils.getInstance().processAge(sub.getEnrollmentDate(), subject.getDateOfBirth());
			}
			// Get the study then the parent study
			StudyDAO studydao = new StudyDAO(getDataSource());
			StudyBean study = (StudyBean) studydao.findByPK(studyId);

			if (study.getParentStudyId() > 0) {
				// this is a site,find parent
				StudyBean parentStudy = (StudyBean) studydao.findByPK(study.getParentStudyId());
				request.setAttribute("studyTitle", parentStudy.getName());
				request.setAttribute("siteTitle", study.getName());
			} else {
				request.setAttribute("studyTitle", study.getName());
			}

			request.setAttribute("studySubject", sub);
			request.setAttribute("subject", subject);
			request.setAttribute("age", age);

		}

		boolean hasItemGroup = false;
		// we will look into db to see if any repeating items for this CRF
		// section
		ItemGroupDAO igdao = new ItemGroupDAO(getDataSource());
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
		dsb = super.getDisplayBean(hasItemGroup, false, request, isSubmitted);

		FormDiscrepancyNotes discNotes = (FormDiscrepancyNotes) session
				.getAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
		if (discNotes == null) {
			discNotes = new FormDiscrepancyNotes();
			session.setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, discNotes);
		}

		List<DisplayItemWithGroupBean> displayItemWithGroups = super.createItemWithGroups(dsb, hasItemGroup,
				eventDefinitionCRFId, request);
		dsb.setDisplayItemGroups(displayItemWithGroups);

		super.populateNotesWithDBNoteCounts(discNotes, dsb, request);

		if (fp.getString("fromViewNotes") != null && "1".equals(fp.getString("fromViewNotes"))) {
			request.setAttribute("fromViewNotes", fp.getString("fromViewNotes"));
		} else {
			session.removeAttribute("viewNotesURL");
		}

		if ("saveNotes".equalsIgnoreCase(action)) {
			logger.info("33333how many group rows:" + dsb.getDisplayItemGroups().size());

			// let's save notes for the blank items
			DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(getDataSource());
			discNotes = (FormDiscrepancyNotes) session.getAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);

			for (int i = 0; i < dsb.getDisplayItemGroups().size(); i++) {
				DisplayItemWithGroupBean diwb = dsb.getDisplayItemGroups().get(i);

				if (diwb.isInGroup()) {
					List<DisplayItemGroupBean> dgbs = diwb.getItemGroups();
					logger.info("dgbs size: " + dgbs.size());
					for (int j = 0; j < dgbs.size(); j++) {
						DisplayItemGroupBean displayGroup = dgbs.get(j);
						List<DisplayItemBean> items = displayGroup.getItems();
						logger.info("item size: " + items.size());
						for (DisplayItemBean displayItem : items) {
							String inputName = getGroupItemInputName(displayGroup, j, displayItem);
							logger.info("inputName:" + inputName);
							logger.info("item data id:" + displayItem.getData().getId());
							AddNewSubjectServlet.saveFieldNotes(inputName, discNotes, dndao, displayItem.getData()
									.getId(), "itemData", currentStudy);

						}
					}

				} else {
					DisplayItemBean dib = diwb.getSingleItem();
					// TODO work on this line

					String inputName = getInputName(dib);
					AddNewSubjectServlet.saveFieldNotes(inputName, discNotes, dndao, dib.getData().getId(), "ItemData",
							currentStudy);

					ArrayList childItems = dib.getChildren();
					for (int j = 0; j < childItems.size(); j++) {
						DisplayItemBean child = (DisplayItemBean) childItems.get(j);
						inputName = getInputName(child);
						AddNewSubjectServlet.saveFieldNotes(inputName, discNotes, dndao, dib.getData().getId(),
								"ItemData", currentStudy);

					}
				}
			}

			addPageMessage("Discrepancy notes are saved successfully.", request);
			request.setAttribute("id", studySubjectId + "");
			forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET, request, response);
			return;
		} else {
			request.setAttribute(BEAN_DISPLAY, dsb);
			request.setAttribute(BEAN_ANNOTATIONS, ecb.getAnnotations());
			request.setAttribute("sec", sb);
			request.setAttribute("EventCRFBean", ecb);

			int tabNum = 1;
			if ("".equalsIgnoreCase(fp.getString("tabId"))) {
				tabNum = 1;
			} else {
				tabNum = fp.getInt("tabId");
			}
			request.setAttribute("tabId", new Integer(tabNum).toString());

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
		return Page.VIEW_SECTION_DATA_ENTRY;
	}

	@Override
	protected Page getServletPage(HttpServletRequest request) {
		return Page.VIEW_SECTION_DATA_ENTRY_SERVLET;
	}

	@Override
	protected boolean validateInputOnFirstRound() {
		return true;
	}

	 /** 
	 * @see org.akaza.openclinica.control.submit.DataEntryServlet#validateDisplayItemBean
	 * (org.akaza.openclinica.core.form.Validator, org.akaza.openclinica.bean.submit.DisplayItemBean)
	 */
	@Override
	protected DisplayItemBean validateDisplayItemBean(DiscrepancyValidator v, DisplayItemBean dib, String inputName,
			HttpServletRequest request) {
		org.akaza.openclinica.bean.core.ResponseType rt = dib.getMetadata().getResponseSet().getResponseType();

		// note that this step sets us up both for
		// displaying the data on the form again, in the event of an error
		// and sending the data to the database, in the event of no error
		dib = loadFormValue(dib, request);

		// types TEL and ED are not supported yet
		if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.TEXT)
				|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.TEXTAREA)
				|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.CALCULATION)
				|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.GROUP_CALCULATION)) {
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

		return formGroups;

	}

	@Override
	protected boolean shouldLoadDBValues(DisplayItemBean dib) {
		return true;
	}

	/**
	 * Current User may access a requested event CRF in the current user's studies
	 * 
	 * @param request
	 *            TODO
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

	@Override
	protected boolean shouldRunRules() {
		return false;
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
