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

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.akaza.openclinica.bean.admin.AuditBean;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.DiscrepancyNoteType;
import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.ItemDataType;
import org.akaza.openclinica.bean.core.ResolutionStatus;
import org.akaza.openclinica.bean.core.ResponseType;
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
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.DisplayItemGroupBean;
import org.akaza.openclinica.bean.submit.DisplayItemWithGroupBean;
import org.akaza.openclinica.bean.submit.DisplaySectionBean;
import org.akaza.openclinica.bean.submit.DisplayTableOfContentsBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.ItemGroupBean;
import org.akaza.openclinica.bean.submit.ResponseOptionBean;
import org.akaza.openclinica.bean.submit.ResponseSetBean;
import org.akaza.openclinica.bean.submit.SCDItemDisplayInfo;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.DiscrepancyValidator;
import org.akaza.openclinica.control.form.FormDiscrepancyNotes;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.RuleValidator;
import org.akaza.openclinica.control.form.ScoreItemValidator;
import org.akaza.openclinica.control.form.Validation;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.core.EmailEngine;
import org.akaza.openclinica.core.SecurityManager;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.AuditDAO;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.hibernate.ConfigurationDao;
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
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.dao.submit.ItemGroupDAO;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.domain.SourceDataVerification;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.action.RuleActionRunBean.Phase;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.logic.expressionTree.ExpressionTreeHelper;
import org.akaza.openclinica.logic.rulerunner.MessageContainer.MessageType;
import org.akaza.openclinica.logic.score.ScoreCalculator;
import org.akaza.openclinica.navigation.Navigation;
import org.akaza.openclinica.service.DiscrepancyNoteThread;
import org.akaza.openclinica.service.DiscrepancyNoteUtil;
import org.akaza.openclinica.service.calendar.CalendarLogic;
import org.akaza.openclinica.service.crfdata.DynamicsMetadataService;
import org.akaza.openclinica.service.crfdata.InstantOnChangeService;
import org.akaza.openclinica.service.crfdata.SimpleConditionalDisplayService;
import org.akaza.openclinica.service.crfdata.front.InstantOnChangeFrontStrGroup;
import org.akaza.openclinica.service.crfdata.front.InstantOnChangeFrontStrParcel;
import org.akaza.openclinica.service.managestudy.DiscrepancyNoteService;
import org.akaza.openclinica.service.rule.RuleSetService;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.akaza.openclinica.web.InconsistentStateException;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.SQLInitServlet;
import org.quartz.impl.StdScheduler;

import com.clinovo.enums.CurrentDataEntryStage;
import com.clinovo.i18n.LocaleResolver;
import com.clinovo.model.EventCRFSectionBean;
import com.clinovo.service.DataEntryService;
import com.clinovo.service.DisplayItemService;
import com.clinovo.service.EventCRFSectionService;
import com.clinovo.service.ReportCRFService;
import com.clinovo.util.CrfShortcutsAnalyzer;
import com.clinovo.util.DAOWrapper;
import com.clinovo.util.DataEntryRenderUtil;
import com.clinovo.util.DataEntryUtil;
import com.clinovo.util.DateUtil;
import com.clinovo.util.EmailUtil;
import com.clinovo.util.SubjectEventStatusUtil;
import com.clinovo.util.ValidatorHelper;
import com.clinovo.validation.DisplayItemBeanValidator;
import com.clinovo.validator.CodedTermValidator;

/**
 * Data Entry Servlet.
 */
@SuppressWarnings({"all"})
public abstract class DataEntryServlet extends Controller {

	public static final String DATA_ENTRY_CURRENT_CRF_VERSION_OID = "dataEntryCurrentCrfVersionOid";
	public static final String DATA_ENTRY_CURRENT_CRF_OID = "dataEntryCurrentCrfOid";
	public static final String ACTION = "action";
	public static final String INPUT_EVENT_CRF_ID = "eventCRFId";
	public static final String INPUT_SECTION_ID = "sectionId";
	public static final String INPUT_EVENT_CRF = "event";
	public static final String INPUT_SECTION = "section";
	public static final String INPUT_IGNORE_PARAMETERS = "ignore";
	public static final String INPUT_CHECK_INPUTS = "checkInputs";
	public static final String INPUT_ANNOTATIONS = "annotations";
	public static final String BEAN_ANNOTATIONS = "annotations";
	public static final String RESUME_LATER = "submittedResume";
	public static final String SAVE_NEXT = "saveAndNext";
	public static final String BEAN_DISPLAY = "section";
	public static final String TOC_DISPLAY = "toc";
	public static final String INPUT_INTERVIEWER = "interviewer";
	public static final String INPUT_INTERVIEW_DATE = "interviewDate";
	public static final String INTERVIEWER_NAME_NOTE = "InterviewerNameNote";
	public static final String INTERVIEWER_DATE_NOTE = "InterviewerDateNote";
	public static final String INPUT_TAB = "tabId";
	public static final String INPUT_MARK_COMPLETE = "markComplete";
	
	public static final String INPUT_MARK_PARTIAL_SAVED = "markPartialSaved";

	public static final String VALUE_YES = "Yes";
	public static final String INPUT_EVENT_DEFINITION_CRF_ID = "eventDefinitionCRFId";
	public static final String INPUT_CRF_VERSION_ID = "crfVersionId";
	public static final String INPUT_STUDY_EVENT_ID = "studyEventId";
	public static final String INPUT_SUBJECT_ID = "subjectId";
	public static final String GO_EXIT = "submittedExit";
	public static final String GROUP_HAS_DATA = "groupHasData";
	public static final String HAS_DATA_FLAG = "hasDataFlag";
	public static final String DDE_PROGESS = "doubleDataProgress";
	public static final String INTERVIEWER_NAME = "interviewer_name";
	public static final String DATE_INTERVIEWED = "date_interviewed";
	public static final String NOTE_SUBMITTED = "note_submitted";
	public static final String SECTION_BEAN = "section_bean";
	public static final String ALL_SECTION_BEANS = "all_section_bean";
	public static final String EVENT_DEF_CRF_BEAN = "event_def_crf_bean";
	public static final String ALL_ITEMS_LIST = "all_items_list";
	public static final String CV_INSTANT_META = "cvInstantMeta";
	private static final String DNS_TO_TRANSFORM = "listOfDNsToTransform";
	private static final String WARNINGS_LIST = "warningsIsDisplayed";
	public static final String DN_ADDITIONAL_CR_PARAMS = "dnAdditionalCreatingParameters";
	public static final String DATA_ENTRY_STAGE = "dataEntryStage";

	public static final int INT_3800 = 3800;
	public static final int INT_255 = 255;
	public static final int INT_3 = 3;
	public static final int INT_4 = 4;

	private DisplayItemBeanValidator displayItemBeanValidator;

	@Override
	protected abstract void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException;

	private String getSectionFirstFieldId(int sectionId) {

		ItemDAO itemDAO = new ItemDAO(getDataSource());
		List<ItemBean> items = itemDAO.findAllBySectionId(sectionId);
		if (!items.isEmpty()) {
			return new Integer(items.get(0).getId()).toString();
		}
		return "";
	}

	private void logMe(String message) {
		logger.trace(message);
	}

