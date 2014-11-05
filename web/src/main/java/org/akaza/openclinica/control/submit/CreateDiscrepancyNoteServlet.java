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

import com.clinovo.service.DiscrepancyDescriptionService;
import com.clinovo.util.SessionUtil;
import com.clinovo.util.ValidatorHelper;
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
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormDiscrepancyNotes;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.core.EmailEngine;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.core.form.StringUtil;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Create a discrepancy note for a data entity
 * 
 * @author jxu
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
@Component
public class CreateDiscrepancyNoteServlet extends Controller {

	public static final String UPDATED_DISCREPANCY_NOTE = "updatedDiscrepancyNote";

	public static final String DIS_TYPES = "discrepancyTypes";

	public static final String RES_STATUSES = "resolutionStatuses";

	public static final String ENTITY_ID = "id";

	public static final String ST_SUBJECT_ID = "stSubjectId";

	public static final String SUBJECT_ID = "subjectId";

	public static final String ITEM_ID = "itemId";

	public static final String IS_GROUP_ITEM = "isGroup";

	public static final String PARENT_ID = "parentId";// parent note id

	public static final String ENTITY_TYPE = "name";

	public static final String ENTITY_COLUMN = "column";

	public static final String ENTITY_FIELD = "field";

	public static final String FORM_DISCREPANCY_NOTES_NAME = "fdnotes";

	public static final String DIS_NOTE = "discrepancyNote";

	public static final String WRITE_TO_DB = "writeToDB";

	public static final String IS_REASON_FOR_CHANGE = "isRFC";

	public static final String PRESET_RES_STATUS = "strResStatus";

	public static final String CAN_MONITOR = "canMonitor";

	public static final String NEW_NOTE = "new";

	public static final String RES_STATUS_ID = "resStatusId";

	public static final String USER_ACCOUNTS = "userAccounts"; // use to provide

	public static final String USER_ACCOUNT_ID = "strUserAccountId"; // use to

	public static final String SUBMITTED_USER_ACCOUNT_ID = "userAccountId";

	public static final String EMAIL_USER_ACCOUNT = "sendEmail";

	public static final String WHICH_RES_STATUSES = "whichResStatus";

	public static final String SUBMITTED_DNS_MAP = "submittedDNs";

	public static final String TRANSFORMED_SUBMITTED_DNS = "transformedSubmittedDNs";

	public static final String EVENT_CRF_ID = "eventCRFId";
	public static final String PARENT_ROW_COUNT = "rowCount";

	public static final String NO_PERMISSION_EXCEPTION = "no_permission_to_create_discrepancy_note";

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		checkStudyLocked(Page.MENU_SERVLET, respage.getString("current_study_locked"), request, response);

		String exceptionName = resexception.getString("no_permission_to_create_discrepancy_note");
		String noAccessMessage = respage.getString("you_may_not_create_discrepancy_note")
				+ respage.getString("change_study_contact_sysadmin");

		if (SubmitDataServlet.mayViewData(ub, currentRole)) {
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

		String popupMessage = respage.getString("this_note_is_associated_with_data_in_the_CRF");

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

		int isGroup = fp.getInt(IS_GROUP_ITEM);
		int eventCRFId = fp.getInt(EVENT_CRF_ID);
		request.setAttribute(EVENT_CRF_ID, eventCRFId);
		int rowCount = fp.getInt(PARENT_ROW_COUNT);
		// run only once: try to recalculate writeToDB
		if (!StringUtil.isBlank(entityType) && "itemData".equalsIgnoreCase(entityType) && isGroup != 0
				&& eventCRFId != 0) {
			int ordinal_for_repeating_group_field = calculateOrdinal(request, isGroup, field, eventCRFId, rowCount);
			logger.info("*** found entity id: " + entityId);
			int writeToDBStatus = isWriteToDB(request, isGroup, field, entityId, itemId,
					ordinal_for_repeating_group_field, eventCRFId);
			writeToDB = writeToDBStatus != -1 && (writeToDBStatus == 1 || writeToDB);
		}

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
		} else if ("1".equalsIgnoreCase(monitor)) {// change to allow user to
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

		DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT, SessionUtil.getLocale(request));
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
				EventCRFBean ec = (EventCRFBean) ecdao.findByPK(itemData.getEventCRFId());

