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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.DiscrepancyNoteType;
import org.akaza.openclinica.bean.core.ResolutionStatus;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.DisplayItemBeanWrapper;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ODMContainer;
import org.akaza.openclinica.bean.submit.crfdata.SubjectDataBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.admin.AuditDAO;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.logic.rulerunner.ExecutionMode;
import org.akaza.openclinica.logic.rulerunner.ImportDataRuleRunnerContainer;
import org.akaza.openclinica.service.rule.RuleSetService;
import org.akaza.openclinica.util.ImportSummaryInfo;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import com.clinovo.util.DAOWrapper;
import com.clinovo.util.SubjectEventStatusUtil;

/**
 * View the uploaded data and verify what is going to be saved into the system and what is not.
 * 
 * @author Krikor Krumlian
 */
@SuppressWarnings({"rawtypes"})
@Component
public class VerifyImportedCRFDataServlet extends Controller {

	private static final long serialVersionUID = 1L;
	public static final int INT_3600 = 3600;
	public static final int INT_10800 = 10800;
	public static final int INT_52 = 52;

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin()) {
			return;
		}

		Role r = currentRole.getRole();
		if (r.equals(Role.STUDY_DIRECTOR) || r.equals(Role.STUDY_ADMINISTRATOR) || r.equals(Role.INVESTIGATOR)
				|| r.equals(Role.CLINICAL_RESEARCH_COORDINATOR)) {
			return;
		}

		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("may_not_submit_data"), "1");
	}

	/**
	 * Creates a disc note.
	 * 
	 * @param itemBean
	 *            ItemBean
	 * @param message
	 *            String
	 * @param eventCrfBean
	 *            EventCRFBean
	 * @param displayItemBean
	 *            DisplayItemBean
	 * @param parentId
	 *            Integer
	 * @param uab
	 *            UserAccountBean
	 * @param ds
	 *            DataSource
	 * @param study
	 *            StudyBean
	 * @param con
	 *            Connection
	 * @return DiscrepancyNoteBean
	 */
	public static DiscrepancyNoteBean createDiscrepancyNote(ItemBean itemBean, String message,
			EventCRFBean eventCrfBean, DisplayItemBean displayItemBean, Integer parentId, UserAccountBean uab,
			DataSource ds, StudyBean study, Connection con) {
		DiscrepancyNoteBean note = new DiscrepancyNoteBean();
		StudySubjectDAO ssdao = new StudySubjectDAO(ds, con);
		note.setDescription(message);
		note.setDetailedNotes(restext.getString("failed_validation_check"));
		note.setOwner(uab);
		note.setCreatedDate(new Date());
		note.setResolutionStatusId(ResolutionStatus.OPEN.getId());
		note.setDiscrepancyNoteTypeId(DiscrepancyNoteType.FAILEDVAL.getId());
		if (parentId != null) {
			note.setParentDnId(parentId);
		}

		note.setField(itemBean.getName());
		note.setStudyId(study.getId());
		note.setEntityName(itemBean.getName());
		note.setEntityType("ItemData");
		note.setEntityValue(displayItemBean.getData().getValue());

		note.setEventName(eventCrfBean.getName());
		note.setEventStart(eventCrfBean.getCreatedDate());
		note.setCrfName(displayItemBean.getEventDefinitionCRF().getCrfName());

		StudySubjectBean ss = (StudySubjectBean) ssdao.findByPK(eventCrfBean.getStudySubjectId());
		note.setSubjectName(ss.getName());

		note.setEntityId(displayItemBean.getData().getId());
		note.setColumn("value");

		DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(ds, con);
		note = (DiscrepancyNoteBean) dndao.create(note, con);
		dndao.createMapping(note, con);

		return note;
	}

	private void deleteEventCRF(UserAccountBean ub, int eventCrfId) {
		ItemDataDAO iddao = getItemDataDAO();
		EventCRFDAO ecdao = getEventCRFDAO();
		DiscrepancyNoteDAO dnDao = getDiscrepancyNoteDAO();
		ArrayList itemData = iddao.findAllByEventCRFId(eventCrfId);
		for (Object anItemData : itemData) {
			ItemDataBean item = (ItemDataBean) anItemData;
			ArrayList discrepancyList = dnDao.findExistingNotesForItemData(item.getId());
			iddao.deleteDnMap(item.getId());
			for (Object aDiscrepancyList : discrepancyList) {
				DiscrepancyNoteBean noteBean = (DiscrepancyNoteBean) aDiscrepancyList;
				dnDao.deleteNotes(noteBean.getId());
			}
			item.setUpdater(ub);
			iddao.updateUser(item);
			iddao.delete(item.getId());
		}
		// delete event crf
		ecdao.deleteEventCRFDNMap(eventCrfId);
		ecdao.delete(eventCrfId);
	}

	private EventCRFBean createEventCRFBean(UserAccountBean ub, StudyBean studyBean, int studySubjectId,
			int crfVersionId, int studyEventId, StudyEventDAO studyEventDAO, EventCRFDAO eventCRFDAO) {
		EventCRFBean eventCRFBean = new EventCRFBean();
		eventCRFBean.setAnnotations("");
		eventCRFBean.setCreatedDate(new Date());
		eventCRFBean.setCRFVersionId(crfVersionId);

		StudyEventBean studyEvent = (StudyEventBean) studyEventDAO.findByPK(studyEventId);

		if (studyBean.getStudyParameterConfig().getInterviewerNameDefault().equals("blank")) {
			eventCRFBean.setInterviewerName("");
		} else {
			eventCRFBean.setInterviewerName(studyEvent.getOwner().getName());

		}
		if (!studyBean.getStudyParameterConfig().getInterviewDateDefault().equals("blank")) {
			eventCRFBean.setDateInterviewed(null);
		} else {
			eventCRFBean.setDateInterviewed(studyEvent.getDateStarted());
		}

		eventCRFBean.setOwner(ub);

		eventCRFBean.setNotStarted(true);
		eventCRFBean.setStatus(Status.AVAILABLE);
		eventCRFBean.setCompletionStatusId(1);
		eventCRFBean.setStudySubjectId(studySubjectId);
		eventCRFBean.setStudyEventId(studyEventId);
		eventCRFBean.setValidateString("");
		eventCRFBean.setValidatorAnnotations("");

		return (EventCRFBean) eventCRFDAO.create(eventCRFBean);
	}

	@Override
	@SuppressWarnings(value = {"unchecked", "deprecation"})
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection con = null;
		try {
			List<Map<String, Object>> auditItemList = new ArrayList<Map<String, Object>>();
			UserAccountBean ub = getUserAccountBean(request);
			StudyBean currentStudy = getCurrentStudy(request);

			ImportSummaryInfo summary = new ImportSummaryInfo();
			request.getSession().setMaxInactiveInterval(INT_10800);
			con = getDataSource().getConnection();
			con.setAutoCommit(false);
			System.out.println("JDBC open connection for transaction");
			StudyDAO sdao = new StudyDAO(getDataSource());
			EventCRFDAO ecdao = new EventCRFDAO(getDataSource(), con);
			StudySubjectDAO ssdao = new StudySubjectDAO(getDataSource());
			StudyEventDAO sedao = new StudyEventDAO(getDataSource(), con);
			ItemDataDAO itemDataDao = new ItemDataDAO(getDataSource(), con);
			DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(getDataSource(), con);
			EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(getDataSource());
			String action = request.getParameter("action");
			FormProcessor fp = new FormProcessor(request);

			// checks which module the requests are from
			String module = fp.getString(MODULE);
			request.setAttribute(MODULE, module);

			request.getSession().removeAttribute(STUDY_INFO_PANEL);
			StudyInfoPanel panel = getStudyInfoPanel(request);
			panel.reset();
			panel.setStudyInfoShown(false);
			panel.setOrderedData(true);

			setToPanel(resword.getString("create_CRF"), respage.getString("br_create_new_CRF_entering"), request);

			setToPanel(resword.getString("create_CRF_version"), respage.getString("br_create_new_CRF_uploading"),
					request);
			setToPanel(resword.getString("revise_CRF_version"), respage.getString("br_if_you_owner_CRF_version"),
					request);
			setToPanel(resword.getString("CRF_spreadsheet_template"),
					respage.getString("br_download_blank_CRF_spreadsheet_from"), request);
			setToPanel(resword.getString("example_CRF_br_spreadsheets"),
					respage.getString("br_download_example_CRF_instructions_from"), request);

			if ("confirm".equalsIgnoreCase(action)) {
				List<DisplayItemBeanWrapper> displayItemBeanWrappers = (List<DisplayItemBeanWrapper>) request
						.getSession().getAttribute("importedData");
				logger.debug("Size of displayItemBeanWrappers : " + displayItemBeanWrappers.size());
				forwardPage(Page.VERIFY_IMPORT_CRF_DATA, request, response);
			}

			if ("save".equalsIgnoreCase(action)) {
				// setup ruleSets to run if applicable
				logger.debug("=== about to generate rule containers ===");
				List<ImportDataRuleRunnerContainer> containers = this.ruleRunSetup(request, true, con, getDataSource(),
						currentStudy, ub);
				List<DisplayItemBeanWrapper> displayItemBeanWrappers = (List<DisplayItemBeanWrapper>) request
						.getSession().getAttribute("importedData");
				Set<Integer> studyEventIds = new HashSet<Integer>();
				Set<Integer> skippedItemIds = new HashSet<Integer>();
				for (DisplayItemBeanWrapper wrapper : displayItemBeanWrappers) {
					HashMap<Integer, EventCRFBean> idToEventCrfBeans = new HashMap<Integer, EventCRFBean>();
					logger.debug("=== right before we check to make sure it is savable: " + wrapper.isSavable());
					if (wrapper.isSavable()) {
						logger.debug("wrapper problems found : " + wrapper.getValidationErrors().toString());
						for (DisplayItemBean displayItemBean : wrapper.getDisplayItemBeans()) {
							EventCRFBean eventCrfBean;
							ItemDataBean itemDataBean;
							int eventCrfBeanId = displayItemBean.getData().getEventCRFId();
							if (idToEventCrfBeans.containsKey(eventCrfBeanId)) {
								eventCrfBean = idToEventCrfBeans.get(eventCrfBeanId);
							} else {
								eventCrfBean = (EventCRFBean) ecdao.findByPK(eventCrfBeanId);
								if (!displayItemBean.isSkip()) {
									idToEventCrfBeans.put(eventCrfBeanId, eventCrfBean);
								}
							}
							if (!displayItemBean.isSkip()
									&& displayItemBean.getMetadata().getCrfVersionId() != eventCrfBean
											.getCRFVersionId()) {
								deleteEventCRF(ub, eventCrfBean.getId());
								eventCrfBean = createEventCRFBean(ub, currentStudy, eventCrfBean.getStudySubjectId(),
										displayItemBean.getMetadata().getCrfVersionId(),
										eventCrfBean.getStudyEventId(), sedao, ecdao);
								idToEventCrfBeans.put(eventCrfBean.getId(), eventCrfBean);
								for (DisplayItemBean dib : wrapper.getDisplayItemBeans()) {
									dib.getData().setEventCRFId(eventCrfBean.getId());
								}
							}
							logger.debug("found value here: " + displayItemBean.getData().getValue());
							logger.debug("found status here: " + eventCrfBean.getStatus().getName());
							StudyEventBean studyEventBean = (StudyEventBean) sedao.findByPK(eventCrfBean
									.getStudyEventId());
							itemDataBean = itemDataDao.findByItemIdAndEventCRFIdAndOrdinal(displayItemBean.getItem()
									.getId(), eventCrfBean.getId(), displayItemBean.getData().getOrdinal());
							summary.processStudySubject(eventCrfBean.getStudySubjectId(), displayItemBean.isSkip());
							summary.processStudyEvent(studyEventBean.getId() + "_" + studyEventBean.getRepeatingNum(),
									displayItemBean.isSkip());
							summary.processItem(studyEventBean.getId() + "_" + studyEventBean.getRepeatingNum() + "_"
									+ displayItemBean.getItem().getId() + "_" + displayItemBean.getData().getOrdinal(),
									displayItemBean.isSkip());
							if (!displayItemBean.isSkip()) {
								if (wrapper.isOverwrite() && itemDataBean.getStatus() != null) {
									logger.debug("just tried to find item data bean on item name "
											+ displayItemBean.getItem().getName());
									itemDataBean.setUpdatedDate(new Date());
									itemDataBean.setUpdater(ub);
									itemDataBean.setValue(displayItemBean.getData().getValue());
									// set status?
									itemDataDao.update(itemDataBean, con);
									logger.debug("updated: " + itemDataBean.getItemId());
									// need to set pk here in order to create dn
									displayItemBean.getData().setId(itemDataBean.getId());
								} else {
									itemDataBean = (ItemDataBean) itemDataDao.create(displayItemBean.getData(), con);
									logger.debug("created: " + displayItemBean.getData().getItemId()
											+ "event CRF ID = " + eventCrfBean.getId() + "CRF VERSION ID ="
											+ eventCrfBean.getCRFVersionId());
									displayItemBean.getData().setId(itemDataBean.getId());
								}
								ItemDAO idao = getItemDAO();
								ItemBean ibean = (ItemBean) idao.findByPK(displayItemBean.getData().getItemId());
								String itemOid = displayItemBean.getItem().getOid() + "_"
										+ wrapper.getStudyEventRepeatKey() + "_"
										+ displayItemBean.getData().getOrdinal() + "_" + wrapper.getStudySubjectOid();
								if (wrapper.getValidationErrors().containsKey(itemOid)) {
									ArrayList messageList = (ArrayList) wrapper.getValidationErrors().get(itemOid);
									// could be more then one will have to iterate
									for (Object aMessageList : messageList) {
										String message = (String) aMessageList;
										DiscrepancyNoteBean parentDn = createDiscrepancyNote(ibean, message,
												eventCrfBean, displayItemBean, null, ub, getDataSource(), currentStudy,
												con);
										createDiscrepancyNote(ibean, message, eventCrfBean, displayItemBean,
												parentDn.getId(), ub, getDataSource(), currentStudy, con);
									}
								}
							} else {
								skippedItemIds.add(displayItemBean.getItem().getId());
								Map<String, Object> auditItemMap = new HashMap<String, Object>();
								auditItemMap.put("audit_log_event_type_id", INT_52);
								auditItemMap.put("user_id", ub.getId());
								auditItemMap.put("audit_table", "item_data");
								auditItemMap.put("entity_id", itemDataBean.getId());
								auditItemMap.put("entity_name", displayItemBean.getItem().getName());
								auditItemMap.put("old_value", itemDataBean.getValue());
								auditItemMap.put("new_value", displayItemBean.getData().getValue());
								auditItemMap.put("event_crf_id", displayItemBean.getData().getEventCRFId());
								auditItemList.add(auditItemMap);
							}
						}

						for (EventCRFBean eventCrfBean : idToEventCrfBeans.values()) {
							studyEventIds.add(eventCrfBean.getStudyEventId());

							eventCrfBean.setSdvStatus(false);
							eventCrfBean.setNotStarted(false);
							eventCrfBean.setStatus(Status.AVAILABLE);
							if (currentStudy.getStudyParameterConfig().getMarkImportedCRFAsCompleted()
									.equalsIgnoreCase("yes")) {
								EventDefinitionCRFBean edcb = edcdao.findByStudyEventIdAndCRFVersionId(currentStudy,
										eventCrfBean.getStudyEventId(), eventCrfBean.getCRFVersionId());

								eventCrfBean.setUpdaterId(ub.getId());
								eventCrfBean.setUpdater(ub);
								eventCrfBean.setUpdatedDate(new Date());
								eventCrfBean.setDateCompleted(new Date());
								eventCrfBean.setDateValidateCompleted(new Date());
								eventCrfBean.setStatus(Status.UNAVAILABLE);
								eventCrfBean.setStage(edcb.isDoubleEntry()
										? DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE
										: DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE);
								itemDataDao.updateStatusByEventCRF(eventCrfBean, Status.UNAVAILABLE, con);
							}

							ecdao.update(eventCrfBean, con);
							getItemSDVService().sdvCrfItems(eventCrfBean.getId(), ub.getId(), false, con);
						}

						for (int studyEventId : studyEventIds) {
							if (studyEventId > 0) {
								StudyEventBean seb = (StudyEventBean) sedao.findByPK(studyEventId);

								seb.setUpdatedDate(new Date());
								seb.setUpdater(ub);

								sedao.update(seb, con);
							}
						}
					}
				}

				con.commit();
				con.setAutoCommit(true);

				for (int studyEventId : studyEventIds) {
					if (studyEventId > 0) {
						StudyEventBean seb = (StudyEventBean) sedao.findByPK(studyEventId);

						SubjectEventStatusUtil.determineSubjectEventState(seb, new DAOWrapper(sdao, getCRFVersionDAO(),
								sedao, ssdao, ecdao, edcdao, dndao));

						sedao.update(seb, con);
					}
				}

				addPageMessage(respage.getString("data_has_been_successfully_import"), request);
				System.out.println("Data is committed");

				addPageMessage(summary.prepareSummaryMessage(currentStudy, resword), request);

				con.close();
				if (auditItemList.size() > 0) {
					new AuditDAO(getDataSource()).saveItems(auditItemList);
				}
				try {
					con = getDataSource().getConnection();
					con.setAutoCommit(false);
					logger.debug("=== about to run rules ===");
					addPageMessage(this.ruleActionWarnings(this.runRules(true, con, skippedItemIds, currentStudy, ub,
							containers, ExecutionMode.SAVE)), request);
					con.commit();
					con.close();
				} catch (SQLException sqle) {
					con.rollback();
					con.close();
				}
				request.getSession().setMaxInactiveInterval(INT_3600);
				forwardPage(Page.LIST_STUDY_SUBJECTS_SERVLET, request, response);
			}

		} catch (SQLException sqle) {
			if (con != null) {
				con.rollback();
				con.close();
			}
		}
	}

	private List<ImportDataRuleRunnerContainer> ruleRunSetup(HttpServletRequest request, Boolean runRulesOptimisation,
			Connection connection, DataSource dataSource, StudyBean studyBean, UserAccountBean userBean) {
		RuleSetService ruleSetService = getRuleSetService();
		List<ImportDataRuleRunnerContainer> containers = new ArrayList<ImportDataRuleRunnerContainer>();
		ODMContainer odmContainer = (ODMContainer) request.getSession().getAttribute("odmContainer");
		logger.debug("=== about to check if odm container is null ===");
		if (odmContainer != null) {
			ArrayList<SubjectDataBean> subjectDataBeans = odmContainer.getCrfDataPostImportContainer().getSubjectData();
			logger.debug("=== found number of rules present: " + ruleSetService.getCountByStudy(studyBean) + " ===");
			if (ruleSetService.getCountByStudy(studyBean) > 0) {
				ImportDataRuleRunnerContainer container;
				for (SubjectDataBean subjectDataBean : subjectDataBeans) {
					container = new ImportDataRuleRunnerContainer();
					container.initRuleSetsAndTargets(dataSource, studyBean, subjectDataBean, ruleSetService);
					logger.debug("=== found container: should run rules? " + container.getShouldRunRules() + " ===");
					if (container.getShouldRunRules()) {
						logger.debug("=== added a container in run rule setup ===");
						containers.add(container);
					}
				}
				if (!containers.isEmpty()) {
					logger.debug("=== running rules dry run ===");
					ruleSetService.runRulesInImportData(runRulesOptimisation, connection, containers, studyBean,
							userBean, ExecutionMode.DRY_RUN);
				}
			}
		}
		return containers;
	}

	private List<String> runRules(Boolean runRulesOptimisation, Connection connection, Set<Integer> skippedItemIds,
			StudyBean studyBean, UserAccountBean userBean, List<ImportDataRuleRunnerContainer> containers,
			ExecutionMode executionMode) {

		List<String> messages = new ArrayList<String>();
		if (containers != null && !containers.isEmpty()) {
			HashMap<String, ArrayList<String>> summary = getRuleSetService().runRulesInImportData(runRulesOptimisation,
					connection, containers, skippedItemIds, studyBean, userBean, executionMode);
			logger.debug("=== found summary " + summary.toString());
			messages = extractRuleActionWarnings(summary);
		}
		return messages;
	}

	private List<String> extractRuleActionWarnings(HashMap<String, ArrayList<String>> summaryMap) {
		List<String> messages = new ArrayList<String>();
		if (summaryMap != null && !summaryMap.isEmpty()) {
			for (String key : summaryMap.keySet()) {
				StringBuilder mesg = new StringBuilder(key + " : ");
				for (String s : summaryMap.get(key)) {
					mesg.append(s).append(", ");
				}
				messages.add(mesg.toString());
			}
		}
		return messages;
	}

	private String ruleActionWarnings(List<String> warnings) {
		if (warnings.isEmpty()) {
			return "";
		} else {
			StringBuilder mesg = new StringBuilder(respage.getString("rule_action_warnings"));
			for (String s : warnings) {
				mesg.append(s).append("; ");
			}
			return mesg.toString();
		}
	}
}