	private void prepareSessionNotesIfValidationsWillFail(HttpServletRequest request, boolean hasGroup,
														  boolean isSubmitted, List<DiscrepancyNoteBean> allNotes) {
		try {
			if (request.getMethod().equalsIgnoreCase("POST") && request.getAttribute("section") == null) {
				request.setAttribute("section", getDisplayBean(hasGroup, request, isSubmitted).getSection());
				FormDiscrepancyNotes fdn = (FormDiscrepancyNotes) request.getSession().getAttribute(
						AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
				if (fdn != null) {
					for (Object list : fdn.getFieldNotes().values()) {
						for (DiscrepancyNoteBean discrepancyNoteBean : (List<DiscrepancyNoteBean>) list) {
							if (discrepancyNoteBean.getId() == 0) {
								allNotes.add(discrepancyNoteBean);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("An error has occured in the prepareSessionNotesIfValidationsWillFail method.", e);
		}
	}

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Locale locale = LocaleResolver.getLocale(request);
		FormProcessor fp = new FormProcessor(request);
		String action = fp.getString(ACTION);
		DynamicsMetadataService dynamicsMetadataService = getDynamicsMetadataService();
		DisplayItemService displayItemService = getDisplayItemService(getServletContext());
		if (request.getMethod().equalsIgnoreCase("POST") && action.equalsIgnoreCase("ide_s")) {
			request.getSession().setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME,
					new FormDiscrepancyNotes());
		}

		ConfigurationDao configurationDao = SpringServletAccess.getApplicationContext(
				request.getSession().getServletContext()).getBean(ConfigurationDao.class);
		ValidatorHelper validatorHelper = new ValidatorHelper(request, configurationDao);

		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);
		SectionBean sb = (SectionBean) request.getAttribute(SECTION_BEAN);

		putDataEntryStageFlagToRequest(request);
		ItemFormMetadataDAO ifmdao = new ItemFormMetadataDAO(getDataSource());
		ItemDataDAO iddao = new ItemDataDAO(getDataSource(), locale);
		EventCRFDAO ecdao = new EventCRFDAO(getDataSource());
		SectionDAO sdao = new SectionDAO(getDataSource());

		HttpSession session = request.getSession();
		StudyBean currentStudy = (StudyBean) session.getAttribute("study");
		StudyUserRoleBean currentRole = (StudyUserRoleBean) session.getAttribute("userRole");

		boolean isSubmitted = false;
		boolean hasGroup = false;

		logMe("Enterting DataEntry Servlet" + System.currentTimeMillis());
		EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(getDataSource());
		
		StudyInfoPanel panel = getStudyInfoPanel(request);
		panel.setStudyInfoShown(false);
		String age = "";
		UserAccountBean ub = (UserAccountBean) request.getSession().getAttribute(USER_BEAN_NAME);

		int isFirstTimeOnSection = fp.getInt("isFirstTimeOnSection");
		request.setAttribute("isFirstTimeOnSection", isFirstTimeOnSection + "");
		request.setAttribute("currentDataEntryStage", getCurrentDataEntryStage());

		boolean headerRequiredField = currentStudy.getStudyParameterConfig().getInterviewerNameRequired().equals("yes")
				|| currentStudy.getStudyParameterConfig().getInterviewDateRequired().equals("yes");
		request.setAttribute("expandCrfInfo", headerRequiredField);
		if (fp.getString(GO_EXIT).equals("") && !isSubmitted && fp.getString("tabId").equals("")
				&& fp.getString("sectionId").equals("")) {
			if (getUnavailableCRFList().containsKey(ecb.getId())) {
				int userId = (Integer) getUnavailableCRFList().get(ecb.getId());
				UserAccountDAO udao = new UserAccountDAO(getDataSource());
				UserAccountBean ubean = (UserAccountBean) udao.findByPK(userId);
				addPageMessage(
						resword.getString("CRF_unavailable") + " " + ubean.getName() + " "
								+ resword.getString("Currently_entering_data") + " "
								+ resword.getString("Leave_the_CRF"), request);

				forwardPage(Page.LIST_STUDY_SUBJECTS_SERVLET, request, response);
			} else {
				lockThisEventCRF(ecb.getId(), ub.getId());
			}
		}

		if (!ecb.isActive()) {
			throw new InconsistentStateException(Page.LIST_STUDY_SUBJECTS_SERVLET,
					resexception.getString("event_not_exists"));
		}

		logMe("Enterting DataEntry Get the status/number of item discrepancy notes" + System.currentTimeMillis());
		// Get the status/number of item discrepancy notes
		DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(getDataSource());
		List<DiscrepancyNoteBean> allNotes = new ArrayList<DiscrepancyNoteBean>();
		List<DiscrepancyNoteBean> eventCrfNotes = new ArrayList<DiscrepancyNoteBean>();
		List<DiscrepancyNoteThread> noteThreads = new ArrayList<DiscrepancyNoteThread>();
		dndao = new DiscrepancyNoteDAO(getDataSource());

		request.setAttribute("eventCrfDoesNotHaveOutstandingDNs",
				ecb.getId() <= 0 || dndao.doesNotHaveOutstandingDNs(ecb));

		allNotes = dndao.findAllTopNotesByEventCRF(ecb.getId());
		eventCrfNotes = dndao.findOnlyParentEventCRFDNotesFromEventCRF(ecb);

		// Filter out coder notes
		allNotes = filterNotesByUserRole(allNotes, request);
		eventCrfNotes = filterNotesByUserRole(eventCrfNotes, request);

		if (!eventCrfNotes.isEmpty()) {
			allNotes.addAll(eventCrfNotes);

		}

		DiscrepancyNoteUtil dNoteUtil = new DiscrepancyNoteUtil();
		prepareSessionNotesIfValidationsWillFail(request, hasGroup, isSubmitted, allNotes);
		noteThreads = dNoteUtil.createThreadsOfParents(allNotes, currentStudy, null, -1);

		StudySubjectDAO ssdao = new StudySubjectDAO(getDataSource());
		StudySubjectBean ssb = (StudySubjectBean) ssdao.findByPK(ecb.getStudySubjectId());
		Status s = ssb.getStatus();
		if ("removed".equalsIgnoreCase(s.getName()) || "auto-removed".equalsIgnoreCase(s.getName())) {
			addPageMessage(
					respage.getString("you_may_not_perform_data_entry_on_a_CRF")
							+ respage.getString("study_subject_has_been_deleted"), request);
			request.setAttribute("id", new Integer(ecb.getStudySubjectId()).toString());
			forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET, request, response);
		}

		HashMap<String, String> newUploadedFiles = (HashMap<String, String>) session.getAttribute("newUploadedFiles");
		if (newUploadedFiles == null) {
			newUploadedFiles = new HashMap<String, String>();
		}
		request.setAttribute("newUploadedFiles", newUploadedFiles);

		boolean autoCloseDataEntryPage = request.getParameter(CW) != null;

		if (!fp.getString(GO_EXIT).equals("")) {
			request.getSession().removeAttribute(DN_ADDITIONAL_CR_PARAMS);
			session.removeAttribute(GROUP_HAS_DATA);
			session.removeAttribute("to_create_crf");
			session.removeAttribute("mayProcessUploading");
			// Removing the user and EventCRF from the locked CRF List
			justRemoveLockedCRF(ecb.getId());
			if (newUploadedFiles.size() > 0) {
				if (!this.unloadFiles(newUploadedFiles)) {
					String missed = "";
					Iterator iter = newUploadedFiles.keySet().iterator();
					while (iter.hasNext()) {
						missed += " " + newUploadedFiles.get(iter.next());
					}
					addPageMessage(respage.getString("uploaded_files_not_deleted_or_not_exist") + ": " + missed,
							request);
				}
			}
			session.removeAttribute("newUploadedFiles");
			addPageMessage(respage.getString("exit_without_saving"), request);
			storePageMessages(request);
			if (autoCloseDataEntryPage) {
				forwardPage(Page.AUTO_CLOSE_PAGE, request, response);
			} else {
				response.sendRedirect(Navigation.getSavedUrl(request));
			}
			return;
		}

		hasGroup = checkGroups(fp, ecb);

		Boolean b = (Boolean) request.getAttribute(INPUT_IGNORE_PARAMETERS);
		isSubmitted = fp.isSubmitted() && b == null;
		// variable is used for fetching any null values like "not applicable"
		int eventDefinitionCRFId = 0;
		if (fp != null) {
			eventDefinitionCRFId = fp.getInt("eventDefinitionCRFId");
		}

		StudyDAO studydao = new StudyDAO(getDataSource());
		StudyBean subjectsStudy = (StudyBean) studydao.findByPK(ssb.getStudyId());

		if (eventDefinitionCRFId <= 0) {
			EventDefinitionCRFBean edcBean = edcdao.findByStudyEventIdAndCRFVersionId((StudyBean) studydao
					.findByPK(((StudySubjectBean) ssdao.findByPK(ecb.getStudySubjectId())).getStudyId()), ecb
					.getStudyEventId(), ecb.getCRFVersionId());
			eventDefinitionCRFId = edcBean.getId();
		}

		List<SectionBean> allSections = sdao.findAllByCRFVersionId(ecb.getCRFVersionId());
		CrfShortcutsAnalyzer crfShortcutsAnalyzer = getCrfShortcutsAnalyzer(request, getItemSDVService(), true);
		crfShortcutsAnalyzer.prepareCrfShortcutLinks(ecb, ifmdao, eventDefinitionCRFId, allSections, noteThreads);
		logMe("Entering DataEntry Create disc note threads out of the various notes DONE" + System.currentTimeMillis());

		logMe("Entering some EVENT DEF CRF CHECK DONE " + System.currentTimeMillis());
		logMe("Entering some Study EVENT DEF CRF CHECK  " + System.currentTimeMillis());
		StudyEventDAO seDao = new StudyEventDAO(getDataSource());
		EventDefinitionCRFBean edcBean = (EventDefinitionCRFBean) edcdao.findByPK(eventDefinitionCRFId);
		EventDefinitionCRFBean edcb = (EventDefinitionCRFBean) edcdao.findByPK(eventDefinitionCRFId);
		// JN:Putting the event_def_crf_bean in the request attribute.
		request.setAttribute(EVENT_DEF_CRF_BEAN, edcb);

		StudyEventBean studyEventBean = (StudyEventBean) seDao.findByPK(ecb.getStudyEventId());
		edcBean.setId(eventDefinitionCRFId);

		request.setAttribute("studyEvent", studyEventBean);
		request.setAttribute("studyEventId", studyEventBean.getId());
		request.setAttribute("eventDefinitionCRFId", eventDefinitionCRFId);

		StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(getDataSource());
		StudyEventDefinitionBean studyEventDefinition = (StudyEventDefinitionBean) seddao.findByPK(edcBean
				.getStudyEventDefinitionId());

		CRFDAO cdao = new CRFDAO(getDataSource());
		CRFVersionDAO cvdao = new CRFVersionDAO(getDataSource());
		CRFVersionBean crfVersionBean = (CRFVersionBean) cvdao.findByPK(ecb.getCRFVersionId());
		CRFBean crfBean = (CRFBean) cdao.findByPK(crfVersionBean.getCrfId());

		request.setAttribute(DATA_ENTRY_CURRENT_CRF_VERSION_OID, crfVersionBean.getOid());
		request.setAttribute(DATA_ENTRY_CURRENT_CRF_OID, crfBean.getOid());

		Phase phase2 = Phase.INITIAL_DATA_ENTRY;
		if (getServletPage(request).equals(Page.DOUBLE_DATA_ENTRY_SERVLET)) {
			phase2 = Phase.DOUBLE_DATA_ENTRY;
		} else if (getServletPage(request).equals(Page.ADMIN_EDIT_SERVLET)) {
			phase2 = Phase.ADMIN_EDITING;
		}

		DisplaySectionBean section = getDisplayBean(hasGroup, request, isSubmitted);
		if (section.getSection().hasSCDItem()) {
			SimpleConditionalDisplayService cds0 = (SimpleConditionalDisplayService) SpringServletAccess
					.getApplicationContext(getServletContext()).getBean("simpleConditionalDisplayService");
			section = cds0.initConditionalDisplays(section);
		}

		// Find out the id of the section's first field
		String firstFieldId = getSectionFirstFieldId(section.getSection().getId());
		request.setAttribute("formFirstField", firstFieldId);

		logMe("Entering  displayItemWithGroups " + System.currentTimeMillis());
		List<DisplayItemWithGroupBean> displayItemWithGroups = displayItemService.createItemWithGroups(section, hasGroup,
				eventDefinitionCRFId, request);
		logMe("Entering  displayItemWithGroups end " + System.currentTimeMillis());

		dynamicsMetadataService.updateGroupDynamicsInSection(displayItemWithGroups, section.getSection().getId(), ecb);
		section.setDisplayItemGroups(displayItemWithGroups);
		DisplayTableOfContentsBean toc = getDisplayBeanWithShownSections(
				(DisplayTableOfContentsBean) request.getAttribute(TOC_DISPLAY), dynamicsMetadataService);
		request.setAttribute(TOC_DISPLAY, toc);
		LinkedList<Integer> sectionIdsInToc = DataEntryUtil.getSectionIdsInToc(toc);

		logMe("Entering  displayItemWithGroups sdao.findPrevious  " + System.currentTimeMillis());
		int sIndex = DataEntryUtil.getSectionIndexInToc(section.getSection(), toc, sectionIdsInToc);
		SectionBean previousSec = DataEntryUtil.getPrevSection(toc, sIndex);
		logMe("Entering  displayItemWithGroups sdao.findPrevious  end " + System.currentTimeMillis());
		SectionBean nextSec = DataEntryUtil.getNextSection(toc, sIndex);
		section.setFirstSection(!previousSec.isActive());
		section.setLastSection(!nextSec.isActive());

		setPartialSaveParameters(request, ecb.getId(), section);
		
		SubjectDAO subjectDao = new SubjectDAO(getDataSource());
		SubjectBean subject = (SubjectBean) subjectDao.findByPK(ssb.getSubjectId());

		// Get the study then the parent study
		logMe("Entering  Get the study then the parent study   " + System.currentTimeMillis());
		if (subjectsStudy.getParentStudyId() > 0) {
			// this is a site,find parent
			StudyBean parentStudy = (StudyBean) studydao.findByPK(subjectsStudy.getParentStudyId());
			request.setAttribute("studyTitle", parentStudy.getName());
			request.setAttribute("siteTitle", subjectsStudy.getName());
		} else {
			request.setAttribute("studyTitle", subjectsStudy.getName());
		}

		provideRandomizationStatisticsForSite(request);

		logMe("Entering  Get the study then the parent study end  " + System.currentTimeMillis());
		// Let us process the age
		if (currentStudy.getStudyParameterConfig().getCollectDob().equals("1")) {
			Date enrollmentDate = ssb.getEnrollmentDate();
			age = Utils.getInstance().processAge(enrollmentDate, subject.getDateOfBirth());
		}
		ArrayList beans = getDisplayStudyEventsForStudySubject(ssb, getDataSource(), ub, currentRole, false);
		request.setAttribute("studySubject", ssb);
		request.setAttribute("subject", subject);
		request.setAttribute("beans", beans);
		request.setAttribute("eventCRF", ecb);
		request.setAttribute("age", age);
		request.setAttribute(
				"decryptedPassword",
				((SecurityManager) SpringServletAccess.getApplicationContext(getServletContext()).getBean(
						"securityManager")).encryptPassword("root", getUserDetails()));

		// set up interviewer name and date
		fp.addPresetValue(INPUT_INTERVIEWER, ecb.getInterviewerName());
		if (ecb.getDateInterviewed() != null) {
			fp.addPresetValue(INPUT_INTERVIEW_DATE, DateUtil.printDate(ecb.getDateInterviewed(),
					ub.getUserTimeZoneId(), DateUtil.DatePattern.DATE, getLocale()));
		} else {
			fp.addPresetValue(INPUT_INTERVIEW_DATE, "");
		}
		setPresetValues(fp.getPresetValues(), request);
		logMe("Entering Checks !submitted  " + System.currentTimeMillis());
		if (!isSubmitted) {
			// data entry, if this is second round
			logMe("Entering Checks !submitted entered  " + System.currentTimeMillis());
			long t = System.currentTimeMillis();
			request.setAttribute(BEAN_ANNOTATIONS, getEventCRFAnnotations(request));
			session.setAttribute("shouldRunValidation", null);
			session.setAttribute("rulesErrors", null);
			session.setAttribute(DataEntryServlet.NOTE_SUBMITTED, null);
			clearSession(request);
			request.getSession().removeAttribute(DN_ADDITIONAL_CR_PARAMS);

			FormDiscrepancyNotes discNotes = new FormDiscrepancyNotes();
			session.setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, discNotes);
			section = populateNotesWithDBNoteCounts(discNotes, noteThreads, section, request);
			populateInstantOnChange(request.getSession(), ecb, section);
			logger.debug("+++ just ran populateNotes, printing field notes: " + discNotes.getFieldNotes().toString());
			logger.debug("found disc notes: " + discNotes.getNumExistingFieldNotes().toString());

			if (section.getSection().hasSCDItem()) {
				section = SCDItemDisplayInfo.generateSCDDisplayInfo(
						section,
						this.getServletPage(request).equals(Page.INITIAL_DATA_ENTRY_SERVLET)
								|| this.getServletPage(request).equals(Page.ADMIN_EDIT_SERVLET)
								&& !this.isAdminForcedReasonForChange(request));
			}

			int keyId = ecb.getId();
			session.removeAttribute(DoubleDataEntryServlet.COUNT_VALIDATE + keyId);
			setUpPanel(request, section);
			request.setAttribute(BEAN_DISPLAY, getDisplaySectionBeanDependingOnStage(section));

			if (newUploadedFiles.size() > 0) {
				if (!this.unloadFiles(newUploadedFiles)) {
					String missed = "";
					Iterator iter = newUploadedFiles.keySet().iterator();
					while (iter.hasNext()) {
						missed += " " + newUploadedFiles.get(iter.next());
					}
					addPageMessage(respage.getString("uploaded_files_not_deleted_or_not_exist") + ": " + missed,
							request);
				}
			}
			logMe("Entering Checks !submitted entered end forwarding page " + System.currentTimeMillis());
			logMe("Time Took for this block" + (System.currentTimeMillis() - t));
			request.getSession().setAttribute(DN_ADDITIONAL_CR_PARAMS, createDNParametersMap(request, section));
			forwardPage(getJSPPage(), request, response);
			return;
		} else {
			logMe("Entering Checks !submitted not entered  " + System.currentTimeMillis());
			boolean isMarkedPartialSaved = fp.getString("markPartialSaved").equals(VALUE_YES);
			boolean validate = (!isMarkedPartialSaved) && validateInputOnFirstRound();
			HashMap errors = new HashMap();

			FormDiscrepancyNotes discNotes = (FormDiscrepancyNotes) session
					.getAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
			if (discNotes == null) {
				discNotes = new FormDiscrepancyNotes();
			}

			// all items- include items in item groups and other single items
			List<DisplayItemWithGroupBean> allItems = section.getDisplayItemGroups();
			String attachedFilePath = Utils.getAttachedFilePath(getParentStudy());

			DiscrepancyValidator v = new DiscrepancyValidator(validatorHelper, discNotes);
			RuleValidator ruleValidator = new RuleValidator(validatorHelper);

			runHardEditChecks(allItems, validate, displayItemService, fp, v, ecb);
			
			List<ItemBean> itemBeansWithSCDShown = new ArrayList<ItemBean>();
			if (validate && section.getSection().hasSCDItem()) {
				for (int i = 0; i < allItems.size(); ++i) {
					DisplayItemBean dib = allItems.get(i).getSingleItem();
					ItemFormMetadataBean ifmb = dib.getMetadata();
					if (ifmb.getParentId() == 0) {
						if (dib.getScdData().getScdSetsForControl().size() > 0) {
							// for control item
							// dib has to loadFormValue first. Here loadFormValue has been done in
							section.setShowSCDItemIds(SimpleConditionalDisplayService.conditionalDisplayToBeShown(dib,
									section.getShowSCDItemIds()));
						}
						if (dib.getScdData().getScdItemMetadataBean().getScdItemFormMetadataId() > 0) {
							// for scd item
							// a control item is always before its scd item
							dib.setIsSCDtoBeShown(section.getShowSCDItemIds().contains(dib.getMetadata().getItemId()));
							if (dib.getIsSCDtoBeShown()) {
								itemBeansWithSCDShown.add(dib.getItem());
							}
							validateSCDItemBean(v, dib);
						}
						ArrayList<DisplayItemBean> children = dib.getChildren();
						for (int j = 0; j < children.size(); j++) {
							DisplayItemBean child = children.get(j);
							if (child.getScdData().getScdSetsForControl().size() > 0) {
								// for control item
								// dib has to loadFormValue first. Here loadFormValue has been done in
								section.setShowSCDItemIds(SimpleConditionalDisplayService.conditionalDisplayToBeShown(
										child, section.getShowSCDItemIds()));
							} else if (child.getScdData().getScdItemMetadataBean().getScdItemFormMetadataId() > 0) {
								// for scd item
								// a control item is always before its scd item
								child.setIsSCDtoBeShown(section.getShowSCDItemIds().contains(
										child.getMetadata().getItemId()));
								if (child.getIsSCDtoBeShown()) {
									itemBeansWithSCDShown.add(dib.getItem());
								}
								validateSCDItemBean(v, child);
							}
						}
					}
				}
			}

			RuleSetService ruleSetService = getRuleSetService();
			List<RuleSetBean> ruleSets = createAndInitializeRuleSet(currentStudy, studyEventDefinition, crfVersionBean,
					studyEventBean, ecb, true, request, response, itemBeansWithSCDShown);
			boolean shouldRunRules = ruleSetService.shouldRunRulesForRuleSets(ruleSets, phase2) && validate;
			HashMap<String, ArrayList<String>> groupOrdinalPLusItemOid = null;
			groupOrdinalPLusItemOid = runRules(allItems, ruleSets, true, shouldRunRules, MessageType.ERROR, phase2,
					ecb, request);
			
			runSoftEditChecks(allItems, validate, displayItemService, fp, ruleValidator, groupOrdinalPLusItemOid, ecb);
			
			// A map from item name to item bean object.
			HashMap<String, ItemBean> scoreItems = new HashMap<String, ItemBean>();
			HashMap<String, String> scoreItemdata = new HashMap<String, String>();
			HashMap<Integer, String> oldItemdata = prepareSectionItemdata(sb.getId(), request);
			// hold all item names of changed ItemBean in current section
			TreeSet<String> changedItems = new TreeSet<String>();
			ArrayList<String> changedItemNamesList = new ArrayList<String>();
			// holds complete disply item beans for checking against 'request
			// for change' restriction
			ArrayList<DisplayItemBean> changedItemsList = new ArrayList<DisplayItemBean>();
			// key is repeating item name, value is its display item group bean
			HashMap<String, DisplayItemGroupBean> changedItemsMap = new HashMap<String, DisplayItemGroupBean>();
			// key is itemid, value is set of itemdata-ordinal
			HashMap<Integer, TreeSet<Integer>> itemOrdinals = prepareItemdataOrdinals(request);
			HashMap<String, Boolean> rfcForNewRows = new HashMap<String, Boolean>();

			// prepare item data for scoring
			updateDataOrdinals(allItems);
			section.setDisplayItemGroups(allItems);
			scoreItems = prepareScoreItems(request);
			scoreItemdata = prepareScoreItemdata(request);

			for (int i = 0; i < allItems.size(); i++) {
				DisplayItemWithGroupBean diwb = allItems.get(i);
				if (diwb.isInGroup()) {
					List<DisplayItemGroupBean> dbGroups = diwb.getDbItemGroups();
					for (int j = 0; j < dbGroups.size(); j++) {
						DisplayItemGroupBean displayGroup = dbGroups.get(j);
						List<DisplayItemBean> items = displayGroup.getItems();
						if ("remove".equalsIgnoreCase(displayGroup.getEditFlag())) {
							for (DisplayItemBean displayItem : items) {
								int itemId = displayItem.getItem().getId();
								int ordinal = displayItem.getData().getOrdinal();
								if (itemOrdinals.containsKey(itemId)) {
									itemOrdinals.get(itemId).remove(ordinal);
								}
								if (scoreItemdata.containsKey(itemId + "_" + ordinal)) {
									scoreItemdata.remove(itemId + "_" + ordinal);
								}
								boolean manual = j != 0;
								String formName = DataEntryUtil.getGroupItemInputName(displayGroup, j, displayItem, manual);
								changedItems.add(formName);
								changedItemsList.add(displayItem);
								changedItemNamesList.add(formName);
								changedItemsMap.put(formName, displayGroup);
								logger.debug("adding to changed items map: " + formName);
							}
						}
					}

					List<DisplayItemGroupBean> dgbs = diwb.getItemGroups();
					HashMap<Integer, Integer> maxOrdinals = new HashMap<Integer, Integer>();
					boolean first = true;
					for (int j = 0; j < dgbs.size(); j++) {
						boolean newRow = false;
						DisplayItemGroupBean displayGroup = dgbs.get(j);
						List<DisplayItemBean> items = displayGroup.getItems();
						boolean isAdd = "add".equalsIgnoreCase(displayGroup.getEditFlag()) ? true : false;
						if (dgbs.indexOf(displayGroup) > dbGroups.size() - 1) {
							newRow = true;
							rfcForNewRows.put(
									displayGroup.getItemGroupBean().getId() + "_" + displayGroup.getOrdinal(), false);
						}
						for (DisplayItemBean displayItem : items) {
							ItemBean ib = displayItem.getItem();
							String itemName = ib.getName();
							int itemId = ib.getId();
							if (first) {
								maxOrdinals.put(itemId,
										iddao.getMaxOrdinalForGroup(ecb, sb, displayGroup.getItemGroupBean()));
							}
							ItemDataBean idb = displayItem.getData();
							String value = idb.getValue();
							scoreItems.put(itemName, ib);
							int ordinal = displayItem.getData().getOrdinal();
							if (isAdd && scoreItemdata.containsKey(itemId + "_" + ordinal)) {
								int formMax = 1;
								if (maxOrdinals.containsKey(itemId)) {
									formMax = maxOrdinals.get(itemId);
								}
								int dbMax = iddao.getMaxOrdinalForGroup(ecb, sb, displayGroup.getItemGroupBean());
								ordinal = ordinal >= dbMax ? formMax + 1 : ordinal;
								maxOrdinals.put(itemId, ordinal);
								displayItem.getData().setOrdinal(ordinal);
								scoreItemdata.put(itemId + "_" + ordinal, value);
							} else {
								scoreItemdata.put(itemId + "_" + ordinal, value);
							}
							if (itemOrdinals.containsKey(itemId)) {
								itemOrdinals.get(itemId).add(ordinal);
							} else {
								TreeSet<Integer> ordinalSet = new TreeSet<Integer>();
								ordinalSet.add(ordinal);
								itemOrdinals.put(itemId, ordinalSet);
							}
							if (newRow || isChanged(displayItem, oldItemdata, attachedFilePath)) {
								boolean manual = j != 0;
								String formName = DataEntryUtil.getGroupItemInputName(displayGroup, j, displayItem, manual);
								logger.debug("RESET: formName group-item-input:" + formName);
								changedItems.add(formName);
								changedItemsList.add(displayItem);
								changedItemNamesList.add(formName);
								changedItemsMap.put(formName, displayGroup);
								logger.debug("adding to changed items map: " + formName);
							}
						}
						first = false;
					}
				} else {
					DisplayItemBean dib = diwb.getSingleItem();
					ItemBean ib = dib.getItem();
					ItemDataBean idb = dib.getData();
					int itemId = ib.getId();
					String itemName = ib.getName();
					String value = idb.getValue();
					scoreItems.put(itemName, ib);
					// for items which are not in any group, their ordinal is
					// set as 1
					TreeSet<Integer> ordinalset = new TreeSet<Integer>();
					ordinalset.add(1);
					itemOrdinals.put(itemId, ordinalset);
					scoreItemdata.put(itemId + "_" + 1, value);
					if (isChanged(idb, oldItemdata, dib, attachedFilePath)) {
						changedItems.add(DataEntryUtil.getInputName(dib));
						changedItemsList.add(dib);
						changedItemNamesList.add(DataEntryUtil.getInputName(dib));
						changedItemsMap.put(DataEntryUtil.getInputName(dib), new DisplayItemGroupBean());
					}

					ArrayList children = dib.getChildren();
					for (int j = 0; j < children.size(); j++) {
						DisplayItemBean child = (DisplayItemBean) children.get(j);
						ItemBean cib = child.getItem();
						scoreItems.put(cib.getName(), cib);
						TreeSet<Integer> cordinalset = new TreeSet<Integer>();
						cordinalset.add(1);
						itemOrdinals.put(itemId, cordinalset);
						scoreItemdata.put(cib.getId() + "_" + 1, child.getData().getValue());
						if (isChanged(child.getData(), oldItemdata, child, attachedFilePath)) {
							changedItems.add(DataEntryUtil.getInputName(child));
							changedItemsList.add(child);
							changedItemNamesList.add(DataEntryUtil.getInputName(child));
							changedItemsMap.put(DataEntryUtil.getInputName(child), new DisplayItemGroupBean());
						}
					}
				}
			}
			// do calculation for 'calculation' and 'group-calculation' type
			// items
			// and write the result in DisplayItemBean's ItemDateBean - data
			ScoreItemValidator sv = new ScoreItemValidator(validatorHelper, discNotes);
			// *** doing calc here, load it where? ***
			SessionManager sm = getSessionManager(request);
			ScoreCalculator sc = new ScoreCalculator(sm, ecb, ub);

			for (int i = 0; i < allItems.size(); i++) {
				DisplayItemWithGroupBean diwb = allItems.get(i);
				if (diwb.isInGroup()) {
					List<DisplayItemGroupBean> dgbs = diwb.getItemGroups();
					for (int j = 0; j < dgbs.size(); j++) {
						DisplayItemGroupBean displayGroup = dgbs.get(j);

						List<DisplayItemBean> items = displayGroup.getItems();
						for (DisplayItemBean displayItem : items) {
							ItemFormMetadataBean ifmb = displayItem.getMetadata();
							int responseTypeId = ifmb.getResponseSet().getResponseTypeId();
							if (responseTypeId == ResponseType.CALCULATION.getId()
									|| responseTypeId == ResponseType.GROUP_CALCULATION.getId()) {
								StringBuffer err = new StringBuffer();
								ResponseOptionBean robBean = (ResponseOptionBean) ifmb.getResponseSet().getOptions()
										.get(0);
								String value = "";
								String inputName = DataEntryUtil.getGroupItemInputName(displayGroup, displayGroup.getFormInputOrdinal(),
										displayItem, !displayGroup.isAuto());
								logger.debug("returning input name: " + inputName);

								value = sc.doCalculation(displayItem, scoreItems, scoreItemdata, itemOrdinals, err,
										displayItem.getData().getOrdinal());

								displayItem.loadFormValue(value);
								if (isChanged(displayItem, oldItemdata, attachedFilePath)) {
									changedItemsList.add(displayItem);
									changedItems.add(inputName);
									changedItemNamesList.add(inputName);
									changedItemsMap.put(inputName, displayGroup);
								}

								request.setAttribute(inputName, value);
								if (validate) {
									displayItem = getDisplayItemBeanValidator().validateCalcTypeDisplayItemBean(sv, displayItem, inputName, request);
									if (err.length() > 0) {
										Validation validation = new Validation(Validator.CALCULATION_FAILED);
										validation.setErrorMessage(err.toString());
										sv.addValidation(inputName, validation);
									}
								}
							}
						}
					}
				} else {
					DisplayItemBean dib = diwb.getSingleItem();
					ItemFormMetadataBean ifmb = dib.getMetadata();
					int responseTypeId = ifmb.getResponseSet().getResponseTypeId();
					if (responseTypeId == ResponseType.CALCULATION.getId()
							|| responseTypeId == ResponseType.GROUP_CALCULATION.getId()) {
						StringBuffer err = new StringBuffer();
						ResponseOptionBean robBean = (ResponseOptionBean) ifmb.getResponseSet().getOptions().get(0);
						String value = sc.doCalculation(dib, scoreItems, scoreItemdata, itemOrdinals, err, 1);
						dib.loadFormValue(value);
						if (isChanged(dib.getData(), oldItemdata, dib, attachedFilePath)) {
							changedItems.add(DataEntryUtil.getInputName(dib));
							changedItemsList.add(dib);
							changedItemNamesList.add(DataEntryUtil.getInputName(dib));
							changedItemsMap.put(DataEntryUtil.getInputName(dib), new DisplayItemGroupBean());
						}
						String inputName = DataEntryUtil.getInputName(dib);
						request.setAttribute(inputName, value);
						if (validate) {
							dib = getDisplayItemBeanValidator().validateCalcTypeDisplayItemBean(sv, dib, "", request);
							if (err.length() > 0) {
								Validation validation = new Validation(Validator.CALCULATION_FAILED);
								validation.setErrorMessage(err.toString());
								sv.addValidation(inputName, validation);
							}
						}
					}

					ArrayList<DisplayItemBean> children = dib.getChildren();
					for (int j = 0; j < children.size(); j++) {
						DisplayItemBean child = (DisplayItemBean) children.get(j);
						ItemFormMetadataBean cifmb = child.getMetadata();
						int resTypeId = cifmb.getResponseSet().getResponseTypeId();
						if (resTypeId == ResponseType.CALCULATION.getId()
								|| resTypeId == ResponseType.GROUP_CALCULATION.getId()) {
							StringBuffer cerr = new StringBuffer();
							child.getDbData().setValue(child.getData().getValue());
							ResponseOptionBean crobBean = (ResponseOptionBean) cifmb.getResponseSet().getOptions()
									.get(0);
							String cvalue = "";
							cvalue = sc.doCalculation(child, scoreItems, scoreItemdata, itemOrdinals, cerr, 1);
							child.loadFormValue(cvalue);
							if (isChanged(child.getData(), oldItemdata, child, attachedFilePath)) {
								changedItems.add(DataEntryUtil.getInputName(child));
								changedItemsList.add(child);
								changedItemNamesList.add(DataEntryUtil.getInputName(child));
								changedItemsMap.put(DataEntryUtil.getInputName(child), new DisplayItemGroupBean());
							}
							String cinputName = DataEntryUtil.getInputName(child);
							request.setAttribute(cinputName, cvalue);
							if (validate) {
								child = getDisplayItemBeanValidator().validateCalcTypeDisplayItemBean(sv, child, "", request);
								if (cerr.length() > 0) {
									Validation cvalidation = new Validation(Validator.CALCULATION_FAILED);
									cvalidation.setErrorMessage(cerr.toString());
									sv.addValidation(cinputName, cvalidation);
								}
							}
						}
						children.set(j, child);
					}
				}
			}

			section.setDisplayItemGroups(allItems);

			if (currentStudy.getStudyParameterConfig().getInterviewerNameRequired().equals("yes") && 
					!isMarkedPartialSaved) {
				v.addValidation(INPUT_INTERVIEWER, Validator.NO_BLANKS);
			}

			if (currentStudy.getStudyParameterConfig().getInterviewDateRequired().equals("yes") && 
					!isMarkedPartialSaved) {
				v.addValidation(INPUT_INTERVIEW_DATE, Validator.NO_BLANKS);
			}

			if (!StringUtil.isBlank(fp.getString(INPUT_INTERVIEW_DATE)) && !isMarkedPartialSaved) {
				v.addValidation(INPUT_INTERVIEW_DATE, Validator.IS_A_DATE);
				v.alwaysExecuteLastValidation(INPUT_INTERVIEW_DATE);
			} 

			if (section.getSection().hasSCDItem()) {
				section = SCDItemDisplayInfo.generateSCDDisplayInfo(
						section,
						this.getServletPage(request).equals(Page.INITIAL_DATA_ENTRY_SERVLET)
								|| this.getServletPage(request).equals(Page.ADMIN_EDIT_SERVLET)
								&& !this.isAdminForcedReasonForChange(request));
			}

			removeFieldsValidationsForSubmittedDN(v, request);

			// get hard rules, skip soft if errors > 0
			errors = v.validate();
			if (errors.size() > 0 && !checkDobleDataEntryErrors(errors)) {
				request.setAttribute("Hardrules", true);
			}
			reshuffleErrorGroupNamesKK(errors, allItems, request);

			if (this.isAdminForcedReasonForChange(request) && this.isAdministrativeEditing() && errors.isEmpty()) {
				String error = respage.getString("reason_for_change_error");
				if (changedItemsMap.size() > 0) {
					request.setAttribute("Hardrules", true);

					for (DisplayItemBean displayItem : changedItemsList) {
						if (displayItem.getMetadata().getResponseSet().getResponseType()
								.equals(ResponseType.CALCULATION)) {
							continue;
						}
						int index = changedItemsList.indexOf(displayItem);
						String formName = changedItemNamesList.get(index);

						ItemDataBean idb = displayItem.getData();
						ItemFormMetadataBean ifmb = displayItem.getMetadata();
						DisplayItemGroupBean digb = changedItemsMap.get(formName);
						String newRowRfcKey = digb != null
								? digb.getItemGroupBean().getId() + "_" + digb.getOrdinal()
								: null;
						Boolean rfcWasAdded = rfcForNewRows.get(newRowRfcKey);
						if (rfcWasAdded != null && rfcWasAdded) {
							continue;
						}
						logger.debug("-- found group label " + ifmb.getGroupLabel());
						if (!ifmb.getGroupLabel().equalsIgnoreCase("Ungrouped")
								&& !ifmb.getGroupLabel().equalsIgnoreCase("")) {
							changedItemsMap.remove(formName);
							if (digb != null) {
								this.setReasonForChangeError(errors, idb, formName, error, request);
								if (rfcWasAdded != null) {
									rfcForNewRows.put(newRowRfcKey, true);
								}
							}
						} else {
							this.setReasonForChangeError(errors, idb, formName, error, request);
							if (rfcWasAdded != null) {
								rfcForNewRows.put(newRowRfcKey, true);
							}
							logger.debug("form name added: " + formName);
						}
					}
				}
				reshuffleErrorGroupNamesKK(errors, allItems, request);
			}

			populateNotesWithDBNoteCounts(discNotes, noteThreads, section, request);

			logger.debug("errors here: " + errors.toString());

			if (errors.isEmpty() && shouldRunRules) {
				createListOfDNsForTransformation(ruleValidator, dndao, request);
				logger.debug("Errors was empty");
				if (session.getAttribute("rulesErrors") != null) {
					HashMap h = ruleValidator.validate();
					reshuffleErrorGroupNamesKK(h, allItems, request);
					Set<String> a = (Set<String>) session.getAttribute("rulesErrors");
					Set<String> ba = h.keySet();
					Boolean showErrors = false;
					for (Object key : ba) {
						if (!a.contains(key)) {
							showErrors = true;
						}
					}
					if (showErrors) {
						errors = h;
						if (errors.size() > 0) {
							session.setAttribute("shouldRunValidation", "1");
							session.setAttribute("rulesErrors", errors.keySet());
						} else {
							session.setAttribute("shouldRunValidation", null);
							session.setAttribute("rulesErrors", null);
						}
					} else {
						session.setAttribute("shouldRunValidation", null);
						session.setAttribute("rulesErrors", null);

					}

				} else if (session.getAttribute("shouldRunValidation") != null
						&& session.getAttribute("shouldRunValidation").toString().equals("1")) {
					session.setAttribute("shouldRunValidation", null);
					session.setAttribute("rulesErrors", null);
				} else {
					// get soft rules
					errors = ruleValidator.validate();
					reshuffleErrorGroupNamesKK(errors, allItems, request);
					if (errors.size() > 0) {
						session.setAttribute("shouldRunValidation", "1");
						session.setAttribute("rulesErrors", errors.keySet());
					}
				}
			}

			Boolean warningsIsDisplayed = (Boolean) request.getSession().getAttribute(WARNINGS_LIST);
			HashMap warningMessages = new HashMap();
			if (warningsIsDisplayed == null) {
				HashMap<String, ArrayList<String>> siErrors = sv.validate();
				if (siErrors != null && !siErrors.isEmpty()) {
					Iterator iter = siErrors.keySet().iterator();
					while (iter.hasNext()) {
						String fieldName = iter.next().toString();
						warningMessages.put(fieldName, siErrors.get(fieldName));
					}
				}
				if (warningMessages.size() > 0) {
					request.getSession().setAttribute(WARNINGS_LIST, true);
					request.setAttribute("warningMessages", warningMessages);
				}
			}

			if (!errors.isEmpty() || !warningMessages.isEmpty()) {
				if (errors.keySet().contains(INPUT_INTERVIEWER) || errors.keySet().contains(INPUT_INTERVIEW_DATE)) {
					request.setAttribute("expandCrfInfo", true);
				}

				logger.debug("threw an error with data entry...");

				String[] textFields = {INPUT_INTERVIEWER, INPUT_INTERVIEW_DATE};
				fp.setCurrentStringValuesAsPreset(textFields);
				setPresetValues(fp.getPresetValues(), request);

				request.setAttribute("markComplete", fp.getString(INPUT_MARK_COMPLETE));
				request.setAttribute("markPartialSaved", fp.getString(INPUT_MARK_PARTIAL_SAVED));
				request.setAttribute(BEAN_DISPLAY, getDisplaySectionBeanDependingOnStage(section));
				request.setAttribute(BEAN_ANNOTATIONS, fp.getString(INPUT_ANNOTATIONS));
				setInputMessages(errors, request);
				addPageMessage(respage.getString("errors_in_submission_see_below"), request);
				request.setAttribute("hasError", "true");
				session.setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, discNotes);
				setUpPanel(request, section);
				request.getSession().setAttribute(DN_ADDITIONAL_CR_PARAMS, createDNParametersMap(request, section));
				forwardPage(getJSPPage(), request, response);
			} else {
				request.getSession().removeAttribute(DN_ADDITIONAL_CR_PARAMS);
				boolean success = true;
				boolean temp = true;

				// save interviewer name and date into DB
				ecb.setInterviewerName(fp.getString(INPUT_INTERVIEWER));
				if (!StringUtil.isBlank(fp.getString(INPUT_INTERVIEW_DATE)) 
						&& isValidFormatOfDateToSave(fp, INPUT_INTERVIEW_DATE)) {
					if (ecb.getDateInterviewed() != null) {
						String actualDateInterviewed = DateUtil.printDate(ecb.getDateInterviewed(),
								ub.getUserTimeZoneId(), DateUtil.DatePattern.DATE, getLocale());
						if (!actualDateInterviewed.equals(fp.getString(INPUT_INTERVIEW_DATE))) {
							ecb.setDateInterviewed(fp.getDateInputWithServerTimeOfDay(INPUT_INTERVIEW_DATE));
						}
					} else {
						ecb.setDateInterviewed(fp.getDateInputWithServerTimeOfDay(INPUT_INTERVIEW_DATE));
					}

				} else {
					ecb.setDateInterviewed(null);
				}

				if (ecdao == null) {
					ecdao = new EventCRFDAO(getDataSource());
				}
				// set validator id for DDE
				DataEntryStage stage = ecb.getStage();
				if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE)
						|| stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY)) {
					ecb.setValidatorId(ub.getId());

				}