				request.setAttribute("eventCrfOwnerId", ec.getOwnerId());
				preUserId = ec.getOwnerId();
			} else if ("studySub".equalsIgnoreCase(entityType)) {
				ssub = (StudySubjectBean) getStudySubjectDAO().findByPK(entityId);
				SubjectBean sub = (SubjectBean) getSubjectDAO().findByPK(ssub.getSubjectId());
				preUserId = ssub.getOwnerId();
				popupMessage = respage.getString("this_note_is_associated_with_data_for_the_Study_Subject");

				if (!StringUtil.isBlank(column)) {
					if ("enrollment_date".equalsIgnoreCase(column)) {
						if (ssub.getEnrollmentDate() != null) {
							request.setAttribute("entityValue", dateFormatter.format(ssub.getEnrollmentDate()));
						} else {
							request.setAttribute("entityValue", resword.getString("N/A"));
						}
						request.setAttribute("entityName", resword.getString("enrollment_date"));
					} else if ("gender".equalsIgnoreCase(column)) {
						String genderToDisplay = resword.getString("not_specified");
						if ('m' == sub.getGender()) {
							genderToDisplay = resword.getString("male");
						} else if ('f' == sub.getGender()) {
							genderToDisplay = resword.getString("female");
						}
						request.setAttribute("entityValue", genderToDisplay);
						request.setAttribute("entityName", resword.getString("gender"));
					} else if ("date_of_birth".equalsIgnoreCase(column)) {
						if (sub.getDateOfBirth() != null) {
							request.setAttribute("entityValue", dateFormatter.format(sub.getDateOfBirth()));
						} else {
							request.setAttribute("entityValue", resword.getString("N/A"));
						}
						request.setAttribute("entityName", resword.getString("date_of_birth"));
					} else if ("year_of_birth".equalsIgnoreCase(column)) {
						if (sub.getDateOfBirth() != null) {
							GregorianCalendar cal = new GregorianCalendar();
							cal.setTime(sub.getDateOfBirth());
							request.setAttribute("entityValue", String.valueOf(cal.get(Calendar.YEAR)));
						} else {
							request.setAttribute("entityValue", resword.getString("N/A"));
						}
						request.setAttribute("entityName", resword.getString("year_of_birth"));
					} else if ("unique_identifier".equalsIgnoreCase(column)) {
						if (sub.getUniqueIdentifier() != null) {
							request.setAttribute("entityValue", sub.getUniqueIdentifier());
						}
						request.setAttribute("entityName", resword.getString("unique_identifier"));
					}
				}
			} else if ("subject".equalsIgnoreCase(entityType)) {
				SubjectBean sub = (SubjectBean) getSubjectDAO().findByPK(entityId);
				preUserId = sub.getOwnerId();
				popupMessage = respage.getString("this_note_is_associated_with_data_for_the_Subject");

				if (!StringUtil.isBlank(column)) {
					if ("gender".equalsIgnoreCase(column)) {
						String genderToDisplay = resword.getString("not_specified");
						if ('m' == sub.getGender()) {
							genderToDisplay = resword.getString("male");
						} else if ('f' == sub.getGender()) {
							genderToDisplay = resword.getString("female");
						}
						request.setAttribute("entityValue", genderToDisplay);
						request.setAttribute("entityName", resword.getString("gender"));
					} else if ("date_of_birth".equalsIgnoreCase(column)) {
						if (sub.getDateOfBirth() != null) {
							request.setAttribute("entityValue", dateFormatter.format(sub.getDateOfBirth()));
						}
						request.setAttribute("entityName", resword.getString("date_of_birth"));
					} else if ("year_of_birth".equalsIgnoreCase(column)) {
						if (sub.getDateOfBirth() != null) {
							GregorianCalendar cal = new GregorianCalendar();
							cal.setTime(sub.getDateOfBirth());
							request.setAttribute("entityValue", String.valueOf(cal.get(Calendar.YEAR)));
						}
						request.setAttribute("entityName", resword.getString("year_of_birth"));
					} else if ("unique_identifier".equalsIgnoreCase(column)) {
						request.setAttribute("entityValue", sub.getUniqueIdentifier());
						request.setAttribute("entityName", resword.getString("unique_identifier"));
					}
				}
			} else if ("studyEvent".equalsIgnoreCase(entityType)) {
				StudyEventBean se = (StudyEventBean) getStudyEventDAO().findByPK(entityId);
				preUserId = se.getOwnerId();
				popupMessage = respage.getString("this_note_is_associated_with_data_for_the_Study_Event");

				if (!StringUtil.isBlank(column)) {
					if ("location".equalsIgnoreCase(column)) {
						request.setAttribute("entityValue",
								(se.getLocation().equals("") || se.getLocation() == null) ? resword.getString("N/A")
										: se.getLocation());
						request.setAttribute("entityName", resword.getString("location"));
					} else if ("date_start".equalsIgnoreCase(column)) {
						if (se.getDateStarted() != null) {
							request.setAttribute("entityValue", dateFormatter.format(se.getDateStarted()));
						} else {
							request.setAttribute("entityValue", resword.getString("N/A"));
						}
						request.setAttribute("entityName", resword.getString("start_date"));
					} else if ("date_end".equalsIgnoreCase(column)) {
						if (se.getDateEnded() != null) {
							request.setAttribute("entityValue", dateFormatter.format(se.getDateEnded()));
						} else {
							request.setAttribute("entityValue", resword.getString("N/A"));
						}
						request.setAttribute("entityName", resword.getString("end_date"));
					}
				}
			} else if ("eventCrf".equalsIgnoreCase(entityType)) {
				EventCRFBean ec = (EventCRFBean) getEventCRFDAO().findByPK(entityId);
				preUserId = ec.getOwnerId();

				if (!StringUtil.isBlank(column)) {
					if ("date_interviewed".equals(column)) {
						if (ec.getDateInterviewed() != null) {
							request.setAttribute("entityValue", dateFormatter.format(ec.getDateInterviewed()));
						} else {
							request.setAttribute("entityValue", resword.getString("N/A"));
						}
						request.setAttribute("entityName", resword.getString("date_interviewed"));
					} else if ("interviewer_name".equals(column)) {
						request.setAttribute("entityValue", ec.getInterviewerName());
						request.setAttribute("entityName", resword.getString("interviewer_name"));
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

			if (parent.getId() == 0 || isNew) {// no parent, new note thread
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
							|| currentRole.getRole().equals(Role.INVESTIGATOR)) {
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
					|| currentRole.getRole().equals(Role.INVESTIGATOR)) {
				dnb.setDiscrepancyNoteTypeId(DiscrepancyNoteType.ANNOTATION.getId());
				dnb.setResolutionStatusId(ResolutionStatus.NOT_APPLICABLE.getId());
			}
			dnb.setOwnerId(parent.getOwnerId());
			String detailedDes = fp.getString("strErrMsg");
			if (detailedDes != null && !detailedDes.contains(respage.getString("reason_for_change_error"))) {
				dnb.setDetailedNotes(detailedDes);
				logger.debug("found strErrMsg: " + fp.getString("strErrMsg"));
			}

			// If the data entry form has not been saved yet, collecting info from parent page.
			dnb = getNoteInfo(request, dnb);// populate note infos
			if (dnb.getEventName() == null || dnb.getEventName().equals("")) {
				dnb.setEventName(fp.getString("eventName"));
			}
			if (dnb.getEventStart() == null) {
				dnb.setEventStart(fp.getDate("eventDate"));
			}
			if (dnb.getCrfName() == null || dnb.getCrfName().equals("")) {
				dnb.setCrfName(fp.getString("crfName"));
			}
			// // #4346 TBH 10/2009
			request.setAttribute(DIS_NOTE, dnb);
			request.setAttribute("unlock", "0");
			request.setAttribute(WRITE_TO_DB, writeToDB ? "1" : "0");// this should go from UI & here

			request.setAttribute(USER_ACCOUNTS, DiscrepancyNoteUtil.generateUserAccounts(ssub.getId(), currentStudy,
					new UserAccountDAO(getDataSource()), new StudyDAO(getDataSource())));

			// ideally should be only two cases
			if (currentRole.getRole().equals(Role.CLINICAL_RESEARCH_COORDINATOR)
					&& currentStudy.getId() != currentStudy.getParentStudyId()) {
				// assigning back to OP, tbh
				request.setAttribute(USER_ACCOUNT_ID, Integer.valueOf(parent.getOwnerId()).toString());
				logger.debug("assigned owner id: " + parent.getOwnerId());
			} else if (dnb.getEventCRFId() > 0) {
				logger.debug("found a event crf id: " + dnb.getEventCRFId());
				EventCRFDAO eventCrfDAO = getEventCRFDAO();
				EventCRFBean eventCrfBean = (EventCRFBean) eventCrfDAO.findByPK(dnb.getEventCRFId());
				request.setAttribute(USER_ACCOUNT_ID, Integer.valueOf(eventCrfBean.getOwnerId()).toString());
				logger.debug("assigned owner id: " + eventCrfBean.getOwnerId());
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
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
			v.addValidation("detailedDes", Validator.LENGTH_NUMERIC_COMPARISON,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 1000);

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
					Validator.addError(errors, RES_STATUS_ID, restext.getString("not_valid_res_status"));
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

			note = getNoteInfo(request, note);// populate note infos

			request.setAttribute(DIS_NOTE, note);
			request.setAttribute(WRITE_TO_DB, writeToDB ? "1" : "0");// this should go from UI & here
			ArrayList userAccounts = DiscrepancyNoteUtil.generateUserAccounts(ssub.getId(), currentStudy,
					new UserAccountDAO(getDataSource()), new StudyDAO(getDataSource()));

			request.setAttribute(USER_ACCOUNT_ID, Integer.valueOf(note.getAssignedUserId()).toString());
			// formality more than anything else, we should go to say the note
			// is done

			Role r = currentRole.getRole();
			if (r.equals(Role.STUDY_MONITOR) || r.equals(Role.INVESTIGATOR)
					|| r.equals(Role.CLINICAL_RESEARCH_COORDINATOR) || r.equals(Role.STUDY_ADMINISTRATOR)) { // investigator
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
					// if ("itemData".equalsIgnoreCase(entityType) && !isNew) {
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
						EventCRFBean ec = (EventCRFBean) getEventCRFDAO().findByPK(itemData.getEventCRFId());
						int userToAssignId = ec.getUpdaterId() == 0 ? ec.getOwnerId() : ec.getUpdaterId();
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

					addPageMessage(respage.getString("note_saved_into_db"), request);
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

	public static void saveNoteToSubmittedDNsMap(DiscrepancyNoteBean note, HttpServletRequest request) {
		if (note.getDiscrepancyNoteTypeId() != DiscrepancyNoteType.ANNOTATION.getId()
				&& note.getDiscrepancyNoteTypeId() != DiscrepancyNoteType.REASON_FOR_CHANGE.getId()
				&& note.getDiscrepancyNoteTypeId() != DiscrepancyNoteType.FAILEDVAL.getId())
			return;
		HashMap<String, DiscrepancyNoteBean> submittedDNs = (HashMap) request.getSession().getAttribute(
				SUBMITTED_DNS_MAP);
		if (submittedDNs == null)
			submittedDNs = new HashMap<String, DiscrepancyNoteBean>();
		if (submittedDNs.get(note.getField()) == null) {
			submittedDNs.put(note.getField(), note);
		}
		request.getSession().setAttribute(SUBMITTED_DNS_MAP, submittedDNs);
	}

	public static void turnOffIsDataChangedParamOfDN(String field, HttpServletRequest request) {
		setParameterForDN(field, "isDataChanged", "0", request);
	}

	public static void turnOffIsInRFCErrorParamOfDN(String field, HttpServletRequest request) {
		setParameterForDN(field, "isInRFCError", "0", request);
	}

	public static void turnOffIsInErrorParamOfDN(String field, HttpServletRequest request) {
		setParameterForDN(field, "isInError", "0", request);
	}

	public static void setParameterForDN(String field, String parameterName, String value, HttpServletRequest request) {
		setParameterForDN("1", field, parameterName, value, request);
	}

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

	public static Map<String, String> getMapWithParameters(String field, HttpServletRequest request) {
		Map<String, HashMap<String, String>> dnAdditionalCreatingParameters = (Map<String, HashMap<String, String>>) request
				.getSession().getAttribute("dnAdditionalCreatingParameters");
		if (dnAdditionalCreatingParameters != null) {
			return dnAdditionalCreatingParameters.containsKey(field) ? dnAdditionalCreatingParameters.get(field)
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
	 * Constructs a url for creating new note on 'view note list' page
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
		arguments.add("monitor" + "=" + 1);// of course, when resolving a note,
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

	private void manageReasonForChangeState(HttpSession session, String field) {
		HashMap<String, Boolean> noteSubmitted = (HashMap<String, Boolean>) session
				.getAttribute(DataEntryServlet.NOTE_SUBMITTED);
		if (noteSubmitted == null) {
			noteSubmitted = new HashMap<String, Boolean>();
		}
		noteSubmitted.put(field, Boolean.TRUE);
		session.setAttribute(DataEntryServlet.NOTE_SUBMITTED, noteSubmitted);
	}

	private int isWriteToDB(HttpServletRequest request, int isGroup, String field, int item_data_id, int item_id,
			int ordinal_for_repeating_group_field, int event_crf_id) {
		if (item_data_id > 0 && isGroup == -1) {// non repeating group; coming from showItemInput.jsp
			return 1;
		} else if (item_data_id < 0 && isGroup == -1) {// non repeating group; coming from showItemInput.jsp
			return -1;
		} else if (isGroup == 1) {// repeating group;
			// initial data entry or if template cell is empty (last row)
			if (item_data_id < 0) {
				return -1;
			}
			if (item_data_id > 0) {
				if (field.contains("_0input") || field.contains("manual")) {
					// get ordinal
					ItemDataDAO iddao = new ItemDataDAO(getDataSource(), SessionUtil.getLocale(request));

					boolean isExistInDB = iddao.isItemExists(item_id, ordinal_for_repeating_group_field, event_crf_id);
					return (isExistInDB) ? 1 : -1;
				} else if (field.contains("input")) {
					return -1;
				}
			}
		}
		return 0;
	}

	public int calculateOrdinal(HttpServletRequest request, int isGroup, String field_name, int event_crf_id,
			int rowCount) {
		int ordinal = 0;
		int start;
		int end;
		if (isGroup == -1) {
			return 1;
		}
		if (field_name.contains("_0input")) {
			return 1;
		}
		try {
			if (field_name.contains("manual")) {
				start = field_name.indexOf("manual") + 5;
				end = field_name.indexOf("input");
				if (start == 4 || end == -1) {
					return 0;
				}
				ordinal = Integer.valueOf(field_name.substring(start + 1, end));
				return ordinal + 1;

			} else {
				// get max ordinal from DB
				ItemDataDAO iddao = new ItemDataDAO(getDataSource(), SessionUtil.getLocale(request));
				String[] field_name_items = field_name.split("_");

				String group_oid = field_name.substring(0,
						field_name.indexOf(field_name_items[field_name_items.length - 1]) - 1);
				int maxOrdinal = iddao.getMaxOrdinalForGroupByGroupOID(group_oid, event_crf_id);

				// get ordinal from field
				end = field_name.indexOf("input");
				start = field_name.lastIndexOf("_");
				if (end == -1 || start == -1) {
					return 0;
				}
				ordinal = Integer.valueOf(field_name.substring(start + 1, end));
				return ordinal + maxOrdinal + rowCount;
			}
		} catch (NumberFormatException e) {
			// DO NOTHING
		}

		return ordinal;

	}

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
		message.append(MessageFormat.format(respage.getString("mailDNHeader"), assignedUser.getFirstName(),
				assignedUser.getLastName()));
		message.append("<A HREF='").append(SQLInitServlet.getSystemURL())
				.append("ViewNotes?module=submit&listNotes_f_discrepancyNoteBean.user=").append(assignedUser.getName())
				.append("&listNotes_f_entityName=").append(note.getEntityName()).append("'>")
				.append(SQLInitServlet.getField("sysURL")).append("</A><BR/>");
		message.append(respage.getString("you_received_this_from"));
		StudyBean study = (StudyBean) studyDAO.findByPK(note.getStudyId());
		SectionDAO sectionDAO = getSectionDAO();

		if ("itemData".equalsIgnoreCase(entityType)) {
			ItemDataBean itemData = (ItemDataBean) iddao.findByPK(note.getEntityId());
			item = (ItemBean) itemDAO.findByPK(itemData.getItemId());
			if (sectionId > 0) {
				section = (SectionBean) sectionDAO.findByPK(sectionId);
			}
		}

		message.append(respage.getString("email_body_separator"));
		message.append(respage.getString("disc_note_info"));
		message.append(respage.getString("email_body_separator"));
		message.append(MessageFormat.format(respage.getString("mailDNParameters1"), note.getDescription(),
				note.getDetailedNotes(), userName));
		message.append(respage.getString("email_body_separator"));
		message.append(respage.getString("entity_information"));
		message.append(respage.getString("email_body_separator"));
		message.append(MessageFormat.format(respage.getString("mailDNParameters2"), study.getName(),
				note.getSubjectName()));

		if (!("studySub".equalsIgnoreCase(entityType) || "subject".equalsIgnoreCase(entityType))) {
			message.append(MessageFormat.format(respage.getString("mailDNParameters3"), note.getEventName()));
			if (!"studyEvent".equalsIgnoreCase(note.getEntityType())) {
				message.append(MessageFormat.format(respage.getString("mailDNParameters4"), note.getCrfName()));
				if (!"eventCrf".equalsIgnoreCase(note.getEntityType())) {
					if (sectionId > 0) {
						message.append(MessageFormat.format(respage.getString("mailDNParameters5"), section.getName()));
					}
					message.append(MessageFormat.format(respage.getString("mailDNParameters6"), item.getName()));
				}
			}
		}

		message.append(respage.getString("email_body_separator"));
		message.append(MessageFormat.format(respage.getString("mailDNThanks"), study.getName()));
		message.append(respage.getString("email_body_separator"));
		message.append(respage.getString("disclaimer"));
		message.append(respage.getString("email_body_separator"));
		message.append(respage.getString("email_footer"));

		String emailBodyString = message.toString();
		sendEmail(alertEmail.trim(), EmailEngine.getAdminEmail(),
				MessageFormat.format(respage.getString("mailDNSubject"), study.getName(), note.getEntityName()),
				emailBodyString, true, null, null, true, request);
	}

	public static void checkSubjectInCorrectStudy(String entityType, StudySubjectBean ssub, StudyBean currentStudy,
			DataSource dataSource, Logger aLogger, HttpServletRequest request) throws InsufficientPermissionException {
		if ("subject".equals(entityType))
			return;

		StudyDAO studyDAO = new StudyDAO(dataSource);
		StudyBean studyBeanSub = (StudyBean) studyDAO.findByPK(ssub.getStudyId());
		if (ssub.getStudyId() != currentStudy.getId() && currentStudy.getId() != studyBeanSub.getParentStudyId()) {
			addPageMessage(
					respage.getString("you_may_not_create_discrepancy_note")
							+ respage.getString("change_study_contact_sysadmin"), request, aLogger);
			throw new InsufficientPermissionException(Page.MENU_SERVLET,
					resexception.getString(NO_PERMISSION_EXCEPTION), "1");
		}
	}

	private void sendDNTypesAndResStatusesLists(boolean isRFC, StudyUserRoleBean currentRole, HttpServletRequest request) {
		if (currentRole.getRole().equals(Role.CLINICAL_RESEARCH_COORDINATOR)
				|| currentRole.getRole().equals(Role.INVESTIGATOR)) {
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
			request.setAttribute(RES_STATUSES, ResolutionStatus.simpleList);
			request.setAttribute(WHICH_RES_STATUSES, "1");
		}
	}
}
