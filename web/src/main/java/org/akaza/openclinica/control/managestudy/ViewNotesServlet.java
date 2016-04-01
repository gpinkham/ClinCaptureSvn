/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2014 Clinovo Inc.
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
 *
 * Created on Sep 23, 2005
 */
package org.akaza.openclinica.control.managestudy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.core.DiscrepancyNoteType;
import org.akaza.openclinica.bean.core.ResolutionStatus;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteStatisticBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.RememberLastPage;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.submit.ListNotesTableFactory;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.core.CoreResources;
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
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.service.DiscrepancyNoteUtil;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.jmesa.facade.TableFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailSendException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.clinovo.util.DcfEmailer;
import com.clinovo.util.DcfRenderType;

/**
 * View a list of all discrepancy notes in current study.
 * 
 */
@Component
@SuppressWarnings("unused")
public class ViewNotesServlet extends RememberLastPage {

	private static final long serialVersionUID = 1L;

	private final Logger logger = LoggerFactory.getLogger(getClass().getName());

	public static final String PRINT = "print";
	public static final String GENERATE_DCF = "generateDcf";
	public static final String PRINT_DCF = "printDcf";
	public static final String SAVE_DCF = "saveDcf";
	public static final String DCF_SAVED = "dcfSaved";
	public static final String DCF_FILE_NAME_ATTRIBUTE = "dcf_file_name";
	public static final String DCF_RENDER_CHECKBOX_NAME = "dcfRenderType";
	public static final String DCF_ICON_NAME = "dcfIconId";
	public static final String RECIPIENT_EMAIL = "email";
	public static final String RESOLUTION_STATUS = "resolutionStatus";
	public static final String TYPE = "discNoteType";
	public static final String WIN_LOCATION = "window_location";
	public static final String NOTES_TABLE = "notesTable";
	public static final String DISCREPANCY_NOTE_TYPE = "discrepancyNoteType";
	public static final String DISCREPANCY_NOTE_TYPE_PARAM = "listNotes_f_discrepancyNoteBean.disType";
	public static final String DISCREPANCY_NOTE_STATUS_PARAM = "listNotes_f_discrepancyNoteBean.resolutionStatus";

