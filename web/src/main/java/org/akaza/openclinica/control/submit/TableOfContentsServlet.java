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

import com.clinovo.util.ValidatorHelper;

import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.DisplayEventCRFBean;
import org.akaza.openclinica.bean.submit.DisplayTableOfContentsBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.DiscrepancyValidator;
import org.akaza.openclinica.control.form.FormDiscrepancyNotes;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.service.managestudy.DiscrepancyNoteService;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InconsistentStateException;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ssachs
 */

// TODO: make it possible to input an event crf bean to this servlet rather than
// an int
@SuppressWarnings({"rawtypes", "unchecked",  "serial"})
@Component
public class TableOfContentsServlet extends Controller {
	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
	public static final String BEAN_DISPLAY = "toc";

	// these inputs are used when you get here from a jsp page
	// e.g. TableOfContents?action=ide_c&id=123
	public static final String INPUT_ACTION = "action";

	public static final String INPUT_ID = "ecid";

	// these inputs are used when another servlet sends you here
	// such as mark crf complete, initial data entry, etc
	public static final String INPUT_EVENT_CRF_BEAN = "eventCRF";

	// these are only for use with ACTION_START_INITIAL_DATA_ENTRY
	public static final String INPUT_EVENT_DEFINITION_CRF_ID = "eventDefinitionCRFId";

	public static final String INPUT_CRF_VERSION_ID = "crfVersionId";

	public static final String INPUT_STUDY_EVENT_ID = "studyEventId";

	public static final String INPUT_SUBJECT_ID = "subjectId";

	public static final String INPUT_EVENT_CRF_ID = "eventCRFId";

	// these inputs are displayed on the table of contents and
	// are used to edit Event CRF properties
	public static final String INPUT_INTERVIEWER = "interviewer";

	public static final String INPUT_INTERVIEW_DATE = "interviewDate";

	public static final String[] ACTIONS = { ACTION_START_INITIAL_DATA_ENTRY, ACTION_CONTINUE_INITIAL_DATA_ENTRY,
			ACTION_START_DOUBLE_DATA_ENTRY, ACTION_CONTINUE_DOUBLE_DATA_ENTRY, ACTION_ADMINISTRATIVE_EDITING };

    private class ObjectPairs {
        private String action;
        private EventCRFBean ecb;
    }

	private ObjectPairs getEventCRFAndAction(HttpServletRequest request) {
        ObjectPairs objectPairs = new ObjectPairs();
        EventCRFDAO ecdao = getEventCRFDAO();
        StudyBean currentStudy = getCurrentStudy(request);
        EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF_BEAN);

