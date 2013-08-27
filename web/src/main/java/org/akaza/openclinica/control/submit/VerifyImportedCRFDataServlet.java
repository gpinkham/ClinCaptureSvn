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
import java.util.List;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.DiscrepancyNoteType;
import org.akaza.openclinica.bean.core.ResolutionStatus;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
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
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.core.SecureController;
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
import org.akaza.openclinica.service.rule.RuleSetServiceInterface;
import org.akaza.openclinica.util.DAOWrapper;
import org.akaza.openclinica.util.SubjectEventStatusUtil;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

/**
 * View the uploaded data and verify what is going to be saved into the system and what is not.
 * 
 * @author Krikor Krumlian
 */
@SuppressWarnings({ "rawtypes" })
public class VerifyImportedCRFDataServlet extends SecureController {
	
	private Connection con;
	private Boolean runRulesOptimisation = true;
	
	private static final long serialVersionUID = 1L;
	
	@Override
	public void mayProceed() throws InsufficientPermissionException {

		if (ub.isSysAdmin()) {
			return;
		}

		Role r = currentRole.getRole();
		if (r.equals(Role.STUDY_DIRECTOR) || r.equals(Role.STUDY_ADMINISTRATOR) || r.equals(Role.INVESTIGATOR)
				|| r.equals(Role.CLINICAL_RESEARCH_COORDINATOR)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("may_not_submit_data"), "1");
	}

	public static DiscrepancyNoteBean createDiscrepancyNote(ItemBean itemBean, String message,
			EventCRFBean eventCrfBean, DisplayItemBean displayItemBean, Integer parentId, UserAccountBean uab,
			DataSource ds, StudyBean study, Connection con) {
		DiscrepancyNoteBean note = new DiscrepancyNoteBean();
		StudySubjectDAO ssdao = new StudySubjectDAO(ds, con);
		note.setDescription(message);
		note.setDetailedNotes("Failed Validation Check");
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

	@Override
	@SuppressWarnings(value = { "unchecked", "deprecation" })
	public void processRequest() throws Exception {
		try {
			session.setMaxInactiveInterval(10800);
			con = sm.getDataSource().getConnection();
            con.setAutoCommit(false);
			System.out.println("JDBC open connection for transaction");
			StudyDAO sdao = new StudyDAO(sm.getDataSource());
			EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource(), con);
			StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
			StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource(), con);
			ItemDataDAO itemDataDao = new ItemDataDAO(sm.getDataSource(), con);
			DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(sm.getDataSource(), con);
			EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
			String action = request.getParameter("action");
            StringBuffer skippedItemsSql = new StringBuffer();
			FormProcessor fp = new FormProcessor(request);

			// checks which module the requests are from
			String module = fp.getString(MODULE);
			request.setAttribute(MODULE, module);

			resetPanel();
			panel.setStudyInfoShown(false);
			panel.setOrderedData(true);

			setToPanel(resword.getString("create_CRF"),
                    respage.getString("br_create_new_CRF_entering"));

			setToPanel(resword.getString("create_CRF_version"),
                    respage.getString("br_create_new_CRF_uploading"));
			setToPanel(resword.getString("revise_CRF_version"),
                    respage.getString("br_if_you_owner_CRF_version"));
			setToPanel(resword.getString("CRF_spreadsheet_template"),
					respage.getString("br_download_blank_CRF_spreadsheet_from"));
			setToPanel(
					resword.getString("example_CRF_br_spreadsheets"),
					respage.getString("br_download_example_CRF_instructions_from"));

			if ("confirm".equalsIgnoreCase(action)) {
				List<DisplayItemBeanWrapper> displayItemBeanWrappers = (List<DisplayItemBeanWrapper>) session
						.getAttribute("importedData");
				logger.debug("Size of displayItemBeanWrappers : "
						+ displayItemBeanWrappers.size());
				forwardPage(Page.VERIFY_IMPORT_CRF_DATA);
			}

			if ("save".equalsIgnoreCase(action)) {

				// setup ruleSets to run if applicable
				RuleSetServiceInterface ruleSetService = (RuleSetServiceInterface) SpringServletAccess
						.getApplicationContext(context).getBean(
								"ruleSetService");
				logger.debug("=== about to generate rule containers ===");
				List<ImportDataRuleRunnerContainer> containers = this
						.ruleRunSetup(runRulesOptimisation, con, sm.getDataSource(), currentStudy,
								ub, ruleSetService);

				List<DisplayItemBeanWrapper> displayItemBeanWrappers = (List<DisplayItemBeanWrapper>) session
						.getAttribute("importedData");

				for (DisplayItemBeanWrapper wrapper : displayItemBeanWrappers) {

					int eventCrfBeanId = -1;
					EventCRFBean eventCrfBean = new EventCRFBean();

					logger.debug("=== right before we check to make sure it is savable: "
							+ wrapper.isSavable());
					if (wrapper.isSavable()) {
						con.commit();
						con.setAutoCommit(true);
						for (DisplayItemBean displayItemBean : wrapper
								.getDisplayItemBeans()) {
                            
							eventCrfBeanId = displayItemBean.getData()
									.getEventCRFId();
							eventCrfBean = (EventCRFBean) ecdao
									.findByPK(eventCrfBeanId);
							logger.debug("found value here: "
									+ displayItemBean.getData().getValue());
							logger.debug("found status here: "
									+ eventCrfBean.getStatus().getName());

                            ItemDataBean itemDataBean = new ItemDataBean();
                            itemDataBean = itemDataDao.findByItemIdAndEventCRFIdAndOrdinal(displayItemBean
                                    .getItem().getId(), eventCrfBean.getId(), displayItemBean.getData()
                                    .getOrdinal());
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
								ItemDAO idao = new ItemDAO(sm.getDataSource());
								ItemBean ibean = (ItemBean) idao.findByPK(displayItemBean.getData().getItemId());
								String itemOid = displayItemBean.getItem().getOid() + "_"
										+ wrapper.getStudyEventRepeatKey() + "_"
										+ displayItemBean.getData().getOrdinal() + "_" + wrapper.getStudySubjectOid();
								if (wrapper.getValidationErrors().containsKey(itemOid)) {
									ArrayList messageList = (ArrayList) wrapper.getValidationErrors().get(itemOid);
									// could be more then one will have to iterate
									for (int iter = 0; iter < messageList.size(); iter++) {
										String message = (String) messageList.get(iter);
										DiscrepancyNoteBean parentDn = createDiscrepancyNote(ibean, message,
												eventCrfBean, displayItemBean, null, ub, sm.getDataSource(),
												currentStudy, con);
										createDiscrepancyNote(ibean, message, eventCrfBean, displayItemBean,
												parentDn.getId(), ub, sm.getDataSource(), currentStudy, con);
									}
								}
							} else {
                                skippedItemsSql.append("INSERT INTO audit_log_event(audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value, event_crf_id) " +
                                        "VALUES (35, now(), " + ub.getId() + ", 'item_data', " + itemDataBean.getId() + ", '" + displayItemBean.getItem().getName() + "', '" + itemDataBean.getValue() + "', '" + displayItemBean.getData().getValue() + "', " + displayItemBean.getData().getEventCRFId() + "); ");
                            }

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
								eventCrfBean.setStage(edcb.isDoubleEntry() ? DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE
                                        : DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE);
								itemDataDao.updateStatusByEventCRF(eventCrfBean, Status.UNAVAILABLE, con);
							}

							ecdao.update(eventCrfBean, con);
							con.setAutoCommit(false);
						}

						if (eventCrfBean.getStudyEventId() > 0) {
							StudyEventBean seb = (StudyEventBean) sedao.findByPK(eventCrfBean.getStudyEventId());
							StudySubjectBean ssb = (StudySubjectBean) ssdao.findByPK(seb.getStudySubjectId());
							StudyBean sb = (StudyBean) sdao.findByPK(ssb.getStudyId());

							SubjectEventStatusUtil.determineSubjectEventState(seb, sb, new DAOWrapper(sdao, sedao,
									ssdao, ecdao, edcdao, dndao));
							seb.setUpdatedDate(new Date());
							seb.setUpdater(ub);

							sedao.update(seb, con);
						}
					}
				}

