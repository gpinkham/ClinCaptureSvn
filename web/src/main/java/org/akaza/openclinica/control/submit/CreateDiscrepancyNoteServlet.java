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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import com.clinovo.util.DateUtil;

import org.akaza.openclinica.bean.core.DiscrepancyNoteType;
import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.ResolutionStatus;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.core.SpringServlet;
import org.akaza.openclinica.control.form.FormDiscrepancyNotes;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.core.EmailEngine;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.service.DiscrepancyNoteUtil;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.SQLInitServlet;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import com.clinovo.service.DiscrepancyDescriptionService;
import com.clinovo.util.EmailUtil;
import com.clinovo.util.ValidatorHelper;

/**
 * Create a discrepancy note for a data entity.
 * 
 * @author jxu
 * 
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Component
public class CreateDiscrepancyNoteServlet extends SpringServlet {

	public static final String UPDATED_DISCREPANCY_NOTE = "updatedDiscrepancyNote";

	public static final String DIS_TYPES = "discrepancyTypes";

	public static final String RES_STATUSES = "resolutionStatuses";

	public static final String ENTITY_ID = "id";

	public static final String ST_SUBJECT_ID = "stSubjectId";

	public static final String SUBJECT_ID = "subjectId";

	public static final String ITEM_ID = "itemId";

	public static final String PARENT_ID = "parentId"; // parent note id

	public static final String ENTITY_TYPE = "name";

	public static final String ENTITY_COLUMN = "column";

	public static final String ENTITY_FIELD = "field";

	public static final String FORM_DISCREPANCY_NOTES_NAME = "fdnotes";

	public static final String DIS_NOTE = "discrepancyNote";

	public static final String WRITE_TO_DB = "writeToDB";

	public static final String IS_REASON_FOR_CHANGE = "isRFC";

	public static final String PRESET_RES_STATUS = "strResStatus";

	public static final String CAN_MONITOR = "canMonitor";

	public static final String NEW_NOTE = "newNote";

	public static final String RES_STATUS_ID = "resStatusId";

	public static final String USER_ACCOUNTS = "userAccounts"; // use to provide

	public static final String USER_ACCOUNT_ID = "strUserAccountId"; // use to

	public static final String SUBMITTED_USER_ACCOUNT_ID = "userAccountId";

	public static final String EMAIL_USER_ACCOUNT = "sendEmail";

	public static final String WHICH_RES_STATUSES = "whichResStatus";

	public static final String SUBMITTED_DNS_MAP = "submittedDNs";

	public static final String TRANSFORMED_SUBMITTED_DNS = "transformedSubmittedDNs";

	public static final String EVENT_CRF_ID = "eventCRFId";

	public static final String NO_PERMISSION_EXCEPTION = "no_permission_to_create_discrepancy_note";

	private static final int TWO_FIFTY_FIVE = 255;
	private static final int ONE_THOUSAND = 1000;

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		checkStudyLocked(Page.MENU_SERVLET, getResPage().getString("current_study_locked"), request, response);

		String exceptionName = getResException().getString("no_permission_to_create_discrepancy_note");
		String noAccessMessage = getResPage().getString("you_may_not_create_discrepancy_note")
				+ getResPage().getString("change_study_contact_sysadmin");

		if (mayViewData(ub, currentRole) && !currentRole.isStudySponsor()) {
			return;
		}

		addPageMessage(noAccessMessage, request);
		throw new InsufficientPermissionException(Page.MENU, exceptionName, "1");
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		FormProcessor fp = new FormProcessor(request);
		DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(getDataSource());
		DiscrepancyDescriptionService dDescriptionService = (DiscrepancyDescriptionService) SpringServletAccess
				.getApplicationContext(getServletContext()).getBean("discrepancyDescriptionService");
		ArrayList types = DiscrepancyNoteType.toArrayList();

		request.setAttribute(DIS_TYPES, types);
		request.setAttribute(RES_STATUSES, ResolutionStatus.toArrayList());

		String popupMessage = getResPage().getString("this_note_is_associated_with_data_in_the_CRF");

		String field = fp.getString(ENTITY_FIELD);

		Map<String, String> additionalParameters = getMapWithParameters(field, request);

		SessionManager sm = getSessionManager(request);
		boolean isInError = !additionalParameters.isEmpty() && "1".equals(additionalParameters.get("isInError"));
		boolean isRFC = !additionalParameters.isEmpty() && calculateIsRFC(additionalParameters, request, sm);
		String originJSP = request.getParameter("originJSP") == null ? "" : request.getParameter("originJSP");
		request.setAttribute("originJSP", originJSP);
		request.setAttribute(IS_REASON_FOR_CHANGE, isRFC);

		boolean writeToDB = fp.getBoolean(WRITE_TO_DB, true); // this should be set based on a new property of
																// DisplayItemBean
		boolean isNew = fp.getBoolean(NEW_NOTE);
		request.setAttribute(NEW_NOTE, isNew ? "1" : "0");

		int entityId = fp.getInt(ENTITY_ID);
		// subjectId has to be added to the database when disc notes area saved
		// as entity_type 'subject'
		int stSubjectId = fp.getInt(ST_SUBJECT_ID);

		int itemId = fp.getInt(ITEM_ID);
		String entityType = fp.getString(ENTITY_TYPE);

		String column = fp.getString(ENTITY_COLUMN);
		int parentId = fp.getInt(PARENT_ID);

		int eventCRFId = fp.getInt(EVENT_CRF_ID);
		request.setAttribute(EVENT_CRF_ID, eventCRFId);
		EventCRFBean ecb = (EventCRFBean) getEventCRFDAO().findByPK(eventCRFId);

		String strResStatus = fp.getString(PRESET_RES_STATUS);
		if (!strResStatus.equals("")) {
			request.setAttribute(PRESET_RES_STATUS, strResStatus);
		}

		String monitor = fp.getString("monitor");
		String enterData = fp.getString("enterData");
		request.setAttribute("enterData", enterData);

		boolean enteringData = false;
		if (enterData != null && "1".equalsIgnoreCase(enterData)) {
			// variables are not set in JSP, so not from viewing data and from
			// entering data
			request.setAttribute(CAN_MONITOR, "1");
			request.setAttribute("monitor", monitor);

			enteringData = true;
		} else if ("1".equalsIgnoreCase(monitor)) { // change to allow user to
			// enter note for all items,
			// not just blank items

			request.setAttribute(CAN_MONITOR, "1");
			request.setAttribute("monitor", monitor);

		} else {
			request.setAttribute(CAN_MONITOR, "0");

		}

		if ("itemData".equalsIgnoreCase(entityType) && enteringData) {
			request.setAttribute("enterItemData", "yes");
		}

		StudySubjectDAO ssdao = new StudySubjectDAO(getDataSource());
		StudySubjectBean ssub = (StudySubjectBean) ssdao.findByPK(stSubjectId);
		int preUserId = 0;
		if (!StringUtil.isBlank(entityType)) {
			if ("itemData".equalsIgnoreCase(entityType) || "itemdata".equalsIgnoreCase(entityType)) {
				ItemBean item = (ItemBean) getItemDAO().findByPK(itemId);
				ItemDataBean itemData = (ItemDataBean) getItemDataDAO().findByPK(entityId);
				request.setAttribute("entityValue", itemData.getValue());
				request.setAttribute("entityName", item.getName());
				EventCRFDAO ecdao = getEventCRFDAO();
				ecb = (EventCRFBean) ecdao.findByPK(itemData.getEventCRFId());

				request.setAttribute("eventCrfOwnerId", ecb.getOwnerId());
				preUserId = ecb.getOwnerId();
			} else if ("studySub".equalsIgnoreCase(entityType)) {
				ssub = (StudySubjectBean) getStudySubjectDAO().findByPK(entityId);
				SubjectBean sub = (SubjectBean) getSubjectDAO().findByPK(ssub.getSubjectId());
				preUserId = ssub.getOwnerId();
				popupMessage = getResPage().getString("this_note_is_associated_with_data_for_the_Study_Subject");

				if (!StringUtil.isBlank(column)) {
					if ("enrollment_date".equalsIgnoreCase(column)) {
						if (ssub.getEnrollmentDate() != null) {
							request.setAttribute("entityValue", DateUtil.printDate(ssub.getEnrollmentDate(),
									getUserAccountBean().getUserTimeZoneId(), DateUtil.DatePattern.DATE, getLocale()));
						} else {
							request.setAttribute("entityValue", getResWord().getString("N/A"));
						}
						request.setAttribute("entityName", getResWord().getString("enrollment_date"));
					} else if ("gender".equalsIgnoreCase(column)) {
						String genderToDisplay = getResWord().getString("not_specified");
						if ('m' == sub.getGender()) {
							genderToDisplay = getResWord().getString("male");
						} else if ('f' == sub.getGender()) {
							genderToDisplay = getResWord().getString("female");
						}
						request.setAttribute("entityValue", genderToDisplay);
						request.setAttribute("entityName", getResWord().getString("gender"));
					} else if ("date_of_birth".equalsIgnoreCase(column)) {
						if (sub.getDateOfBirth() != null) {
							request.setAttribute("entityValue", DateUtil.printDate(sub.getDateOfBirth(),
									DateUtil.DatePattern.DATE, getLocale()));
						} else {
							request.setAttribute("entityValue", getResWord().getString("N/A"));
						}
						request.setAttribute("entityName", getResWord().getString("date_of_birth"));
					} else if ("year_of_birth".equalsIgnoreCase(column)) {
						if (sub.getDateOfBirth() != null) {
							GregorianCalendar cal = new GregorianCalendar();
							cal.setTime(sub.getDateOfBirth());
							request.setAttribute("entityValue", String.valueOf(cal.get(Calendar.YEAR)));
						} else {
							request.setAttribute("entityValue", getResWord().getString("N/A"));
						}
						request.setAttribute("entityName", getResWord().getString("year_of_birth"));
					} else if ("unique_identifier".equalsIgnoreCase(column)) {
						if (sub.getUniqueIdentifier() != null) {
							request.setAttribute("entityValue", sub.getUniqueIdentifier());
						}
						request.setAttribute("entityName", getResWord().getString("unique_identifier"));
					}
				}
			} else if ("subject".equalsIgnoreCase(entityType)) {
				SubjectBean sub = (SubjectBean) getSubjectDAO().findByPK(entityId);
				preUserId = sub.getOwnerId();
				popupMessage = getResPage().getString("this_note_is_associated_with_data_for_the_Subject");

				if (!StringUtil.isBlank(column)) {
					if ("gender".equalsIgnoreCase(column)) {
						String genderToDisplay = getResWord().getString("not_specified");
						if ('m' == sub.getGender()) {
							genderToDisplay = getResWord().getString("male");
						} else if ('f' == sub.getGender()) {
							genderToDisplay = getResWord().getString("female");
						}
						request.setAttribute("entityValue", genderToDisplay);
						request.setAttribute("entityName", getResWord().getString("gender"));
					} else if ("date_of_birth".equalsIgnoreCase(column)) {
						if (sub.getDateOfBirth() != null) {
							request.setAttribute("entityValue", DateUtil.printDate(sub.getDateOfBirth(),
									DateUtil.DatePattern.DATE, getLocale()));
						}
						request.setAttribute("entityName", getResWord().getString("date_of_birth"));
					} else if ("year_of_birth".equalsIgnoreCase(column)) {
						if (sub.getDateOfBirth() != null) {
							GregorianCalendar cal = new GregorianCalendar();
							cal.setTime(sub.getDateOfBirth());
							request.setAttribute("entityValue", String.valueOf(cal.get(Calendar.YEAR)));
						}
						request.setAttribute("entityName", getResWord().getString("year_of_birth"));
					} else if ("unique_identifier".equalsIgnoreCase(column)) {
						request.setAttribute("entityValue", sub.getUniqueIdentifier());
						request.setAttribute("entityName", getResWord().getString("unique_identifier"));
					}
				}
			} else if ("studyEvent".equalsIgnoreCase(entityType)) {
				StudyEventBean se = (StudyEventBean) getStudyEventDAO().findByPK(entityId);
				preUserId = se.getOwnerId();
				popupMessage = getResPage().getString("this_note_is_associated_with_data_for_the_Study_Event");

				if (!StringUtil.isBlank(column)) {
					if ("location".equalsIgnoreCase(column)) {
						request.setAttribute("entityValue", (se.getLocation().equals("") || se.getLocation() == null)
								? getResWord().getString("N/A")
								: se.getLocation());
						request.setAttribute("entityName", getResWord().getString("location"));
					} else if ("date_start".equalsIgnoreCase(column)) {
						if (se.getDateStarted() != null) {
							request.setAttribute("entityValue", DateUtil.printDate(se.getDateStarted(),
									getUserAccountBean().getUserTimeZoneId(), DateUtil.DatePattern.DATE, getLocale()));
						} else {
							request.setAttribute("entityValue", getResWord().getString("N/A"));
						}
						request.setAttribute("entityName", getResWord().getString("start_date"));
					} else if ("date_end".equalsIgnoreCase(column)) {
						if (se.getDateEnded() != null) {
							request.setAttribute("entityValue", DateUtil.printDate(se.getDateEnded(),
									getUserAccountBean().getUserTimeZoneId(), DateUtil.DatePattern.DATE, getLocale()));
						} else {
							request.setAttribute("entityValue", getResWord().getString("N/A"));
						}
						request.setAttribute("entityName", getResWord().getString("end_date"));
					}
				}
			} else if ("eventCrf".equalsIgnoreCase(entityType)) {
				ecb = (EventCRFBean) getEventCRFDAO().findByPK(entityId);
				preUserId = ecb.getOwnerId();

				if (!StringUtil.isBlank(column)) {
					if ("date_interviewed".equals(column)) {
						if (ecb.getDateInterviewed() != null) {
							request.setAttribute("entityValue", DateUtil.printDate(ecb.getDateInterviewed(),
									getUserAccountBean().getUserTimeZoneId(), DateUtil.DatePattern.DATE, getLocale()));
						} else {
							request.setAttribute("entityValue", getResWord().getString("N/A"));
						}
						request.setAttribute("entityName", getResWord().getString("date_interviewed"));
					} else if ("interviewer_name".equals(column)) {
						request.setAttribute("entityValue", ecb.getInterviewerName());
						request.setAttribute("entityName", getResWord().getString("interviewer_name"));
					}
				}
			}

		}

		// finds all the related notes
		ArrayList notes = (ArrayList) dndao
				.findAllByEntityAndColumnAndStudy(currentStudy, entityType, entityId, column);

		DiscrepancyNoteBean parent = new DiscrepancyNoteBean();
		if (parentId > 0) {
			dndao.setFetchMapping(true);
			parent = (DiscrepancyNoteBean) dndao.findByPK(parentId);
			if (parent.isActive()) {
				request.setAttribute("parent", parent);
			}
			dndao.setFetchMapping(false);
		}

		FormDiscrepancyNotes newNotes = (FormDiscrepancyNotes) request.getSession().getAttribute(
				FORM_DISCREPANCY_NOTES_NAME);

		if (newNotes == null) {
			newNotes = new FormDiscrepancyNotes();
		}

		boolean isNotesExistInSession = !newNotes.getNotes(field).isEmpty();
		if (!notes.isEmpty() || isNotesExistInSession) {
			request.setAttribute("hasNotes", "yes");
		} else {
			request.setAttribute("hasNotes", "no");
			logger.debug("has notes:" + "no");
		}

		// only for adding a new thread
		sendDNTypesAndResStatusesLists(isRFC, currentRole, request);
		request.setAttribute("popupMessage", popupMessage.replaceAll("'", "&rsquo;"));

		if (!fp.isSubmitted()) {
			DiscrepancyNoteBean dnb = new DiscrepancyNoteBean();
			if (stSubjectId > 0) {
				dnb.setSubjectName(ssub.getName());
				dnb.setSubjectId(ssub.getId());
				dnb.setStudySub(ssub);
				checkSubjectInCorrectStudy(entityType, ssub, currentStudy, getDataSource(), logger, request);
			}
			if (itemId > 0) {
				ItemBean item = (ItemBean) getItemDAO().findByPK(itemId);
				dnb.setEntityName(item.getName());
				request.setAttribute("item", item);
			}
			dnb.setEntityType(entityType);
			dnb.setColumn(column);
			dnb.setEntityId(entityId);
			dnb.setField(field);
			dnb.setParentDnId(parent.getId());
			dnb.setCreatedDate(new Date());
			dnb.setItemId(itemId);

			if (parent.getId() == 0 || isNew) { // no parent, new note thread
				if (enteringData) {
					dnb.setDiscrepancyNoteTypeId(DiscrepancyNoteType.ANNOTATION.getId());
					dnb.setResolutionStatusId(ResolutionStatus.NOT_APPLICABLE.getId());
					if (isRFC) {
						request.setAttribute("dDescriptionsMap",
								dDescriptionService.getAssignedToStudySortedDescriptions(currentStudy));
					}
					request.setAttribute("autoView", "0");
					// above set to automatically open up the user panel
				} else {
					dnb.setDiscrepancyNoteTypeId(DiscrepancyNoteType.QUERY.getId());
					if (currentRole.getRole().equals(Role.CLINICAL_RESEARCH_COORDINATOR)
							|| currentRole.getRole().equals(Role.INVESTIGATOR)
							|| currentRole.getRole().equals(Role.STUDY_EVALUATOR)) {
						request.setAttribute("autoView", "0");
					} else {
						request.setAttribute("autoView", "1");
						dnb.setAssignedUserId(preUserId);
					}
				}

			} else if (parent.getDiscrepancyNoteTypeId() > 0) {
				dnb.setDiscrepancyNoteTypeId(parent.getDiscrepancyNoteTypeId());

				// if it is a CRC then we should automatically propose a
				// solution, tbh

				if (currentRole.getRole().equals(Role.CLINICAL_RESEARCH_COORDINATOR)
						&& currentStudy.getId() != currentStudy.getParentStudyId()) {
					dnb.setResolutionStatusId(ResolutionStatus.RESOLVED.getId());
					request.setAttribute("autoView", "0");
					// hide the panel, tbh
				} else {
					dnb.setResolutionStatusId(ResolutionStatus.UPDATED.getId());
				}

			}

			if (currentRole.getRole().equals(Role.CLINICAL_RESEARCH_COORDINATOR)
					|| currentRole.getRole().equals(Role.INVESTIGATOR)
					|| currentRole.getRole().equals((Role.STUDY_EVALUATOR))) {
				dnb.setDiscrepancyNoteTypeId(DiscrepancyNoteType.ANNOTATION.getId());
				dnb.setResolutionStatusId(ResolutionStatus.NOT_APPLICABLE.getId());
			}
			dnb.setOwnerId(parent.getOwnerId());
			String detailedDes = fp.getString("strErrMsg");
			if (detailedDes != null && !detailedDes.contains(getResPage().getString("reason_for_change_error"))) {
				dnb.setDetailedNotes(detailedDes);
				logger.debug("found strErrMsg: " + fp.getString("strErrMsg"));
			}

			// If the data entry form has not been saved yet, collecting info from parent page.
			dnb = getNoteInfo(request, dnb); // populate note infos
			if (dnb.getEventName() == null || dnb.getEventName().equals("")) {
				dnb.setEventName(fp.getString("eventName"));
			}
			if (dnb.getEventStart() == null && !StringUtil.isBlank(fp.getString("eventDate"))) {
				dnb.setEventStart(fp.getDate("eventDate", DateUtil.DatePattern.TIMESTAMP_WITH_SECONDS));
			}
			if (dnb.getCrfName() == null || dnb.getCrfName().equals("")) {
				dnb.setCrfName(fp.getString("crfName"));
			}
			request.setAttribute(DIS_NOTE, dnb);
			request.setAttribute("unlock", "0");
			request.setAttribute(WRITE_TO_DB, writeToDB ? "1" : "0"); // this should go from UI & here

			request.setAttribute(USER_ACCOUNTS, DiscrepancyNoteUtil.generateUserAccounts(ssub.getId(), currentStudy,
					getUserAccountDAO(), getStudyDAO(), ecb, getEventDefinitionCRFDAO()));

			// ideally should be only two cases
			if (currentRole.getRole().equals(Role.CLINICAL_RESEARCH_COORDINATOR)
					&& currentStudy.getId() != currentStudy.getParentStudyId()) {
				// assigning back to OP, tbh
				request.setAttribute(USER_ACCOUNT_ID, Integer.valueOf(parent.getOwnerId()).toString());
				logger.debug("assigned owner id: " + parent.getOwnerId());
			} else if (dnb.getEventCRFId() > 0) {
				logger.debug("found a event crf id: " + dnb.getEventCRFId());
				EventCRFDAO eventCrfDAO = getEventCRFDAO();
				ecb = (EventCRFBean) eventCrfDAO.findByPK(dnb.getEventCRFId());
				request.setAttribute(USER_ACCOUNT_ID, Integer.valueOf(ecb.getOwnerId()).toString());
				logger.debug("assigned owner id: " + ecb.getOwnerId());
			}
			request.getSession().setAttribute("cdn_eventCRFId", fp.getString("eventCRFId"));
			request.getSession().setAttribute("cdn_groupOid", fp.getString("groupOid"));
			request.getSession().setAttribute("cdn_itemId", itemId);
			request.getSession().setAttribute("cdn_order", fp.getString("order"));

			// set the user account id for the user who completed data entry
			forwardPage(Page.ADD_DISCREPANCY_NOTE, request, response);
		} else {
			FormDiscrepancyNotes noteTree = (FormDiscrepancyNotes) request.getSession().getAttribute(
					FORM_DISCREPANCY_NOTES_NAME);

			if (noteTree == null) {
				noteTree = new FormDiscrepancyNotes();
				logger.debug("No note tree initialized in session");
			}

			Validator v = new Validator(new ValidatorHelper(request, getConfigurationDao()));
			String description = fp.getString("description");
			int typeId = fp.getInt("typeId");
			int assignedUserAccountId = fp.getInt(SUBMITTED_USER_ACCOUNT_ID);
			UserAccountDAO uacdao = getUserAccountDAO();
			int resStatusId = fp.getInt(RES_STATUS_ID);
			String detailedDes = fp.getString("detailedDes");
			int sectionId = fp.getInt("sectionId");
			DiscrepancyNoteBean note = new DiscrepancyNoteBean();
			v.addValidation("description", Validator.NO_BLANKS);
			v.addValidation("description", Validator.LENGTH_NUMERIC_COMPARISON,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, TWO_FIFTY_FIVE);
			v.addValidation("detailedDes", Validator.LENGTH_NUMERIC_COMPARISON,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, ONE_THOUSAND);

			v.addValidation("typeId", Validator.NO_BLANKS);

			HashMap errors = v.validate();
			note.setDescription(description);
			note.setDetailedNotes(detailedDes);
			note.setOwner(ub);
			note.setOwnerId(ub.getId());
			note.setCreatedDate(new Date());
			note.setResolutionStatusId(resStatusId);
			note.setDiscrepancyNoteTypeId(typeId);
			note.setItemId(itemId);

			// Annotations associated with an edit check to a Failed Validation (FV) note
			// Annotation associated with a field changed under Administrative Editing
			// (after a CRF is marked complete) to a Reason For Change note.
			if (DiscrepancyNoteType.get(typeId) == DiscrepancyNoteType.ANNOTATION
					|| DiscrepancyNoteType.get(typeId) == DiscrepancyNoteType.QUERY) {
				note.setAssignedUserId(isRFC ? ub.getId() : preUserId);
				note.setAssignedUser(isRFC ? ub : (UserAccountBean) uacdao.findByPK(preUserId));
				if ("itemdata".equalsIgnoreCase(entityType)) {
					if (isRFC && DiscrepancyNoteType.get(typeId) == DiscrepancyNoteType.ANNOTATION) {
						typeId = DiscrepancyNoteType.REASON_FOR_CHANGE.getId();
						note.setDisType(DiscrepancyNoteType.REASON_FOR_CHANGE);
						note.setDiscrepancyNoteTypeId(typeId);
					} else if (isInError) {
						typeId = DiscrepancyNoteType.FAILEDVAL.getId();
						note.setDisType(DiscrepancyNoteType.FAILEDVAL);
						note.setDiscrepancyNoteTypeId(typeId);
					}
				}
			}

			note.setParentDnId(parent.getId());
			if (typeId == DiscrepancyNoteType.FAILEDVAL.getId()) { // <- failed validation check
				note.setAssignedUser(ub);
				note.setAssignedUserId(ub.getId());
			}

			if (typeId != DiscrepancyNoteType.ANNOTATION.getId() && typeId != DiscrepancyNoteType.FAILEDVAL.getId()
					&& typeId != DiscrepancyNoteType.REASON_FOR_CHANGE.getId()) {
				if (assignedUserAccountId > 0) {
					note.setAssignedUserId(assignedUserAccountId);
					note.setAssignedUser((UserAccountBean) uacdao.findByPK(assignedUserAccountId));
					logger.debug("^^^ found assigned user id: " + assignedUserAccountId);
				} else {
					// a little bit of a workaround, should ideally be always from
					// the form
					note.setAssignedUserId(parent.getOwnerId());
					logger.debug("found user assigned id, in the PARENT OWNER ID: " + parent.getOwnerId()
							+ " note that user assgined id did not work: " + assignedUserAccountId);
				}
			}

			note.setField(field);

			if (DiscrepancyNoteType.ANNOTATION.getId() == note.getDiscrepancyNoteTypeId()
					|| DiscrepancyNoteType.REASON_FOR_CHANGE.getId() == note.getDiscrepancyNoteTypeId()) {
				note.setResStatus(ResolutionStatus.NOT_APPLICABLE);
				note.setResolutionStatusId(ResolutionStatus.NOT_APPLICABLE.getId());
			}

			if (DiscrepancyNoteType.QUERY.getId() == note.getDiscrepancyNoteTypeId()) {
				if (ResolutionStatus.NOT_APPLICABLE.getId() == note.getResolutionStatusId()) {
					Validator.addError(errors, RES_STATUS_ID, getResText().getString("not_valid_res_status"));
				}
			}

			if (DiscrepancyNoteType.FAILEDVAL.getId() == note.getDiscrepancyNoteTypeId()) {
				note.setResStatus(ResolutionStatus.OPEN);
				note.setResolutionStatusId(ResolutionStatus.OPEN.getId());
			}

			if (!parent.isActive()) {
				note.setEntityId(entityId);
				note.setEntityType(entityType);
				note.setColumn(column);
			} else {
				note.setEntityId(parent.getEntityId());
				note.setEntityType(parent.getEntityType());
				if (!StringUtil.isBlank(parent.getColumn())) {
					note.setColumn(parent.getColumn());
				} else {
					note.setColumn(column);
				}
				note.setParentDnId(parent.getId());
			}

			if (stSubjectId == 0) {
				note.setStudyId(currentStudy.getId());
			} else {
				note.setStudyId(ssub.getStudyId());
			}

			note = getNoteInfo(request, note);

			request.setAttribute(DIS_NOTE, note);
			request.setAttribute(WRITE_TO_DB, writeToDB ? "1" : "0");
			ArrayList userAccounts = DiscrepancyNoteUtil.generateUserAccounts(ssub.getId(), currentStudy,
					getUserAccountDAO(), getStudyDAO(), ecb, getEventDefinitionCRFDAO());

			request.setAttribute(USER_ACCOUNT_ID, Integer.valueOf(note.getAssignedUserId()).toString());
			// formality more than anything else, we should go to say the note
			// is done

			Role r = currentRole.getRole();
			if (Role.isMonitor(r) || r.equals(Role.INVESTIGATOR) || r.equals(Role.STUDY_EVALUATOR)
					|| r.equals(Role.CLINICAL_RESEARCH_COORDINATOR) || r.equals(Role.STUDY_ADMINISTRATOR)) {
				request.setAttribute("unlock", "1");
				logger.debug("set UNLOCK to ONE");
			} else {
				request.setAttribute("unlock", "0");
				logger.debug("set UNLOCK to ZERO");
			}

			request.setAttribute(USER_ACCOUNTS, userAccounts);

			if (errors.isEmpty()) {
				if (!isWritingIntoDBAllowed(writeToDB, request.getSession(), note)) {
					noteTree.addNote(field, note);
					noteTree.addIdNote(note.getEntityId(), field);
					request.getSession().setAttribute(FORM_DISCREPANCY_NOTES_NAME, noteTree);
					/*
					 * Setting a marker to check later while saving administrative edited data. This is needed to make
					 * sure the system flags error while changing data for items which already has a DiscrepanyNote
					 */
					if (note.getDiscrepancyNoteTypeId() == DiscrepancyNoteType.REASON_FOR_CHANGE.getId()) {
						turnOffIsDataChangedParamOfDN(field, request);
						turnOffIsInRFCErrorParamOfDN(field, request);
						turnOffIsInErrorParamOfDN(field, request);
						manageReasonForChangeState(request.getSession(), field);
					} else if (note.getDiscrepancyNoteTypeId() == DiscrepancyNoteType.FAILEDVAL.getId()) {
						manageReasonForChangeState(request.getSession(), field);
					}

					saveNoteToSubmittedDNsMap(note, request);
					request.setAttribute(UPDATED_DISCREPANCY_NOTE, note);
					request.setAttribute("responseMessage", "Show pop-up");
					forwardPage(Page.ADD_DISCREPANCY_NOTE_DIV, request, response);
				} else {
					// if not creating a new thread(note), update existing notes
					// if necessary
					int pdnId = note != null ? note.getParentDnId() : 0;
					if (pdnId > 0) {
						logger.debug("Create:find parent note for item data:" + note.getEntityId());

						DiscrepancyNoteBean pNote = (DiscrepancyNoteBean) dndao.findByPK(pdnId);

						logger.debug("setting DN owner id: " + pNote.getOwnerId());

						note.setOwnerId(pNote.getOwnerId());

						if (note.getDiscrepancyNoteTypeId() == pNote.getDiscrepancyNoteTypeId()) {

							if (note.getResolutionStatusId() != pNote.getResolutionStatusId()) {
								pNote.setResolutionStatusId(note.getResolutionStatusId());
								dndao.update(pNote);
							}

							if (note.getAssignedUserId() != pNote.getAssignedUserId()) {
								pNote.setAssignedUserId(note.getAssignedUserId());
								if (pNote.getAssignedUserId() > 0) {
									dndao.updateAssignedUser(pNote);
								} else {
									dndao.updateAssignedUserToNull(pNote);
								}
							}
						}
					}

					int dnTypeId = note.getDiscrepancyNoteTypeId();
					if ("itemData".equalsIgnoreCase(entityType)
							&& (DiscrepancyNoteType.FAILEDVAL.getId() == dnTypeId || DiscrepancyNoteType.QUERY.getId() == dnTypeId)
							&& note.getAssignedUserId() == 0) {

						ItemDataBean itemData = (ItemDataBean) getItemDataDAO().findByPK(entityId);
						ecb = (EventCRFBean) getEventCRFDAO().findByPK(itemData.getEventCRFId());
						int userToAssignId = ecb.getUpdaterId() == 0 ? ecb.getOwnerId() : ecb.getUpdaterId();
						note.setAssignedUserId(userToAssignId);
					}

					note = (DiscrepancyNoteBean) dndao.create(note);

					dndao.createMapping(note);

					request.setAttribute(DIS_NOTE, note);

					if (note.getParentDnId() == 0) {
						note.setParentDnId(note.getId());
						note = (DiscrepancyNoteBean) dndao.create(note);
						dndao.createMapping(note);
					}

					if (note.getDiscrepancyNoteTypeId() == DiscrepancyNoteType.REASON_FOR_CHANGE.getId()
							|| note.getDiscrepancyNoteTypeId() == DiscrepancyNoteType.FAILEDVAL.getId()) {
						turnOffIsDataChangedParamOfDN(field, request);
						turnOffIsInRFCErrorParamOfDN(field, request);
						turnOffIsInErrorParamOfDN(field, request);
						manageReasonForChangeState(request.getSession(), field);
					}

					logger.debug("found resolution status: " + note.getResolutionStatusId());

					String email = fp.getString(EMAIL_USER_ACCOUNT);

					logger.debug("found email: " + email);
					if (note.getAssignedUserId() > 0 && "1".equals(email.trim())
							&& DiscrepancyNoteType.QUERY.getId() == note.getDiscrepancyNoteTypeId()) {
						sendDNEmail(sectionId, entityType, ub.getName(), note, uacdao, request);
					} else {
						logger.debug("did not send email, but did save DN");
					}

					addPageMessage(getResPage().getString("note_saved_into_db"), request);
					saveNoteToSubmittedDNsMap(note, request);
					request.setAttribute(UPDATED_DISCREPANCY_NOTE, note);
					request.setAttribute("responseMessage", "Save Done");
					forwardPage(Page.ADD_DISCREPANCY_NOTE_DIV, request, response);
				}
			} else {
				if (parentId > 0) {
					if (note.getResolutionStatusId() == ResolutionStatus.NOT_APPLICABLE.getId()) {
						request.setAttribute("autoView", "0");
					}
				} else {
					if (note.getDiscrepancyNoteTypeId() == DiscrepancyNoteType.QUERY.getId()) {
						request.setAttribute("autoView", "1");
					} else {
						request.setAttribute("autoView", "0");
					}
				}

				if (isRFC) {
					request.setAttribute("dDescriptionsMap",
							dDescriptionService.getAssignedToStudySortedDescriptions(currentStudy));
				}

				setInputMessages(errors, request);
				request.setAttribute("responseMessage", "Error in data");
				forwardPage(Page.ADD_DISCREPANCY_NOTE_DIV, request, response);
			}
		}
	}

	/**
	 * 
	 * @param note
	 *            DiscrepancyNoteBean
	 * @param request
	 *            HttpServletRequest
	 */
	public static void saveNoteToSubmittedDNsMap(DiscrepancyNoteBean note, HttpServletRequest request) {
		if (note.getDiscrepancyNoteTypeId() != DiscrepancyNoteType.ANNOTATION.getId()
				&& note.getDiscrepancyNoteTypeId() != DiscrepancyNoteType.REASON_FOR_CHANGE.getId()
				&& note.getDiscrepancyNoteTypeId() != DiscrepancyNoteType.FAILEDVAL.getId()) {
			return;
		}
		Map<String, List<DiscrepancyNoteBean>> submittedDNs = (Map) request.getSession().getAttribute(
				SUBMITTED_DNS_MAP);
		submittedDNs = submittedDNs == null ? new HashMap<String, List<DiscrepancyNoteBean>>() : submittedDNs;
		
		List<DiscrepancyNoteBean> notes;
		if (submittedDNs.containsKey(note.getField())) {
			notes = submittedDNs.get(note.getField());
		} else {
			notes = new ArrayList<DiscrepancyNoteBean>();
		}

		notes.add(note);
		submittedDNs.put(note.getField(), notes);
		
		request.getSession().setAttribute(SUBMITTED_DNS_MAP, submittedDNs);
	}

	/**
	 * 
	 * @param field
	 *            String
	 * @param request
	 *            HttpServletRequest
	 */
	public static void turnOffIsDataChangedParamOfDN(String field, HttpServletRequest request) {
		setParameterForDN(field, "isDataChanged", "0", request);
	}

	/**
	 * 
	 * @param field
	 *            String
	 * @param request
	 *            HttpServletRequest
	 */
	public static void turnOffIsInRFCErrorParamOfDN(String field, HttpServletRequest request) {
		setParameterForDN(field, "isInRFCError", "0", request);
	}

	/**
	 * 
	 * @param field
	 *            String
	 * @param request
	 *            HttpServletRequest
	 */
	public static void turnOffIsInErrorParamOfDN(String field, HttpServletRequest request) {
		setParameterForDN(field, "isInError", "0", request);
	}

	/**
	 * 
	 * @param field
	 *            String
	 * @param parameterName
	 *            String
	 * @param value
	 *            String
	 * @param request
	 *            HttpServletRequest
	 */
	public static void setParameterForDN(String field, String parameterName, String value, HttpServletRequest request) {
		setParameterForDN("1", field, parameterName, value, request);
	}

	/**
	 * 
	 * @param toOverwrite
	 *            String
	 * @param field
	 *            String
	 * @param parameterName
	 *            String
	 * @param value
	 *            String
	 * @param request
	 *            HttpServletRequest
	 */
	public static void setParameterForDN(String toOverwrite, String field, String parameterName, String value,
			HttpServletRequest request) {
		Map<String, HashMap<String, String>> dnAdditionalCreatingParameters = (Map<String, HashMap<String, String>>) request
				.getSession().getAttribute("dnAdditionalCreatingParameters");
		if (dnAdditionalCreatingParameters != null) {
			if (field != null && dnAdditionalCreatingParameters.get(field) != null) {
				changeParameter(toOverwrite, field, parameterName, value, dnAdditionalCreatingParameters);
			}
		}
	}

	private static void changeParameter(String toOverwrite, String field, String parameterName, String value,
			Map<String, HashMap<String, String>> dnAdditionalCreatingParameters) {
		// logic of changing parameter is here
		if ("0".equals(toOverwrite)) {
			if (dnAdditionalCreatingParameters.get(field).get(parameterName) == null
					|| "".equals(dnAdditionalCreatingParameters.get(field).get(parameterName))) {
				dnAdditionalCreatingParameters.get(field).put(parameterName, value);
			}
		} else {
			dnAdditionalCreatingParameters.get(field).put(parameterName, value);
		}
	}

	/**
	 * 
	 * @param field
	 *            String
	 * @param request
	 *            HttpServletRequest
	 * @return Map<String, String>
	 */
	public static Map<String, String> getMapWithParameters(String field, HttpServletRequest request) {
		Map<String, HashMap<String, String>> dnAdditionalCreatingParameters = (Map<String, HashMap<String, String>>) request
				.getSession().getAttribute("dnAdditionalCreatingParameters");
		if (dnAdditionalCreatingParameters != null) {
			return dnAdditionalCreatingParameters.containsKey(field)
					? dnAdditionalCreatingParameters.get(field)
					: new HashMap<String, String>();
		} else {
			return new HashMap<String, String>();
		}
	}

	private boolean isWritingIntoDBAllowed(boolean writeToDB, HttpSession session, DiscrepancyNoteBean note) {
		boolean result = writeToDB;
		try {
			String groupOid = (String) session.getAttribute("cdn_groupOid");
			if (groupOid != null && !groupOid.trim().isEmpty()) {
				Integer eventCRFId = Integer.parseInt((String) session.getAttribute("cdn_eventCRFId"));
				Integer order = Integer.parseInt((String) session.getAttribute("cdn_order"));
				Integer itemId = (Integer) session.getAttribute("cdn_itemId");
				ItemDataDAO iddao = getItemDataDAO();
				note.setEventCRFId(eventCRFId);
				ItemDataBean itemDataBean = iddao.findByItemIdAndEventCRFIdAndOrdinal(itemId, eventCRFId, ++order);
				result = !(itemDataBean == null || itemDataBean.getId() == 0);
			}
			if (note.getEntityId() == 0) {
				result = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return writeToDB && result;
	}

	/**
	 * Constructs a url for creating new note on 'view note list' page.
	 * 
	 * @param note
	 *            DiscrepancyNoteBean
	 * @param preset
	 *            ResolutionStatus
	 * @param toView
	 *            boolean
	 * @return String
	 */
	public static String getAddChildURL(DiscrepancyNoteBean note, ResolutionStatus preset, boolean toView) {
		ArrayList<String> arguments = new ArrayList<String>();

		arguments.add(ENTITY_TYPE + "=" + note.getEntityType());
		arguments.add(ENTITY_ID + "=" + note.getEntityId());
		arguments.add(WRITE_TO_DB + "=" + "1");
		arguments.add("monitor" + "=" + 1); // of course, when resolving a note,
		// we have monitor privilege

		if (preset.isActive()) {
			arguments.add(PRESET_RES_STATUS + "=" + String.valueOf(preset.getId()));
		}

		if (toView) {
			String columnValue = "".equalsIgnoreCase(note.getColumn()) ? "value" : note.getColumn();
			arguments.add(ENTITY_FIELD
					+ "="
					+ (note.getEntityType().equalsIgnoreCase("itemData") ? note.getField() : DiscrepancyNoteBean
							.getColumnToFieldMap().get(note.getColumn())));
			arguments.add(ENTITY_COLUMN + "=" + columnValue);
			arguments.add(ST_SUBJECT_ID + "=" + note.getSubjectId());
			arguments.add(ITEM_ID + "=" + note.getItemId());
			String queryString = StringUtil.join("&", arguments);
			return "ViewDiscrepancyNote?" + queryString;
		} else {
			arguments.add(PARENT_ID + "=" + note.getId());
			String queryString = StringUtil.join("&", arguments);
			return "CreateDiscrepancyNote?" + queryString;
		}
	}

	static void manageReasonForChangeState(HttpSession session, Object fieldNameOrItemDataBeanId) {
		HashMap<Object, Boolean> noteSubmitted = (HashMap<Object, Boolean>) session
				.getAttribute(DataEntryServlet.NOTE_SUBMITTED);
		if (noteSubmitted == null) {
			noteSubmitted = new HashMap<Object, Boolean>();
		}
		noteSubmitted.put(fieldNameOrItemDataBeanId, Boolean.TRUE);
		session.setAttribute(DataEntryServlet.NOTE_SUBMITTED, noteSubmitted);
	}

	/**
	 * 
	 * @param study
	 *            StudyBean
	 * @param sm
	 *            SessionManager
	 * @return boolean
	 */
	public static boolean isStudyParamForRFCSwitchOn(StudyBean study, SessionManager sm) {
		// Study Parameter: Forced Reason For Change in Administrative Editing
		StudyDAO studyDAO = new StudyDAO(sm.getDataSource());
		if (study.getParentStudyId() > 0) {
			StudyBean parentStudy = (StudyBean) studyDAO.findByPK(study.getParentStudyId());
			return parentStudy.getStudyParameterConfig().getAdminForcedReasonForChange().equals("true");
		} else {
			return study.getStudyParameterConfig().getAdminForcedReasonForChange().equals("true");
		}
	}

	/**
	 * 
	 * @param additionalParameters
	 *            Map<String, String>
	 * @param request
	 *            HttpServletRequest
	 * @param sm
	 *            Session Manager
	 * @return boolean
	 */
	public static boolean calculateIsRFC(Map<String, String> additionalParameters, HttpServletRequest request,
			SessionManager sm) {
		boolean isInRFCError = "1".equals(additionalParameters.get("isInRFCError"));
		boolean isDataChanged = "1".equals(additionalParameters.get("isDataChanged"));

		String originJSP = request.getParameter("originJSP") == null ? "" : request.getParameter("originJSP");
		return originJSP.equals("administrativeEditing")
				&& (isDataChanged || isInRFCError)
				&& CreateDiscrepancyNoteServlet.isStudyParamForRFCSwitchOn((StudyBean) request.getSession()
						.getAttribute("study"), sm);
	}

	private void sendDNEmail(int sectionId, String entityType, String userName, DiscrepancyNoteBean note,
			UserAccountDAO userAccountDAO, HttpServletRequest request) throws Exception {
		// logic of changing parameter is there
		logger.debug("++++++ found our way here: " + note.getDiscrepancyNoteTypeId() + " id number and "
				+ note.getDisType().getName());
		// generate email for user here
		StringBuilder message = new StringBuilder();

		// generate message here
		ItemDAO itemDAO = getItemDAO();
		ItemDataDAO iddao = getItemDataDAO();
		ItemBean item = new ItemBean();
		SectionBean section = new SectionBean();

		StudyDAO studyDAO = getStudyDAO();
		UserAccountBean assignedUser = (UserAccountBean) userAccountDAO.findByPK(note.getAssignedUserId());
		String alertEmail = assignedUser.getEmail();
		message.append(EmailUtil.getEmailBodyStart());
		message.append(MessageFormat.format(getResPage().getString("mailDNHeader"), assignedUser.getFirstName(),
				assignedUser.getLastName()));
		message.append("<A HREF='").append(SQLInitServlet.getSystemURL())
				.append("ViewNotes?module=submit&listNotes_f_discrepancyNoteBean.user=").append(assignedUser.getName())
				.append("&listNotes_f_entityName=").append(note.getEntityName()).append("'>")
				.append(SQLInitServlet.getField("sysURL")).append("</A><BR/>");
		message.append(getResPage().getString("you_received_this_from"));
		StudyBean study = (StudyBean) studyDAO.findByPK(note.getStudyId());
		SectionDAO sectionDAO = getSectionDAO();

		if ("itemData".equalsIgnoreCase(entityType)) {
			ItemDataBean itemData = (ItemDataBean) iddao.findByPK(note.getEntityId());
			item = (ItemBean) itemDAO.findByPK(itemData.getItemId());
			if (sectionId > 0) {
				section = (SectionBean) sectionDAO.findByPK(sectionId);
			}
		}

		message.append(getResPage().getString("email_body_separator"));
		message.append(getResPage().getString("disc_note_info"));
		message.append(getResPage().getString("email_body_separator"));
		message.append(MessageFormat.format(getResPage().getString("mailDNParameters1"), note.getDescription(),
				note.getDetailedNotes(), userName));
		message.append(getResPage().getString("email_body_separator"));
		message.append(getResPage().getString("entity_information"));
		message.append(getResPage().getString("email_body_separator"));
		message.append(MessageFormat.format(getResPage().getString("mailDNParameters2"), study.getName(),
				note.getSubjectName()));

		if (!("studySub".equalsIgnoreCase(entityType) || "subject".equalsIgnoreCase(entityType))) {
			message.append(MessageFormat.format(getResPage().getString("mailDNParameters3"), note.getEventName()));
			if (!"studyEvent".equalsIgnoreCase(note.getEntityType())) {
				message.append(MessageFormat.format(getResPage().getString("mailDNParameters4"), note.getCrfName()));
				if (!"eventCrf".equalsIgnoreCase(note.getEntityType())) {
					if (sectionId > 0) {
						message.append(MessageFormat.format(getResPage().getString("mailDNParameters5"), section.getName()));
					}
					message.append(MessageFormat.format(getResPage().getString("mailDNParameters6"), item.getName()));
				}
			}
		}

		message.append(getResPage().getString("email_body_separator"));
		message.append(MessageFormat.format(getResPage().getString("mailDNThanks"), study.getName()));
		message.append(getResPage().getString("email_body_separator"));
		message.append(getResPage().getString("disclaimer"));
		message.append(getResPage().getString("email_body_separator"));
		message.append(EmailUtil.getEmailBodyEnd() + EmailUtil.getEmailFooter(CoreResources.getSystemLocale()));

		String emailBodyString = message.toString();
		sendEmail(alertEmail.trim(), EmailEngine.getAdminEmail(),
				MessageFormat.format(getResPage().getString("mailDNSubject"), study.getName(), note.getEntityName()),
				emailBodyString, true, null, null, true, request);
	}

	/**
	 * 
	 * @param entityType
	 *            String
	 * @param ssub
	 *            StudySubjectBean
	 * @param currentStudy
	 *            StudyBean
	 * @param dataSource
	 *            DataSource
	 * @param aLogger
	 *            Logger
	 * @param request
	 *            HttpServletRequest
	 * @throws InsufficientPermissionException
	 *             thrown if incorrect study is detected.
	 */
	public static void checkSubjectInCorrectStudy(String entityType, StudySubjectBean ssub, StudyBean currentStudy,
			DataSource dataSource, Logger aLogger, HttpServletRequest request) throws InsufficientPermissionException {
		if ("subject".equals(entityType)) {
			return;
		}
		StudyDAO studyDAO = new StudyDAO(dataSource);
		StudyBean studyBeanSub = (StudyBean) studyDAO.findByPK(ssub.getStudyId());
		if (ssub.getStudyId() != currentStudy.getId() && currentStudy.getId() != studyBeanSub.getParentStudyId()) {
			addPageMessage(
					getResPage().getString("you_may_not_create_discrepancy_note")
							+ getResPage().getString("change_study_contact_sysadmin"), request, aLogger);
			throw new InsufficientPermissionException(Page.MENU_SERVLET,
					getResException().getString(NO_PERMISSION_EXCEPTION), "1");
		}
	}

	private void sendDNTypesAndResStatusesLists(boolean isRFC, StudyUserRoleBean currentRole, HttpServletRequest request) {
		if (currentRole.getRole().equals(Role.CLINICAL_RESEARCH_COORDINATOR)
				|| currentRole.getRole().equals(Role.INVESTIGATOR)
				|| currentRole.getRole().equals(Role.STUDY_EVALUATOR)) {
			request.setAttribute("showStatus", false);
			request.setAttribute(RES_STATUSES, Arrays.asList(ResolutionStatus.NOT_APPLICABLE));
			request.setAttribute(DIS_TYPES, Arrays.asList(DiscrepancyNoteType.ANNOTATION));
			request.setAttribute(WHICH_RES_STATUSES, "2");
		} else {
			if (isRFC) {
				request.setAttribute(DIS_TYPES, Arrays.asList(DiscrepancyNoteType.ANNOTATION));
			} else {
				request.setAttribute(DIS_TYPES, DiscrepancyNoteType.simpleList);
			}
			request.setAttribute("showStatus", true);
			request.setAttribute(RES_STATUSES, ResolutionStatus.SIMPLE_LIST);
			request.setAttribute(WHICH_RES_STATUSES, "1");
		}
	}
}