        FormProcessor fp = new FormProcessor(request);
		if (ecb == null) {
			int ecid = fp.getInt(INPUT_ID, true);
			AuditableEntityBean aeb = ecdao.findByPKAndStudy(ecid, currentStudy);

			if (!aeb.isActive()) {
                objectPairs.ecb = new EventCRFBean();
			} else {
                objectPairs.ecb = (EventCRFBean) aeb;
			}

            objectPairs.action = fp.getString(INPUT_ACTION, true);
		} else {
            objectPairs.action = getActionForStage(ecb.getStage());
		}
        return objectPairs;
	}

	/**
	 * Determines if the action requested is a valid action.
	 * 
	 * @param action
	 *            The action requested.
	 * @return <code>true</code> if the action is valid, <code>false</code> otherwise.
	 */
	private boolean invalidAction(String action) {
		ArrayList validActions = new ArrayList(Arrays.asList(ACTIONS));
		return !validActions.contains(action);
	}

	/**
	 * Determines if the action requested is consistent with the specified Event CRF's data entry stage.
	 * 
	 * @param action
	 *            The action requested.
	 * @param ecb
	 *            The Event CRF whose data entry stage is being checked for consistency with the action.
	 * @return <code>true</code> if the action is consistent with the Event CRF's stage, <code>false</code> otherwise.
	 */
	private boolean isConsistentAction(String action, EventCRFBean ecb) {
		DataEntryStage stage = ecb.getStage();

		boolean isConsistent = true;
		if (action.equals(ACTION_START_INITIAL_DATA_ENTRY) && !stage.equals(DataEntryStage.UNCOMPLETED)) {
			isConsistent = false;
		} else if (action.equals(ACTION_CONTINUE_INITIAL_DATA_ENTRY)
				&& !stage.equals(DataEntryStage.INITIAL_DATA_ENTRY)) {
			isConsistent = false;
		} else if (action.equals(ACTION_START_DOUBLE_DATA_ENTRY)
				&& !stage.equals(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE)) {
			isConsistent = false;
		} else if (action.equals(ACTION_CONTINUE_DOUBLE_DATA_ENTRY) && !stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY)) {
			isConsistent = false;
		} else if (action.equals(ACTION_ADMINISTRATIVE_EDITING)
				&& !stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE)) {
			isConsistent = false;
		}

		return isConsistent;
	}

	/**
	 * Creates a new Event CRF or update the exsiting one, that is, an event CRF can be created but not item data yet,
	 * in this case, still consider it is not started(called uncompleted before)
	 * 
	 * @return EventCRFBean
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	private EventCRFBean createEventCRF(HttpServletRequest request) throws Exception {
        UserAccountBean ub = getUserAccountBean(request);
        StudyBean currentStudy = getCurrentStudy(request);

		EventCRFBean ecb;
        EventCRFDAO ecdao = getEventCRFDAO();

        FormProcessor fp= new FormProcessor(request);
		int crfVersionId = fp.getInt(INPUT_CRF_VERSION_ID);
		int studyEventId = fp.getInt(INPUT_STUDY_EVENT_ID);
		int eventDefinitionCRFId = fp.getInt(INPUT_EVENT_DEFINITION_CRF_ID);
		int subjectId = fp.getInt(INPUT_SUBJECT_ID);
		int eventCRFId = fp.getInt(INPUT_EVENT_CRF_ID);

		logger.info("Creating event CRF within Table of Contents.  Study id: " + currentStudy.getId()
				+ "; CRF Version id: " + crfVersionId + "; Study Event id: " + studyEventId
				+ "; Event Definition CRF id: " + eventDefinitionCRFId + "; Subject: " + subjectId);

		StudySubjectDAO ssdao = getStudySubjectDAO();
		StudySubjectBean ssb = ssdao.findBySubjectIdAndStudy(subjectId, currentStudy);

		if (!ssb.isActive()) {
			throw new InconsistentStateException(Page.LIST_STUDY_SUBJECTS_SERVLET,
					resexception.getString("trying_to_begin_DE1"));
		}

		StudyEventDefinitionDAO seddao = getStudyEventDefinitionDAO();
		StudyEventDefinitionBean sedb = seddao.findByEventDefinitionCRFId(eventDefinitionCRFId);

		if (!ssb.isActive() || !sedb.isActive()) {
			throw new InconsistentStateException(Page.LIST_STUDY_SUBJECTS_SERVLET,
					resexception.getString("trying_to_begin_DE2"));
		}

		CRFVersionDAO cvdao = getCRFVersionDAO();
		EntityBean eb = cvdao.findByPK(crfVersionId);

		if (!eb.isActive()) {
			throw new InconsistentStateException(Page.LIST_STUDY_SUBJECTS_SERVLET,
					resexception.getString("trying_to_begin_DE3"));
		}

		StudyEventDAO sedao = getStudyEventDAO();
		StudyEventBean sEvent = (StudyEventBean) sedao.findByPK(studyEventId);

		StudyBean studyWithSED = currentStudy;
		if (currentStudy.getParentStudyId() > 0) {
			studyWithSED = new StudyBean();
			studyWithSED.setId(currentStudy.getParentStudyId());
		}

		AuditableEntityBean aeb = sedao.findByPKAndStudy(studyEventId, studyWithSED);

		if (!aeb.isActive()) {
			throw new InconsistentStateException(Page.LIST_STUDY_SUBJECTS_SERVLET,
					resexception.getString("trying_to_begin_DE4"));
		}

		ecb = new EventCRFBean();
		if (eventCRFId == 0) {// no event CRF created yet
			ecb.setAnnotations("");
			ecb.setCreatedDate(new Date());
			ecb.setCRFVersionId(crfVersionId);
			ecb.setInterviewerName("");
			if (sEvent.getDateStarted() != null) {
				ecb.setDateInterviewed(sEvent.getDateStarted());// default date
			} else {
				ecb.setDateInterviewed(null);
			}
			ecb.setOwnerId(ub.getId());
			ecb.setStatus(Status.AVAILABLE);
			ecb.setCompletionStatusId(1);
			ecb.setStudySubjectId(ssb.getId());
			ecb.setStudyEventId(studyEventId);
			ecb.setValidateString("");
			ecb.setValidatorAnnotations("");

			ecb = (EventCRFBean) ecdao.create(ecb);
			logger.info("CREATED EVENT CRF");
		} else {
			// there is an event CRF already, only need to update
			ecb = (EventCRFBean) ecdao.findByPK(eventCRFId);
			ecb.setCRFVersionId(crfVersionId);
			ecb.setUpdatedDate(new Date());
			ecb.setUpdater(ub);
			ecb = (EventCRFBean) ecdao.update(ecb);

		}

		if (!ecb.isActive()) {
			throw new InconsistentStateException(Page.LIST_STUDY_SUBJECTS_SERVLET,
					resexception.getString("new_event_CRF_not_created_database_error"));
		} else {
			sEvent.setSubjectEventStatus(SubjectEventStatus.DATA_ENTRY_STARTED);
			sEvent.setUpdater(ub);
			sEvent.setUpdatedDate(new Date());
			sedao.update(sEvent);

		}

		return ecb;
	}

	private void validateEventCRFAndAction(HttpServletRequest request, ObjectPairs objectPairs) throws Exception {
        UserAccountBean ub = getUserAccountBean(request);

		if (invalidAction(objectPairs.action)) {
			throw new InconsistentStateException(Page.LIST_STUDY_SUBJECTS_SERVLET,
					resexception.getString("no_action_specified_or_invalid"));
		}

		if (!isConsistentAction(objectPairs.action, objectPairs.ecb)) {
			HashMap verbs = new HashMap();
			verbs.put(ACTION_START_INITIAL_DATA_ENTRY, resword.getString("start_initial_data_entry"));
			verbs.put(ACTION_CONTINUE_INITIAL_DATA_ENTRY, resword.getString("continue_initial_data_entry"));
			verbs.put(ACTION_START_DOUBLE_DATA_ENTRY, resword.getString("start_double_data_entry"));
			verbs.put(ACTION_CONTINUE_DOUBLE_DATA_ENTRY, resword.getString("continue_double_data_entry"));
			verbs.put(ACTION_ADMINISTRATIVE_EDITING, resword.getString("perform_administrative_editing"));
			String verb = (String) verbs.get(objectPairs.action);

			if (verb == null) {
				verb = "start initial data entry";
			}

			throw new InconsistentStateException(Page.LIST_STUDY_SUBJECTS_SERVLET,
					resexception.getString("you_are_trying_to") + verb + " "
							+ resexception.getString("on_event_CRF_inappropiate_action"));
		}

		if (objectPairs.action.equals(ACTION_START_DOUBLE_DATA_ENTRY)) {
            objectPairs.ecb.setValidatorId(ub.getId());
            objectPairs.ecb.setDateValidate(new Date());

            objectPairs.ecb = (EventCRFBean) getEventCRFDAO().update(objectPairs.ecb);
		}
	}

	private void updatePresetValues(FormProcessor fp,  EventCRFBean ecb) {
		fp.addPresetValue(INPUT_INTERVIEWER, ecb.getInterviewerName());
		if (ecb.getDateInterviewed() != null) {
			String idateFormatted = getLocalDf(fp.getRequest()).format(ecb.getDateInterviewed());
			fp.addPresetValue(INPUT_INTERVIEW_DATE, idateFormatted);
		} else {
			fp.addPresetValue(INPUT_INTERVIEW_DATE, "");
		}
		setPresetValues(fp.getPresetValues(), fp.getRequest());
	}

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserAccountBean ub = getUserAccountBean(request);
        StudyBean currentStudy = getCurrentStudy(request);
        StudyUserRoleBean currentRole = getCurrentRole(request);

        FormDiscrepancyNotes discNotes;
        ObjectPairs objectPairs = getEventCRFAndAction(request);

		if (objectPairs.action.equals(ACTION_START_INITIAL_DATA_ENTRY)) {
            objectPairs.ecb = createEventCRF(request);
		} else {
            validateEventCRFAndAction(request, objectPairs);
		}

        FormProcessor fp = new FormProcessor(request);
        HashMap errors = getErrorsHolder(request);
		updatePresetValues(fp, objectPairs.ecb);

		Boolean b = (Boolean) request.getAttribute(DataEntryServlet.INPUT_IGNORE_PARAMETERS);

		if (fp.isSubmitted() && b == null) {
			discNotes = (FormDiscrepancyNotes) request.getSession().getAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
			if (discNotes == null) {
				discNotes = new FormDiscrepancyNotes();
                request.getSession().setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, discNotes);

			}
			DiscrepancyValidator v = new DiscrepancyValidator(new ValidatorHelper(request, getConfigurationDao()),
					discNotes);

			v.addValidation(INPUT_INTERVIEWER, Validator.NO_BLANKS);
			v.addValidation(INPUT_INTERVIEW_DATE, Validator.IS_A_DATE);
			v.alwaysExecuteLastValidation(INPUT_INTERVIEW_DATE);

			errors = v.validate();
            EventCRFDAO ecdao = getEventCRFDAO();

			if (errors.isEmpty()) {

                objectPairs.ecb.setInterviewerName(fp.getString(INPUT_INTERVIEWER));
                objectPairs.ecb.setDateInterviewed(fp.getDate(INPUT_INTERVIEW_DATE));

				if (ecdao == null) {
					ecdao = getEventCRFDAO();
				}

                objectPairs.ecb = (EventCRFBean) ecdao.update(objectPairs.ecb);

				// save discrepancy notes into DB
				DiscrepancyNoteService dnService = new DiscrepancyNoteService(getDataSource());
				FormDiscrepancyNotes fdn = (FormDiscrepancyNotes) request.getSession()
						.getAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);

				dnService.saveFieldNotes(INPUT_INTERVIEWER, fdn, objectPairs.ecb.getId(), "EventCRF", currentStudy);
				dnService.saveFieldNotes(INPUT_INTERVIEW_DATE, fdn, objectPairs.ecb.getId(), "EventCRF", currentStudy);

				if (ecdao.isQuerySuccessful()) {
					updatePresetValues(fp, objectPairs.ecb);
					if (!fp.getBoolean("editInterview", true)) {
						// editing completed
						addPageMessage(respage.getString("interviewer_name_date_updated"), request);
					}
				} else {
					addPageMessage(respage.getString("database_error_interviewer_name_date_not_updated"), request);
				}

			} else {
				String[] textFields = { INPUT_INTERVIEWER, INPUT_INTERVIEW_DATE };
				fp.setCurrentStringValuesAsPreset(textFields);

				setInputMessages(errors, request);
				setPresetValues(fp.getPresetValues(), request);
			}
		} else {
			discNotes = new FormDiscrepancyNotes();
			request.getSession().setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, discNotes);

		}

		DisplayTableOfContentsBean displayBean = getDisplayBean(objectPairs.ecb);

		// this is for generating side info panel
		StudySubjectDAO ssdao = getStudySubjectDAO();
		StudySubjectBean ssb = (StudySubjectBean) ssdao.findByPK(objectPairs.ecb.getStudySubjectId());
		ArrayList beans = getDisplayStudyEventsForStudySubject(ssb, getDataSource(), ub,
				currentRole, false);
		request.setAttribute("studySubject", ssb);
		request.setAttribute("beans", beans);
		request.setAttribute("eventCRF", objectPairs.ecb);

		StudyEventBean seb;
		if (objectPairs.ecb != null) {
			seb = (StudyEventBean) getStudyEventDAO().findByPK(objectPairs.ecb.getStudyEventId());
			if (seb != null && seb.getId() > 0) {
				request.setAttribute("studyEvent", seb);
			}
		}

		request.setAttribute(BEAN_DISPLAY, displayBean);

		boolean allowEnterData = true;
		if (StringUtil.isBlank(objectPairs.ecb.getInterviewerName())) {
			if (discNotes.getNotes(TableOfContentsServlet.INPUT_INTERVIEWER).isEmpty()) {
				allowEnterData = false;
			}
		}

		if (objectPairs.ecb.getDateInterviewed() == null) {
			if (discNotes.getNotes(TableOfContentsServlet.INPUT_INTERVIEW_DATE).isEmpty()) {
				allowEnterData = false;
			}
		}

		if (!allowEnterData) {
			request.setAttribute("allowEnterData", "no");
			forwardPage(Page.INTERVIEWER_ENTIRE_PAGE, request, response);
		} else {

			if (fp.getBoolean("editInterview", true)) {
				// user wants to edit interview info
				request.setAttribute("allowEnterData", "yes");
				forwardPage(Page.INTERVIEWER, request, response);
			} else {
				if (fp.isSubmitted() && !errors.isEmpty()) {
					// interview form submitted, but has blank field or
					// validation error
					request.setAttribute("allowEnterData", "no");
					forwardPage(Page.INTERVIEWER, request, response);
				} else {
					request.setAttribute("allowEnterData", "yes");
					forwardPage(Page.TABLE_OF_CONTENTS, request, response);
				}
			}
		}

	}

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response) throws InsufficientPermissionException {
        UserAccountBean ub = getUserAccountBean(request);
        StudyUserRoleBean currentRole = getCurrentRole(request);

        ObjectPairs objectPairs = getEventCRFAndAction(request);

		Role r = currentRole.getRole();
		boolean isSuper = DisplayEventCRFBean.isSuper(r);

		if (!SubmitDataServlet.maySubmitData(ub, currentRole)) {
			String exceptionName = resexception.getString("no_permission_to_perform_data_entry");
			String noAccessMessage = respage.getString("you_may_not_perform_data_entry_on_a_CRF") + " "
					+ respage.getString("change_study_contact_study_coordinator");

			addPageMessage(noAccessMessage, request);
			throw new InsufficientPermissionException(Page.MENU, exceptionName, "1");
		}

		// we're creating an event crf
		if (!objectPairs.action.equals(ACTION_START_INITIAL_DATA_ENTRY)) {
		    // we're editing an existing event crf
			if (!objectPairs.ecb.isActive()) {
				addPageMessage(respage.getString("event_CRF_not_exist_contact_study_coordinator"), request);
				throw new InsufficientPermissionException(Page.LIST_STUDY_SUBJECTS_SERVLET,
						resexception.getString("event_CRF_not_belong_current_study"), "1");
			}

			if (objectPairs.action.equals(ACTION_CONTINUE_INITIAL_DATA_ENTRY)) {
				if (!(objectPairs.ecb.getOwnerId() == ub.getId() || isSuper)) {
					addPageMessage(respage.getString("not_begin_DE_on_CRF_not_resume_DE"), request);
					throw new InsufficientPermissionException(Page.LIST_STUDY_SUBJECTS_SERVLET,
							resexception.getString("event_CRF_not_belong_current_user"), "1");
				}
			} else if (objectPairs.action.equals(ACTION_START_DOUBLE_DATA_ENTRY)) {
				if (objectPairs.ecb.getOwnerId() == ub.getId()) {
					if (!DisplayEventCRFBean.initialDataEntryCompletedMoreThanTwelveHoursAgo(objectPairs.ecb) && !isSuper) {
						addPageMessage(respage.getString("began_DE_on_CRF_marked_complete_less_12_not_begin_DE"), request);
						throw new InsufficientPermissionException(Page.LIST_STUDY_SUBJECTS_SERVLET,
								resexception.getString("owner_attempting_DDE_12_hours"), "1");
					}
				}
			} else if (objectPairs.action.equals(ACTION_CONTINUE_INITIAL_DATA_ENTRY)) {
				if (!(objectPairs.ecb.getValidatorId() == ub.getId() || isSuper)) {
					addPageMessage(respage.getString("not_begin_DDE_on_CRF_not_resume_DE"), request);
					throw new InsufficientPermissionException(Page.LIST_STUDY_SUBJECTS_SERVLET,
							resexception.getString("validation_event_CRF_not_begun_user"), "1");
				}
			} else if (objectPairs.action.equals(ACTION_ADMINISTRATIVE_EDITING)) {
				if (!isSuper) {
					addPageMessage(respage.getString("you_may_not_perform_administrative_editing") + " "
							+ respage.getString("change_study_contact_study_coordinator"), request);
					throw new InsufficientPermissionException(Page.LIST_STUDY_SUBJECTS_SERVLET,
							resexception.getString("no_permission_to_perform_administrative_editing"), "1");
				}
			} 
		} 
	}

	/**
	 * Assumes the Event CRF's data entry stage is not Uncompleted.
	 * 
	 * @param ecb
	 *            An Event CRF which should be displayed in the table of contents.
	 * @return A text link to the Table of Contents servlet for the bean.
	 */
	public String getLink(EventCRFBean ecb) {
        String answer = Page.TABLE_OF_CONTENTS_SERVLET.getFileName();
		answer += "?action=" + getActionForStage(ecb.getStage());
		answer += "&" + INPUT_ID + "=" + ecb.getId();
		return answer;
	}

	public static LinkedList<Integer> sectionIdsInToc(DisplayTableOfContentsBean toc) {
		LinkedList<Integer> ids = new LinkedList<Integer>();
		if (toc != null) {
			ArrayList<SectionBean> sectionBeans = toc.getSections();
			if (sectionBeans != null && sectionBeans.size() > 0) {
                for (SectionBean s : sectionBeans) {
                    ids.add(s.getId());
                }
			}
		}
		return ids;
	}

	/**
	 * Index starts from 0. If not in, return -1.
	 * 
	 * @param sb SectionBean
	 * @param toc DisplayTableOfContentsBean
	 * @param sectionIdsInToc LinkedList<Integer>
	 * @return int
	 */
	public static int sectionIndexInToc(SectionBean sb, DisplayTableOfContentsBean toc,
			LinkedList<Integer> sectionIdsInToc) {
		ArrayList<SectionBean> sectionBeans = new ArrayList<SectionBean>();
		int index = -1;
		if (toc != null) {
			sectionBeans = toc.getSections();
		}
		if (sectionBeans != null && sectionBeans.size() > 0) {
			for (int i = 0; i < sectionIdsInToc.size(); ++i) {
				if (sb.getId() == sectionIdsInToc.get(i)) {
					index = i;
					break;
				}
			}
		}
		return index;
	}
}