				addPageMessage(respage
						.getString("data_has_been_successfully_import"));
				System.out.println("Data is committed");
				con.commit();
				con.close();
                if (!skippedItemsSql.toString().isEmpty()) {
                    new AuditDAO(sm.getDataSource()).select(skippedItemsSql.toString());
                }
				try {
					con = sm.getDataSource().getConnection();
					con.setAutoCommit(false);
					logger.debug("=== about to run rules ===");
					addPageMessage(this.ruleActionWarnings(this.runRules(runRulesOptimisation, con,
							currentStudy, ub, containers, ruleSetService,
							ExecutionMode.SAVE)));
					con.commit();
					con.close();
				} catch (SQLException sqle) {
					con.rollback();
					con.close();
				}
				session.setMaxInactiveInterval(3600);
				forwardPage(Page.LIST_STUDY_SUBJECTS_SERVLET);
			}

		} catch (SQLException sqle) {
			con.rollback();
			con.close();
		}
	}

	private List<ImportDataRuleRunnerContainer> ruleRunSetup(Boolean runRulesOptimisation, Connection connection, DataSource dataSource, StudyBean studyBean,
			UserAccountBean userBean, RuleSetServiceInterface ruleSetService) {
		List<ImportDataRuleRunnerContainer> containers = new ArrayList<ImportDataRuleRunnerContainer>();
		ODMContainer odmContainer = (ODMContainer) session.getAttribute("odmContainer");
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
				if (containers != null && !containers.isEmpty()) {
					logger.debug("=== running rules dry run ===");
					ruleSetService.runRulesInImportData(runRulesOptimisation, connection, containers, studyBean, userBean, ExecutionMode.DRY_RUN);
				}
			}
		}
		return containers;
	}

	private List<String> runRules(Boolean runRulesOptimisation, Connection connection, StudyBean studyBean, UserAccountBean userBean,
			List<ImportDataRuleRunnerContainer> containers, RuleSetServiceInterface ruleSetService,
			ExecutionMode executionMode) {

		List<String> messages = new ArrayList<String>();
		if (containers != null && !containers.isEmpty()) {
			HashMap<String, ArrayList<String>> summary = ruleSetService.runRulesInImportData(runRulesOptimisation, connection, containers, studyBean,
					userBean, executionMode);
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
					mesg.append(s + ", ");
				}
				messages.add(mesg.toString());
			}
		}
		return messages;
	}

	private String ruleActionWarnings(List<String> warnings) {
		if (warnings.isEmpty())
			return "";
		else {
			StringBuilder mesg = new StringBuilder("Rule Action Warnings: ");
			for (String s : warnings) {
				mesg.append(s + "; ");
			}
			return mesg.toString();
		}
	}
}
