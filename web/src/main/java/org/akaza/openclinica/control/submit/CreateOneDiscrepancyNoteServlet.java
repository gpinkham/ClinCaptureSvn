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
 *
 * For details see: http://www.openclinica.org/license
 */
package org.akaza.openclinica.control.submit;

import com.clinovo.util.ValidatorHelper;
import org.akaza.openclinica.bean.core.DiscrepancyNoteType;
import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.ResolutionStatus;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormDiscrepancyNotes;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.core.EmailEngine;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.SQLInitServlet;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Create a discrepancy note
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
@Component
public class CreateOneDiscrepancyNoteServlet extends Controller {

	public static final String UPDATED_DISCREPANCY_NOTE = "updatedDiscrepancyNote";
	public static final String REFRESH_PARENT_WINDOW = "refreshParentWindow";
	public static final String ENTITY_ID = "id";
	public static final String ITEM_ID = "itemId";
	public static final String PARENT_ID = "parentId";// parent note id
	public static final String ENTITY_TYPE = "name";
	public static final String ENTITY_COLUMN = "column";
	public static final String ENTITY_FIELD = "field";
	public static final String FORM_DISCREPANCY_NOTES_NAME = "fdnotes";
	public static final String RES_STATUS_ID = "resStatusId";
	public static final String SUBMITTED_USER_ACCOUNT_ID = "userAccountId";
	public static final String EMAIL_USER_ACCOUNT = "sendEmail";
	public static final String BOX_DN_MAP = "boxDNMap";
	public static final String BOX_TO_SHOW = "boxToShow";

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

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);
		FormProcessor fp = new FormProcessor(request);
		DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(getDataSource());

		HashMap errors = getErrorsHolder(request);
		int parentId = fp.getInt(PARENT_ID);
		DiscrepancyNoteBean parent = parentId > 0 ? (DiscrepancyNoteBean) dndao.findByPK(parentId)
				: new DiscrepancyNoteBean();
		HashMap<Integer, DiscrepancyNoteBean> boxDNMap = (HashMap<Integer, DiscrepancyNoteBean>) request.getSession()
				.getAttribute(BOX_DN_MAP);
		boxDNMap = boxDNMap == null ? new HashMap<Integer, DiscrepancyNoteBean>() : boxDNMap;
		DiscrepancyNoteBean dn = boxDNMap.size() > 0 && boxDNMap.containsKey(Integer.valueOf(parentId)) ? boxDNMap
				.get(Integer.valueOf(parentId)) : new DiscrepancyNoteBean();
		int entityId = fp.getInt(ENTITY_ID, true);
		entityId = entityId > 0 ? entityId : parent.getEntityId();
		if (entityId == 0) {
			Validator.addError(errors, "newChildAdded" + parentId, respage.getString("note_cannot_be_saved"));
			logger.info("entityId is 0. Note saving can not be started.");
		}
		String entityType = fp.getString(ENTITY_TYPE, true);

		FormDiscrepancyNotes noteTree = (FormDiscrepancyNotes) request.getSession().getAttribute(
				FORM_DISCREPANCY_NOTES_NAME);
		if (noteTree == null) {
			noteTree = new FormDiscrepancyNotes();
		}
		String ypos = fp.getString("ypos" + parentId);
		int refresh = 0;

		String field = fp.getString(ENTITY_FIELD);

		SessionManager sm = getSessionManager(request);
		Map<String, String> additionalParameters = CreateDiscrepancyNoteServlet.getMapWithParameters(field, request);
		boolean isInFVCError = !additionalParameters.isEmpty() && "1".equals(additionalParameters.get("isInFVCError"));
		boolean isRFC = !additionalParameters.isEmpty()
				&& CreateDiscrepancyNoteServlet.calculateIsRFC(additionalParameters, request, sm);

		String description;
		String detailedDes = fp.getString("detailedDes" + parentId);
		int resStatusId = fp.getInt(RES_STATUS_ID + parentId);
		int assignedUserAccountId = fp.getInt(SUBMITTED_USER_ACCOUNT_ID + parentId);
		String viewNoteLink = fp.getString("viewDNLink" + parentId);
		viewNoteLink = this.appendPageFileName(viewNoteLink, "fromBox", "1");
		Validator v = new Validator(new ValidatorHelper(request, getConfigurationDao()));
		int typeId = fp.getInt("typeId" + parentId);

		if (isRFC && typeId == DiscrepancyNoteType.ANNOTATION.getId()) {
			typeId = DiscrepancyNoteType.REASON_FOR_CHANGE.getId();
			dn.setDisType(DiscrepancyNoteType.REASON_FOR_CHANGE);
			description = fp.getString("description");
		} else {
			if (isInFVCError) {
				typeId = DiscrepancyNoteType.FAILEDVAL.getId();
				resStatusId = ResolutionStatus.OPEN.getId();
			}
			description = fp.getString("description" + parentId);
			v.addValidation("description" + parentId, Validator.NO_BLANKS);
			v.addValidation("description" + parentId, Validator.LENGTH_NUMERIC_COMPARISON,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
		}

		v.addValidation("detailedDes" + parentId, Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 1000);
		v.addValidation("typeId" + parentId, Validator.NO_BLANKS);
		errors = v.validate();

		dn.setParentDnId(parentId);
		dn.setDescription(description);
		dn.setDiscrepancyNoteTypeId(typeId);
		dn.setDetailedNotes(detailedDes);
		dn.setResolutionStatusId(resStatusId);
		if (typeId != DiscrepancyNoteType.ANNOTATION.getId() && typeId != DiscrepancyNoteType.REASON_FOR_CHANGE.getId()) {
			dn.setAssignedUserId(assignedUserAccountId);
		}

		if (DiscrepancyNoteType.ANNOTATION.getId() == dn.getDiscrepancyNoteTypeId()
				|| DiscrepancyNoteType.REASON_FOR_CHANGE.getId() == dn.getDiscrepancyNoteTypeId()) {
			dn.setResStatus(ResolutionStatus.NOT_APPLICABLE);
			dn.setResolutionStatusId(ResolutionStatus.NOT_APPLICABLE.getId());
		}
		if (DiscrepancyNoteType.FAILEDVAL.getId() == dn.getDiscrepancyNoteTypeId()
				|| DiscrepancyNoteType.QUERY.getId() == dn.getDiscrepancyNoteTypeId()) {
			if (ResolutionStatus.NOT_APPLICABLE.getId() == dn.getResolutionStatusId()) {
				Validator.addError(errors, RES_STATUS_ID + parentId, restext.getString("not_valid_res_status"));
			}
		}

		if (errors.isEmpty()) {
			HashMap<String, ArrayList<String>> results = new HashMap<String, ArrayList<String>>();
			ArrayList<String> mess = new ArrayList<String>();

			String column = fp.getString(ENTITY_COLUMN, true);

			dn.setOwner(ub);
			dn.setStudyId(currentStudy.getId());
			dn.setEntityId(entityId);
			dn.setEntityType(entityType);
			dn.setColumn(column);
			dn.setField(field);

			if (parentId > 0) {
				if (dn.getResolutionStatusId() != parent.getResolutionStatusId()) {
					parent.setResolutionStatusId(dn.getResolutionStatusId());
					dndao.update(parent);
					if (!parent.isActive()) {
						logger.info("Failed to update resolution status ID for the parent dn ID = " + parentId + ". ");
					}
				}
				if (dn.getAssignedUserId() != parent.getAssignedUserId()) {
					parent.setAssignedUserId(dn.getAssignedUserId());
					if (parent.getAssignedUserId() > 0) {
						dndao.updateAssignedUser(parent);
					} else {
						dndao.updateAssignedUserToNull(parent);
					}
					if (!parent.isActive()) {
						logger.info("Failed to update assigned user ID for the parent dn ID= " + parentId + ". ");
					}
				}
			} else {
				ypos = "0";
			}

			dn = (DiscrepancyNoteBean) dndao.create(dn);
			boolean success = dn.getId() > 0;
			if (success) {
				refresh = 1;
				dndao.createMapping(dn);
				success = dndao.isQuerySuccessful();
				if (!success) {
					mess.add(restext.getString("failed_create_dn_mapping_for_dnId") + dn.getId() + ". ");
				}
				noteTree.addNote(field, dn);
				noteTree.addIdNote(dn.getEntityId(), field);
				request.getSession().setAttribute(FORM_DISCREPANCY_NOTES_NAME, noteTree);
				if (dn.getParentDnId() == 0) {
					// see issue 2659 this is a new thread, we will create
					// two notes in this case,
					// This way one can be the parent that updates as the
					// status changes, but one also stays as New.
					dn.setParentDnId(dn.getId());
					dn = (DiscrepancyNoteBean) dndao.create(dn);
					if (dn.getId() > 0) {
						dndao.createMapping(dn);
						if (!dndao.isQuerySuccessful()) {
							mess.add(restext.getString("failed_create_dn_mapping_for_dnId") + dn.getId() + ". ");
						}
						noteTree.addNote(field, dn);
						noteTree.addIdNote(dn.getEntityId(), field);
						request.getSession().setAttribute(FORM_DISCREPANCY_NOTES_NAME, noteTree);
					} else {
						mess.add(restext.getString("failed_create_child_dn_for_new_parent_dnId") + dn.getId() + ". ");
					}
				}
			} else {
				mess.add(restext.getString("failed_create_new_dn") + ". ");
			}

			if (success) {
				if (boxDNMap.size() > 0 && boxDNMap.containsKey(parentId)) {
					boxDNMap.remove(parentId);
				}
				request.getSession().removeAttribute(BOX_TO_SHOW);
				/*
				 * Copied from CreateDiscrepancyNoteServlet Setting a marker to check later while saving administrative
				 * edited data. This is needed to make sure the system flags error while changing data for items which
				 * already has a DiscrepanyNote
				 */

				if (dn.getDiscrepancyNoteTypeId() == DiscrepancyNoteType.REASON_FOR_CHANGE.getId()
						|| ((dn.getDiscrepancyNoteTypeId() == DiscrepancyNoteType.QUERY.getId() || dn
								.getDiscrepancyNoteTypeId() == DiscrepancyNoteType.FAILEDVAL.getId()) && "Updated"
								.equals(dn.getResStatus().getName()))) {
					CreateDiscrepancyNoteServlet.turnOffIsDataChangedParamOfDN(field, request);
					CreateDiscrepancyNoteServlet.turnOffIsInRFCErrorParamOfDN(field, request);
					CreateDiscrepancyNoteServlet.turnOffIsInErrorParamOfDN(field, request);
					manageReasonForChangeState(request.getSession(), entityId);
				} else if (dn.getDiscrepancyNoteTypeId() == DiscrepancyNoteType.FAILEDVAL.getId()) {
					manageReasonForChangeState(request.getSession(), entityId);
				}

				String email = fp.getString(EMAIL_USER_ACCOUNT + parentId);
				if (dn.getAssignedUserId() > 0 && "1".equals(email.trim())) {
					dn = getNoteInfo(request, dn);
					sendDNEmail(entityType, ub.getName(), dn, request);
				}

				String close = fp.getString("close" + parentId);
				// session.setAttribute(CLOSE_WINDOW, "true".equals(close)?"true":"");
				if ("true".equals(close)) {
					sendUpdatedDN(currentStudy, entityId, dn, dndao, request);
					CreateDiscrepancyNoteServlet.saveNoteToSubmittedDNsMap(dn, request);
					request.setAttribute("responseMessage", "Save Done");
					request.setAttribute("refresh", true);

					forwardPage(Page.ADD_ONE_DISCREPANCY_NOTE_DIV, request, response);
					return;
				} else {
					if (parentId == dn.getParentDnId()) {
						mess.add(restext.getString("a_new_child_dn_added"));
						results.put("newChildAdded" + parentId, mess);
						setInputMessages(results, request);
					} else {
						addPageMessage(restext.getString("a_new_dn_thread_added"), request);
					}
				}

			} else {
				request.getSession().setAttribute(BOX_TO_SHOW, parentId + "");
			}
			CreateDiscrepancyNoteServlet.saveNoteToSubmittedDNsMap(dn, request);
		} else {
			setInputMessages(errors, request);
			request.setAttribute("errorsMap", errors);
			boxDNMap.put(parentId, dn);
			request.getSession().setAttribute(BOX_TO_SHOW, parentId + "");
		}
		request.getSession().setAttribute(BOX_DN_MAP, boxDNMap);
		viewNoteLink = this.appendPageFileName(viewNoteLink, "refresh", refresh + "");
		viewNoteLink = this.appendPageFileName(viewNoteLink, "y", ypos != null && ypos.length() > 0 ? ypos : "0");
		request.setAttribute(REFRESH_PARENT_WINDOW, true);
		dn.setItemId(fp.getInt(ITEM_ID));
		sendUpdatedDN(currentStudy, entityId, dn, dndao, request);

		forwardPage(Page.setNewPage(viewNoteLink, Page.VIEW_DISCREPANCY_NOTE.getTitle()), request, response);
	}

	private void sendUpdatedDN(StudyBean currentStudy, int entityId, DiscrepancyNoteBean dn, DiscrepancyNoteDAO dndao,
			HttpServletRequest request) {
		// we should send fake dn with correct resolution status to change flag color for the field
		ArrayList notes = (ArrayList) dndao.findAllByEntityAndColumnAndStudy(currentStudy, dn.getEntityType(),
				dn.getEntityId(), dn.getColumn());
		DiscrepancyNoteBean dnDuplicate = new DiscrepancyNoteBean(dn);
		dnDuplicate.setResolutionStatusId(getDiscrepancyNoteResolutionStatus(request, dndao, entityId, notes));
		request.setAttribute(UPDATED_DISCREPANCY_NOTE, dnDuplicate);
	}

	private void manageReasonForChangeState(HttpSession session, Integer itemDataBeanId) {
		HashMap<Integer, Boolean> noteSubmitted = (HashMap<Integer, Boolean>) session
				.getAttribute(DataEntryServlet.NOTE_SUBMITTED);
		if (noteSubmitted == null) {
			noteSubmitted = new HashMap<Integer, Boolean>();
		}
		noteSubmitted.put(itemDataBeanId, Boolean.TRUE);
		session.setAttribute(DataEntryServlet.NOTE_SUBMITTED, noteSubmitted);
	}

	private String appendPageFileName(String origin, String parameterName, String parameterValue) {
		String parameter = parameterName + "=" + parameterValue;
		String[] a = origin.split("\\?");
		if (a.length == 2) {
			if (("&" + a[1]).contains("&" + parameterName + "=")) {
				String result = a[0] + "?";
				String[] b = ("&" + a[1]).split("&" + parameterName + "=");
				if (b.length == 2) {
					result += b[0].substring(1) + "&" + parameter
							+ (b[1].contains("&") ? b[1].substring(b[1].indexOf("&")) : "");
					return result;
				} else if (b.length > 2) {
					result += b[0].substring(1) + "&" + parameter;
					for (int i = 2; i < b.length - 2; ++i) {
						result += b[i].substring(b[i].indexOf("&"));
					}
					int j = b.length - 1;
					result += b[j].contains("&") ? b[j].substring(b[j].indexOf("&")) : "";
					return result;
				}

			} else {
				return origin + "&" + parameter;
			}
		} else if (a.length == 1) {
			if (origin.endsWith("?")) {
				return origin + parameter;
			} else {
				return origin + "?" + parameter;
			}
		}
		logger.info("Original pageFileName: " + origin);
		return origin;
	}

	private void sendDNEmail(String entityType, String userName, DiscrepancyNoteBean dn, HttpServletRequest request)
			throws Exception {
		logger.info("++++++ found our way here");
		// generate email for user here
		StringBuilder message = new StringBuilder();

		UserAccountDAO userAccountDAO = getUserAccountDAO();
		ItemDAO itemDAO = getItemDAO();
		ItemDataDAO iddao = getItemDataDAO();
		ItemBean item = new ItemBean();

		StudyDAO studyDAO = getStudyDAO();
		UserAccountBean assignedUser = (UserAccountBean) userAccountDAO.findByPK(dn.getAssignedUserId());
		String alertEmail = assignedUser.getEmail();
		message.append(MessageFormat.format(respage.getString("mailDNHeader"), assignedUser.getFirstName(),
				assignedUser.getLastName()));
		message.append("<A HREF='").append(SQLInitServlet.getSystemURL())
				.append("ViewNotes?module=submit&listNotes_f_discrepancyNoteBean.user=").append(assignedUser.getName())
				.append("&listNotes_f_entityName=").append(dn.getEntityName()).append("'>")
				.append(SQLInitServlet.getField("sysURL")).append("</A><BR/>");
		message.append(respage.getString("you_received_this_from"));
		StudyBean study = (StudyBean) studyDAO.findByPK(dn.getStudyId());

		if ("itemData".equalsIgnoreCase(entityType)) {
			ItemDataBean itemData = (ItemDataBean) iddao.findByPK(dn.getEntityId());
			item = (ItemBean) itemDAO.findByPK(itemData.getItemId());
		}

		message.append(respage.getString("email_body_separator"));
		message.append(respage.getString("disc_note_info"));
		message.append(respage.getString("email_body_separator"));
		message.append(MessageFormat.format(respage.getString("mailDNParameters1"), dn.getDescription(),
				dn.getDetailedNotes(), userName));
		message.append(respage.getString("email_body_separator"));
		message.append(respage.getString("entity_information"));
		message.append(respage.getString("email_body_separator"));
		message.append(MessageFormat.format(respage.getString("mailDNParameters2"), study.getName(),
				dn.getSubjectName()));

		if (!("studySub".equalsIgnoreCase(entityType) || "subject".equalsIgnoreCase(entityType))) {
			message.append(MessageFormat.format(respage.getString("mailDNParameters3"), dn.getEventName()));
			if (!"studyEvent".equalsIgnoreCase(dn.getEntityType())) {
				message.append(MessageFormat.format(respage.getString("mailDNParameters4"), dn.getCrfName()));
				if (!"eventCrf".equalsIgnoreCase(dn.getEntityType())) {
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

		/*
		 * 
		 * Please select the link below to view the information provided. You may need to login to OpenClinica_testbed
		 * with your user name and password after selecting the link. If you receive a page cannot be displayed message,
		 * please make sure to select the Change Study/Site link in the upper right table of the page, select the study
		 * referenced above, and select the link again.
		 */

		String emailBodyString = message.toString();
		sendEmail(alertEmail.trim(), EmailEngine.getAdminEmail(),
				MessageFormat.format(respage.getString("mailDNSubject"), study.getName(), dn.getEntityName()),
				emailBodyString, true, null, null, true, request);
	}
}