	public static final int ALL = -1;
	public static final int DN_STATUS_NEW = 1;
	public static final int DN_STATUS_NOT_APPLICABLE = 5;

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		FormProcessor fp = new FormProcessor(request);
		String print = fp.getString(PRINT);
		String generateDcf = fp.getString(GENERATE_DCF);
		String printDcf = fp.getString(PRINT_DCF);
		String saveDcf = fp.getString(SAVE_DCF);
		if (!shouldProceedAfterProcessingDcfRequest(request, response, fp, generateDcf, printDcf, saveDcf)) {
			return;
		}
		if (!print.equalsIgnoreCase("yes") && shouldRedirect(request, response)) {
			return;
		}
		UserAccountBean ub = getUserAccountBean(request);
		removeLockedCRF(ub.getId());
		StudyBean currentStudy = getCurrentStudy(request);
		String module = request.getParameter("module");
		String moduleStr = "manage";
		if (module != null && module.trim().length() > 0) {
			if ("submit".equals(module)) {
				request.setAttribute("module", "submit");
				moduleStr = "submit";
			} else if ("admin".equals(module)) {
				request.setAttribute("module", "admin");
				moduleStr = "admin";
			} else {
				request.setAttribute("module", "manage");
			}
		}
		boolean showMoreLink = fp.getString("showMoreLink").equals("")
				|| Boolean.parseBoolean(fp.getString("showMoreLink"));
		boolean allowDcf = allowDcfForUserInCurrentStudy(currentStudy, ub);
		request.setAttribute("allowDcf", allowDcf);
		if (allowDcf) {
			request.setAttribute("system_lang", CoreResources.getSystemLocale().toString());
		}
		int oneSubjectId = fp.getInt("id");
		request.getSession().setAttribute("subjectId", oneSubjectId);
		int discNoteTypeId;
		try {
			DiscrepancyNoteType discNoteType = DiscrepancyNoteType
					.getByName(request.getParameter(DISCREPANCY_NOTE_TYPE_PARAM));
			discNoteTypeId = discNoteType.getId();
		} catch (Exception e) {
			e.printStackTrace();
			discNoteTypeId = ALL;
		}
		request.setAttribute(DISCREPANCY_NOTE_TYPE, discNoteTypeId);
		boolean removeSession = fp.getBoolean("removeSession");
		request.getSession().setAttribute("module", module);
		String viewForOne = fp.getString("viewForOne");
		DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(getDataSource());
		dndao.setFetchMapping(true);
		int resolutionStatusId;
		try {
			ResolutionStatus resolutionStatus = ResolutionStatus
					.getByName(request.getParameter(DISCREPANCY_NOTE_STATUS_PARAM));
			resolutionStatusId = resolutionStatus.getId();
		} catch (Exception e) {
			e.printStackTrace();
			resolutionStatusId = ALL;
		}
		if (removeSession) {
			request.getSession().removeAttribute(WIN_LOCATION);
			request.getSession().removeAttribute(NOTES_TABLE);
		}
		request.getSession().setAttribute(WIN_LOCATION, "ViewNotes?viewForOne=" + viewForOne + "&id=" + oneSubjectId
				+ "&module=" + module + " &removeSession=1");
		boolean hasAResolutionStatus = resolutionStatusId >= DN_STATUS_NEW
				&& resolutionStatusId <= DN_STATUS_NOT_APPLICABLE;
		Set<Integer> resolutionStatusIds = (HashSet) request.getSession().getAttribute(RESOLUTION_STATUS);
		if (!hasAResolutionStatus && resolutionStatusIds != null) {
			request.getSession().removeAttribute(RESOLUTION_STATUS);
			resolutionStatusIds = null;
		}
		if (hasAResolutionStatus) {
			if (resolutionStatusIds == null) {
				resolutionStatusIds = new HashSet<Integer>();
			}
			resolutionStatusIds.add(resolutionStatusId);
			request.getSession().setAttribute(RESOLUTION_STATUS, resolutionStatusIds);
		}
		StudySubjectDAO subdao = getStudySubjectDAO();
		StudyDAO studyDao = getStudyDAO();
		SubjectDAO sdao = getSubjectDAO();
		UserAccountDAO uadao = getUserAccountDAO();
		CRFVersionDAO crfVersionDao = getCRFVersionDAO();
		CRFDAO crfDao = getCRFDAO();
		StudyEventDAO studyEventDao = getStudyEventDAO();
		StudyEventDefinitionDAO studyEventDefinitionDao = getStudyEventDefinitionDAO();
		EventDefinitionCRFDAO eventDefinitionCRFDao = getEventDefinitionCRFDAO();
		ItemDataDAO itemDataDao = getItemDataDAO();
		ItemDAO itemDao = getItemDAO();
		EventCRFDAO eventCRFDao = getEventCRFDAO();
		ListNotesTableFactory factory = new ListNotesTableFactory(showMoreLink);
		factory.setSubjectDao(sdao);
		factory.setStudySubjectDao(subdao);
		factory.setUserAccountDao(uadao);
		factory.setStudyDao(studyDao);
		factory.setCurrentStudy(currentStudy);
		factory.setDiscrepancyNoteDao(dndao);
		factory.setCrfDao(crfDao);
		factory.setCrfVersionDao(crfVersionDao);
		factory.setStudyEventDao(studyEventDao);
		factory.setStudyEventDefinitionDao(studyEventDefinitionDao);
		factory.setItemGroupMetadataDAO(getItemGroupMetadataDAO());
		factory.setEventDefinitionCRFDao(eventDefinitionCRFDao);
		factory.setItemDao(itemDao);
		factory.setItemDataDao(itemDataDao);
		factory.setEventCRFDao(eventCRFDao);
		factory.setModule(moduleStr);
		factory.setDiscNoteType(discNoteTypeId);
		factory.setResolutionStatus(resolutionStatusId);
		factory.setEnableDcf(allowDcf);
		factory.setDataSource(getDataSource());
		factory.setCurrentUser(ub);
		factory.setUserRole(getCurrentRole(request));
		TableFacade tf = factory.createTable(request, response);
		if ("yes".equalsIgnoreCase(print)) {
			request.setAttribute("allNotes", factory.getNotesForPrintPop(tf.getLimit(), ub));
			forwardPage(Page.VIEW_DISCREPANCY_NOTES_IN_STUDY_PRINT, request, response);
			return;
		}
		String viewNotesHtml = tf.render();
		request.setAttribute("viewNotesHtml", viewNotesHtml);
		String viewNotesURL = this.getPageURL(request);
		request.getSession().setAttribute("viewNotesURL", viewNotesURL);
		String viewNotesPageFileName = this.getPageServletFileName(request);
		request.getSession().setAttribute("viewNotesPageFileName", viewNotesPageFileName);
		List<DiscrepancyNoteStatisticBean> statisticBeans;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserAccountBean loggedInUser = (UserAccountBean) uadao.findByUserName(authentication.getName());
		if (isCoder(loggedInUser, request) || isEvaluator(loggedInUser, request)) {
			statisticBeans = dndao.countUserNotesStatistics(currentStudy, ub);
		} else {
			statisticBeans = dndao.countNotesStatisticWithMasks(currentStudy, ub);
		}
		Map<String, String> customTotalMap = ListNotesTableFactory.getNotesTypesStatistics(statisticBeans);
		Map<String, Map<String, String>> customStat = ListNotesTableFactory.getNotesStatistics(statisticBeans);
		request.setAttribute("summaryMap", customStat);
		request.setAttribute("typeKeys", customTotalMap);
		request.setAttribute("mapKeys", ResolutionStatus.getMembersForDisplayStatistics());
		request.setAttribute("typeNames", DiscrepancyNoteUtil.getTypeNames(getResTerm()));
		request.setAttribute("grandTotal", customTotalMap.get("Total"));
		if (request.getSession().getAttribute(PRINT_DCF) != null) {
			request.setAttribute(PRINT_DCF, request.getSession().getAttribute(PRINT_DCF));
			request.getSession().removeAttribute(PRINT_DCF);
		}
		if (request.getSession().getAttribute(SAVE_DCF) != null) {
			request.setAttribute(SAVE_DCF, request.getSession().getAttribute(SAVE_DCF));
			request.getSession().removeAttribute(SAVE_DCF);
		}
		forwardPage(Page.VIEW_DISCREPANCY_NOTES_IN_STUDY, request, response);
	}

	@SuppressWarnings("unchecked")
	private boolean shouldProceedAfterProcessingDcfRequest(HttpServletRequest request, HttpServletResponse response,
			FormProcessor fp, String generateDcf, String printDcf, String saveDcf) {
		boolean shouldPrintDcf = printDcf != null && printDcf.equalsIgnoreCase("yes");
		boolean shouldSaveDcf = saveDcf != null && saveDcf.equalsIgnoreCase("yes")
				&& request.getSession().getAttribute(DCF_SAVED) == null;
		if (generateDcf != null && generateDcf.equalsIgnoreCase("yes")) {
			List<String> selectedNoteAndEntityIds = getSelectedNoteAndEntityIds(fp);
			List<String> selectedRenderTypes = fp.getStringArray(DCF_RENDER_CHECKBOX_NAME);
			String recipientEmail = fp.getString(RECIPIENT_EMAIL);
			if (selectedNoteAndEntityIds.size() > 0) {
				generateDcfs(selectedNoteAndEntityIds, selectedRenderTypes, recipientEmail, request, response);
			}
		}
		if (shouldPrintDcf || shouldSaveDcf) {
			String dcfFileName = request.getSession().getAttribute(DCF_FILE_NAME_ATTRIBUTE).toString();
			String contentDisposition = shouldPrintDcf ? "inline" : "attachment";
			writeDcfToResponseStream(response, dcfFileName, contentDisposition);
			if (shouldPrintDcf) {
				request.removeAttribute(PRINT_DCF);
			}
			if (shouldSaveDcf) {
				request.removeAttribute(SAVE_DCF);
				request.getSession().setAttribute(DCF_SAVED, "yes");
			}
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	private List<String> getSelectedNoteAndEntityIds(FormProcessor fp) {
		List<String> selectedNoteAndEntityIds = new ArrayList<String>();
		String noteAndEntityIdFromIconSelect = fp.getString(DCF_ICON_NAME).trim();
		if (noteAndEntityIdFromIconSelect.length() > 0) {
			selectedNoteAndEntityIds.add(noteAndEntityIdFromIconSelect);
			return selectedNoteAndEntityIds;
		}
		return fp.getStringArray(ListNotesTableFactory.DCF_CHECKBOX_NAME);
	}

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (mayViewData(ub, currentRole)) {
			return;
		}

		addPageMessage(getResPage().getString("no_permission_to_view_discrepancies")
				+ getResPage().getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET,
				getResException().getString("not_study_director_or_study_cordinator"), "1");
	}

	@Override
	protected String getDefaultUrl(HttpServletRequest request) {
		FormProcessor fp = new FormProcessor(request);
		return "?module=" + fp.getString("module")
				+ "&maxRows=15&showMoreLink=true&listNotes_tr_=true&listNotes_p_=1&listNotes_mr_=15";
	}

	@Override
	protected boolean userDoesNotUseJmesaTableForNavigation(HttpServletRequest request) {
		return request.getQueryString() == null || !request.getQueryString().contains("&listNotes_")
				|| request.getQueryString().contains("&print=yes");
	}

	private void generateDcfs(List<String> selectedNoteIds, List<String> selectedRenderTypes, String recipientEmail,
			HttpServletRequest request, HttpServletResponse response) {
		StudyBean currentStudy = getCurrentStudy(request);
		UserAccountBean currentUser = getUserAccountBean(request);
		request.getSession().removeAttribute(PRINT_DCF);
		request.getSession().removeAttribute(SAVE_DCF);
		request.getSession().removeAttribute(DCF_SAVED);
		try {
			Map<Integer, Map<Integer, String>> noteAndEntityIds = transformSelectedNoteAndEntityIdsToInt(
					selectedNoteIds);
			String dcfFile = getDcfService().generateDcf(currentStudy, noteAndEntityIds.keySet(), currentUser,
					getLocale());
			boolean multipleDcfs = noteAndEntityIds.keySet().size() > 1;
			setRenderTypes(selectedRenderTypes, dcfFile, recipientEmail, multipleDcfs, request, currentStudy,
					currentUser);
			boolean renderSuccessful = getDcfService().renderDcf();
			if (renderSuccessful) {
				getDcfService().updateDiscrepancyNotes(noteAndEntityIds, currentStudy, currentUser);
				String successMessage;
				if (selectedRenderTypes.contains("email")) {
					successMessage = MessageFormat.format(getResWord().getString("dcf_generated_successfully_emailed"),
							recipientEmail);
				} else {
					successMessage = getResWord().getString("dcf_generated_successfully");
				}
				addPageMessage(successMessage, request);
			} else {
				addPageMessage(getResWord().getString("dcf_generation_failed"), request);
			}
		} catch (FileNotFoundException e) {
			logger.debug("DCF generation failed on {} due to: {}", new Date(), e.getMessage());
			e.printStackTrace();
			addPageMessage(
					MessageFormat.format(getResWord().getString("dcf_generation_failed_details"), e.getMessage()),
					request);
		} catch (MailSendException e) {
			logger.debug("DCF generation failed on {} due to: {}", new Date(), e.toString());
			e.printStackTrace();
			addPageMessage(getResWord().getString("dcf_generation_email_failed"), request);
		} catch (Exception e) {
			logger.debug("DCF generation failed on {} due to: {}", new Date(), e.toString());
			e.printStackTrace();
			addPageMessage(getResWord().getString("dcf_generation_failed"), request);
		}
	}

	private Map<Integer, Map<Integer, String>> transformSelectedNoteAndEntityIdsToInt(List<String> selectedNoteIds) {
		final int noteIdEntityIdAndColumnExpectedLength = 3;
		Map<Integer, Map<Integer, String>> noteAndEntityIds = new HashMap<Integer, Map<Integer, String>>();
		for (String noteAndEntityId : selectedNoteIds) {
			String[] parts = noteAndEntityId.split("___");
			if (parts.length == noteIdEntityIdAndColumnExpectedLength) {
				int noteId = Integer.parseInt(parts[0]);
				noteAndEntityIds.put(noteId, new HashMap<Integer, String>());
				noteAndEntityIds.get(noteId).put(Integer.valueOf(parts[1]), parts[2]);
			}
		}
		return noteAndEntityIds;
	}

	private void writeDcfToResponseStream(HttpServletResponse response, String dcfFile, String contentDisposition) {
		try {
			if (dcfFile != null) {
				File pdfFile = new File(dcfFile);
				response.setContentType("application/pdf");
				response.addHeader("Content-Disposition", contentDisposition + "; filename="
						+ dcfFile.substring(dcfFile.lastIndexOf(File.separator) + 1));
				response.setContentLength((int) pdfFile.length());
				FileInputStream fileInputStream = new FileInputStream(pdfFile);
				OutputStream responseOutputStream = response.getOutputStream();
				int bytes;
				while ((bytes = fileInputStream.read()) != -1) {
					responseOutputStream.write(bytes);
				}
				responseOutputStream.flush();
				fileInputStream.close();
			}
		} catch (IOException e) {
			logger.error("An error occurred while writing DCF to response stream. Details: " + e.getMessage());
		}
	}

	private void setRenderTypes(List<String> selectedRenderTypes, String dcfFile, String recipientEmail,
			boolean multipleDcfs, HttpServletRequest request, StudyBean currentStudy, UserAccountBean currentUser) {
		getDcfService().clearRenderTypes();
		if (selectedRenderTypes.contains("email")) {
			getDcfService().addDcfRenderType(
					getEmailRenderType(dcfFile, recipientEmail, multipleDcfs, currentStudy, currentUser));
		}
		if (selectedRenderTypes.contains("print")) {
			getDcfService().addDcfRenderType(getPrintOrSaveRenderType(PRINT_DCF, dcfFile, "dcf_printed", request));
		}
		if (selectedRenderTypes.contains("save")) {
			getDcfService().addDcfRenderType(getPrintOrSaveRenderType(SAVE_DCF, dcfFile, "dcf_saved", request));
		}
	}

	private DcfRenderType getEmailRenderType(String dcfFile, String recipientEmail, boolean multipleDcfs,
			StudyBean currentStudy, UserAccountBean currentUser) {
		String dcfName = dcfFile.substring(dcfFile.lastIndexOf(File.separator) + 1).replace(".pdf", "");
		String dcfEmailSubject = multipleDcfs
				? getResWord().getString("dcf_email_subject_multiple")
				: getResWord().getString("dcf").concat(": ").concat(dcfName);
		DcfRenderType emailer = new DcfEmailer.DcfEmailerBuilder().addDcfFilePath(dcfFile).addDcfName(dcfName)
				.addEmailSubject(dcfEmailSubject).addMailSender(getMailSender()).addRecipientEmail(recipientEmail)
				.setMultipleDcfs(multipleDcfs).setCurrentStudy(currentStudy).setCurrentUser(currentUser).build();
		return emailer;
	}

	private DcfRenderType getPrintOrSaveRenderType(final String printOrSave, final String dcfFile,
			final String actionKey, final HttpServletRequest request) {
		return new DcfRenderType() {
			public boolean render() {
				request.getSession().setAttribute(DCF_FILE_NAME_ATTRIBUTE, dcfFile);
				request.getSession().setAttribute(printOrSave, "yes");
				return true;
			}

			public String getResourceBundleKeyForAction() {
				return actionKey;
			}
		};
	}

	private boolean allowDcfForUserInCurrentStudy(StudyBean currentStudy, UserAccountBean ub) {
		StudyUserRoleBean surb = StudyUserRoleBean.getStudyUserRoleInCurrentStudy(ub, currentStudy);
		if (!surb.isCanGenerateDCF()) {
			return false;
		}
		return currentStudy.getStudyParameterConfig().getAllowDiscrepancyCorrectionForms().equalsIgnoreCase("yes");
	}
}