				if (studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.SIGNED)
						&& changedItemsList.size() > 0) {
					studyEventBean.setSubjectEventStatus(SubjectEventStatus.COMPLETED);
					studyEventBean.setUpdater(ub);
					studyEventBean.setUpdatedDate(new Date());
					seDao.update(studyEventBean);
				}

				// If the Study Subject's Satus is signed and we save a section, change status to available
				logger.debug("Status of Study Subject {}", ssb.getStatus().getName());
				if (ssb.getStatus() == Status.SIGNED && changedItemsList.size() > 0) {
					logger.debug("Status of Study Subject is Signed we are updating");
					StudySubjectDAO studySubjectDao = new StudySubjectDAO(getDataSource());
					ssb.setStatus(Status.AVAILABLE);
					ssb.setUpdater(ub);
					ssb.setUpdatedDate(new Date());
					studySubjectDao.update(ssb);
				}

				boolean resetSDVForItems = false;
				if (ecb.isSdvStatus() && changedItemsList.size() > 0) {
					logger.debug("Status of Study Subject is SDV we are updating");
					StudySubjectDAO studySubjectDao = new StudySubjectDAO(getDataSource());
					ssb.setStatus(Status.AVAILABLE);
					ssb.setUpdater(ub);
					ssb.setUpdatedDate(new Date());
					studySubjectDao.update(ssb);
					ecb.setSdvUpdateId(ub.getId());
					if (edcBean.getSourceDataVerification().equals(SourceDataVerification.AllREQUIRED)
							|| (edcBean.getSourceDataVerification().equals(SourceDataVerification.PARTIALREQUIRED) && getItemSDVService()
							.hasChangedSDVRequiredItems(changedItemsList))) {
						ecb.setSdvStatus(false);
						resetSDVForItems = true;
					}
				}

				ecb = (EventCRFBean) ecdao.update(ecb);

				if (ecb.isNotStarted()) {
					ecb.setOwner(ub);
				}
				ecb.setNotStarted(false);
				ecdao.update(ecb);

				request.setAttribute("previousSEStatus", studyEventBean.getSubjectEventStatus());

				StudyEventDefinitionBean sedb = (StudyEventDefinitionBean) seddao.findByPK(studyEventBean
						.getStudyEventDefinitionId());
				SubjectEventStatusUtil.determineSubjectEventState(studyEventBean, new DAOWrapper(getDataSource()));
				studyEventBean = (StudyEventBean) seDao.update(studyEventBean);

				// save discrepancy notes into DB
				DiscrepancyNoteService dnService = new DiscrepancyNoteService(getDataSource());
				FormDiscrepancyNotes fdn = (FormDiscrepancyNotes) session
						.getAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);

				if ((stage.equals(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE) || stage
						.equals(DataEntryStage.DOUBLE_DATA_ENTRY)) && edcb.isEvaluatedCRF()) {
					String noteDescription = resword.getString("crf_eval_rfc_description");
					String detailedDescription = resword.getString("crf_eval_rfc_detailed_description");
					List<DiscrepancyNoteBean> dbns = new DiscrepancyNoteService(getDataSource())
							.generateRFCsForChangedFields(changedItemsList, changedItemNamesList, oldItemdata, ub,
									noteDescription, detailedDescription);
					fdn.addAutoRFCs(dbns);
				}

				savePartialSavedStatus(fp, ecb, sb.getId(), ssb.getId());

				dndao = new DiscrepancyNoteDAO(getDataSource());
				dnService.saveFieldNotes(INPUT_INTERVIEWER, fdn, ecb.getId(), "EventCRF", currentStudy);
				dnService.saveFieldNotes(INPUT_INTERVIEW_DATE, fdn, ecb.getId(), "EventCRF", currentStudy);
				transformSubmittedDNsToFVC(ub, dndao, request);
				allItems = section.getDisplayItemGroups();

				logger.debug("all items before saving into DB" + allItems.size());

				resetCodedItemTerms(allItems, iddao, ecb, changedItemsList);
				if (resetSDVForItems) {
					getItemSDVService().resetSDVForItems(changedItemsList, ub);
				}

				for (int i = 0; i < allItems.size(); i++) {
					DisplayItemWithGroupBean diwb = allItems.get(i);

					if (diwb.isInGroup()) {
						List<DisplayItemGroupBean> dgbs = diwb.getItemGroups();

						for (int j = 0; j < dgbs.size(); j++) {
							DisplayItemGroupBean displayGroup = dgbs.get(j);
							List<DisplayItemBean> items = displayGroup.getItems();

							for (DisplayItemBean displayItem : items) {
								String fileName = this.addAttachedFilePath(displayItem, attachedFilePath);
								displayItem.setEditFlag(displayGroup.getEditFlag());
								logger.debug("group item value: " + displayItem.getData().getValue());
								if ("add".equalsIgnoreCase(displayItem.getEditFlag()) && fileName.length() > 0
										&& !newUploadedFiles.containsKey(fileName)) {
									displayItem.getData().setValue("");
								}
								temp = writeToDB(displayItem, iddao, displayGroup.getOrdinal() + 1, request);
								logger.debug("just executed writeToDB - 1");
								logger.debug("ordinal: " + displayGroup.getOrdinal() + 1);
								if (temp && newUploadedFiles.containsKey(fileName)) {
									newUploadedFiles.remove(fileName);
								}
								String inputName = DataEntryUtil.getGroupItemInputName(displayGroup, displayGroup.getFormInputOrdinal(),
										displayItem, !displayGroup.isAuto());
								dnService.saveFieldNotes(inputName, fdn, displayItem.getData().getId(), "itemData",
										currentStudy);
								success = success && temp;
							}
						}
						List<DisplayItemGroupBean> dbGroups = diwb.getDbItemGroups();
						for (int j = 0; j < dbGroups.size(); j++) {
							DisplayItemGroupBean displayGroup = dbGroups.get(j);
							if ("remove".equalsIgnoreCase(displayGroup.getEditFlag())) {
								List<DisplayItemBean> items = displayGroup.getItems();
								for (DisplayItemBean displayItem : items) {
									String fileName = this.addAttachedFilePath(displayItem, attachedFilePath);
									displayItem.setEditFlag(displayGroup.getEditFlag());
									logger.debug("group item value: " + displayItem.getData().getValue());
									if ("add".equalsIgnoreCase(displayItem.getEditFlag()) && fileName.length() > 0
											&& !newUploadedFiles.containsKey(fileName)) {
										displayItem.getData().setValue("");
									}
									temp = writeToDB(displayItem, iddao, 0, request);
									logger.debug("just executed writeToDB - 2");
									if (temp && newUploadedFiles.containsKey(fileName)) {
										newUploadedFiles.remove(fileName);
									}
									success = success && temp;
								}
							}
						}
					} else {
						DisplayItemBean dib = diwb.getSingleItem();
						this.addAttachedFilePath(dib, attachedFilePath);
						temp = writeToDB(dib, iddao, 1, request);
						logger.debug("just executed writeToDB - 3");
						if (temp && newUploadedFiles.containsKey(dib.getItem().getId() + "")) {
							// so newUploadedFiles will contain only failed file
							// items;
							newUploadedFiles.remove(dib.getItem().getId() + "");
						}

						String inputName = DataEntryUtil.getInputName(dib);
						logger.trace("3 - found input name: " + inputName);
						dnService.saveFieldNotes(inputName, fdn, dib.getData().getId(), "itemData", currentStudy);

						success = success && temp;

						ArrayList childItems = dib.getChildren();
						for (int j = 0; j < childItems.size(); j++) {
							DisplayItemBean child = (DisplayItemBean) childItems.get(j);
							this.addAttachedFilePath(child, attachedFilePath);
							temp = writeToDB(child, iddao, 1, request);
							logger.debug("just executed writeToDB - 4");
							if (temp && newUploadedFiles.containsKey(child.getItem().getId() + "")) {
								newUploadedFiles.remove(child.getItem().getId() + "");
							}
							inputName = DataEntryUtil.getInputName(child);
							dnService.saveFieldNotes(inputName, fdn, child.getData().getId(), "itemData", currentStudy);
							success = success && temp;
						}
					}
				}

				logMe("DisplayItemWithGroupBean allitems4 end " + System.currentTimeMillis());
				List<Integer> prevShownDynItemDataIds = shouldRunRules ? dynamicsMetadataService
						.getDynamicsItemFormMetadataDao().findShowItemDataIdsInSection(section.getSection().getId(),
								ecb.getCRFVersionId(), ecb.getId()) : new ArrayList<Integer>();
				logMe("DisplayItemWithGroupBean dryrun  start" + System.currentTimeMillis());
				HashMap<String, ArrayList<String>> rulesPostDryRun = runRules(allItems, ruleSets, false,
						shouldRunRules, MessageType.WARNING, phase2, ecb, request);

				if (getItemSDVService().getCountOfItemsToSDV(ecb.getId()) > 0 && ecb.isSdvStatus()) {
					ecb.setUpdater(ub);
					ecb.setSdvStatus(false);
					ecb.setUpdatedDate(new Date());
					ecdao.update(ecb);
				}

				HashMap<String, ArrayList<String>> errorsPostDryRun = new HashMap<String, ArrayList<String>>();
				// additional step needed, run rules and see if any items are 'shown' AFTER saving data
				logMe("DisplayItemWithGroupBean dryrun  end" + System.currentTimeMillis());
				boolean inSameSection = false;
				logMe("DisplayItemWithGroupBean allitems4 " + System.currentTimeMillis());
				if (!rulesPostDryRun.isEmpty()) {
					Iterator iter3 = rulesPostDryRun.keySet().iterator();
					while (iter3.hasNext()) {
						String fieldName = iter3.next().toString();
						logger.debug("found oid after post dry run " + fieldName);
						int ordinal = -1;
						String newFieldName = fieldName;
						String[] fieldNames = fieldName.split("\\.");
						fieldNames = Arrays.copyOfRange(fieldNames, fieldNames.length == INT_3
								? 1
								: (fieldNames.length == INT_4 ? 2 : 0), fieldNames.length);
						if (fieldNames.length == 2) {
							newFieldName = fieldNames[1];
							// check items in item groups here?
							if (fieldNames[0].contains("[")) {
								int p1 = fieldNames[0].indexOf("[");
								int p2 = fieldNames[0].indexOf("]");
								try {
									ordinal = Integer.valueOf(fieldNames[0].substring(p1 + 1, p2));
								} catch (NumberFormatException e) {
									ordinal = -1;
								}
								fieldNames[0] = fieldNames[0].substring(0, p1);
							}
						}
						List<DisplayItemWithGroupBean> displayGroupsWithItems = section.getDisplayItemGroups();
						for (int i = 0; i < displayGroupsWithItems.size(); i++) {
							DisplayItemWithGroupBean itemWithGroup = displayGroupsWithItems.get(i);
							if (itemWithGroup.isInGroup()) {
								logger.debug("found group: " + fieldNames[0]);
								// do something there
								List<DisplayItemGroupBean> digbs = itemWithGroup.getItemGroups();
								logger.debug("digbs size: " + digbs.size());
								for (int j = 0; j < digbs.size(); j++) {
									DisplayItemGroupBean displayGroup = digbs.get(j);
									if (displayGroup.getItemGroupBean().getOid().equals(fieldNames[0])
											&& displayGroup.getOrdinal() == ordinal - 1) {
										List<DisplayItemBean> items = displayGroup.getItems();

										for (int k = 0; k < items.size(); k++) {
											DisplayItemBean dib = items.get(k);
											if (dib.getItem().getOid().equals(newFieldName)) {
												if (!dib.getMetadata().isShowItem()) {
													dib.getMetadata().setShowItem(true);
												}
												if (prevShownDynItemDataIds == null
														|| !prevShownDynItemDataIds.contains(dib.getData().getId())) {
													inSameSection = true;
													errorsPostDryRun.put(
															DataEntryUtil.getGroupItemInputName(displayGroup, j, dib, false),
															rulesPostDryRun.get(fieldName));
												}
											}
											items.set(k, dib);
										}
										displayGroup.setItems(items);
										digbs.set(j, displayGroup);
									}
								}
								itemWithGroup.setItemGroups(digbs);
							} else {
								DisplayItemBean displayItemBean = itemWithGroup.getSingleItem();
								ItemBean itemBean = displayItemBean.getItem();
								if (newFieldName.equals(itemBean.getOid())) {
									if (!displayItemBean.getMetadata().isShowItem()) {
										displayItemBean.getMetadata().setShowItem(true);
										if (prevShownDynItemDataIds == null
												|| !prevShownDynItemDataIds.contains(displayItemBean.getData().getId())) {
											inSameSection = true;
											errorsPostDryRun.put(DataEntryUtil.getInputName(displayItemBean),
													rulesPostDryRun.get(fieldName));
										}
									}
								}
								itemWithGroup.setSingleItem(displayItemBean);
							}
							displayGroupsWithItems.set(i, itemWithGroup);
						}
						logMe("DisplayItemWithGroupBean allitems4  end,begin" + System.currentTimeMillis());
						List<DisplayItemWithGroupBean> itemGroups = section.getDisplayItemGroups();
						for (DisplayItemWithGroupBean itemGroup : itemGroups) {
							DisplayItemGroupBean displayGroup = itemGroup.getItemGroup();
							if (newFieldName.equals(displayGroup.getItemGroupBean().getOid())) {
								if (!displayGroup.getGroupMetaBean().isShowGroup()) {
									inSameSection = true;
									logger.debug("found itemgroup " + displayGroup.getItemGroupBean().getOid()
											+ " vs. " + fieldName + " and is show item: "
											+ displayGroup.getGroupMetaBean().isShowGroup());
									errorsPostDryRun.put(displayGroup.getItemGroupBean().getOid(),
											rulesPostDryRun.get(fieldName));
									displayGroup.getGroupMetaBean().setShowGroup(true);
									displayItemService.loadItemsWithGroupRows(itemGroup, sb, edcb, ecb, request);
								}
							}
						}
						logMe("DisplayItemWithGroupBean allitems4  end,end" + System.currentTimeMillis());
						section.setDisplayItemGroups(displayGroupsWithItems);
					}
					dynamicsMetadataService.updateGroupDynamicsInSection(displayItemWithGroups, section.getSection()
							.getId(), ecb);
					toc = getDisplayBeanWithShownSections(
							(DisplayTableOfContentsBean) request.getAttribute(TOC_DISPLAY), dynamicsMetadataService);
					request.setAttribute(TOC_DISPLAY, toc);
					sectionIdsInToc = DataEntryUtil.getSectionIdsInToc(toc);
					sIndex = DataEntryUtil.getSectionIndexInToc(section.getSection(), toc, sectionIdsInToc);
					previousSec = DataEntryUtil.getPrevSection(toc, sIndex);
					nextSec = DataEntryUtil.getNextSection(toc, sIndex);
					section.setFirstSection(!previousSec.isActive());
					section.setLastSection(!nextSec.isActive());

					logger.debug(" in same section: " + inSameSection);
					if (inSameSection) {
						String[] textFields = {INPUT_INTERVIEWER, INPUT_INTERVIEW_DATE};
						fp.setCurrentStringValuesAsPreset(textFields);
						setPresetValues(fp.getPresetValues(), request);
						request.setAttribute(BEAN_DISPLAY, getDisplaySectionBeanDependingOnStage(section));
						request.setAttribute(BEAN_ANNOTATIONS, fp.getString(INPUT_ANNOTATIONS));
						setInputMessages(errorsPostDryRun, request);
						addPageMessage(respage.getString("your_answers_activated_hidden_items"), request);
						request.setAttribute("hasError", "true");
						request.setAttribute("hasShown", "true");

						session.setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, discNotes);
						setUpPanel(request, section);
						forwardPage(getJSPPage(), request, response);
					}
				}

				if (!inSameSection) {
					ArrayList<String> updateFailedItems = sc.redoCalculations(scoreItems, scoreItemdata, changedItems,
							itemOrdinals, sb.getId());
					success = updateFailedItems.size() > 0 ? false : true;

					boolean markComplete = fp.getString(INPUT_MARK_COMPLETE).equals(VALUE_YES);
					
					boolean markSuccessfully = false; // if the CRF was marked


					if (markComplete && section.isLastSection()) {
						logger.debug("need to mark CRF as complete");
						markSuccessfully = markCRFComplete(request);
						logger.debug("...marked CRF as complete: " + markSuccessfully);
						if (!markSuccessfully) {
							request.setAttribute(BEAN_DISPLAY, getDisplaySectionBeanDependingOnStage(section));
							request.setAttribute(BEAN_ANNOTATIONS, fp.getString(INPUT_ANNOTATIONS));
							setUpPanel(request, section);
							forwardPage(getJSPPage(), request, response);
							return;
						}
					}

					// send email with CRF-report
					if (markSuccessfully && "complete".equals(edcb.getEmailStep())) {
						sendEmailWithCRFReport(crfVersionBean, crfBean, ssb, edcb, ecb, request);
					}
					// now write the event crf bean to the database
					String annotations = fp.getString(INPUT_ANNOTATIONS);
					setEventCRFAnnotations(annotations, request);
					Date now = new Date();
					ecb.setUpdatedDate(now);
					ecb.setUpdater(ub);
					ecb = (EventCRFBean) ecdao.update(ecb);
					success = success && ecb.isActive();

					StudyEventDAO sedao = new StudyEventDAO(getDataSource());
					StudyEventBean seb = (StudyEventBean) sedao.findByPK(ecb.getStudyEventId());
					seb.setUpdatedDate(now);
					seb.setUpdater(ub);
					seb = (StudyEventBean) sedao.update(seb);
					success = success && seb.isActive();

					request.setAttribute(INPUT_IGNORE_PARAMETERS, Boolean.TRUE);

					if (newUploadedFiles.size() > 0) {
						if (!this.unloadFiles(newUploadedFiles)) {
							String missed = "";
							Iterator iter = newUploadedFiles.keySet().iterator();
							while (iter.hasNext()) {
								missed += " " + newUploadedFiles.get(iter.next());
							}
							addPageMessage(
									respage.getString("uploaded_files_not_deleted_or_not_exist") + ": " + missed,
									request);
						}
					}
					if (!success) {

						if (updateFailedItems.size() > 0) {
							String mess = "";
							for (String ss : updateFailedItems) {
								mess += ss + ", ";
							}
							mess = mess.substring(0, mess.length() - 2);
							addPageMessage(resexception.getString("item_save_failed_because_database_error") + mess,
									request);
						} else {

							addPageMessage(resexception.getString("database_error"), request);
						}
						request.setAttribute(BEAN_DISPLAY, getDisplaySectionBeanDependingOnStage(section));
						session.removeAttribute(GROUP_HAS_DATA);
						session.removeAttribute(HAS_DATA_FLAG);
						session.removeAttribute(DDE_PROGESS);
						session.removeAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
						logger.debug("try to remove to_create_crf");
						session.removeAttribute("to_create_crf");

						if (autoCloseDataEntryPage) {
							storePageMessages(request);
							forwardPage(Page.AUTO_CLOSE_PAGE, request, response);
						} else {
							forwardPage(Page.LIST_STUDY_SUBJECTS_SERVLET, request, response);
						}
					} else {
						if (markSuccessfully) {
							addPageMessage(respage.getString("data_saved_CRF_marked_complete"), request);
							session.removeAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
							session.removeAttribute(GROUP_HAS_DATA);
							session.removeAttribute(HAS_DATA_FLAG);
							session.removeAttribute(DDE_PROGESS);
							session.removeAttribute("to_create_crf");

							request.setAttribute("eventId", new Integer(ecb.getStudyEventId()).toString());

							storePageMessages(request);
							if (autoCloseDataEntryPage) {
								forwardPage(Page.AUTO_CLOSE_PAGE, request, response);
							} else {
								if (!fp.getString(SAVE_NEXT).equals("")) {
									goToNextCRF(request, response, ecb, currentRole, ub, edcb, studyEventBean);
								} else {
									response.sendRedirect(Navigation.getSavedUrl(request));
								}
							}
						} else {
							// user clicked 'save'
							addPageMessage(respage.getString("data_saved_continue_entering_edit_later"), request);
							request.setAttribute(INPUT_EVENT_CRF, ecb);
							request.setAttribute(INPUT_EVENT_CRF_ID, new Integer(ecb.getId()).toString());
							// forward to the next section if the previous one
							// is not the last section
							if (!section.isLastSection()) {
								request.setAttribute(INPUT_SECTION, nextSec);
								request.setAttribute(INPUT_SECTION_ID, Integer.toString(nextSec.getId()));
								request.setAttribute(
										"tabId",
										Integer.toString(CrfShortcutsAnalyzer.getTabNum(allSections,
												nextSec.getId())));
								session.removeAttribute("mayProcessUploading");
							} else if (section.isLastSection()) {
								// already the last section, should go back to
								// view event page
								session.removeAttribute(GROUP_HAS_DATA);
								session.removeAttribute(HAS_DATA_FLAG);
								session.removeAttribute(DDE_PROGESS);
								session.removeAttribute("to_create_crf");
								session.removeAttribute("mayProcessUploading");

								request.setAttribute("eventId", new Integer(ecb.getStudyEventId()).toString());

								// Clinovo Ticket #173 start
								StdScheduler scheduler = getScheduler(request);
								CalendarLogic calLogic = new CalendarLogic(getDataSource(), scheduler);
								if (!isAdministrativeEditing()) {
									String message = calLogic.maxMinDaysValidator(seb);
									if (!"empty".equalsIgnoreCase(message)) {
										addPageMessage(message, request);
									}
								}
								// end

								storePageMessages(request);
								if (autoCloseDataEntryPage) {
									forwardPage(Page.AUTO_CLOSE_PAGE, request, response);
								} else {
									if (!fp.getString(SAVE_NEXT).equals("")) {
										goToNextCRF(request, response, ecb, currentRole, ub, edcb, studyEventBean);
									} else {
										response.sendRedirect(Navigation.getSavedUrl(request));
									}
								}
								return;
							}

							forwardPage(getServletPage(request), request, response);
						}
					}
				}

				SubjectEventStatusUtil.determineSubjectEventStates(sedb, ssb, ub, new DAOWrapper(getDataSource()));
			}
		}
	}

	private boolean isValidFormatOfDateToSave(FormProcessor fp, String itemName) {
		try {
			fp.getDateInputWithServerTimeOfDay(itemName);
		} catch (Exception e) {
			return false; // format is wrong
		}

		return true;
	}

	private void runHardEditChecks(List<DisplayItemWithGroupBean> allItems, boolean validate, DisplayItemService displayItemService, 
			FormProcessor fp, DiscrepancyValidator v, EventCRFBean ecb) {
		runValidations(allItems, validate, displayItemService, fp, null, v, null, ecb);
	}

	private void runSoftEditChecks(List<DisplayItemWithGroupBean> allItems, boolean validate, DisplayItemService displayItemService, 
			FormProcessor fp, RuleValidator ruleValidator, HashMap<String, ArrayList<String>> groupOrdinalPLusItemOid, EventCRFBean ecb) {
		runValidations(allItems, validate, displayItemService, fp, ruleValidator, null, groupOrdinalPLusItemOid, ecb);
	}
	
	private void runValidations(List<DisplayItemWithGroupBean> allItems, boolean validate, DisplayItemService displayItemService, 
			FormProcessor fp, RuleValidator ruleValidator, DiscrepancyValidator v, HashMap<String, ArrayList<String>> groupOrdinalPLusItemOid, EventCRFBean ecb) {
		for (int i = 0; i < allItems.size(); i++) {
			DisplayItemWithGroupBean diwg = allItems.get(i);
			if (diwg.isInGroup()) {
				// for the items in groups
				DisplayItemGroupBean dgb = diwg.getItemGroup();
				List<DisplayItemGroupBean> dbGroups = diwg.getDbItemGroups();
				List<DisplayItemGroupBean> formGroups = new ArrayList<DisplayItemGroupBean>();
				if (validate) {
					if (getCurrentDataEntryStage() != CurrentDataEntryStage.VIEW_DATA_ENTRY) {
						formGroups = displayItemService.loadFormValueForItemGroup(dgb, dbGroups, formGroups, fp.getRequest());
						if (ruleValidator != null) { 
							formGroups = getDisplayItemBeanValidator().validateDisplayItemGroupBean(dbGroups,
									formGroups, ruleValidator,groupOrdinalPLusItemOid, fp.getRequest());
						} else if (v != null) {
							formGroups = getDisplayItemBeanValidator().validateDisplayItemGroupBean(v, dbGroups, formGroups, fp.getRequest());
						}
						logger.debug("*** form group size after validation " + formGroups.size());
					}
				} else {
					formGroups = displayItemService.loadFormValueForItemGroup(dgb, dbGroups, formGroups, fp.getRequest());
				}
				diwg.setItemGroup(dgb);
				diwg.setItemGroups(formGroups);

				allItems.set(i, diwg);

			} else {
				DisplayItemBean dib = diwg.getSingleItem();
				if (validate) {
					if (ruleValidator != null) {
						dib = getDisplayItemBeanValidator().validateDisplayItemBean(dib, "", ruleValidator, groupOrdinalPLusItemOid, false, null,
								fp.getRequest());
					} else if (v != null) {
						dib = getDisplayItemBeanValidator().validateDisplayItemBean(v, dib, "", fp.getRequest());
					}
				} else if ( v != null) {
					dib = DataEntryUtil.loadFormValue(dib, fp.getRequest());
				}
				
				ArrayList children = dib.getChildren();
				for (int j = 0; j < children.size(); j++) {
					DisplayItemBean child = (DisplayItemBean) children.get(j);
					String itemName = DataEntryUtil.getInputName(child);
					child.loadFormValue(fp.getString(itemName));
					if (validate) { 
						if (ruleValidator != null) {
							child = getDisplayItemBeanValidator().validateDisplayItemBean(child, "", ruleValidator, groupOrdinalPLusItemOid,
									false, null, fp.getRequest());
						} else if (v != null) {
							child = getDisplayItemBeanValidator().validateDisplayItemBean(v, child, itemName, fp.getRequest());
						}
					} else if (v != null) {
						child = DataEntryUtil.loadFormValue(child, fp.getRequest());
					}
					children.set(j, child);
				}

				dib.setChildren(children);
				diwg.setSingleItem(DataEntryUtil.runDynamicsItemCheck(getDynamicsMetadataService(), dib, ecb));
				allItems.set(i, diwg);
			}
		}
	}
	
	private void savePartialSavedStatus(FormProcessor fp, EventCRFBean eventCRFBean, int sectionBeanId, int sSubjectId) {
		boolean markPartialSaved = fp.getString("markPartialSaved").equals(VALUE_YES);
		EventCRFSectionService eventCRFSectionService = (EventCRFSectionService) SpringServletAccess.getApplicationContext(
				getServletContext()).getBean("eventCRFSectionService");
		
		EventCRFSectionBean ecsb = eventCRFSectionService.findByEventCRFIdAndSectionId(eventCRFBean.getId(), sectionBeanId);
		ecsb.setEventCRFId(eventCRFBean.getId());
		ecsb.setSectionId(sectionBeanId);
		ecsb.setStudySubjectId(sSubjectId);
		if (ecsb.isPartialSaved() != markPartialSaved) {
			ecsb.setPartialSaved(markPartialSaved);
			eventCRFSectionService.saveEventCRFSectionBean(ecsb);
			
			boolean doesPSSectionExist = eventCRFSectionService.findAllPartiallySavedByEventCRFId(eventCRFBean.getId()).size() > 0;
			if (!eventCRFBean.getStatus().equals(Status.PARTIAL_DATA_ENTRY) && doesPSSectionExist) {
				eventCRFBean.setStatus(Status.PARTIAL_DATA_ENTRY);
			} else if (eventCRFBean.getStatus().equals(Status.PARTIAL_DATA_ENTRY) && !doesPSSectionExist) {
				eventCRFBean.setStatus(Status.AVAILABLE);
			}
		}
	}

	private void setPartialSavedSectionNames(HttpServletRequest request,
			Map<Integer, EventCRFSectionBean> sectionIdToEvCRFSection, DisplaySectionBean dsb) {
		List<String> partialSavedSectionNames = new ArrayList<String>();
		List<SectionBean> sectionBeanList = getSectionDAO().findAllByCRFVersionId(dsb.getCrfVersion().getId());
		for (SectionBean sectionBean : sectionBeanList) {
			if (sectionIdToEvCRFSection.get(sectionBean.getId()) != null) {
				partialSavedSectionNames.add(sectionBean.getName());
			}
		}
		request.setAttribute("partialSavedSectionNames", partialSavedSectionNames);
	}
	
	private void setPartialSaveParameters(HttpServletRequest request, int eventCRFId, DisplaySectionBean dsb) {
		EventCRFSectionService eventCRFSectionService = (EventCRFSectionService) SpringServletAccess.getApplicationContext(
				getServletContext()).getBean("eventCRFSectionService");
		EventCRFSectionBean ecsb = eventCRFSectionService.findByEventCRFIdAndSectionId(eventCRFId, dsb.getSection().getId());
		List<EventCRFSectionBean> psSectionsList = eventCRFSectionService.findAllPartiallySavedByEventCRFId(eventCRFId);
		Map<Integer, EventCRFSectionBean> sectionIdToEvCRFSection = eventCRFSectionService.getSectionIdToEvCRFSectionMap(psSectionsList);
		request.setAttribute("disableMarkCRFComplete", dsb.isLastSection() && (psSectionsList.size() > 1 
				|| (psSectionsList.size() == 1 	&& !sectionIdToEvCRFSection.containsKey(dsb.getSection().getId()))));
		request.setAttribute("psSectionsList", psSectionsList);
		request.setAttribute("sectionIdToEvCRFSection", sectionIdToEvCRFSection);
		request.setAttribute("markPartialSaved", ecsb.isPartialSaved()? "Yes" : "No");
		setPartialSavedSectionNames(request, sectionIdToEvCRFSection, dsb);
	}

	protected abstract void putDataEntryStageFlagToRequest(HttpServletRequest request);

	private void goToNextCRF(HttpServletRequest request, HttpServletResponse response, EventCRFBean ecb,
							 StudyUserRoleBean currentRole, UserAccountBean ub, EventDefinitionCRFBean edcb,
							 StudyEventBean studyEventBean) throws IOException {
		HttpSession session = request.getSession();
		StudyBean currentStudy = (StudyBean) session.getAttribute("study");
		EventDefinitionCRFBean currentEdcb = edcb;
		EventCRFBean eventCrf = getEventCRFService().getNextEventCRFForDataEntry(studyEventBean, currentEdcb, ub,
				currentRole, currentStudy);
		String redirectPath = null;
		try {
			redirectPath = getRedirectPathForNextCRF(request, eventCrf);
		} catch (Exception e) {
			logger.error("An error has occured during generation of URL for next crf for data entry.", e);
		}
		if (redirectPath != null) {
			response.sendRedirect(redirectPath);
		} else {
			if (Navigation.getSavedUrl(request).contains("AddNewSubject")) {
				forwardPage(Page.LIST_STUDY_SUBJECTS_SERVLET, request, response);
			} else {
				response.sendRedirect(Navigation.getLastVisitedURL(request));
			}
		}
	}

	private String getRedirectPathForNextCRF(HttpServletRequest request, EventCRFBean eventCrf)
			throws UnsupportedEncodingException {
		if (eventCrf != null) {
			if (eventCrf.getId() > 0
					&& (eventCrf.getEventDefinitionCrf().isDoubleEntry() || eventCrf.getEventDefinitionCrf()
					.isEvaluatedCRF())) {
				if (eventCrf.getStage() == DataEntryStage.DOUBLE_DATA_ENTRY
						|| eventCrf.getStage() == DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE) {
					String ddeUrl = request.getContextPath() + Page.DOUBLE_DATA_ENTRY_SERVLET.getFileName();
					ddeUrl += "?eventCRFId=" + eventCrf.getId();
					return ddeUrl;
				}
			}
			return buildIdeUrlForNextCRF(request, eventCrf);
		}
		return null;
	}

	private String buildIdeUrlForNextCRF(HttpServletRequest request, EventCRFBean eventCrf) {
		StringBuilder ideUrl = new StringBuilder("");
		if (eventCrf.getId() > 0) {
			ideUrl.append(request.getContextPath()).append(Page.INITIAL_DATA_ENTRY_SERVLET.getFileName());
			ideUrl.append("?eventCRFId=").append(eventCrf.getId());
		} else {
			StudySubjectBean ssb = (StudySubjectBean) getStudySubjectDAO().findByPK(
					eventCrf.getStudyEventBean().getStudySubjectId());
			ideUrl.append(request.getContextPath()).append(Page.INITIAL_DATA_ENTRY_SERVLET.getFileName());
			ideUrl.append("?action=ide_s&eventDefinitionCRFId=").append(eventCrf.getEventDefinitionCrf().getId());
			ideUrl.append("&studyEventId=").append(eventCrf.getStudyEventBean().getId());
			ideUrl.append("&subjectId=").append(ssb.getSubjectId());
			ideUrl.append("&eventCRFId=").append(eventCrf.getId());
			ideUrl.append("&crfVersionId=").append(eventCrf.getEventDefinitionCrf().getDefaultVersionId());
			ideUrl.append("&exitTo=").append(Page.ENTER_DATA_FOR_STUDY_EVENT_SERVLET.getFileName().replace("/", ""))
					.append("?eventId=").append(eventCrf.getStudyEventBean().getId());
		}
		return ideUrl.toString();
	}

	private void sendEmailWithCRFReport(CRFVersionBean crfVersionBean, CRFBean crfBean, StudySubjectBean ssb,
										EventDefinitionCRFBean edcb, EventCRFBean ecb, HttpServletRequest request) {
		Locale locale = LocaleResolver.getLocale(request);
		StudyBean currentStudy = (StudyBean) request.getSession().getAttribute("study");
		ReportCRFService reportCRFService = (ReportCRFService) SpringServletAccess.getApplicationContext(
				getServletContext()).getBean("reportCRFService");
		try {
			String urlPath = request.getRequestURL().toString().replaceAll(request.getServletPath(), "");
			String sysPath = this.getServletContext().getRealPath("/");
			String studyOid = currentStudy.isSite() ? currentStudy.getParentStudyOid() : currentStudy.getOid();
			String dataPath = SQLInitServlet.getField("filePath") + ReportCRFService.CRF_REPORT_DIR + File.separator
					+ studyOid + File.separator;

			reportCRFService.setUrlPath(urlPath);
			reportCRFService.setSysPath(sysPath);
			reportCRFService.setDataPath(dataPath);
			reportCRFService.setResword(resword);

			File dataPatchDir = new File(dataPath);
			if (!dataPatchDir.exists()) {
				dataPatchDir.mkdirs();
			}
			String reportFilePath = reportCRFService.createPDFReport(ecb.getId(), locale, getDynamicsMetadataService());

			if (!StringUtil.isBlank(reportFilePath) && "complete".equals(edcb.getEmailStep())) {
				String body = EmailUtil.getCRFReportMessageBody(currentStudy.getName(), locale);
				String[] files = {reportFilePath};
				String succcesMessage = respage.getString("crf_report_was_sent_successfully_message");
				String errorMessage = respage.getString("crf_report_was_not_sent_successfully_message");
				String subject = MessageFormat.format(respage.getString("crf_report_message"), ssb.getLabel(),
						crfBean.getName() + " " + crfVersionBean.getName());
				sendEmailWithAttach(edcb.getEmailTo(), EmailEngine.getAdminEmail(), subject, body.toString(), true,
						succcesMessage, errorMessage, true, files, request);
			}
		} catch (Exception e) {
			logger.error("Error occurs while creating a CRF-report");
			e.printStackTrace();
		}
	}

	private void removeFieldsValidationsForSubmittedDN(Validator v, HttpServletRequest request) {
		if (request.getSession().getAttribute(DataEntryServlet.NOTE_SUBMITTED) != null) {
			Map<Object, Boolean> newNotesMap = (Map<Object, Boolean>) request.getSession().getAttribute(
					DataEntryServlet.NOTE_SUBMITTED);
			for (Object fieldName : newNotesMap.keySet()) {
				if (fieldName instanceof String && !StringUtil.isBlank((String) fieldName)) {
					v.removeFieldValidations((String) fieldName);
				}
			}
		}
	}

	private void clearSession(HttpServletRequest request) {
		request.getSession().removeAttribute(WARNINGS_LIST);
		request.getSession().removeAttribute(CreateDiscrepancyNoteServlet.SUBMITTED_DNS_MAP);
		request.getSession().removeAttribute(CreateDiscrepancyNoteServlet.TRANSFORMED_SUBMITTED_DNS);
		request.getSession().removeAttribute(DNS_TO_TRANSFORM);
	}

	private void createListOfDNsForTransformation(RuleValidator ruleValidator, DiscrepancyNoteDAO dndao,
												  HttpServletRequest request) {
		// we should transform submitted DNs to FVC, close them and turn off
		// ruleValidator for corresponding fields

		HashMap<String, DiscrepancyNoteBean> submittedDNs = (HashMap) request.getSession().getAttribute(
				CreateDiscrepancyNoteServlet.SUBMITTED_DNS_MAP);
		if (submittedDNs == null || submittedDNs.isEmpty()) {
			return;
		}

		List<DiscrepancyNoteBean> listOfDNsToTransform = (ArrayList<DiscrepancyNoteBean>) request.getSession()
				.getAttribute(DNS_TO_TRANSFORM);
		if (listOfDNsToTransform == null) {
			listOfDNsToTransform = new ArrayList<DiscrepancyNoteBean>();
		}

		HashMap ruleErrors = ruleValidator.validate();
		Set<String> fieldNames = new HashSet(submittedDNs.keySet());
		fieldNames.retainAll(ruleErrors.keySet());
		ruleValidator.dropErrors();
		List<DiscrepancyNoteBean> transformedDNs = getTransformedDNsList(request);
		Set<Integer> transformedSavedDNIds = getTransformedSavedDNIds(transformedDNs);
		Set<String> transformedUnSavedDNFieldNames = getTransformedUnSavedDNIds(transformedDNs);

		for (String fieldName : fieldNames) {
			DiscrepancyNoteBean dn = submittedDNs.get(fieldName);
			// for RFC we need to show validation error-message
			if (dn.getDiscrepancyNoteTypeId() != DiscrepancyNoteType.REASON_FOR_CHANGE.getId()) {
				ruleValidator.removeFieldValidations(fieldName);
			}
			if ((!transformedSavedDNIds.contains(dn.getId()) && dn.getId() > 0)
					|| (!transformedUnSavedDNFieldNames.contains(fieldName) && !StringUtil.isBlank(dn.getField()))) {
				listOfDNsToTransform.add(dn);
			}
		}

		request.getSession().setAttribute(DNS_TO_TRANSFORM, listOfDNsToTransform);
	}

	private void transformSubmittedDNsToFVC(UserAccountBean ub, DiscrepancyNoteDAO dndao, HttpServletRequest request) {
		// we should transform submitted DNs to FVC, close them

		List<DiscrepancyNoteBean> listDNsToTransform = (ArrayList<DiscrepancyNoteBean>) request.getSession()
				.getAttribute(DNS_TO_TRANSFORM);
		if (listDNsToTransform == null || listDNsToTransform.isEmpty()) {
			return;
		}

		List<DiscrepancyNoteBean> transformedDNs = getTransformedDNsList(request);
		Set<Integer> transformedSavedDNIds = getTransformedSavedDNIds(transformedDNs);
		Set<String> transformedUnSavedDNFieldNames = getTransformedUnSavedDNIds(transformedDNs);

		for (DiscrepancyNoteBean dn : listDNsToTransform) {
			// for RFC we need to show validation error-message
			if (!transformedSavedDNIds.contains(dn.getId()) && dn.getId() > 0) {
				if (dn.getDiscrepancyNoteTypeId() == DiscrepancyNoteType.ANNOTATION.getId()) {
					DiscrepancyNoteUtil.transformSavedAnnotationToFVC(dn, ub, ResolutionStatus.UPDATED.getId(), dndao);
				}
				if (dn.getDiscrepancyNoteTypeId() == DiscrepancyNoteType.REASON_FOR_CHANGE.getId()) {
					DiscrepancyNoteUtil.transformSavedRFCToFVC(dn, ub, ResolutionStatus.UPDATED.getId(), dndao);
				}
				transformedDNs.add(dn);
			} else if (!transformedUnSavedDNFieldNames.contains(dn.getField()) && !StringUtil.isBlank(dn.getField())) {
				if (dn.getDiscrepancyNoteTypeId() == DiscrepancyNoteType.ANNOTATION.getId()) {
					DiscrepancyNoteUtil.transformAnnotationToFVC(dn, ub, ResolutionStatus.UPDATED.getId());
					transformedDNs.add(dn);
				}
				if (dn.getDiscrepancyNoteTypeId() == DiscrepancyNoteType.FAILEDVAL.getId()) {
					transformedDNs.add(dn);
				}
			}
		}

		request.getSession().setAttribute(CreateDiscrepancyNoteServlet.TRANSFORMED_SUBMITTED_DNS, transformedDNs);
	}

	private void resetCodedItemTerms(List<DisplayItemWithGroupBean> allItems, ItemDataDAO iddao, EventCRFBean ecrfBean,
									 ArrayList<DisplayItemBean> changedItemsList) throws Exception {

		CodedTermValidator codedTermValidator = new CodedTermValidator(getDataSource(), getCodedItemService());

		for (int i = 0; i < allItems.size(); i++) {
			DisplayItemWithGroupBean diwb = allItems.get(i);
			if (diwb.isInGroup()) {
				List<DisplayItemGroupBean> dgbs = diwb.getItemGroups();
				for (int j = 0; j < dgbs.size(); j++) {
					DisplayItemGroupBean displayGroup = dgbs.get(j);
					List<DisplayItemBean> items = displayGroup.getItems();
					for (DisplayItemBean item : items) {
						if (item.getItem().getDataType().equals(ItemDataType.CODE)) {
							codedTermValidator.validateCodedTerm(changedItemsList, item, ecrfBean);
						}
					}
				}
			} else {
				DisplayItemBean dib = diwb.getSingleItem();
				if (dib.getItem().getDataType().equals(ItemDataType.CODE)) {
					codedTermValidator.validateCodedTerm(changedItemsList, dib, ecrfBean);
				}
			}
		}
	}

	protected void setReasonForChangeError(HashMap errors, ItemDataBean idb, String formName, String error,
										   HttpServletRequest request) {
		DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(getDataSource());
		Set<String> setOfItemNamesWithRFCErrors = request.getAttribute("setOfItemNamesWithRFCErrors") == null
				? new TreeSet<String>()
				: (TreeSet<String>) request.getAttribute("setOfItemNamesWithRFCErrors");
		if (getNumberOfNotesForField(idb.getId(), formName, dndao, request) > 0) {

			logger.debug("has a note in db " + formName);

			/*
			 * Having existing notes is not enough to let it pass through after changing data. There has to be a
			 * DiscrepancyNote for the latest changed data
			 */
			HashMap<String, Boolean> noteSubmitted = (HashMap<String, Boolean>) request.getSession().getAttribute(
					DataEntryServlet.NOTE_SUBMITTED);
			if ((noteSubmitted == null || noteSubmitted.get(formName) == null || !(Boolean) noteSubmitted.get(formName))
					&& (noteSubmitted == null || noteSubmitted.get(idb.getId()) == null || !(Boolean) noteSubmitted
					.get(idb.getId()))) {
				errors.put(formName, error);
				setOfItemNamesWithRFCErrors.add(formName);
			}

		} else {
			logger.debug("setting an error for " + formName);
			errors.put(formName, error);
			setOfItemNamesWithRFCErrors.add(formName);
		}
		request.setAttribute("setOfItemNamesWithRFCErrors", setOfItemNamesWithRFCErrors);
	}

	private static int getNumberOfNotesForField(int itemDataBeanId, String formName, DiscrepancyNoteDAO dndao,
												HttpServletRequest request) {
		int existingNotes = dndao.findNumExistingNotesForItem(itemDataBeanId);
		FormDiscrepancyNotes fdn = (FormDiscrepancyNotes) request.getSession().getAttribute(
				AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);

		if (existingNotes <= 0 && fdn != null) {
			existingNotes = fdn.getNotes(formName).size();
		}

		return existingNotes;
	}

	/**
	 * Get the input beans - the EventCRFBean and the SectionBean. For both beans, look first in the request attributes
	 * to see if the bean has been stored there. If not, look in the parameters for the bean id, and then retrieve the
	 * bean from the database. The beans are stored as protected class members.
	 *
	 * @param request HttpServletRequest
	 */
	protected void getInputBeans(HttpServletRequest request) throws InsufficientPermissionException {

		HttpSession session = request.getSession();
		StudyBean currentStudy = (StudyBean) session.getAttribute("study");

		FormProcessor fp = new FormProcessor(request);
		EventCRFDAO ecdao = new EventCRFDAO(getDataSource());

		SectionDAO sdao = new SectionDAO(getDataSource());
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);
		if (ecb == null) {
			int eventCRFId = fp.getInt(INPUT_EVENT_CRF_ID, true);
			logger.debug("found event crf id: " + eventCRFId);
			if (eventCRFId > 0) {
				logger.debug("***NOTE*** that we didnt have to create an event crf because we already have one: "
						+ eventCRFId);
				// there is an event CRF already, only need to update
				ecb = (EventCRFBean) ecdao.findByPK(eventCRFId);
				int crfVersionId = fp.getInt(INPUT_CRF_VERSION_ID);
				if (ecb.isNotStarted() && crfVersionId > 0 && crfVersionId != ecb.getCRFVersionId()) {
					ecb.setCRFVersionId(crfVersionId);
					ecb = (EventCRFBean) ecdao.update(ecb);
				}
				int studyEventId = fp.getInt(INPUT_STUDY_EVENT_ID);
				request.setAttribute(INPUT_EVENT_CRF, ecb);
				if (studyEventId > 0) {
					StudyEventDAO sedao = new StudyEventDAO(getDataSource());
					StudyEventBean sEvent = (StudyEventBean) sedao.findByPK(studyEventId);
					ecb = updateECB(sEvent, request);
				}
				session.setAttribute("ecb", ecb);
				request.setAttribute(INPUT_EVENT_CRF, ecb);
			} else {
				// CRF id <=0, so we need to create a new CRF
				// use toCreateCRF as a flag to prevent user to submit event CRF
				// more than once
				// for example, user reloads the page
				String toCreateCRF = (String) session.getAttribute("to_create_crf");
				if (StringUtil.isBlank(toCreateCRF) || "0".equals(toCreateCRF)) {
					session.setAttribute("to_create_crf", "1");
				}
				try {
					logger.debug("Initial: to create an event CRF.");
					String toCreateCRF1 = (String) session.getAttribute("to_create_crf");
					if (!StringUtil.isBlank(toCreateCRF1) && "1".equals(toCreateCRF1)) {
						ecb = createEventCRF(request, fp);
						session.setAttribute("ecb", ecb);
						request.setAttribute(INPUT_EVENT_CRF, ecb);
						session.setAttribute("to_create_crf", "0");
					} else {
						ecb = (EventCRFBean) session.getAttribute("ecb");
					}

				} catch (InconsistentStateException ie) {
					ie.printStackTrace();
					addPageMessage(ie.getOpenClinicaMessage(), request);
					throw new InsufficientPermissionException(Page.LIST_STUDY_SUBJECTS_SERVLET,
							ie.getOpenClinicaMessage(), "1");
				} catch (NullPointerException ne) {
					ne.printStackTrace();
					addPageMessage(ne.getMessage(), request);
					throw new InsufficientPermissionException(Page.LIST_STUDY_SUBJECTS_SERVLET, ne.getMessage(), "1");
				}
			}
		}
		// added to allow sections shown on this page
		DisplayTableOfContentsBean displayBean = new DisplayTableOfContentsBean();
		displayBean = getDisplayBean(ecb);
		request.setAttribute(TOC_DISPLAY, displayBean);

		int sectionId = fp.getInt(INPUT_SECTION_ID, true);
		ArrayList sections;
		if (sectionId <= 0) {
			StudyEventDAO studyEventDao = new StudyEventDAO(getDataSource());
			int maximumSampleOrdinal = studyEventDao.getMaxSampleOrdinal(displayBean.getStudyEventDefinition(),
					displayBean.getStudySubject());
			request.setAttribute("maximumSampleOrdinal", maximumSampleOrdinal);
			sections = sdao.findAllByCRFVersionId(ecb.getCRFVersionId());
			sectionId = getFirstSectionId(sections);
		}
		SectionBean sb = new SectionBean();
		if (sectionId > 0) {
			sb = (SectionBean) sdao.findByPK(sectionId);
		}

		int tabId = fp.getInt("tabId", true);
		if (tabId <= 0) {
			tabId = 1;
		}
		request.setAttribute(INPUT_TAB, new Integer(tabId));
		request.setAttribute(SECTION_BEAN, sb);
	}

	/**
	 * Tries to check if a seciton has item groups.
	 *
	 * @param fp  FormProcessor
	 * @param ecb EventCRFBean
	 * @return boolean
	 */
	protected boolean checkGroups(FormProcessor fp, EventCRFBean ecb) {
		int sectionId = fp.getInt(INPUT_SECTION_ID, true);
		SectionDAO sdao = new SectionDAO(getDataSource());
		if (sectionId <= 0) {
			ArrayList sections = sdao.findAllByCRFVersionId(ecb.getCRFVersionId());
			sectionId = getFirstSectionId(sections);
		}

		// we will look into db to see if any repeating items for this CRF
		// section
		ItemGroupDAO igdao = new ItemGroupDAO(getDataSource());
		// find any item group which doesn't equal to 'Ungrouped'
		ItemGroupBean itemGroup = igdao.findTopOneGroupBySectionId(sectionId);
		if (itemGroup != null && itemGroup.getId() > 0) {
			logger.trace("This section has group");
			return true;
		}
		return false;

	}

	/**
	 * Creates a new Event CRF or update the exsiting one, that is, an event CRF can be created but not item data yet,
	 * in this case, still consider it is not started(called uncompleted before).
	 *
	 * @param request HttpServletRequest
	 * @param fp      FormProcessor
	 * @return EventCRFBean
	 * @throws Exception an Exception
	 */
	private EventCRFBean createEventCRF(HttpServletRequest request, FormProcessor fp) throws InconsistentStateException {

		UserAccountBean ub = (UserAccountBean) request.getSession().getAttribute(USER_BEAN_NAME);
		StudyBean currentStudy = (StudyBean) request.getSession().getAttribute("study");
		EventCRFBean ecb;
		EventCRFDAO ecdao = new EventCRFDAO(getDataSource());

		int crfVersionId = fp.getInt(INPUT_CRF_VERSION_ID);

		logger.trace("***FOUND*** crfversionid: " + crfVersionId);
		int studyEventId = fp.getInt(INPUT_STUDY_EVENT_ID);
		int eventDefinitionCRFId = fp.getInt(INPUT_EVENT_DEFINITION_CRF_ID);
		int subjectId = fp.getInt(INPUT_SUBJECT_ID);
		int eventCRFId = fp.getInt(INPUT_EVENT_CRF_ID);

		logger.trace("look specifically wrt event crf id: " + eventCRFId);

		logger.trace("Creating event CRF.  Study id: " + currentStudy.getId() + "; CRF Version id: " + crfVersionId
				+ "; Study Event id: " + studyEventId + "; Event Definition CRF id: " + eventDefinitionCRFId
				+ "; Subject: " + subjectId);

		StudySubjectDAO ssdao = new StudySubjectDAO(getDataSource());
		StudySubjectBean ssb = ssdao.findBySubjectIdAndStudy(subjectId, currentStudy);

		if (ssb.getId() <= 0) {
			logger.trace("throwing ISE with study subject bean id of " + ssb.getId());
			throw new InconsistentStateException(Page.LIST_STUDY_SUBJECTS_SERVLET,
					resexception.getString("begin_data_entry_without_event_but_subject"));
		}

		StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(getDataSource());
		StudyEventDefinitionBean sedb = seddao.findByEventDefinitionCRFId(eventDefinitionCRFId);
		if (sedb.getId() <= 0) {
			addPageMessage(resexception.getString("begin_data_entry_without_event_but_study"), request);
			throw new InconsistentStateException(Page.LIST_STUDY_SUBJECTS_SERVLET,
					resexception.getString("begin_data_entry_without_event_but_study"));
		}

		CRFVersionDAO cvdao = new CRFVersionDAO(getDataSource());
		EntityBean eb = cvdao.findByPK(crfVersionId);

		if (eb.getId() <= 0) {
			throw new InconsistentStateException(Page.LIST_STUDY_SUBJECTS_SERVLET,
					resexception.getString("begin_data_entry_without_event_but_CRF"));
		}

		StudyEventDAO sedao = new StudyEventDAO(getDataSource());
		StudyEventBean sEvent = (StudyEventBean) sedao.findByPK(studyEventId);
		StudyBean studyWithSED = currentStudy;
		if (currentStudy.getParentStudyId() > 0) {
			studyWithSED = new StudyBean();
			studyWithSED.setId(currentStudy.getParentStudyId());
		}

		AuditableEntityBean aeb = sedao.findByPKAndStudy(studyEventId, studyWithSED);

		if (aeb.getId() <= 0) {
			addPageMessage(resexception.getString("begin_data_entry_without_event_but_especified_event"), request);
			throw new InconsistentStateException(Page.LIST_STUDY_SUBJECTS_SERVLET,
					resexception.getString("begin_data_entry_without_event_but_especified_event"));
		}

		ecb = new EventCRFBean();
		if (eventCRFId == 0) {
			// no event CRF created yet
			ArrayList ecList = ecdao.findByEventSubjectVersion(sEvent, ssb, (CRFVersionBean) eb);
			if (ecList.size() > 0) {
				ecb = (EventCRFBean) ecList.get(0);
			} else {
				ecb.setAnnotations("");
				ecb.setCreatedDate(new Date());
				ecb.setCRFVersionId(crfVersionId);

				if (currentStudy.getStudyParameterConfig().getInterviewerNameDefault().equals("blank")) {
					ecb.setInterviewerName("");
				} else {
					// default will be event's owner name
					ecb.setInterviewerName(sEvent.getOwner().getName());

				}
				if (!currentStudy.getStudyParameterConfig().getInterviewDateDefault().equals("blank")) {
					if (sEvent.getDateStarted() != null) {
						ecb.setDateInterviewed(sEvent.getDateStarted());
						// default date
					} else {
						ecb.setDateInterviewed(null);
					}
				} else {
					ecb.setDateInterviewed(null);
				}

				// above depreciated, try without it
				ecb.setOwner(ub);

				ecb.setNotStarted(true);
				ecb.setStatus(Status.AVAILABLE);
				ecb.setCompletionStatusId(1);
				ecb.setStudySubjectId(ssb.getId());
				ecb.setStudyEventId(studyEventId);
				ecb.setValidateString("");
				ecb.setValidatorAnnotations("");

				ecb = (EventCRFBean) ecdao.create(ecb);
				logger.debug("*********CREATED EVENT CRF");
			}
		} else {
			// there is an event CRF already, only need to update
			ecb = (EventCRFBean) ecdao.findByPK(eventCRFId);
			ecb.setCRFVersionId(crfVersionId);
			ecb.setUpdatedDate(new Date());
			ecb.setUpdater(ub);
			ecb = updateECB(sEvent, request);
			ecb = (EventCRFBean) ecdao.update(ecb);
		}

		if (ecb.getId() <= 0) {
			addPageMessage(resexception.getString("new_event_CRF_not_created"), request);
			throw new InconsistentStateException(Page.LIST_STUDY_SUBJECTS_SERVLET,
					resexception.getString("new_event_CRF_not_created"));
		} else {
			sEvent.setUpdater(ub);
			sEvent.setUpdatedDate(new Date());
			sedao.update(sEvent);
		}

		return ecb;
	}

	/**
	 * @return <code>true</code> if processRequest should validate inputs when the user clicks the "Save" button,
	 * <code>false</code> otherwise.
	 */
	protected abstract boolean validateInputOnFirstRound();

	protected void validateSCDItemBean(DiscrepancyValidator v, DisplayItemBean dib) {
		ItemFormMetadataBean ibMeta = dib.getMetadata();
		ItemDataBean idb = dib.getData();
		if (StringUtil.isBlank(idb.getValue())) {
			if (ibMeta.isRequired() && dib.getIsSCDtoBeShown()) {
				v.addValidation(DataEntryUtil.getInputName(dib), Validator.IS_REQUIRED);
			}
		} else {
			validateShownSCDToBeHiddenSingle(v, dib);
		}
	}

	protected void validateShownSCDToBeHiddenSingle(DiscrepancyValidator v, DisplayItemBean dib) {
		String value = dib.getData().getValue();
		boolean hasDN = dib.getDiscrepancyNotes() != null && dib.getDiscrepancyNotes().size() > 0 ? true : false;
		if (value != null && value.length() > 0 && !dib.getIsSCDtoBeShown() && !hasDN) {
			String message = dib.getScdData().getScdItemMetadataBean().getMessage();
			Validation vl = new Validation(Validator.TO_HIDE_CONDITIONAL_DISPLAY);
			vl.setErrorMessage(message);
			v.addValidation(DataEntryUtil.getInputName(dib), vl);
		}
	}

	/**
	 * Writes data from the DisplayItemBean to the database. Note that if the bean contains an inactive ItemDataBean,
	 * the ItemDataBean is created; otherwise, the ItemDataBean is updated.
	 *
	 * @param dib     The DisplayItemBean from which to write data.
	 * @param iddao   The DAO to use to access the database.
	 * @param request HttpServletRequest
	 * @return <code>true</code> if the query succeeded, <code>false</code> otherwise.
	 * @throws Exception
	 */
	protected boolean writeToDB(DisplayItemBean dib, ItemDataDAO iddao, int ordinal, HttpServletRequest request)
			throws Exception {
		DynamicsMetadataService dynamicsMetadataService = getDynamicsMetadataService();
		ItemDataBean idb = dib.getData();
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);
		if (dib.getEditFlag() != null && "remove".equalsIgnoreCase(dib.getEditFlag())
				&& dynamicsMetadataService.isShown(idb.getItemId(), ecb, idb)) {
			dynamicsMetadataService.hideItem(dib.getMetadata(), ecb, idb);
		} else {
			if (getServletPage(request).equals(Page.DOUBLE_DATA_ENTRY_SERVLET)) {
				if (!dib.getMetadata().isShowItem()
						&& !(dib.getScdData().getScdItemMetadataBean().getScdItemFormMetadataId() > 0)
						&& idb.getValue().equals("")
						&& !dynamicsMetadataService.hasPassedDDE(dib.getMetadata(), ecb, idb)) {

					return true;
				}
			} else {

				if (!dib.getMetadata().isShowItem() && idb.getValue().equals("")
						&& !dynamicsMetadataService.isShown(dib.getItem().getId(), ecb, dib.getData())
						&& !(dib.getScdData().getScdItemMetadataBean().getScdItemFormMetadataId() > 0)
						&& !(dib.getItem().getItemDataTypeId() == ItemDataType.CODE.getId())) {

					logger.debug("*** not shown - not writing for idb id " + dib.getData().getId() + " and item id "
							+ dib.getItem().getId());
					return true;
				}
			}
		}

		return writeToDB(idb, dib, iddao, ordinal, request);
	}

	protected boolean writeToDB(ItemDataBean itemData, DisplayItemBean dib, ItemDataDAO iddao, int ordinal,
								HttpServletRequest request) throws Exception {
		ItemDataBean idb = itemData;
		UserAccountBean ub = (UserAccountBean) request.getSession().getAttribute(USER_BEAN_NAME);
		StudyBean currentStudy = (StudyBean) request.getSession().getAttribute("study");
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);
		idb.setItemId(dib.getItem().getId());
		idb.setEventCRFId(ecb.getId());

		if (idb.getValue().equals("")) {
			idb.setStatus(getBlankItemStatus());
		} else {
			idb.setStatus(getNonBlankItemStatus(request));
		}
		if (StringUtil.isBlank(dib.getEditFlag())) {

			if (!idb.isActive()) {
				// will this need to change for double data entry?
				idb.setOrdinal(ordinal);
				idb.setCreatedDate(new Date());
				idb.setOwner(ub);
				idb = (ItemDataBean) iddao.create(idb);

				// create coded items for event/crf
				if (getCodedItemService() != null) {
					getCodedItemService().createCodedItem(ecb, dib.getItem(), idb, currentStudy);
				}

			} else {

				idb.setUpdater(ub);
				// should we update the logic here for nonrepeats?
				logger.info("update item update_id " + idb.getUpdater().getId());
				idb = (ItemDataBean) iddao.updateValue(idb);
			}
		} else {

			// for the items in group, they have editFlag
			if ("add".equalsIgnoreCase(dib.getEditFlag())) {

				idb.setOrdinal(ordinal);
				idb.setCreatedDate(new Date());
				idb.setOwner(ub);
				logger.debug("create a new item data" + idb.getItemId() + idb.getValue());
				idb.setUpdater(ub);
				idb = (ItemDataBean) iddao.upsert(idb);

				if (getCodedItemService() != null) {
					getCodedItemService().createCodedItem(ecb, dib.getItem(), idb, currentStudy);
				}

			} else if ("edit".equalsIgnoreCase(dib.getEditFlag())) {

				idb.setUpdater(ub);

				logger.info("update item update_id " + idb.getUpdater().getId());
				// update tbh #5999, #5998; if an item_data was not included in
				// an import data, it won't exist; we need to check on item_data_id
				// to make sure we are running the correct command on the db
				if (idb.getId() != 0) {

					idb.setUpdatedDate(new Date());
					idb = (ItemDataBean) iddao.updateValue(idb);

				} else {

					idb.setCreatedDate(new Date());
					idb.setOrdinal(ordinal);
					idb.setOwner(ub);
					idb = (ItemDataBean) iddao.upsert(idb);
					logger.debug("just ran upsert! " + idb.getId());
				}

			} else if ("remove".equalsIgnoreCase(dib.getEditFlag())) {

				logger.debug("REMOVE an item data" + idb.getItemId() + idb.getValue());
				idb.setUpdater(ub);
				idb.setStatus(Status.DELETED);
				idb = (ItemDataBean) iddao.updateValueForRemoved(idb);

				DiscrepancyNoteDAO dnDao = new DiscrepancyNoteDAO(getDataSource());
				List dnNotesOfRemovedItem = dnDao.findExistingNotesForItemData(idb.getId());
				if (!dnNotesOfRemovedItem.isEmpty()) {
					DiscrepancyNoteBean itemParentNote = null;
					for (Object obj : dnNotesOfRemovedItem) {
						if (((DiscrepancyNoteBean) obj).getParentDnId() == 0) {
							itemParentNote = (DiscrepancyNoteBean) obj;
						}
					}
					DiscrepancyNoteBean dnb = new DiscrepancyNoteBean();
					if (itemParentNote != null) {
						dnb.setParentDnId(itemParentNote.getId());
						dnb.setDiscrepancyNoteTypeId(itemParentNote.getDiscrepancyNoteTypeId());
					}
					dnb.setResolutionStatusId(ResolutionStatus.CLOSED.getId());
					dnb.setStudyId(currentStudy.getId());
					dnb.setAssignedUserId(ub.getId());
					dnb.setOwner(ub);
					dnb.setEntityType(DiscrepancyNoteBean.ITEM_DATA);
					dnb.setEntityId(idb.getId());
					dnb.setCreatedDate(new Date());
					dnb.setColumn("value");
					dnb.setDescription("The item has been removed, this Discrepancy Note has been Closed.");
					dnDao.create(dnb);
					dnDao.createMapping(dnb);
					itemParentNote.setResolutionStatusId(ResolutionStatus.CLOSED.getId());
					dnDao.update(itemParentNote);
				}
			}
		}

		return idb.isActive();
	}

	protected String addAttachedFilePath(DisplayItemBean dib, String attachedFilePath) {
		String fileName = "";
		ItemDataBean idb = dib.getData();
		String itemDataValue = idb.getValue();
		String dbValue = dib.getDbData().getValue();
		ResponseSetBean rsb = dib.getMetadata().getResponseSet();
		org.akaza.openclinica.bean.core.ResponseType rt = rsb.getResponseType();
		if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.FILE) && itemDataValue.length() > 0) {
			File file = new File(itemDataValue);
			fileName = file.getName();
			if (itemDataValue.length() > fileName.length()) {
				idb.setValue(itemDataValue);
			} else {
				File f = new File(dbValue);
				fileName = f.getName();
				if (fileName.equals(itemDataValue) && dbValue.length() > itemDataValue.length()) {
					// since filename is unique by timestamp, re-upload will
					// append
					// another timestamp to the same filename
					idb.setValue(dbValue);
				} else {
					idb.setValue(attachedFilePath + itemDataValue);
					fileName = itemDataValue;
				}
			}
		}
		return fileName;
	}

	/**
	 * Retrieve the status which should be assigned to ItemDataBeans which have blank values for this data entry
	 * servlet.
	 */
	protected abstract Status getBlankItemStatus();

	// unavailable in admin. editing

	/**
	 * Retrieve the status which should be assigned to ItemDataBeans which have non-blank values for this data entry
	 * servlet.
	 *
	 * @param request HttpServletRequest
	 */
	protected abstract Status getNonBlankItemStatus(HttpServletRequest request);

	// unavailable in admin. editing

	/**
	 * Get the eventCRF's annotations as appropriate for this data entry servlet.
	 *
	 * @param request HttpServletRequest
	 */
	protected abstract String getEventCRFAnnotations(HttpServletRequest request);

	/**
	 * Set the eventCRF's annotations properties as appropriate for this data entry servlet.
	 *
	 * @param request HttpServletRequest
	 */
	protected abstract void setEventCRFAnnotations(String annotations, HttpServletRequest request);

	/**
	 * Retrieve the DisplaySectionBean which will be used to display the Event CRF Section on the JSP, and also is used
	 * to controll processRequest.
	 *
	 * @param request     HttpServletRequest
	 * @param isSubmitted isSubmitted
	 */
	protected DisplaySectionBean getDisplayBean(boolean hasGroup, HttpServletRequest request, boolean isSubmitted) throws Exception {
		return getDataEntryService(getServletContext()).getDisplayBean(hasGroup, isSubmitted,
				getServletPage(request), request);
	}

	/**
	 * @return The Page object which represents this servlet's JSP.
	 */
	protected abstract Page getJSPPage();

	/**
	 * @param request HttpServletRequest
	 * @return The Page object which represents this servlet.
	 */
	protected abstract Page getServletPage(HttpServletRequest request);

	protected void setUpPanel(HttpServletRequest request, DisplaySectionBean section) {
		StudyInfoPanel panel = getStudyInfoPanel(request);
		panel.reset();
		panel.setStudyInfoShown(false);
		panel.setOrderedData(true);

	}

	/*
	 * change to explicitly re-set the section bean after reviewing the disc note counts
	 */
	protected DisplaySectionBean populateNotesWithDBNoteCounts(FormDiscrepancyNotes discNotes,
															   List<DiscrepancyNoteThread> noteThreads, DisplaySectionBean section, HttpServletRequest request) {
		DynamicsMetadataService dynamicsMetadataService = getDynamicsMetadataService();
		StudyBean currentStudy = (StudyBean) request.getSession().getAttribute(STUDY);
		DiscrepancyNoteUtil dNoteUtil = new DiscrepancyNoteUtil();
		DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(getDataSource());
		ItemFormMetadataDAO ifmdao = new ItemFormMetadataDAO(getDataSource());
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);

		List<DiscrepancyNoteBean> ecNotes = dndao.findEventCRFDNotesFromEventCRF(ecb);
		List<DiscrepancyNoteBean> existingNameNotes = new ArrayList();
		List<DiscrepancyNoteBean> existingIntrvDateNotes = new ArrayList();

		ecNotes = filterNotesByUserRole(ecNotes, request);

		for (int i = 0; i < ecNotes.size(); i++) {
			DiscrepancyNoteBean dn = ecNotes.get(i);
			if (INTERVIEWER_NAME.equalsIgnoreCase(dn.getColumn())) {
				discNotes.setNumExistingFieldNotes(INPUT_INTERVIEWER, 1);
				request.setAttribute("hasNameNote", "yes");
				request.setAttribute(INTERVIEWER_NAME_NOTE, dn);
				if (dn.getParentDnId() == 0) {
					existingNameNotes.add(dn);
				}
			}

			if (DATE_INTERVIEWED.equalsIgnoreCase(dn.getColumn())) {
				discNotes.setNumExistingFieldNotes(INPUT_INTERVIEW_DATE, 1);
				request.setAttribute("hasDateNote", "yes");
				request.setAttribute(INTERVIEWER_DATE_NOTE, dn);
				if (dn.getParentDnId() == 0) {
					existingIntrvDateNotes.add(dn);
				}
			}
		}

		CrfShortcutsAnalyzer crfShortcutsAnalyzer = getCrfShortcutsAnalyzer(request, getItemSDVService());

		ArrayList notes = new ArrayList(discNotes.getNotes(INPUT_INTERVIEWER));
		notes.addAll(existingNameNotes);
		noteThreads = dNoteUtil.createThreadsOfParents(notes, currentStudy, null, -1);
		crfShortcutsAnalyzer.prepareCrfShortcutAnchors(crfShortcutsAnalyzer.getInterviewerDisplayItemBean(),
				noteThreads, false);

		notes = new ArrayList(discNotes.getNotes(INPUT_INTERVIEW_DATE));
		notes.addAll(existingIntrvDateNotes);
		noteThreads = dNoteUtil.createThreadsOfParents(notes, currentStudy, null, -1);
		crfShortcutsAnalyzer.prepareCrfShortcutAnchors(crfShortcutsAnalyzer.getInterviewDateDisplayItemBean(),
				noteThreads, false);

		setToolTipEventNotes(request);

		request.setAttribute("existingNameNotes", existingNameNotes);
		request.setAttribute("hasNameNote", existingNameNotes.size() > 0 ? "yes" : "");
		request.setAttribute("nameNoteResStatus",
				DiscrepancyNoteUtil.getDiscrepancyNoteResolutionStatus(existingNameNotes));

		request.setAttribute("existingIntrvDateNotes", existingIntrvDateNotes);
		request.setAttribute("hasDateNote", existingIntrvDateNotes.size() > 0 ? "yes" : "");
		request.setAttribute("IntrvDateNoteResStatus",
				DiscrepancyNoteUtil.getDiscrepancyNoteResolutionStatus(existingIntrvDateNotes));

		Map<Integer, List<DiscrepancyNoteBean>> itemDataDNCache = new HashMap<Integer, List<DiscrepancyNoteBean>>();
		List<DiscrepancyNoteBean> discrepancyNoteBeanList = dndao.findAllByEventCrfId(ecb.getId());
		for (DiscrepancyNoteBean discrepancyNoteBean : discrepancyNoteBeanList) {
			List<DiscrepancyNoteBean> dnList = itemDataDNCache.get(discrepancyNoteBean.getEntityId());
			if (dnList == null) {
				dnList = new ArrayList<DiscrepancyNoteBean>();
				itemDataDNCache.put(discrepancyNoteBean.getEntityId(), dnList);
			}
			dnList.add(discrepancyNoteBean);
		}

		Map<Integer, List<DiscrepancyNoteBean>> toolTipItemDataDNCache = new HashMap<Integer, List<DiscrepancyNoteBean>>();
		List<DiscrepancyNoteBean> toolTipDiscrepancyNoteBeanList = dndao.findExistingNotesForToolTipByEventCrfId(ecb
				.getId());
		for (DiscrepancyNoteBean discrepancyNoteBean : toolTipDiscrepancyNoteBeanList) {
			List<DiscrepancyNoteBean> dnList = toolTipItemDataDNCache.get(discrepancyNoteBean.getEntityId());
			if (dnList == null) {
				dnList = new ArrayList<DiscrepancyNoteBean>();
				toolTipItemDataDNCache.put(discrepancyNoteBean.getEntityId(), dnList);
			}
			dnList.add(discrepancyNoteBean);
		}

		List<DisplayItemWithGroupBean> allItems = section.getDisplayItemGroups();
		logger.debug("start to populate notes: " + section.getDisplayItemGroups().size());
		for (int k = 0; k < allItems.size(); k++) {
			DisplayItemWithGroupBean itemWithGroup = allItems.get(k);

			if (itemWithGroup.isInGroup()) {
				logger.debug("group item DNote...");
				List<DisplayItemGroupBean> digbs = itemWithGroup.getItemGroups();
				logger.trace("digbs size: " + digbs.size());
				for (int i = 0; i < digbs.size(); i++) {
					DisplayItemGroupBean displayGroup = digbs.get(i);
					List<DisplayItemBean> items = displayGroup.getItems();
					for (int j = 0; j < items.size(); j++) {
						DisplayItemBean dib = items.get(j);
						boolean manual = i != 0;
						String inputName = DataEntryUtil.getGroupItemInputName(displayGroup, i, dib, manual);
						int itemDataId = 0;
						if (i <= itemWithGroup.getDbItemGroups().size() - 1) {
							itemDataId = dib.getData().getId();
						} else {
							dib.getData().setId(0);
						}

						List<DiscrepancyNoteBean> toolTipDNotes = toolTipItemDataDNCache.get(itemDataId);
						toolTipDNotes = toolTipDNotes == null ? new ArrayList() : new ArrayList(toolTipDNotes);

						List dbNotes = itemDataDNCache.get(itemDataId);
						List parentNotes = dbNotes == null ? new ArrayList() : new ArrayList(dbNotes);
						dbNotes = dbNotes == null ? new ArrayList() : new ArrayList(dbNotes);

						dbNotes = filterNotesByUserRole(dbNotes, request);

						notes = new ArrayList(discNotes.getNotes(inputName));
						notes.addAll(dbNotes);
						noteThreads = dNoteUtil.createThreadsOfParents(notes, currentStudy, null, -1);
						discNotes.setNumExistingFieldNotes(inputName, dbNotes.size());
						dib.setNumDiscrepancyNotes(dbNotes.size() + notes.size());
						dib.setDiscrepancyNoteStatus(getDiscrepancyNoteResolutionStatus(request, dndao, itemDataId,
								discNotes.getNotes(inputName)));
						dib = setTotals(dib, itemDataId, toolTipDNotes, parentNotes, notes, ecb.getId(), request);
						crfShortcutsAnalyzer.prepareCrfShortcutAnchors(dib, noteThreads, false);
						logger.debug("dib note size:" + dib.getNumDiscrepancyNotes() + " " + dib.getData().getId()
								+ " " + inputName);
						items.set(j, dib);
					}
					displayGroup.setItems(items);
					digbs.set(i, displayGroup);
				}
				itemWithGroup.setItemGroups(digbs);

			} else {
				logger.trace("single item db note");
				DisplayItemBean dib = itemWithGroup.getSingleItem();
				int itemDataId = dib.getData().getId();
				int itemId = dib.getItem().getId();
				String inputFieldName = "input" + itemId;

				List<DiscrepancyNoteBean> toolTipDNotes = toolTipItemDataDNCache.get(itemDataId);
				toolTipDNotes = toolTipDNotes == null ? new ArrayList() : new ArrayList(toolTipDNotes);

				List dbNotes = itemDataDNCache.get(itemDataId);
				List parentNotes = dbNotes == null ? new ArrayList() : new ArrayList(dbNotes);
				dbNotes = dbNotes == null ? new ArrayList() : new ArrayList(dbNotes);

				notes = new ArrayList(discNotes.getNotes(inputFieldName));

				dbNotes = filterNotesByUserRole(dbNotes, request);

				notes.addAll(dbNotes);
				discNotes.setNumExistingFieldNotes(inputFieldName, dbNotes.size());
				dib.setNumDiscrepancyNotes(dbNotes.size() + notes.size());
				dib.setDiscrepancyNoteStatus(getDiscrepancyNoteResolutionStatus(request, dndao, itemDataId,
						discNotes.getNotes(inputFieldName)));
				dib = setTotals(dib, itemDataId, toolTipDNotes, parentNotes, discNotes.getNotes(inputFieldName),
						ecb.getId(), request);
				noteThreads = dNoteUtil.createThreadsOfParents(notes, currentStudy, null, -1);
				crfShortcutsAnalyzer.prepareCrfShortcutAnchors(dib, noteThreads, false);

				ArrayList childItems = dib.getChildren();

				for (int j = 0; j < childItems.size(); j++) {
					DisplayItemBean child = (DisplayItemBean) childItems.get(j);
					int childItemDataId = child.getData().getId();
					int childItemId = child.getItem().getId();
					String childInputFieldName = "input" + childItemId;

					List<DiscrepancyNoteBean> toolTipChildDNotes = toolTipItemDataDNCache.get(childItemDataId);
					toolTipChildDNotes = toolTipChildDNotes == null ? new ArrayList() : new ArrayList(
							toolTipChildDNotes);

					List dbChildNotes = itemDataDNCache.get(childItemDataId);
					List parentChildNotes = dbChildNotes == null ? new ArrayList() : new ArrayList(dbChildNotes);
					dbChildNotes = dbChildNotes == null ? new ArrayList() : new ArrayList(dbChildNotes);

					List childNotes = new ArrayList(discNotes.getNotes(inputFieldName));

					dbChildNotes = filterNotesByUserRole(dbChildNotes, request);

					childNotes.addAll(dbNotes);
					noteThreads = dNoteUtil.createThreadsOfParents(notes, currentStudy, null, -1);
					discNotes.setNumExistingFieldNotes(childInputFieldName, dbChildNotes.size());
					child.setNumDiscrepancyNotes(dbChildNotes.size() + childNotes.size());
					child.setDiscrepancyNoteStatus(getDiscrepancyNoteResolutionStatus(request, dndao, childItemDataId,
							discNotes.getNotes(childInputFieldName)));
					child = setTotals(child, childItemDataId, toolTipChildDNotes, parentChildNotes,
							discNotes.getNotes(childInputFieldName), ecb.getId(), request);
					crfShortcutsAnalyzer.prepareCrfShortcutAnchors(child, noteThreads, false);
					childItems.set(j, child);
				}
				dib.setChildren(childItems);
				itemWithGroup.setSingleItem(DataEntryUtil.runDynamicsItemCheck(getDynamicsMetadataService(), dib, ecb));
			}
			allItems.set(k, itemWithGroup);
		}

		section.setDisplayItemGroups(allItems);
		return section;
	}

	private void setToolTipEventNotes(HttpServletRequest request) {
		DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(getDataSource());
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);
		List<DiscrepancyNoteBean> ecNotes = dndao.findEventCRFDNotesToolTips(ecb);

		ecNotes = filterNotesByUserRole(ecNotes, request);

		ArrayList<DiscrepancyNoteBean> nameNotes = new ArrayList();
		ArrayList<DiscrepancyNoteBean> dateNotes = new ArrayList();

		for (DiscrepancyNoteBean dn : ecNotes) {

			if (INTERVIEWER_NAME.equalsIgnoreCase(dn.getColumn())) {
				nameNotes.add(dn);
			}

			if (DATE_INTERVIEWED.equalsIgnoreCase(dn.getColumn())) {
				dateNotes.add(dn);
			}
		}
		request.setAttribute("nameNotes", nameNotes);
		request.setAttribute("intrvDates", dateNotes);
	}

	/**
	 * To set the totals of each resolution status on the DisplayItemBean for each item.
	 *
	 * @param dib
	 * @param notes
	 * @param ecbId TODO
	 */
	private DisplayItemBean setTotals(DisplayItemBean dib, int itemDataId, List<DiscrepancyNoteBean> toolTipDNotes,
									  List parentNotes, ArrayList<DiscrepancyNoteBean> notes, int ecbId, HttpServletRequest request) {

		int totNew = 0, totRes = 0, totClosed = 0, totUpdated = 0, totNA = 0;
		boolean hasOtherThread = false;

		DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(getDataSource());

		toolTipDNotes = filterNotesByUserRole(toolTipDNotes, request);

		dib.setDiscrepancyNotes(toolTipDNotes);

		for (DiscrepancyNoteBean obj : dib.getDiscrepancyNotes()) {
			DiscrepancyNoteBean note = obj;

			if (note.getParentDnId() == 0) {
				totNew++;
			}
		}

		// Adding this to show the value of only parent threads on discrepancy notes tool tip
		for (Object obj : parentNotes) {
			DiscrepancyNoteBean note = (DiscrepancyNoteBean) obj;
			/*
			 * We would only take the resolution status of the parent note of any note thread. If there are more than
			 * one note thread, the thread with the worst resolution status will be taken.
			 */

			if (note.getParentDnId() == 0) {
				if (hasOtherThread) {
					totNew++;
				}
				hasOtherThread = true;
			}
		}

		AuditDAO adao = new AuditDAO(getDataSource());
		ArrayList itemAuditEvents = adao.checkItemAuditEventsExist(dib.getItem().getId(), "item_data", ecbId);
		if (itemAuditEvents.size() > 0) {
			AuditBean itemFirstAudit = (AuditBean) itemAuditEvents.get(0);
			String firstRFC = itemFirstAudit.getReasonForChange();
			String oldValue = itemFirstAudit.getOldValue();
			if (firstRFC != null && "initial value".equalsIgnoreCase(firstRFC)
					&& (oldValue == null || oldValue.isEmpty())) {
				dib.getData().setAuditLog(false);
			} else {
				dib.getData().setAuditLog(true);
			}
		}

		dib.setTotNew(totNew); // totNew is used for parent thread count
		dib.setTotRes(totRes);
		dib.setTotUpdated(totUpdated);
		dib.setTotClosed(totClosed);
		dib.setTotNA(totNA);

		return dib;
	}

	/**
	 * The following methods are for 'mark CRF complete'.
	 *
	 * @param request HttpServletRequest
	 * @return boolean
	 */

	protected boolean markCRFComplete(HttpServletRequest request) throws Exception {

		HttpSession session = request.getSession();
		UserAccountBean ub = (UserAccountBean) request.getSession().getAttribute(USER_BEAN_NAME);
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);
		EventDefinitionCRFBean edcb = (EventDefinitionCRFBean) request.getAttribute(EVENT_DEF_CRF_BEAN);
		EventCRFDAO ecdao = new EventCRFDAO(getDataSource());
		ItemDataDAO iddao = new ItemDataDAO(getDataSource());
		getEventCRFBean(request);
		getEventDefinitionCRFBean(request);
		DataEntryStage stage = ecb.getStage();

		logger.trace("inout_event_crf_id:" + ecb.getId());

		if (stage.equals(DataEntryStage.UNCOMPLETED) || stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE)
				|| stage.equals(DataEntryStage.LOCKED)) {
			addPageMessage(respage.getString("not_mark_CRF_complete1"), request);
			return false;
		}

		if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE) || stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY)) {

			if (!(edcb.isDoubleEntry() || edcb.isEvaluatedCRF())) {
				addPageMessage(respage.getString("not_mark_CRF_complete2"), request);
				return false;
			}
		}

		if (!isEachRequiredFieldFillout(request)) {
			addPageMessage(respage.getString("not_mark_CRF_complete4"), request);
			return false;
		}

		Status newStatus = ecb.getStatus();
		boolean ide = true;
		if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY) && (edcb.isDoubleEntry() || edcb.isEvaluatedCRF())) {
			newStatus = Status.PENDING;
			ecb.setUpdaterId(ub.getId());
			ecb.setUpdater(ub);
			ecb.setUpdatedDate(new Date());
			ecb.setDateCompleted(new Date());
		} else if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY) && !(edcb.isDoubleEntry() || edcb.isEvaluatedCRF())) {
			newStatus = Status.UNAVAILABLE;
			ecb.setUpdaterId(ub.getId());
			ecb.setUpdater(ub);
			ecb.setUpdatedDate(new Date());
			ecb.setDateCompleted(new Date());
			ecb.setDateValidateCompleted(new Date());

		} else if (stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE)
				&& (edcb.isDoubleEntry() || edcb.isEvaluatedCRF())) {
			newStatus = Status.UNAVAILABLE;
			ecb.setUpdaterId(ub.getId());
			ecb.setUpdater(ub);
			ecb.setUpdatedDate(new Date());
			ecb.setDateCompleted(new Date());
			ecb.setDateValidateCompleted(new Date());
			ide = false;
		} else if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE)
				|| stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY)) {
			newStatus = Status.UNAVAILABLE;
			ecb.setDateValidateCompleted(new Date());
			ide = false;
		}

		// for the non-reviewed sections, no item data in DB yet, need to
		// create them
		if (!isEachSectionReviewedOnce(request)) {
			if (!saveItemsToMarkComplete(newStatus, request)) {
				addPageMessage(respage.getString("not_mark_CRF_complete3"), request);
				return false;
			}
		}
		ecb.setStatus(newStatus);
		/*
		 * Marking the data entry as signed if the corresponding EventDefinitionCRF is being enabled for electronic
		 * signature.
		 */
		if (edcb.isElectronicSignature()) {
			ecb.setElectronicSignatureStatus(true);
		}
		ecb = (EventCRFBean) ecdao.update(ecb);
		// note the below statement only updates the DATES, not the STATUS
		ecdao.markComplete(ecb, ide);

		// update all the items' status to complete
		iddao.updateStatusByEventCRF(ecb, newStatus);

		// change status for study event
		StudyDAO sdao = new StudyDAO(getDataSource());
		StudySubjectDAO ssdao = new StudySubjectDAO(getDataSource());
		StudyEventDAO sedao = new StudyEventDAO(getDataSource());
		StudyEventBean seb = (StudyEventBean) sedao.findByPK(ecb.getStudyEventId());
		seb.setUpdatedDate(new Date());
		seb.setUpdater(ub);

		SubjectEventStatus previousSEStatus = (SubjectEventStatus) request.getAttribute("previousSEStatus");

		EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(getDataSource());
		DiscrepancyNoteDAO discDao = new DiscrepancyNoteDAO(getDataSource());
		StudyBean study = (StudyBean) session.getAttribute("study");
		SubjectEventStatusUtil.determineSubjectEventState(seb, new DAOWrapper(getDataSource()));
		seb = (StudyEventBean) sedao.update(seb);

		// Clinovo calendar func
		if (seb.getSubjectEventStatus() == SubjectEventStatus.COMPLETED
				&& previousSEStatus != SubjectEventStatus.COMPLETED) {
			System.out.println("AutoSchedule");
			StdScheduler scheduler = getScheduler(request);
			CalendarLogic calLogic = new CalendarLogic(getDataSource(), scheduler);
			calLogic.scheduleSubjectEvents(seb);
			if (!isAdministrativeEditing()) {
				String message = calLogic.maxMinDaysValidator(seb);
				if (!"empty".equalsIgnoreCase(message)) {
					addPageMessage(message, request);
				}
			}
		}
		// end

		request.setAttribute(INPUT_EVENT_CRF, ecb);
		request.setAttribute(EVENT_DEF_CRF_BEAN, edcb);
		return true;
	}

	private void getEventCRFBean(HttpServletRequest request) {
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);
		FormProcessor fp = new FormProcessor(request);
		int eventCRFId = fp.getInt(INPUT_EVENT_CRF_ID);

		EventCRFDAO ecdao = new EventCRFDAO(getDataSource());
		ecb = (EventCRFBean) ecdao.findByPK(eventCRFId);
	}

	protected boolean isEachRequiredFieldFillout(HttpServletRequest request) {
		HttpSession session = request.getSession();
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);
		DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(getDataSource());
		// need to update this method to accomodate dynamics, tbh
		ItemDataDAO iddao = new ItemDataDAO(getDataSource());
		ItemDAO idao = new ItemDAO(getDataSource());
		ItemFormMetadataDAO itemFormMetadataDao = new ItemFormMetadataDAO(getDataSource());
		int allRequiredNum = idao.findAllRequiredByCRFVersionId(ecb.getCRFVersionId());
		int allRequiredFilledOut = iddao.findAllRequiredByEventCRFId(ecb);
		int allRequiredButHidden = itemFormMetadataDao.findCountAllHiddenByCRFVersionId(ecb.getCRFVersionId());
		int allHiddenButShown = itemFormMetadataDao.findCountAllHiddenButShownByEventCRFId(ecb.getId());
		// add all hidden items minus all hidden but now shown items to the allRequiredFilledOut variable

		if (allRequiredNum > allRequiredFilledOut + allRequiredButHidden - allHiddenButShown) {
			logger.debug("using crf version number: " + ecb.getCRFVersionId());
			logger.debug("allRequiredNum > allRequiredFilledOut:" + allRequiredNum + " " + allRequiredFilledOut
					+ " plus " + allRequiredButHidden + " minus " + allHiddenButShown);
			return false;
		}
		// had to change the query below to allow for hidden items here
		ArrayList allFilled = iddao.findAllBlankRequiredByEventCRFId(ecb.getId(), ecb.getCRFVersionId());
		int numNotes = 0;
		if (!allFilled.isEmpty()) {
			logger.trace("allFilled is not empty");
			FormDiscrepancyNotes fdn = (FormDiscrepancyNotes) session
					.getAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
			HashMap idNotes = fdn.getIdNotes();
			for (int i = 0; i < allFilled.size(); i++) {
				ItemDataBean idb = (ItemDataBean) allFilled.get(i);
				int exsitingNotes = dndao.findNumExistingNotesForItem(idb.getId());
				if (exsitingNotes > 0) {
					logger.trace("has existing note");
					numNotes++;
				} else if (idNotes.containsKey(idb.getId())) {
					logger.trace("has note in session");
					numNotes++;
				}
			}
			logger.trace("numNotes allFilled.size:" + numNotes + " " + allFilled.size());
			if (numNotes >= allFilled.size()) {
				logger.trace("all required are filled out");
				return true;
			} else {
				logger.debug("numNotes < allFilled.size() " + numNotes + ": " + allFilled.size());
				return false;
			}
		}
		return true;
	}

	/**
	 * 06/13/2007- jxu Since we don't require users to review each section before mark a CRF as complete, we need to
	 * create item data in the database because items will not be created unless the section which contains the items is
	 * reviewed by users.
	 *
	 * @param completeStatus Status
	 * @param request        HttpServletRequest
	 * @return boolean
	 */
	private boolean saveItemsToMarkComplete(Status completeStatus, HttpServletRequest request) throws Exception {
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);
		SectionDAO sdao = new SectionDAO(getDataSource());
		ArrayList sections = sdao.findAllByCRFVersionId(ecb.getCRFVersionId());
		UserAccountBean ub = (UserAccountBean) request.getSession().getAttribute(USER_BEAN_NAME);
		for (int i = 0; i < sections.size(); i++) {
			SectionBean sb = (SectionBean) sections.get(i);
			if (!isCreateItemReqd(sb, request)) {
				getDataEntryService(getServletContext()).saveItemsWithoutItemData(sb.getId(), completeStatus, ub, ecb);
			}
		}
		return true;
	}

	protected boolean isCreateItemReqd(SectionBean sb, HttpServletRequest request) {
		SectionDAO sdao = new SectionDAO(getDataSource());
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);
		EventDefinitionCRFBean edcb = (EventDefinitionCRFBean) request.getAttribute(EVENT_DEF_CRF_BEAN);
		DataEntryStage stage = ecb.getStage();

		HashMap numItemsHM = sdao.getNumItemsBySectionId();
		HashMap numItemsPendingHM = sdao.getNumItemsPendingBySectionId(ecb);
		HashMap numItemsCompletedHM = sdao.getNumItemsCompletedBySection(ecb);
		HashMap numItemsBlankHM = sdao.getNumItemsBlankBySectionId(ecb);

		Integer key = new Integer(sb.getId());

		int numItems = getIntById(numItemsHM, key);
		int numItemsPending = getIntById(numItemsPendingHM, key);
		int numItemsCompleted = getIntById(numItemsCompletedHM, key);
		int numItemsBlank = getIntById(numItemsBlankHM, key);
		System.out.println(" for " + key + " num items " + numItems + " num items blank " + numItemsBlank
				+ " num items pending " + numItemsPending + " completed " + numItemsCompleted);

		if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY) && edcb.isDoubleEntry()) {
			if (numItemsPending == 0 && numItems > 0) {
				System.out.println("returns false on ide loop " + key);
				return false;
			}
		} else {
			if (numItemsCompleted < numItems) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if all the sections in an event crf are reviewed once tbh updated to prevent duplicates, 03/2011.
	 *
	 * @param request HttpServletRequest
	 * @return boolean
	 */
	protected boolean isEachSectionReviewedOnce(HttpServletRequest request) {
		SectionDAO sdao = new SectionDAO(getDataSource());
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);
		DataEntryStage stage = ecb.getStage();
		EventDefinitionCRFBean edcb = (EventDefinitionCRFBean) request.getAttribute(EVENT_DEF_CRF_BEAN);

		ArrayList<SectionBean> sections = sdao.findAllByCRFVersionId(ecb.getCRFVersionId());

		HashMap numItemsHM = sdao.getNumItemsBySectionId();
		HashMap numItemsPendingHM = sdao.getNumItemsPendingBySectionId(ecb);
		HashMap numItemsCompletedHM = sdao.getNumItemsCompletedBySectionId(ecb);

		for (int i = 0; i < sections.size(); i++) {
			SectionBean sb = sections.get(i);
			Integer key = new Integer(sb.getId());

			int numItems = getIntById(numItemsHM, key);
			int numItemsPending = getIntById(numItemsPendingHM, key);
			int numItemsCompleted = getIntById(numItemsCompletedHM, key);

			if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY) && edcb.isDoubleEntry()) {
				if (numItemsPending == 0 && numItems > 0) {
					return false;
				}

			} else {
				if (numItemsCompleted == 0 && numItems > 0) {
					return false;
				} else if (numItemsCompleted < numItems) {
					return false;
				}
			}
		}

		return true;
		// if we get this far, all sections are checked and we return a true
		// return true;
	}

	protected void getEventDefinitionCRFBean(HttpServletRequest request) {
		HttpSession session = request.getSession();
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);
		EventDefinitionCRFBean edcb = (EventDefinitionCRFBean) request.getAttribute(EVENT_DEF_CRF_BEAN);
		EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(getDataSource());
		StudyBean study = (StudyBean) session.getAttribute("study");
		edcb = edcdao.findByStudyEventIdAndCRFVersionId(study, ecb.getStudyEventId(), ecb.getCRFVersionId());
	}

	private EventCRFBean updateECB(StudyEventBean sEvent, HttpServletRequest request) {
		StudyBean currentStudy = (StudyBean) request.getSession().getAttribute("study");
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);
		if (!currentStudy.getStudyParameterConfig().getInterviewerNameDefault().equals("blank")
				&& ("".equals(ecb.getInterviewerName()) || ecb.getInterviewerName() == null)) {
			// default will be event's owner name
			ecb.setInterviewerName(sEvent.getOwner().getName());
		}

		if (!currentStudy.getStudyParameterConfig().getInterviewDateDefault().equals("blank")
				&& ("".equals(ecb.getDateInterviewed()) || ecb.getDateInterviewed() == null)) {
			if (sEvent.getDateStarted() != null) {
				ecb.setDateInterviewed(sEvent.getDateStarted());
				// default date
			} else {
				ecb.setDateInterviewed(null);
			}
		}

		return ecb;
	}

	protected HashMap<String, ItemBean> prepareScoreItems(HttpServletRequest request) {
		HashMap<String, ItemBean> scoreItems = new HashMap<String, ItemBean>();
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);
		ItemDAO idao = new ItemDAO(getDataSource());
		ArrayList<ItemBean> ibs = idao.findAllItemsByVersionId(ecb.getCRFVersionId());
		for (ItemBean ib : ibs) {
			scoreItems.put(ib.getName(), ib);
		}

		return scoreItems;
	}

	protected HashMap<String, String> prepareScoreItemdata(HttpServletRequest request) {
		HashMap<String, String> scoreItemdata = new HashMap<String, String>();
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);
		SectionDAO sdao = new SectionDAO(getDataSource());
		ArrayList<SectionBean> sbs = sdao.findAllByCRFVersionId(ecb.getCRFVersionId());
		for (SectionBean section : sbs) {
			HashMap<String, String> data = prepareSectionItemDataBeans(section.getId(), request);
			if (data != null && data.size() > 0) {
				scoreItemdata.putAll(data);
			}
		}
		return scoreItemdata;
	}

	protected HashMap<String, String> prepareSectionItemDataBeans(int sectionId, HttpServletRequest request) {
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);
		HashMap<String, String> scoreItemdata = new HashMap<String, String>();
		ItemDataDAO iddao = new ItemDataDAO(getDataSource());
		ArrayList<ItemDataBean> idbs = iddao.findAllActiveBySectionIdAndEventCRFId(sectionId, ecb.getId());
		for (ItemDataBean idb : idbs) {
			if (idb.getId() > 0) {
				int ordinal = idb.getOrdinal();
				ordinal = ordinal > 0 ? ordinal : 1;
				scoreItemdata.put(idb.getItemId() + "_" + ordinal, idb.getValue().length() > 0 ? idb.getValue() : "");
			}
		}
		return scoreItemdata;
	}

	protected HashMap<Integer, TreeSet<Integer>> prepareItemdataOrdinals(HttpServletRequest request) {
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);
		HashMap<Integer, TreeSet<Integer>> ordinals = new HashMap<Integer, TreeSet<Integer>>();
		SectionDAO sdao = new SectionDAO(getDataSource());
		ArrayList<SectionBean> sbs = sdao.findAllByCRFVersionId(ecb.getCRFVersionId());
		ItemDataDAO iddao = new ItemDataDAO(getDataSource());
		for (SectionBean section : sbs) {
			ArrayList<ItemDataBean> idbs = iddao.findAllActiveBySectionIdAndEventCRFId(section.getId(), ecb.getId());
			if (idbs != null && idbs.size() > 0) {
				for (ItemDataBean idb : idbs) {
					int itemId = idb.getItemId();
					TreeSet<Integer> os = new TreeSet<Integer>();
					if (ordinals == null) {
						os.add(idb.getOrdinal());
						ordinals.put(itemId, os);
					} else if (ordinals.containsKey(itemId)) {
						os = ordinals.get(itemId);
						os.add(idb.getOrdinal());
						ordinals.put(itemId, os);
					} else {
						os.add(idb.getOrdinal());
						ordinals.put(itemId, os);
					}
				}
			}
		}
		return ordinals;
	}

	protected HashMap<Integer, String> prepareSectionItemdata(int sectionId, HttpServletRequest request) {
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);
		ItemDataDAO iddao = new ItemDataDAO(getDataSource());
		HashMap<Integer, String> itemdata = new HashMap<Integer, String>();
		ArrayList<ItemDataBean> idbs = iddao.findAllActiveBySectionIdAndEventCRFId(sectionId, ecb.getId());
		for (ItemDataBean idb : idbs) {
			itemdata.put(idb.getId(), idb.getValue());
		}
		return itemdata;
	}

	private boolean isChanged(DisplayItemBean dib, HashMap<Integer, String> oldItemdata, String attachedFilePath) {
		ItemDataBean idb = dib.getData();
		String value = idb.getValue();
		if (!dib.getItem().getDataType().equals(ItemDataType.CODE)) {
			if (!oldItemdata.containsKey(idb.getId())) {
				if (isAdministrativeEditing() && "".equals(value)) {
					return false;
				}
				return true;
			} else {
				String oldValue = oldItemdata.get(idb.getId());
				if (oldValue != null) {
					if (value == null) {
						return true;
					} else if (dib.getItem().getDataType().getId() == ItemDataType.FILE.getId()) {
						String theOldValue = oldValue.split("(/|\\\\)")[oldValue.split("(/|\\\\)").length - 1].trim();
						return !value.equals(theOldValue);
					} else if (!oldValue.equals(value)) {
						return true;
					}
				} else if (value != null) {
					return true;
				}
			}
		} else {
			ItemDataDAO itdao = new ItemDataDAO(getDataSource());
			ItemDataBean itemDataBean = (ItemDataBean) itdao.findByPK(dib.getData().getId());
			if (itemDataBean != null) {
				dib.getData().setValue(itemDataBean.getValue());
			}
		}
		return false;
	}

	private boolean isChanged(ItemDataBean idb, HashMap<Integer, String> oldItemdata, DisplayItemBean dib,
							  String attachedFilePath) {
		return isChanged(dib, oldItemdata, attachedFilePath);
	}

	protected void updateDataOrdinals(List<DisplayItemWithGroupBean> displayItemWithGroups) {
		for (int i = 0; i < displayItemWithGroups.size(); ++i) {
			DisplayItemWithGroupBean diwb = displayItemWithGroups.get(i);
			HashMap<Integer, String> editFlags = new HashMap<Integer, String>();
			HashMap<Integer, Integer> nextOrdinals = new HashMap<Integer, Integer>();
			if (diwb.isInGroup()) {
				List<DisplayItemGroupBean> dbGroups = diwb.getDbItemGroups();
				for (int j = 0; j < dbGroups.size(); j++) {
					DisplayItemGroupBean displayGroup = dbGroups.get(j);
					List<DisplayItemBean> items = displayGroup.getItems();
					for (DisplayItemBean displayItem : items) {
						int itemId = displayItem.getItem().getId();
						int ordinal = displayItem.getData().getOrdinal();
						if ("initial".equalsIgnoreCase(displayGroup.getEditFlag())) {
							nextOrdinals.put(itemId, 1);
						} else {
							if (nextOrdinals.containsKey(itemId)) {
								int max = nextOrdinals.get(itemId);
								nextOrdinals.put(itemId, ordinal > max ? ordinal + 1 : max);
							} else {
								nextOrdinals.put(itemId, ordinal + 1);
							}
						}
						editFlags.put(displayItem.getData().getId(), displayGroup.getEditFlag());
					}
				}

				List<DisplayItemGroupBean> dgbs = diwb.getItemGroups();
				int nextOrdinal = 0;
				for (int j = 0; j < dgbs.size(); j++) {
					DisplayItemGroupBean displayGroup = dgbs.get(j);
					List<DisplayItemBean> oItems = displayGroup.getItems();
					String editFlag = displayGroup.getEditFlag();
					for (DisplayItemBean displayItem : oItems) {
						int itemId = displayItem.getItem().getId();
						if (nextOrdinals.get(itemId) != null) {
							nextOrdinal = nextOrdinals.get(itemId);
							int ordinal = 0;
							String editflag = "add".equalsIgnoreCase(editFlag) ? editFlag : editFlags.get(displayItem
									.getData().getId());
							if (editflag.length() > 0) {
								if ("add".equalsIgnoreCase(editflag)) {
									ordinal = nextOrdinals.get(itemId);
									displayItem.getData().setOrdinal(ordinal);
									nextOrdinals.put(itemId, nextOrdinal + 1);
								} else if ("edit".equalsIgnoreCase(editflag)) {
									displayItem.getData().setOrdinal(displayItem.getDbData().getOrdinal());
								}
							}
						}
					}
				}
			}
		}
	}

	private String ifValueIsDate(ItemBean itemBean, String value, boolean dryRun) {

		String dateFormat = ResourceBundleProvider.getFormatBundle().getString("date_format_string");
		String dateRegexp = ResourceBundleProvider.getFormatBundle().getString("date_regexp");

		String theFinalValue = value;
		if (value != null && itemBean.getDataType() == ItemDataType.DATE && dryRun) {
			theFinalValue = ExpressionTreeHelper.ifValidDateFormatAsyyyyMMdd(value, dateFormat, dateRegexp);
		} else {
			theFinalValue = ExpressionTreeHelper.isValidDateMMddyyyy(value);
		}
		return theFinalValue;
	}

	/**
	 * This method will populate grouped and variableAndValue HashMaps grouped : Used to correctly populate group
	 * ordinals variableAndValue : Holds itemOID , value (in Form ) pairs passed in to rule processor.
	 *
	 * @param allItems
	 */
	private Container populateRuleSpecificHashMaps(List<DisplayItemWithGroupBean> allItems, Container c, Boolean dryRun) {

		for (DisplayItemWithGroupBean displayItemWithGroupBean : allItems) {
			if (displayItemWithGroupBean.getSingleItem() != null) {
				if (displayItemWithGroupBean.getSingleItem().getItem().getOid() != null) {
					c.grouped.put(displayItemWithGroupBean.getSingleItem().getItem().getOid(), 1);
					c.variableAndValue.put(
							displayItemWithGroupBean.getSingleItem().getItem().getOid(),
							ifValueIsDate(displayItemWithGroupBean.getSingleItem().getItem(), displayItemWithGroupBean
									.getSingleItem().getData().getValue(), dryRun));
					logger.debug("Type : " + displayItemWithGroupBean.getSingleItem().getItem().getItemDataTypeId());
					for (Object displayItemBean : displayItemWithGroupBean.getSingleItem().getChildren()) {
						String oid = ((DisplayItemBean) displayItemBean).getItem().getOid();
						String value = ifValueIsDate(((DisplayItemBean) displayItemBean).getItem(),
								((DisplayItemBean) displayItemBean).getData().getValue(), dryRun);
						logger.debug("Type : " + ((DisplayItemBean) displayItemBean).getItem().getItemDataTypeId());
						c.grouped.put(oid, 1);
						c.variableAndValue.put(oid, value);

					}
				}
			}
			// Items in repeating groups
			for (DisplayItemGroupBean itemGroupBean : displayItemWithGroupBean.getItemGroups()) {
				for (DisplayItemBean displayItemBean : itemGroupBean.getItems()) {
					String key1 = itemGroupBean.getItemGroupBean().getOid() + "[" + (itemGroupBean.getIndex() + 1)
							+ "]." + displayItemBean.getItem().getOid();
					String key = itemGroupBean.getItemGroupBean().getOid() + "." + displayItemBean.getItem().getOid();
					c.variableAndValue.put(key1,
							ifValueIsDate(displayItemBean.getItem(), displayItemBean.getData().getValue(), dryRun));
					if (c.grouped.containsKey(key)) {
						c.grouped.put(key, c.grouped.get(key) + 1);
					} else {
						c.grouped.put(key, 1);
					}
				}
			}
		}
		return c;
	}

	private List<RuleSetBean> createAndInitializeRuleSet(StudyBean currentStudy,
														 StudyEventDefinitionBean studyEventDefinition, CRFVersionBean crfVersionBean,
														 StudyEventBean studyEventBean, EventCRFBean eventCrfBean, Boolean shouldRunRules,
														 HttpServletRequest request, HttpServletResponse response, List<ItemBean> itemBeansWithSCDShown) {
		RuleSetService ruleSetService = getRuleSetService();
		if (shouldRunRules) {
			logMe("Current Thread:::" + Thread.currentThread());
			List<RuleSetBean> ruleSets = ruleSetService.getRuleSetsByCrfStudyAndStudyEventDefinition(currentStudy,
					studyEventDefinition, crfVersionBean);
			logMe("Current Thread:::" + Thread.currentThread() + "RuleSet Now?" + ruleSets);
			if (ruleSets != null && ruleSets.size() > 0) {

				ruleSets = ruleSetService.filterByStatusEqualsAvailable(ruleSets);
				ruleSets = ruleSetService.filterRuleSetsByStudyEventOrdinal(ruleSets, studyEventBean, crfVersionBean,
						studyEventDefinition);
				ruleSets = ruleSetService.filterRuleSetsByHiddenItems(ruleSets, eventCrfBean, crfVersionBean,
						itemBeansWithSCDShown);
			}
			return ruleSets != null && ruleSets.size() > 0 ? ruleSets : new ArrayList<RuleSetBean>();
		} else {
			return new ArrayList<RuleSetBean>();
		}
	}

	private HashMap<String, ArrayList<String>> runRules(List<DisplayItemWithGroupBean> allItems,
														List<RuleSetBean> ruleSets, Boolean dryRun, Boolean shouldRunRules, MessageType mt, Phase phase,
														EventCRFBean ecb, HttpServletRequest request) {
		HashMap<String, ArrayList<String>> result = new HashMap<String, ArrayList<String>>();
		UserAccountBean ub = (UserAccountBean) request.getSession().getAttribute(USER_BEAN_NAME);
		if (shouldRunRules) {
			Container c = new Container();
			RuleSetService ruleSetService = getRuleSetService();
			ruleSetService.getDynamicsMetadataService().getExpressionService().clearItemDataCache();
			try {
				c = populateRuleSpecificHashMaps(allItems, c, dryRun);
				ruleSets = ruleSetService.filterRuleSetsBySectionAndGroupOrdinal(ruleSets, c.grouped);
				ruleSets = ruleSetService.solidifyGroupOrdinalsUsingFormProperties(ruleSets, c.grouped);
				// next line here ?
			} catch (NullPointerException npe) {
				logger.debug("found NPE " + npe.getMessage());
				npe.printStackTrace();
			}
			logger.debug("running rules ... rule sets size is " + ruleSets.size());
			result = ruleSetService.runRulesInDataEntry(ruleSets, dryRun, ub, c.variableAndValue, phase, ecb, request)
					.getByMessageType(mt);
		}
		return result;
	}

	protected abstract boolean shouldRunRules();

	protected abstract boolean isAdministrativeEditing();

	protected abstract boolean isAdminForcedReasonForChange(HttpServletRequest request);

	protected boolean unloadFiles(HashMap<String, String> newUploadedFiles) {
		boolean success = true;
		Iterator iter = newUploadedFiles.keySet().iterator();
		while (iter.hasNext()) {
			String itemId = (String) iter.next();
			String filename = newUploadedFiles.get(itemId);
			File f = new File(filename);
			if (f.exists()) {
				if (f.delete()) {
					newUploadedFiles.remove("filename");
				} else {
					success = false;
				}
			} else {
				newUploadedFiles.remove("filename");
			}
		}
		return success;
	}

	private class Container {
		HashMap<String, Integer> grouped;
		HashMap<String, String> variableAndValue;

		public Container() {
			super();
			this.grouped = new HashMap<String, Integer>();
			this.variableAndValue = new HashMap<String, String>();
		}
	}

	private HashMap reshuffleErrorGroupNamesKK(HashMap errors, List<DisplayItemWithGroupBean> allItems,
											   HttpServletRequest request) {
		int manualRows = 0;
		if (errors != null && errors.size() > 0) {
			for (int i = 0; i < allItems.size(); i++) {
				DisplayItemWithGroupBean diwb = allItems.get(i);

				if (diwb.isInGroup()) {
					List<DisplayItemGroupBean> dgbs = diwb.getItemGroups();
					for (int j = 0; j < dgbs.size(); j++) {

						DisplayItemGroupBean digb = dgbs.get(j);
						List<DisplayItemBean> dibs = digb.getItems();

						if (j == 0) {
							for (DisplayItemBean dib : dibs) {
								String intendedKey = digb.getInputId() + DataEntryUtil.getInputName(dib);
								String replacementKey = digb.getItemGroupBean().getOid() + "_" + j + DataEntryUtil.getInputName(dib);
								if (!replacementKey.equals(intendedKey) && errors.containsKey(intendedKey)) {
									errors.put(replacementKey, errors.get(intendedKey));
									errors.remove(intendedKey);
								}
							}
						} else {
							manualRows++;
							for (DisplayItemBean dib : dibs) {
								String intendedKey = digb.getInputId() + DataEntryUtil.getInputName(dib);
								String replacementKey = digb.getItemGroupBean().getOid() + "_manual"
										+ digb.getOrdinal() + DataEntryUtil.getInputName(dib);
								if (!replacementKey.equals(intendedKey) && errors.containsKey(intendedKey)) {
									errors.put(replacementKey, errors.get(intendedKey));
									errors.remove(intendedKey);
								}
							}
						}
					}
				}
			}
		}
		request.setAttribute("manualRows", new Integer(manualRows));
		return errors;
	}

	/**
	 * Method checks access.
	 *
	 * @param request HttpServletRequest
	 * @throws InsufficientPermissionException the InsufficientPermissionException
	 */
	public void mayAccess(HttpServletRequest request) throws InsufficientPermissionException {
		FormProcessor fp = new FormProcessor(request);
		EventCRFDAO edao = new EventCRFDAO(getDataSource());
		UserAccountBean ub = (UserAccountBean) request.getSession().getAttribute(USER_BEAN_NAME);
		int eventCRFId = fp.getInt("ecId", true);
		if (eventCRFId == 0) {
			eventCRFId = fp.getInt("eventCRFId", true);
		}

		if (eventCRFId > 0) {
			if (!entityIncluded(eventCRFId, ub.getName(), edao)) {
				addPageMessage(respage.getString("required_event_CRF_belong"), request);
				throw new InsufficientPermissionException(Page.MENU_SERVLET,
						resexception.getString("entity_not_belong_studies"), "1");
			}
		}

	}

	protected void populateInstantOnChange(HttpSession session, EventCRFBean ecb, DisplaySectionBean section) {
		int cvId = ecb.getCRFVersionId();
		int sectionId = section.getSection().getId();
		InstantOnChangeService ins = (InstantOnChangeService) SpringServletAccess.getApplicationContext(
				getServletContext()).getBean("instantOnChangeService");
		InstantOnChangeFrontStrParcel strsInSec = new InstantOnChangeFrontStrParcel();
		HashMap<Integer, InstantOnChangeFrontStrGroup> nonRepOri = null;
		HashMap<String, Map<Integer, InstantOnChangeFrontStrGroup>> grpOri = null;
		HashMap<Integer, InstantOnChangeFrontStrParcel> instantOnChangeFrontStrParcels = (HashMap<Integer, InstantOnChangeFrontStrParcel>) session
				.getAttribute(CV_INSTANT_META + cvId);
		if (instantOnChangeFrontStrParcels != null && instantOnChangeFrontStrParcels.containsKey(sectionId)) {
			strsInSec = instantOnChangeFrontStrParcels.get(sectionId);
			nonRepOri = (HashMap<Integer, InstantOnChangeFrontStrGroup>) strsInSec.getNonRepOrigins();
			grpOri = (HashMap<String, Map<Integer, InstantOnChangeFrontStrGroup>>) strsInSec.getRepOrigins();
		} else if (instantOnChangeFrontStrParcels == null || instantOnChangeFrontStrParcels.size() == 0) {
			instantOnChangeFrontStrParcels = (HashMap<Integer, InstantOnChangeFrontStrParcel>) ins
					.instantOnChangeFrontStrParcelInCrfVersion(cvId);
			if (instantOnChangeFrontStrParcels.size() > 0) {
				session.setAttribute(CV_INSTANT_META + cvId, instantOnChangeFrontStrParcels);
				if (instantOnChangeFrontStrParcels.containsKey(sectionId)) {
					strsInSec = instantOnChangeFrontStrParcels.get(sectionId);
					if (strsInSec != null) {
						grpOri = (HashMap<String, Map<Integer, InstantOnChangeFrontStrGroup>>) strsInSec
								.getRepOrigins();
						nonRepOri = (HashMap<Integer, InstantOnChangeFrontStrGroup>) strsInSec.getNonRepOrigins();
					}
				}
			}
		}
		if (grpOri != null && grpOri.size() > 0) {
			ins.itemGroupsInstantUpdate(section.getDisplayItemGroups(), grpOri);
		}
		if (nonRepOri != null && nonRepOri.size() > 0) {
			ins.itemsInstantUpdate(section.getDisplayItemGroups(), nonRepOri);
		}
	}

	private StdScheduler getScheduler(HttpServletRequest request) {
		StdScheduler scheduler = (StdScheduler) SpringServletAccess.getApplicationContext(
				request.getSession().getServletContext()).getBean("schedulerFactoryBean");
		return scheduler;
	}

	private static boolean checkDobleDataEntryErrors(HashMap errors) {
		Iterator iter = errors.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry pairs = (Map.Entry) iter.next();
			if (pairs.getValue().toString().contains(respage.getString("value_you_specified"))) {
				return true;
			}
		}
		return false;
	}

	private Map<String, HashMap<String, String>> createDNParametersMap(HttpServletRequest request,
																	   DisplaySectionBean section) {
		// we create map with parameters for creating DNs for each field in CRF
		Map<String, HashMap<String, String>> dnCreatingParameters = new HashMap<String, HashMap<String, String>>();
		for (int i = 0; i < section.getDisplayItemGroups().size(); i++) {
			DisplayItemWithGroupBean diwgb = (DisplayItemWithGroupBean) section.getDisplayItemGroups().get(i);
			String inputName;
			if (diwgb.isInGroup()) {
				List<DisplayItemGroupBean> dbGroups = diwgb.getItemGroups();
				for (int j = 0; j < dbGroups.size(); j++) {
					DisplayItemGroupBean displayGroup = dbGroups.get(j);
					for (DisplayItemBean displayItem : displayGroup.getItems()) {
						// DisplayItemBean from repeating Group
						boolean manual = j != 0;
						inputName = DataEntryUtil.getGroupItemInputName(displayGroup, j, displayItem, manual);
						dnCreatingParameters.put(inputName,
								calculateDNParametersForOneItem(displayItem, inputName, request));
					}
				}
			} else {
				DisplayItemBean displayItem = diwgb.getSingleItem();
				inputName = DataEntryUtil.getInputName(displayItem);
				dnCreatingParameters.put(inputName, calculateDNParametersForOneItem(displayItem, inputName, request));
			}

		}
		return dnCreatingParameters;
	}

	private HashMap<String, String> calculateDNParametersForOneItem(DisplayItemBean dib, String field,
																	HttpServletRequest request) {
		HashMap<String, String> result = new HashMap<String, String>();
		// calculate parameters block
		// started values should be initialed by "", not "0"
		String isInError = "";
		HashMap formMessages = (HashMap) request.getAttribute("formMessages");
		if (formMessages != null) {
			if (formMessages.keySet().contains(field)) {
				isInError = "1";
			}
		}

		String isInRFCError = "";
		Set<String> setOfItemNamesWithRFCErrors = (Set<String>) request.getAttribute("setOfItemNamesWithRFCErrors");
		if (setOfItemNamesWithRFCErrors != null) {
			if (setOfItemNamesWithRFCErrors.contains(field)) {
				isInRFCError = "1";
			}
		}

		String isInFVCError = "";
		if ("1".equals(isInError) && ("0".equals(isInRFCError) || "".equals(isInRFCError))) {
			isInFVCError = "1";
		}

		result.put("isInError", isInError);
		result.put("isInRFCError", isInRFCError);
		result.put("isInFVCError", isInFVCError);
		result.put("isDataChanged", "");
		result.put("field", field);

		return result;
	}

	protected DataEntryService getDataEntryService(ServletContext context) {
		DataEntryService dataEntryService = (DataEntryService) SpringServletAccess.getApplicationContext(context)
				.getBean("dataEntryService");
		return dataEntryService;
	}

	protected DisplayItemService getDisplayItemService(ServletContext context) {
		return (DisplayItemService) SpringServletAccess.getApplicationContext(context).getBean("displayItemService");
	}

	/**
	 * TODO: Move from here
	 *
	 * @param request
	 */
	private void provideRandomizationStatisticsForSite(HttpServletRequest request) {
		StudyBean currentStudy = getCurrentStudy(request);

		if (currentStudy.isSite()) {
			int subjectsNumberAssignedToDynamicGroup;
			Map<String, Integer> subjectsNumberAssignedToEachDynamicGroupMap = new HashMap<String, Integer>();
			List<StudyGroupClassBean> activeDynamicGroupClasses = getStudyGroupClassDAO()
					.findAllActiveDynamicGroupClassesByStudyId(currentStudy.getParentStudyId());

			for (StudyGroupClassBean dynamicGroupClass : activeDynamicGroupClasses) {
				subjectsNumberAssignedToDynamicGroup = getStudySubjectDAO()
						.getCountOfStudySubjectsByStudyIdAndDynamicGroupClassId(currentStudy.getId(),
								dynamicGroupClass.getId());
				subjectsNumberAssignedToEachDynamicGroupMap.put(dynamicGroupClass.getName(),
						subjectsNumberAssignedToDynamicGroup);
			}
			request.setAttribute("subjectsNumberAssignedToEachDynamicGroupMap",
					subjectsNumberAssignedToEachDynamicGroupMap);
		}
	}

	protected abstract CurrentDataEntryStage getCurrentDataEntryStage();

	protected DisplayItemBeanValidator getDisplayItemBeanValidator() {
		if (displayItemBeanValidator == null) {
			DisplayItemBeanValidator displayValidator = (DisplayItemBeanValidator) SpringServletAccess
					.getApplicationContext(getServletContext()).getBean("displayItemValidator");
			displayItemBeanValidator = displayValidator;
		}
		return displayItemBeanValidator;
	}

	private List<DiscrepancyNoteBean> getTransformedDNsList(HttpServletRequest request) {
		List<DiscrepancyNoteBean> transformedDNs = (List<DiscrepancyNoteBean>) request.getSession().getAttribute(
				CreateDiscrepancyNoteServlet.TRANSFORMED_SUBMITTED_DNS);
		return transformedDNs == null ? new ArrayList<DiscrepancyNoteBean>() : transformedDNs;
	}

	private Set<Integer> getTransformedSavedDNIds(List<DiscrepancyNoteBean> transformedDNs) {
		Set<Integer> transformedSavedDNIds = new HashSet<Integer>();
		for (DiscrepancyNoteBean dn : transformedDNs) {
			if (dn.getId() > 0) {
				transformedSavedDNIds.add(dn.getId());
			}
		}
		return transformedSavedDNIds;
	}

	private Set<String> getTransformedUnSavedDNIds(List<DiscrepancyNoteBean> transformedDNs) {
		Set<String> transformedUnSavedDNFieldNames = new HashSet<String>();
		for (DiscrepancyNoteBean dn : transformedDNs) {
			if (dn.getId() <= 0) {
				transformedUnSavedDNFieldNames.add(dn.getField());
			}
		}
		return transformedUnSavedDNFieldNames;
	}

	private int getFirstSectionId(ArrayList<SectionBean> sections) {
		int sectionId = 0;
		for (int i = 0; i < sections.size(); i++) {
			SectionBean sb = (SectionBean) sections.get(i);
			sectionId = sb.getId();
			break;
		}
		return sectionId;
	}

	private DisplaySectionBean getDisplaySectionBeanDependingOnStage(DisplaySectionBean sectionBean) {
		if (getCurrentDataEntryStage() == CurrentDataEntryStage.VIEW_DATA_ENTRY) {
			return sectionBean;
		} else {
			return DataEntryRenderUtil.convertSingleItemsToDisplayItemRowsDependingOnSource(sectionBean);
		}
	}
}